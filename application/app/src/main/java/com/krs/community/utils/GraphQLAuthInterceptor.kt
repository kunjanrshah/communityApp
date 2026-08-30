package com.krs.community.utils

import android.content.Context
import android.util.Log
import com.krs.community.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

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
                requestBuilder.addHeader(AUTH_HEADER, BEARER_PREFIX + accessToken)
                Log.d(tag, "Added Bearer token to request: ${original.url}")
            } else {
                Log.w(
                    tag,
                    "No access_token found in SharedPreferences for request: ${original.url}"
                )
            }
        } else {
            Log.d(tag, "Skipping auth for Login/Register request: ${original.url}")
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
}
