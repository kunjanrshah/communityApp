package com.krs.community.interfaces

import com.krs.community.model.*

interface ILoginListener {
    fun userForgotPass(data:String)
    fun getUserLogin(data:LoginData)
    fun getFailure(message:String)
}