package com.krs.community.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.databinding.ActivityProfileDetailBinding
import com.krs.community.fragments.*
import com.krs.community.interfaces.EditMemberListener
import com.krs.community.model.Member
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.ProfileDetailViewModelFactory
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class ProfileDetailActivity : BaseActivity(), KodeinAware,EditMemberListener, UCropFragmentCallback {

    private lateinit var binding: ActivityProfileDetailBinding
    private var str_profile_hash = ""
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance()
    val listFragments = mutableListOf<Fragment>()
    private lateinit var head_id:String
    private lateinit var id:String
    private var isProfileImage=false
    private lateinit var member:Member
    override val kodein by kodein()
    private lateinit var logger: Logger
    private lateinit var professionalDetailsFragment:ProfessionalDetailsFragment
    companion object {
        private val TAG = ProfileDetailActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ProfileDetailActivity, R.layout.activity_profile_detail)
        logger = Logger(TAG)
        profileDetailViewModel = ViewModelProviders.of(this, factory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.mdtp_white, false)
        }

        val mainDetailsFragment = MainDetailsFragment()
        val personalDetailsFragment = PersonalDetailsFragment()
        professionalDetailsFragment = ProfessionalDetailsFragment()
        val matrimonyDetailsFragment = MatrimonyDetailsFragment()

        listFragments.add(mainDetailsFragment)
        listFragments.add(personalDetailsFragment)
        listFragments.add(professionalDetailsFragment)
        listFragments.add(matrimonyDetailsFragment)

        member= intent.getSerializableExtra("member") as Member

        setMemberValues()

//        var id:String=intent.getStringExtra("id")
        /*val filter = SearchData()
        filter.start = "0"
        filter.length = "1"
        val filterBy = FilterBy()
        filterBy.id = "39"  //id
        filter.filterBy = filterBy
        profileDetailViewModel.getMemberByFilters(filter)*/

        val imageSteps = findViewById<ImageSteps>(R.id.imageSteps)
        imageSteps.setSteps(R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four)
        imageSteps.scaleUp = 2.0f
        imageSteps.animationDuration = 500
        imageSteps.setupWithViewPager(binding.viewpager)

        binding.llViewFamily.setOnClickListener {

            val intent= Intent(this,FamilyDetailActivity::class.java)
            if(head_id.equals("0")){
                intent.putExtra("id",id)
            }else{
                intent.putExtra("id",head_id)
            }
            startActivity(intent)
            finish()
            Utility.fade(this)
        }

        binding.imgBack.setOnClickListener { v: View? ->
            finish()
            Utility.fade(this)
        }

        binding.tvSave.setOnClickListener {
            val jsonObject=JSONObject()
            mainDetailsFragment.getSaveData(jsonObject)
            personalDetailsFragment.getSaveData(jsonObject)
            professionalDetailsFragment.getSaveData(jsonObject)
            matrimonyDetailsFragment.getSaveData(jsonObject)
            jsonObject.put(getString(R.string.user_id),"39")
            jsonObject.put(getString(R.string.access_token),"c764e")
            // jsonObject.put(getString(R.string.updated_dt),Utility.DatetoString(Date(),Utility.yyyy_MM_dd))
            val profile=  JsonParser().parse(jsonObject.toString()) as JsonObject
            Utility.startSweetProgress(this,"Updating your profie","Please wait...")
            profileDetailViewModel.updateProfile(profile)
            Log.d(ProfileDetailActivity::class.java.simpleName,"jsonObject: "+jsonObject.toString())
        }

        binding.imgProfile.setOnClickListener {
            isProfileImage=true
            pickFromGallery(this)
        }

        Utility.hideKeyboard(this)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_REQUEST) {
                val selectedUri = data?.data
                if (selectedUri != null) {
                    startCrop(selectedUri,this)
                } else {
                    Toast.makeText(this@ProfileDetailActivity, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                if(isProfileImage){
                    isProfileImage=false
                    data?.let { handleCropResult(it,this,binding.imgProfile) }
                }else{
                    professionalDetailsFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            data?.let { handleCropError(it,this) }
        }
    }

    fun setMemberValues(){
        if(member.firstName.isNotEmpty()){
            binding.txtTitle.text = "${member.firstName}'s Profile"
            binding.tvSave.text="Save"
        }else{
            binding.tvSave.text="Add"
            binding.txtTitle.text = "New Profile"
        }

        binding.switch4.isOn = !member.isLocationEnable.isNullOrEmpty() && member.isLocationEnable.equals("1")
        if (!member.profilePic.isNullOrEmpty()) {
            try {
                Glide.with(AppController.mApplication).load(member.profilePic).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(binding.imgProfile)
            } catch (e: Exception) {
                e.message
            }
        }


        binding.progressView.setAnimate(true)
        binding.progressView.setAnimateDuration(5000)

        var percentage=0
        if(member.profileCompleted.isNotEmpty()){
            percentage=0
        }

        binding.progressView.setProgress(percentage,true)
        Handler().postDelayed({
            binding.imgProfile.alpha=0.10f
        }, 500)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.20f
        }, 1000)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.30f
        }, 1500)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.40f
        }, 2000)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.50f
        }, 2500)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.60f
        }, 3000)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.70f
        }, 3500)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.80f
        }, 4000)

        Handler().postDelayed({
            binding.imgProfile.alpha=0.90f
        }, 4500)

        Handler().postDelayed({
            binding.imgProfile.alpha=1.00f
            binding.tvPercent.setText("${percentage}%")
        }, 5000)
        head_id=member.headId
        id=member.id
        val bundle = Bundle()
        bundle.putSerializable("member", member)
        listFragments.get(0).arguments = bundle
        listFragments.get(1).arguments = bundle
        listFragments.get(2).arguments = bundle
        listFragments.get(3).arguments = bundle
        binding.viewpager.adapter = MyPagerAdapter(listFragments, supportFragmentManager)
    }

    override fun getMessage(response: UpdateProfileResponse) {
        Utility.hideSweetProgress()
        binding.llParent.snackbar("Profile updated!",Snackbar.LENGTH_LONG)
    }


    override fun getFailure(message: String) {
        Utility.hideSweetProgress()
        binding.llParent.snackbar("Something went wrong!",Snackbar.LENGTH_LONG)
        Log.d(ProfileDetailActivity::class.java.simpleName,"getFailure: "+message)
    }

    class MyPagerAdapter(val listFragments: List<Fragment>, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return listFragments[position]
        }

        override fun getCount(): Int {
            return listFragments.size
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            BaseActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION ->

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery(this)
                } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                    displayNeverAskAgainDialog(this)
                } else {
                    promptReadPermission(this)
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }



    override fun onCropFinish(result: UCropFragment.UCropResult) {
        when (result.mResultCode) {
            BaseActivity.RESULT_OK -> handleCropResult(result.mResultData,this,binding.imgProfile)
            UCrop.RESULT_ERROR -> handleCropError(result.mResultData,this)
        }
    }

    override fun loadingProgress(showLoader: Boolean) {
        //mShowLoader = showLoader
    }

}