package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.entities.States
import com.krs.community.listeners.IbrowseCityRecordsListener
import com.krs.community.model.SearchByCityData
import com.krs.community.repositories.BrowseCityRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class BrowseCityViewModel(
        private val browsCityRepository: BrowseCityRepository,
        var app: Application) : AndroidViewModel(app) {

    private var job_users: CompletableJob? = null
    var ibrowseCityRecordsListener: IbrowseCityRecordsListener? = null
    var TAG: String = BrowseCityViewModel::class.java.simpleName

    suspend fun getStates(): LiveData<List<States>> {
        return browsCityRepository.getStates()
    }

    suspend fun getLastName(id: Int): String {
        return browsCityRepository.getLastnameById(id)
    }

    /*suspend fun getCitiesByState(id: Int): LiveData<List<City>> {
        return browsCityRepository.getCityByStateId(id)
    }*/

    suspend fun getNativeById(id: Int): String {
        return browsCityRepository.getNativeById(id)
    }


    fun getCitiesByState(request: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            job_users = Job()
            job_users.let { thejob ->
                CoroutineScope(Dispatchers.IO + thejob!!).launch {
                    try {
                        val response = browsCityRepository.cityByState(request)
                        response.let {
                            withContext(Dispatchers.Main) {
                                ibrowseCityRecordsListener?.getCitiesByState(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let { ibrowseCityRecordsListener?.getFailure(it) }
                    } catch (e: NoInternetException) {
                        e.message?.let { ibrowseCityRecordsListener?.getFailure(it) }
                    } catch (e: Exception) {
                        e.message?.let { ibrowseCityRecordsListener?.getFailure(it) }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun fetchRecordsByCity(data: SearchByCityData) {
        if (isNetworkConnected(app.applicationContext)) {
            job_users = Job()
            job_users.let { thejob ->
                CoroutineScope(Dispatchers.IO + thejob!!).launch {
                    try {
                        val response = browsCityRepository.userRecords(data)
                        response.let {
                            withContext(Dispatchers.Main) {
                                ibrowseCityRecordsListener?.getSearchRecords(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let { ibrowseCityRecordsListener?.getFailure(it) }
                    } catch (e: NoInternetException) {
                        e.message?.let { ibrowseCityRecordsListener?.getFailure(it) }
                    } catch (e: Exception) {
                        e.message?.let { ibrowseCityRecordsListener?.getFailure(it) }
                    }
                    thejob.complete()
                }
            }
        }
    }
}