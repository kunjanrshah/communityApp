package com.krs.community.auth

import android.util.Log
import okhttp3.Request
import okhttp3.Response

/**
 * Strategy for responses that indicate the session is permanently
 * invalid and no retry should be attempted.
 *
 * When this strategy handles a 401 it immediately:
 * 1. Clears persisted tokens via [TokenManager].
 * 2. Notifies all registered [TokenRefreshCallback] observers through
 *    [TokenRefreshCallbackRegistry], which triggers the session-expired
 *    UI flow (dialog → navigate to Login).
 */
class SessionExpirationStrategy(
    private val tokenManager: TokenManager
) : AuthExceptionHandler {

    companion object {
        private const val TAG = "SessionExpirationStrategy"
    }

    override fun canHandle(response: Response): Boolean =
        response.code == 401

    override suspend fun handle(response: Response): Request? {
        if (!canHandle(response)) return null

        Log.w(TAG, "Session expired — clearing tokens and notifying observers")
        tokenManager.clearTokens()
        TokenRefreshCallbackRegistry.notifySessionExpired()
        return null
    }
}
