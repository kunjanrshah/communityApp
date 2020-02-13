package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.model.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class DemoRepository(
    private val api:ApiServices,private val  db:AppDatabase
): SafeApiRequest() {

    /*suspend fun getDemoApi(jsonObject: JsonObject): LoginResponse {
        return apiRequest{
            api.getDemoAPI("")
        }
    }*/

    suspend fun getRelationName(id:Int){
        db.getRelationsDao().getRelationById(id)
    }
}