package com.krs.community.interfaces

import com.krs.community.responses.ByDistanceResponse

interface ByDistanceListener {
     fun getUsers(response: ByDistanceResponse)
    fun getFailure(message:String)
}