package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.model.RegisterModel
import com.krs.community.retrofit.ApiServices

class RegisterRepository(
        private val api: ApiServices
) : SafeApiRequest() {

    private val TAG: String = DashboardRepository::class.java.simpleName

    suspend fun getUserRegister(jsonObject: JsonObject): RegisterModel {
        return apiRequest {
            api.getUserRegister(jsonObject)
        }
    }
}