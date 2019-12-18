package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.krs.community.app.lazyDeferred
import com.krs.community.interfaces.DashboardListener
import com.krs.community.model.Datum
import com.krs.community.repositories.DashboardRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.Coroutines
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class DashboardViewModel(
        private val mDashboardRepository: DashboardRepository,
        var app: Application) : AndroidViewModel(app) {

    var TAG: String = DashboardViewModel::class.java.simpleName

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