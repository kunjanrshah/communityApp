package com.krs.vastipatrak.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.widget.FlexibleCalendar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.adapter.ExpandableListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
import static com.krs.vastipatrak.utils.AppConstants.INIT_TIMEOUT;
import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;

public class CalendarFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private FlexibleCalendar viewCalendar;
    private FloatingActionButton mFloatingActionButton;
    private boolean isExpanded = false;
    private ExpandableListView lvCustomList;
    private ExpandableListAdapter mExpandableListAdapter = null;
    private ArrayList<ListParentData> listDataHeader = null;
    private HashMap<ListParentData, List<ListChildData>> listDataChild = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_calendar);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_topback);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initCalendar(rootView);


        viewCalendar.setCalendarListener(new FlexibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = viewCalendar.getSelectedDay();
                int d = day.getDay();
                int m = (day.getMonth() + 1);
                String m1 = "", d1 = "";
                if (0 < m && m < 10) {
                    m1 = "0" + m;
                } else {
                    m1 = String.valueOf(m);
                }
                if (0 < d && d < 10) {
                    d1 = "0" + d;
                } else {
                    d1 = String.valueOf(d);
                }
                String date = day.getYear() + "-" + m1 + "-" + d1;
                Log.i(getClass().getName(), "Selected Day: " + date);

                CalendarWS(date);
            }

            @Override
            public void onItemClick(View v) {
                Day day = viewCalendar.getSelectedDay();
                Log.i(getClass().getName(), "The Day of Clicked View: " + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());
            }

            @Override
            public void onDataUpdate() {
                Log.i(getClass().getName(), "Data Updated");
            }

            @Override
            public void onMonthChange() {
                Log.i(getClass().getName(), "Month Changed" + ". Current Year: " + viewCalendar.getYear() + ", Current Month: " + (viewCalendar.getMonth() + 1));
            }

            @Override
            public void onWeekChange(int position) {
                Log.i(getClass().getName(), "Week Changed" + ". Current Year: " + viewCalendar.getYear() + ", Current Month: " + (viewCalendar.getMonth() + 1) + ", Current Week position of Month: " + position);
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    isExpanded = false;
                    viewCalendar.collapse(500);
                    mFloatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.expand));

                } else {
                    isExpanded = true;
                    viewCalendar.expand(500);
                    mFloatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.collapse));
                }
            }
        });

        return rootView;
    }

    private void initCalendar(View rootView) {
        mSharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        mFloatingActionButton = rootView.findViewById(R.id.floating_action_button);
        lvCustomList = rootView.findViewById(R.id.lvCustomList);
        viewCalendar = rootView.findViewById(R.id.calendar);
        Calendar cal = Calendar.getInstance();
        CalendarAdapter adapter = new CalendarAdapter(getActivity(), cal);
        viewCalendar.setAdapter(adapter);
        listDataChild = new HashMap<>();
        listDataHeader = new ArrayList<>();
        // use methods
       /* viewCalendar.addEventTag(2015, 8, 10);
        viewCalendar.addEventTag(2015, 8, 14);
        viewCalendar.addEventTag(2015, 8, 23);
*/
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String month1 = "", day1 = "";
        if (0 < month && month < 10) {
            month1 = "0" + month;
        } else {
            month1 = String.valueOf(month);
        }
        if (0 < day && day < 10) {
            day1 = "0" + day;
        } else {
            day1 = String.valueOf(day);
        }
        // viewCalendar.select(new Day(year, month, day));
        String date = year + "-" + month1 + "-" + day1;
        CalendarWS(date);

    }

    private void CalendarWS(String date) {
        if (Utility.isOnline(getActivity())) {
            Utility.showProgressDialog(getActivity());
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.DATE, date);
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.GET_USERS_BY_DATE_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    displayData(response);
                    Utility.hideProgressDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(getClass().getName(), "Error: " + error.getMessage());
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    private void displayData(JSONObject response) {
        try {
            String success = response.getString(AppConstants.SUCCESS);
            String message = response.getString(AppConstants.MESSAGE);
            listDataHeader.clear();
            listDataChild.clear();
            if (success.equalsIgnoreCase(AppConstants.TRUE)) {

                lvCustomList.setVisibility(View.VISIBLE);
                JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsondata = mJsonArray.getJSONObject(i);
                    String profile_id = mJsondata.getString(AppConstants.ID);
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

                    String bdate_rem_id = mJsondata.getString(AppConstants.BDATE_REMINDER_ID);
                    String spouse_rem_id = mJsondata.getString(AppConstants.SPOUSE_BDATE_REMINDER_ID);
                    String mdate_rem_id = mJsondata.getString(AppConstants.MDATE_REMINDER_ID);
                    JSONArray childs = mJsondata.getJSONArray("childs");


                    String can_share = "0";
                    if (mJsondata.has(AppConstants.CAN_SHARE)) {
                        can_share = mJsondata.getString(AppConstants.CAN_SHARE);
                    }

                    ListParentData lpd = new ListParentData();
                    if (mJsondata.has(getString(R.string.search_date_info))) {
                        JSONArray array = mJsondata.getJSONArray(getString(R.string.search_date_info));
                        lpd.setCalLabelArray(array);
                    }

                    lpd.setBdate_rem_id(bdate_rem_id);
                    lpd.setSpouse_rem_id(spouse_rem_id);
                    lpd.setMdate_rem_id(mdate_rem_id);
                    lpd.setChilds(childs);

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
                    String spouse = mJsondata.getString(AppConstants.SPOUSE_NAME);

                    ListChildData lcd = new ListChildData();
                    lcd.setID(profile_id);
                    lcd.setCan_share(can_share);
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
                    lcd.setSpouse_name(spouse);
                    ArrayList<ListChildData> mlstChildData = new ArrayList<>();
                    mlstChildData.add(lcd);
                    listDataHeader.add(lpd);
                    listDataChild.put(lpd, mlstChildData);
                }
            } else {
                if (response.has(AppConstants.ERROR_CODE)) {
                    String error = response.getString(AppConstants.ERROR_CODE);
                    if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                        Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        getActivity().finish();
                    }
                }
            }
            mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, false);
            lvCustomList.setAdapter(mExpandableListAdapter);
            Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
            hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
