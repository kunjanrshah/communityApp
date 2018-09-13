package com.krs.vastipatrak.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.adapter.ExpandableListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.interfaces.IAdminControl;
import com.krs.vastipatrak.model.ExportProfileData;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.utils.Common;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.realm.RealmResults;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.isOnline;
import static com.krs.vastipatrak.utils.Common.showProgressDialog;
import static com.krs.vastipatrak.utils.Common.textAsBitmap;


public class SearchFragment extends Fragment implements IAdminControl {


    private static final String[] CALL_PHONE_PERMS = {Manifest.permission.CALL_PHONE};
    private static final int CALL_PHONE_REQUEST = 3;
    @NonNull
    private final String TAG = "SearchFragment";
    @NonNull
    private final String tag_json_obj = "jobj_req";
    int page_count = 0;
    @Nullable
    private ArrayList<String> lstSelectedIDs = null;
    @Nullable
    private ArrayList<ListParentData> listDataHeader = null;
    @Nullable
    private HashMap<ListParentData, List<ListChildData>> listDataChild = null;
    private String query = "";
    private String query_string = "";
    private int adminControl = -1;
    @Nullable
    private ExpandableListView lvCustomList;
    @Nullable
    private ExpandableListAdapter mExpandableListAdapter = null;
    @Nullable
    private TextView txtLable = null;
    @Nullable
    private SharedPreferences mSharedPreferences = null;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private int page = 1;
    private String search = "";
    private String search_url = "";
    private FloatingActionButton mFloatingActionButton;
    private ISearchCallback iSearchCallback;
    private Context mContext;
    private String SearchString = "";

    public SearchFragment() {
        // Required empty public constructor
    }

    private static void ExportAlert(@NonNull final Activity mActivity, @NonNull final File file) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(mActivity.getString(R.string.app_name));

        builder.setMessage("Data Exported in a Excel Sheet");
        builder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                //  File fileWithinMyDir = new File(myFilePath);
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (file.exists()) {
                    intentShareFile.setType("application/xls");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                    mActivity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
                dialog.dismiss();

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE_REQUEST:
                if (Common.canCallPhone(Objects.requireNonNull(getActivity()))) {
                    Toast.makeText(getActivity(), "You need to give permission to access phone ! ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (Common.canCallPhone(Objects.requireNonNull(getActivity()))) {
            requestPermissions(CALL_PHONE_PERMS, CALL_PHONE_REQUEST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle("Search");
        Bundle args = getArguments();
        if (args != null) {
            query = args.getString(Common.Constant_Class.QUERY, "");
            query_string = args.getString(Common.Constant_Class.QUERY_STRING, "");
            adminControl = args.getInt(Common.Constant_Class.AdminControl, -1);
        }
        Memory_Allocation(rootView);

        if (query != null && !query.equalsIgnoreCase("")) {
            Common.Title = query;
            OnlineSearch(query, Common.Constant_Class.GLOBAL_SEARCH_URL);
        } else if (query_string != null && !query_string.equalsIgnoreCase("")) {
            OnlineSearch(query_string, Common.Constant_Class.ADVANCE_SEARCH_URL);
        } else if (adminControl == Common.Constant_Class.NonActive) {
            callNonActivesWS();
        } else {
            lvCustomList.setVisibility(View.GONE);
            Objects.requireNonNull(txtLable).setVisibility(View.VISIBLE);
        }

        lvCustomList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) lvCustomList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    page--;
                } else {
                    page++;
                }
                if (page > 0) {
                    OnlineSearch(search, search_url);
                } else {
                    mSwipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "No record found!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        lvCustomList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {

                    mFloatingActionButton.setVisibility(View.INVISIBLE);
                } else {
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        lvCustomList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
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

                            OnlineSearch(search, search_url);
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


    private void Memory_Allocation(View root) {

        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        lvCustomList = root.findViewById(R.id.lvCustomList);
        mFloatingActionButton = root.findViewById(R.id.floating_action_button);
        txtLable = root.findViewById(R.id.txtLable);
        mSwipyRefreshLayout = root.findViewById(R.id.swipyrefreshlayout);
        TextView tv = root.findViewById(R.id.txt_marquee);
        tv.setSelected(true);

        lstSelectedIDs = new ArrayList<>();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem exportItem = menu.findItem(R.id.action_export);
        exportItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Toast.makeText(getActivity(), "Export", Toast.LENGTH_SHORT).show();
                ExportSearch();

                return false;
            }
        });
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                query_string = "";
                query = str;
                page = 1;
                OnlineSearch(str, Common.Constant_Class.GLOBAL_SEARCH_URL);
                //callSearchWS(str, Common.Constant_Class.GLOBAL_SEARCH_URL);
                return false;
            }

            @Override
            public boolean onQueryTextChange(@NonNull String newText) {
                /*query_string = "";
                query = newText;
                callSearchWS(newText);*/
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

   /* private void callSearchWS(String str_search,String url) {
        if (str_search.length() > 3) {

            Objects.requireNonNull(txtLable).setVisibility(View.GONE);
            lvCustomList.setVisibility(View.VISIBLE);

            if (query_string != null && !query_string.equalsIgnoreCase("")) {
                Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(Common.Title);
                //  OfflineSearch(str_search, 2);
                OnlineSearch(str_search,url);
            } else {
                if (query != null) {
                    Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(str_search);
                    OfflineSearch(str_search, 1);
                }
            }
        }
    }
*/


   /* private void OnlineSearch(String query_string) {
        saveRecordsFromServerWS(query_string);
       *//* ListProfiles mProfilelist = new ListProfiles(new RealmList<ListProfileData>());
        RealmResults<ListProfileData> realmList=AppController.getInstance().realm.where(ListProfileData.class).findAll();
        mProfilelist.realmlist.addAll(realmList);
        setAdapter(mProfilelist);*//*
    }*/

    @NonNull
    private String getSelectedName() {
        StringBuilder msgNames = new StringBuilder();
        Objects.requireNonNull(lstSelectedIDs).clear();
        if (mExpandableListAdapter != null) {
            for (int i = 0, j = 1; i < Objects.requireNonNull(listDataHeader).size(); i++) {
                if (mExpandableListAdapter.checkboxMap.get(i)) {
                    msgNames.append(j).append(": ").append(listDataHeader.get(i).getName()).append("\n");
                    lstSelectedIDs.add(listDataHeader.get(i).getId());
                    j++;
                }
            }
        }
        return msgNames.toString();
    }

    private void alert(String message, final int mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getActivity().getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {

                switch (mode) {
                    case 0:
                        callStatusChangeWS(0);
                        break;
                    case 1:
                        callStatusChangeWS(1);
                        break;
                    case 2:
                        callDeleteWS();
                        break;
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void ExportSearch() {
        JSONObject mJsonObject = null;
        if (!SearchString.isEmpty()) {
            try {
                mJsonObject = new JSONObject(SearchString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, search_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        mSwipyRefreshLayout.setRefreshing(false);
                        boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success) {
                            AppController.getInstance().realm.beginTransaction();
                            RealmResults<ExportProfileData> mlistData = AppController.getInstance().realm.where(ExportProfileData.class).findAll();
                            mlistData.deleteAllFromRealm();
                            AppController.getInstance().realm.commitTransaction();

                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                Common.ExportProfile(mJsondata);
                            }
                            ExportSearchData();
                        }
                        hideProgressDialog();
                        //Common.alert(getActivity(), message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    hideProgressDialog();
                    mSwipyRefreshLayout.setRefreshing(false);
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        } else {
            Toast.makeText(getActivity(), "Please search result!", Toast.LENGTH_SHORT).show();
        }
    }



   /* private void OnlineSearch(String query_string) {
        saveRecordsFromServerWS(query_string);
       *//* ListProfiles mProfilelist = new ListProfiles(new RealmList<ListProfileData>());
        RealmResults<ListProfileData> realmList=AppController.getInstance().realm.where(ListProfileData.class).findAll();
        mProfilelist.realmlist.addAll(realmList);
        setAdapter(mProfilelist);*//*
    }*/


    private void OnlineSearch(String search, String search_url) {
        if (getActivity() != null) {
            setmContext(getActivity());
        }
        if (Common.isOnline(getmContext())) {

            if (!search.equalsIgnoreCase("")) {
                JSONObject mJsonObject = null;
                try {
                    this.search = search;
                    this.search_url = search_url;
                    if (search_url.equalsIgnoreCase(Common.Constant_Class.GLOBAL_SEARCH_URL)) {
                        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(search);
                        JSONObject globalObj = new JSONObject();
                        globalObj.put("search_str", search.toLowerCase().trim());
                        search = globalObj.toString();
                    }
                    SearchString = search;
                    mJsonObject = new JSONObject(search);
                    mJsonObject.put(Common.Constant_Class.PAGE, String.valueOf(page));
                    if (mSharedPreferences != null) {
                        mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                        mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (page == 1) {
                    showProgressDialog(getActivity());
                }
                mSwipyRefreshLayout.setRefreshing(true);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, search_url, mJsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            hideProgressDialog();
                            iSearchCallback = (ISearchCallback) getmContext();
                            if (iSearchCallback != null) {
                                iSearchCallback.setIsSearch(true);
                            }

                            mSwipyRefreshLayout.setRefreshing(false);
                            displayData(response, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hideProgressDialog();
                        mSwipyRefreshLayout.setRefreshing(false);
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
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

            } else {
                Toast.makeText(getActivity(), getString(R.string.err_msg_search), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayData(@NonNull JSONObject response, boolean isNonActive) {
        try {
            iSearchCallback = (ISearchCallback) getActivity();
            String success = response.getString(Common.Constant_Class.SUCCESS);
            String message = response.getString(Common.Constant_Class.MESSAGE);
            String total_records = "0";
            if (response.has(Common.Constant_Class.TOTAL_RECORDS)) {
                total_records = response.getString(Common.Constant_Class.TOTAL_RECORDS);
            }

            if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                Objects.requireNonNull(listDataHeader).clear();
                Objects.requireNonNull(listDataChild).clear();
                int total = 0;
                lvCustomList.setVisibility(View.VISIBLE);
                try {
                    total = Integer.parseInt(total_records);
                    page_count = total / 25;
                    int mod = total % 25;
                    if (mod != 0) {
                        page_count = page_count + 1;
                    }
                    mFloatingActionButton.setImageBitmap(textAsBitmap(String.valueOf(page) + "/" + String.valueOf(page_count), 40, Color.WHITE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Objects.requireNonNull(txtLable).setVisibility(View.GONE);
                JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsondata = mJsonArray.getJSONObject(i);
                    String profile_id = mJsondata.getString(Common.Constant_Class.ID);
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
                    String can_share = "0";
                    if (mJsondata.has(Common.Constant_Class.CAN_SHARE)) {
                        can_share = mJsondata.getString(Common.Constant_Class.CAN_SHARE);
                    }
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
                    lcd.setCan_share(can_share);
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
                    ArrayList<ListChildData> mlstChildData = new ArrayList<>();
                    mlstChildData.add(lcd);
                    listDataHeader.add(lpd);
                    listDataChild.put(lpd, mlstChildData);

                }
                mExpandableListAdapter = new ExpandableListAdapter(getmContext(), listDataHeader, listDataChild,"0");
                lvCustomList.setAdapter(mExpandableListAdapter);
                Toast.makeText(getmContext(), "" + message + " Page " + page, Toast.LENGTH_LONG).show();
                hideProgressDialog();

                iSearchCallback.setIsSearch(true);
            } else {
                if (lstSelectedIDs != null) {
                    lstSelectedIDs.clear();
                }
                iSearchCallback.setIsSearch(false);
                hideProgressDialog();
                if (isNonActive) {
                    lvCustomList.setVisibility(View.GONE);
                    Objects.requireNonNull(txtLable).setVisibility(View.VISIBLE);
                    Common.alert(Objects.requireNonNull(getActivity()), message);
                } else {
                    Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callNonActivesWS() {
        if (Common.isOnline(Objects.requireNonNull(getActivity()))) {
            showProgressDialog(getActivity());
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setSubtitle(getString(R.string.action_nonActives));

            JSONObject mJsonObject = new JSONObject();
            try {
                mJsonObject.put(Common.Constant_Class.USER_ID, Objects.requireNonNull(mSharedPreferences).getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String NonActives_url = Common.Constant_Class.INACTIVES_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, NonActives_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    mSwipyRefreshLayout.setRefreshing(false);

                    displayData(response, true);
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
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        } else {
            Toast.makeText(getActivity(), Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void callStatusChangeWS(final int mode) {
        if (Common.isOnline(Objects.requireNonNull(getActivity()))) {
            showProgressDialog(getActivity());
            JSONObject mJsonObject = new JSONObject();
            try {
                mJsonObject.put(Common.Constant_Class.USER_ID, Objects.requireNonNull(mSharedPreferences).getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.IDList, android.text.TextUtils.join(",", Objects.requireNonNull(lstSelectedIDs)));
                mJsonObject.put(Common.Constant_Class.STATUS, String.valueOf(mode));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }
            String status_url = Common.Constant_Class.STATUS_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, status_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
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
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
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
        if (Common.isOnline(Objects.requireNonNull(getActivity()))) {
            showProgressDialog(getActivity());
            String delete_url = Common.Constant_Class.DELETE_URL;
            JSONObject mJsonObject = new JSONObject();
            try {
                assert mSharedPreferences != null;
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.IDList, android.text.TextUtils.join(",", Objects.requireNonNull(lstSelectedIDs)));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, delete_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
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
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        } else {
            Toast.makeText(getActivity(), Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getActivity().getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {

                String str = (String) Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).getSubtitle();
                assert str != null;
                if (str.equalsIgnoreCase(getString(R.string.action_nonActives))) {
                    callNonActivesWS();
                } else {
                    page = 1;
                    OnlineSearch(Common.Title, Common.Constant_Class.GLOBAL_SEARCH_URL);
                    // callSearchWS(Common.Title, Common.Constant_Class.GLOBAL_SEARCH_URL);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(Objects.requireNonNull(getActivity()).getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            AppController.getInstance().mListSearchList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void CallActivate() {
        String msg1 = getSelectedName();
        assert lstSelectedIDs != null;
        String msg = "Do you want to Activate " + lstSelectedIDs.size() + " Records ? \n" + msg1;
        if (lstSelectedIDs.size() > 0) {
            alert(msg, 1);
        } else {
            Toast.makeText(getActivity(), "Please select profile !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void CallDelete() {
        String msg1 = getSelectedName();
        assert lstSelectedIDs != null;
        String msg = "Do you want to Delete  " + lstSelectedIDs.size() + " Records ? \n" + msg1;
        if (lstSelectedIDs.size() > 0) {
            alert(msg, 2);
        } else {
            Toast.makeText(getActivity(), "Please select profile !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void CallDeActivate() {
        String msg1 = getSelectedName();
        assert lstSelectedIDs != null;
        String msg = "Do you want to Deactivate  " + lstSelectedIDs.size() + " Records ? \n" + msg1;
        if (lstSelectedIDs.size() > 0) {
            alert(msg, 0);
        } else {
            Toast.makeText(getActivity(), "Please select profile !", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeRoleWS(String ids, String role) {
        if (!ids.equalsIgnoreCase("") && !role.equalsIgnoreCase("")) {
            if (Common.isOnline(getActivity())) {
                JSONObject json = null;
                try {
                    json = new JSONObject();
                    json.put(Common.Constant_Class.ID, ids);
                    json.put(Common.Constant_Class.ROLE, role);
                    json.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                    json.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Common.showProgressDialog(getActivity());
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.CHANGE_ROLE_URL, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            Common.hideProgressDialog();
                            boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                            String message = response.getString(Common.Constant_Class.MESSAGE);
                            if (success) {
                                lvCustomList.setAdapter(mExpandableListAdapter);
                            }
                            Common.alert(getActivity(), message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        String message = null;
                        Common.hideProgressDialog();
                        if (error instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
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
                        if (mSharedPreferences != null) {
                            params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                        }
                        return params;
                    }
                };


                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
        }
    }

    private void changeRoleDialog(String msg) {
        String[] SPINNERLIST = {"ADMIN", "USER"};
        final Dialog role_dialog = new Dialog(getActivity());
        role_dialog.setTitle("Change Role");
        role_dialog.setContentView(R.layout.custom_role_dialog);
        TextView txtlist = role_dialog.findViewById(R.id.txtlist);
        txtlist.setText(msg);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        final MaterialBetterSpinner role_spinner = role_dialog.findViewById(R.id.role_spinner);

        role_spinner.setAdapter(arrayAdapter);
        Button btnSubmit = role_dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!role_spinner.getText().toString().isEmpty()) {
                    changeRoleWS(lstSelectedIDs.toString().replace("[", "").replace("]", ""), role_spinner.getText().toString());
                    role_dialog.cancel();
                } else {
                    Toast.makeText(getActivity(), "Please select Role !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        role_dialog.show();
    }

    private void ExportSearchData() {

        RealmResults<ExportProfileData> mListProfileResult = AppController.getInstance().realm.where(ExportProfileData.class).findAll();

        if (mListProfileResult != null && mListProfileResult.size() > 0) {

            try {
                File sd = Environment.getExternalStorageDirectory();
                String csvFile = "Vastipatrak.xls";

                File directory = new File(sd.getAbsolutePath());
                //create directory if not exist
                if (!directory.isDirectory()) directory.mkdirs();

                showProgressDialog(getActivity());

                //file path
                File file = new File(directory, csvFile);
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));
                WritableWorkbook workbook;
                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("profileList", 0);

                sheet.addCell(new Label(0, 0, "ID"));
                sheet.addCell(new Label(1, 0, "FirstName"));
                sheet.addCell(new Label(2, 0, "LastName"));
                sheet.addCell(new Label(3, 0, "Address"));
                sheet.addCell(new Label(4, 0, "City"));
                sheet.addCell(new Label(5, 0, "Father"));
                sheet.addCell(new Label(6, 0, "Mother"));
                sheet.addCell(new Label(7, 0, "Email"));
                sheet.addCell(new Label(8, 0, "Mobile"));
                sheet.addCell(new Label(9, 0, "Phone"));
                sheet.addCell(new Label(10, 0, "Blood"));
                sheet.addCell(new Label(11, 0, "Gotra"));
                sheet.addCell(new Label(12, 0, "Native"));
                sheet.addCell(new Label(13, 0, "Birth Place"));
                sheet.addCell(new Label(14, 0, "Birth date"));
                sheet.addCell(new Label(15, 0, "Birth time"));
                sheet.addCell(new Label(16, 0, "Education"));
                sheet.addCell(new Label(17, 0, "Occupation"));
                sheet.addCell(new Label(18, 0, "Work"));
                sheet.addCell(new Label(19, 0, "Office Address"));
                sheet.addCell(new Label(20, 0, "Office Mobile"));
                sheet.addCell(new Label(21, 0, "Spouse"));
                sheet.addCell(new Label(22, 0, "Marriage date"));
                sheet.addCell(new Label(23, 0, "Father in law"));
                sheet.addCell(new Label(24, 0, "Mother in law"));
                sheet.addCell(new Label(25, 0, "Updated"));
                sheet.addCell(new Label(26, 0, "Sync"));

                for (int i = 0; i < mListProfileResult.size(); i++) {
                    int k = i + 1;

                    sheet.addCell(new Label(0, k, Objects.requireNonNull(mListProfileResult.get(i)).getProfile_id()));
                    sheet.addCell(new Label(1, k, Objects.requireNonNull(mListProfileResult.get(i)).getFirst_name()));
                    sheet.addCell(new Label(2, k, Objects.requireNonNull(mListProfileResult.get(i)).getLast_name()));
                    sheet.addCell(new Label(3, k, Objects.requireNonNull(mListProfileResult.get(i)).getAddress()));
                    sheet.addCell(new Label(4, k, Objects.requireNonNull(mListProfileResult.get(i)).getCity()));
                    sheet.addCell(new Label(5, k, Objects.requireNonNull(mListProfileResult.get(i)).getFather_name()));
                    sheet.addCell(new Label(6, k, Objects.requireNonNull(mListProfileResult.get(i)).getMother_name()));
                    sheet.addCell(new Label(7, k, Objects.requireNonNull(mListProfileResult.get(i)).getEmail_address())); // column and row
                    sheet.addCell(new Label(8, k, Objects.requireNonNull(mListProfileResult.get(i)).getMobile()));
                    sheet.addCell(new Label(9, k, Objects.requireNonNull(mListProfileResult.get(i)).getPhone()));
                    sheet.addCell(new Label(10, k, Objects.requireNonNull(mListProfileResult.get(i)).getBlood_group()));
                    sheet.addCell(new Label(11, k, Objects.requireNonNull(mListProfileResult.get(i)).getGotra()));
                    sheet.addCell(new Label(12, k, Objects.requireNonNull(mListProfileResult.get(i)).getNative_place()));
                    sheet.addCell(new Label(13, k, Objects.requireNonNull(mListProfileResult.get(i)).getBirth_place()));
                    sheet.addCell(new Label(14, k, Objects.requireNonNull(mListProfileResult.get(i)).getBirth_date()));
                    sheet.addCell(new Label(15, k, Objects.requireNonNull(mListProfileResult.get(i)).getBirth_time()));
                    sheet.addCell(new Label(16, k, Objects.requireNonNull(mListProfileResult.get(i)).getEducation()));
                    sheet.addCell(new Label(17, k, Objects.requireNonNull(mListProfileResult.get(i)).getOccupation()));
                    sheet.addCell(new Label(18, k, Objects.requireNonNull(mListProfileResult.get(i)).getWork()));
                    sheet.addCell(new Label(19, k, Objects.requireNonNull(mListProfileResult.get(i)).getOffice_address()));
                    sheet.addCell(new Label(20, k, Objects.requireNonNull(mListProfileResult.get(i)).getOffice_mobile()));
                    sheet.addCell(new Label(21, k, Objects.requireNonNull(mListProfileResult.get(i)).getSpouse_name()));
                    sheet.addCell(new Label(22, k, Objects.requireNonNull(mListProfileResult.get(i)).getMarriage_date()));
                    sheet.addCell(new Label(23, k, Objects.requireNonNull(mListProfileResult.get(i)).getSfather_name()));
                    sheet.addCell(new Label(24, k, Objects.requireNonNull(mListProfileResult.get(i)).getSmother_name()));
                    sheet.addCell(new Label(25, k, Objects.requireNonNull(mListProfileResult.get(i)).getUpdated_time()));
                    sheet.addCell(new Label(26, k, Objects.requireNonNull(mListProfileResult.get(i)).getSync_time()));
                    int counter = 26;
                    for (int j = 0; j < Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().size(); j++) {

                        sheet.addCell(new Label(++counter, 0, "Child Id"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_id()));

                        sheet.addCell(new Label(++counter, 0, "Child Name"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_name()));

                        sheet.addCell(new Label(++counter, 0, "Child Gender"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getGender()));

                        sheet.addCell(new Label(++counter, 0, "Child Bdate"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_bday()));

                        sheet.addCell(new Label(++counter, 0, "Child Btime"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j))).getBirth_time()));

                        sheet.addCell(new Label(++counter, 0, "Child Bplace"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getBirth_place()));

                        sheet.addCell(new Label(++counter, 0, "Interested"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).isInterest() + ""));

                        sheet.addCell(new Label(++counter, 0, "Child Edu"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_edu()));

                        sheet.addCell(new Label(++counter, 0, "Child Work"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_work()));
                    }
                }
                workbook.write();
                workbook.close();
                ExportAlert(getActivity(), file);
                //Toast.makeText(mActiviy, "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();

                hideProgressDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.alert(getActivity(), "No Search records found!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.MOVE_TO_SEARCH = 0;
    }

    @Override
    public void ChangeRole() {
        String msg1 = getSelectedName();
        assert lstSelectedIDs != null;
        String msg = "Do you want to Change Role for  " + lstSelectedIDs.size() + " Records ? \n" + msg1;
        if (lstSelectedIDs.size() > 0) {
            changeRoleDialog(msg);
        } else {
            Toast.makeText(getActivity(), "Please select profile !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void SearchAdmins() {
        if (getActivity() != null) {
            setmContext(getActivity());
        }
        if (isOnline(getmContext())) {
            JSONObject mjson = new JSONObject();
            try {
                mjson.put(Common.Constant_Class.ROLE, "ADMIN");
            } catch (Exception e) {
                e.printStackTrace();
            }
            OnlineSearch(mjson.toString(), Common.Constant_Class.ADVANCE_SEARCH_URL);
        }
    }

    public interface ISearchCallback {
        void setIsSearch(boolean isSearch);
    }
}
