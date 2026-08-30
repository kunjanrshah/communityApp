package com.krs.community.auth

import okhttp3.Response

/**
 * Strategy interface for deciding how to handle an HTTP response
 * that indicates an authentication failure (e.g., 401 Unauthorized).
 *
 * Implementations encapsulate different policies:
 * - [RefreshTokenStrategy]  — attempt a token refresh and retry.
 * - [SessionExpirationStrategy] — signal that the session is permanently expired.
 *
 * Using the Strategy pattern keeps the [TokenAuthenticator] decoupled
 * from the concrete refresh / expiration logic, making each policy
 * independently testable and swappable.
 */
interface AuthExceptionHandler {
    /**
     * Returns true if this strategy can handle the given response.
     */
    fun canHandle(response: Response): Boolean

    /**
     * Attempts to resolve the auth failure.
     *
     * Returns the new [okhttp3.Request] to retry with, or null if the
     * failure could not be resolved (e.g., refresh token also expired).
     */
    suspend fun handle(response: Response): okhttp3.Request?
}
