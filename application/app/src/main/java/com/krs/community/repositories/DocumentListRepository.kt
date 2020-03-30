package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.responses.UploadedFilesResponse
import com.krs.community.retrofit.ApiServices

class DocumentListRepository(private val api: ApiServices) : SafeApiRequest() {

    suspend fun getDocumentList(jsonObject: JsonObject): UploadedFilesResponse {
        return apiRequest {
            api.getDocumentList(jsonObject)
        }
    }
}