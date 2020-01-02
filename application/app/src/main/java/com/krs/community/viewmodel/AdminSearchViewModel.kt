package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.repositories.AdminSearchRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class AdminSearchViewModel(
        private val mAdminSearchRepository: AdminSearchRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = AdminSearchViewModel::class.java.simpleName
    private lateinit var completableJob: CompletableJob
    lateinit var mByFilterListener: ByFilterListener

    fun getListCityName():LiveData<List<String>>{
        return mAdminSearchRepository.getListCityName()
    }

     fun getCityIdByName(name:String):Int{
        return mAdminSearchRepository.getCityIdByName(name)
    }


    fun getCityNamebyId(id:String):String{
        return mAdminSearchRepository.getCityName(id)
    }

     fun getLastNameById(id:Int):String{
        return mAdminSearchRepository.getLastNameById(id)
    }

     fun getIdByLastName(name:String):Int{
        return mAdminSearchRepository.getIdByLastName(name)
    }

    fun getAdminSearch(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mAdminSearchRepository.getAdminSearch(jsonObject)
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