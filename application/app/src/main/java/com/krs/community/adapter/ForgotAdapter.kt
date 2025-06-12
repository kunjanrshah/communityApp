package com.krs.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.krs.community.R
import com.krs.community.utils.Utility

class ForgotAdapter(private val mContext: Context) : BaseAdapter() {

    private val mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var forgotInterface: ForgotInterface

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
            forgotInterface = mContext as ForgotInterface
            convertView = mLayoutInflater.inflate(R.layout.forgot_password_bottom_sheet, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.btnSubmit.setOnClickListener {
            val str = viewHolder.edtEM.text.trim()
            if (str.isNotEmpty()) {
                if (Utility.isEmailValid(str.toString())) {
                    forgotInterface.sendEmail(str.toString())
                } else {
                    forgotInterface.sendMobile(str.toString())
                }
            }
        }

        return convertView
    }

    interface ForgotInterface {
        fun sendEmail(email: String)
        fun sendMobile(mobile: String)
    }


    internal class ViewHolder(view: View) {
        var btnSubmit: AppCompatButton = view.findViewById(R.id.btn_submit)
        var edtEM: EditText = view.findViewById(R.id.edt_em)
    }
}