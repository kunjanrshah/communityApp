package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationAlertService extends Service {

    private SharedPreferences mSharedPreferences;
    private String TAG = LocationAlertService.class.getSimpleName();
    private HashMap<String,String>  lstLocation=null;
    private ArrayList<Handler> mlstHandler=null;
    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mlstHandler=new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    //  String id=  intent.getStringExtra("id");
    //  String time=  intent.getStringExtra("time");
     // boolean status= intent.getBooleanExtra("status",false);

        String storedHashMapString = mSharedPreferences.getString("hashString", null);
        Gson gson = new Gson();
        HashMap<String, String> testHashMap2;
        if (storedHashMapString != null) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            testHashMap2 = gson.fromJson(storedHashMapString, type);
            List<String> l = new ArrayList<>(testHashMap2.keySet());
            mlstHandler.clear();
            for (int i = 0; i < l.size(); i++) {
                final String profile_id = l.get(i);
                final String alert_time = testHashMap2.get(profile_id);
                Log.d(TAG,"profile_id: "+profile_id);
                Log.d(TAG,"alert_time: "+alert_time);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"handler profile_id :"+profile_id+" alert_time: "+alert_time);
                        SyncUser(profile_id);
                    }
                }, 60 * 1000 * Integer.parseInt(alert_time));
                mlstHandler.add(handler);
            }
        } // If we get killed, after returning from here, restart
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
                                    new Common.getDistance(null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Double.parseDouble(user_lat), Double.parseDouble(user_lng), Double.parseDouble(MainActivity.lat), Double.parseDouble(MainActivity.lon));
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
