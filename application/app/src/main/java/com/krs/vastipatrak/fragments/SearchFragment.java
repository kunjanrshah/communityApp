package com.krs.vastipatrak.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.adapter.ExpandableListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;


public class SearchFragment extends Fragment {


    private static final String[] CALL_PHONE_PERMS = {Manifest.permission.CALL_PHONE};
    private static final int CALL_PHONE_REQUEST = 3;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    ExpandableListView lvCustomList;
    String TAG = "SearchFragment";
    String tag_json_obj = "jobj_req";
    SearchView searchView;
    ArrayList<ListParentData> listDataHeader = null;
    HashMap<ListParentData, List<ListChildData>> listDataChild = null;
    String query = "", query_string = "";
    ProgressDialog pDialog;
    ArrayList<String> lstSelectedIDs = null;
    ExpandableListAdapter mExpandableListAdapter = null;
    // WebView wv_home = null;
    TextView txtLable = null;
    //String webViewUrl = "http://www.androidexample.com/media/webview/details.html";
    //String webViewUrl = "http://www.superbinstruments.com/WEB/photoes.php";
    private SharedPreferences mSharedPreferences = null;
    private AdView mAdView;

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
            //   wv_home.setVisibility(View.VISIBLE);
            txtLable.setVisibility(View.VISIBLE);
            //  wv_home.getSettings().setJavaScriptEnabled(true);
         /*   wv_home.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            wv_home.loadUrl(webViewUrl);*/

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device ID
                    .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                    .build();

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClosed() {
                    Toast.makeText(getActivity().getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Toast.makeText(getActivity().getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                    Toast.makeText(getActivity().getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });

            mAdView.loadAd(adRequest);
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

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();

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

        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
        lvCustomList = root.findViewById(R.id.lvCustomList);
        //   wv_home = (WebView) root.findViewById(R.id.wv_home);
        txtLable = root.findViewById(R.id.txtLable);
        TextView tv = root.findViewById(R.id.TextView03);
        tv.setSelected(true);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(true);

        lstSelectedIDs = new ArrayList<String>();
        listDataHeader = new ArrayList<ListParentData>();
        listDataChild = new HashMap<ListParentData, List<ListChildData>>();

        mAdView = root.findViewById(R.id.adView);
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

        MenuItem voiceItem = menu.findItem(R.id.action_voice);

        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.promptSpeechInput(getActivity());
                return false;
            }
        });

        MenuItem activeItem = menu.findItem(R.id.action_activate);
        MenuItem activeAdd = menu.findItem(R.id.action_add);
        MenuItem deactiveItem = menu.findItem(R.id.action_deactive);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem nonActives = menu.findItem(R.id.action_nonActives);

        nonActives.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                callNonActivesWS();

                return false;
            }
        });


        if (AppController.isAdmin) {
            if (Common.isOnline(getActivity())) {
                activeItem.setVisible(true);
                deactiveItem.setVisible(true);
                deleteItem.setVisible(true);
                nonActives.setVisible(true);
                activeAdd.setVisible(true);
            } else {
                Toast.makeText(getActivity(), "" + Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        }

        activeAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                mIntent.putExtra(Common.Constant_Class.SCREEN, Common.Constant_Class.SEARCH_FRAGMENT);
                startActivity(mIntent);
                return false;
            }
        });

        activeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String msg1 = getSelectedName();
                String msg = "Do you want to Activate " + lstSelectedIDs.size() + " Records ? \n" + msg1;
                if (lstSelectedIDs.size() > 0) {
                    alert(msg, 1);
                } else {
                    Toast.makeText(getActivity(), "Please select profile !", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        deactiveItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String msg1 = getSelectedName();
                String msg = "Do you want to Deactivate  " + lstSelectedIDs.size() + " Records ? \n" + msg1;
                if (lstSelectedIDs.size() > 0) {
                    alert(msg, 0);
                } else {
                    Toast.makeText(getActivity(), "Please select profile !", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String msg1 = getSelectedName();
                String msg = "Do you want to Delete  " + lstSelectedIDs.size() + " Records ? \n" + msg1;
                if (lstSelectedIDs.size() > 0) {
                    alert(msg, 2);
                } else {
                    Toast.makeText(getActivity(), "Please select profile !", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    private String getSelectedName() {
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

    private void alert(String message, final int mode) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;

            default:
                break;
        }


        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    searchView.setQueryHint(result.get(0));
                    searchView.setQuery(result.get(0), true);
                }
                break;
            }

        }
    }


    private void callSearchWS(String str_search) {
        if (str_search.length() > 3) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Common.Title);
            txtLable.setVisibility(View.GONE);
            lvCustomList.setVisibility(View.VISIBLE);
            if (query_string != null && !query_string.equalsIgnoreCase("")) {
                OfflineSearch(str_search, 2);
            } else {
                if (query != null && !str_search.equalsIgnoreCase("")) {
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
        RealmList<ListProfileData> mListProfileDatas = new RealmList<>();
        RealmList<ListProfileData> mListParentData = Common.getDataFromParentTable(str_search, search);
        for (int i = 0; i < mListParentData.size(); i++) {
            mListProfileDatas.add(mListParentData.get(i));
        }

        RealmList<ListProfileData> mListChildData = Common.getDataFromChildTable(str_search, search);
        for (int j = 0; j < mListChildData.size(); j++) {
            boolean flag = true;
            for (int k = 0; k < mListProfileDatas.size(); k++) {
                if (mListProfileDatas.get(k).getProfile_id() == mListChildData.get(j).getProfile_id()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                mListProfileDatas.add(mListChildData.get(j));
            }
        }
        listDataHeader.clear();
        listDataChild.clear();
        AppController.getInstance().mListSearchData = mListProfileDatas;
        for (int i = 0; i < mListProfileDatas.size(); i++) {
            ListParentData lpd = new ListParentData();
            lpd.setName(mListProfileDatas.get(i).getFirst_name() + " " + mListProfileDatas.get(i).getLast_name());
            lpd.setFatherName(mListProfileDatas.get(i).getFather_name());
            lpd.setMotherName(mListProfileDatas.get(i).getMother_name());
            lpd.setStatus(mListProfileDatas.get(i).getStatus());
            lpd.setId(mListProfileDatas.get(i).getProfile_id());
            lpd.setProfilePicUrl(mListProfileDatas.get(i).getProfile_pic_url());
            lpd.setUser_lat(mListProfileDatas.get(i).getUser_lat());
            lpd.setUser_lng(mListProfileDatas.get(i).getUser_lng());
            lpd.setIs_location_enable(mListProfileDatas.get(i).isIs_location_enable());
            lpd.setUpdated_time(mListProfileDatas.get(i).getUpdated_time());
            lpd.setCity(mListProfileDatas.get(i).getCity());
            lpd.setUser_lng(mListProfileDatas.get(i).getUser_lng());
            lpd.setUser_lat(mListProfileDatas.get(i).getUser_lat());
            lpd.setHome_lat(mListProfileDatas.get(i).getHome_lat());
            lpd.setHome_lng(mListProfileDatas.get(i).getHome_lng());

            ListChildData lcd = new ListChildData();
            lcd.setID(mListProfileDatas.get(i).getProfile_id());
            lcd.setNative(mListProfileDatas.get(i).getNative_place());
            lcd.setAddress(mListProfileDatas.get(i).getAddress());
            lcd.setbirth_date(mListProfileDatas.get(i).getBirth_date());
            lcd.setbirth_time(mListProfileDatas.get(i).getBirth_time());
            lcd.setBirth_place(mListProfileDatas.get(i).getBirth_place());
            lcd.setBlood_Group(mListProfileDatas.get(i).getBlood_group());
            lcd.setMobile(mListProfileDatas.get(i).getMobile());
            lcd.setPhone(mListProfileDatas.get(i).getPhone());
            lcd.setGender(mListProfileDatas.get(i).getGender());
            lcd.setGotra(mListProfileDatas.get(i).getGotra());
            ArrayList<ListChildData> mlstChildData = new ArrayList<ListChildData>();
            mlstChildData.add(lcd);
            listDataHeader.add(lpd);
            listDataChild.put(lpd, mlstChildData);
        }

        // lvCustomList.requestFocus();
        if (listDataHeader.size() > 0) {
            mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            lvCustomList.setAdapter(mExpandableListAdapter);
        } else {
            lvCustomList.setVisibility(View.GONE);
            txtLable.setVisibility(View.VISIBLE);
//            NoRecordAlert("No Records Found !!");
        }

    }

    private void callNonActivesWS() {
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


}
