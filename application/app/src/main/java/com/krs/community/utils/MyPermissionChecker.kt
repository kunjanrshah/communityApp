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

class MyPermissionChecker {

    fun checkReadCallLogPermission(mContext: Context?): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_CALL_LOG)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    companion object{

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
        fun checkGPSPermission(){
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "permission required !!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        @JvmStatic
        fun checkReadStoragePermission(mContext: Context?): Boolean {
            val permissionState: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            return permissionState == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun requestStoragePermission(mActivity: AppCompatActivity?) {
            if(!checkReadStoragePermission(mActivity)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(mActivity!!, arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE), Utility.PICK_GALLERY_REQUEST)
                } else {
                    requestPermissions(mActivity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), Utility.PICK_GALLERY_REQUEST)
                }
            }
        }

        private val RequestPermissionCode = 5
        @RequiresApi(Build.VERSION_CODES.M)
        @JvmStatic
        fun checkRequestReadContactPermission(activity: Activity?) :Boolean{
            return false
        }

        private val RECORD_REQUEST_CODE = 101
        @JvmStatic
         fun checkRequestCameraPermissions(mActivity: AppCompatActivity) {
            val permission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.e("", "Permission to record denied")
                ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.CAMERA), RECORD_REQUEST_CODE)
            }
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @JvmStatic
        fun checkRequestNotificationPermissions(mActivity: AppCompatActivity) {
            val permission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.POST_NOTIFICATIONS)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.e("", "Permission to record denied")
                requestPermissions(mActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), RECORD_REQUEST_CODE)
            }
        }
    }
}