package com.krs.community.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.activity.BaseActivity
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.app.AppController
import com.krs.community.interfaces.ByDistanceListener
import com.krs.community.model.ByDistanceModel
import com.krs.community.model.Member
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.ByDistanceViewModel
import com.krs.community.viewmodel.ByDistanceViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import kotlinx.android.synthetic.main.fragment_add_relative.*
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import kotlin.system.measureTimeMillis

class SearchByDistanceFragment : Fragment(), KodeinAware,ByDistanceListener, Listener, LocationData.AddressCallBack,ParallaxRecyclerAdapter.OnLoadMore  {

    lateinit var recyclerView: RecyclerView
    internal lateinit var mByDistanceViewModel: ByDistanceViewModel
    private val factory: ByDistanceViewModelFactory by instance()
    private val lstMembers=ArrayList<Member>()
    override val kodein by kodein()
    var TAG=SearchByDistanceFragment::class.java.simpleName
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var llRoot: FrameLayout
    private lateinit var imgMap: ImageView
    private lateinit var rbtnHome:RadioButton
    private lateinit var rbtnOffice:RadioButton
    private lateinit var rbtnUser:RadioButton
    private lateinit var rbtnAll:RadioButton
    private lateinit var edtKm:EditText
    private lateinit var nearBy:String
    private lateinit var tvRecords:TextView
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var getLocationDetail: GetLocationDetail
    private var curr_lat = MutableLiveData<Double>()
    private var curr_lng = MutableLiveData<Double>()
    private var isCallAPI=false
    private var selectedPosition = 0
    private var start: Int = 0
    private val length: Int = 30
    private lateinit var distance:ByDistanceModel

    override fun getMembers(response: ByDistanceResponse) {
        DashboardActivity.stop=false
        if(response.success){
            lstMembers.clear()
            start=0
            for (item in response.member) {
                if(item.isLocationEnable!="0" && item.nearBy.toLowerCase() != "user"){
                    lstMembers.add(item)
                }
            }
            byDistanceAdapter?.notifyDataSetChanged()
            recyclerView.layoutManager?.scrollToPosition(selectedPosition)
            selectedPosition = lstMembers.size - 1
            DashboardActivity.stop = false

            if(Integer.parseInt(response.totalRecords)<=length){
                DashboardActivity.stop = true
                Snackbar.make(llRoot, "End of the Records", Snackbar.LENGTH_LONG).show()
            }

            if(lstMembers.size>0){
                tvRecords.text="Records found: "+response.totalRecords
                imgMap.visibility=View.GONE
                tvRecords.visibility=View.VISIBLE
            }else{
                tvRecords.visibility=View.GONE
                imgMap.visibility=View.VISIBLE
                DashboardActivity.stop = true
            }
        }else{
            tvRecords.visibility=View.GONE
            imgMap.visibility=View.VISIBLE
        }
        mShimmerViewContainer.stopShimmerAnimation()
        mShimmerViewContainer.visibility = View.GONE
        llRoot.snackbar(response.message,Snackbar.LENGTH_LONG)
        Utility.hideKeyboard(activity)
}

    override fun getFailure(message: String) {
        Log.d(TAG, "getFailure: $message")
        activity?.runOnUiThread {
            if(mShimmerViewContainer.isAnimationStarted){
                mShimmerViewContainer.stopShimmerAnimation()
            }
            mShimmerViewContainer.visibility = View.GONE
            imgMap.visibility=View.VISIBLE
            tvRecords.visibility=View.GONE
            DashboardActivity.stop = false
        }

        llRoot.snackbar(message,Snackbar.LENGTH_LONG)
        Utility.hideKeyboard(activity)
    }

    private var byDistanceAdapter: ParallaxRecyclerAdapter<Member>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragmnet_search_by_distance, container, false)

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Distance"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        }

        mByDistanceViewModel = ViewModelProviders.of(this,factory).get(ByDistanceViewModel::class.java)
        mByDistanceViewModel.mByDistanceListener =this

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container)
        llRoot= root.findViewById(R.id.ll_root)
        recyclerView= root.findViewById(R.id.recycler_view)
        imgMap= root.findViewById(R.id.img_map)

        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        createCardAdapter(recyclerView)
        imgMap.visibility=View.VISIBLE
        getLocationDetail = GetLocationDetail(this, activity)
        val request = LocationRequest()
        request.interval = Utility.INTERVAL
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        easyWayLocation = EasyWayLocation(activity, request, false, this)
        if (Utility.finePermissionIsGranted(activity)) {
            easyWayLocation.startLocation() //calculateDistance()
        } else {
            ActivityCompat.requestPermissions(activity as AppCompatActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), BaseActivity.REQUEST_LOCATION_PERMISSION)
        }

        DashboardActivity.stop=false
        callDistanceAPI()
        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        easyWayLocation.startLocation()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        easyWayLocation.endUpdates()
    }

    private fun createCardAdapter(recyclerView: RecyclerView) {

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(R.layout.header_nearby, recyclerView, false)

        rbtnHome = header.findViewById(R.id.rbtnHome)
        rbtnOffice = header.findViewById(R.id.rbtnOffice)
        rbtnUser = header.findViewById(R.id.rbtnUser)
        rbtnAll = header.findViewById(R.id.rbtnAll)
        tvRecords= header.findViewById(R.id.tv_total)
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

        edtKm = header.findViewById<EditText>(R.id.edtKm)
        edtKm.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                isCallAPI=true
                DashboardActivity.stop=false
                callDistanceAPI()
            }
            false;
        })

        val btnSearch = header.findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener { v ->
            isCallAPI=true
            DashboardActivity.stop=false
            callDistanceAPI()
        }

        byDistanceAdapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, i: Int) {

                (viewHolder as DistanceViewHolder).tvName.text = lstMembers.get(i).firstName

                mByDistanceViewModel.getLastNamebyId(lstMembers.get(i).subCastId).observeForever {
                    viewHolder.tvName.text = lstMembers.get(i).firstName+" "+it
                }

                mByDistanceViewModel.getCityNamebyId(lstMembers.get(i).cityId).observeForever {
                    viewHolder.tvArea.text = lstMembers.get(i).area+" "+it
                }

                viewHolder.tvEmail.text = lstMembers.get(i).emailAddress
                viewHolder.tvMobile.text = lstMembers.get(i).mobile
                if(lstMembers.get(i).headId.equals("0")){
                    viewHolder.tvRole.text = "Family Head"
                }else{
                    viewHolder.tvRole.text = "Member"
                }

                viewHolder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    viewHolder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }

                viewHolder.boomMenuButton.setOnClickListener {
                    viewHolder.boomMenuButton.boom()
                }
                var distance=""
                val index= lstMembers.get(i).distance.indexOf(".")
                if(lstMembers.get(i).distance.length>(index+3)){
                    distance=lstMembers.get(i).distance.substring(0,(index+3))
                }else{
                    distance=lstMembers.get(i).distance
                }
                viewHolder.tvDistance.text="${distance} KM"
                if(nearBy.equals("All")){
                    viewHolder.tvLabel.text= lstMembers.get(i).nearBy
                }else{
                    viewHolder.tvLabel.text=nearBy
                }

                viewHolder.tvUpdate.text=Utility.changeDateFormat(lstMembers.get(i).updatedDt,Utility.yyyy_MM_dd_TIME,Utility.dd_MM_yyyy_TIME)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return DistanceViewHolder(layoutInflater.inflate(R.layout.list_distance, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }

        byDistanceAdapter?.setOnClickEvent { v, position ->
            val intent=Intent(activity,ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member),lstMembers.get(position))
            startActivity(intent)
            Utility.fade(activity)
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
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var tvArea: TextView = v.findViewById(R.id.tv_area)
        var tvEmail: TextView = v.findViewById(R.id.tv_email)
        var tvMobile: TextView = v.findViewById(R.id.tv_mobile)
        var tvRole: TextView = v.findViewById(R.id.tv_role)
        var tvDistance: TextView = v.findViewById(R.id.tv_distance)
        var tvLabel: TextView = v.findViewById(R.id.tv_label)
        var tvUpdate: TextView = v.findViewById(R.id.tv_update)

        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.bmb1)
    }

    private fun callDistanceAPI(){
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            lstMembers.clear()
            tvRecords.visibility=View.GONE
            byDistanceAdapter?.notifyDataSetChanged()
            nearBy="All"
            if(rbtnHome.isChecked){
                nearBy="Home"
            }else if(rbtnOffice.isChecked){
                nearBy="Office"
            }else if(rbtnUser.isChecked){
                nearBy="User"
            }

            distance = ByDistanceModel()
            distance.start=start.toString()
            distance.length=length.toString()
            distance.km = edtKm.text.toString().trim()
            distance.nearBy = nearBy
            distance.userId = Guru.getString(getString(R.string.user_id),Guru.getString(getString(R.string.user_id),""))
            distance.accessToken = Guru.getString(getString(R.string.access_token),Guru.getString(getString(R.string.access_token),""))
            distance.lat=curr_lat.toString()
            distance.lng=curr_lng.toString()

            mShimmerViewContainer.startShimmerAnimation()
            mShimmerViewContainer.visibility =View.VISIBLE
            imgMap.visibility=View.GONE

            Handler().postDelayed({
                if(lstMembers.size==0){
                    mShimmerViewContainer.stopShimmerAnimation()
                    mShimmerViewContainer.visibility =View.GONE
                    imgMap.visibility=View.VISIBLE
                }
            },5000)

            curr_lat.observe(activity as AppCompatActivity, Observer {
                if(curr_lat.value!=0.0 && curr_lng.value!=0.0 && isCallAPI){
                    isCallAPI=false
                    distance.lat=curr_lat.value.toString()
                    distance.lng=curr_lng.value.toString()
                    mByDistanceViewModel.getUserByDistance(distance)
                }
            })

            curr_lng.observe(activity as AppCompatActivity, Observer {
                distance.lat=curr_lat.value.toString()
                distance.lng=curr_lng.value.toString()
                /*if(curr_lat.value!=0.0 && curr_lng.value!=0.0 && isCallAPI){
                    isCallAPI=false

                    mByDistanceViewModel.getUserByDistance(distance)
                }*/
            })
        }
    }

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            start = (lstMembers.size+1)
            distance.start=start.toString()
            mByDistanceViewModel.getUserByDistance(distance)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EasyWayLocation.LOCATION_SETTING_REQUEST_CODE) {
            easyWayLocation.onActivityResult(resultCode)
        }
    }

    override fun locationCancelled() {
    }

    override fun locationOn() {

    }

    override fun currentLocation(location: Location) {
        curr_lat.value=location.latitude
        curr_lng.value=location.longitude
        isCallAPI=true
    }

    override fun locationData(locationData: LocationData) {

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
