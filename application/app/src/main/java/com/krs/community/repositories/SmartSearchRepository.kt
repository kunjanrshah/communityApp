package com.krs.community.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.entities.RoomMember
import com.krs.community.model.*
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.retrofit.ApiServices

class SmartSearchRepository(private val api: ApiServices, private val db:AppDatabase): SafeApiRequest()  {
    suspend fun searchByKeyword(jsonObject: JsonObject): searchByKeywordsResponse {
        return apiRequest{
            api.getSearchByKeywords(jsonObject)
        }
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

    suspend fun getRoomMembers(): List<RoomMember> {
        return db.getRoomMemberDao().getRoomMembers()
    }

    suspend fun insertRoomMember(member: RoomMember){
       return db.getRoomMemberDao().saveRoomMember(member)
   }

    suspend fun deleteRoomMember(id: Int){
        return db.getRoomMemberDao().deleteRoomMember(id)
   }

  fun getRoomMember(id: Int):LiveData<RoomMember>{
        return db.getRoomMemberDao().getRoomMember(id)
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