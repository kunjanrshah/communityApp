package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.entities.City
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.repositories.MatrimonySearchRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class MatrimonySearchViewModel(
        private val mMatrimonySearchRepository: MatrimonySearchRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = MatrimonySearchViewModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    lateinit var mByFilterListener: ByFilterListener

    fun getListCityName():LiveData<List<String>>{
        return mMatrimonySearchRepository.getListCityName()
    }

     suspend fun getCityIdByName(name:String):Int{
        return mMatrimonySearchRepository.getCityIdByName(name)
    }

    fun getLastName():LiveData<List<String>>{
        return mMatrimonySearchRepository.getLastName()
    }

    suspend fun getIdByLastName(name:String):Int{
        return mMatrimonySearchRepository.getIdByLastName(name)
    }

    fun getSearchByName(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mMatrimonySearchRepository.searchByName(jsonObject)
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