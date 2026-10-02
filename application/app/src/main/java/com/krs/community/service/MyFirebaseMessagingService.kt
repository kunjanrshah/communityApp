package com.krs.community.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.squti.guru.Guru
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.krs.community.R
import com.krs.community.activity.SplashActivity
import com.krs.community.auth.TokenManager
import com.krs.community.graphql.GraphQLClientProvider
import com.krs.community.repositories.DeviceTokenRepository
import com.krs.community.utils.AppConstants
import com.krs.community.utils.Coroutines
import com.krs.community.utils.NotificationUtils


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var tokenManager: TokenManager
    private var deviceTokenRepository: DeviceTokenRepository? = null

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(applicationContext)
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("newToken", s)
        Guru.putString(AppConstants.DEVICE_TOKEN, s)
        if (::tokenManager.isInitialized) {
            val accessToken = tokenManager.accessToken
            if (!accessToken.isNullOrEmpty()) {
                if (deviceTokenRepository == null) {
                    deviceTokenRepository = DeviceTokenRepository(
                        GraphQLClientProvider.provideApolloClient(applicationContext)
                    )
                }
                Coroutines.io {
                    deviceTokenRepository?.updateDeviceToken()
                }
            }
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        Log.d(TAG, "data: " + remoteMessage.data)
        if (remoteMessage.data.isEmpty() && remoteMessage.notification == null) return

        Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
        val payload = remoteMessage.data
        val userId = payload["user_id"].orEmpty()
        val address = payload["address"].orEmpty()
        val mobile = payload["mobile"].orEmpty()
        val firstName = payload["first_name"].orEmpty()
        val city = payload["city"].orEmpty()
        val email = payload["email"].orEmpty()
        val lastName = payload["last_name"].orEmpty()
        val notificationType = payload["notification_type"]
        val role = payload["role"]
        val photo = payload["profile_pic"].takeUnless { it.isNullOrBlank() }
            ?: "https://muslimghanchi.samajapp.in/uploads/users/noimage.png"

        var message = payload["message"]
            ?: remoteMessage.notification?.body
            ?: remoteMessage.notification?.title
            ?: getString(R.string.app_name)

        if (message.contains("approved", ignoreCase = true)) {
            message = "Approved your request, Please login"
        }

        val fullName = "$firstName $lastName".trim()
        Coroutines.main {
            val resultIntent = Intent(applicationContext, SplashActivity::class.java)
            resultIntent.putExtra("notification_type", notificationType)
            resultIntent.putExtra("role", role)
            resultIntent.putExtra("user_id", userId)
            if (!message.contains("Approved", ignoreCase = true)) {
                Guru.putBoolean(getString(R.string.notification), true)
            }
            showNotification(
                applicationContext,
                resultIntent,
                userId,
                message,
                fullName,
                mobile,
                email,
                photo,
                address,
                city,
                notificationType
            )
        }
    }

    private fun showNotification(
        context: Context, intent: Intent, userId: String, message: String,
        fullname: String, mobile: String, email: String, photo: String,
        address: String, city: String, notificationType: String?
    ) {

        val notificationUtils = NotificationUtils(context)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationUtils.getBitmapAsyncAndNotification(
            message, intent, fullname, mobile, email, photo, address, userId, city, notificationType
        )
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}