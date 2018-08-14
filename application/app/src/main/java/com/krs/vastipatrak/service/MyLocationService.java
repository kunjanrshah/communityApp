package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.krs.vastipatrak.utils.Common.Constant_Class.LOCATION_INTERVAL;


public class MyLocationService extends Service {

    private static final String TAG = "MyLocationService";
    //private static final int LOCATION_INTERVAL = 1000 * 3 * 60;
    private static final float LOCATION_DISTANCE = 1f;
    @NonNull
    private final String tag_json_obj = "jobj_req";
    @NonNull
    private final LocationListener[] mLocationListeners = new LocationListener[]{new LocationListener(LocationManager.GPS_PROVIDER), new LocationListener(LocationManager.NETWORK_PROVIDER)};
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Location mLastLocation;
    @Nullable
    private LocationManager mLocationManager = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
        initializeLocationManager();
        try {
            assert mLocationManager != null;
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        userLocationUpdateWS("0");
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void userLocationUpdateWS(@NonNull final String isUpdate) {

        if (Common.isOnline(MyLocationService.this)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_LAT, mLastLocation.getLatitude());
                mJsonObject.put(Common.Constant_Class.USER_LNG, mLastLocation.getLongitude());
                mJsonObject.put(Common.Constant_Class.IS_LOCATION_ENABLE, mSharedPreferences.getBoolean(Common.Constant_Class.TBTN_SHARE, false));
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.IS_UPDATE, isUpdate);
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.PROFILE_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        //  String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            Handler mHandler=new Handler();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isUpdate.equals("1")) {
                                        Log.d(TAG,"step yes");
                                        Toast.makeText(MyLocationService.this, "Vastipatrak is sharing your location!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d(TAG,"step no");
                                        Toast.makeText(MyLocationService.this, "Stop Sharing location successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },1000);

                            //String data = response.getString(Common.Constant_Class.DATA);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d("TimeService", "Error: " + error.getMessage());
                }
            }){
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN,mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN,""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }

    private class LocationListener implements android.location.LocationListener {


        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            /*mEditor.putString(Common.Constant_Class.MY_LATITUDE, String.valueOf(location.getLatitude()));
            mEditor.putString(Common.Constant_Class.MY_LONGITUDE, String.valueOf(location.getLongitude()));
            mEditor.apply();*/
            userLocationUpdateWS("1");

           /* new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2000);*/

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}
