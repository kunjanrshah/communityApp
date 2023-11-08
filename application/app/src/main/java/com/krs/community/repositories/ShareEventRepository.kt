package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.retrofit.ApiServices
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ShareEventRepository(
        private val api: ApiServices
) : SafeApiRequest() {

//
//    suspend fun createEvent(images: List<MultipartBody.Part>, id: RequestBody, user_id: RequestBody, access_token: RequestBody, params: RequestBody, yourtube: List<RequestBody>): JsonObject {
//        return apiRequest {
//            api.createEvent(images, id, user_id, access_token, params, yourtube)
//        }
//    }

    suspend fun createEvent(
        images: List<MultipartBody.Part>,
        id: RequestBody,
        title: RequestBody,
        description: RequestBody,
        location: RequestBody,
        lat: RequestBody,
        lng: RequestBody,
        youtube: List<RequestBody>,
        eventDate: RequestBody
    ): JsonObject {
        return apiRequest {
            api.createEvent(images, id, title, description, location, lat, lng, youtube, eventDate)
        }
    }
}