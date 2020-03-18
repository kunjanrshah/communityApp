package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.entities.MasterCounts
import com.krs.community.listeners.UpdateListener
import com.krs.community.repositories.DashboardRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class DashboardViewModel(
        private val mDashboardRepository: DashboardRepository,
        var app: Application) : AndroidViewModel(app) {

    var TAG: String = DashboardViewModel::class.java.simpleName

    private lateinit var completableJob: CompletableJob
    lateinit var listener: UpdateListener

    fun getUpdatedVersion(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mDashboardRepository.getUpdatedVersion(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            listener.getVersionResponse(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        listener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        listener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        listener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    fun getMasterUpdate() {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mDashboardRepository.getMasterUpdate()
                    response.let {
                        withContext(Dispatchers.Main) {
                            listener.getMastersResponse(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        listener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        listener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        listener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    suspend fun insertMasterCounts(masterCounts: MasterCounts) {
        mDashboardRepository.insertMasterCounts(masterCounts)
    }

    suspend fun getMasterCounts(): MasterCounts? {
        return mDashboardRepository.getMasterCounts()
    }

    suspend fun fetchCommittee(index: Int) {
        mDashboardRepository.fetchCommittee(index)
    }

    suspend fun fetchDesignation(index: Int) {
        mDashboardRepository.fetchDesignation(index)
    }

    suspend fun fetchSubCommunities(index: Int) {
        mDashboardRepository.fetchSubCommunities(index)
    }

    suspend fun fetchLocalCommunities(index: Int) {
        mDashboardRepository.fetchLocalCommunities(index)
    }

    suspend fun fetchLastName(index: Int) {
        mDashboardRepository.fetchLastName(index)
    }

    suspend fun fetchEducation(index: Int) {
        mDashboardRepository.fetchEducation(index)
    }

    suspend fun fetchGotra(index: Int) {
        mDashboardRepository.fetchGotra(index)
    }

    suspend fun fetchState(index: Int) {
        mDashboardRepository.fetchState(index)
    }

    suspend fun fetchCity(index: Int) {
        mDashboardRepository.fetchCity(index)
    }

    suspend fun fetchBusinessCategory(index: Int) {
        mDashboardRepository.fetchBusinessCategory(index)
    }

    suspend fun fetchBusinessSubCategory(index: Int) {
        mDashboardRepository.fetchBusinessSubCategory(index)
    }

    suspend fun fetchNative(index: Int) {
        mDashboardRepository.fetchNative(index)
    }

    suspend fun fetchOccupation(index: Int) {
        mDashboardRepository.fetchOccupation(index)
    }

    suspend fun fetchRelations(index: Int) {
        mDashboardRepository.fetchRelations(index)
    }

    suspend fun fetchCurrentActivity(index: Int) {
        mDashboardRepository.fetchCurrentActivity(index)
    }


}