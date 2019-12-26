package com.krs.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.ByDistanceRepository
import com.krs.community.repositories.StatisticsRepository

class StatisticsViewModelFactory(private val repository: StatisticsRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StatisticsViewModel(repository, AppController.mApplication) as T
    }
}