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
import okhttp3.RequestBody.Companion.asRequestBody
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ShareEventViewModel(
    private val shareEventRepository: ShareEventRepository,
    var app: Application
) : AndroidViewModel(app) {

    private var TAG: String = ShareEventViewModel::class.java.simpleName
    lateinit var mCreateEventListener: CreateEventListener

    fun createEvent(
        galleryPaths: List<MultipartBody.Part>,
        id: RequestBody,
        title: RequestBody, // Changed from String
        description: RequestBody, // Changed from String
        location: RequestBody, // Changed from String
        lat: RequestBody, // Changed from String
        lng: RequestBody, // Changed from String
        youtubeLinks: List<RequestBody>, // Changed from List<String>
        eventDate: RequestBody, // Changed from String
        access_token: RequestBody,
        user_id: RequestBody,
    ) {
        if (isNetworkConnected(app.applicationContext)) {
            val job = Job()

            CoroutineScope(Dispatchers.IO + job).launch {
                try {
                    // Use the galleryPaths and youtubeLinks directly as they are already the correct type
                    val response: JsonObject = shareEventRepository.createEvent(
                        gallery = galleryPaths,
                        id = id,
                        youtubeLinks = youtubeLinks,
                        description = description,
                        title = title,
                        location = location,
                        eventDate = eventDate,
                        lat = lat,
                        lng = lng,
                        access_token = access_token,
                        user_id = user_id
                    )

                    withContext(Dispatchers.Main) {
                        if (response.get("success").asBoolean) {
                            mCreateEventListener.getResult(response.get("message").asString)
                        } else {
                            mCreateEventListener.onFailure(response.get("message").asString)
                        }
                    }
                } catch (e: ApiException) {
                    handleException(e)
                } catch (e: NoInternetException) {
                    handleException(e)
                } catch (e: Exception) {
                    handleException(e)
                } finally {
                    job.complete()
                }
            }
        }
    }

    private suspend fun handleException(exception: Exception) {
        withContext(Dispatchers.Main) {
            mCreateEventListener.onFailure(exception.message ?: "An error occurred")
        }
    }
}
