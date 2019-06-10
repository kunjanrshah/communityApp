package com.krs.community.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.krs.community.R
import com.krs.community.fragments.SearchDetailFragment
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter

import java.util.ArrayList

import com.facebook.FacebookSdk.getApplicationContext
import com.krs.community.fragments.NearbyDetailFragment
import com.krs.community.utils.copyViewImage
import kotlinx.android.synthetic.main.activity_dashboard.*

class SearchByDistanceFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var ll_root: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragmnet_search_by_distance, container, false)

        ll_root= root.findViewById<LinearLayout>(R.id.ll_root)
        recyclerView= root.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        createCardAdapter(recyclerView)

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Distance"

        return root
    }

    private fun movetoFragment(fragment: Fragment) {
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left)
        fragmentTransaction.replace(R.id.container_body, fragment).commit()
    }

    private fun createCardAdapter(recyclerView: RecyclerView) {
        val content = ArrayList<String>()
        for (i in 0..49) {
            content.add("item $i")
        }

        val adapter = object : ParallaxRecyclerAdapter<String>(content) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<String>, i: Int) {
                (viewHolder as ViewHolder1).tv_title.text = adapter.data[i]
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<String>, i: Int): RecyclerView.ViewHolder {
                return ViewHolder1(layoutInflater.inflate(R.layout.row_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<String>): Int {
                return content.size
            }
        }

        adapter.setOnClickEvent { v, position ->
            val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            startAnimation(copy, fragmentTransaction)


            /* float[] positions = new float[3];
                    positions[0] = 40;
                    positions[1] = 40;
                    positions[2] = 229;
                    SearchDetailFragment searchDetailFragment=SearchDetailFragment.Companion.newInstance(positions,position);
                    movetoFragment(searchDetailFragment);*/
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(R.layout.header_nearby, recyclerView, false)

        val rbtnHome: RadioButton
        val rbtnOffice: RadioButton
        val rbtnUser: RadioButton
        val rbtnAll: RadioButton
        rbtnHome = header.findViewById(R.id.rbtnHome)
        rbtnOffice = header.findViewById(R.id.rbtnOffice)
        rbtnUser = header.findViewById(R.id.rbtnUser)
        rbtnAll = header.findViewById(R.id.rbtnAll)

        rbtnHome.setOnClickListener { v ->
            rbtnHome.isChecked = true
            rbtnOffice.isChecked = false
            rbtnUser.isChecked = false
            rbtnAll.isChecked = false
        }

        rbtnOffice.setOnClickListener { v ->
            rbtnHome.isChecked = false
            rbtnOffice.isChecked = true
            rbtnUser.isChecked = false
            rbtnAll.isChecked = false
        }

        rbtnUser.setOnClickListener { v ->
            rbtnHome.isChecked = false
            rbtnOffice.isChecked = false
            rbtnUser.isChecked = true
            rbtnAll.isChecked = false
        }

        rbtnAll.setOnClickListener { v ->
            rbtnHome.isChecked = false
            rbtnOffice.isChecked = false
            rbtnUser.isChecked = false
            rbtnAll.isChecked = true
        }

        val edtKm = header.findViewById<EditText>(R.id.edtKm)
        val btnSearch = header.findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener { v ->

        }

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, recyclerView)
        adapter.data = content
        recyclerView.adapter = adapter
    }

    internal class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title: TextView

        init {
            tv_title = itemView.findViewById<View>(R.id.tv_title) as TextView
        }
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = recyclerView.getChildAdapterPosition(view)
        val detailsFragment = NearbyDetailFragment.newInstance(positions, adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, SearchDetailFragment.TAG)
                ?.addToBackStack(null)

       /* supportsLollipop {
            val transition = TransitionInflater.from(context)
                    .inflateTransition(R.transition.shared_element_transition)
            detailsFragment.sharedElementEnterTransition = transition

            transaction
                    ?.addSharedElement(view, view.transitionName)
            //  ?.addSharedElement(details_toolbar_transition_helper, details_toolbar_transition_helper.transitionName)
        }*/

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
