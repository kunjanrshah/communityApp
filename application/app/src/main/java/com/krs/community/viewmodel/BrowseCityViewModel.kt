package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.krs.community.interfaces.IBrowseCityListener
import com.krs.community.interfaces.IRegisterListener
import com.krs.community.interfaces.IbrowseCityRecordsListener
import com.krs.community.model.CitiesDatum
import com.krs.community.model.SearchByCityData
import com.krs.community.repositories.BrowseCityRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class BrowseCityViewModel(
        private val browsCityRepository: BrowseCityRepository,
        var app: Application) : AndroidViewModel(app) {

    var job_states: CompletableJob? = null
    var job_cities: CompletableJob? = null
    var iBrowsecityListener: IBrowseCityListener? = null
    var ibrowseCityRecordsListener: IbrowseCityRecordsListener?=null
    var TAG: String = BrowseCityViewModel::class.java.simpleName

    fun getUserStates() {
        job_states = Job()
        job_states.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = browsCityRepository.userState()
                    response.data?.let {
                        withContext(Dispatchers.Main) {
                            iBrowsecityListener?.getStates(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iBrowsecityListener?.getFailure(response.message as String)
                } catch (e: ApiException) {
                    e.message?.let {
                        iBrowsecityListener?.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        iBrowsecityListener?.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        iBrowsecityListener?.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    fun fetchCitiesForStateId(id: Int) {
        job_cities = Job()
        job_cities.let {thejob ->
            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = browsCityRepository.userCity(id)
                    response.data?.let {
                        withContext(Dispatchers.Main) {
                            iBrowsecityListener?.getCities(id,response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iBrowsecityListener?.getFailure(response.message)
                } catch (e: ApiException) {
                    e.message?.let { iBrowsecityListener?.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { iBrowsecityListener?.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { iBrowsecityListener?.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun fetchRecordsByCity(data: SearchByCityData) {
        job_cities = Job()
        job_cities.let {thejob ->
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
                    ibrowseCityRecordsListener?.getFailure(response.success)
                } catch (e: ApiException) {
                    e.message?.let { iBrowsecityListener?.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { iBrowsecityListener?.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { iBrowsecityListener?.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }
}