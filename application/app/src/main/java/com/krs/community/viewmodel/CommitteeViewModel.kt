package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.entities.Committee
import com.krs.community.entities.Designation
import com.krs.community.entities.LocalCommunity
import com.krs.community.listeners.ByFilterListener
import com.krs.community.repositories.CommitteeRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class CommitteeViewModel(
        private val committeeRepository: CommitteeRepository,
        var app: Application) : AndroidViewModel(app) {

   private var TAG: String = CommitteeViewModel::class.java.simpleName
   private lateinit var completableJob: CompletableJob
   lateinit var filterListener: ByFilterListener

   fun getUsersInCommittee(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = committeeRepository.getUsersInCommittee(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            filterListener.getMembers(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        filterListener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        filterListener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        filterListener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    suspend fun getLastName(id:Int):String{
        return committeeRepository.getLastnameById(id)
    }

   suspend fun getLocalCommunityName(name:String): Int {
        return committeeRepository.getLocalCommunityName(name)
   }

   suspend fun getCommitteeName(name:String): Int {
        return committeeRepository.getCommitteeName(name)
   }

   suspend fun getDesignationName(name:String): Int {
        return committeeRepository.getDesignationName(name)
   }

   suspend fun getLocalCommunity(id:Int): LiveData<List<String>> {
      return committeeRepository.getLocalCommunity(id)
   }

   suspend fun getCommitteeList(): LiveData<List<String>>{
       return committeeRepository.getCommitteeList()
   }

   suspend fun getDesignation(): LiveData<List<String>>{
        return committeeRepository.getDesignationList()
   }
}