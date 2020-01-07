package com.krs.community.interfaces

import com.krs.community.responses.searchByKeywordsResponse

interface ByKeywordListener {
    fun refreshList()
    fun getMembers(response: searchByKeywordsResponse)
    fun getFailure(message:String)
}