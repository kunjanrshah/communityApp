package com.krs.community.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.print.PrintAttributes
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.ViewUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.FragmentActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.entities.RoomMember
import com.krs.community.fragments.MatrimonyListFragment
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.model.Member
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.SmartFilterViewModel
import com.uttampanchasara.pdfgenerator.CreatePdf
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}




fun View.snackbar(message: String, snack: Int) {
    Snackbar.make(this, message, snack).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

var logger: Logger = Logger(ViewUtils::class.java.simpleName)
fun handleCropResult(result: Intent, context: Context, image: ImageView) {
    val resultUri = UCrop.getOutput(result)
    if (resultUri != null) {
        try {
            Glide.with(AppController.mApplication).load(resultUri).thumbnail(0.5f).into(image)
        } catch (e: Exception) {
            e.message
        }
        logger.debug("resultUri: $resultUri")
    } else {
        Toast.makeText(context, "Cannot retrieve cropped image", Toast.LENGTH_SHORT).show()
    }
}

fun handleCropError(result: Intent, context: Context) {
    val cropError = UCrop.getError(result)
    if (cropError != null) {
        logger.error(cropError)
        Toast.makeText(context, cropError.message, Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show()
    }
}

fun startCrop(uri: Uri, context: Context) {
    val destinationFileName = "${Utility.getRandomString(10)}.jpg"
    var uCrop = UCrop.of(uri, Uri.fromFile(File(context.cacheDir, destinationFileName)))
    uCrop = advancedConfig(uCrop, context)
    uCrop.start(context as Activity)
}

fun advancedConfig(uCrop: UCrop, context: Context): UCrop {
    val options = UCrop.Options()
    options.setCompressionFormat(Bitmap.CompressFormat.JPEG)

    options.setCompressionQuality(100)

    options.setHideBottomControls(false)
    options.setFreeStyleCropEnabled(true)

    options.setBrightnessEnabled(true)
    options.setContrastEnabled(true)
    options.setSaturationEnabled(true)
    options.setSharpnessEnabled(true)

    options.setImageToCropBoundsAnimDuration(666)
    //  options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimary));
    //options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
    options.setStatusBarColor(ContextCompat.getColor(context, R.color.white))
    options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorPrimary))
    options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorPrimary))
    //options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

    // Aspect ratio options
    options.setAspectRatioOptions(1,
            AspectRatio("WOW", 1f, 2f),
            AspectRatio("MUCH", 3f, 4f),
            AspectRatio("RATIO", 0f, 0f),
            AspectRatio("SO", 16f, 9f),
            AspectRatio("ASPECT", 1f, 1f))

    return uCrop.withOptions(options)
}

fun displayNeverAskAgainDialog(context: Context) {

    SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            .setTitleText("Storage Read Permission")
            .setContentText("Permission is needed to pick image from gallery for your Profile. Please permit the permission through " + "Settings screen.\n\nSelect Permissions -> Enable permission")
            .setConfirmText("Permit Manually")
            .setCustomImage(R.drawable.ic_app)
            .setCancelText("Cancel")
            .showCancelButton(true)
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
                val intent = Intent()
                intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
            .show()
}

fun openImageDialog(activity: AppCompatActivity,url: String) {
    val dialog = Dialog(activity)
    dialog.setCancelable(true)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.image_dialog)
    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    val image: ImageView = dialog.findViewById(R.id.img_dialog)
    Glide.with(activity)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .apply(RequestOptions()
                    .placeholder(R.drawable.image_placeholder)
            )
            .thumbnail(1f).into(image)
    dialog.show()
}

fun pickFromGallery(context: FragmentActivity) {
    if(Utility.checkReadExternalStoragePermission(context)){
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Utility.PICK_GALLERY_REQUEST)
    }else{
        promptReadPermission(context)
    }
}

fun promptReadPermission(context: Context) {

        SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Storage Read Permission")
                .setContentText("Permission is needed to pick image from gallery")
                .setConfirmText("Yes, please!")
                .setCancelText("No!")
                .setCustomImage(R.drawable.ic_app)
                .showCancelButton(true)
                .setConfirmClickListener { sDialog ->
                    sDialog.dismiss()
                Utility.requestReadStoragePermission(context as AppCompatActivity)
                }
                .show()

}

fun openFilter(context: Context, smartFilterViewModel: SmartFilterViewModel) {

    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.filter_matrimony)
    dialog.setCancelable(false)
    val spMarital = dialog.findViewById<JRSpinner>(R.id.sp_marital)
    val spCity = dialog.findViewById<JRSpinner>(R.id.sp_city)
    val spLname = dialog.findViewById<JRSpinner>(R.id.sp_lname)
    val btnMale = dialog.findViewById<Button>(R.id.btnmale)
    val btnFemale = dialog.findViewById<Button>(R.id.btnfemale)
    val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
    val btnSearch = dialog.findViewById<Button>(R.id.btn_search)
    val btnClear = dialog.findViewById<Button>(R.id.btn_clear)
    val edtName = dialog.findViewById<EditText>(R.id.edt_name)
    val edtHead = dialog.findViewById<EditText>(R.id.edt_head_name)
    val edtMobile = dialog.findViewById<EditText>(R.id.edt_mobile)
    val edtMail = dialog.findViewById<EditText>(R.id.edt_mail)
    val tvMin = dialog.findViewById<TextView>(R.id.tv_min)
    val tvMax = dialog.findViewById<TextView>(R.id.tv_max)
    val rangeSeekbar = dialog.findViewById<CrystalRangeSeekbar>(R.id.rangeSeekbar)

    rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
        tvMin.text = "$minValue"
        tvMax.text = "$maxValue"
    }

    var isMale: Boolean = false
    val lstMarital = context.resources.getStringArray(R.array.marital)
    val list = ArrayList<String>()
    list.addAll(lstMarital)
    list.remove(context.getString(R.string.married))
    spMarital.setItems(list.toTypedArray())
    spMarital.setExpandTint(R.color.black)

    smartFilterViewModel.getListCityName().observeForever {
        if (it.isNotEmpty()) {
            val list = ArrayList<String>()
            list.add(context.getString(R.string.select))
            list.addAll(it)
            spCity.setItems(list.toTypedArray())
            spCity.setExpandTint(R.color.black)
        }
    }

    smartFilterViewModel.getLastName().observeForever {
        if (it.isNotEmpty()) {
            val list = ArrayList<String>()
            list.add(context.getString(R.string.select))
            list.addAll(it)
            spLname.setItems(list.toTypedArray())
            spLname.setExpandTint(R.color.black)
        }
    }

    btnMale.setOnClickListener {
        isMale = true
        btnMale.background = context.resources.getDrawable(R.drawable.round_corner_primary)
        btnMale.setTextColor(context.resources.getColor(R.color.white))
        btnFemale.background = context.resources.getDrawable(R.drawable.round_corner_white)
        btnFemale.setTextColor(context.resources.getColor(R.color.black))
    }
    btnFemale.setOnClickListener {
        isMale = false
        btnMale.background = context.resources.getDrawable(R.drawable.round_corner_white)
        btnMale.setTextColor(context.resources.getColor(R.color.black))
        btnFemale.background = context.resources.getDrawable(R.drawable.round_corner_primary)
        btnFemale.setTextColor(context.resources.getColor(R.color.white))
    }

    btnClear.setOnClickListener {
        Guru.putString("mdialog","")
        edtName.setText("")
        edtHead.setText("")
        edtMobile.setText("")
        edtMail.setText("")
        spLname.setText("")
        spCity.setText("")
        spMarital.setText("")
        btnFemale.performClick()
        rangeSeekbar.setMinStartValue(0f)
        rangeSeekbar.setMaxStartValue(100f)
        rangeSeekbar.apply()
       // Toast.makeText(context,"Filter Cleared",Toast.LENGTH_SHORT).show()
    }

    val mdialog = Guru.getString("mdialog", "")
    val mjsonObject: JSONObject
    if (!mdialog.isNullOrEmpty()) {
        mjsonObject = JSONObject(mdialog)
        if (mjsonObject.has(context.getString(R.string.first_name))) {
            val fname = mjsonObject.getString(context.getString(R.string.first_name))
            if (!fname.isNullOrEmpty()) edtName.setText(fname)
        }

        if (mjsonObject.has(context.getString(R.string.head_name))) {
            val head = mjsonObject.getString(context.getString(R.string.head_name))
            if (!head.isNullOrEmpty()) edtHead.setText(head)
        }

        if (mjsonObject.has(context.getString(R.string.mobile))) {
            val mobile = mjsonObject.getString(context.getString(R.string.mobile))
            if (!mobile.isNullOrEmpty()) edtMobile.setText(mobile)
        }

        if (mjsonObject.has(context.getString(R.string.email_address))) {
            val email = mjsonObject.getString(context.getString(R.string.email_address))
            if (!email.isNullOrEmpty()) edtMail.setText(email)
        }

        if (mjsonObject.has(context.getString(R.string.gender))) {
            val gender = mjsonObject.getString(context.getString(R.string.gender))
            if (!gender.isNullOrEmpty()) {
                if (gender == "Male") {
                    btnMale.performClick()
                } else {
                    btnFemale.performClick()
                }
            }
        }

        Coroutines.io {
            if (mjsonObject.has(context.getString(R.string.sub_cast_id))) {
                val subcastId = mjsonObject.getString(context.getString(R.string.sub_cast_id))
                if (!subcastId.isNullOrEmpty()) {
                    spLname.setText(smartFilterViewModel.getLastNameById(subcastId.toInt()))
                }
            }

            if (mjsonObject.has(context.getString(R.string.city_id))) {
                val cityid = mjsonObject.getString(context.getString(R.string.city_id))
                if (!cityid.isNullOrEmpty()) {
                    spCity.setText(smartFilterViewModel.getCityNamebyId(cityid))
                }
            }
        }

        if (mjsonObject.has(context.getString(R.string.marital_status))) {
            val status = mjsonObject.getString(context.getString(R.string.marital_status))
            if (!status.isNullOrEmpty()) spMarital.setText(status)
        }

        if (mjsonObject.has(context.getString(R.string.min_age))) {
            val minAge = mjsonObject.getString(context.getString(R.string.min_age))
            if (!minAge.isNullOrEmpty()) {
                rangeSeekbar.setMinStartValue(minAge.toFloat())
                rangeSeekbar.apply()
            }
        }

        if (mjsonObject.has(context.getString(R.string.max_age))) {
            val maxAge = mjsonObject.getString(context.getString(R.string.max_age))
            if (!maxAge.isNullOrEmpty()) {
                rangeSeekbar.setMaxStartValue(maxAge.toFloat())
                rangeSeekbar.apply()
            }
        }
    }


    ivClose.setOnClickListener { dialog.dismiss() }
    btnSearch.setOnClickListener {
        dialog.dismiss()

        val jsonObject = JSONObject()

        Coroutines.io{

            if (edtName.text.trim().isNotEmpty()) {
                jsonObject.put(context.getString(R.string.first_name), edtName.text.trim())
            }
            if (edtHead.text.trim().isNotEmpty()) {
                jsonObject.put(context.getString(R.string.head_name), edtHead.text.trim())
            }
            if (edtMobile.text.trim().isNotEmpty()) {
                jsonObject.put(context.getString(R.string.mobile), edtMobile.text.trim())
            }
            if (edtMail.text.trim().isNotEmpty()) {
                jsonObject.put(context.getString(R.string.email_address), edtMail.text.trim())
            }
            if (!spLname.text.isNullOrEmpty() && spLname.text.toString() != context.getString(R.string.select)) {
                val subCastId = smartFilterViewModel.getIdByLastName(spLname.text.toString())
                jsonObject.put(context.getString(R.string.sub_cast_id), subCastId)
            }

            if (!spCity.text.isNullOrEmpty() && spCity.text.toString() != context.getString(R.string.select)) {
                val cityId = smartFilterViewModel.getCityIdByName(spCity.text.toString())
                jsonObject.put(context.getString(R.string.city_id), cityId)
            }
            if (isMale) {
                jsonObject.put(context.getString(R.string.gender), "Male")
            } else {
                jsonObject.put(context.getString(R.string.gender), "Female")
            }
            if (!spMarital.text.isNullOrEmpty()) {
                jsonObject.put(context.getString(R.string.marital_status), spMarital.text)
            }
            jsonObject.put(context.getString(R.string.min_age), rangeSeekbar.selectedMinValue)
            jsonObject.put(context.getString(R.string.max_age), rangeSeekbar.selectedMaxValue)
            Guru.putString("mdialog", jsonObject.toString())

            Coroutines.main {
                moveToFragmentListScreen(context as FragmentActivity, jsonObject.toString())
            }

        }

    }
    dialog.show()
}

fun moveToFragmentListScreen(activity: FragmentActivity?, filter: String) {
    val fragment = MatrimonyListFragment()
    val bundle = Bundle()
    bundle.putString("filter", filter)
    fragment.arguments = bundle
    Utility.movetoFragment(activity, fragment)
}

fun getRoomMemberFromMember(member:Member):RoomMember{

    val roomMember = RoomMember(Integer.parseInt(member.id),member.headId,true,member.role,member.memberCode,member.emailAddress,
            member.mobile,member.relationId,member.subCommunityId,member.localCommunityId,member.committeeId,member.designationId,
            member.firstName,member.fatherName,member.motherName,member.subCastId,member.gender,member.address,member.localAddress,
            member.cityId,member.stateId,member.area,member.pincode,member.phone,member.matrimony,member.birthDate,member.birthTime,
            member.birthPlace,member.nativePlaceId,member.bloodGroup,member.aboutMe,member.weight,member.height,member.isSpect,member.isMangal,
            member.isShani,member.hobby,member.facebookProfile,member.expectation,member.currentActivityId,member.maritalStatus,member.marriageDate,
            member.gotraId,member.profilePic,member.isRented,member.isExpired,member.expireDate,member.isDonor,member.businessCategoryId,member.businessSubCategoryId,
            member.workDetails,member.companyName,member.businessAddress,member.businessLogo,member.website,member.educationId,member.occupationId,member?.userLat,member?.userLng,
            member.homeLat, member.homeLng, member.officeLat, member.officeLng,member.isLocationEnable,member.updatedDt)
    return roomMember
}

fun getMemberFromRoomMember(roomMember:RoomMember):Member{
    val member=Member()
    member.id= roomMember.id.toString()
    member.headId=roomMember.headId
    member.isImportant=roomMember.isImportant
    member.role=roomMember.role
    member.emailAddress=roomMember.emailAddress
    member.memberCode=roomMember.memberCode
    member.firstName=roomMember.firstName
    member.fatherName=roomMember.fatherName
    member.motherName=roomMember.motherName
    member.subCastId=roomMember.subCastId
    member.gender=roomMember.gender
    member.address=roomMember.address
    member.localAddress=roomMember.localAddress
    member.designationId=roomMember.designationId
    member.committeeId=roomMember.committeeId
    member.localCommunityId=roomMember.localCommunityId
    member.subCommunityId=roomMember.subCommunityId
    member.relationId=roomMember.relationId
    member.mobile=roomMember.mobile
    member.cityId=roomMember.cityId
    member.stateId=roomMember.stateId
    member.area=roomMember.area
    member.pincode=roomMember.pincode
    member.phone=roomMember.phone
    member.matrimony=roomMember.matrimony
    member.birthDate=roomMember.birthDate
    member.birthTime=roomMember.birthTime
    member.birthPlace=roomMember.birthPlace
    member.nativePlaceId=roomMember.nativePlaceId
    member.bloodGroup=roomMember.bloodGroup
    member.aboutMe=roomMember.aboutMe
    member.weight=roomMember.weight
    member.height=roomMember.height
    member.isSpect=roomMember.isSpect
    member.isMangal=roomMember.isMangal
    member.isShani=roomMember.isShani
    member.hobby=roomMember.hobby
    member.facebookProfile=roomMember.facebookProfile
    member.expectation=roomMember.expectation
    member.currentActivityId=roomMember.currentActivityId
    member.maritalStatus=roomMember.maritalStatus
    member.marriageDate=roomMember.marriageDate
    member.gotraId=roomMember.gotraId
    member.profilePic=roomMember.profilePic
    member.isRented=roomMember.isRented
    member.isExpired=roomMember.isExpired
    member.expireDate=roomMember.expireDate
    member.isDonor=roomMember.isDonor
    member.businessCategoryId=roomMember.businessCategoryId
    member.businessSubCategoryId=roomMember.businessSubCategoryId
    member.workDetails=roomMember.workDetails
    member.companyName=roomMember.companyName
    member.businessAddress=roomMember.businessAddress
    member.businessLogo=roomMember.businessLogo
    member.website=roomMember.website
    member.educationId=roomMember.educationId
    member.occupationId=roomMember.occupationId
    member.userLat=roomMember.userLat
    member.userLng=roomMember.userLng
    member.homeLat=roomMember.homeLat
    member.homeLng=roomMember.homeLng
    member.officeLat=roomMember.officeLat
    member.officeLng=roomMember.officeLng
    member.isLocationEnable=roomMember.isLocationEnable
    member.updatedDt=roomMember.updatedDt
    return member
}

fun createMemberListPDF(mContext:Context, lstMember: ArrayList<Member>, profileDetailViewModel: ProfileDetailViewModel)= Coroutines.main{

    var Bdate=""
    val df = SimpleDateFormat("dd.MM.yyyy h:mm a") //'at'
    val currentdate = df.format(Calendar.getInstance().time)
    val header  = "<center>  <h1><b>Community App</b></h1> </center> <object align=right>$currentdate</object><br><br>"
    var rows=header
    for(member in lstMember){

        var name = member.firstName
        var state = member.stateId
        var city = member.cityId
        if(!member.birthDate.isNullOrEmpty()){
            Bdate= Utility.changeDateFormat(member.birthDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
        }
        if(!member.subCastId.isNullOrEmpty()){
            profileDetailViewModel.selectedLastNameId = Integer.parseInt(member.subCastId)
            profileDetailViewModel.lastName.await().observeForever {
                name= member.firstName+" "+it
            }
        }

        if(!member.stateId.isNullOrEmpty()){
            profileDetailViewModel.selectedStateId = Integer.parseInt(member.stateId)
            profileDetailViewModel.stateName.await().observeForever {
                state= it
            }
        }

        if(!member.cityId.isNullOrEmpty()){
            profileDetailViewModel.selectedCityId = Integer.parseInt(member.cityId)
            profileDetailViewModel.cityName.await().observeForever {
                city= it
            }
        }

        val path=mContext.getString(R.string.base_url_thumb)+member.profilePic
        val headerImage  = "<img src=$path alt=$name>"
        val lblName = "Name: $name"
        val lblGender = "Gender: ${member.gender}"
        val lblBdate = "BirthDate: $Bdate"
        val lblFather = "FatherName: ${member.fatherName}"
        val lblMother = "MotherName: ${member.motherName}"
        val lblEmail = "Email: ${member.emailAddress}"
        val lblMobile = "Mobile: ${member.mobile}"
        val lblState = "State: $state"
        val lblCity = "City: $city"
        val lblArea = "Area: ${member.area}"
        val lblAddress = "Address: ${member.address}"
        val lblPinCode = "Pincode: ${member.pincode}"
        val lblBg = "BloodGroup: ${member.bloodGroup}"
        val lblMarital = "Marital: ${member.maritalStatus}"
        rows=rows+"<table><tr><td><b> $lblName </b></tr>" +
                "<tr><td> $headerImage </td><td> $lblGender<br> $lblMobile <br> $lblEmail<br> $lblMarital </td></tr>" +
                "<tr><td> $lblFather</td><td> $lblMother</td></tr   >"+
                "<tr><td> $lblBdate</td><td> $lblBg</td></tr>"+
                "<tr><td> $lblState</td><td> $lblCity</td></tr>"+
                "<tr><td> $lblArea</td><td> $lblPinCode</td></tr>"+
                "<tr><td colspan='2'> $lblAddress</td></tr></table><br><br>"
    }

    if(Utility.checkExternalStoragePermission(mContext)){
        createPdf(mContext,"community_${currentdate}",rows)
    }else{
        Utility.requestStoragePermission(mContext as AppCompatActivity)
    }
}

fun createMemberPDF(mContext:Context, member: Member, profileDetailViewModel: ProfileDetailViewModel) = Coroutines.main{

    //------- Main Detail---------
    var Bdate=""
    var Exdate=""
    var Mdate=""
    if(!member.birthDate.isNullOrEmpty()){
        Bdate= Utility.changeDateFormat(member.birthDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
    }
    if(!member.expireDate.isNullOrEmpty()){
        Exdate= Utility.changeDateFormat(member.expireDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
    }
    if(!member.marriageDate.isNullOrEmpty()){
        Mdate= Utility.changeDateFormat(member.marriageDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
    }

    val df = SimpleDateFormat("dd.MM.yyyy h:mm a") //'at'
    val currentdate = df.format(Calendar.getInstance().time)

    var name = member.firstName
    val gender = member.gender
    val father = member.fatherName
    val mother = member.motherName
    val email = member.emailAddress
    val mobile = member.mobile
    var state = member.stateId
    var city = member.cityId
    val area = member.area
    val address = member.address
    val pincode = member.pincode

    if(!member.subCastId.isNullOrEmpty()){
        profileDetailViewModel.selectedLastNameId = Integer.parseInt(member.subCastId)
        profileDetailViewModel.lastName.await().observeForever {
            name= member.firstName+" "+it
        }
    }

    if(!member.stateId.isNullOrEmpty()){
        profileDetailViewModel.selectedStateId = Integer.parseInt(member.stateId)
        profileDetailViewModel.stateName.await().observeForever {
            state= it
        }
    }

    if(!member.cityId.isNullOrEmpty()){
        profileDetailViewModel.selectedCityId = Integer.parseInt(member.cityId)
        profileDetailViewModel.cityName.await().observeForever {
            city= it
        }
    }

    val header  = "<center>  <h1><b>Community App</b></h1> </center> <object align=right>$currentdate</object>"
    val path=mContext.getString(R.string.base_url_thumb)+member.profilePic
    val headerImage  = "<img src=$path alt=$name>"
    val labelMain = "<b>Main Detail  </b> "
    val lblName = "Name: "
    val lblGender = "Gender: "
    val lblFather = "FatherName: "
    val lblMother = "MotherName: "
    val lblEmail = "Email: "
    val lblMobile = "Mobile: "
    val lblState = "State: "
    val lblCity = "City: "
    val lblArea = "Area: "
    val lblAddress = "Address: "
    val lblPinCode = "Pincode: "

    //------- Personal Detail---------
    var strRole=""
    if(member.role.equals("LOCAL_ADMIN")){
        strRole = "Local Admin"

    }else if(member.role.equals("SUB_ADMIN")) {
        strRole = "Sub Admin"

    }else{
        strRole = "User"
    }

    val lblPersonal = "<b> Personal </b>"
    val lblRole = "Role: "
    val lblBdate = "BirthDate: "
    val lblNative = "Native: "
    val lblEdate = "ExpireDate: "
    val lblBG = "Blood Group: "
    val lblEducation = "Eduction: "
    val lblActivity = "Current Activity: "
    val lblMarital = "Marital Status: "
    val lblMdate = "MarriageDate: "
    val lblLaddress = "Local Address: "

    val bloodGroup = member.bloodGroup
    val maritalStatus = member.maritalStatus
    val localAddress = member.localAddress
    var native = member.nativePlaceId
    var education = member.educationId
    var currentActivity = member.currentActivityId

    if(!member.nativePlaceId.isNullOrEmpty()){
        profileDetailViewModel.selectedNativeId = Integer.parseInt(member.nativePlaceId)
        profileDetailViewModel.nativeName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            native= it
        })
    }
    if(!member.educationId.isNullOrEmpty()){
        profileDetailViewModel.selectedEducationId = Integer.parseInt(member.educationId)
        profileDetailViewModel.educationName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            education= it
        })
    }

    if(!member.currentActivityId.isNullOrEmpty()){
        profileDetailViewModel.selectedActivityId = Integer.parseInt(member.currentActivityId)
        profileDetailViewModel.activityName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            currentActivity= it
        })
    }

    //------- Professional Detail---------
    val lblProfessional = "<b> Professional </b>"
    val lblLogo = "Logo: "
    val lblCompany = "Company Name: "
    val lblWorkCat = "Work Category: "
    val lblWorkSubCat = "Work Sub Category: "
    val lblOcc = "Occupation: "
    val lblWorkDetail = "Work Detail: "
    val lblWebsite = "WebSite URL: "
    val lblWorkAddr = "Work Address: "

    val companyName = member.companyName
    val bpath=mContext.getString(R.string.base_url_thumb)+member.businessLogo
    val logo  = "<img src=$bpath alt=$companyName>"
    val website = member.website
    val workDetails = member.workDetails
    val businessAddress = member.businessAddress
    var workcategory = member.businessCategoryId
    var worksubCategory = member.businessSubCategoryId
    var occupation = member.occupationId

    if(!member.businessCategoryId.isNullOrEmpty()){
        profileDetailViewModel.selectedBusinessCategoryId = Integer.parseInt(member.businessCategoryId)
        profileDetailViewModel.businessCategoryName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            workcategory= it
        })
    }

    if(!member.businessSubCategoryId.isNullOrEmpty()){
        profileDetailViewModel.selectedBusinessCategoryId = Integer.parseInt(member.businessSubCategoryId)
        profileDetailViewModel.businessSubCategoryName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            workcategory= it
        })
    }

    if(!member.businessCategoryId.isNullOrEmpty()){
        profileDetailViewModel.selectedBusinessCategoryId = Integer.parseInt(member.businessCategoryId)
        profileDetailViewModel.businessCategoryName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            worksubCategory= it
        })
    }

    if(!member.occupationId.isNullOrEmpty()){
        profileDetailViewModel.selectedOccupationId = Integer.parseInt(member.occupationId)
        profileDetailViewModel.occupationName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            occupation= it
        })
    }

    //------- Matrimony Detail---------
    val lblAbout = "About Me: "
    val lblFBurl = "Facebook Profile URL: "
    val lblBtime = "Birth Time: "
    val lblBPlace = "Birth Place: "
    val lblHobby = "Hobby: "
    val lblExpectation = "Expectation: "
    val lblWeight = "Weight: "
    val lblHeight = "Height: "
    val lblGotra = "Gotra: "
    val lblMatrimony = "<b> Matrimony </b> "

    val about = member.aboutMe
    val facebookProfile = member.facebookProfile
    val birthTime = member.birthTime
    val birthPlace = member.birthPlace
    val hobby = member.hobby
    val expectation = member.expectation
    val weight = member.weight
    val height = member.height
    var gotra = member.gotraId

    if(!member.gotraId.isNullOrEmpty()){
        profileDetailViewModel.selectedGotraId = Integer.parseInt(member.gotraId)
        profileDetailViewModel.gotraName.await().observe(mContext as AppCompatActivity, androidx.lifecycle.Observer {
            gotra= it
        })
    }

    Handler().postDelayed({
        val MainDetail =header+"<br><h3><b>"+
                lblName + name +"</b></h3><br>"+
                headerImage+"<br><br>"+
                labelMain+"<br>"+
                lblGender +gender +"<br>"+
                lblFather+father +"<br>" +
                lblMother+ mother +"<br>" +
                lblEmail+email + "<br>"+
                lblMobile +mobile + "<br>" +
                lblState +state +"<br>" +
                lblCity +city + "<br>" +
                lblArea+ area +"<br>" +
                lblAddress+address+"<br>" +
                lblPinCode+pincode

        val PersonalDetail = "<br> <br>"+lblPersonal+"<br>"+
                lblRole + strRole+"<br>"+
                lblBdate+Bdate +"<br>"+
                lblBG+bloodGroup+"<br>"+
                lblNative+native+"<br>"+
                lblEducation+education+"<br>"+
                lblActivity+currentActivity+"<br>"+
                lblEdate+Exdate+"<br>"+
                lblMarital+maritalStatus+"<br>"+
                lblMdate+Mdate+"<br>"+
                lblLaddress+localAddress

        val ProfessionalDetail = "<br> <br>"+lblProfessional+"<br>"+
                lblLogo+logo+"<br><br>"+
                lblCompany+companyName +"<br>"+
                lblOcc+occupation+"<br>"+
                lblWorkCat+workcategory+"<br>"+
                lblWorkSubCat+worksubCategory+"<br>"+
                lblWebsite+website+"<br>"+
                lblWorkDetail+workDetails+"<br>"+
                lblWorkAddr+businessAddress

        val MatrimonyDetail ="<br><br>"+lblMatrimony+"<br>"+
                lblGotra+gotra+"<br>"+
                lblAbout + about+"<br>"+
                lblBtime+birthTime+"<br>"+
                lblBPlace+birthPlace+"<br>"+
                lblHobby+hobby+"<br>"+
                lblExpectation+expectation+"<br>"+
                lblFBurl+facebookProfile +"<br>"+
                lblWeight+weight+"<br>"+
                lblHeight+height

        val MailString = MainDetail + PersonalDetail + ProfessionalDetail + MatrimonyDetail
        Log.v("ViewUtils","MailString: $MailString")

        if(Utility.checkExternalStoragePermission(mContext)){
            createPdf(mContext,name,MailString)
        }else{
            Utility.requestStoragePermission(mContext as AppCompatActivity)
        }

    },1500)

}

private fun createPdf(mContext: Context, fname: String, test: String) {

    CreatePdf(mContext)
            .setPdfName(fname)
            .openPrintDialog(false)
            .setContentBaseUrl(null)
            .setPageSize(PrintAttributes.MediaSize.ISO_A4)
            .setContent(test)
            .setFilePath(Environment.getExternalStorageDirectory().absolutePath + "/Community")
            .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                override fun onFailure(errorMsg: String) {
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(filePath: String) {
                    Log.d("Pdf Saved : ",filePath)
                    Toast.makeText(mContext, "Pdf Saved : $filePath", Toast.LENGTH_LONG).show()
                    displayPDFDialog(mContext,fname,filePath,test)
                }
            })
            .create()
}

fun displayPDFDialog(context: Context,name:String,filePath:String,content:String) {

    SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            .setTitleText("$name's Profile")
            .setContentText("You can View, Share and Print the PDF Profile")
            .setCustomImage(R.drawable.ic_app)
            .showCancelButton(true)
            .setNeutralText("Print")
            .setNeutralClickListener {sDialog ->
                CreatePdf(context)
                        .setPdfName(name)
                        .openPrintDialog(true)
                        .setContentBaseUrl(null)
                        .setPageSize(PrintAttributes.MediaSize.ISO_A4)
                        .setContent(content)
                        .setFilePath(Environment.getExternalStorageDirectory().absolutePath + "/Community").create()
            }
            .setConfirmText("Share")
            .setConfirmClickListener { sDialog ->
                shareFile(context,filePath)
            }
            .setCancelText("View")
            .setCancelClickListener {
               openPdf(context,filePath)
            }
            .show()
}


fun openPdf(context: Context,filePath: String) {

    val file = File(filePath)
    val path = Uri.fromFile(file)

    val pdfOpenintent = Intent(Intent.ACTION_VIEW);
    pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
    pdfOpenintent.setDataAndType(path, "application/pdf");
    try {
    context.startActivity(pdfOpenintent);
    }
    catch ( e: ActivityNotFoundException) {

    }

}


fun shareFile(context: Context,filePath: String){
    val file = File(filePath)
    val intent = Intent(Intent.ACTION_SEND)
    if(file.exists()) {
        val path = Uri.fromFile(file)
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, path);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Sharing File from Community App");
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing File from Community App");

        context.startActivity(Intent.createChooser(intent, "Share File Details"));
    }
}

fun shareDetails(activity: FragmentActivity?,name:String,mobile:String,email:String,area:String,address:String){

    val text = "Install your Community App\n" + "https://play.google.com/store/apps/details?id=com.krs.community \n \n" +
            "Name : "+ name +"\n" +
            "Mobile : " + mobile + "\n" +
            "Email : " + email + "\n" +
            "Area : " + area +"\n" +
            "Address : " + address

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)
    activity?.startActivity(Intent.createChooser(intent, "Choose one"))

}

