package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppController.Companion.mApplication
import com.krs.community.bkservice.ProcessMainClass
import com.krs.community.bkservice.restarter.RestartServiceBroadcastReceiver
import com.krs.community.databinding.ActivityProfileDetailBinding
import com.krs.community.fragments.*
import com.krs.community.listeners.EditMemberListener
import com.krs.community.listeners.ImageUploadListener
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.*
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.wessam.library.NetworkChecker
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import kotlinx.android.synthetic.main.activity_profile_detail.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File

class ProfileDetailActivity : AppCompatActivity(), KodeinAware, EditMemberListener, UCropFragmentCallback, Listener, LocationData.AddressCallBack, ImageUploadListener {

    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance()
    private val listFragments = mutableListOf<Fragment>()
    private var isProfileImage = false
    private var member: Member? = null
    override val kodein by kodein()
    private lateinit var logger: Logger
    private lateinit var professionalDetailsFragment: ProfessionalDetailsFragment
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var request: LocationRequest
    private var scanId: String? = null
    private var isStopService = false

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mApp = applicationContext as AppController
        mApp.FirebaseAnalytics(this@ProfileDetailActivity, ProfileDetailActivity.javaClass.simpleName)

        getLocationDetail = GetLocationDetail(this, this)
        request = LocationRequest()
        request.interval = INTERVAL
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        easyWayLocation = EasyWayLocation(this, request, true, this)

        binding = DataBindingUtil.setContentView(this@ProfileDetailActivity, R.layout.activity_profile_detail)
        logger = Logger(TAG)
        profileDetailViewModel = ViewModelProvider(this, factory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener = this
        profileDetailViewModel.mImageUploadListener = this
        member = intent.getSerializableExtra(getString(R.string.member)) as Member?
        scanId = intent.getStringExtra(getString(R.string.scanId))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeStatusbarColor(this, R.color.white, false)
        }

        val mainDetailsFragment = MainDetailsFragment()
        val personalDetailsFragment = PersonalDetailsFragment()
        professionalDetailsFragment = ProfessionalDetailsFragment()
        val matrimonyDetailsFragment = MatrimonyDetailsFragment()

        listFragments.add(mainDetailsFragment)
        listFragments.add(personalDetailsFragment)
        listFragments.add(professionalDetailsFragment)
        listFragments.add(matrimonyDetailsFragment)

        val imageSteps = findViewById<ImageSteps>(R.id.imageSteps)
        imageSteps.setSteps(R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four)
        imageSteps.scaleUp = 2.0f
        imageSteps.animationDuration = 500
        imageSteps.setupWithViewPager(binding.viewpager)
        if (member != null) {
            val percentage = Utility.calculatePercentage(member)
            setPercentage(percentage)
            setMemberValues()
        }

        if (!scanId.isNullOrEmpty()) {
            val jsonObject = JSONObject()
            jsonObject.put("" + mApplication.start, "0")
            jsonObject.put("" + mApplication.length, "1")
            val jsonObj = JSONObject()
            jsonObj.put(getString(R.string.id), scanId)
            jsonObject.put(getString(R.string.filter_by), jsonObj)
            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
            profileDetailViewModel.getMemberByFilters(updated)
        }

        if (member?.isLocationEnable == "1") {
            //binding.switchLocation.isActivated = true
            binding.switchLocation.isOn = true
            binding.switchLocation.labelOn = "ON"
            binding.tvDistance.text = getString(R.string.Finding)
            val mem_id = Guru.getString(getString(R.string.member_id), "")
            if (member?.id == mem_id) {
                startLocationService()
                Utility.displaySnackBarWithBottomMargin(binding.viewpager, "You are sharing your location")
            }
        } else {
            binding.switchLocation.isOn = false
            binding.switchLocation.labelOff = "OFF"
            binding.tvDistance.text = getString(R.string.user)
            /*if (!userId.equals(member?.id)) {
                binding.switchLocation.isActivated = false
            }*/
        }

        if (checkFineLocationPermission(this)) {
            easyWayLocation.startLocation()
        } else {
            requestFineLocationPermission(this)
        }

        cur_lat.observe(this, Observer {
            setDistance()
        })

        cur_lng.observe(this, Observer {
            setDistance()
        })

        binding.switchLocation.setOnClickListener {
            if (!binding.switchLocation.isOn) {
                member?.isLocationEnable = "1"
                startLocationService()
            } else {
                stopLocationServiceAndUpdateProfile()
            }
        }

        binding.tvDistance.setOnClickListener {
            if (binding.switchLocation.isOn) {
                // val address= Utility.getAddress(this,member.userLat.toDouble(),member.userLng.toDouble())
                Utility.showDirections(this, member!!.userLat.toDouble(), member!!.userLng.toDouble(), "${member?.firstName}'s Location")
            } else {
                if (!member?.id.isNullOrEmpty()) {
                    Toast.makeText(this, "${member?.firstName}" + getString(R.string.locationOff), Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.llViewFamily.setOnClickListener {
            goToFamilyDetailActivity()
        }

        binding.imgBack.setOnClickListener {
            finish()
            hideKeyboard(this)
            fade(this)
        }

        binding.imgProfile.setOnClickListener {
            isProfileImage = true
            pickFromGallery(this)
        }

        binding.tvSave.setOnClickListener {

            if (NetworkChecker.isNetworkConnected(this)) {
                val jsonObject = JSONObject()
                mainDetailsFragment.getSaveData(jsonObject)
                personalDetailsFragment.getSaveData(jsonObject)
                professionalDetailsFragment.getSaveData(jsonObject)
                matrimonyDetailsFragment.getSaveData(jsonObject)
                jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))

                if (member?.isLocationEnable == "1") {
                    jsonObject.put(getString(R.string.is_location_enable), "1")
                } else {
                    jsonObject.put(getString(R.string.is_location_enable), "0")
                }
                jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))

                if (binding.tvSave.text.toString().toLowerCase().equals("save")) {

                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.updateprofile))
                            .setConfirmText(getString(R.string.update))
                            .setCancelText(getString(R.string.no))
                            .setCancelClickListener {
                                it.dismissWithAnimation()
                            }
                            .setContentText(getString(R.string.you_sure))
                            .setConfirmClickListener {
                                it.dismissWithAnimation()
                                startSweetProgress(this, getString(R.string.updatingProfile), getString(R.string.pleaseWait))
                                jsonObject.put(getString(R.string.id), member?.id)
                                // jsonObject.put(getString(R.string.status), "2")
                                val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                                profileDetailViewModel.updateProfile(profile, true)
                            }
                            .show()

                } else {
                    if (jsonObject.getString(getString(R.string.first_name)).isNullOrEmpty()) {
                        mainDetailsFragment.binding.fname.error = getString(R.string.EnterFirstName)
                        return@setOnClickListener
                    } else if (jsonObject.getString(getString(R.string.sub_cast_id)).isNullOrEmpty() || jsonObject.getString(getString(R.string.sub_cast_id)) == "0") {
                        displaySnackBarWithBottomMargin(ll_parent, getString(R.string.selectYourLastName))
                        return@setOnClickListener
                    } else if (jsonObject.getString(getString(R.string.gender)).isNullOrEmpty()) {
                        displaySnackBarWithBottomMargin(ll_parent, getString(R.string.SelectYourGender))
                        return@setOnClickListener
                    } else if (jsonObject.getString(getString(R.string.relation_id)).isNullOrEmpty() || jsonObject.getString(getString(R.string.relation_id)) == "0") {
                        displaySnackBarWithBottomMargin(ll_parent, getString(R.string.SelectRelation))
                        return@setOnClickListener
                    }

                    startSweetProgress(this, "Adding ${jsonObject.get(getString(R.string.first_name))}'s Profie", "Please wait...")
                    val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                    profileDetailViewModel.updateProfile(profile, false)
                }
                Log.d(ProfileDetailActivity::class.java.simpleName, "jsonObject: " + jsonObject.toString())
                hideSweetProgress()
            } else {
                setNoInternetLayout()
            }

        }

    }

    private fun setNoInternetLayout() {
        setContentView(R.layout.no_internet_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.app_name)
        val anim = AlphaAnimation(0f, 1f)
        anim.duration = 6000
        anim.repeatMode = AlphaAnimation.RESTART
        anim.repeatCount = Animation.INFINITE
        val imageView = findViewById<AppCompatImageView>(R.id.no_internet_image)
        imageView.animation = anim
        val retryButton = findViewById<AppCompatButton>(R.id.retry_button)
        retryButton.setOnClickListener { v: View? -> onBackPressed() }
    }


    private fun goToFamilyDetailActivity() {
        val intent = Intent(this, FamilyDetailActivity::class.java)
        if (member?.headId == "0") {
            intent.putExtra(getString(R.string.id), member?.id)
        } else {
            intent.putExtra(getString(R.string.id), member?.headId)
        }
        startActivity(intent)
        fade(this)
    }

    private fun startLocationService() {
        member?.isLocationEnable = "1"
        try {
            if (easyWayLocation.hasLocationEnabled()) {
                if (checkFineLocationPermission(this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RestartServiceBroadcastReceiver.scheduleJob(applicationContext)
                    } else {
                        val bck = ProcessMainClass()
                        bck.launchService(applicationContext)
                    }
                    setDistance()
                } else {
                    if (checkFineLocationPermission(this)) {
                        easyWayLocation.startLocation() //calculateDistance()
                    } else {
                        requestFineLocationPermission(this)
                    }
                }
            } else {
                easyWayLocation = EasyWayLocation(this, request, true, this)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stopLocationServiceAndUpdateProfile() {
        isStopService = true
        member?.isLocationEnable = "0"
        binding.tvDistance.text = getString(R.string.Distance)
        stopService(ProcessMainClass.serviceIntent)
        val jsonObject = JSONObject()
        jsonObject.put(getString(R.string.is_location_enable), "0")
        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
        jsonObject.put(getString(R.string.id), member?.id)
        jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
        val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
        profileDetailViewModel.updateProfile(profile, true)
    }

    private fun setMemberValues() {

        binding.txtTitle.text = "${member?.firstName}" + getString(R.string.Profile)

        val memberId = Guru.getString(getString(R.string.member_id), "")
        binding.switchLocation.isEnabled = memberId.equals(member?.id)
        if (member?.id == memberId) {
            binding.tvSave.visibility = View.VISIBLE
            binding.tvSave.text = getString(R.string.save)
            binding.imgProfile.isEnabled = true
        } else if (member?.id.isNullOrEmpty()) {
            binding.tvSave.text = getString(R.string.add)
            binding.imgProfile.isEnabled = true
            binding.txtTitle.text = getString(R.string.newProfile)
        } else {
            binding.imgProfile.isEnabled = false
            binding.tvSave.visibility = View.GONE
        }

        if (!member?.isLocationEnable.isNullOrEmpty() && member?.isLocationEnable.equals("1")) {
            binding.switchLocation.isOn = true
        } else {
            binding.tvDistance.text = getString(R.string.Distance)
            binding.switchLocation.isOn = false
        }

        if (!member?.profilePic.isNullOrEmpty()) {
            try {
                val str = resources.getString(R.string.base_url_thumb) + member?.profilePic
                Log.d(TAG, "path: $str")
                Glide.with(mApplication).load(str).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(binding.imgProfile)
            } catch (e: Exception) {
                e.message
            }
        }

        val bundle = Bundle()
        bundle.putSerializable(getString(R.string.member), member)
        listFragments.get(0).arguments = bundle
        listFragments.get(1).arguments = bundle
        listFragments.get(2).arguments = bundle
        listFragments.get(3).arguments = bundle
        binding.viewpager.offscreenPageLimit = 4
        binding.viewpager.adapter = MyPagerAdapter(listFragments, supportFragmentManager)
        hideKeyboard(this)
    }

    override fun getScanResult(response: SmartFilterResponse) {
        if (response.success) {
            member = response.members[0]
            val percentage = Utility.calculatePercentage(member)
            setPercentage(percentage)
            setMemberValues()
        }
    }

    override fun getUpdateOrAddResult(response: UpdateProfileResponse) {
        hideSweetProgress()
        if (response.message.toString().toLowerCase().contains("added")) {
            member?.headId = response.member.headId
            goToFamilyDetailActivity()
        } else if (response.message.toString().toLowerCase().contains("updated")) {
            val member = response.member

            val memberString = Guru.getString(getString(R.string.loginMember), "")
            val loginMember = Gson().fromJson(memberString, Member::class.java)

            if (loginMember.id == member.id) {
                Guru.putString(getString(R.string.loginMember), Gson().toJson(member))
            }

            var str = ""
            if (!isStopService) {
                val percentage = Utility.calculatePercentage(member)
                setPercentage(percentage)
                str = getString(R.string.profileUpdate)
            } else {
                str = getString(R.string.locationUpdate)
            }

            startSweetDialog(this, SweetAlertDialog.SUCCESS_TYPE, getString(R.string.Success), str)

            isStopService = false
        } else {
            val member = response.member
            if (!member.mobile.isNullOrEmpty()) {
                Toast.makeText(this, response.member.mobile, Toast.LENGTH_LONG).show()
            } else if (!member.emailAddress.isNullOrEmpty()) {
                Toast.makeText(this, response.member.emailAddress, Toast.LENGTH_LONG).show()
            } else if (!member.memberCode.isNullOrEmpty()) {
                Toast.makeText(this, response.member.memberCode, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override suspend fun getFailure(message: String) {
        hideSweetProgress()
        binding.viewpager.snackbar(getString(R.string.went_wrong), Snackbar.LENGTH_LONG)
        Log.d(ProfileDetailActivity::class.java.simpleName, "getFailure: " + message)
    }

    override fun getResult(jsonObject: JsonObject) {
        hideSweetProgress()
        member?.profilePic = jsonObject.get("profile").asString
        Guru.putString(getString(R.string.loginMember), Gson().toJson(member))
        displaySnackBarWithBottomMargin(binding.llParent, getString(R.string.profileUpdate))
    }

    override suspend fun onFailure(message: String) {
        hideSweetProgress()
        binding.viewpager.snackbar(getString(R.string.went_wrong), Snackbar.LENGTH_LONG)
        Log.d(ProfileDetailActivity::class.java.simpleName, "getFailure: " + message)
    }


    @SuppressLint("SetTextI18n")
    private fun setDistance() {
        if (member?.isLocationEnable == "1") {
            if (cur_lat.value != null && cur_lng.value != null && !member?.userLat.isNullOrEmpty() && !member?.userLng.isNullOrEmpty()) {
                val dist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member!!.userLat.toDouble(), member!!.userLng.toDouble()) / 1000
                binding.tvDistance.text = "km"
            } else {
                binding.tvDistance.text = getString(R.string.Distance)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkFineLocationPermission(this)) {
            easyWayLocation.startLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            easyWayLocation.endUpdates()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PICK_GALLERY_REQUEST) {
            pickFromGallery(this)
        } else if (requestCode == FINE_LOCATION_REQUEST) {
            easyWayLocation.startLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_REQUEST) {
                val selectedUri = data?.data

                Log.e("selectedUri", "" + selectedUri)
                if (selectedUri != null) {
                    startCrop(selectedUri, this)
                } else {
                    Toast.makeText(this@ProfileDetailActivity, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {

                if (isProfileImage) {
                    isProfileImage = false
                    data?.let {
                        val resultUri = UCrop.getOutput(it)
                        com.krs.community.utils.logger.debug("resultUri: $resultUri")
                        if (resultUri != null) {
                            try {
                                Glide.with(mApplication).load(resultUri).thumbnail(0.5f).into(binding.imgProfile)

                                Log.e("resultUri---", "" + resultUri)
                                val uploadImage = File(resultUri.path.toString())

                                Log.e("uploadImage---", "" + uploadImage)

                                startSweetProgress(this, "Image", getString(R.string.loading))
                                profileDetailViewModel.uploadImage(uploadImage, member?.id.toString(), getString(R.string.profile))
                            } catch (e: Exception) {
                                e.message
                            }
                        } else {
                            binding.llParent.snackbar("Requested crop image not found!", Snackbar.LENGTH_LONG)
                        }
                    }

                } else {
                    professionalDetailsFragment.onActivityResult(requestCode, resultCode, data)
                }


            } else if (requestCode == EasyWayLocation.LOCATION_SETTING_REQUEST_CODE) {
                easyWayLocation.onActivityResult(resultCode)
            }
        }


        if (resultCode == UCrop.RESULT_ERROR) {
            data?.let { handleCropError(it, this) }
        }
    }


    class MyPagerAdapter(val listFragments: List<Fragment>, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return listFragments[position]
        }

        override fun getCount(): Int {
            return listFragments.size
        }
    }

    override fun onCropFinish(result: UCropFragment.UCropResult) {
        when (result.mResultCode) {
            RESULT_OK -> handleCropResult(result.mResultData, this, binding.imgProfile)
            UCrop.RESULT_ERROR -> handleCropError(result.mResultData, this)
        }
    }

    override fun loadingProgress(showLoader: Boolean) {
        //mShowLoader = showLoader
    }


    override fun locationCancelled() {
        ll_parent.snackbar(getString(R.string.LocationOff), Snackbar.LENGTH_SHORT)
    }

    override fun locationOn() {
        ll_parent.snackbar(getString(R.string.LocationOn), Snackbar.LENGTH_SHORT)
    }

    override fun currentLocation(location: Location) {
        setDistance()
        cur_lat.postValue(location.latitude)
        cur_lng.postValue(location.longitude)
        getLocationDetail.getAddress(location.latitude, location.longitude, getString(R.string.map_api_key))
    }

    override fun locationData(locationData: LocationData) {
        cur_addr.postValue(locationData.full_address)
    }

    companion object {
        lateinit var binding: ActivityProfileDetailBinding
        lateinit var getLocationDetail: GetLocationDetail
        val TAG = ProfileDetailActivity::class.java.simpleName
        var cur_lat = MutableLiveData<Double>()
        var cur_lng = MutableLiveData<Double>()
        var cur_addr = MutableLiveData<String>()

        @SuppressLint("NewApi")
        fun setPercentage(percentage: Int) {
            binding.progressView.setAnimate(true)
            binding.progressView.setAnimateDuration(5000)
            binding.progressView.setProgress(percentage, true)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.10f
            }, 500)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.20f
            }, 1000)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.30f
            }, 1500)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.40f
            }, 2000)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.50f
            }, 2500)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.60f
            }, 3000)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.70f
            }, 3500)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.80f
            }, 4000)

            Handler().postDelayed({
                binding.imgProfile.alpha = 0.90f
            }, 4500)

            Handler().postDelayed({
                binding.imgProfile.alpha = 1.00f
                binding.tvPercent.text = "${percentage}%"
            }, 5000)
        }
    }


}