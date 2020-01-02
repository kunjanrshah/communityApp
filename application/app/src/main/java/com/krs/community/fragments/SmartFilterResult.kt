package com.krs.community.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Coroutines
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodel.SmartFilterViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class SmartFilterResult : Fragment(), KodeinAware, ByFilterListener, ParallaxRecyclerAdapter.OnLoadMore {

    override val kodein by kodein()

    private lateinit var rvFilters: RecyclerView
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private val lstMembers: MutableList<Member> = ArrayList()
    private lateinit var actionModeCallback: ActionModeCallback
    private lateinit var actionMode: ActionMode
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var selectedItems: SparseBooleanArray
    private lateinit var animationItemsIndex: SparseBooleanArray
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    private lateinit var smartFilterViewModel: SmartFilterViewModel
    private val factory: SmartFilterViewModelFactory by instance()
    private lateinit var tvCount:TextView
    private var selectedPosition = 0
    private var start: Int = 0
    private val length: Int = 30
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var llRoot:LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_filter_result, container, false)
        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        rvFilters = rootView.findViewById(R.id.lstFilter)
        actionModeCallback = ActionModeCallback()
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_view_container)
        smartFilterViewModel = ViewModelProviders.of(this,factory).get(SmartFilterViewModel::class.java)
        smartFilterViewModel.mByFilterListener =this
        llRoot= rootView.findViewById(R.id.ll_parent)

        adapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {
                val member = lstMembers[position]
                val holder = viewHolder as SmartFilterResult.ViewHolder

                if(lstMembers.size>0){
                    tvCount.visibility=View.VISIBLE
                    tvCount.text = "Member ${lstMembers.size} found"
                }else{
                    tvCount.visibility=View.GONE
                }

                holder.tvName.text = member.firstName
                holder.tvArea.text = member.area

                Coroutines.io {
                    if(!member.subCastId.isNullOrEmpty()){
                        viewHolder.tvName.text=member.firstName+" "+smartFilterViewModel.getLastNameById(member.subCastId.toInt())
                    }

                    if(!member.cityId.isNullOrEmpty()){
                        holder.tvArea.text = member.area+" "+smartFilterViewModel.getCityNamebyId(member.cityId)
                    }
                }

                holder.tvEmail.text = member.emailAddress
                holder.tvMobile.text = member.mobile

                holder.boomMenuButton.clearBuilders()
                for (i in 0 until holder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                holder.boomMenuButton.setOnClickListener { v: View? -> holder.boomMenuButton.boom() }
                holder.iconText.text = holder.tvName.text.substring(0, 1)
                holder.itemView.isActivated = selectedItems[position, false]
                applyIconAnimation(holder, position)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }
        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_filter, container, false)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, ExpandableFilterListFragment()) }
        tvCount = header.findViewById(R.id.tv_count)
        val ivExport = header.findViewById<ImageView>(R.id.iv_export)
        ivExport.setOnClickListener {

        }

        val ivAtoz = header.findViewById<ImageView>(R.id.iv_atoz)
        ivAtoz.setOnClickListener { v: View? ->
            val adapter = AtoZBottomAdapter(context)
            val dialog = DialogPlus.newDialog(context)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true, 1200)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            dialog.show()
        }
        adapter.setParallaxHeader(header, rvFilters)

        DashboardActivity.stop=false
        getFilterMembers()
        //setupList()
       // inbox
        return rootView
    }

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            start = (lstMembers.size+1)
            getFilterMembers()
        }
    }

    private fun getFilterMembers() {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            val jsonObject= JSONObject()
            val jsonObj=JSONObject()
            jsonObject.put("start",start)
            jsonObject.put("length",length)
            jsonObject.put("filter_by",jsonObj)
            val updated=  JsonParser().parse(jsonObject.toString()) as JsonObject
            smartFilterViewModel.smartFilterSearch(updated)
            Handler().postDelayed({
                shimmerFrameLayout.stopShimmerAnimation()
                shimmerFrameLayout.visibility=View.GONE
            },4000)
            lstMembers.clear()
            adapter.notifyDataSetChanged()

            if(start==0){
                shimmerFrameLayout.startShimmerAnimation()
                shimmerFrameLayout.visibility = View.VISIBLE
            }
            Utility.hideKeyboard(activity)
        }
    }

    override fun getMembers(response: SmartFilterResponse) {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility=View.GONE

        if(response.success){
            if (response.members.size > 0) {
                lstMembers.clear()
                lstMembers.addAll(response.members)
                adapter.notifyDataSetChanged()

                rvFilters.layoutManager?.scrollToPosition(selectedPosition)
                selectedPosition = lstMembers.size - 1
                DashboardActivity.stop = false

                if(lstMembers.size<=length){
                    DashboardActivity.stop = true
                    Snackbar.make(llRoot, "End of Records", Snackbar.LENGTH_LONG).show()
                }
            }else{
                DashboardActivity.stop = true
                Snackbar.make(llRoot, "End of Records", Snackbar.LENGTH_LONG).show()
            }
        }else {
            DashboardActivity.stop = false
        }

        Toast.makeText(activity,"Success", Toast.LENGTH_SHORT).show()
    }

    override fun getFailure(message: String) {
        Coroutines.main {
            DashboardActivity.stop = false
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE
            Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun setupList() {
        rvFilters.layoutManager = LinearLayoutManager(activity)
        rvFilters.adapter = adapter
        Handler().postDelayed({
            mShimmerViewContainer.stopShimmerAnimation()
            mShimmerViewContainer.visibility = View.GONE
        }, 2000)
    }*/

    /*private val inbox: Unit
        private get() {
            lstMembers!!.clear()
            for (i in 0..19) {
                val message = Message()
                message.id = 1
                message.isImportant = false
                message.message = "Now android supports multiple voice recogonization"
                message.picture = "https://api.androidhive.info/json/google.png"
                message.isRead = false
                message.timestamp = "10:30 AM"
                message.from = "Google Alerts"
                message.subject = "Google Alert - android"
                message.color = Utility.getRandomMaterialColor(activity, "400")
                lstMembers.add(message)
            }
            adapter!!.notifyDataSetChanged()
        }*/

    private fun applyClickEvents(holder: ViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener { onIconClicked(position) }
        holder.messageContainer.setOnClickListener { onMessageRowClicked(position) }
        holder.messageContainer.setOnLongClickListener { view ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: ViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            Glide.with(activity!!).load(member.profilePic)
                    .thumbnail(0.5f)
                    .transition(DrawableTransitionOptions.withCrossFade())
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
        if (selectedItems[position, false]) {
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
            if (reverseAllAnimations && animationItemsIndex[position, false] || currentSelectedIndex == position) {
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
        animationItemsIndex.clear()
    }

    private fun toggleSelected(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems[pos, false]) {
            selectedItems.delete(pos)
            animationItemsIndex.delete(pos)
        } else {
            selectedItems.put(pos, true)
            animationItemsIndex.put(pos, true)
        }
        adapter.notifyItemChanged(pos + 1)
    }

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private val selectedItemCount: Int
        private get() = selectedItems.size()

    private fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private fun removeData(position: Int) {
        lstMembers.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        val boomMenuButton: BoomMenuButton = itemView.findViewById(R.id.bmb1)
        val tvArea: TextView = itemView.findViewById(R.id.tv_area)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvMobile: TextView = itemView.findViewById(R.id.tv_mobile)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        var iconContainer: RelativeLayout = itemView.findViewById(R.id.icon_container)
        var iconBack: RelativeLayout = itemView.findViewById(R.id.icon_back)
        var iconFront: RelativeLayout = itemView.findViewById(R.id.icon_front)
        var iconText: TextView = itemView.findViewById(R.id.icon_text)
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
        var messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)

        init {
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
        mShimmerViewContainer.startShimmerAnimation()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        DashboardActivity.spaceNavigationView.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        mShimmerViewContainer.stopShimmerAnimation()
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions[i])
        }
        adapter.notifyDataSetChanged()
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    deleteMessages()
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
           // actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rvFilters.post { resetAnimationIndex() }
        }
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = selectedItemCount
        if (count == 0) {
            actionMode.finish()
        } else {
            actionMode.title = count.toString()
            actionMode.invalidate()
        }
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)!!
        }
        toggleSelection(position)
    }

    private fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)!!
        }
        toggleSelection(position)
    }

    private fun onIconImportantClicked(position: Int) {
        val message = lstMembers[position]
       // message.isImportant = !message.isImportant
        lstMembers[position] = message
        adapter.notifyDataSetChanged()
    }

    private fun onMessageRowClicked(position: Int) {
        if (selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            val message = lstMembers[position]
            //message.isRead = true
            lstMembers[position] = message
            adapter.notifyDataSetChanged()
           // Toast.makeText(activity, "Read: " + message.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }


}