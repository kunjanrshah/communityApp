package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.krs.community.app.AppDatabase
import com.krs.community.entities.City
import com.krs.community.entities.States
import com.krs.community.model.SearchByCityData
import com.krs.community.model.SearchByCityModel
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BrowseCityRepository(private val api: ApiServices, private val db: AppDatabase
) : SafeApiRequest() {


    suspend fun getStates(): LiveData<List<States>> {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getStates()
        }
    }

    suspend fun getCityByStateId(id: Int): LiveData<List<City>> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getCityById(id)
        }
    }

    suspend fun getLastnameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName(id)
        }
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
        }
    }


    suspend fun userRecords(data: SearchByCityData): SearchByCityModel {
        return apiRequest {
            api.getSearchByCity(data)
        }
    }

}