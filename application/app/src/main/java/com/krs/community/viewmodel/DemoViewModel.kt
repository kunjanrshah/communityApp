package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.app.lazyDeferred
import com.krs.community.listeners.ILoginListener
import com.krs.community.listeners.StatisticsListener
import com.krs.community.repositories.DemoRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class DemoViewModel(
        private val demoRepository: DemoRepository,
        var app: Application) : AndroidViewModel(app) {

    var job_statistics: CompletableJob? = null
    var TAG: String = DemoViewModel::class.java.simpleName

    var mStatisticsListener: ILoginListener? = null

  /* suspend fun getRelationName(id:Int){
       demoRepository.getRelationName(id)
   }*/


/*
    fun getLoginAPI(jsonObject: JsonObject) {
        job_statistics = Job()
        job_statistics.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = demoRepository.getDemoApi(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mStatisticsListener?.userLogin(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        mStatisticsListener?.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        mStatisticsListener?.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        mStatisticsListener?.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }
*/

}