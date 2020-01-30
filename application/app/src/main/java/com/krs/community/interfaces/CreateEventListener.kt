package com.krs.community.interfaces

import com.google.gson.JsonObject


interface CreateEventListener {
    fun getResult(profile: String)
    suspend fun onFailure(message:String)
}