package com.krs.community.utils

import android.content.Context
import android.util.Log
import com.auth0.android.jwt.JWT
import com.krs.community.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.Date

class GraphQLAuthInterceptor(
    private val context: Context,
    private val tokenManager: TokenManager = TokenManager(context),
    private val tag: String = "GraphQLAuth"
) : Interceptor {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val body = original.body
        val buffer = Buffer()
        body?.writeTo(buffer)
        val requestBodyString = buffer.readUtf8()

        val shouldSkipAuth = isAuthRequest(requestBodyString)

        val requestBuilder = original.newBuilder()

        if (!shouldSkipAuth) {
            val accessToken = tokenManager.accessToken

            if (!accessToken.isNullOrEmpty()) {
                val trimmedToken = accessToken.trim()
                if (trimmedToken != accessToken) {
                    Log.w(tag, "Access token had whitespace — trimmed before use: ${original.url}")
                }
                if (isTokenExpired(trimmedToken)) {
                    Log.w(
                        tag,
                        "Access token is expired — request will likely fail with 401: ${original.url}"
                    )
                }
                requestBuilder.removeHeader(AUTH_HEADER)
                requestBuilder.header(AUTH_HEADER, BEARER_PREFIX + trimmedToken)
                Log.d(
                    tag,
                    "Added Bearer token (length=${trimmedToken.length}) to request: ${original.url}"
                )
            } else {
                Log.w(
                    tag,
                    "No access_token found in SharedPreferences for request: ${original.url}"
                )
            }
        } else {
            Log.d(tag, "Skipping auth for Login/Register request: ${original.url}")
        }

        // Rebuild the request body so downstream interceptors and the network
        // layer see a fresh, unconsumed body. writeTo() on the original body
        // consumes it in OkHttp, so we must recreate it from the buffer content.
        if (body != null && requestBodyString.isNotEmpty()) {
            val contentType = body.contentType()
            val newBody = okhttp3.RequestBody.create(
                contentType,
                requestBodyString
            )
            requestBuilder.method(original.method, newBody)
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun isAuthRequest(requestBody: String?): Boolean {
        if (requestBody.isNullOrEmpty()) return false

        val normalizedBody = requestBody.lowercase()
        return normalizedBody.contains("mutation") &&
                (normalizedBody.contains("login(") ||
                        normalizedBody.contains("register(") ||
                        normalizedBody.contains("refreshtoken("))
    }

    private fun isTokenExpired(token: String): Boolean {
        if (token.isBlank()) return true
        val segments = token.split('.')
        if (segments.size != 3) {
            Log.w(tag, "Token does not look like a JWT — skipping expiry check")
            return false
        }

        return try {
            val jwt = JWT(token)
            val expiresAt = jwt.expiresAt
            if (expiresAt == null) {
                Log.w(tag, "Token has no expiry claim — not treating it as expired")
                false
            } else {
                expiresAt.before(Date())
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to parse JWT for expiry check: ${e.message}")
            false
        }
    }
}
