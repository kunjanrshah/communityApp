package com.krs.community.listeners

import com.krs.community.model.SearchByCityModel

interface IbrowseCityRecordsListener {
    fun getSearchRecords(data: SearchByCityModel)
    suspend fun getFailure(message: String)
}