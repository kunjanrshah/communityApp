package com.krs.community.listeners

import com.krs.community.responses.DocumentListResponse
import com.krs.community.responses.SmartFilterResponse

interface ByDocumentListener {
    fun getMembers(response: DocumentListResponse)
    suspend fun getFailure(message:String)
}