package com.krs.community.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.github.squti.guru.Guru
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.SplashActivity
import com.krs.community.adapter.NavigationDrawerAdapter
import com.krs.community.listeners.InnerLogoutListner
import com.krs.community.model.NavDrawerItem
import com.krs.community.responses.UserInnerLogoutResponse
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodelfactory.FamilyDetailViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class FragmentDrawer : Fragment(), KodeinAware, InnerLogoutListner {
    var mDrawerToggle: ActionBarDrawerToggle? = null

    var containerView: View? = null
    private var drawerListener: FragmentDrawerListener? = null
    private var view1: View? = null
    private lateinit var familyDetailViewModel: FamilyDetailViewModel
    private val familyDetailViewModelFactory: FamilyDetailViewModelFactory by instance()
    override val kodein by kodein()

    fun setDrawerListener(listener: FragmentDrawerListener?) {
        drawerListener = listener
    }

    private var titles: Array<String>? = null
    private val data: List<NavDrawerItem>
        get() {
            val data: MutableList<NavDrawerItem> = ArrayList()
            assert(titles != null)
            for (title in titles!!) {
                val navItem = NavDrawerItem()
                navItem.title = title
                data.add(navItem)
            }
            return data
        }

    companion object {
        @kotlin.jvm.JvmField
        var mDrawerLayout: DrawerLayout? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titles = activity!!.resources.getStringArray(R.array.nav_drawer_labels)
        familyDetailViewModel = ViewModelProvider(this, familyDetailViewModelFactory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.innerLogoutListner = this
    }

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflating view layout
        val layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false)
        val recyclerView: RecyclerView = layout.findViewById(R.id.drawerList)
        val tvSettings = layout.findViewById<TextView>(R.id.tv_settings)
        val tvContactUs = layout.findViewById<TextView>(R.id.tv_contact_us)
        val llChangeLan = layout.findViewById<LinearLayout>(R.id.ll_change_lan)
        llChangeLan.setOnClickListener { v: View? ->
            mDrawerLayout!!.closeDrawers()
            Utility.movetoFragment(activity, ChangeLanguageFragment())
        }
        val llLogout = layout.findViewById<LinearLayout>(R.id.ll_logout)
        llLogout.setOnClickListener { v: View? ->
            TTFancyGifDialog.Builder(activity)
                    .setTitle(getString(R.string.you_sure))
                    .setMessage("Logout the Community App")
                    .setPositiveBtnText("Yes")
                    .setPositiveBtnBackground("#22b573")
                    .setNegativeBtnText("No")
                    .setNegativeBtnBackground("#c1272d")
                    .setGifResource(R.drawable.gif2)
                    .isCancellable(true)
                    .OnPositiveClicked {
                        if (Utility.isOnline(context)) {
                            getMemberLogout()
                        }
                    }
                    .OnNegativeClicked {

                    }
                    .build()
        }
        val tvVersion = layout.findViewById<TextView>(R.id.tv_version)
        tvVersion.text = resources.getString(R.string.Version) + BuildConfig.VERSION_NAME
        tvSettings.setOnClickListener { v: View? ->
            mDrawerLayout!!.closeDrawers()
            Utility.movetoFragment(activity, SettingFragment())
        }
        tvContactUs.setOnClickListener { v: View? ->
            mDrawerLayout!!.closeDrawers()
            Utility.movetoFragment(activity, ContactUsFragment())
        }
        val adapter = NavigationDrawerAdapter(activity, data)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addOnItemTouchListener(RecyclerTouchListener(activity, recyclerView, object : ClickListener {
            override fun onClick(view: View?, position: Int) {
                /*if (view1 != null) {
                    val llItem = view1!!.findViewById<LinearLayout>(R.id.ll_item)
                    val imgDrawer = view1!!.findViewById<ImageView>(R.id.imgDrawer)
                    val title = view1!!.findViewById<TextView>(R.id.title)
                    llItem.background = null
                    title.setTextColor(resources.getColor(R.color.black2))
                    if (view1!!.tag.toString() == "0") {
                        imgDrawer.setBackgroundResource(R.drawable.home)
                    } else if (view1!!.tag.toString() == "1") {
                    //    imgDrawer.setBackgroundResource(R.drawable.filter_outline)
                    } else if (view1!!.tag.toString() == "2") {
                      //  imgDrawer.setBackgroundResource(R.drawable.analytics)
                    } else if (view1!!.tag.toString() == "3") {
                       // imgDrawer.setBackgroundResource(R.drawable.committee1)
                    }
                }*/
                /*   view?.tag = position
                   view1 = view
                   val llItem = view?.findViewById<LinearLayout>(R.id.ll_item)
                   val imgDrawer = view?.findViewById<ImageView>(R.id.imgDrawer)
                   val title = view?.findViewById<TextView>(R.id.title)
                   if (position == 0) {
                       llItem?.background = resources.getDrawable(R.drawable.right_round_corner)
                       title?.setTextColor(resources.getColor(R.color.colorPrimary))
                       imgDrawer?.setBackgroundResource(R.drawable.home_primary)
                   } else if (position == 1) {
                      // imgDrawer?.setBackgroundResource(R.drawable.filter_outline_color_primary)
                   } else if (position == 2) {
                      // imgDrawer?.setBackgroundResource(R.drawable.analytics_color_primary)
                   } else if (position == 3) {
                     //  imgDrawer?.setBackgroundResource(R.drawable.committee1_color_primary)
                   }*/
                drawerListener!!.onDrawerItemSelected(view, position)
                mDrawerLayout!!.closeDrawers()
            }
        }))
        return layout
    }

    private fun getMemberLogout() {
        val jsonObject = JSONObject()
        val userId = Guru.getString(getString(R.string.user_id), "")
        val memberId = Guru.getString(getString(R.string.member_id), "")
        if (!userId.isNullOrEmpty() && !memberId.isNullOrEmpty()) {
            jsonObject.put(getString(R.string.user_id), userId)
            jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
            jsonObject.put(getString(R.string.id), memberId)
            val records = JsonParser().parse(jsonObject.toString()) as JsonObject
            Utility.startSweetProgress(context, getString(R.string.Exit), getString(R.string.loading))
            familyDetailViewModel.getInnerLogout(records)
        } else {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setUp(fragmentId: Int, drawerLayout: DrawerLayout?, toolbar: Toolbar) {

        containerView = Objects.requireNonNull(activity)?.findViewById(fragmentId)
        mDrawerLayout = drawerLayout
        mDrawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle = object : ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Utility.hideKeyboard(activity)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                //getActivity().invalidateOptionsMenu();
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                toolbar.alpha = 1 - slideOffset / 2
            }
        }
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
        mDrawerLayout!!.post { mDrawerToggle?.syncState() }
        /* mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "sdf", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    internal interface ClickListener {
        fun onClick(view: View?, position: Int)
    }

    interface FragmentDrawerListener {
        fun onDrawerItemSelected(view: View?, position: Int)
    }

    internal class RecyclerTouchListener(context: Context?, recyclerView: RecyclerView, private val clickListener: ClickListener?) : OnItemTouchListener {
        private val gestureDetector: GestureDetector?
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector!!.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child))
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) { /* View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }*/
                }
            })
        }
    }

    override fun userLogout(response: UserInnerLogoutResponse) {
        Utility.hideSweetProgress()
        if (response.success) {
            Guru.clear()
            val intent = Intent(activity, SplashActivity::class.java)
            startActivity(intent)
            activity!!.finish()
            Utility.fade(activity)
        } else {
            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
        }
    }

    override suspend fun getFailure(message: String) {
        Utility.hideSweetProgress()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}