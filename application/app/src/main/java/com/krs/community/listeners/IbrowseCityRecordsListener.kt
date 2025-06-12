package com.krs.community.listeners

import com.krs.community.model.SearchByCityModel
import com.krs.community.responses.CityResponse

interface IbrowseCityRecordsListener {
    fun getCitiesByState(response: CityResponse)
    fun getSearchRecords(data: SearchByCityModel)
    suspend fun getFailure(message: String)
}