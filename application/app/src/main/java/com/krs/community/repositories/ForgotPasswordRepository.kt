package com.krs.community.repositories

import com.apollographql.apollo.ApolloClient
import com.krs.community.ForgotPasswordMutation
import com.krs.community.type.ForgotPasswordInput

class ForgotPasswordRepository(
    private val apolloClient: ApolloClient
) {

    suspend fun forgotPassword(input: ForgotPasswordInput): kotlin.Result<String> {
        return try {
            val response = apolloClient.mutation(ForgotPasswordMutation(input)).execute()
            val message = response.data?.forgotPassword

            if (message != null) {
                kotlin.Result.success(message)
            } else {
                val errorMessage = response.errors?.firstOrNull()?.message
                    ?: "Unknown error"
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }
}
