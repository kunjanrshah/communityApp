package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.model.LoginResponse
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.responses.UserInnerLogoutResponse
import com.krs.community.retrofit.ApiServices

class FamilyDetailRepository(private val api: ApiServices) : SafeApiRequest() {

    suspend fun getFamilyMembers(data: JsonObject): FamilyDetailResponse {
        return apiRequest {
            api.getFamilyMembers(data)
        }
    }

    suspend fun deleteMember(data: JsonObject): DeleteProfileResponse {
        return apiRequest {
            api.deleteMember(data)
        }
    }

    suspend fun innerLogin(data: JsonObject): LoginResponse {
        return apiRequest {
            api.innerLogin(data)
        }
    }

    suspend fun getUserLogout(data: JsonObject): UserInnerLogoutResponse {
        return apiRequest {
            api.getUserLogout(data)
        }
    }

}