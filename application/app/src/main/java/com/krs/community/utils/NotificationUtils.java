package com.krs.community.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.krs.community.R;
import com.krs.community.app.NotificationReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationUtils {

    private final Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return !isInBackground;
    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private static long getTimeMilliSec(String timeStamp) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBitmapAsyncAndNotification(String imageUrl, String timeStamp, String title, String message, @NonNull Intent intent, String id) {
        final Bitmap[] bitmap = {null};
        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Glide.with(mContext.getApplicationContext())
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmap[0] = resource;
                                displayImageNotification(bitmap[0], timeStamp, title, message, intent, id);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.user_profile);
                                displayImageNotification(bitmap1, timeStamp, title, message, intent, id);
                            }
                        });
            }
        }
    }

    private void displayImageNotification(Bitmap bitmap, String timeStamp, String title, String message, @NonNull Intent intent, String id) {
        String CHANNEL_ID = mContext.getString(R.string.notification_channel_id);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent broadcastIntent = new Intent(mContext, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(mContext, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine("9427051418");
        inboxStyle.addLine("kunjanrshah@gmail.com");
        inboxStyle.addLine("Home Address");
        inboxStyle.addLine("Ahmedabad, Gujarat");
        /*NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);*/

        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.notification_icon).setTicker(title)
                .setWhen(getTimeMilliSec(timeStamp)).setAutoCancel(true)
                .setContentTitle(title).setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)//bigPictureStyle
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                .addAction(R.drawable.bg_circle, "Call", resultPendingIntent)
                .addAction(R.drawable.bg_circle, "WhatsApp", actionIntent)
                .addAction(R.drawable.bg_circle, "Approve", resultPendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLargeIcon(bitmap).setContentText(message)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.RED, 0, 1).build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CommunityAppNotification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (notificationManager != null) {
            notificationManager.notify(Integer.parseInt(id), notification);
            playNotificationSound();
        }
    }

/*
    public void showNotificationMessage(final String title, final String message, final String timeStamp, @NonNull Intent intent, @Nullable String imageUrl, String id) {
        // Check for empty push message
        if (TextUtils.isEmpty(message)) return;

        // notification icon
        final int icon = R.drawable.ic_medk;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, resultPendingIntent, alarmSound,id);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,id);
                }
            }
        } else {
            // showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            showNotification(icon, message, timeStamp, resultPendingIntent, alarmSound, id);
            playNotificationSound();
        }
    }

    private void showNotification(int icon, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, String id) {

        Notification.Builder builder = new Notification.Builder(mContext);

        Notification notification = builder.setContentTitle(message)
                */
    /*.setContentText(message)*//*

     */
    /* .setTicker("New Message Alert!")*//*
.setSmallIcon(icon).setAutoCancel(false).setSound(alarmSound).setWhen(getTimeMilliSec(timeStamp)).setContentIntent(resultPendingIntent).build();
        String CHANNEL_ID = id;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "NotificationDemo", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        if (id == null || id.isEmpty()) {
            id = "0";
        }
        Log.d("NotificationUtils", "NotificationUtils:" + Integer.parseInt(id));
        notificationManager.notify(Integer.parseInt(id), notification);
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound,String id) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.notification_icon).setTicker(title).setWhen(0).setAutoCancel(true).setContentTitle(title).setContentIntent(resultPendingIntent).setSound(alarmSound).setStyle(inboxStyle).setWhen(getTimeMilliSec(timeStamp))
                // .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon)).setContentText(message).build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(Integer.parseInt(id), notification);
        }
    }
*/


}
