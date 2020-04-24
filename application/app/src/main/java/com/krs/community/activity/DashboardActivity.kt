package com.krs.community.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.crashlytics.android.Crashlytics
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.databinding.ActivityDashboardBinding
import com.krs.community.entities.MasterCounts
import com.krs.community.fragments.*
import com.krs.community.fragments.FragmentDrawer.FragmentDrawerListener
import com.krs.community.listeners.UpdateListener
import com.krs.community.model.Member
import com.krs.community.responses.MasterUpdateResponse
import com.krs.community.responses.UserStatusResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.*
import com.krs.community.viewmodel.DashboardViewModel
import com.krs.community.viewmodelfactory.DashboardViewModelFactory
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import com.luseen.spacenavigation.SpaceOnLongClickListener
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DashboardActivity : AppCompatActivity(), FragmentDrawerListener, KodeinAware, Listener, UpdateListener, LocationData.AddressCallBack {

    private val TAG = DashboardActivity::class.java.simpleName
    private lateinit var dashboardViewModel: DashboardViewModel
    private val factory: DashboardViewModelFactory by instance<DashboardViewModelFactory>()
    private var menu: Menu? = null


    companion object {
        var stop: Boolean = false
        lateinit var binding: ActivityDashboardBinding
        var getLocationDetail: GetLocationDetail? = null
        var request: LocationRequest? = null
        var easyWayLocation: EasyWayLocation? = null
        var cur_lat = MutableLiveData<Double>()
        var cur_lng = MutableLiveData<Double>()
        var curAddr = MutableLiveData<String>()
        var matrimonyCounts = MutableLiveData<String>()
        var statusCounts = MutableLiveData<String>()
    }

    private val PERMISSION_REQUEST_READ_PHONE_STATE = 1

    override val kodein by kodein()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@DashboardActivity, R.layout.activity_dashboard)
        dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
        dashboardViewModel.listener = this

        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this@DashboardActivity, DashboardActivity.javaClass.simpleName)
        mApp.facebookAnalytics(this@DashboardActivity, DashboardActivity.javaClass.simpleName)

        if (Guru.getString(getString(R.string.user_id), "")!!.isEmpty()) {
            val mIntent = Intent(this@DashboardActivity, SplashActivity::class.java)
            startActivity(mIntent)
            finish()
            //    fade(this)
        }

        setSupportActionBar(binding.toolbar as Toolbar?)
        (binding.toolbar as Toolbar?)?.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.home)

        binding.myAppBar.translationY = -toolbarHeight.toFloat()
        binding.myAppBar.animate().translationY(0f).alpha(1f).setDuration(2000).start()

        val drawerFragment = supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as FragmentDrawer?
        drawerFragment!!.setUp(R.id.fragment_navigation_drawer, binding.drawerLayout, (binding.toolbar as Toolbar))
        drawerFragment.mDrawerToggle!!.isDrawerIndicatorEnabled = false
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.menu_slide1, theme)
        drawerFragment.mDrawerToggle!!.setHomeAsUpIndicator(drawable)
        drawerFragment.setDrawerListener(this)
        if (drawerFragment.mDrawerToggle != null) {
            drawerFragment.mDrawerToggle!!.toolbarNavigationClickListener = View.OnClickListener { v: View? ->
                if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }

        binding.space.initWithSaveInstanceState(savedInstanceState)
        binding.space.addSpaceItem(SpaceItem(getString(R.string.home), R.drawable.home))
        binding.space.addSpaceItem(SpaceItem(getString(R.string.Calendar), R.drawable.calendar4))
        binding.space.shouldShowFullBadgeText(false)
        binding.space.setCentreButtonIconColorFilterEnabled(false)
        binding.space.setCentreButtonIcon(R.drawable.filter_icon)
        binding.space.animate().translationY(0f).alpha(1f).setDuration(2000).start()

        binding.space.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                movetoFragment(this@DashboardActivity, ExpandableFilterListFragment())
            }

            override fun onItemClick(itemIndex: Int, itemName: String) {
                if (itemIndex == 1) {
                    val fragment = supportFragmentManager.findFragmentByTag(CalendarFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        movetoFragment(this@DashboardActivity, CalendarFragment())
                    }
                } else if (itemIndex == 0) {
                    val fragment = supportFragmentManager.findFragmentByTag(DashboardFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        movetoFragment(this@DashboardActivity, DashboardFragment())
                    }
                }
            }

            override fun onItemReselected(itemIndex: Int, itemName: String) {
                if (itemIndex == 1) {
                    val fragment = supportFragmentManager.findFragmentByTag(CalendarFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        movetoFragment(this@DashboardActivity, CalendarFragment())
                    }
                } else if (itemIndex == 0) {
                    val fragment = supportFragmentManager.findFragmentByTag(DashboardFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        movetoFragment(this@DashboardActivity, DashboardFragment())
                    }
                }
            }
        })

        binding.space.setSpaceOnLongClickListener(object : SpaceOnLongClickListener {
            override fun onCentreButtonLongClick() {
                // Toast.makeText(this@DashboardActivity, getString(R.string.onCentreButtonLongClick), Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClick(itemIndex: Int, itemName: String) {
                //Toast.makeText(this@DashboardActivity, "$itemIndex $itemName", Toast.LENGTH_SHORT).show()
            }
        })

        movetoFragment(this@DashboardActivity, DashboardFragment())
        if (isNetworkConnected(this)) {
            val JsonObj = JSONObject()
            JsonObj.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
            JsonObj.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
            JsonObj.put("insert", "")
            JsonObj.put("version", getAppVersion(this))
            val updated = JsonParser().parse(JsonObj.toString()) as JsonObject
            dashboardViewModel.getUpdatedVersion(updated)
        }

        if (isNetworkConnected(this)) {
            dashboardViewModel.getMasterUpdate()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
        NotificationUtils.clearNotifications(applicationContext)
        hideSweetProgress()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(this)
        }

        if (checkFineLocationPermission(this)) {
            getLocationDetail = GetLocationDetail(this, this)
            request = LocationRequest()
            request?.interval = INTERVAL
            request?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            easyWayLocation = EasyWayLocation(this, request, true, this)
            easyWayLocation?.startLocation() //calculateDistance()

        } else {
            requestFineLocationPermission(this)
        }

        val locale = Guru.getString(resources.getString(R.string.locale_sp), resources.getString(R.string._english))
        Log.e("Lang", "" + locale)
        if (locale.equals(resources.getString(R.string._gujarati), ignoreCase = true)) {
            changeLang(applicationContext, "ગુજરાતી")
        } else if (locale.equals(resources.getString(R.string._hindi), ignoreCase = true)) {
            changeLang(applicationContext, "हिन्दी")
        } else {
            changeLang(applicationContext, "English")
        }


    }

    override fun onPause() {
        super.onPause()
        if (checkFineLocationPermission(this)) {
            easyWayLocation?.endUpdates()
        } else {
            requestFineLocationPermission(this)
        }
    }

    override fun onBackPressed() {
        backNavigation(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EasyWayLocation.LOCATION_SETTING_REQUEST_CODE) {
            easyWayLocation?.onActivityResult(resultCode)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_REQUEST) {
            easyWayLocation?.startLocation()
        } else {
            when (requestCode) {
                PERMISSION_REQUEST_READ_PHONE_STATE ->
                    if (grantResults.size > 0) {
                        val CallAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                        if (!CallAccepted) {
                            binding.containerBody.snackbar("Permission Required for Incoming Call Dialog Feature", Snackbar.LENGTH_LONG)
                        }
                    }
            }
        }
    }


    private val toolbarHeight: Int
        get() {
            val tv = TypedValue()
            return if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            } else {
                0
            }
        }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.space.onSaveInstanceState(outState)
    }

    override fun onDrawerItemSelected(view: View?, position: Int) {
        Log.d(TAG, "position: $position")
        if (position == 0) {
            movetoFragment(this, DashboardFragment())
        } else if (position == 1) {
            movetoFragment(this, FilterListFragment())
        } else if (position == 2) {
            movetoFragment(this, StatisticFragment())
        } else if (position == 3) {
            movetoFragment(this, CommitteeFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        this.menu = menu
        loadProfile()
        return true
    }

    private fun logUser(member: Member) {
        Crashlytics.setUserIdentifier(member.id)
        Crashlytics.setUserEmail(member.emailAddress)
        Crashlytics.setUserName(member.firstName)
    }

    private fun loadProfile() {
        val memberString = Guru.getString(getString(R.string.loginMember), "")
        val member = Gson().fromJson(memberString, Member::class.java)
        val str = resources.getString(R.string.base_url_thumb) + member?.profilePic
        logUser(member)
        Glide.with(this)
                .load(str)
                .apply(RequestOptions.circleCropTransform()).thumbnail(0.5f)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        menu?.findItem(R.id.action_profile)?.icon = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startSweetProgress(this, getString(R.string.MoveProfile), getString(R.string.loading))
                val intent = Intent(this, ProfileDetailActivity::class.java)
                val memberString = Guru.getString(getString(R.string.loginMember), "")
                val member = Gson().fromJson(memberString, Member::class.java)
                intent.putExtra(getString(R.string.member), member)
                startActivity(intent)
                //fade(this)
                true
            }
            R.id.action_notify -> {
                binding.containerBody.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                true
                //movetoFragment(this, NotificationListFragment())
            }
            R.id.action_share -> {
                shareApp(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun locationCancelled() {
        binding.containerBody.snackbar(getString(R.string.LocationOff), Snackbar.LENGTH_SHORT)
    }

    override fun locationOn() {
        binding.containerBody.snackbar(getString(R.string.LocationOn), Snackbar.LENGTH_SHORT)
    }

    override fun currentLocation(location: Location) {
        cur_lat.postValue(location.latitude)
        cur_lng.postValue(location.longitude)
        getLocationDetail?.getAddress(location.latitude, location.longitude, getString(R.string.map_api_key))
    }

    override fun locationData(locationData: LocationData) {
        curAddr.postValue(locationData.full_address)
    }

    override fun getVersionResponse(response: UserStatusResponse) {
        if (!response.success) {
            showVersionDialog(this)
        }
    }

    override fun getMastersResponse(response: MasterUpdateResponse) {
        if (response.success) {

            Coroutines.io {
                if (!response.userCounts.matrimonyCounts.isNullOrEmpty()) {
                    matrimonyCounts.postValue(response.userCounts.matrimonyCounts)
                }
                if (!response.userCounts.statusCounts.isNullOrEmpty()) {
                    statusCounts.postValue(response.userCounts.statusCounts)
                }

                val counts = MasterCounts()
                counts.business_categories = Integer.parseInt(response.countList.businessCategories)
                counts.business_sub_categories = Integer.parseInt(response.countList.businessSubCategories)
                counts.cities = Integer.parseInt(response.countList.cities)
                counts.committees = Integer.parseInt(response.countList.committees)
                counts.current_activity = Integer.parseInt(response.countList.currentActivity)
                counts.designations = Integer.parseInt(response.countList.designations)
                counts.districts = Integer.parseInt(response.countList.districts)
                counts.educations = Integer.parseInt(response.countList.educations)
                counts.local_community = Integer.parseInt(response.countList.localCommunity)
                counts.native_place = Integer.parseInt(response.countList.native)
                counts.occupation = Integer.parseInt(response.countList.occupation)
                counts.relations = Integer.parseInt(response.countList.relations)
                counts.states = Integer.parseInt(response.countList.states)
                counts.sub_casts = Integer.parseInt(response.countList.subCasts)
                counts.sub_community = Integer.parseInt(response.countList.subCommunity)
                counts.gotra = Integer.parseInt(response.countList.gotra)
                val dbCount = dashboardViewModel.getMasterCounts()

                if (dbCount == null) {
                    dashboardViewModel.fetchBusinessCategory(counts.business_categories)
                    dashboardViewModel.fetchBusinessSubCategory(counts.business_sub_categories)
                    dashboardViewModel.fetchState(counts.states)
                    dashboardViewModel.fetchCity(counts.cities)
                    dashboardViewModel.fetchRelations(counts.relations)
                    dashboardViewModel.fetchSubCommunities(counts.sub_community)
                    dashboardViewModel.fetchLocalCommunities(counts.local_community)
                    dashboardViewModel.fetchLastName(counts.sub_casts)
                    dashboardViewModel.fetchEducation(counts.educations)
                    dashboardViewModel.fetchNative(counts.native_place)
                    dashboardViewModel.fetchCurrentActivity(counts.current_activity)
                    dashboardViewModel.fetchOccupation(counts.occupation)
                    dashboardViewModel.fetchCommittee(counts.committees)
                    dashboardViewModel.fetchDesignation(counts.designations)
                    if (BuildConfig.FLAVOR != "ghanchi") {
                        dashboardViewModel.fetchGotra(counts.gotra)
                    }

                    dashboardViewModel.insertMasterCounts(counts)
                } else {
                    if (dbCount.business_categories != counts.business_categories) {
                        dashboardViewModel.fetchBusinessCategory(counts.business_categories)
                    }
                    if (dbCount.business_sub_categories != counts.business_sub_categories) {
                        dashboardViewModel.fetchBusinessSubCategory(counts.business_sub_categories)
                    }
                    if (dbCount.states != counts.states) {
                        dashboardViewModel.fetchState(counts.states)
                    }
                    if (dbCount.cities != counts.cities) {
                        dashboardViewModel.fetchCity(counts.cities)
                    }
                    if (dbCount.relations != counts.relations) {
                        dashboardViewModel.fetchRelations(counts.relations)
                    }
                    if (dbCount.sub_community != counts.sub_community) {
                        dashboardViewModel.fetchSubCommunities(counts.sub_community)
                    }
                    if (dbCount.local_community != counts.local_community) {
                        dashboardViewModel.fetchLocalCommunities(counts.local_community)
                    }
                    if (dbCount.sub_casts != counts.sub_casts) {
                        dashboardViewModel.fetchLastName(counts.sub_casts)
                    }
                    if (dbCount.educations != counts.educations) {
                        dashboardViewModel.fetchEducation(counts.educations)
                    }
                    if (dbCount.native_place != counts.native_place) {
                        dashboardViewModel.fetchNative(counts.native_place)
                    }
                    if (dbCount.current_activity != counts.current_activity) {
                        dashboardViewModel.fetchCurrentActivity(counts.current_activity)
                    }
                    if (dbCount.occupation != counts.occupation) {
                        dashboardViewModel.fetchOccupation(counts.occupation)
                    }
                    if (dbCount.committees != counts.committees) {
                        dashboardViewModel.fetchCommittee(counts.committees)
                    }
                    if (dbCount.designations != counts.designations) {
                        dashboardViewModel.fetchDesignation(counts.designations)
                    }
                    if (BuildConfig.FLAVOR != "ghanchi") {
                        if (dbCount.gotra != counts.gotra) {
                            dashboardViewModel.fetchGotra(counts.gotra)
                        }
                    }
                }
            }
        }
    }

    override suspend fun getFailure(msg: String) {

    }
}