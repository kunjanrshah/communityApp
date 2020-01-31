package com.krs.community.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmnetSharedLocationBinding
import com.krs.community.entities.RoomMember
import com.krs.community.interfaces.EditMemberListener
import com.krs.community.interfaces.RoomMemberListener
import com.krs.community.interfaces.SharedProfileListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SharedProfileResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
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

class SharedLocationFragment : Fragment(), KodeinAware, LocationAdapter.SetLocationListner, SharedProfileListener, RoomMemberListener, EditMemberListener {
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray = SparseBooleanArray()
    private var reverseAllAnimations = false
    private val members: MutableList<Member> = ArrayList()

    override val kodein by kodein()

    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var filterViewModel: SmartFilterViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel

    private val filterViewModelFactory: SmartFilterViewModelFactory by instance()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance()
    private val roomMemberViewModelFactory: RoomMemberViewModelFactory by instance()

    private lateinit var binding: FragmnetSharedLocationBinding
    private var setLocationDialog: DialogPlus? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorBG, false)
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragmnet_shared_location, container, false)

        roomMemberViewModel = ViewModelProviders.of(this, roomMemberViewModelFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProviders.of(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener = this
        filterViewModel = ViewModelProviders.of(this, filterViewModelFactory).get(SmartFilterViewModel::class.java)
        filterViewModel.sharedProfileListener = this

        actionModeCallback = ActionModeCallback()
        adapter = object : ParallaxRecyclerAdapter<Member>(members) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val member = members[position]
                val holder = viewHolder as ListViewHolder
                val name = member.firstName
                Coroutines.io {
                    val lastname = filterViewModel.getLastNameById(Integer.parseInt(member.subCastId.toString()))
                    holder.tvName.text = "$name $lastname"
                }
                holder.iconText.text = name.substring(0, 1)
                holder.itemView.isActivated = selectedItems.get(position, false)
                holder.tvArea.text = member.area
                holder.tvEmail.text = member.emailAddress
                holder.tvMobile.text = member.mobile
                holder.imgLocation.visibility = View.GONE
                holder.tvUpdate.text = "updated " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)

                if (member.headId.equals("0")) {
                    holder.tvRole.text = resources.getString(R.string.Family_Head)
                } else {
                    holder.tvRole.text = resources.getString(R.string.Member)
                }

                holder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {
                            createMemberPDF(activity as AppCompatActivity, member, profileDetailViewModel)
                            Handler().post {
                                Utility.startSweetProgress(activity, "Exporting ${member.firstName}'s Details", getString(R.string.please_wait))
                            }
                            Handler().postDelayed({
                                Utility.hideSweetProgress()
                            }, 5000)
                        } else if (it == 1) {
                            val intent = Intent(activity, FamilyTreeListActivity::class.java)
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
                            val intent = Intent(activity, QRCodeActivity::class.java)
                            intent.putExtras(mBundle)
                            startActivity(intent)
                            Utility.fade(activity)
                        } else if (it == 4) {
                            shareDetails(activity, viewHolder.tvName.text.toString(), member.mobile, member.emailAddress, viewHolder.tvArea.text.toString(), member.address)
                        } else if (it == 5) {
                            val adapter: LocationAdapter = LocationAdapter(context as AppCompatActivity, member)
                            adapter.setLocationListner(this@SharedLocationFragment)
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

                applyIconAnimation(holder, position)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position)
                applyImportant(viewHolder, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return members.size
            }
        }
        val layoutManager = LinearLayoutManager(activity)

        binding.rvLocation.layoutManager = layoutManager
        binding.rvLocation.itemAnimator = DefaultItemAnimator()
        binding.rvLocation.setHasFixedSize(true)
        val header = LayoutInflater.from(activity).inflate(R.layout.header_shared, container, false)
        val tvCount = header.findViewById<ImageView>(R.id.tv_count)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        adapter.setParallaxHeader(header, binding.rvLocation)
        binding.rvLocation.adapter = adapter
        getSharedProfiles()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    private fun getSharedProfiles() {

        if (AppController.mApplication.start == 0) {
            binding.shimmerViewContainer.startShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.VISIBLE
        }
        val jsonObj = JSONObject()
        jsonObj.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
        jsonObj.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
        jsonObj.put(getString(R.string.id), Guru.getString(getString(R.string.user_id), ""))
        val updated = JsonParser().parse(jsonObj.toString()) as JsonObject
        filterViewModel.getSharedProfiles(updated)

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

    private fun applyImportant(holder: ListViewHolder, member: Member) {

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

    private fun applyClickEvents(holder: ListViewHolder, position: Int) {
        holder.imgProfile.setOnClickListener {
            try {
                val path = getString(R.string.base_url_original) + "" + members.get(position).profilePic
                // Log.d(TAG, "path: $path")
                openImageDialog(activity as AppCompatActivity, path)
            } catch (e: Exception) {
                e.message
            }
        }

        holder.iconImp.setOnClickListener {

            val member: Member = members.get(position)
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

        holder.messageContainer.setOnClickListener { onMessageRowClicked(position) }
        holder.messageContainer.setOnLongClickListener { view ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: ListViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            val url = resources.getString(R.string.base_url_thumb) + member.profilePic
            //Log.d(TAG,"url: "+url)
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

    private fun applyIconAnimation(holder: ListViewHolder, position: Int) {
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

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun onMessageRowClicked(position: Int) {
        if (selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), members.get(position))
            startActivity(intent)
            Utility.fade(activity)
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = selectedItemCount
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
        }
    }

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private fun removeData(position: Int) {
        members.removeAt(position)
        resetCurrentIndex()
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions[i])
        }
        adapter.notifyDataSetChanged()
    }

    private fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private val selectedItemCount: Int
        get() = selectedItems.size()

    private inner class ListViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v), View.OnLongClickListener {

        val boomMenuButton: BoomMenuButton = itemView.findViewById(R.id.bmb1)
        var imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvArea: TextView = itemView.findViewById(R.id.tv_area)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvMobile: TextView = itemView.findViewById(R.id.tv_mobile)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        var iconImp: ImageView = itemView.findViewById(R.id.icon_star)
        var imgLocation: ImageView = itemView.findViewById(R.id.img_location)
        var iconBack: RelativeLayout = itemView.findViewById(R.id.icon_back)
        var iconFront: RelativeLayout = itemView.findViewById(R.id.icon_front)
        var iconText: TextView = itemView.findViewById(R.id.icon_text)
        var messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
        var llMobile: LinearLayout = itemView.findViewById(R.id.ll_mobile)
        var tvUpdate: TextView = itemView.findViewById(R.id.tv_update)

        override fun onLongClick(v: View): Boolean {
            enableActionMode(adapterPosition)
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }

        init {
            v.setOnLongClickListener(this)
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.shared_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_private -> {
                    val selectedItemPositions = getSelectedItems()
                    SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.you_sure))
                            .setContentText("Private your location to " + selectedItemPositions.size + " Profiles!")
                            .setConfirmText("Yes,Private it!")
                            .setCancelText("No")
                            .setConfirmClickListener {
                                it.dismiss()
                                val jsonObject = JSONObject()
                                jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.user_id), ""))

                                val loginuser = Guru.getString(getString(R.string.loginUser), "")
                                val loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)

                                val loginSharedIds = loginMember.sharingId.split(',')
                                val sharedId = loginSharedIds.toMutableList()
                                for (index in selectedItemPositions) {
                                    if (loginSharedIds.contains(members[index].id)) {
                                        sharedId.remove(members[index].id)
                                    }
                                }
                                val Ids = sharedId.toString().substring(1, sharedId.toString().length - 1)
                                jsonObject.put(getString(R.string.sharing_id), Ids)
                                val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                                binding.shimmerViewContainer.startShimmerAnimation()
                                binding.shimmerViewContainer.visibility = View.VISIBLE
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
                    for (i in members.indices) {
                        enableActionMode(i)
                    }
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            binding.rvLocation.post {
                resetAnimationIndex()
                adapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        private var currentSelectedIndex = -1
    }

    override fun getMembers(response: SharedProfileResponse) {
        if (response.success) {
            if (response.members != null) {
                if (response.members.size > 0) {
                    members.clear()
                    members.addAll(response.members)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.rvLocation.snackbar(response.message.toString(), Snackbar.LENGTH_LONG)

        Handler().post {
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE
        }
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {

    }

    override fun getScanResult(response: SmartFilterResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUpdateOrAddResult(response: UpdateProfileResponse) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE

        if (response.success) {
            if (response.member != null) {
                Guru.putString(getString(R.string.loginUser), Gson().toJson(response.member))
            }
            binding.rvLocation.snackbar("Location private successfully", Snackbar.LENGTH_LONG)
        }
        deleteMessages()
        clearSelections()
        actionMode?.finish()
    }

    override suspend fun getFailure(message: String) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    override fun cancelDialog() {
        setLocationDialog?.dismiss()
    }
}