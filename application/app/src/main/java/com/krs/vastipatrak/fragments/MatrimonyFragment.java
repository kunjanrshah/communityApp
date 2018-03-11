package com.krs.vastipatrak.fragments;

import android.os.Bundle;
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

import io.realm.Realm;
import io.realm.RealmResults;

;

/**
 * Created by Kunjan on 09-03-2018.
 */

public class MatrimonyFragment extends Fragment {

    ExpandableListView lvMatrimonyList;
    ExpandableMarimonyListAdapter mExpandableMatrimonyListAdapter = null;
    ArrayList<ListMatrimonyParentData> listDataHeader = null;
    HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> listDataChild = null;
    Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matrimony, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_matrimony);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        getChildRecords();

        mExpandableMatrimonyListAdapter = new ExpandableMarimonyListAdapter(getActivity(), listDataHeader, listDataChild);
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
                lpd.setHome_lat(home_lat);
                lpd.setHome_lng(home_lng);
                lpd.setUpdated_time(updated);

                listDataHeader.add(lpd);
                ListMatrimonyChildData lcd = new ListMatrimonyChildData();
                lcd.setProfile_id(data.getProfile_id());
                lcd.setChild_address(address);
                lcd.setChild_birth_date(data.getChild_bday());
                lcd.setChild_birth_time(data.getChild_btime());
                lcd.setChild_birth_place(data.getChild_bplace());
                lcd.setChild_blood_group("");
                lcd.setChild_mobile("");
                lcd.setChild_gotra(gotra);
                ArrayList<ListMatrimonyChildData> mlstChildData = new ArrayList<ListMatrimonyChildData>();
                mlstChildData.add(lcd);
                listDataChild.put(lpd, mlstChildData);
            }
        }
    }
}
