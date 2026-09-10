package com.krs.community.adapter

import android.app.Activity
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppDatabase
import com.krs.community.graphql.GraphQLClientProvider
import com.krs.community.listeners.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.repositories.SmartFilterRepository
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.MyPermissionChecker.Companion.checkFineLocationPermission
import com.krs.community.utils.MyPermissionChecker.Companion.requestFineLocationPermission
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.SmartFilterViewModelFactory
import org.json.JSONObject

class LocationAdapter(var mContext: Context, var member: Member) : BaseAdapter(), Listener, ByFilterListener {
    var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var easyWayLocation: EasyWayLocation? = null
    private var setLocationListner: SetLocationListner? = null
    private var cur_lat = MutableLiveData<Double>()
    private var cur_lng = MutableLiveData<Double>()
    private lateinit var filterViewModel: SmartFilterViewModel
    private val filterViewModelFactory = SmartFilterViewModelFactory(
        SmartFilterRepository(
            ApiServices(),
            AppDatabase.invoke(mContext),
            GraphQLClientProvider.provideApolloClient(mContext.applicationContext)
        )
    )
    private lateinit var request: LocationRequest
    private var tvUserDist: TextView? = null

    fun setLocationListner(setLocationListner: SetLocationListner?) {

        filterViewModel = ViewModelProvider(mContext as AppCompatActivity, filterViewModelFactory).get(SmartFilterViewModel::class.java)
        filterViewModel.mByFilterListener = this
        this.setLocationListner = setLocationListner
        if (checkFineLocationPermission(mContext)) {

            request = LocationRequest()
            request.interval = Utility.INTERVAL
            request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            easyWayLocation = EasyWayLocation(mContext, request, true, this)
            easyWayLocation?.startLocation()

            val jsonObj = JSONObject()
            jsonObj.put(mContext.getString(R.string.user_id), Guru.getString(mContext.getString(R.string.user_id), ""))
            jsonObj.put(mContext.getString(R.string.access_token), Guru.getString(mContext.getString(R.string.access_token), ""))
            jsonObj.put(mContext.getString(R.string.id), Guru.getString(mContext.getString(R.string.member_id), ""))
            val updated = JsonParser().parse(jsonObj.toString()) as JsonObject
            filterViewModel.getSharedProfiles(updated)

        } else {
            requestFineLocationPermission(mContext as AppCompatActivity)
        }
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.location_bottom_sheet, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        tvUserDist = viewHolder.tvUserDist
        viewHolder.ivCancel.setOnClickListener { v: View? ->
            setLocationListner?.cancelDialog()
        }

        viewHolder.llHome.setOnClickListener { v: View? ->

            if (!member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()) {
                Utility.showDirections(mContext as Activity, member.homeLat.toDouble(), member.homeLng.toDouble(), "${member.firstName}'s Home")
            } else {
                Toast.makeText(mContext, "Home location not found!", Toast.LENGTH_LONG).show()
            }
        }

        viewHolder.llOffice.setOnClickListener { v: View? ->

            if (!member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()) {
                Utility.showDirections(mContext as Activity, member.officeLat.toDouble(), member.officeLng.toDouble(), "${member.firstName}'s Office")
            } else {
                Toast.makeText(mContext, "Office location not found!", Toast.LENGTH_LONG).show()
            }
        }

        viewHolder.llUser.setOnClickListener { v: View? ->
            if (viewHolder.tvUserDist.text.toString() != "Private") {
                if (!member.userLat.isNullOrEmpty() && !member.userLng.isNullOrEmpty()) {
                    Utility.showDirections(mContext as Activity, member.userLat.toDouble(), member.userLng.toDouble(), "${member.firstName}'s Location")
                } else {
                    Toast.makeText(mContext, "User location not found!", Toast.LENGTH_LONG).show()
                }
            }
        }

        cur_lat.observeForever {
            setDistance(viewHolder.tvHomeDist, viewHolder.tvOfficeDist)
        }

        cur_lng.observeForever {
            setDistance(viewHolder.tvHomeDist, viewHolder.tvOfficeDist)
        }
        return convertView!!
    }

    /*private fun isShareLocation():Boolean{
        val loginUser= Guru.getString(mContext.getString(R.string.loginMember),"")
        val loginMember = Gson().fromJson<Member>(loginUser, Member::class.java)
        var isShare=false
        val arrayId = loginMember?.sharingId?.split(',')
        if (arrayId!= null) {
            for(id in arrayId){
                if(member.id==id){
                    isShare=true
                    break
                }
            }
        }
        return isShare
    }*/

    private fun setDistance(tvHome: TextView, tvOffice: TextView) {

        if (cur_lat.value != null && cur_lng.value != null && !member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()) {
            val homeDist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member.homeLat.toDouble(), member.homeLng.toDouble()) / 1000
            tvHome.text = String.format("%.2f KM", homeDist)
        }
        if (cur_lat.value != null && cur_lng.value != null && !member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()) {
            val officeDist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member.officeLat.toDouble(), member.officeLng.toDouble()) / 1000
            tvOffice.text = String.format("%.2f KM", officeDist)
        }
    }

    interface SetLocationListner {
        fun cancelDialog()
    }

    internal class ViewHolder(view: View) {

        var llHome: LinearLayout = view.findViewById(R.id.ll_home)
        var llOffice: LinearLayout = view.findViewById(R.id.ll_office)
        var llUser: LinearLayout = view.findViewById(R.id.ll_user)
        var ivCancel: ImageView = view.findViewById(R.id.iv_cancel)
        var tvUserDist: TextView = view.findViewById(R.id.tv_user)
        var tvHomeDist: TextView = view.findViewById(R.id.tv_home_dist)
        var tvOfficeDist: TextView = view.findViewById(R.id.tv_office_dist)
    }

    override fun locationCancelled() {
    }

    override fun locationOn() {
    }

    override fun currentLocation(location: Location?) {
        cur_lat.postValue(location?.latitude)
        cur_lng.postValue(location?.longitude)
    }

    override fun getMembers(response: SmartFilterResponse) {
        var isShared = false
        if (response.success) {
            if (response.membersharing != null && response.membersharing.size > 0) {
                for (member1 in response.membersharing) {
                    if (member.id == member1.id) {
                        if (cur_lat.value != null && cur_lng.value != null && !member.userLat.isNullOrEmpty() && !member.userLng.isNullOrEmpty()) {
                            val userDist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member.userLat.toDouble(), member.userLng.toDouble()) / 1000
                            tvUserDist?.text = String.format("%.2f KM", userDist)
                            isShared = true
                            break
                        }
                    }
                }
            }
        }
        if (!isShared) {
            tvUserDist?.text = "Private"
        }
    }

    override suspend fun getFailure(message: String) {
        tvUserDist?.text = "Private"
    }
}