package com.krs.community.interfaces

import com.krs.community.model.*

interface IRegisterListener {
    fun getRegisterSuccess(data:RegisterModel)
    fun getSubCommunity(data:List<SubDatum>)
    fun getLocalCommunity(data:List<LocalDatum>)
    fun getLastname(data:List<LastNameDatum>)
    fun getCities(data:List<CitiesDatum>)
    fun getStates(data:List<StateDatum>)
    fun getRegisterFailure(message:String,field:Int)
   suspend fun getFailure(message:String)
}