package com.krs.community.listeners

import com.google.gson.JsonObject

interface ImageUploadListener {
    fun getResult(jsonObject: JsonObject)
    suspend fun onFailure(message:String)
}