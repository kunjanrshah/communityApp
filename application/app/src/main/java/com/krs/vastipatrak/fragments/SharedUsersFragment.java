package com.krs.vastipatrak.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.ExpandableListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;
import static com.krs.vastipatrak.utils.Utility.showProgressDialog;

public class SharedUsersFragment extends Fragment {

    private String TAG = "";
    private SharedPreferences mSharedPreferences;
    private ExpandableListView lvSharedUsers, lvSharedFromUsers;
    private TextView txt_sharedUsers, txt_sharedFromUsers;

    @Nullable
    private ArrayList<ListParentData> listDataHeader = null;
    @Nullable
    private HashMap<ListParentData, List<ListChildData>> listDataChild = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_topback);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TAG = SharedUsersFragment.class.getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shared_users, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(R.string.title_shared_users);
        MemoryAllocation(rootView);
        SharedUsers();
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        mSharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        txt_sharedUsers = rootView.findViewById(R.id.txt_sharedUsers);
        txt_sharedFromUsers = rootView.findViewById(R.id.txt_sharedFromUsers);
        lvSharedUsers = rootView.findViewById(R.id.lvSharedUsers);
        lvSharedFromUsers = rootView.findViewById(R.id.lvSharedFromUsers);
    }

    private void setAdapter(JSONArray mJsonarr, String shared) throws Exception {
        for (int j = 0; j < mJsonarr.length(); j++) {
            JSONObject mjsondata = mJsonarr.getJSONObject(j);
            String profile_id = mjsondata.getString(AppConstants.ID);
            String email = mjsondata.getString(AppConstants.EMAIL_ADDRESS);
            String profile_pic_url = mjsondata.getString(AppConstants.PROFILE_PIC_URL);
            String first_name = mjsondata.getString(AppConstants.FIRST_NAME);
            String last_name = mjsondata.getString(AppConstants.LAST_NAME);
            String father_name = mjsondata.getString(AppConstants.FATHER_NAME);
            String mother_name = mjsondata.getString(AppConstants.MOTHER_NAME);
            String status = mjsondata.getString(AppConstants.STATUS);
            String city = mjsondata.getString(AppConstants.CITY);
            String mobile = mjsondata.getString(AppConstants.MOBILE);
            String updated_time = mjsondata.getString(AppConstants.UPDATED_TIME);
            String is_location_enable = mjsondata.getString(AppConstants.IS_LOCATION_ENABLE);
            String user_lat = mjsondata.getString(AppConstants.USER_LAT);
            String user_lng = mjsondata.getString(AppConstants.USER_LNG);
            ListParentData lpd = new ListParentData();
            lpd.setName(first_name + " " + last_name);
            lpd.setFatherName(father_name);
            lpd.setMotherName(mother_name);
            lpd.setMobile(mobile);
            lpd.setProfilePicUrl(profile_pic_url);
            lpd.setStatus(status);
            lpd.setId(profile_id);
            lpd.setCity(city);
            lpd.setMail(email);
            lpd.setUpdated_time(updated_time);
            lpd.setIs_location_enable(is_location_enable);
            lpd.setUser_lat(user_lat);
            lpd.setUser_lng(user_lng);
            lpd.setShared(shared);
            String native_place = mjsondata.getString(AppConstants.NATIVE_PLACE);
            String address = mjsondata.getString(AppConstants.ADDRESS);
            String birth_date = mjsondata.getString(AppConstants.BIRTH_DATE);
            String blood_group = mjsondata.getString(AppConstants.BLOOD_GROUP);
            String is_share = "0";
            if (mjsondata.has(AppConstants.IS_SHARE)) {
                is_share = mjsondata.getString(AppConstants.IS_SHARE);
            }
            if (shared.equalsIgnoreCase("from")) {
                lpd.setIs_share("1");
            } else {
                lpd.setIs_share(is_share);
            }
            String phone = mjsondata.getString(AppConstants.PHONE);
            String gender = mjsondata.getString(AppConstants.GENDER);
            String gotra = mjsondata.getString(AppConstants.GOTRA);

            ListChildData lcd = new ListChildData();
            lcd.setID(profile_id);
            lcd.setNative(native_place);
            lcd.setAddress(address);
            lcd.setbirth_date(birth_date);
            lcd.setBlood_Group(blood_group);
            lcd.setMobile(mobile);
            lcd.setMother_name(mother_name);
            lcd.setPhone(phone);
            lcd.setGender(gender);
            lcd.setGotra(gotra);
            lcd.setShared(shared);
            lcd.setName(first_name + " " + last_name);
            lcd.setCan_share("1");
            ArrayList<ListChildData> mlstChildData = new ArrayList<>();
            mlstChildData.add(lcd);
            listDataHeader.add(lpd);
            listDataChild.put(lpd, mlstChildData);
        }
    }


    private void setDataAdapter(JSONArray mJsonarr, ExpandableListView listView, String shared) throws Exception {

        setAdapter(mJsonarr, shared);
        if (listDataHeader.size() > 0) {
            //if (listView == lvSharedUsers) {
            ExpandableListAdapter mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, true);
            listView.setAdapter(mExpandableListAdapter);
            // txt_sharedUsers.setVisibility(View.VISIBLE);
            //  txt_sharedUsers.setText("You have Shared Your Location");
            /*} else {
                ExpandableListAdapter mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, "2");
                listView.setAdapter(mExpandableListAdapter);
                txt_sharedFromUsers.setVisibility(View.VISIBLE);
                txt_sharedFromUsers.setText("Users have shared their location");
            }*/
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.GONE);
            if (listView == lvSharedUsers) {
                txt_sharedUsers.setVisibility(View.GONE);
            } else {
                txt_sharedFromUsers.setVisibility(View.GONE);
            }
        }
    }

    private void SharedUsers() {
        if (Utility.isOnline(getActivity())) {
            Utility.showProgressDialog(getActivity());

            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.SHARED_USERS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);
                        JSONObject mJsondata = mJsonArray.getJSONObject(0);
                        JSONArray mJsonarr1 = mJsondata.getJSONArray("sharedUsers");
                        JSONArray mJsonarr2 = mJsondata.getJSONArray("sharedFromUsers");
                        listDataHeader.clear();
                        listDataChild.clear();
                        setDataAdapter(mJsonarr1, lvSharedUsers, "to");
                        setDataAdapter(mJsonarr2, lvSharedUsers, "from");
                        hideProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Utility.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }
}
