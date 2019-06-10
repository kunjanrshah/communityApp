package com.krs.community.fragments

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.adapter.RecyclerAdapter
import com.krs.community.interfaces.OnBackPressedListener
import com.krs.community.model.DataProvider
import com.krs.community.utils.AppConstants.*
import com.krs.community.utils.Utility
import com.krs.community.utils.supportsLollipop
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_search_list_detail.*
import kotlinx.android.synthetic.main.row_list.*
import spencerstudios.com.bungeelib.Bungee

class NearbyDetailFragment : Fragment(), OnBackPressedListener,RecyclerAdapter.ItemClickListener {
    override fun itemClick(id: Int) {
        val intent = Intent(activity, ProfileDetailActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
        Bungee.fade(context)
    }

    private lateinit var coordinates: FloatArray

    companion object {

        const val TAG = "NearbyDetailFragment"
        fun newInstance(coordinates: FloatArray, adapterPosition: Int): NearbyDetailFragment {

            val bundle = Bundle().apply {
                putFloatArray(EXTRA_COORDINATES, coordinates)
                putInt(EXTRA_POSITION, adapterPosition)
            }

            return NearbyDetailFragment().apply { arguments = bundle }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            coordinates = it.getFloatArray(EXTRA_COORDINATES)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search_list_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = if (arguments != null) (arguments as Bundle).getInt(EXTRA_POSITION)
        else 0

        setupViews(position)

        if (savedInstanceState == null) {
            animateToolbar()
            fab_negative.animate()
                    .translationY(0f)
                    .setDuration(650)
                    .setInterpolator(OvershootInterpolator(4f))
                    .start()
            fab_positive.animate()
                    .translationY(0f)
                    .setStartDelay(100)
                    .setDuration(650)
                    .setInterpolator(OvershootInterpolator(4f))
                    .start()
        } else {
            activity!!.myAppBar.alpha = 1f
        }
        Utility.hideKeyboard(activity)
    }

    private fun setupViews(position: Int) {
        supportsLollipop {
            details_card.transitionName = TRANSITION_CARD + position
            toolbar_container.transitionName = TRANSITION_TOOLBAR
        }

        (details_card.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 229// coordinates[2].toInt()

        val data = DataProvider.getCardData()[position]
        tv_title.text = data.name
        tv_amount.text = data.amount
        tv_date.text = data.date
        tv_status.text = data.status.code
        img_status.setImageResource(data.status.iconId)
        img_card.setImageResource(data.imageId)

        fab_negative.setOnClickListener { onBackPressed() }

        with(recycler_view) {
            adapter = RecyclerAdapter(DataProvider.getDetailsData(),this@NearbyDetailFragment)

            setHasFixedSize(true)
            fab_negative.doOnLayout {
                val paddingBottom = (paddingBottom + fab_negative.height * 1.5).toInt()
                updatePadding(bottom = paddingBottom)
            }
        }
    }


    override fun onBackPressed() {
        animateViewsOut()
    }

    private fun animateViewsOut() {
        val translateTo = fab_negative.height * 2f
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(recycler_view)
            start()
        }

        fab_negative.animate()
                .translationY(translateTo)
                .setDuration(1000)
                .setInterpolator(AnticipateInterpolator(2f))
                .start()
        fab_positive.animate()
                .translationY(translateTo)
                .setStartDelay(50)
                .setDuration(1000)
                .withEndAction {
                    activity?.supportFragmentManager?.popBackStack()
                }
                .setInterpolator(AnticipateInterpolator(2f))
                .start()

       // animateToolbar(0f, 350)
      //  activity!!.myAppBar.translationY = -getToolbarHeight(context).toFloat()
       // activity!!.myAppBar.animate().translationY(0f).alpha(1f).setDuration(1000).start()
    }

    private fun animateToolbar(alphaTo: Float = 1f, duration: Long = 1000) {
    //    activity!!.myAppBar.animate().alpha(alphaTo).setDuration(duration).start()
       // activity!!.myAppBar.translationY = -getToolbarHeight(context).toFloat()
       // activity!!.myAppBar.animate().translationY(0f).alpha(1f).setDuration(1000).start()
    }
}
