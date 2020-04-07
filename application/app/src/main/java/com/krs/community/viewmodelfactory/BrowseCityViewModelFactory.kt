package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.BrowseCityRepository
import com.krs.community.viewmodel.BrowseCityViewModel

class BrowseCityViewModelFactory(private val repository: BrowseCityRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BrowseCityViewModel(repository, AppController.mApplication) as T
    }
}