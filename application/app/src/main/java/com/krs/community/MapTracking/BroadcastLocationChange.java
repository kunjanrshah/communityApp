package com.krs.community.MapTracking;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;


class BroadcastLocationChange extends IntentService{

    public BroadcastLocationChange(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
