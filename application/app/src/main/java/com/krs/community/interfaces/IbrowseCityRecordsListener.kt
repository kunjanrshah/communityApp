package com.krs.community.interfaces

import com.krs.community.model.*

interface IbrowseCityRecordsListener {
    fun getSearchRecords(data:SearchByCityModel)
   suspend fun getFailure(message:Boolean)
}