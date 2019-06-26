package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.Utility;

public class StatisticFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_statistics,container,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.bg_gray,false);
        }

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
    }
}
