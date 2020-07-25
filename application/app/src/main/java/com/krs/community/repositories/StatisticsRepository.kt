package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.responses.StatisticResponse
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatisticsRepository(
        private val api: ApiServices,
        private val db: AppDatabase
) : SafeApiRequest() {

    private val TAG: String = StatisticsRepository::class.java.simpleName
    suspend fun getStatistics(jsonObject: JsonObject): StatisticResponse {
        return apiRequest {
            api.getStatistics(jsonObject)
        }
    }

    suspend fun getcityIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityIdByName(name)
        }
    }

    suspend fun getSubIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommIdByName(name)
        }
    }

    suspend fun getLocalIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommunityId(name)
        }
    }

    suspend fun getSubComm(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommName()
        }
    }

    suspend fun getLocalComm(SubId: Int): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommNameBySubId(SubId)
        }
    }

    suspend fun getCityNames(): List<String> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityListName()
        }
    }
}