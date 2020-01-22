package com.krs.community.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.krs.community.R
import com.krs.community.adapter.SmartPopUpAdapter.ICloseDialog
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.orhanobut.dialogplus.DialogPlus
import java.util.*

class SmartFilterAdapter(private val _context: Context,var profileDetailViewModel: ProfileDetailViewModel) : BaseExpandableListAdapter(), ICloseDialog {
    var previousGroup = -1
    private val header: MutableList<String>
    private val mapChildValues: HashMap<String, String> = HashMap()
    private var edtHeadName: EditText?=null
    private var edtFamilyCode: EditText?=null
    private var edtMemberName: EditText?=null
    private var edtEmail: EditText?=null
    private var edtMobile: EditText?=null
    private var edtLocalAdd: EditText?=null
    private var edtPermanentAdd: EditText?=null
    private var edtPinCode: EditText?=null
    private var edtBdate: EditText?=null
    private var edtMdate: EditText?=null
    private var edtOffice: EditText?=null
    private var edtBirthTime: EditText?=null
    private var edtHeightMeter: EditText?=null
    private var edtWeightKg: EditText?=null
    private var edtCreated: EditText?=null
    private var edtUpdated: EditText?=null
    private var edtArea: EditText?=null
    private var edtMosaad: EditText?=null

    private lateinit var spSurname: JRSpinner
    private lateinit var spLocalComm: JRSpinner
    private lateinit var spMarital: JRSpinner
    private lateinit var spCity: JRSpinner
    private lateinit var spGender: JRSpinner
    private lateinit var spNative: JRSpinner
    private lateinit var spState: JRSpinner

    private lateinit var spEducation: JRSpinner
    private lateinit var spGotra: JRSpinner
    private lateinit var spBg: JRSpinner
    private lateinit var spMainCat: JRSpinner
    private lateinit var spSubCat: JRSpinner
    private lateinit var spOccupation: JRSpinner
    private lateinit var spActivity: JRSpinner
    private lateinit var spBplace: JRSpinner

    private var rangeAgeBar: CrystalRangeSeekbar?=null
    private var rangeUpdationBar: CrystalRangeSeekbar?=null
    private var chkIsDonor: CheckBox?=null
    private var chkIsRented: CheckBox?=null
    private var chkIsExpired: CheckBox?=null
    private var chkIsSpect: CheckBox?=null
    private var chkIsShani: CheckBox?=null
    private var chkIsMangal: CheckBox?=null

    /*private lateinit var mosadAdapter: ArrayAdapter<String>
    private lateinit var educationAdapter: ArrayAdapter<String>
    private lateinit var gotraAdapter: ArrayAdapter<String>
    private lateinit var bgAdapter: ArrayAdapter<String>
    private lateinit var areaAdapter: ArrayAdapter<String>
    private lateinit var stateAdapter: ArrayAdapter<String>
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var subcatAdapter: ArrayAdapter<String>
    private lateinit var occupationAdapter: ArrayAdapter<String>
    private lateinit var curActivityAdapter: ArrayAdapter<String>
    private lateinit var birthPlaceAdapter: ArrayAdapter<String>*/
    private lateinit var dialog: DialogPlus
    override fun getGroupCount(): Int {
        return 8
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroup(groupPosition: Int): Any {
        return header[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return null!!
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View { // Getting header name
        var convertView = convertView
        val headerTitle = getGroup(groupPosition) as String
        // Inflating header layout and setting text
        if (convertView == null) {
            val infalInflater = LayoutInflater.from(_context)
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.header, parent, false)
            }
        }
        val llTitle = convertView?.findViewById<LinearLayout>(R.id.ll_title)
        val tvHeader = convertView?.findViewById<TextView>(R.id.tv_header)
        val tvBottom = convertView?.findViewById<TextView>(R.id.tv_bottom)
        if (groupPosition == 0) {
            llTitle?.visibility = View.VISIBLE
            tvHeader?.visibility = View.GONE
            tvBottom?.visibility = View.GONE
        } else if (groupPosition == 7) {
            llTitle?.visibility = View.GONE
            tvHeader?.visibility = View.GONE
            tvBottom?.visibility = View.INVISIBLE
        } else {
            tvBottom?.visibility = View.GONE
            llTitle?.visibility = View.GONE
            tvHeader?.visibility = View.VISIBLE
        }
        tvHeader?.text = headerTitle

        if (isExpanded) {
            tvHeader?.background = _context.resources.getDrawable(R.drawable.round_top_corner)
            tvHeader?.setTypeface(null, Typeface.BOLD)
            tvHeader?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sort_up, 0)
        } else {
            tvHeader?.background = _context.resources.getDrawable(R.drawable.round_corner_gray)
            tvHeader?.setTypeface(null, Typeface.NORMAL)
            tvHeader?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sort_down, 0)
        }
        return convertView!!
    }

    fun storeFieldsValues() {
        if (edtHeadName != null) {
            val familyCode = edtFamilyCode?.text.toString().trim { it <= ' ' }
            if (!familyCode.isEmpty()) {
                mapChildValues[_context.resources.getString(R.string.ss_family_code)] = familyCode
            }
            val headName = edtHeadName?.text.toString().trim { it <= ' ' }
            if (!headName.isEmpty()) {
                mapChildValues[_context.resources.getString(R.string.ss_head_name)] = headName
            }
            val mem_name = edtMemberName?.text.toString().trim { it <= ' ' }
            if (!mem_name.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_mem_name)] = mem_name
            }
            val surname = spSurname.text.toString().trim { it <= ' ' }
            if (!surname.isEmpty() && !surname.equals("Surname", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_surname)] = surname
            }
            val samaj = spLocalComm.text.toString().trim { it <= ' ' }
            if (!samaj.isEmpty() && !samaj.equals("samaj", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_samaj)] = samaj
            }
            val marital = spMarital.text.toString().trim { it <= ' ' }
            if (!marital.isEmpty() && !marital.equals("Marital", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_marital)] = marital
            }
            val city = spCity.text.toString().trim { it <= ' ' }
            if (!city.isEmpty() && !city.equals("City", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_city)] = city
            }
            val gender = spGender.text.toString().trim { it <= ' ' }
            if (!gender.isEmpty() && !gender.equals("gender", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_gender)] = gender
            }

            val _native = spNative.text.toString().trim { it <= ' ' }
            if (!_native.isEmpty() && !_native.equals("Native", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_native)] = _native
            }

            val min = rangeAgeBar?.selectedMinValue.toString()
            val max = rangeAgeBar?.selectedMaxValue.toString()
            if (!min.isEmpty() && !min.equals("0", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_minage)] = min
            }
            if (!max.isEmpty() && !max.equals("100", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_maxage)] = max
            }
        }
        if (edtEmail != null) {
            val email = edtEmail?.text.toString().trim { it <= ' ' }
            if (!email.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_email)] = email
            }
            val mobile = edtMobile?.text.toString().trim { it <= ' ' }
            if (!mobile.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_mobile)] = mobile
            }
            val local_add = edtLocalAdd?.text.toString().trim { it <= ' ' }
            if (!local_add.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_local_add)] = local_add
            }
            val permanent = edtPermanentAdd?.text.toString().trim { it <= ' ' }
            if (!permanent.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_permanent_add)] = permanent
            }
            val pincode = edtPinCode?.text.toString().trim { it <= ' ' }
            if (!pincode.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_pin_code)] = pincode
            }
            val area = edtArea?.text.toString().trim { it <= ' ' }
            if (!area.isEmpty() && !area.equals("area", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_edt_area)] = area
            }
            val state = spState?.text.toString().trim { it <= ' ' }
            if (!state.isEmpty() && !state.equals("State", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_state)] = state
            }
        }
        if (edtBdate != null) {
            val bdate = edtBdate?.text.toString().trim { it <= ' ' }
            if (!bdate.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_bdate)] = bdate
            }
            val mdate = edtMdate?.text.toString().trim { it <= ' ' }
            if (!mdate.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_mdate)] = mdate
            }
            val mosad = edtMosaad?.text.toString().trim { it <= ' ' }
            if (!mosad.isEmpty() && !mosad.equals("Mosad", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_edt_mosad)] = mosad
            }
            val educaiton = spEducation.text.toString().trim { it <= ' ' }
            if (!educaiton.isEmpty() && !educaiton.equals("Education", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_education)] = educaiton
            }
            val gotra = spGotra.text.toString().trim { it <= ' ' }
            if (!gotra.isEmpty() && !gotra.equals("Gotra", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_gotra)] = gotra
            }
            val bg = spBg.text.toString().trim { it <= ' ' }
            if (!bg.isEmpty() && !bg.equals("BG", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_bg)] = bg
            }
            val donor = chkIsDonor?.isChecked.toString()
            if (!donor.isEmpty() && !donor.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_donor)] = donor
            }
            val is_rented = chkIsRented?.isChecked.toString()
            if (!is_rented.isEmpty() && !is_rented.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_rented)] = is_rented
            }
            val expired = chkIsExpired?.isChecked.toString()
            if (!expired.isEmpty() && !expired.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_expired)] = expired
            }
        }
        if (edtOffice != null) {
            val office = edtOffice?.text.toString().trim { it <= ' ' }
            if (!office.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_office)] = office
            }
            val main_cat = spMainCat.text.toString().trim { it <= ' ' }
            if (!main_cat.isEmpty() && !main_cat.equals("Category", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_main_cat)] = main_cat
            }
            val sub_cat = spSubCat.text.toString().trim { it <= ' ' }
            if (!sub_cat.isEmpty() && !sub_cat.equals("Sub_Cat", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_sub_cat)] = sub_cat
            }
            val occupatation = spOccupation.text.toString().trim { it <= ' ' }
            if (!occupatation.isEmpty() && !occupatation.equals("Occupation", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_occupation)] = occupatation
            }
            val activity = spActivity.text.toString().trim { it <= ' ' }
            if (!activity.isEmpty() && !activity.equals("Activity", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_activity)] = activity
            }
        }
        if (edtBirthTime != null) {
            val btime = edtBirthTime?.text.toString().trim { it <= ' ' }
            if (!btime.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_birth_time)] = btime
            }
            val meter = edtHeightMeter?.text.toString().trim { it <= ' ' }
            if (!meter.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_height_meter)] = meter
            }
            val weight = edtWeightKg?.text.toString().trim { it <= ' ' }
            if (!weight.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_weight_kg)] = weight
            }
            val bplace = spBplace.text.toString().trim { it <= ' ' }
            if (!bplace.isEmpty() && !bplace.equals("bplace", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_bplace)] = bplace
            }
            val is_spect = chkIsSpect?.isChecked.toString()
            if (!is_spect.isEmpty() && !is_spect.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_spect)] = is_spect
            }
            val is_shani = chkIsShani?.isChecked.toString()
            if (!is_shani.isEmpty() && !is_shani.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_shani)] = is_shani
            }
            val is_mangal = chkIsMangal?.isChecked.toString()
            if (!is_mangal.isEmpty() && !is_mangal.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_mangal)] = is_mangal
            }
        }
        if (edtCreated != null) {
            val created = edtCreated?.text.toString().trim { it <= ' ' }
            if (!created.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_created)] = created
            }
            val updated = edtUpdated?.text.toString().trim { it <= ' ' }
            if (!updated.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_updated)] = updated
            }
            val min = rangeUpdationBar?.selectedMinValue.toString()
            val max = rangeUpdationBar?.selectedMaxValue.toString()
            if (!min.isEmpty() && !max.isEmpty() && !max.equals("100", ignoreCase = true) && !min.equals("0", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_minUpdate)] = min
                mapChildValues[_context.resources.getString(R.string.ss_maxUpdate)] = max
            }
        }
    }

    private fun retrieveFieldsValues() {
        if (mapChildValues.size > 0) {
            if (edtHeadName != null) {
                val family_code = mapChildValues[_context.resources.getString(R.string.ss_family_code)]
                if (family_code != null && !family_code.isEmpty()) {
                    edtFamilyCode?.setText(family_code)
                }
                val head_name = mapChildValues[_context.resources.getString(R.string.ss_head_name)]
                if (head_name != null && !head_name.isEmpty()) {
                    edtHeadName?.setText(head_name)
                }
                val mem_name = mapChildValues[_context.resources.getString(R.string.ss_mem_name)]
                if (mem_name != null && !mem_name.isEmpty()) {
                    edtMemberName?.setText(mem_name)
                }
                val surname = mapChildValues[_context.resources.getString(R.string.ss_sp_surname)]
                if ((surname != null) && !surname.isEmpty() && !surname.equals("Surname", ignoreCase = true)) {
                    spSurname.setText(surname)
                }
                val samaj = mapChildValues[_context.resources.getString(R.string.ss_sp_samaj)]
                if (samaj != null && !samaj.isEmpty()) {
                    spLocalComm.setText(samaj)
                }
                val marital = mapChildValues[_context.resources.getString(R.string.ss_sp_marital)]
                if ((marital != null) && !marital.isEmpty() && !marital.equals("Marital", ignoreCase = true)) {
                    spMarital.setText(marital)
                }
                val city = mapChildValues[_context.resources.getString(R.string.ss_sp_city)]
                if (city != null && !city.isEmpty()) {
                    spCity.setText(city)
                }
                val gender = mapChildValues[_context.resources.getString(R.string.ss_sp_gender)]
                if ((gender != null) && !gender.isEmpty() && !gender.equals("gender", ignoreCase = true)) {
                    spGender.setText(gender)
                }
                val native1 = mapChildValues[_context.resources.getString(R.string.ss_sp_native)]
                if (native1 != null && !native1.isEmpty()) {
                    spNative.setText(native1)
                }
                val maxAge = mapChildValues[_context.getString(R.string.ss_maxAge)]
                val minAge = mapChildValues[_context.getString(R.string.ss_minAge)]
                if ((maxAge != null) && !maxAge.isEmpty() && !maxAge.equals("100", ignoreCase = true)) {
                    rangeAgeBar?.setMaxStartValue(mapChildValues[_context.getString(R.string.ss_maxAge)]!!.toInt().toFloat())?.apply()
                }
                if ((minAge != null) && !minAge.isEmpty() && !minAge.equals("0", ignoreCase = true)) {
                    rangeAgeBar?.setMinStartValue(mapChildValues[_context.getString(R.string.ss_minAge)]!!.toInt().toFloat())?.apply()
                }
            }
            if (edtEmail != null) {
                val email = mapChildValues[_context.getString(R.string.ss_edt_email)]
                if (email != null && !email.isEmpty()) {
                    edtEmail?.setText(email)
                }
                val mobile = mapChildValues[_context.getString(R.string.ss_edt_mobile)]
                if (mobile != null && !mobile.isEmpty()) {
                    edtMobile?.setText(mobile)
                }
                val local_add = mapChildValues[_context.getString(R.string.ss_edt_local_add)]
                if (local_add != null && !local_add.isEmpty()) {
                    edtLocalAdd?.setText(local_add)
                }
                val permanent_add = mapChildValues[_context.getString(R.string.ss_edt_permanent_add)]
                if (permanent_add != null && !permanent_add.isEmpty()) {
                    edtPermanentAdd?.setText(permanent_add)
                }
                val pin_code = mapChildValues[_context.getString(R.string.ss_edt_pin_code)]
                if (pin_code != null && !pin_code.isEmpty()) {
                    edtPinCode?.setText(pin_code)
                }
                val area = mapChildValues[_context.getString(R.string.ss_edt_area)]
                if (area != null && !area.isEmpty()) {
                    edtArea?.setText(area)
                }
                val state = mapChildValues[_context.getString(R.string.ss_sp_state)]
                if (state != null && !state.isEmpty()) {
                    spState.setText(state)
                }
            }
            if (edtBdate != null) {
                val bdate = mapChildValues[_context.getString(R.string.ss_edt_bdate)]
                if (bdate != null && !bdate.isEmpty()) {
                    edtBdate?.setText(bdate)
                }
                val mdate = mapChildValues[_context.getString(R.string.ss_edt_mdate)]
                if (mdate != null && !mdate.isEmpty()) {
                    edtMdate?.setText(mdate)
                }
                val mosad = mapChildValues[_context.getString(R.string.ss_edt_mosad)]
                if (mosad != null && !mosad.isEmpty()) {
                    edtMosaad?.setText(mosad)
                }
                val education = mapChildValues[_context.getString(R.string.ss_sp_education)]
                if (education != null && !education.isEmpty()) {
                    spEducation.setText(education)
                }
                val gotra = mapChildValues[_context.getString(R.string.ss_sp_gotra)]
                if (gotra != null && !gotra.isEmpty()) {
                    spGotra.setText(gotra)
                }
                val bg = mapChildValues[_context.getString(R.string.ss_sp_bg)]
                if (gotra != null && !gotra.isEmpty()) {
                    spBg.setText(bg)
                }
                val is_doner = mapChildValues[_context.getString(R.string.ss_chk_is_donor)]
                if (is_doner != null && is_doner.equals(_context.getString(R.string.ss_true), ignoreCase = true)) {
                    chkIsDonor?.isChecked = true
                } else {
                    chkIsDonor?.isChecked = false
                }
                val is_rented = mapChildValues[_context.getString(R.string.ss_chk_is_rented)]
                if (is_rented != null && is_rented.equals(_context.getString(R.string.ss_true), ignoreCase = true)) {
                    chkIsRented?.isChecked = true
                } else {
                    chkIsRented?.isChecked = false
                }
                val is_expired = mapChildValues[_context.getString(R.string.ss_chk_is_expired)]
                if (is_expired != null && is_expired.equals(_context.getString(R.string.ss_true), ignoreCase = true)) {
                    chkIsExpired?.isChecked = true
                } else {
                    chkIsExpired?.isChecked = false
                }
            }
            if (edtOffice != null) {
                val office = mapChildValues[_context.getString(R.string.ss_edt_office)]
                if (office != null && !office.isEmpty()) {
                    edtOffice?.setText(office)
                }
                val main_cat = mapChildValues[_context.getString(R.string.ss_sp_main_cat)]
                if (main_cat != null && !main_cat.isEmpty()) {
                    spMainCat.setText(main_cat)
                }
                val sub_cat = mapChildValues[_context.getString(R.string.ss_sp_sub_cat)]
                if (sub_cat != null && !sub_cat.isEmpty()) {
                    spSubCat.setText(sub_cat)
                }
                val occupation = mapChildValues[_context.getString(R.string.ss_sp_occupation)]
                if (occupation != null && !occupation.isEmpty()) {
                    spOccupation.setText(occupation)
                }
                val activity = mapChildValues[_context.getString(R.string.ss_sp_activity)]
                if (activity != null && !activity.isEmpty()) {
                    spActivity.setText(activity)
                }
            }
            if (edtBirthTime != null) {
                val birth_time = mapChildValues[_context.getString(R.string.ss_edt_birth_time)]
                if (birth_time != null && !birth_time.isEmpty()) {
                    edtBirthTime?.setText(birth_time)
                }
                val height_meter = mapChildValues[_context.getString(R.string.ss_edt_height_meter)]
                if (height_meter != null && !height_meter.isEmpty()) {
                    edtHeightMeter?.setText(height_meter)
                }
                val weight_kg = mapChildValues[_context.getString(R.string.ss_edt_weight_kg)]
                if (weight_kg != null && !weight_kg.isEmpty()) {
                    edtWeightKg?.setText(weight_kg)
                }
                val bplace = mapChildValues[_context.getString(R.string.ss_sp_bplace)]
                if (bplace != null && !bplace.isEmpty()) {
                    spBplace.setText(bplace)
                }
                val is_spect = mapChildValues[_context.getString(R.string.ss_chk_is_spect)]
                if (is_spect != null && is_spect.equals(_context.getString(R.string.ss_true), ignoreCase = true)) {
                    chkIsSpect?.isChecked = true
                } else {
                    chkIsSpect?.isChecked = false
                }
                val is_shani = mapChildValues[_context.getString(R.string.ss_chk_is_shani)]
                if (is_shani != null && is_shani.equals(_context.getString(R.string.ss_true), ignoreCase = true)) {
                    chkIsShani?.isChecked = true
                } else {
                    chkIsShani?.isChecked = false
                }
                val is_mangal = mapChildValues[_context.getString(R.string.ss_chk_is_mangal)]
                if (is_mangal != null && is_mangal.equals(_context.getString(R.string.ss_true), ignoreCase = true)) {
                    chkIsMangal?.isChecked = true
                } else {
                    chkIsMangal?.isChecked = false
                }
            }
            if (edtCreated != null) {
                val created = mapChildValues[_context.getString(R.string.ss_edt_created)]
                if (created != null && !created.isEmpty()) {
                    edtCreated?.setText(created)
                }
                val updated = mapChildValues[_context.getString(R.string.ss_edt_updated)]
                if (updated != null && !updated.isEmpty()) {
                    edtUpdated?.setText(updated)
                }
                val maxUpdate = mapChildValues[_context.getString(R.string.ss_maxUpdate)]
                val minUpdate = mapChildValues[_context.getString(R.string.ss_minUpdate)]
                if (maxUpdate != null && !maxUpdate.isEmpty()) {
                    rangeUpdationBar?.setMaxStartValue(maxUpdate.toInt().toFloat())?.apply()
                }
                if (minUpdate != null && !minUpdate.isEmpty()) {
                    rangeUpdationBar?.setMinStartValue(minUpdate.toInt().toFloat())?.apply()
                }
            }
        }
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val inflater: LayoutInflater? = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        Log.d("SmartFilterAdapter", "groupPosition: $groupPosition")

        when (groupPosition) {
            1 -> {
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.main_details, null)
                }

                edtFamilyCode = convertView!!.findViewById(R.id.edt_family_code)
                edtHeadName = convertView.findViewById(R.id.edt_head_name)
                edtMemberName = convertView.findViewById(R.id.edt_member_name)
                spSurname = convertView.findViewById(R.id.sp_surname)
                spLocalComm = convertView.findViewById(R.id.sp_samaj)
                spCity = convertView.findViewById(R.id.sp_city)
                spGender = convertView.findViewById(R.id.sp_gender)
                spMarital = convertView.findViewById(R.id.sp_marital)
                spNative = convertView.findViewById(R.id.sp_native)
                rangeAgeBar = convertView.findViewById(R.id.rangeSeekbar)
                val tvMin = convertView.findViewById<TextView>(R.id.textMin1)
                val tvMax = convertView.findViewById<TextView>(R.id.textMax1)

                Coroutines.main {
                    profileDetailViewModel.lstNativeName.await().observeForever {
                        spNative.setItems(it.toTypedArray())
                        spNative.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstLastName.await().observeForever {
                        spSurname.setItems(it.toTypedArray())
                        spSurname.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.getLocalCommName.await().observeForever {
                        spLocalComm.setItems(it.toTypedArray())
                        spLocalComm.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstCityName.await().observeForever {
                        spCity.setItems(it.toTypedArray())
                        spCity.setExpandTint(R.color.black)
                    }

                    val lstMarital =  _context.resources.getStringArray(R.array.marital)
                    spMarital.setItems(lstMarital)
                    spMarital.setExpandTint(R.color.black)

                    val lstGender = arrayOf("Male", "Female")
                    spGender.setItems(lstGender)
                    spGender.setExpandTint(R.color.black)

                    rangeAgeBar?.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                        tvMin.text = "Age $minValue"
                        tvMax.text = "Age $maxValue"
                    }

                    rangeAgeBar?.setOnRangeSeekbarFinalValueListener { minValue: Number, maxValue: Number -> Log.d("CRS=>", minValue.toString() + " : " + maxValue) }
                    retrieveFieldsValues()
                }

            }
            2 -> {

                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.contact_details, null)
                }

                edtEmail = convertView!!.findViewById(R.id.edt_email)
                edtMobile = convertView.findViewById(R.id.edt_mobile)
                edtLocalAdd = convertView.findViewById(R.id.edt_local_add)
                edtPermanentAdd = convertView.findViewById(R.id.edt_permanent_add)
                edtPinCode = convertView.findViewById(R.id.edt_pin_code)
                spState = convertView.findViewById(R.id.sp_state)
                edtArea = convertView.findViewById(R.id.edt_area)

                Coroutines.main {
                   profileDetailViewModel.lstStateName.await().observeForever {
                       spState.setItems(it.toTypedArray())
                       spState.setExpandTint(R.color.black)
                   }
                    retrieveFieldsValues()
                }
            }
            3 -> {
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.personal_details, null)
                }

                edtBdate = convertView!!.findViewById(R.id.edt_bdate)
                edtMdate = convertView.findViewById(R.id.edt_mdate)
                edtMosaad = convertView.findViewById(R.id.edt_mosaad)
                spEducation = convertView.findViewById(R.id.sp_education)
                spGotra = convertView.findViewById(R.id.sp_gotra)
                spBg = convertView.findViewById(R.id.sp_bg)
                chkIsDonor = convertView.findViewById(R.id.chk_is_donor)
                chkIsRented = convertView.findViewById(R.id.chk_is_rented)
                chkIsExpired = convertView.findViewById(R.id.chk_is_expired)

                Coroutines.main {

                    val lstBlood =  _context.resources.getStringArray(R.array.bloodGroup)
                    spBg.setItems(lstBlood)
                    spBg.setExpandTint(R.color.black)

                    profileDetailViewModel.lstGotraName.await().observeForever {
                        spGotra.setItems(it.toTypedArray())
                        spGotra.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstEducationName.await().observeForever {
                        spEducation.setItems(it.toTypedArray())
                        spEducation.setExpandTint(R.color.black)
                    }





                    retrieveFieldsValues()
                }

            }
            4 -> {
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.professional_details, null)
                }

                edtOffice = convertView!!.findViewById(R.id.edt_office)
                spMainCat = convertView.findViewById(R.id.sp_main_cat)
                spSubCat = convertView.findViewById(R.id.sp_sub_cat)
                spOccupation = convertView.findViewById(R.id.sp_occupation)
                spActivity = convertView.findViewById(R.id.sp_current_activity)

                retrieveFieldsValues()
            }
            5 -> {
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.matrimony_details, null)
                }

                edtBirthTime = convertView!!.findViewById(R.id.edt_birth_time)
                edtHeightMeter = convertView.findViewById(R.id.edt_height_meter)
                edtWeightKg = convertView.findViewById(R.id.edt_weight_kg)
                spBplace = convertView.findViewById(R.id.sp_birth_place)
                chkIsSpect = convertView.findViewById(R.id.chk_is_spect)
                chkIsShani = convertView.findViewById(R.id.chk_is_shani)
                chkIsMangal = convertView.findViewById(R.id.chk_is_mangal)

                retrieveFieldsValues()
            }
            6 -> {
                if (inflater != null) convertView = inflater.inflate(R.layout.see_more, null)

                edtCreated = convertView!!.findViewById(R.id.edt_created)
                edtUpdated = convertView.findViewById(R.id.edt_updated)
                rangeUpdationBar = convertView.findViewById(R.id.rangeUpdationBar)
                val tvMin1 = convertView.findViewById<TextView>(R.id.textMin1)
                val tvMax1 = convertView.findViewById<TextView>(R.id.textMax1)
                // set listener
                rangeUpdationBar?.setOnRangeSeekbarChangeListener(OnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                    tvMin1.setText(minValue.toString() + "%")
                    tvMax1.setText(maxValue.toString() + "%")
                })
                // set final value listener
                rangeUpdationBar?.setOnRangeSeekbarFinalValueListener(OnRangeSeekbarFinalValueListener { minValue, maxValue -> Log.d("CRS=>", "$minValue : $maxValue") })
                retrieveFieldsValues()
            }
            else -> {
            }
        }
        return convertView!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun PopupClose() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun openBottomSheetDailog() {
        storeFieldsValues()
        if (mapChildValues.size > 0) {
            val popUpAdapter = SmartPopUpAdapter(_context, this, mapChildValues)
            dialog = DialogPlus
                    .newDialog(_context)
                    .setAdapter(popUpAdapter)
                    .setExpanded(true)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .setOnItemClickListener({ dialog12: DialogPlus?, item: Any?, view: View?, position: Int -> Toast.makeText(_context, "Clicked " + position, Toast.LENGTH_SHORT).show() })
                    .setCancelable(true)
                    .setGravity(Gravity.BOTTOM)
                    .setExpanded(true)
                    .create()
            dialog.show()
        } else {
            Utility.startSweetDialog(_context, SweetAlertDialog.ERROR_TYPE, "Smart Filter", "Enter filter values")
        }
    }

    init {
        header = ArrayList()
        header.add("")
        header.add(_context.getString(R.string.main))
        header.add(_context.getString(R.string.contact))
        header.add(_context.getString(R.string.personal))
        header.add(_context.getString(R.string.professonal))
        header.add(_context.getString(R.string._matrimony))
        header.add(_context.getString(R.string.some_more))
        header.add("")

        /*val lstGender: MutableList<String> = ArrayList()
        lstGender.add(_context.getString(R.string.ss_gender))
        lstGender.add("Male")
        lstGender.add("Female")

        val lstSurname: MutableList<String> = ArrayList()
        lstSurname.add(_context.getString(R.string.ss_surname))
        lstSurname.add("surname1")
        lstSurname.add("surname2")
        val lstSamaj: MutableList<String> = ArrayList()
        lstSamaj.add(_context.getString(R.string.ss_samaj))
        lstSamaj.add("samaj1")
        lstSamaj.add("samaj2")
        val lstCity: MutableList<String> = ArrayList()
        lstCity.add(_context.getString(R.string.ss_city))
        lstCity.add("city1")
        lstCity.add("city2")
        val lstMarital: MutableList<String> = ArrayList()
        lstMarital.add(_context.getString(R.string.ss_marital))
        lstMarital.add("marital1")
        lstMarital.add("marital2")
        val lstArea: MutableList<String> = ArrayList()
        lstArea.add(_context.getString(R.string.ss_area))
        lstArea.add("area1")
        lstArea.add("area2")
        val lstState: MutableList<String> = ArrayList()
        lstState.add(_context.getString(R.string.ss_state))
        lstState.add("state1")
        lstState.add("state2")
        val lstMosad: MutableList<String> = ArrayList()
        lstMosad.add(_context.getString(R.string.ss_mosad))
        lstMosad.add("mosad1")
        lstMosad.add("mosad2")
        val lstEducation: MutableList<String> = ArrayList()
        lstEducation.add(_context.getString(R.string.ss_education))
        lstEducation.add("education1")
        lstEducation.add("education2")
        val lstGotra: MutableList<String> = ArrayList()
        lstGotra.add(_context.getString(R.string.ss_gotra))
        lstGotra.add("gotra1")
        lstGotra.add("gotra2")
        val lstBg: MutableList<String> = ArrayList()
        lstBg.add(_context.getString(R.string.ss_bg))
        lstBg.add("bg1")
        lstBg.add("bg2")
        val lstCategory: MutableList<String> = ArrayList()
        lstCategory.add(_context.getString(R.string.ss_catogory))
        lstCategory.add("category1")
        lstCategory.add("category2")
        val lstSubCat: MutableList<String> = ArrayList()
        lstSubCat.add(_context.getString(R.string.ss_sub_cat))
        lstSubCat.add("sub_cat1")
        lstSubCat.add("sub_cat2")
        val lstOccupation: MutableList<String> = ArrayList()
        lstOccupation.add(_context.getString(R.string.ss_Occupation))
        lstOccupation.add("occupation1")
        lstOccupation.add("occupation2")
        val lstActivity: MutableList<String> = ArrayList()
        lstActivity.add(_context.getString(R.string.ss_activity))
        lstActivity.add("activity1")
        lstActivity.add("activity2")
        val lstBplace: MutableList<String> = ArrayList()
        lstBplace.add(_context.resources.getString(R.string.ss_bplace))
        lstBplace.add("bplace1")
        lstBplace.add("bplace2")*/

        /*birthPlaceAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstBplace)
        birthPlaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstCategory)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subcatAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstSubCat)
        subcatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        occupationAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstOccupation)
        occupationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        curActivityAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstActivity)
        curActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/
        /*surnameAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstSurname)
        surnameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        samajAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstSamaj)
        samajAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        maritalAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstMarital)
        maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cityAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstCity)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstGender)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/

        /*mosadAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstMosad)
        mosadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        educationAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstEducation)
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gotraAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstGotra)
        gotraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bgAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstBg)
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        areaAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstArea)
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, lstState)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/
    }
}