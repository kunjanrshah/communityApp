package com.krs.community.listeners

import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse

interface IFamilyMembersListener {
    fun getMessage(response: DeleteProfileResponse)
    suspend fun getFailure(message: String)
    fun getFamilyMembers(data: FamilyDetailResponse)
}