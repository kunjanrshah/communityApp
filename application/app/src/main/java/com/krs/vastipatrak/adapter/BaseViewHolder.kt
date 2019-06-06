package com.krs.vastipatrak.adapter

import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.krs.vastipatrak.R
import com.krs.vastipatrak.model.DataProvider
import com.krs.vastipatrak.utils.AppConstants.TRANSITION_CARD
import com.krs.vastipatrak.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_details.*
import kotlinx.android.synthetic.main.row_list.*

/**
 * Created by Alexander Kolpakov on 29.07.2018
 */
abstract class  BaseViewHolder<T : DataProvider.DataProvider1.BaseData>(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

    override val containerView: View?
        get() = itemView

    abstract fun bind(data: T, listener: RecyclerAdapter.ItemClickListener?, listener1: View.OnClickListener?)


    class CardViewHolder(parent: ViewGroup) : BaseViewHolder<DataProvider.DataProvider1.Card>(parent.inflate(R.layout.row_list)) {

        override fun bind(data: DataProvider.DataProvider1.Card, listener: RecyclerAdapter.ItemClickListener?,listener1: View.OnClickListener?) {
            containerView?.setOnClickListener(listener1)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                containerView?.transitionName = TRANSITION_CARD + adapterPosition
            }

            tv_title.text = data.name
            tv_amount.text = data.amount
            tv_date.text = data.date
            tv_status.text = data.status.code
            img_status.setImageResource(data.status.iconId)
            img_card.setImageResource(data.imageId)
        }
    }

    class DetailsViewHolder(parent: ViewGroup) : BaseViewHolder<DataProvider.DataProvider1.Details>(parent.inflate(R.layout.item_details)) {

        override fun bind(data: DataProvider.DataProvider1.Details, listener: RecyclerAdapter.ItemClickListener?,listener1: View.OnClickListener?) {

            itemView.setOnClickListener({
                listener!!.itemClick(data.id)
            })

            tv_details_title.text = data.title
            tv_details_subtitle.text = data.subtitle
            tv_details_amount.text = data.amount
        }

    }
}


