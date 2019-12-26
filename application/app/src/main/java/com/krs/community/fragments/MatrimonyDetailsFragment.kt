package com.krs.community.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.krs.community.R
import com.krs.community.databinding.FragmentMatrimonyDetailsBinding
import com.krs.community.model.Member
import com.krs.community.utils.NumberPadTimePickerDialogFragment
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.ProfileDetailViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class MatrimonyDetailsFragment : Fragment(), KodeinAware {

    lateinit var binding: FragmentMatrimonyDetailsBinding
    private lateinit var member: Member
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val factory: ProfileDetailViewModelFactory by instance()
    var numberOfLines=5

    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_matrimony_details, container, false)

        profileDetailViewModel = ViewModelProviders.of(this, factory).get(ProfileDetailViewModel::class.java)
        member = arguments?.getSerializable(getString(R.string.member)) as Member


        binding.edtAbout.setText(member.aboutMe)
        binding.edtFbUrl.setText(member.facebookProfile)
        binding.txtBtime.setText(member.birthTime)
        binding.edtBplace.setText(member.birthPlace)
        binding.edtHobby.setText(member.hobby)
        binding.edtExpectation.setText(member.expectation)
        binding.edtWeight.setText(member.weight)
        binding.edtHeight.setText(member.height)
        binding.chkIsMangal.isChecked = member.isMangal.equals("1")
        binding.chkIsShani.isChecked = member.isShani.equals("1")
        binding.chkGlass.isChecked = member.isSpect.equals("1")
        binding.chkInterested.isChecked = member.matrimony.toString().toLowerCase().equals("yes")

        binding.txtBtime.setOnClickListener {
            NumberPadTimePickerDialogFragment.newInstance(mListener).show(activity!!.getSupportFragmentManager(), "bottom_sheet")
        }

        binding.edtAbout.addTextChangedListener(object: TextWatcher {
            private var text: String? = null
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lineCount: Int = binding.edtAbout.getLineCount()
                if (lineCount > numberOfLines) {
                    binding.edtAbout.setText(text)
                }
            }
        })

        binding.edtAbout.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val editTextLineCount: Int = (v as EditText).getLineCount()
                if (editTextLineCount >= numberOfLines) return@OnKeyListener true
            }
            false
        })
        
        return binding.getRoot()
    }

    fun getSaveData(jsonObject:JSONObject){
        try{
            jsonObject.put(getString(R.string.about_me),binding.edtAbout.text.trim())
            jsonObject.put(getString(R.string.facebook_profile),binding.edtFbUrl.text.trim())
            jsonObject.put(getString(R.string.birth_time),binding.txtBtime.text.trim())
            jsonObject.put(getString(R.string.birth_place),binding.edtBplace.text.trim())
            jsonObject.put(getString(R.string.hobby),binding.edtHobby.text.trim())
            jsonObject.put(getString(R.string.expectation),binding.edtExpectation.text.trim())
            jsonObject.put(getString(R.string.weight),binding.edtWeight.text.trim())
            jsonObject.put(getString(R.string.height),binding.edtHeight.text.trim())

            if(binding.chkInterested.isChecked){
                jsonObject.put(getString(R.string.matrimony),"YES")
            }else{
                jsonObject.put(getString(R.string.matrimony),"NO")
            }

            if(binding.chkGlass.isChecked){
                jsonObject.put(getString(R.string.is_spect),1)
            }else{
                jsonObject.put(getString(R.string.is_spect),0)
            }

            if(binding.chkIsMangal.isChecked){
                jsonObject.put(getString(R.string.is_mangal),1)
            }else{
                jsonObject.put(getString(R.string.is_mangal),0)
            }

            if(binding.chkIsShani.isChecked){
                jsonObject.put(getString(R.string.is_shani),1)
            }else{
                jsonObject.put(getString(R.string.is_shani),0)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private val mListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

        var hour:String=hourOfDay.toString()
        var min:String=minute.toString()
        if(hour.length==1){
            hour="0$hour"
        }
        if(min.length==1){
            min="0$min"
        }

        binding.txtBtime.setText("$hour:$min")
    }
}