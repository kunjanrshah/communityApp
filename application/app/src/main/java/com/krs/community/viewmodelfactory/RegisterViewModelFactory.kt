package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.DeviceTokenRepository
import com.krs.community.repositories.RegisterRepository
import com.krs.community.viewmodel.RegisterViewModel

class RegisterViewModelFactory(
    private val repository: RegisterRepository,
    private val deviceTokenRepository: DeviceTokenRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterViewModel(repository, deviceTokenRepository, AppController.mApplication) as T
    }
}
