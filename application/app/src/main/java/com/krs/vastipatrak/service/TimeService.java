package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.krs.vastipatrak.utils.Common;


public class TimeService extends Service implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
    // constant
   // public static final long NOTIFY_INTERVAL = 1 * 1000 *60; // 1 minute
    //String tag_json_obj = "jobj_req";
    // run on another Thread to avoid crash
   // private Handler mHandler = new Handler();
    // timer handling
    //private Timer mTimer = null;
   // GPSTracker gpsTracker;
    //double lat = 0, lon = 0;
    public String lat, lon;
    private LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;

    //SharedPreferences mSharedPreferences = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        if (Build.VERSION.SDK_INT >= 23) {
            if (Common.canAccessLocation(this)) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }

/*        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, MODE_PRIVATE);
            gpsTracker = new GPSTracker(this);
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);*/
    }


    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    @Override
    public void onDestroy() {
/*
        if (mTimer != null) {

            mTimer.cancel();
        }*/
        super.onDestroy();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

      //  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,TimeService.this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

/*    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    if (gpsTracker.IsGetLocation()) {
                     //   lat = gpsTracker.getLatitude();
                      //  lon = gpsTracker.getLongitude();
                        if (Common.isOnline(TimeService.this)) {
                            callProfileWS();
                        }
                    }

                    // display toast
                    //  Toast.makeText(getApplicationContext(), getDateTime(),Toast.LENGTH_SHORT).show();
                }

            });
        }

        private void callProfileWS() {

            if (Common.isOnline(TimeService.this)) {
                JSONObject mJsonObject = null;
                try {


                    mJsonObject = new JSONObject();
                    mJsonObject.put(Common.Constant_Class.USER_LAT, lat);
                    mJsonObject.put(Common.Constant_Class.USER_LNG, lon);

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
                                 Toast.makeText(TimeService.this, ""+message, Toast.LENGTH_SHORT).show();

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

    }*/
}
