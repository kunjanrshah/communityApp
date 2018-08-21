package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationAlertService extends Service{

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String storedHashMapString = mSharedPreferences.getString("hashString", null);
        Gson gson = new Gson();
        HashMap<String, String> testHashMap2;
        if (storedHashMapString != null) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            testHashMap2 = gson.fromJson(storedHashMapString, type);
                List<String> l = new ArrayList<>(testHashMap2.keySet());
                for(int i=0; i<l.size(); i++)
                {
                    String profile_id=l.get(i);
                    String alert_time=testHashMap2.get(profile_id);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           // new Common.getDistance(null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Double.parseDouble(user_lat), Double.parseDouble(user_lng), Double.parseDouble(MainActivity.lat), Double.parseDouble(MainActivity.lon));
                        }
                    }, 60*1000* Integer.parseInt(alert_time));
                }
        } // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
