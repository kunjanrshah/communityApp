package com.krs.community.activity

import android.Manifest
import android.content.Intent
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
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.PermissionRequest
import com.krs.community.R
import com.krs.community.databinding.ActivityDashboardBinding
import com.krs.community.fragments.*
import com.krs.community.fragments.FragmentDrawer.FragmentDrawerListener
import com.krs.community.model.Member
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.utils.Utility.backNavigation
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.DashboardViewModel
import com.krs.community.viewmodel.DashboardViewModelFactory
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.luseen.spacenavigation.SpaceOnLongClickListener
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import com.karumi.dexter.listener.multi.MultiplePermissionsListener as MultiplePermissionsListener

class DashboardActivity : BaseActivity(), FragmentDrawerListener, KodeinAware, Listener, LocationData.AddressCallBack {

    private val TAG = DashboardActivity::class.java.simpleName
    private lateinit var dashboardViewModel: DashboardViewModel
    private val factory: DashboardViewModelFactory by instance()
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var request: LocationRequest

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
        dashboardViewModel = ViewModelProviders.of(this, factory).get(DashboardViewModel::class.java)

        //  val from= intent.getStringExtra("from")
        //  val id= intent.getStringExtra("id")

        if (Guru.getString(getString(R.string.user_id), "")!!.isEmpty()) {
            val mIntent = Intent(this@DashboardActivity, SplashActivity::class.java)
            startActivity(mIntent)
            finish()
            Utility.fade(this)
        }


        setSupportActionBar(binding.toolbar as Toolbar)
        (binding.toolbar as Toolbar).setTitleTextColor(resources.getColor(R.color.colorPrimary))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Home"

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
        binding.space.addSpaceItem(SpaceItem("Home", R.drawable.home))
        binding.space.addSpaceItem(SpaceItem("Calendar", R.drawable.calendar4))
        binding.space.shouldShowFullBadgeText(false)
        binding.space.setCentreButtonIconColorFilterEnabled(false)
        binding.space.setCentreButtonIcon(R.drawable.filter_icon)
        binding.space.animate().translationY(0f).alpha(1f).setDuration(2000).start()

        binding.space.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick")
                Utility.movetoFragment(this@DashboardActivity, ExpandableFilterListFragment())
            }

            override fun onItemClick(itemIndex: Int, itemName: String) {
                Log.d("onItemClick ", "$itemIndex $itemName")
                if (itemIndex == 1) {
                    val fragment = supportFragmentManager.findFragmentByTag(CalendarFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        Utility.movetoFragment(this@DashboardActivity, CalendarFragment())
                    }
                } else if (itemIndex == 0) {
                    val fragment = supportFragmentManager.findFragmentByTag(DashboardFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        Utility.movetoFragment(this@DashboardActivity, DashboardFragment())
                    }
                }
            }

            override fun onItemReselected(itemIndex: Int, itemName: String) {
                Log.d("onItemReselected ", "$itemIndex $itemName")
                if (itemIndex == 1) {
                    val fragment = supportFragmentManager.findFragmentByTag(CalendarFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        Utility.movetoFragment(this@DashboardActivity, CalendarFragment())
                    }
                } else if (itemIndex == 0) {
                    val fragment = supportFragmentManager.findFragmentByTag(DashboardFragment::class.java.simpleName)
                    if (fragment == null || !fragment.isVisible) {
                        Utility.movetoFragment(this@DashboardActivity, DashboardFragment())
                    }
                }
            }
        })

        binding.space.setSpaceOnLongClickListener(object : SpaceOnLongClickListener {
            override fun onCentreButtonLongClick() {
                Toast.makeText(this@DashboardActivity, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClick(itemIndex: Int, itemName: String) {
                Toast.makeText(this@DashboardActivity, "$itemIndex $itemName", Toast.LENGTH_SHORT).show()
            }
        })

        getLocationDetail = GetLocationDetail(this, this)
        request = LocationRequest()
        request.interval = Utility.INTERVAL
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        easyWayLocation = EasyWayLocation(this, request, true, this)

        if (Utility.finePermissionIsGranted(this)) {
            easyWayLocation.startLocation() //calculateDistance()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }

        getMasterList()
        Utility.movetoFragment(this@DashboardActivity, DashboardFragment())

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener( MultiplePermissionsListener {

                }).check();

        //spaceNavigationView.showIconOnly();
    }

    override fun onResume() {
        super.onResume()
        easyWayLocation.startLocation()
    }

    override fun onPause() {
        super.onPause()
        easyWayLocation.endUpdates()
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

    override fun onDrawerItemSelected(view: View, position: Int) {
        Log.d(TAG, "position: $position")
        if (position == 0) {
            Utility.movetoFragment(this, DashboardFragment())
        } else if (position == 1) {
            Utility.movetoFragment(this, FiltersFragment())
        } else if (position == 2) {
            Utility.movetoFragment(this, StatisticFragment())
        } else if (position == 3) {
            Utility.movetoFragment(this, CommitteeFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, ProfileDetailActivity::class.java)
                val memberString = Guru.getString(getString(R.string.loginUser), "")
                val member = Gson().fromJson(memberString, Member::class.java)
                intent.putExtra(getString(R.string.member), member)
                startActivity(intent)
                Utility.fade(this)
                true
            }
            R.id.action_notify -> {
                Utility.movetoFragment(this, NotificationListFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun locationCancelled() {
        binding.containerBody.snackbar("Location Off", Snackbar.LENGTH_SHORT)
    }

    override fun locationOn() {
        binding.containerBody.snackbar("Location On", Snackbar.LENGTH_SHORT)
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