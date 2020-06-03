package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ByKeywordListener
import com.krs.community.repositories.SmartSearchRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException

import kotlinx.coroutines.*

class SmartSearchViewModel(
        private val mSmartSearchRepository: SmartSearchRepository,
        var app: Application) : AndroidViewModel(app) {

    private lateinit var jobBySearch: CompletableJob

    lateinit var mByKeywordListener: ByKeywordListener
    private var TAG: String = SmartSearchViewModel::class.java.simpleName

    fun getLastName(id: Int): LiveData<String> {
        return mSmartSearchRepository.getLastName(id)
    }

    fun getCityNamebyId(id: String): LiveData<String> {
        return mSmartSearchRepository.getCityName(id)
    }

    suspend fun getNativeById(id: Int): String {
        return mSmartSearchRepository.getNativeById(id)
    }

    suspend fun getLocalCommById(id: String): String {
        return mSmartSearchRepository.getLocalCommName(id)
    }

    fun getMemberByKeywords(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            jobBySearch = Job()
            jobBySearch.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response = mSmartSearchRepository.searchByKeyword(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                mByKeywordListener.getMembers(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                        mByKeywordListener.getFailure(response.message as String)
                    } catch (e: ApiException) {
                        e.message?.let {
                            mByKeywordListener.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mByKeywordListener.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mByKeywordListener.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

}