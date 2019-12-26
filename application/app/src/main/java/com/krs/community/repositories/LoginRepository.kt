package com.krs.community.repositories

import com.krs.community.model.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class LoginRepository(
    private val api:ApiServices
): SafeApiRequest() {

    suspend fun getLogin(userLogin: AppConstants.LoginRequest): LoginResponse {
        return apiRequest{
            api.getUserLogin(userLogin)
        }
    }
}