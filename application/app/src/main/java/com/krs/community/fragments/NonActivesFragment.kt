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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.app.AppController
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.ByFilterListener
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.krs.community.utils.openImageDialog
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.krs.community.viewmodelfactory.SmartFilterViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class NonActivesFragment : Fragment(), KodeinAware, RoomMemberListener, ByFilterListener, ParallaxRecyclerAdapter.OnLoadMore {

    private lateinit var rvSearch: RecyclerView
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private val lstMembers: MutableList<Member> = ArrayList()
    private lateinit var actionModeCallback: ActionModeCallback
    private var actionMode: ActionMode? = null
    private lateinit var selectedItems: SparseBooleanArray
    private lateinit var animationItemsIndex: SparseBooleanArray
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    override val kodein by kodein()
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private lateinit var tvCount: TextView
    private lateinit var llRoot: LinearLayout
    private var selectedPosition = 0

    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private lateinit var smartFilterViewModel: SmartFilterViewModel

    private val smartFilterViewModelFactory: SmartFilterViewModelFactory by instance()
    private val roomMemberFactory: RoomMemberViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_nonactives, container, false)

        val mApp =(activity as AppCompatActivity). applicationContext as AppController
        mApp.FirebaseAnalytics(context, NonActivesFragment::class.simpleName)


        smartFilterViewModel = ViewModelProvider(this, smartFilterViewModelFactory).get(SmartFilterViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        roomMemberViewModel.mRoomMemberListener= this
        smartFilterViewModel.mByFilterListener = this
        AppController.mApplication.start=0
        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()
        actionModeCallback = ActionModeCallback()

        rvSearch = root.findViewById(R.id.rv_search)
        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container)
        llRoot = root.findViewById(R.id.ll_root)
        (activity as AppCompatActivity).supportActionBar!!.title = ""

        adapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val member = lstMembers[position]
                val holder = viewHolder as MyViewHolder

                holder.tvName.text = member.firstName
                holder.tvArea.text = member.area
                holder.tvAddr.text = member.address

                if (member.status == "2") {
                    holder.ivVerify.visibility = View.VISIBLE
                    if (member.updatedDt.isNotEmpty()) {
                        holder.tvCreated.text = "Updated " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    }
                } else {
                    holder.ivVerify.visibility = View.GONE
                    if (member.createdDt.isNotEmpty()) {
                        holder.tvCreated.text = "Created " + Utility.changeDateFormat(member.createdDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
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
                        val city = member.area + " " + smartFilterViewModel.getCityNamebyId(member.cityId)
                        Coroutines.main {
                            holder.tvArea.text = city
                        }
                    }
                }

                if (member.mobile.isEmpty()){
                    viewHolder.tvMobile.text = getString(R.string.mobile_not_available)
                    viewHolder.ivMobile.visibility = View.GONE
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                }else{
                    viewHolder.ivMobile.visibility = View.VISIBLE
                    viewHolder.tvMobile.text = member.mobile
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.com_facebook_blue))
                }

                if (member.emailAddress.isEmpty()){
                    viewHolder.ivEmail.visibility = View.GONE
                    viewHolder.tvEmail.text = getString(R.string.email_not_available)
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                }else{
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.red_btn_bg_color))
                    viewHolder.ivEmail.visibility = View.VISIBLE
                    viewHolder.tvEmail.text = member.emailAddress
                }

                holder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                viewHolder.itemView.isActivated = selectedItems.get(position, false)

                applyIconAnimation(holder, position)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position,member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_row_nonactives, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity!!.applicationContext)
        rvSearch.layoutManager = mLayoutManager
        rvSearch.itemAnimator = DefaultItemAnimator()

        val header = LayoutInflater.from(activity).inflate(R.layout.header_nonactives, container, false)
        tvCount = header.findViewById(R.id.tv_count)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        adapter.setParallaxHeader(header, rvSearch)
        adapter.setContext(this)
        rvSearch.adapter = adapter

        DashboardActivity.stop = false
        getNonActivesUsers()

        return root
    }

    private fun getNonActivesUsers() {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            val jsonObject = JSONObject()
            jsonObject.put(getString(R.string.start), AppController.mApplication.start)
            jsonObject.put(getString(R.string.length), AppController.mApplication.length)
            jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
            jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
            smartFilterViewModel.getInActiveRecords(updated)

            if (AppController.mApplication.start == 0) {
                shimmerFrameLayout.startShimmerAnimation()
                shimmerFrameLayout.visibility = View.VISIBLE
                Handler().postDelayed({
                    shimmerFrameLayout.stopShimmerAnimation()
                    shimmerFrameLayout.visibility = View.GONE
                }, 4000)
            }
            Utility.hideKeyboard(activity)
        }
    }

    override fun getMembers(response: SmartFilterResponse) {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility = View.GONE
        actionMode?.finish()
        selectedItems.clear()
        if (response.success) {
            if (response.members.size > 0) {
                tvCount.visibility = View.VISIBLE
                tvCount.text = getString(R.string.mem) + " ${response.totalRecords} " + getString(R.string.found)

                lstMembers.addAll(response.members)
                adapter.notifyDataSetChanged()

               // rvSearch.layoutManager?.scrollToPosition(selectedPosition)
                //selectedPosition = lstMembers.size - 1
                DashboardActivity.stop = false
                if (response.totalRecords <= AppController.mApplication.length) {
                    DashboardActivity.stop = true
                    Snackbar.make(llRoot, getString(R.string.endNonActives), Snackbar.LENGTH_LONG).show()
                }
            } else {
                tvCount.visibility = View.GONE
                DashboardActivity.stop = true
                Snackbar.make(llRoot, getString(R.string.noFoundNonActives), Snackbar.LENGTH_LONG).show()
            }
        } else {
            tvCount.visibility = View.GONE
            DashboardActivity.stop = true
        }
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions[i])
        }
        adapter.notifyDataSetChanged()
    }

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private fun removeData(position: Int) {
        lstMembers.removeAt(position)
        resetCurrentIndex()
    }

    override suspend fun getFailure(message: String) {
        Coroutines.main {
            if(message.contains("success")){
                Utility.startSweetDialog(activity,SweetAlertDialog.SUCCESS_TYPE,getString(R.string.Approved),"${selectedItems.size()} Profiles approved")
                deleteMessages()
                clearSelections()
                actionMode?.finish()
            }else{
                Utility.startSweetDialog(activity,SweetAlertDialog.ERROR_TYPE,getString(R.string.Restricted),message)
            }
        }
    }

    override fun loadApi() {
         if (!DashboardActivity.stop) {
             AppController.mApplication.start = (lstMembers.size+1)
             getNonActivesUsers()
         }
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var iconText: TextView = view.findViewById(R.id.icon_text)
        var tvName: TextView = view.findViewById(R.id.tv_name)
        var imgProfile: ImageView = view.findViewById(R.id.icon_profile)
        var messageContainer: LinearLayout = view.findViewById(R.id.message_container)
        var iconBack: RelativeLayout = view.findViewById(R.id.icon_back)
        var iconFront: RelativeLayout = view.findViewById(R.id.icon_front)
        var tvArea: TextView = view.findViewById(R.id.tv_area)
        var tvMobile: TextView = view.findViewById(R.id.tv_mobile)
        var tvEmail: TextView = view.findViewById(R.id.tv_email)
        var tvAddr: TextView = view.findViewById(R.id.tv_addr)
        var llMobile: LinearLayout = itemView.findViewById(R.id.ll_mobile)
        var ll_email: LinearLayout = itemView.findViewById(R.id.ll_email)
        var ivMobile: ImageView = itemView.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = itemView.findViewById(R.id.iv_email)
        var ivVerify: ImageView = itemView.findViewById(R.id.iv_verify)
        var tvCreated: TextView = itemView.findViewById(R.id.tv_created)
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int,member: Member) {

        holder.messageContainer.setOnClickListener { view: View? -> onMessageRowClicked(position) }
        holder.messageContainer.setOnLongClickListener { view: View ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
        holder.tvMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + holder.tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }
        holder.imgProfile.setOnClickListener {
            if (member.profilePic.isNotEmpty()) {
                holder.imgProfile.isClickable = true
                try {
                    val path = getString(R.string.base_url_original) + "" + member.profilePic
                    Log.d("NonActiveFragment", "path: $path")
                    openImageDialog(activity as AppCompatActivity,path)
                } catch (e: Exception) {
                    e.message
                }
            }else{
                holder.imgProfile.isClickable = false
            }
        }

       }

    fun applyProfilePicture(holder: MyViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            if (member.profilePic.isNotEmpty()) {
                holder.imgProfile.isClickable = true
                val path = getString(R.string.base_url_thumb) + "" + member.profilePic
                Log.d("NonActives", "path: $path")
                try {
                    Glide.with(AppController.mApplication).load(path).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(holder.imgProfile)
                } catch (e: Exception) {
                    e.message
                }
            }
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {
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

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
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

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex.clear()
        }
    }

    private fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
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
        adapter.notifyItemChanged(pos+ 1)
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

    private fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    private fun onMessageRowClicked(position: Int) {
        if (getSelectedItemCount() > 0) {
            enableActionMode(position)
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)!!
        }
        toggleSelection(position)
    }


    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_non_actives, menu)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
                when (item.itemId) {
                    R.id.action_activate -> {
                        val selectedItemPositions = getSelectedItems()
                        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getString(R.string.you_sure))
                                .setContentText("Approve" + " ${selectedItemPositions.size}" + " Profiles!")
                                .setConfirmText(getString(R.string.YesApprovenon))
                                .setCancelText(getString(R.string.no))
                                .setConfirmClickListener {
                                    it.dismiss()

                                    val jsonObject = JSONObject()
                                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                    jsonObject.put("status", "1")

                                    var Ids = ""
                                    for (index in selectedItemPositions) {
                                        Ids += lstMembers[index].id + ","
                                    }

                                    Ids = Ids.substring(0, Ids.length - 1)
                                    jsonObject.put(getString(R.string.idList), Ids)
                                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject

                                    Utility.startSweetProgress(activity,getString(R.string.Restricted),getString(R.string.loading))
                                    roomMemberViewModel.changeStatus(updated)
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
            rvSearch.post {
                resetAnimationIndex()
            }
        }

        fun clearSelections() {
            reverseAllAnimations = true
            selectedItems.clear()
            adapter.notifyDataSetChanged()
        }
    }
}