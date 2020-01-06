package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.responses.NewsResponse
import com.krs.community.retrofit.ApiServices

class NewsRepository(private val api: ApiServices, private val db:AppDatabase): SafeApiRequest()  {

    suspend fun getNewsList(jsonObject: JsonObject): NewsResponse {
        return apiRequest{
            api.getNewsList(jsonObject)
        }
    }
}