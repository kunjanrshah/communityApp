package com.krs.vastipatrak.fragments

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionInflater
import android.util.Half.toFloat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.facebook.shimmer.ShimmerFrameLayout
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.krs.vastipatrak.R
import com.krs.vastipatrak.adapter.RecyclerAdapter
import com.krs.vastipatrak.interfaces.BottomNavigationViewListener
import com.krs.vastipatrak.model.DataProvider
import com.krs.vastipatrak.model.RecentProfiles
import com.krs.vastipatrak.utils.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_search_list_detail.*
import kotlinx.android.synthetic.main.fragment_search_list_detail.toolbar
import kotlinx.android.synthetic.main.fragment_search_result.*

import java.util.ArrayList

class SearchResultListFragment : BaseFragment(), View.OnClickListener {

    private var lstRecentSearch: RecyclerView? = null
    private var lstProfile: RecyclerView? = null
    private var recyclerAdapter: RecyclerAdapter<DataProvider.DataProvider1.Card>? = null
    private var mShimmerViewContainer: ShimmerFrameLayout? = null

    private val listRecentProfiles = ArrayList<RecentProfiles>()
    private val RecentProfileNames = arrayOf("Rajendra", "Tejas", "Kunjan", "Mukund", "Kushal")
    private val RecentProfileImages = intArrayOf(R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile)
    private var multiSearchView: MultiSearchView? = null

    var bottomNavListener: BottomNavigationViewListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_result, container, false)
        lstRecentSearch = rootView.findViewById(R.id.lstRecentSearch)
        multiSearchView = rootView.findViewById(R.id.multiSearchView)
        lstProfile = rootView.findViewById(R.id.lstProfile)
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
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

        setRecentSearch()
        setupList()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        mShimmerViewContainer!!.startShimmerAnimation()
        Handler().postDelayed({ multiSearchView!!.binding.imageViewSearch.performClick() }, 500)
    }

    override fun onPause() {
        mShimmerViewContainer!!.stopShimmerAnimation()
        super.onPause()
    }

    private fun setupList() {

        lstProfile!!.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = RecyclerAdapter<DataProvider.DataProvider1.Card>(DataProvider.getCardData(), this)
        lstProfile!!.adapter = recyclerAdapter
        lstProfile!!.setHasFixedSize(true)
        Handler().postDelayed({
            // stop animating Shimmer and hide the layout
            mShimmerViewContainer!!.stopShimmerAnimation()
            mShimmerViewContainer!!.visibility = View.GONE
        }, 3000)
    }


    private fun setRecentSearch() {
        listRecentProfiles.clear()
        for (i in RecentProfileNames.indices) {
            val item = RecentProfiles()
            item.cardName = RecentProfileNames[i]
            item.imageResourceId = RecentProfileImages[i]
            listRecentProfiles.add(item)
        }

        lstRecentSearch!!.setHasFixedSize(true)
        val MyLayoutManager = LinearLayoutManager(activity)
        MyLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        if ((listRecentProfiles.size > 0) and (lstRecentSearch != null)) {
            lstRecentSearch!!.adapter = RecentProfileAdapter(listRecentProfiles)
        }
        if (lstRecentSearch != null) {
            lstRecentSearch!!.layoutManager = MyLayoutManager
        }
    }

    override fun onClick(v: View) {
        val fragmentTransaction = initFragmentTransaction(v)
        val copy = view!!.copyViewImage()
        copy.y += activity!!.myAppBar.height
        ll_root.addView(copy)
        view!!.visibility = View.INVISIBLE
        startAnimation(copy, fragmentTransaction)

    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = lstProfile!!.getChildAdapterPosition(view)
        val detailsFragment = SearchResultDetailFragment.newInstance(positions, adapterPosition)

        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, SearchResultDetailFragment.TAG)
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

    private fun startAnimation(view: View, fragmentTransaction: FragmentTransaction?) {
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(lstProfile)
            //withStartAction { animateToolbarElevation(true) }
            withEndAction {
                lstProfile!!.visibility = View.INVISIBLE

                val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

                view.animate().y(229f).start()
                fragmentTransaction!!.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right)
                fragmentTransaction?.commitAllowingStateLoss()

                /*activity?.myAppBar!!.animate()
                        .translationY(-activity!!.myAppBar.height.toFloat())
                        .alpha(0f)
                        .setDuration(1000)
                        .withStartAction {
                            bottomNavListener?.hideBottomNavigationView()
                            //details_toolbar_transition_helper.animate().translationY(0f).setDuration(500).start()
                        }
                        .withEndAction {
                            fragmentTransaction!!.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            fragmentTransaction?.commitAllowingStateLoss()
                        }
                        .start()*/
            }
            start()
        }
    }

  /*  private fun animateToolbarElevation(animateOut: Boolean) {
        var valueFrom = resources.getDimension(R.dimen.toolbar_elevation)
        var valueTo = 0f
        if (!animateOut) {
            valueTo = valueFrom
            valueFrom = 0f
        }
        ValueAnimator.ofFloat(valueFrom, valueTo).setDuration(1000).apply {
            startDelay = 0
            addUpdateListener { activity?.card_toolbar!!.cardElevation = it.animatedValue as Float }
            start()
        }
    }*/

    inner class RecentProfileAdapter internal constructor(private val list: ArrayList<RecentProfiles>) : RecyclerView.Adapter<RecentActivityViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentActivityViewHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_menu, parent, false)
            return RecentActivityViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecentActivityViewHolder, position: Int) {

            holder.titleTextView.text = list[position].cardName
            holder.coverImageView.setImageResource(list[position].imageResourceId)
            holder.coverImageView.tag = list[position].imageResourceId

        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    inner class RecentActivityViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var titleTextView: TextView
        var coverImageView: ImageView

        init {
            titleTextView = v.findViewById(R.id.titleTextView)
            coverImageView = v.findViewById(R.id.coverImageView)
        }
    }

}
