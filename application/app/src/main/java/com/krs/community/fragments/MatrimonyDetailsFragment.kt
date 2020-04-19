package com.krs.community.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentMatrimonyDetailsBinding
import com.krs.community.model.Member
import com.krs.community.utils.NumberPadTimePickerDialogFragment
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MatrimonyDetailsFragment : Fragment(), KodeinAware {

    lateinit var binding: FragmentMatrimonyDetailsBinding
    private lateinit var member: Member
    private lateinit var loginMem: Member
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    var numberOfLines = 5

    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_matrimony_details, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, MatrimonyDetailsFragment::class.simpleName)
        mApp.facebookAnalytics(context, MatrimonyDetailsFragment::class.simpleName)

        profileDetailViewModel = ViewModelProvider(this, factory).get(ProfileDetailViewModel::class.java)
        member = arguments?.getSerializable(getString(R.string.member)) as Member

        val loginMember = Guru.getString(getString(R.string.loginMember), "")
        loginMem = Gson().fromJson(loginMember, Member::class.java)
        if (member.id == loginMem.id || member.headId == loginMem.id || loginMem.role.toString().toLowerCase() != "user") {
            binding.chkInterested.isClickable = true
            binding.chkGlass.isClickable = true
            binding.chkIsMangal.isClickable = true
            binding.chkIsShani.isClickable = true
            binding.edtAbout.isFocusable = true
            binding.edtFbUrl.isFocusable = true
            binding.edtBplace.isFocusable = true
            binding.edtHobby.isFocusable = true
            binding.edtExpectation.isFocusable = true
            binding.edtWeight.isFocusable = true
            binding.edtHeight.isFocusable = true
        } else {
            binding.chkInterested.isClickable = false
            binding.chkGlass.isClickable = false
            binding.chkIsMangal.isClickable = false
            binding.chkIsShani.isClickable = false
            binding.edtAbout.isFocusable = false
            binding.edtFbUrl.isFocusable = false
            binding.edtFbUrl.movementMethod = LinkMovementMethod.getInstance()
            binding.edtBplace.isFocusable = false
            binding.edtHobby.isFocusable = false
            binding.edtExpectation.isFocusable = false
            binding.edtWeight.isFocusable = false
            binding.edtHeight.isFocusable = false
        }

        binding.edtAbout.setText(member.aboutMe)
        if (!member.facebookProfile.isNullOrEmpty()) {
            val spannable: Spannable = SpannableString(member.facebookProfile)
            Linkify.addLinks(spannable, Linkify.WEB_URLS)
            val text: CharSequence = TextUtils.concat(spannable, "\u200B")
            binding.edtFbUrl.setText(text)
        } else {
            binding.edtFbUrl.setText(member.facebookProfile)
        }

        binding.txtBtime.text = member.birthTime
        binding.edtBplace.setText(member.birthPlace)
        binding.edtHobby.setText(member.hobby)
        binding.edtExpectation.setText(member.expectation)
        binding.edtWeight.setText(member.weight)
        binding.edtHeight.setText(member.height)
        binding.chkIsMangal.isChecked = member.isMangal.equals("1")
        binding.chkIsShani.isChecked = member.isShani.equals("1")
        binding.chkGlass.isChecked = member.isSpect.equals("1")
        binding.chkInterested.isChecked = member.matrimony.toString().toLowerCase().equals("yes")
        binding.chkInterested.setOnClickListener {

            SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Matrimony")
                    .setContentText("Are you interested for Matrimony?")
                    .setConfirmText("Interested")
                    .setCancelText("No,Please")
                    .setCustomImage(R.drawable.ic_medk)
                    .showCancelButton(true)
                    .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                        sweetAlertDialog.dismissWithAnimation()
                        binding.chkInterested.isChecked = true
                    }.setCancelClickListener {
                        it.dismissWithAnimation()
                        binding.chkInterested.isChecked = false
                    }
                    .show()
        }
        binding.txtBtime.setOnClickListener {
            if (member.id == loginMem.id || member.headId == loginMem.id || loginMem.role.toString().toLowerCase() != "user") {
                NumberPadTimePickerDialogFragment.newInstance(mListener).show(activity!!.supportFragmentManager, getString(R.string.bottomSheet))
            }
        }

        binding.edtAbout.addTextChangedListener(object : TextWatcher {
            private var text: String? = null
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lineCount: Int = binding.edtAbout.lineCount
                if (lineCount > numberOfLines) {
                    binding.edtAbout.setText(text)
                }
            }
        })

        binding.edtAbout.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val editTextLineCount: Int = (v as EditText).lineCount
                if (editTextLineCount >= numberOfLines) return@OnKeyListener true
            }
            false
        })

        return binding.root
    }

    fun getSaveData(jsonObject: JSONObject) {
        try {
            jsonObject.put(getString(R.string.about_me), binding.edtAbout.text.trim())
            jsonObject.put(getString(R.string.facebook_profile), binding.edtFbUrl.text.trim())
            jsonObject.put(getString(R.string.birth_time), binding.txtBtime.text.trim())
            jsonObject.put(getString(R.string.birth_place), binding.edtBplace.text.trim())
            jsonObject.put(getString(R.string.hobby), binding.edtHobby.text.trim())
            jsonObject.put(getString(R.string.expectation), binding.edtExpectation.text.trim())
            jsonObject.put(getString(R.string.weight), binding.edtWeight.text.trim())
            jsonObject.put(getString(R.string.height), binding.edtHeight.text.trim())

            if (binding.chkInterested.isChecked) {
                jsonObject.put(getString(R.string.matrimony), "YES")
            } else {
                jsonObject.put(getString(R.string.matrimony), "NO")
            }

            if (binding.chkGlass.isChecked) {
                jsonObject.put(getString(R.string.is_spect), 1)
            } else {
                jsonObject.put(getString(R.string.is_spect), 0)
            }

            if (binding.chkIsMangal.isChecked) {
                jsonObject.put(getString(R.string.is_mangal), 1)
            } else {
                jsonObject.put(getString(R.string.is_mangal), 0)
            }

            if (binding.chkIsShani.isChecked) {
                jsonObject.put(getString(R.string.is_shani), 1)
            } else {
                jsonObject.put(getString(R.string.is_shani), 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

        var hour: String = hourOfDay.toString()
        var min: String = minute.toString()
        if (hour.length == 1) {
            hour = "0$hour"
        }
        if (min.length == 1) {
            min = "0$min"
        }

        binding.txtBtime.text = "$hour:$min"
    }
}