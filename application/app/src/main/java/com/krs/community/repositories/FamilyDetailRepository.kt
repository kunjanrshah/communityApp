package com.krs.community.repositories

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.GetFamilyMembersQuery
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.responses.UserInnerLogoutResponse
import com.krs.community.retrofit.ApiServices

class FamilyDetailRepository(
    private val api: ApiServices,
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    suspend fun getFamilyMembers(data: JsonObject): FamilyDetailResponse {
        val headId = data.get("head_id")?.asNumber?.toDouble()
            ?: data.get("headId")?.asNumber?.toDouble()
            ?: throw IllegalArgumentException("head_id/headId is required for getFamilyMembers")

        val loginUserId = data.get("loginUserId")?.asNumber?.toInt()
            ?: data.get("login_user_id")?.asNumber?.toInt()

        val loginUserIdOptional = if (loginUserId != null) {
            Optional.Present(loginUserId)
        } else {
            Optional.Absent
        }

        val response = apolloClient.query(
            GetFamilyMembersQuery(headId, loginUserIdOptional)
        ).execute()

        val errors = response.errors?.firstOrNull()?.message
        if (!errors.isNullOrEmpty()) {
            throw Exception(errors)
        }

        val result = response.data?.getFamilyMembers

        return FamilyDetailResponse().apply {
            success = result?.success ?: false
            total_records = (result?.total_records ?: 0).toString()
            member = result?.members?.map { member ->
                val m = Member()
                m.id = member.id.toString()
                m.firstName = member.first_name
                m.lastName = member.last_name ?: ""
                m.headId = member.head_id?.toString() ?: "0"
                m.head_name = member.head_name ?: ""
                m.relation = member.relation ?: ""
                m.city = member.city ?: ""
                m.state = member.state ?: ""
                m.subCommunity = member.sub_community ?: ""
                m.localCommunity = member.local_community ?: ""
                m.designation = member.designation ?: ""
                m.committee = member.committee ?: ""
                m.education = member.education ?: ""
                m.occupation = member.occupation ?: ""
                m.currentActivity = member.current_activity ?: ""
                m.gotra = member.gotra ?: ""
                m.nativePlace = member.native ?: ""
                m.businessCategory = member.business_category ?: ""
                m.profileCompleted = member.profile_completed ?: ""
                m.onlineStatus = member.online_status ?: 0
                m.loginStatus = if (member.login_status == true) 1 else 0
                m
            } ?: emptyList()
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
