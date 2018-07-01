package com.krs.vastipatrak.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.app.Config;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        /*if (remoteMessage == null)
            return;*/

       /* // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }*/

        //MyFirebaseMessagingService: From: 900336668958
        //MyFirebaseMessagingService: Data Payload: {user_id=4185, notification=Please approve Abcd1 's Request }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            String user_id = remoteMessage.getData().get("user_id");
            String notification = remoteMessage.getData().get("notification");
            String title = remoteMessage.getData().get("title");
            String location = remoteMessage.getData().get("location");
            String imgUrl = remoteMessage.getData().get("imgUrl");
            //   String timestamp= remoteMessage.getData().get("timestamp");

            if (title == null || title.isEmpty()) {
                title = "MEDK Vastipatrak";
            }

            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            try {
                JSONObject json = new JSONObject();
                json.put("notification", notification);
                json.put("title", title);
                //json.put("isBackground","");
                json.put("imageUrl", imgUrl);
                json.put("user_id", user_id);
                json.put("timestamp", ts);
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

  /*  private void handleNotification(String message) {
        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
    }*/

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        String notification = "", title = "", imageUrl = "", timestamp = "",user_id="";

        try {

            notification = json.getString("notification");
            timestamp = json.getString("timestamp");
            if (json.has("title")) {
                title = json.getString("title");
            }
            if (json.has("imageUrl")) {
                imageUrl = json.getString("imageUrl");
            }
            if (json.has("user_id")) {
                user_id = json.getString("user_id");
            }

            //  boolean isBackground = data.getBoolean("is_background");


            //  JSONObject payload = json.getJSONObject("payload");

            Log.e(TAG, "notification: " + notification);
            Log.e(TAG, "title: " + title);
            //  Log.e(TAG, "isBackground: " + isBackground);
            //Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra(Common.Constant_Class.PUSH_MESSAGE, notification);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra(Common.Constant_Class.PUSH_MESSAGE, notification);
                resultIntent.putExtra(Common.Constant_Class.USER_ID, user_id);
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, notification, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, notification, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
