package com.krs.community.repositories

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

    suspend fun getCityNames(): List<String> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityListName()
        }
    }


}