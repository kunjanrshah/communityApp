package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactListRepository(
        private val api: ApiServices, private val db: AppDatabase?
) : SafeApiRequest() {
    suspend fun getContactList(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getUserByMobile(jsonObject)
        }
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db!!.getNativeDao().getNative(id)
        }
    }

    suspend fun getLocalCommunity(id: String): String {
        return withContext(Dispatchers.IO) {
            db!!.getLocalCommunityDao().getLocalCommName(id)
        }
    }

    suspend fun getCityName(id: String): String {
        return db!!.getCityDao().getcityName(Integer.parseInt(id))
    }

    suspend fun getLastNameById(id: Int): String {
        return db!!.getLastNameDao().getLastName(id)
    }
}