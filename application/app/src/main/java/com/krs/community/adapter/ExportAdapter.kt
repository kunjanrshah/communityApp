package com.krs.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.krs.community.R

class ExportAdapter(var mContext: Context) : BaseAdapter() {
    private var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var listener: exportPdfListener? = null

    fun setExportListner(exportPdfListener: exportPdfListener?) {
        this.listener = exportPdfListener
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
            convertView = mLayoutInflater.inflate(R.layout.export_sheet_dialog, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.llPhoto.setOnClickListener {
            viewHolder.llPhoto.visibility = View.GONE
        }
        viewHolder.llName.setOnClickListener {
            viewHolder.llName.visibility = View.GONE
        }
       /* viewHolder.llFather.setOnClickListener {
            viewHolder.llFather.visibility = View.GONE
        }*/
        viewHolder.llMother.setOnClickListener {
            viewHolder.llMother.visibility = View.GONE
        }
        viewHolder.llMarital.setOnClickListener {
            viewHolder.llMarital.visibility = View.GONE
        }
        viewHolder.llGender.setOnClickListener {
            viewHolder.llGender.visibility = View.GONE
        }
        viewHolder.llBlood.setOnClickListener {
            viewHolder.llBlood.visibility = View.GONE
        }
        viewHolder.llEmail.setOnClickListener {
            viewHolder.llEmail.visibility = View.GONE
        }
        viewHolder.llMobile.setOnClickListener {
            viewHolder.llMobile.visibility = View.GONE
        }

        viewHolder.llBdate.setOnClickListener {
            viewHolder.llBdate.visibility = View.GONE
        }
        viewHolder.llAddress.setOnClickListener {
            viewHolder.llAddress.visibility = View.GONE
        }
        viewHolder.llState.setOnClickListener {
            viewHolder.llState.visibility = View.GONE
        }
        viewHolder.llCity.setOnClickListener {
            viewHolder.llCity.visibility = View.GONE
        }
        viewHolder.llArea.setOnClickListener {
            viewHolder.llArea.visibility = View.GONE
        }
        viewHolder.llPincode.setOnClickListener {
            viewHolder.llPincode.visibility = View.GONE
        }
        viewHolder.llLocalComm.setOnClickListener {
            viewHolder.llLocalComm.visibility = View.GONE
        }
        viewHolder.llSubComm.setOnClickListener {
            viewHolder.llSubComm.visibility = View.GONE
        }
        viewHolder.llAll.setOnClickListener {
            viewHolder.llPincode.visibility = View.VISIBLE
            viewHolder.llLocalComm.visibility = View.VISIBLE
            viewHolder.llSubComm.visibility = View.VISIBLE
            viewHolder.llArea.visibility = View.VISIBLE
            viewHolder.llCity.visibility = View.VISIBLE
            viewHolder.llState.visibility = View.VISIBLE
            viewHolder.llAddress.visibility = View.VISIBLE
            viewHolder.llBdate.visibility = View.VISIBLE
            viewHolder.llMobile.visibility = View.VISIBLE
            viewHolder.llEmail.visibility = View.VISIBLE
            viewHolder.llBlood.visibility = View.VISIBLE
            viewHolder.llGender.visibility = View.VISIBLE
            viewHolder.llMarital.visibility = View.VISIBLE
            viewHolder.llMother.visibility = View.VISIBLE
            // viewHolder.llFather.visibility = View.VISIBLE
            viewHolder.llName.visibility = View.VISIBLE
            viewHolder.llPhoto.visibility = View.VISIBLE
        }

        viewHolder.tvDone.setOnClickListener {
            val filters = ArrayList<String>()
            if (viewHolder.llPincode.isVisible) {
                filters.add("pincode")
            }
            if (viewHolder.llLocalComm.isVisible) {
                filters.add("local_community")
            }
            if (viewHolder.llSubComm.isVisible) {
                filters.add("sub_community")
            }
            if (viewHolder.llArea.isVisible) {
                filters.add("area")
            }
            if (viewHolder.llCity.isVisible) {
                filters.add("city")
            }
            if (viewHolder.llState.isVisible) {
                filters.add("state")
            }
            if (viewHolder.llAddress.isVisible) {
                filters.add("address")
            }
            if (viewHolder.llBdate.isVisible) {
                filters.add("bdate")
            }
            if (viewHolder.llMobile.isVisible) {
                filters.add("mobile")
            }
            if (viewHolder.llEmail.isVisible) {
                filters.add("email")
            }
            if (viewHolder.llBlood.isVisible) {
                filters.add("blood")
            }
            if (viewHolder.llGender.isVisible) {
                filters.add("gender")
            }
            if (viewHolder.llMarital.isVisible) {
                filters.add("marital")
            }
            if (viewHolder.llMother.isVisible) {
                filters.add("mother")
            }
            /* if (viewHolder.llFather.isVisible) {
                 filters.add("father")
             }*/
            if (viewHolder.llName.isVisible) {
                filters.add("name")
            }
            if (viewHolder.llPhoto.isVisible) {
                filters.add("photo")
            }
            listener?.cancelDialog()
            listener?.exportPdf(filters)
        }

        viewHolder.ivCancel.setOnClickListener { v: View? ->
            listener?.cancelDialog()
        }

        return convertView!!
    }

    interface exportPdfListener {
        fun exportPdf(filters: ArrayList<String>)
        fun cancelDialog()
    }

    internal class ViewHolder(view: View) {
        var llPhoto: LinearLayout = view.findViewById(R.id.ll_photo)
        var llName: LinearLayout = view.findViewById(R.id.ll_name)

        //  var llFather: LinearLayout = view.findViewById(R.id.ll_father)
        var ivCancel: ImageView = view.findViewById(R.id.iv_cancel)
        var llMother: LinearLayout = view.findViewById(R.id.ll_mother)
        var llMobile: LinearLayout = view.findViewById(R.id.ll_mobile)
        var llEmail: LinearLayout = view.findViewById(R.id.ll_email)
        var llGender: LinearLayout = view.findViewById(R.id.ll_gender)
        var llMarital: LinearLayout = view.findViewById(R.id.ll_marital)
        var llBlood: LinearLayout = view.findViewById(R.id.ll_blood)
        var llBdate: LinearLayout = view.findViewById(R.id.ll_bdate)
        var llAddress: LinearLayout = view.findViewById(R.id.ll_address)
        var llState: LinearLayout = view.findViewById(R.id.ll_state)
        var llCity: LinearLayout = view.findViewById(R.id.ll_city)
        var llArea: LinearLayout = view.findViewById(R.id.ll_area)
        var llPincode: LinearLayout = view.findViewById(R.id.ll_pincode)
        var llSubComm: LinearLayout = view.findViewById(R.id.ll_sub_comm)
        var llLocalComm: LinearLayout = view.findViewById(R.id.ll_local_comm)
        var llAll: LinearLayout = view.findViewById(R.id.ll_all)
        var tvDone: TextView = view.findViewById(R.id.tv_done)
    }
}