package com.krs.community.fragments

import android.annotation.SuppressLint
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
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentPersonalDetailsBinding
import com.krs.community.model.Member
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_personal_details.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PersonalDetailsFragment : Fragment(), KodeinAware, DatePickerDialog.OnDateSetListener {

    companion object {
        lateinit var binding: FragmentPersonalDetailsBinding
    }
    private lateinit var member: Member
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private var datepicker = SpinnerDatePickerDialogBuilder()
    private var which: Int = 0
    var numberOfLines = 5
    private lateinit var loginMem: Member

    override val kodein by kodein()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_details, container, false)
        profileDetailViewModel = ViewModelProvider(this, factory).get(ProfileDetailViewModel::class.java)
        member = arguments?.getSerializable(getString(R.string.member)) as Member

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, PersonalDetailsFragment::class.simpleName)
        mApp.facebookAnalytics(context, PersonalDetailsFragment::class.simpleName)

        val loginMember = Guru.getString(getString(R.string.loginMember), "")

        loginMem = Gson().fromJson(loginMember, Member::class.java)
        if (member.id.isNullOrEmpty() || member.id == loginMem.id || member.headId == loginMem.id || isAdmin()) {
            binding.edtLocalAddr.isFocusable = true
            binding.edtRole.isFocusable = true
            binding.spNative.isClickable = true
            binding.chkExpired.isEnabled = true
            binding.chkIsDonor.isFocusable = true
            binding.chkIsDonor.isClickable = true
            binding.txtBdate.isClickable = true
            binding.txtBdate.isEnabled = true
            binding.txtExpire.isClickable = true
            binding.txtBdate.isFocusable = true
            binding.txtExpire.isFocusable = true
            binding.spBg.isEnabled = true
            binding.spGotra.isClickable = true
            binding.spEducation.isClickable = true
            binding.spCurrentActivity.isClickable = true
            binding.spMarital.isClickable = true
            binding.txtMdate.isEnabled = true
            binding.spSubComm.isClickable = true
            binding.spLocalComm.isClickable = true
        } else {
            binding.spSubComm.isClickable = false
            binding.spLocalComm.isClickable = false
            binding.edtRole.isFocusable = false
            binding.spNative.isClickable = false
            binding.chkExpired.isEnabled = false
            binding.chkIsDonor.isFocusable = false
            binding.chkIsDonor.isClickable = false
            binding.txtBdate.isClickable = false
            binding.txtBdate.isEnabled = false
            binding.txtBdate.isFocusable = false
            binding.txtExpire.isClickable = false
            binding.txtExpire.isFocusable = false
            binding.spBg.isEnabled = false
            binding.spGotra.isClickable = false
            binding.spEducation.isClickable = false
            binding.spCurrentActivity.isClickable = false
            binding.spMarital.isClickable = false
            binding.txtMdate.isClickable = false
            binding.txtMdate.isEnabled = false
            binding.edtLocalAddr.isFocusable = false
        }

        if (member.role.equals(getString(R.string.LOCAL_ADMIN))) {
            binding.edtRole.text = getString(R.string.localAdmin)
        } else if (member.role.equals(getString(R.string.SUB_ADMIN))) {
            binding.edtRole.text = getString(R.string.SubAdmin)
        } else if (member.role.equals(getString(R.string.super_admin))) {
            binding.edtRole.text = "SUPER ADMIN"
        } else {
            binding.edtRole.text = getString(R.string.USER)
        }

        binding.chkIsDonor.isChecked = member.isDonor.equals("1")
        if (!member.bloodGroup.isNullOrEmpty()) {
            binding.spBg.setText(member.bloodGroup)
        }
        if (!member.localAddress.isNullOrEmpty()) {
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

            if (it == 1 || it == 2) {
                MatrimonyDetailsFragment.binding.chkInterested.isChecked = false
                MatrimonyDetailsFragment.binding.chkInterested.isEnabled = false
            } else {
                MatrimonyDetailsFragment.binding.chkInterested.isEnabled = true
                MatrimonyDetailsFragment.binding.chkInterested.isChecked = member.matrimony.toString() == "Yes"
            }

            if (it == 2) {
                binding.txtMdate.isClickable = true
                binding.txtMdate.isEnabled = true
            } else {
                binding.txtMdate.text = ""
                binding.txtMdate.isClickable = false
                binding.txtMdate.isEnabled = false
            }
        }

        if (!member.marriageDate.isNullOrEmpty()) {
            binding.txtMdate.text = Utility.ChangedateFormat(member.marriageDate)
        }
        if (!member.maritalStatus.isNullOrEmpty()) {
            binding.spMarital.setText(member.maritalStatus)
            if (member.maritalStatus == "Married") {
                binding.txtMdate.isClickable = true
                binding.txtMdate.isEnabled = true
                if (!member.marriageDate.isNullOrEmpty()) {
                    binding.txtMdate.text = Utility.changeDateFormat(member.marriageDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                }
            } else {
                binding.txtMdate.text = ""
                binding.txtMdate.isClickable = false
                binding.txtMdate.isEnabled = false
            }
        } else {
            binding.txtMdate.text = ""
            binding.txtMdate.isClickable = false
            binding.txtMdate.isEnabled = false
        }

        Coroutines.main {
            try {
                profileDetailViewModel.selectedRelationId = Integer.parseInt(member.relationId)
                profileDetailViewModel.relationName.await().observeForever {
                    if (it == "Wife") {
                        binding.spMarital.setText("Married")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (!member.nativePlaceId.isNullOrEmpty()) {
            profileDetailViewModel.selectedNativeId = Integer.parseInt(member.nativePlaceId)
        }
        binding.spNative.setOnItemClickListener {
            Coroutines.io {
                profileDetailViewModel.selectedNativeId = profileDetailViewModel.getNativeIdByName(binding.spNative.text.toString())
            }
        }

        if (!member.educationId.isNullOrEmpty()) {
            profileDetailViewModel.selectedEducationId = Integer.parseInt(member.educationId)
        }
        binding.spEducation.setOnItemClickListener {
            Coroutines.io {
                profileDetailViewModel.selectedEducationId = profileDetailViewModel.getEducationIdByName(binding.spEducation.text.toString())
            }
        }

        if (!member.currentActivityId.isNullOrEmpty()) {
            profileDetailViewModel.selectedActivityId = Integer.parseInt(member.currentActivityId)
        }
        binding.spCurrentActivity.setOnItemClickListener {
            Coroutines.io {
                profileDetailViewModel.selectedActivityId = profileDetailViewModel.getActivityIdByName(binding.spCurrentActivity.text.toString())
            }
        }

        if (!member.gotraId.isNullOrEmpty()) {
            profileDetailViewModel.selectedGotraId = Integer.parseInt(member.gotraId)
        }

        binding.spGotra.setOnItemClickListener {
            Coroutines.io {
                profileDetailViewModel.selectedGotraId = profileDetailViewModel.getGotraIdByName(binding.spGotra.text.toString())
            }
        }


        if (!member.expireDate.isNullOrBlank()) {
            binding.txtExpire.text = Utility.changeDateFormat(member.expireDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
        }

        if (member.isExpired.equals("1")) {
            binding.chkExpired.isChecked = true
            binding.txtExpire.text = Utility.changeDateFormat(member.expireDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
        } else {
            binding.chkExpired.isChecked = false
            binding.txtExpire.text = ""
        }

        binding.chkExpired.setOnClickListener {
            if (binding.chkExpired.isChecked) {
                binding.txtExpire.isClickable = true
                binding.txtExpire.isEnabled = true
            } else {
                binding.txtExpire.isEnabled = false
                binding.txtExpire.isClickable = false
                binding.txtExpire.text = ""
            }
        }

        if (!member.birthDate.isNullOrBlank()) {
            val date = Utility.changeDateFormat(member.birthDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)

            if (binding.chkExpired.isChecked) {
                if (!binding.txtExpire.text.isNullOrEmpty()) {
                    val date1 = Utility.StringToDate(date, Utility.dd_MM_yyyy)
                    val date2 = Utility.StringToDate(binding.txtExpire.text.toString(), Utility.dd_MM_yyyy)
                    val age = Utility.getDiffYears(date1, date2)
                    binding.txtBdate.text = date + "($age)"
                } else {
                    binding.txtBdate.text = date
                }
            } else {
                val age = Utility.getAge(date, Utility.dd_MM_yyyy)
                binding.txtBdate.text = date + "($age)"
            }
        }

        binding.txtBdate.setOnClickListener {
            which = 1
            val mem_date = binding.txtBdate.text.toString().trim()
            setDatePicker(mem_date)
        }

        binding.txtExpire.setOnClickListener {
            if (!chkExpired.isChecked) {
                return@setOnClickListener
            }
            which = 2
            val mem_date = binding.txtExpire.text.toString().trim()
            setDatePicker(mem_date)
        }

        binding.txtMdate.setOnClickListener {
            which = 3
            val mem_date = binding.txtMdate.text.toString().trim()
            setDatePicker(mem_date)
        }

        if (member.id.isNullOrEmpty()) {
            binding.llNative.visibility = View.GONE
            binding.llGotra.visibility = View.GONE
            binding.llLocalAdd.visibility = View.GONE
            binding.llCommunity.visibility = View.GONE
        } else {
            binding.llNative.visibility = View.VISIBLE
            binding.llGotra.visibility = View.VISIBLE
            binding.llLocalAdd.visibility = View.VISIBLE
            if (BuildConfig.FLAVOR == "ghanchi") {
                binding.llCommunity.visibility = View.VISIBLE
            } else {
                binding.llCommunity.visibility = View.GONE
            }
        }

        if (BuildConfig.FLAVOR == "ghanchi") {
            binding.llGotra.visibility = View.GONE
        } else {
            binding.llGotra.visibility = View.VISIBLE
        }

        getMasterList()
        return binding.root
    }

    /*override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            Handler().postDelayed({
                binding.scroll.fullScroll(ScrollView.FOCUS_UP)
                binding.scroll.isSmoothScrollingEnabled = true
            }, 1000)
        }
    }*/

    fun getSaveData(json: JSONObject) {
        try {
            json.put(getString(R.string.role), member.role)
            json.put(getString(R.string.native_place_id), profileDetailViewModel.selectedNativeId)

            val bdate = binding.txtBdate.text.toString().substringBefore("(")
            if (!bdate.isEmpty()) {
                val str = Utility.changeDateFormat(bdate, Utility.dd_MM_yyyy, Utility.yyyy_MM_dd)
                json.put(getString(R.string.birth_date), str)
            }
            if (!binding.txtExpire.text.isNullOrEmpty()) {
                val str = Utility.changeDateFormat(binding.txtExpire.text.toString(), Utility.dd_MM_yyyy, Utility.yyyy_MM_dd)
                json.put(getString(R.string.expire_date), str)
            }
            if (!binding.txtMdate.text.isNullOrEmpty()) {
                val str = Utility.changeDateFormat(binding.txtMdate.text.toString(), Utility.dd_MM_yyyy, Utility.yyyy_MM_dd)
                json.put(getString(R.string.marriage_date), str)
            }

            if (binding.chkExpired.isChecked) {
                json.put(getString(R.string.is_expired), 1)
            } else {
                json.put(getString(R.string.is_expired), 0)
            }
            if (binding.chkIsDonor.isChecked) {
                json.put(getString(R.string.is_donor), 1)
            } else {
                json.put(getString(R.string.is_donor), 0)
            }
            json.put(getString(R.string.blood_group), binding.spBg.text)

            json.put(getString(R.string.gotra_id), profileDetailViewModel.selectedGotraId)
            json.put(getString(R.string.education_id), profileDetailViewModel.selectedEducationId)
            json.put(getString(R.string.current_activity_id), profileDetailViewModel.selectedActivityId)
            json.put(getString(R.string.marital_status), binding.spMarital.text)
            json.put(getString(R.string.local_address), binding.edtLocalAddr.text)
            json.put(getString(R.string.local_community_id), profileDetailViewModel.selectedLocalCommunityId)
            json.put(getString(R.string.sub_community_id), profileDetailViewModel.selectedSubCommId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMasterList() = Coroutines.main {
        profileDetailViewModel.lstNativeName.await().observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                val lst = ArrayList<String>()
                lst.addAll(it)
                lst.remove(getString(R.string.other))
                val lstNativeName = ArrayList<String>()
                lstNativeName.add(getString(R.string.other))
                lst.sort()
                lstNativeName.addAll(lst)
                binding.spNative.setItems(lstNativeName.toTypedArray())
                binding.spNative.setExpandTint(R.color.black)
            }
        })

        profileDetailViewModel.nativeName.await().observe(viewLifecycleOwner, Observer {
            binding.spNative.setText(it)
        })

        profileDetailViewModel.lstEducationName.await().observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {

                val lst = ArrayList<String>()
                lst.addAll(it)
                lst.remove(getString(R.string.select))
                lst.remove(getString(R.string.other))

                val lstEducationName = ArrayList<String>()
                lstEducationName.add(getString(R.string.select))
                lstEducationName.add(getString(R.string.other))
                lst.sort()
                lstEducationName.addAll(lst)
                binding.spEducation.setItems(lstEducationName.toTypedArray())
                binding.spEducation.setExpandTint(R.color.black)
            }
        })
        profileDetailViewModel.educationName.await().observe(viewLifecycleOwner, Observer {
            binding.spEducation.setText(it)
        })

        profileDetailViewModel.lstActivityName.await().observe(viewLifecycleOwner, Observer {

            if (!it.isNullOrEmpty()) {

                val lst = ArrayList<String>()
                lst.addAll(it)
                lst.remove(getString(R.string.select))
                lst.remove(getString(R.string.other))

                val lstActivityName = ArrayList<String>()
                lstActivityName.add(getString(R.string.select))
                lstActivityName.add(getString(R.string.other))
                lst.sort()
                lstActivityName.addAll(lst)
                binding.spCurrentActivity.setItems(lstActivityName.toTypedArray())
                binding.spCurrentActivity.setExpandTint(R.color.black)
            }
        })
        profileDetailViewModel.activityName.await().observe(viewLifecycleOwner, Observer {
            binding.spCurrentActivity.setText(it)
        })

        profileDetailViewModel.lstGotraName.await().observe(viewLifecycleOwner, Observer {

            if (!it.isNullOrEmpty()) {
                val lst = ArrayList<String>()
                lst.addAll(it)
                lst.remove(getString(R.string.select))

                val lstGotraName = ArrayList<String>()
                lstGotraName.add(getString(R.string.select))
                lst.sort()
                lstGotraName.addAll(lst)
                binding.spGotra.setItems(lstGotraName.toTypedArray())
                binding.spGotra.setExpandTint(R.color.black)

            }
        })
        profileDetailViewModel.gotraName.await().observe(viewLifecycleOwner, Observer {
            binding.spGotra.setText(it)
        })

        val lstBlood = activity!!.resources.getStringArray(R.array.bloodGroup)
        binding.spBg.setItems(lstBlood)
        binding.spBg.setExpandTint(R.color.black)

        val lstMarital = activity!!.resources.getStringArray(R.array.marital)
        binding.spMarital.setItems(lstMarital)
        binding.spMarital.setExpandTint(R.color.black)

        profileDetailViewModel.lstSubCommName.await().observe(viewLifecycleOwner, Observer {
            binding.spSubComm.setItems(it.toTypedArray())
            binding.spSubComm.setExpandTint(R.color.black)
        })

        binding.spSubComm.setOnItemClickListener {
            Coroutines.io {
                setSunCommItemClick(true)
            }
        }

        binding.spLocalComm.setOnItemClickListener {
            Coroutines.io {
                val localName = binding.spLocalComm.text.toString().trim()
                profileDetailViewModel.selectedLocalCommunityId = profileDetailViewModel.getLocalCommunityId(localName)
            }
        }

        setMemberSubCommunity()

    }

    private suspend fun setSunCommItemClick(isClicked: Boolean) {
        Coroutines.io {
            profileDetailViewModel.selectedSubCommId = profileDetailViewModel.getSubCommIdByName(binding.spSubComm.text.toString())

            Coroutines.main {
                profileDetailViewModel.getLocalCommunity(profileDetailViewModel.selectedSubCommId).observeForever {
                    binding.spLocalComm.clear()
                    if (isClicked) {
                        binding.spLocalComm.setText(getString(R.string.select))
                        profileDetailViewModel.selectedLocalCommunityId = 0
                    } else {
                        setMemberLocalCommunity()
                    }
                    binding.spLocalComm.setItems(it.toTypedArray())
                    binding.spLocalComm.setExpandTint(R.color.black)
                }
            }
        }
    }


    private fun setMemberSubCommunity() = Coroutines.io {
        if (member.subCommunityId.isNotEmpty()) {
            profileDetailViewModel.selectedSubCommId = Integer.parseInt(member.subCommunityId)
            val name = profileDetailViewModel.getSubCommName(member.subCommunityId)

            Coroutines.main {
                binding.spSubComm.setText(name)
                setSunCommItemClick(false)
            }
        }
    }

    private fun isAdmin(): Boolean {
        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        var isAdmin = false
        if (!loginuser.isNullOrEmpty()) {
            val loginMem = Gson().fromJson<Member>(loginuser, Member::class.java)
            if (((loginMem.role == getString(R.string.LOCAL_ADMIN) && loginMem.localCommunityId == member.localCommunityId))) {
                isAdmin = true
            } else if (((loginMem.role == getString(R.string.SUB_ADMIN) && loginMem.subCommunityId == member.subCommunityId))) {
                isAdmin = true
            } else if ((loginMem.role == getString(R.string.super_admin))) {
                isAdmin = true
            }
        }
        return isAdmin
    }

    private fun setMemberLocalCommunity() = Coroutines.io {
        if (!member.localCommunityId.isNullOrEmpty()) {
            profileDetailViewModel.selectedLocalCommunityId = Integer.parseInt(member.localCommunityId)
            val local = profileDetailViewModel.getLocalCommunityName(member.localCommunityId)
            Coroutines.main {
                binding.spLocalComm.setText(local)
            }
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

        if (member.id.isNullOrEmpty() || member.id == loginMem.id
                || member.headId == loginMem.id
                || isAdmin()) {
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

        if (which == 1) {
            if (binding.txtExpire.text.isNotEmpty()) {
                val date1 = Utility.StringToDate(date, Utility.dd_MM_yyyy)
                val date2 = Utility.StringToDate(binding.txtExpire.text.toString(), Utility.dd_MM_yyyy)
                val age = Utility.getDiffYears(date1, date2)
                binding.txtBdate.text = date + "($age)"
            } else {
                val age = Utility.getAge(date, Utility.dd_MM_yyyy)
                binding.txtBdate.text = date + "($age)"
            }

        } else if (which == 2) {
            binding.txtExpire.text = date
            if (!binding.txtBdate.text.toString().isEmpty()) {
                var mydate = binding.txtBdate.text.toString()
                mydate = mydate.substringBefore("(")
                val date1 = Utility.StringToDate(mydate, Utility.dd_MM_yyyy)
                val date2 = Utility.StringToDate(date, Utility.dd_MM_yyyy)
                val age = Utility.getDiffYears(date1, date2)
                binding.txtBdate.text = mydate + "($age)"
            }
        } else if (which == 3) {
            binding.txtMdate.text = date
        }
        which = 0
    }
}