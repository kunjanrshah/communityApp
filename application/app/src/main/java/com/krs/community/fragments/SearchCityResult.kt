package com.krs.community.fragments

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

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.model.Message
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus

import java.util.ArrayList

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.krs.community.interfaces.IbrowseCityRecordsListener
import com.krs.community.model.SearchByCityModel
import com.krs.community.viewmodel.BrowseCityViewModel
import com.krs.community.viewmodel.BrowseCityViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchCityResult : Fragment(), SwipeRefreshLayout.OnRefreshListener, KodeinAware, IbrowseCityRecordsListener {
    override fun getSearchRecords(data: SearchByCityModel) {
        Log.d("SearchCityResult","data: "+data)
    }

    override suspend fun getFailure(message: Boolean) {
        Log.d("SearchCityResult","message: "+message)
    }

    private var rv_filters: RecyclerView? = null
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private val messages = ArrayList<Message>()
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var adapter: ParallaxRecyclerAdapter<Message>? = null
    private var selectedItems: SparseBooleanArray? = null
    private var animationItemsIndex: SparseBooleanArray? = null
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    private val selectedItemCount: Int get() = selectedItems!!.size()

    private val factory: BrowseCityViewModelFactory by instance()
    internal var browseCityViewModel: BrowseCityViewModel? = null
    override val kodein by kodein()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_filter_result, container, false)
        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        rv_filters = rootView.findViewById(R.id.lstFilter)
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout!!.setOnRefreshListener(this)
        actionModeCallback = ActionModeCallback()

        val city_name = if (this.arguments != null) this.arguments!!.getString("city_name") else null

        browseCityViewModel = ViewModelProviders.of(this,factory).get(BrowseCityViewModel::class.java)
        browseCityViewModel?.ibrowseCityRecordsListener=this

        adapter = object : ParallaxRecyclerAdapter<Message>(messages) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Message>, position: Int) {

                val message = messages[position]
                val name = "Kunjan Shah"

                val holder =  viewHolder as SearchCityResult.ViewHolder

                holder.tv_name.setText(name)
                holder.boomMenuButton.clearBuilders()

                for (i in 0 until holder.boomMenuButton.getPiecePlaceEnum().pieceNumber()) {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                holder.boomMenuButton.setOnClickListener({ v -> holder.boomMenuButton.boom() })

                holder.iconText.setText(name.substring(0, 1))
                holder.itemView.isActivated = selectedItems!!.get(position, false)
                applyIconAnimation(holder, position)
                applyProfilePicture(holder, message)
                applyClickEvents(holder, position)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Message>, i: Int): RecyclerView.ViewHolder {
                return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Message>): Int {
                return messages?.size ?: 0
            }
        }


        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_filter, container, false)
        val iv_cancel = header.findViewById<ImageView>(R.id.iv_cancel)

        val tvTitle = header.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = city_name

        val fitlerName = header.findViewById<EditText>(R.id.edt_filter_name)
        fitlerName.visibility = View.GONE

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

        adapter!!.setParallaxHeader(header, rv_filters!!)

        setupList()
        getInbox()
        return rootView
    }

    private fun setupList() {
        rv_filters!!.layoutManager = LinearLayoutManager(activity)
        // mAdapter = new FilterResultAdapter(getActivity(), messages, this);
        rv_filters!!.adapter = adapter

        Handler().postDelayed({
            mShimmerViewContainer!!.stopShimmerAnimation()
            mShimmerViewContainer!!.visibility = View.GONE
        }, 2000)
    }

    private fun getInbox() {
        swipeRefreshLayout!!.isRefreshing = true
        messages.clear()

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
            message.color = Utility.getRandomMaterialColor(activity!!, "400")
            messages.add(message)
        }

        adapter!!.notifyDataSetChanged()
        swipeRefreshLayout!!.isRefreshing = false
    }

    private fun applyClickEvents(holder: ViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener { onIconClicked(position) }


        holder.messageContainer.setOnClickListener { onMessageRowClicked(position) }

        holder.messageContainer.setOnLongClickListener { view ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: ViewHolder, message: Message) {
        if (!TextUtils.isEmpty(message.picture)) {
            Glide.with(activity!!).load(message.picture)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(message.color)
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
        messages.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }


    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        private val iv_profile: ImageView
        val boomMenuButton: BoomMenuButton
        private val tv_area: TextView
        private val tv_role: TextView
        private val tv_mobile: TextView
        private val tv_email: TextView

        internal var iconContainer: RelativeLayout
        internal var iconBack: RelativeLayout
        internal var iconFront: RelativeLayout
        internal var iconText: TextView
        internal var tv_name: TextView
        internal var imgProfile: ImageView
        internal var messageContainer: LinearLayout


        init {
            iv_profile = itemView.findViewById(R.id.iv_profile)
            boomMenuButton = itemView.findViewById(R.id.bmb1)
            tv_name = itemView.findViewById(R.id.tv_name)
            tv_area = itemView.findViewById(R.id.tv_area)
            tv_role = itemView.findViewById(R.id.tv_role)
            tv_mobile = itemView.findViewById(R.id.tv_mobile)
            tv_email = itemView.findViewById(R.id.tv_email)

            tv_name = itemView.findViewById(R.id.tv_name)
            iconText = itemView.findViewById(R.id.icon_text)
            iconBack = itemView.findViewById(R.id.icon_back)
            iconFront = itemView.findViewById(R.id.icon_front)
            imgProfile = itemView.findViewById(R.id.icon_profile)
            messageContainer = itemView.findViewById(R.id.message_container)
            iconContainer = itemView.findViewById(R.id.icon_container)
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
        mShimmerViewContainer!!.startShimmerAnimation()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        DashboardActivity.spaceNavigationView.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        mShimmerViewContainer!!.stopShimmerAnimation()
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
        getInbox()
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            swipeRefreshLayout!!.isEnabled = false
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
            swipeRefreshLayout!!.isEnabled = true
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rv_filters!!.post { resetAnimationIndex() }
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


    private fun onIconImportantClicked(position: Int) {
        val message = messages[position]
        message.isImportant = !message.isImportant
        messages[position] = message
        adapter!!.notifyDataSetChanged()
    }

    private fun onMessageRowClicked(position: Int) {
        if (selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            val message = messages[position]
            message.isRead = true
            messages[position] = message
            adapter!!.notifyDataSetChanged()

            Toast.makeText(activity, "Read: " + message.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }
}
