package com.krs.community.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.ExportAdapter
import com.krs.community.adapter.LocationAdapter
import com.krs.community.adapter.MyRoleAdapter
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.app.NotificationBadge
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.ByKeywordListener
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.hideKeyboard
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodel.SmartSearchViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.krs.community.viewmodelfactory.SmartSearchViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus

import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchListFragment : Fragment(), KodeinAware, ByKeywordListener, ParallaxRecyclerAdapter.OnLoadMore, MyRoleAdapter.iChangeRoleListner, RoomMemberListener, LocationAdapter.SetLocationListner, ExportAdapter.exportPdfListener {

    private lateinit var rvSearch: RecyclerView
    private lateinit var frameRoot: FrameLayout
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var multiSearchView: MultiSearchView
    private lateinit var rvAdapter: ParallaxRecyclerAdapter<Member>
    private var TAG: String? = SearchListFragment::class.qualifiedName

    private lateinit var smartSearchViewModel: SmartSearchViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel

    private val smartSearchViewModelFactory: SmartSearchViewModelFactory by instance<SmartSearchViewModelFactory>()
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private val roomMemberFactory: RoomMemberViewModelFactory by instance<RoomMemberViewModelFactory>()

    override val kodein by kodein()
    private val lstMembers = ArrayList<Member>()
    private lateinit var tvRecords: TextView
    private lateinit var llLabel: LinearLayout
    private lateinit var ivExport: ImageView
    private lateinit var searchWord: String
    private var selectedPosition = 0
    private val lstKeyword = ArrayList<String>()
    private var changeRoleDialog: DialogPlus? = null
    private var setLocationDialog: DialogPlus? = null
    private var exportDialog: DialogPlus? = null
    private var reverseAllAnimations = false
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray = SparseBooleanArray()
    private var currentSelectedIndex = -1
    private var actionMode: ActionMode? = null
    private lateinit var actionModeCallback: ActionModeCallback
    private var loginMember: Member? = null
    private var snackbar: Snackbar? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_list, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, SearchListFragment::class.simpleName)
        mApp.facebookAnalytics(context, SearchListFragment::class.simpleName)

        smartSearchViewModel = ViewModelProvider(this, smartSearchViewModelFactory).get(SmartSearchViewModel::class.java)
        roomMemberViewModel = ViewModelProvider(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProvider(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)

        smartSearchViewModel.mByKeywordListener = this
        roomMemberViewModel.mRoomMemberListener = this

        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)

        frameRoot = rootView.findViewById(R.id.frameRoot)
        rvSearch = rootView.findViewById(R.id.rv_search)
        rvSearch.layoutManager = LinearLayoutManager(activity)
        rvSearch.setHasFixedSize(true)
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        actionModeCallback = ActionModeCallback()
        (activity as AppCompatActivity).supportActionBar?.hide()

        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_search, container, false)
        multiSearchView = header.findViewById(R.id.multiSearchView)
        tvRecords = header.findViewById(R.id.tv_record)
        llLabel = header.findViewById(R.id.ll_label)
        AppController.mApplication.start = 0
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener {
            hideKeyboard(activity)
            Utility.backNavigation(activity)
        }

        ivExport = header.findViewById(R.id.iv_export)
        ivExport.setOnClickListener {
            if (Utility.checkExternalStoragePermission(activity as AppCompatActivity)) {
                val adapter: ExportAdapter = ExportAdapter(activity as AppCompatActivity)
                adapter.setExportListner(this@SearchListFragment)
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

        rvAdapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>?): Int {
                return lstMembers.size
            }

            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder?, adapter: ParallaxRecyclerAdapter<Member>?, position: Int) {
                val viewHolder: MyViewHolder = viewHolder as MyViewHolder

                val member = lstMembers[position]
                var count = member.membersCount
                if (count != 0) {
                    count += 1
                }
                viewHolder.badge.setNumber(count)
                viewHolder.tvName.text = member.firstName
                smartSearchViewModel.getLastName(member.subCastId.toInt()).observeForever {
                    viewHolder.tvName.text = member.firstName + " " + it
                }

                if (!member.cityId.isNullOrEmpty()) {
                    smartSearchViewModel.getCityNamebyId(member.cityId).observeForever {
                        viewHolder.tvArea.text = member.area + " " + it
                    }
                }
                Coroutines.io {
                    if (!member.nativePlaceId.isNullOrEmpty()) {
                        val native = smartSearchViewModel.getNativeById(Integer.parseInt(member.nativePlaceId.trim()))
                        Coroutines.main {
                            viewHolder.tvNative.text = "Native: $native"
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
                if (member.gender.equals("Male")) {
                    viewHolder.ivGender.setBackgroundResource(R.drawable.male)
                } else {
                    viewHolder.ivGender.setBackgroundResource(R.drawable.female)
                }
                if (member.status == "2") {
                    viewHolder.ivVerify.visibility = View.VISIBLE
                } else {
                    viewHolder.ivVerify.visibility = View.GONE
                }
                if (member.headId == "0") {
                    viewHolder.tvRole.text = resources.getString(R.string.Family_Head)
                } else {
                    viewHolder.tvRole.text = resources.getString(R.string.Member)
                }

                if (member.updatedDt.isNotEmpty()) {
                    if (member.updatedDt.contains(getString(R.string.zero_date))) {
                        viewHolder.tvUpdate.text = getString(R.string.not_updated)
                    } else {
                        viewHolder.tvUpdate.text = getString(R.string.UpdateList) + " " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    }
                }

                viewHolder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {
                            createMemberPDF(activity as AppCompatActivity, member, profileDetailViewModel)
                            Handler().post(Runnable {
                                Utility.startSweetProgress(activity, getString(R.string.ExportList) + "${member.firstName}" + getString(R.string.DetailsList), getString(R.string.please_wait))
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
                            adapter.setLocationListner(this@SearchListFragment)
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

                viewHolder.boomMenuButton.setOnClickListener { v -> viewHolder.boomMenuButton.boom() }

                viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                viewHolder.itemView.isActivated = selectedItems.get(position, false)

                viewHolder.lstFound.setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(activity)
                linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

                viewHolder.lstFound.adapter = FoundListAdapter(activity as AppCompatActivity, member.matches)
                viewHolder.lstFound.layoutManager = linearLayoutManager

                applyIconAnimation(viewHolder, position)
                applyImportant(viewHolder, member)
                applyClickEvents(viewHolder, position, member)
                applyProfilePicture(viewHolder, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>?, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_list_search, viewGroup, false))
            }
        }

        rvAdapter.setParallaxHeader(header, rvSearch)
        rvSearch.adapter = rvAdapter
        rvAdapter.setContext(this)

        multiSearchView.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener {
            override fun onTextChanged(index: Int, s: CharSequence) {
                //   Toast.makeText(getActivity(), "onTextChanged", Toast.LENGTH_SHORT).show();
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                lstKeyword.add(s.toString())
                searchWord = s.toString()
                DashboardActivity.stop = false
                getMembersByKeyword()
            }

            override fun onSearchItemRemoved(index: Int) {
                searchWord = ""
                if (lstKeyword.size > 0) {
                    try {
                        lstKeyword.removeAt(index)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    if (lstKeyword.size > 0) {
                        searchWord = when (lstKeyword.size) {
                            1 -> {
                                lstKeyword[0]
                            }
                            index -> {
                                lstKeyword[index - 1]
                            }
                            else -> {
                                lstKeyword[index]
                            }
                        }
                    }
                }
                if (searchWord.isNotEmpty()) {
                    DashboardActivity.stop = false
                    getMembersByKeyword()
                } else {
                    lstMembers.clear()
                    mShimmerViewContainer.stopShimmerAnimation()
                    mShimmerViewContainer.visibility = View.GONE
                    llLabel.visibility = View.VISIBLE
                    ivExport.visibility = View.GONE
                    tvRecords.visibility = View.GONE
                    rvAdapter.notifyDataSetChanged()
                }
            }

            override fun onItemSelected(index: Int, s: CharSequence) {
                searchWord = s.toString()
                DashboardActivity.stop = false
                getMembersByKeyword()
            }
        })

        DashboardActivity.stop = true

        val keyword = arguments?.getString("keyword")
        if (!keyword.isNullOrEmpty()) {
            searchWord = keyword
            DashboardActivity.stop = false
            getMembersByKeyword()
        }

        return rootView
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    class FoundListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var txtName: TextView = v.findViewById(R.id.txt_name)
    }

    class FoundListAdapter(val context: Context, val list: List<String>) : RecyclerView.Adapter<FoundListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.found_search, parent, false)
            return FoundListViewHolder(view)
        }

        override fun onBindViewHolder(holder: FoundListViewHolder, position: Int) {
            val filter = list[position].replace("_", " ")
            if (filter.equals("sub cast")) {
                holder.txtName.text = "last name"
            } else {
                holder.txtName.text = filter
            }

            holder.txtName.setBackgroundResource(R.drawable.filter_found_search)
            holder.txtName.setTextColor(getColor(context, R.color.black1))
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    private fun getMembersByKeyword() {
        if (isNetworkConnected(activity as AppCompatActivity)) {
            if (!DashboardActivity.stop) {
                //  lstMembers.clear()
                tvRecords.visibility = View.GONE
                llLabel.visibility = View.GONE
                if (loginMember?.role.isNullOrEmpty() || loginMember?.role == getString(R.string.USER) || loginMember?.role == getString(R.string.LOCAL_ADMIN)) {
                    ivExport.visibility = View.GONE
                } else {
                    ivExport.visibility = View.VISIBLE
                }
//                rvAdapter.notifyDataSetChanged()
                DashboardActivity.stop = true
                val mJSONObject = JSONObject()
                mJSONObject.put(getString(R.string.start), AppController.mApplication.start)
                mJSONObject.put(getString(R.string.length), AppController.mApplication.length)
                mJSONObject.put(getString(R.string.filter_by), searchWord)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                if (AppController.mApplication.start == 0) {
                    mShimmerViewContainer.startShimmerAnimation()
                    mShimmerViewContainer.visibility = View.VISIBLE
                } else {
                    snackbar = Snackbar.make(rvSearch, getString(R.string.load_more), Snackbar.LENGTH_INDEFINITE)
                    snackbar?.show()
                }
                smartSearchViewModel.getMemberByKeywords(updated)
            }
        }
    }


    override fun loadApi() {
        AppController.mApplication.start = (lstMembers.size + 1)
        getMembersByKeyword()
    }

    override fun refreshList() {
        rvAdapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {

    }

    override fun getMembers(response: searchByKeywordsResponse) {
        snackbar?.dismiss()
        DashboardActivity.stop = false
        mShimmerViewContainer.stopShimmerAnimation()
        mShimmerViewContainer.visibility = View.GONE
        actionMode?.finish()
        selectedItems.clear()
        cancelDialog()
        hideKeyboard(activity)

        if (response.success) {
            //lstMembers.clear()
            rvSearch.visibility = View.VISIBLE
            // AppController.mApplication.start = 0
            for (item in response.member) {
                lstMembers.add(item)
            }
            //  rvAdapter.notifyDataSetChanged()
            selectedPosition = AppController.mApplication.start
            rvSearch.layoutManager?.scrollToPosition(selectedPosition)

            if (Integer.parseInt(response.totalRecords) == 0) {
                DashboardActivity.stop = true
                    val gif: Int = R.drawable.gif_dialog
                    TTFancyGifDialog.Builder(activity)
                            .setMessage(getString(R.string.noFoundNonActives))
                            .setPositiveBtnText(getString(R.string.ok))
                            .setPositiveBtnBackground("#843f52")
                            .setGifResource(gif)
                            .isCancellable(false)
                            .OnPositiveClicked {

                            }
                            .build()
                    true
            } else if (response.member.size < AppController.mApplication.length) {
                    Snackbar.make(frameRoot, getString(R.string.endRecord), Snackbar.LENGTH_LONG).show()
                DashboardActivity.stop = true
            }

            if (lstMembers.size > 0) {
                tvRecords.text = getString(R.string.RecordList) + " " + response.totalRecords
                tvRecords.visibility = View.VISIBLE
                llLabel.visibility = View.GONE
                if (loginMember?.role.isNullOrEmpty() || loginMember?.role == getString(R.string.USER) || loginMember?.role == getString(R.string.LOCAL_ADMIN)) {
                    ivExport.visibility = View.GONE
                } else {
                    ivExport.visibility = View.VISIBLE
                }
            } else {
                llLabel.visibility = View.VISIBLE
                tvRecords.visibility = View.GONE
                ivExport.visibility = View.GONE
                DashboardActivity.stop = true
                mShimmerViewContainer.stopShimmerAnimation()
                mShimmerViewContainer.visibility = View.GONE
            }
        } else {
            // rvSearch.visibility = View.GONE
            // tvRecords.visibility = View.GONE
            ivExport.visibility = View.GONE
            DashboardActivity.stop = true
            llLabel.visibility = View.VISIBLE

            mShimmerViewContainer.stopShimmerAnimation()
            mShimmerViewContainer.visibility = View.GONE

            //  val gif: Int = R.drawable.gif_no_record
            val gif: Int = R.drawable.gif_dialog
            TTFancyGifDialog.Builder(activity)
                    //.setTitle(getString(R.string.you_sure))
                    .setMessage(getString(R.string.noFoundNonActives))
                    .setPositiveBtnText(getString(R.string.ok))
                    .setPositiveBtnBackground("#843f52")
                    .setGifResource(gif)
                    .isCancellable(false)
                    .OnPositiveClicked {

                    }
                    .build()

            //  Snackbar.make(frameRoot, getString(R.string.endRecord), Snackbar.LENGTH_LONG).show()
        }
    }

    override suspend fun getFailure(message: String) {
        Log.d(TAG, "getFailure: $message")
        DashboardActivity.stop = false
        snackbar?.dismiss()
        if (message.toLowerCase().contains("successfully")) {
            getMembersByKeyword()
        } else {
            activity?.runOnUiThread {
                if (mShimmerViewContainer.isAnimationStarted) {
                    mShimmerViewContainer.stopShimmerAnimation()
                }
                mShimmerViewContainer.visibility = View.GONE
                //    tvRecords.visibility = View.GONE
                llLabel.visibility = View.VISIBLE
                ivExport.visibility = View.GONE
                //  rvSearch.visibility = View.GONE
                //DashboardActivity.stop = true
                mShimmerViewContainer.stopShimmerAnimation()
                mShimmerViewContainer.visibility = View.GONE
            }

        }

        frameRoot.snackbar(message, Snackbar.LENGTH_LONG)
        Utility.hideKeyboard(activity)
    }

    private fun getSelectedItemCount(): Int {
        return selectedItems.size()
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

        rvAdapter.notifyItemChanged(pos + 1)
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

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex.clear()
        }
    }

    private fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private fun onMessageRowClicked(position: Int, v: View) {

        if (getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMembers.get(position))
            DashboardActivity.stop = false
            startActivityForResult(intent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == 102) {
            getMembersByKeyword()
        }
    }

    private fun applyImportant(holder: MyViewHolder, member: Member) {

        roomMemberViewModel.getRoomMember(Integer.parseInt(member.id)).observe(activity as AppCompatActivity, Observer {
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

    private fun applyClickEvents(holder: MyViewHolder, position: Int, member: Member) {

        holder.iconImp.setOnClickListener {

            val member: Member = lstMembers.get(position)
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

        holder.imgProfile.setOnClickListener { view ->
            try {
                val path = getString(R.string.base_url_original) + "" + member.profilePic
                Log.d(TAG, "path: $path")
                openImageDialog(activity as AppCompatActivity, path)
            } catch (e: Exception) {
                e.message
            }
        }

        holder.messageContainer.setOnClickListener { view -> onMessageRowClicked(position, holder.itemView) }

        holder.messageContainer.setOnLongClickListener { view ->
            enableActionMode(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    @SuppressLint("CheckResult")
    private fun applyProfilePicture(holder: MyViewHolder, member: Member) {
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


    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {
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

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        rvAdapter.notifyDataSetChanged()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }


    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener {
        var iconText: TextView = view.findViewById(R.id.icon_text1)
        var tvName: TextView = view.findViewById(R.id.tv_name1)
        var tvArea: TextView = view.findViewById(R.id.tv_area)
        var tvEmail: TextView = view.findViewById(R.id.tv_email)
        var tvMobile: TextView = view.findViewById(R.id.tv_mobile)
        var tvRole: TextView = view.findViewById(R.id.tv_role)
        var iconImp: ImageView = view.findViewById(R.id.icon_star)
        var tvUpdate: TextView = view.findViewById(R.id.tv_update)
        var imgProfile: ImageView = view.findViewById(R.id.icon_profile1)
        var messageContainer: LinearLayout = view.findViewById(R.id.message_container1)
        var iconBack: RelativeLayout = view.findViewById(R.id.icon_back1)
        var iconFront: RelativeLayout = view.findViewById(R.id.icon_front1)
        var boomMenuButton: BoomMenuButton = view.findViewById(R.id.boomMenuButton1)
        var lstFound: RecyclerView = view.findViewById(R.id.lst_found)
        var llMobile: LinearLayout = view.findViewById(R.id.ll_mobile)
        var ivMobile: ImageView = view.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = view.findViewById(R.id.iv_email)
        var ivGender: ImageView = itemView.findViewById(R.id.iv_gender)
        var ivVerify: ImageView = itemView.findViewById(R.id.iv_verify)
        var badge: NotificationBadge = itemView.findViewById(R.id.badge)
        var tvNative: TextView = itemView.findViewById(R.id.tv_native)
        init {
            view.setOnLongClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            enableActionMode(adapterPosition)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    override fun onStart() {
        super.onStart()
        mShimmerViewContainer.startShimmerAnimation()
    }


    override fun onPause() {
        super.onPause()
        actionMode?.finish()
        selectedItems.clear()
        mShimmerViewContainer.stopShimmerAnimation()
        super.onStop()
        Handler().postDelayed({
            hideKeyboard(activity)
        }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            val menuDelete = menu.findItem(R.id.action_delete)
            val menuRole = menu.findItem(R.id.action_my_role)

            if (loginMember?.role.isNullOrEmpty() || loginMember?.role == getString(R.string.USER)) {
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
                                .setConfirmText(getString(R.string.YesDisable))
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
                                    tvRecords.visibility = View.GONE
                                    rvAdapter.notifyDataSetChanged()
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
                        adapter.setChangeRoleListner(this@SearchListFragment)
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
            rvSearch.post {
                resetAnimationIndex()
                // mAdapter.notifyDataSetChanged();
            }
        }
    }

    override fun changeRole(role: String) {

        val selectedItemPositions = getSelectedItems()
        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.you_sure))
                .setContentText("${selectedItemPositions.size}" + getString(R.string.ProfileList) + "'$role'!")
                .setConfirmText(getString(R.string.YesPlList))
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
                    tvRecords.visibility = View.GONE
                    rvAdapter.notifyDataSetChanged()
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
            rvSearch.snackbar(getString(R.string.NoRecordList), Snackbar.LENGTH_SHORT)
        }
    }

    override fun cancelDialog() {
        actionMode?.finish()
        setLocationDialog?.dismiss()
        changeRoleDialog?.dismiss()
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }
}
