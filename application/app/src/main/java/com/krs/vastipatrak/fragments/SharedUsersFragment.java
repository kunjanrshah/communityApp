package com.krs.vastipatrak.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

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
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.showProgressDialog;

public class SharedUsersFragment extends Fragment {

    private String TAG = "";
    private SharedPreferences mSharedPreferences;
    private ExpandableListView lvCustomList;
    private TextView txtLable;

    @Nullable
    private ArrayList<ListParentData> listDataHeader = null;
    @Nullable
    private HashMap<ListParentData, List<ListChildData>> listDataChild = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        lvCustomList = rootView.findViewById(R.id.lvCustomList);
        txtLable = rootView.findViewById(R.id.txtLable);
    }

    private void setAdapter(JSONArray mJsonarr) throws Exception {
        for (int j = 0; j < mJsonarr.length(); j++) {
            JSONObject mjsondata = mJsonarr.getJSONObject(j);
            String profile_id = mjsondata.getString(Common.Constant_Class.ID);
            String email = mjsondata.getString(Common.Constant_Class.EMAIL_ADDRESS);
            String profile_pic_url = mjsondata.getString(Common.Constant_Class.PROFILE_PIC_URL);
            String first_name = mjsondata.getString(Common.Constant_Class.FIRST_NAME);
            String last_name = mjsondata.getString(Common.Constant_Class.LAST_NAME);
            String father_name = mjsondata.getString(Common.Constant_Class.FATHER_NAME);
            String mother_name = mjsondata.getString(Common.Constant_Class.MOTHER_NAME);
            String status = mjsondata.getString(Common.Constant_Class.STATUS);
            String city = mjsondata.getString(Common.Constant_Class.CITY);
            String mobile = mjsondata.getString(Common.Constant_Class.MOBILE);
            String updated_time = mjsondata.getString(Common.Constant_Class.UPDATED_TIME);
            String is_location_enable = mjsondata.getString(Common.Constant_Class.IS_LOCATION_ENABLE);
            String user_lat = mjsondata.getString(Common.Constant_Class.USER_LAT);
            String user_lng = mjsondata.getString(Common.Constant_Class.USER_LNG);
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

            String native_place = mjsondata.getString(Common.Constant_Class.NATIVE_PLACE);
            String address = mjsondata.getString(Common.Constant_Class.ADDRESS);
            String birth_date = mjsondata.getString(Common.Constant_Class.BIRTH_DATE);
            String birth_time = mjsondata.getString(Common.Constant_Class.BIRTH_TIME);
            String birth_place = mjsondata.getString(Common.Constant_Class.BIRTH_PLACE);
            String blood_group = mjsondata.getString(Common.Constant_Class.BLOOD_GROUP);
            String is_share = "0";
            if (mjsondata.has(Common.Constant_Class.IS_SHARE)) {
                is_share = mjsondata.getString(Common.Constant_Class.IS_SHARE);
            }
            lpd.setIs_share(is_share);
            String phone = mjsondata.getString(Common.Constant_Class.PHONE);
            String gender = mjsondata.getString(Common.Constant_Class.GENDER);
            String gotra = mjsondata.getString(Common.Constant_Class.GOTRA);

            ListChildData lcd = new ListChildData();
            lcd.setID(profile_id);
            lcd.setNative(native_place);
            lcd.setAddress(address);
            lcd.setbirth_date(birth_date);
            lcd.setbirth_time(birth_time);
            lcd.setBirth_place(birth_place);
            lcd.setBlood_Group(blood_group);
            lcd.setMobile(mobile);
            lcd.setMother_name(mother_name);
            lcd.setPhone(phone);
            lcd.setGender(gender);
            lcd.setGotra(gotra);
            lcd.setName(first_name + " " + last_name);
            lcd.setCan_share("1");
            ArrayList<ListChildData> mlstChildData = new ArrayList<>();
            mlstChildData.add(lcd);
            listDataHeader.add(lpd);
            listDataChild.put(lpd, mlstChildData);
        }
    }

    private void SharedUsers() {
        if (Common.isOnline(getActivity())) {
            Common.showProgressDialog(getActivity());

            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SHARED_USERS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        listDataHeader.clear();
                        listDataChild.clear();
                        JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                        JSONObject mJsondata = mJsonArray.getJSONObject(0);
                        JSONArray mJsonarr1 = mJsondata.getJSONArray("sharedUsers");
                        JSONArray mJsonarr2 = mJsondata.getJSONArray("sharedFromUsers");
                        setAdapter(mJsonarr1);
                        setAdapter(mJsonarr2);
                        hideProgressDialog();
                        if (listDataHeader.size() > 0) {
                            ExpandableListAdapter mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
                            lvCustomList.setAdapter(mExpandableListAdapter);
                            lvCustomList.setVisibility(View.VISIBLE);
                            txtLable.setVisibility(View.GONE);
                        } else {
                            lvCustomList.setVisibility(View.GONE);
                            txtLable.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Common.hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Common.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }
}
