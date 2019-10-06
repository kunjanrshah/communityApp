package com.krs.community.retrofit


import com.krs.community.app.AppController
import com.krs.community.model.*
import com.krs.community.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {

    @POST(AppConstants.UrlPath.GET_STATE)
    suspend fun getUserState(): Response<RBStates>

    @POST(AppConstants.UrlPath.GET_SUBCASTE)
    suspend fun getUserLastName(): LastName

    @POST(AppConstants.UrlPath.GET_SUBCOMM)
    suspend fun getSubCommunity(): Response<SubComm>

    @POST(AppConstants.UrlPath.GET_LOCALCOMM)
    suspend fun getLocalCommunity(@Body request: AppConstants.LocalCommRequest): Response<LocalComm>

    @POST(AppConstants.UrlPath.GET_CITIES)
    suspend fun getUserCities(@Body request: AppConstants.CitiesRequest): Response<RBCities>

    companion object{
        operator fun invoke():ApiServices{
            return AppController.getInstance().retrofitBase.apiServices
        }
    }
}
