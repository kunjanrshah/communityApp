package com.krs.community.activity

import android.content.Intent
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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.github.squti.guru.Guru
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.krs.community.R
import com.krs.community.fragments.*
import com.krs.community.fragments.FragmentDrawer.FragmentDrawerListener
import com.krs.community.model.Member
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.DashboardViewModel
import com.krs.community.viewmodel.DashboardViewModelFactory
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.luseen.spacenavigation.SpaceOnLongClickListener
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DashboardActivity : BaseActivity(), FragmentDrawerListener, KodeinAware {

    private var mDrawerLayout: DrawerLayout? = null
    private val TAG = DashboardActivity::class.java.simpleName
    private lateinit var dashboardViewModel: DashboardViewModel
    private val factory: DashboardViewModelFactory by instance()

    companion object {
        lateinit var myAppBar: AppBarLayout
        //@JvmField
        lateinit var spaceNavigationView: SpaceNavigationView
    }

    override val kodein by kodein()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dashboardViewModel = ViewModelProviders.of(this,factory).get(DashboardViewModel::class.java)

     //  val from= intent.getStringExtra("from")
     //  val id= intent.getStringExtra("id")

        if (Guru.getString(getString(R.string.user_id), "")!!.isEmpty()) {
            val mIntent = Intent(this@DashboardActivity, SplashActivity::class.java)
            startActivity(mIntent)
            finish()
            Utility.fade(this)
        }
        setContentView(R.layout.activity_dashboard)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar)
        mToolbar.setTitleTextColor(resources.getColor(R.color.colorPrimary))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Home")

        myAppBar = findViewById(R.id.myAppBar)
        myAppBar.setTranslationY(-toolbarHeight.toFloat())
        myAppBar.animate()!!.translationY(0f).alpha(1f).setDuration(2000).start()
        val drawerFragment = supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as FragmentDrawer?
        mDrawerLayout = findViewById(R.id.drawer_layout)
        drawerFragment!!.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, mToolbar)
        drawerFragment.mDrawerToggle!!.isDrawerIndicatorEnabled = false
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.menu_slide1, theme)
        drawerFragment.mDrawerToggle!!.setHomeAsUpIndicator(drawable)
        drawerFragment.setDrawerListener(this)
        if (drawerFragment.mDrawerToggle != null) {
            drawerFragment.mDrawerToggle!!.toolbarNavigationClickListener = View.OnClickListener { v: View? ->
                if (mDrawerLayout!!.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout?.closeDrawer(GravityCompat.START)
                } else {
                    mDrawerLayout?.openDrawer(GravityCompat.START)
                }
            }
        }
        spaceNavigationView = findViewById(R.id.space)
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState)
        spaceNavigationView.addSpaceItem(SpaceItem("Home", R.drawable.home))
        spaceNavigationView.addSpaceItem(SpaceItem("Calendar", R.drawable.calendar4))
        spaceNavigationView.shouldShowFullBadgeText(false)
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false)
        spaceNavigationView.setCentreButtonIcon(R.drawable.filter_icon)
        spaceNavigationView.animate().translationY(0f).alpha(1f).setDuration(2000).start()
        Utility.movetoFragment(this@DashboardActivity, DashboardFragment())
        spaceNavigationView.setSpaceOnClickListener(object : SpaceOnClickListener {
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

        spaceNavigationView.setSpaceOnLongClickListener(object : SpaceOnLongClickListener {
            override fun onCentreButtonLongClick() {
                Toast.makeText(this@DashboardActivity, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClick(itemIndex: Int, itemName: String) {
                Toast.makeText(this@DashboardActivity, "$itemIndex $itemName", Toast.LENGTH_SHORT).show()
            }
        })
        getMasterList()
        //spaceNavigationView.showIconOnly();
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
        spaceNavigationView.onSaveInstanceState(outState)
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
                val intent= Intent(this,ProfileDetailActivity::class.java)
                val memberString= Guru.getString(getString(R.string.loginUser),"")
                val member=Gson().fromJson(memberString, Member::class.java)
                intent.putExtra(getString(R.string.member),member)
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
 }