package com.krs.community.interfaces

import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse

interface EditMemberListener {
    fun getMembers(response:SmartFilterResponse)
    fun getMessage(response:UpdateProfileResponse)
    fun getFailure(message:String)
}