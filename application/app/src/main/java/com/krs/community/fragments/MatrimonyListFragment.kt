package com.krs.community.fragments

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import com.krs.community.databinding.FragmentMatrimonylistBinding
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
import java.lang.Exception

class MatrimonyListFragment : Fragment(), KodeinAware, ByFilterListener,RoomMemberListener, ParallaxRecyclerAdapter.OnLoadMore,LocationAdapter.SetLocationListner {

    private var lstMembers= ArrayList<Member>()
    override val kodein by kodein()

    private val TAG=MatrimonyListFragment::class.java.simpleName
    private lateinit var smartFilterViewModel: SmartFilterViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private val smartFilterViewModelFactory: SmartFilterViewModelFactory by instance()
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance()
    private val roomMemberFactory: RoomMemberViewModelFactory by instance()
    private lateinit var jsonObj:JSONObject
    private lateinit var binding:FragmentMatrimonylistBinding
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private var setLocationDialog: DialogPlus? = null
    private lateinit var tvRecords: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_matrimonylist, container, false)

        smartFilterViewModel = ViewModelProviders.of(this,smartFilterViewModelFactory).get(SmartFilterViewModel::class.java)
        roomMemberViewModel = ViewModelProviders.of(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        profileDetailViewModel = ViewModelProviders.of(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        roomMemberViewModel.mRoomMemberListener= this
        smartFilterViewModel.mByFilterListener =this
        AppController.mApplication.start=0
        val header = LayoutInflater.from(activity).inflate(R.layout.header_matrimony, container, false)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        val ivExport= header.findViewById<ImageView>(R.id.iv_export)
        tvRecords= header.findViewById<TextView>(R.id.tvCount)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        val edtSearch = header.findViewById<EditText>(R.id.edtSearch)
        edtSearch.setOnTouchListener { v: View?, event: MotionEvent ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if ((event.rawX+35) >= edtSearch.right - edtSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    activity?.let { openFilter(it,smartFilterViewModel) }
                    return@setOnTouchListener true
                }
            }
            false
        }

        edtSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                jsonObj=JSONObject()
                jsonObj.put(getString(R.string.first_name),edtSearch.text)
                DashboardActivity.stop = false
                searchMatrimonyList(jsonObj)
                true
            }
            false
        }

        ivExport.setOnClickListener {
            if (lstMembers.size > 0) {

                    Handler().post {
                        Utility.startSweetProgress(activity, getString(R.string.exporting_search_list), getString(R.string.please_wait))
                    }
                    createMemberListPDF(activity as AppCompatActivity, lstMembers, profileDetailViewModel)
                    Handler().postDelayed({
                        Utility.hideSweetProgress()
                    }, 7000)
            }
        }

        adapter= object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val holder = viewHolder as ListViewHolder
                val member = lstMembers[position]
                holder.tvName.text = member.firstName
                Coroutines.io {
                    holder.tvName.text=member.firstName+" "+smartFilterViewModel.getLastNameById(member.subCastId.toInt())
                }
                if(!member.cityId.isNullOrEmpty()){
                    Coroutines.io {
                        holder.tvArea.text = member.area+" "+smartFilterViewModel.getCityNamebyId(member.cityId)
                    }
                }
                if(member.gender.equals("Male")){
                    holder.ivGender.setBackgroundResource(R.drawable.male)
                }else{
                    holder.ivGender.setBackgroundResource(R.drawable.female)
                }

                Coroutines.io {
                    if(!member.head_sub_cast_id.isNullOrEmpty() && !member.head_name.isNullOrEmpty()){
                        holder.txtHead.text= member.head_name+" "+smartFilterViewModel.getLastNameById(member.head_sub_cast_id.toInt())
                    }
                }


                val age=Utility.getAge(member.birthDate,Utility.yyyy_MM_dd)
                if(age in 0..100){
                    holder.tvAge.text="$age"
                }else{
                    holder.tvAge.text="N/A"
                }
                viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                holder.tvStatus.text= member.maritalStatus
                holder.tvEmail.text = member.emailAddress
                holder.tvMobile.text = member.mobile
                holder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {
                            createMemberPDF(activity as AppCompatActivity, member, profileDetailViewModel)
                            Handler().post(Runnable {
                                Utility.startSweetProgress(activity, getString(R.string.ExportingList)+"${member.firstName}" +getString(R.string.DetailList), getString(R.string.please_wait))
                            })
                            Handler().postDelayed({
                                Utility.hideSweetProgress()
                            }, 5000)
                        } else if (it == 1) {
                            Toast.makeText(activity,"Coming soon",Toast.LENGTH_SHORT).show()
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
                            adapter.setLocationListner(this@MatrimonyListFragment)
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


                applyImportant(viewHolder, member)
                applyClickEvents(viewHolder, position,member)
                applyProfilePicture(viewHolder, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_matrimony_profile, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }
        adapter.setParallaxHeader(header, binding.listMatrimony)
        adapter.setContext(this)
        adapter.setOnClickEvent { _, position ->
            val intent= Intent(activity,ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMembers[position])
            startActivity(intent)
            Utility.fade(activity)
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        binding.listMatrimony.layoutManager = linearLayoutManager
        binding.listMatrimony.itemAnimator = DefaultItemAnimator()
        binding.listMatrimony.setHasFixedSize(true)
        binding.listMatrimony.adapter = adapter

        jsonObj=JSONObject()
        val filter= arguments?.getString("filter")
        if(!filter.isNullOrEmpty()){
            jsonObj=JSONObject(filter)
            try{
                val fname= jsonObj.getString(getString(R.string.first_name))
                if(!fname.isNullOrEmpty()){
                    edtSearch.setText(fname)
                }else{
                    edtSearch.setText("")
                }
            }catch (e:Exception){
                e.message
                edtSearch.setText("")
            }
        }
        DashboardActivity.stop = false
        searchMatrimonyList(jsonObj)
        return binding.root
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
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })
    }

    private fun applyClickEvents(holder: ListViewHolder, position: Int, member: Member) {

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
                openImageDialog(activity as AppCompatActivity,path)
            } catch (e: Exception) {
                e.message
            }
        }

        holder.messageContainer.setOnClickListener { view ->
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMembers.get(position))
            startActivity(intent)
            Utility.fade(activity)
        }
    }

    @SuppressLint("CheckResult")
    private fun applyProfilePicture(holder: ListViewHolder, member: Member) {
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


    private fun searchMatrimonyList(jsonObj:JSONObject){
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            val jsonObject=JSONObject()
            jsonObject.put(getString(R.string.start), AppController.mApplication.start)
            jsonObject.put(getString(R.string.length), AppController.mApplication.length)
            jsonObj.put(getString(R.string.matrimony),"Yes")
            jsonObject.put(getString(R.string.filter_by),jsonObj)
            val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
            smartFilterViewModel.smartFilterSearch(updated)
            Handler().postDelayed({
                binding.shimmerViewContainer.stopShimmerAnimation()
                binding.shimmerViewContainer.visibility=View.GONE
            },4000)
            lstMembers.clear()
            adapter.notifyDataSetChanged()
            tvRecords.visibility = View.GONE
            binding.shimmerViewContainer.startShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.VISIBLE
            Utility.hideKeyboard(activity)
        }

    }

    inner class ListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var tvArea: TextView = v.findViewById(R.id.tv_area)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var txtHead: TextView = v.findViewById(R.id.txt_head)
        var tvAge: TextView = v.findViewById(R.id.tv_age)
        var tvStatus: TextView = v.findViewById(R.id.tv_status)
        var ivGender: ImageView = v.findViewById(R.id.iv_gender)
        var iconImp: ImageView = v.findViewById(R.id.icon_star)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile1)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.boomMenuButton)
        var messageContainer: LinearLayout = v.findViewById(R.id.message_container1)
        var iconText: TextView = v.findViewById(R.id.icon_text1)
        var llMobile: LinearLayout = itemView.findViewById(R.id.ll_mobile)
    }

    override fun getMembers(response: SmartFilterResponse) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility=View.GONE
        if(response.success){
            tvRecords.text = "Records found: " + response.totalRecords
            tvRecords.visibility = View.VISIBLE

            if(response.members.size>0){
                DashboardActivity.stop = false
                lstMembers.clear()
                lstMembers.addAll(response.members)

                if (response.totalRecords <= AppController.mApplication.length) {
                    DashboardActivity.stop = true
                    Snackbar.make(binding.listMatrimony, getString(R.string.EndRecordList), Snackbar.LENGTH_LONG).show()
                }
                adapter.notifyDataSetChanged()
            }else {
                tvRecords.visibility = View.GONE
                DashboardActivity.stop = true
                Snackbar.make(binding.listMatrimony, getString(R.string.EndRecordList), Snackbar.LENGTH_LONG).show()
            }
        }else{
            tvRecords.visibility = View.GONE
            DashboardActivity.stop = true
        }
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun getRoomMembers(response: List<RoomMember>) {

    }

    override suspend fun getFailure(message: String) {
        Coroutines.main {
            tvRecords.visibility = View.GONE
            Utility.displaySnackBarWithBottomMargin(binding.listMatrimony,message)
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility=View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            AppController.mApplication.start = (lstMembers.size + 1)
            searchMatrimonyList(jsonObj)
        }
    }

    override fun cancelDialog() {

    }
}