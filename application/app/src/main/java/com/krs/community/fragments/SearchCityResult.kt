package com.krs.community.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.SparseBooleanArray
import android.view.ActionMode
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus

import java.util.ArrayList

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.krs.community.databinding.FragmentFilterResultBinding
import com.krs.community.interfaces.IbrowseCityRecordsListener
import com.krs.community.model.*
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter.VIEW_TYPES.VIEW_TYPE_LOADING
import com.krs.community.viewmodel.BrowseCityViewModel
import com.krs.community.viewmodel.BrowseCityViewModelFactory
import kotlinx.android.synthetic.main.fragment_filter_result.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchCityResult : Fragment(), SwipeRefreshLayout.OnRefreshListener, KodeinAware, IbrowseCityRecordsListener{

   /* override fun onRefresh(direction: SwipyRefreshLayoutDirection?) {
        start=start+length
     //   rootView?.swipyrefreshlayout?.setRefreshing(true)
        setupList()
    }*/

    override fun getSearchRecords(data: SearchByCityModel) {

        if(data.success){
            data.totalHead
            data.totalMem
            data.users.size
            for (user in data.users){
                users.add(user)
            }
            adapter?.notifyDataSetChanged()
         //   rootView?.swipyrefreshlayout?.setRefreshing(false)
            rootView?.shimmer_view_container?.stopShimmerAnimation()
            rootView?.shimmer_view_container?.visibility = View.GONE
        }
        Log.d("SearchCityResult","data: "+data.toString())
    }

    override suspend fun getFailure(message: Boolean) {
        Log.d("SearchCityResult","message: "+message)
    }

    var isLoading = false
    private var start:String?="0"
    private val length:String?="30"
    private var city_id:String?=""
    private val users = ArrayList<User>()
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var adapter: ParallaxRecyclerAdapter<User>? = null
    private var selectedItems: SparseBooleanArray? = null
    private var animationItemsIndex: SparseBooleanArray? = null
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    private val selectedItemCount: Int get() = selectedItems!!.size()
    private var rootView: View?=null
    private var TAG:String =SearchCityResult::class.java.simpleName
    private val factory: BrowseCityViewModelFactory by instance()
    internal var browseCityViewModel: BrowseCityViewModel? = null
    override val kodein by kodein()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentFilterResultBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_result, container, false)
        rootView=  binding.root

        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()

        actionModeCallback = ActionModeCallback()
        browseCityViewModel = ViewModelProviders.of(this,factory).get(BrowseCityViewModel::class.java)
        browseCityViewModel?.ibrowseCityRecordsListener=this
      //  rootView?.swipyrefreshlayout?.setOnRefreshListener(this)
    //    rootView?.swipyrefreshlayout?.setDirection(SwipyRefreshLayoutDirection.BOTTOM)

        val city_name = if (this.arguments != null) this.arguments!!.getString("city_name") else null
        city_id = if (this.arguments != null) this.arguments!!.getString("city_id") else null

        adapter = object : ParallaxRecyclerAdapter<User>(users) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<User>, position: Int) {

                val user = users[position]
                val name = user.firstName+" "+user.lastName

              //  if(!adapter.getItemViewType(position+1).equals(VIEW_TYPE_LOADING)){
                    val holder =  viewHolder as SearchCityResult.ViewHolder
                    holder.tv_name.setText(name)
                    holder.tv_area.setText(user.area+" "+user.city)
                    holder.tv_email.setText(user.emailAddress)
                    holder.tv_mobile.setText(user.mobile)
                    if(user.headId.equals("0")){
                        holder.tv_role.setText("Head")
                    }else{
                        holder.tv_role.setText("Member")
                    }

                    holder.boomMenuButton.clearBuilders()
                    for (i in 0 until holder.boomMenuButton.getPiecePlaceEnum().pieceNumber()) {
                        holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                    }
                    holder.boomMenuButton.setOnClickListener({ v -> holder.boomMenuButton.boom() })

                    holder.iconText.setText(name.substring(0, 1))
                    holder.itemView.isActivated = selectedItems!!.get(position, false)
                    applyIconAnimation(holder, position)
                    applyProfilePicture(holder, user)
                    applyClickEvents(holder, position)
                //}

            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<User>, i: Int): RecyclerView.ViewHolder {
                return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<User>): Int {
                return users.size ?: 0
            }
        }


        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_filter, container, false)
        Log.d(TAG,"City Name: "+city_name)
        val tvTitle = header.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = city_name

        val edt_filter_name = header.findViewById<EditText>(R.id.edt_filter_name)
        edt_filter_name.visibility = View.GONE

        val iv_cancel = header.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel.setOnClickListener { v -> Utility.movetoFragment(activity, ExpandableFilterListFragment()) }

        val iv_export = header.findViewById<ImageView>(R.id.iv_export)
        val iv_atoz = header.findViewById<ImageView>(R.id.iv_atoz)
        iv_atoz.setOnClickListener { v ->
            val adapter = AtoZBottomAdapter(context)
            val dialog = DialogPlus.newDialog(context!!)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            dialog.show()
        }

        adapter?.setParallaxHeader(header, rootView?.lstFilter)
        rootView?.lstFilter?.layoutManager = LinearLayoutManager(activity)
        rootView?.lstFilter?.adapter = adapter
        rootView?.lstFilter?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

                /*if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() === users.size - 1) {
                        //bottom of list!
                 //       loadMore()
                        isLoading = true
                    }
                }*/
            }
        })


        setupList()
        return rootView
    }

    private fun loadMore() {

       // adapter?.notifyItemInserted(users.size - 1)
        Handler().postDelayed(Runnable {
            isLoading = false
        },2000)

    }

    private fun setupList() {
        val data= SearchByCityData()
        data.start=start
        data.length=length
        val filterBy= FilterBy()
        filterBy.cityId=city_id
        data.filterBy=filterBy
        rootView?.shimmer_view_container?.startShimmerAnimation()
        browseCityViewModel?.fetchRecordsByCity(data)
    }

    private fun applyClickEvents(holder: ViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener { onIconClicked(position) }
        holder.ll_email.setOnClickListener {
            Toast.makeText(activity,"Email Id",Toast.LENGTH_SHORT).show()
        }

        holder.ll_mobile.setOnClickListener {
            Toast.makeText(activity,"Mobile",Toast.LENGTH_SHORT).show()
            val intent=Intent(Intent.ACTION_DIAL)
            val str="tel:"+holder.tv_mobile.text
            intent.setData(Uri.parse(str));
            startActivity(intent);
        }

        holder.messageContainer.setOnClickListener { onMessageRowClicked(position) }
        holder.messageContainer.setOnLongClickListener { view ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: ViewHolder, user: User) {
        if (!TextUtils.isEmpty(user.profilePic) && user.profilePic.contains("http://")) {
            Glide.with(activity!!).load(user.profilePic)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyIconAnimation(holder: ViewHolder, position: Int) {
        if (selectedItems!!.get(position, false)) {
            holder.iconFront.visibility = View.GONE
            resetIconYAxis(holder.iconBack)
            holder.iconBack.visibility = View.VISIBLE
            holder.iconBack.alpha = 1f
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(activity, holder.iconBack, holder.iconFront, true)
                resetCurrentIndex()
            }
        } else {
            holder.iconBack.visibility = View.GONE
            resetIconYAxis(holder.iconFront)
            holder.iconFront.visibility = View.VISIBLE
            holder.iconFront.alpha = 1f
            if (reverseAllAnimations && animationItemsIndex!!.get(position, false) || currentSelectedIndex == position) {
                FlipAnimator.flipView(activity, holder.iconBack, holder.iconFront, false)
                resetCurrentIndex()
            }
        }
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex!!.clear()
    }

    private fun toggleSelected(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems!!.get(pos, false)) {
            selectedItems!!.delete(pos)
            animationItemsIndex!!.delete(pos)
        } else {
            selectedItems!!.put(pos, true)
            animationItemsIndex!!.put(pos, true)
        }
        adapter!!.notifyItemChanged(pos + 1)
    }

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems!!.clear()
        adapter!!.notifyDataSetChanged()
    }

    private fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems!!.size())
        for (i in 0 until selectedItems!!.size()) {
            items.add(selectedItems!!.keyAt(i))
        }
        return items
    }

    private fun removeData(position: Int) {
        users.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        val boomMenuButton: BoomMenuButton
        var imgProfile: ImageView
        var tv_name: TextView
        val tv_area: TextView
        val tv_role: TextView
        val tv_mobile: TextView
        val tv_email: TextView
        var iconContainer: RelativeLayout
        var iconBack: RelativeLayout
        var iconFront: RelativeLayout
        var iconText: TextView
        var messageContainer: LinearLayout
        var ll_mobile:LinearLayout
        var ll_email:LinearLayout

        init {
            imgProfile = itemView.findViewById(R.id.icon_profile)
            tv_name = itemView.findViewById(R.id.tv_name)
            tv_mobile = itemView.findViewById(R.id.tv_mobile)
            tv_email = itemView.findViewById(R.id.tv_email)
            tv_area = itemView.findViewById(R.id.tv_area)
            tv_role = itemView.findViewById(R.id.tv_role)
            boomMenuButton = itemView.findViewById(R.id.bmb1)
            iconText = itemView.findViewById(R.id.icon_text)
            iconBack = itemView.findViewById(R.id.icon_back)
            iconFront = itemView.findViewById(R.id.icon_front)
            messageContainer = itemView.findViewById(R.id.message_container)
            iconContainer = itemView.findViewById(R.id.icon_container)
            ll_mobile = itemView.findViewById(R.id.ll_mobile)
            ll_email = itemView.findViewById(R.id.ll_email)

            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View): Boolean {
            onRowLongClicked(adapterPosition)
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        DashboardActivity.spaceNavigationView.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        rootView?.shimmer_view_container?.stopShimmerAnimation()
        rootView?.shimmer_view_container?.visibility = View.GONE
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions[i])
        }
        adapter!!.notifyDataSetChanged()
    }

    override fun onRefresh() {
       // setupList()
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

           // rootView!!.swipe_refresh_layout.isEnabled = false
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {


            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_delete -> {
                    deleteMessages()
                    mode.finish()
                    return true
                }

                else -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
          //  rootView?.swipe_refresh_layout!!.isEnabled = true
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rootView?.lstFilter!!.post { resetAnimationIndex() }
        }
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = selectedItemCount

        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
        }
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }


    private fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    /*private fun onIconImportantClicked(position: Int) {
        val user = users[position]
        message.isImportant = !message.isImportant
        users[position] = message
        adapter!!.notifyDataSetChanged()
    }*/

    private fun onMessageRowClicked(position: Int) {
        if (selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            /*val user = users[position]
            user.isRead = true
            users[position] = user
            adapter!!.notifyDataSetChanged()*/
            Toast.makeText(activity, "Read: " + position, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }
}
