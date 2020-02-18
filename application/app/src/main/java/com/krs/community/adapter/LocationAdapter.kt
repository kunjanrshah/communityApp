package com.krs.community.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.easywaylocation.EasyWayLocation
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.krs.community.R
import com.krs.community.activity.DashboardActivity.Companion.cur_lat
import com.krs.community.activity.DashboardActivity.Companion.cur_lng
import com.krs.community.model.Member
import com.krs.community.utils.Utility

class LocationAdapter(var mContext: Context, var member: Member) : BaseAdapter() {
    var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var setLocationListner: SetLocationListner? = null

    fun setLocationListner(setLocationListner: SetLocationListner?) {
        this.setLocationListner = setLocationListner
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

        viewHolder.ivCancel.setOnClickListener { v: View? ->

            Log.e("ivCancel--","ivCancel----");
            setLocationListner?.cancelDialog()

        }

        viewHolder.llHome.setOnClickListener { v: View? ->

            Log.e("homeLat--",""+member.homeLat);
            if(!member.homeLat.isNullOrEmpty() &&  !member.homeLng.isNullOrEmpty()){
                Utility.showDirections(mContext as Activity, member.homeLat.toDouble(), member.homeLng.toDouble(), "${member.firstName}'s Home")
            }
        }

        viewHolder.llOffice.setOnClickListener { v: View? ->

            Log.e("officeLat--",""+member.officeLat);
            if(!member.officeLat.isNullOrEmpty() &&  !member.officeLng.isNullOrEmpty()){
                Utility.showDirections(mContext as Activity, member.officeLat.toDouble(), member.officeLng.toDouble(), "${member.firstName}'s Office")
            }
        }

        viewHolder.llUser.setOnClickListener { v: View? ->

            Log.e("userLat--",""+member.userLat);

            if(!member.userLat.isNullOrEmpty() &&  !member.userLng.isNullOrEmpty() && isShareLocation()){
                Utility.showDirections(mContext as Activity, member.userLat.toDouble(), member.userLng.toDouble(), "${member.firstName}'s Location")
            }else{
               Toast.makeText(mContext, R.string.Private,Toast.LENGTH_LONG).show()
            }
        }

        cur_lat.observeForever {
            setDistance(viewHolder.tvHomeDist,viewHolder.tvOfficeDist,viewHolder.tvUserDist)
        }

        cur_lng.observeForever {
            setDistance(viewHolder.tvHomeDist,viewHolder.tvOfficeDist,viewHolder.tvUserDist)
        }

        return convertView!!
    }

    private fun isShareLocation():Boolean{
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
    }

    private fun setDistance(tvHome:TextView,tvOffice:TextView,tvUser:TextView){
        if(!member.isLocationEnable.isNullOrEmpty() && member.isLocationEnable.equals("1") && isShareLocation()){
            if (cur_lat.value != null && cur_lng.value != null && !member.userLat.isNullOrEmpty() && !member.userLng.isNullOrEmpty()) {
                val userDist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member.userLat.toDouble(), member.userLng.toDouble()) / 1000
                tvUser.text= String.format("%.2f KM", userDist)
            }
        }else{
            tvUser.text= "Private"
        }

        if (cur_lat.value != null && cur_lng.value != null && !member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()) {
            val homeDist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member.homeLat.toDouble(), member.homeLng.toDouble()) / 1000
            tvHome.text= String.format("%.2f KM", homeDist)
        }
        if (cur_lat.value != null && cur_lng.value != null && !member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()) {
            val officeDist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member.officeLat.toDouble(), member.officeLng.toDouble()) / 1000
            tvOffice.text= String.format("%.2f KM", officeDist)
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
        var tvUserDist: TextView = view.findViewById(R.id.tv_user_dist)
        var tvHomeDist: TextView = view.findViewById(R.id.tv_home_dist)
        var tvOfficeDist: TextView = view.findViewById(R.id.tv_office_dist)
    }

}