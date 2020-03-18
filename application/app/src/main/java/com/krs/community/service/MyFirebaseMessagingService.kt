package com.krs.community.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.squti.guru.Guru
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.app.AppDatabase
import com.krs.community.repositories.SmartFilterRepository
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.*
import kotlinx.coroutines.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class MyFirebaseMessagingService : FirebaseMessagingService() , KodeinAware{
    override val kodein by kodein()

    private lateinit var completableJob: CompletableJob


    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("newToken", s)

        Guru.putString(AppConstants.DEVICE_TOKEN, s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.from)
        if (remoteMessage.data.size > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
            val userId = remoteMessage.data["user_id"]
            val message = remoteMessage.data["message"]
            val tsLong = System.currentTimeMillis() / 1000
            val ts = java.lang.Long.toString(tsLong)



            smartFilterSearch(userId)

            val resultIntent = Intent(applicationContext, DashboardActivity::class.java)

            //showNotification(getApplicationContext(), userName, message, ts, resultIntent, userPhoto, userId);
        }
    }

    fun smartFilterSearch(userId: String?) {
        completableJob = Job()
        completableJob.let { thejob ->

            val jsonObj = JSONObject()
            jsonObj.put(getString(R.string.id), userId)
            val updated = JsonParser().parse(jsonObj.toString()) as JsonObject

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                   val mSmartFilterRepository= SmartFilterRepository(ApiServices(), AppDatabase.invoke(applicationContext))
                    val response = mSmartFilterRepository.searchByName(updated)
                    response.let {
                        withContext(Dispatchers.Main) {

                            getMembers(response)

                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                       // mByFilterListener.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                       // mByFilterListener.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                       // mByFilterListener.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    private fun getMembers(response: SmartFilterResponse) {


    }

    private fun showNotification(context: Context, title: String, message: String, timeStamp: String, intent: Intent, imageUrl: String, user_id: String) {
        val notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationUtils.getBitmapAsyncAndNotification(imageUrl, timeStamp, title, message, intent, user_id)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}