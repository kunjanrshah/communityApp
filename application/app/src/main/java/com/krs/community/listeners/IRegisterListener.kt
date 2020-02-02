package com.krs.community.listeners

import com.krs.community.entities.LastName
import com.krs.community.entities.States
import com.krs.community.entities.SubCommunity
import com.krs.community.model.*

interface IRegisterListener {
    fun getSubCommunity(data:List<SubCommunity>)
    fun getLocalCommunity(data:List<Datum>)
    fun getLastname(data:List<LastName>)
    fun getCities(data:List<Datum>)
    fun getStates(data:List<States>)
    fun getRegisterSuccess(data:RegisterModel)
    fun getRegisterFailure(message:String,field:Int)
   suspend fun getFailure(message:String)
}