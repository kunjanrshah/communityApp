package com.krs.community.fragments

import android.graphics.Typeface
import android.os.Build
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
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter

import java.util.ArrayList

import com.facebook.FacebookSdk.getApplicationContext
import com.krs.community.app.AppController
import com.krs.community.utils.Utility
import com.krs.community.utils.copyViewImage
import com.nightonke.boommenu.BoomMenuButton
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
        recyclerView.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        createCardAdapter(recyclerView)

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Distance"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    private fun createCardAdapter(recyclerView: RecyclerView) {

        val content = ArrayList<String>()
        for (i in 0..49) {
            content.add("item $i")
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

        val img_cancel = header.findViewById<ImageView>(R.id.img_cancel)
        img_cancel.setOnClickListener {
            Utility.movetoFragment(activity,DashboardFragment())
        }

        val edtKm = header.findViewById<EditText>(R.id.edtKm)
        val btnSearch = header.findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener { v ->

        }

        val adapter = object : ParallaxRecyclerAdapter<String>(content) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<String>, i: Int) {
                (viewHolder as DistanceViewHolder).tv_name.setText("Kunjan Shah")
                (viewHolder as DistanceViewHolder).tv_area.setText("Maninagar, Ahmedabad")
                (viewHolder as DistanceViewHolder).tv_email.setText("kunjanrshah@gmail.com")
                (viewHolder as DistanceViewHolder).tv_mobile.setText("9427051418")
                (viewHolder as DistanceViewHolder).tv_role.setText("Family Head")

                (viewHolder as DistanceViewHolder).bmb1.clearBuilders()
                for (i in 0 until (viewHolder as DistanceViewHolder).bmb1.piecePlaceEnum.pieceNumber()) {
                    (viewHolder as DistanceViewHolder).bmb1.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }

                (viewHolder as DistanceViewHolder).bmb1.setOnClickListener {
                    (viewHolder as DistanceViewHolder).bmb1.boom()
                }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<String>, i: Int): RecyclerView.ViewHolder {
                return DistanceViewHolder(layoutInflater.inflate(R.layout.list_distance, viewGroup, false))
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

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, recyclerView)
        adapter.data = content
        recyclerView.adapter = adapter
    }

    internal class DistanceViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv_name: TextView
        var tv_area: TextView
        var tv_email: TextView
        var tv_mobile: TextView
        var tv_role: TextView
        var bmb1: BoomMenuButton

        init {
            val typeface: Typeface = AppController.getInstance().typeface
            val typeface_bold: Typeface = AppController.getInstance().typeface_bold
            tv_name = v.findViewById<View>(com.krs.community.R.id.tv_name) as TextView
            tv_name.setTypeface(typeface_bold)

            tv_area = v.findViewById(R.id.tv_area)
            tv_area.setTypeface(typeface)
            tv_email = v.findViewById(R.id.tv_email)
            tv_email.setTypeface(typeface)
            tv_mobile = v.findViewById(R.id.tv_mobile)
            tv_mobile.setTypeface(typeface)
            tv_role = v.findViewById(R.id.tv_role)
            tv_role.setTypeface(typeface_bold)

            bmb1=v.findViewById(R.id.bmb1)
        }
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = recyclerView.getChildAdapterPosition(view)
        val detailsFragment = FamilyDetailFragment.newInstance(positions, adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, FamilyDetailFragment.TAG)
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
        //  Utility.fade(context);


        /* AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
             setTarget(lstProfile)
             //withStartAction { animateToolbarElevation(true) }
             withEndAction {
                 lstProfile!!.visibility = View.INVISIBLE

                 val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

                 view.animate().y(229f).start()
                // fragmentTransaction!!.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right)
                 fragmentTransaction?.commitAllowingStateLoss()
                 Utility.fade(context);
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
