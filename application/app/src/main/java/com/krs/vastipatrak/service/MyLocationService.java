package com.krs.vastipatrak.service;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONObject;


public class MyLocationService extends Service {

    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 2 * 1;
    private static final float LOCATION_DISTANCE = 10f;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Location mLastLocation;
    String tag_json_obj = "jobj_req";

    private class LocationListener implements android.location.LocationListener {


        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            /*mEditor.putString(Common.Constant_Class.MY_LATITUDE, String.valueOf(location.getLatitude()));
            mEditor.putString(Common.Constant_Class.MY_LONGITUDE, String.valueOf(location.getLongitude()));
            mEditor.commit();*/
            callProfileWS();

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

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        initializeLocationManager();
        try {
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
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void callProfileWS() {

        if (Common.isOnline(MyLocationService.this)) {
            JSONObject mJsonObject = null;
            try {


                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_LAT, mLastLocation.getLatitude());
                mJsonObject.put(Common.Constant_Class.USER_LNG, mLastLocation.getLongitude());

            } catch (Exception e) {
                e.printStackTrace();
            }


            final String profile_url = Common.Constant_Class.PROFILE_URL + mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, profile_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            String data = response.getString(Common.Constant_Class.DATA);
                            Toast.makeText(MyLocationService.this,  message, Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("TimeService", "Error: " + error.getMessage());
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }


}
