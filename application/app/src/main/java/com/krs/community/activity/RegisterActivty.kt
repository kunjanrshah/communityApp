package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.databinding.ActivityRegisterBinding
import com.krs.community.entities.MasterCounts
import com.krs.community.listeners.IRegisterListener
import com.krs.community.listeners.ImageUploadListener
import com.krs.community.listeners.UpdateListener
import com.krs.community.model.RegisterModel
import com.krs.community.responses.MasterUpdateResponse
import com.krs.community.responses.UserStatusResponse
import com.krs.community.utils.*
import com.krs.community.utils.Utility.isOnline
import com.krs.community.viewmodel.DashboardViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RegisterViewModel
import com.krs.community.viewmodelfactory.DashboardViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RegisterViewModelFactory
import com.wessam.library.NetworkChecker
import com.yalantis.ucrop.UCrop.*
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File

class RegisterActivty : AppCompatActivity(), UCropFragmentCallback, IRegisterListener, KodeinAware, ImageUploadListener, UpdateListener {

    private var mShowLoader: Boolean = false
    private val PICK_GALLERY_REQUEST = 1
    private lateinit var logger: Logger
    lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var dashboardViewModel: DashboardViewModel

    private var resultUri: Uri? = null
    private var mNetworkReceiver: BroadcastReceiver? = null
    private var isLogin: Boolean = true

    companion object {
        private val TAG = RegisterActivty::class.java.simpleName
    }

    override val kodein by kodein()
    private val registerViewModelFactory: RegisterViewModelFactory by instance()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance()
    private val factory: DashboardViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isLogin = intent.getBooleanExtra(getString(R.string.is_logged_in), true)
        mNetworkReceiver = NetworkChangeReceiver()

        registerNetworkBroadcastForNougat()

        if (NetworkChecker.isNetworkConnected(this)) {
            setScreenLayout()
        } else {
            setNoInternetLayout()
        }

        val mApp = applicationContext as AppController
        mApp.FirebaseAnalytics(this@RegisterActivty, RegisterActivty.javaClass.simpleName)
        mApp.FacebookAnalytics(this@RegisterActivty, RegisterActivty.javaClass.simpleName)

    }

    override fun onBackPressed() {
        super.onBackPressed()
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
        retryButton.setOnClickListener { v: View? ->
            if (NetworkChecker.isNetworkConnected(this)) {
                setScreenLayout()
            }
        }
    }

    @SuppressLint("NewApi", "ClickableViewAccessibility")
    fun setScreenLayout() {
        if (NetworkChecker.isNetworkConnected(this)) {
            logger = Logger(TAG)

            registerViewModel = ViewModelProvider(this, registerViewModelFactory).get(RegisterViewModel::class.java)
            registerViewModel.iRegisterListener = this

            profileDetailViewModel = ViewModelProvider(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)
            profileDetailViewModel.mImageUploadListener = this

            dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
            dashboardViewModel.listener = this

            binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
            binding.lifecycleOwner = this
            binding.registerviewmodel = registerViewModel

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(this, R.color.colorBG, false)
            }

            if (isLogin) {
                val str = resources.getString(R.string.already_have_a_account_sign_in) + "<b>" + " " + getString(R.string.login) + "</b>"
                binding.txtAlready.text = Html.fromHtml(str)
                binding.txtAlready.visibility = View.VISIBLE
            } else {
                binding.txtAlready.visibility = View.INVISIBLE
            }

            binding.btnRegister.setOnClickListener {
                Utility.startSweetProgress(this, getString(R.string.RegisterFamily), resources.getString(R.string.loading))
                registerViewModel.getUserRegistration()
            }
            binding.txtAlready.setOnClickListener { registerViewModel.onTextAlreadyClicked(this) }
            binding.txtHowRegister.setOnClickListener { registerViewModel.onHowRegisterClicked(this) }
            binding.imgCancel.visibility = View.GONE
            binding.imgCancel.setOnClickListener {
                binding.imgProfile.setImageResource(R.drawable.man_reg)
                binding.imgCancel.visibility = View.GONE
            }

            if (isOnline(this)) {
                dashboardViewModel.getMasterUpdate()
            }

            binding. spinnerCountries.setOnItemClickListener { pos->
                registerViewModel.countryCode =CountryData.countryAreaCodes[pos]
            }

            binding.spinnerStates.setOnItemClickListener {
                Coroutines.main {
                    val stateId=profileDetailViewModel.getstateIdByName(binding.spinnerStates.text.toString())
                    val lstCity = profileDetailViewModel.getCityNamebyState(stateId)
                    registerViewModel.stateId =stateId
                    spinnerCities.clear()
                    registerViewModel.cityId = null
                    spinnerCities.setItems(lstCity.toTypedArray())
                    spinnerCities.setExpandTint(R.color.black)
                }
            }

            binding.spinnerSub.setOnItemClickListener {
                Coroutines.main {
                  val sub_id=  profileDetailViewModel.getSubCommIdByName(binding.spinnerSub.text.toString())
                    registerViewModel.subCommId = sub_id
                    profileDetailViewModel.getLocalCommunity(sub_id).observeForever {
                        spinnerLocal.clear()
                        registerViewModel.localCommId = null
                        spinnerLocal.setItems(it.toTypedArray())
                        spinnerLocal.setExpandTint(R.color.black)
                    }
                }
            }

            binding.spinnerLname.setOnItemClickListener {
                Coroutines.io {
                    registerViewModel.lastnameId = profileDetailViewModel.getIdByLastName(binding.spinnerLname.text.toString())
                    Log.d(TAG, "lname id: " + registerViewModel.lastnameId)
                }
            }

            binding.spinnerCities.setOnItemClickListener { position ->
                Coroutines.io {
                    registerViewModel.cityId = profileDetailViewModel.getCityIdByName(binding.spinnerCities.text.toString())
                    Log.d(TAG, "cityId: " + registerViewModel.cityId)
                }
            }

            binding.spinnerLocal.setOnItemClickListener { position ->
                Coroutines.io {
                    registerViewModel.localCommId = profileDetailViewModel.getLocalCommunityId(binding.spinnerLocal.text.toString())
                    Log.d(TAG, "localCommId: " + registerViewModel.localCommId)
                }
            }

            val lstGender = arrayOf(getString(R.string.male), getString(R.string.female))
            binding.spinnerGender.setItems(lstGender)
            binding.spinnerGender.setExpandTint(R.color.black)

            binding.spinnerGender.setOnItemClickListener { position ->

                registerViewModel.gender = lstGender[position]
                Log.e("spinnerGender--", "" + lstGender[position]);

            }
            binding.imgProfile.setOnClickListener { v ->
                pickFromGallery(this)
            }

            Coroutines.main {
                profileDetailViewModel.lstLastName.await().observe(this, Observer {
                    spinnerLname.setItems(it.toTypedArray())
                    spinnerLname.setExpandTint(R.color.black)
                })

                profileDetailViewModel.lstSubCommName.await().observe(this, Observer {
                    spinnerSub.setItems(it.toTypedArray())
                    spinnerSub.setExpandTint(R.color.black)
                })

                profileDetailViewModel.lstStateName.await().observe(this, Observer {
                    spinnerStates.setItems(it.toTypedArray())
                    spinnerStates.setExpandTint(R.color.black)
                })

            }
        }
    }
    inner class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (!NetworkChecker.isNetworkConnected(context)) {
                    setNoInternetLayout()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver,  IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    private fun unregisterNetworkBroadcastForNougat() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                unregisterReceiver(mNetworkReceiver)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                unregisterReceiver(mNetworkReceiver)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PICK_GALLERY_REQUEST){
            pickFromGallery(this)
        }
    }

    private fun ScrollView.scrollToBottom() {
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY+ height)
        smoothScrollBy(0, delta)
    }

    override fun getRegisterFailure(message: String?, filed: Int) {
        Utility.hideSweetProgress()
        if(message.equals(getString(R.string.fname))){
            root_layout.snackbar(getString(R.string.enter_firstname), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.lastnameId))){
            root_layout.snackbar(getString(R.string.enter_lastname), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.emailstr))){
            root_layout.snackbar(getString(R.string.enter_email), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.genderstr))){
            root_layout.snackbar(getString(R.string.enter_gender), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.mobilestr))){
            root_layout.snackbar(getString(R.string.enter_mobile), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.pass))){
            root_layout.snackbar(getString(R.string.enter_password), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.passsecond))){
            root_layout.snackbar(getString(R.string.make_strong_pass), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.cpass))){
            root_layout.snackbar(getString(R.string.confirm_password), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.cpasssecond))){
            root_layout.snackbar(getString(R.string.make_strong_pass), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.passequals))){
            root_layout.snackbar(getString(R.string.password_mismatch), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.addressstr))){
            root_layout.snackbar(getString(R.string.enter_home_address), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.stateis))){
            root_layout.snackbar(getString(R.string.select_state), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.cityid))){
            root_layout.snackbar(getString(R.string.select_city), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.subcommid))){
            root_layout.snackbar(getString(R.string.select_sub_comm), Snackbar.LENGTH_LONG)
            return
        }
        if(message.equals(getString(R.string.localcommid))){
            root_layout.snackbar(getString(R.string.select_local), Snackbar.LENGTH_LONG)
            return
        }

      //  root_layout.snackbar(getString(R.string.lastname), Snackbar.LENGTH_LONG)
        when(filed){
            1 -> binding.edtHeadName.requestFocus()
            2 -> binding.spinnerLname.requestFocus()
            3 -> binding.edtEmailId.requestFocus()
            4 -> binding.spinnerGender.requestFocus()
            5 -> binding.edtMobile.requestFocus()
            6 -> binding.edtPassword.requestFocus()
            7 -> binding.edtCpassword.requestFocus()
            8 -> binding.edtAddress.requestFocus()
            9 -> binding.spinnerStates.requestFocus()
            10 -> binding.spinnerCities.requestFocus()
            11 -> binding.spinnerSub.requestFocus()
            12 -> binding.spinnerLocal.requestFocus()
            else -> ""
        }
    }

    override fun getRegisterSuccess(data: RegisterModel) {
        Utility.hideSweetProgress()
        Log.d(TAG, "onRegisterButtonClick")
        if(resultUri!=null){
            try {
                val uploadImage = File(resultUri?.path.toString())
                Utility.startSweetProgress(this, getString(R.string.RegisterFamilyPhoto), getString(R.string.loading))
                profileDetailViewModel.uploadImage(uploadImage, data.userId.toString(),getString(R.string.profile))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
            successResponse(data.message)
        }
    }

    private fun successResponse(message: String) {
        Utility.startSweetDialog(this, SweetAlertDialog.SUCCESS_TYPE, getString(R.string.Register), message)
        binding.edtHeadName.text.clear()
        binding.edtAddress.text.clear()
        binding.edtCpassword.text.clear()
        binding.edtEmailId.text.clear()
        binding.edtMobile.text.clear()
        binding.edtPassword.text.clear()
        binding.imgProfile.setImageResource(R.drawable.man_reg)

        binding.spinnerLname.setText("Select LastName")
        binding.spinnerGender.setText("Select Gender")
        binding.spinnerStates.setText("Select State")
        binding.spinnerCities.setText("Select City")
        binding.spinnerSub.setText("Select Sub Community")
        binding.spinnerLocal.setText("Select Local Community")
    }

    override fun getResult(profile: JsonObject) {
        Utility.hideSweetProgress()
        successResponse(getString(R.string.RequestAdmin))
    }

    override suspend fun onFailure(message: String) {
        Utility.hideSweetProgress()
        root_layout.snackbar(message, Snackbar.LENGTH_INDEFINITE)
    }

    override suspend fun getFailure(message: String) {

        withContext(Main) {
            if (Utility.dialog != null && Utility.dialog.isShowing) {
                Utility.dialog.dismissWithAnimation()
            }
            Utility.hideSweetProgress()
            root_layout.snackbar(message, Snackbar.LENGTH_INDEFINITE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        registerViewModel.cancelAllJobs()
        unregisterNetworkBroadcastForNougat()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_REQUEST) {
                val selectedUri = data!!.data
                if (selectedUri != null) {
                    startCrop(selectedUri, this)
                } else {
                    Toast.makeText(this@RegisterActivty, getString(R.string.CannotImage), Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == REQUEST_CROP) {
                resultUri = getOutput(data!!)
                try {
                    Glide.with(AppController.mApplication).load(resultUri).thumbnail(0.5f).into(binding.imgProfile)
                } catch (e: Exception) {
                    e.message
                }
            }
        }
        if (resultCode == RESULT_ERROR) {
            handleCropError(data!!, this)
        }
    }

   override fun onCropFinish(result: UCropFragment.UCropResult) {
        when (result.mResultCode) {
            RESULT_OK -> handleCropResult(result.mResultData,this,binding.imgProfile)
            RESULT_ERROR -> handleCropError(result.mResultData,this)
        }
    }

    override fun loadingProgress(showLoader: Boolean) {
        mShowLoader = showLoader
    }

    override fun getVersionResponse(response: UserStatusResponse) {
        showVersionDialog(this)
    }


    override fun getMastersResponse(response: MasterUpdateResponse) {
        if (response.success) {
            Coroutines.io {

                val counts = MasterCounts()
                counts.business_categories = -1
                counts.business_sub_categories = -1
                counts.committees = -1
                counts.current_activity = -1
                counts.designations = -1
                counts.districts = -1
                counts.educations = -1
                counts.gotra = -1
                counts.native_place = -1
                counts.occupation = -1
                counts.relations = -1
                counts.states = Integer.parseInt(response.countList.states)
                counts.cities = Integer.parseInt(response.countList.cities)
                counts.sub_casts = Integer.parseInt(response.countList.subCasts)
                counts.sub_community = Integer.parseInt(response.countList.subCommunity)
                counts.local_community = Integer.parseInt(response.countList.localCommunity)

                val dbCount = dashboardViewModel.getMasterCounts()
                if (dbCount == null) {
                    dashboardViewModel.insertMasterCounts(counts)
                    dashboardViewModel.fetchState(counts.states)
                    dashboardViewModel.fetchCity(counts.cities)
                    dashboardViewModel.fetchSubCommunities(counts.sub_community)
                    dashboardViewModel.fetchLocalCommunities(counts.local_community)
                    dashboardViewModel.fetchLastName(counts.sub_casts)

                    Coroutines.main {
                        profileDetailViewModel.lstLastName.await().observe(this, Observer {
                            spinnerLname.setItems(it.toTypedArray())
                            spinnerLname.setExpandTint(R.color.black)
                        })

                        profileDetailViewModel.lstSubCommName.await().observe(this, Observer {
                            spinnerSub.setItems(it.toTypedArray())
                            spinnerSub.setExpandTint(R.color.black)
                        })

                        profileDetailViewModel.lstStateName.await().observe(this, Observer {
                            spinnerStates.setItems(it.toTypedArray())
                            spinnerStates.setExpandTint(R.color.black)
                        })

                    }

                } else {
                    if (dbCount.states != counts.states) {
                        dashboardViewModel.fetchState(counts.states)
                        Coroutines.main {
                            profileDetailViewModel.lstStateName.await().observe(this, Observer {
                                spinnerStates.setItems(it.toTypedArray())
                                spinnerStates.setExpandTint(R.color.black)
                            })
                        }
                    }
                    if (dbCount.cities != counts.cities) {
                        dashboardViewModel.fetchCity(counts.cities)
                    }
                    if (dbCount.sub_community != counts.sub_community) {
                        dashboardViewModel.fetchSubCommunities(counts.sub_community)
                        Coroutines.main {
                            profileDetailViewModel.lstSubCommName.await().observe(this, Observer {
                                spinnerSub.setItems(it.toTypedArray())
                                spinnerSub.setExpandTint(R.color.black)
                            })
                        }
                    }
                    if (dbCount.local_community != counts.local_community) {
                        dashboardViewModel.fetchLocalCommunities(counts.local_community)
                    }
                    if (dbCount.sub_casts != counts.sub_casts) {
                        dashboardViewModel.fetchLastName(counts.sub_casts)
                        Coroutines.main {
                            profileDetailViewModel.lstLastName.await().observe(this, Observer {
                                spinnerLname.setItems(it.toTypedArray())
                                spinnerLname.setExpandTint(R.color.black)
                            })
                        }
                    }
                }
            }
        }
    }
}

