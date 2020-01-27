package com.krs.community.interfaces

import com.krs.community.model.*

interface ILoginListener {
    fun getUserLogin(response: LoginResponse)
    fun getFailure(message:String)
}