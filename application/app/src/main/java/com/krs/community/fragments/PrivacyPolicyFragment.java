package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.app.AppController;
import com.krs.community.utils.Utility;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PrivacyPolicyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragmnet_privacy_policy, container, false);

        AppController mApp = (AppController) getApplicationContext();
        mApp.FirebaseAnalytics(getContext(),PrivacyPolicyFragment.class.getSimpleName());
        mApp.FacebookAnalytics(getContext(),PrivacyPolicyFragment.class.getSimpleName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
        }

        ImageView iv_cancel=root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new SettingFragment());
        });


        return root;
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
