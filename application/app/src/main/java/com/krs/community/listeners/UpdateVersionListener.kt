package com.krs.community.listeners

import com.krs.community.responses.UserStatusResponse

interface UpdateVersionListener {
    fun getSuccess(response: UserStatusResponse)
    fun getFailure(msg: String)
}