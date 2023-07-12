package com.krs.community.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krs.community.app.AppController
import com.krs.community.repositories.RoomMemberRepository
import com.krs.community.viewmodel.RoomMemberViewModel

class RoomMemberViewModelFactory(private val repository: RoomMemberRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoomMemberViewModel(repository, AppController.mApplication) as T
    }
}