package com.krs.community.adapter

import android.os.Build
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.krs.community.R
import com.krs.community.model.DataProvider
import com.krs.community.model.Message
import com.krs.community.utils.AppConstants
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.krs.community.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_list_search.*

class RecyclerAdapter<messages1 : MutableList<Message>>(var mContext: FragmentActivity?,
                                                        var messages: java.util.ArrayList<Message>,
                                                        var itemClickListener: ItemClickListener? = null,
                                                        var onClickListener: View.OnClickListener? = null, var listener: RecyclerAdapterListener? = null)
    : RecyclerView.Adapter<RecyclerAdapter.BaseViewHolder.CardViewHolder<MutableList<Message>>>() {

    private var selectedItems: SparseBooleanArray? = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray? = SparseBooleanArray()
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder.CardViewHolder<MutableList<Message>> {

        return BaseViewHolder.CardViewHolder(parent)

        /*return when (viewType) {
            0 -> BaseViewHolder.DetailsViewHolder(parent) as BaseViewHolder<MutableList<Message>>
            else -> BaseViewHolder.CardViewHolder<Any>(parent) as BaseViewHolder<MutableList<Message>>
        }*/
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        val type = messages[0].javaClass
        return when (type) {
            DataProvider.DataProvider1.Details::class.java -> 0
            else -> 1
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder.CardViewHolder<MutableList<Message>>, position: Int) {

        holder.bind(messages[position], itemClickListener, onClickListener)

        holder.itemView.isActivated = selectedItems!!.get(position, false)

        val message = messages.get(position)
        applyProfilePicture(holder, message)
        applyIconAnimation(holder, position)
        applyClickEvents(holder, position)
    }

    interface ItemClickListener {
        fun itemClick(id: Int)
    }

    abstract class BaseViewHolder<messages : MutableList<Message>>(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView: View?
            get() = itemView

        abstract fun bind(message: Message, itemClickListener: ItemClickListener?, onClickListener: View.OnClickListener?)

        class CardViewHolder<T>(parent: ViewGroup) : BaseViewHolder<MutableList<Message>>(parent.inflate(R.layout.row_list_search)) {

            override fun bind(message: Message, itemClickListener: ItemClickListener?, onClickListener: View.OnClickListener?) {
                containerView?.setOnClickListener(onClickListener)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    containerView?.transitionName = AppConstants.TRANSITION_CARD + adapterPosition
                }

                boomMenuButton1!!.clearBuilders()

                for (i in 0 until boomMenuButton1!!.piecePlaceEnum.pieceNumber()) {
                    boomMenuButton1!!.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                boomMenuButton1!!.setOnClickListener({ v -> boomMenuButton1!!.boom() })

                // val message = messages[position]
                val name = "Kunjan Shah"
                tv_name1!!.text = name

                // displaying the first letter of From in icon text
                icon_text1!!.text = name.substring(0, 1)
            }
        }
    }

    private fun applyClickEvents(holder: BaseViewHolder.CardViewHolder<MutableList<Message>>, position: Int) {
        holder.icon_container1.setOnClickListener({ listener?.onIconClicked(position) })

        holder.message_container1.setOnClickListener({ listener?.onMessageRowClicked(position, holder.itemView) })

        holder.message_container1.setOnLongClickListener({ view ->
            listener?.onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        })
    }

    private fun applyProfilePicture(holder: BaseViewHolder.CardViewHolder<MutableList<Message>>, message: Message) {
        if (!TextUtils.isEmpty(message.picture)) {
            Glide.with(mContext!!).load(message.picture)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.icon_profile1!!)
            //holder.imgProfile.setColorFilter(null)
            holder.icon_text1!!.visibility = View.GONE
        } else {
            holder.icon_profile1!!.setImageResource(R.drawable.bg_circle)
            holder.icon_profile1!!.setColorFilter(message.color)
            holder.icon_text1!!.visibility = View.VISIBLE
        }
    }

    private fun applyIconAnimation(holder: BaseViewHolder.CardViewHolder<MutableList<Message>>, position: Int) {
        if (selectedItems!!.get(position, false)) {
            holder.icon_front1!!.visibility = View.GONE
            resetIconYAxis(holder.icon_back1!!)
            holder.icon_back1!!.visibility = View.VISIBLE
            holder.icon_back1!!.alpha = 1f
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.icon_back1, holder.icon_front1, true)
                resetCurrentIndex()
            }
        } else {
            holder.icon_back1!!.visibility = View.GONE
            holder.icon_front1?.let { resetIconYAxis(it) }
            holder.icon_front1?.visibility = View.VISIBLE
            holder.icon_front1?.alpha = 1f
            if (reverseAllAnimations && animationItemsIndex!!.get(position, false) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.icon_back1, holder.icon_front1, false)
                resetCurrentIndex()
            }
        }
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex!!.clear()
        }
    }

    fun toggleSelection(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems!!.get(pos, false)) {
            selectedItems!!.delete(pos)
            animationItemsIndex!!.delete(pos)
        } else {
            selectedItems!!.put(pos, true)
            animationItemsIndex!!.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems!!.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItemCount(): Int {
        return selectedItems!!.size()
    }

    fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems!!.size())
        for (i in 0 until selectedItems!!.size()) {
            items.add(selectedItems!!.keyAt(i))
        }
        return items
    }

    fun removeData(position: Int) {
        messages.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    interface RecyclerAdapterListener {
        fun onIconClicked(position: Int)

        fun onIconImportantClicked(position: Int)

        fun onMessageRowClicked(position: Int, view: View)

        fun onRowLongClicked(position: Int)
    }

}

