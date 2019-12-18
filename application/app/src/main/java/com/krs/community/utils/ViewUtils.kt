package com.krs.community.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.ViewUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.activity.BaseActivity
import com.krs.community.app.AppController
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG ).show()
}

fun ProgressBar.show(){
    visibility = View.VISIBLE
}

fun ProgressBar.hide(){
    visibility = View.GONE
}

fun View.snackbar(message: String,snack:Int ){
    Snackbar.make(this, message, snack).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

var logger: Logger=Logger(ViewUtils::class.java.simpleName)
fun handleCropResult(result: Intent,context:Context,image: ImageView) {
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

fun handleCropError(result: Intent,context:Context) {
    val cropError = UCrop.getError(result)
    if (cropError != null) {
        logger.error(cropError)
        Toast.makeText(context, cropError.message, Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show()
    }
}

fun startCrop(uri: Uri,context:Context) {
    val destinationFileName = "${Utility.getRandomString(10)}.jpg"
    var uCrop = UCrop.of(uri, Uri.fromFile(File(context.cacheDir, destinationFileName)))
    uCrop = advancedConfig(uCrop,context)
    uCrop.start(context as Activity)
}

fun advancedConfig(uCrop: UCrop,context:Context): UCrop {
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

fun displayNeverAskAgainDialog(context:Context) {

    SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            .setTitleText("Storage read Permission")
            .setContentText("Permission is needed to pick image from gallery for your Profile. Please permit the permission through " + "Settings screen.\n\nSelect Permissions -> Enable permission")
            .setConfirmText("Permit Manually")
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

fun promptReadPermission(context:Context) {
    if (!Utility.hasPermission(context, "READ_EXTERNAL_STORAGE")) {
        SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Storage read Permission")
                .setContentText("Permission is needed to pick image from gallery for your Profile")
                .setConfirmText("Yes, please!")
                .setCancelText("No!")
                .showCancelButton(true)
                .setConfirmClickListener { sDialog ->
                    sDialog.dismiss()
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), BaseActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION)
                    //requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage read permission is needed to pick files.", REQUEST_STORAGE_READ_ACCESS_PERMISSION);
                }
                .show()
    }
}