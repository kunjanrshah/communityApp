package com.krs.community.repositories

import com.apollographql.apollo.ApolloClient
import com.google.gson.Gson
import com.krs.community.LoginMutation
import com.krs.community.app.FileUtils.decodeJwtPayload
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.type.LoginInput

class LoginRepository(
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    private val TAG: String = DashboardRepository::class.java.simpleName

    suspend fun getLogin(input: LoginInput): kotlin.Result<LoginResponse> {
        return try {
            val response = apolloClient.mutation(LoginMutation(input)).execute()
            response.exception?.let { throw it }

            response.errors?.firstOrNull()?.message?.let { throw Exception(it) }
            val loginData = response.data?.login
                ?: throw Exception("Login response was empty")

            val gson = Gson()
            val memberData = loginData.data
            val member = memberData?.let {
                val accessToken = it.access_token
                    ?.takeIf(String::isNotBlank)
                    ?: throw Exception("Login response did not include an access token")
                val refreshToken = it.refresh_token.orEmpty()
                val accessClaims = decodeJwtPayload(accessToken)
                val refreshClaims = refreshToken.takeIf(String::isNotBlank)
                    ?.let(::decodeJwtPayload)

                fun claim(name: String): String =
                    accessClaims.optString(name).takeIf(String::isNotBlank)
                        ?: refreshClaims?.optString(name)?.takeIf(String::isNotBlank)
                        ?: ""

                val id = claim("userId")
                val mobile = claim("mobile")
                val role = claim("role")
                if (id.isBlank() || role.isBlank()) {
                    throw Exception("Login tokens did not include required user claims")
                }

                gson.fromJson(gson.toJson(it), Member::class.java).apply {
                    this.id = id
                    this.mobile = mobile
                    this.role = role
                    this.accessToken = accessToken
                    this.refreshToken = refreshToken
                }
            }

            val loginResponse = LoginResponse().apply {
                success = loginData.success
                setOTP(loginData.OTP.orEmpty())
                message = loginData.message
                data = member
            }
            kotlin.Result.success(loginResponse)
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

}
