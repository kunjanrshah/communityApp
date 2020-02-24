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
                         private val editFilter:String?) : BaseExpandableListAdapter(), ICloseDialog , DatePickerDialog.OnDateSetListener{
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
    private var edtOffice: EditText?=null
    private var edtHeightMeter: EditText?=null
    private var edtWeightKg: EditText?=null
    private var edtArea: EditText?=null
    private var edtMosaad: EditText?=null
    private var edtBirthPlace: EditText?=null

    private var tvBdate: TextView?=null
    private var tvMdate: TextView?=null
    private var tvCreated: TextView?=null
    private var tvUpdated: TextView?=null
    private var tvBirthTime: TextView?=null

    private var imgBdateClose: ImageView?=null
    private var imgMdateClose: ImageView?=null
    private var imgUpdatedClose: ImageView?=null
    private var imgCreatedClose: ImageView?=null

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

    private var rangeAgeBar: CrystalRangeSeekbar?=null
    private var rangeUpdationBar: CrystalRangeSeekbar?=null

    private var chkIsDonor: CheckBox?=null
    private var chkIsRented: CheckBox?=null
    private var chkIsExpired: CheckBox?=null
    private var chkIsSpect: CheckBox?=null
    private var chkIsShani: CheckBox?=null
    private var chkIsMangal: CheckBox?=null

    private var which:Int=0
    private var pattern="dd-MM-yyyy"
    private var datepicker = SpinnerDatePickerDialogBuilder()
    private lateinit var dialog: DialogPlus

    init{
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


    private fun setEditFilterValues(){
        if(!editFilter.isNullOrEmpty()){
            val values=JSONObject(editFilter).getString(_context.getString(R.string.value_filter))
            val json=JSONObject(values)
            val iterator:Iterator<String>
            iterator=json.keys()
            mapChildValues.clear()
            while(iterator.hasNext()){
                val key=iterator.next()
                mapChildValues[key] = json.getString(key)
            }
        }
    }

    fun getFiledValues() {
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
            val state = spState.text.toString().trim { it <= ' ' }
            if (!state.isEmpty() && !state.equals("State", ignoreCase = true)) {
                mapChildValues[_context.getString(R.string.ss_sp_state)] = state
            }
        }
        if (tvBdate != null) {
            val bdate = tvBdate?.text.toString().trim { it <= ' ' }
            if (!bdate.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_bdate)] = bdate
            }
            val mdate = tvMdate?.text.toString().trim { it <= ' ' }
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
        if (tvBirthTime != null) {
            val btime = tvBirthTime?.text.toString().trim { it <= ' ' }
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
            val bplace = edtBirthPlace?.text.toString().trim { it <= ' ' }
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
        if (tvCreated != null) {
            val created = tvCreated?.text.toString().trim { it <= ' ' }
            if (!created.isEmpty()) {
                mapChildValues[_context.getString(R.string.ss_edt_created)] = created
            }
            val updated = tvUpdated?.text.toString().trim { it <= ' ' }
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

    private fun setFieldValues() {
        if (mapChildValues.size > 0) {
            if (edtHeadName != null) {
                val familyCode = mapChildValues[_context.resources.getString(R.string.ss_family_code)]
                if (familyCode != null && !familyCode.isEmpty()) {
                    edtFamilyCode?.setText(familyCode)
                }
                val headName = mapChildValues[_context.resources.getString(R.string.ss_head_name)]
                if (headName != null && !headName.isEmpty()) {
                    edtHeadName?.setText(headName)
                }
                val memName = mapChildValues[_context.resources.getString(R.string.ss_mem_name)]
                if (memName != null && !memName.isEmpty()) {
                    edtMemberName?.setText(memName)
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

            if (tvBdate != null) {
                val bdate = mapChildValues[_context.getString(R.string.ss_edt_bdate)]
                if (bdate != null && !bdate.isEmpty()) {
                    tvBdate?.text = bdate
                }
                val mdate = mapChildValues[_context.getString(R.string.ss_edt_mdate)]
                if (mdate != null && !mdate.isEmpty()) {
                    tvMdate?.text = mdate
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
                val isDoner = mapChildValues[_context.getString(R.string.ss_chk_is_donor)]
                chkIsDonor?.isChecked = isDoner != null && isDoner.equals(_context.getString(R.string.ss_true), ignoreCase = true)
                val isRented = mapChildValues[_context.getString(R.string.ss_chk_is_rented)]
                chkIsRented?.isChecked = isRented != null && isRented.equals(_context.getString(R.string.ss_true), ignoreCase = true)
                val isExpired = mapChildValues[_context.getString(R.string.ss_chk_is_expired)]
                chkIsExpired?.isChecked = isExpired != null && isExpired.equals(_context.getString(R.string.ss_true), ignoreCase = true)
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
            if (tvBirthTime != null) {
                val birth_time = mapChildValues[_context.getString(R.string.ss_edt_birth_time)]
                if (birth_time != null && !birth_time.isEmpty()) {
                    tvBirthTime?.text = birth_time
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
                    edtBirthPlace?.setText(bplace)
                }
                val isSpect = mapChildValues[_context.getString(R.string.ss_chk_is_spect)]
                chkIsSpect?.isChecked = isSpect != null && isSpect.equals(_context.getString(R.string.ss_true), ignoreCase = true)
                val is_shani = mapChildValues[_context.getString(R.string.ss_chk_is_shani)]
                chkIsShani?.isChecked = is_shani != null && is_shani.equals(_context.getString(R.string.ss_true), ignoreCase = true)
                val isMangal = mapChildValues[_context.getString(R.string.ss_chk_is_mangal)]
                chkIsMangal?.isChecked = isMangal != null && isMangal.equals(_context.getString(R.string.ss_true), ignoreCase = true)
            }
            if (tvCreated != null) {
                val created = mapChildValues[_context.getString(R.string.ss_edt_created)]
                if (created != null && !created.isEmpty()) {
                    tvCreated?.text = created
                }
                val updated = mapChildValues[_context.getString(R.string.ss_edt_updated)]
                if (updated != null && !updated.isEmpty()) {
                    tvUpdated?.text = updated
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

                spSurname.setOnItemClickListener {
                    if(it==0){
                        spSurname.setText("")
                    }
                }
                spLocalComm.setOnItemClickListener {
                    if(it==0){
                        spLocalComm.setText("")
                    }
                }
                spCity.setOnItemClickListener {
                    if(it==0){
                        spCity.setText("")
                    }
                }
                spGender.setOnItemClickListener {
                    if(it==0){
                        spGender.setText("")
                    }
                }
                spMarital.setOnItemClickListener {
                    if(it==0){
                        spMarital.setText("")
                    }
                }
                spNative.setOnItemClickListener {
                    if(it==0){
                        spNative.setText("")
                    }
                }


                Coroutines.main {
                    profileDetailViewModel.lstNativeName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spNative.setItems(lstValue.toTypedArray())
                        spNative.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstLastName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spSurname.setItems(lstValue.toTypedArray())
                        spSurname.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.getLocalCommName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spLocalComm.setItems(lstValue.toTypedArray())
                        spLocalComm.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstCityName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spCity.setItems(lstValue.toTypedArray())
                        spCity.setExpandTint(R.color.black)
                    }

                    val lstMarital =  _context.resources.getStringArray(R.array.marital)
                    val lstValue=ArrayList<String>()
                    lstValue.add(_context.getString(R.string.no_selection))
                    lstValue.addAll(lstMarital)
                    spMarital.setItems(lstValue.toTypedArray())
                    spMarital.setExpandTint(R.color.black)

                    val lstGender = arrayOf(_context.getString(R.string.no_selection),"Male", "Female")
                    spGender.setItems(lstGender)
                    spGender.setExpandTint(R.color.black)

                    rangeAgeBar?.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
                        tvMin.text = "Age"+" $minValue"
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
                    if(it==0){
                        spState.setText("")
                    }
                }


                Coroutines.main {
                   profileDetailViewModel.lstStateName.await().observeForever {
                       val lstValue=ArrayList<String>()
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
                tvMdate = convertView.findViewById(R.id.tv_mdate)
                edtMosaad = convertView.findViewById(R.id.edt_mosaad)
                spEducation = convertView.findViewById(R.id.sp_education)
                spGotra = convertView.findViewById(R.id.sp_gotra)
                spBg = convertView.findViewById(R.id.sp_bg)
                chkIsDonor = convertView.findViewById(R.id.chk_is_donor)
                chkIsRented = convertView.findViewById(R.id.chk_is_rented)
                chkIsExpired = convertView.findViewById(R.id.chk_is_expired)

                imgBdateClose?.setOnClickListener {
                    tvBdate?.text = ""
                }

                imgMdateClose?.setOnClickListener {
                    tvMdate?.text = ""
                }

                tvBdate?.setOnClickListener {
                    which=1
                    val memDate = tvBdate?.text.toString().trim()
                    setDatePicker(memDate)
                }

                tvMdate?.setOnClickListener {
                    which=2
                    val memDate = tvMdate?.text.toString().trim()
                    setDatePicker(memDate)
                }

                spEducation.setOnItemClickListener {
                    if(it==0){
                        spEducation.setText("")
                    }
                }

                spGotra.setOnItemClickListener {
                    if(it==0){
                        spGotra.setText("")
                    }
                }

                spBg.setOnItemClickListener {
                    if(it==0){
                        spBg.setText("")
                    }
                }

                Coroutines.main {

                    val lstBlood =  _context.resources.getStringArray(R.array.bloodGroup)
                    val lstValue=ArrayList<String>()
                    lstValue.add(_context.getString(R.string.no_selection))
                    lstValue.addAll(lstBlood)
                    spBg.setItems(lstValue.toTypedArray())
                    spBg.setExpandTint(R.color.black)

                    profileDetailViewModel.lstGotraName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spGotra.setItems(lstValue.toTypedArray())
                        spGotra.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstEducationName.await().observeForever {
                        val lstValue=ArrayList<String>()
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
                    if(it==0){
                        spMainCat.setText("")
                    }
                }
                spSubCat.setOnItemClickListener {
                    if(it==0){
                        spSubCat.setText("")
                    }
                }
                spOccupation.setOnItemClickListener {
                    if(it==0){
                        spOccupation.setText("")
                    }
                }
                spActivity.setOnItemClickListener {
                    if(it==0){
                        spActivity.setText("")
                    }
                }

                Coroutines.main {

                    profileDetailViewModel.lstBusinessCategoryName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spMainCat.setItems(lstValue.toTypedArray())
                        spMainCat.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstBusinessSubCategoryName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spSubCat.setItems(lstValue.toTypedArray())
                        spSubCat.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstOccupationName.await().observeForever {
                        val lstValue=ArrayList<String>()
                        lstValue.add(_context.getString(R.string.no_selection))
                        lstValue.addAll(it.toTypedArray())
                        spOccupation.setItems(lstValue.toTypedArray())
                        spOccupation.setExpandTint(R.color.black)
                    }

                    profileDetailViewModel.lstActivityName.await().observeForever {
                        val lstValue=ArrayList<String>()
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
                edtHeightMeter = convertView.findViewById(R.id.edt_height_meter)
                edtWeightKg = convertView.findViewById(R.id.edt_weight_kg)
                edtBirthPlace = convertView.findViewById(R.id.edt_birth_place)
                chkIsSpect = convertView.findViewById(R.id.chk_is_spect)
                chkIsShani = convertView.findViewById(R.id.chk_is_shani)
                chkIsMangal = convertView.findViewById(R.id.chk_is_mangal)

                tvBirthTime?.setOnClickListener {
                    NumberPadTimePickerDialogFragment.newInstance(mListener).show((_context as AppCompatActivity).supportFragmentManager, "birth_time")
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
                    tvUpdated?.text=""
                }

                imgCreatedClose?.setOnClickListener {
                    tvCreated?.text=""
                }

                tvCreated?.setOnClickListener {
                    which=3
                    setDatePicker(tvCreated!!.text.toString().trim())
                }

                tvUpdated?.setOnClickListener {
                    which=4
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

        var hour:String=hourOfDay.toString()
        var min:String=minute.toString()
        if(hour.length==1){
            hour="0$hour"
        }
        if(min.length==1){
            min="0$min"
        }
        tvBirthTime?.text = "$hour:$min"
    }

    fun clearAll(){
        if(edtFamilyCode!=null){
            edtFamilyCode?.text?.clear()
            edtHeadName?.text?.clear()
            edtMemberName?.text?.clear()
            spSurname.text?.clear()
            spLocalComm.text?.clear()
            spGender.text?.clear()
            spMarital.text?.clear()
            spNative.text?.clear()
            spCity.text?.clear()
        }
        if(edtEmail!=null){
            edtEmail?.text?.clear()
            edtMobile?.text?.clear()
            edtLocalAdd?.text?.clear()
            edtPermanentAdd?.text?.clear()
            edtPinCode?.text?.clear()
            edtArea?.text?.clear()
            spState.text?.clear()
        }
        if(tvBdate!=null){
            spEducation.text?.clear()
            spGotra.text?.clear()
            spBg.text?.clear()
            tvBdate?.text=""
            tvMdate?.text=""
            edtMosaad?.text?.clear()
            chkIsDonor?.isChecked=false
            chkIsRented?.isChecked=false
            chkIsExpired?.isChecked=false
        }
        if(edtOffice!=null){
            edtOffice?.text?.clear()
            spMainCat.text?.clear()
            spSubCat.text?.clear()
            spOccupation.text?.clear()
            spActivity.text?.clear()
        }
        if(tvBirthTime!=null){
            tvBirthTime?.text=""
            edtBirthPlace?.text?.clear()
            chkIsSpect?.isChecked=false
            chkIsShani?.isChecked=false
            chkIsMangal?.isChecked=false
            edtHeightMeter?.text?.clear()
            edtWeightKg?.text?.clear()
        }
        if(tvCreated!=null){
            tvCreated?.text=""
            tvUpdated?.text=""

            rangeAgeBar?.setMinStartValue(0f)
            rangeAgeBar?.setMaxStartValue(100f)
            rangeAgeBar?.apply()

            rangeUpdationBar?.setMinStartValue(0f)
            rangeUpdationBar?.setMaxStartValue(100f)
            rangeUpdationBar?.apply()
        }
        mapChildValues.clear()
        getFiledValues()
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
            val popUpAdapter = SmartPopUpAdapter(_context, this, mapChildValues,profileDetailViewModel,editFilter)
            dialog = DialogPlus
                    .newDialog(_context)
                    .setAdapter(popUpAdapter)
                    .setExpanded(true)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .setOnItemClickListener({ dialog12: DialogPlus?, item: Any?, view: View?, position: Int -> Toast.makeText(_context, "Clicked " + position, Toast.LENGTH_SHORT).show() })
                    .setCancelable(true)
                    .setGravity(Gravity.BOTTOM)
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
        if(which==1){
            tvBdate?.text = date
        }else if(which==2){
            tvMdate?.text = date
        }else if(which==3){
            tvCreated?.text=date
        }else if(which==4){
            tvUpdated?.text=date
        }
    }
}