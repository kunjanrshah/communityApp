package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.Utility;

import static com.krs.community.utils.Utility.changeStatusbarColor;

public class ChangeLanguageFragment extends Fragment {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_lan, container, false);

        ImageView iv_lan_cancel = root.findViewById(R.id.iv_lan_cancel);
        iv_lan_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        changeStatusbarColor(getActivity(), R.color.colorBG, false);

        LinearLayout ll_english, ll_gujarati, ll_hindi;
        ll_english = root.findViewById(R.id.ll_english);
        ll_gujarati = root.findViewById(R.id.ll_gujarati);
        ll_hindi = root.findViewById(R.id.ll_hindi);

        AppCompatRadioButton rb_hindi, rb_gujarati, rb_english;
        rb_hindi = root.findViewById(R.id.rb_hindi);
        rb_gujarati = root.findViewById(R.id.rb_gujarati);
        rb_english = root.findViewById(R.id.rb_english);

        ll_hindi.setOnClickListener(v -> {
            if (!rb_hindi.isChecked()) {
                rb_hindi.setChecked(true);
                rb_gujarati.setChecked(false);
                rb_english.setChecked(false);
            }
        });

        ll_gujarati.setOnClickListener(v -> {
            if (!rb_gujarati.isChecked()) {
                rb_hindi.setChecked(false);
                rb_gujarati.setChecked(true);
                rb_english.setChecked(false);
            }
        });

        ll_english.setOnClickListener(v -> {
            if (!rb_english.isChecked()) {
                rb_hindi.setChecked(false);
                rb_gujarati.setChecked(false);
                rb_english.setChecked(true);
            }
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
