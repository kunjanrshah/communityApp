package com.krs.community.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.activity.FamilyTreeDetailActivity;
import com.krs.community.utils.Utility;

public class AddFactsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_facts, container, false);
        ImageView img_cancel = view.findViewById(R.id.img_cancel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.colorPrimary, true);
        }

        img_cancel.setOnClickListener(v -> {
            Intent mIntent = new Intent(getActivity(), FamilyTreeDetailActivity.class);
            startActivity(mIntent);
            getActivity().finish();
            Utility.fade(getActivity());
        });
        return view;
    }
}
