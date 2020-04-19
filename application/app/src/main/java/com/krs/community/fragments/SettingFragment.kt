package com.krs.community.fragments

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

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, SettingFragment::class.simpleName)
        mApp.facebookAnalytics(context, SettingFragment::class.simpleName)

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

        // val isShowCallLog = Utility.checkReadCallLogPermission((activity as AppCompatActivity))
        // val isShowCallPhone = Utility.checkReadPhoneStatePermission((activity as AppCompatActivity))

        // switchDialog?.isOn = isShow && isShowCallPhone

        switchDialog?.setOnClickListener {

            if (switchDialog?.isOn!!) {
                Guru.putBoolean(getString(R.string.isdialogshow), false)
            } else {
                Guru.putBoolean(getString(R.string.isdialogshow), true)
                //  permissionCheck(isShowCallPhone)

                SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Help")
                        .setContentText("To enjoy this feature some mobile need to enable AutoStart mode")
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
    private fun permissionCheck(showCallPhone: Boolean) {
        if (showCallPhone) {
            switchDialog?.isOn = false
        } else {
            switchDialog?.isOn = true
            Utility.requestPermissions(activity as AppCompatActivity)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty()) {
                    val callAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (!callAccepted) {
                        Snackbar.make(view!!, "Permission Required for Phone Call Dialog Feature", Snackbar.LENGTH_LONG).show()
                        switchDialog?.isOn = false
                    }
                }
            }
            else -> {
                if (grantResults.isNotEmpty()) {
                    val callAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (!callAccepted) {
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