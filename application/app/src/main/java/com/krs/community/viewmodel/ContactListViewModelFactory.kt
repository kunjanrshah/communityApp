package com.krs.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.ContactListRepository

class ContactListViewModelFactory(private val repository: ContactListRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ContactListViewModel(repository, AppController.mApplication) as T
    }
}