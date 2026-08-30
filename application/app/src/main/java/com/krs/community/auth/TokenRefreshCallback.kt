package com.krs.community.auth

/**
 * Observer contract for session lifecycle events.
 *
 * Implemented by components that need to react when the session
 * expires (e.g., showing a dialog and navigating to Login).
 *
 * Registered with [TokenRefreshCallbackRegistry] so that any
 * part of the app can be notified without tight coupling.
 */
interface TokenRefreshCallback {
    /**
     * Called when the refresh token is no longer valid and the
     * session must be considered expired.
     */
    fun onSessionExpired()
}
