package com.krs.community.listeners


interface ImageUploadListener {
    fun getResult(profile: String)
    suspend fun onFailure(message:String)
}