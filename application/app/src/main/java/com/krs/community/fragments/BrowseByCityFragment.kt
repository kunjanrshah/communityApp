package com.krs.community.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.ericliu.asyncexpandablelist.CollectionView
import com.ericliu.asyncexpandablelist.async.AsyncExpandableListView
import com.ericliu.asyncexpandablelist.async.AsyncExpandableListViewCallbacks
import com.ericliu.asyncexpandablelist.async.AsyncHeaderViewHolder
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentBrowseCityBinding
import com.krs.community.entities.City
import com.krs.community.entities.States
import com.krs.community.listeners.IbrowseCityRecordsListener
import com.krs.community.model.Member
import com.krs.community.model.SearchByCityModel
import com.krs.community.responses.CityResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.BrowseCityViewModel
import com.krs.community.viewmodelfactory.BrowseCityViewModelFactory
import kotlinx.android.synthetic.main.fragment_browse_city.view.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class BrowseByCityFragment : Fragment(), AsyncExpandableListViewCallbacks<String, City>, KodeinAware, IbrowseCityRecordsListener {

    private lateinit var mAsyncExpandableListView: AsyncExpandableListView<String, City>
    private lateinit var shimmer_view_container: ShimmerFrameLayout

    private var inventory: CollectionView.Inventory<String, City>? = null
    private val factory: BrowseCityViewModelFactory by instance<BrowseCityViewModelFactory>()
    internal var browseCityViewModel: BrowseCityViewModel? = null
    override val kodein by kodein()
    private var selectedGroupOrdinal = 0
    lateinit var sortedList: ArrayList<States>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorBG, false)
        }

        val binding: FragmentBrowseCityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_browse_city, container, false)
        val view = binding.root

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, BrowseByCityFragment::class.simpleName)
        mApp.facebookAnalytics(context, BrowseByCityFragment::class.simpleName)

        browseCityViewModel = ViewModelProvider(this, factory).get(BrowseCityViewModel::class.java)
        browseCityViewModel?.ibrowseCityRecordsListener = this
        mAsyncExpandableListView = view.findViewById(R.id.asyncExpandableCollectionView)
        mAsyncExpandableListView.setCallbacks(this)

        inventory = CollectionView.Inventory()

        shimmer_view_container = view.findViewById(R.id.shimmer_view_container)
        shimmer_view_container.startShimmerAnimation()
        shimmer_view_container.visibility = View.VISIBLE

        getStatesFromDB()

        view.iv_cancel.setOnClickListener { v -> Utility.movetoFragment(activity, DashboardFragment()) }
        return view
    }

    private fun getStatesFromDB() = Coroutines.main {
        browseCityViewModel?.getStates()?.observeForever {
            sortedList = ArrayList<States>()
            if (BuildConfig.FLAVOR == "medk") {
                sortedList.add(States(14, "Ahmedabad-Gujarat"))
                sortedList.add(States(17, "North-Gujarat"))
                sortedList.add(States(15, "South-Gujarat"))
                sortedList.add(States(16, "Saurashtra-Gujarat"))
                sortedList.add(States(2, "Mumbai-Maharashtra"))
                sortedList.add(States(3, "Marathvada-Maharashtra"))
                sortedList.add(States(4, "Konkan-Maharashtra"))
                sortedList.add(States(8, "Rajasthan"))
            }

            for (States in it) {
                if (!sortedList.contains(States)) {
                    sortedList.add(States)
                }
            }

            for ((index, stateData) in sortedList.withIndex()) {
                val group = inventory?.newGroup(index) //Integer.parseInt(stateData.id)// groupOrdinal is the smallest, displayed first
                group?.headerItem = stateData.name
            }
            shimmer_view_container.stopShimmerAnimation()
            shimmer_view_container.visibility = View.GONE
            mAsyncExpandableListView.updateInventory(inventory)
        }
    }

    private fun fetchCities(stateId: Int) = Coroutines.main {

        val loginMember = Guru.getString(getString(R.string.loginMember), "")
        val loginMem = Gson().fromJson(loginMember, Member::class.java)

        val mJson = JSONObject()
        mJson.put(getString(R.string.state_id), stateId)

        if (loginMem.role != activity?.getString(R.string.super_admin)) {
            mJson.put(getString(R.string.sub_community_id), loginMem.subCommunityId)
        } else {
            mJson.put(getString(R.string.sub_community_id), 0)
        }

        val updated = JsonParser().parse(mJson.toString()) as JsonObject
        browseCityViewModel?.getCitiesByState(updated)
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        DashboardActivity.binding.space.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }

    override fun onStartLoadingGroup(groupOrdinal: Int) {
        selectedGroupOrdinal = groupOrdinal
        val stateId = sortedList[groupOrdinal].id
        fetchCities(stateId)
    }

    override fun newCollectionHeaderView(context: Context, groupOrdinal: Int, parent: ViewGroup): AsyncHeaderViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.header_row_item_async, parent, false)
        return StateViewHolder(v, groupOrdinal, mAsyncExpandableListView)
    }

    override fun newCollectionItemView(context: Context, groupOrdinal: Int, parent: ViewGroup): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.text_row_item_async, parent, false)
        return CityItemHolder(v)
    }

    override fun bindCollectionHeaderView(context: Context, holder: AsyncHeaderViewHolder, groupOrdinal: Int, headerItem: String) {
        val stateViewHolder = holder as StateViewHolder
        stateViewHolder.textView.text = headerItem
    }

    override fun bindCollectionItemView(context: Context, holder: RecyclerView.ViewHolder, i: Int, item: City) {
        val cityItemHolder = holder as CityItemHolder
        cityItemHolder.textViewCity.text = item.name
        cityItemHolder.tvCount.text = "" + item.count
        cityItemHolder.cityId = item.id.toString()
        if (item.name.equals("other", ignoreCase = true)) {
            cityItemHolder.textViewDevider.visibility = View.GONE
        } else {
            cityItemHolder.textViewDevider.visibility = View.VISIBLE
        }
    }

    inner class CityItemHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {

        internal val textViewCity = v.findViewById<TextView>(R.id.tv_city)
        internal val tvCount: TextView = v.findViewById<TextView>(R.id.tv_count)
        internal val textViewDevider = v.findViewById<View>(R.id.view_devider)
        internal var cityId: String = ""

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { Log.d(TAG, "Element $position clicked.") }


            val rowCity = v.findViewById<LinearLayout>(R.id.row_city)

            rowCity.setOnClickListener { v1 ->
                val fragment = SearchCityResult()
                val mBundle = Bundle()
                mBundle.putString("city_name", textViewCity.text.toString())
                mBundle.putString("city_id", cityId)
                fragment.arguments = mBundle
                Utility.movetoFragment(activity, fragment)
            }
        }
    }

    class StateViewHolder internal constructor(v: View, groupOrdinal: Int, asyncExpandableListView: AsyncExpandableListView<*, *>) : AsyncHeaderViewHolder(v, groupOrdinal, asyncExpandableListView), AsyncExpandableListView.OnGroupStateChangeListener {

        internal val textView: TextView = v.findViewById(R.id.title)
        private val mProgressBar: ProgressBar = v.findViewById(R.id.progressBar)
        private val ivExpansionIndicator: ImageView = v.findViewById(R.id.ivExpansionIndicator)

        init {
            mProgressBar.indeterminateDrawable.setColorFilter(-0x1, android.graphics.PorterDuff.Mode.MULTIPLY)
        }

        override fun onGroupStartExpending() {
            mProgressBar.visibility = View.VISIBLE
            ivExpansionIndicator.visibility = View.GONE
        }

        override fun onGroupExpanded() {
            mProgressBar.visibility = View.GONE
            ivExpansionIndicator.visibility = View.VISIBLE
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_up)
        }

        override fun onGroupCollapsed() {
            mProgressBar.visibility = View.GONE
            ivExpansionIndicator.visibility = View.VISIBLE
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_down)
        }
    }

    override fun getCitiesByState(response: CityResponse) {
        if (response.success) {
            mAsyncExpandableListView.onFinishLoadingGroup(selectedGroupOrdinal, response.data)
        }
    }

    override fun getSearchRecords(data: SearchByCityModel) {

    }

    override suspend fun getFailure(message: String) {

    }
}
