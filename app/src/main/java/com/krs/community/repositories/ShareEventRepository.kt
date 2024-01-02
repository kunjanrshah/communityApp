package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.retrofit.ApiServices
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ShareEventRepository(
    private val api: ApiServices
) : SafeApiRequest() {

    suspend fun createEvent(
        gallery: List<MultipartBody.Part>,
        access_token: RequestBody,
        id: RequestBody,
        user_id: RequestBody,
        youtubeLinks: List<RequestBody>,
        description: RequestBody,
        title: RequestBody,
        location: RequestBody,
        eventDate: RequestBody,
        lat: RequestBody,
        lng: RequestBody,
    ): JsonObject {
        return apiRequest {
            api.createEvent(
                gallery,
                access_token,
                id,
                user_id,
                youtubeLinks,
                description,
                title,
                location,
                eventDate,
                lat,
                lng,
                )
        }
    }
}
