package com.krs.vastipatrak.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.widget.FlexibleCalendar;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.ExpandableListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;

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
        initCalendar(rootView);


        viewCalendar.setCalendarListener(new FlexibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = viewCalendar.getSelectedDay();
                String date = day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay();
                Log.i(getClass().getName(), "Selected Day: "
                        + date);
                CalendarWS(date);
            }

            @Override
            public void onItemClick(View v) {
                Day day = viewCalendar.getSelectedDay();
                Log.i(getClass().getName(), "The Day of Clicked View: "
                        + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());
            }

            @Override
            public void onDataUpdate() {
                Log.i(getClass().getName(), "Data Updated");
            }

            @Override
            public void onMonthChange() {
                Log.i(getClass().getName(), "Month Changed"
                        + ". Current Year: " + viewCalendar.getYear()
                        + ", Current Month: " + (viewCalendar.getMonth() + 1));
            }

            @Override
            public void onWeekChange(int position) {
                Log.i(getClass().getName(), "Week Changed"
                        + ". Current Year: " + viewCalendar.getYear()
                        + ", Current Month: " + (viewCalendar.getMonth() + 1)
                        + ", Current Week position of Month: " + position);
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
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mFloatingActionButton = rootView.findViewById(R.id.floating_action_button);
        lvCustomList = rootView.findViewById(R.id.lvCustomList);
        viewCalendar = (FlexibleCalendar) rootView.findViewById(R.id.calendar);
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

        // viewCalendar.select(new Day(year, month, day));
        String date = year + "-" + month + "-" + day;
        CalendarWS(date);
    }

    private void CalendarWS(String date) {
        if (Common.isOnline(getActivity())) {
            Common.showProgressDialog(getActivity());
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put("search_str", "kunjan");//date.trim()
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.GLOBAL_SEARCH_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    displayData(response);
                    Common.hideProgressDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(getClass().getName(), "Error: " + error.getMessage());
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
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    private void displayData(JSONObject response) {
        try {
            String success = response.getString(Common.Constant_Class.SUCCESS);
            String message = response.getString(Common.Constant_Class.MESSAGE);

            if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                listDataHeader.clear();
                listDataChild.clear();
                lvCustomList.setVisibility(View.VISIBLE);
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
                mExpandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, "-1");
                lvCustomList.setAdapter(mExpandableListAdapter);
            }
            Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
            hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
