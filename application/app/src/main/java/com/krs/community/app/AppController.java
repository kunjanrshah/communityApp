package com.krs.community.app;


import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.krs.community.R;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.ConnectivityReceiver;
import com.krs.community.utils.LocaleHelper;
import com.krs.community.volley.LruBitmapCache;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;
    public boolean isUpdate = false;
    //public FirebaseAnalytics firebaseAnalytics;
    /*public ArrayAdapter<String> dataAdapter;
    public List<String> lstGotra;*/
    public ArrayList<String> lstGotra;
    public ArrayList<String> lstNative;
    public ArrayList<String> lstEducation;

    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor mEditor;
    ConnectivityReceiver broadcastRevcevier;
    private RequestQueue mRequestQueue;
    public GoogleSignInClient mGoogleSignInClient;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;
    public Typeface typeface,typeface_bold;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.montserrat_regular);
        typeface_bold = ResourcesCompat.getFont(getApplicationContext(),R.font.montserrat_semibold);

        final Fabric fabric = new Fabric.Builder(this).kits(new Crashlytics()).debuggable(true).build();
        Fabric.with(fabric);

        MultiDex.install(this);
        mInstance = this;
        lstGotra =new ArrayList<>();
        lstNative =new ArrayList<>();
        lstEducation =new ArrayList<>();

        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        initFirebaseAnalytics();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        broadcastRevcevier = new ConnectivityReceiver();
        registerReceiver(broadcastRevcevier, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        Log.d(TAG,"AppController Screen");
        mEditor.putBoolean(getString(R.string.app_create),true);
        mEditor.apply();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
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

       public RequestQueue getRequestQueue() {
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
