package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.interfaces.ByDistanceListener
import com.krs.community.interfaces.ByKeywordListener
import com.krs.community.model.ByDistanceModel
import com.krs.community.repositories.SmartSearchRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class SmartSearchViewModel(
        private val mSmartSearchRepository: SmartSearchRepository,
        var app: Application) : AndroidViewModel(app) {

    private lateinit var job_by_search: CompletableJob
    private var TAG: String = SmartSearchViewModel::class.java.simpleName
    lateinit var mByKeywordListener: ByKeywordListener


   fun getLastName(id:Int):LiveData<String>{
       return mSmartSearchRepository.getLastName(id)
    }

    fun getCityNamebyId(id:String):LiveData<String>{
        return mSmartSearchRepository.getCityName(id)
    }

    fun getMemberByKeywords(jsonObject: JsonObject) {
        job_by_search = Job()
        job_by_search.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mSmartSearchRepository.searchByKeyword(jsonObject)
                    response.member?.let {
                        withContext(Dispatchers.Main) {
                            mByKeywordListener.getMembers(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                    mByKeywordListener.getFailure(response.message as String)
                } catch (e: ApiException) {
                    e.message?.let {
                        mByKeywordListener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        mByKeywordListener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        mByKeywordListener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

}