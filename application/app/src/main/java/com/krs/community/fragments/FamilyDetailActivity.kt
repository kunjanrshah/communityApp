package com.krs.community.fragments


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.adapter.RecyclerAdapter.ItemClickListener
import com.krs.community.app.AppController
import com.krs.community.interfaces.IFamilyMembersListener
import com.krs.community.interfaces.OnBackPressedListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodel.FamilyDetailViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import kotlinx.android.synthetic.main.header_detail.view.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class FamilyDetailActivity : AppCompatActivity(), KodeinAware, OnBackPressedListener, IFamilyMembersListener {

    lateinit var members:ArrayList<Member>
    var headId:String?=null
    val TAG = FamilyDetailActivity::class.java.simpleName
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private lateinit var rvDetail:RecyclerView
    private lateinit var llRoot:LinearLayout
    private lateinit var adapter:ParallaxRecyclerAdapter<Member>
    private var deletedId=""
    override val kodein by kodein()
    private lateinit var familyDetailViewModel:FamilyDetailViewModel
    private val factory: FamilyDetailViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_family_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorPrimary, true)
        }

        headId = intent.getStringExtra(getString(R.string.id))
        familyDetailViewModel = ViewModelProviders.of(this, factory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.mIFamilyMembersListener = this

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container1)
        rvDetail=findViewById(R.id.rv_detail)
        llRoot=findViewById(R.id.ll_root)

        rvDetail.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        rvDetail.layoutManager = mLayoutManager
        rvDetail.itemAnimator = DefaultItemAnimator()

        getFamilyDetails()
    }

   /* override fun itemClick(id: Int) {
        val intent = Intent(this, ProfileDetailActivity::class.java)
        intent.putExtra(getString(R.string.member), members[id])
        intent.putExtra("from", FamilyDetailActivity::class.java)
        startActivity(intent)
        finish()
        Utility.fade(this)
    }*/

    private fun getFamilyDetails(){
        val jsonObject=JSONObject()
        jsonObject.put("head_id",headId)
        val records=  JsonParser().parse(jsonObject.toString()) as JsonObject
        mShimmerViewContainer?.startShimmerAnimation()
        mShimmerViewContainer?.visibility=View.VISIBLE
        familyDetailViewModel.getFamilyDetails(records)
        Handler().postDelayed({
            mShimmerViewContainer?.stopShimmerAnimation()
            mShimmerViewContainer?.visibility=View.GONE
        },4000)
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
                    (viewHolder as HeaderViewHolder).tvName.text = "${family[i].firstName} ${family[i].lastName}"
                    viewHolder.tvSubtext.text = family[i].relation
                    viewHolder.tvEmail.text = family[i].emailAddress
                    viewHolder.tvMobile.text = family[i].mobile

                    viewHolder.frontLayout.setOnClickListener {
                        val intent = Intent(this@FamilyDetailActivity, ProfileDetailActivity::class.java)
                        intent.putExtra(getString(R.string.member), family[i])
                        intent.putExtra("from", FamilyDetailActivity::class.java)
                        startActivity(intent)
                        finish()
                        Utility.fade(this@FamilyDetailActivity)
                    }
                    viewHolder.deleteLayout.setOnClickListener {

                        SweetAlertDialog(this@FamilyDetailActivity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this Profile!")
                                .setConfirmText("Yes,delete it!")
                                .setCancelText("No")
                                .setConfirmClickListener {
                                    it.dismiss()
                                    deleteFamilyMember(family[i].id)
                                }
                                .setCancelClickListener {
                                    it.dismiss()
                                }
                                .show()
                    }
                    viewHolder.bmB.clearBuilders()
                    for (i in 0 until viewHolder.bmB.piecePlaceEnum.pieceNumber()) {
                        viewHolder.bmB.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                    }

                    viewHolder.bmB.setOnClickListener {
                        viewHolder.bmB.boom()
                    }
                }

                override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                    return HeaderViewHolder(layoutInflater.inflate(R.layout.row_list_family_detail, viewGroup, false))
                }

                override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                    return (family.size)
                }
            }
            /*
            adapter.setOnClickEvent { v, position ->
                val intent = Intent(this, ProfileDetailActivity::class.java)
                intent.putExtra(getString(R.string.member),family.get(position))
                intent.putExtra("from", FamilyDetailActivity::class.java)
                startActivity(intent)
                finish()
                Utility.fade(this)
            }*/
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(this)
        rvDetail.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(R.layout.header_detail, rvDetail, false)

        val cancel:ImageView
        cancel = header.findViewById(R.id.img_cancel1)
        cancel.setOnClickListener {
            finish()
            Utility.fade(this)
        }
        val tvName: TextView = header.findViewById(R.id.tv_name1)
        tvName.text=members.get(0).firstName+" "+members.get(0).lastName

        val tvArea: TextView = header.findViewById(R.id.tv_area)
        tvArea.text=members.get(0).area+" "+members.get(0).city

        val tvMobile: TextView = header.findViewById(R.id.tv_mobile)
        tvMobile.text=members.get(0).mobile

        val tvEmail: TextView = header.findViewById(R.id.tv_email)
        tvEmail.text=members.get(0).emailAddress

        val tvAddr: TextView = header.findViewById(R.id.tv_addr)
        tvAddr.text=members.get(0).address

        val tvLabel: TextView = header.findViewById(R.id.tv_label)
        tvLabel.text="Family Member List (${members.size})"
        //imgProfile.setImageURI(user.profilePic)

        val llFamilyHead: LinearLayout = header.findViewById(R.id.ll_family_head)
        llFamilyHead.setOnClickListener {
            val intent = Intent(this, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), members.get(0))
            intent.putExtra("from", FamilyDetailActivity::class.java)
            startActivity(intent)
            finish()
            Utility.fade(this)
        }

        val tvAdd: TextView = header.findViewById(R.id.tv_add)
        tvAdd.setOnClickListener {
            val intent = Intent(this, ProfileDetailActivity::class.java)
            intent.putExtra("action", "add")
            intent.putExtra(getString(R.string.member), Member())
            intent.putExtra("from", FamilyDetailActivity::class.java)
            startActivity(intent)
            finish()
            Utility.fade(this)
        }

        /*header.img_cancel.setOnClickListener {
            *//*if(screen_name.equals(SearchCityResult::class.java.simpleName)){
                Utility.movetoFragment(activity, SearchCityResult())
            }else if(screen_name.equals(SearchListFragment::class.java.simpleName)){
                Utility.movetoFragment(activity, SearchListFragment())
            }*//*
        }*/

        header.bmb.clearBuilders()
        for (i in 0 until header.bmb.piecePlaceEnum.pieceNumber()) {
            header.bmb.addBuilder(Utility.getTextInsideCircleButtonBuilder())
        }

        header.bmb.setOnClickListener {
            header.bmb.boom()
        }

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, rvDetail)
        adapter.data = family
        rvDetail.adapter = adapter
    }

    internal class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView
        var tvSubtext: TextView
        var tvEmail: TextView
        var tvMobile: TextView
        var bmB: BoomMenuButton
        var deleteLayout: FrameLayout
        var frontLayout: FrameLayout

        init {
            tvName = v.findViewById<View>(R.id.tv_name) as TextView
            tvSubtext = v.findViewById(R.id.tv_subtext)
            tvSubtext.typeface = AppController.mApplication.typeface_bold
            tvEmail = v.findViewById(R.id.tv_email)
            tvMobile = v.findViewById(R.id.tv_mobile)
            frontLayout = v.findViewById(R.id.front_layout)
            deleteLayout = v.findViewById(R.id.delete_layout)
            bmB = v.findViewById(R.id.bmb1)
        }
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

    override fun getFailure(message: String) {
        mShimmerViewContainer?.stopShimmerAnimation()
        mShimmerViewContainer?.visibility=View.GONE
        llRoot.snackbar(message,Snackbar.LENGTH_LONG)
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
        Handler().postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(rvDetail.windowToken, 0)
        }, 1000)
    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.show()
    }
    override fun onBackPressed() {
        //  animateViewsOut()
    }
}
