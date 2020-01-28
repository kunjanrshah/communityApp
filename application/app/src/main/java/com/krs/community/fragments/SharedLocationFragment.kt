package com.krs.community.fragments

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.krs.community.R
import com.krs.community.model.Member
import com.krs.community.model.Message
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.nightonke.boommenu.BoomMenuButton
import java.util.*

class SharedLocationFragment : Fragment() {
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var selectedItems: SparseBooleanArray  = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray = SparseBooleanArray()
    private var reverseAllAnimations = false
    private lateinit var rvProfiles: RecyclerView
    private val messages: MutableList<Member> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorBG, false)
        }
        val root = inflater.inflate(R.layout.fragmnet_favorite, container, false)

        actionModeCallback = ActionModeCallback()
        adapter = object : ParallaxRecyclerAdapter<Member>(messages) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {
                val message = messages[position]
                val name = "Kunjan Shah"
                val holder = viewHolder as ListViewHolder
                holder.tv_name.text = name
                holder.boomMenuButton.clearBuilders()
                for (i in 0 until holder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                holder.boomMenuButton.setOnClickListener { v: View? -> holder.boomMenuButton.boom() }
                holder.iconText.text = name.substring(0, 1)
                holder.itemView.isActivated = selectedItems!![position, false]
                applyIconAnimation(holder, position)
                applyProfilePicture(holder, message)
                applyClickEvents(holder, position)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.favorite_list_item, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return messages.size
            }
        }
        val MyLayoutManager = LinearLayoutManager(activity)
        rvProfiles = root.findViewById(R.id.rv_favorite)
        rvProfiles.setLayoutManager(MyLayoutManager)
        rvProfiles.setItemAnimator(DefaultItemAnimator())
        rvProfiles.setHasFixedSize(true)
        val header = LayoutInflater.from(activity).inflate(R.layout.header_favorite, container, false)
        val iv_cancel = header.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        adapter.setParallaxHeader(header, rvProfiles)
        rvProfiles.setAdapter(adapter)
        inbox
        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private val inbox: Unit
        private get() {
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
                message.color = Utility.getRandomMaterialColor(activity, "400")
                messages.add(message)
            }
            adapter.notifyDataSetChanged()
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

    private fun applyClickEvents(holder: ListViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener {
            if (actionMode == null) {
                actionMode = activity!!.startActionMode(actionModeCallback)
            }
            toggleSelection(position)
        }
        holder.messageContainer.setOnClickListener { view: View? ->
            // verify whether action mode is enabled or not
            // if enabled, change the row state to activated
            if (selectedItemCount > 0) {
                enableActionMode(position)
            } else { // read the message which removes bold from the row
                val message = messages[position]
                message.isRead = true
                messages[position] = message
                adapter.notifyDataSetChanged()
                Toast.makeText(activity, "Read: " + message.message, Toast.LENGTH_SHORT).show()
            }
        }
        holder.messageContainer.setOnLongClickListener { view ->
            enableActionMode(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: ListViewHolder, message: Member) {
        if (!TextUtils.isEmpty(message.picture)) {
            Glide.with(activity!!).load(message.picture)
                    .thumbnail(0.5f)
                    .transition(DrawableTransitionOptions.withCrossFade())
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

    private fun applyIconAnimation(holder: ListViewHolder, position: Int) {
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

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)
        }
        toggleSelection(position)
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

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private fun removeData(position: Int) {
        messages.removeAt(position)
        resetCurrentIndex()
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions[i])
        }
        adapter.notifyDataSetChanged()
    }

    private fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private val selectedItemCount: Int
        get() = selectedItems.size()

    private inner class ListViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v), View.OnLongClickListener {
        var boomMenuButton: BoomMenuButton
        var iconContainer: RelativeLayout
        var iconBack: RelativeLayout
        var iconFront: RelativeLayout
        var iconText: TextView
        var tv_name: TextView
        var imgProfile: ImageView
        var messageContainer: LinearLayout
        override fun onLongClick(v: View): Boolean {
            enableActionMode(adapterPosition)
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }

        init {
            tv_name = v.findViewById(R.id.tv_name)
            boomMenuButton = v.findViewById(R.id.boomMenuButton)
            iconText = v.findViewById(R.id.icon_text)
            iconBack = v.findViewById(R.id.icon_back)
            iconFront = v.findViewById(R.id.icon_front)
            imgProfile = v.findViewById(R.id.icon_profile)
            messageContainer = v.findViewById(R.id.message_container)
            iconContainer = v.findViewById(R.id.icon_container)
            v.setOnLongClickListener(this)
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.fav_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    // delete all the selected messages
                    deleteMessages()
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rvProfiles.post {
                resetAnimationIndex()
                adapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        private var currentSelectedIndex = -1
    }
}