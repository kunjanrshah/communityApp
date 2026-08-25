package com.krs.community.repositories

import com.apollographql.apollo.ApolloClient
import com.krs.community.RegisterMutation
import com.krs.community.app.FileUtils.decodeJwtPayload
import com.krs.community.model.RegisterModel
import com.krs.community.type.RegisterInput

class RegisterRepository(
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    private val TAG: String = DashboardRepository::class.java.simpleName

//    suspend fun getUserRegister(jsonObject: JsonObject): RegisterModel {
//        return apiRequest {
//            api.getUserRegister(jsonObject)
//        }
//    }

//    suspend fun getUserRegister(profile: MultipartBody.Part?,fname: RequestBody?,father: RequestBody?,bdate: RequestBody?,lastName: RequestBody?,email: RequestBody?,mobile: RequestBody?,gender: RequestBody?,pass: RequestBody?,address: RequestBody?,state: RequestBody?,city: RequestBody?,native: RequestBody?, subComm: RequestBody?,local: RequestBody?, marital: RequestBody?,relation: RequestBody?,isAdmin: RequestBody?): RegisterModel {
//        return apiRequest {
//            api.getUserRegister(profile,fname,father,bdate,lastName,email,mobile,gender,pass,address,state,city,native, subComm,local, marital,relation,isAdmin)
//        }
//    }

    suspend fun getUserRegister(input: RegisterInput): kotlin.Result<RegisterModel> {
        return try {
            val response = apolloClient.mutation(RegisterMutation(input)).execute()
            val registerData = response.data?.register

            if (registerData != null) {
                val json = decodeJwtPayload(registerData.accessToken)

                val registerModel = RegisterModel(
                    accessToken = registerData.accessToken,
                    userId = json.getString("userId"),
                    mobile = json.getString("mobile"),
                    role = json.getString("role"),
                    expiresAt = json.getString("expiresAt"),
                    refreshToken = registerData.refreshToken,
                    message = registerData.message
                )
                kotlin.Result.success(registerModel)
            } else {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }


}