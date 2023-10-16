package com.krs.community.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.facebook.FacebookSdk.getApplicationContext
import com.zfdang.multiple_images_selector.ImagesSelectorActivity

class MyPermissionChecker {

    fun checkReadCallLogPermission(mContext: Context?): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_CALL_LOG)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        @JvmStatic
        fun checkReadExternalStoragePermission(mContext: Context?): Boolean {
            val permissionState: Int
            permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                ActivityCompat.checkSelfPermission(
                    mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            return permissionState == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun checkReadPhoneStatePermission(mContext: Context?): Boolean {
            val permissionState = ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_PHONE_STATE)
            return permissionState == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun requestFineLocationPermission(mActivity: AppCompatActivity?) {
            ActivityCompat.requestPermissions(
                mActivity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Utility.FINE_LOCATION_REQUEST
            )
        }
        @JvmStatic
        fun checkFineLocationPermission(mContext: Context?): Boolean {
            val permissionState = ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION)
            return permissionState == PackageManager.PERMISSION_GRANTED
        }
        @JvmStatic
        fun checkExternalStoragePermission(mContext: Context?): Boolean {
            val permissionState: Int
            val permissionState1: Int
            permissionState1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE)
            }
          //  val permissionState2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionState = if (permissionState1 == 0 /*&& permissionState2 == 0*/) {
                0
            } else {
                1
            }
            return permissionState == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun checkGPSPermission(){
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "permission required !!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        @JvmStatic
        fun requestReadStoragePermission(mActivity: AppCompatActivity?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(
                    mActivity!!, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), Utility.PICK_GALLERY_REQUEST
                )
            } else {
                requestPermissions(
                    mActivity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Utility.PICK_GALLERY_REQUEST
                )
            }
        }
        private val RequestPermissionCode = 5
        @RequiresApi(Build.VERSION_CODES.M)
        @JvmStatic
        fun enableRuntimePermission(activity: Activity?) :Boolean{

                if ((activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED || (activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CONTACTS)
                    requestPermissions(activity,permissions, RequestPermissionCode)
                    return false
                } else {
                    return true
                }
        }
        private val RECORD_REQUEST_CODE = 101
        @JvmStatic
         fun setupPermissions(mActivity: AppCompatActivity) {
            val permission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.e("", "Permission to record denied")
                ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.CAMERA), RECORD_REQUEST_CODE)
            }
        }

        @JvmStatic
        fun requestStoragePermission(mActivity: AppCompatActivity?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    mActivity!!, arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), Utility.EXTERNAL_STORAGE_REQUEST
                )
            } else {
                ActivityCompat.requestPermissions(
                    mActivity!!, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), Utility.EXTERNAL_STORAGE_REQUEST
                )
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    fun requestPermissions(mActivity: AppCompatActivity?) {
        if ( /*ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED*/ActivityCompat.checkSelfPermission(
                mActivity!!, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                mActivity, Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                mActivity, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                mActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mActivity, arrayOf( /*Manifest.permission.READ_CALL_LOG,*/
                    Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION
                ), Utility.READ_CALL_LOG
            )
        }
    }
}