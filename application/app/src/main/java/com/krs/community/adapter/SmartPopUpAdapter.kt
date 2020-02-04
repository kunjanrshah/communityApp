package com.krs.community.adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.github.squti.guru.Guru
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.fragments.SmartFilterResult
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility

import com.krs.community.viewmodel.ProfileDetailViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SmartPopUpAdapter(private val _context: Context, adapter: SmartFilterAdapter,
                        private val mapChildValues: HashMap<String, String>,
                        var profileDetailViewModel: ProfileDetailViewModel) : BaseAdapter() {

    private var mICloseDialog: ICloseDialog = adapter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        val viewHolder: PopUpViewHolder
        val mInflater = _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.list_smart_popup, null)
            viewHolder = PopUpViewHolder(convertView)
            convertView.tag = viewHolder
            for ((key, value1) in mapChildValues) {
                val value = value1.trim { it <= ' ' }
                println("$key = $value1")
                if (key.equals(_context.resources.getString(R.string.ss_family_code), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llFamilyCode.visibility = View.VISIBLE
                        viewHolder.edtFamilyCode.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_head_name), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llHead.visibility = View.VISIBLE
                        viewHolder.edtHeadName.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_mem_name), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llMember.visibility = View.VISIBLE
                        viewHolder.edtMember.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_surname), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_surname), ignoreCase = true)) {

                        Coroutines.main {
                            profileDetailViewModel.lstLastName.await().observeForever {
                                viewHolder.spSurname.setItems(it.toTypedArray())
                                viewHolder.spSurname.setExpandTint(R.color.black)
                                viewHolder.spSurname.setText(value1)
                                viewHolder.llSurname.visibility = View.VISIBLE
                            }
                        }
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_samaj), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_samaj), ignoreCase = true)) {

                        Coroutines.main {
                            profileDetailViewModel.getLocalCommName.await().observeForever {
                                viewHolder.spSamaj.setItems(it.toTypedArray())
                                viewHolder.spSamaj.setExpandTint(R.color.black)
                                viewHolder.spSamaj.setText(value1)
                                viewHolder.llSamaj.visibility = View.VISIBLE
                            }
                        }
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_marital), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_marital), ignoreCase = true)) {
                        val lstMarital = _context.resources.getStringArray(R.array.marital)
                        viewHolder.spMarital.setItems(lstMarital)
                        viewHolder.spMarital.setExpandTint(R.color.black)
                        viewHolder.spMarital.setText(value1)
                        viewHolder.llMarital.visibility = View.VISIBLE
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_city), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_city), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstCityName.await().observeForever {
                                viewHolder.spCity.setItems(it.toTypedArray())
                                viewHolder.spCity.setExpandTint(R.color.black)
                                viewHolder.spCity.setText(value1)
                                viewHolder.llCity.visibility = View.VISIBLE
                            }
                        }
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_gender), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_gender), ignoreCase = true)) {
                        val lstGender = arrayOf("Male", "Female")
                        viewHolder.spGender.setItems(lstGender)
                        viewHolder.spGender.setExpandTint(R.color.black)
                        viewHolder.spGender.setText(value1)
                        viewHolder.llGender.visibility = View.VISIBLE
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_native), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_native), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstNativeName.await().observeForever {
                                viewHolder.spNative.setItems(it.toTypedArray())
                                viewHolder.spNative.setExpandTint(R.color.black)
                                viewHolder.spNative.setText(value1)
                                viewHolder.llNative.visibility = View.VISIBLE
                            }
                        }
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_minage), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.rangeAgeBar.setMinStartValue(value.toInt().toFloat()).apply()
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_maxage), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.rangeAgeBar.setMaxStartValue(value.toInt().toFloat()).apply()
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_email), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llEmail.visibility = View.VISIBLE
                        viewHolder.edtEmail.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_mobile), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llMobile.visibility = View.VISIBLE
                        viewHolder.edtMobile.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_local_add), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llLocalAdd.visibility = View.VISIBLE
                        viewHolder.edtLocalAdd.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_permanent_add), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llPermanentAdd.visibility = View.VISIBLE
                        viewHolder.edtPermanentAdd.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_chk_is_donor), ignoreCase = true)) {
                    if (value.equals("true", ignoreCase = true)) {
                        viewHolder.llIsdonor.visibility = View.VISIBLE
                        viewHolder.chkIsDonor.isChecked = true
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_chk_is_expired), ignoreCase = true)) {
                    if (value.equals("true", ignoreCase = true)) {
                        viewHolder.llIsexpired.visibility = View.VISIBLE
                        viewHolder.chkIsExpired.isChecked = true
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_chk_is_rented), ignoreCase = true)) {
                    if (value.equals("true", ignoreCase = true)) {
                        viewHolder.llIsrented.visibility = View.VISIBLE
                        viewHolder.chkIsRented.isChecked = true
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_pin_code), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llPincode.visibility = View.VISIBLE
                        viewHolder.edtPincode.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_area), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llArea.visibility = View.VISIBLE
                        viewHolder.edtArea.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_state), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_state), ignoreCase = true)) {

                        Coroutines.main {
                            profileDetailViewModel.lstStateName.await().observeForever {
                                viewHolder.spState.setItems(it.toTypedArray())
                                viewHolder.spState.setExpandTint(R.color.black)
                                viewHolder.llState.visibility = View.VISIBLE
                                viewHolder.spState.setText(value1)
                            }
                        }

                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_mosad), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llMosad.visibility = View.VISIBLE
                        viewHolder.edtMosad.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_education), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_education), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstEducationName.await().observeForever {
                                viewHolder.spEducation.setItems(it.toTypedArray())
                                viewHolder.spEducation.setExpandTint(R.color.black)
                                viewHolder.llEducation.visibility = View.VISIBLE
                                viewHolder.spEducation.setText(value1)
                            }
                        }

                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_gotra), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_gotra), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstGotraName.await().observeForever {
                                viewHolder.spGotra.setItems(it.toTypedArray())
                                viewHolder.spGotra.setExpandTint(R.color.black)
                                viewHolder.llGotra.visibility = View.VISIBLE
                                viewHolder.spGotra.setText(value1)
                            }
                        }
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_bg), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_bg), ignoreCase = true)) {

                        val lstBlood = _context.resources.getStringArray(R.array.bloodGroup)
                        viewHolder.spBg.setItems(lstBlood)
                        viewHolder.spBg.setExpandTint(R.color.black)
                        viewHolder.llBg.visibility = View.VISIBLE
                        viewHolder.spBg.setText(value1)

                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_office), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llOfficeAdd.visibility = View.VISIBLE
                        viewHolder.edtOffice.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_main_cat), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_catogory), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstBusinessCategoryName.await().observeForever {
                                viewHolder.spMainCat.setItems(it.toTypedArray())
                                viewHolder.spMainCat.setExpandTint(R.color.black)
                                viewHolder.llCat.visibility = View.VISIBLE
                                viewHolder.spMainCat.setText(value1)
                            }
                        }

                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_sub_cat), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_sub_cat), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstBusinessSubCategoryName.await().observeForever {
                                viewHolder.spSubCat.setItems(it.toTypedArray())
                                viewHolder.spSubCat.setExpandTint(R.color.black)
                                viewHolder.llSubCat.visibility = View.VISIBLE
                                viewHolder.spSubCat.setText(value1)
                            }
                        }

                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_occupation), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_Occupation), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstOccupationName.await().observeForever {
                                viewHolder.spOccupation.setItems(it.toTypedArray())
                                viewHolder.spOccupation.setExpandTint(R.color.black)
                                viewHolder.llOccupation.visibility = View.VISIBLE
                                viewHolder.spOccupation.setText(value1)
                            }
                        }

                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_activity), ignoreCase = true)) {
                    if (value.isNotEmpty() && !value.equals(_context.getString(R.string.ss_activity), ignoreCase = true)) {
                        Coroutines.main {
                            profileDetailViewModel.lstActivityName.await().observeForever {
                                viewHolder.spActivity.setItems(it.toTypedArray())
                                viewHolder.spActivity.setExpandTint(R.color.black)
                                viewHolder.llActivity.visibility = View.VISIBLE
                                viewHolder.spActivity.setText(value1)
                            }
                        }

                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_birth_time), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llBtime.visibility = View.VISIBLE
                        viewHolder.edtBtime.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_sp_bplace), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llBplace.visibility = View.VISIBLE
                        viewHolder.edtBplace.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_height_meter), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llHeight.visibility = View.VISIBLE
                        viewHolder.edtHeightMeter.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_weight_kg), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llWeight.visibility = View.VISIBLE
                        viewHolder.edtWeightKg.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_chk_is_shani), ignoreCase = true)) {
                    if (value.equals("true", ignoreCase = true)) {
                        viewHolder.llIsshani.visibility = View.VISIBLE
                        viewHolder.chkIsShani.isChecked = true
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_chk_is_mangal), ignoreCase = true)) {
                    if (value.equals("true", ignoreCase = true)) {
                        viewHolder.llIsmangal.visibility = View.VISIBLE
                        viewHolder.chkIsMangal.isChecked = true
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_chk_is_spect), ignoreCase = true)) {
                    if (value.equals("true", ignoreCase = true)) {
                        viewHolder.llIsspect.visibility = View.VISIBLE
                        viewHolder.chkIsSpect.isChecked = true
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_bdate), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llBirthDate.visibility = View.VISIBLE
                        viewHolder.tvBirthDate.text = value1
                    }
                }else if (key.equals(_context.resources.getString(R.string.ss_edt_mdate), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llMdate.visibility = View.VISIBLE
                        viewHolder.tvMdate.text = value1
                    }
                }

                else if (key.equals(_context.resources.getString(R.string.ss_edt_created), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llCreated.visibility = View.VISIBLE
                        viewHolder.tvCreated.setText(value1)
                    }
                }

                else if (key.equals(_context.resources.getString(R.string.ss_edt_created), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llCreated.visibility = View.VISIBLE
                        viewHolder.tvCreated.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_edt_updated), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.llUpdated.visibility = View.VISIBLE
                        viewHolder.tvUpdated.setText(value1)
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_minUpdate), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.rangeUpdationBar.setMinStartValue(value.toInt().toFloat()).apply()
                    }
                } else if (key.equals(_context.resources.getString(R.string.ss_maxUpdate), ignoreCase = true)) {
                    if (value.isNotEmpty()) {
                        viewHolder.rangeUpdationBar.setMaxStartValue(value.toInt().toFloat()).apply()
                    }
                }
            }
            viewHolder.flexboxLayout.flexDirection = FlexDirection.ROW
            val view = viewHolder.flexboxLayout.getChildAt(0)
            val lp = view.layoutParams as FlexboxLayout.LayoutParams
            lp.flexGrow = 1.0f
            lp.alignSelf = AlignItems.FLEX_START
            view.layoutParams = lp
            // set listener
            viewHolder.rangeAgeBar.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                if (minValue.toInt() > 0 || maxValue.toInt() < 100) {
                    viewHolder.llAge.visibility = View.VISIBLE
                    viewHolder.tvMin.text = "Age $minValue"
                    viewHolder.tvMax.text = "Age $maxValue"
                }
            }
            // set final value listener
            viewHolder.rangeAgeBar.setOnRangeSeekbarFinalValueListener { minValue: Number, maxValue: Number -> Log.d("CRS=>", "$minValue : $maxValue") }
            // set listener
            viewHolder.rangeUpdationBar.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                if (minValue.toInt() > 0 || maxValue.toInt() < 100) {
                    viewHolder.llPercentage.visibility = View.VISIBLE
                    viewHolder.tvMinPer.text = "$minValue%"
                    viewHolder.tvMaxPer.text = "$maxValue%"
                }
            }
            // set final value listener
            viewHolder.rangeAgeBar.setOnRangeSeekbarFinalValueListener { minValue: Number, maxValue: Number -> Log.d("CRS=>", "$minValue : $maxValue") }
        } else {
            viewHolder = convertView.tag as PopUpViewHolder
        }
        viewHolder.imgCodeClose.setOnClickListener { v: View? -> viewHolder.llFamilyCode.visibility = View.GONE }
        viewHolder.imgAgeClose.setOnClickListener { v: View? -> viewHolder.llAge.visibility = View.GONE }
        viewHolder.imgHeadClose.setOnClickListener { v: View? -> viewHolder.llHead.visibility = View.GONE }
        viewHolder.imgMemberClose.setOnClickListener { v: View? -> viewHolder.llMember.visibility = View.GONE }
        viewHolder.imgSurnameClose.setOnClickListener { v: View? -> viewHolder.llSurname.visibility = View.GONE }
        viewHolder.imgSamajClose.setOnClickListener { v: View? -> viewHolder.llSamaj.visibility = View.GONE }
        viewHolder.imgGenderClose.setOnClickListener { v: View? -> viewHolder.llGender.visibility = View.GONE }
        viewHolder.imgMaritalClose.setOnClickListener { v: View? -> viewHolder.llMarital.visibility = View.GONE }
        viewHolder.imgNativeClose.setOnClickListener { v: View? -> viewHolder.llNative.visibility = View.GONE }
        viewHolder.imgCityClose.setOnClickListener { v: View? -> viewHolder.llCity.visibility = View.GONE }
        viewHolder.imgEmailClose.setOnClickListener { v: View? -> viewHolder.llEmail.visibility = View.GONE }
        viewHolder.imgMobileClose.setOnClickListener { v: View? -> viewHolder.llMobile.visibility = View.GONE }
        viewHolder.imgLocalAddClose.setOnClickListener { v: View? -> viewHolder.llLocalAdd.visibility = View.GONE }
        viewHolder.imgPermanentAddClose.setOnClickListener { v: View? -> viewHolder.llPermanentAdd.visibility = View.GONE }
        viewHolder.imgPincodeClose.setOnClickListener { v: View? -> viewHolder.llPincode.visibility = View.GONE }
        viewHolder.imgAreaClose.setOnClickListener { v: View? -> viewHolder.llArea.visibility = View.GONE }
        viewHolder.imgStateClose.setOnClickListener { v: View? -> viewHolder.llState.visibility = View.GONE }
        viewHolder.imgBdateClose.setOnClickListener { v: View? -> viewHolder.llBirthDate.visibility = View.GONE }
        viewHolder.imgMdateClose.setOnClickListener { v: View? -> viewHolder.llMdate.visibility = View.GONE }
        viewHolder.imgMosadClose.setOnClickListener { v: View? -> viewHolder.llMosad.visibility = View.GONE }
        viewHolder.imgEducationClose.setOnClickListener { v: View? -> viewHolder.llEducation.visibility = View.GONE }
        viewHolder.imgGotraClose.setOnClickListener { v: View? -> viewHolder.llGotra.visibility = View.GONE }
        viewHolder.imgBgClose.setOnClickListener { v: View? -> viewHolder.llBg.visibility = View.GONE }
        viewHolder.imgRentedClose.setOnClickListener { v: View? -> viewHolder.llIsrented.visibility = View.GONE }
        viewHolder.imgExpiredClose.setOnClickListener { v: View? -> viewHolder.llIsexpired.visibility = View.GONE }
        viewHolder.imgDonorClose.setOnClickListener { v: View? -> viewHolder.llIsdonor.visibility = View.GONE }
        viewHolder.imgOaddressClose.setOnClickListener { v: View? -> viewHolder.llOfficeAdd.visibility = View.GONE }
        viewHolder.imgSubCatClose.setOnClickListener { v: View? -> viewHolder.llSubCat.visibility = View.GONE }
        viewHolder.imgCatClose.setOnClickListener { v: View? -> viewHolder.llCat.visibility = View.GONE }
        viewHolder.imgOccuClose.setOnClickListener { v: View? -> viewHolder.llOccupation.visibility = View.GONE }
        viewHolder.imgActivityClose.setOnClickListener { v: View? -> viewHolder.llActivity.visibility = View.GONE }
        viewHolder.imgBtimeClose.setOnClickListener { v: View? -> viewHolder.llBtime.visibility = View.GONE }
        viewHolder.imgBplaceClose.setOnClickListener { v: View? -> viewHolder.llBplace.visibility = View.GONE }
        viewHolder.imgSpectClose.setOnClickListener { v: View? -> viewHolder.llIsspect.visibility = View.GONE }
        viewHolder.imgShaniClose.setOnClickListener { v: View? -> viewHolder.llIsshani.visibility = View.GONE }
        viewHolder.imgMangalClose.setOnClickListener { v: View? -> viewHolder.llIsmangal.visibility = View.GONE }
        viewHolder.imgHeightClose.setOnClickListener { v: View? -> viewHolder.llHeight.visibility = View.GONE }
        viewHolder.imgWeightClose.setOnClickListener { v: View? -> viewHolder.llWeight.visibility = View.GONE }
        viewHolder.imgPerClose.setOnClickListener { v: View? -> viewHolder.llPercentage.visibility = View.GONE }
        viewHolder.imgUpdatedClose.setOnClickListener { v: View? -> viewHolder.llUpdated.visibility = View.GONE }
        viewHolder.imgCreatedClose.setOnClickListener { v: View? -> viewHolder.llCreated.visibility = View.GONE }
        viewHolder.chkSave.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                viewHolder.edtFilterName.visibility = View.VISIBLE
            } else {
                viewHolder.edtFilterName.visibility = View.INVISIBLE
            }
        }
        viewHolder.btnApply.setOnClickListener { v: View? ->

            val filter = getValues(viewHolder).toString()
            if (filter.isNotEmpty()) {
                mICloseDialog.PopupClose()
                val filterResult = SmartFilterResult()
                val mBundle = Bundle()
                mBundle.putString(_context.getString(R.string.filter_values), filter)
                filterResult.arguments = mBundle
                Utility.movetoFragment(_context as Activity, filterResult)

                if(viewHolder.chkSave.isChecked && viewHolder.edtFilterName.text.trim().isNotEmpty()){
                    val jsonArray:JSONArray
                    val listFilter= Guru.getString(_context.getString(R.string.list_filter),"")
                    if(listFilter.isNullOrEmpty()){
                        jsonArray= JSONArray()
                    }else{
                        jsonArray= JSONArray(listFilter)
                    }
                    val jsonObject= JSONObject()
                    jsonObject.put(_context.getString(R.string.name_filter),viewHolder.edtFilterName.text.trim())
                    jsonObject.put(_context.getString(R.string.value_filter),filter)
                    jsonArray.put(jsonObject)
                    Guru.putString(_context.getString(R.string.list_filter),jsonArray.toString())
                }else{
                    Toast.makeText(_context, "Filter not saved!", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(_context, "No Filter found!", Toast.LENGTH_SHORT).show()
            }
        }
        return convertView!!
    }

    private fun getValues(viewHolder: PopUpViewHolder): JSONObject = runBlocking {
        val lstValues = JSONObject()
        try {
            if (viewHolder.llEducation.isShown) {
                val job1 = async {
                    val name = viewHolder.spEducation.text.toString().trim { it <= ' ' }
                    val id = profileDetailViewModel.getEducationIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_education), id)
                }
                job1.await()
            }
            if (viewHolder.llGotra.isShown) {
                val job1 = async {
                    val name = viewHolder.spGotra.text.toString().trim { it <= ' ' }
                    val id = profileDetailViewModel.getGotraIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_gotra), id)
                }
                job1.await()
            }
            if (viewHolder.llCat.isShown) {
                val job1 = async {
                    val name = viewHolder.spMainCat.text.toString().trim { it <= ' ' }
                    val id = profileDetailViewModel.getCategoryIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_main_cat), id)
                }
                job1.await()
            }
            if (viewHolder.llSubCat.isShown) {
                val job1 = async {
                    val name = viewHolder.spSubCat.text.toString().trim { it <= ' ' }
                    val id = profileDetailViewModel.getSubCategoryIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_sub_cat), id)
                }
                job1.await()
            }
            if (viewHolder.llOccupation.isShown) {
                val job1 = async {
                    val name = viewHolder.spOccupation.text.toString().trim { it <= ' ' }
                    val id = profileDetailViewModel.getOccupationIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_occupation), id)
                }
                job1.await()
            }
            if (viewHolder.llActivity.isShown) {
                val job1 = async {
                    val name = viewHolder.spActivity.text.toString().trim { it <= ' ' }
                    val id = profileDetailViewModel.getActivityIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_activity), id)
                }
                job1.await()
            }

            if (viewHolder.llSurname.isShown) {
                val lname = viewHolder.spSurname.text.toString().trim { it <= ' ' }
                val job1 = async {
                    val id = profileDetailViewModel.getIdByLastName(lname)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_surname), id)
                }
                job1.await()
            }
            if (viewHolder.llSamaj.isShown) {
                val local = viewHolder.spSamaj.text.toString().trim { it <= ' ' }
                val job1 = async {
                    val id = profileDetailViewModel.getLocalCommunityId(local)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_samaj), id)
                }
                job1.await()
            }
            if (viewHolder.llNative.isShown) {
                val name = viewHolder.spNative.text.toString().trim { it <= ' ' }
                val job1 = async {
                    val id = profileDetailViewModel.getNativeIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_native), id)
                }
                job1.await()
            }
            if (viewHolder.llCity.isShown) {
                val name = viewHolder.spCity.text.toString().trim { it <= ' ' }
                val job1 = async {
                    val city = profileDetailViewModel.getCityIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_city), city)
                }
                job1.await()
            }
            if (viewHolder.llState.isShown) {
                val job1 = async {
                    val name = viewHolder.spState.text.toString().trim { it <= ' ' }
                    val id = profileDetailViewModel.getstateIdByName(name)
                    lstValues.put(_context.resources.getString(R.string.ss_sp_state), id)
                }
                job1.await()
            }

            if (viewHolder.llFamilyCode.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_family_code), viewHolder.edtFamilyCode.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llHead.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_head_name), viewHolder.edtHeadName.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llMember.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_mem_name), viewHolder.edtMember.text.toString().trim { it <= ' ' })
            }

            if (viewHolder.llGender.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_sp_gender), viewHolder.spGender.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llMarital.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_sp_marital), viewHolder.spMarital.text.toString().trim { it <= ' ' })
            }

            if (viewHolder.llAge.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_minAge), viewHolder.rangeAgeBar.selectedMinValue.toString().trim { it <= ' ' })
                lstValues.put(_context.resources.getString(R.string.ss_maxAge), viewHolder.rangeAgeBar.selectedMaxValue.toString().trim { it <= ' ' })
            }
            if (viewHolder.llEmail.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_email), viewHolder.edtEmail.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llMobile.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_mobile), viewHolder.edtMobile.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llLocalAdd.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_local_add), viewHolder.edtLocalAdd.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llPermanentAdd.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_permanent_add), viewHolder.edtPermanentAdd.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llPincode.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_pin_code), viewHolder.edtPincode.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llArea.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_area), viewHolder.edtArea.text.toString().trim { it <= ' ' })
            }

            if (viewHolder.llBirthDate.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_bdate), viewHolder.tvBirthDate.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llMdate.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_mdate), viewHolder.tvMdate.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llMosad.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_mosad), viewHolder.edtMosad.text.toString().trim { it <= ' ' })
            }

            if (viewHolder.llBg.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_sp_bg), viewHolder.spBg.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llIsdonor.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_chk_is_donor), viewHolder.chkIsDonor.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llIsrented.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_chk_is_rented), viewHolder.chkIsRented.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llIsexpired.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_chk_is_expired), viewHolder.chkIsExpired.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llOfficeAdd.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_office), viewHolder.edtOffice.text.toString().trim { it <= ' ' })
            }

            if (viewHolder.llBtime.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_birth_time), viewHolder.edtBtime.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llBplace.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_sp_bplace), viewHolder.edtBplace.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llIsspect.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_chk_is_spect), viewHolder.chkIsSpect.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llIsshani.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_chk_is_shani), viewHolder.chkIsShani.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llIsmangal.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_chk_is_mangal), viewHolder.chkIsMangal.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llHeight.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_height_meter), viewHolder.edtHeightMeter.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llWeight.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_weight_kg), viewHolder.edtWeightKg.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llCreated.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_created), viewHolder.tvCreated.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llUpdated.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_edt_updated), viewHolder.tvUpdated.text.toString().trim { it <= ' ' })
            }
            if (viewHolder.llPercentage.isShown) {
                lstValues.put(_context.resources.getString(R.string.ss_maxUpdate), viewHolder.rangeUpdationBar.selectedMaxValue.toString().trim { it <= ' ' })
                lstValues.put(_context.resources.getString(R.string.ss_minUpdate), viewHolder.rangeUpdationBar.selectedMinValue.toString().trim { it <= ' ' })
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        lstValues
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return null!!
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    interface ICloseDialog {
        fun PopupClose()
    }

    private inner class PopUpViewHolder internal constructor(view: View) {
        var flexboxLayout: FlexboxLayout = view.findViewById(R.id.flexbox_layout)
        var imgCodeClose: ImageView = view.findViewById(R.id.img_code_close)
        var imgAgeClose: ImageView = view.findViewById(R.id.img_age_close)
        var imgHeadClose: ImageView = view.findViewById(R.id.img_head_close)
        var imgMemberClose: ImageView = view.findViewById(R.id.img_member_close)
        var imgSurnameClose: ImageView = view.findViewById(R.id.img_surname_close)
        var imgSamajClose: ImageView = view.findViewById(R.id.img_samaj_close)
        var imgGenderClose: ImageView = view.findViewById(R.id.img_gender_close)
        var imgMaritalClose: ImageView = view.findViewById(R.id.img_marital_close)
        var imgNativeClose: ImageView = view.findViewById(R.id.img_native_close)
        var imgCityClose: ImageView = view.findViewById(R.id.img_city_close)
        var imgEmailClose: ImageView = view.findViewById(R.id.img_email_close)
        var imgMobileClose: ImageView = view.findViewById(R.id.img_mobile_close)
        var imgLocalAddClose: ImageView = view.findViewById(R.id.img_local_add_close)
        var imgPermanentAddClose: ImageView = view.findViewById(R.id.img_permanent_add_close)
        var imgPincodeClose: ImageView = view.findViewById(R.id.img_pincode_close)
        var imgAreaClose: ImageView = view.findViewById(R.id.img_area_close)
        var imgStateClose: ImageView = view.findViewById(R.id.img_state_close)
        var imgBdateClose: ImageView = view.findViewById(R.id.img_bdate_close)
        var imgMdateClose: ImageView = view.findViewById(R.id.img_mdate_close)
        var imgMosadClose: ImageView = view.findViewById(R.id.img_mosad_close)
        var imgEducationClose: ImageView = view.findViewById(R.id.img_education_close)
        var imgGotraClose: ImageView = view.findViewById(R.id.img_gotra_close)
        var imgBgClose: ImageView = view.findViewById(R.id.img_bg_close)
        var imgExpiredClose: ImageView = view.findViewById(R.id.img_expired_close)
        var imgRentedClose: ImageView = view.findViewById(R.id.img_rented_close)
        var imgDonorClose: ImageView = view.findViewById(R.id.img_donor_close)
        var imgOaddressClose: ImageView = view.findViewById(R.id.img_oaddress_close)
        var imgSubCatClose: ImageView = view.findViewById(R.id.img_sub_cat_close)
        var imgCatClose: ImageView = view.findViewById(R.id.img_cat_close)
        var imgOccuClose: ImageView = view.findViewById(R.id.img_occu_close)
        var imgActivityClose: ImageView = view.findViewById(R.id.img_activity_close)
        var imgBtimeClose: ImageView = view.findViewById(R.id.img_btime_close)
        var imgBplaceClose: ImageView = view.findViewById(R.id.img_bplace_close)
        var imgSpectClose: ImageView = view.findViewById(R.id.img_spect_close)
        var imgShaniClose: ImageView = view.findViewById(R.id.img_shani_close)
        var imgMangalClose: ImageView = view.findViewById(R.id.img_mangal_close)
        var imgHeightClose: ImageView = view.findViewById(R.id.img_height_close)
        var imgWeightClose: ImageView = view.findViewById(R.id.img_weight_close)
        var imgPerClose: ImageView = view.findViewById(R.id.img_per_close)
        var imgUpdatedClose: ImageView = view.findViewById(R.id.img_updated_close)
        var imgCreatedClose: ImageView = view.findViewById(R.id.img_created_close)
        var llFamilyCode: LinearLayout = view.findViewById(R.id.ll_family_code)
        var llHead: LinearLayout = view.findViewById(R.id.ll_head)
        var llMember: LinearLayout = view.findViewById(R.id.ll_member)
        var llSurname: LinearLayout = view.findViewById(R.id.ll_surname)
        var llSamaj: LinearLayout = view.findViewById(R.id.ll_samaj)
        var llGender: LinearLayout = view.findViewById(R.id.ll_gender)
        var llMarital: LinearLayout = view.findViewById(R.id.ll_marital)
        var llNative: LinearLayout = view.findViewById(R.id.ll_native)
        var llCity: LinearLayout = view.findViewById(R.id.ll_city)
        var llAge: LinearLayout = view.findViewById(R.id.ll_age)
        var llEmail: LinearLayout = view.findViewById(R.id.ll_email)
        var llMobile: LinearLayout = view.findViewById(R.id.ll_mobile)
        var llLocalAdd: LinearLayout = view.findViewById(R.id.ll_local_add)
        var llPermanentAdd: LinearLayout = view.findViewById(R.id.ll_permanent_add)
        var llPincode: LinearLayout = view.findViewById(R.id.ll_pincode)
        var llArea: LinearLayout = view.findViewById(R.id.ll_area)
        var llState: LinearLayout = view.findViewById(R.id.ll_state)
        var llBirthDate: LinearLayout = view.findViewById(R.id.ll_birth_date)
        var llMdate: LinearLayout = view.findViewById(R.id.ll_mdate)
        var llMosad: LinearLayout = view.findViewById(R.id.ll_mosad)
        var llEducation: LinearLayout = view.findViewById(R.id.ll_education)
        var llGotra: LinearLayout = view.findViewById(R.id.ll_gotra)
        var llBg: LinearLayout = view.findViewById(R.id.ll_bg)
        var llIsexpired: LinearLayout = view.findViewById(R.id.ll_isExpired)
        var llIsrented: LinearLayout = view.findViewById(R.id.ll_isRented)
        var llIsdonor: LinearLayout = view.findViewById(R.id.ll_isDonor)
        var llOfficeAdd: LinearLayout = view.findViewById(R.id.ll_office_add)
        var llCat: LinearLayout = view.findViewById(R.id.ll_cat)
        var llSubCat: LinearLayout = view.findViewById(R.id.ll_sub_cat)
        var llOccupation: LinearLayout = view.findViewById(R.id.ll_occupation)
        var llActivity: LinearLayout = view.findViewById(R.id.ll_activity)
        var llBtime: LinearLayout = view.findViewById(R.id.ll_btime)
        var llBplace: LinearLayout = view.findViewById(R.id.ll_bplace)
        var llIsspect: LinearLayout = view.findViewById(R.id.ll_isSpect)
        var llIsshani: LinearLayout = view.findViewById(R.id.ll_isShani)
        var llIsmangal: LinearLayout = view.findViewById(R.id.ll_isMangal)
        var llHeight: LinearLayout = view.findViewById(R.id.ll_height)
        var llWeight: LinearLayout = view.findViewById(R.id.ll_weight)
        var llCreated: LinearLayout = view.findViewById(R.id.ll_created)
        var llUpdated: LinearLayout = view.findViewById(R.id.ll_updated)
        var llPercentage: LinearLayout = view.findViewById(R.id.ll_percentage)
        var edtFamilyCode: EditText = view.findViewById(R.id.edt_family_code)
        var edtHeadName: EditText = view.findViewById(R.id.edt_head_name)
        var edtMember: EditText = view.findViewById(R.id.edt_member)
        var edtEmail: EditText = view.findViewById(R.id.edt_email)
        var edtMobile: EditText = view.findViewById(R.id.edt_mobile)
        var edtLocalAdd: EditText = view.findViewById(R.id.edt_local_add)
        var edtPermanentAdd: EditText = view.findViewById(R.id.edt_permanent_add)
        var edtPincode: EditText = view.findViewById(R.id.edt_pincode)
        var tvBirthDate: TextView = view.findViewById(R.id.tv_birth_date)
        var tvMdate: TextView = view.findViewById(R.id.tv_mdate)
        var edtOffice: EditText = view.findViewById(R.id.edt_office)
        var edtBtime: EditText = view.findViewById(R.id.edt_btime)
        var edtHeightMeter: EditText = view.findViewById(R.id.edt_height_meter)
        var edtWeightKg: EditText = view.findViewById(R.id.edt_weight_kg)
        var tvUpdated: TextView = view.findViewById(R.id.tv_updated)
        var tvCreated: TextView = view.findViewById(R.id.tv_created)
        var edtArea: EditText = view.findViewById(R.id.edt_area)
        var edtBplace: EditText = view.findViewById(R.id.edt_bplace)
        var edtMosad: EditText = view.findViewById(R.id.edt_mosad)

        var spSurname: JRSpinner = view.findViewById(R.id.sp_surname)
        var spSamaj: JRSpinner = view.findViewById(R.id.sp_samaj)
        var spMarital: JRSpinner = view.findViewById(R.id.sp_marital)
        var spCity: JRSpinner = view.findViewById(R.id.sp_city)
        var spGender: JRSpinner = view.findViewById(R.id.sp_gender)
        var spNative: JRSpinner = view.findViewById(R.id.sp_native)
        var spState: JRSpinner = view.findViewById(R.id.sp_state)
        var spEducation: JRSpinner = view.findViewById(R.id.sp_education)
        var spGotra: JRSpinner = view.findViewById(R.id.sp_gotra)
        var spBg: JRSpinner = view.findViewById(R.id.sp_bg)
        var spMainCat: JRSpinner = view.findViewById(R.id.sp_main_cat)
        var spSubCat: JRSpinner = view.findViewById(R.id.sp_sub_cat)
        var spOccupation: JRSpinner = view.findViewById(R.id.sp_occupation)
        var spActivity: JRSpinner = view.findViewById(R.id.sp_activity)

        var rangeAgeBar: CrystalRangeSeekbar = view.findViewById(R.id.rangeSeekbar)
        var rangeUpdationBar: CrystalRangeSeekbar = view.findViewById(R.id.rangeUpdationBar)
        var tvMin: TextView = view.findViewById(R.id.textMin1)
        var tvMax: TextView = view.findViewById(R.id.textMax1)
        var tvMinPer: TextView = view.findViewById(R.id.txt_min_per)
        var tvMaxPer: TextView = view.findViewById(R.id.txt_max_per)
        var edtFilterName: EditText = view.findViewById(R.id.edt_filter_name)
        var chkSave: CheckBox = view.findViewById(R.id.chk_save)
        var chkIsExpired: CheckBox = view.findViewById(R.id.chk_is_expired)
        var chkIsRented: CheckBox = view.findViewById(R.id.chk_is_rented)
        var chkIsDonor: CheckBox = view.findViewById(R.id.chk_is_donor)
        var chkIsSpect: CheckBox = view.findViewById(R.id.chk_is_spect)
        var chkIsShani: CheckBox = view.findViewById(R.id.chk_is_shani)
        var chkIsMangal: CheckBox = view.findViewById(R.id.chk_is_mangal)
        var btnApply: Button = view.findViewById(R.id.btnApply)
    }

}