package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.listeners.UpdateVersionListener
import com.krs.community.repositories.DashboardRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class DashboardViewModel(
        private val mDashboardRepository: DashboardRepository,
        var app: Application) : AndroidViewModel(app) {

    var TAG: String = DashboardViewModel::class.java.simpleName

    private lateinit var completableJob: CompletableJob
    lateinit var listener: UpdateVersionListener

    fun getUpdatedVersion(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mDashboardRepository.getUpdatedVersion(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            listener.getSuccess(response)
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


    suspend fun fetchCommittee(){
        mDashboardRepository.fetchCommittee()
    }

    suspend fun fetchDesignation(){
        mDashboardRepository.fetchDesignation()
    }

    suspend fun fetchSubCommunities(){
        mDashboardRepository.fetchSubCommunities()
    }

    suspend fun fetchLocalCommunities(){
        mDashboardRepository.fetchLocalCommunities()
    }

    suspend fun fetchLastName(){
        mDashboardRepository.fetchLastName()
    }

    suspend fun fetchEducation(){
        mDashboardRepository.fetchEducation()
    }

    suspend fun fetchGotra(){
        mDashboardRepository.fetchGotra()
    }

    suspend fun fetchState(){
        mDashboardRepository.fetchState()
    }

    suspend fun fetchCity(){
        mDashboardRepository.fetchCity()
    }

    suspend fun fetchBusinessCategory(){
        mDashboardRepository.fetchBusinessCategory()
    }

    suspend fun fetchBusinessSubCategory(){
        mDashboardRepository.fetchBusinessSubCategory()
    }

    suspend fun fetchNative(){
        mDashboardRepository.fetchNative()
    }

    suspend fun fetchOccupation(){
        mDashboardRepository.fetchOccupation()
    }

    suspend fun fetchRelations(){
        mDashboardRepository.fetchRelations()
    }

    suspend fun fetchCurrentActivity(){
        mDashboardRepository.fetchCurrentActivity()
    }


}