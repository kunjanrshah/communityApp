package com.krs.community.listeners


interface CreateDocumentListener {
    fun getResult(profile: String)
    suspend fun onFailure(message:String)
}