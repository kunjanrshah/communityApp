package com.krs.community.app


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.StrictMode
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDex
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.krs.community.R
import com.krs.community.repositories.RegisterRepository
import com.krs.community.retrofit.ApiServices
import com.krs.community.retrofit.RetrofitBase
import com.krs.community.utils.AppConstants
import com.krs.community.utils.ConnectivityReceiver
import com.krs.community.utils.LocaleHelper
import com.krs.community.viewmodel.RegisterViewModelFactory
import com.krs.community.volley.LruBitmapCache
import io.fabric.sdk.android.Fabric
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class AppController : Application(), KodeinAware {

    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mEditor: SharedPreferences.Editor
    internal var broadcastRevcevier: ConnectivityReceiver? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient

    var typeface: Typeface? = null
    var typeface_bold: Typeface? = null
    lateinit var retrofitBase: RetrofitBase
    lateinit var mRequestQueue: RequestQueue
    lateinit var mLruBitmapCache: LruBitmapCache
    lateinit var mImageLoader: ImageLoader

    override val kodein= Kodein.lazy {

        import(androidXModule(this@AppController))

        bind() from singleton { ApiServices() }
        bind() from singleton {  RegisterRepository(instance()) }
        bind() from provider { RegisterViewModelFactory(instance()) }


    }


    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()

        mApplication = this

        typeface = ResourcesCompat.getFont(applicationContext, R.font.montserrat_regular)
        typeface_bold = ResourcesCompat.getFont(applicationContext, R.font.montserrat_semibold)

        retrofitBase = RetrofitBase(this, false)

        Fresco.initialize(applicationContext)

        val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
        Fabric.with(fabric)

        MultiDex.install(this)


        Log.d(TAG, "AppController Screen")
        mEditor.putBoolean(getString(R.string.app_create), true)
        mEditor.apply()

        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        broadcastRevcevier = ConnectivityReceiver()
        registerReceiver(broadcastRevcevier, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    fun getRequestQueue(): RequestQueue {
        if(mRequestQueue==null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    fun getImageLoader(): ImageLoader {
        getRequestQueue()
        if (mImageLoader == null) {
            getLruBitmapCache()
            mImageLoader = ImageLoader(this.mRequestQueue, mLruBitmapCache)
        }

        return this.mImageLoader
    }

    fun getLruBitmapCache():LruBitmapCache{
        if (mLruBitmapCache == null){
            mLruBitmapCache = LruBitmapCache();
        }
        return mLruBitmapCache;
    }

    override fun onTerminate() {
        super.onTerminate()
        if (broadcastRevcevier != null) {
            unregisterReceiver(broadcastRevcevier)
            broadcastRevcevier = null
        }
    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
    }

    companion object {
        private val TAG = AppController::class.java.simpleName
        lateinit var mApplication: AppController
    }
}
