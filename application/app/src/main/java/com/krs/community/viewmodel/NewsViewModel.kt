package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.listeners.NewsListener
import com.krs.community.repositories.NewsRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class NewsViewModel(
        private val mNewsRepository: NewsRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = NewsViewModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    lateinit var mNewsListener: NewsListener

   fun getNewsSearch(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mNewsRepository.getNewsList(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mNewsListener.getNewsList(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        mNewsListener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        mNewsListener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        mNewsListener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

}