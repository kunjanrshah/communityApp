package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.SmartSearchRepository
import com.krs.community.viewmodel.SmartSearchViewModel

class SmartSearchViewModelFactory(private val repository: SmartSearchRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SmartSearchViewModel(repository, AppController.mApplication) as T
    }
}