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
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.squti.guru.Guru
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.fragments.FamilyDetailActivity
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.listeners.ByKeywordListener
import com.krs.community.listeners.RefreshListListener
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AddMemberAdapter(private val mContext: Context, val profileDetailViewModel: ProfileDetailViewModel, val head_id: String) : BaseAdapter(), ByKeywordListener {

    private val mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var refreshListListener: RefreshListListener? = null

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
            convertView = mLayoutInflater.inflate(R.layout.add_member_bottom_sheet, parent, false)
            viewHolder = ViewHolder(convertView)
            profileDetailViewModel.keywordListener = this
            refreshListListener = (mContext as FamilyDetailActivity)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        Coroutines.main {
            profileDetailViewModel.lstRelationName.await().observe(mContext as AppCompatActivity, Observer {
                if (it.isNotEmpty()) {
                    val list = it.subList(1, it.size)
                    Collections.sort(list)
                    val lstRelation = ArrayList<String>()
                    lstRelation.addAll(mContext.resources.getStringArray(R.array.lst_relative))
                    for (relation in list) {
                        if (!lstRelation.contains(relation)) {
                            lstRelation.add(relation)
                        }
                    }
                    viewHolder.spRelation.setItems(lstRelation.toTypedArray())
                    viewHolder.spRelation.setExpandTint(R.color.black)
                }
            })
        }

        viewHolder.btnSave.setOnClickListener {
            if (viewHolder.edtCode.text.isNullOrEmpty()) {
                Toast.makeText(mContext, "Enter Member Code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (viewHolder.edtPassword.text.isNullOrEmpty()) {
                Toast.makeText(mContext, "Enter Member Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!(viewHolder.edtPassword.text.toString().length in 6..12)) {
                Toast.makeText(mContext, "Password length between 6 to 12", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FamilyDetailActivity.addMemberDialog?.dismiss()
            Coroutines.io {
                val id: Int? = profileDetailViewModel.getIdByRelation(viewHolder.spRelation.text.toString())
                Coroutines.main {
                    if (id == null || id.equals("0") || id.equals("")) {
                        Toast.makeText(mContext, "Select Relation", Toast.LENGTH_SHORT).show()
                        return@main
                    }

                    val jsonObject = JSONObject()
                    jsonObject.put(mContext.getString(R.string.user_id), Guru.getString(mContext.getString(R.string.user_id), ""))
                    jsonObject.put(mContext.getString(R.string.access_token), Guru.getString(mContext.getString(R.string.access_token), ""))
                    jsonObject.put(mContext.getString(R.string.profile_password), viewHolder.edtPassword.text)
                    jsonObject.put(mContext.getString(R.string.head_id), head_id)
                    jsonObject.put(mContext.getString(R.string.id), Guru.getString(mContext.getString(R.string.user_id), ""))
                    jsonObject.put(mContext.getString(R.string.idList), viewHolder.edtCode.text.trim())
                    jsonObject.put(mContext.getString(R.string.relation_id), id)
                    jsonObject.put(mContext.getString(R.string.extra_info), "1")
                    jsonObject.put(mContext.getString(R.string.status), "1")
                    Utility.startSweetProgress(mContext, mContext.getString(R.string.addingProfile), mContext.getString(R.string.pleaseWait))
                    val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                    profileDetailViewModel.changeFamilyHead(profile)

                    Coroutines.main {
                        Utility.hideKeyboard(mContext as AppCompatActivity)
                    }
                }
            }
        }

        viewHolder.ivCancel.setOnClickListener {
            FamilyDetailActivity.addMemberDialog?.dismiss()
            Utility.hideKeyboard(mContext as AppCompatActivity)
        }

        return convertView
    }

    internal class ViewHolder(view: View) {
        var btnSave: AppCompatButton = view.findViewById(R.id.btn_save)
        var spRelation: JRSpinner = view.findViewById(R.id.sp_relation)
        var edtCode: EditText = view.findViewById(R.id.edt_code)
        var edtPassword: EditText = view.findViewById(R.id.edt_password)
        var ivCancel: ImageView = view.findViewById(R.id.iv_cancel)
    }

    override fun getMembers(response: searchByKeywordsResponse) {
        Utility.hideSweetProgress()
        if (response.success) {
            refreshListListener?.refreshList()
            Utility.startSweetDialog(mContext, SweetAlertDialog.SUCCESS_TYPE, "Success", "Members added")
        }
    }

    override suspend fun getFailure(message: String) {
        Utility.hideSweetProgress()
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}