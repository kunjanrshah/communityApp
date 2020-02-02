package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.listeners.IFamilyMembersListener
import com.krs.community.listeners.ILoginListener
import com.krs.community.listeners.ActivityStatusListner
import com.krs.community.repositories.FamilyDetailRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class FamilyDetailViewModel(
        private val mFamilyDetailRepository: FamilyDetailRepository,
        var app: Application) : AndroidViewModel(app) {


    var jobFamilyDetails: CompletableJob? = null
    var jobDeletMember: CompletableJob? = null
    var jobUserActivityStatus: CompletableJob? = null
    var jobInnerLogin: CompletableJob? = null

    lateinit var mIFamilyMembersListener: IFamilyMembersListener
    lateinit var mILoginListener: ILoginListener
    lateinit var mActivityStatusListner: ActivityStatusListner

    var TAG: String = FamilyDetailViewModel::class.java.simpleName

    fun cancelAllJobs() {
        jobFamilyDetails?.cancel()
        jobDeletMember?.cancel()
        jobUserActivityStatus?.cancel()
        jobInnerLogin?.cancel()
    }

    fun getFamilyDetails(data: JsonObject) {
        jobFamilyDetails = Job()
        jobFamilyDetails.let { thejob ->
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
        jobDeletMember = Job()
        jobDeletMember.let { thejob ->
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

    fun getUserActivityStatus(data: JsonObject) {
        jobUserActivityStatus = Job()
        jobUserActivityStatus.let { thejob ->
            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = mFamilyDetailRepository.getUserActivityStatus(data)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mActivityStatusListner.memberStatus(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let { mActivityStatusListner.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { mActivityStatusListner.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { mActivityStatusListner.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun innerLogin(data: JsonObject) {
        jobInnerLogin = Job()
        jobInnerLogin.let { thejob ->
            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = mFamilyDetailRepository.innerLogin(data)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mILoginListener.userLogin(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let { mILoginListener.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { mILoginListener.getFailure(it) }
                } catch (e: Exception) {
                    e.message?.let { mILoginListener.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

}