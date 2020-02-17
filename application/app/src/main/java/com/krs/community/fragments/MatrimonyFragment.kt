package com.krs.community.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.databinding.FragmentMatrimonyBinding
import com.krs.community.utils.Utility
import com.krs.community.utils.moveToFragmentListScreen
import com.krs.community.utils.openFilter
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.SmartFilterViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MatrimonyFragment : Fragment(), KodeinAware {

    private lateinit var binding:FragmentMatrimonyBinding
    override val kodein by kodein()
    private val factory: SmartFilterViewModelFactory by instance()
    private lateinit var smartFilterViewModel: SmartFilterViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_matrimony, container, false)
        smartFilterViewModel = ViewModelProvider(this, factory).get(SmartFilterViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        binding.ivCancel.setOnClickListener { Utility.backNavigation(activity) }

        binding.edtName.setOnTouchListener { _: View?, event: MotionEvent ->
            val RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if ((event.rawX+35) >= binding.edtName.right - binding.edtName.compoundDrawables[RIGHT].bounds.width()) {
                    activity?.let { openFilter(it,smartFilterViewModel) }
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.edtName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.btnSearch.performClick()
                true
            }
            false
        }

        binding.btnSearch.setOnClickListener {
            if(binding.edtName.text.trim().isNotEmpty()){
                val jsonObject=JSONObject()
                jsonObject.put(getString(R.string.first_name),binding.edtName.text.trim())
                moveToFragmentListScreen(activity,jsonObject.toString())
            }else{
                binding.edtName.error=getString(R.string.enterName)
            }

        }
        binding.btnSkip.setOnClickListener {
            moveToFragmentListScreen(activity,"")
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        DashboardActivity.binding.space.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }
}