package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

    suspend fun lstCityName(): List<String> {
        return mStatisticsRepository.getCityNames()
    }

    suspend fun getCityIdByName(name: String): Int {
        return mStatisticsRepository.getcityIdByName(name)
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