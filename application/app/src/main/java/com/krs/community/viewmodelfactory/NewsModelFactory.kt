package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.NewsRepository
import com.krs.community.viewmodel.NewsViewModel

class NewsModelFactory(
        private val repository: NewsRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(repository, AppController.mApplication) as T
    }
}