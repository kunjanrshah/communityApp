package com.krs.community.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
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
import com.krs.community.bkservice.ProcessMainClass
import com.krs.community.bkservice.restarter.RestartServiceBroadcastReceiver
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
import kotlinx.android.synthetic.main.activity_profile_detail.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class ProfileDetailActivity : BaseActivity(), KodeinAware, EditMemberListener, UCropFragmentCallback, Listener, LocationData.AddressCallBack {


    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance()
    private val listFragments = mutableListOf<Fragment>()
    private lateinit var head_id: String
    private lateinit var id: String
    private var isProfileImage = false
    private lateinit var member: Member
    override val kodein by kodein()
    private lateinit var logger: Logger
    private lateinit var professionalDetailsFragment: ProfessionalDetailsFragment
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var getLocationDetail: GetLocationDetail

    companion object {
        lateinit var binding: ActivityProfileDetailBinding
        val TAG = ProfileDetailActivity::class.java.simpleName
        var cur_lat = MutableLiveData<Double>()
        var cur_lng = MutableLiveData<Double>()
        var cur_addr = MutableLiveData<String>()

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ProfileDetailActivity, R.layout.activity_profile_detail)
        logger = Logger(TAG)
        profileDetailViewModel = ViewModelProviders.of(this, factory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener = this
        member = intent.getSerializableExtra(getString(R.string.member)) as Member
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.mdtp_white, false)
        }

        getLocationDetail = GetLocationDetail(this, this)
        val request = LocationRequest()
        request.interval = Utility.INTERVAL
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        easyWayLocation = EasyWayLocation(this, request, false, this)
        cur_lat.observeForever {
            setDistance()
        }

        cur_lng.observeForever {
            setDistance()
        }

        val mainDetailsFragment = MainDetailsFragment()
        val personalDetailsFragment = PersonalDetailsFragment()
        professionalDetailsFragment = ProfessionalDetailsFragment()
        val matrimonyDetailsFragment = MatrimonyDetailsFragment()

        listFragments.add(mainDetailsFragment)
        listFragments.add(personalDetailsFragment)
        listFragments.add(professionalDetailsFragment)
        listFragments.add(matrimonyDetailsFragment)


        val percentage = Utility.calculatePercentage(member)
        setPercentage(percentage)
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

            val intent = Intent(this, FamilyDetailActivity::class.java)
            if (head_id.equals("0")) {
                intent.putExtra("id", id)
            } else {
                intent.putExtra("id", head_id)
            }
            startActivity(intent)
            finish()
            Utility.fade(this)
        }

        binding.imgBack.setOnClickListener {

            finish()
            Utility.fade(this)
        }

        binding.tvSave.setOnClickListener {

            val jsonObject = JSONObject()
            mainDetailsFragment.getSaveData(jsonObject)
            personalDetailsFragment.getSaveData(jsonObject)
            professionalDetailsFragment.getSaveData(jsonObject)
            matrimonyDetailsFragment.getSaveData(jsonObject)
            jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
            jsonObject.put(getString(R.string.id), member.id)
            if(member.isLocationEnable=="1"){
                jsonObject.put(getString(R.string.is_location_enable), "1")
            }else{
                jsonObject.put(getString(R.string.is_location_enable), "0")
            }
            jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
            val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
            if (binding.tvSave.text.toString().toLowerCase().equals("save")) {
                Utility.startSweetProgress(this, "Updating your profie", "Please wait...")
                profileDetailViewModel.updateProfile(profile, true)
            } else {
                // jsonObject.put(getString(R.string.updated_dt),Utility.DatetoString(Date(),Utility.yyyy_MM_dd))
                Utility.startSweetProgress(this, "Adding ${jsonObject.get(getString(R.string.first_name))}'s profie", "Please wait...")
                profileDetailViewModel.updateProfile(profile, false)
            }
            Log.d(ProfileDetailActivity::class.java.simpleName, "jsonObject: " + jsonObject.toString())
        }

        binding.imgProfile.setOnClickListener {
            isProfileImage = true
            pickFromGallery(this)
        }

        Utility.hideKeyboard(this)


        if (Utility.finePermissionIsGranted(this)) {
            easyWayLocation.startLocation() //calculateDistance()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }

        if (member.isLocationEnable == "0") {
            binding.userLocation.isOn = false
            binding.tvDistance.text = "User"
        } else {
            binding.userLocation.performClick()
            binding.userLocation.isOn = true
        }

        binding.userLocation.setOnClickListener {
            if (!binding.userLocation.isOn) {
                member.isLocationEnable="1"
                if (Utility.finePermissionIsGranted(this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RestartServiceBroadcastReceiver.scheduleJob(applicationContext)
                    } else {
                        val bck = ProcessMainClass()
                        bck.launchService(applicationContext)
                    }
                    setDistance()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
                }
            } else {
                member.isLocationEnable="0"
                binding.tvDistance.text = "User"
                stopService(ProcessMainClass.serviceIntent)
                val jsonObject = JSONObject()
                jsonObject.put(getString(R.string.is_location_enable), "0")
                jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                jsonObject.put(getString(R.string.id), member.id)
                jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                profileDetailViewModel.updateProfile(profile, true)
            }
        }
    }

    private fun setDistance() {
        if (member.isLocationEnable == "1") {
            if (cur_lat.value != null && cur_lng.value != null && !member.userLat.isNullOrEmpty() && !member.userLng.isNullOrEmpty()) {
                val dist = EasyWayLocation.calculateDistance(cur_lat.value!!.toDouble(), cur_lng.value!!.toDouble(), member.userLat.toDouble(), member.userLng.toDouble()) / 1000
                binding.tvDistance.text = String.format("%.2f KM", dist)
            } else {
                binding.tvDistance.text = "User"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        easyWayLocation.startLocation()
    }

    override fun onPause() {
        super.onPause()
        easyWayLocation.endUpdates()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_REQUEST) {
                val selectedUri = data?.data
                if (selectedUri != null) {
                    startCrop(selectedUri, this)
                } else {
                    Toast.makeText(this@ProfileDetailActivity, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                if (isProfileImage) {
                    isProfileImage = false
                    data?.let { handleCropResult(it, this, binding.imgProfile) }
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


    private fun setMemberValues() {

        binding.txtTitle.text = "${member.firstName}'s Profile"
        val userId = Guru.getString(getString(R.string.user_id), "")
        if (member.id.equals(userId) || member.headId.equals(userId)) {
            binding.tvSave.visibility = View.VISIBLE
            binding.tvSave.text = "Save"
        } else if (member.id.isNullOrEmpty()) {
            binding.tvSave.text = "Add"
            binding.txtTitle.text = "New Profile"
        } else {
            binding.tvSave.visibility = View.GONE
        }


        binding.userLocation.isOn = !member.isLocationEnable.isNullOrEmpty() && member.isLocationEnable.equals("1")
        if (!member.profilePic.isNullOrEmpty()) {
            try {
                Glide.with(AppController.mApplication).load(member.profilePic).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(binding.imgProfile)
            } catch (e: Exception) {
                e.message
            }
        }

        head_id = member.headId
        id = member.id
        val bundle = Bundle()
        bundle.putSerializable(getString(R.string.member), member)
        listFragments.get(0).arguments = bundle
        listFragments.get(1).arguments = bundle
        listFragments.get(2).arguments = bundle
        listFragments.get(3).arguments = bundle
        binding.viewpager.offscreenPageLimit = 4
        binding.viewpager.adapter = MyPagerAdapter(listFragments, supportFragmentManager)

    }

    override fun getMessage(response: UpdateProfileResponse) {
        Utility.hideSweetProgress()
        if (response.message.toString().toLowerCase().contains("added")) {
            binding.llViewFamily.performClick()
        } else {
            val member = response.member
            val percentage = Utility.calculatePercentage(member)
            setPercentage(percentage)
            Utility.displaySnackBarWithBottomMargin(binding.llParent, "Profile updated!")
            if (member.headId == "0") {
                Guru.putString(getString(R.string.loginUser), Gson().toJson(member))
                Guru.putString(getString(R.string.user_mobile), member.mobile)
            }
        }
    }


    override fun getFailure(message: String) {
        Utility.hideSweetProgress()
        binding.viewpager.snackbar("Something went wrong!", Snackbar.LENGTH_LONG)
        Log.d(ProfileDetailActivity::class.java.simpleName, "getFailure: " + message)
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
            REQUEST_STORAGE_READ_ACCESS_PERMISSION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery(this)
                } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                    displayNeverAskAgainDialog(this)
                } else {
                    promptReadPermission(this)
                }

            /*REQUEST_LOCATION_PERMISSION ->
                easyWayLocation.startLocation()*/
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
    }

    override fun locationOn() {
        ll_parent.snackbar("Location On", Snackbar.LENGTH_SHORT)
    }

    override fun currentLocation(location: Location) {

        cur_lat.postValue(location.latitude)
        cur_lng.postValue(location.longitude)

        getLocationDetail.getAddress(location.latitude, location.longitude, "xyz")
    }

    override fun locationData(locationData: LocationData) {
        cur_addr.postValue(locationData.full_address)
    }
}