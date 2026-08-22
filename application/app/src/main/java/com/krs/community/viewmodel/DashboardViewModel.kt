package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.entities.MasterCounts
import com.krs.community.listeners.UpdateListener
import com.krs.community.repositories.DashboardRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel(
        private val mDashboardRepository: DashboardRepository,
        var app: Application) : AndroidViewModel(app) {

    var TAG: String = DashboardViewModel::class.java.simpleName

    private lateinit var completableJob: CompletableJob
    lateinit var listener: UpdateListener

    fun getUpdatedVersion(version: Double) {
        if (isNetworkConnected(app.applicationContext)) {
            completableJob = Job()
            completableJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response = mDashboardRepository.getUpdatedVersion(version)
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
    }

    fun getMasterUpdate() {
        if (isNetworkConnected(app.applicationContext)) {
            completableJob = Job()
            completableJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response = mDashboardRepository.getMastersCounts()
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
    }

    suspend fun insertMasterCounts(masterCounts: MasterCounts) {
        mDashboardRepository.insertMasterCounts(masterCounts)
    }

    suspend fun getMasterCounts(): MasterCounts? {
        return mDashboardRepository.getMasterCounts()
    }

    suspend fun getLastNameCount(): Int {
        return mDashboardRepository.getLastNameCount()
    }

    suspend fun getNativeCount(): Int {
        return mDashboardRepository.getNativeCount()
    }

    suspend fun getSubCommCount(): Int {
        return mDashboardRepository.getSubCommCount()
    }

    suspend fun getLocalCommCount(): Int {
        return mDashboardRepository.getLocalCommCount()
    }


    suspend fun getStatesCount(): Int {
        return mDashboardRepository.getStatesCount()
    }

    suspend fun getCitiesCount(): Int {
        return mDashboardRepository.getCitiesCount()
    }

    suspend fun fetchCommittee(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchCommittee(index)
        }
    }

    suspend fun fetchDesignation(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchDesignation(index)
        }
    }

    suspend fun fetchSubCommunities(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchSubCommunities(index)
        }
    }

    suspend fun fetchLocalCommunities(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchLocalCommunities(index)
        }
    }

    suspend fun fetchLastName(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchLastName(index)
        }
    }

    suspend fun fetchEducation(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchEducation(index)
        }
    }

    suspend fun fetchGotra(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchGotra(index)
        }
    }

    suspend fun fetchState(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchState(index)
        }
    }

    suspend fun fetchCity(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchCity(index)
        }
    }

    suspend fun fetchBusinessCategory(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchBusinessCategory(index)
        }
    }

    suspend fun fetchRelations(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            mDashboardRepository.fetchRelations(index)
        }
    }

    suspend fun fetchBusinessSubCategory(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            //  mDashboardRepository.fetchBusinessSubCategory(index)
        }
    }

    suspend fun fetchNative(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            //   mDashboardRepository.fetchNative(index)
        }
    }

    suspend fun fetchOccupation(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            //   mDashboardRepository.fetchOccupation(index)
        }
    }


    suspend fun fetchCurrentActivity(index: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            //   mDashboardRepository.fetchCurrentActivity(index)
        }
    }
}