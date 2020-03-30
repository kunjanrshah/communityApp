package com.krs.community.listeners

import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse

interface EditMemberListener {
    fun getScanResult(response: SmartFilterResponse)
    fun getUpdateOrAddResult(response: UpdateProfileResponse)
    suspend fun getFailure(message: String)
}