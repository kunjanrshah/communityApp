package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ByDocumentListener
import com.krs.community.repositories.DocumentListRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException

import kotlinx.coroutines.*

class DocumentsListModel(
        private val documentListRepository: DocumentListRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = DocumentsListModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    lateinit var byDocumentListener: ByDocumentListener

    fun getUploadedFiles(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            completableJob = Job()
            completableJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response = documentListRepository.getDocumentList(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                byDocumentListener.getDocuments(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            byDocumentListener.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            byDocumentListener.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            byDocumentListener.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}