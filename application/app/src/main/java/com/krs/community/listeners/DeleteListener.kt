package com.krs.community.listeners

import com.google.gson.JsonObject

interface DeleteListener {
    fun getSuccess(id: Int, jsonObject: JsonObject)
    fun getFail(message: String)
}