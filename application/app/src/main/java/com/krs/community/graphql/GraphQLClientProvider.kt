package com.krs.community.graphql

import android.content.Context
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.github.squti.guru.Guru.getString
import com.krs.community.BuildConfig
import com.krs.community.auth.RefreshTokenStrategy
import com.krs.community.auth.TokenAuthenticator
import com.krs.community.auth.TokenManager
import com.krs.community.auth.TokenRefreshApi
import com.krs.community.utils.ApiLogInterceptor
import com.krs.community.utils.AppConstants
import com.krs.community.utils.GraphQLAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object GraphQLClientProvider {

    private var cachedTokenRefreshApi: TokenRefreshApi? = null
    private var cachedTokenAuthenticator: TokenAuthenticator? = null

    fun provideApolloClient(context: Context): ApolloClient {
        return createApolloClient(context, provideTokenAuthenticator(context))
    }

    @Synchronized
    fun provideTokenAuthenticator(context: Context): TokenAuthenticator {
        return cachedTokenAuthenticator ?: run {
            val appContext = context.applicationContext
            val tokenRefreshApi = provideTokenRefreshApi(appContext)
            TokenAuthenticator(
                listOf(RefreshTokenStrategy(TokenManager(appContext), tokenRefreshApi))
            ).also { cachedTokenAuthenticator = it }
        }
    }

    @Synchronized
    fun provideTokenRefreshApi(context: Context): TokenRefreshApi {
        return cachedTokenRefreshApi ?: run {
            // Keep refresh calls on a client without an authenticator to avoid recursion.
            val refreshClient = createApolloClient(context.applicationContext, authenticator = null)
            TokenRefreshApi.getInstance(refreshClient).also {
                cachedTokenRefreshApi = it
            }
        }
    }

    private fun createApolloClient(
        context: Context,
        authenticator: TokenAuthenticator? = null
    ): ApolloClient {
        val okHttpClient = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor { message ->
                        Log.v("Apollo-Logging", message)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .addInterceptor(GraphQLAuthInterceptor(context))
            .addInterceptor(ApiLogInterceptor("Apollo"))
            .apply {
                // The TokenAuthenticator is only attached for the main client.
                // The refresh-only client (authenticator == null) skips this
                // so refresh calls do not trigger another refresh attempt.
                if (authenticator != null) {
                    authenticator(authenticator)
                }
            }
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .addHeader("apikey", AppConstants.API_KEY_VALUE)
                    .addHeader("devicetoken", getString("device_token", "") ?: "")
                    .addHeader("intudid", "145dfdfs")
                    .build()
                chain.proceed(request)
            }
            .build()

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.BASE_URL)
            .okHttpClient(okHttpClient)
            .sendApqExtensions(false)
            .sendDocument(true)
            .build()
    }
}
