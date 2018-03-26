package com.krs.vastipatrak.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.EventListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.utils.Common;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by kunjan on 23/3/18.
 */

public class EventlistActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private RecyclerView listEvents;
    private Realm realm;
    private String TAG = "EventlistActivity";
    private int position = 0;
    private RealmResults<ListEventData> eventData;
    private ListEventData data;
    private EventListAdapter adapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fragment_eventlist);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            position = mBundle.getInt("position");
        }

        MemoryAllocation();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listEvents.setLayoutManager(mLayoutManager);
        listEvents.setItemAnimator(new DefaultItemAnimator());
        listEvents.setAdapter(adapter);
    }

    private void MemoryAllocation() {
        listEvents = findViewById(R.id.listEvents);
        realm = AppController.getInstance().realm;
        eventData = realm.where(ListEventData.class).findAll();
        data = eventData.get(position);
        adapter = new EventListAdapter(this, data);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        String url = adapter.getYoutubeUrl();
        int i = url.indexOf("v=");
        url = url.substring(i + 2);
        youTubePlayer.cueVideo(url);//fhWaJi1Hsfo
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            EventListAdapter.ViewHolder.youTubeView.initialize(Common.Constant_Class.YOUTUBE_API_KEY, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
