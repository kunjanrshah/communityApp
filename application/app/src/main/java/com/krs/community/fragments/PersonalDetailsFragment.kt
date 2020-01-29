package com.krs.community.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.krs.community.R
import com.krs.community.databinding.FragmentPersonalDetailsBinding
import com.krs.community.model.Member
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PersonalDetailsFragment : Fragment(), KodeinAware, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentPersonalDetailsBinding
    private lateinit var member: Member
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance()
    private var datepicker = SpinnerDatePickerDialogBuilder()
    private var which:Int=0
    var numberOfLines = 5
    private lateinit var loginMem:Member
    private var pattern="dd-MM-yyyy"
    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_details, container, false)
       profileDetailViewModel = ViewModelProviders.of(this, factory).get(ProfileDetailViewModel::class.java)
       member = arguments?.getSerializable(getString(R.string.member)) as Member

        val loginMember= Guru.getString(getString(R.string.loginUser),"")

        loginMem= Gson().fromJson(loginMember,Member::class.java)
        if(member.id == loginMem.id || member.headId == loginMem.id){
        binding.edtRole.isFocusable=true
        binding.spNative.isClickable=true
        binding.chkExpired.isEnabled=true
        binding.chkIsDonor.isFocusable=true
        binding.chkIsDonor.isClickable=true
        binding.txtBdate.isClickable=true
        binding.txtExpire.isClickable=true
        binding.txtBdate.isFocusable=true
        binding.txtExpire.isFocusable=true
        binding.spBg.isEnabled=true
        binding.spGotra.isClickable=true
        binding.spEducation.isClickable=true
        binding.spCurrentActivity.isClickable=true
        binding.spMarital.isClickable=true
        binding.txtMdate.isEnabled=true
        binding.edtLocalAddr.isFocusable=true
        }else{
            binding.edtRole.isFocusable=false
            binding.spNative.isClickable=false
            binding.chkExpired.isEnabled=false
            binding.chkIsDonor.isFocusable=false
            binding.chkIsDonor.isClickable=false
            binding.txtBdate.isClickable=false
            binding.txtBdate.isFocusable=false
            binding.txtExpire.isClickable=false
            binding.txtExpire.isFocusable=false
            binding.spBg.isEnabled=false
            binding.spGotra.isClickable=false
            binding.spEducation.isClickable=false
            binding.spCurrentActivity.isClickable=false
            binding.spMarital.isClickable=false
            binding.txtMdate.isClickable=false
            binding.txtMdate.isEnabled=false
            binding.edtLocalAddr.isFocusable=false
        }


        if(member.role.equals("LOCAL_ADMIN")){
            binding.edtRole.text = "Local Admin"
        }else if(member.role.equals("SUB_ADMIN")) {
            binding.edtRole.text = "Sub Admin"
        }else{
            binding.edtRole.text = "User"
        }

       binding.chkIsDonor.isChecked = member.isDonor.equals("1")
       if(!member.bloodGroup.isNullOrEmpty()){
           binding.spBg.setText(member.bloodGroup)
       }
       if(!member.localAddress.isNullOrEmpty()){
            binding.edtLocalAddr.setText(member.localAddress)
       }

        binding.edtLocalAddr.addTextChangedListener(object : TextWatcher {
            private var text: String? = null
            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lineCount: Int = binding.edtLocalAddr.lineCount
                if (lineCount > numberOfLines) {
                    binding.edtLocalAddr.setText(text)
                }
            }
        })

        binding.edtLocalAddr.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val editTextLineCount: Int = (v as EditText).lineCount
                if (editTextLineCount >= numberOfLines) return@OnKeyListener true
            }
            false
        })

        binding.spMarital.setOnItemClickListener {
            if(it==2){
                binding.txtMdate.isClickable=true
                binding.txtMdate.isEnabled=true
            }else{
                binding.txtMdate.text = ""
                binding.txtMdate.isClickable=false
                binding.txtMdate.isEnabled=false
            }
        }

        if(!member.maritalStatus.isNullOrEmpty()){
            binding.spMarital.setText(member.maritalStatus)
            if(member.maritalStatus.equals("Married")){
                binding.txtMdate.isClickable=true
                binding.txtMdate.isEnabled=true
                if(!member.marriageDate.isNullOrEmpty()){
                    binding.txtMdate.text = Utility.changeDateFormat(member.marriageDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                }
            }else{
                binding.txtMdate.text = ""
                binding.txtMdate.isClickable=false
                binding.txtMdate.isEnabled=false
            }
        }else{
            binding.txtMdate.text = ""
            binding.txtMdate.isClickable=false
            binding.txtMdate.isEnabled=false
        }

        if(!member.nativePlaceId.isNullOrEmpty()){
            profileDetailViewModel.selectedNativeId = Integer.parseInt(member.nativePlaceId)
        }
        binding.spNative.setOnItemClickListener {
            profileDetailViewModel.selectedNativeId = profileDetailViewModel.lstNativeId[it]
        }

        if(!member.educationId.isNullOrEmpty()){
            profileDetailViewModel.selectedEducationId = Integer.parseInt(member.educationId)
        }
        binding.spEducation.setOnItemClickListener {
            profileDetailViewModel.selectedEducationId = profileDetailViewModel.lstEducationId[it]
        }

        if(!member.currentActivityId.isNullOrEmpty()){
            profileDetailViewModel.selectedActivityId = Integer.parseInt(member.currentActivityId)
        }
        binding.spCurrentActivity.setOnItemClickListener {
            profileDetailViewModel.selectedActivityId = profileDetailViewModel.lstActivityId[it]
        }

        if(!member.gotraId.isNullOrEmpty()){
            profileDetailViewModel.selectedGotraId = Integer.parseInt(member.gotraId)
        }
        binding.spGotra.setOnItemClickListener {
            profileDetailViewModel.selectedGotraId = profileDetailViewModel.lstGotraId[it]
        }
        if(!member.marriageDate.isNullOrEmpty()){
            binding.txtMdate.text = Utility.ChangedateFormat(member.marriageDate)
        }

        if (!member.expireDate.isNullOrBlank()) {
            binding.txtExpire.text = Utility.changeDateFormat(member.expireDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
        }

        if (member.isExpired.equals("1")) {
            binding.txtExpire.text= Utility.changeDateFormat(member.expireDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
        } else {
            binding.txtExpire.text = ""
        }

        /*if(!binding.chkExpired.isChecked){
            binding.txtExpire.isEnabled=false
            binding.txtExpire.isClickable=false
            binding.txtExpire.text = ""
        }else
        {
            binding.txtExpire.isEnabled=true
            binding.txtExpire.isClickable=true
        }*/

        binding.chkExpired.setOnClickListener {
            if(binding.chkExpired.isChecked){
                binding.txtExpire.isClickable=true
                binding.txtExpire.isEnabled=true
            }else{
                binding.txtExpire.isEnabled=false
                binding.txtExpire.isClickable=false
                binding.txtExpire.text = ""
            }
        }



        if (!member.birthDate.isNullOrBlank()) {
            val date= Utility.changeDateFormat(member.birthDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)

            if(binding.chkExpired.isChecked){
                if(!binding.txtExpire.text.isNullOrEmpty()){
                  val date1=  Utility.StringToDate(date,pattern)
                  val date2=  Utility.StringToDate(binding.txtExpire.text.toString(),pattern)
                  val age=Utility.getDiffYears(date1,date2)
                  binding.txtBdate.text =date+"($age)"
                }else{
                    binding.txtBdate.text =date
                }
            }else {
                val age= Utility.getAge(date,Utility.dd_MM_yyyy)
                binding.txtBdate.text =date+"($age)"
            }
        }

        binding.txtBdate.setOnClickListener {
            which=1
            val mem_date = binding.txtBdate.text.toString().trim()
            setDatePicker(mem_date)
        }

        binding.txtExpire.setOnClickListener {
            which=2
            val mem_date = binding.txtExpire.text.toString().trim()
            setDatePicker(mem_date)
        }

        binding.txtMdate.setOnClickListener {
            which=3
            val mem_date = binding.txtMdate.text.toString().trim()
            setDatePicker(mem_date)
        }

        getMasterList()
       return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            Handler().postDelayed(Runnable {
                binding.scroll.fullScroll(ScrollView.FOCUS_UP);
                binding.scroll.isSmoothScrollingEnabled=true
            },1000)
        }
    }

    fun getSaveData(json:JSONObject){
        try{
            json.put(getString(R.string.role),member.role)
            json.put(getString(R.string.native_place_id),profileDetailViewModel.selectedNativeId)

            val bdate=binding.txtBdate.text.toString().substringBefore("(")
            if(!bdate.isEmpty()){
                val str=Utility.changeDateFormat(bdate,Utility.dd_MM_yyyy,Utility.yyyy_MM_dd)
                json.put(getString(R.string.birth_date),str)
            }
            if(!binding.txtExpire.text.isNullOrEmpty()){
                val str=Utility.changeDateFormat(binding.txtExpire.text.toString(),Utility.dd_MM_yyyy,Utility.yyyy_MM_dd)
                json.put(getString(R.string.expire_date),str)
            }
            if(!binding.txtMdate.text.isNullOrEmpty()){
                val str=Utility.changeDateFormat(binding.txtMdate.text.toString(),Utility.dd_MM_yyyy,Utility.yyyy_MM_dd)
                json.put(getString(R.string.marriage_date),str)
            }

            if(binding.chkExpired.isChecked){
                json.put(getString(R.string.is_expired),1)
            }else{
                json.put(getString(R.string.is_expired),0)
            }
            if(binding.chkIsDonor.isChecked){
                json.put(getString(R.string.is_donor),1)
            }else{
                json.put(getString(R.string.is_donor),0)
            }
            json.put(getString(R.string.blood_group),binding.spBg.text)

            json.put(getString(R.string.gotra_id),profileDetailViewModel.selectedGotraId)
            json.put(getString(R.string.education_id),profileDetailViewModel.selectedEducationId)
            json.put(getString(R.string.current_activity_id),profileDetailViewModel.selectedActivityId)
            json.put(getString(R.string.marital_status),binding.spMarital.text)
            json.put(getString(R.string.local_address),binding.edtLocalAddr.text)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getMasterList() = Coroutines.main {
        profileDetailViewModel.lstNativeName.await().observe(this, Observer {
            binding.spNative.setItems(it.toTypedArray())
            binding.spNative.setExpandTint(R.color.black)
        })
        profileDetailViewModel.nativeIds.await().observe(this, Observer {
            profileDetailViewModel.lstNativeId = it
        })
        profileDetailViewModel.nativeName.await().observe(this, Observer {
            binding.spNative.setText(it)
        })

        profileDetailViewModel.lstEducationName.await().observe(this, Observer {
            binding.spEducation.setItems(it.toTypedArray())
            binding.spEducation.setExpandTint(R.color.black)
        })
        profileDetailViewModel.educationName.await().observe(this, Observer {
            binding.spEducation.setText(it)
        })
        profileDetailViewModel.educationIds.await().observe(this, Observer {
            profileDetailViewModel.lstEducationId = it
        })

        profileDetailViewModel.lstActivityName.await().observe(this, Observer {
            binding.spCurrentActivity.setItems(it.toTypedArray())
            binding.spCurrentActivity.setExpandTint(R.color.black)
        })
        profileDetailViewModel.activityName.await().observe(this, Observer {
            binding.spCurrentActivity.setText(it)
        })
        profileDetailViewModel.activityIds.await().observe(this, Observer {
            profileDetailViewModel.lstActivityId = it
        })

        profileDetailViewModel.lstGotraName.await().observe(this, Observer {
            binding.spGotra.setItems(it.toTypedArray())
            binding.spGotra.setExpandTint(R.color.black)
        })
        profileDetailViewModel.gotraName.await().observe(this, Observer {
            binding.spGotra.setText(it)
        })
        profileDetailViewModel.gotraIds.await().observe(this, Observer {
            profileDetailViewModel.lstGotraId = it
        })

        val lstBlood =  activity!!.resources.getStringArray(R.array.bloodGroup)
        binding.spBg.setItems(lstBlood)
        binding.spBg.setExpandTint(R.color.black)

        val lstMarital =  activity!!.resources.getStringArray(R.array.marital)
        binding.spMarital.setItems(lstMarital)
        binding.spMarital.setExpandTint(R.color.black)
    }

    private fun setDatePicker(mem_date:String) {
        val year: Int = Calendar.getInstance().get(Calendar.YEAR)
        val day: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val month: Int = Calendar.getInstance().get(Calendar.MONTH)

        val format = SimpleDateFormat(pattern)
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

        if(member.id == loginMem.id || member.headId == loginMem.id){
            datepicker.context(activity)
                    .callback(this)
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(year1, month1, day1)
                    .maxDate(year, month, day)
                    .minDate(1900, 0, 1)
                    .build().show()
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

        if(which==1){
            if(binding.txtExpire.text.isNotEmpty()){
                val date1=  Utility.StringToDate(date,pattern)
                val date2=  Utility.StringToDate(binding.txtExpire.text.toString(),pattern)
                val age=Utility.getDiffYears(date1,date2)
                binding.txtBdate.text =date+"($age)"
            }else{
                val age= Utility.getAge(date,Utility.dd_MM_yyyy)
                binding.txtBdate.text = date+"($age)"
            }

        }else if(which==2){
            binding.txtExpire.text = date
            if(!binding.txtBdate.text.toString().isEmpty()){
                var mydate=binding.txtBdate.text.toString();
                mydate=mydate.substringBefore("(")
                val date1=  Utility.StringToDate(mydate,pattern)
                val date2=  Utility.StringToDate(date,pattern)
                val age=Utility.getDiffYears(date1,date2)
                binding.txtBdate.text =mydate+"($age)"
            }
        }else if(which==3){
            binding.txtMdate.text = date
        }
        which=0
    }
}