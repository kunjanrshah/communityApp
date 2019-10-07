package com.krs.community.repositories

import com.krs.community.model.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class RegisterRepository(
    private val api:ApiServices
): SafeApiRequest() {

    private var registerRepository: RegisterRepository? = null

    fun getInstance(): RegisterRepository {
        if (registerRepository == null) {
            registerRepository = RegisterRepository(api)

        }
        return registerRepository as RegisterRepository
    }

    suspend fun userState(): RBStates {
        return apiRequest{
            api.getUserState()
        }
    }

    suspend fun userCity(id: Int): RBCities {
        return apiRequest{
            api.getUserCities(AppConstants.CitiesRequest(id.toString()))
        }
    }

    suspend fun userLastName():LastName {
        return apiRequest{ api.getUserLastName()  }
    }

    suspend fun userSubCommunity():SubComm {
       return apiRequest{
           api.getSubCommunity()
       }
    }

    suspend fun getLocalCommunity(id: Int): LocalComm {
        return apiRequest{
            api.getLocalCommunity(AppConstants.LocalCommRequest(id.toString()))
        }
    }
}