package com.krs.community.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.MemoryFile
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.ViewUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.activity.BaseActivity
import com.krs.community.app.AppController
import com.krs.community.entities.RoomMember
import com.krs.community.fragments.MatrimonyListFragment
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.model.Member
import com.krs.community.viewmodel.SmartFilterViewModel
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import org.json.JSONObject
import java.io.File


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

        try {

            val f = File(resultUri.path.toString())

            /*runOnUiThread {
                var bmp1: Bitmap? = null
                try {
                    bmp1 = Utility.getBitmap(this, f)
                    str_profile_hash = Utility.getBase64(bmp1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }*/
            // img_cancel!!.visibility = View.VISIBLE
            //  binding.imgProfile.setImageURI(resultUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
            .setTitleText("Storage read Permission")
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


fun pickFromGallery(context: FragmentActivity) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= 23) {
            promptReadPermission(context)
        }
    } else {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), BaseActivity.PICK_GALLERY_REQUEST)
    }
}

fun promptReadPermission(context: Context) {
    if (!Utility.hasPermission(context, "READ_EXTERNAL_STORAGE")) {
        SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Storage read Permission")
                .setContentText("Permission is needed to pick image from gallery for your Profile")
                .setConfirmText("Yes, please!")
                .setCancelText("No!")
                .setCustomImage(R.drawable.ic_app)
                .showCancelButton(true)
                .setConfirmClickListener { sDialog ->
                    sDialog.dismiss()
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), BaseActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION)
                    //requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage read permission is needed to pick files.", REQUEST_STORAGE_READ_ACCESS_PERMISSION);
                }
                .show()
    }
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

    var isMale: Boolean = true
    val lstMarital = context.resources.getStringArray(R.array.marital)
    val list = ArrayList<String>()
    list.addAll(lstMarital)
    list.remove(context.getString(R.string.married))
    spMarital.setItems(list.toTypedArray())
    spMarital.setExpandTint(R.color.black)

    smartFilterViewModel.getListCityName().observeForever {
        if (it.isNotEmpty()) {
            val list = ArrayList<String>()
            list.add(context.getString(R.string.Select))
            list.addAll(it)
            spCity.setItems(list.toTypedArray())
            spCity.setExpandTint(R.color.black)
        }
    }

    smartFilterViewModel.getLastName().observeForever {
        if (it.isNotEmpty()) {
            val list = ArrayList<String>()
            list.add(context.getString(R.string.Select))
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
        rangeSeekbar.setMinStartValue(0f)
        rangeSeekbar.setMaxStartValue(100f)
        rangeSeekbar.apply()
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
            if (!spLname.text.isNullOrEmpty() && spLname.text.toString() != context.getString(R.string.Select)) {
                val subCastId = smartFilterViewModel.getIdByLastName(spLname.text.toString())
                jsonObject.put(context.getString(R.string.sub_cast_id), subCastId)
            }

            if (!spCity.text.isNullOrEmpty() && spCity.text.toString() != context.getString(R.string.Select)) {
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
        }

        Coroutines.main {
            moveToFragmentListScreen(context as FragmentActivity, jsonObject.toString())
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

fun getRoomMember(member:Member):RoomMember{

    val roomMember = RoomMember(Integer.parseInt(member.id),true,member.role,member.memberCode,member.emailAddress,
            member.mobile,member.relationId,member.subCommunityId,member.localCommunityId,member.committeeId,member.designationId,
            member.firstName,member.fatherName,member.motherName,member.subCastId,member.gender,member.address,member.localAddress,
            member.cityId,member.stateId,member.area,member.pincode,member.phone,member.matrimony,member.birthDate,member.birthTime,
            member.birthPlace,member.nativePlaceId,member.bloodGroup,member.aboutMe,member.weight,member.height,member.isSpect,member.isMangal,
            member.isShani,member.hobby,member.facebookProfile,member.expectation,member.currentActivityId,member.maritalStatus,member.marriageDate,
            member.gotraId,member.profilePic,member.isRented,member.isExpired,member.expireDate,member.isDonor,member.businessCategoryId,member.businessSubCategoryId,
            member.workDetails,member.companyName,member.businessAddress,member.businessLogo,member.website,member.educationId,member.occupationId,member.userLat,member.userLng,
            member.homeLat,member.homeLng,member.officeLat,member.officeLng,member.isLocationEnable,member.updatedDt)
    return roomMember
}