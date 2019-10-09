package com.krs.community.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.LoginActivity
import com.krs.community.interfaces.IRegisterListener
import com.krs.community.repositories.RegisterRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.Coroutines
import com.krs.community.utils.NoInternetException
import com.krs.community.utils.Utility

class RegisterViewModel(
        private val registerRepository:RegisterRepository,
        var app: Application) : AndroidViewModel(app) {

    var fname: String? = null
    var email: String? = null
    var pass: String? = null
    var cpass: String? = null
    var address: String? = null
    var iRegisterListener:IRegisterListener?=null
    var TAG:String=RegisterViewModel::class.java.simpleName

    //private lateinit var registerRepository: RegisterRepository

    fun init() {
       // registerRepository = RegisterRepository().getInstance()
    }

   fun getUserStates() {
        Coroutines.main {
            try{
                val response=registerRepository.userState()
                response.data?.let {
                    iRegisterListener?.getStates(response.data)
                    return@main
                }
                iRegisterListener?.getFailure(response.message as String)
            }catch (e:ApiException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }catch (e:NoInternetException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }catch (e:Exception){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }
        }
    }

    fun fetchCitiesForStateId(id: Int){
        Coroutines.main {
            try{
                val response=registerRepository.userCity(id)
                response.data?.let {
                    iRegisterListener?.getCities(response.data)
                    return@main
                }
                iRegisterListener?.getFailure(response.message)
            }catch (e:ApiException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }catch (e:NoInternetException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }catch (e:Exception){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }
        }
    }

    fun getUserLastName() {
         Coroutines.main {
             try {
                 val response=registerRepository.userLastName()
                 response.data?.let {
                     iRegisterListener?.getLastname(response.data)
                     return@main
                 }
                 iRegisterListener?.getFailure(response.message)
             }catch (e:ApiException){
                 e.message?.let { iRegisterListener?.getFailure(it) }
             }catch (e:NoInternetException){
                 e.message?.let { iRegisterListener?.getFailure(it) }
             }catch (e:Exception){
                 e.message?.let { iRegisterListener?.getFailure(it) }
             }
         }
    }

    fun getLstSubCommunity() {
        Coroutines.main {
            try {
                val response=registerRepository.userSubCommunity()
                response.data?.let {
                    iRegisterListener?.getSubCommunity(response.data)
                    return@main
                }
                iRegisterListener?.getFailure(response.message)
            }catch (e:ApiException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }catch (e:NoInternetException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }
        }
    }

    fun getLstLocalCommunity(id: Int) {
        Coroutines.main {
            try {
                val response=registerRepository.getLocalCommunity(id)
                response.data?.let {
                    iRegisterListener?.getLocalCommunity(response.data)
                    return@main
                }
                iRegisterListener?.getFailure(response.message)
            }catch (e:ApiException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }catch (e:NoInternetException){
                e.message?.let { iRegisterListener?.getFailure(it) }
            }catch (e:Exception){
                e.message?.let { iRegisterListener?.getFailure(it) }
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
