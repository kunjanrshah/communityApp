package com.krs.community.interfaces

import com.krs.community.responses.SmartFilterResponse

interface ByFilterListener {
    fun getMembers(response: SmartFilterResponse)
    suspend fun getFailure(message:String)
}