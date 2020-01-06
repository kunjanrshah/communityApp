package com.krs.community.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.CalendarSearchViewModel
import com.krs.community.viewmodel.CalendarSearchViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarFragment : Fragment(), SlyCalendarDialog.Callback, KodeinAware, ByFilterListener, ParallaxRecyclerAdapter.OnLoadMore {

    override val kodein by kodein()
    private var lstCalendar= ArrayList<Member>()
    private lateinit var calendarSearchViewModel: CalendarSearchViewModel
    private val factory: CalendarSearchViewModelFactory by instance()
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
    private var start: Int = 0
    private val length: Int = 5
    private var filterAdapter: FilterAdapter? = null
    override fun loadApi() {
        if (!DashboardActivity.stop) {
            start = (lstCalendar.size+1)
            searchCalendarList(filter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity,R.color.colorPrimary,true)
        }

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Calendar"
        calendarSearchViewModel = ViewModelProviders.of(this,factory).get(CalendarSearchViewModel::class.java)
        calendarSearchViewModel.mByFilterListener =this

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
            jsonObject.put("start",start)
            jsonObject.put("length",length)

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

            if(start==0){
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

                if(lstCalendar.size<=length){
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

        Toast.makeText(activity,"Success",Toast.LENGTH_SHORT).show()
        //Utility.displaySnackBarWithBottomMargin(recyclerView,"${response.members.size} Records found")
    }

    override fun getFailure(message: String) {
        Coroutines.main {
            DashboardActivity.stop = false
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE
            Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
        }

       // Utility.displaySnackBarWithBottomMargin(recyclerView,"Something went wrong")
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


   /* private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(com.krs.community.R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = recyclerView.getChildAdapterPosition(view)
        val detailsFragment = FamilyDetailActivity.newInstance(adapterPosition)

        //Utility.movetoFragment(activity,FamilyDetailFragment())

        val transaction = fragmentManager?.beginTransaction()
                ?.replace(com.krs.community.R.id.container_body, detailsFragment, FamilyDetailActivity.TAG)
                ?.addToBackStack(null)

         return transaction
    }

    private fun startAnimation(view: View, fragmentTransaction: FragmentTransaction?) {
        fragmentTransaction?.commitAllowingStateLoss()
    }*/

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
                    viewHolder.tvRole.text = "Family Head"
                }else{
                    viewHolder.tvRole.text = "Member"
                }

                viewHolder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    viewHolder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }

                viewHolder.boomMenuButton.setOnClickListener {
                    viewHolder.boomMenuButton.boom()
                }

                if(!member.matched.isNullOrEmpty()){
                    if(member.matched.contains("birth_date")){
                        val birth= Utility.changeDateFormat(member.birthDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                        val age=Utility.getAge(birth,Utility.dd_MM_yyyy)

                        val first = "$birth ($age) ";
                        val next = "<font color='#EE0000'>BirthDay</font>";
                        viewHolder.tvEvent1.text=(Html.fromHtml(first + next));
                        viewHolder.tvEvent1.visibility=View.VISIBLE

                    }else if(member.matched.contains("marriage_date")){
                        val mdate= Utility.changeDateFormat(member.marriageDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                        val age=Utility.getAge(mdate,Utility.dd_MM_yyyy)

                        val first = "$mdate ($age) ";
                        val next = "<font color='#00EE00'>Marriage Anniversary</font>";
                        viewHolder.tvEvent2.text=(Html.fromHtml(first + next));
                        viewHolder.tvEvent2.visibility=View.VISIBLE
                    }
                    else if(lstCalendar[i].matched.contains("expire_date")){
                        val edate= Utility.changeDateFormat(member.expireDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                        val age=Utility.getAge(edate,Utility.dd_MM_yyyy)

                        val first = "$edate ($age) ";
                        val next = "<font color='#0000EE'>Death Anniversary</font>";
                        viewHolder.tvEvent3.text=(Html.fromHtml(first + next));
                        viewHolder.tvEvent3.visibility=View.VISIBLE
                    }
                }
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
            /*val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            startAnimation(copy, fragmentTransaction)*/
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
            Utility.movetoFragment(activity,DashboardFragment())
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
                        holder.txtName.setBackgroundResource(R.drawable.filter_panchag)
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
                    start=0
                    filter=""
                    filterAdapter?.notifyDataSetChanged()
                    DashboardActivity.stop=false
                    searchCalendarList(filter)
                } else if (holder.txtName.text == lblDeath) {
                    all=false
                    death=true
                    birthday=false
                    anniversay=false
                    start=0
                    filter="2"
                    filterAdapter?.notifyDataSetChanged()
                    DashboardActivity.stop=false
                    searchCalendarList(filter)
                } else if (holder.txtName.text == lblBirthday) {
                    all=false
                    death=false
                    birthday=true
                    anniversay=false
                    start=0
                    filter="0"
                    filterAdapter?.notifyDataSetChanged()
                    DashboardActivity.stop=false
                    searchCalendarList(filter)
                } else if (holder.txtName.text == lblMarriage) {
                    all=false
                    death=false
                    birthday=false
                    anniversay=true
                    start=0
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
}
