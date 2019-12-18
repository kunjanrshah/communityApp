package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.krs.community.entities.City
import com.krs.community.entities.States
import com.krs.community.interfaces.IbrowseCityRecordsListener
import com.krs.community.model.SearchByCityData
import com.krs.community.repositories.BrowseCityRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class BrowseCityViewModel(
        private val browsCityRepository: BrowseCityRepository,
        var app: Application) : AndroidViewModel(app) {

    var job_users: CompletableJob? = null
    var ibrowseCityRecordsListener: IbrowseCityRecordsListener?=null
    var TAG: String = BrowseCityViewModel::class.java.simpleName

    suspend fun getStates(): LiveData<List<States>> {
       return browsCityRepository.getStates()
    }

    suspend fun getLastName(id:Int):String{
       return browsCityRepository.getLastnameById(id)
    }

    suspend fun getCitiesByState(id:Int): LiveData<List<City>> {
        return browsCityRepository.getCityByStateId(id)
    }

    fun fetchRecordsByCity(data: SearchByCityData) {
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

    /*fun getUserStates() {
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
    }*/


}