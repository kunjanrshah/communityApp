package com.krs.vastipatrak.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.ExpandableMarimonyListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListMatrimonyChildData;
import com.krs.vastipatrak.model.ListMatrimonyParentData;
import com.krs.vastipatrak.model.MatrimonyProfileData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.showProgressDialog;

public class MatrimonyFragment extends Fragment {

    private final String TAG = MatrimonyFragment.class.getSimpleName();
    ToggleButton tbtn_interest, tbtn_gender;
    Button btnSearch;
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
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildRecords();
            }
        });
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        lvMatrimonyList = rootView.findViewById(R.id.lvMatrimonyList);
        tbtn_interest = rootView.findViewById(R.id.tbtn_interest);
        tbtn_interest.setTextOn(null);
        tbtn_interest.setTextOff(null);
        tbtn_gender = rootView.findViewById(R.id.tbtn_gender);
        tbtn_gender.setTextOff(null);
        tbtn_gender.setTextOn(null);
        btnSearch = rootView.findViewById(R.id.btnSearch);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        realm = AppController.getInstance().realm;


    }

    private void getChildRecords() {
        final String is_interested, gender;
        if (tbtn_interest.isChecked()) {
            is_interested = "1";
        } else {
            is_interested = "0";
        }
        if (tbtn_gender.isChecked()) {
            gender = "male";
        } else {
            gender = "female";
        }
        if (Common.isOnline(getActivity())) {
            JSONObject mjsonObject = new JSONObject();
            try {
                mjsonObject.put(Common.Constant_Class.IS_INTERESTED, is_interested);
                mjsonObject.put(Common.Constant_Class.CHILD_GENDER, gender);
                showProgressDialog(getActivity());
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.ADVANCE_SEARCH_URL, mjsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                            String message = response.getString(Common.Constant_Class.MESSAGE);
                            if (success) {
                                RealmResults<MatrimonyProfileData> profileData = realm.where(MatrimonyProfileData.class).findAll();

                                realm.beginTransaction();
                                profileData.deleteAllFromRealm();
                                realm.delete(MatrimonyProfileData.class);
                                realm.delete(ListChildrenData.class);
                                realm.commitTransaction();

                                JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                    Common.MatrimonyProfile(mJsondata, gender, is_interested);
                                }
                            }
                            getChildRecords1();
                            ExpandableMarimonyListAdapter mExpandableMatrimonyListAdapter = new ExpandableMarimonyListAdapter(getActivity(), listDataHeader, listDataChild);
                            lvMatrimonyList.setAdapter(mExpandableMatrimonyListAdapter);
                            hideProgressDialog();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hideProgressDialog();
                    }
                }) {
                    @NonNull
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                        params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                        return params;
                    }
                };

                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getChildRecords1() {
        RealmResults<MatrimonyProfileData> profileData = realm.where(MatrimonyProfileData.class).findAll();
        if (profileData != null && profileData.size() > 0) {
            for (MatrimonyProfileData data : profileData) {

                for (ListChildrenData childrenData : data.getmListChildrenData()) {
                    ListMatrimonyParentData lpd = new ListMatrimonyParentData();
                    lpd.setId(childrenData.getChild_id());
                    lpd.setProfile_id(childrenData.getProfile_id());
                    lpd.setProfilePicUrl(childrenData.getChild_img_url());
                    lpd.setName(childrenData.getChild_name());
                    lpd.setChild_gender(childrenData.getGender());
                    lpd.setFatherName(data.getFather_name());
                    lpd.setMotherName(data.getMother_name());
                    lpd.setCity(data.getCity());
                    lpd.setUpdated_time(data.getUpdated_time());

                    Objects.requireNonNull(listDataHeader).add(lpd);
                    ListMatrimonyChildData lcd = new ListMatrimonyChildData();
                    lcd.setProfile_id(data.getProfile_id());
                    lcd.setChild_address(data.getAddress());
                    lcd.setChild_birth_date(childrenData.getChild_bday());
                    lcd.setChild_birth_time(childrenData.getBirth_time());
                    lcd.setChild_birth_place(childrenData.getBirth_place());
                    lcd.setChild_blood_group(childrenData.getBlood_group());
                    lcd.setChild_mobile(childrenData.getMobile());
                    lcd.setChild_gotra(data.getGotra());
                    lcd.setHome_lat(data.getHome_lat());
                    lcd.setHome_lng(data.getHome_lng());
                    lcd.setName(childrenData.getChild_name());
                    ArrayList<ListMatrimonyChildData> mlstChildData = new ArrayList<>();
                    mlstChildData.add(lcd);
                    Objects.requireNonNull(listDataChild).put(lpd, mlstChildData);
                }
            }
        }
    }

}
