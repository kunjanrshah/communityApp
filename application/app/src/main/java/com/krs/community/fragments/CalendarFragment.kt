package com.krs.community.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.krs.community.R
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.copyViewImage
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.header_calendar.*
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment(), SlyCalendarDialog.Callback {
    override fun onDataSelected(firstDate: Calendar?, secondDate: Calendar?, hours: Int, minutes: Int) {

        if (firstDate != null) {
            var str: String
            if (secondDate == null) {

                str = SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.getTime())
                Log.d(TAG, str)

            } else {
                str = getString(
                        R.string.period,
                        SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.getTime()),
                        SimpleDateFormat(getString(R.string.dateFormat)).format(secondDate.getTime())
                )
                Log.d(TAG, str)
                //Toast.makeText(activity,"" + str,Toast.LENGTH_LONG).show();
            }
            txtdate.setText(str)
        }
    }

    override fun onCancelled() {
        //Nothing
    }


    lateinit var recyclerView: RecyclerView
    lateinit var ll_root: LinearLayout
    var TAG: String = "CalendarFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(com.krs.community.R.layout.fragment_calendar, container, false)

        ll_root = root.findViewById<LinearLayout>(com.krs.community.R.id.ll_root)
        recyclerView = root.findViewById<RecyclerView>(com.krs.community.R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        createCardAdapter(recyclerView)

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Calendar"

        return root
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
                return ViewHolder1(layoutInflater.inflate(com.krs.community.R.layout.row_list, viewGroup, false))
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
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(com.krs.community.R.layout.header_calendar, recyclerView, false)

        val fab: FloatingActionButton
        val txtdate: TextView
        val imgCalendar: ImageView
        fab = header.run { findViewById(com.krs.community.R.id.fab) }
        txtdate = header.findViewById(com.krs.community.R.id.txtdate)
        imgCalendar = header.findViewById(com.krs.community.R.id.imgCalendar)

        imgCalendar.setOnClickListener {
            SlyCalendarDialog()
                    .setSingle(false)
                    .setCallback(this)
                    .setHeaderColor(resources.getColor(R.color.colorPrimary))
                    .setBackgroundColor(Color.parseColor("#ffffff"))
                    .setSelectedColor(Color.parseColor("#c48395"))
                    .show(activity?.getSupportFragmentManager(), "TAG_SLYCALENDAR")
        }
        fab.setOnClickListener {
            Toast.makeText(activity, "search", Toast.LENGTH_LONG).show()
        }

        val str = SimpleDateFormat(getString(R.string.dateFormat)).format(Date())
        txtdate.setText(str)

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, recyclerView)
        adapter.data = content
        recyclerView.adapter = adapter
    }


    internal class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title: TextView

        init {
            tv_title = itemView.findViewById<View>(com.krs.community.R.id.tv_title) as TextView
        }
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(com.krs.community.R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = recyclerView.getChildAdapterPosition(view)
        val detailsFragment = SearchbyDetailFragment.newInstance(positions, adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(com.krs.community.R.id.container_body, detailsFragment, SearchDetailFragment.TAG)
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
