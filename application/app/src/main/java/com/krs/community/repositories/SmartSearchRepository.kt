package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.model.*
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.retrofit.ApiServices
import org.json.JSONObject

class SmartSearchRepository(private val api: ApiServices, private val db:AppDatabase): SafeApiRequest()  {
    suspend fun searchByKeyword(jsonObject: JsonObject): searchByKeywordsResponse {
        return apiRequest{
            api.getSearchByKeywords(jsonObject)
        }
    }

   fun getLastName(id:Int):LiveData<String>{
      return  db.getLastNameDao().getLastNameById(id)
    }
    fun getCityName(id:String): LiveData<String> {
        return db.getCityDao().getcityNameById(Integer.parseInt(id))
    }

    fun getRelationName(id:String): String {
        return db.getRelationsDao().getRelationNameById(Integer.parseInt(id))
    }
}