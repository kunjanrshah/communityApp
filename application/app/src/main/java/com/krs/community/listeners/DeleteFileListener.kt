package com.krs.community.listeners

import com.google.gson.JsonObject

interface DeleteFileListener {
    fun getSuccess(id: Int, jsonObject: JsonObject)
    fun getFail(message: String)
}