package com.krs.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.AdminSearchRepository
import com.krs.community.repositories.ByDistanceRepository
import com.krs.community.repositories.CalendarSearchRepository

class AdminSearchViewModelFactory(private val repository: AdminSearchRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AdminSearchViewModel(repository, AppController.mApplication) as T
    }
}