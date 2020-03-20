package com.krs.community.listeners

import com.krs.community.model.*

interface GetRemindersListener {
    fun userReminders(response: GetRemindersResponse)
    suspend fun getFailure(message:String)
}