package com.krs.community.interfaces

import androidx.lifecycle.LiveData
import com.krs.community.entities.RoomMember
import com.krs.community.responses.searchByKeywordsResponse

interface ByKeywordListener {
    fun refreshList()
    fun getRoomMembers(response: List<RoomMember>)
    fun getRoomFailure(message:String)
    fun getMembers(response: searchByKeywordsResponse)
    fun getFailure(message:String)
}