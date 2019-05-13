package com.krs.vastipatrak.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.SmartSearchAdapter;

public class SmartSearchFragment extends Fragment {
    private ExpandableListView expandableListView;
    int previousGroup = -1;
    private SmartSearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_smart_search, container, false);
        expandableListView = rootView.findViewById(R.id.lst_expandable);
        expandableListView.setGroupIndicator(null);
        adapter = new SmartSearchAdapter(getActivity());
        expandableListView.setAdapter(adapter);
        setListener();

        return rootView;
    }

    // Setting different listeners to expandablelistview
    void setListener() {

        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener((listview, view, group_pos, id) -> {

            if (group_pos == 0) {
                return true;
            }  else {
                adapter.storeFieldsValues();
                return false;
            }
        });

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
        // Default position
        expandableListView.setOnGroupExpandListener(groupPosition -> {
            adapter.previousGroup=groupPosition;
            if (groupPosition != previousGroup)

            // Collapse the expanded group
            expandableListView.collapseGroup(previousGroup);
            previousGroup = groupPosition;
        });

        // This listener will show toast on child click
        expandableListView.setOnChildClickListener((listview, view, groupPos, childPos, id) -> {
            Toast.makeText(getActivity(), "You clicked : " + adapter.getChild(groupPos, childPos), Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}
