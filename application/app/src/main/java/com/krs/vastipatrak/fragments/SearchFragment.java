package com.krs.vastipatrak.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.model.ListProfiles;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmResults;


public class SearchFragment extends Fragment {


    private static final String[] CALL_PHONE_PERMS = {Manifest.permission.CALL_PHONE};
    private static final int CALL_PHONE_REQUEST = 3;
    String TAG = "SearchFragment";
    String tag_json_obj = "jobj_req";
    SearchView searchView;
    ArrayList<ListParentData> listDataHeader = null;
    HashMap<ListParentData, List<ListChildData>> listDataChild = null;
    String query = "", query_string = "";
    ProgressDialog pDialog;
    public ArrayList<String> lstSelectedIDs = null;
    ExpandableListView lvCustomList;
    ExpandableListAdapter mExpandableListAdapter = null;
    TextView txtLable = null;
    private SharedPreferences mSharedPreferences = null;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE_REQUEST:
                if (!Common.canCallPhone(getActivity())) {
                    Toast.makeText(getActivity(), "You need to give permission to access phone ! ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (!Common.canCallPhone(getActivity())) {
            requestPermissions(CALL_PHONE_PERMS, CALL_PHONE_REQUEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Search");
        Bundle args = getArguments();
        if (args != null) {
            query = args.getString(Common.Constant_Class.QUERY);
            query_string = args.getString(Common.Constant_Class.QUERY_STRING);
        }

        Memory_Allocation(rootView);

        if (query != null && !query.equalsIgnoreCase("")) {
            Common.Title = query;
            callSearchWS(query);
        } else if (query_string != null && !query_string.equalsIgnoreCase("")) {
            callSearchWS(query_string);
        } else {
            lvCustomList.setVisibility(View.GONE);
            txtLable.setVisibility(View.VISIBLE);
       }

        lvCustomList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    lvCustomList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        return rootView;
    }


    private void showProgressDialog() {

        if (pDialog == null) {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(Common.Constant_Class.LOADING);
            pDialog.setCancelable(true);
        }

        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void Memory_Allocation(View root) {

        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        lvCustomList = root.findViewById(R.id.lvCustomList);
        txtLable = root.findViewById(R.id.txtLable);
        TextView tv = root.findViewById(R.id.txt_marquee);
        tv.setSelected(true);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(true);

        lstSelectedIDs = new ArrayList<String>();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        //mAdView = root.findViewById(R.id.adView);
//        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdUnitId(getString(R.string.banner1));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                /*query_string = "";
                query = str;
                callSearchWS(str);*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query_string = "";
                query = newText;
                callSearchWS(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public String getSelectedName() {
        String msgNames = "";
        lstSelectedIDs.clear();
        if (mExpandableListAdapter != null) {
            for (int i = 0, j = 1; i < listDataHeader.size(); i++) {
                if (mExpandableListAdapter.checkboxMap.get(i)) {
                    msgNames += j + ": " + listDataHeader.get(i).getName() + "\n";
                    lstSelectedIDs.add(listDataHeader.get(i).getId());
                    j++;
                }
            }
        }
        return msgNames;
    }

    public void alert(String message, final int mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getActivity().getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (mode == 0) {
                    callStatusChangeWS(0);
                } else if (mode == 1) {
                    callStatusChangeWS(1);
                } else if (mode == 2) {
                    callDeleteWS();
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void callSearchWS(String str_search) {
        if (str_search.length() > 3) {

            txtLable.setVisibility(View.GONE);
            lvCustomList.setVisibility(View.VISIBLE);
            if (query_string != null && !query_string.equalsIgnoreCase("")) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Common.Title);
                OfflineSearch(str_search, 2);
            } else {
                if (query != null && !str_search.equalsIgnoreCase("")) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(str_search);
                    OfflineSearch(str_search, 1);
                }
            }
        }
    }

    private void alertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getActivity().getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).show();
    }

    private void displayData(JSONObject response, int stat) {
        try {
            String success = response.getString(Common.Constant_Class.SUCCESS);
            String message = response.getString(Common.Constant_Class.MESSAGE);

            listDataHeader.clear();
            listDataChild.clear();
            if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {

                lvCustomList.setVisibility(View.VISIBLE);
                //  wv_home.setVisibility(View.GONE);
                txtLable.setVisibility(View.GONE);
                JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                for (int i = 0; i < mJsonArray.length(); i++) {

                    JSONObject mJsondata = mJsonArray.getJSONObject(i);
                    String profile_id = mJsondata.getString(Common.Constant_Class.ID);
                    String profile_pic_url = mJsondata.getString(Common.Constant_Class.PROFILE_PIC_URL);
                    String first_name = mJsondata.getString(Common.Constant_Class.FIRST_NAME);
                    String last_name = mJsondata.getString(Common.Constant_Class.LAST_NAME);
                    String father_name = mJsondata.getString(Common.Constant_Class.FATHER_NAME);
                    String mother_name = mJsondata.getString(Common.Constant_Class.MOTHER_NAME);
                    String status = mJsondata.getString(Common.Constant_Class.STATUS);
                    String city = mJsondata.getString(Common.Constant_Class.CITY);
                    String updated_time = mJsondata.getString(Common.Constant_Class.UPDATED_TIME);
                    boolean is_location_enable = mJsondata.getBoolean(Common.Constant_Class.IS_LOCATION_ENABLE);

                    if (status.equalsIgnoreCase("1") || stat == 0) {
                        ListParentData lpd = new ListParentData();
                        lpd.setName(first_name + " " + last_name);
                        lpd.setFatherName(father_name);
                        lpd.setMotherName(mother_name);
                        lpd.setProfilePicUrl(profile_pic_url);
                        lpd.setStatus(status);
                        lpd.setId(profile_id);
                        lpd.setCity(city);
                        lpd.setUpdated_time(updated_time);
                        lpd.setIs_location_enable(is_location_enable);

                        String native_place = mJsondata.getString(Common.Constant_Class.NATIVE_PLACE);
                        String address = mJsondata.getString(Common.Constant_Class.ADDRESS);
                        String birth_date = mJsondata.getString(Common.Constant_Class.BIRTH_DATE);
                        String birth_time = mJsondata.getString(Common.Constant_Class.BIRTH_TIME);
                        String birth_place = mJsondata.getString(Common.Constant_Class.BIRTH_PLACE);
                        String blood_group = mJsondata.getString(Common.Constant_Class.BLOOD_GROUP);
                        String mobile = mJsondata.getString(Common.Constant_Class.MOBILE);
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
                        lcd.setPhone(phone);
                        lcd.setGender(gender);
                        lcd.setGotra(gotra);
                        ArrayList<ListChildData> mlstChildData = new ArrayList<ListChildData>();
                        mlstChildData.add(lcd);
                        listDataHeader.add(lpd);
                        listDataChild.put(lpd, mlstChildData);
                    }
                }

                mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
                lvCustomList.setAdapter(mExpandableListAdapter);
            } else {
                lvCustomList.setVisibility(View.GONE);
                txtLable.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OfflineSearch(String str_search, int search) {
        ListProfiles mProfilelist = new ListProfiles(new RealmList<ListProfileData>());
        RealmList<ListProfileData> mListParentData = Common.getDataFromParentTable(str_search, search);
        for (int i = 0; i < mListParentData.size(); i++) {
            mProfilelist.realmlist.add(mListParentData.get(i));
        }

        RealmList<ListProfileData> mListChildData = Common.getDataFromChildTable(str_search, search);
        for (int j = 0; j < mListChildData.size(); j++) {
            boolean flag = true;
            for (int k = 0; k < mProfilelist.realmlist.size(); k++) {
                if (mProfilelist.realmlist.get(k).getProfile_id() == mListChildData.get(j).getProfile_id()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                mProfilelist.realmlist.add(mListChildData.get(j));
            }
        }
        listDataHeader.clear();
        listDataChild.clear();

        AppController.getInstance().realm.beginTransaction();
        mProfilelist = AppController.getInstance().realm.copyToRealm(mProfilelist);
        RealmResults<ListProfileData> mSortedProfiles = mProfilelist.realmlist.sort(Common.Constant_Class.CITY);
        AppController.getInstance().realm.commitTransaction();

        AppController.getInstance().mListSearchList = mSortedProfiles;

        for (int i = 0; i < mSortedProfiles.size(); i++) {
            ListParentData lpd = new ListParentData();
            lpd.setName(mSortedProfiles.get(i).getFirst_name() + " " + mSortedProfiles.get(i).getLast_name());
            lpd.setFatherName(mSortedProfiles.get(i).getFather_name());
            lpd.setMotherName(mSortedProfiles.get(i).getMother_name());
            lpd.setStatus(mSortedProfiles.get(i).getStatus());
            lpd.setId(mSortedProfiles.get(i).getProfile_id());
            lpd.setProfilePicUrl(mSortedProfiles.get(i).getProfile_pic_url());
            lpd.setUser_lat(mSortedProfiles.get(i).getUser_lat());
            lpd.setUser_lng(mSortedProfiles.get(i).getUser_lng());
            lpd.setIs_location_enable(mSortedProfiles.get(i).isIs_location_enable());
            lpd.setUpdated_time(mSortedProfiles.get(i).getUpdated_time());
            lpd.setCity(mSortedProfiles.get(i).getCity());
            lpd.setUser_lng(mSortedProfiles.get(i).getUser_lng());
            lpd.setUser_lat(mSortedProfiles.get(i).getUser_lat());
            lpd.setHome_lat(mSortedProfiles.get(i).getHome_lat());
            lpd.setHome_lng(mSortedProfiles.get(i).getHome_lng());
            lpd.setMobile(mSortedProfiles.get(i).getMobile());
            ListChildData lcd = new ListChildData();
            lcd.setID(mSortedProfiles.get(i).getProfile_id());
            lcd.setNative(mSortedProfiles.get(i).getNative_place());
            lcd.setAddress(mSortedProfiles.get(i).getAddress());
            lcd.setbirth_date(mSortedProfiles.get(i).getBirth_date());
            lcd.setbirth_time(mSortedProfiles.get(i).getBirth_time());
            lcd.setBirth_place(mSortedProfiles.get(i).getBirth_place());
            lcd.setBlood_Group(mSortedProfiles.get(i).getBlood_group());
            lcd.setMobile(mSortedProfiles.get(i).getMobile());
            lcd.setPhone(mSortedProfiles.get(i).getPhone());
            lcd.setGender(mSortedProfiles.get(i).getGender());
            lcd.setGotra(mSortedProfiles.get(i).getGotra());
            ArrayList<ListChildData> mlstChildData = new ArrayList<ListChildData>();
            mlstChildData.add(lcd);
            listDataHeader.add(lpd);
            listDataChild.put(lpd, mlstChildData);
        }
        if (listDataHeader.size() > 0) {
            mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            lvCustomList.setAdapter(mExpandableListAdapter);
        } else {
            lvCustomList.setVisibility(View.GONE);
            txtLable.setVisibility(View.VISIBLE);
        }
    }

    public void callNonActivesWS() {
        if (Common.isOnline(getActivity())) {
            showProgressDialog();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getString(R.string.action_nonActives));

            JSONObject mJsonObject = new JSONObject();
            try {
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String NonActives_url = Common.Constant_Class.INACTIVES_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, NonActives_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    hideProgressDialog();
                    displayData(response, 0);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    hideProgressDialog();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN,mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN,""));
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        } else {
            Toast.makeText(getActivity(), Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void callStatusChangeWS(final int mode) {
        if (Common.isOnline(getActivity())) {
            showProgressDialog();
            JSONObject mJsonObject = new JSONObject();
            try {

                mJsonObject.put(Common.Constant_Class.IDList, android.text.TextUtils.join(",", lstSelectedIDs));
                mJsonObject.put(Common.Constant_Class.STATUS, String.valueOf(mode));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }
            String status_url = Common.Constant_Class.STATUS_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, status_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    hideProgressDialog();
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            Common.UpdateProfileStatus(lstSelectedIDs, String.valueOf(mode));
                            alert(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    hideProgressDialog();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN,mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN,""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        } else {
            Toast.makeText(getActivity(), Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void callDeleteWS() {
        if (Common.isOnline(getActivity())) {
            showProgressDialog();
            String delete_url = Common.Constant_Class.DELETE_URL;
            JSONObject mJsonObject = new JSONObject();
            try {
                mJsonObject.put(Common.Constant_Class.IDList, android.text.TextUtils.join(",", lstSelectedIDs));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, delete_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    hideProgressDialog();
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            Common.DeleteProfiles(lstSelectedIDs);
                            alert(message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    hideProgressDialog();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN,mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN,""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        } else {
            Toast.makeText(getActivity(), Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void NoRecordAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getActivity().getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getActivity().getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String str = (String) ((AppCompatActivity) getActivity()).getSupportActionBar().getSubtitle();

                if (str.equalsIgnoreCase(getString(R.string.action_nonActives))) {
                    callNonActivesWS();
                } else {
                    callSearchWS(Common.Title);
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            AppController.getInstance().mListSearchList=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
