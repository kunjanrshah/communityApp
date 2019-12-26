package com.krs.community.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.easywaylocation.EasyWayLocation
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.BaseActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentProfessionalDetailsBinding
import com.krs.community.interfaces.EditMemberListener
import com.krs.community.model.Member
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.ProfileDetailViewModelFactory
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dashboard_menu.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File

class ProfessionalDetailsFragment : Fragment(), KodeinAware, EditMemberListener {

    lateinit var binding: FragmentProfessionalDetailsBinding
    private lateinit var member: Member
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance()
    var numberOfLines = 5

    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_professional_details, container, false)
        profileDetailViewModel = ViewModelProviders.of(this, factory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener=this

        member = arguments?.getSerializable(getString(R.string.member)) as Member

        if (!member.businessLogo.isNullOrEmpty()) {
            try {
                Glide.with(AppController.mApplication).load(member.businessLogo).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(binding.imgLogo)
            } catch (e: Exception) {
                e.message
            }
        }

        if(!member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()){
            ProfileDetailActivity.cur_lat.observeForever {
                if(ProfileDetailActivity.cur_lat.value!=null && ProfileDetailActivity.cur_lng.value!=null){
                    var dist= EasyWayLocation.calculateDistance(member.officeLat.toDouble(),member.officeLng.toDouble(),ProfileDetailActivity.cur_lat.value!!.toDouble(),ProfileDetailActivity.cur_lng.value!!.toDouble())
                    dist /= 1000
                    binding.tvDistance.text=String.format("%.2f KM",dist)
                }
            }

            ProfileDetailActivity.cur_lng.observeForever {
                if(ProfileDetailActivity.cur_lat.value!=null && ProfileDetailActivity.cur_lng.value!=null){
                    var dist= EasyWayLocation.calculateDistance(member.officeLat.toDouble(),member.officeLng.toDouble(),ProfileDetailActivity.cur_lat.value!!.toDouble(),ProfileDetailActivity.cur_lng.value!!.toDouble())
                    dist /= 1000
                    binding.tvDistance.text=String.format("%.2f KM",dist)
                }
            }
        }else{
            binding.tvDistance.text="Work"
        }
        binding.llWork.setOnClickListener {
            SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Office Location")
                    .setContentText("With Google Map")
                    .setConfirmText("Set")
                    .setCancelText("View")
                    .setConfirmClickListener {
                        it.dismiss()
                        val jsonObject = JSONObject()

                        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id),""))
                        jsonObject.put(getString(R.string.id), member.id)
                        jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token),""))
                        jsonObject.put(getString(R.string.office_lat), ProfileDetailActivity.cur_lat.value)
                        jsonObject.put(getString(R.string.office_lng), ProfileDetailActivity.cur_lng.value)

                        val profile = JsonParser().parse(jsonObject.toString()) as JsonObject

                        Utility.startSweetProgress(activity, "Updating your office location", "Please wait...")
                        profileDetailViewModel.updateProfile(profile, true)

                    }
                    .setCancelClickListener {
                        it.dismiss()
                    }
                    .show()
        }


        if (!member.workDetails.isNullOrEmpty()) {
            binding.edtDetail.setText(member.workDetails)
        }
        binding.edtDetail.addTextChangedListener(object : TextWatcher {
            private var text: String? = null
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lineCount: Int = binding.edtDetail.lineCount
                if (lineCount > numberOfLines) {
                    binding.edtDetail.setText(text)
                }
            }
        })

        binding.edtDetail.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val editTextLineCount: Int = (v as EditText).lineCount
                if (editTextLineCount >= numberOfLines) return@OnKeyListener true
            }
            false
        })

        if (!member.businessCategoryId.isNullOrEmpty()) {
            profileDetailViewModel.selectedBusinessCategoryId = Integer.parseInt(member.businessCategoryId)
        }
        if (!member.businessSubCategoryId.isNullOrEmpty()) {
            profileDetailViewModel.selectedBusinessSubCategoryId = Integer.parseInt(member.businessSubCategoryId)
        }
        if (!member.occupationId.isNullOrEmpty()) {
            profileDetailViewModel.selectedOccupationId = Integer.parseInt(member.occupationId)
        }

        binding.spMainCat.setOnItemClickListener {
            profileDetailViewModel.selectedBusinessCategoryId = profileDetailViewModel.lstBusinessCategoryId[it]
        }

        binding.spSubCat.setOnItemClickListener {
            profileDetailViewModel.selectedBusinessSubCategoryId = profileDetailViewModel.lstBusinessSubCategoryId[it]
        }
        binding.spOccupation.setOnItemClickListener {
            profileDetailViewModel.selectedOccupationId = profileDetailViewModel.lstOccupationId[it]
        }

        binding.edtComName.setText(member.companyName)
        binding.edtUrl.setText(member.website)
        binding.edtAddr.setText(member.businessAddress)
        binding.edtAddr.addTextChangedListener(object : TextWatcher {
            private var text: String? = null
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lineCount: Int = binding.edtAddr.lineCount
                if (lineCount > numberOfLines) {
                    binding.edtAddr.setText(text)
                }
            }
        })

        binding.edtAddr.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val editTextLineCount: Int = (v as EditText).lineCount
                if (editTextLineCount >= numberOfLines) return@OnKeyListener true
            }
            false
        })

        binding.imgLogo.setOnClickListener {
            activity?.let { it1 -> pickFromGallery(it1) }
        }

        getMasterList()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let { activity?.let { it1 -> handleCropResult(it, it1,binding.imgLogo) } }
    }


    fun getSaveData(jsonObject:JSONObject){
        try{
            jsonObject.put(getString(R.string.business_logo),"")
            jsonObject.put(getString(R.string.company_name),binding.edtComName.text.trim())
            jsonObject.put(getString(R.string.business_category_id),profileDetailViewModel.selectedBusinessCategoryId)
            jsonObject.put(getString(R.string.business_sub_category_id),profileDetailViewModel.selectedBusinessSubCategoryId)
            jsonObject.put(getString(R.string.occupation_id),profileDetailViewModel.selectedOccupationId)
            jsonObject.put(getString(R.string.website),binding.edtUrl.text.trim())
            jsonObject.put(getString(R.string.work_details),binding.edtDetail.text.trim())
            jsonObject.put(getString(R.string.business_address),binding.edtAddr.text.trim())
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getMasterList() = Coroutines.main {
        profileDetailViewModel.lstBusinessCategoryName.await().observe(this, Observer {
            binding.spMainCat.setItems(it.toTypedArray())
            binding.spMainCat.setExpandTint(R.color.black)
        })
        profileDetailViewModel.businessCategoryIds.await().observe(this, Observer {
            profileDetailViewModel.lstBusinessCategoryId = it
        })
        profileDetailViewModel.businessCategoryName.await().observe(this, Observer {
            binding.spMainCat.setText(it)
        })


        profileDetailViewModel.lstBusinessSubCategoryName.await().observe(this, Observer {
            binding.spSubCat.setItems(it.toTypedArray())
            binding.spSubCat.setExpandTint(R.color.black)
        })
        profileDetailViewModel.businessSubCategoryIds.await().observe(this, Observer {
            profileDetailViewModel.lstBusinessSubCategoryId = it
        })
        profileDetailViewModel.businessSubCategoryName.await().observe(this, Observer {
            binding.spSubCat.setText(it)
        })


        profileDetailViewModel.lstOccupationName.await().observe(this, Observer {
            binding.spOccupation.setItems(it.toTypedArray())
            binding.spOccupation.setExpandTint(R.color.black)
        })
        profileDetailViewModel.occupationIds.await().observe(this, Observer {
            profileDetailViewModel.lstOccupationId = it
        })
        profileDetailViewModel.occupationName.await().observe(this, Observer {
            binding.spOccupation.setText(it)
        })

    }

    override fun getMessage(response: UpdateProfileResponse) {
        Utility.hideSweetProgress()
        val updatedMem = response.member
        member.officeLat=ProfileDetailActivity.cur_lat.value.toString()
        member.officeLng=ProfileDetailActivity.cur_lng.value.toString()
        val percentage = Utility.calculatePercentage(updatedMem)
        ProfileDetailActivity.setPercentage(percentage)
        if (updatedMem.headId == "0") {
            Guru.putString(getString(R.string.loginUser), Gson().toJson(updatedMem))
            Guru.putString(getString(R.string.user_mobile), updatedMem.mobile)
        }
        if(ProfileDetailActivity.cur_lat.value!=null && ProfileDetailActivity.cur_lng.value!=null){
            var dist= EasyWayLocation.calculateDistance(member.officeLat.toDouble(),member.officeLng.toDouble(),ProfileDetailActivity.cur_lat.value!!.toDouble(),ProfileDetailActivity.cur_lng.value!!.toDouble())
            dist /= 1000
            binding.tvDistance.text=String.format("%.2f KM",dist)
        }
        Utility.displaySnackBarWithBottomMargin(binding.llMain, "Office location updated!")
    }

    override fun getFailure(message: String) {
        Utility.displaySnackBarWithBottomMargin(binding.llMain, message)
    }
}