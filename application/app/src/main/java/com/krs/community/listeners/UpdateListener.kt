package com.krs.community.listeners

import com.krs.community.responses.MasterUpdateResponse
import com.krs.community.responses.UserStatusResponse

interface UpdateListener {
    fun getVersionResponse(response: UserStatusResponse)
    fun getMastersResponse(response: MasterUpdateResponse)
    suspend fun getFailure(msg: String)
}