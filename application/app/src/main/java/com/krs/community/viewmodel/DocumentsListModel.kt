package com.krs.community.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ByDocumentListener
import com.krs.community.listeners.DeleteListener
import com.krs.community.repositories.DocumentListRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class DocumentsListModel(
        private val documentListRepository: DocumentListRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = DocumentsListModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    private lateinit var job_by_update: CompletableJob
    lateinit var byDocumentListener: ByDocumentListener
    lateinit var mDeleteListener: DeleteListener

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

    fun deleteFile(fileId: String, position: Int) {
        if (isNetworkConnected(app.applicationContext)) {
            job_by_update = Job()
            job_by_update.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {

                        val id = fileId.toRequestBody("text/plain".toMediaTypeOrNull())
                        val response: JsonObject = documentListRepository.deleteUploadedFile(id)

                        response.let {
                            withContext(Dispatchers.Main) {
                                Log.d("Response", response.toString())
                                mDeleteListener.getSuccess(position, response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mDeleteListener.getFail(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mDeleteListener.getFail(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mDeleteListener.getFail(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}