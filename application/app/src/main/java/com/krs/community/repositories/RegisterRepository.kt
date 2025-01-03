package com.krs.community.repositories

import com.google.gson.JsonObject
import com.krs.community.model.RegisterModel
import com.krs.community.retrofit.ApiServices
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RegisterRepository(
        private val api: ApiServices
) : SafeApiRequest() {

    private val TAG: String = DashboardRepository::class.java.simpleName

//    suspend fun getUserRegister(jsonObject: JsonObject): RegisterModel {
//        return apiRequest {
//            api.getUserRegister(jsonObject)
//        }
//    }

    suspend fun getUserRegister(profile: MultipartBody.Part?,fname: RequestBody?,father: RequestBody?,bdate: RequestBody?,lastName: RequestBody?,email: RequestBody?,mobile: RequestBody?,gender: RequestBody?,pass: RequestBody?,address: RequestBody?,state: RequestBody?,city: RequestBody?,native: RequestBody?, subComm: RequestBody?,local: RequestBody?, marital: RequestBody?,relation: RequestBody?,isAdmin: RequestBody?): RegisterModel {
        return apiRequest {
            api.getUserRegister(profile,fname,father,bdate,lastName,email,mobile,gender,pass,address,state,city,native, subComm,local, marital,relation,isAdmin)
        }
    }
}