package com.krs.community.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.databinding.ActivityRegisterBinding
import com.krs.community.entities.LastName
import com.krs.community.entities.States
import com.krs.community.entities.SubCommunity
import com.krs.community.interfaces.IRegisterListener
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.model.*
import com.krs.community.utils.*
import com.krs.community.viewmodel.RegisterViewModel
import com.krs.community.viewmodelfactory.RegisterViewModelFactory
import com.wooplr.spotlight.SpotlightView
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

class RegisterActivty : BaseActivity(), UCropFragmentCallback ,IRegisterListener,KodeinAware{

    private var str_profile_hash = ""
    private var isShow = true
    private var isShow1 = true
    private var isShow2 = true
    private lateinit var mPreferencesManager:PreferencesManager;
    private var mShowLoader: Boolean = false
    private val PICK_GALLERY_REQUEST = 1
    private lateinit var logger: Logger
    lateinit var lstLastnameId:Array<Int?>
    lateinit var lstStateId:Array<Int?>
    lateinit var lstCityId:Array<Int?>
    lateinit var lstSubCommId:Array<Int?>
    lateinit var lstLocalCommId:Array<Int?>
    lateinit var binding:ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    companion object {
        private val TAG = RegisterActivty::class.java.simpleName
    }

    override val kodein by kodein()
    private val factory: RegisterViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = Logger(TAG)

        registerViewModel = ViewModelProviders.of(this,factory).get(RegisterViewModel::class.java)
        registerViewModel.iRegisterListener=this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.registerviewmodel = registerViewModel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false)
        }

        Memory_Allocation()

        btn_register?.setOnClickListener {
            Utility.startSweetProgress(this,"Registering your family","Loading...")
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

        edt_password.setOnTouchListener(fun(_: View, event: MotionEvent): Boolean {
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edt_password.right - edt_password!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (isShow) {
                        edt_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0)
                        edt_password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        isShow = false
                    } else {
                        edt_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0)
                        edt_password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        isShow = true
                    }
                    edt_password.setSelection(edt_password.length())
                    return true
                }
            }
            return false
        })

        edt_cpassword.setOnTouchListener(fun(v: View, event: MotionEvent): Boolean {
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edt_cpassword!!.right - edt_cpassword!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (isShow1) {
                        edt_cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0)
                        edt_cpassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        isShow1 = false
                    } else {
                        edt_cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0)
                        edt_cpassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        isShow1 = true
                    }
                    try {
                        edt_cpassword.setSelection(edt_cpassword.length())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
            }
            return false
        })

        /*get Lastnames */
        registerViewModel.getUserLastName()

        /*get countries */
        /*spinnerCountries.setItems(CountryData.countryNames)
        spinnerCountries.setExpandTint(R.color.black)
        spinnerCountries.select(0)
        registerViewModel.country_code=CountryData.countryAreaCodes[0]*/

        spinnerCountries.setOnItemClickListener(JRSpinner.OnItemClickListener {pos->
            registerViewModel.country_code =CountryData.countryAreaCodes[pos]
        })

        /*get states */
        registerViewModel.getUserStates()
        spinnerStates.setOnItemClickListener {
            //Utility.startSweetProgress(this,"Fetching Cities of ${spinnerStates.text}","Loading...")
            Utility.startSweetProgress(this,"List of City","Loading...")
            registerViewModel.state_id=lstStateId[it]
            registerViewModel.fetchCitiesForStateId(it + 1)
        }


        /*get sub communities */
        registerViewModel.getLstSubCommunity()
        spinnerSub.setOnItemClickListener {
            Utility.startSweetProgress(this,"List of local Community","Loading...")
            //Utility.startSweetProgress(this,"Fetching Local Communities of ${spinnerSub.text}","Loading...")
            registerViewModel.sub_comm_id=lstSubCommId[it]
            registerViewModel.getLstLocalCommunity(it + 1)
        }

        spinnerLname.setOnItemClickListener {position->
            Log.d(TAG,"spinnerLname: "+lstLastnameId[position])
            registerViewModel.lastname_id=lstLastnameId[position]
        }

        spinnerCities.setOnItemClickListener {position->
            Log.d(TAG,"spinnerCities: "+lstCityId[position])
            registerViewModel.city_id=lstCityId[position]
        }

        spinnerLocal.setOnItemClickListener {position->
            Log.d(TAG,"spinnerLocal: "+lstLocalCommId[position])
            registerViewModel.local_comm_id=lstLocalCommId[position]
        }

        binding.imgProfile.setOnClickListener { v ->
                pickFromGallery(this)
        }

        mPreferencesManager=PreferencesManager(this)
        mPreferencesManager.resetAll()
        Handler(Looper.getMainLooper()).postDelayed({
            showPhotoIntro()
        }, 400)

        scroll.getViewTreeObserver().addOnScrollChangedListener {
            if (scroll.getChildAt(0).getBottom()  > (scroll.getHeight() + scroll.getScrollY())) {
               if(isShow2){
                   isShow2=false
                   Handler(Looper.getMainLooper()).postDelayed({
                       scroll.scrollToBottom()
                       showSequence()
                   }, 400)
               }
            }
        }
    }

    fun ScrollView.scrollToBottom() {
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY+ height)
        smoothScrollBy(0, delta)
    }

    fun showSequence(){
        SpotlightSequence.getInstance(this, null)
                .addSpotlight(txt_how_register, "Youtube Video", "How to Register?", "how_register")
                .addSpotlight(btn_register, "Register Button", "Fill up your details\n" +"Click here to Register", "btn_register")
                .startSequence()
    }

    fun showPhotoIntro(){
        SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(false)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Profile Photo")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Upload your Photo")
                .maskColor(Color.parseColor("#dc000000"))
                .target(img_profile)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("img_profile") //UNIQUE ID
                .show()
    }

    override fun getRegisterFailure(message: String,filed:Int) {
        Utility.hideSweetProgress()
        root_layout.snackbar(message, Snackbar.LENGTH_LONG)
        when(filed){
            1 -> binding.edtHeadName.requestFocus()
            2 -> binding.spinnerLname.requestFocus()
            3 -> binding.edtEmailId.requestFocus()
            4 -> binding.spinnerCountries.requestFocus()
            5 -> binding.edtMobile.requestFocus()
            6 -> binding.edtPassword.requestFocus()
            7 -> binding.edtCpassword.requestFocus()
            8 -> binding.edtAddress.requestFocus()
            9 -> binding.spinnerStates.requestFocus()
            10 -> binding.spinnerCities.requestFocus()
            11 -> binding.spinnerSub.requestFocus()
            12 -> binding.spinnerLocal.requestFocus()
            else -> binding.imgProfile
        }
    }

    override fun getRegisterSuccess(data: RegisterModel) {
        Utility.hideSweetProgress()
        root_layout.snackbar(data.message, Snackbar.LENGTH_INDEFINITE)
        Log.d(TAG, "onRegisterButtonClick")
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

    override suspend fun getFailure(message: String) {

        withContext(Main){
            if(Utility.dialog!=null && Utility.dialog.isShowing) {
                Utility.dialog.dismissWithAnimation()
            }
            Utility.hideSweetProgress()
            root_layout.snackbar(message,Snackbar.LENGTH_INDEFINITE)
        }
    }


    private fun Memory_Allocation() {

        val str = resources.getString(R.string.already_have_a_account_sign_in) + "<b>" + " " + getString(R.string.login) + "</b>"
        txt_already?.text = Html.fromHtml(str)
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
                handleCropResult(data!!,this,binding.imgProfile)
            }
        }
        if (resultCode == RESULT_ERROR) {
            handleCropError(data!!,this)
        }

    }



    override fun showPermissionGranted(permission: String) {
        super.showPermissionGranted(permission)
        if(permission.contains("EXTERNAL_STORAGE")){
            pickFromGallery(this)
        }
    }

    override fun showPermissionDenied(permission: String, isPermanentlyDenied: Boolean) {
        super.showPermissionDenied(permission, isPermanentlyDenied)

        if(permission.contains("EXTERNAL_STORAGE")){
            promptReadPermission(this)
        }

        if(isPermanentlyDenied){
            displayNeverAskAgainDialog(this)
        }
    }

    override fun loadingProgress(showLoader: Boolean) {
        mShowLoader = showLoader
        //  supportInvalidateOptionsMenu()
    }

    override fun onCropFinish(result: UCropFragment.UCropResult) {
        when (result.mResultCode) {
            RESULT_OK -> handleCropResult(result.mResultData,this,binding.imgProfile)
            RESULT_ERROR -> handleCropError(result.mResultData,this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        registerViewModel.cancelAllJobs()
    }
}

