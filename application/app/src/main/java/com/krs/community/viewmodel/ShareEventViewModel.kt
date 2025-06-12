package com.krs.community.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.BuildConfig
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.CreateEventListener
import com.krs.community.repositories.ShareEventRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException

import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ShareEventViewModel(
        private val shareEventRepository: ShareEventRepository,
        var app: Application) : AndroidViewModel(app) {

    private var TAG: String = ShareEventViewModel::class.java.simpleName
    private lateinit var job_by_update: CompletableJob
    lateinit var mCreateEventListener: CreateEventListener


//    fun createEvent(images: List<String>, id: String, user_id: String, access_token: String, params: String, yourtube: List<String>) {
//        if (isNetworkConnected(app.applicationContext)) {
//            job_by_update = Job()
//            job_by_update.let { thejob ->
//
//                CoroutineScope(Dispatchers.IO + thejob).launch {
//                    try {
//                        var imagesList: MutableList<MultipartBody.Part> = ArrayList()
//                        var videoURLs: MutableList<RequestBody> = ArrayList()
//
//                        for (i in 0..images.size - 1) {
//                            val requestFile = File(images.get(i))
//                                    .asRequestBody("image/*".toMediaTypeOrNull())
//                            val body = MultipartBody.Part.createFormData("uploaded_file", File(images.get(i)).name, requestFile)
//                            imagesList.add(body)
//                        }
//
//                        for (i in 0..yourtube.size - 1) {
//                            val url = id.toRequestBody("text/plain".toMediaTypeOrNull())
//                            videoURLs.add(url)
//                        }
//
//                        val id = id.toRequestBody("text/plain".toMediaTypeOrNull())
//
//
//                        val user_id = user_id.toRequestBody("text/plain".toMediaTypeOrNull())
//
//                        val access_token = access_token.toRequestBody("text/plain".toMediaTypeOrNull())
//
//                        val body = params.toRequestBody("text/plain".toMediaTypeOrNull())
//
//                        val response: JsonObject = shareEventRepository.createEvent(imagesList, id, user_id, access_token, body, videoURLs)
//
//
//                        response.let {
//                            withContext(Dispatchers.Main) {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("Response", response.toString())
//                                }
//
//                                if (response.get("success").asString.equals("true")) {
//                                    mCreateEventListener.getResult(response.get("message").asString)
//                                } else {
//                                    mCreateEventListener.onFailure(response.get("message").asString)
//                                }
//                                thejob.complete()
//                            }
//                            return@launch
//                        }
//                    } catch (e: ApiException) {
//                        e.message?.let {
//                            mCreateEventListener.onFailure(it)
//                        }
//                    } catch (e: NoInternetException) {
//                        e.message?.let {
//                            mCreateEventListener.onFailure(it)
//                        }
//                    } catch (e: Exception) {
//                        e.message?.let {
//                            mCreateEventListener.onFailure(it)
//                        }
//                    }
//                    thejob.complete()
//                }
//            }
//        }
//    }

    fun createEvent(
        images: List<String>,
        id: String,
        title: String,
        description: String,
        location: String,
        lat: String,
        lng: String,
        youtube: List<String>,
        eventDate: String
    ) {
        if (isNetworkConnected(app.applicationContext)) {
            job_by_update = Job()
            job_by_update.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val imagesList: MutableList<MultipartBody.Part> = ArrayList()
                        val youtubeLinks: MutableList<RequestBody> = ArrayList()

                        for (imagePath in images) {
                            val file = File(imagePath)
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            val body = MultipartBody.Part.createFormData("images[]", file.name, requestFile)
                            imagesList.add(body)
                        }

                        for (link in youtube) {
                            val url = link.toRequestBody("text/plain".toMediaTypeOrNull())
                            youtubeLinks.add(url)
                        }

                        val idBody = id.toRequestBody("text/plain".toMediaTypeOrNull())
                        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                        val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                        val locationBody = location.toRequestBody("text/plain".toMediaTypeOrNull())
                        val latBody = lat.toRequestBody("text/plain".toMediaTypeOrNull())
                        val lngBody = lng.toRequestBody("text/plain".toMediaTypeOrNull())
                        val eventDateBody = eventDate.toRequestBody("text/plain".toMediaTypeOrNull())

                        val response: JsonObject = shareEventRepository.createEvent(imagesList, idBody, titleBody, descriptionBody, locationBody, latBody, lngBody, youtubeLinks, eventDateBody)

                        response.let {
                            withContext(Dispatchers.Main) {
                                if (BuildConfig.DEBUG) {
                                    Log.d("Response", response.toString())
                                }

                                if (response.get("success").asBoolean) {
                                    mCreateEventListener.getResult(response.get("message").asString)
                                } else {
                                    mCreateEventListener.onFailure(response.get("message").asString)
                                }
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        Log.e("ApiException",e.message.toString())
                        e.message?.let {
                            mCreateEventListener.onFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mCreateEventListener.onFailure(it)
                        }
                    } catch (e: Exception) {
                        Log.e("Exception",e.message.toString())
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