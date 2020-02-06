package com.krs.community.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
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
import com.krs.community.activity.*
import com.krs.community.adapter.LocationAdapter
import com.krs.community.app.AppController
import com.krs.community.listeners.IFamilyMembersListener
import com.krs.community.listeners.OnBackPressedListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.*
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.FamilyDetailViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import kotlinx.android.synthetic.main.header_detail.view.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class FamilyDetailActivity : AppCompatActivity(), KodeinAware,   OnBackPressedListener, IFamilyMembersListener,  LocationAdapter.SetLocationListner {

    lateinit var members:ArrayList<Member>
    var headId:String?=null
    val TAG = FamilyDetailActivity::class.java.simpleName
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private lateinit var rvDetail:RecyclerView
    private lateinit var llRoot:LinearLayout
    private lateinit var adapter:ParallaxRecyclerAdapter<Member>
    private var deletedId=""
    override val kodein by kodein()
    private var setLocationDialog: DialogPlus? = null
    private lateinit var familyDetailViewModel:FamilyDetailViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance()
    private val familyDetailViewModelFactory: FamilyDetailViewModelFactory by instance()
    lateinit var mainHandler: Handler
    private var isShimmer:Boolean=true
    private var memberId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_family_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeStatusbarColor(this, R.color.colorPrimary, true)
        }
        mainHandler = Handler(Looper.getMainLooper())
        headId = intent.getStringExtra(getString(R.string.id))
        profileDetailViewModel = ViewModelProviders.of(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        familyDetailViewModel = ViewModelProviders.of(this, familyDetailViewModelFactory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.mIFamilyMembersListener = this
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container1)
        rvDetail=findViewById(R.id.rv_detail)
        llRoot=findViewById(R.id.ll_root)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        rvDetail.setHasFixedSize(true)
        rvDetail.layoutManager = mLayoutManager
        rvDetail.itemAnimator = DefaultItemAnimator()
        memberId = Guru.getString(getString(R.string.member_id), "")
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateAdapter)
    }

    private val updateAdapter = object : Runnable {
        override fun run() {
            getFamilyDetails()
            mainHandler.postDelayed(this, 1000*60*3)
        }
    }

    private fun getFamilyDetails(){
        if(isShimmer){
            isShimmer=false
            mShimmerViewContainer?.visibility=View.VISIBLE
            mShimmerViewContainer?.startShimmerAnimation()
        }
        val jsonObject=JSONObject()
        if(!memberId.isNullOrEmpty()){
            jsonObject.put(getString(R.string.id),memberId)
        }
        jsonObject.put(getString(R.string.head_id),headId)
        val records=  JsonParser().parse(jsonObject.toString()) as JsonObject
        familyDetailViewModel.getFamilyDetails(records)
    }

    private fun deleteFamilyMember(id:String){
        mShimmerViewContainer?.startShimmerAnimation()
        mShimmerViewContainer?.visibility=View.VISIBLE
        val mJSONObject= JSONObject()
        mJSONObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id),""))
        mJSONObject.put(getString(R.string.access_token),Guru.getString(getString(R.string.access_token),""))
        mJSONObject.put("member_id", id)
        deletedId= id
        val records=  JsonParser().parse(mJSONObject.toString()) as JsonObject
        familyDetailViewModel.deleteMember(records)


        Handler().postDelayed({
            mShimmerViewContainer?.stopShimmerAnimation()
            mShimmerViewContainer?.visibility=View.GONE
        },4000)
    }

    override fun getFamilyMembers(data: FamilyDetailResponse) {
        mShimmerViewContainer?.stopShimmerAnimation()
        mShimmerViewContainer?.visibility=View.GONE

       if(data.success){
           members= data.member as ArrayList<Member>
           createCardAdapter()
       }
    }

    private fun createCardAdapter() {
        var family:MutableList<Member>?=null
        if(members.size>0){
           family=members.subList(1,members.size)
        }
        if(family!=null){
            adapter = object : ParallaxRecyclerAdapter<Member>(family) {
                override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {

                    val member = family[i]

                    (viewHolder as FamilyDetailViewHolder).tvName.text = "${member.firstName} ${member.lastName}"
                    viewHolder.tvSubtext.text = member.relation
                    viewHolder.tvEmail.text = member.emailAddress
                    viewHolder.tvMobile.text = member.mobile
                    viewHolder.tvUpdate.text = "updated "+changeDateFormat(member.updatedDt,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                    viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                    var imgLogin=R.drawable.ic_logout
                    if(member.loginStatus==1){
                        imgLogin=R.drawable.ic_logout
                        viewHolder.tvLogin.setText("Exit")
                    }else{
                        imgLogin=R.drawable.ic_login
                        viewHolder.tvLogin.setText("Enter")
                    }
                    try {
                        Glide.with(AppController.mApplication).load(imgLogin).thumbnail(0.5f).into(viewHolder.imgLogin)
                    } catch (e: Exception) {
                        e.message
                    }
                    
                    var imgStatus=R.drawable.ico_red
                    if(memberId==member.id){
                        imgStatus=R.drawable.ico_blue
                    }else if(member.loginStatus==1 && member.onlineStatus==0){
                        imgStatus=R.drawable.ico_pink
                    }else if(member.loginStatus==0){
                        imgStatus=R.drawable.ico_red
                    }else if(member.onlineStatus==1){
                        imgStatus=R.drawable.ico_green
                    }
                    try {
                        Glide.with(AppController.mApplication).load(imgStatus).thumbnail(0.5f).into(viewHolder.imgState)
                    } catch (e: Exception) {
                        e.message
                    }

                    viewHolder.frontLayout.setOnClickListener {
                        if(!memberId.isNullOrEmpty()){
                            val intent = Intent(this@FamilyDetailActivity, ProfileDetailActivity::class.java)
                            intent.putExtra(getString(R.string.member), member)
                            startActivity(intent)
                            fade(this@FamilyDetailActivity)
                        }else{
                            if(!member.profilePassword.isNullOrEmpty()){
                                val intent=Intent(this@FamilyDetailActivity,PinViewActivity::class.java)
                                intent.putExtra(getString(R.string.member), member)
                                startActivity(intent)
                            }else{
                                llRoot.snackbar("PIN not found!",Snackbar.LENGTH_LONG)
                            }
                        }
                    }
                    viewHolder.llDelete.setOnClickListener {
                        if(!memberId.isNullOrEmpty()){
                            SweetAlertDialog(this@FamilyDetailActivity, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(getString(R.string.you_sure))
                                    .setContentText("Won't be able to recover this Profile!")
                                    .setConfirmText("Yes,delete it!")
                                    .setCancelText("No")
                                    .setConfirmClickListener {
                                        it.dismiss()
                                        deleteFamilyMember(member.id)
                                    }
                                    .setCancelClickListener {
                                        it.dismiss()
                                    }
                                    .show()
                        }else{
                            llRoot.snackbar(getString(R.string.enter_pin),Snackbar.LENGTH_LONG)
                        }
                    }
                    viewHolder.boomMenuButton.clearBuilders()
                    for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                        val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                        builder?.listener {
                            if (it == 0) {

                                    createMemberPDF(this@FamilyDetailActivity, member, profileDetailViewModel)

                                    Handler().post(Runnable {
                                        startSweetProgress(this@FamilyDetailActivity, "Exporting ${member.firstName}'s Details", getString(R.string.please_wait))
                                    })
                                    Handler().postDelayed({
                                        hideSweetProgress()
                                    }, 5000)


                            } else if (it == 1) {
                                val intent: Intent = Intent(this@FamilyDetailActivity, FamilyTreeListActivity::class.java)
                                startActivity(intent)
                            } else if (it == 2) {
                                if (!member.mobile.isNullOrEmpty()) {
                                    sendWhatsappMessage(this@FamilyDetailActivity, member.mobile, getString(R.string.install_app))
                                } else {
                                    Toast.makeText(this@FamilyDetailActivity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                                }
                            } else if (it == 3) {
                                val mBundle = Bundle()
                                mBundle.putSerializable(getString(R.string.member), member)
                                val intent: Intent = Intent(this@FamilyDetailActivity, QRCodeActivity::class.java)
                                intent.putExtras(mBundle)
                                startActivity(intent)
                                fade(this@FamilyDetailActivity)
                            } else if (it == 4) {
                                shareDetails(this@FamilyDetailActivity, viewHolder.tvName.text.toString(), member.mobile, member.emailAddress, member.area, member.address)
                            } else if (it == 5) {
                                val adapter: LocationAdapter = LocationAdapter(AppController.mApplication.applicationContext, member)
                                adapter.setLocationListner(this@FamilyDetailActivity)
                                setLocationDialog = DialogPlus.newDialog(AppController.mApplication.applicationContext)
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

                    applyClickEvents(viewHolder, i,member)
                    applyProfilePicture(viewHolder, member)
                }

                override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                    return FamilyDetailViewHolder(layoutInflater.inflate(R.layout.row_list_family_detail, viewGroup, false))
                }

                override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                    return (family.size)
                }
            }
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(this)
        rvDetail.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(R.layout.header_detail, rvDetail, false)

        val cancel = header.findViewById<ImageView>(R.id.img_cancel1)
        if(!memberId.isNullOrEmpty()){
            cancel.visibility=View.VISIBLE
        }else{
            cancel.visibility=View.INVISIBLE
        }
        cancel.setOnClickListener {
            val intent=Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()
            fade(this)
        }

        val member = members.get(0)
        val tvName: TextView = header.findViewById(R.id.tv_name1)
        tvName.text = member.firstName+" "+member.lastName

        val iconText: TextView = header.findViewById(R.id.icon_text1)
        iconText.text = tvName.text.substring(0, 1)

        val tvMobile: TextView = header.findViewById(R.id.tv_mobile)
        tvMobile.text=member.mobile

        tvMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }

        val imgProfile: ImageView = header.findViewById(R.id.icon_profile1)
        imgProfile.setOnClickListener {
            val path = getString(R.string.base_url_original) + "" + member.profilePic
            Log.d(TAG, "path: $path")
            openImageDialog(this,path)
        }
        if (!TextUtils.isEmpty(member.profilePic)) {
            imgProfile.isClickable = true
            val url=resources.getString(R.string.base_url_thumb)+member.profilePic
            Glide.with(this@FamilyDetailActivity).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(imgProfile)
            imgProfile.colorFilter = null
            iconText.visibility = View.GONE

        } else {
            imgProfile.isClickable = false
            imgProfile.setImageResource(R.drawable.bg_circle)
            imgProfile.setColorFilter(Utility.getRandomMaterialColor(this@FamilyDetailActivity, "400"))
            iconText.visibility = View.VISIBLE
        }

        val tvArea: TextView = header.findViewById(R.id.tv_area)
        tvArea.text=member.area+" "+member.city

        val tvEmail: TextView = header.findViewById(R.id.tv_email)
        tvEmail.text=member.emailAddress

        val tvAddr: TextView = header.findViewById(R.id.tv_addr)
        tvAddr.text=member.address

        val imgState: ImageView = header.findViewById(R.id.img_state)
        var icStatus=R.drawable.ico_red
        if(memberId==member.id){
            icStatus=R.drawable.ico_blue
        }else if(member.loginStatus==1 && member.onlineStatus==0){
            icStatus=R.drawable.ico_pink
        }else if(member.loginStatus==0){
            icStatus=R.drawable.ico_red
        }else if(member.onlineStatus==1){
            icStatus=R.drawable.ico_green
        }
        try {
            Glide.with(AppController.mApplication).load(icStatus).thumbnail(0.5f).into(imgState)
        } catch (e: Exception) {
            e.message
        }


        val tvLabel: TextView = header.findViewById(R.id.tv_label)
        tvLabel.text="Family Member List (${members.size})"

        header.bmb.clearBuilders()
        for (i in 0 until header.bmb.piecePlaceEnum.pieceNumber()) {
            val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
            builder?.listener {
                if (it == 0) {
                        createMemberPDF(this@FamilyDetailActivity, member, profileDetailViewModel)
                        Handler().post {
                            startSweetProgress(this@FamilyDetailActivity, "Exporting ${member.firstName}'s Details", getString(R.string.please_wait))
                        }
                        Handler().postDelayed({
                            hideSweetProgress()
                        }, 5000)
                } else if (it == 1) {
                    val intent: Intent = Intent(this@FamilyDetailActivity, FamilyTreeListActivity::class.java)
                    startActivity(intent)
                } else if (it == 2) {
                    if (!member.mobile.isNullOrEmpty()) {
                        sendWhatsappMessage(this@FamilyDetailActivity, member.mobile, getString(R.string.install_app))
                    } else {
                        Toast.makeText(this@FamilyDetailActivity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                    }
                } else if (it == 3) {
                    val mBundle = Bundle()
                    mBundle.putSerializable(getString(R.string.member), member)
                    val intent: Intent = Intent(this@FamilyDetailActivity, QRCodeActivity::class.java)
                    intent.putExtras(mBundle)
                    startActivity(intent)
                    fade(this@FamilyDetailActivity)
                } else if (it == 4) {
                    shareDetails(this@FamilyDetailActivity, tvName.text.toString(), member.mobile, member.emailAddress, member.area, member.address)
                } else if (it == 5) {
                    val adapter: LocationAdapter = LocationAdapter(this@FamilyDetailActivity, member)
                    adapter.setLocationListner(this@FamilyDetailActivity)
                    setLocationDialog = DialogPlus.newDialog(this@FamilyDetailActivity)
                            .setAdapter(adapter)
                            .setGravity(Gravity.BOTTOM)
                            .setCancelable(true)
                            .setExpanded(true, 600)
                            .setContentBackgroundResource(R.drawable.popup_top_corner)
                            .create()
                    setLocationDialog?.show()
                }
            }
            header.bmb.addBuilder(builder)
        }

        header.bmb.setOnClickListener {
            header.bmb.boom()
        }

        val llFamilyHead: LinearLayout = header.findViewById(R.id.ll_family_head)
        llFamilyHead.setOnClickListener {
            if(!memberId.isNullOrEmpty()){
                val intent = Intent(this, ProfileDetailActivity::class.java)
                intent.putExtra(getString(R.string.member), members.get(0))
                startActivity(intent)
                fade(this)
            }else{

                if(!member.profilePassword.isNullOrEmpty()){
                    val intent=Intent(this@FamilyDetailActivity,PinViewActivity::class.java)
                    intent.putExtra(getString(R.string.member), member)
                    startActivity(intent)
                }else{
                    llRoot.snackbar("PIN not found!",Snackbar.LENGTH_LONG)
                }


            }

        }

        val tvAdd: TextView = header.findViewById(R.id.tv_add)
        if(!memberId.isNullOrEmpty()){
            tvAdd.visibility=View.VISIBLE
        }else{
            tvAdd.visibility=View.GONE
        }
        tvAdd.setOnClickListener {
                val intent = Intent(this, ProfileDetailActivity::class.java)
                intent.putExtra(getString(R.string.member), Member())
                startActivity(intent)
                fade(this)
        }


        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, rvDetail)
        adapter.data = family
        rvDetail.adapter = adapter
    }

    @SuppressLint("CheckResult")
    private fun applyProfilePicture(holder: FamilyDetailViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            holder.imgProfile.isClickable = true
            val url=resources.getString(R.string.base_url_thumb)+member.profilePic
            Glide.with(this@FamilyDetailActivity).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE

        } else {
            holder.imgProfile.isClickable = false
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(this@FamilyDetailActivity, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyClickEvents(holder: FamilyDetailViewHolder, position: Int, member: Member) {
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
                openImageDialog(this@FamilyDetailActivity,path)
            } catch (e: Exception) {
                e.message
            }
        }
    }

    internal class FamilyDetailViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById<View>(R.id.tv_name) as TextView
        var tvSubtext: TextView = v.findViewById(R.id.tv_subtext)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvUpdate: TextView = v.findViewById(R.id.tv_update)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.bmb1)
        var frontLayout: FrameLayout = v.findViewById(R.id.front_layout)
        var iconText: TextView = v.findViewById(R.id.icon_text1)
        var llDelete: LinearLayout = v.findViewById(R.id.ll_delete)
        var llLogin: LinearLayout = v.findViewById(R.id.ll_login)
        var llMobile: LinearLayout = v.findViewById(R.id.llMobile)
        var imgProfile: ImageView = v.findViewById(R.id.icon_profile1)
        val imgState: ImageView = v.findViewById(R.id.img_state)
        val imgLogin: ImageView = v.findViewById(R.id.img_login)
        val tvLogin: TextView = v.findViewById(R.id.tv_login)
    }

    override fun getMessage(response: DeleteProfileResponse) {
        if(response.success){
        var member1:Member?=null
        for(member in members){
              if(member.id == deletedId){
                  member1=member
                break
              }
         }

        if(member1!=null){
            members.remove(member1)
            createCardAdapter()
        }
      }
        mShimmerViewContainer?.stopShimmerAnimation()
        mShimmerViewContainer?.visibility=View.GONE
        llRoot.snackbar(response.message,Snackbar.LENGTH_LONG)
    }

    override suspend fun getFailure(message: String) {
        mShimmerViewContainer?.stopShimmerAnimation()
        mShimmerViewContainer?.visibility=View.GONE
        llRoot.snackbar(message,Snackbar.LENGTH_LONG)
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
        Handler().postDelayed({
            hideKeyboard(this)
            /*val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(rvDetail.windowToken, 0)*/
        }, 1000)

        mainHandler.post(updateAdapter)
    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.show()
    }
    override fun onBackPressed() {
        //  animateViewsOut()
    }

    override fun cancelDialog() {
        setLocationDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        familyDetailViewModel.cancelAllJobs()
    }


}
