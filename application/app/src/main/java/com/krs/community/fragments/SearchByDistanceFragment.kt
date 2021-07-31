package com.krs.community.fragments

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.LocationAdapter
import com.krs.community.app.AppController
import com.krs.community.app.NotificationBadge
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.ByDistanceListener
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.ByDistanceModel
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.checkFineLocationPermission
import com.krs.community.viewmodel.ByDistanceViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodelfactory.ByDistanceViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchByDistanceFragment : Fragment(), KodeinAware, ByDistanceListener, Listener, LocationData.AddressCallBack, ParallaxRecyclerAdapter.OnLoadMore, RoomMemberListener, LocationAdapter.SetLocationListner {

    lateinit var recyclerView: RecyclerView
    private lateinit var imgMap: ImageView
    private val lstMembers = ArrayList<Member>()
    override val kodein by kodein()
    var TAG = SearchByDistanceFragment::class.java.simpleName
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var llRoot: FrameLayout
    private lateinit var rbtnHome: RadioButton
    private lateinit var rbtnOffice: RadioButton
    private lateinit var rbtnUser: RadioButton
    private lateinit var rbtnAll: RadioButton
    private lateinit var edtKm: EditText
    private lateinit var nearBy: String
    private lateinit var tvRecords: TextView
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var getLocationDetail: GetLocationDetail
    private var currLat = MutableLiveData<Double>()
    private var currLng = MutableLiveData<Double>()
    private var isCallAPI = false
    private var snackbar: Snackbar? = null
    private lateinit var distance: ByDistanceModel
    var request: LocationRequest? = null
    internal lateinit var mByDistanceViewModel: ByDistanceViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel

    private val byDistanceViewModelFactory: ByDistanceViewModelFactory by instance<ByDistanceViewModelFactory>()
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private val roomMemberFactory: RoomMemberViewModelFactory by instance<RoomMemberViewModelFactory>()
    var memberId: String? = null
    var loginMem: Member? = null
    private var byDistanceAdapter: ParallaxRecyclerAdapter<Member>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragmnet_search_by_distance, container, false)

        if (checkFineLocationPermission(activity)) {
            getLocationDetail = GetLocationDetail(this, activity)
            val request = LocationRequest()
            request.interval = Utility.INTERVAL
            request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            easyWayLocation = EasyWayLocation(activity, request, false, this)
        }


        (activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.SearchDistance)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        }

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, SearchByDistanceFragment::class.simpleName)
        mApp.facebookAnalytics(context, SearchByDistanceFragment::class.simpleName)
        profileDetailViewModel = ViewModelProvider(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        mByDistanceViewModel = ViewModelProvider(this, byDistanceViewModelFactory).get(ByDistanceViewModel::class.java)
        mByDistanceViewModel.mByDistanceListener = this
        roomMemberViewModel.mRoomMemberListener = this

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container)
        llRoot = root.findViewById(R.id.ll_root)
        recyclerView = root.findViewById(R.id.recycler_view)
        imgMap = root.findViewById(R.id.iv_map)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        createCardAdapter(recyclerView)

        AppController.mApplication.start = 0
        DashboardActivity.stop = false
        isCallAPI = true
        callDistanceAPI()


        memberId = Guru.getString(getString(R.string.member_id), "")

        val loginMember = Guru.getString(getString(R.string.loginMember), "")
        loginMem = Gson().fromJson(loginMember, Member::class.java)


        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        if (Utility.checkFineLocationPermission(activity)) {
            easyWayLocation.startLocation() //calculateDistance()
        } else {
            Utility.requestFineLocationPermission(activity as AppCompatActivity)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        if (Utility.checkFineLocationPermission(activity)) {
            easyWayLocation.endUpdates()
        } else {
            Utility.requestFineLocationPermission(activity as AppCompatActivity)
        }
    }

    private fun createCardAdapter(recyclerView: RecyclerView) {

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(R.layout.header_nearby, recyclerView, false)

        rbtnHome = header.findViewById(R.id.rbtnHome)
        rbtnOffice = header.findViewById(R.id.rbtnOffice)
        rbtnUser = header.findViewById(R.id.rbtnUser)
        rbtnAll = header.findViewById(R.id.rbtnAll)
        tvRecords = header.findViewById(R.id.tv_total)
        val imgHeaderMap = header.findViewById<ImageView>(R.id.img_map)
        imgHeaderMap.setOnClickListener {
            Toast.makeText(activity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
            /* val intent = Intent(activity, MapTrackingActivity::class.java)
             intent.putExtra("head_id", memberId)
             startActivity(intent)
             Utility.fade(activity)*/
        }

        rbtnHome.setOnClickListener { v ->
            rbtnHome.isChecked = true
            rbtnOffice.isChecked = false
            rbtnUser.isChecked = false
            rbtnAll.isChecked = false
        }

        rbtnOffice.setOnClickListener { v ->
            rbtnHome.isChecked = false
            rbtnOffice.isChecked = true
            rbtnUser.isChecked = false
            rbtnAll.isChecked = false
        }

        rbtnUser.setOnClickListener { v ->
            rbtnHome.isChecked = false
            rbtnOffice.isChecked = false
            rbtnUser.isChecked = true
            rbtnAll.isChecked = false
        }

        rbtnAll.setOnClickListener { v ->
            rbtnHome.isChecked = false
            rbtnOffice.isChecked = false
            rbtnUser.isChecked = false
            rbtnAll.isChecked = true
        }

        rbtnAll.setOnClickListener { v ->
            rbtnHome.isChecked = false
            rbtnOffice.isChecked = false
            rbtnUser.isChecked = false
            rbtnAll.isChecked = true
        }


        val imgCancel = header.findViewById<ImageView>(R.id.img_cancel)
        imgCancel.setOnClickListener {
            Utility.movetoFragment(activity, DashboardFragment())
        }

        edtKm = header.findViewById(R.id.edtKm)
        edtKm.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                isCallAPI = true
                DashboardActivity.stop = false
                callDistanceAPI()
            }
            false
        }

        val btnSearch = header.findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener { v ->
            isCallAPI = true
            DashboardActivity.stop = false
            callDistanceAPI()
        }

        byDistanceAdapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {
                val viewHolder: DistanceViewHolder = viewHolder as DistanceViewHolder
                val member = lstMembers[i]
                viewHolder.tvName.text = member.firstName

                mByDistanceViewModel.getLastNamebyId(member.subCastId).observeForever {
                    viewHolder.tvName.text = member.firstName + " " + member.fatherName + " " + it
                }

                mByDistanceViewModel.getCityNamebyId(member.cityId).observeForever {
                    viewHolder.tvArea.text = member.area + " " + it
                }

                Coroutines.io {
                    if (!member.nativePlaceId.isNullOrEmpty()) {
                        val native = mByDistanceViewModel.getNativeById(Integer.parseInt(member.nativePlaceId.trim()))
                        Coroutines.main {
                            viewHolder.tvNative.text = "Native: $native"
                        }
                    }
                }

                Coroutines.io {
                    if (!member.localCommunityId.isNullOrEmpty()) {
                        val local = mByDistanceViewModel.getLocalCommById(member.localCommunityId.trim())
                        Coroutines.main {
                            viewHolder.tvRegion.text = local
                        }
                    }
                }



                /*viewHolder.tvEmail.text = member.emailAddress
                viewHolder.tvMobile.text = member.mobile*/

                if (member.mobile.isEmpty()) {
                    viewHolder.tvMobile.text = getString(R.string.mobile_not_available)
                    viewHolder.ivMobile.visibility = View.GONE
                    viewHolder.tvMobile.setTextColor(getColor(activity as AppCompatActivity, R.color.gray_btn_bg_color))
                } else {
                    viewHolder.ivMobile.visibility = View.VISIBLE
                    viewHolder.tvMobile.text = member.mobile
                    viewHolder.tvMobile.setTextColor(getColor(activity as AppCompatActivity, R.color.com_facebook_blue))
                }

                if (member.emailAddress.isNullOrEmpty()) {
                    viewHolder.ivEmail.visibility = View.GONE
                    viewHolder.tvEmail.text = getString(R.string.email_not_available)
                    viewHolder.tvEmail.setTextColor(getColor(activity as AppCompatActivity, R.color.gray_btn_bg_color))
                } else {
                    viewHolder.tvEmail.setTextColor(getColor(activity as AppCompatActivity, R.color.red_btn_bg_color))
                    viewHolder.ivEmail.visibility = View.VISIBLE
                    viewHolder.tvEmail.text = member.emailAddress
                }

                if (member.gender.equals("Male")) {
                    viewHolder.ivGender.setBackgroundResource(R.drawable.male)
                } else {
                    viewHolder.ivGender.setBackgroundResource(R.drawable.female)
                }

                var count = member.memberCount
                if (count != 0) {
                    count += 1
                }
                viewHolder.badge.setNumber(count)

                var code: String? = null
                code = if (!member.memberCode.isNullOrEmpty() && member.memberCode.length > 5) {
                    member.memberCode.substring(0, 5)
                } else {
                    member.memberCode
                }
                if (BuildConfig.FLAVOR == "yadav") {
                    viewHolder.tvCode.text = getString(R.string.yss) + code + "/" + member.id
                } else {
                    viewHolder.tvCode.text = getMemberCode(code)
                }

                if (BuildConfig.FLAVOR == "ghanchi") {
                    viewHolder.viewLine.visibility = View.VISIBLE
                    viewHolder.tvRegion.visibility = View.VISIBLE
                    viewHolder.tvLabel.visibility = View.VISIBLE
                } else {
                    viewHolder.viewLine.visibility = View.GONE
                    viewHolder.tvRegion.visibility = View.GONE
                    viewHolder.tvLabel.visibility = View.GONE
                }

                if (member.headId.equals("0")) {
                    viewHolder.tvRole.text = resources.getString(R.string.Family_Head)
                } else {
                    viewHolder.tvRole.text = resources.getString(R.string.Member)
                }

                viewHolder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {
                            val profileDetailFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
                            val profileDetailViewModel = ViewModelProvider(activity as AppCompatActivity, profileDetailFactory).get(ProfileDetailViewModel::class.java)
                            createMemberPDF(activity as AppCompatActivity, member, profileDetailViewModel)
                            Handler().post {
                                Utility.startSweetProgress(activity, "Exporting ${member.firstName}'s Details", getString(R.string.please_wait))
                            }
                            /* Handler().postDelayed({
                                 Utility.hideSweetProgress()
                             }, 5000)*/
                        } else if (it == 1) {
                            Toast.makeText(activity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                            return@listener
                            val intent: Intent = Intent(activity as AppCompatActivity, FamilyTreeListActivity::class.java)
                            startActivity(intent)

                        } else if (it == 2) {
                            if (!member.mobile.isNullOrEmpty()) {
                                val toNumber = "+91" + member.mobile
                                val text = "Install your Community App\n" + "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                                Utility.sendWhatsAppMessage(activity as AppCompatActivity, toNumber, text)
                            }

                        } else if (it == 3) {
                            val mBundle = Bundle()
                            mBundle.putSerializable(getString(R.string.member), member)
                            val intent: Intent = Intent(activity as AppCompatActivity, QRCodeActivity::class.java)
                            intent.putExtras(mBundle)
                            startActivity(intent)
                            //  Utility.fade(activity as AppCompatActivity)
                        } else if (it == 4) {
                            shareDetails(activity as AppCompatActivity, viewHolder.tvName.text.toString(), member.mobile.toString(), member.emailAddress.toString(), viewHolder.tvArea.text.toString(), member.address.toString())
                        } else if (it == 5) {
                            val adapter = LocationAdapter(activity as AppCompatActivity, member)
                            adapter.setLocationListner(this@SearchByDistanceFragment)
                            val setLocationDialog = DialogPlus.newDialog(activity as AppCompatActivity)
                                    .setAdapter(adapter)
                                    .setGravity(Gravity.BOTTOM)
                                    .setCancelable(true)
                                    .setExpanded(true, 600)
                                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                                    .create()
                            setLocationDialog.show()

                        }
                    }
                    viewHolder.boomMenuButton.addBuilder(builder)
                }

                viewHolder.boomMenuButton.setOnClickListener {
                    viewHolder.boomMenuButton.boom()
                }

                viewHolder.tvHome.visibility = View.GONE
                viewHolder.llHome.visibility = View.GONE
                viewHolder.tvOffice.visibility = View.GONE
                viewHolder.llOffice.visibility = View.GONE
                viewHolder.tvUser.visibility = View.GONE
                viewHolder.llUser.visibility = View.GONE

                if (nearBy == "Home") {
                    viewHolder.llHome.visibility = View.VISIBLE
                    viewHolder.tvHome.visibility = View.VISIBLE
                    viewHolder.tvHome.text = getString(R.string.homeDistance)
                    viewHolder.tvHomeDist.text = getDistance(member.distance)

                    viewHolder.llHome.setOnClickListener {
                        if (!member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()) {
                            Utility.showDirections(activity, member.homeLat.toDouble(), member.homeLng.toDouble(), "${member.firstName}'s Home")
                        } else {
                            Toast.makeText(activity, "Home location not found!", Toast.LENGTH_LONG).show()
                        }
                    }


                } else if (nearBy == "Office") {
                    viewHolder.llOffice.visibility = View.VISIBLE
                    viewHolder.tvOffice.visibility = View.VISIBLE
                    viewHolder.tvOffice.text = getString(R.string.Officedistance)
                    viewHolder.tvOfficeDist.text = getDistance(member.distance)
                    val params = viewHolder.llOffice.layoutParams as LinearLayout.LayoutParams
                    // params.setMargins(0, 20, 0, 0)
                    viewHolder.llOffice.layoutParams = params

                    viewHolder.llOffice.setOnClickListener {
                        if (!member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()) {
                            Utility.showDirections(activity, member.officeLat.toDouble(), member.officeLng.toDouble(), "${member.firstName}'s Office")
                        } else {
                            Toast.makeText(activity, "Office location not found!", Toast.LENGTH_LONG).show()
                        }
                    }

                }/*else if(nearBy.equals("User")){
                    viewHolder.llUser.visibility=View.VISIBLE
                    viewHolder.tvUser.visibility=View.VISIBLE
                    viewHolder.tvUser.text=getString(R.string.userDetails)
                    viewHolder.tvUserDist.text=getDistance(member.distance)
                }*/ else if (nearBy == "All") {
                    try {
                        val nearBy: List<String> = member.nearBy.split(",")
                        val elements: List<String> = member.distance.split(",")
                        for (count in nearBy.indices) {
                            if (nearBy[count].contains("home")) {
                                viewHolder.llHome.visibility = View.VISIBLE
                                viewHolder.tvHome.visibility = View.VISIBLE
                                viewHolder.tvHome.text = getString(R.string.homeDistance)
                                viewHolder.tvHomeDist.text = getDistance(elements[count].trim())

                                viewHolder.llHome.setOnClickListener {
                                    if (!member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()) {
                                        Utility.showDirections(activity, member.homeLat.toDouble(), member.homeLng.toDouble(), "${member.firstName}'s Home")
                                    } else {
                                        Toast.makeText(activity, "Home location not found!", Toast.LENGTH_LONG).show()
                                    }
                                }

                            } else if (nearBy[count].contains("office")) {
                                viewHolder.llOffice.visibility = View.VISIBLE
                                viewHolder.tvOffice.visibility = View.VISIBLE
                                viewHolder.tvOffice.text = "Office"
                                viewHolder.tvOfficeDist.text = getDistance(elements[count].trim())
                                val params = viewHolder.llOffice.layoutParams as LinearLayout.LayoutParams
                                params.setMargins(0, 10, 0, 0)
                                viewHolder.llOffice.layoutParams = params

                                viewHolder.llOffice.setOnClickListener {
                                    if (!member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()) {
                                        Utility.showDirections(activity, member.officeLat.toDouble(), member.officeLng.toDouble(), "${member.firstName}'s Office")
                                    } else {
                                        Toast.makeText(activity, "Office location not found!", Toast.LENGTH_LONG).show()
                                    }
                                }


                            } /*else if (nearBy[count].contains("user")) {
                                viewHolder.llUser.visibility = View.VISIBLE
                                viewHolder.tvUser.visibility = View.VISIBLE
                                viewHolder.tvUser.text = "User"
                                viewHolder.tvUserDist.text = getDistance(elements[count].trim())
                            }*/
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

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
                applyImportant(viewHolder, member)
                applyProfilePicture(viewHolder, member)
                applyClickEvents(viewHolder, i, member)

            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return DistanceViewHolder(layoutInflater.inflate(R.layout.list_distance, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }

        byDistanceAdapter?.setOnClickEvent { v, position ->
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMembers.get(position))
            startActivityForResult(intent, 101)
        }

        layoutManagerFixed.setHeaderIncrementFixer(header)
        byDistanceAdapter?.isShouldClipView = false
        byDistanceAdapter?.setParallaxHeader(header, recyclerView)
        byDistanceAdapter?.data = lstMembers
        byDistanceAdapter?.setContext(this)
        recyclerView.adapter = byDistanceAdapter
    }

    fun getDistance(dist: String): String {
        var distance = ""
        try {
            val index = dist.indexOf(".")
            distance = if (dist.length > (index + 3)) {
                dist.substring(0, (index + 3))
            } else {
                dist
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return "$distance KM"
    }

    internal class DistanceViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var tvArea: TextView = v.findViewById(R.id.tv_area)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvRole: TextView = v.findViewById(R.id.tv_role)

        var tvHome: TextView = v.findViewById(R.id.tv_home)
        var tvOffice: TextView = v.findViewById(R.id.tv_office)
        var tvUser: TextView = v.findViewById(R.id.tv_user)

        var tvHomeDist: TextView = v.findViewById(R.id.tv_home_dist)
        var tvOfficeDist: TextView = v.findViewById(R.id.tv_office_dist)
        var tvUserDist: TextView = v.findViewById(R.id.tv_user_dist)

        var llHome: LinearLayout = v.findViewById(R.id.ll_home)
        var llOffice: LinearLayout = v.findViewById(R.id.ll_office)
        var llUser: LinearLayout = v.findViewById(R.id.ll_user)

        var tvUpdate: TextView = v.findViewById(R.id.tv_update)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.bmb1)
        var iconImp: ImageView = v.findViewById(R.id.icon_star)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile1)
        var iconText: TextView = v.findViewById(R.id.icon_text1)
        var llMobile: LinearLayout = v.findViewById(R.id.ll_mobile)
        var ll_email: LinearLayout = v.findViewById(R.id.ll_email)

        var ivMobile: ImageView = v.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = v.findViewById(R.id.iv_email)
        var ivGender: ImageView = itemView.findViewById(R.id.iv_gender)

        var tvCode: TextView = itemView.findViewById(R.id.tv_code)
        var badge: NotificationBadge = itemView.findViewById(R.id.badge)
        var tvNative: TextView = itemView.findViewById(R.id.tv_native)

        var tvRegion: TextView = itemView.findViewById(R.id.tv_region)
        var viewLine: View = itemView.findViewById(R.id.view_line)
        var tvLabel: TextView = itemView.findViewById(R.id.tv_label)
    }

    private fun applyImportant(holder: DistanceViewHolder, member: Member) {

        roomMemberViewModel.getRoomMember(Integer.parseInt(member.id)).observe(activity as AppCompatActivity, Observer {
            try {
                if (it != null) {
                    holder.iconImp.setImageDrawable(ContextCompat.getDrawable(activity as AppCompatActivity, R.drawable.ic_star_black_24dp))
                    holder.iconImp.setColorFilter(getColor(activity as AppCompatActivity, R.color.icon_tint_selected))
                } else {
                    holder.iconImp.setImageDrawable(ContextCompat.getDrawable(activity as AppCompatActivity, R.drawable.ic_star_border_black_24dp))
                    holder.iconImp.setColorFilter(getColor(activity as AppCompatActivity, R.color.icon_tint_normal))
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        })
    }

    private fun applyClickEvents(holder: DistanceViewHolder, position: Int, member: Member) {

        holder.iconImp.setOnClickListener {

            val member: Member = lstMembers.get(position)
            var flag = true
            roomMemberViewModel.getRoomMember(Integer.parseInt(member.id)).observe(activity as AppCompatActivity, Observer {
                if (flag) {
                    flag = false
                    if (it != null) {
                        roomMemberViewModel.deleteRoomMember(Integer.parseInt(member.id))
                    } else {
                        roomMemberViewModel.insertRoomMember(getRoomMemberFromMember(member))
                    }
                }
            })
        }

        holder.tvMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + holder.tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }

        holder.imgProfile.setOnClickListener { view ->
            try {
                val path = getString(R.string.base_url_original) + "" + member.profilePic
                Log.d(TAG, "path: $path")
                openImageDialog(activity as AppCompatActivity, path)
            } catch (e: Exception) {
                e.message
            }
        }

    }

    private fun applyProfilePicture(holder: DistanceViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            holder.imgProfile.isClickable = true
            val url = resources.getString(R.string.base_url_thumb) + member.profilePic
            Glide.with(activity!!).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE

        } else {
            holder.imgProfile.isClickable = false
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun callDistanceAPI() {
        if (!DashboardActivity.stop) {

            Log.e("edtKm---", "" + edtKm.text.toString().trim())
            if (edtKm.text.toString().trim().isNotEmpty()) {
                DashboardActivity.stop = true
                lstMembers.clear()
                tvRecords.visibility = View.GONE
                byDistanceAdapter?.notifyDataSetChanged()
                nearBy = "Home"
                if (rbtnAll.isChecked) {
                    nearBy = "All"
                } else if (rbtnOffice.isChecked) {
                    nearBy = "Office"
                } else if (rbtnUser.isChecked) {
                    nearBy = "User"
                }

                distance = ByDistanceModel()
                distance.start = AppController.mApplication.start.toString()
                distance.length = AppController.mApplication.length.toString()
                distance.km = edtKm.text.toString().trim()
                distance.nearBy = nearBy
                distance.userId = Guru.getString(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                distance.accessToken = Guru.getString(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                distance.lat = currLat.toString()
                distance.lng = currLng.toString()
                imgMap.visibility = View.GONE

                if (AppController.mApplication.start == 0) {
                    mShimmerViewContainer.startShimmerAnimation()
                    mShimmerViewContainer.visibility = View.VISIBLE
                } else {
                    snackbar = Snackbar.make(recyclerView, getString(R.string.load_more), Snackbar.LENGTH_INDEFINITE)
                    snackbar?.show()
                }

                /*Handler().postDelayed({
                    if (lstMembers.size == 0) {
                        mShimmerViewContainer.stopShimmerAnimation()
                        mShimmerViewContainer.visibility = View.GONE
                        imgMap.visibility = View.VISIBLE
                    }
                }, 5000)*/

                currLng.observe(activity as AppCompatActivity, Observer {
                    distance.lat = currLat.value.toString()
                    distance.lng = currLng.value.toString()
                    if (currLat.value != null && currLat.value != 0.0 && currLng.value != null && currLng.value != 0.0 && isCallAPI) {
                        isCallAPI = false
                        if (loginMem?.role != activity?.getString(R.string.super_admin)) {
                            distance.sub_community_id = loginMem?.subCommunityId
                        }

                        mByDistanceViewModel.getUserByDistance(distance)
                    }
                })
            }
        }
    }

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            distance.start = (lstMembers.size + 1).toString()
            if (loginMem?.role != activity?.getString(R.string.super_admin)) {
                distance.sub_community_id = loginMem?.subCommunityId
            }
            mByDistanceViewModel.getUserByDistance(distance)
        }
    }

    override fun getMembers(response: ByDistanceResponse) {
        DashboardActivity.stop = false
        mShimmerViewContainer.stopShimmerAnimation()
        mShimmerViewContainer.visibility = View.GONE
        snackbar?.dismiss()
        if (response.success) {
            //  lstMembers.clear()
            lstMembers.addAll(response.member)
            byDistanceAdapter?.notifyDataSetChanged()

            recyclerView.layoutManager?.scrollToPosition(distance.start.toInt())
            if (response.member.size < AppController.mApplication.length) {
                DashboardActivity.stop = true
                Snackbar.make(llRoot, getString(R.string.EndRecordDistance), Snackbar.LENGTH_LONG).show()
            }

            if (lstMembers.size > 0) {
                tvRecords.text = getString(R.string.RecordDistance) + " " + response.totalRecords
                imgMap.visibility = View.GONE
                tvRecords.visibility = View.VISIBLE
            } else {
                tvRecords.visibility = View.GONE
                imgMap.visibility = View.VISIBLE
                DashboardActivity.stop = true
            }
        } else {
            tvRecords.visibility = View.GONE
            imgMap.visibility = View.VISIBLE
        }

        Utility.hideKeyboard(activity)
    }

    override suspend fun getFailure(message: String) {
        Log.d(TAG, "getFailure: $message")
        activity?.runOnUiThread {
            snackbar?.dismiss()
            if (mShimmerViewContainer.isAnimationStarted) {
                mShimmerViewContainer.stopShimmerAnimation()
            }
            mShimmerViewContainer.visibility = View.GONE
            imgMap.visibility = View.VISIBLE
            tvRecords.visibility = View.GONE
            DashboardActivity.stop = true
        }

        llRoot.snackbar(message, Snackbar.LENGTH_LONG)
        Utility.hideKeyboard(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EasyWayLocation.LOCATION_SETTING_REQUEST_CODE) {
            easyWayLocation.onActivityResult(resultCode)
        } else if (requestCode == 101 && resultCode == 102) {
            isCallAPI = true
            DashboardActivity.stop = false
            callDistanceAPI()
        }
    }

    override fun locationCancelled() {
    }

    override fun locationOn() {

    }

    override fun currentLocation(location: Location) {
        currLat.value = location.latitude
        currLng.value = location.longitude
        isCallAPI = true
    }

    override fun locationData(locationData: LocationData) {

    }

    override fun refreshList() {
        byDistanceAdapter?.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
