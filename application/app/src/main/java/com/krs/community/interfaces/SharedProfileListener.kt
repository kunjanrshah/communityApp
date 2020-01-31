package com.krs.community.interfaces

import com.krs.community.responses.SharedProfileResponse


interface SharedProfileListener {
    fun getMembers(response: SharedProfileResponse)
    suspend fun getFailure(message:String)
}