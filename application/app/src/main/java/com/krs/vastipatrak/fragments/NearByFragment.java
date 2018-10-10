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
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.Constant_Class.LOCATION_TYPE;
import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.showProgressDialog;

public class NearByFragment extends Fragment {

    //String lat = "", lng = "";
    private String TAG = "";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ExpandableListView lvCustomList;
    private TextView txtLable;
    private RadioButton rdb_home, rdb_office, rdb_user;
    private String type = "Home";
    private EditText edt_distance;
    private Button btnok;
    @Nullable
    private ArrayList<ListParentData> listDataHeader = null;
    @Nullable
    private HashMap<ListParentData, List<ListChildData>> listDataChild = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        TAG = NearByFragment.class.getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_near_by, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(R.string.title_near_by_users);
        MemoryAllocation(rootView);
        callNearby();
        return rootView;
    }

    private void setType() {
        type = mSharedPreferences.getString(LOCATION_TYPE, "Home");
        if (type.isEmpty() || type.equalsIgnoreCase("Home")) {
            rdb_home.setChecked(true);
            rdb_office.setChecked(false);
            rdb_user.setChecked(false);
        } else if (type.equalsIgnoreCase("Office")) {
            rdb_home.setChecked(false);
            rdb_office.setChecked(true);
            rdb_user.setChecked(false);
        } else if (type.equalsIgnoreCase("User")) {
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
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
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
                    type = "Home";
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
                    type = "Office";
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
                    type = "User";
                    mEditor.putString(LOCATION_TYPE, type);
                    mEditor.apply();
                }
            }
        });
    }

    private void NearByUsers() {
        if (Common.isOnline(getActivity())) {
            Common.showProgressDialog(getActivity());
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.NEAR_BY, type);
                mJsonObject.put(Common.Constant_Class.LAT, mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, ""));
                mJsonObject.put(Common.Constant_Class.LNG, mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, ""));
                mJsonObject.put(Common.Constant_Class.KM, edt_distance.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.NEAR_BY_USERS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        listDataHeader.clear();
                        listDataChild.clear();
                        if (response.has(Common.Constant_Class.DATA)) {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                String profile_id = mJsondata.getString(Common.Constant_Class.ID);
                                String distance = "";
                                if (mJsondata.has(Common.Constant_Class.DISTANCE)) {
                                    distance = mJsondata.getString(Common.Constant_Class.DISTANCE);
                                }
                                String email = mJsondata.getString(Common.Constant_Class.EMAIL_ADDRESS);
                                String profile_pic_url = mJsondata.getString(Common.Constant_Class.PROFILE_PIC_URL);
                                String first_name = mJsondata.getString(Common.Constant_Class.FIRST_NAME);
                                String last_name = mJsondata.getString(Common.Constant_Class.LAST_NAME);
                                String father_name = mJsondata.getString(Common.Constant_Class.FATHER_NAME);
                                String mother_name = mJsondata.getString(Common.Constant_Class.MOTHER_NAME);
                                String status = mJsondata.getString(Common.Constant_Class.STATUS);
                                String city = mJsondata.getString(Common.Constant_Class.CITY);
                                String mobile = mJsondata.getString(Common.Constant_Class.MOBILE);
                                String updated_time = mJsondata.getString(Common.Constant_Class.UPDATED_TIME);
                                String is_location_enable = mJsondata.getString(Common.Constant_Class.IS_LOCATION_ENABLE);
                                String user_lat = mJsondata.getString(Common.Constant_Class.USER_LAT);
                                String user_lng = mJsondata.getString(Common.Constant_Class.USER_LNG);

                                String home_lat = mJsondata.getString(Common.Constant_Class.HOME_LAT);
                                String home_lng = mJsondata.getString(Common.Constant_Class.HOME_LNG);

                                String office_lat = mJsondata.getString(Common.Constant_Class.OFFICE_LAT);
                                String office_lng = mJsondata.getString(Common.Constant_Class.OFFICE_LNG);
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
                                String native_place = mJsondata.getString(Common.Constant_Class.NATIVE_PLACE);
                                String address = mJsondata.getString(Common.Constant_Class.ADDRESS);
                                String birth_date = mJsondata.getString(Common.Constant_Class.BIRTH_DATE);
                                String birth_time = mJsondata.getString(Common.Constant_Class.BIRTH_TIME);
                                String birth_place = mJsondata.getString(Common.Constant_Class.BIRTH_PLACE);
                                String blood_group = mJsondata.getString(Common.Constant_Class.BLOOD_GROUP);
                                String is_share = "0";
                                if (mJsondata.has(Common.Constant_Class.IS_SHARE)) {
                                    is_share = mJsondata.getString(Common.Constant_Class.IS_SHARE);
                                }
                                lpd.setIs_share(is_share);
                                String phone = mJsondata.getString(Common.Constant_Class.PHONE);
                                String gender = mJsondata.getString(Common.Constant_Class.GENDER);
                                String gotra = mJsondata.getString(Common.Constant_Class.GOTRA);

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
