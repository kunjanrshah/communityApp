package com.krs.community.fragments

import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.LocationAdapter
import com.krs.community.adapter.MyRoleAdapter
import com.krs.community.app.AppController
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

class AdminsFragment : Fragment(), KodeinAware, ByFilterListener, RoomMemberListener, MyRoleAdapter.iChangeRoleListner, LocationAdapter.SetLocationListner {

    override val kodein by kodein()
    private var lstAdmins: ArrayList<Member> = ArrayList()

    private lateinit var smartFilterViewModel: SmartFilterViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel

    private val smartFilterViewModelFactory: SmartFilterViewModelFactory by instance()
    private val roomMemberViewModelFactory: RoomMemberViewModelFactory by instance()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance()

    private lateinit var tvCount: TextView
    private var loginUserSubCommunityId = ""
    private var loginUserLocalCommunityId = ""
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var llRoot: FrameLayout
    private lateinit var rvAdmins: RecyclerView
    private var count = 0
    private var changeRoleDialog: DialogPlus? = null
    private var setLocationDialog: DialogPlus? = null
    private var reverseAllAnimations = false
    private var TAG: String? = AdminsFragment::class.qualifiedName
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray = SparseBooleanArray()
    private var currentSelectedIndex = -1
    private var actionMode: ActionMode? = null
    private lateinit var actionModeCallback: ActionModeCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_admins, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, AdminsFragment::class.simpleName)
        mApp.facebookAnalytics(context, AdminsFragment::class.simpleName)


        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container)
        llRoot = root.findViewById(R.id.ll_root)
        (activity as AppCompatActivity).supportActionBar!!.title = ""

        smartFilterViewModel = ViewModelProvider(this, smartFilterViewModelFactory).get(SmartFilterViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberViewModelFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProvider(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)

        smartFilterViewModel.mByFilterListener = this
        roomMemberViewModel.mRoomMemberListener = this

        actionModeCallback = ActionModeCallback()
        (activity as AppCompatActivity).supportActionBar?.hide()
        adapter = object : ParallaxRecyclerAdapter<Member>(lstAdmins) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {
                val holder = viewHolder as ListViewHolder

                if (lstAdmins.size > 0) {
                    tvCount.visibility = View.VISIBLE
                    tvCount.text = "${lstAdmins.size} " + getString(R.string.adminsfound)
                } else {
                    tvCount.visibility = View.GONE
                }
                val member = lstAdmins[i]
                holder.tvName.text = member.firstName
                viewHolder.tvArea.text = member.area
                val strRole = member.role
                when {
                    strRole == resources.getString(R.string.LOCAL_ADMIN) -> {
                        holder.tvRole.text = resources.getString(R.string.Local_Admin)
                        Coroutines.io {
                            val name = smartFilterViewModel.getLocalCommunity(member.localCommunityId)
                            Coroutines.main {
                                holder.tvRegion.text = name
                            }
                        }
                    }
                    strRole == resources.getString(R.string.SUB_ADMIN) -> {
                        holder.tvRole.text = resources.getString(R.string.Sub_Admin)
                        Coroutines.io {
                            val name = smartFilterViewModel.getSubCommunity(member.subCommunityId)
                            holder.tvRegion.text = name
                        }
                    }
                    else -> {
                        holder.tvRole.text = resources.getString(R.string.User)
                        holder.tvRegion.text = ""
                    }
                }

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
                    holder.tvType.text = resources.getString(R.string.Family_Head)
                } else {
                    holder.tvType.text = resources.getString(R.string.Member)
                }

                viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                viewHolder.itemView.isActivated = selectedItems.get(i, false)

                applyProfilePicture(holder, member)
                applyClickEvents(holder, i, member)
                applyImportant(viewHolder, member)
                applyIconAnimation(viewHolder, i)
                holder.boomMenuButton.clearBuilders()

                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {
                            createMemberPDF(activity as AppCompatActivity, member, profileDetailViewModel)
                            Handler().post {
                                Utility.startSweetProgress(activity, getString(R.string.expo) + "${member.firstName}" + getString(R.string.DetailList), getString(R.string.please_wait))
                            }
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
                            adapter.setLocationListner(this@AdminsFragment)
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
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.admin_list_item, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstAdmins.size
            }
        }

        val header = LayoutInflater.from(activity).inflate(R.layout.header_admins, container, false)
        tvCount = header.findViewById<TextView>(R.id.tv_count)

        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        val MyLayoutManager = LinearLayoutManager(activity)
        rvAdmins = root.findViewById(R.id.rv_Admins)
        rvAdmins.layoutManager = MyLayoutManager
        rvAdmins.itemAnimator = DefaultItemAnimator()
        adapter.setParallaxHeader(header, rvAdmins)
        rvAdmins.adapter = adapter
        rvAdmins.setHasFixedSize(true)

        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        val member: Member = Gson().fromJson<Member>(loginuser, Member::class.java)
        loginUserSubCommunityId = member.subCommunityId
        loginUserLocalCommunityId = member.localCommunityId

        getSubAdmin()
        return root
    }

    private fun getSubAdmin() {
        count = 1
        val jsonObject = JSONObject()
        jsonObject.put(getString(R.string.start), AppController.mApplication.start)
        jsonObject.put(getString(R.string.length), AppController.mApplication.length)
        val jsonObj = JSONObject()
        jsonObj.put(getString(R.string.role), resources.getString(R.string.SUB_ADMIN))
        jsonObj.put(getString(R.string.sub_community_id), loginUserSubCommunityId)
        jsonObject.put(getString(R.string.filter_by), jsonObj)
        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
        smartFilterViewModel.smartFilterSearch(updated)
        shimmerFrameLayout.startShimmerAnimation()
        shimmerFrameLayout.visibility = View.VISIBLE
        lstAdmins.clear()
        adapter.notifyDataSetChanged()
        Utility.hideKeyboard(activity)
    }

    private fun getLocalAdmin() {
        count = 2
        val jsonObject = JSONObject()
        jsonObject.put(getString(R.string.start), AppController.mApplication.start)
        jsonObject.put(getString(R.string.length), AppController.mApplication.length)
        val jsonObj = JSONObject()
        jsonObj.put(getString(R.string.role), resources.getString(R.string.LOCAL_ADMIN))
        jsonObj.put(getString(R.string.local_community_id), loginUserLocalCommunityId)
        jsonObject.put(getString(R.string.filter_by), jsonObj)
        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
        smartFilterViewModel.smartFilterSearch(updated)

        Handler().postDelayed({
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE
        }, 4000)
    }

    override fun getMembers(response: SmartFilterResponse) {
        if (count == 1) {
            lstAdmins.clear()
            if (response.members != null && response.members.size > 0) {
                lstAdmins.addAll(response.members)
            }
            getLocalAdmin()
        } else if (count == 2) {
            if (response.members != null && response.members.size > 0) {
                lstAdmins.addAll(response.members)
            }
            adapter.notifyDataSetChanged()
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE
            actionMode?.finish()
            selectedItems.clear()
            cancelDialog()
            if (lstAdmins.size == 0) {
                rvAdmins.snackbar(getString(R.string.noFoundNonActives), Snackbar.LENGTH_SHORT)
            }
        }
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {

    }

    override suspend fun getFailure(message: String) {
        if (message.toLowerCase().contains("success")) {
            getSubAdmin()
        } else {
            llRoot.snackbar(getString(R.string.went_wrong), Snackbar.LENGTH_LONG)
        }
    }

    internal inner class ListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.boomMenuButton)
        var tvArea: TextView = v.findViewById(R.id.tv_area)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvType: TextView = v.findViewById(R.id.tv_type)
        var tvRole: TextView = v.findViewById(R.id.tv_role)
        var tvRegion: TextView = v.findViewById(R.id.tv_region)
        var iconProfile: ImageView = v.findViewById(R.id.icon_profile1)
        var iconText: TextView = v.findViewById(R.id.icon_text1)
        var llMobile: LinearLayout = v.findViewById(R.id.llMobile)
        var iconImp: ImageView = v.findViewById(R.id.icon_star)
        var messageContainer: LinearLayout = v.findViewById(R.id.message_container1)
        var iconBack: RelativeLayout = v.findViewById(R.id.icon_back1)
        var iconFront: RelativeLayout = v.findViewById(R.id.icon_front1)
        var ll_email: LinearLayout = itemView.findViewById(R.id.ll_email)
        var ivMobile: ImageView = v.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = v.findViewById(R.id.iv_email)
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun applyIconAnimation(holder: ListViewHolder, position: Int) {
        if (selectedItems.get(position, false)) {
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
            if (reverseAllAnimations && animationItemsIndex.get(position, false) || currentSelectedIndex == position) {
                FlipAnimator.flipView(activity, holder.iconBack, holder.iconFront, false)
                resetCurrentIndex()
            }
        }
    }

    private fun applyImportant(holder: ListViewHolder, member: Member) {

        roomMemberViewModel.getRoomMember(Integer.parseInt(member.id)).observe(activity as AppCompatActivity, Observer {
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

    private fun applyClickEvents(holder: ListViewHolder, position: Int, member: Member) {

        holder.iconProfile.setOnClickListener {
            try {
                val path = getString(R.string.base_url_original) + "" + member.profilePic
                Log.d(TAG, "path: $path")
                openImageDialog(activity as AppCompatActivity, path)
            } catch (e: Exception) {
                e.message
            }
        }

        holder.iconImp.setOnClickListener {

            val member: Member = lstAdmins.get(position)
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

        holder.messageContainer.setOnClickListener { view -> onMessageRowClicked(position, holder.itemView) }

        holder.messageContainer.setOnLongClickListener { view ->
            enableActionMode(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    private fun onMessageRowClicked(position: Int, v: View) {

        if (getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstAdmins.get(position))
            startActivity(intent)
            //  Utility.fade(activity)
        }
    }

    private fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = getSelectedItemCount()

        if (count <= 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private fun toggleSelected(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos)
            animationItemsIndex.delete(pos)
        } else {
            selectedItems.put(pos, true)
            animationItemsIndex.put(pos, true)
        }

        adapter.notifyItemChanged(pos + 1)
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    @SuppressLint("CheckResult")
    private fun applyProfilePicture(holder: ListViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            if (member.profilePic.isNotEmpty()) {
                holder.iconProfile.isClickable = true
                val url = resources.getString(R.string.base_url_thumb) + member.profilePic
                Glide.with(activity!!).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(holder.iconProfile)
            } else {
                holder.iconProfile.isClickable = false
            }
            holder.iconProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.iconProfile.isClickable = false
            holder.iconProfile.setImageResource(R.drawable.bg_circle)
            holder.iconProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        actionMode?.finish()
        selectedItems.clear()
    }


    override fun changeRole(role: String?) {
        val selectedItemPositions = getSelectedItems()
        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.you_sure))
                .setContentText("${selectedItemPositions.size}" + getString(R.string.profilerole) + "'$role'!")
                .setConfirmText(getString(R.string.YesPleaseCity))
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
                        changed = getString(R.string.User)
                    }

                    jsonObject.put(getString(R.string.role), changed)
                    jsonObject.put(getString(R.string.local_community_id), loginUserLocalCommunityId)
                    jsonObject.put(getString(R.string.sub_community_id), loginUserSubCommunityId)

                    var Ids = ""
                    for (index in selectedItemPositions) {
                        Ids += lstAdmins[index].id + ","
                    }

                    Ids = Ids.substring(0, Ids.length - 1)
                    jsonObject.put(getString(R.string.idList), Ids)
                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                    lstAdmins.clear()
                    tvCount.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    shimmerFrameLayout.startShimmerAnimation()
                    shimmerFrameLayout.visibility = View.VISIBLE
                    roomMemberViewModel.changeRole(updated)

                }
                .setCancelClickListener {
                    it.dismiss()
                }
                .show()
    }


    override fun cancelDialog() {
        actionMode?.finish()
        changeRoleDialog?.dismiss()
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            val locButton = menu.findItem(R.id.action_location)
            locButton.isVisible = false
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
                                .setContentText(getString(R.string.wantDisable) + "${selectedItemPositions.size}" + getString(R.string.profileAdmins))
                                .setConfirmText(getString(R.string.YesDisable))
                                .setCancelText(getString(R.string.NoAdmins))
                                .setConfirmClickListener {
                                    it.dismiss()

                                    val jsonObject = JSONObject()
                                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                    jsonObject.put("status", "0")

                                    var Ids = ""
                                    for (index in selectedItemPositions) {
                                        Ids += lstAdmins[index].id + ","
                                    }

                                    Ids = Ids.substring(0, Ids.length - 1)
                                    jsonObject.put(getString(R.string.idList), Ids)
                                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                                    lstAdmins.clear()
                                    tvCount.visibility = View.GONE
                                    adapter.notifyDataSetChanged()
                                    shimmerFrameLayout.startShimmerAnimation()
                                    shimmerFrameLayout.visibility = View.VISIBLE
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
                        adapter.setChangeRoleListner(this@AdminsFragment)
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
                    R.id.action_select_all -> {
                        clearSelections()
                        for (i in lstAdmins.indices) {
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
            rvAdmins.post {
                resetAnimationIndex()
            }
        }
    }

    private fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }


    fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex.clear()
        }
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

}