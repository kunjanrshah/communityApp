package com.krs.community.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.easywaylocation.EasyWayLocation
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.ProfileDetailActivity.Companion.setPercentage
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentMainDetailsBinding
import com.krs.community.listeners.EditMemberListener
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import kotlinx.android.synthetic.main.fragment_main_details.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class MainDetailsFragment : Fragment(), KodeinAware, EditMemberListener {

    lateinit var binding: FragmentMainDetailsBinding
    private lateinit var member: Member
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    var numberOfLines = 5
    override val kodein by kodein()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_details, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, MainDetailsFragment::class.simpleName)
        mApp.facebookAnalytics(context, MainDetailsFragment::class.simpleName)

        profileDetailViewModel = ViewModelProvider(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener = this
        member = arguments?.getSerializable(getString(R.string.member)) as Member
        val loginMember = Guru.getString(getString(R.string.loginMember), "")
        val loginMem = Gson().fromJson(loginMember, Member::class.java)

        if (member.id.isNullOrEmpty() || member.id == loginMem.id || member.headId == loginMem.id || loginMem.role.toString().toLowerCase() != "user") {

            binding.fname.isFocusable = true
            binding.edtArea.isFocusable = true
            binding.edtAddr.isFocusable = true
            binding.edtEmail.isFocusable = true
            binding.edtPincode.isFocusable = true
            binding.edtFather.isFocusable = true
            binding.edtMother.isFocusable = true
            binding.edtMobile.isFocusable = true
            binding.edtPassword.isFocusable = true
            binding.edtCpassword.isFocusable = true
            setMemberCode(member.memberCode)
            binding.tvId.text = "/ ${member.id}"
            binding.spState.isEnabled = true
            binding.spCity.isEnabled = true
            binding.spRelation.isEnabled = !(member.headId == "0" && !member.id.isNullOrEmpty())

            binding.spLastname.isEnabled = true
            binding.spGender.isEnabled = true

            binding.chkRented.isFocusable = false
            binding.chkRented.isClickable = true

            binding.llMcode.visibility = View.VISIBLE
            if (member.id == loginMem.id || member.headId == loginMem.id) {
                member.stateId = loginMem.stateId
                member.cityId = loginMem.cityId
                member.city = loginMem.city
                member.area = loginMem.area
                member.pincode = loginMem.pincode
                member.address = loginMem.address
                member.isRented = loginMem.isRented
            }

        } else {
            binding.fname.isFocusable = false
            binding.edtEmail.isFocusable = false
            binding.edtEmail.movementMethod = LinkMovementMethod.getInstance()
            binding.edtMobile.isFocusable = false
            binding.edtMother.isFocusable = false
            binding.edtFather.isFocusable = false
            binding.edtArea.isFocusable = false
            binding.edtAddr.isFocusable = false
            binding.edtPincode.isFocusable = false
            binding.edtCode.isFocusable = false
            binding.edtCode.isClickable = false
            binding.edtPassword.isFocusable = false
            binding.edtCpassword.isFocusable = false

            binding.spGender.isEnabled = false
            binding.spLastname.isEnabled = false
            binding.spRelation.isEnabled = false
            binding.spState.isEnabled = false
            binding.spCity.isEnabled = false
            binding.chkRented.isFocusable = false
            binding.chkRented.isClickable = false

            if (member.memberCode.isNullOrEmpty()) {
                binding.llMcode.visibility = View.GONE
            } else {
                binding.llMcode.visibility = View.VISIBLE
                setMemberCode(member.memberCode)
                binding.tvId.text = "/ ${member.id}"
            }
        }

        if (member.id.isNullOrEmpty()) {
            binding.llPin.visibility = View.VISIBLE
        } else {
            binding.llPin.visibility = View.GONE
        }
        if (!member.emailAddress.isNullOrEmpty()) {
            val spannable: Spannable = SpannableString(member.emailAddress)
            Linkify.addLinks(spannable, Linkify.WEB_URLS)
            val text: CharSequence = TextUtils.concat(spannable, "\u200B")
            binding.edtEmail.setText(text)
        } else {
            binding.edtEmail.setText(member.emailAddress)
        }

        binding.edtEmail.filters = arrayOf(Utility.filter)
        binding.fname.setText(member.firstName)
        binding.edtFather.setText(member.fatherName)
        binding.edtMother.setText(member.motherName)
        binding.edtMobile.setText(member.mobile)
        binding.edtAddr.setText(member.address)
        binding.spGender.setText(member.gender)
        binding.edtArea.setText(member.area)
        binding.edtPincode.setText(member.pincode)
        binding.chkRented.isChecked = member.isRented.equals("1")

        binding.llHome.setOnClickListener {

            if (binding.tvDistance.text.toString() != "Home") {
                val memberId = Guru.getString(getString(R.string.member_id), "")
                if (memberId == member.id) {
                    SweetAlertDialog(activity, SweetAlertDialog.FORGOT_TYPE)
                } else {
                    SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                }
                        .setTitleText(getString(R.string.homeLocation))
                        .setContentText(getString(R.string.withGoogleMap))
                        .setConfirmText(getString(R.string.View))
                        .setNeutralText(getString(R.string.set))
                        .setCustomImage(R.drawable.ic_medk)
                        .setConfirmClickListener {
                            it.dismiss()
                            Utility.showDirections(activity, member.homeLat.toDouble(), member.homeLng.toDouble(), "${member.firstName}" + getString(R.string.homeDetail))
                        }
                        .setNeutralClickListener {
                            it.dismiss()
                            if (member.headId == "0" && !member.id.isNullOrEmpty()) {
                                val jsonObject = JSONObject()
                                jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                jsonObject.put(getString(R.string.id), member.id)
                                jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                jsonObject.put(getString(R.string.home_lat), ProfileDetailActivity.cur_lat.value)
                                jsonObject.put(getString(R.string.home_lng), ProfileDetailActivity.cur_lng.value)
                                val profile = JsonParser().parse(jsonObject.toString()) as JsonObject
                                Utility.startSweetProgress(activity, getString(R.string.updatingLocation), getString(R.string.PleaseWait))
                                profileDetailViewModel.updateProfile(profile, true)
                            } else {
                                Utility.displaySnackBarWithBottomMargin(ll_main, getString(R.string.OnlyFamilyHeadLocation))
                            }
                        }
                        .show()
            } else {
                binding.llMain.snackbar("Home location not set", Snackbar.LENGTH_SHORT)
            }

        }

        binding.edtAddr.addTextChangedListener(object : TextWatcher {
            private var text: String? = null
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lineCount: Int = binding.edtAddr.lineCount
                if (lineCount > numberOfLines) {
                    binding.edtAddr.setText(text)
                }
            }
        })

        binding.edtAddr.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val editTextLineCount: Int = (v as EditText).lineCount
                if (editTextLineCount >= numberOfLines) return@OnKeyListener true
            }
            false
        })

        binding.spState.setOnItemClickListener {
            Coroutines.io {
                profileDetailViewModel.selectedStateId = profileDetailViewModel.getstateIdByName(binding.spState.text.toString())

                Coroutines.main {
                    val cities = profileDetailViewModel.getCityNamebyState(profileDetailViewModel.selectedStateId)
                    binding.spCity.clear()
                    binding.spCity.setText(getString(R.string.select))
                    profileDetailViewModel.selectedCityId = 0
                    binding.spCity.setItems(cities.toTypedArray())
                    binding.spCity.setExpandTint(R.color.black)
                }
            }

        }

        binding.spCity.setOnItemClickListener {
            Coroutines.io {
                val cityName = binding.spCity.text.toString().trim()
                profileDetailViewModel.selectedCityName = cityName
                profileDetailViewModel.selectedCityId = profileDetailViewModel.getCityIdByName(cityName)
            }
        }

        binding.spRelation.setOnItemClickListener {
            Coroutines.io {
                profileDetailViewModel.selectedRelationId = profileDetailViewModel.getIdByRelation(binding.spRelation.text.toString())
            }
        }

        binding.spLastname.setOnItemClickListener {
            Coroutines.io {
                profileDetailViewModel.selectedLastNameId = profileDetailViewModel.getIdByLastName(binding.spLastname.text.toString())
            }
        }

        setMemberRelation()
        setMemberLastname()
        setMemberState()
        setMemberCity()
        getMasterList()
        setHomeLocation()
        return binding.root
    }

    private fun setMemberCode(code: String) {
        if (code.isEmpty()) {
            binding.edtCode.setText("00000")
        } else if (code.length == 1) {
            binding.edtCode.setText("0000${code}")
        } else if (code.length == 2) {
            binding.edtCode.setText("000${code}")
        } else if (code.length == 3) {
            binding.edtCode.setText("00${code}")
        } else if (code.length == 4) {
            binding.edtCode.setText("0${code}")
        } else {
            binding.edtCode.setText(code)
        }
    }

    private fun setHomeLocation() {
        if (!member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()) {
            ProfileDetailActivity.cur_lat.observeForever {
                if (ProfileDetailActivity.cur_lat.value != null && ProfileDetailActivity.cur_lng.value != null) {
                    var dist = EasyWayLocation.calculateDistance(member.homeLat.toDouble(), member.homeLng.toDouble(), ProfileDetailActivity.cur_lat.value!!.toDouble(), ProfileDetailActivity.cur_lng.value!!.toDouble())
                    dist /= 1000
                    binding.tvDistance.text = String.format("%.2f KM", dist)
                }
            }
            ProfileDetailActivity.cur_lng.observeForever {
                if (ProfileDetailActivity.cur_lat.value != null && ProfileDetailActivity.cur_lng.value != null) {
                    var dist = EasyWayLocation.calculateDistance(member.homeLat.toDouble(), member.homeLng.toDouble(), ProfileDetailActivity.cur_lat.value!!.toDouble(), ProfileDetailActivity.cur_lng.value!!.toDouble())
                    dist /= 1000
                    binding.tvDistance.text = String.format("%.2f KM", dist)
                }
            }
        } else {
            binding.tvDistance.text = "Home"
        }
    }

    private fun getEmailText(email: String): String {
        var str = ""
        if (email.isNotEmpty()) {
            str = email.reversed()
            for (char in str) {
                if (char == '\u200b') {
                    str = str.substring(1, str.length)
                } else {
                    break
                }
            }
            Log.d("Email", "str: " + str)
        }
        return str.reversed()
    }

    fun getSaveData(jsonObject: JSONObject) {

        try {
            jsonObject.put(getString(R.string.member_code), binding.edtCode.text?.trim())
            jsonObject.put(getString(R.string.first_name), binding.fname.text.trim())
            jsonObject.put(getString(R.string.father_name), binding.edtFather.text.trim())
            jsonObject.put(getString(R.string.mother_name), binding.edtMother.text.trim())
            jsonObject.put(getString(R.string.mobile), binding.edtMobile.text.trim())
            jsonObject.put(getString(R.string.email_address), getEmailText(binding.edtEmail.text.toString().trim()))
            jsonObject.put(getString(R.string.address), binding.edtAddr.text.trim())
            jsonObject.put(getString(R.string.gender), binding.spGender.text)
            jsonObject.put(getString(R.string.area), binding.edtArea.text.trim())
            jsonObject.put(getString(R.string.pincode), binding.edtPincode.text.trim())
            jsonObject.put(getString(R.string.relation_id), profileDetailViewModel.selectedRelationId)
            jsonObject.put(getString(R.string.sub_cast_id), profileDetailViewModel.selectedLastNameId)
            jsonObject.put(getString(R.string.state_id), profileDetailViewModel.selectedStateId)
            jsonObject.put(getString(R.string.city_id), profileDetailViewModel.selectedCityId)
            if (member.id.isNullOrEmpty()) {
                jsonObject.put(getString(R.string.profile_password), binding.edtPassword.text.trim())
                jsonObject.put(getString(R.string.confPin), binding.edtCpassword.text.trim())
            }

            if (binding.chkRented.isChecked) {
                jsonObject.put(getString(R.string.is_rented), 1)
            } else {
                jsonObject.put(getString(R.string.is_rented), 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setMemberRelation() = Coroutines.main {
        if (member.relationId.isNotEmpty()) {
            if (!member.relationId.equals("0")) {
                profileDetailViewModel.selectedRelationId = Integer.parseInt(member.relationId)
                profileDetailViewModel.relationName.await().observeForever {
                    binding.spRelation.setText(it)
                }
            } else {
                binding.spRelation.setText(resources.getString(R.string.Family_Head))
            }
        }
    }

    private fun setMemberLastname() = Coroutines.main {
        if (member.subCastId.isNotEmpty()) {
            profileDetailViewModel.selectedLastNameId = Integer.parseInt(member.subCastId)
            val lname = profileDetailViewModel.getLastNameById(Integer.parseInt(member.subCastId))
            binding.spLastname.setText(lname)
        }
    }

    private fun setMemberState() = Coroutines.main {
        if (member.stateId.isNotEmpty()) {
            profileDetailViewModel.selectedStateId = Integer.parseInt(member.stateId)
            val name = profileDetailViewModel.getstateNameById(Integer.parseInt(member.stateId))
            binding.spState.setText(name)
        }
    }

    private fun setMemberCity() = Coroutines.main {
        if (!member.cityId.isNullOrEmpty()) {
            profileDetailViewModel.selectedCityId = Integer.parseInt(member.cityId)
            profileDetailViewModel.cityName.await().observeForever {
                binding.spCity.setText(it)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Utility.hideKeyboard(activity)
    }

    private fun getMasterList() = Coroutines.main {

        val lstGender = arrayOf("Male", "Female")
        binding.spGender.setItems(lstGender)
        binding.spGender.setExpandTint(R.color.black)

        profileDetailViewModel.lstRelationName.await().observe(viewLifecycleOwner, Observer {
            if (member.relationId != "0") {
                if (it.isNotEmpty()) {
                    binding.spRelation.setItems(it.subList(1, it.size).toTypedArray())
                    binding.spRelation.setExpandTint(R.color.black)
                }

            }
        })

        profileDetailViewModel.lstLastName.await().observe(viewLifecycleOwner, Observer {
            binding.spLastname.setItems(it.toTypedArray())
            binding.spLastname.setExpandTint(R.color.black)
        })

        profileDetailViewModel.lstStateName.await().observe(viewLifecycleOwner, Observer {
            binding.spState.setItems(it.toTypedArray())
            binding.spState.setExpandTint(R.color.black)
        })

        val cities = profileDetailViewModel.getCityNamebyState(profileDetailViewModel.selectedStateId)
        binding.spCity.setItems(cities.toTypedArray())
        binding.spCity.setExpandTint(R.color.black)

    }

    override fun getScanResult(response: SmartFilterResponse) {
    }

    override fun getUpdateOrAddResult(response: UpdateProfileResponse) {
        Utility.hideSweetProgress()
        val updatedMem = response.member
        member.homeLat = ProfileDetailActivity.cur_lat.value.toString()
        member.homeLng = ProfileDetailActivity.cur_lng.value.toString()
        val percentage = Utility.calculatePercentage(updatedMem)
        setPercentage(percentage)
        if (updatedMem.id == member.id) {
            Guru.putString(getString(R.string.loginMember), Gson().toJson(updatedMem))

        }
        if (ProfileDetailActivity.cur_lat.value != null && ProfileDetailActivity.cur_lng.value != null) {
            var dist = EasyWayLocation.calculateDistance(member.homeLat.toDouble(), member.homeLng.toDouble(), ProfileDetailActivity.cur_lat.value!!.toDouble(), ProfileDetailActivity.cur_lng.value!!.toDouble())
            dist /= 1000
            binding.tvDistance.text = String.format("%.2f KM", dist)
        }
        Utility.displaySnackBarWithBottomMargin(binding.llMain, "Home location updated!")
    }

    override suspend fun getFailure(message: String) {
        Utility.displaySnackBarWithBottomMargin(binding.llMain, message)
    }
}

