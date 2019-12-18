package com.krs.community.interfaces

import com.krs.community.responses.FamilyDetailResponse

interface IFamilyMembersListener {
    fun getFamilyMembers(data:FamilyDetailResponse)
    fun getFailure(message:String)
}