package com.krs.community.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.squti.guru.Guru;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.NotificationUtils;

import org.jetbrains.annotations.NotNull;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NotNull String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        Guru.putString(AppConstants.DEVICE_TOKEN, s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            String userId = remoteMessage.getData().get("user_id");
            String userName = remoteMessage.getData().get("user_name");
            String userPhoto = remoteMessage.getData().get("user_photo");
            String message = remoteMessage.getData().get("message");

            long tsLong = System.currentTimeMillis() / 1000;
            String ts = Long.toString(tsLong);

            Intent resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
            showNotification(getApplicationContext(), userName, message, ts, resultIntent, userPhoto, userId);
        }
    }

    private void showNotification(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl, String user_id) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationUtils.getBitmapAsyncAndNotification(imageUrl, timeStamp, title, message, intent, user_id);
    }
}
