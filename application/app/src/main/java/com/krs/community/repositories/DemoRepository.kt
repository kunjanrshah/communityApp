package com.krs.community.repositories

import com.krs.community.app.AppDatabase
import com.krs.community.retrofit.ApiServices

class DemoRepository(
        private val api: ApiServices, private val db: AppDatabase
) : SafeApiRequest() {

    /*suspend fun getDemoApi(jsonObject: JsonObject): LoginResponse {
        return apiRequest{
            api.getDemoAPI("")
        }
    }*/

    suspend fun getRelationName(id: Int) {
        db.getRelationsDao().getRelationById(id)
    }
}