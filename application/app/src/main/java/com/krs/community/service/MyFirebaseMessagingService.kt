package com.krs.community.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.github.squti.guru.Guru
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.krs.community.R
import com.krs.community.activity.SplashActivity
import com.krs.community.utils.AppConstants
import com.krs.community.utils.Coroutines
import com.krs.community.utils.NotificationUtils


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("newToken", s)
        Guru.putString(AppConstants.DEVICE_TOKEN, s)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        Log.d(TAG, "data: " + remoteMessage.data)
        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
            val user_id = remoteMessage.data["user_id"].toString()
            val address = remoteMessage.data["address"].toString()
            val mobile = remoteMessage.data["mobile"].toString()
            val first_name = remoteMessage.data["first_name"].toString()
            val city = remoteMessage.data["city"].toString()
            val email = remoteMessage.data["email"].toString()
            val last_name = remoteMessage.data["last_name"].toString()
            var photo = remoteMessage.data["profile_pic"]
            var message = remoteMessage.data["message"].toString()

            if (message.contains("approved")) {
                message = "Approved your request, Please login"
            }
            if (photo.isNullOrEmpty()) {
                photo = "https://muslimghanchi.samajapp.in/uploads/users/noimage.png"
            }

            val fullName = "$first_name $last_name"
            Coroutines.main {
                val resultIntent = Intent(applicationContext, SplashActivity::class.java)
                if (!message.contains("Approved")) {
                    Guru.putBoolean(getString(R.string.notification), true)
                }
                showNotification(applicationContext, resultIntent, user_id, message, fullName, mobile, email, photo, address, city)
            }
        }
    }

    private fun showNotification(context: Context, intent: Intent, userId: String, message: String, fullname: String, mobile: String, email: String, photo: String, address: String, city: String) {

        val notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationUtils.getBitmapAsyncAndNotification(message, intent, fullname, mobile, email, photo, address, userId, city)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}