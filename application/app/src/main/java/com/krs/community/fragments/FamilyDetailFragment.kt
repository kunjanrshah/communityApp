package com.krs.community.fragments


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.adapter.RecyclerAdapter.ItemClickListener
import com.krs.community.app.AppController
import com.krs.community.interfaces.OnBackPressedListener
import com.krs.community.model.DataProvider
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.AppConstants.*
import com.krs.community.utils.Utility
import com.krs.community.utils.copyViewImage
import com.krs.community.utils.supportsLollipop
import com.nightonke.boommenu.BoomMenuButton
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.family_header_detail.tv_title
import kotlinx.android.synthetic.main.family_header_detail.view.*
import kotlinx.android.synthetic.main.fragment_search_list_detail.*
import kotlinx.android.synthetic.main.row_list.*


class FamilyDetailFragment : Fragment(), OnBackPressedListener, ItemClickListener {

    override fun itemClick(id: Int) {
        val intent = Intent(activity, ProfileDetailActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
        Utility.fade(context)
    }

    // private lateinit var coordinates: FloatArray
    lateinit var rv_detail: RecyclerView
    lateinit var ll_root: LinearLayout

    companion object {

        const val TAG = "FamilyDetailFragment"
        fun newInstance(adapterPosition: Int): FamilyDetailFragment {
            val bundle = Bundle().apply {

                //putFloatArray(EXTRA_COORDINATES, coordinates)
                putInt(EXTRA_POSITION, adapterPosition)
            }

            return FamilyDetailFragment().apply { arguments = bundle }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(com.krs.community.R.layout.fragment_header_detail, container, false)
        (activity as AppCompatActivity).supportActionBar!!.title = "Header Detail"

        ll_root = root.findViewById<LinearLayout>(com.krs.community.R.id.ll_root)
        rv_detail = root.findViewById<RecyclerView>(com.krs.community.R.id.rv_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = if (arguments != null) (arguments as Bundle).getInt(EXTRA_POSITION)
        else 0

        rv_detail.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        rv_detail.layoutManager = mLayoutManager
        rv_detail.itemAnimator = DefaultItemAnimator()
        createCardAdapter(rv_detail, position)

        setupViews(position)
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

        details_card.setOnClickListener {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            startActivity(intent)
            Utility.fade(context)
        }

       // fab_negative.setOnClickListener { onBackPressed() }

        /*with(rv_detail) {
            adapter = RecyclerAdapter(DataProvider.getDetailsData(),this@FamilyDetailFragment)

            setHasFixedSize(true)
            fab_negative.doOnLayout {
                val paddingBottom = (paddingBottom + fab_negative.height * 1.5).toInt()
                updatePadding(bottom = paddingBottom)
            }
        }*/
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        Handler().postDelayed({
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }, 1000)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    private fun createCardAdapter(recyclerView: RecyclerView, position: Int) {
        val content = ArrayList<String>()
        for (i in 0..4) {
            content.add("item $i")
        }

        val adapter = object : ParallaxRecyclerAdapter<String>(content) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<String>, i: Int) {
                (viewHolder as HeaderViewHolder).tv_name.text = "Kunjan Shah"
                viewHolder.tv_subtext.text = "Son"
                viewHolder.tv_email.text = "kunjanrshah@gmail.com"
                viewHolder.tv_mobile.text = "9427051418"

                viewHolder.bmb1.clearBuilders()
                for (i in 0 until viewHolder.bmb1.piecePlaceEnum.pieceNumber()) {
                    viewHolder.bmb1.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }

                viewHolder.bmb1.setOnClickListener {
                    viewHolder.bmb1.boom()
                }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<String>, i: Int): RecyclerView.ViewHolder {
                return HeaderViewHolder(layoutInflater.inflate(R.layout.row_list_header, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<String>): Int {
                return content.size
            }
        }

        adapter.setOnClickEvent { v, position ->

            /*val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
            Utility.fade(context)*/

            val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            startAnimation(copy, fragmentTransaction)
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(com.krs.community.R.layout.family_header_detail, recyclerView, false)
        val ll_family_head: LinearLayout

        ll_family_head = header.findViewById(R.id.ll_family_head)
        ll_family_head.setOnClickListener {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            Utility.fade(context)
        }

        val tv_add: TextView
        tv_add = header.findViewById(R.id.tv_add)
        tv_add.setOnClickListener {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            Utility.fade(context)
        }

        header.img_cancel.setOnClickListener {
            Utility.movetoFragment(activity, SearchListFragment())
        }



        header.bmb.clearBuilders()
        for (i in 0 until header.bmb.piecePlaceEnum.pieceNumber()) {
            header.bmb.addBuilder(Utility.getTextInsideCircleButtonBuilder())
        }

        header.bmb.setOnClickListener {
            header.bmb.boom()
        }

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, recyclerView)
        adapter.data = content
        recyclerView.adapter = adapter

    }

    private fun startAnimation(view: View, fragmentTransaction: FragmentTransaction?) {
        fragmentTransaction?.commitAllowingStateLoss()
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        /* val toY = view.resources.getDimensionPixelOffset(com.krs.community.R.dimen.details_toolbar_container_height) - view.height / 2f

         val positions = FloatArray(3)
         positions[0] = view.x
         positions[1] = view.y + activity!!.myAppBar.height
         positions[2] = toY*/

        val adapterPosition = rv_detail.getChildAdapterPosition(view)
        val detailsFragment = newInstance(adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(com.krs.community.R.id.container_body, detailsFragment, SearchDetailFragment.TAG)
                ?.addToBackStack(null)

        return transaction
    }

    internal class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv_name: TextView
        var tv_subtext: TextView
        var tv_email: TextView
        var tv_mobile: TextView
        var bmb1: BoomMenuButton

        init {
            tv_name = v.findViewById<View>(com.krs.community.R.id.tv_name) as TextView
            tv_subtext = v.findViewById(R.id.tv_subtext)
            tv_subtext.typeface = AppController.getInstance().typeface_bold
            tv_email = v.findViewById(R.id.tv_email)
            tv_mobile = v.findViewById(R.id.tv_mobile)
            bmb1 = v.findViewById(R.id.bmb1)
        }
    }

    override fun onBackPressed() {
        //  animateViewsOut()
    }

    /* private fun animateViewsOut() {
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
                     //activity?.supportFragmentManager?.popBackStack()
                     val intent = Intent(activity, DashboardActivity::class.java)
                     intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                     startActivity(intent)
                     activity?.finish()
                     Utility.fade(context)
                 }
                 .setInterpolator(AnticipateInterpolator(2f))
                 .start()



         //getActivity()?.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)


          animateToolbar(0f, 350)
           activity!!.myAppBar.translationY = -getToolbarHeight(context).toFloat()
          activity!!.myAppBar.animate().translationY(0f).alpha(1f).setDuration(1000).start()
     }*/


    /* private fun animateToolbar(alphaTo: Float = 1f, duration: Long = 1000) {
             activity!!.myAppBar.animate().alpha(alphaTo).setDuration(duration).start()
          activity!!.myAppBar.translationY = -getToolbarHeight(context).toFloat()
          activity!!.myAppBar.animate().translationY(0f).alpha(1f).setDuration(1000).start()
     }*/
}
