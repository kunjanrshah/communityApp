package com.krs.community.auth

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.exception.ApolloHttpException
import com.krs.community.RefreshTokenMutation

/**
 * Executes the GraphQL `refreshToken` mutation against the API.
 *
 * This class is the single place that knows how to call the refresh
 * endpoint, so token-refresh logic is centralized and easy to test.
 */
class TokenRefreshApi private constructor(
    private val apolloClient: ApolloClient
) {

    companion object {
        private const val TAG = "TokenRefreshApi"

        @Volatile
        private var INSTANCE: TokenRefreshApi? = null

        fun getInstance(apolloClient: ApolloClient): TokenRefreshApi {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenRefreshApi(apolloClient).also { INSTANCE = it }
            }
        }
    }

    /**
     * Calls the refresh-token mutation.
     *
     * @param refreshToken The current refresh token.
     * @return [RefreshResult] indicating success or failure.
     */
    suspend fun refreshToken(refreshToken: String): RefreshResult {
        if (refreshToken.isBlank()) {
            Log.w(TAG, "refreshToken called with blank token")
            return RefreshResult.Failure(SessionExpired("Refresh token is blank"))
        }

        return try {
            val response = apolloClient.mutation(RefreshTokenMutation(refreshToken)).execute()
            val data = response.data?.refreshToken

            if (data != null && data.accessToken != null && data.accessToken.isNotBlank()) {
                Log.d(TAG, "Token refresh successful")
                RefreshResult.Success(
                    accessToken = data.accessToken,
                    refreshToken = data.refreshToken ?: refreshToken,
                    message = data.message ?: ""
                )
            } else {
                val error = response.errors?.firstOrNull()?.message ?: "Refresh token failed"
                Log.w(TAG, "Refresh token rejected: $error")
                val httpException = response.exception as? ApolloHttpException
                val isHttp401 = httpException?.statusCode == 401
                val isGraphQl401 = isUnauthorizedGraphQlError(response.errors)
                if (isHttp401 || isGraphQl401) {
                    RefreshResult.Failure(SessionExpired(error))
                } else {
                    RefreshResult.Failure(RefreshFailed(error))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during token refresh: ${e.message}", e)
            RefreshResult.Failure(RefreshFailed(e.message ?: "Unknown error"))
        }
    }

    /** Result wrapper for token refresh operations. */
    sealed interface RefreshResult {
        data class Success(
            val accessToken: String,
            val refreshToken: String,
            val message: String
        ) : RefreshResult

        /** Refresh failed but can be retried (e.g., network error). */
        data class Failure(val error: Throwable) : RefreshResult
    }

    /** Thrown when the refresh token itself is expired/invalid → session expired. */
    class SessionExpired(message: String) : Exception(message)

    /** Thrown when refresh fails for a transient or non-auth reason. */
    class RefreshFailed(message: String) : Exception(message)

    private fun isUnauthorizedGraphQlError(errors: List<Error>?): Boolean {
        if (errors.isNullOrEmpty()) return false
        return errors.any { error ->
            val statusCode = (error.extensions as? Map<*, *>)?.get("statusCode")?.toString()
            val message = error.message.lowercase()
            statusCode == "401" || message.contains("unauthorized") || message.contains("401")
        }
    }
}
