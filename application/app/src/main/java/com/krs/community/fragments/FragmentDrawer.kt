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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.ContactUsActivity
import com.krs.community.activity.SplashActivity
import com.krs.community.adapter.NavigationDrawerAdapter
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.InnerLogoutListner
import com.krs.community.model.Member
import com.krs.community.model.NavDrawerItem
import com.krs.community.responses.UserInnerLogoutResponse
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodelfactory.FamilyDetailViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class FragmentDrawer : Fragment(), KodeinAware, InnerLogoutListner {
    var mDrawerToggle: ActionBarDrawerToggle? = null

    var containerView: View? = null
    private var drawerListener: FragmentDrawerListener? = null
    private lateinit var familyDetailViewModel: FamilyDetailViewModel
    private val familyDetailViewModelFactory: FamilyDetailViewModelFactory by instance<FamilyDetailViewModelFactory>()
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

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, FragmentDrawer::class.simpleName)
        mApp.facebookAnalytics(context, FragmentDrawer::class.simpleName)

        val recyclerView: RecyclerView = layout.findViewById(R.id.drawerList)
        val tvSettings = layout.findViewById<TextView>(R.id.tv_settings)
        val tvContactUs = layout.findViewById<TextView>(R.id.tv_contact_us)
        val llChangeLan = layout.findViewById<LinearLayout>(R.id.ll_change_lan)
        val llFoundation = layout.findViewById<LinearLayout>(R.id.ll_foundation)

        if (BuildConfig.FLAVOR == "ghanchi") {
            llFoundation.visibility = View.VISIBLE
            val ivLogo = layout.findViewById<AppCompatImageView>(R.id.iv_logo)
            Glide.with(this).load(ContextCompat.getDrawable(activity as AppCompatActivity, R.drawable.sara_foundation)).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(ivLogo)
        } else {
            llFoundation.visibility = View.GONE
        }


        llChangeLan.setOnClickListener { v: View? ->
            mDrawerLayout!!.closeDrawers()

            Utility.movetoFragment(activity, ChangeLanguageFragment())

        }
        val llLogout = layout.findViewById<LinearLayout>(R.id.ll_logout)
        llLogout.setOnClickListener { v: View? ->
            TTFancyGifDialog.Builder(activity)
                    .setTitle(getString(R.string.you_sure))
                    .setMessage("Exit the " + getString(R.string.app_name))
                    .setPositiveBtnText("Yes")
                    .setPositiveBtnBackground("#22b573")
                    .setNegativeBtnText("No")
                    .setNegativeBtnBackground("#c1272d")
                    .setGifResource(R.drawable.gif_dialog)
                    .isCancellable(false)
                    .OnPositiveClicked {
                        if (isNetworkConnected(context as AppCompatActivity)) {
                            getMemberLogout()
                        }
                    }
                    .OnNegativeClicked {

                    }
                    .build()
        }
        val tvVersion = layout.findViewById<TextView>(R.id.tv_version)
        tvVersion.text = resources.getString(R.string.Version) + " " + Utility.getAppVersionName(AppController.mApplication)
        tvSettings.setOnClickListener { v: View? ->
            mDrawerLayout!!.closeDrawers()
            Utility.movetoFragment(activity, SettingFragment())
        }
        tvContactUs.setOnClickListener { v: View? ->
            mDrawerLayout!!.closeDrawers()
            val intent = Intent(activity, ContactUsActivity::class.java)
            startActivity(intent)
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

    private fun closefragment() {


    }

    private fun getMemberLogout() {

        Guru.clear()
        Guru.putBoolean(getString(R.string.policy), true)
        val intent = Intent(activity, SplashActivity::class.java)
        startActivity(intent)
        activity?.finish()

        /*val jsonObject = JSONObject()
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
        }*/
    }

    fun setUp(fragmentId: Int, drawerLayout: DrawerLayout?, toolbar: Toolbar) {

        containerView = Objects.requireNonNull(activity)?.findViewById(fragmentId)
        mDrawerLayout = drawerLayout
        mDrawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        mDrawerToggle = object : ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Utility.hideKeyboard(activity)
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

            val intent = Intent(activity, FamilyDetailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val memberString = Guru.getString(getString(R.string.loginMember), "")
            val member = Gson().fromJson(memberString, Member::class.java)
            Guru.putString(getString(R.string.member_id), "")
            Guru.putString(getString(R.string.loginMember), "")
            var id = ""
            if (member.headId == "0") {
                id = member.id
            } else {
                id = member.headId
            }
            intent.putExtra(getString(R.string.id), id)
            startActivity(intent)
            activity?.finish()
            //  Utility.fade(activity)
        } else {
            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
        }
    }

    override suspend fun getFailure(message: String) {
        Utility.hideSweetProgress()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}