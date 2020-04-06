package com.krs.community.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.ExportAdapter
import com.krs.community.adapter.LocationAdapter
import com.krs.community.adapter.MyRoleAdapter
import com.krs.community.app.AppController
import com.krs.community.app.NotificationBadge
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.ByFilterListener
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.krs.community.viewmodelfactory.SmartFilterViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SmartFilterResult : Fragment(), KodeinAware, ByFilterListener, ParallaxRecyclerAdapter.OnLoadMore, MyRoleAdapter.iChangeRoleListner, RoomMemberListener, LocationAdapter.SetLocationListner, ExportAdapter.exportPdfListener {

    override val kodein by kodein()

    private lateinit var rvFilters: RecyclerView
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private val lstMembers: ArrayList<Member> = ArrayList()
    private lateinit var actionModeCallback: ActionModeCallback
    private var actionMode: ActionMode? = null
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var selectedItems: SparseBooleanArray
    private lateinit var animationItemsIndex: SparseBooleanArray
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    private lateinit var tvCount: TextView
    private lateinit var ivExport: ImageView
    private var selectedPosition = 0
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var llRoot: LinearLayout
    private var argus: String? = null
    private val TAG = SmartFilterResult::class.java.simpleName
    private lateinit var smartFilterViewModel: SmartFilterViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private val smartFilterViewModelFactory: SmartFilterViewModelFactory by instance()
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance()
    private val roomMemberFactory: RoomMemberViewModelFactory by instance()
    private var exportDialog: DialogPlus? = null
    private var changeRoleDialog: DialogPlus? = null
    private var setLocationDialog: DialogPlus? = null
    private var loginMember: Member? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_filter_result, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, SmartFilterResult::class.simpleName)
        mApp.facebookAnalytics(context, SmartFilterResult::class.simpleName)

        llRoot = rootView.findViewById(R.id.ll_parent)
        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        rvFilters = rootView.findViewById(R.id.lstFilter)
        actionModeCallback = ActionModeCallback()
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_view_container)
        AppController.mApplication.start = 0
        smartFilterViewModel = ViewModelProvider(this, smartFilterViewModelFactory).get(SmartFilterViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProvider(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        smartFilterViewModel.mByFilterListener = this
        roomMemberViewModel.mRoomMemberListener = this
        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)

        adapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {
                val member = lstMembers[position]
                val holder = viewHolder as SmartFilterResult.ViewHolder

                if (lstMembers.size > 0) {
                    tvCount.visibility = View.VISIBLE
                    tvCount.text = "${lstMembers.size} members found"
                } else {
                    tvCount.visibility = View.GONE
                }

                holder.tvName.text = member.firstName
                holder.tvArea.text = member.area
                if (member.gender.equals("Male")) {
                    holder.ivGender.setBackgroundResource(R.drawable.male)
                } else {
                    holder.ivGender.setBackgroundResource(R.drawable.female)
                }
                if (member.status == "2") {
                    holder.ivVerify.visibility = View.VISIBLE
                } else {
                    holder.ivVerify.visibility = View.GONE
                }
                var count = member.membersCount
                if (count != 0) {
                    count += 1
                }
                holder.badge.setNumber(count)

                Coroutines.io {
                    if (!member.subCastId.isNullOrEmpty()) {
                        val name = member.firstName + " " + smartFilterViewModel.getLastNameById(member.subCastId.toInt())
                        Coroutines.main {
                            viewHolder.tvName.text = name
                        }
                    }

                    if (!member.cityId.isNullOrEmpty()) {
                        val area = member.area + " " + smartFilterViewModel.getCityNamebyId(member.cityId)
                        Coroutines.main {
                            holder.tvArea.text = area
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

                if (member.emailAddress.isEmpty()) {
                    viewHolder.ivEmail.visibility = View.GONE
                    viewHolder.tvEmail.text = getString(R.string.email_not_available)
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                } else {
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.red_btn_bg_color))
                    viewHolder.ivEmail.visibility = View.VISIBLE
                    viewHolder.tvEmail.text = member.emailAddress
                }

                if (member.headId == "0") {
                    holder.tvRole.text = resources.getString(R.string.Family_Head)
                } else {
                    holder.tvRole.text = resources.getString(R.string.Member)
                }
                if (member.updatedDt.isNotEmpty()) {
                    if (member.updatedDt.contains(getString(R.string.zero_date))) {
                        viewHolder.tvUpdate.text = getString(R.string.not_updated)
                    } else {
                        viewHolder.tvUpdate.text = getString(R.string.UpdateList) + " " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    }
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
                            Utility.fade(activity)
                        } else if (it == 4) {
                            shareDetails(activity, viewHolder.tvName.text.toString(), member.mobile, member.emailAddress, viewHolder.tvArea.text.toString(), member.address)
                        } else if (it == 5) {
                            val adapter: LocationAdapter = LocationAdapter(context as AppCompatActivity, member)
                            adapter.setLocationListner(this@SmartFilterResult)
                            setLocationDialog = DialogPlus.newDialog(context)
                                    .setAdapter(adapter)
                                    .setGravity(Gravity.BOTTOM)
                                    .setCancelable(true)
                                    .setExpanded(false, 600)
                                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                                    .create()
                            setLocationDialog?.show()
                        }
                    }
                    viewHolder.boomMenuButton.addBuilder(builder)
                }

                holder.boomMenuButton.setOnClickListener { v: View? -> holder.boomMenuButton.boom() }

                holder.iconText.text = holder.tvName.text.substring(0, 1)
                holder.itemView.isActivated = selectedItems[position, false]
                applyIconAnimation(holder, position)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position, member)
                applyImportant(viewHolder, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }
        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_filter, container, false)
        val ivAtoz = header.findViewById<ImageView>(R.id.iv_atoz)
        ivAtoz.visibility = View.GONE
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        tvCount = header.findViewById(R.id.tv_count)
        ivExport = header.findViewById<ImageView>(R.id.iv_export)
        ivExport.setOnClickListener {

            if (Utility.checkExternalStoragePermission(activity)) {
                val adapter: ExportAdapter = ExportAdapter(activity as AppCompatActivity)
                adapter.setExportListner(this@SmartFilterResult)
                exportDialog = DialogPlus.newDialog(activity as AppCompatActivity)
                        .setAdapter(adapter)
                        .setGravity(Gravity.BOTTOM)
                        .setCancelable(true)
                        .setExpanded(true, 800)
                        .setContentBackgroundResource(R.drawable.popup_top_corner)
                        .create()
                exportDialog?.show()

            } else {
                Utility.requestStoragePermission(activity as AppCompatActivity)
            }


        }

        adapter.setParallaxHeader(header, rvFilters)
        adapter.setContext(this)
        rvFilters.layoutManager = LinearLayoutManager(activity)
        rvFilters.adapter = adapter

        argus = arguments?.getString(getString(R.string.filter_values))

        DashboardActivity.stop = false
        getFilterMembers()

        return rootView
    }

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            AppController.mApplication.start = (lstMembers.size + 1)
            getFilterMembers()
        }
    }

    private fun getFilterMembers() {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            val jsonObject = JSONObject()
            val jsonObj = JSONObject(argus)
            jsonObject.put(getString(R.string.start), AppController.mApplication.start)
            jsonObject.put(getString(R.string.length), AppController.mApplication.length)
            jsonObject.put(getString(R.string.filter_by), jsonObj)
            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
            smartFilterViewModel.smartFilterSearch(updated)
            Handler().postDelayed({
                shimmerFrameLayout.stopShimmerAnimation()
                shimmerFrameLayout.visibility = View.GONE
            }, 4000)
            lstMembers.clear()
            adapter.notifyDataSetChanged()

            if (AppController.mApplication.start == 0) {
                shimmerFrameLayout.startShimmerAnimation()
                shimmerFrameLayout.visibility = View.VISIBLE
            }
            Utility.hideKeyboard(activity)
        }
    }

    override fun getMembers(response: SmartFilterResponse) {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility = View.GONE

        if (response.success) {
            if (response.members.size > 0) {
                lstMembers.clear()
                lstMembers.addAll(response.members)
                adapter.notifyDataSetChanged()

                rvFilters.layoutManager?.scrollToPosition(selectedPosition)
                selectedPosition = lstMembers.size - 1
                DashboardActivity.stop = false
                if (loginMember?.role == getString(R.string.USER) || loginMember?.role == getString(R.string.LOCAL_ADMIN)) {
                    ivExport.visibility = View.GONE
                } else {
                    ivExport.visibility = View.VISIBLE
                }
                if (lstMembers.size <= AppController.mApplication.length) {
                    DashboardActivity.stop = true
                    Snackbar.make(llRoot, getString(R.string.endrecord), Snackbar.LENGTH_LONG).show()
                }
            } else {
                noRecordsFound()
            }
        } else {
            noRecordsFound()
        }
    }

    private fun noRecordsFound() {
        ivExport.visibility = View.GONE
        DashboardActivity.stop = true
        val gif: Int = R.drawable.gif_dialog
        TTFancyGifDialog.Builder(activity)
                .setMessage("No Record Found")
                .setPositiveBtnText("OK")
                .setPositiveBtnBackground("#843f52")
                .setGifResource(gif)
                .isCancellable(false)
                .OnPositiveClicked {
                    try {
                        val jsonObject = JSONObject()
                        jsonObject.put(activity?.getString(R.string.name_filter), getString(R.string.default_filter_name))
                        jsonObject.put(activity?.getString(R.string.value_filter), argus)
                        val listFragment = ExpandableFilterListFragment()
                        val mBundle = Bundle()
                        mBundle.putString(activity?.getString(R.string.edit_filter), jsonObject.toString())
                        listFragment.arguments = mBundle
                        Utility.movetoFragment(activity, listFragment)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .build()
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFailure(message: String) {
        Coroutines.main {
            DashboardActivity.stop = false
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyImportant(holder: ViewHolder, member: Member) {

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

    private fun applyClickEvents(holder: ViewHolder, position: Int, member: Member) {

        holder.iconImp.setOnClickListener {

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

        holder.messageContainer.setOnClickListener { onMessageRowClicked(position) }
        holder.messageContainer.setOnLongClickListener { view ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: ViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            val imgURL = context?.getString(R.string.base_url_thumb) + member.profilePic
            Glide.with(activity!!).load(imgURL)
                    .thumbnail(0.5f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyIconAnimation(holder: ViewHolder, position: Int) {
        if (selectedItems[position, false]) {
            holder.iconFront.visibility = View.GONE
            resetIconYAxis(holder.iconBack)
            holder.iconBack.visibility = View.VISIBLE
            holder.iconBack.alpha = 1f
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(activity, holder.iconBack, holder.iconFront, true)
                resetCurrentIndex()
            }
        } else {
            holder.iconBack.visibility = View.GONE
            resetIconYAxis(holder.iconFront)
            holder.iconFront.visibility = View.VISIBLE
            holder.iconFront.alpha = 1f
            if (reverseAllAnimations && animationItemsIndex[position, false] || currentSelectedIndex == position) {
                FlipAnimator.flipView(activity, holder.iconBack, holder.iconFront, false)
                resetCurrentIndex()
            }
        }
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    private fun toggleSelected(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems[pos, false]) {
            selectedItems.delete(pos)
            animationItemsIndex.delete(pos)
        } else {
            selectedItems.put(pos, true)
            animationItemsIndex.put(pos, true)
        }
        adapter.notifyItemChanged(pos + 1)
    }

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private val selectedItemCount: Int
        private get() = selectedItems.size()

    private fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private fun removeData(position: Int) {
        lstMembers.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        val boomMenuButton: BoomMenuButton = itemView.findViewById(R.id.bmb1)
        val tvArea: TextView = itemView.findViewById(R.id.tv_area)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvMobile: TextView = itemView.findViewById(R.id.tv_mobile)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        var iconBack: RelativeLayout = itemView.findViewById(R.id.icon_back)
        var iconImp: ImageView = itemView.findViewById(R.id.icon_star)
        var iconFront: RelativeLayout = itemView.findViewById(R.id.icon_front)
        var iconText: TextView = itemView.findViewById(R.id.icon_text)
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
        var tvUpdate: TextView = itemView.findViewById(R.id.tv_update)
        var messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
        var ivMobile: ImageView = itemView.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = itemView.findViewById(R.id.iv_email)
        var ivGender: ImageView = itemView.findViewById(R.id.iv_gender)
        var ivVerify: ImageView = itemView.findViewById(R.id.iv_verify)
        var badge: NotificationBadge = itemView.findViewById(R.id.badge)

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View): Boolean {
            onRowLongClicked(adapterPosition)
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        mShimmerViewContainer.startShimmerAnimation()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        mShimmerViewContainer.stopShimmerAnimation()
        actionMode?.finish()
        selectedItems.clear()
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions[i])
        }
        adapter.notifyDataSetChanged()
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            val menuDelete = menu.findItem(R.id.action_delete)
            val menuRole = menu.findItem(R.id.action_my_role)

            if (loginMember?.role == getString(R.string.User)) {
                menuDelete.isVisible = false
                menuRole.isVisible = false
            } else {
                menuDelete.isVisible = true
                menuRole.isVisible = true
            }

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
                when (item.itemId) {
                    R.id.action_delete -> {
                        val selectedItemPositions = getSelectedItems()
                        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getString(R.string.you_sure))
                                .setContentText(getString(R.string.WantDisable) + "${selectedItemPositions.size}" + getString(R.string.Proffiles))
                                .setConfirmText(getString(R.string.yesDisable))
                                .setCancelText(getString(R.string.no))
                                .setConfirmClickListener {
                                    it.dismiss()

                                    val jsonObject = JSONObject()
                                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                    jsonObject.put("status", "0")

                                    var Ids = ""
                                    for (index in selectedItemPositions) {
                                        Ids += lstMembers[index].id + ","
                                    }

                                    Ids = Ids.substring(0, Ids.length - 1)
                                    jsonObject.put(getString(R.string.idList), Ids)
                                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                                    lstMembers.clear()
                                    tvCount.visibility = View.GONE
                                    adapter.notifyDataSetChanged()
                                    mShimmerViewContainer.startShimmerAnimation()
                                    mShimmerViewContainer.visibility = View.VISIBLE
                                    roomMemberViewModel.changeStatus(updated)

                                }
                                .setCancelClickListener {
                                    it.dismiss()
                                }
                                .show()

                        true
                    }
                    R.id.action_my_role -> {

                        val adapter: MyRoleAdapter = MyRoleAdapter(context)
                        adapter.setChangeRoleListner(this@SmartFilterResult)
                        changeRoleDialog = DialogPlus.newDialog(context)
                                .setAdapter(adapter)
                                .setGravity(Gravity.BOTTOM)
                                .setCancelable(true)
                                .setOnCancelListener {
                                    actionMode?.finish()
                                }
                                .setExpanded(true, 700)
                                .setContentBackgroundResource(R.drawable.popup_top_corner)
                                .create()
                        changeRoleDialog?.show()



                        true
                    }

                    R.id.action_location -> {
                        val selectedItemPositions = getSelectedItems()
                        val message = getString(R.string.ShareCity) + " " + selectedItemPositions.size + " " + getString(R.string.ProfileCity)
                        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getString(R.string.you_sure))
                                .setContentText(message)
                                .setConfirmText(getString(R.string.YesCity))
                                .setCancelText(getString(R.string.no))
                                .setConfirmClickListener {
                                    it.dismiss()
                                    val jsonObject = JSONObject()
                                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                    jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.member_id), ""))

                                    var Ids = ""
                                    val loginuser = Guru.getString(getString(R.string.loginMember), "")
                                    val loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)
                                    if (loginMember?.sharingId != null && loginMember.sharingId.isNotEmpty()) {
                                        Ids = loginMember.sharingId + ","
                                    }
                                    val loginSharedIds = Ids.split(',')
                                    for (index in selectedItemPositions) {
                                        if (!loginSharedIds.contains(lstMembers[index].id)) {
                                            Ids += lstMembers[index].id + ","
                                        }
                                    }
                                    Ids = Ids.substring(0, Ids.length - 1)

                                    jsonObject.put(getString(R.string.sharing_id), Ids)
                                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                                    mShimmerViewContainer.startShimmerAnimation()
                                    mShimmerViewContainer.visibility = View.VISIBLE
                                    profileDetailViewModel.updateProfile(updated, true)

                                }
                                .setCancelClickListener {
                                    it.dismiss()
                                }
                                .show()

                        true
                    }


                    R.id.action_select_all -> {
                        clearSelections()
                        for (i in lstMembers.indices) {
                            enableActionMode(i)
                        }
                        true
                    }
                    else -> false
                }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rvFilters.post {
                resetAnimationIndex()
            }
        }
    }


    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = selectedItemCount
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)!!
        }
        toggleSelection(position)
    }

    private fun onMessageRowClicked(position: Int) {
        if (selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMembers.get(position))
            startActivity(intent)
            //  Utility.fade(activity)
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }

    override fun changeRole(role: String?) {
        val selectedItemPositions = getSelectedItems()
        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.you_sure))
                .setContentText("${selectedItemPositions.size}" + getString(R.string.profileRole) + "'$role'!")
                .setConfirmText(getString(R.string.YesPFilter))
                .setCancelText(getString(R.string.no))
                .setConfirmClickListener {
                    it.dismiss()

                    val jsonObject = JSONObject()
                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))

                    var changed = ""
                    if (role == getString(R.string.Local_Admin)) {
                        changed = getString(R.string.LOCAL_ADMIN)
                    } else if (role == getString(R.string.Sub_Admin)) {
                        changed = getString(R.string.SUB_ADMIN)
                    } else {
                        changed = getString(R.string.USER)
                    }

                    jsonObject.put(getString(R.string.role), changed)
                    val loginuser = Guru.getString(getString(R.string.loginMember), "")
                    val member: Member = Gson().fromJson<Member>(loginuser, Member::class.java)
                    jsonObject.put(getString(R.string.local_community_id), member.localCommunityId)
                    jsonObject.put(getString(R.string.sub_community_id), member.subCommunityId)

                    var Ids = ""
                    for (index in selectedItemPositions) {
                        Ids += lstMembers[index].id + ","
                    }

                    Ids = Ids.substring(0, Ids.length - 1)
                    jsonObject.put(getString(R.string.idList), Ids)
                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                    lstMembers.clear()

                    tvCount.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    mShimmerViewContainer.startShimmerAnimation()
                    mShimmerViewContainer.visibility = View.VISIBLE
                    roomMemberViewModel.changeRole(updated)

                }
                .setCancelClickListener {
                    it.dismiss()
                }
                .show()
    }

    override fun exportPdf(filters: ArrayList<String>) {
        if (lstMembers.size > 0) {
            Handler().post {
                Utility.startSweetProgress(activity, getString(R.string.exporting_search_list), getString(R.string.please_wait))
            }
            createMemberListPDF(activity as AppCompatActivity, lstMembers, filters, profileDetailViewModel)
            Handler().postDelayed({
                Utility.hideSweetProgress()
            }, 7000)
        } else {
            rvFilters.snackbar(getString(R.string.NoRecordList), Snackbar.LENGTH_SHORT)
        }
    }

    override fun cancelDialog() {
        actionMode?.finish()
        changeRoleDialog?.dismiss()
        setLocationDialog?.dismiss()
        exportDialog?.dismiss()
    }

}