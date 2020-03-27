package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ByFilterListener
import com.krs.community.listeners.ReminderListener
import com.krs.community.repositories.CalendarSearchRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class CalendarSearchViewModel(
        private val mCalendarSearchRepository: CalendarSearchRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = CalendarSearchViewModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    lateinit var mByFilterListener: ByFilterListener
    lateinit var mReminderListener: ReminderListener

    fun getListCityName(): LiveData<List<String>> {
        return mCalendarSearchRepository.getListCityName()
    }

    fun getCityIdByName(name: String): Int {
        return mCalendarSearchRepository.getCityIdByName(name)
    }

    fun getCityNamebyId(id: String): String {
        return mCalendarSearchRepository.getCityName(id)
    }

    fun getLastNameById(id: Int): String {
        return mCalendarSearchRepository.getLastNameById(id)
    }

    fun getIdByLastName(name: String): Int {
        return mCalendarSearchRepository.getIdByLastName(name)
    }

    fun getCalendarSearch(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            completableJob = Job()
            completableJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response = mCalendarSearchRepository.getSearchByDate(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                mByFilterListener.getMembers(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mByFilterListener.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mByFilterListener.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mByFilterListener.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun setReminder(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mCalendarSearchRepository.setReminder(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mReminderListener.reminderResponse(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        mReminderListener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        mReminderListener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        mReminderListener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

}