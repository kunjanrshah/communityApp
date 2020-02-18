package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppDatabase
import com.krs.community.entities.City
import com.krs.community.entities.LastUpdated
import com.krs.community.responses.StatisticResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class StatisticsRepository(
        private val api: ApiServices,
                    private val db:AppDatabase
): SafeApiRequest() {

    private val TAG:String=StatisticsRepository::class.java.simpleName
    suspend fun getStatistics(jsonObject:JsonObject):StatisticResponse {
        return apiRequest{
            api.getStatistics(jsonObject)
        }
    }

    suspend fun getcityNameById(name:String): LiveData<Int> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getCityIdByName(name)
        }
    }

    suspend fun getCityNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityNames()
        }
    }


}