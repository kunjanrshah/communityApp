package com.krs.community.repositories

import com.apollographql.apollo.ApolloClient
import com.krs.community.ChangePasswordMutation
import com.krs.community.type.ChangePasswordInput

class ChangePasswordRepository(
    private val apolloClient: ApolloClient
) {

    suspend fun changePassword(input: ChangePasswordInput): kotlin.Result<String> {
        return try {
            val response = apolloClient.mutation(ChangePasswordMutation(input)).execute()
            val message = response.data?.changePassword?.message

            if (message != null) {
                kotlin.Result.success(message)
            } else {
                val errorMessage = response.errors?.firstOrNull()?.message
                    ?: response.exception?.message
                    ?: "Unknown error"
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }
}
