package com.krs.vastipatrak.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class EventlistFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private RecyclerView listEvents;
    private Realm realm;
    private String TAG = "EventlistFragment";
    private int position = 0;
    private RealmResults<ListEventData> eventData;
    private ListEventData data;
    private EventListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_eventlist, container, false);
        MemoryAllocation(rootView);

        position = getArguments().getInt("position");
        eventData = realm.where(ListEventData.class).findAll();
        data = eventData.get(position);

        adapter = new EventListAdapter(getActivity(), data);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listEvents.setLayoutManager(mLayoutManager);
        listEvents.setItemAnimator(new DefaultItemAnimator());
        listEvents.setAdapter(adapter);

        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        listEvents = rootView.findViewById(R.id.listEvents);
        realm = AppController.getInstance().realm;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo("fhWaJi1Hsfo");
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // Retry initialization if user performed a recovery action
            EventListAdapter.ViewHolder.youTubeView.initialize(Common.Constant_Class.YOUTUBE_API_KEY, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
