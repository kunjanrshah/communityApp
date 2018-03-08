package com.krs.vastipatrak.app;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.ListProfileData;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();
    public static DatabaseHandler dbHelper;
    public static boolean isSplashLive = false;
    public static boolean isAdmin=false;
    private static AppController mInstance;
    public Realm realm;
    public boolean isUpdate = false;
    public FirebaseAnalytics firebaseAnalytics;
    public RealmResults<ListProfileData> mListSearchResult = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Activity mActivity;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public Activity getMainActivityContext() {
        return mActivity;
    }

    public void setMainActivityContext(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Fabric.with(this, new Crashlytics());
       // Fabric.with(this, new Crashlytics());
        mInstance = this;
      //  dbHelper = new DatabaseHandler(this);

        initRealm();
        initFirebaseAnalytics();
        // initialize the AdMob app
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        // Obtain the Firebase Analytics instance.

    }

    private void initFirebaseAnalytics()
    {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Set whether analytics collection is enabled for this app on this device.
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Set the minimum engagement time required before starting a session.
        firebaseAnalytics.setMinimumSessionDuration(2000);

        //Set the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        firebaseAnalytics.setSessionTimeoutDuration(300000);
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("vastipatrak.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


  /*  public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }*/

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
