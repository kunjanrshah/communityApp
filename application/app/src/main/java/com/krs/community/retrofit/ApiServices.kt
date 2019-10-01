package com.krs.community.retrofit


import com.krs.community.app.AppController
import com.krs.community.model.RBCities
import com.krs.community.model.RBStates
import com.krs.community.utils.AppConstants


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {

    @POST(AppConstants.UrlPath.GET_STATE)
    fun getUserState(): Call<RBStates>

    @POST(AppConstants.UrlPath.GET_SUBCASTE)
    fun getUserLastName(): Call<ResponseBody>

    @POST(AppConstants.UrlPath.GET_CITIES)
    fun getUserCities(@Body request: AppConstants.CitiesRequest): Call<RBCities>

    companion object{
        operator fun invoke():ApiServices{
            return AppController.getInstance().retrofitBase.apiServices
        }
    }
}
