package com.krs.community.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.databinding.FragmentMatrimonyBinding
import com.krs.community.jrspinner.JRSpinner
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.utils.moveToFragmentListScreen
import com.krs.community.utils.openFilter
import com.krs.community.viewmodel.MatrimonySearchViewModel
import com.krs.community.viewmodel.MatrimonySearchViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MatrimonyFragment : Fragment(), KodeinAware {

    private lateinit var binding:FragmentMatrimonyBinding
    override val kodein by kodein()
    private val factory: MatrimonySearchViewModelFactory by instance()
    private lateinit var matrimonySearchViewModel: MatrimonySearchViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_matrimony, container, false)
        matrimonySearchViewModel = ViewModelProviders.of(this,factory).get(MatrimonySearchViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        binding.ivCancel.setOnClickListener { Utility.movetoFragment(activity, DashboardFragment()) }

        binding.edtName.setOnTouchListener { _: View?, event: MotionEvent ->
            val RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if ((event.rawX+35) >= binding.edtName.right - binding.edtName.compoundDrawables[RIGHT].bounds.width()) {
                    activity?.let { openFilter(it,matrimonySearchViewModel) }
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.btnSearch.setOnClickListener {
            if(binding.edtName.text.trim().isNotEmpty()){
                val jsonObject=JSONObject()
                jsonObject.put("name",binding.edtName.text.trim())
                moveToFragmentListScreen(activity,jsonObject.toString())
            }else{
                binding.edtName.error="Enter name please"
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
        DashboardActivity.spaceNavigationView.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        DashboardActivity.spaceNavigationView.visibility = View.VISIBLE
    }
}