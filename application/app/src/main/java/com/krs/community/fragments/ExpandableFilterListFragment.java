package com.krs.community.fragments;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.adapter.SmartFilterAdapter;
import com.krs.community.utils.Utility;


public class ExpandableFilterListFragment extends Fragment {
    private ExpandableListView expandableListView;
    private int previousGroup = -1;
    private SmartFilterAdapter adapter;
    private String TAG = ExpandableFilterListFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_smart_search, container, false);
        expandableListView = rootView.findViewById(R.id.lst_expandable);
        expandableListView.setGroupIndicator(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.white,false);
        }

        adapter = new SmartFilterAdapter(getActivity());
        expandableListView.setAdapter(adapter);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Smart Filter");

        setListener();

        ImageView iv_cancel=rootView.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });

        ImageView iv_filter=rootView.findViewById(R.id.iv_filter);
        iv_filter.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new FiltersFragment());
        });

        TextView tv_done=rootView.findViewById(R.id.tv_done);
        tv_done.setOnClickListener(v -> {
            Utility.hideKeyboard(getActivity());

            new Handler().postDelayed(() -> adapter.openBottomSheetDailog(),250);
        });

        expandableListView.setOnScrollListener(new OnScrollObserver() {
            @Override
            public void onScrollUp() {
                Log.d(TAG, "onScrollUp");
                DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrollDown() {
                Log.d(TAG, "onScrollDown");
                DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utility.hideKeyboard(getActivity());
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    // Setting different listeners to expandablelistview
    void setListener() {

        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener((listview, view, group_pos, id) -> {

            if (group_pos == 0 || group_pos==7) {
                return true;
            } else {
                adapter.storeFieldsValues();
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(groupPosition -> {
            adapter.previousGroup = groupPosition;
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

    public abstract class OnScrollObserver implements AbsListView.OnScrollListener {

        int last = 0;
        boolean control = true;

        public abstract void onScrollUp();

        public abstract void onScrollDown();

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int current, int visibles, int total) {
            if (current < last && !control) {
                onScrollUp();
                control = true;
            } else if (current > last && control) {
                onScrollDown();
                control = false;
            }

            last = current;
        }
    }
}
