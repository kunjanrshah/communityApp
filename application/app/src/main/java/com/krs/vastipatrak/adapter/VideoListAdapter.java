package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.Common;

import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> listUrls;
    private final ArrayList<String> listTitles;
    private String YoutubeUrl = "";

    public VideoListAdapter(Context context) {
        this.context = context;
        listUrls = new ArrayList<>();
        listTitles = new ArrayList<>();
        setUrls();
        setTitles();
    }

    private void setTitles() {
        listTitles.add(context.getResources().getString(R.string.how_to_download_1));
        listTitles.add(context.getResources().getString(R.string.how_to_login_2));
        listTitles.add(context.getResources().getString(R.string.how_to_forgot_3));
        listTitles.add(context.getResources().getString(R.string.how_to_event_list_4));
        listTitles.add(context.getResources().getString(R.string.how_to_update_profile_5));
        listTitles.add(context.getResources().getString(R.string.how_ror_6));
        listTitles.add(context.getResources().getString(R.string.how_search_profile_7));
        listTitles.add(context.getResources().getString(R.string.how_share_loc_8));
        listTitles.add(context.getResources().getString(R.string.how_near_by_9));
        listTitles.add(context.getResources().getString(R.string.how_matrimony_10));
        listTitles.add(context.getResources().getString(R.string.how_export_search_11));
        listTitles.add(context.getResources().getString(R.string.how_share_code_12));
        listTitles.add(context.getResources().getString(R.string.how_calendar_13));
        listTitles.add(context.getResources().getString(R.string.how_reminder_14));
        listTitles.add(context.getResources().getString(R.string.how_event_manage_15));
        listTitles.add(context.getResources().getString(R.string.how_admin_manage_16));
    }

    private void setUrls() {
        listUrls.add(context.getResources().getString(R.string.download_1));
        listUrls.add(context.getResources().getString(R.string.login_2));
        listUrls.add(context.getResources().getString(R.string.forgot_3));
        listUrls.add(context.getResources().getString(R.string.event_list_4));
        listUrls.add(context.getResources().getString(R.string.update_profile_5));
        listUrls.add(context.getResources().getString(R.string.ror_6));
        listUrls.add(context.getResources().getString(R.string.search_profile_7));
        listUrls.add(context.getResources().getString(R.string.share_loc_8));
        listUrls.add(context.getResources().getString(R.string.near_by_9));
        listUrls.add(context.getResources().getString(R.string.matrimony_10));
        listUrls.add(context.getResources().getString(R.string.export_search_11));
        listUrls.add(context.getResources().getString(R.string.share_code_12));
        listUrls.add(context.getResources().getString(R.string.calendar_13));
        listUrls.add(context.getResources().getString(R.string.reminder_14));
        listUrls.add(context.getResources().getString(R.string.event_manage_15));
        listUrls.add(context.getResources().getString(R.string.admin_manage_16));
    }

    public String getYoutubeUrl() {
        return YoutubeUrl;
    }

    private void setYoutubeUrl(String youtubeUrl) {
        YoutubeUrl = youtubeUrl;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView;
        cardView = inflater.inflate(R.layout.video_item_child, null, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        setYoutubeUrl(listUrls.get(position));
        holder.txt_title.setVisibility(View.VISIBLE);
        holder.txt_title.setText(listTitles.get(position));
        holder.youTubeView1.initialize(Common.Constant_Class.YOUTUBE_API_KEY, (YouTubePlayer.OnInitializedListener) context);

    }

    @Override
    public int getItemCount() {
        return listUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public static YouTubePlayerView youTubeView1;
        public TextView txt_title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubeView1 = itemView.findViewById(R.id.youtube_view);
            txt_title = itemView.findViewById(R.id.txt_title);
        }
    }
}
