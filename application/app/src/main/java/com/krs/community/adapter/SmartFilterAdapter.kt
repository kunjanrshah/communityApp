package com.krs.community.adapter

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.adapter.SmartPopUpAdapter.ICloseDialog
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.utils.Coroutines
import com.krs.community.utils.NumberPadTimePickerDialogFragment
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.orhanobut.dialogplus.DialogPlus
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SmartFilterAdapter(private val _context: Context,
                         var profileDetailViewModel: ProfileDetailViewModel,
                         private val editFilter: String?) : BaseExpandableListAdapter(), ICloseDialog, DatePickerDialog.OnDateSetListener {
    var previousGroup = -1
    private val header: MutableList<String>
    private val mapChildValues: HashMap<String, String> = HashMap()
    private var edtHeadName: EditText? = null
    private var edtFamilyCode: EditText? = null
    private var edtMemberName: EditText? = null
    private var edtFatherName: EditText? = null
    private var edtMotherName: EditText? = null
    private var edtEmail: EditText? = null
    private var edtMobile: EditText? = null
    private var edtLocalAdd: EditText? = null
    private var edtPermanentAdd: EditText? = null
    private var edtPinCode: EditText? = null
    private var edtOffice: EditText? = null

    // private var edtHeightMeter: EditText?=null
    //private var edtWeightKg: EditText?=null
    private var edtArea: EditText? = null
    private var tvExpired: TextView? = null
    private var edtBirthPlace: EditText? = null

    private var tvBdate: TextView? = null
    private var tvMdate: TextView? = null
    private var tvCreated: TextView? = null
    private var tvUpdated: TextView? = null
    private var tvBirthTime: TextView? = null
    private var tvMinWeight: TextView? = null
    private var tvMaxWeight: TextView? = null
    private var tvMinHeight: TextView? = null
    private var tvMaxHeight: TextView? = null

    private var imgBdateClose: ImageView? = null
    private var imgMdateClose: ImageView? = null
    private var imgEdateClose: ImageView? = null
    private var imgUpdatedClose: ImageView? = null
    private var imgCreatedClose: ImageView? = null

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
    private lateinit var rlGotra: RelativeLayout
    private var rangeAgeBar: CrystalRangeSeekbar? = null
    private var rangeUpdationBar: CrystalRangeSeekbar? = null
    private var rangeHeightBar1: CrystalRangeSeekbar? = null
    private var rangeWeightBar: CrystalRangeSeekbar? = null

    private var chkIsDonor: CheckBox? = null
    private var chkIsRented: CheckBox? = null
    private var chkIsExpired: CheckBox? = null
    private var chkIsSpect: CheckBox? = null
    private var chkIsInterested: CheckBox? = null
    private var chkIsShani: CheckBox? = null
    private var chkIsMangal: CheckBox? = null

    private var which: Int = 0
    private var pattern = "dd-MM-yyyy"
    private var datepicker = SpinnerDatePickerDialogBuilder()
    private lateinit var dialog: DialogPlus

    init {
        setEditFilterValues()
    }

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


    private fun setEditFilterValues() {
        if (!editFilter.isNullOrEmpty()) {
            val values = JSONObject(editFilter).getString(_context.getString(R.string.value_filter))
            val json = JSONObject(values)
            val iterator: Iterator<String>
            iterator = json.keys()
            mapChildValues.clear()
            while (iterator.hasNext()) {
                val key = iterator.next()
                mapChildValues[key] = json.getString(key)
            }
        }
    }

    fun getFiledValues() {
        if (edtHeadName != null) {
            val familyCode = edtFamilyCode?.text.toString().trim { it <= ' ' }
            if (familyCode.isNotEmpty()) {
                mapChildValues[_context.resources.getString(R.string.ss_family_code)] = familyCode
            } else {
                mapChildValues[_context.resources.getString(R.string.ss_family_code)] = ""
            }
            val headName = edtHeadName?.text.toString().trim { it <= ' ' }
            if (headName.isNotEmpty()) {
                mapChildValues[_context.resources.getString(R.string.ss_head_name)] = headName
            } else {
                mapChildValues[_context.resources.getString(R.string.ss_head_name)] = ""
            }

            val mem_name = edtMemberName?.text.toString().trim { it <= ' ' }
            if (mem_name.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_mem_name)] = mem_name
            } else {
                mapChildValues[_context.getString(R.string.ss_mem_name)] = ""
            }

            val fatherName = edtFatherName?.text.toString().trim { it <= ' ' }
            if (fatherName.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_father_name)] = fatherName
            } else {
                mapChildValues[_context.getString(R.string.ss_father_name)] = ""
            }

            val motherName = edtMotherName?.text.toString().trim { it <= ' ' }
            if (motherName.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_mother_name)] = motherName
            } else {
                mapChildValues[_context.getString(R.string.ss_mother_name)] = ""
            }


            val surname = spSurname.text.toString().trim { it <= ' ' }
            if (surname.isNotEmpty() && !surname.equals("Surname", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_surname)] = surname
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_surname)] = ""
            }
            val samaj = spLocalComm.text.toString().trim { it <= ' ' }
            if (!samaj.isNotEmpty() && !samaj.equals("samaj", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_samaj)] = samaj
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_samaj)] = ""
            }
            val marital = spMarital.text.toString().trim { it <= ' ' }
            if (marital.isNotEmpty() && !marital.equals("Marital", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_marital)] = marital
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_marital)] = ""
            }
            val city = spCity.text.toString().trim { it <= ' ' }
            if (city.isNotEmpty() && !city.equals("City", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_city)] = city
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_city)] = ""
            }
            val gender = spGender.text.toString().trim { it <= ' ' }
            if (gender.isNotEmpty() && !gender.equals("gender", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_gender)] = gender
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_gender)] = ""
            }

            val _native = spNative.text.toString().trim { it <= ' ' }
            if (_native.isNotEmpty() && !_native.equals("Native", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_native)] = _native
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_native)] = ""
            }

            val min = rangeAgeBar?.selectedMinValue.toString()
            val max = rangeAgeBar?.selectedMaxValue.toString()
            if (min.isNotEmpty() && !min.equals("0", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_minage)] = min
            } else {
                mapChildValues[_context.getString(R.string.ss_minage)] = "0"
            }
            if (max.isNotEmpty() && !max.equals("100", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_maxage)] = max
            } else {
                mapChildValues[_context.getString(R.string.ss_maxage)] = "100"
            }
        }
        if (edtEmail != null) {
            val email = edtEmail?.text.toString().trim { it <= ' ' }
            if (email.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_email)] = email
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_email)] = ""
            }
            val mobile = edtMobile?.text.toString().trim { it <= ' ' }
            if (mobile.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_mobile)] = mobile
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_mobile)] = ""
            }
            val local_add = edtLocalAdd?.text.toString().trim { it <= ' ' }
            if (local_add.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_local_add)] = local_add
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_local_add)] = ""
            }

            val permanent = edtPermanentAdd?.text.toString().trim { it <= ' ' }
            if (permanent.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_permanent_add)] = permanent
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_permanent_add)] = ""
            }

            val pincode = edtPinCode?.text.toString().trim { it <= ' ' }
            if (pincode.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_pin_code)] = pincode
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_pin_code)] = ""
            }

            val area = edtArea?.text.toString().trim { it <= ' ' }
            if (area.isNotEmpty() && !area.equals("area", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_edt_area)] = area
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_area)] = ""
            }

            val state = spState.text.toString().trim { it <= ' ' }
            if (state.isNotEmpty() && !state.equals("State", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_state)] = state
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_state)] = ""
            }
        }
        if (tvBdate != null) {
            val bdate = tvBdate?.text.toString().trim { it <= ' ' }
            if (bdate.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_bdate)] = bdate
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_bdate)] = ""
            }
            val mdate = tvMdate?.text.toString().trim { it <= ' ' }
            if (mdate.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_mdate)] = mdate
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_mdate)] = ""
            }
            val expireDate = tvExpired?.text.toString().trim { it <= ' ' }
            if (expireDate.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.expire_date)] = expireDate
            } else {
                mapChildValues[_context.getString(R.string.expire_date)] = ""
            }
            val educaiton = spEducation.text.toString().trim { it <= ' ' }
            if (educaiton.isNotEmpty() && !educaiton.equals("Education", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_education)] = educaiton
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_education)] = ""
            }
            val gotra = spGotra.text.toString().trim { it <= ' ' }
            if (gotra.isNotEmpty() && !gotra.equals("Gotra", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_gotra)] = gotra
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_gotra)] = ""
            }
            val bg = spBg.text.toString().trim { it <= ' ' }
            if (bg.isNotEmpty() && !bg.equals("BG", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_bg)] = bg
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_bg)] = ""
            }

            val donor = chkIsDonor?.isChecked.toString()
            if (donor.isNotEmpty() && !donor.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_donor)] = "1"
            } else {
                mapChildValues[_context.getString(R.string.ss_chk_is_donor)] = "0"
            }

            val isRented = chkIsRented?.isChecked.toString()
            if (isRented.isNotEmpty() && !isRented.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_rented)] = "1"
            } else {
                mapChildValues[_context.getString(R.string.ss_chk_is_rented)] = "0"
            }

            val expired = chkIsExpired?.isChecked.toString()
            if (expired.isNotEmpty() && !expired.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_expired)] = "1"
            } else {
                mapChildValues[_context.getString(R.string.ss_chk_is_expired)] = "0"
            }
        }
        if (edtOffice != null) {
            val office = edtOffice?.text.toString().trim { it <= ' ' }
            if (office.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_office)] = office
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_office)] = ""
            }
            val main_cat = spMainCat.text.toString().trim { it <= ' ' }
            if (main_cat.isNotEmpty() && !main_cat.equals("Category", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_main_cat)] = main_cat
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_main_cat)] = ""
            }
            val sub_cat = spSubCat.text.toString().trim { it <= ' ' }
            if (sub_cat.isNotEmpty() && !sub_cat.equals("Sub_Cat", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_sub_cat)] = sub_cat
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_sub_cat)] = ""
            }
            val occupatation = spOccupation.text.toString().trim { it <= ' ' }
            if (occupatation.isNotEmpty() && !occupatation.equals("Occupation", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_occupation)] = occupatation
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_occupation)] = ""
            }
            val activity = spActivity.text.toString().trim { it <= ' ' }
            if (activity.isNotEmpty() && !activity.equals("Activity", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_activity)] = activity
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_activity)] = ""
            }
        }
        if (tvBirthTime != null) {
            val btime = tvBirthTime?.text.toString().trim { it <= ' ' }
            if (btime.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_birth_time)] = btime
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_birth_time)] = ""
            }

            var min = rangeHeightBar1?.selectedMinValue.toString()
            var max = rangeHeightBar1?.selectedMaxValue.toString()
            if (min.isNotEmpty() && !min.equals("0", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_min_height)] = min
            } else {
                mapChildValues[_context.getString(R.string.ss_min_height)] = "0"
            }
            if (max.isNotEmpty() && !max.equals("200", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_max_height)] = max
            } else {
                mapChildValues[_context.getString(R.string.ss_max_height)] = "200"
            }

            min = rangeWeightBar?.selectedMinValue.toString()
            max = rangeWeightBar?.selectedMaxValue.toString()
            if (min.isNotEmpty() && !min.equals("0", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_min_weight)] = min
            } else {
                mapChildValues[_context.getString(R.string.ss_min_weight)] = "0"
            }
            if (max.isNotEmpty() && !max.equals("200", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_max_weight)] = max
            } else {
                mapChildValues[_context.getString(R.string.ss_max_weight)] = "200"
            }

            val bplace = edtBirthPlace?.text.toString().trim { it <= ' ' }
            if (bplace.isNotEmpty() && !bplace.equals("bplace", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_bplace)] = bplace
            } else {
                mapChildValues[_context.getString(R.string.ss_sp_bplace)] = ""
            }
            val isSpect = chkIsSpect?.isChecked.toString()
            if (isSpect.isNotEmpty() && !isSpect.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_spect)] = "1"
            } else {
                mapChildValues[_context.getString(R.string.ss_chk_is_spect)] = "0"
            }

            val isInterested = chkIsInterested?.isChecked.toString()
            if (isInterested.isNotEmpty() && !isInterested.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.matrimony)] = "Yes"
            } else {
                mapChildValues[_context.getString(R.string.matrimony)] = "No"
            }

            val isShani = chkIsShani?.isChecked.toString()
            if (isShani.isNotEmpty() && !isShani.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_shani)] = "1"
            } else {
                mapChildValues[_context.getString(R.string.ss_chk_is_shani)] = "0"
            }
            val isMangal = chkIsMangal?.isChecked.toString()
            if (isMangal.isNotEmpty() && !isMangal.equals("false", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_chk_is_mangal)] = "1"
            } else {
                mapChildValues[_context.getString(R.string.ss_chk_is_mangal)] = "0"
            }
        }
        if (tvCreated != null) {
            val created = tvCreated?.text.toString().trim { it <= ' ' }
            if (created.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_created)] = created
            }
            val updated = tvUpdated?.text.toString().trim { it <= ' ' }
            if (updated.isNotEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_updated)] = updated
            } else {
                mapChildValues[_context.getString(R.string.ss_edt_updated)] = ""
            }
            val min = rangeUpdationBar?.selectedMinValue.toString()
            val max = rangeUpdationBar?.selectedMaxValue.toString()
            if ((min.isNotEmpty() && max.isNotEmpty()) || !(max.equals("100", ignoreCase = true) && min.equals("0", ignoreCase = true))) {
                mapChildValues[_context.getString(R.string.ss_min_percentage)] = min
                mapChildValues[_context.resources.getString(R.string.ss_max_percentage)] = max
            } else {
                mapChildValues[_context.getString(R.string.ss_min_percentage)] = "0"
                mapChildValues[_context.resources.getString(R.string.ss_max_percentage)] = "100"
            }
        }
    }

    private fun setFieldValues() {
        if (mapChildValues.size > 0) {
            if (edtHeadName != null) {
                val familyCode = mapChildValues[_context.resources.getString(R.string.ss_family_code)]
                if (familyCode != null && familyCode.isNotEmpty()) {
                    edtFamilyCode?.setText(familyCode)
                }
                val headName = mapChildValues[_context.resources.getString(R.string.ss_head_name)]
                if (headName != null && headName.isNotEmpty()) {
                    edtHeadName?.setText(headName)
                }
                val memName = mapChildValues[_context.resources.getString(R.string.ss_mem_name)]
                if (memName != null && memName.isNotEmpty()) {
                    edtMemberName?.setText(memName)
                }
                val fatherName = mapChildValues[_context.resources.getString(R.string.ss_father_name)]
                if (fatherName != null && fatherName.isNotEmpty()) {
                    edtFatherName?.setText(fatherName)
                }
                val motherName = mapChildValues[_context.resources.getString(R.string.ss_mother_name)]
                if (motherName != null && motherName.isNotEmpty()) {
                    edtMotherName?.setText(motherName)
                }
                val surname = mapChildValues[_context.resources.getString(R.string.ss_sp_surname)]
                if ((surname != null) && surname.isNotEmpty() && !surname.equals("Surname", ignoreCase = true)) {
                    spSurname.setText(surname)
                }
                val samaj = mapChildValues[_context.resources.getString(R.string.ss_sp_samaj)]
                if (samaj != null && samaj.isNotEmpty()) {
                    spLocalComm.setText(samaj)
                }
                val marital = mapChildValues[_context.resources.getString(R.string.ss_sp_marital)]
                if ((marital != null) && marital.isNotEmpty() && !marital.equals("Marital", ignoreCase = true)) {
                    spMarital.setText(marital)
                }
                val city = mapChildValues[_context.resources.getString(R.string.ss_sp_city)]
                if (city != null && city.isNotEmpty()) {
                    spCity.setText(city)
                }
                val gender = mapChildValues[_context.resources.getString(R.string.ss_sp_gender)]
                if ((gender != null) && gender.isNotEmpty() && !gender.equals("gender", ignoreCase = true)) {
                    spGender.setText(gender)
                }
                val native1 = mapChildValues[_context.resources.getString(R.string.ss_sp_native)]
                if (native1 != null && native1.isNotEmpty()) {
                    spNative.setText(native1)
                }
                val maxAge = mapChildValues[_context.getString(R.string.ss_maxAge)]
                val minAge = mapChildValues[_context.getString(R.string.ss_minAge)]
                if ((maxAge != null) && maxAge.isNotEmpty() && !maxAge.equals("100", ignoreCase = true)) {
                    rangeAgeBar?.setMaxStartValue(mapChildValues[_context.getString(R.string.ss_maxAge)]!!.toInt().toFloat())?.apply()
                }
                if ((minAge != null) && minAge.isNotEmpty() && !minAge.equals("0", ignoreCase = true)) {
                    rangeAgeBar?.setMinStartValue(mapChildValues[_context.getString(R.string.ss_minAge)]!!.toInt().toFloat())?.apply()
                }
            }
            if (edtEmail != null) {
                val email = mapChildValues[_context.getString(R.string.ss_edt_email)]
                if (email != null && email.isNotEmpty()) {
                    edtEmail?.setText(email)
                }
                val mobile = mapChildValues[_context.getString(R.string.ss_edt_mobile)]
                if (mobile != null && mobile.isNotEmpty()) {
                    edtMobile?.setText(mobile)
                }
                val local_add = mapChildValues[_context.getString(R.string.ss_edt_local_add)]
                if (local_add != null && local_add.isNotEmpty()) {
                    edtLocalAdd?.setText(local_add)
                }
                val permanent_add = mapChildValues[_context.getString(R.string.ss_edt_permanent_add)]
                if (permanent_add != null && permanent_add.isNotEmpty()) {
                    edtPermanentAdd?.setText(permanent_add)
                }
                val pin_code = mapChildValues[_context.getString(R.string.ss_edt_pin_code)]
                if (pin_code != null && pin_code.isNotEmpty()) {
                    edtPinCode?.setText(pin_code)
                }
                val area = mapChildValues[_context.getString(R.string.ss_edt_area)]
                if (area != null && area.isNotEmpty()) {
                    edtArea?.setText(area)
                }
                val state = mapChildValues[_context.getString(R.string.ss_sp_state)]
                if (state != null && state.isNotEmpty()) {
                    spState.setText(state)
                }
            }

            if (tvBdate != null) {
                val bdate = mapChildValues[_context.getString(R.string.ss_edt_bdate)]
                if (bdate != null && bdate.isNotEmpty()) {
                    if (Utility.isValidFormat(bdate, Utility.yyyy_MM_dd)) {
                        tvBdate?.text = Utility.changeDateFormat(bdate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    } else {
                        tvBdate?.text = bdate.toString().trim()
                    }
                }
                val mdate = mapChildValues[_context.getString(R.string.ss_edt_mdate)]
                if (mdate != null && mdate.isNotEmpty()) {
                    if (Utility.isValidFormat(mdate, Utility.yyyy_MM_dd)) {
                        tvMdate?.text = Utility.changeDateFormat(mdate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    } else {
                        tvMdate?.text = mdate
                    }
                }
                val expireDate = mapChildValues[_context.getString(R.string.expire_date)]
                if (expireDate != null && expireDate.isNotEmpty()) {
                    if (Utility.isValidFormat(expireDate, Utility.yyyy_MM_dd)) {
                        tvExpired?.text = Utility.changeDateFormat(expireDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    } else {
                        tvExpired?.text = expireDate
                    }
                }
                val education = mapChildValues[_context.getString(R.string.ss_sp_education)]
                if (education != null && education.isNotEmpty()) {
                    spEducation.setText(education)
                }
                val gotra = mapChildValues[_context.getString(R.string.ss_sp_gotra)]
                if (gotra != null && gotra.isNotEmpty()) {
                    spGotra.setText(gotra)
                }
                val bg = mapChildValues[_context.getString(R.string.ss_sp_bg)]
                if (gotra != null && gotra.isNotEmpty()) {
                    spBg.setText(bg)
                }
                val isDoner = mapChildValues[_context.getString(R.string.ss_chk_is_donor)]
                chkIsDonor?.isChecked = isDoner != null && isDoner.equals("1", ignoreCase = true)
                val isRented = mapChildValues[_context.getString(R.string.ss_chk_is_rented)]
                chkIsRented?.isChecked = isRented != null && isRented.equals("1", ignoreCase = true)
                val isExpired = mapChildValues[_context.getString(R.string.ss_chk_is_expired)]
                chkIsExpired?.isChecked = isExpired != null && isExpired.equals("1", ignoreCase = true)
            }
            if (edtOffice != null) {
                val office = mapChildValues[_context.getString(R.string.ss_edt_office)]
                if (office != null && office.isNotEmpty()) {
                    edtOffice?.setText(office)
                }
                val main_cat = mapChildValues[_context.getString(R.string.ss_sp_main_cat)]
                if (main_cat != null && main_cat.isNotEmpty()) {
                    spMainCat.setText(main_cat)
                }
                val sub_cat = mapChildValues[_context.getString(R.string.ss_sp_sub_cat)]
                if (sub_cat != null && sub_cat.isNotEmpty()) {
                    spSubCat.setText(sub_cat)
                }
                val occupation = mapChildValues[_context.getString(R.string.ss_sp_occupation)]
                if (occupation != null && occupation.isNotEmpty()) {
                    spOccupation.setText(occupation)
                }
                val activity = mapChildValues[_context.getString(R.string.ss_sp_activity)]
                if (activity != null && activity.isNotEmpty()) {
                    spActivity.setText(activity)
                }
            }
            if (tvBirthTime != null) {
                val birth_time = mapChildValues[_context.getString(R.string.ss_edt_birth_time)]
                if (birth_time != null && birth_time.isNotEmpty()) {
                    tvBirthTime?.text = birth_time
                }

                val maxHeight = mapChildValues[_context.getString(R.string.ss_max_height)]
                val minHeight = mapChildValues[_context.getString(R.string.ss_min_height)]
                if (maxHeight != null && maxHeight.isNotEmpty()) {
                    rangeHeightBar1?.setMaxStartValue(maxHeight.toInt().toFloat())?.apply()
                }
                if (minHeight != null && minHeight.isNotEmpty()) {
                    rangeHeightBar1?.setMinStartValue(minHeight.toInt().toFloat())?.apply()
                }

                val maxWeight = mapChildValues[_context.getString(R.string.ss_max_weight)]
                val minWeight = mapChildValues[_context.getString(R.string.ss_min_weight)]
                if (maxWeight != null && maxWeight.isNotEmpty()) {
                    rangeWeightBar?.setMaxStartValue(maxWeight.toInt().toFloat())?.apply()
                }
                if (minWeight != null && minWeight.isNotEmpty()) {
                    rangeWeightBar?.setMinStartValue(minWeight.toInt().toFloat())?.apply()
                }


                val bplace = mapChildValues[_context.getString(R.string.ss_sp_bplace)]
                if (bplace != null && bplace.isNotEmpty()) {
                    edtBirthPlace?.setText(bplace)
                }
                val isInterested = mapChildValues[_context.getString(R.string.matrimony)]
                chkIsInterested?.isChecked = isInterested != null && isInterested.equals("Yes", ignoreCase = true)
                val isSpect = mapChildValues[_context.getString(R.string.ss_chk_is_spect)]
                chkIsSpect?.isChecked = isSpect != null && isSpect.equals("1", ignoreCase = true)
                val isShani = mapChildValues[_context.getString(R.string.ss_chk_is_shani)]
                chkIsShani?.isChecked = isShani != null && isShani.equals("1", ignoreCase = true)
                val isMangal = mapChildValues[_context.getString(R.string.ss_chk_is_mangal)]
                chkIsMangal?.isChecked = isMangal != null && isMangal.equals("1", ignoreCase = true)
            }
            if (tvCreated != null) {
                val created = mapChildValues[_context.getString(R.string.ss_edt_created)]
                if (created != null && created.isNotEmpty()) {
                    if (Utility.isValidFormat(created, Utility.yyyy_MM_dd)) {
                        tvCreated?.text = Utility.changeDateFormat(created, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    } else {
                        tvCreated?.text = created
                    }
                }
                val updated = mapChildValues[_context.getString(R.string.ss_edt_updated)]
                if (updated != null && updated.isNotEmpty()) {
                    if (Utility.isValidFormat(updated, Utility.yyyy_MM_dd)) {
                        tvUpdated?.text = Utility.changeDateFormat(updated, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    } else {
                        tvUpdated?.text = updated
                    }
                }
                val maxUpdate = mapChildValues[_context.getString(R.string.ss_max_percentage)]
                val minUpdate = mapChildValues[_context.getString(R.string.ss_min_percentage)]
                if (maxUpdate != null && maxUpdate.isNotEmpty()) {
                    rangeUpdationBar?.setMaxStartValue(maxUpdate.toInt().toFloat())?.apply()
                }
                if (minUpdate != null && minUpdate.isNotEmpty()) {
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
                edtFatherName = convertView.findViewById(R.id.edt_father)
                edtMotherName = convertView.findViewById(R.id.edt_mother)
                spSurname = convertView.findViewById(R.id.sp_surname)
                spLocalComm = convertView.findViewById(R.id.sp_samaj)
                spCity = convertView.findViewById(R.id.sp_city)
                spGender = convertView.findViewById(R.id.sp_gender)
                spMarital = convertView.findViewById(R.id.sp_marital)
                spNative = convertView.findViewById(R.id.sp_native)
                rangeAgeBar = convertView.findViewById(R.id.rangeSeekbar)
                val tvMin = convertView.findViewById<TextView>(R.id.textMin1)
                val tvMax = convertView.findViewById<TextView>(R.id.textMax1)

                spSurname.setOnItemClickListener {
                    if (it == 0) {
                        spSurname.setText("")
                    }
                }
                spLocalComm.setOnItemClickListener {
                    if (it == 0) {
                        spLocalComm.setText("")
                    }
                }
                spCity.setOnItemClickListener {
                    if (it == 0) {
                        spCity.setText("")
                    }
                }
                spGender.setOnItemClickListener {
                    if (it == 0) {
                        spGender.setText("")
                    }
                }
                spMarital.setOnItemClickListener {
                    if (it == 0) {
                        spMarital.setText("")
                    }
                }
                spNative.setOnItemClickListener {
                    if (it == 0) {
                        spNative.setText("")
                    }
                }


                Coroutines.main {
                    profileDetailViewModel.lstNativeName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spNative.setItems(lstValue.toTypedArray())
                        spNative.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstLastName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spSurname.setItems(lstValue.toTypedArray())
                        spSurname.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.getLocalCommName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spLocalComm.setItems(lstValue.toTypedArray())
                        spLocalComm.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstCityName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spCity.setItems(lstValue.toTypedArray())
                        spCity.setExpandTint(R.color.black)
                    }

                    val lstMarital = _context.resources.getStringArray(R.array.marital)
                    val lstValue = ArrayList<String>()
                    lstValue.add(_context.getString(R.string.no_selection))
                    lstValue.addAll(lstMarital)
                    spMarital.setItems(lstValue.toTypedArray())
                    spMarital.setExpandTint(R.color.black)

                    val lstGender = arrayOf(_context.getString(R.string.no_selection), "Male", "Female")
                    spGender.setItems(lstGender)
                    spGender.setExpandTint(R.color.black)

                    rangeAgeBar?.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                        tvMin.text = "Age" + " $minValue"
                        tvMax.text = "Age $maxValue"
                    }

                    rangeAgeBar?.setOnRangeSeekbarFinalValueListener { minValue: Number, maxValue: Number -> Log.d("CRS=>", minValue.toString() + " : " + maxValue) }
                    setFieldValues()
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

                spState.setOnItemClickListener {
                    if (it == 0) {
                        spState.setText("")
                    }
                }


                Coroutines.main {
                    profileDetailViewModel.lstStateName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spState.setItems(lstValue.toTypedArray())
                        spState.setExpandTint(R.color.black)
                    }
                    setFieldValues()
                }
            }
            3 -> {
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.personal_details, null)
                }

                tvBdate = convertView!!.findViewById(R.id.tv_bdate)
                imgBdateClose = convertView.findViewById(R.id.img_bdate_close)
                imgMdateClose = convertView.findViewById(R.id.img_mdate_close)
                imgEdateClose = convertView.findViewById(R.id.img_edate_close)

                tvMdate = convertView.findViewById(R.id.tv_mdate)
                tvExpired = convertView.findViewById(R.id.tv_edate)
                spEducation = convertView.findViewById(R.id.sp_education)
                spGotra = convertView.findViewById(R.id.sp_gotra)
                rlGotra = convertView.findViewById(R.id.rl_gotra)
                if (BuildConfig.FLAVOR == "ghanchi") {
                    rlGotra.visibility = View.GONE
                } else {
                    rlGotra.visibility = View.VISIBLE
                }
                spBg = convertView.findViewById(R.id.sp_bg)
                chkIsDonor = convertView.findViewById(R.id.chk_is_donor)
                chkIsRented = convertView.findViewById(R.id.chk_is_rented)
                chkIsExpired = convertView.findViewById(R.id.chk_is_expired)

                imgBdateClose?.setOnClickListener {
                    tvBdate?.text = ""
                }

                imgEdateClose?.setOnClickListener {
                    tvExpired?.text = ""
                }
                imgMdateClose?.setOnClickListener {
                    tvMdate?.text = ""
                }

                tvExpired?.setOnClickListener {
                    which = 5
                    val memDate = tvExpired?.text.toString().trim()
                    setDatePicker(memDate)
                }

                tvBdate?.setOnClickListener {
                    which = 1
                    val memDate = tvBdate?.text.toString().trim()
                    setDatePicker(memDate)
                }

                tvMdate?.setOnClickListener {
                    which = 2
                    val memDate = tvMdate?.text.toString().trim()
                    setDatePicker(memDate)
                }

                spEducation.setOnItemClickListener {
                    if (it == 0) {
                        spEducation.setText("")
                    }
                }

                spGotra.setOnItemClickListener {
                    if (it == 0) {
                        spGotra.setText("")
                    }
                }

                spBg.setOnItemClickListener {
                    if (it == 0) {
                        spBg.setText("")
                    }
                }

                Coroutines.main {

                    val lstBlood = _context.resources.getStringArray(R.array.bloodGroup)
                    val lstValue = ArrayList<String>()
                    lstValue.add(_context.getString(R.string.no_selection))
                    lstValue.addAll(lstBlood)
                    spBg.setItems(lstValue.toTypedArray())
                    spBg.setExpandTint(R.color.black)

                    profileDetailViewModel.lstGotraName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spGotra.setItems(lstValue.toTypedArray())
                        spGotra.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstEducationName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spEducation.setItems(lstValue.toTypedArray())
                        spEducation.setExpandTint(R.color.black)
                    }
                    setFieldValues()
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


                spMainCat.setOnItemClickListener {
                    if (it == 0) {
                        spMainCat.setText("")
                    }
                }
                spSubCat.setOnItemClickListener {
                    if (it == 0) {
                        spSubCat.setText("")
                    }
                }
                spOccupation.setOnItemClickListener {
                    if (it == 0) {
                        spOccupation.setText("")
                    }
                }
                spActivity.setOnItemClickListener {
                    if (it == 0) {
                        spActivity.setText("")
                    }
                }

                Coroutines.main {

                    profileDetailViewModel.lstBusinessCategoryName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spMainCat.setItems(lstValue.toTypedArray())
                        spMainCat.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstBusinessSubCategoryName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spSubCat.setItems(lstValue.toTypedArray())
                        spSubCat.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstOccupationName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spOccupation.setItems(lstValue.toTypedArray())
                        spOccupation.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstActivityName.await().observeForever {
                        val lstValue = ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spActivity.setItems(lstValue.toTypedArray())
                        spActivity.setExpandTint(R.color.black)
                    }

                    setFieldValues()
                }

            }
            5 -> {
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.matrimony_details, null)
                }

                tvBirthTime = convertView!!.findViewById(R.id.tv_birth_time)
                rangeHeightBar1 = convertView.findViewById(R.id.rangeHeight1)
                rangeWeightBar = convertView.findViewById(R.id.rangeWeight)
                edtBirthPlace = convertView.findViewById(R.id.edt_birth_place)
                chkIsInterested = convertView.findViewById(R.id.chk_is_interested)
                chkIsSpect = convertView.findViewById(R.id.chk_is_spect)
                chkIsShani = convertView.findViewById(R.id.chk_is_shani)
                chkIsMangal = convertView.findViewById(R.id.chk_is_mangal)
                tvMinWeight = convertView.findViewById(R.id.tv_min_weight)
                tvMaxWeight = convertView.findViewById(R.id.tv_max_weight)
                tvMinHeight = convertView.findViewById(R.id.tv_min_height)
                tvMaxHeight = convertView.findViewById(R.id.tv_max_height)

                if (BuildConfig.FLAVOR == "ghanchi") {
                    chkIsShani?.visibility = View.GONE
                    chkIsMangal?.visibility = View.GONE
                } else {
                    chkIsShani?.visibility = View.VISIBLE
                    chkIsMangal?.visibility = View.VISIBLE
                }

                tvBirthTime?.setOnClickListener {
                    NumberPadTimePickerDialogFragment.newInstance(mListener).show((_context as AppCompatActivity).supportFragmentManager, "birth_time")
                }

                rangeHeightBar1?.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                    tvMinHeight?.text = "$minValue"
                    tvMaxHeight?.text = "$maxValue"
                }

                rangeWeightBar?.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                    tvMinWeight?.text = "$minValue"
                    tvMaxWeight?.text = "$maxValue"
                }

                setFieldValues()
            }
            6 -> {
                if (inflater != null) convertView = inflater.inflate(R.layout.see_more, null)

                tvCreated = convertView!!.findViewById(R.id.tv_created)
                imgCreatedClose = convertView.findViewById(R.id.img_created_close)
                tvUpdated = convertView.findViewById(R.id.tv_updated)
                imgUpdatedClose = convertView.findViewById(R.id.img_updated_close)
                rangeUpdationBar = convertView.findViewById(R.id.rangeUpdationBar)
                val tvMin1 = convertView.findViewById<TextView>(R.id.textMin1)
                val tvMax1 = convertView.findViewById<TextView>(R.id.textMax1)

                imgUpdatedClose?.setOnClickListener {
                    tvUpdated?.text = ""
                }

                imgCreatedClose?.setOnClickListener {
                    tvCreated?.text = ""
                }

                tvCreated?.setOnClickListener {
                    which = 3
                    setDatePicker(tvCreated!!.text.toString().trim())
                }

                tvUpdated?.setOnClickListener {
                    which = 4
                    setDatePicker(tvUpdated!!.text.toString().trim())
                }

                rangeUpdationBar?.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                    tvMin1.text = "$minValue%"
                    tvMax1.text = "$maxValue%"
                }
                rangeUpdationBar?.setOnRangeSeekbarFinalValueListener({ minValue, maxValue -> Log.d("CRS=>", "$minValue : $maxValue") })

                setFieldValues()
            }
            else -> {
            }
        }
        return convertView!!
    }

    private val mListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

        var hour: String = hourOfDay.toString()
        var min: String = minute.toString()
        if (hour.length == 1) {
            hour = "0$hour"
        }
        if (min.length == 1) {
            min = "0$min"
        }
        tvBirthTime?.text = "$hour:$min"
    }

    fun clearAll() {
        if (edtFamilyCode != null) {
            edtFamilyCode?.text?.clear()
            edtHeadName?.text?.clear()
            edtMemberName?.text?.clear()
            edtFatherName?.text?.clear()
            edtMotherName?.text?.clear()
            spSurname.text?.clear()
            spLocalComm.text?.clear()
            spGender.text?.clear()
            spMarital.text?.clear()
            spNative.text?.clear()
            spCity.text?.clear()
        }
        if (edtEmail != null) {
            edtEmail?.text?.clear()
            edtMobile?.text?.clear()
            edtLocalAdd?.text?.clear()
            edtPermanentAdd?.text?.clear()
            edtPinCode?.text?.clear()
            edtArea?.text?.clear()
            spState.text?.clear()
        }
        if (tvBdate != null) {
            spEducation.text?.clear()
            spGotra.text?.clear()
            spBg.text?.clear()
            tvBdate?.text = ""
            tvMdate?.text = ""
            tvExpired?.text = ""
            chkIsDonor?.isChecked = false
            chkIsRented?.isChecked = false
            chkIsExpired?.isChecked = false
        }
        if (edtOffice != null) {
            edtOffice?.text?.clear()
            spMainCat.text?.clear()
            spSubCat.text?.clear()
            spOccupation.text?.clear()
            spActivity.text?.clear()
        }
        if (tvBirthTime != null) {
            tvBirthTime?.text = ""
            edtBirthPlace?.text?.clear()
            chkIsSpect?.isChecked = false
            chkIsShani?.isChecked = false
            chkIsInterested?.isChecked = false
            chkIsMangal?.isChecked = false

            rangeHeightBar1?.setMinStartValue(0f)
            rangeHeightBar1?.setMaxStartValue(200f)
            rangeHeightBar1?.apply()

            rangeWeightBar?.setMinStartValue(0f)
            rangeWeightBar?.setMaxStartValue(200f)
            rangeWeightBar?.apply()
        }
        if (tvCreated != null) {
            tvCreated?.text = ""
            tvUpdated?.text = ""

            rangeAgeBar?.setMinStartValue(0f)
            rangeAgeBar?.setMaxStartValue(100f)
            rangeAgeBar?.apply()

            rangeUpdationBar?.setMinStartValue(0f)
            rangeUpdationBar?.setMaxStartValue(100f)
            rangeUpdationBar?.apply()
        }
        mapChildValues.clear()
        getFiledValues()
        Utility.displaySnackBarWithBottomMargin((_context as AppCompatActivity).findViewById(android.R.id.content), "Smart Filter values reset successfully")
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
        getFiledValues()
        if (mapChildValues.size > 0) {
            val popUpAdapter = SmartPopUpAdapter(_context, this, mapChildValues, profileDetailViewModel, editFilter)
            dialog = DialogPlus
                    .newDialog(_context)
                    .setAdapter(popUpAdapter)
                    .setExpanded(true)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .setCancelable(true)
                    .setGravity(Gravity.BOTTOM)
                    .create()
            dialog.show()
        } else {
            Utility.startSweetDialog(_context, SweetAlertDialog.ERROR_TYPE, _context.getString(R.string.smart_filter), _context.getString(R.string.enter_filter_value))
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
    }

    private fun setDatePicker(mem_date: String) {
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

        datepicker.context(_context)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(year1, month1, day1)
                .maxDate(year, month, day)
                .minDate(1900, 0, 1)
                .build().show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var month = "${monthOfYear + 1}"
        var day = "$dayOfMonth"
        if (day.length == 1) {
            day = "0$day"
        }
        if (month.length == 1) {
            month = "0${month}"
        }
        val date = "$day-$month-$year"
        if (which == 1) {
            tvBdate?.text = date
        } else if (which == 2) {
            tvMdate?.text = date
        } else if (which == 3) {
            tvCreated?.text = date
        } else if (which == 4) {
            tvUpdated?.text = date
        } else if (which == 5) {
            tvExpired?.text = date
        }

    }
}