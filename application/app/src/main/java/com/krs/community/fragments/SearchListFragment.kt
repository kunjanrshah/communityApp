package com.krs.community.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.krs.community.R
import com.krs.community.activity.FamilyTreeListView
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.model.Message
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.krs.community.utils.copyViewImage
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_search_result.*

class SearchListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener{

    private var rv_search: RecyclerView? = null
    private var mShimmerViewContainer: ShimmerFrameLayout? = null
    private var multiSearchView: MultiSearchView? = null
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private val messages = ArrayList<Message>()
    lateinit var view1: View
    private var adapter: ParallaxRecyclerAdapter<Message>? =null
    private var selectedItems: SparseBooleanArray? = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray? = SparseBooleanArray()
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1

    fun getSelectedItemCount(): Int {
        return selectedItems!!.size()
    }

    override fun onRefresh() {
        getInbox()
    }

    fun toggleSelected(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems!!.get(pos, false)) {
            selectedItems!!.delete(pos)
            animationItemsIndex!!.delete(pos)
        } else {
            selectedItems!!.put(pos, true)
            animationItemsIndex!!.put(pos, true)
        }
        adapter!!.notifyItemChanged(pos)
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = getSelectedItemCount()

        if (count == 0) {
            actionMode!!.finish()

        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
        }
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex!!.clear()
        }
    }

    fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems!!.size())
        for (i in 0 until selectedItems!!.size()) {
            items.add(selectedItems!!.keyAt(i))
        }
        return items
    }

    fun removeData(position: Int) {
        messages!!.removeAt(position)
        resetCurrentIndex()
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions.get(i))
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_result, container, false)
        view1 = rootView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        rv_search = rootView.findViewById(R.id.rv_search)
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        swipeRefreshLayout = rootView.findViewById<View>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout!!.setOnRefreshListener(this)
        actionModeCallback = ActionModeCallback()
        (activity as AppCompatActivity).supportActionBar!!.title = "Smart Search"

        adapter=object: ParallaxRecyclerAdapter<Message>(messages) {
            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Message>?): Int {
                return messages.size
            }

            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder?, adapter: ParallaxRecyclerAdapter<Message>?, position: Int) {
                var viewHolder: MyViewHolder = viewHolder as MyViewHolder

                val message = messages[position]
                val name = "Kunjan Shah"

                viewHolder.tv_name.text = name
                viewHolder.boomMenuButton.clearBuilders()

                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                   val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    if (builder != null) {
                        builder.listener {
                            if(it==1)
                            {
                                val intent:Intent=Intent(activity, FamilyTreeListView::class.java)
                                startActivity(intent)

                            }else
                            {
                                Toast.makeText(activity, "Clicked " + it, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    viewHolder.boomMenuButton.addBuilder(builder)
                }
                viewHolder.boomMenuButton.setOnClickListener { v -> viewHolder.boomMenuButton.boom() }

                viewHolder.iconText.text = name.substring(0, 1)
                viewHolder.itemView.setActivated(selectedItems!!.get(position, false))

                applyIconAnimation(viewHolder, position)
                applyProfilePicture(viewHolder, message)
                applyClickEvents(viewHolder, position)

            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Message>?, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_list_search, viewGroup, false))
            }
        }

        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_search, container, false)


        multiSearchView = header.findViewById(R.id.multiSearchView)
        val iv_atoz = header.findViewById(R.id.iv_atoz) as ImageView
        iv_atoz.setOnClickListener {

            val adapter: AtoZBottomAdapter =AtoZBottomAdapter(context)
            val dialog = DialogPlus.newDialog(context)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            dialog.show()
        }

        multiSearchView!!.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener {
            override fun onTextChanged(index: Int, s: CharSequence) {
                // Toast.makeText(getActivity(), "onTextChanged", Toast.LENGTH_SHORT).show();
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                Toast.makeText(activity, "onSearchComplete", Toast.LENGTH_SHORT).show()
            }

            override fun onSearchItemRemoved(index: Int) {
                Toast.makeText(activity, "onSearchItemRemoved", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(index: Int, s: CharSequence) {
                Toast.makeText(activity, "onItemSelected", Toast.LENGTH_SHORT).show()
            }
        })

        val iv_cancel = header.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel.setOnClickListener {
            Utility.movetoFragment(activity, DashboardFragment())
        }

        adapter?.setParallaxHeader(header, rv_search)

        getInbox()
        setupList()

        return rootView
    }

    private fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    private fun onMessageRowClicked(position: Int, v: View) {

        if (getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {

            val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            fragmentTransaction?.commitAllowingStateLoss()
            // startAnimation(copy, fragmentTransaction)

        }
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener { view -> onIconClicked(position) }

        holder.messageContainer.setOnClickListener { view -> onMessageRowClicked(position,holder.itemView) }

        holder.messageContainer.setOnLongClickListener { view ->

            enableActionMode(position)

            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    private fun applyProfilePicture(holder: MyViewHolder, message: Message) {
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

    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {
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

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems!!.clear()
        adapter!!.notifyDataSetChanged()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }


    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener {
        internal var iconText: TextView
        internal var tv_name: TextView
        internal var imgProfile: ImageView
        internal var messageContainer: LinearLayout
        internal var iconContainer: RelativeLayout
        internal var iconBack: RelativeLayout
        internal var iconFront: RelativeLayout
        internal var boomMenuButton: BoomMenuButton

        init {

            boomMenuButton = view.findViewById(R.id.boomMenuButton1)
            tv_name = view.findViewById(R.id.tv_name1)
            iconText = view.findViewById(R.id.icon_text1)
            iconBack = view.findViewById(R.id.icon_back1)
            iconFront = view.findViewById(R.id.icon_front1)
            imgProfile = view.findViewById(R.id.icon_profile1)
            messageContainer = view.findViewById(R.id.message_container1)
            iconContainer = view.findViewById(R.id.icon_container1)
            view.setOnLongClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            enableActionMode(adapterPosition)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    private fun setupList() {

        rv_search!!.layoutManager = LinearLayoutManager(activity)



        rv_search!!.adapter = adapter
        rv_search!!.setHasFixedSize(true)
        Handler().postDelayed({
            // stop animating Shimmer and hide the layout
            mShimmerViewContainer!!.stopShimmerAnimation()
            mShimmerViewContainer!!.visibility = View.GONE
        }, 3000)
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

        adapter?.notifyDataSetChanged()
        swipeRefreshLayout?.isRefreshing = false
    }

    override fun onStart() {
        super.onStart()
        mShimmerViewContainer!!.startShimmerAnimation()
        Handler().postDelayed({
            multiSearchView!!.binding.imageViewSearch.performClick()
        }, 100)
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity).supportActionBar!!.hide()
        Handler().postDelayed({
            Utility.hideKeyboard(activity)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar!!.show()
        mShimmerViewContainer!!.stopShimmerAnimation()
        super.onStop()
        Handler().postDelayed({
            Utility.hideKeyboard(activity)
        }, 500)
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = rv_search!!.getChildAdapterPosition(view)
        val detailsFragment = FamilyDetailFragment.newInstance(positions, adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.container_body, detailsFragment, FamilyDetailFragment.TAG)
                ?.addToBackStack(null)

        return transaction
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout!!.isEnabled = false
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            /* ViewGroup   decorView = (ViewGroup) getActivity().getWindow().getDecorView().findViewById(R.id.action_mode_bar);
            decorView.setBackgroundColor(getResources().getColor(R.color.colorBG));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(),R.color.colorBG,true);
            }*/

            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
                when (item.itemId) {
                    R.id.action_delete -> {
                        // delete all the selected messages
                        deleteMessages()
                        mode.finish()
                        true
                    }

                    else -> false
                }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            swipeRefreshLayout!!.isEnabled = true
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rv_search?.post {
                resetAnimationIndex()
                // mAdapter.notifyDataSetChanged();
            }
        }
    }

    private fun enableActionMode(position: Int) {

        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }
}
