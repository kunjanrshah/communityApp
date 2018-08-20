package com.krs.vastipatrak.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
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

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private final static int TYPE_IMAGE = 1, TYPE_YOUTUBE = 2;
    private final Context context;
    private final ArrayList<String> listUrls;
    private String YoutubeUrl = "";

    public EventListAdapter(Context context, ListEventData data) {
        this.context = context;
        listUrls = new ArrayList<>();
        listUrls.addAll(data.getImages());
        listUrls.addAll(data.getYoutubeUrl());
    }

    public String getYoutubeUrl() {
        return YoutubeUrl;
    }

    private void setYoutubeUrl(String youtubeUrl) {
        YoutubeUrl = youtubeUrl;
    }

    @Override
    public int getItemViewType(int position) {
        if (listUrls.get(position).contains("youtube")) {
            return TYPE_YOUTUBE;
        } else {
            return TYPE_IMAGE;
        }
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View cardView;
        assert inflater != null;
        if (viewType == 1) {
            cardView = inflater.inflate(R.layout.item_child, null, false);
        } else {
            cardView = inflater.inflate(R.layout.video_item_child, null, false);
        }

        return new ViewHolder(cardView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();
        if (viewType == 1) {
            Glide.with(context).load(listUrls.get(position)).apply(new RequestOptions().override(1200, 1000).placeholder(R.drawable.user_profile).error(R.drawable.user_profile)).into(holder.eventImage);
        } else {
            setYoutubeUrl(listUrls.get(position));
            ViewHolder.youTubeView.initialize(Common.Constant_Class.YOUTUBE_API_KEY, (YouTubePlayer.OnInitializedListener) context);
        }
    }

    @Override
    public int getItemCount() {
        return listUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("StaticFieldLeak")
        public static YouTubePlayerView youTubeView;
        ImageView eventImage;

        ViewHolder(@NonNull View itemView, int ViewType) {
            super(itemView);
            if (ViewType == 1) {
                eventImage = itemView.findViewById(R.id.image_event);
            } else {
                youTubeView = itemView.findViewById(R.id.youtube_view);
            }
        }
    }
}
