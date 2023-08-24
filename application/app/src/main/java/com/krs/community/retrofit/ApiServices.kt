package com.krs.community.retrofit


import com.google.gson.JsonObject
import com.krs.community.app.AppController
import com.krs.community.model.*
import com.krs.community.responses.*
import com.krs.community.utils.AppConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

@JvmSuppressWildcards
interface ApiServices {

    @POST(AppConstants.UrlPath.UPDATE_PROFILE)
    suspend fun updateProfile(@Body request: JsonObject): Response<UpdateProfileResponse>

    @POST(AppConstants.UrlPath.GET_COMMITTEE)
    suspend fun getCommittee(@Body request: JsonObject): Response<CommitteeResponse>

    @POST(AppConstants.UrlPath.GET_DESIGNATION)
    suspend fun getDesignation(@Body request: JsonObject): Response<DesignationResponse>

    @POST(AppConstants.UrlPath.GET_SUB_COMM)
    suspend fun getSubCommunity(@Body request: JsonObject): Response<SubCommResponse>

    @POST(AppConstants.UrlPath.GET_LOCAL_COMM)
    suspend fun getLocalCommunity(@Body request: JsonObject): Response<ResponseModel>

    @POST(AppConstants.UrlPath.GET_LIST_LOCAL_COMM)
    suspend fun getListLocalCommunity(@Body request: JsonObject): Response<LocalCommResponse>

    @POST(AppConstants.UrlPath.GET_LIST_CITY)
    suspend fun getListCity(@Body request: JsonObject): Response<CityResponse>

    @POST(AppConstants.UrlPath.GET_LIST_BUSINESS_SUB_CATEGORY)
    suspend fun getListBusinessSubCategory(@Body request: JsonObject): Response<BusinessSubResponse>

    @POST(AppConstants.UrlPath.GET_RELATIONS)
    suspend fun getRelations(@Body request: JsonObject): Response<RelationsResponse>

    @POST(AppConstants.UrlPath.GET_STATE)
    suspend fun getUserState(@Body request: JsonObject): Response<StateResponse>

    @POST(AppConstants.UrlPath.GET_CITIES)
    suspend fun getUserCities(@Body request: JsonObject): Response<CityResponse>

    @POST(AppConstants.UrlPath.GET_NATIVE)
    suspend fun getNative(@Body request: JsonObject): Response<NativeResponse>

    @POST(AppConstants.UrlPath.GET_SUBCASTE)
    suspend fun getUserLastName(@Body request: JsonObject): Response<LastNameResponse>

    @POST(AppConstants.UrlPath.GET_GOTRA)
    suspend fun getGotra(@Body request: JsonObject): Response<GotraResponse>

    @POST(AppConstants.UrlPath.GET_EDUCATION)
    suspend fun getEducation(@Body request: JsonObject): Response<EducationResponse>

    @POST(AppConstants.UrlPath.GET_ACTIVITY)
    suspend fun getActivity(@Body request: JsonObject): Response<ActivityResponse>

    @POST(AppConstants.UrlPath.GET_BUSINESS_CATEGORY)
    suspend fun getBusinessCategory(@Body request: JsonObject): Response<BusinessCategoryResponse>

    @POST(AppConstants.UrlPath.GET_BUSINESS_SUB_CATEGORY)
    suspend fun getBusinessSubCategory(@Body request: JsonObject): Response<ResponseModel>

    @POST(AppConstants.UrlPath.GET_OCCUPATION)
    suspend fun getOccupation(@Body request: JsonObject): Response<OccupationResponse>

    @POST(AppConstants.UrlPath.ADD_MEMBER)
    suspend fun addMember(@Body request: JsonObject): Response<UpdateProfileResponse>

    @POST(AppConstants.UrlPath.DELETE_MEMBER)
    suspend fun deleteMember(@Body request: JsonObject): Response<DeleteProfileResponse>

    @POST(AppConstants.UrlPath.GET_STATISTICS)
    suspend fun getStatistics(@Body request: JsonObject): Response<StatisticResponse>

    @POST(AppConstants.UrlPath.GET_SMART_FILTER)
    suspend fun getSearchByFilter(@Body request: JsonObject): Response<SmartFilterResponse>

    @POST(AppConstants.UrlPath.GET_USERS_BYDATE)
    suspend fun getSearchByDate(@Body request: JsonObject): Response<SmartFilterResponse>

    @POST(AppConstants.UrlPath.GET_SHARED_PROFILE)
    suspend fun getSharedProfile(@Body request: JsonObject): Response<SmartFilterResponse>

    @POST(AppConstants.UrlPath.GET_EVENTS)
    suspend fun getNewsList(@Body request: JsonObject): Response<NewsResponse>

    @POST(AppConstants.UrlPath.GET_SEARCH_NEAR_BY)
    suspend fun getSearchByDistance(@Body request: ByDistanceModel): Response<ByDistanceResponse>

    @POST(AppConstants.UrlPath.SEARCH_BY_KEYWORDS)
    suspend fun getSearchByKeywords(@Body request: JsonObject): Response<searchByKeywordsResponse>

    @POST(AppConstants.UrlPath.CHANGE_STATUS)
    suspend fun changeStatus(@Body request: JsonObject): Response<searchByKeywordsResponse>

    @POST(AppConstants.UrlPath.CHANGE_ROLE)
    suspend fun changeRole(@Body request: JsonObject): Response<searchByKeywordsResponse>

    @POST(AppConstants.UrlPath.GET_COMMITTEE_USERS)
    suspend fun getUsersInCommittee(@Body request: JsonObject): Response<SmartFilterResponse>

    @POST(AppConstants.UrlPath.GET_INACTIVE_USERS)
    suspend fun getInActiveUsers(@Body request: JsonObject): Response<SmartFilterResponse>

    @POST(AppConstants.UrlPath.GET_DOCUMENT)
    suspend fun getDocumentList(@Body request: JsonObject): Response<UploadedFilesResponse>

    @POST(AppConstants.UrlPath.GET_CONTACT_LIST)
    suspend fun getUserByMobile(@Body request: JsonObject): Response<SmartFilterResponse>

    @POST(AppConstants.UrlPath.GET_UPDATED_VERSION)
    suspend fun getUpdatedVersion(@Body request: JsonObject): Response<UserStatusResponse>

    @POST(AppConstants.UrlPath.GET_FAMILY_MEMBER)
    suspend fun getFamilyMembers(@Body request: JsonObject): Response<FamilyDetailResponse>

    @POST(AppConstants.UrlPath.GET_USER_STATUS)
    suspend fun getUserStatus(@Body request: JsonObject): Response<UserStatusResponse>

    @POST(AppConstants.UrlPath.INNER_LOGIN)
    suspend fun innerLogin(@Body request: JsonObject): Response<LoginResponse>

    @POST(AppConstants.UrlPath.INNER_LOGOUT)
    suspend fun getUserLogout(@Body request: JsonObject): Response<UserInnerLogoutResponse>

    @POST(AppConstants.UrlPath.GET_SEARCH_BY_CITY)
    suspend fun getSearchByCity(@Body request: SearchByCityData): Response<SearchByCityModel>

    /*@POST(AppConstants.UrlPath.GET_REGISTER)
    suspend fun getUserRegister(@Body request: JsonObject): Response<RegisterModel>*/

    @POST(AppConstants.UrlPath.GET_LOGIN)
    suspend fun getUserLogin(@Body request: AppConstants.LoginRequest): Response<LoginResponse>

    @POST(AppConstants.UrlPath.GET_CHANGE_PASS)
    suspend fun changePassword(@Body request: JsonObject): Response<LoginResponse>

    @POST(AppConstants.UrlPath.GET_FORGOT_PASS)
    suspend fun forgotPassword(@Body request: JsonObject): Response<LoginResponse>

    @POST(AppConstants.UrlPath.GET_MASTER_UPDATE_COUNTS)
    suspend fun getMasterUpdate(@Body request: JsonObject): Response<MasterUpdateResponse>

    @POST(AppConstants.UrlPath.SET_REMINDER)
    suspend fun setReminder(@Body request: JsonObject): Response<ReminderResponse>

    @POST(AppConstants.UrlPath.GET_USER_PROFILE)
    suspend fun getUserProfile(@Body request: JsonObject): Response<LoginResponse>

    @POST(AppConstants.UrlPath.SEND_MAIL)
    suspend fun sendMail(@Body request: JsonObject): Response<LoginResponse>

    @Multipart
    @POST(AppConstants.UrlPath.UPLOAD_PROFILE_IMAGE)
    suspend fun uploadProfileImage(@Part file: MultipartBody.Part, @Part("id") id: RequestBody, @Part("type") type: RequestBody): Response<JsonObject>

    @Multipart
    @POST(AppConstants.UrlPath.GET_REGISTER)
    suspend fun getUserRegister(@Part file: MultipartBody.Part?, @Part("first_name") fname: RequestBody?, @Part("father_name") father: RequestBody?, @Part("birth_date") bdate: RequestBody?, @Part("sub_cast_id") lastName: RequestBody?, @Part("email_address") email: RequestBody?, @Part("mobile") mobile: RequestBody?,
    @Part("gender") gender: RequestBody?, @Part("plain_password") pass: RequestBody?, @Part("address") address: RequestBody?, @Part("state_id") state: RequestBody?, @Part("city_id") city: RequestBody?, @Part("native_place_id") native: RequestBody?,
    @Part("sub_community_id") subComm: RequestBody?, @Part("local_community_id") local: RequestBody?, @Part("marital_status") marital: RequestBody?, @Part("relation_id") relation: RequestBody?, @Part("is_admin") isAdmin: RequestBody?): Response<RegisterModel>

    @Multipart
    @POST(AppConstants.UrlPath.UPLOAD_FILES)
    suspend fun deleteUploadedFile(@Part("file_id") id: RequestBody): Response<JsonObject>


    @Multipart
    @POST(AppConstants.UrlPath.CREATE_EVENT)
    suspend fun createEvent(@Part file: List<MultipartBody.Part>, @Part("id") id: RequestBody, @Part("user_id") user_id: RequestBody, @Part("access_token") access_token: RequestBody, @Part("params") params: RequestBody, @Part("youtube[]") youtube: List<RequestBody>): Response<JsonObject>

    companion object {
        operator fun invoke(): ApiServices {
            return AppController.mApplication.retrofitBase.apiServices
        }
    }
}
