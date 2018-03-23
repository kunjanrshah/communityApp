package com.krs.vastipatrak.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by Kunjan on 27/08/2016.
 */
public class FamilyFilter extends Fragment {


    public static String gender = "male";
    public static EditText edt_mdate, edt_childbdate, edtSpouseName, edtSpouseFName, edtSpouseMName, edtchild_name, edtcedu, edtchild_work, edtchildbtime, edtchildbplace;
    FloatingActionButton floatingActionButton;
    ObservableScrollView scroll_fdetails;
    private RadioGroup rgroupid;

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

        edt_childbdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_childbdate.getRight() - edt_childbdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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

                                edt_childbdate.setText(date);
                            }
                        });
                        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

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
                } else {
                    gender = "female";
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FilterActivity) getActivity()).callAdvanceSearchWS();

            }
        });


        edtchildbtime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtchildbtime.getRight() - edtchildbtime.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Calendar now = Calendar.getInstance();
                        TimePickerDialog tpd = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) getContext(), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                        tpd.setThemeDark(true);
                        tpd.vibrate(true);
                        tpd.dismissOnPause(false);
                        tpd.enableSeconds(false);
                        if (false) {
                            tpd.setAccentColor(Color.parseColor("#9C27B0"));
                        }
                        if (true) {
                            tpd.setTitle("Birth Time");
                        }
                        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d("TimePicker", "Dialog was cancelled");
                            }
                        });
                        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                                String time = hourString + ":" + minuteString;
                                edtchildbtime.setText(time);
                            }
                        });
                        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");

                        return true;
                    }
                }

                return false;
            }
        });


        return rootView;
    }

    void MemoryAllocation(View rootView) {
        scroll_fdetails = rootView.findViewById(R.id.scroll_fdetails);
        floatingActionButton = rootView.findViewById(R.id.fab_fsave);
        edt_mdate = rootView.findViewById(R.id.edt_mdate);
        edtSpouseName = rootView.findViewById(R.id.edtSpouseName);
        edtSpouseFName = rootView.findViewById(R.id.edtSpouseFName);
        edtSpouseMName = rootView.findViewById(R.id.edtSpouseMName);
        edtchild_name = rootView.findViewById(R.id.edtchild_name);
        edtcedu = rootView.findViewById(R.id.edtcedu);
        edtchild_work = rootView.findViewById(R.id.edtchild_work);

        edt_childbdate = rootView.findViewById(R.id.edt_cdate);
        edtchildbtime = rootView.findViewById(R.id.edtchildbtime);
        edtchildbplace = rootView.findViewById(R.id.edtchildbplace);
        rgroupid = rootView.findViewById(R.id.rgroupid);

    }
}
