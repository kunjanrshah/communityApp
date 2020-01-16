package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.repositories.SmartFilterRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class SmartFilterViewModel(
        private val mSmartFilterRepository: SmartFilterRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = SmartFilterViewModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    lateinit var mByFilterListener: ByFilterListener

    fun getListCityName():LiveData<List<String>>{
        return mSmartFilterRepository.getListCityName()
    }

     fun getCityIdByName(name:String):Int{
        return mSmartFilterRepository.getCityIdByName(name)
    }

    fun getLastName():LiveData<List<String>>{
        return mSmartFilterRepository.getLastName()
    }
     fun getCityNamebyId(id:String):String{
        return mSmartFilterRepository.getCityName(id)
    }

     fun getLastNameById(id:Int):String{
        return mSmartFilterRepository.getLastNameById(id)
    }

     fun getIdByLastName(name:String):Int{
        return mSmartFilterRepository.getIdByLastName(name)
    }

    fun smartFilterSearch(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mSmartFilterRepository.searchByName(jsonObject)
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

    fun getInActiveRecords(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mSmartFilterRepository.getInActiveRecords(jsonObject)
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