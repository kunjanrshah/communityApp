package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.repositories.RoomMemberRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import com.wessam.library.NetworkChecker
import kotlinx.coroutines.*

class RoomMemberViewModel(
        private val mRoomMemberRepository: RoomMemberRepository,
        var app: Application) : AndroidViewModel(app) {

    var jobByDelete: CompletableJob? = null
    var jobByInsert: CompletableJob? = null
    var jobBySearch: CompletableJob? = null
    var jobChangeStatus: CompletableJob? = null
    private lateinit var jobGetMembers: CompletableJob
    var TAG: String = RoomMemberViewModel::class.java.simpleName
    var mRoomMemberListener: RoomMemberListener? = null


    fun getLastName(id:Int):LiveData<String>{
        return mRoomMemberRepository.getLastName(id)
    }

    fun getCityNamebyId(id:String):LiveData<String>{
        return mRoomMemberRepository.getCityName(id)
    }

    fun getRoomMember(id:Int): LiveData<RoomMember> {
        return mRoomMemberRepository.getRoomMember(id)
    }

    fun getRoomMembers(){
        jobGetMembers = Job()
        jobGetMembers.let { thejob ->
            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mRoomMemberRepository.getRoomMembers()
                    response.let {
                        withContext(Dispatchers.Main) {
                            mRoomMemberListener?.getRoomMembers(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: Exception) {
                    mRoomMemberListener?.getFailure(e.message.toString())
                }
                thejob.complete()
            }
        }
    }

    fun deleteRoomMember(id:Int){
        jobByDelete = Job()
        jobByDelete.let { thejob ->
            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    mRoomMemberRepository.deleteRoomMember(id)
                    withContext(Dispatchers.Main) {
                        mRoomMemberListener?.refreshList()
                        thejob.complete()
                    }
                    return@launch
                } catch (e: Exception) {
                    mRoomMemberListener?.getFailure(e.message.toString())
                }
                thejob.complete()
            }
        }
    }

    fun insertRoomMember(roomMember: RoomMember){
        jobByInsert=Job()
        jobByInsert.let {thejob ->
            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    mRoomMemberRepository.insertRoomMember(roomMember)
                    withContext(Dispatchers.Main) {
                        mRoomMemberListener?.refreshList()
                        thejob.complete()
                    }
                    return@launch
                }catch (e:Exception){
                    mRoomMemberListener?.getFailure(e.message.toString())
                }
                thejob.complete()
            }
        }
    }

    fun changeStatus(jsonObject: JsonObject) {
        if (NetworkChecker.isNetworkConnected(app.applicationContext)) {
            jobChangeStatus = Job()
            jobChangeStatus.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob!!).launch {
                    try {
                        val response = mRoomMemberRepository.changeStatus(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                mRoomMemberListener?.getFailure(response.message)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mRoomMemberListener?.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mRoomMemberListener?.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mRoomMemberListener?.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun changeRole(jsonObject: JsonObject) {
        if (NetworkChecker.isNetworkConnected(app.applicationContext)) {
            jobBySearch = Job()
            jobBySearch.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob!!).launch {
                    try {
                        val response = mRoomMemberRepository.changeRole(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                mRoomMemberListener?.getFailure(response.message)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mRoomMemberListener?.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mRoomMemberListener?.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mRoomMemberListener?.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}