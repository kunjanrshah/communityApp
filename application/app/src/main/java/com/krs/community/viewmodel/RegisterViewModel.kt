package com.krs.community.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.LoginActivity
import com.krs.community.interfaces.IRegisterListener
import com.krs.community.model.LocalComm
import com.krs.community.model.RBCities
import com.krs.community.repositories.RegisterRepository
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import kotlinx.coroutines.Dispatchers

class RegisterViewModel(var app: Application) : AndroidViewModel(app) {

    var fname: String? = null
    var email: String? = null
    var pass: String? = null
    var cpass: String? = null
    var address: String? = null
    var iRegisterListener:IRegisterListener?=null
    var TAG:String=RegisterViewModel::class.java.simpleName

    private lateinit var registerRepository: RegisterRepository

    var lstCities: MutableLiveData<RBCities> =  MutableLiveData<RBCities>()
    var lstLocalComm: MutableLiveData<LocalComm> =  MutableLiveData<LocalComm>()

    fun init() {
        registerRepository = RegisterRepository().getInstance()
    }
    fun getUserStates() {
        Coroutines.main {
            val response=registerRepository.userState()
            if(response.isSuccessful){
                response.body()?.data?.let { iRegisterListener?.getStates(it) }
            }else{
                response.code().let {
                    iRegisterListener?.getFailure(response.body()?.message as String)
                }
            }
        }
    }
    fun fetchCitiesForStateId(id: Int){
        Coroutines.main {
            val response=registerRepository.userCity(id)
            if(response.isSuccessful){
                response.body()?.data?.let { iRegisterListener?.getCities(it) }
            }else{
                response.code().let {
                    iRegisterListener?.getFailure(response.body()?.message as String)
                }
            }
        }
    }

    val lastname = liveData(Dispatchers.IO){

    }

    fun getUserLastName() {




        Coroutines.main {
            val response=registerRepository.userLastName()
            if(response.isSuccessful){
                response.body().data.let { iRegisterListener?.getLastname(it) }
            }else{
                response.code().let {
                    iRegisterListener?.getFailure(response.body().message as String)
                }
            }
        }
    }

    fun getLstSubCommunity() {
        Coroutines.main {
            val response=registerRepository.userSubCommunity()
            if(response.isSuccessful){
                response.body()?.data?.let { iRegisterListener?.getSubCommunity(it) }
            }else{
                response.code().let {
                    iRegisterListener?.getFailure(response.body()?.message as String)
                }
            }
        }
    }

    fun getLstLocalCommunity(id: Int) {
        Coroutines.main {
            val response=registerRepository.getLocalCommunity(id)
            if(response.isSuccessful){
                response.body()?.data?.let { iRegisterListener?.getLocalCommunity(it) }
            }else{
                response.code().let {
                    iRegisterListener?.getFailure(response.body()?.message as String)
                }
            }
        }
    }

    fun onRegisterButtonClick(activity: Activity) {
        val mIntent = Intent(activity, DashboardActivity::class.java)
        activity.startActivity(mIntent)
        activity.finish()
        Log.d(TAG,"onRegisterButtonClick")
    }

    fun onTextAlreadyClicked(activity: Activity) {
        val mIntent = Intent(activity, LoginActivity::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(mIntent)
        activity.finish()
        Utility.fade(activity)
    }

    fun onHowRegisterClicked(activity: Activity) {
        Utility.watchYoutubeVideo(activity, activity.resources.getString(R.string.login_1))
    }
}
