package com.krs.vastipatrak.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.crashlytics.android.Crashlytics;
import com.krs.vastipatrak.model.ListProfileData;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class AppController extends Application {

    private static final String TAG = AppController.class
            .getSimpleName();

    //public static boolean isAdmin=false;
    private static AppController mInstance;
    public Realm realm;
    public boolean isUpdate = false;
    //public FirebaseAnalytics firebaseAnalytics;
    @Nullable
    public RealmResults<ListProfileData> mListSearchList = null;
    private RequestQueue mRequestQueue;
    public ArrayAdapter<String> dataAdapter;
    public List<String> lstgotra;
    public static synchronized AppController getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        MultiDex.install(this);
        mInstance = this;
        initRealm();
        initFirebaseAnalytics();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



    }

    private void initFirebaseAnalytics()
    {
       /* firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Set whether analytics collection is enabled for this app on this device.
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Set the minimum engagement time required before starting a session.
        firebaseAnalytics.setMinimumSessionDuration(2000);

        //Set the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        firebaseAnalytics.setSessionTimeoutDuration(300000);*/
    }

    public void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("vastipatrak.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);

    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(@NonNull Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(@NonNull Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }



}
