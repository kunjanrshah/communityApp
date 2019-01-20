package com.yadav.vastipatrak.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yadav.vastipatrak.R;
import com.yadav.vastipatrak.adapter.VideoListAdapter;

public class TourFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tour, container, false);
        MemoryAllocation(rootView);
        return rootView;
    }


    private void MemoryAllocation(View root) {

        RecyclerView recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        VideoListAdapter adapter = new VideoListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }

}
