package com.krs.community.repositories

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.app.AppDatabase
import com.krs.community.model.*
import com.krs.community.responses.LastNameResponse
import com.krs.community.responses.StateResponse
import com.krs.community.responses.SubCommResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants
import org.json.JSONObject

class RegisterRepository(
    private val api:ApiServices,
    private val db:AppDatabase
): SafeApiRequest() {

    suspend fun userState(): StateResponse {
        return apiRequest{
            val date= db.getLastUpdatedDao().getLastUpdatedDate("state")
            val mJSONObject= JSONObject()
            mJSONObject.put("date",date)
            val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
            api.getUserState(updated)
        }
    }

    suspend fun userCity(id: Int): ResponseModel {
        return apiRequest{
            val mJSONObject= JSONObject()
            mJSONObject.put("state_id",id.toString())
            val request=  JsonParser().parse(mJSONObject.toString()) as JsonObject
            api.getUserCities(request)
        }
    }

    suspend fun userLastName(): LastNameResponse {
        val date= db.getLastUpdatedDao().getLastUpdatedDate("last_name")
        val mJSONObject= JSONObject()
        mJSONObject.put("date",date)
        val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
        return apiRequest{ api.getUserLastName(updated)  }
    }

    suspend fun userSubCommunity(): SubCommResponse? {
       return apiRequest{
           val date= db.getLastUpdatedDao().getLastUpdatedDate("sub_community")
           val mJSONObject= JSONObject()
           mJSONObject.put("date",date)
           val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
           api.getSubCommunity(updated)
       }
    }

    suspend fun getLocalCommunity(id: Int): ResponseModel {
        return apiRequest{
            val mJSONObject= JSONObject()
            mJSONObject.put("subcommunityid",id.toString())
            val request=  JsonParser().parse(mJSONObject.toString()) as JsonObject
            api.getLocalCommunity(request)
        }
    }

    suspend fun getUserRegister(userRegister: AppConstants.UserRegister):RegisterModel  {
        return apiRequest{
            api.getUserRegister(userRegister)
        }
    }
}