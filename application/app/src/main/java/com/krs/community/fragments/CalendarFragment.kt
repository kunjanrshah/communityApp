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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
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
import com.krs.community.app.NotificationBadge
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.ByFilterListener
import com.krs.community.listeners.ReminderListener
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.ReminderResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.yyyy_MM_dd
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


class CalendarFragment : Fragment(), SlyCalendarDialog.Callback, KodeinAware, ByFilterListener, ParallaxRecyclerAdapter.OnLoadMore, RoomMemberListener, LocationAdapter.SetLocationListner, ReminderListener {

    override val kodein by kodein()
    private var lstCalendar = ArrayList<Member>()

    private lateinit var calendarSearchViewModel: CalendarSearchViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private var snackbar: Snackbar? = null
    private val calendarSearchViewModelFactory: CalendarSearchViewModelFactory by instance<CalendarSearchViewModelFactory>()
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private val roomMemberFactory: RoomMemberViewModelFactory by instance<RoomMemberViewModelFactory>()
    private var selectedPosition = 0
    private var selectedReminder = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var llRoot: LinearLayout
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    var all: Boolean = true
    var death: Boolean = true
    var anniversay: Boolean = true
    var birthday: Boolean = true
    val lblAll = "All"
    val lblBirthday = "BirthDay"
    val lblMarriage = "Marriage"
    val lblDeath = "Death"
    private var filter: String = ""
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    val TAG: String = CalendarFragment::class.java.simpleName
    private var fromDate: String = ""
    private var toDate: String = ""
    private lateinit var tvCount: TextView
    private lateinit var txtDate: TextView
    private lateinit var ivNoFound: ImageView
    private var setLocationDialog: DialogPlus? = null
    private var filterAdapter: FilterAdapter? = null

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            AppController.mApplication.start = (lstCalendar.size + 1)
            searchCalendarList(filter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        }

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Calendar"

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, CalendarFragment::class.simpleName)
        mApp.facebookAnalytics(context, CalendarFragment::class.simpleName)

        calendarSearchViewModel = ViewModelProvider(this, calendarSearchViewModelFactory).get(CalendarSearchViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProvider(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        calendarSearchViewModel.mByFilterListener = this
        calendarSearchViewModel.mReminderListener = this
        roomMemberViewModel.mRoomMemberListener = this

        llRoot = root.findViewById(R.id.ll_root)
        ivNoFound = root.findViewById(R.id.iv_not_found)

        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container)
        recyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        createCardAdapter(recyclerView)
        val date1 = SimpleDateFormat(yyyy_MM_dd, Locale.getDefault()).format(Date())
        fromDate = date1
        toDate = date1
        txtDate.text = SimpleDateFormat(getString(R.string.dateFormat)).format(Date())
        DashboardActivity.stop = false
        searchCalendarList(filter)
        return root
    }

    private fun searchCalendarList(filter: String) {
        if (!DashboardActivity.stop) {
            ivNoFound.visibility = View.GONE
            DashboardActivity.stop = true
            val jsonObject = JSONObject()
            jsonObject.put(getString(R.string.start), AppController.mApplication.start)
            jsonObject.put(getString(R.string.length), AppController.mApplication.length)
            jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.member_id), ""))
            if (fromDate.isNotEmpty()) {
                jsonObject.put(getString(R.string.fromdate), fromDate)
            }
            if (toDate.isNotEmpty()) {
                jsonObject.put(getString(R.string.todate), toDate)
            }
            if (filter.isNotEmpty()) {
                jsonObject.put(getString(R.string.filter), filter)
            }

            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
            calendarSearchViewModel.getCalendarSearch(updated)

            if (AppController.mApplication.start == 0) {
                shimmerFrameLayout.startShimmerAnimation()
                shimmerFrameLayout.visibility = View.VISIBLE
                tvCount.visibility = View.GONE
            } else {
                // snackbar= Snackbar.make(recyclerView, getString(R.string.load_more), Snackbar.LENGTH_INDEFINITE)
                // snackbar?.show()
            }
            Utility.hideKeyboard(activity)
        }
    }

    override fun getMembers(response: SmartFilterResponse) {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility = View.GONE
        //  snackbar?.dismiss()
        if (response.success) {
            if (response.members.size > 0) {
                tvCount.visibility = View.VISIBLE
                tvCount.text = "Members ${response.totalRecords} found"
                // lstCalendar.clear()

                lstCalendar.addAll(response.members)
                adapter.data = lstCalendar
                recyclerView.adapter = adapter
                recyclerView.layoutManager?.scrollToPosition(AppController.mApplication.start)
                DashboardActivity.stop = false
                ivNoFound.visibility = View.GONE
                if (response.members.size < AppController.mApplication.length) {
                    DashboardActivity.stop = true
                    Snackbar.make(llRoot, getString(R.string.endRecord), Snackbar.LENGTH_LONG).show()
                }
            } else {
                tvCount.visibility = View.GONE
                DashboardActivity.stop = true
                Snackbar.make(llRoot, getString(R.string.endRecord), Snackbar.LENGTH_LONG).show()
            }

        } else {
            DashboardActivity.stop = false
            ivNoFound.visibility = View.VISIBLE
            Snackbar.make(recyclerView, getString(R.string.noFoundNonActives), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun reminderResponse(response: ReminderResponse) {
        Utility.hideSweetProgress()
        if (response.success) {
            lstCalendar[selectedPosition].reminderBirthDate = response.data.reminderId
            if (selectedReminder == getString(R.string.marriage_date)) {
                lstCalendar[selectedPosition].reminderMarriageDate = response.data.reminderId
            } else if (selectedReminder == getString(R.string.expire_date)) {
                lstCalendar[selectedPosition].reminderExpireDate = response.data.reminderId
            }
            adapter.notifyDataSetChanged()
            Utility.startSweetDialog(activity, SweetAlertDialog.SUCCESS_TYPE, "Reminder", response.message)
        } else {
            Utility.startSweetDialog(activity, SweetAlertDialog.ERROR_TYPE, "Reminder", response.message)
        }
    }

    override suspend fun getFailure(message: String) {
        Coroutines.main {
            snackbar?.dismiss()
            Utility.hideSweetProgress()
            DashboardActivity.stop = false
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDataSelected(firstDate: Calendar?, secondDate: Calendar?, hours: Int, minutes: Int) {
        fromDate = ""
        toDate = ""
        if (firstDate != null) {
            val str: String
            if (secondDate == null) {
                str = SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.time)
                fromDate = SimpleDateFormat(Utility.yyyy_MM_dd).format(firstDate.time)
                toDate = SimpleDateFormat(Utility.yyyy_MM_dd).format(firstDate.time)
                Log.d(TAG, str)
            } else {
                fromDate = SimpleDateFormat(Utility.yyyy_MM_dd).format(firstDate.time)
                toDate = SimpleDateFormat(Utility.yyyy_MM_dd).format(secondDate.time)
                str = getString(
                        R.string.period,
                        SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.time),
                        SimpleDateFormat(getString(R.string.dateFormat)).format(secondDate.time)
                )
                Log.d(TAG, str)
            }
            txtDate.text = str
            DashboardActivity.stop = false
            lstCalendar.clear()
            adapter.notifyDataSetChanged()
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


                val member = lstCalendar[i]
                (viewHolder as CalendarViewHolder).tvName.text = member.firstName

                var count = member.membersCount
                if (count != 0) {
                    count += 1
                }
                viewHolder.badge.setNumber(count)
                viewHolder.tvArea.text = member.area
                Coroutines.io {
                    if (!member.subCastId.isNullOrEmpty()) {
                        val name = member.firstName + " " + calendarSearchViewModel.getLastNameById(member.subCastId.toInt())
                        Coroutines.main {
                            viewHolder.tvName.text = name
                        }
                    }
                    if (!member.cityId.isNullOrEmpty()) {
                        val area = member.area + " " + calendarSearchViewModel.getCityNamebyId(member.cityId)
                        Coroutines.main {
                            viewHolder.tvArea.text = area
                        }
                    }
                }

                if (member.mobile.isEmpty()) {
                    viewHolder.tvMobile.text = getString(R.string.mobile_not_available)
                    viewHolder.ivMobile.visibility = View.GONE
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                } else {
                    viewHolder.ivMobile.visibility = View.VISIBLE
                    viewHolder.tvMobile.text = member.mobile
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.com_facebook_blue))
                }

                if (member.emailAddress.isNullOrEmpty()) {
                    viewHolder.ivEmail.visibility = View.GONE
                    viewHolder.tvEmail.text = getString(R.string.email_not_available)
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                } else {
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.red_btn_bg_color))
                    viewHolder.ivEmail.visibility = View.VISIBLE
                    viewHolder.tvEmail.text = member.emailAddress
                }

                if (lstCalendar[i].headId == "0") {
                    viewHolder.tvRole.text = resources.getString(R.string.Family_Head)
                } else {
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
                            Toast.makeText(activity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                            return@listener
                            val intent: Intent = Intent(activity, FamilyTreeListActivity::class.java)
                            startActivity(intent)
                        } else if (it == 2) {
                            if (!member.mobile.isNullOrEmpty()) {
                                Utility.sendWhatsAppMessage(activity as AppCompatActivity, member.mobile, getString(R.string.install_app))
                            } else {
                                Toast.makeText(activity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                            }
                        } else if (it == 3) {
                            val mBundle = Bundle()
                            mBundle.putSerializable(getString(R.string.member), member)
                            val intent: Intent = Intent(activity, QRCodeActivity::class.java)
                            intent.putExtras(mBundle)
                            startActivity(intent)
                            //    Utility.fade(activity)
                        } else if (it == 4) {
                            shareDetails(activity, viewHolder.tvName.text.toString(), member.mobile, member.emailAddress, viewHolder.tvArea.text.toString(), member.address)
                        } else if (it == 5) {
                            val adapter = LocationAdapter(context as AppCompatActivity, member)
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

                if (!member.matched.isNullOrEmpty()) {
                    if (member.matched.contains(getString(R.string.birth_date))) {
                        val birth = Utility.changeDateFormat(member.birthDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                        val age = Utility.getAge(birth, Utility.dd_MM_yyyy)
                        var age1 = ""
                        if (age < 10) {
                            age1 = "0$age"
                        } else {
                            age1 = "$age"
                        }
                        val first = "$birth ($age1) "
                        val next = "<font color='#C54464'>BirthDay</font>"
                        viewHolder.tvEvent1.text = (Html.fromHtml(first + next))
                        viewHolder.llEvent1.visibility = View.VISIBLE

                        if (member.reminderBirthDate != "0") {
                            viewHolder.imgReminder1.visibility = View.VISIBLE
                        } else {
                            viewHolder.imgReminder1.visibility = View.GONE
                        }

                    } else if (member.matched.contains(getString(R.string.marriage_date))) {
                        val mdate = Utility.changeDateFormat(member.marriageDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                        val age = Utility.getAge(mdate, Utility.dd_MM_yyyy)
                        var age1 = ""
                        if (age < 10) {
                            age1 = "0$age"
                        } else {
                            age1 = "$age"
                        }
                        val first = "$mdate ($age1) "
                        val next = "<font color='#C54464'>Marriage</font>"
                        viewHolder.tvEvent2.text = (Html.fromHtml(first + next))
                        viewHolder.llEvent2.visibility = View.VISIBLE
                        if (member.reminderMarriageDate != "0") {
                            viewHolder.imgReminder2.visibility = View.VISIBLE
                        } else {
                            viewHolder.imgReminder2.visibility = View.GONE
                        }
                    } else if (lstCalendar[i].matched.contains(getString(R.string.expire_date))) {
                        val edate = Utility.changeDateFormat(member.expireDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                        val age = Utility.getAge(edate, Utility.dd_MM_yyyy)
                        var age1 = ""
                        if (age < 10) {
                            age1 = "0$age"
                        } else {
                            age1 = "$age"
                        }
                        val first = "$edate ($age1) "
                        val next = "<font color='#C54464'>Death</font>"
                        viewHolder.tvEvent3.text = (Html.fromHtml(first + next))
                        viewHolder.llEvent3.visibility = View.VISIBLE
                        if (member.reminderExpireDate != "0") {
                            viewHolder.imgReminder3.visibility = View.VISIBLE
                        } else {
                            viewHolder.imgReminder3.visibility = View.GONE
                        }
                    }
                }

                applyImportant(viewHolder, member)
                applyClickEvents(viewHolder, i, member)
                applyProfilePicture(viewHolder, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return CalendarViewHolder(layoutInflater.inflate(R.layout.row_list_calendar, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstCalendar.size
            }
        }
        adapter.setContext(this)
        adapter.setOnClickEvent { v, position ->
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstCalendar[position])
            startActivity(intent)
            // Utility.fade(activity)
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(R.layout.header_calendar, recyclerView, false)
        tvCount = header.findViewById(R.id.tv_count)

        txtDate = header.findViewById(R.id.txtdate)
        val llCalendar: LinearLayout = header.findViewById(R.id.ll_calendar)
        val lstCalFliter: RecyclerView = header.findViewById(R.id.lstCalFliter)
        lstCalFliter.setHasFixedSize(true)
        val ivCancel: ImageView = header.findViewById(R.id.iv_cancel)
        ivCancel.setOnClickListener {
            Utility.backNavigation(activity)
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val list: ArrayList<String> = ArrayList()
        list.add(lblAll)
        list.add(lblBirthday)
        list.add(lblMarriage)
        list.add(lblDeath)
        //list.add("Reminder")
        filterAdapter = FilterAdapter(list)
        lstCalFliter.adapter = filterAdapter
        lstCalFliter.layoutManager = linearLayoutManager

        llCalendar.setOnClickListener {
            SlyCalendarDialog()
                    .setSingle(false)
                    .setCallback(this)
                    .setHeaderColor(resources.getColor(R.color.colorPrimary))
                    .setBackgroundColor(Color.parseColor("#ffffff"))
                    .setSelectedColor(Color.parseColor("#c48395"))
                    .show(activity!!.supportFragmentManager, "TAG_SLYCALENDAR")
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

            val member: Member = lstCalendar[position]
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

        holder.imgProfile.setOnClickListener { view ->
            try {
                val path = getString(R.string.base_url_original) + "" + member.profilePic
                Log.d(TAG, "path: $path")
                openImageDialog(activity as AppCompatActivity, path)
            } catch (e: Exception) {
                e.message
            }
        }

        holder.llEvent1.setOnClickListener {
            selectedPosition = position
            setReminderDialog(member.birthDate, getString(R.string.birth_date), member.reminderBirthDate.toString(), member.firstName, member.id)
        }

        holder.llEvent2.setOnClickListener {
            selectedPosition = position
            setReminderDialog(member.marriageDate, getString(R.string.marriage_date), member.reminderMarriageDate.toString(), member.firstName, member.id)
        }

        holder.llEvent3.setOnClickListener {
            selectedPosition = position
            setReminderDialog(member.expireDate, getString(R.string.expire_date), member.reminderExpireDate.toString(), member.firstName, member.id)
        }
    }

    private fun setReminderDialog(date: String, type: String, id: String, name: String, memberId: String) {
        var strType1 = "BirthDay Reminder"
        selectedReminder = type
        if (type == getString(R.string.marriage_date)) {
            strType1 = "Marriage Reminder"
        } else if (type == getString(R.string.expire_date)) {
            strType1 = "Death Reminder"
        }
        var isSet = "Set"
        if (id != "0") {
            isSet = "Unset"
        }

        SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(strType1)
                .setContentText("Do you want to $isSet $strType1 for $name?")
                .setConfirmText(isSet)
                .setCancelText(activity?.getString(R.string.no))
                .setCustomImage(R.drawable.ic_medk)
                .showCancelButton(true)
                .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                    sweetAlertDialog.dismissWithAnimation()
                    Utility.startSweetProgress(activity, getString(R.string.app_name), getString(R.string.pleaseWait))

                    val jsonObject = JSONObject()
                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.member_id), ""))
                    jsonObject.put(getString(R.string.id), memberId)
                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                    jsonObject.put("reminder_date", date)
                    jsonObject.put("reminder_type", type)
                    jsonObject.put("reminder_id", id)
                    jsonObject.put("message", "")

                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                    calendarSearchViewModel.setReminder(updated)

                }
                .show()
    }

    @SuppressLint("CheckResult")
    private fun applyProfilePicture(holder: CalendarViewHolder, member: Member) {
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

    internal class CalendarViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById<View>(R.id.tv_name) as TextView
        var tvArea: TextView = v.findViewById(R.id.tv_area)
        var tvEvent1: TextView = v.findViewById(R.id.tv_event1)
        var tvEvent2: TextView = v.findViewById(R.id.tv_event2)
        var tvEvent3: TextView = v.findViewById(R.id.tv_event3)
        var llEvent1: LinearLayout = v.findViewById(R.id.ll_event1)
        var llEvent2: LinearLayout = v.findViewById(R.id.ll_event2)
        var llEvent3: LinearLayout = v.findViewById(R.id.ll_event3)
        var imgReminder1: ImageView = v.findViewById(R.id.imgReminder1)
        var imgReminder2: ImageView = v.findViewById(R.id.imgReminder2)
        var imgReminder3: ImageView = v.findViewById(R.id.imgReminder3)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvRole: TextView = v.findViewById(R.id.tv_role)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.bmb1)
        var iconImp: ImageView = v.findViewById(R.id.icon_star)
        var iconText: TextView = v.findViewById(R.id.icon_text1)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile1)
        var llMobile: LinearLayout = v.findViewById(R.id.ll_mobile)
        var ivMobile: ImageView = v.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = v.findViewById(R.id.iv_email)
        var badge: NotificationBadge = v.findViewById(R.id.badge)
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

            when {
                all -> {
                    when (holder.txtName.text) {
                        lblAll -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_fill_tithi)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.white))
                        }
                        lblDeath -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_panchag)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblBirthday -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_birthday)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblMarriage -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_ann)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        "Reminder" -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                    }
                }
                death -> {
                    when (holder.txtName.text) {
                        lblAll -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_tithi)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblBirthday -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_birthday)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblMarriage -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_ann)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblDeath -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_fill_panchag)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.white))
                        }
                        "Reminder" -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                    }
                }
                birthday -> {
                    when (holder.txtName.text) {
                        lblAll -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_tithi)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblBirthday -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_fill_birthday)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.white))
                        }
                        lblMarriage -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_ann)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblDeath -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_panchag)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        "Reminder" -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                    }
                }
                anniversay -> {

                    when (holder.txtName.text) {
                        lblAll -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_tithi)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblBirthday -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_birthday)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        lblMarriage -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_fill_ann)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.white))
                        }
                        lblDeath -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_panchag)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                        "Reminder" -> {
                            holder.txtName.setBackgroundResource(R.drawable.filter_reminder)
                            holder.txtName.setTextColor(ContextCompat.getColor(activity as AppCompatActivity, R.color.black2))
                        }
                    }
                }
            }

            holder.txtName.setOnClickListener {
                when (holder.txtName.text) {
                    lblAll -> {
                        all = true
                        death = false
                        birthday = false
                        anniversay = false
                        AppController.mApplication.start = 0
                        filter = ""
                        filterAdapter?.notifyDataSetChanged()
                        DashboardActivity.stop = false
                        lstCalendar.clear()
                        adapter.notifyDataSetChanged()
                        searchCalendarList(filter)
                    }
                    lblDeath -> {
                        all = false
                        death = true
                        birthday = false
                        anniversay = false
                        AppController.mApplication.start = 0
                        filter = "2"
                        filterAdapter?.notifyDataSetChanged()
                        DashboardActivity.stop = false
                        lstCalendar.clear()
                        adapter.notifyDataSetChanged()
                        searchCalendarList(filter)
                    }
                    lblBirthday -> {
                        all = false
                        death = false
                        birthday = true
                        anniversay = false
                        AppController.mApplication.start = 0
                        filter = "0"
                        filterAdapter?.notifyDataSetChanged()
                        lstCalendar.clear()
                        adapter.notifyDataSetChanged()
                        DashboardActivity.stop = false
                        searchCalendarList(filter)
                    }
                    lblMarriage -> {
                        all = false
                        death = false
                        birthday = false
                        anniversay = true
                        AppController.mApplication.start = 0
                        filter = "1"
                        filterAdapter?.notifyDataSetChanged()
                        DashboardActivity.stop = false
                        lstCalendar.clear()
                        adapter.notifyDataSetChanged()
                        searchCalendarList(filter)
                    }
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
