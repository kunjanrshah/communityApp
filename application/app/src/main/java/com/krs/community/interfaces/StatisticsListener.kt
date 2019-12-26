package com.krs.community.interfaces

import com.krs.community.responses.StatisticResponse

interface StatisticsListener {
     fun getStatistics(response: StatisticResponse)
    fun getFailure(message:String)
}