package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.listeners.ByFilterListener
import com.krs.community.repositories.ContactListRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import com.wessam.library.NetworkChecker
import kotlinx.coroutines.*

class ContactListViewModel(
        private val contactListRepository: ContactListRepository,
        var app: Application) : AndroidViewModel(app) {

    var completableJob: CompletableJob? = null
    var TAG: String = ContactListViewModel::class.java.simpleName
    lateinit var filterListener: ByFilterListener

    suspend fun getLastNameById(id: Int): String {
        return contactListRepository.getLastNameById(id)
    }

    suspend fun getCityNamebyId(id: String): String {
        return contactListRepository.getCityName(id)
    }

    fun getContactList(jsonObject: JsonObject) {
        if (NetworkChecker.isNetworkConnected(app.applicationContext)) {
            completableJob = Job()
            completableJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob!!).launch {
                    try {
                        val response = contactListRepository.getContactList(jsonObject)
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
    }

}