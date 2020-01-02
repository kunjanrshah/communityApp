package com.krs.community.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.databinding.FragmentMatrimonylistBinding
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.utils.openFilter
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodel.SmartFilterViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class MatrimonyListFragment : Fragment(), KodeinAware, ByFilterListener {

    private var lstMatrimony= ArrayList<Member>()
    override val kodein by kodein()
    private val factory: SmartFilterViewModelFactory by instance()
    private lateinit var smartFilterViewModel: SmartFilterViewModel
    private lateinit var binding:FragmentMatrimonylistBinding
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_matrimonylist, container, false)

        smartFilterViewModel = ViewModelProviders.of(this,factory).get(SmartFilterViewModel::class.java)
        smartFilterViewModel.mByFilterListener =this

        val header = LayoutInflater.from(activity).inflate(R.layout.header_matrimony, container, false)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
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
                val jsonObj=JSONObject()
                jsonObj.put(getString(R.string.first_name),edtSearch.text)
                searchMatrimonyList(jsonObj)
                true
            }
            false
        }

        adapter= object : ParallaxRecyclerAdapter<Member>(lstMatrimony) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val holder = viewHolder as ListViewHolder
                val member = lstMatrimony[position]
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

                holder.tvStatus.text= member.maritalStatus
                holder.tvEmail.text = member.emailAddress
                holder.tvMobile.text = member.mobile
                holder.boomMenuButton.clearBuilders()
                for (i in 0 until holder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                holder.boomMenuButton.setOnClickListener { v: View? -> holder.boomMenuButton.boom() }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_matrimony_profile, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMatrimony.size
            }
        }
        adapter.setParallaxHeader(header, binding.listMatrimony)

        adapter.setOnClickEvent { _, position ->
            val intent= Intent(activity,ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMatrimony[position])
            startActivity(intent)
            Utility.fade(activity)
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        binding.listMatrimony.layoutManager = linearLayoutManager
        binding.listMatrimony.itemAnimator = DefaultItemAnimator()
        binding.listMatrimony.setHasFixedSize(true)
        binding.listMatrimony.adapter = adapter

        var jsonObj=JSONObject()
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
        searchMatrimonyList(jsonObj)
        return binding.root
    }

    private fun searchMatrimonyList(jsonObj:JSONObject){
        val jsonObject=JSONObject()
        jsonObject.put("start","0")
        jsonObject.put("length","30")
        jsonObj.put(getString(R.string.matrimony),"Yes")
        jsonObject.put("filter_by",jsonObj)
        val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
        smartFilterViewModel.smartFilterSearch(updated)
        Handler().postDelayed({
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility=View.GONE
        },4000)
        lstMatrimony.clear()
        adapter.notifyDataSetChanged()
        binding.shimmerViewContainer.startShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.VISIBLE
        Utility.hideKeyboard(activity)
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
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.boomMenuButton)
    }

    override fun getMembers(response: SmartFilterResponse) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility=View.GONE
        if(response.success){
            lstMatrimony.clear()
            for(member in response.members) {
                if(member.maritalStatus!=getString(R.string.married)){
                    lstMatrimony.add(member)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun getFailure(message: String) {
        Coroutines.main {
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
}