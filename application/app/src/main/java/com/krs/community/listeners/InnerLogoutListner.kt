package com.krs.community.listeners

import com.krs.community.responses.UserInnerLogoutResponse


interface InnerLogoutListner {
    fun userLogout(response: UserInnerLogoutResponse)
    suspend fun getFailure(message:String)
}