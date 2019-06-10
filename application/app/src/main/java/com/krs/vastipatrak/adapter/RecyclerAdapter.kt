package com.krs.vastipatrak.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.krs.vastipatrak.fragments.HeaderDetailFragment
import com.krs.vastipatrak.model.DataProvider


/**
 * Created by Alexander Kolpakov on 17.07.2018
 */
class RecyclerAdapter<T : DataProvider.DataProvider1.BaseData>(private var dataSet: List<T>, val itemClickListener: ItemClickListener? = null,private var onClickListener: View.OnClickListener? = null)
    : RecyclerView.Adapter<BaseViewHolder<T>>() {

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

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) = holder.bind(dataSet[position], itemClickListener,onClickListener)
}