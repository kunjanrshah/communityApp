package com.krs.community.fragments

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
import android.widget.RelativeLayout
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
import com.krs.community.R
import com.krs.community.activity.FavoriteProfileActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.activity.RegisterActivty
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentDashboardBinding
import com.krs.community.listeners.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Utility
import com.krs.community.utils.Utility.fade
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.SmartFilterViewModelFactory
import com.smarteist.autoimageslider.DefaultSliderView
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class DashboardFragment : Fragment(), KodeinAware, ByFilterListener {

    private var sharedProfiles = ArrayList<Member>()
    private var duplicateIds = ArrayList<String>()
    private var defaultProfiles = ArrayList<Member>()
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var filterViewModel: SmartFilterViewModel
    private val filterViewModelFactory: SmartFilterViewModelFactory by instance()
    private var isTouch = false
    private lateinit var layoutManager: LinearLayoutManager
    private var sharedAdapter: SharedProfileAdapter? = null

    private val duration = 10L
    private val pixelsToMove = 30
    private val mHandler = Handler(Looper.getMainLooper())
    private lateinit var member: Member
    var SCROLLING_RUNNABLE: Runnable = object : Runnable {
        override fun run() {
            binding.lstSharedProfile.smoothScrollBy(pixelsToMove, 0)
            mHandler.postDelayed(this, duration)
        }
    }

    override val kodein by kodein()

    companion object {
        val TAG = DashboardFragment::class.java.simpleName
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.FirebaseAnalytics(context, DashboardFragment::class.simpleName)
        mApp.FacebookAnalytics(context, DashboardFragment::class.simpleName)

        filterViewModel = ViewModelProvider(this, filterViewModelFactory).get(SmartFilterViewModel::class.java)
        filterViewModel.mByFilterListener = this

        Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        binding.gridView.isExpanded = true
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.SWAP)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
        binding.imageSlider.scrollTimeInSec = 3 //set scroll delay in seconds :
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

        setSliderViews()
        setDefaultProfileList()
        getSharedProfileList()
        binding.gridView.adapter = MenuAdapter(activity!!)
        binding.tvAllShared.setOnClickListener { v: View? -> Utility.movetoFragment(activity, SharedLocationFragment()) }
        binding.tvAllNews.setOnClickListener { v: View? ->
            binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
            return@setOnClickListener
            Utility.movetoFragment(activity, NewsListFragment())
        }
        Utility.changeStatusbarColor(activity, R.color.white, false)
        //setRecyclerViewScrollListener()

        val loginMember = Guru.getString(getString(R.string.loginMember), "")
        member = Gson().fromJson(loginMember, Member::class.java)
        return binding.root
    }

    private fun getSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        if (intent.resolveActivity(activity!!.packageManager) != null) {
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
                Log.d(TAG, "data: " + result[0])

                val searchFragment = SearchListFragment()
                val bundle = Bundle()
                bundle.putString("keyword", result[0])
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
            val sliderView = DefaultSliderView(activity)
            when (i) {
                0 -> sliderView.setImageDrawable(R.drawable.ic_launcher_background)
                1 -> sliderView.imageUrl = "https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
                2 -> sliderView.imageUrl = "https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"
                3 -> sliderView.imageUrl = "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
            }
            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            sliderView.description = "The quick brown fox jumps over the lazy dog.\n" + "Jackdaws love my big sphinx of quartz. " + (i + 1)
            sliderView.setOnSliderClickListener { sliderView1: SliderView? ->
                binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                return@setOnSliderClickListener
                Utility.movetoFragment(activity, NewsListFragment())
            }
            binding.imageSlider.addSliderView(sliderView)
        }
    }

    internal inner class MenuViewHolder {
        var image: ImageView? = null
        var textView: TextView? = null
    }

    internal inner class MenuAdapter(private val mContext: Context) : BaseAdapter() {

        override fun getCount(): Int {
            return 14
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
                convertView.tag = menuViewHolder
            } else {
                menuViewHolder = convertView.tag as MenuViewHolder
            }
            val imgs: TypedArray = resources.obtainTypedArray(R.array.main_menu_imgs)
            val mainMenu = resources.getStringArray(R.array.main_menu)

            menuViewHolder.image?.setImageResource(imgs.getResourceId(position, -1))
            menuViewHolder.textView?.text = mainMenu[position]
            convertView?.setOnClickListener { v: View? ->
                when (position) {
                    0 -> Utility.movetoFragment(activity, BrowseByCityFragment())
                    1 -> {
                        val mBundle = Bundle()
                        mBundle.putSerializable(getString(R.string.member), member)
                        val intent1 = Intent(activity, QRCodeActivity::class.java)
                        intent1.putExtras(mBundle)
                        startActivity(intent1)
                        Utility.fade(activity)
                    }
                    2 -> Utility.movetoFragment(activity, SearchByDistanceFragment())
                    3 -> Utility.movetoFragment(activity, MatrimonyFragment())

                    4 -> {
                        binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                        return@setOnClickListener
                        Utility.movetoFragment(activity, NewsListFragment())
                    }
                    5 -> {
                        val mIntent = Intent(activity, FavoriteProfileActivity::class.java)
                        startActivity(mIntent)
                        Utility.fade(activity)
                    }
                    6 -> Utility.movetoFragment(activity, AdminsFragment())
                    7 -> {
                        if (member.role != getString(R.string.User)) {
                            Utility.movetoFragment(activity, NonActivesFragment())
                        } else {
                            binding.llParent.snackbar(getString(R.string.admin_only), Snackbar.LENGTH_LONG)
                        }
                    }
                    8 -> {
                        binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                        return@setOnClickListener
                        Utility.movetoFragment(activity, ShareEventFragment())
                    }
                    9 -> {
                        // binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                        //startActivity(Intent(activity, ActivityDebugTools::class.java))
                        Utility.movetoFragment(activity, DocumentsFragment())
                    }

                    10 -> {
                        binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                        return@setOnClickListener
                        Utility.movetoFragment(activity, PaytmFragment())
                    }

                    11 -> {
                        if (member.role != getString(R.string.User)) {
                            val intent = Intent(activity, RegisterActivty::class.java)
                            val bundle = Bundle()
                            bundle.putBoolean(getString(R.string.is_logged_in), false)
                            intent.putExtras(bundle)
                            startActivity(intent)
                            Utility.fade(activity)
                        } else {
                            binding.llParent.snackbar(getString(R.string.admin_only), Snackbar.LENGTH_LONG)
                        }
                    }
                    12 -> {
                        binding.llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
                        return@setOnClickListener
                        Utility.movetoFragment(activity, TourVideoFragment())
                    }
                    13 -> {
                        Utility.movetoFragment(activity, MyContactListFragment())
                    }
                }
            }
            return convertView!!
        }
    }

    inner class SharedLocationViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var iconText: TextView = v.findViewById(R.id.icon_text)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile)
        var iconContainer: RelativeLayout = v.findViewById(R.id.icon_container)
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
            holder.iconText.text = member.firstName.substring(0, 1)
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

            /* holder.cardViewRecentList.setOnClickListener {
                 moveToProfileDetail()
             }*/

            /*holder.iconContainer.setOnClickListener {
                moveToProfileDetail()
            }*/
            /*holder.imgProfile.setOnClickListener {
                moveToProfileDetail()
            }*/
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("CheckResult")
        private fun applyProfilePicture(holder: SharedLocationViewHolder, member: Member) {

            if (!TextUtils.isEmpty(member.profilePic)) {
                holder.imgProfile.isClickable = true
                val url = resources.getString(R.string.base_url_thumb) + member.profilePic
                Log.d(TAG, "url: $url")
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

    private fun moveToProfileDetail() {
        Utility.startSweetProgress(activity, getString(R.string.MoveProfile), getString(R.string.loading))
        val intent = Intent(activity, ProfileDetailActivity::class.java)
        intent.putExtra(getString(R.string.member), member)
        startActivity(intent)
        fade(activity)
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

                sharedAdapter = SharedProfileAdapter(sharedProfiles)
                binding.lstSharedProfile.adapter = sharedAdapter
                binding.lblShared.visibility = View.VISIBLE
                binding.lblShared.alpha = 1.0f
                binding.lstSharedProfile.alpha = 1.0f
                binding.lblPrivate.visibility = View.GONE
                binding.tvAllShared.isClickable = true
            } else {
                setDefaultProfileList()
            }
        } else {
            setDefaultProfileList()
        }
    }

    override suspend fun getFailure(message: String) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
        setDefaultProfileList()
    }

    private fun setDefaultProfileList() {
        binding.lblShared.alpha = 0.25f
        binding.lstSharedProfile.alpha = 0.25f
        binding.tvAllShared.isClickable = false
        binding.lblPrivate.visibility = View.VISIBLE
        binding.lblShared.visibility = View.VISIBLE
        try {
            sharedAdapter = SharedProfileAdapter(defaultProfiles)
            binding.lstSharedProfile.adapter = sharedAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
