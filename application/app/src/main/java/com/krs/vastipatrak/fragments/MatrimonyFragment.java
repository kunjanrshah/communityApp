package com.krs.vastipatrak.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
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
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.adapter.ExpandableMarimonyListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ExportProfileData;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListMatrimonyChildData;
import com.krs.vastipatrak.model.ListMatrimonyParentData;
import com.krs.vastipatrak.model.MatrimonyProfileData;
import com.krs.vastipatrak.utils.Common;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.krs.vastipatrak.utils.Common.ExportProfile;
import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.showProgressDialog;
import static com.krs.vastipatrak.utils.Common.textAsBitmap;

public class MatrimonyFragment extends Fragment {

    private final String TAG = MatrimonyFragment.class.getSimpleName();
    ToggleButton tbtn_interest, tbtn_gender;
    int page_count = 0;
    Button btnSearch;
    private ExpandableListView lvMatrimonyList;
    @Nullable
    private ArrayList<ListMatrimonyParentData> listDataHeader = null;
    @Nullable
    private HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> listDataChild = null;
    private Realm realm;
    private int page = 1;
    private FloatingActionButton mFloatingActionButton;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matrimony, container, false);
        assert getActivity() != null;
        Activity mActivity = getActivity();
        Objects.requireNonNull(((AppCompatActivity) mActivity).getSupportActionBar()).setSubtitle(R.string.title_matrimony);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        getChildRecords(false);

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    if (page > 0) {
                        page--;
                    }
                } else {
                    page++;
                }
                if (page > 0) {
                    getChildRecords(false);
                } else {
                    mSwipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "No record found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lvMatrimonyList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    mFloatingActionButton.hide();
                } else {
                    mFloatingActionButton.show();
                }
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_page_count);
                dialog.setTitle(R.string.app_name);
                dialog.setCancelable(false);
                final EditText input_page = dialog.findViewById(R.id.input_page);

                Button btn_send = dialog.findViewById(R.id.btn_send);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int page1 = Integer.parseInt(input_page.getText().toString());
                        if (page1 > 0 && page1 <= page_count) {
                            page = page1;
                            dialog.dismiss();
                            getChildRecords(false);
                        } else {
                            Toast.makeText(getActivity(), "invalid", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.show();
            }
        });

        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        lvMatrimonyList = rootView.findViewById(R.id.lvMatrimonyList);
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        realm = AppController.getInstance().realm;
        mSwipyRefreshLayout = rootView.findViewById(R.id.swipyrefreshlayout);
        mFloatingActionButton = rootView.findViewById(R.id.floating_action_button);
        tbtn_interest = rootView.findViewById(R.id.tbtn_interest);
        tbtn_interest.setTextOn(getResources().getString(R.string.i_am_interested));
        tbtn_interest.setTextOff(getResources().getString(R.string.i_am_not_interested));
        tbtn_gender = rootView.findViewById(R.id.tbtn_gender);
        tbtn_gender.setTextOff(getResources().getString(R.string.female));
        tbtn_gender.setTextOn(getResources().getString(R.string.male));
        btnSearch = rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildRecords(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem exportItem = menu.findItem(R.id.action_export);
        exportItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                getChildRecords(true);
                return false;
            }
        });
    }


    private void getChildRecords(final boolean isExport) {
        final String is_interested, gender;
        if (tbtn_interest != null && !tbtn_interest.isChecked()) {
            is_interested = "0";
        } else {
            is_interested = "1";
        }
        if (tbtn_gender != null && !tbtn_gender.isChecked()) {
            gender = "female";
        } else {
            gender = "male";
        }

        if (Common.isOnline(getActivity())) {
            final JSONObject mjsonObject = new JSONObject();
            try {
                mjsonObject.put(Common.Constant_Class.IS_INTERESTED, is_interested);
                mjsonObject.put(Common.Constant_Class.CHILD_GENDER, gender);
                mjsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mjsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                if (!isExport) {
                    if (page < 1) {
                        page = 1;
                    }
                    mjsonObject.put(Common.Constant_Class.PAGE, String.valueOf(page));
                }
                showProgressDialog(getActivity());
                mSwipyRefreshLayout.setRefreshing(true);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.ADVANCE_SEARCH_URL, mjsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                            String message = response.getString(Common.Constant_Class.MESSAGE);
                            String total_records = "0";
                            if (response.has(Common.Constant_Class.TOTAL_RECORDS)) {
                                total_records = response.getString(Common.Constant_Class.TOTAL_RECORDS);
                            }

                            if (success) {
                                listDataHeader.clear();
                                listDataChild.clear();
                                RealmResults<MatrimonyProfileData> profileData = realm.where(MatrimonyProfileData.class).findAll();
                                realm.beginTransaction();
                                profileData.deleteAllFromRealm();
                                realm.commitTransaction();

                                if (!isExport) {
                                    try {
                                        int total = Integer.parseInt(total_records);
                                        page_count = total / 25;
                                        int mod = total % 25;
                                        if (mod != 0) {
                                            page_count = page_count + 1;
                                        }
                                        mFloatingActionButton.setImageBitmap(textAsBitmap(String.valueOf(page) + "/" + String.valueOf(page_count), 40, Color.WHITE));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                                    for (int i = 0; i < mJsonArray.length(); i++) {
                                        JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                        Common.MatrimonyProfile(mJsondata, gender, is_interested, false);
                                    }
                                    if (!isExport) {
                                        getChildRecords1();
                                        ExpandableMarimonyListAdapter mExpandableMatrimonyListAdapter = new ExpandableMarimonyListAdapter(getActivity(), listDataHeader, listDataChild);
                                        lvMatrimonyList.setAdapter(mExpandableMatrimonyListAdapter);
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                                    Realm realm = AppController.getInstance().realm;
                                    RealmResults<ExportProfileData> results = realm.where(ExportProfileData.class).findAll();
                                    realm.beginTransaction();
                                    results.deleteAllFromRealm();
                                    realm.commitTransaction();

                                    for (int i = 0; i < mJsonArray.length(); i++) {
                                        JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                        Common.MatrimonyProfile(mJsondata, gender, is_interested, false);
                                        if (isExport) {
                                            ExportProfile(mJsondata, getActivity());
                                        }
                                    }
                                    Common.ExportSearchData(getActivity());
                                    btnSearch.performClick();
                                }
                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                    String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                    if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                        Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mIntent);
                                        getActivity().finish();
                                    }
                                }
                            }
                            mSwipyRefreshLayout.setRefreshing(false);
                            hideProgressDialog();
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
                    lpd.setFatherName(data.getFirst_name()+""+data.getLast_name());
                    lpd.setMotherName(data.getSpouse_name());
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
