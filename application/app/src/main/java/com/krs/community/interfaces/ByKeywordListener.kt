package com.krs.community.interfaces

import com.krs.community.responses.searchByKeywordsResponse

interface ByKeywordListener {
    fun getMembers(response: searchByKeywordsResponse)
    suspend fun getFailure(message:String)
}