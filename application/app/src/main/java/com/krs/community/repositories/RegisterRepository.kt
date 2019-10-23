package com.krs.community.repositories

import com.krs.community.model.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class RegisterRepository(
    private val api:ApiServices
): SafeApiRequest() {

    suspend fun userState(): StatesModel {
        return apiRequest{
            api.getUserState()
        }
    }

    suspend fun userCity(id: Int): CitiesModel {
        return apiRequest{
            api.getUserCities(AppConstants.CitiesRequest(id.toString()))
        }
    }

    suspend fun userLastName(): LastNameModel {
        return apiRequest{ api.getUserLastName()  }
    }

    suspend fun userSubCommunity(): SubCommModel {
       return apiRequest{
           api.getSubCommunity()
       }
    }

    suspend fun getLocalCommunity(id: Int): LocalCommModel {
        return apiRequest{
            api.getLocalCommunity(AppConstants.LocalCommRequest(id.toString()))
        }
    }

    suspend fun getUserRegister(userRegister: AppConstants.UserRegister):RegisterModel  {
        return apiRequest{
            api.getUserRegister(userRegister)
        }
    }
}