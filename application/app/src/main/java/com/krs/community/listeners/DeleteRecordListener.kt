package com.krs.community.listeners

import com.krs.community.responses.DeleteProfileResponse

interface DeleteRecordListener {
    fun getResponse(respose: DeleteProfileResponse)
    suspend fun getFailure(message: String)
}