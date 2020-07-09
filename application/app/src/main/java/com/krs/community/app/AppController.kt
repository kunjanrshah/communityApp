package com.krs.community.app


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.squti.guru.Guru
import com.github.squti.guru.GuruConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.repositories.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.retrofit.RetrofitBase
import com.krs.community.utils.AppConstants
import com.krs.community.utils.Coroutines
import com.krs.community.utils.LocaleHelper
import com.krs.community.viewmodelfactory.*
import io.fabric.sdk.android.Fabric
import net.gotev.uploadservice.UploadServiceConfig
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class AppController : Application(), KodeinAware {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var typeface: Typeface
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var logger: AppEventsLogger
    lateinit var typeface_bold: Typeface
    lateinit var retrofitBase: RetrofitBase
    lateinit var connectionLiveData: ConnectionLiveData
    var start: Int = 0
    val length: Int = 50
    val mHandler: Handler = Handler()


    companion object {
        val TAG = AppController::class.java.simpleName
        lateinit var mApplication: AppController
        const val notificationChannelID = "TestChannel"
        val INTERVAL = 1000 * 60 * 3 //3 minutes
    }

    override val kodein = Kodein.lazy {

        import(androidXModule(this@AppController))

        bind() from singleton { ApiServices() }
        bind() from singleton { AppDatabase(instance()) }

        bind() from singleton { RegisterRepository(instance()) }
        bind() from singleton { LoginRepository(instance()) }
        bind() from singleton { PasswordRepository(instance()) }
        bind() from singleton { ShareEventRepository(instance()) }
        bind() from singleton { BrowseCityRepository(instance(), instance()) }
        bind() from singleton { ByDistanceRepository(instance(), instance()) }
        bind() from singleton { FamilyDetailRepository(instance()) }
        bind() from singleton { ProfileDetailRepository(instance(), instance()) }
        bind() from singleton { DashboardRepository(instance(), instance()) }
        bind() from singleton { StatisticsRepository(instance(), instance()) }
        bind() from singleton { ContactListRepository(instance(), instance()) }
        bind() from singleton { SmartSearchRepository(instance(), instance()) }
        bind() from singleton { SmartFilterRepository(instance(), instance()) }
        bind() from singleton { DocumentListRepository(instance()) }
        bind() from singleton { CalendarSearchRepository(instance(), instance()) }
        bind() from singleton { NewsRepository(instance()) }
        bind() from singleton { RoomMemberRepository(instance(), instance()) }
        bind() from singleton { CommitteeRepository(instance(), instance()) }

        bind() from provider { ContactListViewModelFactory(instance()) }
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
        bind() from provider { DocumentListViewModelFactory(instance()) }
        bind() from provider { SmartFilterViewModelFactory(instance()) }
        bind() from provider { CalendarSearchViewModelFactory(instance()) }
        bind() from provider { NewsModelFactory(instance()) }
        bind() from provider { RoomMemberViewModelFactory(instance()) }
        bind() from provider { CommiteeViewModelFactory(instance()) }

    }

    fun firebaseAnalytics(getContext: Context?, Name: String?) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext!!)
        firebaseAnalytics.setCurrentScreen((getContext as Activity?)!!, "Screen", Name)
    }

    fun facebookAnalytics(getContext: Context?, Name: String?) {
        logger = AppEventsLogger.newLogger(getContext)
        logger.logEvent(Name)
    }

    /*  fun stringTranslateAPI(Name: String?):String {
          translateAPI = TranslateAPI(Language.AUTO_DETECT, Language.TAMIL, Name);

          translateAPI.setTranslateListener(object :TranslateAPI.TranslateListener{
              override fun onSuccess(translatedText: String?) : String? {
                  Log.d(TAG, "onSuccess: " + translatedText);
                  return translatedText
              }

              override fun onFailure(ErrorText: String?): String? {
                  Log.d(TAG, "onSuccess: " + ErrorText);
                  return ErrorText
              }

          })
          return ""
      }*/

    private val mHandlerTask = object : Runnable {
        override fun run() {
            if (Guru.getBoolean(getString(R.string.policy), false)) {
                if (isNetworkConnected(this@AppController)) {
                    updateUserStatus()
                }
                mHandler.postDelayed(this, INTERVAL.toLong())
            }
        }
    }

    private fun startRepeatingTask() {
        mHandlerTask.run()
    }

    private fun stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(notificationChannelID, "TestApp Channel", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()

        mApplication = this
        connectionLiveData = ConnectionLiveData(this)
        FacebookSdk.sdkInitialize(applicationContext)
        createNotificationChannel()

        UploadServiceConfig.initialize(
                context = this,
                defaultNotificationChannel = notificationChannelID,
                debug = BuildConfig.DEBUG
        )

        typeface = ResourcesCompat.getFont(applicationContext, R.font.montserrat_regular)!!
        typeface_bold = ResourcesCompat.getFont(applicationContext, R.font.montserrat_semibold)!!

        retrofitBase = RetrofitBase(this, false)
        Fresco.initialize(applicationContext)

        Fabric.with(this, Crashlytics())

        /* val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
         Fabric.with(fabric)*/

        MultiDex.install(this)

        GuruConfig.initDefault(GuruConfig.Builder()
                .setFileName(AppConstants.PREF_NAME)
                .setMode(Context.MODE_PRIVATE)
                .build())


        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build()
        PRDownloader.initialize(applicationContext, config)

        startRepeatingTask()
        Log.v("AppControler", "onCreate")
    }


    override fun onTerminate() {
        super.onTerminate()
        Log.v("AppControler", "onTerminate")
        stopRepeatingTask()
    }


    private fun updateUserStatus() {
        Coroutines.io {
            val memberId = Guru.getString(getString(R.string.member_id), "")
            if (!memberId.isNullOrEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put(getString(R.string.id), memberId)
                jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                retrofitBase.apiServices.getUserStatus(updated)
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
    }

}