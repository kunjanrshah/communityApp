package com.krs.community.listeners

import com.krs.community.responses.ReminderResponse

interface ReminderListener {
    fun reminderResponse(response: ReminderResponse)
    suspend fun getFailure(message: String)
}