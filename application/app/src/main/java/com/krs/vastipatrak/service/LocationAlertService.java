package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.app.Config;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LocationAlertService extends Service {

    HashMap<String, Timer> mlstMapTimer = null;
    private SharedPreferences mSharedPreferences;
    private String TAG = LocationAlertService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mlstMapTimer = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle mBundle = intent.getExtras();
        boolean isStop;
        String id = "";
        String time = "";
        if (mBundle != null) {
            isStop = mBundle.getBoolean("isStop");
            id = mBundle.getString("id");
            time = mBundle.getString("time");
            Log.d(TAG, "Timer profile_id: " + id + " alert_time: " + time);
            if (!isStop) {
                Timer mTimer = new Timer();
                mlstMapTimer.put(id, mTimer);
                long NOTIFY_INTERVAL = 60 * 1000;
                mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(id), NOTIFY_INTERVAL * Integer.parseInt(time), NOTIFY_INTERVAL * Integer.parseInt(time));
            } else if (isStop) {
                if (mlstMapTimer.get(id) != null) {
                    mlstMapTimer.get(id).cancel();
                }
            }
        }
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void SyncUser(String profile_id) {
        if (Common.isOnline(this)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.PROFILE_ID, profile_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sync_url = Common.Constant_Class.SYNC_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                String user_lat = "", user_lng = "";
                                if (mJsondata.has(Common.Constant_Class.USER_LAT)) {
                                    user_lat = mJsondata.getString(Common.Constant_Class.USER_LAT);
                                }
                                if (mJsondata.has(Common.Constant_Class.USER_LNG)) {
                                    user_lng = mJsondata.getString(Common.Constant_Class.USER_LNG);
                                }
                                if (user_lat != null && user_lng != null && !user_lat.isEmpty() && !user_lng.isEmpty()) {
                                    new getDistance().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Double.parseDouble(user_lat), Double.parseDouble(user_lng), Double.parseDouble(MainActivity.lat), Double.parseDouble(MainActivity.lon));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Common.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    private String getDistanceOnRoad(double latitude, double longitude, double prelatitute, double prelongitude) {
        String result_in_kms = "";
        String strurl = "http://maps.google.com/maps/api/directions/xml?origin=" + latitude + "," + longitude + "&destination=" + prelatitute + "," + prelongitude + "&sensor=false&units=metric";
        String tag[] = {"text"};
        //  HttpResponse response = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strurl);
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream is = urlConnection.getInputStream();

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            if (doc != null) {
                NodeList nl;
                ArrayList args = new ArrayList();
                for (String s : tag) {
                    nl = doc.getElementsByTagName(s);
                    if (nl.getLength() > 0) {
                        Node node = nl.item(nl.getLength() - 1);
                        args.add(node.getTextContent());
                    } else {
                        args.add(" - ");
                    }
                }
                result_in_kms = String.format("%s", args.get(0));
                Log.d("getDistanceOnRoad", "step result_in_kms :" + result_in_kms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result_in_kms;
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class getDistance extends AsyncTask<Double, String, String> {

        @Override
        protected String doInBackground(Double... strings) {
            String result_in_kms = getDistanceOnRoad(strings[0], strings[1], strings[2], strings[3]);
            return result_in_kms;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("getDistance", "distance: " + s);
            String notification = s, title = "MEDK Vastipatrak", timestamp = "";
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();
            if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra(Common.Constant_Class.PUSH_MESSAGE, notification);
                LocalBroadcastManager.getInstance(LocationAlertService.this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra(Common.Constant_Class.PUSH_MESSAGE, notification);
                resultIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                showNotificationMessage(getApplicationContext(), title, notification, timestamp, resultIntent);
            }
        }
    }

    class TimeDisplayTimerTask extends TimerTask {

        String profile_id = "";

        TimeDisplayTimerTask(String profile_id) {
            this.profile_id = profile_id;
        }

        @Override
        public void run() {
            // run on another thread
            Log.d(TAG, "TimeDisplayTimerTask profile_id: " + profile_id);
            SyncUser(profile_id);
        }
    }
}
