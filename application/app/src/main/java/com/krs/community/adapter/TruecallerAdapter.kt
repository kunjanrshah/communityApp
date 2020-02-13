package com.krs.community.adapter

import android.app.Activity
import android.content.Context
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

class TruecallerAdapter(var mContext: Context) : BaseAdapter() {
    var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var setSetTruecallListner: SetSetTruecallListner? = null

    fun setTruecallListner(setTruecallListner: SetSetTruecallListner?) {
        this.setSetTruecallListner = setSetTruecallListner
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
            convertView = mLayoutInflater.inflate(R.layout.truecaller_bottom_sheet, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.ivCancel.setOnClickListener { v: View? ->
            setSetTruecallListner?.cancelDialog()
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

            }
        }
        return isShare
    }



    interface SetSetTruecallListner {
        fun cancelDialog()
    }

    internal class ViewHolder(view: View) {
        var ivCancel: ImageView = view.findViewById(R.id.iv_cancel)

    }

}