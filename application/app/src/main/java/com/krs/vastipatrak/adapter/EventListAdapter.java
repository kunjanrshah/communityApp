package com.krs.vastipatrak.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.utils.Common;

import java.util.ArrayList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private final static int TYPE_IMAGE = 1, TYPE_YOUTUBE = 2;
    private final Context context;
    private final ArrayList<String> listUrls;

    public EventListAdapter(Context context, ListEventData data) {
        this.context = context;
        listUrls = new ArrayList<>();
        listUrls.addAll(data.getImages());
        listUrls.addAll(data.getYoutubeUrl());
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
            cardView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        } else {
            cardView = inflater.inflate(R.layout.video_child_view, null, false);
            cardView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        }
        return new ViewHolder(cardView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        int viewType = holder.getItemViewType();
        if (viewType == 1) {
            Drawable dr = context.getResources().getDrawable(R.drawable.user_profile);
            Bitmap bitmap = getBitmap(dr);
            Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 500, 500, true));
            Glide.with(context).load(listUrls.get(position)).apply(new RequestOptions().placeholder(d).error(d)).into(holder.eventImage);
        } else {

            final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }

                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                }
            };


            holder.youTubeThumbnailView.initialize(Common.Constant_Class.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                    String url = listUrls.get(position);
                    int i = url.indexOf("v=");
                    url = url.substring(i + 2);
                    youTubeThumbnailLoader.setVideo(url);
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    //write something for failure
                }
            });

        }
    }


    private Bitmap getBitmap(Drawable drawable) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return listUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        YouTubeThumbnailView youTubeThumbnailView;
        ImageView playButton;
        ImageView eventImage;

        ViewHolder(@NonNull View itemView, int ViewType) {
            super(itemView);
            if (ViewType == 1) {
                eventImage = itemView.findViewById(R.id.image_event);
            } else {
                playButton = (ImageView) itemView.findViewById(R.id.btnYoutube_player);
                playButton.setOnClickListener(this);
                youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
            }
        }

        @Override
        public void onClick(View v) {

            String url = listUrls.get(getLayoutPosition());
            int i = url.indexOf("v=");
            url = url.substring(i + 2);
            Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, Common.Constant_Class.YOUTUBE_API_KEY, url);
            context.startActivity(intent);
        }
    }
}
