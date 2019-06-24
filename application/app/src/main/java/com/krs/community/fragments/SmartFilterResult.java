package com.krs.community.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.adapter.FilterResultAdapter;
import com.krs.community.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SmartFilterResult extends Fragment {


    private RecyclerView lstFilter;
    private FilterResultAdapter resultAdapter;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_filter_result, container, false);
        lstFilter=rootView.findViewById(R.id.lstFilter);
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);

        ImageView iv_cancel=rootView.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new SmartFilterFragment());
        });

        setupList();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        mShimmerViewContainer.stopShimmerAnimation();
    }

    private void setupList() {
        lstFilter.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultAdapter = new FilterResultAdapter(getActivity(), createList(20));
        lstFilter.setAdapter(resultAdapter);

        new Handler().postDelayed(() -> {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        },3000);
    }

    private List<String> createList(int n) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            list.add("View " + i);
        }

        return list;
    }

}
