package com.krs.community.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.databinding.ActivityRegisterBinding
import com.krs.community.entities.LastName
import com.krs.community.entities.States
import com.krs.community.entities.SubCommunity
import com.krs.community.listeners.IRegisterListener
import com.krs.community.listeners.ImageUploadListener
import com.krs.community.model.*
import com.krs.community.utils.*
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RegisterViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RegisterViewModelFactory
import com.wooplr.spotlight.prefs.PreferencesManager
import com.wooplr.spotlight.utils.SpotlightSequence
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

class RegisterActivty : AppCompatActivity(), UCropFragmentCallback ,IRegisterListener,KodeinAware, ImageUploadListener {

    private var str_profile_hash = ""
    private lateinit var mPreferencesManager:PreferencesManager;
    private var mShowLoader: Boolean = false
    private val PICK_GALLERY_REQUEST = 1
    private lateinit var logger: Logger
    private lateinit var lstLastnameId:Array<Int?>
    private lateinit var lstStateId:Array<Int?>
    private lateinit var lstCityId:Array<Int?>
    private lateinit var lstSubCommId:Array<Int?>
    private lateinit var lstLocalCommId:Array<Int?>
    lateinit var binding:ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private var resultUri: Uri?=null

    companion object {
        private val TAG = RegisterActivty::class.java.simpleName
    }

    override val kodein by kodein()
    private val registerViewModelFactory: RegisterViewModelFactory by instance()
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = Logger(TAG)

        registerViewModel = ViewModelProviders.of(this,registerViewModelFactory).get(RegisterViewModel::class.java)
        registerViewModel.iRegisterListener=this

        profileDetailViewModel = ViewModelProviders.of(this,profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mImageUploadListener=this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.registerviewmodel = registerViewModel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false)
        }

        val str = resources.getString(R.string.already_have_a_account_sign_in) + "<b>" + " " + getString(R.string.login) + "</b>"
        txt_already?.text = Html.fromHtml(str)

        btn_register?.setOnClickListener {
            Utility.startSweetProgress(this,"Registering your family",resources.getString(R.string.loading))
            registerViewModel.getUserRegistration()
        }

        txt_already?.setOnClickListener { registerViewModel.onTextAlreadyClicked(this) }

        txt_how_register.setOnClickListener { registerViewModel.onHowRegisterClicked(this) }

        img_cancel.setOnClickListener {
            img_profile.setImageResource(R.drawable.man_reg)
            img_cancel.visibility = View.GONE
            val icon = BitmapFactory.decodeResource(resources, R.drawable.man_reg)
            if (icon != null) {
                str_profile_hash = Utility.getBase64(icon)
            }
        }

        /*get Lastnames */
        registerViewModel.getUserLastName()

        /*get countries */
        /*spinnerCountries.setItems(CountryData.countryNames)
        spinnerCountries.setExpandTint(R.color.black)
        spinnerCountries.select(0)
        registerViewModel.country_code=CountryData.countryAreaCodes[0]*/

        spinnerCountries.setOnItemClickListener { pos->
            registerViewModel.countryCode =CountryData.countryAreaCodes[pos]
        }

        /*get states */
        registerViewModel.getUserStates()
        spinnerStates.setOnItemClickListener {
            Utility.startSweetProgress(this,"Fetching City",resources.getString(R.string.loading))
            registerViewModel.stateId=lstStateId[it]
            registerViewModel.fetchCitiesForStateId(it + 1)
        }


        /*get sub communities */
        registerViewModel.getLstSubCommunity()
        spinnerSub.setOnItemClickListener {
            Utility.startSweetProgress(this,"Fetching Local Community",resources.getString(R.string.loading))
            registerViewModel.subCommId=lstSubCommId[it]
            registerViewModel.getLstLocalCommunity(it + 1)
        }

        spinnerLname.setOnItemClickListener {position->
            Log.d(TAG,"spinnerLname: "+lstLastnameId[position])
            registerViewModel.lastnameId=lstLastnameId[position]
        }

        spinnerCities.setOnItemClickListener {position->
            Log.d(TAG,"spinnerCities: "+lstCityId[position])
            registerViewModel.cityId=lstCityId[position]
        }

        spinnerLocal.setOnItemClickListener {position->
            Log.d(TAG,"spinnerLocal: "+lstLocalCommId[position])
            registerViewModel.localCommId=lstLocalCommId[position]
        }

        val lstGender = arrayOf("Male", "Female")
        binding.spinnerGender.setItems(lstGender)
        binding.spinnerGender.setExpandTint(R.color.black)

        binding.spinnerGender.setOnClickListener {
            registerViewModel.gender=it.toString()
        }

        binding.imgProfile.setOnClickListener { v ->
            pickFromGallery(this)
        }


        //mPreferencesManager=PreferencesManager(this)
        //mPreferencesManager.resetAll()

        /*scroll.viewTreeObserver.addOnScrollChangedListener {
            if (scroll.getChildAt(0).bottom > (scroll.height + scroll.scrollY)) {
               if(isShow2){
                   isShow2=false
                   Handler(Looper.getMainLooper()).postDelayed({
                       scroll.scrollToBottom()
                       showSequence()
                   }, 400)
               }
            }
        }*/
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

    private fun showSequence(){
        SpotlightSequence.getInstance(this, null)
                .addSpotlight(txt_how_register, "Youtube Video", "How to Register?", "how_register")
                .addSpotlight(btn_register, "Register Button", "Fill up your details\n" +"Click here to Register", "btn_register")
                .startSequence()
    }

    override fun getRegisterFailure(message: String,filed:Int) {
        Utility.hideSweetProgress()
        root_layout.snackbar(message, Snackbar.LENGTH_LONG)
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
                Utility.startSweetProgress(this, "Register", getString(R.string.loading))
                profileDetailViewModel.uploadImage(uploadImage, data.userId.toString(),getString(R.string.profile))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
            moveToLogin(data.message)
        }
    }


    private fun moveToLogin(message: String){
        root_layout.snackbar(message, Snackbar.LENGTH_INDEFINITE)
        val mIntent = Intent(this, LoginActivity::class.java)
        startActivity(mIntent)
        finish()
    }

    override fun getStates(data: List<States>) {
        val lstState = Array<String?>(data.size) { null }
        lstStateId = Array(data.size) { null }
        for ((index, stateData) in data.withIndex()) {
            lstState[index] = stateData.name
            lstStateId[index] = stateData.id
        }
        spinnerStates.setItems(lstState)
        spinnerStates.setExpandTint(R.color.black)
    }

    override fun getCities(data: List<Datum>) {
        val lstCity = Array<String?>(data.size) { null }
        lstCityId = Array(data.size) { null }
        for ((index, cityData) in data.withIndex()) {
            lstCity[index] = cityData.name
            lstCityId[index] = Integer.parseInt(cityData.id)
        }
        spinnerCities.clear()
        spinnerCities.setTitle("Select ${spinnerStates.text}'s City")
        spinnerCities.setItems(lstCity)
        spinnerCities.setExpandTint(R.color.black)
        if(Utility.dialog!=null && Utility.dialog.isShowing) {
            Utility.dialog.dismissWithAnimation()
        }

    }

    override fun getSubCommunity(data: List<SubCommunity>) {
        val lstSubCom = Array<String?>(data.size) { null }
        lstSubCommId = Array(data.size) { null }
        for ((index, subData) in data.withIndex()) {
            lstSubCom[index] = subData.name
            lstSubCommId[index] = subData.id
        }
        spinnerSub.setItems(lstSubCom)
    }

    override fun getLocalCommunity(data: List<Datum>) {
        val lstLocal = Array<String?>(data.size) { null }
        lstLocalCommId = Array(data.size) { null }
        for ((index, LocalData) in data.withIndex()) {
            lstLocal[index] = LocalData.name
            lstLocalCommId[index] = Integer.parseInt(LocalData.id)
        }
        spinnerLocal.clear()
        spinnerLocal.setTitle("Select ${spinnerSub.text}'s Local Community")
        spinnerLocal.setItems(lstLocal)
        spinnerLocal.setExpandTint(R.color.black)
        if(Utility.dialog!=null && Utility.dialog.isShowing) {
            Utility.dialog.dismissWithAnimation()
        }
    }


    override fun getLastname(data: List<LastName>) {
        val lstLastname = Array<String?>(data.size) { null }
        lstLastnameId = Array(data.size) { null }
        for ((index, stateData) in data.withIndex()) {
            lstLastname[index] = stateData.name
            lstLastnameId[index]=stateData.id
        }

        spinnerLname.setItems(lstLastname)
        spinnerLname.setExpandTint(R.color.black)
    }

    override fun getResult(profile: String) {
        Utility.hideSweetProgress()
        moveToLogin("Request sent to your admin")
    }

    override suspend fun onFailure(message: String) {
        Utility.hideSweetProgress()
        root_layout.snackbar(message, Snackbar.LENGTH_INDEFINITE)
    }

    override suspend fun getFailure(message: String) {

        withContext(Main){
            if(Utility.dialog!=null && Utility.dialog.isShowing) {
                Utility.dialog.dismissWithAnimation()
            }
            Utility.hideSweetProgress()
            root_layout.snackbar(message,Snackbar.LENGTH_INDEFINITE)
        }
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
                    startCrop(selectedUri,this)
                } else {
                    Toast.makeText(this@RegisterActivty, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show()
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
            handleCropError(data!!,this)
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


}

