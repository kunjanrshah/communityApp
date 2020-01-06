package com.krs.community.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodel.SmartFilterViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AdminsFragment : Fragment(), KodeinAware, ByFilterListener {

    override val kodein by kodein()
    var lstAdmins: ArrayList<Member> = ArrayList()
    private lateinit var tvCount:TextView
    private lateinit var smartFilterViewModel: SmartFilterViewModel
    private val factory: SmartFilterViewModelFactory by instance()
    private var loginUserSubCommunityId=""
    private var loginUserLocalCommunityId=""
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var llRoot: FrameLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_admins, container, false)
        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container)
        llRoot= root.findViewById(R.id.ll_root)
        (activity as AppCompatActivity).supportActionBar!!.title = ""
        smartFilterViewModel = ViewModelProviders.of(this,factory).get(SmartFilterViewModel::class.java)
        smartFilterViewModel.mByFilterListener =this

        adapter = object : ParallaxRecyclerAdapter<Member>(lstAdmins) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {
                val holder = viewHolder as ListViewHolder

                if(lstAdmins.size>0){
                    tvCount.visibility=View.VISIBLE
                    tvCount.text = "Admin ${lstAdmins.size} found"
                }else{
                    tvCount.visibility=View.GONE
                }

                holder.tvAddr.text=lstAdmins[i].address
                holder.tvName.text = lstAdmins[i].firstName
                viewHolder.tvArea.text = lstAdmins[i].area

                Coroutines.io {
                    if(!lstAdmins[i].subCastId.isNullOrEmpty()){
                        viewHolder.tvName.text=lstAdmins[i].firstName+" "+smartFilterViewModel.getLastNameById(lstAdmins[i].subCastId.toInt())
                    }

                    if(!lstAdmins[i].cityId.isNullOrEmpty()){
                        viewHolder.tvArea.text = lstAdmins[i].area+" "+smartFilterViewModel.getCityNamebyId(lstAdmins.get(i).cityId)
                    }
                }

                viewHolder.tvEmail.text = lstAdmins[i].emailAddress
                viewHolder.tvMobile.text = lstAdmins[i].mobile
                if(lstAdmins[i].headId == "0"){
                    holder.tvRole.text = "Family Head"
                }else{
                    holder.tvRole.text = "Member"
                }

                holder.boomMenuButton.clearBuilders()
                for (i in 0 until holder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
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
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        val MyLayoutManager = LinearLayoutManager(activity)
        val rvAdmins: RecyclerView = root.findViewById(R.id.rv_Admins)
        rvAdmins.layoutManager = MyLayoutManager
        rvAdmins.itemAnimator = DefaultItemAnimator()
        adapter.setParallaxHeader(header, rvAdmins)
        rvAdmins.adapter = adapter
        rvAdmins.setHasFixedSize(true)

        val loginuser= Guru.getString(getString(R.string.loginUser),"")
        val member: Member = Gson().fromJson<Member>(loginuser, Member::class.java)
        loginUserSubCommunityId=member.subCommunityId
        loginUserLocalCommunityId=member.localCommunityId

      //  getSubAdmin()
        getLocalAdmin()
        return root
    }

    private fun getSubAdmin(){
        val jsonObject=JSONObject()
        jsonObject.put("start",0)
        jsonObject.put("length",30)
        val jsonObj=JSONObject()
        jsonObj.put("role","SUB_ADMIN")
        jsonObj.put("sub_community_id",loginUserSubCommunityId)
        jsonObject.put("filter_by",jsonObj)
        val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
        smartFilterViewModel.smartFilterSearch(updated)
        shimmerFrameLayout.startShimmerAnimation()
        shimmerFrameLayout.visibility = View.VISIBLE
        lstAdmins.clear()
        adapter.notifyDataSetChanged()
        Utility.hideKeyboard(activity)
    }

    private fun getLocalAdmin(){
        val jsonObject=JSONObject()
        jsonObject.put("start",0)
        jsonObject.put("length",30)
        val jsonObj=JSONObject()
        jsonObj.put("role","LOCAL_ADMIN")
        jsonObj.put("local_community_id",loginUserLocalCommunityId)
        jsonObject.put("filter_by",jsonObj)
        val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
        smartFilterViewModel.smartFilterSearch(updated)

        Handler().postDelayed({
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility=View.GONE
        },4000)
    }

    override fun getMembers(response: SmartFilterResponse) {
        if(response.success){
            if (response.members.size > 0) {
                if(response.members.get(0).role.equals("SUB_ADMIN")){
                    lstAdmins.clear()
                    lstAdmins.addAll(response.members)
                    getLocalAdmin()
                }else if(response.members.get(0).role.equals("LOCAL_ADMIN")){
                    lstAdmins.addAll(response.members)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun getFailure(message: String) {
        Utility.displaySnackBarWithBottomMargin(llRoot,"Something went wrong")
    }

    internal inner class ListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.boomMenuButton)
        var tvArea: TextView = v.findViewById(R.id.tv_area)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvRole: TextView = v.findViewById(R.id.tv_role)
        var tvAddr: TextView = v.findViewById(R.id.tv_addr)

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


}