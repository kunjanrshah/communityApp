package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.utils.Common;

import java.util.ArrayList;


/**
 * Created by root on 2/3/16.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private final static int TYPE_IMAGE = 1, TYPE_YOUTUBE = 2;
    private Context context;
    private ArrayList<String> listUrls;


    public EventListAdapter(Context context, ListEventData data) {
        this.context = context;
        listUrls = new ArrayList<>();
        for (int i = 0; i < data.getImages().size(); i++) {
            listUrls.add(data.getImages().get(i));
        }
        for (int i = 0; i < data.getYoutubeUrl().size(); i++) {
            listUrls.add(data.getYoutubeUrl().get(i));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (listUrls.get(position).contains("youtube")) {
            return TYPE_YOUTUBE;
        } else {
            return TYPE_IMAGE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View cardView;
        if (viewType == 1) {
            cardView = inflater.inflate(R.layout.item_child, null, false);
        } else {
            cardView = inflater.inflate(R.layout.video_item_child, null, false);
        }

        return new ViewHolder(cardView, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();
        if (viewType == 1) {
            Glide.with(context).load(listUrls.get(position)).apply(new RequestOptions().override(1200, 1000).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher)).into(holder.eventImage);
            //Glide.with(context).load(listUrls.get(position)).thumbnail(1f).into(holder.eventImage);
        } else {
            ViewHolder.youTubeView.initialize(Common.Constant_Class.YOUTUBE_API_KEY, (YouTubePlayer.OnInitializedListener) context);
        }
    }

    @Override
    public int getItemCount() {
        return listUrls.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public static YouTubePlayerView youTubeView;
        ImageView eventImage;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == 1) {
                eventImage = itemView.findViewById(R.id.image_event);
            } else {
                youTubeView = itemView.findViewById(R.id.youtube_view);
            }
        }
    }
}
