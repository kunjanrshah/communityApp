package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommitteeRepository(
        private val api: ApiServices, private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun getUsersInCommittee(data: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getUsersInCommittee(data)
        }
    }

    suspend fun getLastnameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName(id)
        }
    }

    suspend fun getLocalCommunityById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommName(id.toString())
        }
    }

    suspend fun getCommitteeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getCommitteeDao().getCommitteeNameById(id)
        }
    }

    suspend fun getDesignationById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getDesignationDao().getDesignationNameById(id)
        }
    }


    suspend fun getLocalCommunity(id: Int): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommNameBySubId(id)
        }
    }

    suspend fun getCommitteeList(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCommitteeDao().getCommitteeNames()
        }
    }

    suspend fun getDesignationList(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getDesignationDao().getDesignationName()
        }
    }

    suspend fun getLocalCommunityName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommunityId(name)
        }
    }

    suspend fun getCommitteeName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCommitteeDao().getCommitteeName(name)
        }
    }

    suspend fun getDesignationName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getDesignationDao().getDesignationName(name)
        }
    }
}