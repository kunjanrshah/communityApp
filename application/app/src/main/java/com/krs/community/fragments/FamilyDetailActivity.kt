package com.krs.community.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.krs.community.fancygifdialoglib.FancyGifDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.*
import com.krs.community.adapter.AddFamilyHeadAdapter
import com.krs.community.adapter.AddMemberAdapter
import com.krs.community.adapter.ChangeFamilyHeadAdapter
import com.krs.community.adapter.LocationAdapter
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.IFamilyMembersListener
import com.krs.community.listeners.RefreshListListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.*
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.FamilyDetailViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class FamilyDetailActivity : AppCompatActivity(), KodeinAware, IFamilyMembersListener, LocationAdapter.SetLocationListner, RefreshListListener {



    var memId: String? = null

    var isFinish: Boolean = false
    val TAG = FamilyDetailActivity::class.java.simpleName
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private lateinit var rvDetail: RecyclerView
    private lateinit var llRoot: LinearLayout
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private var deletedId = ""
    override val kodein by kodein()
    private var setLocationDialog: DialogPlus? = null
    private lateinit var familyDetailViewModel: FamilyDetailViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private val familyDetailViewModelFactory: FamilyDetailViewModelFactory by instance<FamilyDetailViewModelFactory>()

    private var isShimmer: Boolean = true
    private var loginId: String? = null
    private var textMsg: String? = null
    var family = ArrayList<Member>()

    companion object {
        lateinit var members: ArrayList<Member>
        var headId: String? = null
        var addMemberDialog: DialogPlus? = null
        var addHeadDialog: DialogPlus? = null
        lateinit var mainHandler: Handler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this@FamilyDetailActivity, FamilyDetailActivity::class.simpleName)
        mApp.facebookAnalytics(this@FamilyDetailActivity, FamilyDetailActivity::class.simpleName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeStatusbarColor(this, R.color.colorPrimary, true)
        }
        mainHandler = Handler(Looper.getMainLooper())
        headId = intent.getStringExtra(getString(R.string.id))
        isFinish = intent.getBooleanExtra(getString(R.string.is_finish), false)
        memId = intent.getStringExtra(getString(R.string.member_id))
        profileDetailViewModel = ViewModelProvider(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        familyDetailViewModel = ViewModelProvider(this, familyDetailViewModelFactory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.mIFamilyMembersListener = this

        AppController.mApplication.connectionLiveData.observeForever {
            it?.let {
                if (it) {
                    setScreenLayout()
                } else {
                    setNoInternetLayout()
                }
            }
        }


        val member = Guru.getString(getString(R.string.loginMember), "")
        if (loginId.isNullOrEmpty() && member.isNullOrEmpty()) {

            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Just One Step more")
                    .setContentText("Please click on your name to enter your PIN")
                    .setConfirmText("Okay")
                    .setCustomImage(R.drawable.ic_app)
                    .showCancelButton(false)
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismiss()
                    }
                    .show()
        }
    }

    private fun setScreenLayout() {
        setContentView(R.layout.activity_family_detail)
        supportActionBar?.hide()
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container1)
        rvDetail = findViewById(R.id.rv_detail)
        llRoot = findViewById(R.id.ll_root)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        rvDetail.setHasFixedSize(true)
        rvDetail.layoutManager = mLayoutManager
        rvDetail.itemAnimator = DefaultItemAnimator()
        loginId = Guru.getString(getString(R.string.member_id), "")
        MyPermissionChecker.requestStoragePermission(this)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateAdapter)
    }

    private val updateAdapter = object : Runnable {
        override fun run() {
            getFamilyDetails()
            mainHandler.postDelayed(this, 1000 * 60 * 3)
        }
    }

    private fun setNoInternetLayout() {
        setContentView(R.layout.no_internet_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        setSupportActionBar(toolbar)
        supportActionBar?.show()
        supportActionBar?.title = resources.getString(R.string.app_name)
        val anim = AlphaAnimation(0f, 1f)
        anim.duration = 6000
        anim.repeatMode = AlphaAnimation.RESTART
        anim.repeatCount = Animation.INFINITE
        val imageView = findViewById<ImageView>(R.id.no_internet_image)
        imageView.animation = anim
        val retryButton = findViewById<AppCompatButton>(R.id.retry_button)
        retryButton.setOnClickListener { v: View? ->
            if (isNetworkConnected(this)) {
                setScreenLayout()
            }
        }
    }

    fun getFamilyDetails() {
        if (isShimmer) {
            isShimmer = false
            mShimmerViewContainer?.visibility = View.VISIBLE
            mShimmerViewContainer?.startShimmerAnimation()
        }
        val jsonObject = JSONObject()
        if (!loginId.isNullOrEmpty()) {
            jsonObject.put(getString(R.string.id), loginId)
        }
        jsonObject.put(getString(R.string.head_id), headId)
        val records = JsonParser().parse(jsonObject.toString()) as JsonObject
        familyDetailViewModel.getFamilyDetails(records)
    }

    private fun deleteFamilyMember(id: String) {
        mShimmerViewContainer?.startShimmerAnimation()
        mShimmerViewContainer?.visibility = View.VISIBLE
        val mJSONObject = JSONObject()
        mJSONObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
        mJSONObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
        mJSONObject.put(getString(R.string.member_id), id)
        deletedId = id
        val records = JsonParser().parse(mJSONObject.toString()) as JsonObject
        familyDetailViewModel.deleteMember(records)

        Handler().postDelayed({
            mShimmerViewContainer?.stopShimmerAnimation()
            mShimmerViewContainer?.visibility = View.GONE
        }, 4000)
    }

    override fun getFamilyMembers(data: FamilyDetailResponse) {
        mShimmerViewContainer?.stopShimmerAnimation()
        mShimmerViewContainer?.visibility = View.GONE

        if (data.success) {
            members = data.member as ArrayList<Member>

            if (members.size > 0) {
                family.clear()
                family.addAll(members.subList(1, members.size))
                if (family.size > 0) {
                    if (family[0].headId == "0") {
                        family.removeAt(0)
                    }
                }
            }
            createCardAdapter()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createCardAdapter() {
        if (family != null) {
            adapter = object : ParallaxRecyclerAdapter<Member>(family) {
                override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {

                    val member = family!![i]

                    (viewHolder as FamilyDetailViewHolder).tvName.text = "${member.firstName} ${member.fatherName} ${member.lastName}"
                    viewHolder.tvSubtext.text = member.relation

                    if (member.mobile.isNullOrEmpty()) {
                        viewHolder.tvMobile.text = getString(R.string.mobile_not_available)
                        viewHolder.ivMobile.visibility = View.GONE
                        viewHolder.tvMobile.setTextColor(ContextCompat.getColor(this@FamilyDetailActivity, R.color.gray_btn_bg_color))
                    } else {
                        viewHolder.ivMobile.visibility = View.VISIBLE
                        viewHolder.tvMobile.text = member.mobile
                        viewHolder.tvMobile.setTextColor(ContextCompat.getColor(this@FamilyDetailActivity, R.color.com_facebook_blue))
                    }

                    if (member.emailAddress.isNullOrEmpty()) {
                        viewHolder.ivEmail.visibility = View.GONE
                        viewHolder.tvEmail.text = getString(R.string.email_not_available)
                        viewHolder.tvEmail.setTextColor(ContextCompat.getColor(this@FamilyDetailActivity, R.color.gray_btn_bg_color))
                    } else {
                        viewHolder.tvEmail.setTextColor(ContextCompat.getColor(this@FamilyDetailActivity, R.color.red_btn_bg_color))
                        viewHolder.ivEmail.visibility = View.VISIBLE
                        viewHolder.tvEmail.text = member.emailAddress
                    }
                    try {
                        if (member.updatedDt.isNullOrEmpty() || member.updatedDt.contains(getString(R.string.zero_date))) {
                            viewHolder.tvUpdate.text = "Created " + Utility.changeDateFormat(member.createdDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                        } else {
                            viewHolder.tvUpdate.text = "Updated " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)

                    var imgStatus = R.drawable.ico_red
                    if (loginId == member.id) {
                        imgStatus = R.drawable.ico_blue
                    } else if (member.loginStatus == 1 && member.onlineStatus == 0) {
                        imgStatus = R.drawable.ico_pink
                    } else if (member.loginStatus == 0) {
                        imgStatus = R.drawable.ico_red
                    } else if (member.onlineStatus == 1) {
                        imgStatus = R.drawable.ico_green
                    }
                    try {
                        Glide.with(AppController.mApplication).load(imgStatus).thumbnail(0.5f).into(viewHolder.imgState)
                    } catch (e: Exception) {
                        e.message
                    }

                    val loginuser = Guru.getString(getString(R.string.loginMember), "")
                    var canDelete = false
                    if (!loginuser.isNullOrEmpty()) {
                        val loginMember = Gson().fromJson(loginuser, Member::class.java)
                        if (isAdmin() || (loginMember.headId == "0" && members[0].id == loginMember.id)) {
                            canDelete = true
                        }
                    }

                    if (member.headId == "0") {
                        canDelete = false
                    }

                    if (canDelete) {
                        viewHolder.swipe.setLockDrag(false)
                    } else {
                        viewHolder.swipe.setLockDrag(true)
                    }

                    if (!member.isExpired.isNullOrEmpty() && member.isExpired == "1") {
                        viewHolder.ll_data.visibility = View.GONE
                        viewHolder.imgexpired.visibility = View.VISIBLE
                        viewHolder.swipe.setBackgroundResource(R.drawable.round_corner_gray)
                    } else {
                        viewHolder.ll_data.visibility = View.VISIBLE
                        viewHolder.imgexpired.visibility = View.GONE
                        viewHolder.swipe.setBackgroundResource(R.drawable.round_corner_itemlist)
                    }

                    var code: String? = null
                    code = if (!member.memberCode.isNullOrEmpty() && member.memberCode.length > 5) {
                        member.memberCode.substring(0, 5)
                    } else {
                        member.memberCode
                    }
                    if (BuildConfig.FLAVOR == "yadav") {
                        viewHolder.tvCode.text = getString(R.string.yss) + code + "-" + member.id
                    } else {
                        viewHolder.tvCode.text = getMemberCode(code)
                    }

                    viewHolder.llMake.setOnClickListener {
                        if (isAdmin()) {
                            SweetAlertDialog(this@FamilyDetailActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                    .setTitleText("Make Family Head")
                                    .setContentText("Make sure you have fillup all the mandatory fields!")
                                    .setConfirmText("This Family")
                                    .setCancelText("New Family")
                                    .setCustomImage(R.drawable.ic_app)
                                    .showCancelButton(true)
                                    .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                                        sweetAlertDialog.dismissWithAnimation()
                                        if (isValidFamilyHead(member)) {
                                            val adapter: ChangeFamilyHeadAdapter = ChangeFamilyHeadAdapter(this@FamilyDetailActivity, profileDetailViewModel, member)
                                            addHeadDialog = DialogPlus.newDialog(this@FamilyDetailActivity)
                                                    .setAdapter(adapter)
                                                    .setGravity(Gravity.CENTER)
                                                    .setCancelable(false)
                                                    .setExpanded(false, 700)
                                                    .setContentBackgroundResource(R.drawable.popup_corner)
                                                    .create()
                                            addHeadDialog?.show()
                                        }
                                    }.setCancelClickListener {
                                        it.dismissWithAnimation()
                                        if (isValidFamilyHead(member)) {
                                            val adapter: AddFamilyHeadAdapter = AddFamilyHeadAdapter(this@FamilyDetailActivity, profileDetailViewModel, member)
                                            addHeadDialog = DialogPlus.newDialog(this@FamilyDetailActivity)
                                                    .setAdapter(adapter)
                                                    .setGravity(Gravity.CENTER)
                                                    .setCancelable(false)
                                                    .setExpanded(false, 700)
                                                    .setContentBackgroundResource(R.drawable.popup_corner)
                                                    .create()
                                            addHeadDialog?.show()
                                        }
                                    }
                                    .show()
                        } else {
                            llRoot.snackbar(getString(R.string.admin_only), Snackbar.LENGTH_LONG)
                        }
                    }

                    viewHolder.llDelete.setOnClickListener {
                        if (!loginId.isNullOrEmpty()) {
                            FancyGifDialog.Builder(this@FamilyDetailActivity)
                                    .setTitle(getString(R.string.you_sure))
                                    .setMessage(getString(R.string.wontbeRecover))
                                    .setPositiveBtnText(getString(R.string.yesdelete))
                                    .setPositiveBtnBackground(R.color.fancy_positive)
                                    .setNegativeBtnText(getString(R.string.no))
                                    .setNegativeBtnBackground(R.color.fancy_nagative)
                                    .setGifResource(R.drawable.gif_dialog)
                                    .isCancellable(false)
                                    .OnPositiveClicked {
                                        deleteFamilyMember(member.id)
                                    }
                                    .OnNegativeClicked {

                                    }
                                    .build()
                        } else {
                            llRoot.snackbar(getString(R.string.enter_pin), Snackbar.LENGTH_LONG)
                        }
                    }

                    viewHolder.boomMenuButton.clearBuilders()
                    for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                        val builder: TextInsideCircleButton.Builder? = getTextInsideCircleButtonBuilder()
                        builder?.listener {
                            if (it == 0) {

                                createMemberPDF(this@FamilyDetailActivity, member, profileDetailViewModel)

                                Handler().post {
                                    startSweetProgress(this@FamilyDetailActivity, getString(R.string._export) + " " + member.firstName + getString(R.string.sdetails), getString(R.string.please_wait))
                                }
                                Handler().postDelayed({
                                    hideSweetProgress()
                                }, 5000)


                            } else if (it == 1) {
                                Toast.makeText(this@FamilyDetailActivity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                                return@listener
                                /* val intent: Intent = Intent(this@FamilyDetailActivity, FamilyTreeListActivity::class.java)
                                 startActivity(intent)*/
                            } else if (it == 2) {
                                if (!member.mobile.isNullOrEmpty()) {
                                    sendWhatsAppMessage(this@FamilyDetailActivity, member.mobile, getString(R.string.install_app) + BuildConfig.APPLICATION_ID)
                                } else {
                                    Toast.makeText(this@FamilyDetailActivity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                                }
                            } else if (it == 3) {
                                val mBundle = Bundle()
                                mBundle.putSerializable(getString(R.string.member), member)
                                val intent = Intent(this@FamilyDetailActivity, QRCodeActivity::class.java)
                                intent.putExtras(mBundle)
                                startActivity(intent)
                                //  fade(this@FamilyDetailActivity)
                            } else if (it == 4) {
                                shareDetails(this@FamilyDetailActivity, viewHolder.tvName.text.toString(), member.mobile, member.emailAddress, member.area, member.address)
                            } else if (it == 5) {
                                val adapter = LocationAdapter(this@FamilyDetailActivity, member)
                                adapter.setLocationListner(this@FamilyDetailActivity)
                                setLocationDialog = DialogPlus.newDialog(this@FamilyDetailActivity)
                                        .setAdapter(adapter)
                                        .setGravity(Gravity.BOTTOM)
                                        .setCancelable(true)
                                        .setExpanded(true, 650)
                                        .setContentBackgroundResource(R.drawable.popup_top_corner)
                                        .create()
                                setLocationDialog?.show()
                            }
                        }
                        viewHolder.boomMenuButton.addBuilder(builder)
                    }
                    viewHolder.boomMenuButton.setOnClickListener {
                        viewHolder.boomMenuButton.boom()
                    }
                    applyClickEvents(viewHolder, member)
                    applyProfilePicture(viewHolder, member)
                }

                override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                    return FamilyDetailViewHolder(layoutInflater.inflate(R.layout.row_list_family_detail, viewGroup, false))
                }

                override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                    return (family!!.size)
                }
            }
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(this)
        rvDetail.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(R.layout.header_detail, rvDetail, false)
        val bmb = header.findViewById<BoomMenuButton>(R.id.bmb)
        val cancel = header.findViewById<ImageView>(R.id.img_cancel1)
        val login = header.findViewById<ImageView>(R.id.login)
        val imgMap = header.findViewById<ImageView>(R.id.img_map)
        var tvCode: TextView = header.findViewById(R.id.tv_code)
        if (!loginId.isNullOrEmpty()) {
            cancel.visibility = View.VISIBLE
            if (!memId.isNullOrEmpty() && memId.equals(loginId)) {
                imgMap.visibility = View.VISIBLE
            } else {
                imgMap.visibility = View.GONE
            }
            login.visibility = View.GONE
        } else {
            imgMap.visibility = View.GONE
            login.visibility = View.VISIBLE
            cancel.visibility = View.INVISIBLE
        }

        cancel.setOnClickListener {
            if (isFinish) {
                finish()
            } else {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        val member = members[0]

        var code: String? = null
        code = if (!member.memberCode.isNullOrEmpty() && member.memberCode.length > 5) {
            member.memberCode.substring(0, 5)
        } else {
            member.memberCode
        }
        if (BuildConfig.FLAVOR == "yadav") {
            tvCode.text = getString(R.string.yss) + code + "-" + member.id
        } else {
            tvCode.text = getMemberCode(code)
        }

        imgMap.setOnClickListener {
            //displaySnackBarWithBottomMargin(rvDetail, getString(R.string.coming_soon))
            val intent = Intent(this, MapTrackingActivity::class.java)
            intent.putExtra("head_id", headId)
            startActivity(intent)
            //   fade(this)
        }

        login.setOnClickListener {
            FancyGifDialog.Builder(this)
                    .setTitle(getString(R.string.you_sure))
                    .setMessage(getString(R.string.LogoutComApp) + " " + getString(R.string.app_name) + " App")
                    .setPositiveBtnText(getString(R.string.yes))
                    .setPositiveBtnBackground(R.color.fancy_positive)
                    .setNegativeBtnText(getString(R.string.no))
                    .setNegativeBtnBackground(R.color.fancy_nagative)
                    .setGifResource(R.drawable.gif_dialog)
                    .isCancellable(false)
                    .OnPositiveClicked {
                        Guru.clear()
                        Guru.putBoolean(getString(R.string.policy), true)
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)
                        this.finish()
                        //   fade(this)
                    }
                    .OnNegativeClicked {
                    }
                    .build()
            true
        }
        val tvName: TextView = header.findViewById(R.id.tv_name1)
        tvName.text = member.firstName + " " + member.fatherName + " " + member.lastName
        //  val mApp = applicationContext as AppController
        // val strDemo =  mApp.stringTranslateAPI(member.firstName);
        val iconText: TextView = header.findViewById(R.id.icon_text1)
        iconText.text = tvName.text.substring(0, 1)
        val tvMobile: TextView = header.findViewById(R.id.tv_mobile)
        tvMobile.text = member.mobile
        tvMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }

        val imgProfile: ImageView = header.findViewById(R.id.icon_profile1)
        imgProfile.setOnClickListener {
            val path = getString(R.string.base_url_original) + "" + member.profilePic
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "path: $path")
            }

            openImageDialog(this, path)
        }
        if (!TextUtils.isEmpty(member.profilePic)) {
            imgProfile.isClickable = true
            val url = resources.getString(R.string.base_url_thumb) + member.profilePic
            Glide.with(this@FamilyDetailActivity).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(imgProfile)
            imgProfile.colorFilter = null
            iconText.visibility = View.GONE

        } else {
            imgProfile.isClickable = false
            imgProfile.setImageResource(R.drawable.bg_circle)
            imgProfile.setColorFilter(getRandomMaterialColor(this@FamilyDetailActivity, "400"))
            iconText.visibility = View.VISIBLE
        }

        val tvArea: TextView = header.findViewById(R.id.tv_area)
        tvArea.text = member.area + " " + member.city

        val tvEmail: TextView = header.findViewById(R.id.tv_email)
        tvEmail.text = member.emailAddress

        val tvAddr: TextView = header.findViewById(R.id.tv_addr)
        tvAddr.text = member.address

        val imgState: ImageView = header.findViewById(R.id.img_state)
        var icStatus = R.drawable.ico_red
        if (loginId == member.id) {
            icStatus = R.drawable.ico_blue
        } else if (member.loginStatus == 1 && member.onlineStatus == 0) {
            icStatus = R.drawable.ico_pink
        } else if (member.loginStatus == 0) {
            icStatus = R.drawable.ico_red
        } else if (member.onlineStatus == 1) {
            icStatus = R.drawable.ico_green
        }
        try {
            Glide.with(AppController.mApplication).load(icStatus).thumbnail(0.5f).into(imgState)
        } catch (e: Exception) {
            e.message
        }

        val tvLabel: TextView = header.findViewById(R.id.tv_label)
        tvLabel.text = getString(R.string.fmilyList)

        val tvTitle: TextView = header.findViewById(R.id.tv_title)
        tvTitle.text = getString(R.string.FamilyDetail) + " (${members.size})"

        bmb.clearBuilders()
        for (i in 0 until bmb.piecePlaceEnum.pieceNumber()) {
            val builder: TextInsideCircleButton.Builder? = getTextInsideCircleButtonBuilder()
            builder?.listener {
                if (it == 0) {
                    createMemberPDF(this@FamilyDetailActivity, member, profileDetailViewModel)
                    Handler().post {
                        startSweetProgress(this@FamilyDetailActivity, getString(R.string._export) + " " + member.firstName + getString(R.string.sdetails), getString(R.string.please_wait))
                    }
                    Handler().postDelayed({
                        hideSweetProgress()
                    }, 5000)
                } else if (it == 1) {
                    Toast.makeText(this@FamilyDetailActivity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                    return@listener
                    /* val intent: Intent = Intent(this@FamilyDetailActivity, FamilyTreeListActivity::class.java)
                     startActivity(intent)*/
                } else if (it == 2) {
                    if (!member.mobile.isNullOrEmpty()) {
                        sendWhatsAppMessage(this@FamilyDetailActivity, member.mobile, getString(R.string.install_app) + BuildConfig.APPLICATION_ID)
                    } else {
                        Toast.makeText(this@FamilyDetailActivity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                    }
                } else if (it == 3) {
                    val mBundle = Bundle()
                    mBundle.putSerializable(getString(R.string.member), member)
                    val intent = Intent(this@FamilyDetailActivity, QRCodeActivity::class.java)
                    intent.putExtras(mBundle)
                    startActivity(intent)
                    //   fade(this@FamilyDetailActivity)
                } else if (it == 4) {
                    shareDetails(this@FamilyDetailActivity, tvName.text.toString(), member.mobile, member.emailAddress, member.area, member.address)
                } else if (it == 5) {
                    val adapter = LocationAdapter(this@FamilyDetailActivity, member)
                    adapter.setLocationListner(this@FamilyDetailActivity)
                    setLocationDialog = DialogPlus.newDialog(this@FamilyDetailActivity)
                            .setAdapter(adapter)
                            .setGravity(Gravity.BOTTOM)
                            .setCancelable(true)
                            .setExpanded(true, 600)
                            .setContentBackgroundResource(R.drawable.popup_top_corner)
                            .create()
                    setLocationDialog?.show()
                }
            }
            bmb.addBuilder(builder)
        }
        bmb.setOnClickListener {
            bmb.boom()
        }
        val llFamilyHead: LinearLayout = header.findViewById(R.id.ll_family_head)
        llFamilyHead.setOnClickListener {
            if (!loginId.isNullOrEmpty()) {
                val intent = Intent(this, ProfileDetailActivity::class.java)
                intent.putExtra(getString(R.string.member), members[0])
                startActivity(intent)
                //  fade(this)
            } else {
                if (!member.profilePassword.isNullOrEmpty()) {
                    if (loginId == member.id) {
                        textMsg = getString(R.string.exitDetails)
                    } else if (member.loginStatus == 1 && member.onlineStatus == 0) {
                        textMsg = getString(R.string.exitDetails)
                    } else if (member.loginStatus == 0) {
                        textMsg = getString(R.string.EnterDetails)
                    } else if (member.onlineStatus == 1) {
                        textMsg = getString(R.string.exitDetails)
                    }
                    var gif: Int = R.drawable.gif_dialog
                    var title = ""
                    if (textMsg == getString(R.string.exitDetails)) {
                        title = "Hey " + member.firstName + ", You haven't logout properly"
                    } else {
                        title = "Hey " + member.firstName + ", Welcome"
                    }

                    FancyGifDialog.Builder(this)
                            .setTitle(title)
                            .setMessage("To $textMsg Please type your PIN")
                            .setPositiveBtnText(getString(R.string.yes))
                            .setPositiveBtnBackground(R.color.fancy_positive)
                            .setNegativeBtnText(getString(R.string.no))
                            .setNegativeBtnBackground(R.color.fancy_nagative)
                            .setGifResource(gif)
                            .isCancellable(false)
                            .OnPositiveClicked {
                                val intent = Intent(this@FamilyDetailActivity, PinViewActivity::class.java)
                                intent.putExtra(getString(R.string.member), member)
                                startActivity(intent)
                            }
                            .OnNegativeClicked {
                            }
                            .build()
                    true
                } else {
                    llRoot.snackbar(getString(R.string.pinFoundDetail), Snackbar.LENGTH_LONG)
                }
            }
        }
        val tvAdd: TextView = header.findViewById(R.id.tv_add)

        if ((!loginId.isNullOrEmpty() && member.id == loginId) || isAdmin()) {
            tvAdd.visibility = View.VISIBLE
        } else {
            tvAdd.visibility = View.GONE
        }
        tvAdd.setOnClickListener {

            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Add Member")
                    .setContentText("Do you want to add new member?")
                    .setConfirmText("Add New")
                    .setCancelText("Add Exist")
                    .setCustomImage(R.drawable.ic_app)
                    .showCancelButton(true)
                    .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                        sweetAlertDialog.dismissWithAnimation()
                        val intent = Intent(this, ProfileDetailActivity::class.java)

                        val member = Member()
                        member.subCastId = members[0].subCastId
                        member.gotraId = members[0].gotraId
                        if (members[0].gender.toString().toLowerCase() == "male") {
                            member.fatherName = members[0].firstName
                        }
                        member.nativePlaceId = members[0].nativePlaceId
                        member.localCommunityId = members[0].localCommunityId
                        member.subCommunityId = members[0].subCommunityId

                        intent.putExtra(getString(R.string.member), member)
                        intent.putExtra(getString(R.string.head_id), members[0].id)
                        startActivity(intent)
                    }.setCancelClickListener {
                        it.dismissWithAnimation()
                        if (isAdmin()) {
                            val adapter: AddMemberAdapter = AddMemberAdapter(this, profileDetailViewModel, member.id)
                            addMemberDialog = DialogPlus.newDialog(this)
                                    .setAdapter(adapter)
                                    .setGravity(Gravity.CENTER)
                                    .setCancelable(false)
                                    .setExpanded(true, 800)
                                    .setContentBackgroundResource(R.drawable.popup_corner)
                                    .create()
                            addMemberDialog?.show()
                        } else {
                            Snackbar.make(llRoot, R.string.admin_only, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    .show()
        }
        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, rvDetail)
        adapter.data = family
        rvDetail.adapter = adapter
    }

    private fun isAdmin(): Boolean {
        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        var isAdmin = false
        if (!loginuser.isNullOrEmpty()) {
            val loginMem = Gson().fromJson<Member>(loginuser, Member::class.java)
            if (((loginMem.role == getString(R.string.LOCAL_ADMIN) && loginMem.localCommunityId == members[0].localCommunityId))) {
                isAdmin = true
            } else if (((loginMem.role == getString(R.string.SUB_ADMIN) && loginMem.subCommunityId == members[0].subCommunityId))) {
                isAdmin = true
            } else if ((loginMem.role == getString(R.string.super_admin))) {
                isAdmin = true
            }
        }
        return isAdmin
    }

    /* override fun onBackPressed() {
         super.onBackPressed()
         if (!newHeadId.isNullOrEmpty()) {
             setResult(102)
         }
         finish()
     }*/

    @SuppressLint("CheckResult")
    private fun applyProfilePicture(holder: FamilyDetailViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            holder.imgProfile.isClickable = true
            val url = resources.getString(R.string.base_url_thumb) + member.profilePic
            Glide.with(this@FamilyDetailActivity).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.imgProfile.isClickable = false
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(this@FamilyDetailActivity, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyClickEvents(holder: FamilyDetailViewHolder, member: Member) {
        holder.tvMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + holder.tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }

        holder.imgProfile.setOnClickListener { view ->
            try {
                val path = getString(R.string.base_url_original) + "" + member.profilePic
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "path: $path")
                }

                openImageDialog(this@FamilyDetailActivity, path)
            } catch (e: Exception) {
                e.message
            }
        }

        holder.llContent.setOnClickListener {
            if (!loginId.isNullOrEmpty()) {
                val intent = Intent(this@FamilyDetailActivity, ProfileDetailActivity::class.java)
                intent.putExtra(getString(R.string.member), member)
                startActivity(intent)
                // fade(this@FamilyDetailActivity)
            } else {
                if (!member.profilePassword.isNullOrEmpty()) {

                    if (loginId == member.id) {
                        textMsg = "Exit"
                    } else if (member.loginStatus == 1 && member.onlineStatus == 0) {
                        textMsg = "Exit"
                    } else if (member.loginStatus == 0) {
                        textMsg = "Enter"
                    } else if (member.onlineStatus == 1) {
                        textMsg = "Exit"
                    }

                    var title = ""
                    if (textMsg == getString(R.string.exitDetails)) {
                        title = member.firstName + ", You haven't logged out properly"
                    } else {
                        title = "Hey " + member.firstName + ", Welcome"
                    }

                    FancyGifDialog.Builder(this@FamilyDetailActivity)
                            .setTitle(title)
                            .setMessage("To $textMsg Please type your PIN")
                            .setPositiveBtnText(getString(R.string.yes))
                            .setPositiveBtnBackground(R.color.fancy_positive)
                            .setNegativeBtnText(getString(R.string.no))
                            .setNegativeBtnBackground(R.color.fancy_nagative)
                            .setGifResource(R.drawable.gif_dialog)
                            .isCancellable(false)
                            .OnPositiveClicked {
                                val intent = Intent(this@FamilyDetailActivity, PinViewActivity::class.java)
                                intent.putExtra(getString(R.string.member), member)
                                startActivity(intent)
                            }
                            .OnNegativeClicked {

                            }
                            .build()
                    true

                } else {
                    llRoot.snackbar(getString(R.string.pinFoundDetail), Snackbar.LENGTH_LONG)
                }
            }
        }
    }

    private fun isValidFamilyHead(member: Member): Boolean {
        if (member.mobile.length < 10) {
            Snackbar.make(llRoot, R.string.mobile_not_found, Snackbar.LENGTH_LONG).show()
            return false
        } else if (member.emailAddress.isNullOrEmpty() || !isEmailValid(member.emailAddress)) {
            Snackbar.make(llRoot, R.string.email_not_available, Snackbar.LENGTH_LONG).show()
            return false
        } else if (member.cityId.isNullOrEmpty() || member.cityId == "0") {
            Snackbar.make(llRoot, "Select City", Snackbar.LENGTH_LONG).show()
            return false
        } else if (member.stateId.isNullOrEmpty() || member.stateId == "0") {
            Snackbar.make(llRoot, "Select State", Snackbar.LENGTH_LONG).show()
            return false
        } else if (member.nativePlaceId.isNullOrEmpty() || member.nativePlaceId == "0") {
            Snackbar.make(llRoot, "Select Native", Snackbar.LENGTH_LONG).show()
            return false
        } else if (member.address.isNullOrEmpty()) {
            Snackbar.make(llRoot, "Enter Address", Snackbar.LENGTH_LONG).show()
            return false
        } else if (member.birthDate.isNullOrEmpty()) {
            Snackbar.make(llRoot, "Enter BirthDate", Snackbar.LENGTH_LONG).show()
            return false
        } else if (BuildConfig.FLAVOR == "medk" && (member.gotraId.isNullOrEmpty() || member.gotraId == "0")) {
            Snackbar.make(llRoot, "Select Gotra", Snackbar.LENGTH_LONG).show()
            return false
        } else if (member.gender.isNullOrEmpty()) {
            Snackbar.make(llRoot, "Select Gender", Snackbar.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }

    internal class FamilyDetailViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById<View>(R.id.tv_name) as TextView
        var tvSubtext: TextView = v.findViewById(R.id.tv_subtext)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvUpdate: TextView = v.findViewById(R.id.tv_update)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.bmb1)
        var frontLayout: FrameLayout = v.findViewById(R.id.front_layout)
        var iconText: TextView = v.findViewById(R.id.icon_text1)
        var llDelete: LinearLayout = v.findViewById(R.id.ll_delete)
        var llMake: LinearLayout = v.findViewById(R.id.ll_make)
        var swipe: SwipeRevealLayout = v.findViewById(R.id.swipe)
        var llMobile: LinearLayout = v.findViewById(R.id.llMobile)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile1)
        val imgState: ImageView = v.findViewById(R.id.img_state)
        var ll_email: LinearLayout = v.findViewById(R.id.ll_email)
        var ivMobile: ImageView = v.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = v.findViewById(R.id.iv_email)
        var llContent: LinearLayout = v.findViewById(R.id.ll_content)
        var tvCode: TextView = v.findViewById(R.id.tv_code)
        var ll_data: LinearLayout = v.findViewById(R.id.ll_data)
        var imgexpired: ImageView = v.findViewById(R.id.imgexpired)
    }

    override fun getMessage(response: DeleteProfileResponse) {
        if (response.success) {
            var member1: Member? = null
            for (member in members) {
                if (member.id == deletedId) {
                    member1 = member
                    break
                }
            }

            if (member1 != null) {
                members.remove(member1)
                if (members.size > 0) {
                    family.clear()
                    family.addAll(members.subList(1, members.size))
                    if (family.size > 0) {
                        if (family[0].headId == "0") {
                            family.removeAt(0)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                createCardAdapter()
            }
        }
        mShimmerViewContainer?.stopShimmerAnimation()
        mShimmerViewContainer?.visibility = View.GONE
        llRoot.snackbar(response.message, Snackbar.LENGTH_LONG)
    }

    override suspend fun getFailure(message: String) {
        Coroutines.main {
            mShimmerViewContainer?.stopShimmerAnimation()
            mShimmerViewContainer?.visibility = View.GONE
            llRoot.snackbar(message, Snackbar.LENGTH_LONG)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkConnected(this)) {
            supportActionBar?.hide()
        }

        Handler().postDelayed({
            hideKeyboard(this)
        }, 1000)
        if (isNetworkConnected(this)) {
            mainHandler.post(updateAdapter)
        }


    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.show()
    }

    override fun cancelDialog() {
        setLocationDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        //unregisterNetworkBroadcastForNougat()
        familyDetailViewModel.cancelAllJobs()
    }

    override fun refreshList() {
        getFamilyDetails()
    }

}