package com.krs.community.interfaces

import com.krs.community.entities.RoomMember

interface RoomMemberListener {
    fun refreshList()
    fun getRoomMembers(response: List<RoomMember>)
    suspend fun getFailure(message:String)
}