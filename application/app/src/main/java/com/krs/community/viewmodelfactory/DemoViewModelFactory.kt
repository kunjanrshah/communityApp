package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.BrowseCityRepository
import com.krs.community.repositories.DemoRepository
import com.krs.community.viewmodel.BrowseCityViewModel
import com.krs.community.viewmodel.DemoViewModel

class DemoViewModelFactory(private val repository: DemoRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DemoViewModel(repository, AppController.mApplication) as T
    }
}