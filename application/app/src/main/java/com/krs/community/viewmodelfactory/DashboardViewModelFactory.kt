package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.DashboardRepository
import com.krs.community.viewmodel.DashboardViewModel

class DashboardViewModelFactory(private val repository: DashboardRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardViewModel(repository, AppController.mApplication) as T
    }
}