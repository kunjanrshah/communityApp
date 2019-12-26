package com.krs.community.interfaces

import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse

interface IFamilyMembersListener {
    fun getMessage(response: DeleteProfileResponse)
    fun getFailure(message:String)
    fun getFamilyMembers(data:FamilyDetailResponse)
}