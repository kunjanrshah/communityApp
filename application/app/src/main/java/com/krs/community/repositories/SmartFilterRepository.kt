package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.model.LoginResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices

class SmartFilterRepository(private val api: ApiServices, private val db: AppDatabase) : SafeApiRequest() {

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
        return apiRequest {
            api.getSharedProfile(jsonObject)
        }
    }

    suspend fun getInActiveRecords(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getInActiveUsers(jsonObject)
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