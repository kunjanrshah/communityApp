package com.krs.community.listeners

import com.krs.community.responses.UploadedFilesResponse

interface ByDocumentListener {
    fun getDocuments(response: UploadedFilesResponse)
    suspend fun getFailure(message: String)
}