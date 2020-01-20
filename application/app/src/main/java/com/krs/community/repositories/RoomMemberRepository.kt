package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppDatabase
import com.krs.community.entities.LastUpdated
import com.krs.community.entities.RoomMember
import com.krs.community.responses.StatisticResponse
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RoomMemberRepository(
        private val api: ApiServices,
                    private val db:AppDatabase
): SafeApiRequest() {

    private val TAG:String=RoomMemberRepository::class.java.simpleName

    fun getLastName(id:Int):LiveData<String>{
        return  db.getLastNameDao().getLastNameById(id)
    }
    fun getCityName(id:String): LiveData<String> {
        return db.getCityDao().getcityNameById(Integer.parseInt(id))
    }

    suspend fun getRoomMembers(): List<RoomMember> {
        return db.getRoomMemberDao().getRoomMembers()
    }

    fun getRoomMember(id: Int):LiveData<RoomMember>{
        return db.getRoomMemberDao().getRoomMember(id)
    }

    suspend fun deleteRoomMember(id: Int){
        return db.getRoomMemberDao().deleteRoomMember(id)
    }

    suspend fun insertRoomMember(member: RoomMember){
        return db.getRoomMemberDao().saveRoomMember(member)
    }

    suspend fun changeStatus(jsonObject: JsonObject): searchByKeywordsResponse {
        return apiRequest{
            api.changeStatus(jsonObject)
        }
    }

    suspend fun changeRole(jsonObject: JsonObject): searchByKeywordsResponse {
        return apiRequest{
            api.changeRole(jsonObject)
        }
    }

}