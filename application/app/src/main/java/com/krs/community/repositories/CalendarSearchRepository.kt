package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.model.LoginResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices

class CalendarSearchRepository(private val api: ApiServices, private val db:AppDatabase): SafeApiRequest()  {

    suspend fun getSearchByDate(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest{
            api.getSearchByDate(jsonObject)
        }
    }

    suspend fun setReminder(jsonObject: JsonObject): LoginResponse {
        return apiRequest {
            api.setReminder(jsonObject)
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