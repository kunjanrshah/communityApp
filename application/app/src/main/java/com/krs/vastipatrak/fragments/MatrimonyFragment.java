package com.krs.vastipatrak.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krs.vastipatrak.R;

;

/**
 * Created by Kunjan on 09-03-2018.
 */

public class MatrimonyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matrimony, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_matrimony);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
    }
}
