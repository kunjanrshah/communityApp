package com.krs.vastipatrak.adapter

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.app.PendingIntent.getActivity
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView

import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.google.android.material.appbar.AppBarLayout
import com.krs.vastipatrak.R
import com.krs.vastipatrak.activity.DashboardActivity
import com.krs.vastipatrak.fragments.SearchResultDetailFragment
import com.krs.vastipatrak.utils.Utility
import com.krs.vastipatrak.utils.copyViewImage
import com.krs.vastipatrak.utils.withEndAction
import com.krs.vastipatrak.utils.withStartAction
import com.luseen.spacenavigation.SpaceNavigationView
import java.lang.invoke.ConstantCallSite

class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    override fun onBindViewHolder(h: ViewHolder, position: Int) {

        val holder = h as ViewHolder

        if (mDataSet != null && 0 <= position && position < mDataSet.size) {
            val data = mDataSet[position]

            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            binderHelper.bind(holder.swipeLayout, data)

            // Bind your data here
            holder.bind(data)
        }


    }

    private val mContext: DashboardActivity
    internal val lstProfile: RecyclerView
    private val mDataSet: MutableList<String>?
    private var card_view: CardView? = null
    private var mCoordinatorLayout: CoordinatorLayout? = null
    private var bottom_navigation: SpaceNavigationView? = null
    private var details_toolbar_transition_helper: View? = null

    constructor(mContext: DashboardActivity, lstProfile: RecyclerView, mDataSet: MutableList<String>?) : super() {
        this.mContext = mContext
        this.lstProfile = lstProfile
        this.mDataSet = mDataSet
        this.binderHelper = ViewBinderHelper()
        mInflater = LayoutInflater.from(mContext)
        card_view = mContext.findViewById(R.id.card_view) as CardView
        mCoordinatorLayout=mContext.findViewById(R.id.coordinator_layout)
        bottom_navigation =mContext.findViewById(R.id.space)
        details_toolbar_transition_helper =mContext.findViewById(R.id.details_toolbar_transition_helper)

    }


    private val mInflater: LayoutInflater
    private val binderHelper: ViewBinderHelper

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val swipeLayout: SwipeRevealLayout
        private val frontLayout: View
        private val deleteLayout: View
        private val textView: TextView

        init {
            swipeLayout = itemView.findViewById<View>(R.id.swipe_layout) as SwipeRevealLayout
            frontLayout = itemView.findViewById(R.id.front_layout)
            deleteLayout = itemView.findViewById(R.id.delete_layout)
            textView = itemView.findViewById<View>(R.id.text) as TextView

        }

        internal fun bind(data: String) {
            deleteLayout.setOnClickListener {
                mDataSet!!.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)

            }

            // textView.setText(data);

            frontLayout.setOnClickListener { view ->

                val fragmentTransaction = initFragmentTransaction(view)
                val copy = view.copyViewImage()
                copy.y += Utility.getToolbarHeight(mContext)
                mCoordinatorLayout?.addView(copy)
                view.visibility = View.INVISIBLE
                startAnimation(copy, fragmentTransaction)

                /*val displayText = "$data clicked"
                Toast.makeText(mContext, displayText, Toast.LENGTH_SHORT).show()
                Log.d("RecyclerAdapter", displayText)*/
            }
        }
    }


     private fun startAnimation(view: View, fragmentTransaction: FragmentTransaction?) {
         AnimatorInflater.loadAnimator(mContext, R.animator.main_list_animator).apply {
             setTarget(lstProfile)



             withStartAction { if (card_view!!.cardElevation > 0) animateToolbarElevation(true) }
             withEndAction {
                 lstProfile.visibility = View.INVISIBLE

                 val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

                 view.animate().y(toY).start()

                 card_view!!.animate()
                         .translationY(-Utility.getToolbarHeight(mContext).toFloat())
                         .alpha(0f)
                         .setDuration(600)
                         .withStartAction {
                             hideBottomNavigationView()
                             details_toolbar_transition_helper!!.animate().translationY(0f).setDuration(500).start()
                         }
                         .withEndAction {
                             fragmentTransaction?.commitAllowingStateLoss()
                         }
                         .start()
             }
             start()
         }
     }

     fun hideBottomNavigationView() {
        if (bottom_navigation!!.translationY == 0f)
            bottom_navigation!!.animate()
                    .translationY(bottom_navigation!!.height.toFloat())
                    .setDuration(250)
                    .start()
    }

    private fun animateToolbarElevation(animateOut: Boolean) {
        var valueFrom = mContext.resources.getDimension(R.dimen.toolbar_elevation)
        var valueTo = 0f
        if (!animateOut) {
            valueTo = valueFrom
            valueFrom = 0f
        }
        ValueAnimator.ofFloat(valueFrom, valueTo).setDuration(250).apply {
            startDelay = 0
            addUpdateListener { card_view!!.cardElevation = it.animatedValue as Float }
            start()
        }
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f
        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + Utility.getToolbarHeight(mContext)
        positions[2] = toY


        val adapterPosition = lstProfile.getChildAdapterPosition(view)
        val detailsFragment = SearchResultDetailFragment.newInstance(positions, adapterPosition)
        val fragmentManager = (mContext as AppCompatActivity).supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, SearchResultDetailFragment.TAG)
                ?.addToBackStack(null)

        return transaction

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_list, parent, false)
        return ViewHolder(view)
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in [//android.app.Activity#onSaveInstanceState(Bundle)][//android.app.Activity.onSaveInstanceState]
     */
    fun saveStates(outState: Bundle) {
        binderHelper.saveStates(outState)
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in [//android.app.Activity#onRestoreInstanceState(Bundle)][//android.app.Activity.onRestoreInstanceState]
     */
    fun restoreStates(inState: Bundle) {
        binderHelper.restoreStates(inState)
    }




    override fun getItemCount(): Int {
        return mDataSet?.size ?: 0
    }
}
