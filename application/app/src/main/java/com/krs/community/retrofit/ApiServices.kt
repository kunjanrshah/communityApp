package com.krs.community.retrofit


import com.krs.community.app.AppController
import com.krs.community.model.*
import com.krs.community.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {

    @POST(AppConstants.UrlPath.GET_SUBCASTE)
    suspend fun getUserLastName(): Response<LastNameModel>

    @POST(AppConstants.UrlPath.GET_STATE)
    suspend fun getUserState(): Response<StatesModel>

    @POST(AppConstants.UrlPath.GET_CITIES)
    suspend fun getUserCities(@Body request: AppConstants.CitiesRequest): Response<CitiesModel>

    @POST(AppConstants.UrlPath.GET_SEARCH_BY_CITY)
    suspend fun getSearchByCity(@Body request: SearchByCityData): Response<SearchByCityModel>

    @POST(AppConstants.UrlPath.GET_SUBCOMM)
    suspend fun getSubCommunity(): Response<SubCommModel>

    @POST(AppConstants.UrlPath.GET_LOCALCOMM)
    suspend fun getLocalCommunity(@Body request: AppConstants.LocalCommRequest): Response<LocalCommModel>

    @POST(AppConstants.UrlPath.GET_REGISTER)
    suspend fun getUserRegister(@Body request: AppConstants.UserRegister): Response<RegisterModel>

    @POST(AppConstants.UrlPath.GET_LOGIN)
    suspend fun getUserLogin(@Body request: AppConstants.LoginRequest): Response<LoginModel>

    @POST(AppConstants.UrlPath.GET_FORGOT_PASS)
    suspend fun getUserForgotPass(@Body request: AppConstants.ForgotPass): Response<ForgotPassModel>

    @POST(AppConstants.UrlPath.GET_CHANGE_PASS)
    suspend fun getUserChangePass(@Body request: AppConstants.ChangePass): Response<ChangePassModel>

    @POST(AppConstants.UrlPath.GET_SEARCH_DATA)
    suspend fun getSearchData(@Body request: AppConstants.ChangePass): Response<SearchDataModel>

    companion object{
        operator fun invoke():ApiServices{
            return AppController.mApplication?.retrofitBase?.apiServices!!
        }
    }
}
