package com.krs.community.activity

import android.content.Intent
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.databinding.ActivityRegisterBinding
import com.krs.community.entities.LastName
import com.krs.community.entities.States
import com.krs.community.entities.SubCommunity
import com.krs.community.interfaces.IRegisterListener
import com.krs.community.model.*
import com.krs.community.utils.*
import com.krs.community.viewmodel.RegisterViewModel
import com.krs.community.viewmodelfactory.RegisterViewModelFactory
import com.wooplr.spotlight.prefs.PreferencesManager
import com.wooplr.spotlight.utils.SpotlightSequence
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RegisterActivty : BaseActivity() ,IRegisterListener,KodeinAware{

    private var isShow = true
    private var isShow1 = true
    private var isShow2 = true
    private lateinit var mPreferencesManager:PreferencesManager;
    private lateinit var logger: Logger
    private lateinit var lstLastnameId:Array<Int?>
    private lateinit var lstStateId:Array<Int?>
    private lateinit var lstCityId:Array<Int?>
    private lateinit var lstSubCommId:Array<Int?>
    private lateinit var lstLocalCommId:Array<Int?>
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

        val str = resources.getString(R.string.already_have_a_account_sign_in) + "<b>" + " " + getString(R.string.login) + "</b>"
        txt_already?.text = Html.fromHtml(str)

        btn_register?.setOnClickListener {
            Utility.startSweetProgress(this,"Registering your family",resources.getString(R.string.loading))
            registerViewModel.getUserRegistration()
        }

        txt_already?.setOnClickListener { registerViewModel.onTextAlreadyClicked(this) }

        txt_how_register.setOnClickListener { registerViewModel.onHowRegisterClicked(this) }

        edt_password.setOnTouchListener(fun(_: View, event: MotionEvent): Boolean {
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edt_password.right - edt_password!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (isShow) {
                        edt_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.show_pass, 0)
                        edt_password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        isShow = false
                    } else {
                        edt_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.hide_pass, 0)
                        edt_password.inputType =InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
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
                        edt_cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.show_pass, 0)
                        edt_cpassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        isShow1 = false
                    } else {
                        edt_cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.hide_pass, 0)
                        edt_cpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
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

        spinnerCountries.setOnItemClickListener { pos->
            registerViewModel.countryCode =CountryData.countryAreaCodes[pos]
        }

        /*get states */
        registerViewModel.getUserStates()
        spinnerStates.setOnItemClickListener {
            //Utility.startSweetProgress(this,"Fetching Cities of ${spinnerStates.text}","Loading...")
            Utility.startSweetProgress(this,"fetching city",resources.getString(R.string.loading))
            registerViewModel.stateId=lstStateId[it]
            registerViewModel.fetchCitiesForStateId(it + 1)
        }


        /*get sub communities */
        registerViewModel.getLstSubCommunity()
        spinnerSub.setOnItemClickListener {
            Utility.startSweetProgress(this,"fetching local Community",resources.getString(R.string.loading))
            //Utility.startSweetProgress(this,"Fetching Local Communities of ${spinnerSub.text}","Loading...")
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

    override fun onDestroy() {
        super.onDestroy()
        registerViewModel.cancelAllJobs()
    }
}

