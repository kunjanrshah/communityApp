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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.easywaylocation.EasyWayLocation
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.ProfileDetailActivity.Companion.setPercentage
import com.krs.community.databinding.FragmentMainDetailsBinding
import com.krs.community.listeners.EditMemberListener
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
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
    private val profileDetailViewModelFactory: ProfileDetailViewModelFactory by instance()
    var numberOfLines=5
    override val kodein by kodein()
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_details, container, false)
        profileDetailViewModel = ViewModelProviders.of(this, profileDetailViewModelFactory).get(ProfileDetailViewModel::class.java)
        profileDetailViewModel.mEditMemberListener=this
        member = arguments?.getSerializable(getString(R.string.member)) as Member
        val loginMember=Guru.getString(getString(R.string.loginUser),"")
        val loginMem= Gson().fromJson(loginMember,Member::class.java)
        if(member.id.isNullOrEmpty() || member.id == loginMem.id || member.headId == loginMem.id){

            binding.fname.isFocusable=true
            binding.edtArea.isFocusable=true
            binding.edtAddr.isFocusable=true
            binding.edtPincode.isFocusable=true
            binding.edtFather.isFocusable=true
            binding.edtMother.isFocusable=true
            binding.edtEmail.isFocusable=true
            binding.edtMobile.isFocusable=true
            binding.edtCode.setText(member.memberCode)

            binding.spState.isEnabled=true
            binding.spCity.isEnabled=true
            binding.spRelation.isEnabled=true
            binding.spLastname.isEnabled=true
            binding.spGender.isEnabled=true

            binding.chkRented.isFocusable=false
            binding.chkRented.isClickable=true

            binding.llMcode.visibility=View.VISIBLE

            member.stateId=loginMem.stateId
            member.cityId=loginMem.cityId
            member.city=loginMem.city
            member.area=loginMem.area
            member.pincode=loginMem.pincode
            member.address=loginMem.address
            member.isRented=loginMem.isRented
        }else{
            binding.fname.isFocusable=false
            binding.edtEmail.isFocusable=false
            binding.edtMobile.isFocusable=false
            binding.edtMother.isFocusable=false
            binding.edtFather.isFocusable=false
            binding.edtArea.isFocusable=false
            binding.edtAddr.isFocusable=false
            binding.edtPincode.isFocusable=false
            binding.edtCode.isFocusable=false
            binding.edtCode.isClickable=false

            binding.spGender.isEnabled=false
            binding.spLastname.isEnabled=false
            binding.spRelation.isEnabled=false
            binding.spState.isEnabled=false
            binding.spCity.isEnabled=false
            binding.chkRented.isFocusable=false
            binding.chkRented.isClickable=false

            if(member.memberCode.isNullOrEmpty()){
                binding.llMcode.visibility=View.GONE
            }else{
                binding.llMcode.visibility=View.VISIBLE
                binding.edtCode.setText(member.memberCode)
            }
        }

        binding.fname.setText(member.firstName)
        binding.edtFather.setText(member.fatherName)
        binding.edtMother.setText(member.motherName)
        binding.edtMobile.setText(member.mobile)
        binding.edtEmail.setText(member.emailAddress)
        binding.edtEmail.setFilters(arrayOf(Utility.filter));
        binding.edtAddr.setText(member.address)
        binding.spGender.setText(member.gender)
        binding.edtArea.setText(member.area)
        binding.edtPincode.setText(member.pincode)
        binding.chkRented.isChecked = member.isRented.equals("1")

        binding.llHome.setOnClickListener {
            SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Home Location")
                    .setContentText("With Google Map")
                    .setConfirmText("Set")
                    .setCancelText("View")
                    .setCustomImage(R.drawable.ic_app)
                    .setConfirmClickListener {
                        it.dismiss()
                        if(member.headId=="0" && !member.id.isNullOrEmpty()){
                            val jsonObject = JSONObject()
                            jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id),""))
                            jsonObject.put(getString(R.string.id), member.id)
                            jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token),""))
                            jsonObject.put(getString(R.string.home_lat), ProfileDetailActivity.cur_lat.value)
                            jsonObject.put(getString(R.string.home_lng), ProfileDetailActivity.cur_lng.value)

                            val profile = JsonParser().parse(jsonObject.toString()) as JsonObject

                            Utility.startSweetProgress(activity, "Updating your home location", "Please wait...")
                            profileDetailViewModel.updateProfile(profile, true)
                        }else{
                            Utility.displaySnackBarWithBottomMargin(ll_main,"Only Family Head Set the Home Location")
                        }


                    }
                    .setCancelClickListener {
                        it.dismiss()
                        Utility.showDirections(activity,member.homeLat.toDouble(),member.homeLng.toDouble(),"${member.firstName}'s Home")
                    }
                    .show()
        }

        binding.edtAddr.addTextChangedListener(object:TextWatcher{
            private var text: String? = null
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lineCount: Int = binding.edtAddr.getLineCount()
                if (lineCount > numberOfLines) {
                    binding.edtAddr.setText(text)
                }
            }
        })

        binding.edtAddr.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val editTextLineCount: Int = (v as EditText).getLineCount()
                if (editTextLineCount >= numberOfLines) return@OnKeyListener true
            }
            false
        })

        binding.spState.setOnItemClickListener {
            profileDetailViewModel.selectedStateId = profileDetailViewModel.lstStateId[it]
            Coroutines.main {
               val cities=  profileDetailViewModel.getCityNamebyState(profileDetailViewModel.selectedStateId)
                binding.spCity.clear()
                binding.spCity.setText("Select")
                profileDetailViewModel.selectedCityId=0
                binding.spCity.setItems(cities.toTypedArray())
                binding.spCity.setExpandTint(R.color.black)
            }
        }

        binding.spCity.setOnItemClickListener {
            profileDetailViewModel.selectedCityName = binding.spCity.text.toString().trim()
            Coroutines.main {
                profileDetailViewModel.cityId.await().observe(this, Observer {
                    profileDetailViewModel.selectedCityId = it
                })
            }
        }

        binding.spRelation.setOnItemClickListener {
            profileDetailViewModel.selectedRelationId = profileDetailViewModel.lstRelationId[it]
        }

        binding.spLastname.setOnItemClickListener {
            profileDetailViewModel.selectedLastNameId = profileDetailViewModel.lstLastNameId[it]
        }

        setMemberRelation()
        setMemberLastname()
        setMemberState()
        setMemberCity()
        getMasterList()
        setHomeLocation()
        return binding.root
    }

    private fun setHomeLocation() {
        if(!member.homeLat.isNullOrEmpty() && !member.homeLng.isNullOrEmpty()){
            ProfileDetailActivity.cur_lat.observeForever {
                if(ProfileDetailActivity.cur_lat.value!=null && ProfileDetailActivity.cur_lng.value!=null){
                    var dist=EasyWayLocation.calculateDistance(member.homeLat.toDouble(),member.homeLng.toDouble(),ProfileDetailActivity.cur_lat.value!!.toDouble(),ProfileDetailActivity.cur_lng.value!!.toDouble())
                    dist /= 1000
                    binding.tvDistance.text=String.format("%.2f KM",dist)
                }
            }
            ProfileDetailActivity.cur_lng.observeForever {
                if(ProfileDetailActivity.cur_lat.value!=null && ProfileDetailActivity.cur_lng.value!=null){
                    var dist=EasyWayLocation.calculateDistance(member.homeLat.toDouble(),member.homeLng.toDouble(),ProfileDetailActivity.cur_lat.value!!.toDouble(),ProfileDetailActivity.cur_lng.value!!.toDouble())
                    dist /= 1000
                    binding.tvDistance.text=String.format("%.2f KM",dist)
                }
            }
        }else{
            binding.tvDistance.text="Home"
        }
    }


    fun getSaveData(jsonObject:JSONObject){
        try {
            jsonObject.put(getString(R.string.member_code),binding.edtCode.text.trim())
            jsonObject.put(getString(R.string.first_name),binding.fname.text.trim())
            jsonObject.put(getString(R.string.father_name),binding.edtFather.text.trim())
            jsonObject.put(getString(R.string.mother_name),binding.edtMother.text.trim())
            jsonObject.put(getString(R.string.mobile),binding.edtMobile.text.trim())
            jsonObject.put(getString(R.string.email_address),binding.edtEmail.text.trim())
            jsonObject.put(getString(R.string.address),binding.edtAddr.text.trim())
            jsonObject.put(getString(R.string.gender),binding.spGender.text)
            jsonObject.put(getString(R.string.area),binding.edtArea.text.trim())
            jsonObject.put(getString(R.string.pincode),binding.edtPincode.text.trim())
            jsonObject.put(getString(R.string.relation_id),profileDetailViewModel.selectedRelationId)
            jsonObject.put(getString(R.string.sub_cast_id),profileDetailViewModel.selectedLastNameId)
            jsonObject.put(getString(R.string.state_id),profileDetailViewModel.selectedStateId)
            jsonObject.put(getString(R.string.city_id),profileDetailViewModel.selectedCityId)
            if(binding.chkRented.isChecked){
                jsonObject.put(getString(R.string.is_rented),1)
            }else{
                jsonObject.put(getString(R.string.is_rented),0)
            }    
        }catch (e:Exception){
            e.printStackTrace()
        }
        
    }

    private fun setMemberRelation() = Coroutines.main {
        if(member.relationId.isNotEmpty()){
            if(!member.relationId.equals("0")){
                profileDetailViewModel.selectedRelationId = Integer.parseInt(member.relationId)
                profileDetailViewModel.relationName.await().observeForever {
                    binding.spRelation.setText(it)
                }
            }else{
                binding.spRelation.setText(resources.getString(R.string.Family_Head))
            }
        }
    }

    private fun setMemberLastname() = Coroutines.main {
     if(member.subCastId.isNotEmpty()){
         profileDetailViewModel.selectedLastNameId = Integer.parseInt(member.subCastId)
         profileDetailViewModel.lastName.await().observeForever {
             binding.spLastname.setText(it)
         }
     }
    }

    private fun setMemberState() = Coroutines.main {
     if(member.stateId.isNotEmpty()){
         profileDetailViewModel.selectedStateId = Integer.parseInt(member.stateId)
         profileDetailViewModel.stateName.await().observeForever {
             binding.spState.setText(it)
         }
     }
    }

    private fun setMemberCity() = Coroutines.main {
     if(!member.cityId.isNullOrEmpty()){
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

        profileDetailViewModel.lstRelationName.await().observe(this, Observer {
           if(member.relationId != "0"){
               if(it.isNotEmpty()){
                   binding.spRelation.setItems(it.subList(1,it.size).toTypedArray())
                   binding.spRelation.setExpandTint(R.color.black)
               }

           }
        })
        profileDetailViewModel.relationIds.await().observe(this, Observer {
          if(it.isNotEmpty()){
              profileDetailViewModel.lstRelationId = it.subList(1,it.size)
          }
        })

        profileDetailViewModel.lstLastName.await().observe(this, Observer {
            binding.spLastname.setItems(it.toTypedArray())
            binding.spLastname.setExpandTint(R.color.black)
        })
        profileDetailViewModel.lastNameIds.await().observe(this, Observer {
            profileDetailViewModel.lstLastNameId = it
        })

        profileDetailViewModel.lstStateName.await().observe(this, Observer {
            binding.spState.setItems(it.toTypedArray())
            binding.spState.setExpandTint(R.color.black)
        })

        profileDetailViewModel.stateIds.await().observe(this, Observer {
            profileDetailViewModel.lstStateId = it
        })
        val cities= profileDetailViewModel.getCityNamebyState(profileDetailViewModel.selectedStateId)
        binding.spCity.setItems(cities.toTypedArray())
        binding.spCity.setExpandTint(R.color.black)

    }

    override fun getScanResult(response: SmartFilterResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getUpdateOrAddResult(response: UpdateProfileResponse) {
        Utility.hideSweetProgress()
        val updatedMem = response.member
        member.homeLat=ProfileDetailActivity.cur_lat.value.toString()
        member.homeLng=ProfileDetailActivity.cur_lng.value.toString()
        val percentage = Utility.calculatePercentage(updatedMem)
        setPercentage(percentage)
        if (updatedMem.headId == "0") {
            Guru.putString(getString(R.string.loginUser), Gson().toJson(updatedMem))
            Guru.putString(getString(R.string.user_mobile), updatedMem.mobile)
        }
        if(ProfileDetailActivity.cur_lat.value!=null && ProfileDetailActivity.cur_lng.value!=null){
            var dist=EasyWayLocation.calculateDistance(member.homeLat.toDouble(),member.homeLng.toDouble(),ProfileDetailActivity.cur_lat.value!!.toDouble(),ProfileDetailActivity.cur_lng.value!!.toDouble())
            dist /= 1000
            binding.tvDistance.text=String.format("%.2f KM",dist)
        }
        Utility.displaySnackBarWithBottomMargin(binding.llMain, "Home location updated!")
    }

    override suspend fun getFailure(message: String) {
        Utility.displaySnackBarWithBottomMargin(binding.llMain, message)
    }
}

