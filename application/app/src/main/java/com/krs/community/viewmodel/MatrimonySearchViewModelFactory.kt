package com.krs.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.BrowseCityRepository
import com.krs.community.repositories.MatrimonySearchRepository
import com.krs.community.repositories.RegisterRepository
import com.krs.community.repositories.SmartSearchRepository

class MatrimonySearchViewModelFactory(private val repository: MatrimonySearchRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MatrimonySearchViewModel(repository, AppController.mApplication) as T
    }
}