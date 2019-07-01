package com.krs.community.fragments

import android.os.Bundle
import android.os.Handler
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.krs.community.R
import com.krs.community.adapter.RecyclerAdapter
import com.krs.community.model.DataProvider
import com.krs.community.utils.Utility
import com.krs.community.utils.copyViewImage
import com.krs.community.utils.supportsLollipop
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_search_result.*


class SearchListFragment : Fragment(), View.OnClickListener {

    private var lstProfile: RecyclerView? = null
    private var recyclerAdapter: RecyclerAdapter<DataProvider.DataProvider1.Card>? = null
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private var multiSearchView: MultiSearchView? = null
    lateinit var view1:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_result, container, false)
        view1=rootView
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
        Handler().postDelayed({
            Utility.hideKeyboard(activity)
        }, 1000)
    }



    override fun onPause() {
        mShimmerViewContainer!!.stopShimmerAnimation()
        super.onPause()
    }

    private fun setupList() {

        lstProfile!!.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = RecyclerAdapter<DataProvider.DataProvider1.Card>(DataProvider.getCardData(),null, this@SearchListFragment)
        lstProfile!!.adapter = recyclerAdapter
        lstProfile!!.setHasFixedSize(true)
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
        startAnimation(copy, fragmentTransaction)
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = lstProfile!!.getChildAdapterPosition(view)
        val detailsFragment = HeaderDetailFragment.newInstance(positions, adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, HeaderDetailFragment.TAG)
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

        //   fragmentTransaction!!.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right)
        fragmentTransaction?.commitAllowingStateLoss()
        //  Bungee.fade(context);


        /* AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
             setTarget(lstProfile)
             //withStartAction { animateToolbarElevation(true) }
             withEndAction {
                 lstProfile!!.visibility = View.INVISIBLE

                 val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

                 view.animate().y(229f).start()
                // fragmentTransaction!!.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right)
                 fragmentTransaction?.commitAllowingStateLoss()
                 Bungee.fade(context);
                 *//*activity?.myAppBar!!.animate()
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
                        .start()*//*
            }
            start()
        }*/
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




}
