package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.LoginRepository
import com.krs.community.repositories.PasswordRepository
import com.krs.community.repositories.ShareEventRepository
import com.krs.community.viewmodel.LoginViewModel
import com.krs.community.viewmodel.PasswordViewModel
import com.krs.community.viewmodel.ShareEventViewModel
import org.json.JSONObject

class ShareEventViewModelFactory(private val repository: ShareEventRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShareEventViewModel(repository, AppController.mApplication) as T
    }


}