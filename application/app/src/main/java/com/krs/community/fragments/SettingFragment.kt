package com.krs.community.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar

class SettingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragmnet_settings, container, false)
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
        return root
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