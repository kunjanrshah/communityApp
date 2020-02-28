package com.krs.community.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.listeners.ByDocumentListener
import com.krs.community.listeners.ByFilterListener
import com.krs.community.listeners.ILoginListener
import com.krs.community.repositories.DocumentListRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class DocumentsListModel(
        private val documentListRepository: DocumentListRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = DocumentsListModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    lateinit var mByFilterListener: ByDocumentListener



    fun getInActiveRecords() {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = documentListRepository.getDocumentList()
                    response.let {
                        withContext(Dispatchers.Main) {

                            mByFilterListener.getMembers(response)


                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        mByFilterListener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        mByFilterListener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        mByFilterListener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

}