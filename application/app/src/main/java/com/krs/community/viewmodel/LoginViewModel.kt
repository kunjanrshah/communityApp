package com.krs.community.viewmodel

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.krs.community.interfaces.ILoginListener
import com.krs.community.repositories.LoginRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.AppConstants
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.concurrent.TimeUnit
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.github.squti.guru.Guru
import com.krs.community.R
import com.krs.community.utils.Utility


class LoginViewModel(private val loginRepository: LoginRepository,
                     var app: Application): AndroidViewModel(app) {

    lateinit  var iLoginListener: ILoginListener
    var TAG: String = LoginViewModel::class.java.simpleName
    var job_login: CompletableJob? = null
    var job_forgot: CompletableJob? = null
    var mobile:String?=""
    var country_code:String?=""
    var otp_timer: ObservableField<String>?= ObservableField()

    var cTimer: CountDownTimer? = null

    fun startTimer() {
        cTimer = object : CountDownTimer(1000*60*2, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                var mins=""
                var secs=""
                if(minutes<10){
                    mins="0$minutes"
                }else{
                    mins=minutes.toString()
                }
                if(seconds<10){
                    secs="0$seconds"
                }else{
                    secs=seconds.toString()
                }
                otp_timer?.set("$mins:$secs")

                Log.d(TAG,"remaining time: "+otp_timer?.get())
            }
            override fun onFinish() {
                Log.d(TAG,"onFinish")
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

    fun LoginUsingMobile() {
        if (Utility.isOnline(app)) {

            if (mobile!!.isNotEmpty()) {
                try {
                    val loginRequest = AppConstants.LoginRequest()
                    loginRequest.username = mobile
                    loginRequest.hashcode = Guru.getString(app.getString(R.string.hash_key),"")
                    if (Utility.isValidMobile(loginRequest.username)) {
                        loginRequest.login_type = "1"
                        Utility.startProgress(app, "Seat back & Relax!", "Loading...")
                        getLoginUser(loginRequest)
                    } else {
                        Utility.alert(app, app.resources.getString(R.string.err_msg_invalid_mobile))
                        return
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Utility.alert(app, app.getString(R.string.err_msg_blank))
                return
            }
        }
    }

    fun userForgotPassword(req_forgot:AppConstants.ForgotPass){
        job_forgot=Job()
        job_forgot.let {thejob->
            CoroutineScope(IO + thejob!!).launch {
                try{
                    val response = loginRepository.userForgotPass(req_forgot)
                    response.data?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.userForgotPass(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    withContext(Dispatchers.Main) {
                        iLoginListener.getFailure(response.msg as String)
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

    fun getLoginUser(req_login:AppConstants.LoginRequest){
        job_login= Job()

        job_login.let {thejob ->
            CoroutineScope(IO + thejob!!).launch {

                try {
                    val response = loginRepository.getLogin(req_login)
                    response.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getUserLogin(response)
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