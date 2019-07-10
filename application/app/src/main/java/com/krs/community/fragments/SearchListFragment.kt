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
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.adapter.RecyclerAdapter
import com.krs.community.model.Message
import com.krs.community.utils.Utility
import com.krs.community.utils.copyViewImage
import com.krs.community.utils.supportsLollipop
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.OnItemClickListener
import com.orhanobut.dialogplus.ViewHolder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_search_result.*


class SearchListFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RecyclerAdapter.RecyclerAdapterListener {
    override fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    override fun onIconImportantClicked(position: Int) {
        // Star icon is clicked,
        // mark the message as important
        val message = messages[position]
        message.isImportant = !message.isImportant
        messages[position] = message
        recyclerAdapter!!.notifyDataSetChanged()
    }

    override fun onMessageRowClicked(position: Int, v: View) {

        if (recyclerAdapter!!.getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {

            val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            fragmentTransaction?.commitAllowingStateLoss()
            // startAnimation(copy, fragmentTransaction)

        }
    }

    override fun onClick(v: View) {
        val position = rv_search!!.getChildAdapterPosition(v)

        if (recyclerAdapter!!.getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {
            // read the message which removes bold from the row
            val message = messages[position]
            message.isRead = true
            messages[position] = message
            recyclerAdapter!!.notifyDataSetChanged()

            val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            fragmentTransaction?.commitAllowingStateLoss()
            // startAnimation(copy, fragmentTransaction)
        }
    }

    override fun onRowLongClicked(position: Int) {
        // long press is performed, enable action mode
        ll_title!!.setVisibility(View.GONE)
        enableActionMode(position)
    }

    override fun onRefresh() {
        getInbox()
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        recyclerAdapter!!.toggleSelection(position)
        val count = recyclerAdapter!!.getSelectedItemCount()

        if (count == 0) {
            actionMode!!.finish()
            ll_title!!.setVisibility(View.VISIBLE)
        } else {
            ll_title!!.setVisibility(View.GONE)
            actionMode!!.setTitle(count.toString())
            actionMode!!.invalidate()
        }
    }

    private var rv_search: RecyclerView? = null
    private var recyclerAdapter: RecyclerAdapter<MutableList<Message>>? = null
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private var multiSearchView: MultiSearchView? = null
    private var iv_cancel: ImageView? = null
    lateinit var view1: View
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var ll_title: LinearLayout? = null
    private val messages = ArrayList<Message>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_result, container, false)
        view1 = rootView
        multiSearchView = rootView.findViewById(R.id.multiSearchView)
        rv_search = rootView.findViewById(R.id.rv_search)
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        iv_cancel = rootView.findViewById(R.id.iv_cancel)
        swipeRefreshLayout = rootView.findViewById<View>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout!!.setOnRefreshListener(this)
        ll_title = rootView.findViewById(R.id.ll_title)
        actionModeCallback = ActionModeCallback()

        val iv_atoz = rootView.findViewById(R.id.iv_atoz) as ImageView
        iv_atoz.setOnClickListener {

            val adapter: AtoZBottomAdapter =AtoZBottomAdapter(context);
            val dialog = DialogPlus.newDialog(context)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            dialog.show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        (activity as AppCompatActivity).supportActionBar!!.title = "Smart Search"
        multiSearchView!!.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener {
            override fun onTextChanged(index: Int, s: CharSequence) {
                // Toast.makeText(getActivity(), "onTextChanged", Toast.LENGTH_SHORT).show();
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                Toast.makeText(activity, "onSearchComplete", Toast.LENGTH_SHORT).show()
            }

            override fun onSearchItemRemoved(index: Int) {
                Toast.makeText(activity, "onSearchItemRemoved", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(index: Int, s: CharSequence) {
                Toast.makeText(activity, "onItemSelected", Toast.LENGTH_SHORT).show()
            }
        })

        iv_cancel?.setOnClickListener {
            Utility.movetoFragment(activity, DashboardFragment())
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
        }, 2000)
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
        recyclerAdapter = RecyclerAdapter(activity, messages, null, this@SearchListFragment, this)
        rv_search!!.adapter = recyclerAdapter
        rv_search!!.setHasFixedSize(true)
        Handler().postDelayed({
            // stop animating Shimmer and hide the layout
            mShimmerViewContainer!!.stopShimmerAnimation()
            mShimmerViewContainer!!.visibility = View.GONE
        }, 3000)
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
            rv_search?.post({
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
