package com.krs.community.graphql

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.github.squti.guru.Guru.getString
import com.krs.community.BuildConfig
import com.krs.community.utils.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object GraphQLClientProvider {
    fun provideApolloClient(): ApolloClient {

        val okHttpClient = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor { message ->
                        Log.v("Apollo", message)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
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
            .build()
    }
}
