package com.krs.community.repositories

import com.apollographql.apollo.ApolloClient
import com.krs.community.LoginMutation
import com.krs.community.app.FileUtils.decodeJwtPayload
import com.krs.community.model.LoginModel
import com.krs.community.type.LoginInput

class LoginRepository(
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    private val TAG: String = DashboardRepository::class.java.simpleName

    suspend fun getLogin(input: LoginInput): kotlin.Result<LoginModel> {
        return try {
            val response = apolloClient.mutation(LoginMutation(input)).execute()
            val loginData = response.data?.login

            if (loginData != null) {
                val json = decodeJwtPayload(loginData.accessToken)

                val loginModel = LoginModel(
                    authToken = loginData.accessToken,
                    refreshToken = loginData.refreshToken,
                    message = loginData.message,
                    userId = json.optString("userId", ""),
                    mobile = json.optString("mobile", ""),
                    role = json.optString("role", "")
                )
                kotlin.Result.success(loginModel)
            } else {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

}
