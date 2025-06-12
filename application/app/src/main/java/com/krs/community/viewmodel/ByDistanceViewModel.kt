package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ByDistanceListener
import com.krs.community.model.ByDistanceModel
import com.krs.community.repositories.ByDistanceRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException

import kotlinx.coroutines.*

class ByDistanceViewModel(
        private val mByDistanceRepository: ByDistanceRepository,
        var app: Application) : AndroidViewModel(app) {

    var job_by_distance: CompletableJob? = null
    var TAG: String = ByDistanceViewModel::class.java.simpleName
    var mByDistanceListener: ByDistanceListener? = null


    fun getCityNamebyId(id: String): LiveData<String> {
        return mByDistanceRepository.getCityName(id)
    }

    fun getLastNamebyId(id: String): LiveData<String> {
        return mByDistanceRepository.getLastName(id)
    }

    suspend fun getNativeById(id: Int): String {
        return mByDistanceRepository.getNativeById(id)
    }

    suspend fun getLocalCommById(id: String): String {
        return mByDistanceRepository.getLocalCommName(id)
    }

    fun getUserByDistance(distance: ByDistanceModel) {
        if (isNetworkConnected(app.applicationContext)) {
            job_by_distance = Job()
            job_by_distance.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob!!).launch {
                    try {
                        val response = mByDistanceRepository.byDistance(distance)
                        response.member?.let {
                            withContext(Dispatchers.Main) {
                                mByDistanceListener?.getMembers(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                        mByDistanceListener?.getFailure(response.message as String)
                    } catch (e: ApiException) {
                        e.message?.let {
                            mByDistanceListener?.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mByDistanceListener?.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mByDistanceListener?.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}