package com.krs.community.app


import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.StrictMode
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookSdk
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.squti.guru.Guru
import com.github.squti.guru.GuruConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.repositories.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.retrofit.RetrofitBase
import com.krs.community.utils.AppConstants
import com.krs.community.utils.ConnectivityReceiver
import com.krs.community.utils.Coroutines
import com.krs.community.utils.LocaleHelper
import com.krs.community.viewmodelfactory.*
import io.fabric.sdk.android.Fabric
import org.json.JSONObject
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
    private var mNetworkReceiver: BroadcastReceiver? = null
    var start: Int = 0
    var internet: Int = 0
    val length: Int = 30
    val mHandler:Handler = Handler()

    companion object {
        val TAG = AppController::class.java.simpleName
        lateinit var mApplication: AppController
        val INTERVAL = 1000 * 60 * 3 //3 minutes
    }

    override val kodein= Kodein.lazy {

        import(androidXModule(this@AppController))

        bind() from singleton { ApiServices() }
        bind() from singleton { AppDatabase(instance()) }

        bind() from singleton {  RegisterRepository(instance(),instance()) }
        bind() from singleton {  LoginRepository(instance()) }
        bind() from singleton {  PasswordRepository(instance()) }
        bind() from singleton {  ShareEventRepository(instance()) }
        bind() from singleton {  BrowseCityRepository(instance(),instance()) }
        bind() from singleton {  ByDistanceRepository(instance(),instance()) }
        bind() from singleton {  FamilyDetailRepository(instance()) }
        bind() from singleton {  ProfileDetailRepository(instance(),instance()) }
        bind() from singleton {  DashboardRepository(instance(),instance()) }
        bind() from singleton {  StatisticsRepository(instance(),instance()) }
        bind() from singleton {  SmartSearchRepository(instance(),instance()) }
        bind() from singleton {  SmartFilterRepository(instance(),instance()) }
        bind() from provider  {  CalendarSearchRepository(instance(),instance()) }
        bind() from provider  {  NewsRepository(instance()) }
        bind() from provider  {  RoomMemberRepository(instance(),instance()) }

        bind() from provider { StatisticsViewModelFactory(instance()) }
        bind() from provider { FamilyDetailViewModelFactory(instance()) }
        bind() from provider { RegisterViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance()) }
        bind() from provider { PasswordViewModelFactory(instance()) }
        bind() from provider { ShareEventViewModelFactory(instance()) }
        bind() from provider { BrowseCityViewModelFactory(instance()) }
        bind() from provider { ByDistanceViewModelFactory(instance()) }
        bind() from provider { ProfileDetailViewModelFactory(instance()) }
        bind() from provider { DashboardViewModelFactory(instance()) }
        bind() from provider { SmartSearchViewModelFactory(instance()) }
        bind() from provider { SmartFilterViewModelFactory(instance()) }
        bind() from provider { CalendarSearchViewModelFactory(instance()) }
        bind() from provider { NewsModelFactory(instance()) }
        bind() from provider { RoomMemberViewModelFactory(instance()) }

    }

    private val mHandlerTask= object:Runnable {
        override fun run() {
            updateUserStatus()
            mHandler.postDelayed(this, INTERVAL.toLong())
        }
    }

    private fun startRepeatingTask(){
        mHandlerTask.run()
    }

    private fun stopRepeatingTask(){
        mHandler.removeCallbacks(mHandlerTask);
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }

        startRepeatingTask()

    }

    override fun onTerminate() {
        super.onTerminate()
        stopRepeatingTask()
        if (broadcastRevcevier != null) {
            unregisterReceiver(broadcastRevcevier)
            broadcastRevcevier = null
        }
    }

    private fun updateUserStatus(){
        Coroutines.io{
            val memberId = Guru.getString(getString(R.string.member_id), "")
            if(!memberId.isNullOrEmpty()){
                val jsonObject=JSONObject()
                jsonObject.put(getString(R.string.id),memberId)
                jsonObject.put(getString(R.string.user_id),Guru.getString(getString(R.string.user_id), ""))
                jsonObject.put(getString(R.string.access_token),Guru.getString(getString(R.string.access_token), ""))
                val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
                retrofitBase.apiServices.getUserStatus(updated)
            }
        }
    }


    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
    }

}