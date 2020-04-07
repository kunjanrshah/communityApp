package com.krs.community.listeners

import com.google.gson.JsonObject

interface ImageUploadListener {
    fun onUploadSuccess(jsonObject: JsonObject)
    suspend fun onUploadFail(message: String)
}