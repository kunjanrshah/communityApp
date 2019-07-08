package com.krs.community.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.nightonke.boommenu.BoomMenuButton
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_list_search.*

class RecyclerAdapter<messages1: MutableList<Message>>(var mContext: FragmentActivity?,
                                                      var messages: java.util.ArrayList<Message>,
                                                      var itemClickListener: ItemClickListener? = null,
                                                      var onClickListener: View.OnClickListener? = null)
    : RecyclerView.Adapter<RecyclerAdapter.BaseViewHolder.CardViewHolder<MutableList<Message>>>() {


    private var listener: RecyclerAdapterListener? = null
    private var selectedItems: SparseBooleanArray? = SparseBooleanArray()

    // array used to perform multiple animation at once
    private var animationItemsIndex: SparseBooleanArray? = SparseBooleanArray()
    private var reverseAllAnimations = false

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private var currentSelectedIndex = -1

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder.CardViewHolder<MutableList<Message>> {

        return BaseViewHolder.CardViewHolder(parent)

        /*return when (viewType) {
            0 -> BaseViewHolder.DetailsViewHolder(parent) as BaseViewHolder<MutableList<Message>>
            else -> BaseViewHolder.CardViewHolder<Any>(parent) as BaseViewHolder<MutableList<Message>>
        }*/
    }

    override fun getItemCount() = messages!!.size

    override fun getItemViewType(position: Int): Int {
        val type = messages[0].javaClass
        return when (type) {
            DataProvider.DataProvider1.Details::class.java -> 0
            else -> 1
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder.CardViewHolder<MutableList<Message>>, position: Int) {

        holder.bind(messages[position], itemClickListener, onClickListener)

        // change the row state to activated
        holder.itemView.setActivated(selectedItems!!.get(position, false))

        val message = messages!!.get(position)
        applyProfilePicture(holder,message)

        // apply click events
        applyClickEvents(holder, position)


        applyIconAnimation(holder,position)


    }

    interface ItemClickListener {
        fun itemClick(id: Int)
    }

    abstract class BaseViewHolder<messages: MutableList<Message>>(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView: View?
            get() = itemView

        abstract fun bind(message: Message, itemClickListener: ItemClickListener?, onClickListener: View.OnClickListener?)

        class CardViewHolder<T>(parent: ViewGroup) : BaseViewHolder<MutableList<Message>>(parent.inflate(R.layout.row_list_search)) {


         //   var view:View=parent.inflate(R.layout.row_list_search)

            /*var iconContainer: RelativeLayout = view.findViewById(R.id.icon_container1)
            var iconBack: RelativeLayout? = view.findViewById(R.id.icon_back1)
            var iconFront: RelativeLayout? = view.findViewById(R.id.icon_front1)
            var iconText: TextView? = view.findViewById(R.id.icon_text1)
            var tv_name: TextView? = view.findViewById(R.id.tv_name1)
            var imgProfile: ImageView? = view.findViewById(R.id.icon_profile1)
            var messageContainer: LinearLayout? = view.findViewById(R.id.message_container1)
            var boomMenuButton1: BoomMenuButton? = view.findViewById(R.id.boomMenuButton1)*/



            override fun bind(message: Message, itemClickListener: ItemClickListener?, onClickListener: View.OnClickListener?) {
                containerView?.setOnClickListener(onClickListener)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    containerView?.transitionName = AppConstants.TRANSITION_CARD + adapterPosition
                }


                boomMenuButton1!!.clearBuilders()

                for (i in 0 until boomMenuButton1!!.getPiecePlaceEnum().pieceNumber()) {
                    boomMenuButton1!!.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                boomMenuButton1!!.setOnClickListener({ v -> boomMenuButton1!!.boom() })

                // val message = messages[position]
                val name = "Kunjan Shah"
                tv_name1!!.setText(name)

                /*tv_title.text = data.name
                tv_amount.text = data.amount
                tv_date.text = data.date
                tv_status.text = data.status.code
                img_status.setImageResource(data.status.iconId)
                img_card.setImageResource(data.imageId)*/

                // displaying the first letter of From in icon text
                icon_text1!!.setText(name.substring(0, 1))

            }
        }

        /*class DetailsViewHolder(parent: ViewGroup) : BaseViewHolder<DataProvider.DataProvider1.Details>(parent.inflate(R.layout.item_details_search)) {

            override fun bind(data: DataProvider.DataProvider1.Details, itemClickListener: ItemClickListener?, onClickListener: View.OnClickListener?) {

                itemView.setOnClickListener({
                    itemClickListener!!.itemClick(data.id)
                })

                boomMenuButton.clearBuilders()

                for (i in 0 until boomMenuButton.getPiecePlaceEnum().pieceNumber()) {
                    boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                boomMenuButton.setOnClickListener({ v -> boomMenuButton.boom() })

                *//* tv_details_title.text = data.title
                 tv_details_subtitle.text = data.subtitle
                 tv_details_amount.text = data.amount*//*
            }
        }*/
    }


    private fun applyClickEvents(holder: BaseViewHolder.CardViewHolder<MutableList<Message>>, position: Int) {
        holder.icon_container1.setOnClickListener({ listener!!.onIconClicked(position) })

        holder.message_container1!!.setOnClickListener({ listener!!.onMessageRowClicked(position) })

        holder.message_container1!!.setOnLongClickListener({ view ->
            listener!!.onRowLongClicked(position)
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
            holder.icon_text1!!.setVisibility(View.GONE)
        } else {
            holder.icon_profile1!!.setImageResource(R.drawable.bg_circle)
            holder.icon_profile1!!.setColorFilter(message.color)
            holder.icon_text1!!.setVisibility(View.VISIBLE)
        }
    }

    private fun applyIconAnimation(holder: BaseViewHolder.CardViewHolder<MutableList<Message>>, position: Int) {
        if (selectedItems!!.get(position, false)) {
            holder.icon_front1!!.setVisibility(View.GONE)
            resetIconYAxis(holder.icon_back1!!)
            holder.icon_back1!!.setVisibility(View.VISIBLE)
            holder.icon_back1!!.setAlpha(1f)
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.icon_back1, holder.icon_front1, true)
                resetCurrentIndex()
            }
        } else {
            holder.icon_back1!!.setVisibility(View.GONE)
            holder.icon_front1?.let { resetIconYAxis(it) }
            holder.icon_front1?.setVisibility(View.VISIBLE)
            holder.icon_front1?.setAlpha(1f)
            if (reverseAllAnimations && animationItemsIndex!!.get(position, false) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.icon_back1, holder.icon_front1, false)
                resetCurrentIndex()
            }
        }
    }


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
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
        messages!!.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    interface RecyclerAdapterListener {
        fun onIconClicked(position: Int)

        fun onIconImportantClicked(position: Int)

        fun onMessageRowClicked(position: Int)

        fun onRowLongClicked(position: Int)
    }

}

