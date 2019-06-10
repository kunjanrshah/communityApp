package com.krs.community.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.adapter.SmartFilterAdapter;

public class SmartFilterFragment extends Fragment {
    private ExpandableListView expandableListView;
    private int previousGroup = -1;
    private SmartFilterAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_smart_search, container, false);
        expandableListView = rootView.findViewById(R.id.lst_expandable);

        expandableListView.setGroupIndicator(null);
        adapter = new SmartFilterAdapter(getActivity());
        expandableListView.setAdapter(adapter);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Smart Filter");

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
