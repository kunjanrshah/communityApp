package com.krs.community.repositories

import com.krs.community.model.RegisterModel
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class RegisterRepository(
        private val api: ApiServices
): SafeApiRequest() {

    private val TAG: String = DashboardRepository::class.java.simpleName

    /*   suspend fun userState(): StateResponse {
           return apiRequest{
               val mJSONObject= JSONObject()
               mJSONObject.put("date","")
               val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
               api.getUserState(updated)
           }
       }

       suspend fun getLastName(): LiveData<List<String>> {
           return withContext(Dispatchers.IO) {
               db.getLastNameDao().getLastName()
           }
       }

       suspend fun userCity(id: Int): ResponseModel {
           return apiRequest{
               val mJSONObject= JSONObject()
               mJSONObject.put("state_id",id.toString())
               val request=  JsonParser().parse(mJSONObject.toString()) as JsonObject
               api.getUserCities(request)
           }
       }

       suspend fun userLastName(): LastNameResponse {
           val date= db.getLastUpdatedDao().getLastUpdatedDate("last_name")
           val mJSONObject= JSONObject()
           mJSONObject.put("date",date)
           val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
           return apiRequest{ api.getUserLastName(updated)  }
       }

       suspend fun userSubCommunity(): SubCommResponse? {
          return apiRequest{
              val mJSONObject= JSONObject()
              mJSONObject.put("date","")
              val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
              api.getSubCommunity(updated)
          }
       }

       suspend fun getLocalCommunity(id: Int): ResponseModel {
           return apiRequest{
               val mJSONObject= JSONObject()
               mJSONObject.put("subcommunityid",id.toString())
               val request=  JsonParser().parse(mJSONObject.toString()) as JsonObject
               api.getLocalCommunity(request)
           }
       }*/

    suspend fun getUserRegister(userRegister: AppConstants.UserRegister):RegisterModel  {
        return apiRequest{
            api.getUserRegister(userRegister)
        }
    }
}