package com.krs.community.listeners

import com.krs.community.model.Datum

interface DashboardListener {
    fun getSubCommunity(data:List<Datum>)
    fun getLocalCommunity(data:List<Datum>)
    fun getLastName(data:List<Datum>)
    fun getState(data:List<Datum>)
    fun getCity(data:List<Datum>)
    //fun getRelation(data:List<Datum>)
    fun getNative(data:List<Datum>)
    fun getGotra(data:List<Datum>)
    fun getEducation(data:List<Datum>)
    fun getBusinessCategory(data:List<Datum>)
    fun getBusinessSubCategory(data:List<Datum>)
    fun getOccupation(data:List<Datum>)
    fun getFailure(message:String)
}