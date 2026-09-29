package com.krs.community.auth

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Request
import okhttp3.Response

/**
 * Refreshes the access token after an authenticated request receives 401.
 */
class RefreshTokenStrategy(
    private val tokenManager: TokenManager,
    private val tokenRefreshApi: TokenRefreshApi
) : AuthExceptionHandler {

    companion object {
        private const val TAG = "RefreshTokenStrategy"
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    private val refreshLock = Mutex()

    override fun canHandle(response: Response): Boolean =
        response.code == 401 &&
                response.request.header(AUTH_HEADER)
                    ?.removePrefix(BEARER_PREFIX)
                    ?.isNotBlank() == true

    override suspend fun handle(response: Response): Request? {
        if (!canHandle(response)) return null

        return refreshLock.withLock {
            val failedAccessToken = response.request.header(AUTH_HEADER)
                ?.removePrefix(BEARER_PREFIX)
                ?.trim()
            val latestAccessToken = tokenManager.accessToken

            // Another concurrent request may already have refreshed the token.
            if (!latestAccessToken.isNullOrBlank() && latestAccessToken != failedAccessToken) {
                return@withLock response.request.newBuilder()
                    .header(AUTH_HEADER, BEARER_PREFIX + latestAccessToken)
                    .build()
            }

            val currentRefreshToken = tokenManager.refreshToken
            if (currentRefreshToken.isNullOrBlank()) {
                Log.w(TAG, "No refresh token available — session expired")
                tokenManager.clearTokens()
                TokenRefreshCallbackRegistry.notifySessionExpired()
                return@withLock null
            }

            when (val result = tokenRefreshApi.refreshToken(currentRefreshToken)) {
                is TokenRefreshApi.RefreshResult.Success -> {
                    tokenManager.saveTokens(result.accessToken, result.refreshToken)
                    val refreshedAccessToken = tokenManager.accessToken
                    if (refreshedAccessToken.isNullOrBlank()) {
                        Log.e(TAG, "Refresh succeeded but the access token was blank")
                        return@withLock null
                    }

                    Log.d(TAG, "Tokens refreshed and persisted")
                    response.request.newBuilder()
                        .header(AUTH_HEADER, BEARER_PREFIX + refreshedAccessToken)
                        .build()
                }

                is TokenRefreshApi.RefreshResult.Failure -> {
                    when (result.error) {
                        is TokenRefreshApi.SessionExpired -> {
                            Log.w(TAG, "Session expired: ${result.error.message}")
                            tokenManager.clearTokens()
                            TokenRefreshCallbackRegistry.notifySessionExpired()
                        }

                        else -> Log.e(TAG, "Refresh failed: ${result.error.message}")
                    }
                    null
                }
            }
        }
    }
}
