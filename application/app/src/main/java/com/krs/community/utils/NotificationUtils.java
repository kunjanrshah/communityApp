package com.krs.community.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.krs.community.R;
import com.krs.community.app.NotificationReceiver;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class NotificationUtils {

    private final Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
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

    public void getBitmapAsyncAndNotification(String message, Intent intent, String fullname, String mobile, String email, String photo, String homeAddress, String userId, String CityName) {
        final Bitmap[] bitmap = {null};
        if (!TextUtils.isEmpty(photo)) {
            if (photo.length() > 4 && Patterns.WEB_URL.matcher(photo).matches()) {
                Glide.with(mContext.getApplicationContext())
                        .asBitmap()
                        .load(photo)
                        .thumbnail(0.5f)
                        .transition(withCrossFade())
                        .apply(RequestOptions.circleCropTransform())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmap[0] = resource;
                                displayImageNotification(bitmap[0], intent, message, fullname, mobile, email, homeAddress, userId, CityName);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.user_profile);
                                displayImageNotification(bitmap1, intent, message, fullname, mobile, email, homeAddress, userId, CityName);
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.user_profile);
                                displayImageNotification(bitmap1, intent, message, fullname, mobile, email, homeAddress, userId, CityName);
                            }
                        });
            }
        }
    }

    private void displayImageNotification(Bitmap bitmap, @NonNull Intent intent, String message, String fullname, String mobile, String email, String homeAddress, String userId, String CityName) {

        String CHANNEL_ID = mContext.getString(R.string.notification_channel_id);
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        PendingIntent pendingIntentCall = null, pendingIntentWhatsApp = null, pendingIntentApprove = null;

        Intent intentCall = new Intent(mContext, NotificationReceiver.class);
        intentCall.putExtra("action", "Call");
        intentCall.putExtra("Phone", mobile);
        intentCall.putExtra("userId", userId);
        intentCall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intentWhatsApp = new Intent(mContext, NotificationReceiver.class);
        intentWhatsApp.putExtra("action", "WhatsApp");
        intentWhatsApp.putExtra("Phone", mobile);
        intentWhatsApp.putExtra("userId", userId);
        intentWhatsApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (!message.contains("Approved")) {
            Intent intentApprove = new Intent(mContext, NotificationReceiver.class);
            intentApprove.putExtra("action", "Approve");
            intentApprove.putExtra("Phone", mobile);
            intentApprove.putExtra("userId", userId);
            intentApprove.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntentApprove = PendingIntent.getBroadcast(mContext, 2, intentApprove, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        pendingIntentCall = PendingIntent.getBroadcast(mContext, 0, intentCall, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntentWhatsApp = PendingIntent.getBroadcast(mContext, 1, intentWhatsApp, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(fullname);
        inboxStyle.addLine(email);
        inboxStyle.addLine(mobile);

        if (!message.contains("Approved")) {
            inboxStyle.addLine(homeAddress);
        }
        inboxStyle.addLine(CityName);

        Notification notification;
        if (!message.contains("Approved")) {
            notification = mBuilder
                    .setTicker(mContext.getResources().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(mContext.getResources().getColor(R.color.white))
                    .setContentTitle(message)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setLargeIcon(bitmap).setContentText(message)
                    .setOngoing(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.RED, 0, 1)
                    .addAction(R.drawable.ic_code_scanner_flash_on, "Approve", pendingIntentApprove)
                    .addAction(R.drawable.ic_code_scanner_flash_on, "WhatsApp", pendingIntentWhatsApp)
                    .addAction(R.drawable.ic_code_scanner_flash_on, "Call", pendingIntentCall)
                    .build();
        } else {
            notification = mBuilder
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(mContext.getResources().getColor(R.color.white))
                    .setTicker(mContext.getResources().getString(R.string.app_name))
                    .setContentTitle(message)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setLargeIcon(bitmap).setContentText(message)
                    .setOngoing(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.RED, 0, 1)
                    .addAction(R.drawable.ic_code_scanner_flash_on, "WhatsApp", pendingIntentWhatsApp)
                    .addAction(R.drawable.ic_code_scanner_flash_on, "Call", pendingIntentCall)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, mContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (notificationManager != null) {
            notificationManager.notify(Integer.parseInt(userId), notification);
            playNotificationSound();
        }
    }
}
