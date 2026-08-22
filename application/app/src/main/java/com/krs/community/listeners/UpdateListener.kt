package com.krs.community.listeners

import com.krs.community.responses.MasterUpdateResponse

interface UpdateListener {
    fun getVersionResponse(response: Boolean)
    fun getMastersResponse(response: MasterUpdateResponse?)
    suspend fun getFailure(msg: String)
}