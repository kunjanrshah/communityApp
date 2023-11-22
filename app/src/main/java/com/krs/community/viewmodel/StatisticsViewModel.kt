package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.StatisticsListener
import com.krs.community.repositories.StatisticsRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class StatisticsViewModel(
        private val mStatisticsRepository: StatisticsRepository,
        var app: Application) : AndroidViewModel(app) {

    var job_statistics: CompletableJob? = null
    var TAG: String = StatisticsViewModel::class.java.simpleName
    var mStatisticsListener: StatisticsListener? = null

    suspend fun getSubComm(): LiveData<List<String>> {
        return mStatisticsRepository.getSubComm()
    }

    suspend fun getLocalComm(subId: Int): LiveData<List<String>> {
        return mStatisticsRepository.getLocalComm(subId)
    }

    suspend fun getSubIdByName(name: String): Int {
        return mStatisticsRepository.getSubIdByName(name)
    }

    suspend fun getLocalIdByName(name: String): Int {
        return mStatisticsRepository.getLocalIdByName(name)
    }


    suspend fun lstCityName(): List<String> {
        return mStatisticsRepository.getCityNames()
    }

    suspend fun getCityIdByName(name: String): Int {
        return mStatisticsRepository.getcityIdByName(name)
    }

    suspend fun getCityDistinctName(citiesId: ArrayList<String>): LiveData<List<String>> {
        return mStatisticsRepository.getCityDistinctName(citiesId)
    }

    fun getStatistics(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            job_statistics = Job()
            job_statistics.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob!!).launch {
                    try {
                        val response = mStatisticsRepository.getStatistics(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                mStatisticsListener?.getStatistics(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mStatisticsListener?.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mStatisticsListener?.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mStatisticsListener?.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}