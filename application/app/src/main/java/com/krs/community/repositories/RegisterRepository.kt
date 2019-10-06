package com.krs.community.repositories

import com.krs.community.model.LocalComm
import com.krs.community.model.RBCities
import com.krs.community.model.RBStates
import com.krs.community.model.SubComm
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants
import retrofit2.Response

class RegisterRepository {

    private var registerRepository: RegisterRepository? = null

    fun getInstance(): RegisterRepository {
        if (registerRepository == null) {
            registerRepository = RegisterRepository()
        }
        return registerRepository as RegisterRepository
    }

    suspend fun userState(): Response<RBStates> {
        return ApiServices().getUserState()
    }

    suspend fun userCity(id: Int): Response<RBCities> {
      return  ApiServices().getUserCities(AppConstants.CitiesRequest(id.toString()))
    }

    suspend fun userLastName() = ApiServices().getUserLastName()


    suspend fun userSubCommunity():Response<SubComm> {
       return ApiServices().getSubCommunity()
    }

    suspend fun getLocalCommunity(id: Int): Response<LocalComm> {
      return  ApiServices().getLocalCommunity(AppConstants.LocalCommRequest(id.toString()))
    }
}