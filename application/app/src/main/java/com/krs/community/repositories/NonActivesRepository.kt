package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.entities.City
import com.krs.community.model.*
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.retrofit.ApiServices
import org.json.JSONObject

class NonActivesRepository(private val api: ApiServices, private val db:AppDatabase): SafeApiRequest()  {

    suspend fun getNonActiveUsers(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest{
            api.getSearchByFilter(jsonObject)
        }
    }

    fun getListCityName(): LiveData<List<String>> {
        return db.getCityDao().getcityNames()
    }
     fun getCityIdByName(name:String): Int {
        return db.getCityDao().getCityId(name)
    }

    fun getLastName(): LiveData<List<String>> {
        return db.getLastNameDao().getLastName()
    }

    fun getCityName(id:String): String {
        return db.getCityDao().getcityName(Integer.parseInt(id))
    }

     fun getLastNameById(id:Int): String {
        return db.getLastNameDao().getLastName(id)
    }

     fun getIdByLastName(name:String): Int {
        return db.getLastNameDao().getIdOfLastName(name)
    }
}