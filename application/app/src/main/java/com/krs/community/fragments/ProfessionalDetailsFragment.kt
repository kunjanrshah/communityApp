package com.krs.community.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.easywaylocation.EasyWayLocation
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.app.AppController.Companion.mApplication
import com.krs.community.databinding.FragmentProfessionalDetailsBinding
import com.krs.community.listeners.EditMemberListener
import com.krs.community.listeners.ImageUploadListener
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.*
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import kotlinx.android.synthetic.main.fragment_professional_details.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File

class ProfessionalDetailsFragment : Fragment(), KodeinAware, EditMemberListener, UCropFragmentCallback, ImageUploadListener {

    lateinit var binding: FragmentProfessionalDetailsBinding
    private lateinit var member: Member
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance()
    var numberOfLines = 5
    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_professional_details, container, false)
        profileDetailViewModel = ViewModelProvider(this, factory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener = this
        profileDetailViewModel.mImageUploadListener = this
        member = arguments?.getSerializable(getString(R.string.member)) as Member
        val loginMember = Guru.getString(getString(R.string.loginMember), "")
        val loginMem = Gson().fromJson(loginMember, Member::class.java)
        if (member.id == loginMem.id || member.headId == loginMem.id) {
            binding.imgLogo.isEnabled = true
            binding.edtComName.isFocusable = true
            binding.spMainCat.isClickable = true
            binding.spSubCat.isClickable = true
            binding.spOccupation.isClickable = true
            binding.edtUrl.isFocusable = true
            binding.edtDetail.isFocusable = true
            binding.edtAddr.isFocusable = true
        } else {
            binding.imgLogo.isEnabled = false
            binding.edtComName.isFocusable = false
            binding.spMainCat.isClickable = false
            binding.spSubCat.isClickable = false
            binding.spOccupation.isClickable = false
            binding.edtUrl.isFocusable = false
            binding.edtDetail.isFocusable = false
            binding.edtAddr.isFocusable = false
        }

        if (!member.businessLogo.isNullOrEmpty()) {
            try {
                val str = getString(R.string.base_url_logo) + "" + member.businessLogo
                Glide.with(mApplication).load(str).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(binding.imgLogo)
            } catch (e: Exception) {
                e.message
            }
        }

        if (!member.officeLat.isNullOrEmpty() && !member.officeLng.isNullOrEmpty()) {

            ProfileDetailActivity.cur_lat.observeForever {
                try {
                    if (ProfileDetailActivity.cur_lat.value != null && ProfileDetailActivity.cur_lng.value != null) {
                        var dist = EasyWayLocation.calculateDistance(member.officeLat.toDouble(), member.officeLng.toDouble(), ProfileDetailActivity.cur_lat.value!!.toDouble(), ProfileDetailActivity.cur_lng.value!!.toDouble())
                        dist /= 1000
                        binding.tvDistance.text = String.format(getString(R.string.kmPDetail), dist)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            ProfileDetailActivity.cur_lng.observeForever {
                try {
                    if (ProfileDetailActivity.cur_lat.value != null && ProfileDetailActivity.cur_lng.value != null) {
                        var dist = EasyWayLocation.calculateDistance(member.officeLat.toDouble(), member.officeLng.toDouble(), ProfileDetailActivity.cur_lat.value!!.toDouble(), ProfileDetailActivity.cur_lng.value!!.toDouble())
                        dist /= 1000
                        binding.tvDistance.text = String.format(getString(R.string.kmPDetail), dist)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } else {
            binding.tvDistance.text = "Work"
        }
        binding.llWork.setOnClickListener {
            SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText(getString(R.string.OfficeLocation))
                    .setContentText(getString(R.string.WithGoogle))
                    .setConfirmText(getString(R.string.SetDetail))
                    .setCancelText(getString(R.string.ViewDetails))
                    .setCustomImage(R.drawable.ic_app)
                    .setConfirmClickListener {
                        it.dismiss()
                        if (member.headId == "0" && !member.id.isNullOrEmpty()) {
                            val jsonObject = JSONObject()
                            jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                            jsonObject.put(getString(R.string.id), member.id)
                            jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                            jsonObject.put(getString(R.string.office_lat), ProfileDetailActivity.cur_lat.value)
                            jsonObject.put(getString(R.string.office_lng), ProfileDetailActivity.cur_lng.value)
                            val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                            startSweetProgress(activity, getString(R.string.updatingLocationDetail), getString(R.string.PleasWaitDetails))
                            profileDetailViewModel.updateProfile(profile, true)
                        } else {
                            displaySnackBarWithBottomMargin(ll_main, getString(R.string.headDetails))
                        }
                    }
                    .setCancelClickListener {
                        it.dismiss()
                        showDirections(activity, member.officeLat.toDouble(), member.officeLng.toDouble(), "${member.firstName}'s Work")
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
            pickFromGallery(activity!!)
        }

        getMasterList()
        return binding.root
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_GALLERY_REQUEST) {
            pickFromGallery(activity!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_GALLERY_REQUEST) {
                val selectedUri = data?.data
                if (selectedUri != null) {
                    startCrop(selectedUri, activity!!)
                } else {
                    binding.llMain.snackbar(getString(R.string.SelectedImageDetails), Snackbar.LENGTH_SHORT)
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                data?.let {
                    val resultUri = UCrop.getOutput(it)
                    if (resultUri != null) {
                        try {
                            Glide.with(mApplication).load(resultUri).thumbnail(0.5f).into(binding.imgLogo)
                        } catch (e: Exception) {
                            e.message
                        }
                        logger.debug("resultUri: $resultUri")
                        try {
                            val uploadImage = File(resultUri.path.toString())
                            startSweetProgress(activity!!, getString(R.string.imageDetails), getString(R.string.loading))
                            profileDetailViewModel.uploadImage(uploadImage, member.id.toString(), getString(R.string.company))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        if (resultCode == UCrop.RESULT_ERROR) {
            data?.let { handleCropError(it, activity!!) }
        }
    }


    fun getSaveData(jsonObject: JSONObject) {
        try {
            jsonObject.put(getString(R.string.business_logo), "")
            jsonObject.put(getString(R.string.company_name), binding.edtComName.text.trim())
            jsonObject.put(getString(R.string.business_category_id), profileDetailViewModel.selectedBusinessCategoryId)
            jsonObject.put(getString(R.string.business_sub_category_id), profileDetailViewModel.selectedBusinessSubCategoryId)
            jsonObject.put(getString(R.string.occupation_id), profileDetailViewModel.selectedOccupationId)
            jsonObject.put(getString(R.string.website), binding.edtUrl.text.trim())
            jsonObject.put(getString(R.string.work_details), binding.edtDetail.text.trim())
            jsonObject.put(getString(R.string.business_address), binding.edtAddr.text.trim())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMasterList() = Coroutines.main {
        profileDetailViewModel.lstBusinessCategoryName.await().observe(viewLifecycleOwner, Observer {
            binding.spMainCat.setItems(it.toTypedArray())
            binding.spMainCat.setExpandTint(R.color.black)
        })
        profileDetailViewModel.businessCategoryIds.await().observe(viewLifecycleOwner, Observer {
            profileDetailViewModel.lstBusinessCategoryId = it
        })
        profileDetailViewModel.businessCategoryName.await().observe(viewLifecycleOwner, Observer {
            binding.spMainCat.setText(it)
        })


        profileDetailViewModel.lstBusinessSubCategoryName.await().observe(viewLifecycleOwner, Observer {
            binding.spSubCat.setItems(it.toTypedArray())
            binding.spSubCat.setExpandTint(R.color.black)
        })
        profileDetailViewModel.businessSubCategoryIds.await().observe(viewLifecycleOwner, Observer {
            profileDetailViewModel.lstBusinessSubCategoryId = it
        })
        profileDetailViewModel.businessSubCategoryName.await().observe(viewLifecycleOwner, Observer {
            binding.spSubCat.setText(it)
        })


        profileDetailViewModel.lstOccupationName.await().observe(viewLifecycleOwner, Observer {
            binding.spOccupation.setItems(it.toTypedArray())
            binding.spOccupation.setExpandTint(R.color.black)
        })
        profileDetailViewModel.occupationIds.await().observe(viewLifecycleOwner, Observer {
            profileDetailViewModel.lstOccupationId = it
        })
        profileDetailViewModel.occupationName.await().observe(viewLifecycleOwner, Observer {
            binding.spOccupation.setText(it)
        })

    }

    override fun getScanResult(response: SmartFilterResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUpdateOrAddResult(response: UpdateProfileResponse) {
        hideSweetProgress()
        val updatedMem = response.member
        member.officeLat = ProfileDetailActivity.cur_lat.value.toString()
        member.officeLng = ProfileDetailActivity.cur_lng.value.toString()
        val percentage = Utility.calculatePercentage(updatedMem)
        ProfileDetailActivity.setPercentage(percentage)
        if (updatedMem.id == member.id) {
            Guru.putString(getString(R.string.loginMember), Gson().toJson(updatedMem))
        }
        if (ProfileDetailActivity.cur_lat.value != null && ProfileDetailActivity.cur_lng.value != null) {
            var dist = EasyWayLocation.calculateDistance(member.officeLat.toDouble(), member.officeLng.toDouble(), ProfileDetailActivity.cur_lat.value!!.toDouble(), ProfileDetailActivity.cur_lng.value!!.toDouble())
            dist /= 1000
            binding.tvDistance.text = String.format(getString(R.string.kmPDetail), dist)
        }
        displaySnackBarWithBottomMargin(binding.llMain, "Office location updated!")
    }

    override suspend fun getFailure(message: String) {
        hideSweetProgress()
        displaySnackBarWithBottomMargin(binding.llMain, message)
    }

    override fun onCropFinish(result: UCropFragment.UCropResult?) {
        when (result?.mResultCode) {
            AppCompatActivity.RESULT_OK -> handleCropResult(result.mResultData, activity!!, binding.imgLogo)
            UCrop.RESULT_ERROR -> handleCropError(result.mResultData, activity!!)
        }
    }

    override fun loadingProgress(p0: Boolean) {

    }

    override fun getResult(jsonObject: JsonObject) {
        hideSweetProgress()
        member.businessLogo = jsonObject.get("business_logo").asString
        Guru.putString(getString(R.string.loginMember), Gson().toJson(member))
        displaySnackBarWithBottomMargin(binding.llMain, "Logo updated!")
    }

    override suspend fun onFailure(message: String) {
        hideSweetProgress()
        binding.llMain.snackbar(getString(R.string.went_wrong), Snackbar.LENGTH_LONG)
    }
}