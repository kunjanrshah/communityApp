package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.utils.Utility;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragmnet_settings, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.colorPrivacyPolictyBG,false);
        }

        ImageView img_cancel=root.findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });

        LinearLayout ll_change_pass=root.findViewById(R.id.ll_change_pass);
        ll_change_pass.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new ChangePasswordFragment());
        });


        LinearLayout ll_privacy=root.findViewById(R.id.ll_privacy);
        ll_privacy.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new PrivacyPolicyFragment());
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}
