package com.krs.community.repositories

import com.krs.community.model.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class RegisterRepository(
    private val api:ApiServices
): SafeApiRequest() {

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