package com.krs.community.interfaces

import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse

interface EditMemberListener {
    fun getScanResult(response:SmartFilterResponse)
    fun getUpdateOrAddResult(response:UpdateProfileResponse)
    suspend fun getFailure(message:String)
}