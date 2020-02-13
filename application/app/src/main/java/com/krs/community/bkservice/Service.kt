package com.krs.community.bkservice

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.example.easywaylocation.LocationData.AddressCallBack
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppDatabase
import com.krs.community.bkservice.ProcessMainClass.serviceIntent
import com.krs.community.bkservice.utilities.Notification
import com.krs.community.repositories.ProfileDetailRepository
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import com.krs.community.utils.Utility
import kotlinx.coroutines.*
import org.json.JSONObject

class Service : android.app.Service(), Listener, AddressCallBack {
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var getLocationDetail: GetLocationDetail
    private lateinit var completableJob: CompletableJob


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground()
        }
        mCurrentService = this
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "restarting Service !!")
        getLocationDetail = GetLocationDetail(this, this)
        val request = LocationRequest()
        request.interval = Utility.INTERVAL
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        try{
            easyWayLocation = EasyWayLocation(this, request, true, this)
            easyWayLocation.startLocation()
        }catch (e:Exception){
            stopSelf()
        }


        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            val bck = ProcessMainClass()
            bck.launchService(this)
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground()
        }
        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    private fun restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground")
            try {
                val notification = Notification()
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Service notification", "This is the service's notification", R.drawable.ic_app))
                Log.i(TAG, "restarting foreground successful")
                easyWayLocation.startLocation()


                serviceIntent = Intent(this, PhonecallReceiver::class.java)

            } catch (e: Exception) {
                Log.e(TAG, "Error in notification " + e.message)
            }
        }
    }

    override fun onDestroy() {

        // restart the never ending service
        // Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        // sendBroadcast(broadcastIntent);
        easyWayLocation.endUpdates()
        completableJob.cancel()
        super.onDestroy()
        Log.i(TAG, "onDestroy called")
    }

    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */
    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Log.i(TAG, "onTaskRemoved called")
        // restart the never ending service
        val broadcastIntent = Intent(Globals.RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    override fun locationOn() {}

    override fun currentLocation(location: Location) {
        Log.e("Location Service: ", "latitude: " + location.latitude + " longitude: " + location.longitude)
        getLocationDetail.getAddress(location.latitude, location.longitude, getString(R.string.map_api_key))

        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {

                    val jsonObject = JSONObject()
                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id),""))
                    jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.user_id),""))
                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token),""))
                    jsonObject.put(getString(R.string.user_lat),location.latitude)
                    jsonObject.put(getString(R.string.user_lng), location.longitude)
                    jsonObject.put(getString(R.string.is_location_enable), "1")

                    val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                    val mProfileDetailRepository= ProfileDetailRepository(ApiServices(),AppDatabase.invoke(AppController.mApplication))
                    val response: UpdateProfileResponse = mProfileDetailRepository.updateProfile(profile)
                    response.let {
                        withContext(Dispatchers.Main) {
                            Guru.putString(getString(R.string.loginMember), Gson().toJson(response.member))
                            Log.d("Location Service: ",response.message)
                            thejob.complete()
                        }
                        return@launch
                    }
                    //mEditMemberListener?.getFailure(response.message)
                } catch (e: ApiException) {
                    e.message?.let {
                        Log.d("Location Service: ",it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        Log.d("Location Service: ",it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        Log.d("Location Service: ",it)
                    }
                }
                thejob.complete()
            }
        }
    }

    override fun locationCancelled() {}
    override fun locationData(locationData: LocationData) {
        Log.e("Location Service: ", "locationData: " + locationData.full_address)
    }

    companion object {
        protected const val NOTIFICATION_ID = 1337
        private const val TAG = "Service"
        private var mCurrentService: Service? = null
        fun getmCurrentService(): Service? {
            return mCurrentService
        }

        fun setmCurrentService(mCurrentService: Service?) {
            Companion.mCurrentService = mCurrentService
        }
    }
}