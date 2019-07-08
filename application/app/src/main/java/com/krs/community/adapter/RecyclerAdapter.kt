package com.krs.community.adapter

import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.krs.community.model.DataProvider
import com.krs.community.model.Message
import com.krs.community.utils.Utility


class RecyclerAdapter<T : DataProvider.DataProvider1.BaseData>(private var dataSet: List<T>,
                                                               val itemClickListener: ItemClickListener? = null,
                                                               private var onClickListener: View.OnClickListener? = null,
                                                               val messages: ArrayList<Message>,
                                                               listener1: RecyclerViewAdapterListener,
                                                               val animationItemsIndex: SparseBooleanArray,
                                                               var reverseAllAnimations: Boolean)
    : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private val selectedItems: SparseBooleanArray? = SparseBooleanArray()
    private val listener: RecyclerViewAdapterListener? = listener1
    private var currentSelectedIndex = -1

    interface RecyclerViewAdapterListener
    {

    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {

        return when (viewType) {
            0 -> BaseViewHolder.DetailsViewHolder(parent) as BaseViewHolder<T>
            else -> BaseViewHolder.CardViewHolder(parent) as BaseViewHolder<T>
        }
    }


    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        val type = dataSet[0].javaClass
        return when (type) {
            DataProvider.DataProvider1.Details::class.java -> 0
            else -> 1
        }
    }

    interface ItemClickListener {
        fun itemClick(id : Int)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int)
    {

        val message = messages[position]
        val name = "Kunjan Shah"
        holder.tv_name.setText(name)
        holder.boomMenuButton.clearBuilders()
        for (i in 0 until holder.boomMenuButton.getPiecePlaceEnum().pieceNumber()) {
            holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
        }
        holder.boomMenuButton.setOnClickListener({ v -> holder.boomMenuButton.boom() })

        holder.bind(dataSet[position], itemClickListener,onClickListener)
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems?.clear()
        notifyDataSetChanged()
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems!!.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
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
}