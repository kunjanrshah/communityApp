package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.app.AppController;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.krs.vastipatrak.utils.Common.CalculationByDistance;
import static com.krs.vastipatrak.utils.Common.round;

public class LocationAlertService extends Service {

    HashMap<String, Timer> mlstMapTimer = null;
    boolean isStop;
    String id = "";
    String time = "";
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
        if (intent == null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LocationAlertService.this, "Intent null", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Bundle mBundle = intent.getExtras();
            if (mBundle != null) {
                isStop = mBundle.getBoolean("isStop");
                id = mBundle.getString("id");
                time = mBundle.getString("time");
                Log.d(TAG, "Timer profile_id: " + id + " alert_time: " + time +" isStop: "+isStop);
                if (!isStop) {
                    Timer mTimer = new Timer();
                    mlstMapTimer.put(id, mTimer);
                    long NOTIFY_INTERVAL = 60 * 1000;
                    mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(id), 0, NOTIFY_INTERVAL * Integer.parseInt(time));
                } else if (isStop) {
                    if (mlstMapTimer.get(id) != null) {
                        mlstMapTimer.get(id).cancel();
                    }
                }
            }
        }

        // If we get killed, after returning from here, restart
        return Service.START_REDELIVER_INTENT;
    }

    private void SyncUser(final String profile_id) {
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
                                String user_lat = "", user_lng = "", name = "";
                                if (mJsondata.has(Common.Constant_Class.USER_LAT)) {
                                    user_lat = mJsondata.getString(Common.Constant_Class.USER_LAT);
                                }
                                if (mJsondata.has(Common.Constant_Class.USER_LNG)) {
                                    user_lng = mJsondata.getString(Common.Constant_Class.USER_LNG);
                                }
                                if (mJsondata.has(Common.Constant_Class.FIRST_NAME)) {
                                      name = mJsondata.getString(Common.Constant_Class.FIRST_NAME);
                                }
                                if (mJsondata.has(Common.Constant_Class.LAST_NAME)) {
                                    name = name + " " + mJsondata.getString(Common.Constant_Class.LAST_NAME);
                                }

                                String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
                                String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");

                                if (user_lat != null && user_lng != null && !user_lat.isEmpty() && !user_lng.isEmpty() && !curr_lat.isEmpty() && !curr_lng.isEmpty()) {
                                    new getDistance(profile_id, name).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Double.parseDouble(user_lat), Double.parseDouble(user_lng), Double.parseDouble(curr_lat), Double.parseDouble(curr_lng));
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Common.Constant_Class.INIT_TIMEOUT, Common.Constant_Class.DEFAULT_MAX_RETRIES, Common.Constant_Class.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        } else {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LocationAlertService.this, "Please connect your internet", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

/*
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
*/

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent, String id) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, "", id);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class getDistance extends AsyncTask<Double, String, String> {

        String name = "", id = "";

        getDistance(String id, String name) {
            this.name = name;
            this.id = id;
        }

        @Override
        protected String doInBackground(Double... strings) {
            double result_in_kms = CalculationByDistance(strings[0], strings[1], strings[2], strings[3]);
            result_in_kms = round(result_in_kms, 2);
            return String.valueOf(result_in_kms);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            String dateToStr = format.format(today);
            System.out.println(dateToStr);

            Log.d("getDistance", "distance: " + s);
            String notification = "Distance from " + name + " is " + s + " at " + dateToStr, title = "MEDK Vastipatrak", timestamp = "";
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();
            Log.d(TAG, "notification: " + notification);
         /*   if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra(Common.Constant_Class.PUSH_MESSAGE, notification);
                LocalBroadcastManager.getInstance(LocationAlertService.this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {*/
            // app is in background, show the notification in notification tray
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra(Common.Constant_Class.PUSH_MESSAGE, notification);
            resultIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
            showNotificationMessage(getApplicationContext(), title, notification, timestamp, resultIntent, id);
            // }
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
