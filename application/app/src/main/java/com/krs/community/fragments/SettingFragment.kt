package com.krs.community.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.angads25.toggle.widget.LabeledSwitch
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.judemanutd.autostarter.AutoStartPermissionHelper
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar


class SettingFragment : Fragment() {
    private val PERMISSION_REQUEST_READ_PHONE_STATE = 1
    private val PERMISSION_REQUEST_READ_PHONE_STATE_CALL = 2
    protected var switchDialog: LabeledSwitch? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragmnet_settings, container, false)

        val mApp =(activity as AppCompatActivity). applicationContext as AppController
        mApp.FirebaseAnalytics(context, SettingFragment::class.simpleName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorBG, false)
        }
        val llParent = root.findViewById<LinearLayout>(R.id.ll_parent)
        val imgCancel = root.findViewById<ImageView>(R.id.img_cancel)
        imgCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }

        val llChangePass = root.findViewById<LinearLayout>(R.id.ll_change_pass)
        llChangePass.setOnClickListener { v: View? -> Utility.movetoFragment(activity, ChangePasswordFragment()) }

        val llPrivacy = root.findViewById<LinearLayout>(R.id.ll_privacy)
        llPrivacy.setOnClickListener { v: View? ->
            llParent.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_LONG)
            return@setOnClickListener
            Utility.movetoFragment(activity, PrivacyPolicyFragment())
        }

        switchDialog = root.findViewById<LabeledSwitch>(R.id.switch_dialog)
        val isShow = Guru.getBoolean(getString(R.string.isdialogshow), false)

        val isShowCallLog= Utility.CallLogPermission((activity as AppCompatActivity))
        val isShowCallPhone= Utility.CallPhonePermission((activity as AppCompatActivity))

        if (isShow && isShowCallLog && isShowCallPhone){
            switchDialog?.isOn = Utility.CallLogPermission((activity as AppCompatActivity))
        }else{
            switchDialog?.isOn = false
        }


        switchDialog?.setOnClickListener {

            if (switchDialog?.isOn!!) {
                Guru.putBoolean(getString(R.string.isdialogshow), false)
            } else {
                Guru.putBoolean(getString(R.string.isdialogshow), true)
                Guru.putBoolean(getString(R.string.isdialogApi), true)
                // permissionCheck(isShow,isShowCallLog,isShowCallPhone)

                SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Help")
                        .setContentText("Enjoy this feature some mobile need to enable AutoStart mode")
                        .setConfirmText("Yes,Please")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(activity!!)) {
                                AutoStartPermissionHelper.getInstance().getAutoStartPermission(activity!!)
                            }
                        }
                        .setCancelClickListener {
                            it.dismissWithAnimation()
                        }
                        .setCancelText("No Need")
                        .show()
            }
        }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionCheck(show: Boolean, showCallLog: Boolean, showCallPhone: Boolean) {
        if (show && showCallLog && showCallPhone){
            switchDialog?.isOn = Utility.CallLogPermission((activity as AppCompatActivity))
        }else{
            switchDialog?.isOn = false
            PhoneCallPermission()
            CallPermission()
        }
    }

    private fun PhoneCallPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || (activity as AppCompatActivity).checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun CallPermission() {

        if ((activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED || (activity as AppCompatActivity).checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
            requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE_CALL)
        }
    }


    /*fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_PHONE_STATE -> if (grantResults.size > 0) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted && cameraAccepted)
                    Snackbar.make(view!!, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show() else {
                    Snackbar.make(view!!, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show()

                }
            }
        }
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {PERMISSION_REQUEST_READ_PHONE_STATE ->
        {
            if (grantResults.size > 0) {
                val CallAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (CallAccepted)
                // Snackbar.make(view!!, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show()
                else {
                    Snackbar.make(view!!, "Permission Required for Phone Call Dialog Feature", Snackbar.LENGTH_LONG).show()
                    switchDialog?.isOn = false
                }
            }

        }else -> {
            if (grantResults.size > 0) {
                val CallAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (CallAccepted)
                // Snackbar.make(view!!, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show()
                else {
                    Snackbar.make(view!!, "Permission Required for Incoming Call Dialog Feature", Snackbar.LENGTH_LONG).show()
                    switchDialog?.isOn = false
                }
            }
        }
        }
    }



    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}