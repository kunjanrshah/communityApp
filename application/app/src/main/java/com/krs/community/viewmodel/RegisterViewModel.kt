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
import com.krs.community.model.RBCities
import com.krs.community.model.RBStates
import com.krs.community.utils.Utility
import repositories.RegisterRepository


class RegisterViewModel(app: Application) : AndroidViewModel(app) {

    var fname: String? = null
    var email: String? = null
    var pass: String? = null
    var cpass: String? = null
    var address: String? = null
    var TAG:String=RegisterViewModel::class.java.simpleName

    private var registerRepository: RegisterRepository? = null
    var lstCities: MutableLiveData<RBCities>? = null

    fun init() {
        registerRepository = RegisterRepository().getInstance()
    }

    fun getUserStates(): MutableLiveData<RBStates> {
        return registerRepository!!.userState()
    }

    fun fetchCitiesForStateId(id: Int) {
        lstCities = registerRepository?.userCity(id)!!
    }

    fun getCities(): MutableLiveData<RBCities>? {
        return lstCities
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
