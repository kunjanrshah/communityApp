package com.krs.community.listeners

import com.krs.community.entities.RoomMember

interface RoomMemberListener {
    fun refreshList()
    fun getRoomMembers(response: List<RoomMember>)
    suspend fun getFailure(message:String)
}