package com.krs.vastipatrak.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONObject;

import java.util.Objects;

public class TodayFragment extends Fragment {

    private SharedPreferences mSharedPreferences;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(R.string.title_relatives);
        setHasOptionsMenu(true);
        MemoryAllocation();
        return rootView;
    }

    private void MemoryAllocation() {
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);

    }

    private void TodayWS()
    {
        if (Common.isOnline(getActivity())) {
            Common.showProgressDialog(getActivity());
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.PASSWORD, input_password.getText());
                mJsonObject.put(Common.Constant_Class.REPEAT_PASSWORD, input_repeat.getText());
                mJsonObject.put("search_str", search.toLowerCase().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }


            final String password_url = Common.Constant_Class.CHANGE_PASSWORD_URL;

        }
    }
}
