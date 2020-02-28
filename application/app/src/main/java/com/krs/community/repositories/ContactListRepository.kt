package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices

class ContactListRepository(
        private val api: ApiServices
) : SafeApiRequest() {
    suspend fun getContactList(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getUserByMobile(jsonObject)
        }
    }
}