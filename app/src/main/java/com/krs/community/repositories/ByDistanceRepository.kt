package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.krs.community.app.AppDatabase
import com.krs.community.model.ByDistanceModel
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    fun getLocalCommName(id: String): String {
        return db.getLocalCommunityDao().getLocalCommName(id)
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
        }
    }
}