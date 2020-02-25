package com.krs.community.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity.Companion.binding
import com.krs.community.databinding.FragmentChangePassBinding
import com.krs.community.listeners.ILoginListener
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.PasswordViewModel
import com.krs.community.viewmodelfactory.PasswordViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

private lateinit var passBinding: FragmentChangePassBinding

class ChangePasswordFragment : Fragment(), KodeinAware, ILoginListener {

    override val kodein by kodein()

    private lateinit var passwordViewModel: PasswordViewModel
    private val passwordViewModelFactory: PasswordViewModelFactory by instance()
    private var showCurr = true

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        passwordViewModel = ViewModelProvider(this, passwordViewModelFactory).get(PasswordViewModel::class.java)
        passwordViewModel.mLoginListener = this
        passBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_pass, container, false)

        passBinding.imgCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        Utility.changeStatusbarColor(activity, R.color.colorPrivacyPolictyBG, false)

        passBinding.btnUpdate.setOnClickListener {
            val newPass = passBinding.edtNew.text.trim()
            val currPass = passBinding.edtCurr.text.trim()
            val confirmPass = passBinding.edtConfirm.text.trim()

            if (currPass.isEmpty()) {
                Snackbar.make(passBinding.llParent, "Enter Current PIN", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (newPass.isEmpty()) {
                Snackbar.make(passBinding.llParent, "Enter New PIN", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Snackbar.make(passBinding.llParent, getString(R.string.password_mismatch), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (newPass.length < 6) {
                passBinding.llParent.snackbar(getString(R.string.make_strong_pass), Snackbar.LENGTH_LONG)
                return@setOnClickListener
            }

            val jsonObject = JSONObject()
            jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
            jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.member_id), ""))
            jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
            jsonObject.put(getString(R.string.current_password), passBinding.edtCurr.text.trim())
            jsonObject.put(getString(R.string.new_password), passBinding.edtNew.text.trim())
            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
            Utility.startSweetProgress(activity, getString(R.string.change_pin), getString(R.string.loading))
            passwordViewModel.changePassword(updated)
        }

        passBinding.tvForgot.setOnClickListener {
            val memberString = Guru.getString(getString(R.string.loginMember), "")
            val loginMember = Gson().fromJson(memberString, Member::class.java)
            SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.forgotPin))
                    .setConfirmText(getString(R.string.letMeCheck))
                    .setCancelText(getString(R.string.Cancel))
                    .setCancelClickListener {
                        it.dismissWithAnimation()
                    }
                    .setContentText(getString(R.string.pinWillsend) + " ${loginMember.emailAddress}")
                    .setConfirmClickListener {
                        it.dismissWithAnimation()
                        val jsonObject = JSONObject()
                        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                        jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.member_id), ""))
                        jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                        Utility.startSweetProgress(activity, getString(R.string.forgotPin), getString(R.string.loading))
                        passwordViewModel.forgotPassword(updated)
                    }
                    .show()
        }

        passBinding.edtCurr.setOnTouchListener(fun(_: View, event: MotionEvent): Boolean {
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= passBinding.edtCurr.right - passBinding.edtCurr.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (showCurr) {
                        passBinding.edtCurr.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_pass, 0)
                        passBinding.edtCurr.inputType = InputType.TYPE_CLASS_NUMBER or
                                InputType.TYPE_NUMBER_FLAG_DECIMAL or
                                InputType.TYPE_NUMBER_FLAG_SIGNED
                        showCurr = false
                    } else {
                        passBinding.edtCurr.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_pass, 0)
                        passBinding.edtCurr.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                        showCurr = true
                    }
                    passBinding.edtCurr.setSelection(passBinding.edtCurr.length())
                    return true
                }
            }
            return false
        })

        return passBinding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding.space.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        binding.space.visibility = View.VISIBLE
    }

    override fun userLogin(response: LoginResponse) {
        Utility.hideSweetProgress()
        Snackbar.make(passBinding.llParent, response.message, Snackbar.LENGTH_LONG).show()
    }

    override suspend fun getFailure(message: String) {
        Utility.hideSweetProgress()
        Snackbar.make(passBinding.llParent, message, Snackbar.LENGTH_LONG).show()
    }
}