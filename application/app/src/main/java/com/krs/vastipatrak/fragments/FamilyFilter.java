package com.krs.vastipatrak.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.krs.vastipatrak.utils.Common;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.ddMMMyyyy;
import static com.krs.vastipatrak.utils.Common.yyyy_MM_dd;

public class FamilyFilter extends Fragment {

    @NonNull
    public String gender = "";
    public EditText edtSpouseName, edtSpouseFName, edtSpouseMName;
    public EditText edtchild_name, edtcedu, edtchild_work, edtchildbplace, edtcmobile;
    public EditText edt_mdate_from, edt_mdate_to, edt_cdate_from, edt_cdate_to;
    public String from_mdate = "", to_mdate = "", from_cdate = "", to_cdate = "";
    public Spinner spinnerBlood;
    RadioButton radioM, radioF, radioB;
    ArrayAdapter<String> dataAdapter;
    private FloatingActionButton floatingActionButton;
    private ObservableScrollView scroll_fdetails;
    private RadioGroup rgroupid;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_family, container, false);

        MemoryAllocation(rootView);
        setAdapterBGlist();
        setPreferenceData();
        edt_mdate_from.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_mdate_from.getRight() - edt_mdate_from.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Marriage Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = str_day + "/" + str_month + "/" + year;
                                from_mdate = year + "-" + str_month + "-" + str_day;
                                to_mdate = from_mdate;
                                edt_mdate_from.setText(date);
                                edt_mdate_to.setText(date);
                            }
                        });
                        dpd.show(Objects.requireNonNull(getActivity()).getFragmentManager(), "Datepickerdialog");

                        return true;
                    }
                }
                return false;
            }
        });

        edt_mdate_to.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_mdate_to.getRight() - edt_mdate_to.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Marriage Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

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
                                    if (Common.CompareTwoDates(edt_mdate_from.getText().toString(), date)) {
                                        to_mdate = year + "-" + str_month + "-" + str_day;
                                        edt_mdate_to.setText(date);
                                    } else {
                                        Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
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

        edt_cdate_from.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_cdate_from.getRight() - edt_cdate_from.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Child Birth Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = str_day + "/" + str_month + "/" + year;
                                from_cdate = year + "-" + str_month + "-" + str_day;
                                to_cdate = from_cdate;
                                edt_cdate_from.setText(date);
                                edt_cdate_to.setText(date);
                            }
                        });
                        dpd.show(Objects.requireNonNull(getActivity()).getFragmentManager(), "Datepickerdialog");
                        return true;
                    }
                }
                return false;
            }
        });


        edt_cdate_to.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((event.getRawX() - 400) >= (edt_cdate_to.getRight() - edt_cdate_to.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Child Birth Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

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
                                    if (Common.CompareTwoDates(edt_cdate_from.getText().toString(), date)) {
                                        edt_cdate_to.setText(date);
                                        to_cdate = year + "-" + str_month + "-" + str_day;
                                    } else {
                                        Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    Toast.makeText(getActivity(), "Enter child birthdate from", Toast.LENGTH_SHORT).show();
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

        rgroupid.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioM) {
                    gender = "male";
                } else if (checkedId == R.id.radioF) {
                    gender = "female";
                } else {
                    gender = "both";
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FilterActivity) Objects.requireNonNull(getActivity())).callAdvanceSearchWS();
            }
        });

        return rootView;
    }

    private void MemoryAllocation(@NonNull View rootView) {
        scroll_fdetails = rootView.findViewById(R.id.scroll_fdetails);
        floatingActionButton = rootView.findViewById(R.id.fab_fsave);
        edt_mdate_from = rootView.findViewById(R.id.edt_mdate_from);
        edt_mdate_to = rootView.findViewById(R.id.edt_mdate_to);
        spinnerBlood = rootView.findViewById(R.id.spinnerBlood);
        edtSpouseName = rootView.findViewById(R.id.edtSpouseName);
        edtSpouseFName = rootView.findViewById(R.id.edtSpouseFName);
        edtSpouseMName = rootView.findViewById(R.id.edtSpouseMName);
        edtchild_name = rootView.findViewById(R.id.edtchild_name);
        edtcedu = rootView.findViewById(R.id.edtcedu);
        edtchild_work = rootView.findViewById(R.id.edtchild_work);
        edt_cdate_from = rootView.findViewById(R.id.edt_cdate_from);
        edt_cdate_to = rootView.findViewById(R.id.edt_cdate_to);
        edtchildbplace = rootView.findViewById(R.id.edtchildbplace);
        edtcmobile = rootView.findViewById(R.id.edtcmobile);
        rgroupid = rootView.findViewById(R.id.rgroupid);
        radioM = rootView.findViewById(R.id.radioM);
        radioF = rootView.findViewById(R.id.radioF);
        radioB = rootView.findViewById(R.id.radioB);
    }

    private void setAdapterBGlist() {
        List<String> blood_cate = new ArrayList<>();
        blood_cate.add(Common.Constant_Class.TITLE_CHILD_BLOOD_GROUP);
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

    private void setPreferenceData() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_FILTER, Context.MODE_PRIVATE);
        String json = mSharedPreferences.getString("adv_search", "");
        JSONObject mjsonObject = null;
        try {
            mjsonObject = new JSONObject(json);
            if (mjsonObject.has(Common.Constant_Class.CHILD_BLOOD_GROUP)) {
                String compareValue = mjsonObject.getString(Common.Constant_Class.CHILD_BLOOD_GROUP);
                if (!compareValue.isEmpty()) {
                    int spinnerPosition = dataAdapter.getPosition(compareValue);
                    spinnerBlood.setSelection(spinnerPosition);
                }
            }
            if (mjsonObject.has(Common.Constant_Class.FROM_MARRIAGE_DATE)) {
                String bdate = mjsonObject.getString(Common.Constant_Class.FROM_MARRIAGE_DATE);
                bdate = Common.parseDateToddMMyyyy(bdate, yyyy_MM_dd, ddMMMyyyy);
                edt_mdate_from.setText(bdate);
            }
            if (mjsonObject.has(Common.Constant_Class.TO_MARRIAGE_DATE)) {
                String bdate = mjsonObject.getString(Common.Constant_Class.TO_MARRIAGE_DATE);
                bdate = Common.parseDateToddMMyyyy(bdate, yyyy_MM_dd, ddMMMyyyy);
                edt_mdate_to.setText(bdate);
            }
            if (mjsonObject.has(Common.Constant_Class.CHILD_MOBILE)) {
                edtcmobile.setText(mjsonObject.getString(Common.Constant_Class.CHILD_MOBILE));
            }

            if (mjsonObject.has(Common.Constant_Class.SPOUSE_NAME)) {
                edtSpouseName.setText(mjsonObject.getString(Common.Constant_Class.SPOUSE_NAME));
            }
            if (mjsonObject.has(Common.Constant_Class.SPOUSE_FATHER_NAME)) {
                edtSpouseFName.setText(mjsonObject.getString(Common.Constant_Class.SPOUSE_FATHER_NAME));
            }
            if (mjsonObject.has(Common.Constant_Class.SPOUSE_MOTHER_NAME)) {
                edtSpouseMName.setText(mjsonObject.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME));
            }
            if (mjsonObject.has(Common.Constant_Class.CHILD_NAME)) {
                edtchild_name.setText(mjsonObject.getString(Common.Constant_Class.CHILD_NAME));
            }
            if (mjsonObject.has(Common.Constant_Class.CHILD_EDU)) {
                edtcedu.setText(mjsonObject.getString(Common.Constant_Class.CHILD_EDU));
            }
            if (mjsonObject.has(Common.Constant_Class.CHILD_WORK)) {
                edtchild_work.setText(mjsonObject.getString(Common.Constant_Class.CHILD_WORK));
            }
            if (mjsonObject.has(Common.Constant_Class.FROM_CHILD_BDAY)) {
                String bdate = mjsonObject.getString(Common.Constant_Class.FROM_CHILD_BDAY);
                bdate = Common.parseDateToddMMyyyy(bdate, yyyy_MM_dd, ddMMMyyyy);
                edt_cdate_from.setText(bdate);
            }
            if (mjsonObject.has(Common.Constant_Class.TO_CHILD_BDAY)) {
                String bdate = mjsonObject.getString(Common.Constant_Class.TO_CHILD_BDAY);
                bdate = Common.parseDateToddMMyyyy(bdate, yyyy_MM_dd, ddMMMyyyy);
                edt_cdate_to.setText(bdate);
            }
            if (mjsonObject.has(Common.Constant_Class.CHILD_BPLACE)) {
                edtchildbplace.setText(mjsonObject.getString(Common.Constant_Class.CHILD_BPLACE));
            }
            if (mjsonObject.has(Common.Constant_Class.CHILD_GENDER)) {
                String gender = mjsonObject.getString(Common.Constant_Class.CHILD_GENDER);
                if (gender.equals("both")) {
                    radioM.setChecked(false);
                    radioF.setChecked(false);
                    radioB.setChecked(true);
                    this.gender = "both";
                } else if (gender.equals("male")) {
                    radioB.setChecked(false);
                    radioF.setChecked(false);
                    radioM.setChecked(true);
                    this.gender = "male";
                } else if (gender.equals("female")) {
                    radioB.setChecked(false);
                    radioM.setChecked(false);
                    radioF.setChecked(true);
                    this.gender = "female";
                }
            } else {
                radioB.setChecked(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
