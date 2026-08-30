package com.krs.community.auth

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Request
import okhttp3.Response

/**
 * Strategy that handles a 401 by attempting an automated token refresh.
 *
 * Key design decisions:
 *
 * 1. **Synchronized refresh** — Uses a [Mutex] so that when multiple
 *    concurrent requests fail with 401, only the first triggers a
 *    refresh; the rest wait and then reuse the new token.
 *
 * 2. **Token persistence** — After a successful refresh, the new
 *    access and refresh tokens are persisted via [TokenManager].
 *
 * 3. **Session expiration** — If the refresh mutation itself returns
 *    401 (or the token is blank/invalid), this strategy notifies the
 *    [TokenRefreshCallbackRegistry] which triggers the session-expired
 *    flow (dialog + navigation to Login).
 */
class RefreshTokenStrategy(
    private val tokenManager: TokenManager,
    private val tokenRefreshApi: TokenRefreshApi
) : AuthExceptionHandler {

    companion object {
        private const val TAG = "RefreshTokenStrategy"
    }

    // Guards the refresh-critical section so only one refresh runs at a time.
    private val refreshLock = Mutex()

    override fun canHandle(response: Response): Boolean =
        response.code == 401

    override suspend fun handle(response: Response): Request? {
        if (!canHandle(response)) return null

        val currentRefreshToken = tokenManager.refreshToken
        if (currentRefreshToken.isNullOrBlank()) {
            Log.w(TAG, "No refresh token available — session expired")
            TokenRefreshCallbackRegistry.notifySessionExpired()
            return null
        }

        return refreshLock.withLock {
            val result = tokenRefreshApi.refreshToken(currentRefreshToken)
            when (result) {
                is TokenRefreshApi.RefreshResult.Success -> {
                    tokenManager.saveTokens(result.accessToken, result.refreshToken)
                    Log.d(TAG, "Tokens refreshed and persisted")
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${result.accessToken}")
                        .build()
                }

                is TokenRefreshApi.RefreshResult.Failure -> {
                    when (result.error) {
                        is TokenRefreshApi.SessionExpired -> {
                            Log.w(TAG, "Session expired: ${result.error.message}")
                            tokenManager.clearTokens()
                            TokenRefreshCallbackRegistry.notifySessionExpired()
                        }

                        else -> {
                            Log.e(TAG, "Refresh failed: ${result.error.message}")
                        }
                    }
                    null
                }
            }
        }
    }
}
