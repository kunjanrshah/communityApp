package com.krs.community.app


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.graphics.Typeface
import android.location.Location
import android.os.StrictMode
import android.text.TextUtils
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDex
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookSdk
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.squti.guru.GuruConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.LocationRequest
import com.krs.community.R
import com.krs.community.repositories.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.retrofit.RetrofitBase
import com.krs.community.utils.AppConstants
import com.krs.community.utils.ConnectivityReceiver
import com.krs.community.utils.LocaleHelper
import com.krs.community.viewmodel.*
import com.krs.community.volley.LruBitmapCache
import io.fabric.sdk.android.Fabric
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class AppController : Application(), KodeinAware{

    internal var broadcastRevcevier: ConnectivityReceiver? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient

    lateinit var typeface: Typeface
    lateinit var typeface_bold: Typeface
    lateinit var retrofitBase: RetrofitBase
    lateinit var mRequestQueue: RequestQueue
    lateinit var mLruBitmapCache: LruBitmapCache
    lateinit var mImageLoader: ImageLoader

    companion object {
        val TAG = AppController::class.java.simpleName
        lateinit var mApplication: AppController
    }

    override val kodein= Kodein.lazy {

        import(androidXModule(this@AppController))

        bind() from singleton { ApiServices() }
        bind() from singleton { AppDatabase(instance()) }

        bind() from singleton {  RegisterRepository(instance(),instance()) }
        bind() from singleton {  LoginRepository(instance()) }
        bind() from singleton {  BrowseCityRepository(instance(),instance()) }
        bind() from singleton {  ByDistanceRepository(instance(),instance()) }
        bind() from singleton {  FamilyDetailRepository(instance()) }
        bind() from singleton {  ProfileDetailRepository(instance(),instance()) }
        bind() from singleton {  DashboardRepository(instance(),instance()) }
        bind() from singleton {  StatisticsRepository(instance(),instance()) }
        bind() from singleton {  SmartSearchRepository(instance(),instance()) }
        bind() from singleton {  MatrimonySearchRepository(instance(),instance()) }

        bind() from provider { StatisticsViewModelFactory(instance()) }
        bind() from provider { FamilyDetailViewModelFactory(instance()) }
        bind() from provider { RegisterViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance()) }
        bind() from provider { BrowseCityViewModelFactory(instance()) }
        bind() from provider { ByDistanceViewModelFactory(instance()) }
        bind() from provider { ProfileDetailViewModelFactory(instance()) }
        bind() from provider { DashboardViewModelFactory(instance()) }
        bind() from provider { SmartSearchViewModelFactory(instance()) }
        bind() from provider { MatrimonySearchViewModelFactory(instance()) }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()

        mApplication = this
        FacebookSdk.sdkInitialize(applicationContext);

        typeface = ResourcesCompat.getFont(applicationContext, R.font.montserrat_regular)!!
        typeface_bold = ResourcesCompat.getFont(applicationContext, R.font.montserrat_semibold)!!

        retrofitBase = RetrofitBase(this, false)

        Fresco.initialize(applicationContext)

        val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
        Fabric.with(fabric)

        MultiDex.install(this)

        GuruConfig.initDefault(GuruConfig.Builder()
                .setFileName(AppConstants.PREF_NAME)
                .setMode(Context.MODE_PRIVATE)
                .build())


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
        return mRequestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>,tag:String){
        // set the default tag if tag is empty
        req.tag = TextUtils.isEmpty(tag)
        getRequestQueue().add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>){
        // set the default tag if tag is empty
        req.tag = TAG
        getRequestQueue().add(req)
    }

    fun getImageLoader(): ImageLoader {
        getRequestQueue()
        return this.mImageLoader
    }

    fun getLruBitmapCache():LruBitmapCache{
        return mLruBitmapCache
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


}
