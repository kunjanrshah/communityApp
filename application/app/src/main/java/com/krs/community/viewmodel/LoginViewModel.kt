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
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ILoginListener
import com.krs.community.model.LoginModel
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.repositories.LoginRepository
import com.krs.community.type.LoginInput
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import com.krs.community.utils.Utility
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel(private val loginRepository: LoginRepository,
                     var app: Application) : AndroidViewModel(app) {

    lateinit var iLoginListener: ILoginListener
    lateinit var mILoginListener: ILoginListener
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
    var password: String? = null
    private var jobInnerLogin: CompletableJob? = null

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
                stopTime.value = false
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "remaining time: " + otp_timer?.get())
                }

            }

            override fun onFinish() {
                stopTime.value = true

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
        jobInnerLogin?.cancel()
    }

    fun loginWithOTP() {
        try {
            val loginInput = LoginInput(
                mobile = mobile!!,
                password = password ?: ""
            )
            getLoginUser(loginInput)
        } catch (e: Exception) {
            Utility.hideSweetProgress()
            e.printStackTrace()
        }
    }

    fun loginWithPassword() {
        try {
            val loginInput = LoginInput(
                mobile = mobile!!,
                password = password!!
            )
            getLoginUser(loginInput)
        } catch (e: Exception) {
            Utility.hideSweetProgress()
            e.printStackTrace()
        }
    }

    fun loginWithMobile(number: String) {
        try {
            val loginInput = LoginInput(
                mobile = number,
                password = password ?: ""
            )
            getLoginUser(loginInput)
        } catch (e: Exception) {
            Utility.hideSweetProgress()
            e.printStackTrace()
        }
    }

    fun loginWithGoogle(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)

        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = mAuth?.currentUser
                if (user != null) {
                    val loginuser = user.email.toString()
                    if (loginuser.isNotEmpty() && loginuser.isNotBlank()) {
                        val loginInput = LoginInput(
                            mobile = loginuser,
                            password = password ?: ""
                        )
                        getLoginUser(loginInput)
                    } else {
                        status.value = false
                        Utility.hideSweetProgress()
                    }
                }
            } else {
                Utility.hideSweetProgress()
                status.value = false
                Log.w(TAG, "signInWithCredential:failure", task.exception)
            }
        }
    }

    fun loginWithFB(email: String) {
        try {
            if (email.isEmpty()) {
                status.value = false
                return
            }
            val loginInput = LoginInput(
                mobile = email,
                password = password ?: ""
            )
            getLoginUser(loginInput)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLoginUser(input: LoginInput) {
        if (isNetworkConnected(app.applicationContext)) {
            job_login = Job()
            job_login.let { thejob ->
                CoroutineScope(IO + thejob!!).launch {

                    try {
                        val result: kotlin.Result<LoginModel> =
                            loginRepository.getLogin(input)
                        result.let {
                            withContext(Dispatchers.Main) {
                                result.onSuccess { loginModel ->
                                    if (loginModel.message.equals("success", ignoreCase = true)) {
                                        Guru.putString(
                                            app.applicationContext.getString(R.string.access_token),
                                            loginModel.authToken
                                        )
                                        loginModel.refreshToken?.let { refreshToken ->
                                            Guru.putString(
                                                app.applicationContext.getString(R.string.refresh_token),
                                                refreshToken
                                            )
                                        }
                                        val member = Member()
                                        member.accessToken = loginModel.authToken
                                        val loginResponse = LoginResponse()
                                        loginResponse.success = true
                                        loginResponse.message = loginModel.message
                                        loginResponse.data = member
                                        iLoginListener.userLogin(loginResponse, false)
                                    } else {
                                        iLoginListener.getFailure(loginModel.message)
                                    }
                                }
                                result.onFailure { exception ->
                                    iLoginListener.getFailure(
                                        exception.message
                                            ?: app.applicationContext.getString(R.string.Authenticationfailed)
                                    )
                                }
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