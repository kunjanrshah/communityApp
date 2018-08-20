package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.krs.vastipatrak.utils.Common;

public class LocationAlertService extends Service{

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


            }
        }, 10);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
