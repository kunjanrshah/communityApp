package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.CalendarSearchRepository
import com.krs.community.viewmodel.CalendarSearchViewModel

class CalendarSearchViewModelFactory(private val repository: CalendarSearchRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CalendarSearchViewModel(repository, AppController.mApplication) as T
    }
}