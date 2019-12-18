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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.ericliu.asyncexpandablelist.CollectionView
import com.ericliu.asyncexpandablelist.async.AsyncExpandableListView
import com.ericliu.asyncexpandablelist.async.AsyncExpandableListViewCallbacks
import com.ericliu.asyncexpandablelist.async.AsyncHeaderViewHolder
import com.facebook.shimmer.ShimmerFrameLayout
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.databinding.FragmentBrowseCityBinding
import com.krs.community.entities.City
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.BrowseCityViewModel
import com.krs.community.viewmodel.BrowseCityViewModelFactory
import kotlinx.android.synthetic.main.fragment_browse_city.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class BrowseByCityFragment : Fragment(), AsyncExpandableListViewCallbacks<String, City>,KodeinAware {

    private lateinit var mAsyncExpandableListView: AsyncExpandableListView<String, City>
    private lateinit var shimmer_view_container: ShimmerFrameLayout

    private var inventory: CollectionView.Inventory<String, City>? = null
    private val factory: BrowseCityViewModelFactory by instance()
    internal var browseCityViewModel: BrowseCityViewModel? = null
    override val kodein by kodein()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorBG, false)
        }

        val binding: FragmentBrowseCityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_browse_city, container, false)
        val view=  binding.root

        browseCityViewModel = ViewModelProviders.of(this,factory).get(BrowseCityViewModel::class.java)

        mAsyncExpandableListView = view.findViewById(R.id.asyncExpandableCollectionView)
        mAsyncExpandableListView.setCallbacks(this)

        inventory = CollectionView.Inventory()

        shimmer_view_container=view.findViewById(R.id.shimmer_view_container)
        shimmer_view_container.startShimmerAnimation()
        shimmer_view_container.visibility = View.VISIBLE

        getStatesFromDB()

        view.iv_cancel.setOnClickListener { v -> Utility.movetoFragment(activity, DashboardFragment()) }
        return view
    }

    private fun getStatesFromDB()=Coroutines.main{
        browseCityViewModel?.getStates()?.observeForever {
            for ((index, stateData) in it.withIndex()) {
                val group = inventory?.newGroup(index) //Integer.parseInt(stateData.id)// groupOrdinal is the smallest, displayed first
                group?.headerItem = stateData.name
            }
            shimmer_view_container.stopShimmerAnimation()
            shimmer_view_container.visibility = View.GONE
            mAsyncExpandableListView.updateInventory(inventory)
        }
    }

    private fun getCityFromDB(stateId:Int)=Coroutines.main{
        browseCityViewModel?.getCitiesByState(stateId)?.observeForever {
            val groupOrdinal=stateId-1
            mAsyncExpandableListView.onFinishLoadingGroup(groupOrdinal,it)
        }
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        DashboardActivity.spaceNavigationView.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
        DashboardActivity.spaceNavigationView.visibility = View.VISIBLE
    }

    override fun onStartLoadingGroup(groupOrdinal: Int) {
        getCityFromDB(groupOrdinal+1)
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
        cityItemHolder.city_id=item.id.toString()
        if (item.name.equals("other", ignoreCase = true)) {
            cityItemHolder.textViewDevider.visibility = View.GONE
        } else {
            cityItemHolder.textViewDevider.visibility = View.VISIBLE
        }
    }

    inner class CityItemHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {

        internal val textViewCity: TextView
        internal val textViewDevider: View
        internal var city_id:String=""

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { Log.d(TAG, "Element $position clicked.") }

            textViewCity = v.findViewById(R.id.tv_city)
            textViewDevider= v.findViewById(R.id.view_devider)
            val row_city = v.findViewById<LinearLayout>(R.id.row_city)

            row_city.setOnClickListener { v1 ->
                val fragment=SearchCityResult()
                val mBundle = Bundle()
                mBundle.putString("city_name", textViewCity.text.toString())
                mBundle.putString("city_id", city_id)
                fragment.setArguments(mBundle)
                Utility.movetoFragment(activity, fragment)
             }
        }
    }

    class StateViewHolder internal constructor(v: View, groupOrdinal: Int, asyncExpandableListView: AsyncExpandableListView<*, *>) : AsyncHeaderViewHolder(v, groupOrdinal, asyncExpandableListView), AsyncExpandableListView.OnGroupStateChangeListener {

        internal val textView: TextView
        private val mProgressBar: ProgressBar
        private val ivExpansionIndicator: ImageView

        init {
            textView = v.findViewById(R.id.title)
            mProgressBar = v.findViewById(R.id.progressBar)
            mProgressBar.indeterminateDrawable.setColorFilter(-0x1, android.graphics.PorterDuff.Mode.MULTIPLY)
            ivExpansionIndicator = v.findViewById(R.id.ivExpansionIndicator)
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
}
