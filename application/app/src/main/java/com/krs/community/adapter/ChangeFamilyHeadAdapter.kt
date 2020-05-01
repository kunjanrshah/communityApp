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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.fragments.FamilyDetailActivity
import com.krs.community.listeners.ByKeywordListener
import com.krs.community.listeners.RefreshListListener
import com.krs.community.model.Member
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel
import org.json.JSONObject

class ChangeFamilyHeadAdapter(private val mContext: Context, val profileDetailViewModel: ProfileDetailViewModel, val member: Member) : BaseAdapter(), ByKeywordListener {

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
            profileDetailViewModel.keywordListener = this
            refreshListListener = (mContext as FamilyDetailActivity)
            convertView = mLayoutInflater.inflate(R.layout.add_head_bottom_sheet, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.btnSave.setOnClickListener {

            if (viewHolder.edtPassword.text.isNullOrEmpty()) {
                Toast.makeText(mContext, "Enter Member New Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!(viewHolder.edtPassword.text.toString().length in 6..12)) {
                Toast.makeText(mContext, "Password length between 6 to 12", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Utility.hideKeyboard(mContext as AppCompatActivity)
            FamilyDetailActivity.addHeadDialog?.dismiss()
            SweetAlertDialog(mContext, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Change Family Head")
                    .setContentText("${member.firstName} will Change to Family Head")
                    .setConfirmText(mContext.getString(R.string.YesPleaseCity))
                    .setCancelText(mContext.getString(R.string.no))
                    .setCustomImage(R.drawable.ic_app)
                    .showCancelButton(true)
                    .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                        sweetAlertDialog.dismissWithAnimation()

                        val jsonObject = JSONObject()
                        jsonObject.put(mContext.getString(R.string.user_id), Guru.getString(mContext.getString(R.string.user_id), ""))
                        jsonObject.put(mContext.getString(R.string.access_token), Guru.getString(mContext.getString(R.string.access_token), ""))
                        jsonObject.put(mContext.getString(R.string.extra_info), "2")
                        jsonObject.put(mContext.getString(R.string.id), member.headId)
                        jsonObject.put(mContext.getString(R.string.head_id), member.id)
                        jsonObject.put(mContext.getString(R.string.profile_password), viewHolder.edtPassword.text)
                        FamilyDetailActivity.newHeadId = member.id
                        Utility.startSweetProgress(mContext, "Change Family Head", mContext.getString(R.string.pleaseWait))
                        val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                        profileDetailViewModel.changeFamilyHead(profile)
                    }
                    .show()
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

    override fun getMembers(response: searchByKeywordsResponse) {
        Utility.hideSweetProgress()
        if (response.success) {
            refreshListListener?.refreshList()
            Utility.startSweetDialog(mContext, SweetAlertDialog.SUCCESS_TYPE, "Success", "${member.firstName} changed to FamilyHead")
        }

        if (response.success) {
            FamilyDetailActivity.headId = FamilyDetailActivity.newHeadId
            val loginuser = Guru.getString(mContext.getString(R.string.loginMember), "")
            val loginMember = Gson().fromJson(loginuser, Member::class.java)
            loginMember.headId = FamilyDetailActivity.newHeadId
            Guru.putString(mContext.getString(R.string.loginMember), Gson().toJson(loginMember))
            refreshListListener?.refreshList()
            Utility.startSweetDialog(mContext, SweetAlertDialog.SUCCESS_TYPE, "Success", "FamilyHead Changed")
        }
    }

    override suspend fun getFailure(message: String) {
        Utility.hideSweetProgress()
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}