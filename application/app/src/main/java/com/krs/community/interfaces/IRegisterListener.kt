package com.krs.community.interfaces

import com.krs.community.model.*

interface IRegisterListener {
    fun getSubCommunity(data:List<SubDatum>)
    fun getLocalCommunity(data:List<LocalDatum>)
    fun getLastname(data:List<LastNameDatum>)
    fun getCities(data:List<CitiesDatum>)
    fun getStates(data:List<StateDatum>)
    fun getFailure(message:String)
}