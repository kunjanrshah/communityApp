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
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.adapter.LocationAdapter
import com.krs.community.adapter.MyRoleAdapter
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentFilterResultBinding
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.EditMemberListener
import com.krs.community.listeners.IbrowseCityRecordsListener
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.FilterBy
import com.krs.community.model.Member
import com.krs.community.model.SearchByCityData
import com.krs.community.model.SearchByCityModel
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.BrowseCityViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodelfactory.BrowseCityViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class SearchCityResult : Fragment(), RoomMemberListener, KodeinAware, IbrowseCityRecordsListener, ParallaxRecyclerAdapter.OnLoadMore, AtoZBottomAdapter.ISortingRecords, MyRoleAdapter.iChangeRoleListner, LocationAdapter.SetLocationListner, EditMemberListener {

    companion object {
        var alpha: String = ""
        var dialog: DialogPlus? = null
    }

    private var selectedPosition = 0

    private lateinit var cityId: String
    private lateinit var cityName: String
    private val members = ArrayList<Member>()
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var selectedItems: SparseBooleanArray
    private lateinit var animationItemsIndex: SparseBooleanArray
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    private val selectedItemCount: Int get() = selectedItems.size()
    private var TAG: String = SearchCityResult::class.java.simpleName

    internal lateinit var browseCityViewModel: BrowseCityViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel

    private val browseCityViewModelFactory: BrowseCityViewModelFactory by instance()
    private val roomMemberViewModelFactory: RoomMemberViewModelFactory by instance()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance()


    override val kodein by kodein()
    private lateinit var tvCount: TextView
    lateinit var binding: FragmentFilterResultBinding

    private var changeRoleDialog: DialogPlus? = null
    private var setLocationDialog: DialogPlus? = null
    private lateinit var ivExport: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_result, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()
        actionModeCallback = ActionModeCallback()

        browseCityViewModel = ViewModelProvider(this, browseCityViewModelFactory).get(BrowseCityViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberViewModelFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProvider(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)

        browseCityViewModel.ibrowseCityRecordsListener = this
        roomMemberViewModel.mRoomMemberListener = this
        profileDetailViewModel.mEditMemberListener = this

        if (this.arguments != null) {
            cityName = this.arguments!!.getString("city_name").toString()
            cityId = this.arguments!!.getString("city_id").toString()
        }

        members.clear()
        adapter = object : ParallaxRecyclerAdapter<Member>(members) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val member = members[position]
                val holder = viewHolder as SearchCityResult.ViewHolder
                val name = member.firstName

                Coroutines.main {
                    val lastname = browseCityViewModel.getLastName(Integer.parseInt(member.subCastId.toString()))
                    holder.tvName.text = "$name $lastname"
                }
                holder.iconText.text = name.substring(0, 1)
                holder.itemView.isActivated = selectedItems.get(position, false)
                holder.tvArea.text = member.area

               /* holder.tvEmail.text = member.emailAddress
                holder.tvMobile.text = member.mobile*/


                if (member.mobile.isEmpty()){
                    viewHolder.tvMobile.text = getString(R.string.mobile_not_available)
                    viewHolder.ivMobile.visibility = View.GONE
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                }else{
                    viewHolder.ivMobile.visibility = View.VISIBLE
                    viewHolder.tvMobile.text = member.mobile
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.com_facebook_blue))
                }
                if (member.gender.equals("Male")) {
                    viewHolder.ivGender.setBackgroundResource(R.drawable.male)
                } else {
                    viewHolder.ivGender.setBackgroundResource(R.drawable.female)
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

                holder.tvUpdate.text = getString(R.string.UpdateCity) + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)

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
                            adapter.setLocationListner(this@SearchCityResult)
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

                holder.boomMenuButton.setOnClickListener({ v -> holder.boomMenuButton.boom() })


                applyIconAnimation(holder, position)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position)
                applyImportant(viewHolder, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return members.size
            }
        }

        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_filter, container, false)
        Log.d(TAG, "City Name: " + cityName)
        val tvTitle = header.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = cityName

        val edtFilterName = header.findViewById<EditText>(R.id.edt_filter_name)
        edtFilterName.visibility = View.GONE

        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v -> Utility.backNavigation(activity) }

        ivExport = header.findViewById<ImageView>(R.id.iv_export)
        ivExport.setOnClickListener {
            if (members.size > 0) {
                Handler().post {
                    Utility.startSweetProgress(activity, getString(R.string.exporting_search_list), getString(R.string.please_wait))
                }
                Handler().postDelayed({
                    Utility.hideSweetProgress()
                }, 7000)
                createMemberListPDF(activity as AppCompatActivity, members, profileDetailViewModel)
            }
        }
        tvCount = header.findViewById(R.id.tv_count)

        /*val ivAtoz = header.findViewById<ImageView>(R.id.iv_atoz)
        ivAtoz.setOnClickListener { v ->
            val adapter = AtoZBottomAdapter(context)
            adapter.setmISortingRecords(this)
            dialog = DialogPlus.newDialog(context)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true,900)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            dialog?.show()
        }*/

        adapter.setParallaxHeader(header, binding.lstFilter)
        binding.lstFilter.layoutManager = LinearLayoutManager(activity)
        binding.lstFilter.adapter = adapter
        adapter.setContext(this)
        DashboardActivity.stop = false
        alpha = ""
        setupList()
        return binding.root
    }

    private fun setupList() {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            val data = SearchByCityData()
            data.start = AppController.mApplication.start.toString()
            data.length = AppController.mApplication.length.toString()
            data.alpha = alpha
            val filterBy = FilterBy()
            filterBy.cityId = cityId
            data.filterBy = filterBy
            if (AppController.mApplication.start == 0) {
                members.clear()
                binding.shimmerViewContainer.startShimmerAnimation()
                binding.shimmerViewContainer.visibility = View.VISIBLE
            }
            browseCityViewModel.fetchRecordsByCity(data)
        }
    }

    override fun getRecords() {
        members.clear()
        DashboardActivity.stop = false
        AppController.mApplication.start = 0
        setupList()
    }

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            AppController.mApplication.start = (members.size + 1)
            setupList()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun getSearchRecords(data: SearchByCityModel) {

        DashboardActivity.stop = false
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
        actionMode?.finish()
        selectedItems.clear()
        cancelDialog()
        Utility.hideKeyboard(activity)

        if (data.success) {
            if (data.members.size > 0) {
                val count = data.totalHead + data.totalMem
                tvCount.text = "Families: ${data.totalHead}, Members: $count"
                ivExport.visibility = View.VISIBLE
                for (user in data.members) {
                    members.add(user)
                }

                adapter.notifyDataSetChanged()
                binding.lstFilter.layoutManager?.scrollToPosition(selectedPosition)
                selectedPosition = members.size - 1
                DashboardActivity.stop = false

                if (data.totalHead <= AppController.mApplication.length) {
                    DashboardActivity.stop = true
                    Snackbar.make(binding.llParent, getString(R.string.EndCity)+ "$alpha"+getString(R.string.RecordCity), Snackbar.LENGTH_LONG).show()
                }

            } else {
                DashboardActivity.stop = true
                ivExport.visibility = View.GONE
                //rootView!!.lstFilter.layoutManager?.scrollToPosition(selectedPosition)
                Snackbar.make(binding.llParent, getString(R.string.EndCity)+ "$alpha"+getString(R.string.RecordCity), Snackbar.LENGTH_LONG).show()
            }
        } else {
            DashboardActivity.stop = false
        }

        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    override fun getScanResult(response: SmartFilterResponse) {
    }

    override fun getUpdateOrAddResult(response: UpdateProfileResponse) {

        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
        clearSelections()
        actionMode?.finish()
        if (response.success) {
            if (response.member != null) {
                Guru.putString(getString(R.string.loginMember), Gson().toJson(response.member))
                adapter.notifyDataSetChanged()
            }
            binding.llParent.snackbar(getString(R.string.LocationCity), Snackbar.LENGTH_LONG)
        }
    }

    override suspend fun getFailure(message: String) {
        try {
            DashboardActivity.stop = false
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE

            if (message.toLowerCase().contains("success")) {
                setupList()
            } else {
                Snackbar.make(binding.llParent, getString(R.string.went_wrong), Snackbar.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
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

    private fun applyClickEvents(holder: ViewHolder, position: Int) {
        holder.imgProfile.setOnClickListener {
            try {
                val path = getString(R.string.base_url_original) + "" + members.get(position).profilePic
                Log.d(TAG, "path: $path")
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

        holder.tvMobile.setOnClickListener {
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


    private fun applyProfilePicture(holder: ViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            val url = resources.getString(R.string.base_url_thumb) + member.profilePic
            Log.d(TAG, "url: " + url)
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

    private fun applyIconAnimation(holder: ViewHolder, position: Int) {
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

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex.clear()
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

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

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
        var tvUpdate: TextView = itemView.findViewById(R.id.tv_update)
        var llMobile: LinearLayout = itemView.findViewById(R.id.ll_mobile)
        var ll_email: LinearLayout = itemView.findViewById(R.id.ll_email)
        var ivMobile: ImageView = itemView.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = itemView.findViewById(R.id.iv_email)
        var ivGender: ImageView = itemView.findViewById(R.id.iv_gender)

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
        (activity as AppCompatActivity).supportActionBar!!.hide()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
        actionMode?.finish()
        selectedItems.clear()
    }


    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {


            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
                when (item.itemId) {

                    R.id.action_location -> {
                        val selectedItemPositions = getSelectedItems()
                        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getString(R.string.you_sure))
                                .setContentText(getString(R.string.ShareCity)+" ${selectedItemPositions.size}"+getString(R.string.ProfileCity))
                                .setConfirmText(getString(R.string.YesCity))
                                .setCancelText(getString(R.string.no))
                                .setConfirmClickListener {
                                    it.dismiss()
                                    val jsonObject = JSONObject()
                                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                    jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.user_id), ""))

                                    var Ids = ""
                                    val loginuser = Guru.getString(getString(R.string.loginMember), "")
                                    val loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)
                                    if (loginMember?.sharingId != null && loginMember.sharingId.isNotEmpty()) {
                                        Ids = loginMember.sharingId + ","
                                    }
                                    val loginSharedIds = Ids.split(',')
                                    for (index in selectedItemPositions) {
                                        if (!loginSharedIds.contains(members[index].id)) {
                                            Ids += members[index].id + ","
                                        }
                                    }
                                    Ids = Ids.substring(0, Ids.length - 1)

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


                    R.id.action_delete -> {
                        val selectedItemPositions = getSelectedItems()
                        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getString(R.string.you_sure))
                                .setContentText(getString(R.string.ShareCity)+" ${selectedItemPositions.size}"+getString(R.string.ProfileCity))
                                .setConfirmText(getString(R.string.YesCity))
                                .setCancelText(getString(R.string.no))
                                .setConfirmClickListener {
                                    it.dismiss()

                                    val jsonObject = JSONObject()
                                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                    jsonObject.put("status", "0")

                                    var Ids = ""
                                    for (index in selectedItemPositions) {
                                        Ids += members[index].id + ","
                                    }

                                    Ids = Ids.substring(0, Ids.length - 1)
                                    jsonObject.put(getString(R.string.idList), Ids)
                                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                                    members.clear()
                                    tvCount.visibility = View.GONE
                                    adapter.notifyDataSetChanged()
                                    binding.shimmerViewContainer.startShimmerAnimation()
                                    binding.shimmerViewContainer.visibility = View.VISIBLE
                                    roomMemberViewModel.changeStatus(updated)

                                }
                                .setCancelClickListener {
                                    it.dismiss()
                                }
                                .show()

                        true
                    }
                    R.id.action_my_role -> {

                        val adapter = MyRoleAdapter(context)
                        adapter.setChangeRoleListner(this@SearchCityResult)
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
                        for (i in members.indices) {
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
            binding.lstFilter.post { resetAnimationIndex() }
        }
    }


    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = selectedItemCount

        if (count <= 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
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

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {
    }

    override fun changeRole(role: String?) {
        val selectedItemPositions = getSelectedItems()
        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.you_sure))
                .setContentText("${selectedItemPositions.size}"+ getString(R.string.ProfileRoleCity) +"'$role'!")
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
                    val loginuser = Guru.getString(getString(R.string.loginMember), "")
                    val loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)
                    jsonObject.put(getString(R.string.role), changed)
                    jsonObject.put(getString(R.string.local_community_id), loginMember?.localCommunityId)
                    jsonObject.put(getString(R.string.sub_community_id), loginMember?.subCommunityId)

                    var Ids = ""
                    for (index in selectedItemPositions) {
                        Ids += members[index].id + ","
                    }

                    Ids = Ids.substring(0, Ids.length - 1)
                    jsonObject.put(getString(R.string.idList), Ids)
                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                    members.clear()
                    tvCount.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    binding.shimmerViewContainer.startShimmerAnimation()
                    binding.shimmerViewContainer.visibility = View.VISIBLE
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
        setLocationDialog?.dismiss()
    }


}
