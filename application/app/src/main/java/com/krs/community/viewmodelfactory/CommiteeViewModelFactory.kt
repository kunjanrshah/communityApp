package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.CommitteeRepository
import com.krs.community.viewmodel.CommitteeViewModel

class CommiteeViewModelFactory(
        private val repository: CommitteeRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CommitteeViewModel(repository, AppController.mApplication) as T
    }
}