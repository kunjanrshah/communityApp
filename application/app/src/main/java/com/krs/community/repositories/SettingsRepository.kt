package com.krs.community.repositories

import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class SettingsRepository(
        private val api: ApiServices
): SafeApiRequest() {

    /*suspend fun changePass(changePass:AppConstants.ChangePass):ChangePassModel {
        return apiRequest{
            api.getUserChangePass(changePass)
        }
    }*/
}