package com.krs.vastipatrak.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Created by Kunjan on 27/08/2016.
 */
public class FamilyFilter extends Fragment {


    FloatingActionButton floatingActionButton;
    ObservableScrollView scroll_fdetails;
    public static EditText edt_mdate, edt_cdate, edtSpouseName, edtSpouseFName, edtSpouseMName, edtchild_name, edtcedu, edtchild_work;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_family, container, false);

        MemoryAllocation(rootView);

        edt_mdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_mdate.getRight() - edt_mdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        if (false) {
                            dpd.setAccentColor(Color.parseColor("#9C27B0"));
                        }
                        if (true) {
                            dpd.setTitle("Marriage Date");
                        }
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


                                edt_mdate.setText(date);
                            }
                        });
                        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                        return true;
                    }
                }

                return false;
            }
        });

        edt_cdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_cdate.getRight() - edt_cdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        if (false) {
                            dpd.setAccentColor(Color.parseColor("#9C27B0"));
                        }
                        if (true) {
                            dpd.setTitle("Child Birth Date");
                        }
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

                                edt_cdate.setText(date);
                            }
                        });
                        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                        return true;
                    }
                }

                return false;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FilterActivity) getActivity()).callAdvanceSearchWS();

            }
        });

        return rootView;
    }

    void MemoryAllocation(View rootView) {
        scroll_fdetails = (ObservableScrollView) rootView.findViewById(R.id.scroll_fdetails);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_fsave);
        edt_mdate = (EditText) rootView.findViewById(R.id.edt_mdate);
        edt_cdate = (EditText) rootView.findViewById(R.id.edt_cdate);
        edtSpouseName = (EditText) rootView.findViewById(R.id.edtSpouseName);
        edtSpouseFName = (EditText) rootView.findViewById(R.id.edtSpouseFName);
        edtSpouseMName = (EditText) rootView.findViewById(R.id.edtSpouseMName);
        edtchild_name = (EditText) rootView.findViewById(R.id.edtchild_name);
        edtcedu = (EditText) rootView.findViewById(R.id.edtcedu);
        edtchild_work = (EditText) rootView.findViewById(R.id.edtchild_work);
    }
}
