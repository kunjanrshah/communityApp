package com.krs.community.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatButton
import com.github.squti.guru.Guru
import com.krs.community.R
import com.krs.community.activity.SplashActivity
import com.krs.community.fragments.SettingFragment

class PolicyAdapter(private val mContext: Context, val screen: String) : BaseAdapter() {

    private val mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return null!!
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.policy_bottom_sheet, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.btnPrivacy.setOnClickListener {

            val url = mContext.getString(R.string.privacy_policy_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mContext.startActivity(i)
        }

        viewHolder.btnTerms.setOnClickListener {
            val url = mContext.getString(R.string.tms_and_con_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mContext.startActivity(i)
        }

        viewHolder.btnDisclosure.setOnClickListener {
            val url = mContext.getString(R.string.disclaimer_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mContext.startActivity(i)
        }

        viewHolder.btnContinue.setOnClickListener {
            Guru.putBoolean(mContext.getString(R.string.policy), true)
            SplashActivity.polictyDialog?.dismiss()
            SettingFragment.polictyDialog?.dismiss()
        }

        viewHolder.btnDisAgree.setOnClickListener {
            when (screen) {
                "splash" -> {
                    (mContext as SplashActivity).finish()
                }
                "setting" -> {

                }
            }
        }
        return convertView
    }

    internal class ViewHolder(view: View) {
        var btnPrivacy: AppCompatButton = view.findViewById(R.id.btn_privacy)
        var btnTerms: AppCompatButton = view.findViewById(R.id.btn_terms)
        var btnContinue: AppCompatButton = view.findViewById(R.id.btn_continue)
        var btnDisAgree: AppCompatButton = view.findViewById(R.id.btn_disagree)
        var btnDisclosure: AppCompatButton = view.findViewById(R.id.btn_disclosure)
    }
}