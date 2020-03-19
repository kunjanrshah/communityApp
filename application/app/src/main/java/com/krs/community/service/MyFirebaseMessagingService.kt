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
import com.krs.community.model.LoginResponse
import com.krs.community.repositories.SmartFilterRepository
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.*
import kotlinx.coroutines.*
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var completableJob: CompletableJob

    var message = ""
    var userId = ""
    lateinit var mSmartFilterRepository: SmartFilterRepository
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("newToken", s)

        Guru.putString(AppConstants.DEVICE_TOKEN, s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.from)
        if (remoteMessage.data.size > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
            userId = remoteMessage.data["user_id"].toString()
            message = remoteMessage.data["message"].toString()
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
            mSmartFilterRepository= SmartFilterRepository(ApiServices(), AppDatabase(applicationContext))

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mSmartFilterRepository.searchByUser(updated)
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

    private suspend fun getMembers(response: LoginResponse) {

        val firstName = response.data.firstName
        val subCastId = response.data.subCastId
        val mobile = response.data.mobile
        val email = response.data.emailAddress
        val photo =resources.getString(R.string.base_url_thumb)+ response.data.profilePic
        val homeAddress = response.data.address
        val cityId = response.data.cityId
        var Lastname=""
        var CityName =""

        Coroutines.io {
            Lastname =   mSmartFilterRepository.getLastNameById(subCastId.toInt())
            CityName =   mSmartFilterRepository.getCityName(cityId)
            val Fullname = firstName +" "+Lastname

            Log.e("Fullname--",""+Fullname)
            Coroutines.main {
                val resultIntent = Intent(applicationContext, DashboardActivity::class.java)
                showNotification(getApplicationContext(),  resultIntent,Fullname,mobile,email,photo,homeAddress,CityName);
            }

        }


    }

    private fun showNotification(context: Context, intent: Intent, fullname: String, mobile: String, email: String, photo: String, homeAddress: String,CityName: String) {
        val notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationUtils.getBitmapAsyncAndNotification(message, intent,fullname,mobile,email,photo,homeAddress,userId,CityName)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}