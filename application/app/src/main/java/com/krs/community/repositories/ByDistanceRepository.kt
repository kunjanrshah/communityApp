package com.krs.community.repositories

import com.krs.community.model.*
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.retrofit.ApiServices

class ByDistanceRepository(private val api: ApiServices
): SafeApiRequest()  {
    suspend fun byDistance(distance:ByDistanceModel): ByDistanceResponse {
        return apiRequest{
            api.getSearchByDistance(distance)
        }
    }
}