package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.InputFilter
import android.util.Base64
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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.github.squti.guru.Guru
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.databinding.ActivityRegisterBinding
import com.krs.community.entities.MasterCounts
import com.krs.community.fragments.FamilyDetailActivity
import com.krs.community.listeners.IRegisterListener
import com.krs.community.listeners.ImageUploadListener
import com.krs.community.listeners.UpdateListener
import com.krs.community.model.RegisterModel
import com.krs.community.responses.MasterUpdateResponse
import com.krs.community.responses.UserStatusResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.DashboardViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RegisterViewModel
import com.krs.community.viewmodelfactory.DashboardViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RegisterViewModelFactory
import com.orhanobut.dialogplus.DialogPlus
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import com.yalantis.ucrop.UCrop.*
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import kotlinx.android.synthetic.main.activity_register.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivty : AppCompatActivity(), UCropFragmentCallback, IRegisterListener, KodeinAware, ImageUploadListener, UpdateListener, DatePickerDialog.OnDateSetListener {

    private var mShowLoader: Boolean = false
    private val PICK_GALLERY_REQUEST = 1
    private lateinit var logger: Logger
    lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    private var resultUri: Uri? = null
    private var isLogin: Boolean = true
    private var datepicker = SpinnerDatePickerDialogBuilder()

    companion object {
        private val TAG = RegisterActivty::class.java.simpleName
        var polictyDialog: DialogPlus? = null
    }

    override val kodein by kodein()
    private val registerViewModelFactory: RegisterViewModelFactory by instance<RegisterViewModelFactory>()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private val factory: DashboardViewModelFactory by instance<DashboardViewModelFactory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isLogin = intent.getBooleanExtra(getString(R.string.is_logged_in), true)

        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this@RegisterActivty, RegisterActivty.javaClass.simpleName)
        mApp.facebookAnalytics(this@RegisterActivty, RegisterActivty.javaClass.simpleName)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.e("newToken", token)
            Guru.putString(AppConstants.DEVICE_TOKEN, token)
        })

        AppController.mApplication.connectionLiveData.observeForever {
            it?.let {
                if (it) {
                    setScreenLayout()
                } else {
                    setNoInternetLayout()
                }
            }
        }
    }

    private fun setNoInternetLayout() {
        setContentView(R.layout.no_internet_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this as AppCompatActivity, R.color.colorPrimary))
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
            if (isNetworkConnected(this)) {
                setScreenLayout()
            }
        }
    }

    @SuppressLint("NewApi", "ClickableViewAccessibility")
    fun setScreenLayout() {
        if (isNetworkConnected(this)) {
            logger = Logger(TAG)

            registerViewModel = ViewModelProvider(this, registerViewModelFactory).get(RegisterViewModel::class.java)
            registerViewModel.iRegisterListener = this

            profileDetailViewModel = ViewModelProvider(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)
            profileDetailViewModel.mImageUploadListener = this

            dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
            dashboardViewModel.listener = this

            binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
            binding.registerviewmodel = registerViewModel
            binding.lifecycleOwner = this

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(this, R.color.colorBG, false)
            }

            if (BuildConfig.FLAVOR == "yadav") {
                binding.llHeader.visibility = View.VISIBLE
            } else {
                binding.imgHeader.visibility = View.VISIBLE
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
                registerViewModel.getUserRegistration(isLogin)
            }
            binding.txtAlready.setOnClickListener { registerViewModel.onTextAlreadyClicked(this) }
//            binding.txtHowRegister.setOnClickListener { registerViewModel.onHowRegisterClicked(this) }
            binding.imgCancel.visibility = View.GONE
            binding.imgCancel.setOnClickListener {
                binding.imgProfile.setImageResource(R.drawable.man_reg)
                resultUri = null
                registerViewModel.profilePic = null
                binding.imgCancel.visibility = View.GONE
            }

            if (isNetworkConnected(this)) {
                val jsonObject = JsonObject()
                dashboardViewModel.getMasterUpdate(jsonObject)
            }

            binding.spinnerCountries.setOnItemClickListener { pos ->
                registerViewModel.countryCode = CountryData.countryAreaCodes[pos]
            }

            binding.spinnerStates.setOnItemClickListener {
                Coroutines.main {
                    val stateId = profileDetailViewModel.getstateIdByName(binding.spinnerStates.text.toString())
                    val lstCity = profileDetailViewModel.getCityNamebyState(stateId)
                    registerViewModel.stateId = stateId
                    binding.spinnerCities.clear()
                    registerViewModel.cityId = null
                    binding.edtMobile.setText("")
                    if (binding.spinnerStates.text.toString().toLowerCase() == "foreign") {
                        binding.edtMobile.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15))
                    } else {
                        binding.edtMobile.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
                    }

                    if (lstCity.isNotEmpty()) {
                        val lst = ArrayList<String>()
                        lst.addAll(lstCity)
                        lst.remove(getString(R.string.other))
                        val lstcityName = ArrayList<String>()
                        lstcityName.add(getString(R.string.other))
                        lst.sort()
                        lstcityName.addAll(lst)
                        binding.spinnerCities.setItems(lstcityName.toTypedArray())
                        binding.spinnerCities.setExpandTint(R.color.black)
                    }
                }
            }

            binding.spinnerSub.setOnItemClickListener {
                Coroutines.io {
                    val subId = profileDetailViewModel.getSubCommIdByName(binding.spinnerSub.text.toString())
                    registerViewModel.subCommId = subId
                    Coroutines.main {
                        profileDetailViewModel.getLocalCommunity(subId).observeForever {
                            binding.spinnerLocal.clear()
                            registerViewModel.localCommId = null
                            binding.spinnerLocal.setItems(it.toTypedArray())
                            binding.spinnerLocal.setExpandTint(R.color.black)
                            if (BuildConfig.FLAVOR == "medk") {
                                try {
                                    if (!it.isNullOrEmpty()) {
                                        binding.spinnerLocal.select(0)
                                        Coroutines.io {
                                            registerViewModel.localCommId = profileDetailViewModel.getLocalCommunityId(binding.spinnerLocal.text.toString())
                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }

            binding.spinnerLname.setOnItemClickListener {
                Coroutines.io {
                    registerViewModel.lastnameId = profileDetailViewModel.getIdByLastName(binding.spinnerLname.text.toString())
                    Log.d(TAG, "lname id: " + registerViewModel.lastnameId)
                }
            }

            binding.spinnerNative.setOnItemClickListener {
                Coroutines.io {
                    registerViewModel.nativeId = profileDetailViewModel.getNativeIdByName(binding.spinnerNative.text.toString())
                    Log.d(TAG, "native id: " + registerViewModel.nativeId)
                }
            }


            binding.spinnerCities.setOnItemClickListener { position ->
                Coroutines.io {
                    registerViewModel.cityId = profileDetailViewModel.getCityIdByName(binding.spinnerCities.text.toString())
                    Log.d(TAG, "cityId: " + registerViewModel.cityId)
                }
            }

            binding.txtBdate.setOnClickListener {
                val mem_date = binding.txtBdate.text.toString().trim()
                setDatePicker(mem_date)
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
            }
            binding.imgProfile.setOnClickListener { v ->

                SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Register Photo")
                        .setContentText("Do you have good internet speed?")
                        .setConfirmText("Upload")
                        .setCancelText("Later")
                        .setCustomImage(R.drawable.ic_app)
                        .showCancelButton(true)
                        .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                            sweetAlertDialog.dismissWithAnimation()
                            pickFromGallery(this)
                        }
                        .show()

            }

            binding.fab.setOnClickListener {

                SweetAlertDialog(this, SweetAlertDialog.FORGOT_TYPE)
                        .setTitleText("Registration Problem?")
                        .setContentText("We have alternative for you, Please register with " + getString(R.string.dev_link))
                        .setConfirmText("Register on Website")
                        .setNeutralText("I have Suggestion")
                        .setCustomImage(R.drawable.ic_app)
                        .showCancelButton(false)
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismiss()
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dev_link) + "/site/register"))
                            startActivity(browserIntent)
                        }
                        .setNeutralClickListener {
                            it.dismiss()
                            val intent = Intent(this, ContactUsActivity::class.java)
                            startActivity(intent)
                        }

                        .show()
            }

            setDropDownList()
        }
    }

    private fun setDatePicker(mem_date: String) {
        val year: Int = Calendar.getInstance().get(Calendar.YEAR)
        val day: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val month: Int = Calendar.getInstance().get(Calendar.MONTH)

        val format = SimpleDateFormat(Utility.dd_MM_yyyy)
        var day1: Int = day
        var month1: Int = month
        var year1: Int = year

        if (mem_date.isNotBlank() && mem_date.isNotEmpty()) {
            try {
                val date: Date = format.parse(mem_date)
                val c = Calendar.getInstance()
                c.time = date
                day1 = c[Calendar.DAY_OF_MONTH]
                month1 = c[Calendar.MONTH]
                year1 = c[Calendar.YEAR]
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        datepicker.context(this)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(year1, month1, day1)
                .maxDate(year, month, day)
                .minDate(1900, 0, 1)
                .build().show()

    }

    private fun setDropDownList() {

        Coroutines.main {

            val lstMarital = resources.getStringArray(R.array.marital)
            binding.spMarital.setItems(lstMarital)
            binding.spMarital.setExpandTint(R.color.black)

            binding.spMarital.setOnItemClickListener {
                registerViewModel.maritalStatus = binding.spMarital.text.toString()
            }

            profileDetailViewModel.lstLastName.await().observe(this, Observer {
                if (it.isNotEmpty()) {
                    val lst = ArrayList<String>()
                    lst.addAll(it)
                    lst.remove(getString(R.string.other))
                    val lstLastName = ArrayList<String>()
                    lstLastName.add(getString(R.string.other))
                    lst.sort()
                    lstLastName.addAll(lst)
                    binding.spinnerLname.setItems(lstLastName.toTypedArray())
                    binding.spinnerLname.setExpandTint(R.color.black)
                }
            })

            profileDetailViewModel.lstSubCommName.await().observe(this, Observer {
                binding.spinnerSub.setItems(it.toTypedArray())
                binding.spinnerSub.setExpandTint(R.color.black)
                if (BuildConfig.FLAVOR == "medk") {
                    try {
                        if (!it.isNullOrEmpty()) {
                            binding.spinnerSub.select(0)
                            Coroutines.io {
                                registerViewModel.subCommId = profileDetailViewModel.getSubCommIdByName(binding.spinnerSub.text.toString())
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })

            profileDetailViewModel.lstStateName.await().observe(this, Observer {
                binding.spinnerStates.setItems(it.toTypedArray())
                binding.spinnerStates.setExpandTint(R.color.black)
            })

            profileDetailViewModel.lstNativeName.await().observe(this, Observer {
                binding.spinnerNative.setItems(it.toTypedArray())
                binding.spinnerNative.setExpandTint(R.color.black)
            })
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_GALLERY_REQUEST) {
            pickFromGallery(this)
        }
    }

    private fun ScrollView.scrollToBottom() {
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY + height)
        smoothScrollBy(0, delta)
    }

    override fun getRegisterFailure(message: String?, filed: Int) {
        Utility.hideSweetProgress()


        if (filed == 13) {
            root_layout.snackbar(getString(R.string.enter_father), Snackbar.LENGTH_LONG)
            return
        }

        if (filed == 14) {
            root_layout.snackbar(getString(R.string.enter_bdate), Snackbar.LENGTH_LONG)
            return
        }

        if (filed == 15) {
            root_layout.snackbar(getString(R.string.enter_native), Snackbar.LENGTH_LONG)
            return
        }

        if (filed == 16) {
            root_layout.snackbar(getString(R.string.enter_marital), Snackbar.LENGTH_LONG)
            return
        }

        if (message!!.contains(getString(R.string.fname), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.enter_firstname), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.lastnameId), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.enter_lastname), Snackbar.LENGTH_LONG)
            return
        }

//        if (filed == 3) {
//            binding.edtEmailId.requestFocus()
//            return
//        }
        if (message.contains(getString(R.string.emailstr), ignoreCase = true)) {
            if (filed == 0) {
                root_layout.snackbar(message, Snackbar.LENGTH_LONG)
            } else {
                root_layout.snackbar(getString(R.string.enter_email), Snackbar.LENGTH_LONG)
            }
            return
        }


        if (message.contains(getString(R.string.genderstr), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.enter_gender), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.mobilestr), ignoreCase = true)) {
            if (filed == 0) {
                root_layout.snackbar(message, Snackbar.LENGTH_LONG)
            } else {
                root_layout.snackbar(getString(R.string.enter_mobile), Snackbar.LENGTH_LONG)
            }

            return
        }
        if (message.equals(getString(R.string.pass), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.enter_password), Snackbar.LENGTH_LONG)
            return
        }
        if (message.equals(getString(R.string.passsecond), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.make_strong_pass), Snackbar.LENGTH_LONG)
            return
        }
        if (message.equals(getString(R.string.cpass), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.confirm_password), Snackbar.LENGTH_LONG)
            return
        }
        if (message.equals(getString(R.string.cpasssecond), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.make_strong_pass), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.passequals), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.password_mismatch), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.addressstr), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.enter_home_address), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.stateis), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.select_state), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.cityid), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.select_city), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.subcommid), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.select_sub_comm), Snackbar.LENGTH_LONG)
            return
        }
        if (message.contains(getString(R.string.localcommid), ignoreCase = true)) {
            root_layout.snackbar(getString(R.string.select_local), Snackbar.LENGTH_LONG)
            return
        }

        when (filed) {
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
            13 -> binding.edtFatherName.requestFocus()
            14 -> binding.txtBdate.requestFocus()
            15 -> binding.spinnerNative.requestFocus()
            else -> ""
        }
    }

    override fun getRegisterSuccess(data: RegisterModel) {
        Utility.hideSweetProgress()
        successResponse(data)
    }

    private fun successResponse(message: String) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_INDEFINITE).show()
    }

    private fun successResponse(data: RegisterModel) {
        clearAll()
        if (data.message.contains("create")) {
            goToFamilyDetailActivity(data)
        } else {
            root_layout.snackbar(getString(R.string.RequestAdmin), Snackbar.LENGTH_INDEFINITE)
        }
    }

    private fun clearAll() {
        binding.edtHeadName.text.clear()
        binding.edtAddress.text.clear()
        binding.edtCpassword.text.clear()
        binding.edtEmailId.text.clear()
        binding.edtMobile.text.clear()
        binding.edtPassword.text.clear()
        binding.txtBdate.text = ""
        binding.txtBdate.hint = "BirthDate"
        binding.edtFatherName.text.clear()
        registerViewModel.nativeId = null
        registerViewModel.lastnameId = null
        registerViewModel.stateId = null
        registerViewModel.cityId = null
        binding.imgProfile.setImageResource(R.drawable.man_reg)
        binding.spinnerLname.setText("Select LastName")
        binding.spinnerGender.setText("Select Gender")
        binding.spinnerStates.setText("Select State")
        binding.spinnerNative.setText("Select Native")
        binding.spinnerCities.setText("Select City")
        binding.spinnerSub.setText("Select Sub Community")
        binding.spinnerLocal.setText("Select Local Community")
    }

    private fun goToFamilyDetailActivity(data: RegisterModel) {
        val intent = Intent(this, FamilyDetailActivity::class.java)
        intent.putExtra(getString(R.string.id), data.userId.toString())
        intent.putExtra(getString(R.string.is_finish), true)
        startActivity(intent)
        //finish()
        //  Utility.fade(this)
    }

    override fun onUploadSuccess(jsonObject: JsonObject) {
        try {
            clearAll()
            Utility.hideSweetProgress()
            successResponse(getString(R.string.RequestAdmin))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun onUploadFail(message: String) {
        try {
            clearAll()
            Utility.hideSweetProgress()
            successResponse(getString(R.string.RequestAdmin))
            root_layout.snackbar("Photo not uploaded because of poor internet speed!", Snackbar.LENGTH_INDEFINITE)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getFailure(message: String) {
        Utility.hideSweetProgress()
        root_layout.snackbar(message, Snackbar.LENGTH_INDEFINITE)
    }

    override fun onDestroy() {
        super.onDestroy()
        registerViewModel.cancelAllJobs()
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
                    if (resultUri != null) {
                        val uploadImage = File(resultUri?.path.toString())
                      //  val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(resultUri!!))
                      //  val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false)
                       // val image = encodeTobase64(resizedBitmap)
                        registerViewModel.profilePic = uploadImage

                        Glide.with(AppController.mApplication).load(resultUri).thumbnail(0.5f).into(binding.imgProfile)
                        binding.imgCancel.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.message
                }
            }
        }
        if (resultCode == RESULT_ERROR) {
            handleCropError(data!!, this)
        }
    }

    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded: String = Base64.encodeToString(b, Base64.DEFAULT)
        Log.e("LOOK", imageEncoded)
        return imageEncoded
    }


    override fun onCropFinish(result: UCropFragment.UCropResult) {
        when (result.mResultCode) {
            RESULT_OK -> handleCropResult(result.mResultData, this, binding.imgProfile)
            RESULT_ERROR -> handleCropError(result.mResultData, this)
        }
    }

    override fun loadingProgress(showLoader: Boolean) {
        mShowLoader = showLoader
    }

    override fun getVersionResponse(response: UserStatusResponse) {
        showVersionDialog(this)
    }

    override fun getMastersResponse(response: MasterUpdateResponse) {
        Log.v(TAG, "getMastersResponse")
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
                    dashboardViewModel.fetchNative(counts.native_place)
                } else {
                    val statesCount = dashboardViewModel.getStatesCount()
                    if (dbCount.states != counts.states || statesCount == 0) {
                        dashboardViewModel.fetchState(counts.states)
                    }

                    val citiesCount = dashboardViewModel.getCitiesCount()
                    if (dbCount.cities != counts.cities || citiesCount == 0) {
                        dashboardViewModel.fetchCity(counts.cities)
                    }

                    val subCommCount = dashboardViewModel.getSubCommCount()
                    if (dbCount.sub_community != counts.sub_community || subCommCount == 0) {
                        dashboardViewModel.fetchSubCommunities(counts.sub_community)
                    }

                    val localCommCount = dashboardViewModel.getLocalCommCount()
                    if (dbCount.local_community != counts.local_community || localCommCount == 0) {
                        dashboardViewModel.fetchLocalCommunities(counts.local_community)
                    }

                    val lnameCount = dashboardViewModel.getLastNameCount()
                    if (dbCount.sub_casts != counts.sub_casts || lnameCount == 0) {
                        dashboardViewModel.fetchLastName(counts.sub_casts)
                    }

                    val nativeCount = dashboardViewModel.getNativeCount()
                    if (dbCount.native_place != counts.native_place || nativeCount == 0) {
                        dashboardViewModel.fetchNative(counts.native_place)
                    }
                }
                setDropDownList()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var month = "${monthOfYear + 1}"
        var day = "${dayOfMonth}"
        if (day.length == 1) {
            day = "0$day"
        }
        if (month.length == 1) {
            month = "0${month}"
        }
        val date = "$day-$month-$year"
        val age = Utility.getAge(date, Utility.dd_MM_yyyy)
        registerViewModel.bdate = date
        binding.txtBdate.text = date + "($age)"
    }
}