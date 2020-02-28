package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.DocumentListRepository
import com.krs.community.repositories.SmartFilterRepository
import com.krs.community.viewmodel.DocumentsListModel
import com.krs.community.viewmodel.SmartFilterViewModel

class DocumentListViewModelFactory(private val repository: DocumentListRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DocumentsListModel(repository, AppController.mApplication) as T
    }
}