package com.krs.community.adapter

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krs.community.R
import com.krs.community.model.DataProvider
import com.krs.community.utils.AppConstants.TRANSITION_CARD
import com.krs.community.utils.Utility
import com.krs.community.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_details_search.*
import kotlinx.android.synthetic.main.row_list_search.*

abstract class  BaseViewHolder<T : DataProvider.DataProvider1.BaseData>(view: View) : RecyclerView.ViewHolder(view),View.OnLongClickListener, LayoutContainer {

    override val containerView: View?
        get() = itemView

    abstract fun bind(data: T, itemClickListener: RecyclerAdapter.ItemClickListener?, onClickListener: View.OnClickListener?)


    class CardViewHolder(parent: ViewGroup) : BaseViewHolder<DataProvider.DataProvider1.Card>(parent.inflate(R.layout.row_list_search)) {
        override fun onLongClick(v: View?): Boolean {
            listener.onRowLongClicked(adapterPosition)
            if (v != null) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
            return true
        }


        override fun bind(data: DataProvider.DataProvider1.Card, itemClickListener: RecyclerAdapter.ItemClickListener?,onClickListener: View.OnClickListener?) {
            containerView?.setOnClickListener(onClickListener)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                containerView?.transitionName = TRANSITION_CARD + adapterPosition
            }

            bmb1.clearBuilders()

            for (i in 0 until bmb1.getPiecePlaceEnum().pieceNumber()) {
                bmb1.addBuilder(Utility.getTextInsideCircleButtonBuilder())
            }
            bmb1.setOnClickListener({ v -> bmb1.boom() })

            val name = "Kunjan Shah"
            /*tv_name = itemView.findViewById<TextView>(R.id.tv_name)
            iconText = itemView.findViewById<TextView>(R.id.icon_text)
            iconBack = itemView.findViewById<RelativeLayout>(R.id.icon_back)
            iconFront = itemView.findViewById<RelativeLayout>(R.id.icon_front)
            imgProfile = itemView.findViewById<ImageView>(R.id.icon_profile)
            messageContainer = itemView.findViewById<LinearLayout>(R.id.message_container)
            iconContainer = itemView.findViewById<RelativeLayout>(R.id.icon_container)*/
            itemView.setOnLongClickListener(this)

            /*tv_title.text = data.name
            tv_amount.text = data.amount
            tv_date.text = data.date
            tv_status.text = data.status.code
            img_status.setImageResource(data.status.iconId)
            img_card.setImageResource(data.imageId)*/
        }
    }

    class DetailsViewHolder(parent: ViewGroup) : BaseViewHolder<DataProvider.DataProvider1.Details>(parent.inflate(R.layout.item_details_search)) {
        override fun onLongClick(v: View?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun bind(data: DataProvider.DataProvider1.Details, itemClickListener: RecyclerAdapter.ItemClickListener?,onClickListener: View.OnClickListener?) {

            itemView.setOnClickListener({
                itemClickListener!!.itemClick(data.id)
            })

            boomMenuButton.clearBuilders()

            for (i in 0 until boomMenuButton.getPiecePlaceEnum().pieceNumber()) {
                boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
            }
            boomMenuButton.setOnClickListener({ v -> boomMenuButton.boom() })

           /* tv_details_title.text = data.title
            tv_details_subtitle.text = data.subtitle
            tv_details_amount.text = data.amount*/
        }
    }
}


