package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.PasswordRepository
import com.krs.community.viewmodel.PasswordViewModel

class PasswordViewModelFactory(
        private val repository: PasswordRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PasswordViewModel(repository, AppController.mApplication) as T
    }
}