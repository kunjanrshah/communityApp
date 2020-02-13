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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.dewinjm.monthyearpicker.MonthFormat
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.LocationAdapter
import com.krs.community.entities.RoomMember
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.listeners.ByFilterListener
import com.krs.community.listeners.EditMemberListener
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
import java.util.*
import kotlin.collections.ArrayList


class CommitteeFragment : Fragment(), KodeinAware, ByFilterListener, RoomMemberListener,LocationAdapter.SetLocationListner {
    var lstMember: ArrayList<Member> = ArrayList()
    private lateinit var adapter:ParallaxRecyclerAdapter<Member>
    private lateinit var spLocalCommunity:JRSpinner
    private lateinit var spCommittee:JRSpinner
    private lateinit var spDesignation:JRSpinner
    private lateinit var tvStart:TextView
    private lateinit var tvEnd:TextView
    private lateinit var edtName:EditText
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var txtRegion:TextView
    private lateinit var txtDuration:TextView
    private lateinit var txtCommittee:TextView
    private lateinit var txtDesignation:TextView
    var yearSelected = 0
    var monthSelected = 0
    private var isStart=false
    private lateinit var yearPickerDialogFragment:MonthYearPickerDialogFragment
    private lateinit var committeeViewModel: CommitteeViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val roomMemberViewModelFactory: RoomMemberViewModelFactory by instance()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance()
    private val commiteeViewModelFactory: CommiteeViewModelFactory by instance()
    private var setLocationDialog: DialogPlus? = null

    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_committee, container, false)

        shimmerFrameLayout=root.findViewById(R.id.shimmer_view_container)
        committeeViewModel = ViewModelProviders.of(this, commiteeViewModelFactory).get(CommitteeViewModel::class.java)
        roomMemberViewModel = ViewModelProviders.of(this, roomMemberViewModelFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProviders.of(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)

        committeeViewModel.filterListener=this
        roomMemberViewModel.mRoomMemberListener = this

        val calendar = Calendar.getInstance()
        yearSelected = calendar[Calendar.YEAR]
        monthSelected = calendar[Calendar.MONTH]
        createDialog()
        lstMember.clear()
        adapter= object : ParallaxRecyclerAdapter<Member>(lstMember) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {
                val holder = viewHolder as ListViewHolder
                val member = lstMember[position]
                val name = member.firstName
                holder.tvName.text = name
                Coroutines.main {
                    val lastname = committeeViewModel.getLastName(Integer.parseInt(member.subCastId.toString()))
                    holder.tvName.text = "$name $lastname"
                }
                holder.iconText.text = name.substring(0, 1)
                holder.tvArea.text = member.area
                holder.tvEmail.text = member.emailAddress
                holder.tvMobile.text = member.mobile
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

                holder.tvUpdate.text = "updated " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)

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
                            val intent: Intent = Intent(activity, FamilyTreeListActivity::class.java)
                            startActivity(intent)
                        } else if (it == 2) {
                            if (!member.mobile.isNullOrEmpty()) {
                                Utility.sendWhatsappMessage(activity as AppCompatActivity, member.mobile, getString(R.string.install_app))
                            } else {
                                Toast.makeText(activity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                            }
                        } else if (it == 3) {
                            val mBundle = Bundle()
                            mBundle.putSerializable(getString(R.string.member), member)
                            val intent: Intent = Intent(activity, QRCodeActivity::class.java)
                            intent.putExtras(mBundle)
                            startActivity(intent)
                            Utility.fade(activity)
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
        val header = LayoutInflater.from(activity).inflate(R.layout.header_committees, container, false)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        txtRegion = header.findViewById<TextView>(R.id.txt_region)
        txtDuration = header.findViewById<TextView>(R.id.txt_duration)
        txtCommittee = header.findViewById<TextView>(R.id.txt_committee)
        txtDesignation = header.findViewById<TextView>(R.id.txt_designation)
        val llRegion = header.findViewById<LinearLayout>(R.id.ll_region)
        val llDuration = header.findViewById<LinearLayout>(R.id.ll_duration)
        val llCommittee = header.findViewById<LinearLayout>(R.id.ll_committee)
        val llDesignation = header.findViewById<LinearLayout>(R.id.ll_designation)
        val imgRegionClose = header.findViewById<ImageView>(R.id.img_region_close)
        imgRegionClose.setOnClickListener { v: View? ->
            llRegion.visibility = View.GONE
            getUsersInCommittee()
        }
        val imgDurationClose = header.findViewById<ImageView>(R.id.img_duration_close)
        imgDurationClose.setOnClickListener { v: View? ->
            llDuration.visibility = View.GONE
            getUsersInCommittee()
        }
        val imgCommitteeClose = header.findViewById<ImageView>(R.id.img_committee_close)
        imgCommitteeClose.setOnClickListener { v: View? ->
            llCommittee.visibility = View.GONE
            getUsersInCommittee()
        }
        val imgDesignationClose = header.findViewById<ImageView>(R.id.img_designation_close)
        imgDesignationClose.setOnClickListener { v: View? ->
            llDesignation.visibility = View.GONE
            getUsersInCommittee()
        }
        val filter = header.findViewById<ImageView>(R.id.filter)
        filter.setOnClickListener { v: View? -> openFilter() }
        adapter.setParallaxHeader(header, listCommittee)
        listCommittee.adapter = adapter
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

    private fun createDialog(){
        yearPickerDialogFragment = MonthYearPickerDialogFragment.getInstance(monthSelected, yearSelected,getString(R.string.app_name), MonthFormat.LONG)
    }

    private fun openFilter() {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.filter_committee)
        spLocalCommunity = dialog.findViewById<JRSpinner>(R.id.sp_region)
        spCommittee= dialog.findViewById<JRSpinner>(R.id.sp_committee)
        spDesignation= dialog.findViewById<JRSpinner>(R.id.sp_designation)
        tvStart= dialog.findViewById<TextView>(R.id.tv_start)
        tvEnd= dialog.findViewById<TextView>(R.id.tv_end)
        edtName= dialog.findViewById<EditText>(R.id.edt_name)
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
                val list=ArrayList<String>()
                list.add(getString(R.string.select))
                list.addAll(it)
                spLocalCommunity.setItems(list.toTypedArray())
                spLocalCommunity.setExpandTint(R.color.black)
            }

            committeeViewModel.getCommitteeList().observeForever {
                val list=ArrayList<String>()
                list.add(getString(R.string.select))
                list.addAll(it)
                spCommittee.setItems(list.toTypedArray())
                spCommittee.setExpandTint(R.color.black)
            }

            committeeViewModel.getDesignation().observeForever {
                val list=ArrayList<String>()
                list.add(getString(R.string.select))
                list.addAll(it)
                spDesignation.setItems(list.toTypedArray())
                spDesignation.setExpandTint(R.color.black)
            }

        }

        tvStart.setOnClickListener {
            isStart=true
            yearPickerDialogFragment.show(activity?.supportFragmentManager!!, null)
        }

        tvEnd.setOnClickListener {
            isStart=false
            yearPickerDialogFragment.show(activity?.supportFragmentManager!!, null)
        }

        yearPickerDialogFragment.setOnDateSetListener { year, monthOfYear ->
            monthSelected = monthOfYear
            yearSelected = year
            val month = DateFormatSymbols().months[monthSelected]
            if(isStart){
                tvStart.text = String.format("%s  %s", month, yearSelected)
            }else{
                tvEnd.text = String.format("%s  %s", month, yearSelected)
            }
        }

        edtName.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                dialog.dismiss()
                getUsersInCommittee()
                true
            } else {
                false
            }
        }
    }

    private fun getUsersInCommittee(){
        val jsonObject=JSONObject()
        Coroutines.io{
            val lcomm=spLocalCommunity.text.toString().trim()
            if(!lcomm.isNullOrEmpty() && lcomm != getString(R.string.select)){
                val id=committeeViewModel.getLocalCommunityName(lcomm)
                jsonObject.put(getString(R.string.local_community_id),id)
                Coroutines.main {
                    txtRegion.text = lcomm
                }
            }
            val designation=spDesignation.text.toString().trim()
            if(!designation.isNullOrEmpty() && designation != getString(R.string.select)){
                Coroutines.main {
                    txtDesignation.text=designation
                }

                val id=committeeViewModel.getDesignationName(designation)
                jsonObject.put(getString(R.string.designation_id),id)
            }
            val commitee=spCommittee.text.toString().trim()
            if(!commitee.isNullOrEmpty() && commitee != getString(R.string.select)){

                Coroutines.main {
                    txtCommittee.text=commitee
                }
                val id=committeeViewModel.getCommitteeName(commitee)
                jsonObject.put(getString(R.string.committee_id),id)
            }

            Coroutines.main {
                txtDuration.text="${tvStart.text} - ${tvEnd.text}"
            }

            jsonObject.put("",tvStart.text)
            jsonObject.put("",tvEnd.text)
            jsonObject.put("",edtName.text.trim())
            val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
           /* committeeViewModel.getUsersInCommittee(updated)
            shimmerFrameLayout.startShimmerAnimation()
            shimmerFrameLayout.visibility = View.VISIBLE*/
            Utility.hideKeyboard(activity)
        }
    }

    override fun getMembers(response: SmartFilterResponse) {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility = View.GONE
        if(response.success){
            lstMember.clear()
            lstMember.addAll(response.members)
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
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        // DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        // DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
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