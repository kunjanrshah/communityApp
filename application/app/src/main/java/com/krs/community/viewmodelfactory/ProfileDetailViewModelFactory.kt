package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.ProfileDetailRepository
import com.krs.community.viewmodel.ProfileDetailViewModel

class ProfileDetailViewModelFactory(private val repository: ProfileDetailRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileDetailViewModel(repository, AppController.mApplication) as T
    }
}