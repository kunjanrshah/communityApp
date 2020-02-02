package com.krs.community.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.squti.guru.Guru
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.databinding.FragmentStatisticsBinding
import com.krs.community.listeners.StatisticsListener
import com.krs.community.responses.StatisticResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.StatisticsViewModel
import com.krs.community.viewmodelfactory.StatisticsViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class StatisticFragment : Fragment(), KodeinAware,StatisticsListener {

    private lateinit var statisticsViewModel: StatisticsViewModel
    private val factory: StatisticsViewModelFactory by instance()
    private lateinit var binding: FragmentStatisticsBinding
    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.bg_gray, false)
        }

        statisticsViewModel = ViewModelProviders.of(this,factory).get(StatisticsViewModel::class.java)
        statisticsViewModel.mStatisticsListener=this
        binding.spCity.setOnItemClickListener {
            statisticsViewModel.selectedCityName = binding.spCity.text.toString().trim()
            Coroutines.main {
                statisticsViewModel.cityId.await().observe(this, Observer {
                    getStatisticsResult(it)
                })
            }
        }

        Coroutines.main {
            statisticsViewModel.lstCityName.await().observe(this, Observer {
                    binding.spCity.setItems(it.toTypedArray())
                    binding.spCity.setExpandTint(R.color.black)
            })
        }

        getStatisticsResult(0)
        binding.ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        return binding.root
    }

    private fun getStatisticsResult(cityId:Int){
        val jsonObject=JSONObject()
        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id),""))
        jsonObject.put(getString(R.string.access_token),Guru.getString(getString(R.string.access_token),""))
        jsonObject.put("city_id",cityId)
        val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
        binding.shimmerViewContainer.startShimmerAnimation()
        binding.shimmerViewContainer.visibility=View.VISIBLE
        binding.scroll.visibility=View.GONE
        statisticsViewModel.getStatistics(updated)
    }


    override fun getStatistics(response: StatisticResponse) {
        binding.scroll.visibility=View.VISIBLE
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility=View.GONE

        if(response.success){
            binding.tvVillage.text = response.data.totalVillages.toString()
            binding.tvFamily.text =response.data.totalFamily.toString()
            binding.tvMembers.text = response.data.totalMembers.toString()
            binding.tvMale.text = response.data.totalMale.toString()
            binding.tvFemale.text =response.data.totalFemale.toString()
            binding.tvUnMale.text =response.data.totalUnmarriedMale.toString()
            binding.tvUnFemale.text =response.data.totalUnmarriedFemale.toString()
        }
    }

    override fun getFailure(message: String) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility=View.GONE
        //toast(activity,message)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        DashboardActivity.binding.space.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }


}