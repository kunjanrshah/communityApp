package com.krs.community.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.LocationAdapter
import com.krs.community.app.AppController
import com.krs.community.entities.RoomMember
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.interfaces.RoomMemberListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.CalendarSearchViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodelfactory.CalendarSearchViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarFragment : Fragment(), SlyCalendarDialog.Callback, KodeinAware, ByFilterListener, ParallaxRecyclerAdapter.OnLoadMore, RoomMemberListener,  LocationAdapter.SetLocationListner {

    override val kodein by kodein()
    private var lstCalendar= ArrayList<Member>()

    private lateinit var calendarSearchViewModel: CalendarSearchViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel

    private val calendarSearchViewModelFactory: CalendarSearchViewModelFactory by instance()
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance()
    private val roomMemberFactory: RoomMemberViewModelFactory by instance()


    private lateinit var recyclerView: RecyclerView
    private lateinit var llRoot: LinearLayout
    private lateinit var shimmerFrameLayout:ShimmerFrameLayout
    var all: Boolean = true
    var death: Boolean = true
    var anniversay: Boolean = true
    var birthday: Boolean = true
    val lblAll="All"
    val lblBirthday="BirthDay"
    val lblMarriage="Marriage Anniversary"
    val lblDeath="Death Anniversary"
    private var filter:String=""
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    val TAG: String = "CalendarFragment"
    private var fromDate:String=""
    private var toDate:String=""
    private lateinit var tvCount:TextView
    private lateinit var txtDate:TextView
    private var setLocationDialog: DialogPlus? = null
    private var filterAdapter: FilterAdapter? = null

    override fun loadApi() {
        if (!DashboardActivity.stop) {
           AppController.mApplication.start = (lstCalendar.size+1)
            searchCalendarList(filter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity,R.color.colorPrimary,true)
        }

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Calendar"

        calendarSearchViewModel = ViewModelProviders.of(this,calendarSearchViewModelFactory).get(CalendarSearchViewModel::class.java)
        roomMemberViewModel = ViewModelProviders.of(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProviders.of(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        calendarSearchViewModel.mByFilterListener =this
        roomMemberViewModel.mRoomMemberListener= this

        llRoot = root.findViewById(R.id.ll_root)
        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container)
        recyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        createCardAdapter(recyclerView)
        val date1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        fromDate=date1
        toDate=date1
        txtDate.text=SimpleDateFormat(getString(R.string.dateFormat)).format(Date())
        DashboardActivity.stop=false
        searchCalendarList(filter)
        return root
    }

    private fun searchCalendarList(filter:String) {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            val jsonObject=JSONObject()
            jsonObject.put("start",AppController.mApplication.start)
            jsonObject.put("length",AppController.mApplication.length)

            if(fromDate.isNotEmpty()){
                jsonObject.put("fromdate",fromDate)
            }
            if(toDate.isNotEmpty()){
                jsonObject.put("todate",toDate)
            }
            if(filter.isNotEmpty()){
                jsonObject.put("filter",filter)
            }

            val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
            calendarSearchViewModel.getCalendarSearch(updated)
            Handler().postDelayed({
                shimmerFrameLayout.stopShimmerAnimation()
                shimmerFrameLayout.visibility=View.GONE
            },4000)
            lstCalendar.clear()
            adapter.notifyDataSetChanged()

            if(AppController.mApplication.start==0){
                shimmerFrameLayout.startShimmerAnimation()
                shimmerFrameLayout.visibility = View.VISIBLE
            }

            Utility.hideKeyboard(activity)
        }
    }

    override fun getMembers(response: SmartFilterResponse) {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility=View.GONE
        if(response.success){
            if (response.members.size > 0) {
                lstCalendar.clear()
                lstCalendar.addAll(response.members)
                adapter.data = lstCalendar
                recyclerView.adapter = adapter
                DashboardActivity.stop = false

                if(lstCalendar.size<=AppController.mApplication.length){
                    DashboardActivity.stop = true
                    Snackbar.make(llRoot, "End of Records", Snackbar.LENGTH_LONG).show()
                }
            }else{
                DashboardActivity.stop = true
                Snackbar.make(llRoot, "End of Records", Snackbar.LENGTH_LONG).show()
            }
        }else {
            DashboardActivity.stop = false
        }
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override suspend fun getFailure(message: String) {
        Coroutines.main {
            DashboardActivity.stop = false
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE
            Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDataSelected(firstDate: Calendar?, secondDate: Calendar?, hours: Int, minutes: Int) {
        fromDate=""
        toDate=""
        if (firstDate != null) {
            val str: String
            if (secondDate == null) {
                str = SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.time)
                fromDate=SimpleDateFormat(Utility.yyyy_MM_dd).format(firstDate.time)
                toDate=SimpleDateFormat(Utility.yyyy_MM_dd).format(firstDate.time)
                Log.d(TAG, str)
            } else {
                fromDate=SimpleDateFormat(Utility.yyyy_MM_dd).format(firstDate.time)
                toDate=SimpleDateFormat(Utility.yyyy_MM_dd).format(secondDate.time)
                str = getString(
                        R.string.period,
                        SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.time),
                        SimpleDateFormat(getString(R.string.dateFormat)).format(secondDate.time)
                )
                Log.d(TAG, str)
            }
            txtDate.text = str
            DashboardActivity.stop=false
            searchCalendarList(filter)
        }
    }

    override fun onCancelled() {
        //Nothing
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    private fun createCardAdapter(recyclerView: RecyclerView) {
        adapter = object : ParallaxRecyclerAdapter<Member>(lstCalendar) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {

                if(lstCalendar.size>0){
                    tvCount.visibility=View.VISIBLE
                    tvCount.text = "Members ${lstCalendar.size} found"
                }else{
                    tvCount.visibility=View.GONE
                }
                    val member=lstCalendar[i]
                (viewHolder as CalendarViewHolder).tvName.text = member.firstName
                viewHolder.tvArea.text = member.area
                Coroutines.io {
                    if(!member.subCastId.isNullOrEmpty()){
                        viewHolder.tvName.text=member.firstName+" "+calendarSearchViewModel.getLastNameById(member.subCastId.toInt())
                    }
                    if(!member.cityId.isNullOrEmpty()){
                        viewHolder.tvArea.text = member.area+" "+calendarSearchViewModel.getCityNamebyId(member.cityId)
                    }
                }
                if(!member.emailAddress.isNullOrEmpty()){
                    viewHolder.tvEmail.text = member.emailAddress.toLowerCase()
                }

                viewHolder.tvMobile.text = member.mobile
                if(lstCalendar[i].headId == "0"){
                    viewHolder.tvRole.text = resources.getString(R.string.Family_Head)
                }else{
                    viewHolder.tvRole.text = resources.getString(R.string.Member)
                }
                viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                viewHolder.boomMenuButton.clearBuilders()
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
                            adapter.setLocationListner(this@CalendarFragment)
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

                viewHolder.boomMenuButton.setOnClickListener {
                    viewHolder.boomMenuButton.boom()
                }

                if(!member.matched.isNullOrEmpty()){
                    if(member.matched.contains("birth_date")){
                        val birth= Utility.changeDateFormat(member.birthDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                        val age=Utility.getAge(birth,Utility.dd_MM_yyyy)

                        val first = "$birth ($age) "
                        val next = "<font color='#C54464'>BirthDay</font>"
                        viewHolder.tvEvent1.text=(Html.fromHtml(first + next))
                        viewHolder.tvEvent1.visibility=View.VISIBLE

                    }else if(member.matched.contains("marriage_date")){
                        val mdate= Utility.changeDateFormat(member.marriageDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                        val age=Utility.getAge(mdate,Utility.dd_MM_yyyy)

                        val first = "$mdate ($age) "
                        val next = "<font color='#C54464'>Marriage Anniversary</font>"
                        viewHolder.tvEvent2.text=(Html.fromHtml(first + next))
                        viewHolder.tvEvent2.visibility=View.VISIBLE
                    }
                    else if(lstCalendar[i].matched.contains("expire_date")){
                        val edate= Utility.changeDateFormat(member.expireDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                        val age=Utility.getAge(edate,Utility.dd_MM_yyyy)

                        val first = "$edate ($age) "
                        val next = "<font color='#C54464'>Death Anniversary</font>"
                        viewHolder.tvEvent3.text=(Html.fromHtml(first + next))
                        viewHolder.tvEvent3.visibility=View.VISIBLE
                    }
                }

                applyImportant(viewHolder, member)
                applyClickEvents(viewHolder, i,member)
                applyProfilePicture(viewHolder, member)
            }
            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return CalendarViewHolder(layoutInflater.inflate(R.layout.row_list_calendar, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstCalendar.size
            }
        }




        adapter.setOnClickEvent { v, position ->
            val intent= Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstCalendar[position])
            startActivity(intent)
            Utility.fade(activity)
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(com.krs.community.R.layout.header_calendar, recyclerView, false)
        tvCount=header.findViewById(R.id.tv_count)

        val fab: FloatingActionButton=header.run { findViewById(com.krs.community.R.id.fab) }
        txtDate=  header.findViewById(com.krs.community.R.id.txtdate)
        val imgCalendar: ImageView= header.findViewById(com.krs.community.R.id.imgCalendar)
        val lstCalFliter: RecyclerView= header.findViewById(R.id.lstCalFliter)
        lstCalFliter.setHasFixedSize(true)
        val ivCancel:ImageView=header.findViewById(R.id.iv_cancel)
        ivCancel.setOnClickListener {
            Utility.backNavigation(activity)
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        if (lstCalFliter != null) {
            val list: ArrayList<String> = ArrayList()
            list.add(lblAll)
            list.add(lblBirthday)
            list.add(lblMarriage)
            list.add(lblDeath)
            //list.add("Reminder")
            filterAdapter=FilterAdapter(list)
            lstCalFliter.adapter =filterAdapter
        }
        lstCalFliter.layoutManager = linearLayoutManager


        imgCalendar.setOnClickListener {
            SlyCalendarDialog()
                    .setSingle(false)
                    .setCallback(this)
                    .setHeaderColor(resources.getColor(R.color.colorPrimary))
                    .setBackgroundColor(Color.parseColor("#ffffff"))
                    .setSelectedColor(Color.parseColor("#c48395"))
                    .show(activity!!.supportFragmentManager, "TAG_SLYCALENDAR")
        }

        fab.setOnClickListener {
            searchCalendarList(filter)
        }

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, recyclerView)
        adapter.data = lstCalendar
        recyclerView.adapter = adapter
    }


    private fun applyImportant(holder: CalendarViewHolder, member: Member) {

        roomMemberViewModel.getRoomMember(Integer.parseInt(member.id)).observe(activity as AppCompatActivity, androidx.lifecycle.Observer {
            try {
                if (it != null) {
                    holder.iconImp.setImageDrawable(ContextCompat.getDrawable(activity as AppCompatActivity, R.drawable.ic_star_black_24dp))
                    holder.iconImp.setColorFilter(ContextCompat.getColor(activity as AppCompatActivity, R.color.icon_tint_selected))
                } else {
                    holder.iconImp.setImageDrawable(ContextCompat.getDrawable(activity as AppCompatActivity, R.drawable.ic_star_border_black_24dp))
                    holder.iconImp.setColorFilter(ContextCompat.getColor(activity as AppCompatActivity, R.color.icon_tint_normal))
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        })
    }

    private fun applyClickEvents(holder: CalendarViewHolder, position: Int, member: Member) {

        holder.iconImp.setOnClickListener {

            val member: Member = lstCalendar.get(position)
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

        holder.llMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + holder.tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }

        holder.imgProfile.setOnClickListener { view ->
            try {
                val path = getString(R.string.base_url_original) + "" + member.profilePic
                Log.d(TAG, "path: $path")
                openImageDialog(activity as AppCompatActivity,path)
            } catch (e: Exception) {
                e.message
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun applyProfilePicture(holder: CalendarViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            holder.imgProfile.isClickable = true
            val url=resources.getString(R.string.base_url_thumb)+member.profilePic
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

    internal class CalendarViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById<View>(R.id.tv_name) as TextView
        var tvArea: TextView = v.findViewById(R.id.tv_area)
        var tvEvent1: TextView = v.findViewById(R.id.tv_event1)
        var tvEvent2: TextView = v.findViewById(R.id.tv_event2)
        var tvEvent3: TextView = v.findViewById(R.id.tv_event3)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvRole: TextView = v.findViewById(R.id.tv_role)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.bmb1)
        var iconImp: ImageView = v.findViewById(R.id.icon_star)
        var iconText: TextView = v.findViewById(R.id.icon_text1)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile1)
        var llMobile: LinearLayout = v.findViewById(R.id.ll_mobile)
    }

    internal inner class FilterViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var txtName: TextView = v.findViewById(R.id.txt_name)
    }

    internal inner class FilterAdapter(arrayList: ArrayList<String>) : RecyclerView.Adapter<FilterViewHolder>() {
        private var list: ArrayList<String>? = null

        init {
            list = arrayList
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_calendar, parent, false)
            return FilterViewHolder(view)
        }

        override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {

            holder.txtName.text = list!![position]

            if(all){
                when (holder.txtName.text) {
                    lblAll -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_fill_tithi)
                        holder.txtName.setTextColor(resources.getColor(R.color.white))
                    }
                    lblDeath -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_panchag)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblBirthday -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_birthday)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblMarriage -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_ann)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    "Reminder" -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                }
            }else if(death){
                when (holder.txtName.text) {
                    lblAll -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_tithi)
                        holder.txtName.setTextColor(resources.getColor(R.color.white))
                    }
                    lblBirthday -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_birthday)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblMarriage -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_ann)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblDeath -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_fill_panchag)
                        holder.txtName.setTextColor(resources.getColor(R.color.white))
                    }
                    "Reminder" -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                }
            }else if(birthday){
                when (holder.txtName.text) {
                    lblAll -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_tithi)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblBirthday -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_fill_birthday)
                        holder.txtName.setTextColor(resources.getColor(R.color.white))
                    }
                    lblMarriage -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_ann)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblDeath -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_panchag)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    "Reminder" -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                }
            }else if(anniversay){

                when (holder.txtName.text) {
                    lblAll -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_tithi)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblBirthday -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_birthday)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    lblMarriage -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_fill_ann)
                        holder.txtName.setTextColor(resources.getColor(R.color.white))
                    }
                    lblDeath -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_panchag)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                    "Reminder" -> {
                        holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                        holder.txtName.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                }
            }

            holder.txtName.setOnClickListener {
                if (holder.txtName.text == lblAll) {
                    all=true
                    death=false
                    birthday=false
                    anniversay=false
                    AppController.mApplication.start=0
                    filter=""
                    filterAdapter?.notifyDataSetChanged()
                    DashboardActivity.stop=false
                    searchCalendarList(filter)
                } else if (holder.txtName.text == lblDeath) {
                    all=false
                    death=true
                    birthday=false
                    anniversay=false
                    AppController.mApplication.start=0
                    filter="2"
                    filterAdapter?.notifyDataSetChanged()
                    DashboardActivity.stop=false
                    searchCalendarList(filter)
                } else if (holder.txtName.text == lblBirthday) {
                    all=false
                    death=false
                    birthday=true
                    anniversay=false
                    AppController.mApplication.start=0
                    filter="0"
                    filterAdapter?.notifyDataSetChanged()
                    DashboardActivity.stop=false
                    searchCalendarList(filter)
                } else if (holder.txtName.text == lblMarriage) {
                    all=false
                    death=false
                    birthday=false
                    anniversay=true
                    AppController.mApplication.start=0
                    filter="1"
                    filterAdapter?.notifyDataSetChanged()
                    DashboardActivity.stop=false
                    searchCalendarList(filter)
                }
            }
        }

        override fun getItemCount(): Int {
            return list!!.size
        }
    }

    override fun getRoomMembers(response: List<RoomMember>) {
    }

    override fun cancelDialog() {
    }
}
