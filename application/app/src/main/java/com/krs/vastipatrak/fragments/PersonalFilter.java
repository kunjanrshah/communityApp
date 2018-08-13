package com.krs.vastipatrak.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;
import com.melnykov.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.ddMMMyyyy;
import static com.krs.vastipatrak.utils.Common.dd_MMM_yyyy;
import static com.krs.vastipatrak.utils.Common.yyyy_MM_dd;

public class PersonalFilter extends Fragment {


    private static final int CONTACT_PICKER_RESULT = 1001;
    public Spinner spinnerBlood,spinnerGotra;
    public RadioButton rbtnM;
    public RadioButton rbtnF;
    public EditText edtFName, edtLName, edtFatherName, edtMotherName, edtEducation, edtBPlace, edtNPlace, edtMobile, edtAddress, edt_Eaddress, edt_phone, edtCity;
    public EditText edtbdateFrom,edtbdateTo;
    private SharedPreferences mSharedPreferences;
    //public String bdateFrom="",bdateTo="";
   // public String gender = "";
    //private ObservableScrollView scroll_pdetails;
    ArrayAdapter<String> dataAdapter;
    private RadioButton rbtnB;
    private FloatingActionButton floatingActionButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_personal, container, false);

        MemoryAllocation(rootView);
        setAdapterBGlist();
        setPreferenceData();

        rbtnM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    rbtnF.setChecked(false);
                    rbtnB.setChecked(false);
                }
            }
        });

        rbtnF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtnM.setChecked(false);
                    rbtnB.setChecked(false);
                }
            }
        });

        rbtnB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtnM.setChecked(false);
                    rbtnF.setChecked(false);
                }
            }
        });

        edtbdateFrom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtbdateFrom.getRight() - edtbdateFrom.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getActivity(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Birth Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (++monthOfYear);
                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = str_day + "/" + str_month + "/" + year;
                                edtbdateFrom.setText(date);
                                edtbdateTo.setText(date);
                              //  date= year+ "-" + str_month + "-" + str_day;
                               // bdateFrom=date;
                               // bdateTo=date;
                            }
                        });
                        dpd.show(Objects.requireNonNull(getActivity()).getFragmentManager(), "Datepickerdialog");
                        return true;
                    }
                }
                return false;
            }
        });

        edtbdateTo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((event.getRawX()-400) >= (edtbdateTo.getRight() - edtbdateTo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getActivity(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Birth Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (++monthOfYear);
                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = str_day + "/" + str_month + "/" + year;

                                try {
                                    if (Common.CompareTwoDates(edtbdateFrom.getText().toString(), date)) {
                                        edtbdateTo.setText(date);
                                        //date= year + "-" + str_month + "-" +str_day;
                                      //  bdateTo=date;
                                    } else {
                                        Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    Toast.makeText(getActivity(), "Enter birthdate from", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        });
                        dpd.show(Objects.requireNonNull(getActivity()).getFragmentManager(), "Datepickerdialog");
                        return true;
                    }
                }
                return false;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FilterActivity) Objects.requireNonNull(getActivity())).callAdvanceSearchWS();
            }
        });

        edtMobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((event.getRawX()-500) >= (edtMobile.getRight() - edtMobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Common.canReadContacts(Objects.requireNonNull(getActivity()))) {
                                Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(it, CONTACT_PICKER_RESULT);
                            }
                        } else {
                            Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            startActivityForResult(it, CONTACT_PICKER_RESULT);
                        }

                        return true;
                    }
                }
                return false;
            }
        });
        getGotraWS();
        return rootView;
    }

    private void getGotraWS() {
        if (Common.isOnline(getActivity())) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Common.Constant_Class.GET_GOTRA_URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray("data");
                            List<String> lstgotra = new ArrayList<>();

                            for (int i = 0; i < mJsonArray.length(); i++) {
                                lstgotra.add(mJsonArray.getString(i));
                            }
                            Collections.sort(lstgotra);
                            lstgotra.add(0,Common.Constant_Class.TITLE_GOTRA);
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, lstgotra);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerGotra.setAdapter(dataAdapter);


                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d("PersonalFilter", "Error: " + error.getMessage());
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

    private void MemoryAllocation(@NonNull View rootView) {

       // gender = "";
        //  scroll_pdetails = rootView.findViewById(R.id.scroll_pdetails);
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        floatingActionButton = rootView.findViewById(R.id.fab_psave);
        spinnerBlood = rootView.findViewById(R.id.spinnerBlood);
        rbtnM = rootView.findViewById(R.id.rbtnM);
        rbtnF = rootView.findViewById(R.id.rbtnF);
        rbtnB = rootView.findViewById(R.id.rbtnB);
        edtbdateFrom = rootView.findViewById(R.id.edtbdate_from);
        edtbdateTo = rootView.findViewById(R.id.edtbdate_to);
        edtFName = rootView.findViewById(R.id.edtFName);
        edtLName = rootView.findViewById(R.id.edtLName);
        edtFatherName = rootView.findViewById(R.id.edtFatherName);
        edtMotherName = rootView.findViewById(R.id.edtMotherName);
        edtEducation = rootView.findViewById(R.id.edtEducation);
        edtBPlace = rootView.findViewById(R.id.edtBPlace);
        edtNPlace = rootView.findViewById(R.id.edtNPlace);
        edtCity = rootView.findViewById(R.id.edtCity);
        spinnerGotra = rootView.findViewById(R.id.spinnerGotra);
        edtMobile = rootView.findViewById(R.id.edtMobile);
        edtAddress = rootView.findViewById(R.id.edtAddress);
        edt_Eaddress = rootView.findViewById(R.id.edt_Eaddress);
        edt_phone = rootView.findViewById(R.id.edt_phone);

    }

    private void setPreferenceData() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_FILTER, Context.MODE_PRIVATE);
        String json = mSharedPreferences.getString("adv_search", "");
        JSONObject mjsonObject = null;
        try {
            mjsonObject = new JSONObject(json);
            if (mjsonObject.has(Common.Constant_Class.BLOOD_GROUP)) {
                String compareValue = mjsonObject.getString(Common.Constant_Class.BLOOD_GROUP);
                if (!compareValue.isEmpty()) {
                    int spinnerPosition = dataAdapter.getPosition(compareValue);
                    spinnerBlood.setSelection(spinnerPosition);
                }
            }
            if (mjsonObject.has(Common.Constant_Class.GENDER)) {
                String gender = mjsonObject.getString(Common.Constant_Class.GENDER);
                if (gender.equals("both")) {
                    rbtnM.setChecked(false);
                    rbtnF.setChecked(false);
                    rbtnB.setChecked(true);
                } else if (gender.equals("male")) {
                    rbtnB.setChecked(false);
                    rbtnF.setChecked(false);
                    rbtnM.setChecked(true);
                } else if (gender.equals("female")) {
                    rbtnB.setChecked(false);
                    rbtnM.setChecked(false);
                    rbtnF.setChecked(true);
                }
            }
            if (mjsonObject.has(Common.Constant_Class.FROM_BIRTH_DATE)) {
                String bdate = mjsonObject.getString(Common.Constant_Class.FROM_BIRTH_DATE);
              //  bdateFrom=bdate;
                bdate=Common.parseDateToddMMyyyy(bdate,yyyy_MM_dd,ddMMMyyyy);
                edtbdateFrom.setText(bdate);
            }
            if (mjsonObject.has(Common.Constant_Class.TO_BIRTH_DATE)) {
                String bdate = mjsonObject.getString(Common.Constant_Class.TO_BIRTH_DATE);
               // bdateTo=bdate;
                bdate=Common.parseDateToddMMyyyy(bdate,yyyy_MM_dd,ddMMMyyyy);
                edtbdateTo.setText(bdate);
            }

            if (mjsonObject.has(Common.Constant_Class.FIRST_NAME)) {
                String fname = mjsonObject.getString(Common.Constant_Class.FIRST_NAME);
                edtFName.setText(fname);
            }
            if (mjsonObject.has(Common.Constant_Class.LAST_NAME)) {
                edtLName.setText(mjsonObject.getString(Common.Constant_Class.LAST_NAME));
            }
            if (mjsonObject.has(Common.Constant_Class.FATHER_NAME)) {
                edtFatherName.setText(mjsonObject.getString(Common.Constant_Class.FATHER_NAME));
            }
            if (mjsonObject.has(Common.Constant_Class.MOTHER_NAME)) {
                edtMotherName.setText(mjsonObject.getString(Common.Constant_Class.MOTHER_NAME));
            }
            if (mjsonObject.has(Common.Constant_Class.EDUCATION)) {
                edtEducation.setText(mjsonObject.getString(Common.Constant_Class.EDUCATION));
            }

            if (mjsonObject.has(Common.Constant_Class.BIRTH_PLACE)) {
                edtBPlace.setText(mjsonObject.getString(Common.Constant_Class.BIRTH_PLACE));
            }
            if (mjsonObject.has(Common.Constant_Class.NATIVE_PLACE)) {
                edtNPlace.setText(mjsonObject.getString(Common.Constant_Class.NATIVE_PLACE));
            }
            if (mjsonObject.has(Common.Constant_Class.CITY)) {
                edtCity.setText(mjsonObject.getString(Common.Constant_Class.CITY));
            }
            if (mjsonObject.has(Common.Constant_Class.GOTRA)) {
               // edtGotra.setText(mjsonObject.getString(Common.Constant_Class.GOTRA));
            }
            if (mjsonObject.has(Common.Constant_Class.MOBILE)) {
                edtMobile.setText(mjsonObject.getString(Common.Constant_Class.MOBILE));
            }
            if (mjsonObject.has(Common.Constant_Class.ADDRESS)) {
                edtAddress.setText(mjsonObject.getString(Common.Constant_Class.ADDRESS));
            }
            if (mjsonObject.has(Common.Constant_Class.EMAIL_ADDRESS)) {
                edt_Eaddress.setText(mjsonObject.getString(Common.Constant_Class.EMAIL_ADDRESS));
            }
            if (mjsonObject.has(Common.Constant_Class.PHONE)) {
                edt_phone.setText(mjsonObject.getString(Common.Constant_Class.PHONE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapterBGlist() {
        List<String> blood_cate = new ArrayList<>();
        blood_cate.add(Common.Constant_Class.TITLE_BLOOD_GROUP);
        blood_cate.add(Common.Constant_Class.A_POSITIVE);
        blood_cate.add(Common.Constant_Class.A_NAGATIVE);
        blood_cate.add(Common.Constant_Class.B_POSITIVE);
        blood_cate.add(Common.Constant_Class.B_NAGATIVE);
        blood_cate.add(Common.Constant_Class.O_POSITIVE);
        blood_cate.add(Common.Constant_Class.O_NAGATIVE);

        dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, blood_cate);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBlood.setAdapter(dataAdapter);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == Activity.RESULT_OK && null != data) {
            Uri contactUri = data.getData();
            Cursor contactCursor = Objects.requireNonNull(getActivity()).getContentResolver().query(Objects.requireNonNull(contactUri),
                    new String[]{ContactsContract.Contacts._ID}, null, null,
                    null);
            String id = null;
            if (Objects.requireNonNull(contactCursor).moveToFirst()) {
                id = contactCursor.getString(contactCursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
            }
            contactCursor.close();
            String phoneNumber;
            Cursor phoneCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                    new String[]{id}, null);
            if (Objects.requireNonNull(phoneCursor).moveToFirst()) {
                phoneNumber = phoneCursor
                        .getString(phoneCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.v("phoneNumber :", "" + phoneNumber);
                if (phoneNumber != null) {
                    edtMobile.setText(phoneNumber.replace("+", ""));
                }
            }
            phoneCursor.close();

        }
    }
}
