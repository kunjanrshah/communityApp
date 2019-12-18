package com.krs.community.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.app.AppController
import com.krs.community.interfaces.ByDistanceListener
import com.krs.community.model.ByDistanceModel
import com.krs.community.model.Member
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.ByDistanceViewModel
import com.krs.community.viewmodel.ByDistanceViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.header_nearby.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchByDistanceFragment : Fragment(), KodeinAware,ByDistanceListener {

    lateinit var recyclerView: RecyclerView
    internal var mByDistanceViewModel: ByDistanceViewModel? = null
    private val factory: ByDistanceViewModelFactory by instance()
    private val lstMembers=ArrayList<Member>()
    override val kodein by kodein()
    var TAG=SearchByDistanceFragment::class.java.simpleName
    private var mShimmerViewContainer: ShimmerFrameLayout? = null

    override fun getUsers(response: ByDistanceResponse) {

        if(response.success){
            lstMembers.clear()
            for (item in response.member) {
                lstMembers.add(item)
            }
            byDistanceAdapter?.notifyDataSetChanged()
        }
        mShimmerViewContainer!!.stopShimmerAnimation()
        mShimmerViewContainer!!.visibility = View.GONE
    }

    override fun getFailure(message: String) {
        Log.d(TAG,"getFailure: "+message)
        mShimmerViewContainer!!.stopShimmerAnimation()
        mShimmerViewContainer!!.visibility = View.GONE
    }

    private var byDistanceAdapter: ParallaxRecyclerAdapter<Member>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragmnet_search_by_distance, container, false)

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Distance"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        }

        mByDistanceViewModel = ViewModelProviders.of(this,factory).get(ByDistanceViewModel::class.java)
        mByDistanceViewModel?.mByDistanceListener=this

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container)

        recyclerView= root.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        createCardAdapter(recyclerView)

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
        edtKm.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callDistanceAPI()
            }
            false;
        })
        val btnSearch = header.findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener { v ->
            callDistanceAPI()
        }

        byDistanceAdapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {
                (viewHolder as DistanceViewHolder).tv_name.text = lstMembers.get(i).firstName+" "+lstMembers.get(i).lastName
                viewHolder.tv_area.text = lstMembers.get(i).area+" "+lstMembers.get(i).cityId
                viewHolder.tv_email.text = lstMembers.get(i).emailAddress
                viewHolder.tv_mobile.text = lstMembers.get(i).mobile
                if(lstMembers.get(i).headId.equals("0")){
                    viewHolder.tv_role.text = "Family Head"
                }else{
                    viewHolder.tv_role.text = "Member"
                }

                viewHolder.bmb1.clearBuilders()
                for (i in 0 until viewHolder.bmb1.piecePlaceEnum.pieceNumber()) {
                    viewHolder.bmb1.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }

                viewHolder.bmb1.setOnClickListener {
                    viewHolder.bmb1.boom()
                }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return DistanceViewHolder(layoutInflater.inflate(R.layout.list_distance, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }

        byDistanceAdapter?.setOnClickEvent { v, position ->
          if(lstMembers.get(position).headId.equals("0")){
            //  Utility.movetoFragment(activity,FamilyDetailActivity())
          }else{
              val intent=Intent(activity,ProfileDetailActivity::class.java)
              startActivity(intent)
          }
            /*val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            startAnimation(copy, fragmentTransaction)*/
        }

        layoutManagerFixed.setHeaderIncrementFixer(header)
        byDistanceAdapter?.isShouldClipView = false
        byDistanceAdapter?.setParallaxHeader(header, recyclerView)
        byDistanceAdapter?.data = lstMembers
        recyclerView.adapter = byDistanceAdapter
    }

    internal class DistanceViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv_name: TextView
        var tv_area: TextView
        var tv_email: TextView
        var tv_mobile: TextView
        var tv_role: TextView
        var bmb1: BoomMenuButton

        init {
            val typeface: Typeface = AppController.mApplication?.typeface!!
            val typeface_bold: Typeface = AppController.mApplication?.typeface_bold!!
            tv_name = v.findViewById<View>(R.id.tv_name) as TextView
            tv_name.typeface = typeface_bold

            tv_area = v.findViewById(R.id.tv_area)
            tv_area.typeface = typeface
            tv_email = v.findViewById(R.id.tv_email)
            tv_email.typeface = typeface
            tv_mobile = v.findViewById(R.id.tv_mobile)
            tv_mobile.typeface = typeface
            tv_role = v.findViewById(R.id.tv_role)
            tv_role.typeface = typeface_bold
            bmb1=v.findViewById(R.id.bmb1)
        }
    }

    private fun callDistanceAPI(){
        lstMembers.clear()
        byDistanceAdapter?.notifyDataSetChanged()
        var nearBy="All"
        if(rbtnHome.isChecked){
            nearBy="Home"
        }else if(rbtnOffice.isChecked){
            nearBy="Office"
        }else if(rbtnUser.isChecked){
            nearBy="User"
        }

        val distance = ByDistanceModel()
        distance.km = edtKm.text.toString().trim()
        distance.nearBy = nearBy
        distance.userId = Guru.getString("user_id","")
        distance.accessToken = Guru.getString("access_token","")
        distance.lat="22.97400154"
        distance.lng="72.61356707"

        mShimmerViewContainer?.startShimmerAnimation()
        mShimmerViewContainer?.visibility=View.VISIBLE

        mByDistanceViewModel?.getUserByDistance(distance)
    }

    /*private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = recyclerView.getChildAdapterPosition(view)
        val detailsFragment = FamilyDetailActivity.newInstance(adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, FamilyDetailActivity.TAG)
                ?.addToBackStack(null)

       *//* supportsLollipop {
            val transition = TransitionInflater.from(context)
                    .inflateTransition(R.transition.shared_element_transition)
            detailsFragment.sharedElementEnterTransition = transition

            transaction
                    ?.addSharedElement(view, view.transitionName)
            //  ?.addSharedElement(details_toolbar_transition_helper, details_toolbar_transition_helper.transitionName)
        }*//*

        return transaction
    }

    private fun startAnimation(view: View, fragmentTransaction: FragmentTransaction?) {

        //   fragmentTransaction!!.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right)
        fragmentTransaction?.commitAllowingStateLoss()
        //  Utility.fade(context);


        *//* AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
             setTarget(lstProfile)
             //withStartAction { animateToolbarElevation(true) }
             withEndAction {
                 lstProfile!!.visibility = View.INVISIBLE

                 val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

                 view.animate().y(229f).start()
                // fragmentTransaction!!.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right)
                 fragmentTransaction?.commitAllowingStateLoss()
                 Utility.fade(context);
                 *//**//*activity?.myAppBar!!.animate()
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
                        .start()*//**//*
            }
            start()
        }*//*
    }*/

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
