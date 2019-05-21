package com.krs.vastipatrak.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.krs.vastipatrak.fragments.SyncFragment.mHandler;

/**
 * Created by kunjan on 28/2/18.
 */

public class SyncService extends Service {

    public static boolean isProcessing = false;
    @NonNull
    private final String TAG = "SyncService";
    private SharedPreferences mSharedPreferences;
    private boolean is_reset = false;
    private JsonObjectRequest jsonObjReq;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        if (intent != null) {
            ArrayList<String> selectedCities = intent.getStringArrayListExtra("selectedCities");
            is_reset = intent.getBooleanExtra(AppConstants.IS_RESET, false);
            callSyncWS(selectedCities);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {

        if (jsonObjReq != null) {
            jsonObjReq.cancel();
        }
        isProcessing = false;
        stopSelf();

        super.onDestroy();
    }

    private void callSyncWS(@NonNull ArrayList<String> selectedCities) {

        if (Utility.isOnline(this)) {

            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                if (selectedCities.size() > 0) {
                    mJsonObject.put(AppConstants.CITY, android.text.TextUtils.join(",", selectedCities));
                }
                mJsonObject.put(AppConstants.IS_RESET, is_reset);
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("sync_start", true);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            isProcessing = true;

            jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.SYNC_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {

                    Log.d(TAG, "response: " + response.toString());
                    isProcessing = true;
                    try {
                        Message msg = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        String success = response.getString(AppConstants.SUCCESS);
                        String message = response.getString(AppConstants.MESSAGE);

                        if (success.equalsIgnoreCase(AppConstants.TRUE)) {

                            JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);

                            for (int i = 0; i < mJsonArray.length(); i++) {

                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                Utility.SaveProfile(mJsondata);
                            }

                            Date c = Calendar.getInstance().getTime();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                            String formattedDate = df.format(c);
                            bundle.putString("sync_time", formattedDate);
                        }
                        isProcessing = false;
                        bundle.putBoolean("sync_start", false);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);

                        Toast.makeText(SyncService.this, "" + message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                    return params;
                }
            };

            jsonObjReq.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) {

                }
            });


            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }
}
