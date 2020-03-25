package com.krs.community.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.CreateEventListener
import com.krs.community.repositories.ShareEventRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException

import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ShareEventViewModel(
        private val shareEventRepository: ShareEventRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = ShareEventViewModel::class.java.simpleName
    private lateinit var job_by_update: CompletableJob
    lateinit var mCreateEventListener: CreateEventListener


    fun createEvent(images: List<String>, id : String, user_id : String, access_token: String, params: String, yourtube:List<String>) {
        if (isNetworkConnected(app.applicationContext)) {
            job_by_update = Job()
            job_by_update.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        var imagesList: MutableList<MultipartBody.Part> = ArrayList()
                        var videoURLs: MutableList<RequestBody> = ArrayList()

                        for (i in 0..images.size - 1) {
                            val requestFile = RequestBody.create(
                                    "image/*".toMediaTypeOrNull(),
                                    File(images.get(i))
                            )
                            val body = MultipartBody.Part.createFormData("uploaded_file", File(images.get(i)).name, requestFile)
                            imagesList.add(body)
                        }

                        for (i in 0..yourtube.size - 1) {
                            val url = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(),
                                    id)
                            videoURLs.add(url)
                        }

                        val id = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                id)


                        val user_id = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                user_id)

                        val access_token = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                access_token)

                        val body = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                params)

                        val response: JsonObject = shareEventRepository.createEvent(imagesList, id, user_id, access_token, body, videoURLs)


                        response.let {
                            withContext(Dispatchers.Main) {
                                Log.d("Response", response.toString())
                                if (response.get("success").asString.equals("true")) {
                                    mCreateEventListener.getResult(response.get("message").asString)
                                } else {
                                    mCreateEventListener.onFailure(response.get("message").asString)
                                }
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mCreateEventListener.onFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mCreateEventListener.onFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mCreateEventListener.onFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}