package com.krs.community.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionInflater
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.krs.community.R
import com.krs.community.adapter.RecyclerAdapter
import com.krs.community.model.DataProvider
import com.krs.community.model.Message
import com.krs.community.utils.Utility
import com.krs.community.utils.copyViewImage
import com.krs.community.utils.supportsLollipop
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import java.util.ArrayList


class SearchListFragment : Fragment(), View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        getInbox()
    }

    private var rv_search: RecyclerView? = null
    private var recyclerAdapter: RecyclerAdapter<DataProvider.DataProvider1.Card>? = null
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private var multiSearchView: MultiSearchView? = null
    private var iv_cancel: ImageView? = null
    lateinit var view1:View
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var ll_title: LinearLayout? = null
    private val messages = ArrayList<Message>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_result, container, false)
        view1=rootView
        multiSearchView = rootView.findViewById(R.id.multiSearchView)
        rv_search = rootView.findViewById(R.id.rv_search)
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        iv_cancel = rootView.findViewById(R.id.iv_cancel)
        swipeRefreshLayout = rootView.findViewById<View>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout!!.setOnRefreshListener(this)
        ll_title = rootView.findViewById(R.id.ll_title)
        actionModeCallback = ActionModeCallback()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity,R.color.white,false)
        }

        (activity as AppCompatActivity).supportActionBar!!.title = "Smart Search"
        multiSearchView!!.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener {
            override fun onTextChanged(i: Int, charSequence: CharSequence) {
                // Toast.makeText(getActivity(), "onTextChanged", Toast.LENGTH_SHORT).show();
            }

            override fun onSearchComplete(i: Int, charSequence: CharSequence) {
                Toast.makeText(activity, "onSearchComplete", Toast.LENGTH_SHORT).show()
            }

            override fun onSearchItemRemoved(i: Int) {
                Toast.makeText(activity, "onSearchItemRemoved", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(i: Int, charSequence: CharSequence) {
                Toast.makeText(activity, "onItemSelected", Toast.LENGTH_SHORT).show()
            }
        })

        iv_cancel?.setOnClickListener {
            Utility.movetoFragment(activity,DashboardFragment())
        }

        getInbox()
        setupList()

        return rootView
    }

    override fun onStart() {
        super.onStart()
        mShimmerViewContainer!!.startShimmerAnimation()
        Handler().postDelayed({
            multiSearchView!!.binding.imageViewSearch.performClick()
        }, 100)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        Handler().postDelayed({
            Utility.hideKeyboard(activity)
        }, 1500)
    }

    private fun getInbox() {
        swipeRefreshLayout!!.setRefreshing(true)
        messages.clear()

        for (i in 0..19) {
            val message = Message()
            message.id = 1
            message.isImportant = false
            message.message = "Now android supports multiple voice recogonization"
            message.picture = "https://api.androidhive.info/json/google.png"
            message.isRead = false
            message.timestamp = "10:30 AM"
            message.from = "Google Alerts"
            message.subject = "Google Alert - android"
            message.color = Utility.getRandomMaterialColor(activity!!, "400")
            messages.add(message)
        }

        recyclerAdapter?.notifyDataSetChanged()
        swipeRefreshLayout?.setRefreshing(false)
    }

    override fun onPause() {
        (activity as AppCompatActivity).supportActionBar!!.show()
        mShimmerViewContainer!!.stopShimmerAnimation()
        super.onPause()
    }

    private fun setupList() {

        rv_search!!.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = RecyclerAdapter<DataProvider.DataProvider1.Card>(DataProvider.getCardData(),null, this@SearchListFragment)
        rv_search!!.adapter = recyclerAdapter
        rv_search!!.setHasFixedSize(true)
        Handler().postDelayed({
            // stop animating Shimmer and hide the layout
            mShimmerViewContainer!!.stopShimmerAnimation()
            mShimmerViewContainer!!.visibility = View.GONE
        }, 3000)
    }



    override fun onClick(v: View) {
        val fragmentTransaction = initFragmentTransaction(v)
        val copy = view!!.copyViewImage()
        copy.y += activity!!.myAppBar.height
        ll_root.addView(copy)
        view!!.visibility = View.INVISIBLE
        fragmentTransaction?.commitAllowingStateLoss()
       // startAnimation(copy, fragmentTransaction)
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = rv_search!!.getChildAdapterPosition(view)
        val detailsFragment = FamilyDetailFragment.newInstance(positions, adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, FamilyDetailFragment.TAG)
                ?.addToBackStack(null)

        supportsLollipop {
            val transition = TransitionInflater.from(context)
                    .inflateTransition(R.transition.shared_element_transition)
            detailsFragment.sharedElementEnterTransition = transition

            transaction
                    ?.addSharedElement(view, view.transitionName)
            //  ?.addSharedElement(details_toolbar_transition_helper, details_toolbar_transition_helper.transitionName)
        }
        return transaction
    }


    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout!!.setEnabled(false)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            /* ViewGroup   decorView = (ViewGroup) getActivity().getWindow().getDecorView().findViewById(R.id.action_mode_bar);
            decorView.setBackgroundColor(getResources().getColor(R.color.colorBG));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(),R.color.colorBG,true);
            }*/

            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_delete -> {
                    // delete all the selected messages
                    deleteMessages()
                    mode.finish()
                    return true
                }

                else -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            recyclerAdapter?.clearSelections()
            swipeRefreshLayout!!.setEnabled(true)
            actionMode = null
            ll_title!!.setVisibility(View.VISIBLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rv_search?.post(Runnable {
                recyclerAdapter!!.resetAnimationIndex()
                // mAdapter.notifyDataSetChanged();
            })
        }
    }

    private fun deleteMessages() {
        recyclerAdapter!!.resetAnimationIndex()
        val selectedItemPositions = recyclerAdapter!!.getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            recyclerAdapter!!.removeData(selectedItemPositions.get(i))
        }
        recyclerAdapter?.notifyDataSetChanged()
    }
}
