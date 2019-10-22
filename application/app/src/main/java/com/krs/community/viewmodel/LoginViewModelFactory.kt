package com.krs.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.LoginRepository
import com.krs.community.repositories.RegisterRepository

class LoginViewModelFactory(
        private val repository: LoginRepository
        ):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(repository, AppController.mApplication) as T
    }
}