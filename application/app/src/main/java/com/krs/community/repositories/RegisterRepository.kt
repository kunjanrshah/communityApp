package com.krs.community.repositories

import com.krs.community.model.RegisterModel
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class RegisterRepository(
        private val api: ApiServices
): SafeApiRequest() {

    private val TAG: String = DashboardRepository::class.java.simpleName

    suspend fun getUserRegister(userRegister: AppConstants.UserRegister):RegisterModel  {
        return apiRequest{
            api.getUserRegister(userRegister)
        }
    }
}