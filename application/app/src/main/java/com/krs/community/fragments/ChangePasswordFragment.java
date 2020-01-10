package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.Utility;

import static com.krs.community.utils.Utility.changeStatusbarColor;

public class ChangePasswordFragment extends Fragment {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_change_pass,container,false);

        ImageView iv_lan_cancel=root.findViewById(R.id.iv_lan_cancel);
        iv_lan_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new SettingFragment());
        });

        changeStatusbarColor(getActivity(),R.color.colorPrivacyPolictyBG,false);
        return  root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.Companion.getBinding().space.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DashboardActivity.Companion.getBinding().space.setVisibility(View.VISIBLE);
    }

}
