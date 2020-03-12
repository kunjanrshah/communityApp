package com.krs.community.listeners

import com.krs.community.model.RegisterModel

interface IRegisterListener {
    fun getRegisterSuccess(data:RegisterModel)
    fun getRegisterFailure(message:String,field:Int)
   suspend fun getFailure(message:String)
}