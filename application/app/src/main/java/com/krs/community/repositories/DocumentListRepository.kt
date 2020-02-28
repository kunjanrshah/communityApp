package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.responses.DocumentListResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices

class DocumentListRepository(private val api: ApiServices, private val db:AppDatabase): SafeApiRequest()  {


    suspend fun getDocumentList(): DocumentListResponse {
        return apiRequest{
            api.getDocumentList()
        }
    }


}