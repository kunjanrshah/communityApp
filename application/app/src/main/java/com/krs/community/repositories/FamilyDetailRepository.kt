package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.retrofit.ApiServices

class FamilyDetailRepository(private val api: ApiServices): SafeApiRequest()  {

    suspend fun getFamilyMembers(data: JsonObject): FamilyDetailResponse {
        return apiRequest{
            api.getFamilyMembers(data)
        }
    }
}