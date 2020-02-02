package com.krs.community.listeners

import com.krs.community.model.*

interface IbrowseCityRecordsListener {
    fun getSearchRecords(data:SearchByCityModel)
   suspend fun getFailure(message:String)
}