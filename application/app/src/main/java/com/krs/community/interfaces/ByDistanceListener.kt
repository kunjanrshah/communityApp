package com.krs.community.interfaces

import com.krs.community.responses.ByDistanceResponse

interface ByDistanceListener {
     fun getMembers(response: ByDistanceResponse)
    suspend fun getFailure(message:String)
}