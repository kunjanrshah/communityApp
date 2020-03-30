package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.krs.community.app.AppDatabase
import com.krs.community.model.ByDistanceModel
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.retrofit.ApiServices

class ByDistanceRepository(private val api: ApiServices, private val db: AppDatabase) : SafeApiRequest() {
    suspend fun byDistance(distance: ByDistanceModel): ByDistanceResponse {
        return apiRequest {
            api.getSearchByDistance(distance)
        }
    }

    fun getCityName(id: String): LiveData<String> {
        return db.getCityDao().getcityNameById(Integer.parseInt(id))
    }

    fun getLastName(id: String): LiveData<String> {
        return db.getLastNameDao().getLastNameById(Integer.parseInt(id))
    }
}