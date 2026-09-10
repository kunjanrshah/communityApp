package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.google.gson.JsonObject
import com.krs.community.GetSharedProfilesQuery
import com.krs.community.GetSharingProfilesQuery
import com.krs.community.app.AppDatabase
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmartFilterRepository(
    private val api: ApiServices,
    private val db: AppDatabase,
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    suspend fun searchByName(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getSearchByFilter(jsonObject)
        }
    }

    suspend fun searchByUser(jsonObject: JsonObject): LoginResponse {
        return apiRequest {
            api.getUserProfile(jsonObject)
        }
    }

    suspend fun getSharedProfile(jsonObject: JsonObject): SmartFilterResponse {
        val userId = jsonObject.get("id")?.asString?.toIntOrNull()
            ?: jsonObject.get("user_id")?.asString?.toIntOrNull()
            ?: throw IllegalArgumentException("id/user_id is required for getSharedProfile")

        val sharedResponse = apolloClient.query(GetSharedProfilesQuery(userId)).execute()
        val sharingResponse = apolloClient.query(GetSharingProfilesQuery(userId)).execute()

        val sharedErrors = sharedResponse.errors?.firstOrNull()?.message
        if (!sharedErrors.isNullOrEmpty()) {
            throw Exception(sharedErrors)
        }
        val sharingErrors = sharingResponse.errors?.firstOrNull()?.message
        if (!sharingErrors.isNullOrEmpty()) {
            throw Exception(sharingErrors)
        }

        val response = SmartFilterResponse()
        response.success = true
        response.message = "success"
        response.members = mapGraphUsersToMembers(sharedResponse.data?.getSharedProfiles)
        response.membersharing =
            mapGraphSharingUsersToMembers(sharingResponse.data?.getSharingProfiles)
        response.totalRecords = response.members?.size ?: 0
        return response
    }

    private fun mapGraphUsersToMembers(apolloUsers: List<GetSharedProfilesQuery.GetSharedProfile>?): List<Member> {
        return apolloUsers?.map { user ->
            Member().apply {
                id = user.id.toString()
                firstName = user.first_name ?: ""
                subCastId = user.last_name_id?.toString() ?: ""
                memberCode = user.member_code ?: ""
                mobile = user.mobile ?: ""
                emailAddress = user.email ?: ""
                status = user.status.toString()
            }
        } ?: emptyList()
    }

    private fun mapGraphSharingUsersToMembers(apolloUsers: List<GetSharingProfilesQuery.GetSharingProfile>?): List<Member> {
        return apolloUsers?.map { user ->
            Member().apply {
                id = user.id.toString()
                firstName = user.first_name ?: ""
                subCastId = user.last_name_id?.toString() ?: ""
                memberCode = user.member_code ?: ""
                mobile = user.mobile ?: ""
                emailAddress = user.email ?: ""
                status = user.status.toString()
            }
        } ?: emptyList()
    }

    suspend fun getInActiveRecords(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getInActiveUsers(jsonObject)
        }
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
        }
    }

    fun getListCityName(): LiveData<List<String>> {
        return db.getCityDao().getcityNames()
    }

    fun getCityIdByName(name: String): Int {
        return db.getCityDao().getCityId(name)
    }

    fun getLastName(): LiveData<List<String>> {
        return db.getLastNameDao().getLastName()
    }

    suspend fun getCityName(id: String): String {
        return db.getCityDao().getcityName(Integer.parseInt(id))
    }

    fun getSubCommunity(id: String): String {
        return db.getSubCommunityDao().getSubCommunityName(id)
    }

    fun getLocalCommunity(id: String): String {
        return db.getLocalCommunityDao().getLocalCommName(id)
    }

    suspend fun getLastNameById(id: Int): String {
        return db.getLastNameDao().getLastName(id)
    }

    fun getIdByLastName(name: String): Int {
        return db.getLastNameDao().getIdOfLastName(name)
    }
}