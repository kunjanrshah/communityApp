package com.krs.community.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.dewinjm.monthyearpicker.MonthFormat
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.listeners.ByFilterListener
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.CommitteeViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodelfactory.CommiteeViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CommitteeFragment : Fragment(), KodeinAware, ByFilterListener, RoomMemberListener, LocationAdapter.SetLocationListner {
    var lstMember: ArrayList<Member> = ArrayList()
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private var spLocalCommunity: JRSpinner? = null
    private var spCommittee: JRSpinner? = null
    private var spDesignation: JRSpinner? = null
    private var tvStart: TextView? = null
    private var tvEnd: TextView? = null
    private var edtName: EditText? = null
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var txtRegion: TextView
    private lateinit var txtDuration: TextView
    private lateinit var txtCommittee: TextView
    private lateinit var txtDesignation: TextView
    private lateinit var llRegion: LinearLayout
    private lateinit var llDuration: LinearLayout
    private lateinit var llCommittee: LinearLayout
    private lateinit var llDesignation: LinearLayout

    var yearSelected = 0
    var monthSelected = 0
    private var isStart = false
    private lateinit var yearPickerDialogFragment: MonthYearPickerDialogFragment
    private lateinit var committeeViewModel: CommitteeViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val roomMemberViewModelFactory: RoomMemberViewModelFactory by instance<RoomMemberViewModelFactory>()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private val commiteeViewModelFactory: CommiteeViewModelFactory by instance<CommiteeViewModelFactory>()
    private var setLocationDialog: DialogPlus? = null
    private var endDate: String? = null
    private var startDate: String? = null
    private var strEnd: String? = null
    private var strStart: String? = null
    private lateinit var snackbar: Snackbar
    private lateinit var frameRoot: FrameLayout
    private lateinit var ivNotFound: ImageView
    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_committee, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, CommitteeFragment::class.simpleName)
        mApp.facebookAnalytics(context, CommitteeFragment::class.simpleName)

        frameRoot = root.findViewById(R.id.frameRoot)
        ivNotFound = root.findViewById(R.id.iv_not_found)
        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container)
        committeeViewModel = ViewModelProvider(this, commiteeViewModelFactory).get(CommitteeViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberViewModelFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProvider(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)

        committeeViewModel.filterListener = this
        roomMemberViewModel.mRoomMemberListener = this
        val calendar = Calendar.getInstance()
        yearSelected = calendar[Calendar.YEAR]
        monthSelected = calendar[Calendar.MONTH]
        createDialog()
        lstMember.clear()

        adapter = object : ParallaxRecyclerAdapter<Member>(lstMember) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {
                val holder = viewHolder as ListViewHolder
                holder.viewLine.visibility = View.VISIBLE
                holder.llDesignation.visibility = View.VISIBLE
                holder.llCommittee.visibility = View.VISIBLE
                holder.llRegion.visibility = View.VISIBLE

                val member = lstMember[position]
                val name = member.firstName
                holder.tvName.text = name
                var count = member.memberCount
                if (count != 0) {
                    count += 1
                }
                holder.badge.setNumber(count)
                var code: String? = null
                code = if (!member.memberCode.isNullOrEmpty() && member.memberCode.length > 5) {
                    member.memberCode.substring(0, 5)
                } else {
                    member.memberCode
                }
                if (BuildConfig.FLAVOR == "yadav") {
                    holder.tvCode.text = getString(R.string.yss) + code + "/" + member.id
                } else {
                    holder.tvCode.text = getMemberCode(code)
                }

                Coroutines.io {
                    var native = ""
                    val lastname = committeeViewModel.getLastName(Integer.parseInt(member.subCastId.toString()))
                    val localComm = committeeViewModel.getLocalCommunityName(Integer.parseInt(member.localCommunityId.toString()))
                    val committeeName = committeeViewModel.getCommitteeName(Integer.parseInt(member.committeeId.toString()))
                    val desigName = committeeViewModel.getDesignationName(Integer.parseInt(member.designationId.toString()))
                    if (!member.nativePlaceId.isNullOrEmpty()) {
                        native = committeeViewModel.getNativeById(Integer.parseInt(member.nativePlaceId.trim()))
                    }
                    Coroutines.main {
                        holder.tvName.text = "$name ${member.fatherName} $lastname"
                        holder.tvRegion.text = localComm
                        holder.tvCommitee.text = committeeName
                        holder.tvDesignation.text = desigName
                        holder.tvNative.text = "Native: $native"
                    }
                }

                if (member.status == "2") {
                    holder.ivVerify.visibility = View.VISIBLE
                } else {
                    holder.ivVerify.visibility = View.GONE
                }
                if (!name.isNullOrEmpty()) {
                    holder.iconText.text = name.substring(0, 1)
                }

                holder.tvArea.text = member.area

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

                if (member.headId.equals("0")) {
                    holder.tvRole.text = resources.getString(R.string.Family_Head)
                } else {
                    holder.tvRole.text = resources.getString(R.string.Member)
                }
                val loginuser = Guru.getString(getString(R.string.loginMember), "")
                val loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)
                val arrayId = loginMember?.sharingId?.split(',')
                if (arrayId != null) {
                    if (arrayId.contains(member.id)) {
                        holder.imgLocation.visibility = View.VISIBLE
                    } else {
                        holder.imgLocation.visibility = View.GONE
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

                holder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {
                            createMemberPDF(activity as AppCompatActivity, member, profileDetailViewModel)
                            Handler().post(Runnable {
                                Utility.startSweetProgress(activity, "Exporting ${member.firstName}'s Details", getString(R.string.please_wait))
                            })
                            Handler().postDelayed({
                                Utility.hideSweetProgress()
                            }, 5000)
                        } else if (it == 1) {
                            Toast.makeText(activity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                            return@listener
                            val intent: Intent = Intent(activity, FamilyTreeListActivity::class.java)
                            startActivity(intent)
                        } else if (it == 2) {
                            if (!member.mobile.isNullOrEmpty()) {
                                Utility.sendWhatsAppMessage(activity as AppCompatActivity, member.mobile, getString(R.string.install_app) + BuildConfig.APPLICATION_ID)
                            } else {
                                Toast.makeText(activity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                            }
                        } else if (it == 3) {
                            val mBundle = Bundle()
                            mBundle.putSerializable(getString(R.string.member), member)
                            val intent: Intent = Intent(activity, QRCodeActivity::class.java)
                            intent.putExtras(mBundle)
                            startActivity(intent)
                            //  Utility.fade(activity)
                        } else if (it == 4) {
                            shareDetails(activity, viewHolder.tvName.text.toString(), member.mobile, member.emailAddress, viewHolder.tvArea.text.toString(), member.address)
                        } else if (it == 5) {
                            val adapter: LocationAdapter = LocationAdapter(context as AppCompatActivity, member)
                            adapter.setLocationListner(this@CommitteeFragment)
                            setLocationDialog = DialogPlus.newDialog(context)
                                    .setAdapter(adapter)
                                    .setGravity(Gravity.BOTTOM)
                                    .setCancelable(true)
                                    .setExpanded(true, 600)
                                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                                    .create()
                            setLocationDialog?.show()
                        }
                    }
                    viewHolder.boomMenuButton.addBuilder(builder)
                }
                holder.boomMenuButton.setOnClickListener { v: View? -> holder.boomMenuButton.boom() }
                holder.swipe.setLockDrag(true)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position)
                applyImportant(viewHolder, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int { //return lstFilters.size();
                return lstMember.size
            }
        }

        val layoutManager = LinearLayoutManager(activity)
        val listCommittee: RecyclerView = root.findViewById(R.id.listCommittee)
        listCommittee.layoutManager = layoutManager
        listCommittee.itemAnimator = DefaultItemAnimator()
        listCommittee.setHasFixedSize(true)
        snackbar = Snackbar.make(frameRoot, getString(R.string.check_network), Snackbar.LENGTH_INDEFINITE)
        val header = LayoutInflater.from(activity).inflate(R.layout.header_committees, container, false)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        txtRegion = header.findViewById(R.id.txt_region)
        txtDuration = header.findViewById(R.id.txt_duration)
        txtCommittee = header.findViewById(R.id.txt_committee)
        txtDesignation = header.findViewById(R.id.txt_designation)
        llRegion = header.findViewById(R.id.ll_region)
        llDuration = header.findViewById(R.id.ll_duration)
        llCommittee = header.findViewById(R.id.ll_committee)
        llDesignation = header.findViewById(R.id.ll_designation)

        val imgRegionClose = header.findViewById<ImageView>(R.id.img_region_close)
        imgRegionClose.setOnClickListener { v: View? ->
            llRegion.visibility = View.GONE
            spLocalCommunity?.setText("")
            getUsersInCommittee()
        }

        val imgDurationClose = header.findViewById<ImageView>(R.id.img_duration_close)
        imgDurationClose.setOnClickListener { v: View? ->
            llDuration.visibility = View.GONE
            strStart = null
            getUsersInCommittee()
        }

        val imgCommitteeClose = header.findViewById<ImageView>(R.id.img_committee_close)
        imgCommitteeClose.setOnClickListener { v: View? ->
            llCommittee.visibility = View.GONE
            spCommittee?.setText("")
            getUsersInCommittee()
        }

        val imgDesignationClose = header.findViewById<ImageView>(R.id.img_designation_close)
        imgDesignationClose.setOnClickListener { v: View? ->
            llDesignation.visibility = View.GONE
            spDesignation?.setText("")
            getUsersInCommittee()
        }

        val filter = header.findViewById<ImageView>(R.id.filter)
        filter.setOnClickListener { v: View? -> openFilter() }
        adapter.setParallaxHeader(header, listCommittee)
        listCommittee.adapter = adapter

        val date = Date()
        val dateFormat = SimpleDateFormat("YYYY")
        val year = dateFormat.format(date)

        startDate = "$year-01"
        endDate = "$year-12"
        var month = DateFormatSymbols().months[0]
        strStart = String.format("%s  %s", month, year)
        month = DateFormatSymbols().months[11]
        strEnd = String.format("%s  %s", month, year)
        txtDuration.text = "$strStart - $strEnd"

        AppController.mApplication.connectionLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                if (snackbar.isShown) {
                    snackbar.dismiss()
                }
                DashboardActivity.binding.space.visibility = View.VISIBLE
                ivNotFound.visibility = View.GONE
                getUsersInCommittee()
            } else {
                shimmerFrameLayout.stopShimmerAnimation()
                shimmerFrameLayout.visibility = View.GONE
                DashboardActivity.binding.space.visibility = View.GONE
                snackbar.show()
            }
        })

        return root
    }

    private fun applyImportant(holder: ListViewHolder, member: Member) {

        roomMemberViewModel.getRoomMember(Integer.parseInt(member.id)).observe(activity as AppCompatActivity, androidx.lifecycle.Observer {
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

    private fun applyClickEvents(holder: ListViewHolder, position: Int) {
        holder.imgProfile.setOnClickListener {
            try {
                val path = getString(R.string.base_url_original) + "" + lstMember.get(position).profilePic
                Log.d("CommiteeList", "path: $path")
                openImageDialog(activity as AppCompatActivity, path)
            } catch (e: Exception) {
                e.message
            }
        }

        holder.iconImp.setOnClickListener {

            val member: Member = lstMember.get(position)
            var flag = true
            roomMemberViewModel.getRoomMember(Integer.parseInt(member.id)).observe(activity as AppCompatActivity, androidx.lifecycle.Observer {
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
        holder.messageContainer.setOnClickListener {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMember[position])
            startActivity(intent)
            //   Utility.fade(activity)
        }

        holder.llData.setOnClickListener {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMember[position])
            startActivity(intent)
            //   Utility.fade(activity)
        }
    }

    private fun applyProfilePicture(holder: ListViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            val url = resources.getString(R.string.base_url_thumb) + member.profilePic
            Log.d("CommiteeList", "url: " + url)
            Glide.with(activity!!).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
            holder.imgProfile.isClickable = true
        } else {
            holder.imgProfile.isClickable = false
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun createDialog() {
        yearPickerDialogFragment = MonthYearPickerDialogFragment.getInstance(monthSelected, yearSelected, getString(R.string.app_name), MonthFormat.LONG)
    }

    private fun openFilter() {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.filter_committee)
        spLocalCommunity = dialog.findViewById<JRSpinner>(R.id.sp_region)
        spCommittee = dialog.findViewById<JRSpinner>(R.id.sp_committee)
        spDesignation = dialog.findViewById<JRSpinner>(R.id.sp_designation)
        tvStart = dialog.findViewById<TextView>(R.id.tv_start)
        tvEnd = dialog.findViewById<TextView>(R.id.tv_end)
        edtName = dialog.findViewById<EditText>(R.id.edt_name)
        val ivCancel = dialog.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        val btnSearch = dialog.findViewById<Button>(R.id.btn_search)
        btnSearch.setOnClickListener { v: View? ->
            getUsersInCommittee()
            dialog.dismiss()
        }
        dialog.show()

        Coroutines.main {

            val loginuser = Guru.getString(getString(R.string.loginMember), "")
            val loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)
            committeeViewModel.getLocalCommunity(Integer.parseInt(loginMember.subCommunityId)).observeForever {
                val list = ArrayList<String>()
                list.add(getString(R.string.select))
                list.addAll(it)
                spLocalCommunity?.setItems(list.toTypedArray())
                spLocalCommunity?.setExpandTint(R.color.black)
            }

            committeeViewModel.getCommitteeList().observeForever {
                val list = ArrayList<String>()
                list.add(getString(R.string.select))
                list.addAll(it)
                spCommittee?.setItems(list.toTypedArray())
                spCommittee?.setExpandTint(R.color.black)
            }

            committeeViewModel.getDesignation().observeForever {
                val list = ArrayList<String>()
                list.add(getString(R.string.select))
                list.addAll(it)
                spDesignation?.setItems(list.toTypedArray())
                spDesignation?.setExpandTint(R.color.black)
            }

        }

        tvStart?.setOnClickListener {
            isStart = true
            yearPickerDialogFragment.show(activity?.supportFragmentManager!!, null)
        }

        tvEnd?.setOnClickListener {
            isStart = false
            yearPickerDialogFragment.show(activity?.supportFragmentManager!!, null)
        }

        yearPickerDialogFragment.setOnDateSetListener { year, monthOfYear ->
            monthSelected = monthOfYear
            yearSelected = year
            val month = DateFormatSymbols().months[monthSelected]
            var str_month = ""
            monthSelected++
            if (monthSelected < 10) {
                str_month = "0$monthSelected"
            }
            if (isStart) {
                startDate = "$yearSelected-$str_month"
                strStart = String.format("%s  %s", month, yearSelected)
                tvStart?.text = strStart
            } else {
                endDate = "$yearSelected-$str_month"
                strEnd = String.format("%s  %s", month, yearSelected)
                tvEnd?.text = strEnd
            }
        }

        edtName?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dialog.dismiss()
                getUsersInCommittee()
                true
            } else {
                false
            }
        }
    }

    private fun getUsersInCommittee() {
        Utility.hideKeyboard(activity)
        val jsonObject = JSONObject()
        Coroutines.io {

            val lcomm = spLocalCommunity?.text
            if (!lcomm.isNullOrEmpty() && lcomm.toString() != getString(R.string.select)) {
                val id = committeeViewModel.getLocalCommunityId(lcomm.toString())
                jsonObject.put(getString(R.string.local_community_id), id)
                Coroutines.main {
                    llRegion.visibility = View.VISIBLE
                    txtRegion.text = lcomm
                }
            }

            val designation = spDesignation?.text
            if (!designation.isNullOrEmpty() && designation.toString().trim() != getString(R.string.select)) {
                Coroutines.main {
                    llDesignation.visibility = View.VISIBLE
                    txtDesignation.text = designation
                }

                val id = committeeViewModel.getDesignationId(designation.toString().trim())
                jsonObject.put(getString(R.string.designation_id), id)
            }

            val commitee = spCommittee?.text
            if (!commitee.isNullOrEmpty() && commitee.toString().trim() != getString(R.string.select)) {

                Coroutines.main {
                    llCommittee.visibility = View.VISIBLE
                    txtCommittee.text = commitee
                }
                val id = committeeViewModel.getCommitteeId(commitee.toString().trim())
                jsonObject.put(getString(R.string.committee_id), id)
            }

            if (strStart != null && strEnd != null) {
                Coroutines.main {
                    llDuration.visibility = View.VISIBLE
                    txtDuration.text = "$strStart - $strEnd"
                }
                jsonObject.put(getString(R.string.start_date), startDate)
                jsonObject.put(getString(R.string.end_date), endDate)
            }
            if (edtName != null && edtName!!.text.isNotEmpty()) {
                jsonObject.put(getString(R.string.str_search), edtName?.text?.trim())
            }
            if (jsonObject.length() > 0) {
                val jsonObject1 = JSONObject()
                jsonObject1.put(getString(R.string.start), AppController.mApplication.start)
                jsonObject1.put(getString(R.string.length), AppController.mApplication.length)
                jsonObject1.put(getString(R.string.filter_by), jsonObject)

                Coroutines.main {
                    lstMember.clear()
                    adapter.notifyDataSetChanged()
                    shimmerFrameLayout.startShimmerAnimation()
                    shimmerFrameLayout.visibility = View.VISIBLE
                    ivNotFound.visibility = View.GONE
                }
                val updated = JsonParser().parse(jsonObject1.toString()) as JsonObject
                committeeViewModel.getUsersInCommittee(updated)
            } else {
                lstMember.clear()
                Coroutines.main {
                    adapter.notifyDataSetChanged()
                    ivNotFound.visibility = View.VISIBLE
                    frameRoot.snackbar(getString(R.string.select_filter), Snackbar.LENGTH_SHORT)
                }
            }

        }
    }

    override fun getMembers(response: SmartFilterResponse) {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility = View.GONE
        Utility.hideKeyboard(activity)
        if (response.success) {
            lstMember.clear()
            lstMember.addAll(response.members)
            adapter.notifyDataSetChanged()
            if (lstMember.size > 0) {
                ivNotFound.visibility = View.GONE
            } else {
                ivNotFound.visibility = View.VISIBLE
            }
        } else {
            ivNotFound.visibility = View.VISIBLE
            frameRoot.snackbar(getString(R.string.NoRecordList), Snackbar.LENGTH_SHORT)
        }
    }

    inner class ListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var iconImp: ImageView = itemView.findViewById(R.id.icon_star)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.bmb1)
        var iconText: TextView = itemView.findViewById(R.id.icon_text)
        var tvUpdate: TextView = itemView.findViewById(R.id.tv_update)
        var imgLocation: ImageView = itemView.findViewById(R.id.img_location)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvArea: TextView = itemView.findViewById(R.id.tv_area)
        val tvMobile: TextView = itemView.findViewById(R.id.tv_mobile)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        var imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
        var llMobile: LinearLayout = v.findViewById(R.id.ll_mobile)
        var ll_email: LinearLayout = itemView.findViewById(R.id.ll_email)
        var ivMobile: ImageView = itemView.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = itemView.findViewById(R.id.iv_email)
        val viewLine: View = itemView.findViewById(R.id.view_line)
        val tvRegion: TextView = itemView.findViewById(R.id.tv_region)
        val tvCommitee: TextView = itemView.findViewById(R.id.tv_commitee)
        val tvDesignation: TextView = itemView.findViewById(R.id.tv_designation)
        val llRegion: LinearLayout = itemView.findViewById(R.id.ll_region)
        val llCommittee: LinearLayout = itemView.findViewById(R.id.ll_committee)
        val llDesignation: LinearLayout = itemView.findViewById(R.id.ll_designation)
        var ivVerify: ImageView = itemView.findViewById(R.id.iv_verify)
        var badge: NotificationBadge = itemView.findViewById(R.id.badge)
        var messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
        var llData: LinearLayout = itemView.findViewById(R.id.ll_data)
        var tvCode: TextView = itemView.findViewById(R.id.tv_code)
        var tvNative: TextView = itemView.findViewById(R.id.tv_native)
        var swipe: SwipeRevealLayout = itemView.findViewById(R.id.swipe)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {

    }

    override suspend fun getFailure(message: String) {

    }

    override fun cancelDialog() {
        setLocationDialog?.dismiss()
    }
}