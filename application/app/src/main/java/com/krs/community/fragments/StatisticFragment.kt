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
import androidx.lifecycle.ViewModelProvider
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar.make
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.databinding.FragmentStatisticsBinding
import com.krs.community.listeners.StatisticsListener
import com.krs.community.model.Member
import com.krs.community.responses.StatisticResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.StatisticsViewModel
import com.krs.community.viewmodelfactory.StatisticsViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class StatisticFragment : Fragment(), KodeinAware, StatisticsListener {

    private lateinit var statisticsViewModel: StatisticsViewModel
    private val factory: StatisticsViewModelFactory by instance<StatisticsViewModelFactory>()
    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var snackbar: Snackbar
    private lateinit var loginMem: Member
    var subCommId = 0
    var localCommId = 0
    var cityId = 0

    override val kodein by kodein()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, StatisticFragment::class.simpleName)
        mApp.facebookAnalytics(context, StatisticFragment::class.simpleName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.bg_gray, false)
        }

        statisticsViewModel = ViewModelProvider(this, factory).get(StatisticsViewModel::class.java)
        statisticsViewModel.mStatisticsListener = this

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        loginMem = Gson().fromJson<Member>(loginuser, Member::class.java)
        setScreenLayout()

        AppController.mApplication.connectionLiveData.observeForever {
            it?.let {
                if (it) {
                    if (snackbar.isShown) {
                        snackbar.dismiss()
                        getStatisticsResult()
                    }
                } else {
                    binding.shimmerViewContainer.stopShimmerAnimation()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.scroll.visibility = View.VISIBLE
                    snackbar.show()
                }
            }
        }
        return binding.root
    }

    private fun setScreenLayout() {

        if (loginMem.role == getString(R.string.super_admin)) {
            binding.rlSubComm.visibility = View.VISIBLE
        } else {
            binding.rlSubComm.visibility = View.GONE
            Coroutines.main {
                statisticsViewModel.getLocalComm(loginMem.subCommunityId.toInt()).observe(activity as AppCompatActivity, Observer {
                    binding.spLocalComm.clear()
                    val lstValue = ArrayList<String>()
                    lstValue.add("All Local Community")
                    lstValue.addAll(it.toTypedArray())
                    binding.spLocalComm.setItems(lstValue.toTypedArray())
                    binding.spLocalComm.setExpandTint(R.color.black)
                })
            }
        }

        binding.spSubComm.setOnItemClickListener {
            Coroutines.io {
                subCommId = statisticsViewModel.getSubIdByName(binding.spSubComm.text.toString())
                localCommId = 0
                cityId = 0
                binding.spLocalComm.setSelection(0)
                binding.spCity.setSelection(0)

                getStatisticsResult()
                Coroutines.main {
                    statisticsViewModel.getLocalComm(subCommId).observe(activity as AppCompatActivity, Observer {
                        binding.spLocalComm.clear()
                        val lstValue = ArrayList<String>()
                        lstValue.add("All Local Community")
                        lstValue.addAll(it.toTypedArray())
                        binding.spLocalComm.setItems(lstValue.toTypedArray())
                        binding.spLocalComm.setExpandTint(R.color.black)
                    })
                }
            }
        }

        binding.spLocalComm.setOnItemClickListener {
            Coroutines.io {
                localCommId = statisticsViewModel.getLocalIdByName(binding.spLocalComm.text.toString())
                getStatisticsResult()
            }
        }

        binding.spCity.setOnItemClickListener {
            Coroutines.io {
                cityId = statisticsViewModel.getCityIdByName(binding.spCity.text.toString().trim())
                getStatisticsResult()
            }
        }

        Coroutines.io {
            Coroutines.main {
                statisticsViewModel.getSubComm().observe(activity as AppCompatActivity, Observer {
                    binding.spSubComm.clear()
                    val lstSubCommValue = ArrayList<String>()
                    lstSubCommValue.add("All Sub Community")
                    lstSubCommValue.addAll(it.toTypedArray())
                    binding.spSubComm.setItems(lstSubCommValue.toTypedArray())
                    binding.spSubComm.setExpandTint(R.color.black)
                })
            }
        }

        getStatisticsResult()
        snackbar = make(binding.flRoot, getString(R.string.check_network), LENGTH_INDEFINITE)
        binding.ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
    }

    private fun getStatisticsResult() {
        if (isNetworkConnected(activity as AppCompatActivity)) {
            val jsonObject = JSONObject()
            jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
            jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
            jsonObject.put(getString(R.string.city_id), cityId)
            jsonObject.put(getString(R.string.local_community_id), localCommId)

            if (loginMem.role != getString(R.string.super_admin)) {
                jsonObject.put(getString(R.string.sub_community_id), loginMem.subCommunityId)
            } else {
                jsonObject.put(getString(R.string.sub_community_id), subCommId)
            }
            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
            Coroutines.main {
                binding.shimmerViewContainer.startShimmerAnimation()
                binding.shimmerViewContainer.visibility = View.VISIBLE
                binding.scroll.visibility = View.GONE
            }
            statisticsViewModel.getStatistics(updated)
        }
    }

    override fun getStatistics(response: StatisticResponse) {
        binding.scroll.visibility = View.VISIBLE
        Utility.hideKeyboard(activity)
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
        if (response.success) {
            binding.tvFamily.text = response.data.totalFamily.toString()
            binding.tvMembers.text = response.data.totalMembers.toString()
            binding.tvMale.text = response.data.totalMale.toString()
            binding.tvFemale.text = response.data.totalFemale.toString()
            binding.tvUnMale.text = response.data.totalUnmarriedMale.toString()
            binding.tvUnFemale.text = response.data.totalUnmarriedFemale.toString()
            binding.tvInMale.text = response.data.totalInterestedMale.toString()
            binding.tvInFemale.text = response.data.totalInterestedFemale.toString()

            if (response.data.totalVillages == null) {
                binding.llVillages.visibility = View.GONE
            } else {
                binding.llVillages.visibility = View.VISIBLE
                binding.tvVillage.text = response.data.totalVillages.toString()
            }
//            Coroutines.io {
//                val lst = ArrayList<String>()
//                for (city in response.cities) {
//                    lst.add(city.cityId)
//                }
//                if (lst.isNotEmpty()) {
//                    val list = statisticsViewModel.getCityDistinctName(lst)
//                    Coroutines.main {
//                        list.observe(activity as AppCompatActivity, Observer {
//                            binding.spCity.clear()
//                            val lstValue = ArrayList<String>()
//                            lstValue.add("All Villages")
//                            lstValue.addAll(it)
//                            binding.spCity.setItems(lstValue.toTypedArray())
//                            binding.spCity.setExpandTint(R.color.black)
//                        })
//                    }
//                }
//            }
        }
    }

    override fun getFailure(message: String) {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
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
