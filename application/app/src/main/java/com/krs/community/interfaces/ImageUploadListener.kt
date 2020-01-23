package com.krs.community.interfaces

import com.google.gson.JsonObject


interface ImageUploadListener {
    fun getResult(profile: String)
    suspend fun onFailure(message:String)
}