package com.krs.community.activity

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import com.github.squti.guru.Guru
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.MapTracking.LocationJobService
import com.krs.community.R
import com.krs.community.activity.MapTrackingActivity
import com.krs.community.app.AppController
import com.krs.community.listeners.IFamilyMembersListener
import com.krs.community.model.Member
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodelfactory.FamilyDetailViewModelFactory
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MapTrackingActivity : AppCompatActivity(), Listener, KodeinAware, IFamilyMembersListener {
    override val kodein by kodein()

    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
    var mLocationCallback: LocationCallback? = null
    var button: Button? = null
    var bService: Button? = null
    var mapFragment: SupportMapFragment? = null
    var mMap: GoogleMap? = null
    var mapLoaded = false
    var carMarker: Marker? = null
    var oldLocation: Location? = null
    var bearing = 0f
    var registered = false
    var isServiceStarted = false
    private var easyWayLocation: EasyWayLocation? = null
    private var request: LocationRequest? = null
    var Latitude = 0.0
    var Longitude = 0.0
    lateinit var mainHandler: Handler
    private var memberId:String?=null
    var headId:String?=null
    private lateinit var familyDetailViewModel: FamilyDetailViewModel
    private val familyDetailViewModelFactory: FamilyDetailViewModelFactory by instance()
    lateinit var members:ArrayList<Member>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_tracking)

        mainHandler = Handler(Looper.getMainLooper())

        memberId = Guru.getString(getString(R.string.member_id), "")
        headId = Guru.getString(getString(R.string.user_id), "")

        familyDetailViewModel = ViewModelProvider(this, familyDetailViewModelFactory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.mIFamilyMembersListener = this

        mainHandler.post(updateAdapter)

        request = LocationRequest()
        request!!.interval = Utility.INTERVAL
        request!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        easyWayLocation = EasyWayLocation(this, request, true, this)
        if (Utility.checkFineLocationPermission(this)) {
            easyWayLocation!!.startLocation()
        } else {
            Utility.requestFineLocationPermission(this)
        }
        val mApp = applicationContext as AppController
        mApp.FirebaseAnalytics(this@MapTrackingActivity, MapTrackingActivity::class.java.simpleName)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        button = findViewById<View>(R.id.b_action) as Button
        bService = findViewById<View>(R.id.b_service) as Button
        mapFragment = SupportMapFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.map_fragment, mapFragment!!).commitAllowingStateLoss()
        handler = Handler()
        createLocationRequest()

        mapFragment?.getMapAsync(OnMapReadyCallback { googleMap ->
            mMap = googleMap
            val latLng = LatLng(Latitude, Longitude)
            val customMarkerLocationOne = LatLng(23.0387, 72.6308)
            val customMarkerLocationTwo = LatLng(22.9664, 72.6159)
            val customMarkerLocationThree = LatLng(28.580903, 77.317408)
            val customMarkerLocationFour = LatLng(28.580108, 77.315271)
            mMap!!.addMarker(MarkerOptions().position(customMarkerLocationOne).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(this@MapTrackingActivity, R.drawable.usermap)))).setTitle("iPragmatech Solutions Pvt Lmt")
            mMap!!.addMarker(MarkerOptions().position(customMarkerLocationTwo).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(this@MapTrackingActivity, R.drawable.usermap)))).setTitle("Hotel Nirulas Noida")
            mMap!!.addMarker(MarkerOptions().position(customMarkerLocationThree).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(this@MapTrackingActivity, R.drawable.usermap)))).setTitle("Acha Khao Acha Khilao")
            mMap!!.addMarker(MarkerOptions().position(customMarkerLocationFour).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(this@MapTrackingActivity, R.drawable.usermap)))).setTitle("Subway Sector 16 Noida")
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
            mMap!!.setOnMapLoadedCallback {
                mapLoaded = true
                mMap!!.uiSettings.setAllGesturesEnabled(true)
                mMap!!.uiSettings.isZoomControlsEnabled = true
            }
        })
        isServiceStarted = getSharedPreferences("track", Context.MODE_PRIVATE).getBoolean("isServiceStarted", false)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isServiceStarted = LocationJobService.isJobRunning;
        }else{
            isServiceStarted = false;
        }*/changeServiceButton(isServiceStarted)
        if (!registered && isServiceStarted) {
            val i = IntentFilter(JOB_STATE_CHANGED)
            i.addAction(LOCATION_ACQUIRED)
            LocalBroadcastManager.getInstance(this).registerReceiver(jobStateChanged, i)
        }
    }

    private val updateAdapter = object : Runnable {
        override fun run() {
            getFamilyDetails()
            mainHandler.postDelayed(this, 1000*10*1)
        }
    }

    private fun getFamilyDetails(){

        val jsonObject= JSONObject()
        if(!memberId.isNullOrEmpty()){
            jsonObject.put(getString(R.string.id),memberId)
        }
        jsonObject.put(getString(R.string.head_id),headId)
        val records=  JsonParser().parse(jsonObject.toString()) as JsonObject
        familyDetailViewModel.getFamilyDetails(records)
    }
    override fun onResume() {
        super.onResume()
        if (Utility.checkFineLocationPermission(this)) {
            easyWayLocation!!.startLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            easyWayLocation!!.endUpdates()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun OnButtonClick(view: View) {
        when (view.id) {
            R.id.b_action -> if (view.tag == "s") {
                createLocationRequest()
            } else {
                Log.d("clicked", "button")
                stopLocationUpdates()
            }
            R.id.b_service -> if (view.tag == "s") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d("registered", " on start service")
                    startBackgroundService()
                } else {
                    Toast.makeText(baseContext, "service for pre lollipop will be available in next update", Toast.LENGTH_LONG).show()
                }
            } else {
                stopBackgroundService()
            }
        }
    }

    private val jobStateChanged: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == null) {
                return
            }
            if (intent.action == JOB_STATE_CHANGED) {
                changeServiceButton(intent.extras!!.getBoolean("isStarted"))
            } else if (intent.action == LOCATION_ACQUIRED) {
                if (intent.extras != null) {
                    val b = intent.extras
                    val l = b!!.getParcelable<Location>("location")
                    updateMarker(l)
                } else {
                    Log.d("intent", "null")
                }
            }
        }
    }

    private fun changeServiceButton(isStarted: Boolean) {
        if (isStarted) {
            bService!!.tag = "f"
            bService!!.text = "STOP BACKGROUND TRACKING"
            button!!.visibility = View.GONE
        } else {
            bService!!.tag = "s"
            bService!!.text = "START BACKGROUND TRACKING"
            button!!.visibility = View.VISIBLE
        }
    }

    private fun stopBackgroundService() {
        if (getSharedPreferences("track", Context.MODE_PRIVATE).getBoolean("isServiceStarted", false)) {
            Log.d("registered", " on stop service")
            var stopJobService: Intent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                stopJobService = Intent(LocationJobService.ACTION_STOP_JOB)
                LocalBroadcastManager.getInstance(baseContext).sendBroadcast(stopJobService)
                changeServiceButton(false)
            } else {
                Toast.makeText(applicationContext, "yet to be coded - stop service", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun startBackgroundService() {
        if (!registered) {
            val i = IntentFilter(JOB_STATE_CHANGED)
            i.addAction(LOCATION_ACQUIRED)
            LocalBroadcastManager.getInstance(this).registerReceiver(jobStateChanged, i)
        }
        val jobScheduler = (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler)
        jobScheduler.schedule(JobInfo.Builder(LocationJobService.LOCATION_SERVICE_JOB_ID,
                ComponentName(this, LocationJobService::class.java))
                .setOverrideDeadline(500)
                .setPersisted(true)
                .setRequiresDeviceIdle(false)
                .build())
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 30000
        mLocationRequest!!.fastestInterval = 15000
        mLocationRequest!!.smallestDisplacement = 50f
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            // All location settings are satisfied. The client can initialize
// location requests here.
// ...
            bService!!.visibility = View.GONE
            startLocationUpdates()
        }
        task.addOnFailureListener(this) { e ->
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED ->  // Location settings are not satisfied, but this can be fixed
// by showing the user a dialog.
                    try { // Show the dialog by calling startResolutionForResult(),
// and check the result in onActivityResult().
                        val resolvable = e as ResolvableApiException
                        resolvable.startResolutionForResult(this@MapTrackingActivity,
                                REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) { // Ignore the error.
                    }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    private fun startLocationUpdates() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) { // Update UI with location data
// ...
                    updateMarker(location)
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { // TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
            Toast.makeText(applicationContext, "location permission required !!", Toast.LENGTH_SHORT).show()
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */)
        button!!.tag = "f"
        button!!.text = "STOP FOREGROUND TRACKING"
        //        Toast.makeText(getApplicationContext(),"Location update started",Toast.LENGTH_SHORT).show();
    }

    private fun stopLocationUpdates() {
        if (button!!.tag == "s") {
            Log.d("TRACK", "stopLocationUpdates: updates never requested, no-op.")
            return
        }
        // It is a good practice to remove location requests when the activity is in a paused or
// stopped state. Doing so helps battery performance and is especially
// recommended in applications that request frequent location updates.
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
        button!!.tag = "s"
        button!!.text = "START FOREGROUND TRACKING"
        bService!!.visibility = View.VISIBLE
        //        Toast.makeText(getApplicationContext(),"Location update stopped.",Toast.LENGTH_SHORT).show();
    }

    override fun onDestroy() {
        try {
            if (registered) {
                unregisterReceiver(jobStateChanged)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i("Dash", "User agreed to make required location settings changes.")
                    createLocationRequest()
                }
                Activity.RESULT_CANCELED ->  //                    showTimeoutDialog("Without location access, GreenPool Enterprise can't be used !!", true);
                    Log.i("Dash", "User choose not to make required location settings changes.")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun locationOn() {}
    override fun currentLocation(location: Location) {
        Latitude = location.latitude
        Longitude = location.longitude
        Log.e("Latitude--", "" + Latitude)
        Log.e("Longitude--", "" + Longitude)
    }

    override fun locationCancelled() {}
    inner class MoveThread : Runnable {
        var newPoint: LatLng? = null
        var zoom = 16f
        fun setNewPoint(latLng: LatLng?, zoom: Float) {
            newPoint = latLng
            this.zoom = zoom
        }

        override fun run() {
            val point = CameraUpdateFactory.newLatLngZoom(newPoint, zoom)
            runOnUiThread { mMap!!.animateCamera(point) }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopBackgroundService()
    }

    private fun updateMarker(location: Location?) {
        if (location == null) {
            return
        }
        if (mMap != null && mapLoaded) {
            if (carMarker == null) {
                oldLocation = location
                val markerOptions = MarkerOptions()
                val car = BitmapDescriptorFactory.fromResource(R.drawable.pintracking)
                markerOptions.icon(car)
                markerOptions.anchor(0.5f, 0.5f) // set the car image to center of the point instead of anchoring to above or below the location
                markerOptions.flat(true) // set as true, so that when user rotates the map car icon will remain in the same direction
                markerOptions.position(LatLng(location.latitude, location.longitude))
                carMarker = mMap!!.addMarker(markerOptions)
                bearing = if (location.hasBearing()) { // if location has bearing set the same bearing to marker(if location is acquired using GPS bearing will be available)
                    location.bearing
                } else {
                    0f // no need to calculate bearing as it will be the first point
                }
                carMarker?.setRotation(bearing)
                moveThread = MoveThread()
                moveThread!!.setNewPoint(LatLng(location.latitude, location.longitude), 16f)
                handler!!.post(moveThread)
            } else {
                bearing = if (location.hasBearing()) { // if location has bearing set the same bearing to marker(if location is acquired using GPS bearing will be available)
                    location.bearing
                } else { // if not, calculate bearing between old location and new location point
                    oldLocation!!.bearingTo(location)
                }
                carMarker!!.rotation = bearing
                moveThread!!.setNewPoint(LatLng(location.latitude, location.longitude), mMap!!.cameraPosition.zoom) // set the map zoom to current map's zoom level as user may zoom the map while tracking.
                animateMarkerToICS(carMarker, LatLng(location.latitude, location.longitude)) // animate the marker smoothly
            }
        } else {
            Log.e("map null or not loaded", "")
        }
    }

    companion object {
        protected const val REQUEST_CHECK_SETTINGS = 0x1
        var moveThread: MoveThread? = null
        var handler: Handler? = null
        const val JOB_STATE_CHANGED = "jobStateChanged"
        const val LOCATION_ACQUIRED = "locAcquired"
        fun createCustomMarker(context: Context, @DrawableRes resource: Int): Bitmap {
            val marker = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_marker_layout, null)
            val markerImage = marker.findViewById<View>(R.id.user_dp) as CircleImageView
            markerImage.setImageResource(resource)
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
            marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            marker.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(marker.measuredWidth, marker.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            marker.draw(canvas)
            return bitmap
        }

        fun createCustomMarkerCurrent(context: Context, resource: String?): Bitmap {
            val marker = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_marker_layout, null)
            val markerImage = marker.findViewById<View>(R.id.user_dp) as CircleImageView
            Glide.with(context).load("https://muslimghanchisamaj.in/uploads/users/thumb/c2c03908f3eaa4141d678f630bf7f20d.jpg").into(markerImage)
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
            marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            marker.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(marker.measuredWidth, marker.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            marker.draw(canvas)
            return bitmap
        }

        fun animateMarkerToICS(marker: Marker?, finalPosition: LatLng?) {
            val typeEvaluator = TypeEvaluator<LatLng> { fraction, startValue, endValue -> interpolate(fraction, startValue, endValue) }
            val property = Property.of(Marker::class.java, LatLng::class.java, "position")
            val animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
            animator.duration = 3000
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    handler!!.post(moveThread)
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            animator.start()
        }

        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng { // function to calculate the in between values of old latlng and new latlng.
// To get more accurate tracking(Car will always be in the road even when the latlng falls away from road), use roads api from Google apis.
// As it has quota limits I didn't have used that method.
            val lat = (b.latitude - a.latitude) * fraction + a.latitude
            var lngDelta = b.longitude - a.longitude
            // Take the shortest path across the 180th meridian.
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360
            }
            val lng = lngDelta * fraction + a.longitude
            return LatLng(lat, lng)
        }
    }

    override fun getMessage(response: DeleteProfileResponse) {

    }

    override suspend fun getFailure(message: String) {

    }

    override fun getFamilyMembers(data: FamilyDetailResponse) {
        if (data.success) {
            members = data.member as ArrayList<Member>

            Log.e("fname---",""+members[0].firstName)
        }
    }
}