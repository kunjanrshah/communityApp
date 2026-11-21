package com.krs.community.repositories

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloHttpException
import com.krs.community.auth.TokenManager
import com.krs.community.auth.TokenRefreshApi
import com.krs.community.auth.TokenRefreshCallbackRegistry

internal suspend fun <D : Operation.Data> executeWithTokenRefresh(
    tokenManager: TokenManager,
    tokenRefreshApi: TokenRefreshApi,
    execute: suspend () -> ApolloResponse<D>
): ApolloResponse<D> {
    var response = execute()
    if (response.hasUnauthorizedError() ||
        (response.exception as? ApolloHttpException)?.statusCode == 401
    ) {
        refreshAccessToken(tokenManager, tokenRefreshApi)
        response = execute()
    }
    return response
}

private suspend fun refreshAccessToken(
    tokenManager: TokenManager,
    tokenRefreshApi: TokenRefreshApi
) {
    val refreshToken = tokenManager.refreshToken
    if (refreshToken.isNullOrBlank()) {
        tokenManager.clearTokens()
        TokenRefreshCallbackRegistry.notifySessionExpired()
        throw TokenRefreshApi.SessionExpired("Refresh token is unavailable")
    }

    when (val result = tokenRefreshApi.refreshToken(refreshToken)) {
        is TokenRefreshApi.RefreshResult.Success ->
            tokenManager.saveTokens(result.accessToken, result.refreshToken)

        is TokenRefreshApi.RefreshResult.Failure -> {
            if (result.error is TokenRefreshApi.SessionExpired) {
                tokenManager.clearTokens()
                TokenRefreshCallbackRegistry.notifySessionExpired()
            }
            throw result.error
        }
    }
}

private fun ApolloResponse<*>.hasUnauthorizedError(): Boolean =
    errors.hasUnauthorizedError()

private fun List<Error>?.hasUnauthorizedError(): Boolean =
    orEmpty().any { error ->
        val statusCode = (error.extensions as? Map<*, *>)?.get("statusCode")?.toString()
        val message = error.message.lowercase()
        statusCode == "401" || message.contains("unauthorized") || message.contains("401")
    }
