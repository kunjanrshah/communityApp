package com.krs.community.repositories

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.krs.community.UpdateDeviceTokenMutation

class DeviceTokenRepository(
    private val apolloClient: ApolloClient
) {

    companion object {
        private val TAG = DeviceTokenRepository::class.java.simpleName
    }

    suspend fun updateDeviceToken(): Boolean {
        return try {
            val response = apolloClient.mutation(UpdateDeviceTokenMutation()).execute()
            val success = response.data?.updateDeviceToken == true
            if (success) {
                Log.d(TAG, "Device token updated on server successfully")
            } else {
                val error = response.errors?.firstOrNull()?.message
                    ?: response.exception?.message
                    ?: "Unknown error"
                Log.e(TAG, "Failed to update device token: $error")
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "Exception updating device token: ${e.message}", e)
            false
        }
    }
}
