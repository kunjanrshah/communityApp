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
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class RegisterViewModel(
        private val registerRepository: RegisterRepository,
        var app: Application) : AndroidViewModel(app) {

    var fname: String? = null
    var email: String? = null
    var pass: String? = null
    var cpass: String? = null
    var address: String? = null
    var iRegisterListener: IRegisterListener? = null
    var TAG: String = RegisterViewModel::class.java.simpleName

    var job_states: CompletableJob? = null
    var job_cities: CompletableJob? = null
    var job_lastname: CompletableJob? = null
    var job_subcommunity: CompletableJob? = null
    var job_localcommunity: CompletableJob? = null


    fun cancelAllJobs() {
        job_states?.cancel()
        job_cities?.cancel()
        job_lastname?.cancel()
        job_subcommunity?.cancel()
        job_localcommunity?.cancel()
    }

    fun getUserStates() {
        job_states = Job()
        job_states.let { thejob ->

            CoroutineScope(IO + thejob!!).launch {
                try {
                    val response = registerRepository.userState()
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getStates(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message as String)
                } catch (e: ApiException) {
                    e.message?.let {
                        iRegisterListener?.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        iRegisterListener?.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        iRegisterListener?.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    fun fetchCitiesForStateId(id: Int) {
        job_cities = Job()

        job_cities.let {thejob ->
            CoroutineScope(IO + thejob!!).launch {
                try {
                    val response = registerRepository.userCity(id)
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getCities(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
                } catch (e: ApiException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun getUserLastName() {
        job_lastname=Job()
        job_lastname.let {thejob ->
            CoroutineScope(IO+thejob!!).launch {
                try {
                    val response = registerRepository.userLastName()
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getLastname(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
                } catch (e: ApiException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun getLstSubCommunity() {
        job_subcommunity=Job()
        job_subcommunity.let {thejob ->
            CoroutineScope(IO + thejob!!).launch {
                try {
                    val response = registerRepository.userSubCommunity()
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getSubCommunity(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
                } catch (e: ApiException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun getLstLocalCommunity(id: Int) {
        job_localcommunity=Job()
        job_localcommunity.let {thejob ->
            CoroutineScope(IO +thejob!!).launch {
                try {
                    val response = registerRepository.getLocalCommunity(id)
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getLocalCommunity(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
                } catch (e: ApiException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun onRegisterButtonClick(activity: Activity) {
        val mIntent = Intent(activity, DashboardActivity::class.java)
        activity.startActivity(mIntent)
        activity.finish()
        Log.d(TAG, "onRegisterButtonClick")
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
