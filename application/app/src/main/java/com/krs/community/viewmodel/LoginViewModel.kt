package com.krs.community.viewmodel

import android.app.Application
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github.squti.guru.Guru
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.krs.community.R
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ILoginListener
import com.krs.community.repositories.LoginRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.AppConstants
import com.krs.community.utils.NoInternetException
import com.krs.community.utils.Utility

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


class LoginViewModel(private val loginRepository: LoginRepository,
                     var app: Application) : AndroidViewModel(app) {

    lateinit var iLoginListener: ILoginListener
    var TAG: String = LoginViewModel::class.java.simpleName
    var job_login: CompletableJob? = null
    var job_forgot: CompletableJob? = null
    var mobile: String? = ""
    var country_code: String? = ""
    val otp_lable = ""
    var otp_timer: ObservableField<String>? = ObservableField()
    var mAuth: FirebaseAuth? = null
    var cTimer: CountDownTimer? = null
    var status = MutableLiveData<Boolean?>()
    var stopTime = MutableLiveData<Boolean?>()

    fun startTimer() {
        cTimer = object : CountDownTimer(1000 * 60 * 2, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                var mins = ""
                var secs = ""
                if (minutes < 10) {
                    mins = "0$minutes"
                } else {
                    mins = minutes.toString()
                }
                if (seconds < 10) {
                    secs = "0$seconds"
                } else {
                    secs = seconds.toString()
                }
                otp_timer?.set("$mins:$secs")
                stopTime.value=false
                Log.d(TAG, "remaining time: " + otp_timer?.get())
            }

            override fun onFinish() {
                stopTime.value=true

                Log.d(TAG, "onFinish")
            }
        }
        cTimer?.start()
    }

    //cancel timer
    fun cancelTimer() {
        if (cTimer != null)
            cTimer?.cancel()
    }

    fun cancelAllJobs() {
        job_login?.cancel()
        job_forgot?.cancel()
    }

    fun loginWithOTP() {
        try {
            val loginRequest = AppConstants.LoginRequest()
            loginRequest.username = mobile
            loginRequest.hashcode = Guru.getString(app.applicationContext.getString(R.string.hash_key), "")
            loginRequest.login_type = "1"
            getLoginUser(loginRequest)
        } catch (e: Exception) {
            Utility.hideSweetProgress()
            e.printStackTrace()
        }
    }

    fun loginWithMobile(number: String) {
        try {
            val loginRequest = AppConstants.LoginRequest()
            loginRequest.username = number
            loginRequest.login_type = "2"
            getLoginUser(loginRequest)
        } catch (e: Exception) {
            Utility.hideSweetProgress()
            e.printStackTrace()
        }
    }

    fun loginWithGoogle(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
        Log.d(TAG, "firebaseAuthWithGoogle:" + account?.id!!)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth?.currentUser
                if (user != null) {
                    Log.d(TAG, "email: " + user.email + " phone: " + user.phoneNumber + " photo: " + user.photoUrl)
                    val loginRequest = AppConstants.LoginRequest()
                    Log.e(TAG, " email: " + user.email + " phone: " + user.phoneNumber + " Id: " + user.uid + " Name: " + user.displayName)
                    val loginuser = user.email.toString()
                    if (loginuser.isNotEmpty() && loginuser.isNotBlank()) {
                        loginRequest.username = loginuser
                        loginRequest.login_type = "0"
                        getLoginUser(loginRequest)
                    } else {
                        status.value=false
                        Utility.hideSweetProgress()
                    }
                }
            } else {
                Utility.hideSweetProgress()
                status.value=false
                Log.w(TAG, "signInWithCredential:failure", task.exception)
            }
        }
    }

    fun loginWithFB(email: String){
        try {
            val loginRequest = AppConstants.LoginRequest()
            //fb_profile_url = data.getString("url")
            if (email.isEmpty()) {
                status.value=false
                return
            }
            loginRequest.username = email
            loginRequest.login_type = "0"
            getLoginUser(loginRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLoginUser(req_login: AppConstants.LoginRequest) {
        if (isNetworkConnected(app.applicationContext)) {
            job_login = Job()
            job_login.let { thejob ->
                CoroutineScope(IO + thejob!!).launch {

                    try {
                        val response = loginRepository.getLogin(req_login)
                        response.let {
                            withContext(Dispatchers.Main) {
                                iLoginListener.userLogin(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                iLoginListener.getFailure(it)
                            }
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                iLoginListener.getFailure(it)
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                iLoginListener.getFailure(it)
                            }
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

}