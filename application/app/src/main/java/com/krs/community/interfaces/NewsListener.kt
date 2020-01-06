package com.krs.community.interfaces

import com.krs.community.responses.NewsResponse
import com.krs.community.responses.SmartFilterResponse

interface NewsListener {
    fun getNewsList(response: NewsResponse)
    fun getFailure(message:String)
}