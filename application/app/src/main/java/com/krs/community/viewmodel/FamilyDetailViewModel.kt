package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.interfaces.IFamilyMembersListener
import com.krs.community.repositories.FamilyDetailRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class FamilyDetailViewModel(
        private val mFamilyDetailRepository: FamilyDetailRepository,
        var app: Application) : AndroidViewModel(app) {


    var job_users: CompletableJob? = null
    lateinit var mIFamilyMembersListener: IFamilyMembersListener

    var TAG: String = FamilyDetailViewModel::class.java.simpleName

    fun getFamilyDetails(data: JsonObject) {
        job_users = Job()
        job_users.let { thejob ->
            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = mFamilyDetailRepository.getFamilyMembers(data)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mIFamilyMembersListener.getFamilyMembers(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let { mIFamilyMembersListener.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { mIFamilyMembersListener.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { mIFamilyMembersListener.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun deleteMember(data: JsonObject) {
        job_users = Job()
        job_users.let { thejob ->
            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = mFamilyDetailRepository.deleteMember(data)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mIFamilyMembersListener.getMessage(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let { mIFamilyMembersListener.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { mIFamilyMembersListener.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { mIFamilyMembersListener.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

}