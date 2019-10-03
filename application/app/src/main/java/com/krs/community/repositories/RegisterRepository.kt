package com.krs.community.repositories

import androidx.lifecycle.MutableLiveData
import com.krs.community.model.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository {

    private var registerRepository: RegisterRepository? = null

    fun getInstance(): RegisterRepository {
        if (registerRepository == null) {
            registerRepository = RegisterRepository()
        }
        return registerRepository as RegisterRepository
    }

    fun userState(): MutableLiveData<RBStates> {
        val userstate = MutableLiveData<RBStates>()
        ApiServices().getUserState()
                .enqueue(object : Callback<RBStates> {
                    override fun onFailure(call: Call<RBStates>, t: Throwable) {
                        userstate.value = null
                    }
                    override fun onResponse(call: Call<RBStates>, response: Response<RBStates>) {
                        if (response.isSuccessful) {
                            userstate.value = response.body()
                        }
                    }
                })
        return userstate
    }

    fun userCity(id: Int,usercities:MutableLiveData<RBCities>) {

        ApiServices().getUserCities(AppConstants.CitiesRequest(id.toString()))
                .enqueue(object : Callback<RBCities> {
                    override fun onFailure(call: Call<RBCities>, t: Throwable) {
                        usercities.value = null
                    }

                    override fun onResponse(call: Call<RBCities>, response: Response<RBCities>) {
                        if (response.isSuccessful) {
                            usercities.value = response.body()
                        }
                    }
                })
    }

    fun userLastName():MutableLiveData<LastName> {
        val userLastName = MutableLiveData<LastName>()
        ApiServices().getUserLastName()
                .enqueue(object : Callback<LastName> {
                    override fun onFailure(call: Call<LastName>, t: Throwable) {
                        userLastName.value = null
                    }

                    override fun onResponse(call: Call<LastName>, response: Response<LastName>) {
                        if (response.isSuccessful) {
                            userLastName.value = response.body()
                        }
                    }
                })
        return userLastName
    }

    fun userSubCommunity():MutableLiveData<SubComm> {
        val userSubCommunity = MutableLiveData<SubComm>()
        ApiServices().getSubCommunity()
                .enqueue(object : Callback<SubComm> {
                    override fun onFailure(call: Call<SubComm>, t: Throwable) {
                        userSubCommunity.value = null
                    }

                    override fun onResponse(call: Call<SubComm>, response: Response<SubComm>) {
                        if (response.isSuccessful) {
                            userSubCommunity.value = response.body()
                        }
                    }
                })
        return userSubCommunity
    }

    fun getLocalCommunity(id: Int,userLocalComm:MutableLiveData<LocalComm>) {
        ApiServices().getLocalCommunity(AppConstants.LocalCommRequest(id.toString()))
                .enqueue(object : Callback<LocalComm> {
                    override fun onFailure(call: Call<LocalComm>, t: Throwable) {
                        userLocalComm.value = null
                    }

                    override fun onResponse(call: Call<LocalComm>, response: Response<LocalComm>) {
                        if (response.isSuccessful) {
                            userLocalComm.value = response.body()
                        }
                    }
                })
    }

}