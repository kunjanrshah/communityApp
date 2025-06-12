package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.model.LoginResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class LoginRepository(private val api: ApiServices) : SafeApiRequest() {

    suspend fun getLogin(userLogin: AppConstants.LoginRequest): LoginResponse {
        return apiRequest {
            api.getUserLogin(userLogin)
        }
    }

    suspend fun innerLogin(data: JsonObject): LoginResponse {
        return apiRequest {
            api.innerLogin(data)
        }
    }

}