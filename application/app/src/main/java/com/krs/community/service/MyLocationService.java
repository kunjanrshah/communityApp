package com.krs.community.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.community.app.AppController;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MyLocationService extends Service {

    private static final String TAG = "MyLocationService";
    private static final int LOCATION_INTERVAL = 1000 * 1 * 60;
    private static final float LOCATION_DISTANCE = 10f;
    @NonNull
    private final String tag_json_obj = "jobj_req";
    @NonNull
    private final LocationListener[] mLocationListeners = new LocationListener[]{new LocationListener(LocationManager.GPS_PROVIDER), new LocationListener(LocationManager.NETWORK_PROVIDER)};
    boolean checkGPS = false;
    boolean checkNetwork = false;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Location mLastLocation;
    private boolean tbtn_shre = false;
    @Nullable
    private LocationManager mLocationManager = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle mBundle = intent.getExtras();
            if (mBundle != null) {
                tbtn_shre = mBundle.getBoolean(AppConstants.TBTN_SHARE);
            }
        }

        initializeLocationManager();
        checkGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        checkNetwork = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location loc = null;
        if (checkGPS) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
                if (mLocationManager != null) {
                    loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc != null) {
                        mEditor.putString(AppConstants.CURR_LAT, String.valueOf(loc.getLatitude()));
                        mEditor.putString(AppConstants.CURR_LNG, String.valueOf(loc.getLongitude()));
                        mEditor.apply();
                    }
                }
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }
        }
        if (checkNetwork && loc == null) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[1]);
                loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    mEditor.putString(AppConstants.CURR_LAT, String.valueOf(loc.getLatitude()));
                    mEditor.putString(AppConstants.CURR_LNG, String.valueOf(loc.getLongitude()));
                    mEditor.apply();
                }
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "network provider does not exist, " + ex.getMessage());
            }
        }
        if (!checkNetwork && !checkGPS) {
            Toast.makeText(this, "No Service Provider is available", Toast.LENGTH_SHORT).show();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
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
            mLocationManager = null;
        }
        /*if (!mSharedPreferences.getString(AppConstants.TBTN_SHARE, "0").equalsIgnoreCase("")) {
          //  userLocationUpdateWS();
        }*/
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void userLocationUpdateWS() {

        if (Utility.isOnline(MyLocationService.this)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_LAT, mLastLocation.getLatitude());
                mJsonObject.put(AppConstants.USER_LNG, mLastLocation.getLongitude());
                String is_loc = "0";
                if (mSharedPreferences.getString(AppConstants.TBTN_SHARE, "").equalsIgnoreCase("1")) {
                    is_loc = "1";
                } else if (mSharedPreferences.getString(AppConstants.TBTN_SHARE, "").equalsIgnoreCase("0")) {
                    is_loc = "0";
                }
                mJsonObject.put(AppConstants.IS_LOCATION_ENABLE, is_loc);
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.IS_UPDATE, "1");
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.PROFILE_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String success = response.getString(AppConstants.SUCCESS);
                        if (success.equalsIgnoreCase(AppConstants.TRUE)) {
                            if (mSharedPreferences.getString(AppConstants.TBTN_SHARE, "0").equalsIgnoreCase("1")) {
                                Log.d(TAG, "step yes");
                                Toast.makeText(MyLocationService.this, "Vastipatrak is sharing your location!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "step no");
                                Toast.makeText(MyLocationService.this, "Stop Sharing location successfully!", Toast.LENGTH_SHORT).show();
                            }
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
            // Adding request to request queue
            AppController.mApplication.addToRequestQueue(jsonObjReq, tag_json_obj);
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
            mEditor.putString(AppConstants.CURR_LAT, String.valueOf(mLastLocation.getLatitude()));
            mEditor.putString(AppConstants.CURR_LNG, String.valueOf(mLastLocation.getLongitude()));
            mEditor.apply();
            if (tbtn_shre) {
                userLocationUpdateWS();
            }
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
