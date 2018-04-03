package com.krs.vastipatrak.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.ExpandableMarimonyListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListMatrimonyChildData;
import com.krs.vastipatrak.model.ListMatrimonyParentData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class MatrimonyFragment extends Fragment {

    private ExpandableListView lvMatrimonyList;
    @Nullable
    private ArrayList<ListMatrimonyParentData> listDataHeader = null;
    @Nullable
    private HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> listDataChild = null;
    private Realm realm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matrimony, container, false);
        assert getActivity() != null;
        Activity mActivity = getActivity();
        Objects.requireNonNull(((AppCompatActivity) mActivity).getSupportActionBar()).setSubtitle(R.string.title_matrimony);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        getChildRecords();

        ExpandableMarimonyListAdapter mExpandableMatrimonyListAdapter = new ExpandableMarimonyListAdapter(getActivity(), listDataHeader, listDataChild);
        lvMatrimonyList.setAdapter(mExpandableMatrimonyListAdapter);
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        lvMatrimonyList = rootView.findViewById(R.id.lvMatrimonyList);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        realm = AppController.getInstance().realm;
    }

    private void getChildRecords() {
        RealmResults<ListChildrenData> childData = realm.where(ListChildrenData.class).equalTo(Common.Constant_Class.IS_INTERESTED, true).findAll();
        if (childData != null && childData.size() > 0) {
            for (ListChildrenData data : childData) {
                ListMatrimonyParentData lpd = new ListMatrimonyParentData();
                lpd.setId(data.getChild_id());
                lpd.setProfile_id(data.getProfile_id());
                lpd.setProfilePicUrl(data.getChild_img_url());
                lpd.setName(data.getChild_name());
                lpd.setChild_gender(data.getGender());
                ListProfileData parentData = realm.where(ListProfileData.class).equalTo(Common.Constant_Class.PROFILE_ID, data.getProfile_id()).findFirst();
                String father = "", mother = "", city = "", home_lat = "", home_lng = "",address="",gotra="",updated="";
                if (parentData != null) {
                    father = parentData.getFirst_name() + "" + parentData.getLast_name();
                    mother = parentData.getSpouse_name();
                    city = parentData.getCity();
                    home_lat = parentData.getHome_lat();
                    home_lng = parentData.getHome_lng();
                    address=parentData.getAddress();
                    gotra=parentData.getGotra();
                    updated=parentData.getUpdated_time();
                }
                lpd.setFatherName(father);
                lpd.setMotherName(mother);
                lpd.setCity(city);
                lpd.setUpdated_time(updated);

                Objects.requireNonNull(listDataHeader).add(lpd);
                ListMatrimonyChildData lcd = new ListMatrimonyChildData();
                lcd.setProfile_id(data.getProfile_id());
                lcd.setChild_address(address);
                lcd.setChild_birth_date(data.getChild_bday());
                lcd.setChild_birth_time(data.getBirth_time());
                lcd.setChild_birth_place(data.getBirth_place());
                lcd.setChild_blood_group(data.getBlood_group());
                lcd.setChild_mobile(data.getMobile());
                lcd.setChild_gotra(gotra);
                lcd.setHome_lat(home_lat);
                lcd.setHome_lng(home_lng);
                lcd.setName(data.getChild_name());
                ArrayList<ListMatrimonyChildData> mlstChildData = new ArrayList<>();
                mlstChildData.add(lcd);
                Objects.requireNonNull(listDataChild).put(lpd, mlstChildData);
            }
        }
    }

}
