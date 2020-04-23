package com.krs.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.krs.community.R
import com.krs.community.fragments.FamilyDetailActivity
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel

class AddFamilyHeadAdapter(private val mContext: Context, val profileDetailViewModel: ProfileDetailViewModel) : BaseAdapter() {

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
            convertView = mLayoutInflater.inflate(R.layout.add_head_bottom_sheet, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.btnSave.setOnClickListener {

            if (viewHolder.edtPassword.text.isNullOrEmpty()) {
                Toast.makeText(mContext, "Enter Member Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Utility.hideKeyboard(mContext as AppCompatActivity)
        }

        viewHolder.ivCancel.setOnClickListener {
            FamilyDetailActivity.addHeadDialog?.dismiss()
            Utility.hideKeyboard(mContext as AppCompatActivity)
        }

        return convertView
    }

    internal class ViewHolder(view: View) {
        var btnSave: AppCompatButton = view.findViewById(R.id.btn_save)
        var edtPassword: EditText = view.findViewById(R.id.edt_password)
        var ivCancel: ImageView = view.findViewById(R.id.iv_cancel)
    }
}