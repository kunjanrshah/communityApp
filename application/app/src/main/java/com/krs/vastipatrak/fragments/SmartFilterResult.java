package com.krs.vastipatrak.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.FilterResultAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SmartFilterResult extends Fragment {


    private RecyclerView lstFilter;
    private FilterResultAdapter resultAdapter;
    private CircleImageView img_edit;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_filter_result, container, false);
        lstFilter=rootView.findViewById(R.id.lstFilter);
        img_edit=rootView.findViewById(R.id.img_edit);
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);
        try {
            img_edit.setImageResource(R.drawable.pencil_edit_button);
        //    Bitmap myLogo = ((BitmapDrawable)getActivity().getResources().getDrawable(R.drawable.pencil_edit_button)).getBitmap();
         //   Glide.with(getActivity()).load(myLogo).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_edit);
        } catch (Exception e) {
            e.getMessage();
        }
        setupList();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    private void setupList() {
        lstFilter.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultAdapter = new FilterResultAdapter(getActivity(), createList(20));
        lstFilter.setAdapter(resultAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
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
