package com.krs.community.listeners

import com.krs.community.responses.UserActivityStatusResponse


interface ActivityStatusListner {
    fun memberStatus(response: UserActivityStatusResponse)
    suspend fun getFailure(message:String)
}