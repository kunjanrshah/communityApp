package com.krs.community.fragments


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.adapter.RecyclerAdapter.ItemClickListener
import com.krs.community.app.AppController
import com.krs.community.interfaces.IFamilyMembersListener
import com.krs.community.interfaces.OnBackPressedListener
import com.krs.community.model.Member
import com.krs.community.model.User
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.FamilyDetailResponse
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodel.FamilyDetailViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import kotlinx.android.synthetic.main.header_detail.view.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception


class FamilyDetailActivity : AppCompatActivity(), KodeinAware, OnBackPressedListener, ItemClickListener, IFamilyMembersListener {

    override fun itemClick(position: Int) {
        val intent = Intent(this, ProfileDetailActivity::class.java)
        intent.putExtra("member", members.get(position))
        intent.putExtra("from", FamilyDetailActivity::class.java)
        startActivity(intent)
        Utility.fade(this)
    }

    lateinit var members:List<Member>

    var headId:String?=null
    val TAG = FamilyDetailActivity::class.java.simpleName
 //   private lateinit var binding: ActivityFamilyDetailBinding
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private lateinit var rvDetail:RecyclerView
    override val kodein by kodein()
    private lateinit var familyDetailViewModel:FamilyDetailViewModel
    private val factory: FamilyDetailViewModelFactory by instance()
/*    companion object {
        fun newInstance(adapterPosition: Int): FamilyDetailFragment {
            val bundle = Bundle().apply {
                putInt(EXTRA_POSITION, adapterPosition)
            }
            return FamilyDetailFragment().apply { arguments = bundle }
        }
    }*/

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_family_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorPrimary, true)
        }

        headId = intent.getStringExtra("id")
        familyDetailViewModel = ViewModelProviders.of(this, factory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.mIFamilyMembersListener = this

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container1)
        rvDetail=findViewById(R.id.rv_detail)

        mShimmerViewContainer?.startShimmerAnimation()
        mShimmerViewContainer?.visibility=View.VISIBLE

        val jsonObject=JSONObject()
        jsonObject.put("head_id",headId)
        val records=  JsonParser().parse(jsonObject.toString()) as JsonObject
        rvDetail.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        rvDetail.layoutManager = mLayoutManager
        rvDetail.itemAnimator = DefaultItemAnimator()
        familyDetailViewModel.getFamilyDetails(records)
        Handler().postDelayed(Runnable {
            mShimmerViewContainer?.stopShimmerAnimation()
            mShimmerViewContainer?.visibility=View.GONE
        },3000)
    }


    override fun getFamilyMembers(data: FamilyDetailResponse) {
        mShimmerViewContainer?.stopShimmerAnimation()
        mShimmerViewContainer?.visibility=View.GONE

       if(data.success){
           members=data.member
           createCardAdapter()
       }
    }

    private fun createCardAdapter() {
        val family=members.subList(1,members.size)
        val adapter = object : ParallaxRecyclerAdapter<Member>(family) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {
                (viewHolder as HeaderViewHolder).tv_name.text = family.get(i).firstName+" "+family.get(i).lastName
                viewHolder.tv_subtext.text = family.get(i).relation
                viewHolder.tv_email.text = family.get(i).emailAddress
                viewHolder.tv_mobile.text = family.get(i).mobile

                viewHolder.bmb1.clearBuilders()
                for (i in 0 until viewHolder.bmb1.piecePlaceEnum.pieceNumber()) {
                    viewHolder.bmb1.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }

                viewHolder.bmb1.setOnClickListener {
                    viewHolder.bmb1.boom()
                }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return HeaderViewHolder(layoutInflater.inflate(R.layout.row_list_family_detail, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return (family.size)
            }
        }

        adapter.setOnClickEvent { v, position ->
            val intent = Intent(this, ProfileDetailActivity::class.java)
            intent.putExtra("member",family.get(position))
            intent.putExtra("from", FamilyDetailActivity::class.java)
            startActivity(intent)
            Utility.fade(this)
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
        val tv_name: TextView
        tv_name = header.findViewById(R.id.tv_name1)
        tv_name.text=members.get(0).firstName+" "+members.get(0).lastName

        val tv_area: TextView
        tv_area = header.findViewById(R.id.tv_area)
        tv_area.text=members.get(0).area+" "+members.get(0).city

        val tv_mobile: TextView
        tv_mobile = header.findViewById(R.id.tv_mobile)
        tv_mobile.text=members.get(0).mobile

        val tv_email: TextView
        tv_email = header.findViewById(R.id.tv_email)
        tv_email.text=members.get(0).emailAddress

        val tv_addr: TextView
        tv_addr = header.findViewById(R.id.tv_addr)
        tv_addr.text=members.get(0).address

        val tv_label: TextView
        tv_label = header.findViewById(R.id.tv_label)
        tv_label.text="Family Member List (${members.size})"
        //imgProfile.setImageURI(user.profilePic)

        val ll_family_head: LinearLayout
        ll_family_head = header.findViewById(R.id.ll_family_head)
        ll_family_head.setOnClickListener {
            val intent = Intent(this, ProfileDetailActivity::class.java)
            intent.putExtra("member", members.get(0))
            intent.putExtra("from", FamilyDetailActivity::class.java)
            startActivity(intent)
            Utility.fade(this)
        }

        val tv_add: TextView
        tv_add = header.findViewById(R.id.tv_add)
        tv_add.setOnClickListener {
            val intent = Intent(this, ProfileDetailActivity::class.java)
            intent.putExtra("action", "add")
            intent.putExtra("member", Member())
            intent.putExtra("from", FamilyDetailActivity::class.java)
            startActivity(intent)
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
        var tv_name: TextView
        var tv_subtext: TextView
        var tv_email: TextView
        var tv_mobile: TextView
        var bmb1: BoomMenuButton

        init {
            tv_name = v.findViewById<View>(R.id.tv_name) as TextView
            tv_subtext = v.findViewById(R.id.tv_subtext)
            tv_subtext.typeface = AppController.mApplication.typeface_bold
            tv_email = v.findViewById(R.id.tv_email)
            tv_mobile = v.findViewById(R.id.tv_mobile)
            bmb1 = v.findViewById(R.id.bmb1)
        }
    }

    override fun getFailure(message: String) {

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
