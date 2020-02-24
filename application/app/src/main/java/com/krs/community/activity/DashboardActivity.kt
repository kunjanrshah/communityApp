package com.krs.community.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.krs.community.R
import com.krs.community.databinding.ActivityDashboardBinding
import com.krs.community.fragments.*
import com.krs.community.fragments.FragmentDrawer.FragmentDrawerListener
import com.krs.community.model.Member
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility.*
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.DashboardViewModel
import com.krs.community.viewmodelfactory.DashboardViewModelFactory
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import com.luseen.spacenavigation.SpaceOnLongClickListener
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DashboardActivity : AppCompatActivity(), FragmentDrawerListener, KodeinAware, Listener, LocationData.AddressCallBack {

    private val TAG = DashboardActivity::class.java.simpleName
    private lateinit var dashboardViewModel: DashboardViewModel
    private val factory: DashboardViewModelFactory by instance()
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var request: LocationRequest
    private var  menu: Menu?=null
    companion object {
        var stop: Boolean = false
        lateinit var binding:ActivityDashboardBinding
        lateinit var getLocationDetail: GetLocationDetail
        var cur_lat = MutableLiveData<Double>()
        var cur_lng = MutableLiveData<Double>()
        var cur_addr = MutableLiveData<String>()
    }

    override val kodein by kodein()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@DashboardActivity, R.layout.activity_dashboard)
        dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

    //    NoInternetLayout.Builder(this@DashboardActivity, R.layout.activity_dashboard).animate()

        if (Guru.getString(getString(R.string.user_id), "")!!.isEmpty()) {
            val mIntent = Intent(this@DashboardActivity, SplashActivity::class.java)
            startActivity(mIntent)
            finish()
            fade(this)
        }

        setSupportActionBar(binding.toolbar as Toolbar)
        (binding.toolbar as Toolbar).setTitleTextColor(resources.getColor(R.color.colorPrimary))
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

        getLocationDetail = GetLocationDetail(this, this)
        request = LocationRequest()
        request.interval = INTERVAL
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        easyWayLocation = EasyWayLocation(this, request, true, this)

        if (checkFineLocationPermission(this)) {
            easyWayLocation.startLocation() //calculateDistance()
        } else {
            requestFineLocationPermission(this)
        }
        if (isOnline(this)) {
            getMasterList()
        }

        movetoFragment(this@DashboardActivity, DashboardFragment())
        //spaceNavigationView.showIconOnly();
    }


    override fun onResume() {
        super.onResume()
        if (checkFineLocationPermission(this)) {
            easyWayLocation.startLocation() //calculateDistance()
        } else {
            requestFineLocationPermission(this)
        }
        loadProfile()
        hideSweetProgress()
    }


    override fun onPause() {
        super.onPause()
        if (checkFineLocationPermission(this)) {
            easyWayLocation.endUpdates()
        } else {
            requestFineLocationPermission(this)
        }
    }

    override fun onBackPressed() {
        backNavigation(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EasyWayLocation.LOCATION_SETTING_REQUEST_CODE) {
            easyWayLocation.onActivityResult(resultCode)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==FINE_LOCATION_REQUEST){
            easyWayLocation.startLocation()
        }
    }


    private fun getMasterList() = Coroutines.main {
        dashboardViewModel.fetchState()
        dashboardViewModel.fetchCity()
        dashboardViewModel.fetchRelations()
        dashboardViewModel.fetchSubCommunities()
        dashboardViewModel.fetchLocalCommunities()
        dashboardViewModel.fetchLastName()
        dashboardViewModel.fetchEducation()
        dashboardViewModel.fetchGotra()
        dashboardViewModel.fetchBusinessCategory()
        dashboardViewModel.fetchBusinessSubCategory()
        dashboardViewModel.fetchNative()
        dashboardViewModel.fetchCurrentActivity()
        dashboardViewModel.fetchOccupation()
        dashboardViewModel.fetchCommittee()
        dashboardViewModel.fetchDesignation()
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
            binding.containerBody.snackbar(resources.getString(R.string.coming_soon),Snackbar.LENGTH_LONG)
            return
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

    private fun loadProfile(){
        val memberString = Guru.getString(getString(R.string.loginMember), "")
        val member = Gson().fromJson(memberString, Member::class.java)
        val str=resources.getString(R.string.base_url_thumb)+member?.profilePic

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
                startSweetProgress(this,getString(R.string.MoveProfile),getString(R.string.loading))
                val intent = Intent(this, ProfileDetailActivity::class.java)
                val memberString = Guru.getString(getString(R.string.loginMember), "")
                val member = Gson().fromJson(memberString, Member::class.java)
                intent.putExtra(getString(R.string.member), member)
                startActivity(intent)
                fade(this)
                true
            }
            R.id.action_notify -> {
                binding.containerBody.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                true
                //movetoFragment(this, NotificationListFragment())
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
        getLocationDetail.getAddress(location.latitude, location.longitude, getString(R.string.map_api_key))
    }

    override fun locationData(locationData: LocationData) {
        cur_addr.postValue(locationData.full_address)
    }


}