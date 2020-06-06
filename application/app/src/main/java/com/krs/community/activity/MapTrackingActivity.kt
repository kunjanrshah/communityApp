package com.krs.community.activity

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.squti.guru.Guru
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.listeners.IFamilyMembersListener
import com.krs.community.model.Member
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodelfactory.FamilyDetailViewModelFactory
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MapTrackingActivity : AppCompatActivity(), KodeinAware, IFamilyMembersListener {
    override val kodein by kodein()

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var mapLoaded = false
    private var bearing = 0f
    private lateinit var mainHandler: Handler
    private lateinit var familyDetailViewModel: FamilyDetailViewModel
    private val familyDetailViewModelFactory: FamilyDetailViewModelFactory by instance<FamilyDetailViewModelFactory>()
    private lateinit var headId: String
    private val locations = HashMap<String, Location>()
    private val lstTitle = HashMap<String, String>()
    private val lstImage = HashMap<String, String>()
    private val oldLocations = HashMap<String, Location>()
    private val lstMarkers = HashMap<String, Marker>()
    private var mCustomMarkerView: View? = null
    private var mMarkerImageView: ImageView? = null
    private var tvTitle: TextView? = null
    private val ImageUrl = "https://s3.amazonaws.com/uifaces/faces/twitter/jsa/128.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_tracking)
        headId = intent.getStringExtra("head_id")
        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this@MapTrackingActivity, MapTrackingActivity::class.java.simpleName)
        familyDetailViewModel = ViewModelProvider(this, familyDetailViewModelFactory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.mIFamilyMembersListener = this

        mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(updateLocations)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = SupportMapFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.map_fragment, mapFragment!!).commitAllowingStateLoss()
        handler = Handler()
        createLocationRequest()

        mapFragment?.getMapAsync { googleMap ->
            mMap = googleMap
            if (locations.size > 0) {
                val point = CameraUpdateFactory.newLatLngZoom(LatLng(locations["u_$headId"]!!.latitude, locations["u_$headId"]!!.longitude), 9f)
                mMap?.moveCamera(point)
            }
            mMap?.animateCamera(CameraUpdateFactory.zoomTo(9f))
            mMap?.setOnMapLoadedCallback {
                mapLoaded = true
                mCustomMarkerView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.view_custom_marker, null)
                mMarkerImageView = mCustomMarkerView?.findViewById<View>(R.id.profile_image) as CircleImageView
                tvTitle = mCustomMarkerView?.findViewById<View>(R.id.tv_title) as TextView
                getFamilyDetails()
                mMap!!.uiSettings.setAllGesturesEnabled(true)
                mMap!!.uiSettings.isZoomControlsEnabled = true
            }
        }
    }

    private val updateLocations = object : Runnable {
        override fun run() {
            if (mapLoaded && mMap != null) {
                getFamilyDetails()
            }
            mainHandler.postDelayed(this, 1000 * 60 * 5)
        }
    }

    private fun getFamilyDetails() {

        val jsonObject = JSONObject()
        val headId = Guru.getString(getString(R.string.user_id), "")
        if (this.headId.isNotEmpty()) {
            jsonObject.put(getString(R.string.id), this.headId)
        }
        jsonObject.put(getString(R.string.head_id), headId)
        val records = JsonParser().parse(jsonObject.toString()) as JsonObject
        familyDetailViewModel.getFamilyDetails(records)
    }

    override fun getFamilyMembers(data: FamilyDetailResponse) {
        if (data.success) {

            val members = data.member as ArrayList<Member>
            for (member in members) {
                if (member.id == headId) {
                    if (!member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()) {
                        val loc = Location(LocationManager.GPS_PROVIDER)
                        loc.latitude = member.homeLat.toDouble()
                        loc.longitude = member.homeLng.toDouble()
                        locations["h_${member.id}"] = loc
                        lstTitle["h_${member.id}"] = "Home"
                        lstImage["h_${member.id}"] = getString(R.string.base_url_original) + "home_marker.png"
                    }
                }

                if (!member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()) {
                    val loc = Location(LocationManager.GPS_PROVIDER)
                    loc.latitude = member.officeLat.toDouble()
                    loc.longitude = member.officeLng.toDouble()
                    locations["o_${member.id}"] = loc
                    lstImage["o_${member.id}"] = getString(R.string.base_url_original) + "office_marker.png"
                    lstTitle["o_${member.id}"] = member.firstName + " Office"
                }

                if (!member.userLat.isNullOrEmpty() && !member.userLng.isNullOrEmpty()) {
                    val loc = Location(LocationManager.GPS_PROVIDER)
                    loc.latitude = member.userLat.toDouble()
                    loc.longitude = member.userLng.toDouble()
                    if (headId != member.id) {
                        locations["u_${member.id}"] = loc
                        lstImage["u_${member.id}"] = getString(R.string.base_url_original) + member.profilePic
                        lstTitle["u_${member.id}"] = member.firstName.toString()
                    }
                }
            }
            if (locations.size > 0) {
                updateMarker()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        createLocationRequest()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 30000
        mLocationRequest!!.fastestInterval = 15000
        mLocationRequest!!.smallestDisplacement = 50f
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener(this) {
            startLocationUpdates()
        }

        task.addOnFailureListener(this) { e ->
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED ->
                    try {
                        val resolvable = e as ResolvableApiException
                        resolvable.startResolutionForResult(this@MapTrackingActivity, REQUEST_CHECK_SETTINGS)
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
                for (location in locationResult.locations) {
                    locations["u_$headId"]?.set(location)
                    /* if(lstTitle["u_$headId"]!="You"){
                         lstTitle["u_$headId"] = "You"
                         lstImage["u_$headId"] = getString(R.string.base_url_original) + "current_pin.png"
                     }*/
                    lstMarkers["u_$headId"]?.remove()
                    updateMarker()
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "location permission required !!", Toast.LENGTH_SHORT).show()
            return
        }
        mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        Toast.makeText(applicationContext, "Location update started", Toast.LENGTH_SHORT).show()
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
        Toast.makeText(applicationContext, "Location update stopped.", Toast.LENGTH_SHORT).show()
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

    inner class MoveThread : Runnable {
        var newPoint: LatLng? = null
        var zoom = 9f
        fun setNewPoint(latLng: LatLng?, zoom: Float) {
            newPoint = latLng
            this.zoom = zoom
        }

        override fun run() {
            val point = CameraUpdateFactory.newLatLngZoom(newPoint, zoom)
            runOnUiThread { mMap!!.animateCamera(point) }
        }
    }

    private fun updateMarker() {
        if (locations.isEmpty()) {
            return
        }
        if (mMap != null && mapLoaded) {
            for (loc in locations) {
                if (lstMarkers[loc.key] == null) {
                    oldLocations[loc.key] = loc.value
                    Glide.with(applicationContext)
                            .asBitmap()
                            .thumbnail(0.5f)
                            .load(lstImage[loc.key])
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user_profile)
                                    addMarker(loc, bitmap)
                                }

                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    addMarker(loc, resource)
                                }
                            })


                } else {
                    bearing = if (loc.value.hasBearing()) {
                        loc.value.bearing
                    } else {
                        oldLocations[loc.key]!!.bearingTo(loc.value)
                    }
                    lstMarkers[loc.key]?.rotation = bearing
                    moveThread = MoveThread()
                    moveThread?.setNewPoint(LatLng(loc.value.latitude, loc.value.longitude), mMap!!.cameraPosition.zoom)
                    handler?.post(moveThread)
                    animateMarkerToICS(lstMarkers[loc.key], LatLng(loc.value.latitude, loc.value.longitude))
                }
            }
        } else {
            Log.e("map null or not loaded", "")
        }
    }

    private fun addMarker(loc: MutableMap.MutableEntry<String, Location>, bitmap: Bitmap) {
        val markerOptions = MarkerOptions()
        tvTitle?.text = lstTitle[loc.key]
        val car = BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))
        markerOptions.icon(car)
        markerOptions.anchor(0.5f, 0.5f)
        markerOptions.flat(true)
        markerOptions.position(LatLng(loc.value.latitude, loc.value.longitude))
        lstMarkers[loc.key] = mMap!!.addMarker(markerOptions)
        bearing = if (loc.value.hasBearing()) { // if location has bearing set the same bearing to marker(if location is acquired using GPS bearing will be available)
            loc.value.bearing
        } else {
            0f // no need to calculate bearing as it will be the first point
        }
        lstMarkers[loc.key]?.rotation = bearing
        moveThread = MoveThread()
        moveThread?.setNewPoint(LatLng(loc.value.latitude, loc.value.longitude), 9f)
        handler?.post(moveThread)
        animateMarkerToICS(lstMarkers[loc.key], LatLng(loc.value.latitude, loc.value.longitude))
    }

    private fun getMarkerBitmapFromView(view: View?, bitmap: Bitmap): Bitmap? {
        mMarkerImageView?.setImageBitmap(bitmap)
        view?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view?.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view?.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(view?.measuredWidth!!, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        view.background?.draw(canvas)
        view.draw(canvas)
        return returnedBitmap
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 0x1
        var moveThread: MoveThread? = null
        var handler: Handler? = null
        const val JOB_STATE_CHANGED = "jobStateChanged"
        const val LOCATION_ACQUIRED = "locAcquired"

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

        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
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

}