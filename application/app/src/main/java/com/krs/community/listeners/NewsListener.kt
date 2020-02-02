package com.krs.community.listeners

import com.krs.community.responses.NewsResponse

interface NewsListener {
    fun getNewsList(response: NewsResponse)
    fun getFailure(message:String)
}