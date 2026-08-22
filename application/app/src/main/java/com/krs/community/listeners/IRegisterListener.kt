package com.krs.community.listeners

import com.krs.community.model.RegisterModel

interface IRegisterListener {
    fun getRegisterSuccess(data: RegisterModel, isAdmin: Boolean)
    fun getRegisterFailure(message: String?, field: Int)
    suspend fun getFailure(message: String)
}