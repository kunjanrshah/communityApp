package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.SmartFilterRepository
import com.krs.community.viewmodel.SmartFilterViewModel

class SmartFilterViewModelFactory(private val repository: SmartFilterRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SmartFilterViewModel(repository, AppController.mApplication) as T
    }
}