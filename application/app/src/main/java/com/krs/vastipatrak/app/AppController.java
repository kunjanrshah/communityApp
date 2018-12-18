package com.krs.vastipatrak.app;


import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.ConnectivityReceiver;
import com.krs.vastipatrak.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    //public static boolean isAdmin=false;
    private static AppController mInstance;
    public Realm realm;
    public boolean isUpdate = false;
    //public FirebaseAnalytics firebaseAnalytics;
    @Nullable
    public RealmResults<ListProfileData> mListSearchList = null;
    /*public ArrayAdapter<String> dataAdapter;
    public List<String> lstGotra;*/
    public ArrayList<String> lstGotra;
    public ArrayList<String> lstNative;
    public ArrayList<String> lstEducation;
    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor mEditor;
    ConnectivityReceiver broadcastRevcevier;
    private RequestQueue mRequestQueue;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Fabric fabric = new Fabric.Builder(this).kits(new Crashlytics()).debuggable(true).build();
        Fabric.with(fabric);

        MultiDex.install(this);
        mInstance = this;
        lstGotra =new ArrayList<>();
        lstNative =new ArrayList<>();
        lstEducation =new ArrayList<>();
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        initRealm();
        initFirebaseAnalytics();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        broadcastRevcevier = new ConnectivityReceiver();
        registerReceiver(broadcastRevcevier, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        Log.d(TAG,"AppController Screen");
        mEditor.putBoolean("app_create",true);
        mEditor.apply();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (broadcastRevcevier != null) {
            unregisterReceiver(broadcastRevcevier);
            broadcastRevcevier = null;
        }
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    private void initFirebaseAnalytics() {
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
        RealmConfiguration config = new RealmConfiguration.Builder().name(getString(R.string.realm_name)).schemaVersion(1).deleteRealmIfMigrationNeeded().build();
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
