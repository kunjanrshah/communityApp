package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.github.squti.guru.Guru;
import com.google.gson.Gson;
import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.model.Member;
import com.krs.community.utils.Utility;

import org.json.JSONObject;

public class ByQRCodeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_by_qrcode,container,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
        }

        String loginMember=Guru.getString(getString(R.string.loginUser),"");
        Member member= new Gson().fromJson(loginMember, Member.class);

        ImageView iv_cancel=root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });



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
