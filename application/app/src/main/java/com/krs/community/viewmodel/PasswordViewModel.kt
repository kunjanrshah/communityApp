package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.R
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ILoginListener
import com.krs.community.model.LoginResponse
import com.krs.community.repositories.ChangePasswordRepository
import com.krs.community.repositories.ForgotPasswordRepository
import com.krs.community.repositories.PasswordRepository
import com.krs.community.type.ChangePasswordInput
import com.krs.community.type.ForgotPasswordInput
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PasswordViewModel(
        private val passwordRepository: PasswordRepository,
        private val forgotPasswordRepository: ForgotPasswordRepository,
        private val changePasswordRepository: ChangePasswordRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = PasswordViewModel::class.java.simpleName
    private lateinit var changePasswordJob: CompletableJob
    private lateinit var forgotPasswordJob: CompletableJob
    lateinit var mLoginListener: ILoginListener

    fun changePassword(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            changePasswordJob = Job()
            changePasswordJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val currentPassword = jsonObject.get("current_password")?.asString ?: ""
                        val newPassword = jsonObject.get("new_password")?.asString ?: ""
                        val input = ChangePasswordInput(
                            currentPassword = currentPassword,
                            newPassword = newPassword
                        )
                        val result = changePasswordRepository.changePassword(input)
                        result.onSuccess { message ->
                            val response = LoginResponse()
                            response.success = true
                            response.message = message
                            withContext(Dispatchers.Main) {
                                mLoginListener.userLogin(response, false)
                                thejob.complete()
                            }
                        }
                        result.onFailure { exception ->
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(
                                    exception.message
                                        ?: app.applicationContext.getString(R.string.Authenticationfailed)
                                )
                                thejob.complete()
                            }
                        }
                        return@launch
                    } catch (e: ApiException) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(it)
                                thejob.complete()
                            }
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(it)
                                thejob.complete()
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(it)
                                thejob.complete()
                            }
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun forgotPassword(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            forgotPasswordJob = Job()
            forgotPasswordJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val resetType = jsonObject.get("reset_type")?.asString ?: "email"
                        val identifier = jsonObject.get("username")?.asString ?: ""
                        val input = ForgotPasswordInput(
                            resetType = resetType,
                            email = identifier
                        )
                        val result = forgotPasswordRepository.forgotPassword(input)
                        result.onSuccess { message ->
                            val response = LoginResponse()
                            response.success = true
                            response.message = message
                            withContext(Dispatchers.Main) {
                                mLoginListener.userLogin(response, true)
                                thejob.complete()
                            }
                        }
                        result.onFailure { exception ->
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(
                                    exception.message
                                        ?: app.applicationContext.getString(R.string.Authenticationfailed)
                                )
                                thejob.complete()
                            }
                        }
                        return@launch
                    } catch (e: ApiException) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(it)
                                thejob.complete()
                            }
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(it)
                                thejob.complete()
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            withContext(Dispatchers.Main) {
                                mLoginListener.getFailure(it)
                                thejob.complete()
                            }
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun sendEmail(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            forgotPasswordJob = Job()
            forgotPasswordJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response = passwordRepository.sendMail(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                mLoginListener.userLogin(response, false)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mLoginListener.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mLoginListener.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mLoginListener.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}