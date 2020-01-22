package com.krs.community.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.adapter.SmartFilterAdapter
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import okhttp3.internal.Internal.instance
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ExpandableFilterListFragment : Fragment() , KodeinAware {

    private var previousGroup = -1
    private var adapter: SmartFilterAdapter? = null
    private val TAG = ExpandableFilterListFragment::class.java.simpleName

    private lateinit var expandableListView: ExpandableListView
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance()

    override val kodein by kodein()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_smart_search, container, false)

        profileDetailViewModel = ViewModelProviders.of(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)

        expandableListView = rootView.findViewById(R.id.lst_expandable)
        expandableListView.setGroupIndicator(null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }
        adapter = SmartFilterAdapter(activity as AppCompatActivity,profileDetailViewModel)
        expandableListView.setAdapter(adapter)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle("Smart Filter")
        setListener()
        val iv_cancel = rootView.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        val iv_filter = rootView.findViewById<ImageView>(R.id.iv_filter)
        iv_filter.setOnClickListener { v: View? -> Utility.movetoFragment(activity, FiltersFragment()) }
        val tv_done = rootView.findViewById<TextView>(R.id.tv_done)
        tv_done.setOnClickListener { v: View? ->
            Utility.hideKeyboard(activity)
            Handler().postDelayed({ adapter!!.openBottomSheetDailog() }, 250)
        }
        expandableListView.setOnScrollListener(object : OnScrollObserver() {
            override fun onScrollUp() {
                Log.d(TAG, "onScrollUp")
                DashboardActivity.binding.space.visibility = View.VISIBLE
            }

            override fun onScrollDown() {
                Log.d(TAG, "onScrollDown")
                DashboardActivity.binding.space.visibility = View.GONE
            }
        })
        return rootView
    }

    override fun onResume() {
        super.onResume()
        Utility.hideKeyboard(activity)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    // Setting different listeners to expandablelistview
    fun setListener() { // This listener will show toast on group click
        expandableListView.setOnGroupClickListener { listview: ExpandableListView?, view: View?, group_pos: Int, id: Long ->
            if (group_pos == 0 || group_pos == 7) {
                return@setOnGroupClickListener true
            } else {
                adapter!!.storeFieldsValues()
                return@setOnGroupClickListener false
            }
        }
        expandableListView.setOnGroupExpandListener { groupPosition: Int ->
            adapter!!.previousGroup = groupPosition
            if (groupPosition != previousGroup) // Collapse the expanded group
                expandableListView.collapseGroup(previousGroup)
            previousGroup = groupPosition
        }
        // This listener will show toast on child click
        expandableListView.setOnChildClickListener { listview: ExpandableListView?, view: View?, groupPos: Int, childPos: Int, id: Long ->
            Toast.makeText(activity, "You clicked : " + adapter!!.getChild(groupPos, childPos), Toast.LENGTH_SHORT).show()
            false
        }
    }

    inner abstract class OnScrollObserver : AbsListView.OnScrollListener {
        var last = 0
        var control = true
        abstract fun onScrollUp()
        abstract fun onScrollDown()
        override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
        override fun onScroll(view: AbsListView, current: Int, visibles: Int, total: Int) {
            if (current < last && !control) {
                onScrollUp()
                control = true
            } else if (current > last && control) {
                onScrollDown()
                control = false
            }
            last = current
        }
    }
}