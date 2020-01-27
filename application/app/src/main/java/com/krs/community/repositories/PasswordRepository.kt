package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.model.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class PasswordRepository(
    private val api:ApiServices
): SafeApiRequest() {

    suspend fun changePassword(jsonObject: JsonObject): LoginResponse {
        return apiRequest{
            api.changePassword(jsonObject)
        }
    }

    suspend fun forgotPassword(jsonObject: JsonObject): LoginResponse {
        return apiRequest{
            api.forgotPassword(jsonObject)
        }
    }
}