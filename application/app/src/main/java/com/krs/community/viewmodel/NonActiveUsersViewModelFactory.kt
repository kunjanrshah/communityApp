package com.krs.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.AdminSearchRepository
import com.krs.community.repositories.ByDistanceRepository
import com.krs.community.repositories.CalendarSearchRepository
import com.krs.community.repositories.NonActivesRepository

class NonActiveUsersViewModelFactory(private val repository: NonActivesRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NonActiveUsersViewModel(repository, AppController.mApplication) as T
    }
}