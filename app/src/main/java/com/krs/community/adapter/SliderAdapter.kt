package com.krs.community.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.krs.community.R
import com.smarteist.autoimageslider.SliderViewAdapter
import com.bumptech.glide.request.target.Target

class SliderAdapter(private val context: Context, private val imageUrls: List<String>) :
    SliderViewAdapter<SliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, null)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        val imageUrl = imageUrls[position]

        Log.d("image URL", "$imageUrl")

        // Load image using Glide
        Glide.with(context)
            .load(imageUrl)
            .thumbnail(0.5f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(viewHolder.imageView)

    }

    override fun getCount(): Int {
        return imageUrls.size
    }

    class SliderViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}


