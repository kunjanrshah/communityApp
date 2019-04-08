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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.krs.vastipatrak.utils.AppConstants.LOCATION_TYPE;
import static com.krs.vastipatrak.utils.Utility.hideKeyboard;
import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;
import static com.krs.vastipatrak.utils.Utility.showProgressDialog;

public class NearByFragment extends Fragment {

    //String lat = "", lng = "";
    private String TAG = "";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ExpandableListView lvCustomList;
    private TextView txtLable;
    private RadioButton rdb_home, rdb_office, rdb_user;
    private String type = "";
    private EditText edt_distance;
    private Button btnok;
    @Nullable
    private ArrayList<ListParentData> listDataHeader = null;
    @Nullable
    private HashMap<ListParentData, List<ListChildData>> listDataChild = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = NearByFragment.class.getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_near_by, container, false);
        type=getString(R.string.near_by_home);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_near_by_users);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_topback);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MemoryAllocation(rootView);
        callNearby();
        return rootView;
    }

    private void setType() {
        type = mSharedPreferences.getString(LOCATION_TYPE, getString(R.string.near_by_home));
        if (type.isEmpty() || type.equalsIgnoreCase(getString(R.string.near_by_home))) {
            rdb_home.setChecked(true);
            rdb_office.setChecked(false);
            rdb_user.setChecked(false);
        } else if (type.equalsIgnoreCase(getString(R.string.near_by_office))) {
            rdb_home.setChecked(false);
            rdb_office.setChecked(true);
            rdb_user.setChecked(false);
        } else if (type.equalsIgnoreCase(getString(R.string.near_by_users))) {
            rdb_home.setChecked(false);
            rdb_office.setChecked(false);
            rdb_user.setChecked(true);
        }
    }

    private void callNearby() {
        if (!edt_distance.getText().toString().isEmpty()) {
                NearByUsers();
        } else {
            Toast.makeText(getActivity(), "Enter distance", Toast.LENGTH_SHORT).show();
        }
    }

    private void MemoryAllocation(View rootView) {
        mSharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        lvCustomList = rootView.findViewById(R.id.lvCustomList);
        txtLable = rootView.findViewById(R.id.txtLable);
        rdb_home = rootView.findViewById(R.id.rdb_home);
        rdb_office = rootView.findViewById(R.id.rdb_office);
        rdb_user = rootView.findViewById(R.id.rdb_user);
        edt_distance = rootView.findViewById(R.id.edt_distance);
        btnok = rootView.findViewById(R.id.btnok);
        setType();
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setType();
                callNearby();
            }
        });

        rdb_home.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rdb_office.setChecked(false);
                    rdb_user.setChecked(false);
                    type = getString(R.string.near_by_home);
                    mEditor.putString(LOCATION_TYPE, type);
                    mEditor.apply();
                }
            }
        });

        rdb_office.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rdb_home.setChecked(false);
                    rdb_user.setChecked(false);
                    type = getString(R.string.near_by_office);
                    mEditor.putString(LOCATION_TYPE, type);
                    mEditor.apply();
                }
            }
        });

        rdb_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rdb_home.setChecked(false);
                    rdb_office.setChecked(false);
                    type = getString(R.string.near_by_users);
                    mEditor.putString(LOCATION_TYPE, type);
                    mEditor.apply();
                }
            }
        });
    }


    private void NearByUsers() {
        if (Utility.isOnline(getActivity())) {
            Utility.showProgressDialog(getActivity());
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
                mJsonObject.put(AppConstants.NEAR_BY, type);
                mJsonObject.put(AppConstants.LAT, mSharedPreferences.getString(AppConstants.CURR_LAT, ""));
                mJsonObject.put(AppConstants.LNG, mSharedPreferences.getString(AppConstants.CURR_LNG, ""));
                mJsonObject.put(AppConstants.KM, edt_distance.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.NEAR_BY_USERS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        listDataHeader.clear();
                        listDataChild.clear();
                        hideKeyboard(getActivity());
                        if (response.has(AppConstants.DATA)) {
                            JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                String profile_id = mJsondata.getString(AppConstants.ID);
                                String distance = "";
                                if (mJsondata.has(AppConstants.DISTANCE)) {
                                    distance = mJsondata.getString(AppConstants.DISTANCE);
                                }
                                String email = mJsondata.getString(AppConstants.EMAIL_ADDRESS);
                                String profile_pic_url = mJsondata.getString(AppConstants.PROFILE_PIC_URL);
                                String first_name = mJsondata.getString(AppConstants.FIRST_NAME);
                                String last_name = mJsondata.getString(AppConstants.LAST_NAME);
                                String father_name = mJsondata.getString(AppConstants.FATHER_NAME);
                                String mother_name = mJsondata.getString(AppConstants.MOTHER_NAME);
                                String status = mJsondata.getString(AppConstants.STATUS);
                                String city = mJsondata.getString(AppConstants.CITY);
                                String mobile = mJsondata.getString(AppConstants.MOBILE);
                                String updated_time = mJsondata.getString(AppConstants.UPDATED_TIME);
                                String is_location_enable = mJsondata.getString(AppConstants.IS_LOCATION_ENABLE);
                                String user_lat = mJsondata.getString(AppConstants.USER_LAT);
                                String user_lng = mJsondata.getString(AppConstants.USER_LNG);

                                String home_lat = mJsondata.getString(AppConstants.HOME_LAT);
                                String home_lng = mJsondata.getString(AppConstants.HOME_LNG);

                                String office_lat = mJsondata.getString(AppConstants.OFFICE_LAT);
                                String office_lng = mJsondata.getString(AppConstants.OFFICE_LNG);
                                ListParentData lpd = new ListParentData();
                                lpd.setName(first_name + " " + last_name);
                                lpd.setFatherName(father_name);
                                lpd.setMotherName(mother_name);
                                lpd.setDistance(distance);
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
                                lpd.setOffice_lat(office_lat);
                                lpd.setOffice_lng(office_lng);
                                lpd.setHome_lat(home_lat);
                                lpd.setHome_lng(home_lng);
                                lpd.setType(type);
                                String native_place = mJsondata.getString(AppConstants.NATIVE_PLACE);
                                String address = mJsondata.getString(AppConstants.ADDRESS);
                                String birth_date = mJsondata.getString(AppConstants.BIRTH_DATE);
                                String blood_group = mJsondata.getString(AppConstants.BLOOD_GROUP);
                                String is_share = "0";
                                if (mJsondata.has(AppConstants.IS_SHARE)) {
                                    is_share = mJsondata.getString(AppConstants.IS_SHARE);
                                }
                                lpd.setIs_share(is_share);
                                String phone = mJsondata.getString(AppConstants.PHONE);
                                String gender = mJsondata.getString(AppConstants.GENDER);
                                String gotra = mJsondata.getString(AppConstants.GOTRA);

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
                                lcd.setName(first_name + " " + last_name);
                                lcd.setCan_share("1");
                                ArrayList<ListChildData> mlstChildData = new ArrayList<>();
                                mlstChildData.add(lcd);
                                listDataHeader.add(lpd);
                                listDataChild.put(lpd, mlstChildData);
                            }
                        }

                        hideProgressDialog();
                        //if (listDataHeader.size() > 0) {
                        ExpandableListAdapter mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, false);
                        mExpandableListAdapter.setNearby(true);
                        lvCustomList.setAdapter(mExpandableListAdapter);
                        lvCustomList.setVisibility(View.VISIBLE);
                        txtLable.setVisibility(View.GONE);
                       /* } else {
                            lvCustomList.setVisibility(View.GONE);
                            txtLable.setVisibility(View.VISIBLE);
                        }*/
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
