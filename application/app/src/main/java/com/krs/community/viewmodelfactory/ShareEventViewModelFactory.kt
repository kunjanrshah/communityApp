package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.ShareEventRepository
import com.krs.community.viewmodel.ShareEventViewModel

class ShareEventViewModelFactory(private val repository: ShareEventRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShareEventViewModel(repository, AppController.mApplication) as T
    }


}