package com.krs.community.fragments

//import com.krs.community.activity.DashboardActivity.Companion.easyWayLocation
//import com.krs.community.activity.DashboardActivity.Companion.request
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FavoriteProfileActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.activity.RegisterActivty
import com.krs.community.adapter.SliderAdapterExample
import com.krs.community.app.AppController
import com.krs.community.app.NotificationBadge
import com.krs.community.bkservice.ProcessMainClass
import com.krs.community.bkservice.restarter.RestartServiceBroadcastReceiver
import com.krs.community.databinding.FragmentDashboardBinding
import com.krs.community.listeners.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.model.SliderItem
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.SmartFilterViewModelFactory
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController
import com.smarteist.autoimageslider.SliderAnimations
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.Locale


class DashboardFragment : Fragment(), KodeinAware, ByFilterListener {

    private var sharedProfiles = ArrayList<Member>()
    private var duplicateIds = ArrayList<String>()
    private var defaultProfiles = ArrayList<Member>()
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var filterViewModel: SmartFilterViewModel
    private val filterViewModelFactory: SmartFilterViewModelFactory by instance<SmartFilterViewModelFactory>()
    private var isTouch = false
    private lateinit var layoutManager: LinearLayoutManager
    private var sharedAdapter: SharedProfileAdapter? = null

    private val duration = 10L
    private val pixelsToMove = 30
    private val mHandler = Handler(Looper.getMainLooper())
    private var loginMember: Member? = null
    var SCROLLING_RUNNABLE: Runnable = object : Runnable {
        override fun run() {
            binding.lstSharedProfile.smoothScrollBy(pixelsToMove, 0)
            mHandler.postDelayed(this, duration)
        }
    }


    private var adapter: SliderAdapterExample? = null

    override val kodein by kodein()

    companion object {
        val TAG = DashboardFragment::class.java.simpleName
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, DashboardFragment::class.simpleName)
        mApp.facebookAnalytics(context, DashboardFragment::class.simpleName)

        filterViewModel = ViewModelProvider(this, filterViewModelFactory).get(SmartFilterViewModel::class.java)
        filterViewModel.mByFilterListener = this

        Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        binding.gridView.isExpanded = true

        adapter = SliderAdapterExample(activity as AppCompatActivity);
        binding.imageSlider.setSliderAdapter(adapter!!);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.SWAP)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
        binding.imageSlider.scrollTimeInSec = 3 //set scroll delay in seconds :
        setSliderViews()
        binding.imageSlider.setOnIndicatorClickListener(DrawController.ClickListener {
            Log.i(
                "GGG",
                "onIndicatorClicked: " + binding.imageSlider.getCurrentPagePosition()
            )
            Utility.movetoFragment(activity, NewsListFragment())
        })


        (activity as AppCompatActivity?)!!.supportActionBar!!.title = getString(R.string.home)
        binding.edtSearch.inputType = InputType.TYPE_NULL
        binding.edtSearch.keyListener = null

        binding.edtSearch.setOnTouchListener { _: View?, event: MotionEvent? ->
            val DRAWABLE_RIGHT = 2
            if (event?.action == MotionEvent.ACTION_UP) {
                if ((event.rawX + 70) >= (binding.edtSearch.right - binding.edtSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    getSpeechInput()
                    true
                } else {
                    if (!isTouch) {
                        isTouch = true
                        Utility.movetoFragment(activity, SearchListFragment())
                        true
                    }
                }
            }
            false
        }

        binding.lstSharedProfile.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(AppController.mApplication.applicationContext)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        binding.lstSharedProfile.adapter = sharedAdapter
        binding.lstSharedProfile.layoutManager = layoutManager
        defaultProfiles.clear()
        for (index in 1..4) {
            val member = Member()
            member.profilePic = "noimage.png?fhtfhjuyffgh"
            member.firstName = "name"
            defaultProfiles.add(member)
        }


        setDefaultProfileList()
        getSharedProfileList()
        binding.gridView.adapter = MenuAdapter(requireActivity())
        binding.tvAllShared.setOnClickListener { v: View? -> Utility.movetoFragment(activity, SharedLocationFragment()) }
        binding.tvAllNews.setOnClickListener { v: View? ->
           binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
            Utility.movetoFragment(activity, NewsListFragment())
            return@setOnClickListener

        }
        Utility.changeStatusbarColor(activity, R.color.white, false)
        //setRecyclerViewScrollListener()

        val loginMember1 = Guru.getString(getString(R.string.loginMember), "")
        this.loginMember = Gson().fromJson(loginMember1, Member::class.java)
        return binding.root
    }


    /* private fun startLocationService() {
         try {
             if (easyWayLocation?.hasLocationEnabled()!!) {
                 if (Utility.checkFineLocationPermission(activity)) {
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                         RestartServiceBroadcastReceiver.scheduleJob(activity)
                     } else {
                         val bck = ProcessMainClass()
                         bck.launchService(activity)
                     }
                 } else {
                     if (Utility.checkFineLocationPermission(activity)) {
                         easyWayLocation?.startLocation() //calculateDistance()
                     } else {
                         Utility.requestFineLocationPermission(activity as AppCompatActivity)
                     }
                 }
             } else {
                 easyWayLocation = EasyWayLocation(activity, request, true, activity as DashboardActivity)
             }
         } catch (e: java.lang.Exception) {
             e.printStackTrace()
         }
     }*/

    private fun getSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, 10)
        } else {
            binding.llParent.snackbar(getString(R.string.DevicDont), Snackbar.LENGTH_LONG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            10 -> if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "data: " + result?.get(0))
                }


                val searchFragment = SearchListFragment()
                val bundle = Bundle()
                bundle.putString("keyword", result?.get(0))
                searchFragment.arguments = bundle
                Utility.movetoFragment(activity, searchFragment)
            }
        }
    }

    private lateinit var scrollListener: OnScrollListener
    private fun setRecyclerViewScrollListener() {
        scrollListener = object : OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItem: Int = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastItem == layoutManager.itemCount - 1) {
                    mHandler.removeCallbacks(SCROLLING_RUNNABLE)
                    val postHandler = Handler()
                    postHandler.postDelayed({
                        binding.lstSharedProfile.adapter = null
                        binding.lstSharedProfile.adapter = sharedAdapter
                        mHandler.postDelayed(SCROLLING_RUNNABLE, 2000)
                    }, 2000)
                }
            }

        }
        binding.lstSharedProfile.addOnScrollListener(scrollListener)
        mHandler.postDelayed(SCROLLING_RUNNABLE, 2000)
    }

    override fun onResume() {
        super.onResume()
        isTouch = false
    }


    private fun setSliderViews() {

        for (i in 0..3) {


            val sliderItem = SliderItem()
            sliderItem.description = "Advertise with us"

            when (i) {
                0 -> sliderItem.imageUrl =
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRstqpu7EP47LkJH291whXVFwdjJPQRLgwOSw&s"

                1 -> sliderItem.imageUrl =
                    "data:image/jpeg;base64,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"
                2 -> sliderItem.imageUrl = "https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"
                3 -> sliderItem.imageUrl = "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
            }

            adapter!!.addItem(sliderItem)


//            val sliderView = DefaultSliderView(activity)
//            when (i) {
//                0 -> sliderView.setImageDrawable(R.drawable.ic_launcher_background)
//                1 -> sliderView.imageUrl = "https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                2 -> sliderView.imageUrl = "https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"
//                3 -> sliderView.imageUrl = "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//            }
//            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//            // sliderView.description = "The quick brown fox jumps over the lazy dog.\n" + "Jackdaws love my big sphinx of quartz. " + (i + 1)
//            sliderView.description = "Advertise with us"
//            sliderView.setOnSliderClickListener { sliderView1: SliderView? ->
//                binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
//                return@setOnSliderClickListener
//                Utility.movetoFragment(activity, NewsListFragment())
//            }
//            binding.imageSlider.addSliderView(sliderView)
        }
    }

    internal inner class MenuViewHolder {
        var image: ImageView? = null
        var textView: TextView? = null
        var badge: NotificationBadge? = null
    }

internal inner class MenuAdapter(private val mContext: Context) : BaseAdapter() {

    override fun getCount(): Int {
        return 13
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val menuViewHolder: MenuViewHolder
        val inflater = (mContext as Activity).layoutInflater
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dashboard_menu, parent, false)
            menuViewHolder = MenuViewHolder()
            menuViewHolder.image = convertView.findViewById(R.id.image)
            menuViewHolder.textView = convertView.findViewById(R.id.name)
            menuViewHolder.badge = convertView.findViewById(R.id.badge)
            convertView.tag = menuViewHolder
        } else {
            menuViewHolder = convertView.tag as MenuViewHolder
        }
        val imgs: TypedArray = resources.obtainTypedArray(R.array.main_menu_imgs)
        val mainMenu = resources.getStringArray(R.array.main_menu)

        menuViewHolder.image?.setImageResource(imgs.getResourceId(position, -1))
        menuViewHolder.textView?.text = mainMenu[position]
        val txt = menuViewHolder.textView?.text.toString()
        if (txt == "Restricted" && isAdmin()) {
            DashboardActivity.statusCounts.observeForever {
                menuViewHolder.badge?.setNumber(Integer.parseInt(it))
            }
        } else if (txt == "Matrimony") {
            DashboardActivity.matrimonyCounts.observeForever {
                menuViewHolder.badge?.setNumber(Integer.parseInt(it))
            }
        } else {
            menuViewHolder.badge?.setNumber(0)
        }
        convertView?.setOnClickListener {
            when (position) {
                0 -> Utility.movetoFragment(activity, BrowseByCityFragment())
                1 -> {
                    val mBundle = Bundle()
                    mBundle.putSerializable(getString(R.string.member), loginMember)
                    val intent1 = Intent(activity, QRCodeActivity::class.java)
                    intent1.putExtras(mBundle)
                    startActivity(intent1)
                    //   Utility.fade(activity)
                }
                2 -> Utility.movetoFragment(activity, SearchByDistanceFragment())
                3 -> Utility.movetoFragment(activity, MatrimonyFragment())

                4 -> {

                    val intent = Intent(activity, FamilyDetailActivity::class.java)
                    if (loginMember?.headId == "0") {
                        intent.putExtra(getString(R.string.member_id), loginMember?.id)
                        intent.putExtra(getString(R.string.id), loginMember?.id)
                    } else {
                        intent.putExtra(getString(R.string.id), loginMember?.headId)
                    }
                    startActivity(intent)
                }

                5 -> {
                    binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                    Utility.movetoFragment(activity, NewsListFragment())
//                        return@setOnClickListener
                }
                6 -> {
                    val mIntent = Intent(activity, FavoriteProfileActivity::class.java)
                    startActivity(mIntent)
                    //  fade(activity)
                }
                7 -> Utility.movetoFragment(activity, AdminsFragment())
                8 -> {
                    if (!loginMember?.role.isNullOrEmpty() && loginMember?.role != getString(R.string.USER)) {
                        Utility.movetoFragment(activity, NonActivesFragment())
                    } else {
                        binding.llParent.snackbar(getString(R.string.admin_only), Snackbar.LENGTH_LONG)
                    }
                }
                9 -> {
                    binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                        return@setOnClickListener
//                    if (!loginMember?.role.isNullOrEmpty() && loginMember?.role != getString(R.string.USER)) {
//                        Utility.movetoFragment(activity, ShareEventFragment())
//                    }
//                    else {
//                        binding.llParent.snackbar(getString(R.string.admin_only), Snackbar.LENGTH_LONG)
//                    }
//                    Utility.movetoFragment(activity, ShareEventFragment())
                }
                10 -> {
                    // binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                    //startActivity(Intent(activity, ActivityDebugTools::class.java))
                    if (!loginMember?.role.isNullOrEmpty() && loginMember?.role != getString(R.string.USER)) {
                        Utility.movetoFragment(activity, UploadFragment())
                    }
                    else {
                        binding.llParent.snackbar(getString(R.string.admin_only), Snackbar.LENGTH_LONG)
                    }
//                    Utility.movetoFragment(activity, UploadFragment())
                }

//                11 -> {
//                    binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
//                    return@setOnClickListener
//                    Utility.movetoFragment(activity, PaytmFragment())
//                }

                11 -> {
                    if (!loginMember?.role.isNullOrEmpty() && loginMember?.role != getString(R.string.USER)) {
                        val intent = Intent(activity, RegisterActivty::class.java)
                        val bundle = Bundle()
                        bundle.putBoolean(getString(R.string.is_admin), true)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        //    Utility.fade(activity)
                    } else {
                        binding.llParent.snackbar(getString(R.string.admin_only), Snackbar.LENGTH_LONG)
                    }
                }
//                13 -> {
//                    binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
//                    return@setOnClickListener
//                    Utility.movetoFragment(activity, TourVideoFragment())
//                }
                12 -> {
                    Utility.movetoFragment(activity, MyContactListFragment())
                }
            }
        }
        return convertView!!
    }
}




    private fun isAdmin(): Boolean {
        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        var isAdmin = false
        if (!loginuser.isNullOrEmpty()) {
            val loginMem = Gson().fromJson<Member>(loginuser, Member::class.java)
            if (((loginMem.role == getString(R.string.LOCAL_ADMIN)))) {
                isAdmin = true
            } else if (((loginMem.role == getString(R.string.SUB_ADMIN)))) {
                isAdmin = true
            } else if ((loginMem.role == getString(R.string.super_admin))) {
                isAdmin = true
            }
        }
        return isAdmin
    }

    inner class SharedLocationViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var iconText: TextView = v.findViewById(R.id.icon_text)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile)
        var cardViewRecentList: CardView = v.findViewById(R.id.card_view_recent_list)
    }

    inner class SharedProfileAdapter internal constructor(private val list: ArrayList<Member>) : RecyclerView.Adapter<SharedLocationViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedLocationViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.shared_list_profle, parent, false)
            return SharedLocationViewHolder(view)
        }

        override fun onBindViewHolder(holder: SharedLocationViewHolder, position: Int) {
            val member = list[position]
            holder.tvName.text = member.firstName
            if (!holder.tvName.text.isNullOrEmpty()) {
                holder.iconText.text = member.firstName.substring(0, 1)
            }

            applyProfilePicture(holder, member)

            val arrayId = member.sharingId?.split(',')
            val memId = Guru.getString(getString(R.string.member_id), "")
            var isShare = false
            if (arrayId != null) {
                for (id in arrayId) {
                    if (memId == id) {
                        isShare = true
                        break
                    }
                }
            }
            when {
                duplicateIds.contains(member.id) -> {
                    holder.cardViewRecentList.setCardBackgroundColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.colorTree1))
                }
                !isShare -> {
                    holder.cardViewRecentList.setCardBackgroundColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.bg_gray))
                }
                isShare -> {
                    holder.cardViewRecentList.setCardBackgroundColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.white))
                }
            }

            holder.imgProfile.setOnClickListener {
                if (!member.id.isNullOrEmpty()) {
                    moveToProfileDetail(member)
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("CheckResult")
        private fun applyProfilePicture(holder: SharedLocationViewHolder, member: Member) {

            if (!TextUtils.isEmpty(member.profilePic)) {
                holder.imgProfile.isClickable = true
                val url = resources.getString(R.string.base_url_thumb) + member.profilePic
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "url: $url")
                }

                try {
                    Glide.with(activity!!).load(url).placeholder(R.drawable.user_face).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(holder.imgProfile)
                } catch (e: Exception) {
                    e.message
                }
                holder.imgProfile.colorFilter = null
                holder.iconText.visibility = View.GONE

            } else {
                holder.imgProfile.isClickable = false
                holder.imgProfile.setImageResource(R.drawable.bg_circle)
                holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
                holder.iconText.visibility = View.VISIBLE
            }
        }
    }

    private fun moveToProfileDetail(member: Member) {
        Utility.startSweetProgress(activity, getString(R.string.MoveProfile), getString(R.string.loading))
        val intent = Intent(activity, ProfileDetailActivity::class.java)
        intent.putExtra(getString(R.string.member), member)
        startActivity(intent)
        //  fade(activity)
    }

    private fun getSharedProfileList() {
        val jsonObj = JSONObject()
        jsonObj.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
        jsonObj.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
        jsonObj.put(getString(R.string.id), Guru.getString(getString(R.string.member_id), ""))
        val updated = JsonParser().parse(jsonObj.toString()) as JsonObject
        filterViewModel.getSharedProfiles(updated)
    }

    override fun getMembers(response: SmartFilterResponse) {
        if (response.success) {
            if (response.members != null && response.members.size > 0) {

                sharedProfiles.clear()
                duplicateIds.clear()
                //startLocationService()
                sharedProfiles.addAll(response.members)

                for (member1 in response.membersharing) {
                    var isAdd = true
                    for (member2 in response.members) {
                        if (member1.id == member2.id) {
                            isAdd = false
                            break
                        }
                    }
                    if (isAdd) {
                        sharedProfiles.add(member1)
                    } else {
                        duplicateIds.add(member1.id)
                    }
                }
                if (sharedProfiles.size > 0) {
                    binding.lblShared.alpha = 1.0f
                    binding.tvAllShared.isClickable = true
                } else {
                    binding.lblShared.alpha = 0.25f
                    binding.tvAllShared.isClickable = false
                }

                sharedAdapter = SharedProfileAdapter(sharedProfiles)
                binding.lstSharedProfile.adapter = sharedAdapter
                binding.lstSharedProfile.alpha = 1.0f
//                binding.lblPrivate.visibility = View.GONE
            } else {
                setDefaultProfileList()
            }
        } else {
            binding.lblShared.alpha = 0.25f
            binding.tvAllShared.isClickable = false
            setDefaultProfileList()
        }
    }

    override suspend fun getFailure(message: String) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
        setDefaultProfileList()
    }

    private fun setDefaultProfileList() {

        binding.lstSharedProfile.alpha = 0.25f
//        binding.lblPrivate.visibility = View.VISIBLE
        try {
            sharedAdapter = SharedProfileAdapter(defaultProfiles)
            binding.lstSharedProfile.adapter = sharedAdapter

            if (ProcessMainClass.serviceIntent != null) {
                if (RestartServiceBroadcastReceiver.jobScheduler != null) {
                    RestartServiceBroadcastReceiver.jobScheduler.cancel(1)
                }
                activity?.stopService(ProcessMainClass.serviceIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
