package com.krs.community.app;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.net.URLEncoder;

import static com.krs.community.utils.Utility.sendWhatsappMessage;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String Phone = intent.getStringExtra("Phone");
        String userId = intent.getStringExtra("userId");
        String action=intent.getStringExtra("action");
        if (action.equalsIgnoreCase("Call")) {

            String uri = "tel:" + Phone;
            Intent intentcall = new Intent(Intent.ACTION_DIAL);
            intentcall.setData(Uri.parse(uri));
            intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentcall);

            /*NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Integer.parseInt(userId));*/


            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);

        } else if (action.equalsIgnoreCase("WhatsApp")) {


            Log.e("Phone---",""+Phone);
            try {
                Uri uri = Uri.parse("whatsapp://send?phone=+91" + Phone + "&text=" + URLEncoder.encode("message", "UTF-8"));
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
            }

            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);

        } else if (action.equalsIgnoreCase("Approve")) {

            Log.e("Click--","Approve");

        }
    }
}
