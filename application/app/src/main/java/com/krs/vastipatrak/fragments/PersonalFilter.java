package com.krs.vastipatrak.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.krs.vastipatrak.utils.Common;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kunjan on 27/08/2016.
 */
public class PersonalFilter extends Fragment {


    private static final int CONTACT_PICKER_RESULT = 1001;
    public static Spinner spinnerBlood;
    public static RadioButton rbtnM, rbtnF, rbtnB;
    public static EditText edtFName, edtLName, edtFatherName, edtMotherName, edtEducation, edtBPlace, edtNPlace, edtGotra, edtMobile, edtAddress, edt_Eaddress, edt_phone, edtbdate, edtbTime, edtCity;
    public static String gender = "";
    FloatingActionButton floatingActionButton;
    ObservableScrollView scroll_pdetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_personal, container, false);


        MemoryAllocation(rootView);
        setAdapterBGlist();


        rbtnM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    rbtnF.setChecked(false);
                    rbtnB.setChecked(false);
                    gender = "1";
                }
            }
        });

        rbtnF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtnM.setChecked(false);
                    rbtnB.setChecked(false);
                    gender = "0";
                }
            }
        });

        rbtnB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtnM.setChecked(false);
                    rbtnF.setChecked(false);
                    gender = "";
                }
            }
        });

        edtbdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtbdate.getRight() - edtbdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getActivity(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        if (false) {
                            dpd.setAccentColor(Color.parseColor("#9C27B0"));
                        }
                        if (true) {
                            dpd.setTitle("Birth Date");
                        }
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
                                edtbdate.setText(date);
                            }
                        });
                        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");


                        return true;
                    }
                }

                return false;
            }
        });

        edtbTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtbTime.getRight() - edtbTime.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

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
                                edtbTime.setText(time);
                            }
                        });
                        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");

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


        edtMobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtMobile.getRight() - edtMobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Common.canReadContacts(getActivity())) {
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


        return rootView;
    }

    void MemoryAllocation(View rootView) {
        gender = "";
        scroll_pdetails = rootView.findViewById(R.id.scroll_pdetails);
        floatingActionButton = rootView.findViewById(R.id.fab_psave);
        //floatingActionButton.attachToScrollView(scroll_pdetails);
        spinnerBlood = rootView.findViewById(R.id.spinnerBlood);
        //   spinnerBlood.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) getActivity());
        rbtnM = rootView.findViewById(R.id.rbtnM);
        rbtnF = rootView.findViewById(R.id.rbtnF);
        rbtnB = rootView.findViewById(R.id.rbtnB);

        edtbdate = rootView.findViewById(R.id.edtbdate);
        edtbTime = rootView.findViewById(R.id.edtbTime);
        edtFName = rootView.findViewById(R.id.edtFName);
        edtLName = rootView.findViewById(R.id.edtLName);
        edtFatherName = rootView.findViewById(R.id.edtFatherName);
        edtMotherName = rootView.findViewById(R.id.edtMotherName);
        edtEducation = rootView.findViewById(R.id.edtEducation);
        edtBPlace = rootView.findViewById(R.id.edtBPlace);
        edtNPlace = rootView.findViewById(R.id.edtNPlace);
        edtCity = rootView.findViewById(R.id.edtCity);
        edtGotra = rootView.findViewById(R.id.edtGotra);
        edtMobile = rootView.findViewById(R.id.edtMobile);
        edtAddress = rootView.findViewById(R.id.edtAddress);
        edt_Eaddress = rootView.findViewById(R.id.edt_Eaddress);
        edt_phone = rootView.findViewById(R.id.edt_phone);

    }

    void setAdapterBGlist() {
        List<String> blood_cate = new ArrayList<String>();
        blood_cate.add(Common.Constant_Class.TITLE_BLOOD_GROUP);
        blood_cate.add(Common.Constant_Class.A_POSITIVE);
        blood_cate.add(Common.Constant_Class.A_NAGATIVE);
        blood_cate.add(Common.Constant_Class.B_POSITIVE);
        blood_cate.add(Common.Constant_Class.B_NAGATIVE);
        blood_cate.add(Common.Constant_Class.O_POSITIVE);
        blood_cate.add(Common.Constant_Class.O_NAGATIVE);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, blood_cate);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBlood.setAdapter(dataAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == getActivity().RESULT_OK && null != data) {
            Uri contactUri = data.getData();
            Cursor contactCursor = getActivity().getContentResolver().query(contactUri,
                    new String[]{ContactsContract.Contacts._ID}, null, null,
                    null);
            String id = null;
            if (contactCursor.moveToFirst()) {
                id = contactCursor.getString(contactCursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
            }
            contactCursor.close();
            String phoneNumber = null;
            Cursor phoneCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                    new String[]{id}, null);
            if (phoneCursor.moveToFirst()) {
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
