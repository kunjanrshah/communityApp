package com.krs.community.listeners


interface CreateEventListener {
    fun getResult(profile: String)
    suspend fun onFailure(message: String)
}