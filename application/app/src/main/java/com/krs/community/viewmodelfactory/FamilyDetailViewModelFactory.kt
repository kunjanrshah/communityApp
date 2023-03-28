package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.FamilyDetailRepository
import com.krs.community.viewmodel.FamilyDetailViewModel

class FamilyDetailViewModelFactory(private val repository: FamilyDetailRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FamilyDetailViewModel(repository, AppController.mApplication) as T
    }
}