package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ILoginListener
import com.krs.community.repositories.PasswordRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException

import kotlinx.coroutines.*

class PasswordViewModel(
        private val passwordRepository: PasswordRepository,
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
                       val response = passwordRepository.changePassword(jsonObject)
                       response.let {
                           withContext(Dispatchers.Main) {
                               mLoginListener.userLogin(response)
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

   fun forgotPassword(jsonObject: JsonObject) {
       if (isNetworkConnected(app.applicationContext)) {
           forgotPasswordJob = Job()
           forgotPasswordJob.let { thejob ->

               CoroutineScope(Dispatchers.IO + thejob).launch {
                   try {
                       val response = passwordRepository.forgotPassword(jsonObject)
                       response.let {
                           withContext(Dispatchers.Main) {
                               mLoginListener.userLogin(response)
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