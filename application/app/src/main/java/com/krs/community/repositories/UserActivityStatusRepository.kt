package com.krs.community.repositories

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.krs.community.GetUserActivityStatusMutation
import com.krs.community.type.GetUserActivityStatusInput

class UserActivityStatusRepository(
    private val apolloClient: ApolloClient
) {

    suspend fun getUserActivityStatus(id: Long): UserActivityStatusResult {
        return try {
            val input = GetUserActivityStatusInput(id = id)
            val response = apolloClient.mutation(GetUserActivityStatusMutation(input)).execute()
            val data = response.data?.getUserActivityStatus

            val errors = response.errors?.firstOrNull()?.message
            if (data == null || data.success != true) {
                UserActivityStatusResult.Failure(errors ?: response.exception?.message ?: "Unknown error")
            } else {
                Log.d("UserActivityStatus", "Activity status fetched successfully")
                UserActivityStatusResult.Success(data.data?.let {
                    UserActivityStatusData(
                        id = it.id ?: 0,
                        lastLogin = it.last_login?.toString(),
                        loginStatus = it.login_status ?: false,
                        onlineStatus = it.online_status ?: false
                    )
                })
            }
        } catch (e: Exception) {
            Log.e("UserActivityStatus", "Error getting user activity status: ${e.message}", e)
            UserActivityStatusResult.Failure(e.message ?: "Unknown error")
        }
    }
}

data class UserActivityStatusData(
    val id: Long,
    val lastLogin: String?,
    val loginStatus: Boolean,
    val onlineStatus: Boolean
)

sealed interface UserActivityStatusResult {
    data class Success(val data: UserActivityStatusData?) : UserActivityStatusResult
    data class Failure(val error: String) : UserActivityStatusResult
}
