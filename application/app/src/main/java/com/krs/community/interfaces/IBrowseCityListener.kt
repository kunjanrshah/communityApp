package com.krs.community.interfaces

import com.krs.community.model.*

interface IBrowseCityListener {
    fun getCities(data:List<CitiesDatum>)
    fun getStates(data:List<StateDatum>)
   suspend fun getFailure(message:String)
}