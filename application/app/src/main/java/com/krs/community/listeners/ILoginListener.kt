package com.krs.community.listeners

import com.krs.community.model.LoginResponse

interface ILoginListener {
    fun userLogin(response: LoginResponse)
    suspend fun getFailure(message: String)
}