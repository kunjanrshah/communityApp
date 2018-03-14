package com.krs.vastipatrak.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.service.MyLocationService;
import com.krs.vastipatrak.utils.Common;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.RealmList;


public class PersonalFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    public static EditText edtFName, edtLName, edtFatherName, edtMotherName, edtEducation, edtBPlace, edtNPlace, edtGotra, edtMobile, edtAddress, edt_Eaddress, edt_phone, edtbdate = null, edtbTime = null, edtCity = null;
    public static String str_profile_hash = "", str_father_hash = "", str_mother_hash = "";
    public static String gender = "";
    public static Spinner spinnerBlood;
    ToggleButton tbtn_share;
    RadioButton rbtnM, rbtnF;
    ImageView img_profile, img_father, img_mother;
    TextView txt_home;
    String img_selection = "";
    String profile_url = "", father_url = "", mother_url = "";
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    String tag_json_obj = "jobj_req";
    String TAG = "PersonalFragment";
    String name = "";
    String user_id = "";
    double home_lat, home_lng, user_lat, user_lng;


    public PersonalFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.mGoogleApiClient != null) {
            MainActivity.mGoogleApiClient.connect();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        MemoryAllocation(rootView);

        try {
            RealmList<ListProfileData> mListProfileData = ((MyProfileActivity) getActivity()).getMyData();
            if (mListProfileData != null) {
                setOfflineData(mListProfileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        img_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
                    img_selection = "profile";
                    selectImage();
                } else {
                    String Name = edtFName.getText().toString() + " " + edtLName.getText().toString();
                    openImageDialog(Name, profile_url);
                }

            }
        });

        img_father.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
                    img_selection = "father";
                    selectImage();
                } else {
                    String Name = edtFatherName.getText().toString();
                    openImageDialog(Name, father_url);
                }
            }
        });

        img_mother.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
                    img_selection = "mother";
                    selectImage();
                } else {
                    String Name = edtMotherName.getText().toString();
                    openImageDialog(Name, mother_url);
                }
            }
        });


        rbtnM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    rbtnF.setChecked(false);
                    gender = "Male";
                }
            }
        });

        rbtnF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtnM.setChecked(false);
                    gender = "Female";
                }
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
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                                String time = hourString + ":" + minuteString;
                                edtbTime.setText(time);
                            }
                        });
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || AppController.isAdmin) {
                            tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
                        }

                        return true;
                    }
                }
                return false;
            }
        });

        edtbdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtbdate.getRight() - edtbdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
                                // String date = str_day + "/" + str_month + "/" + year;
                                String date = year + "-" + str_month + "-" + str_day;
                                edtbdate.setText(date);
                            }
                        });
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || AppController.isAdmin) {
                            dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                        }

                        return true;
                    }
                }
                return false;
            }
        });

        edtAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtAddress.getRight() - edtAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (MainActivity.lat == null && MainActivity.lon == null) {
                            Common.showSettingsAlert(getActivity());

                        } else {

                            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(getString(R.string.app_name));

                                builder.setMessage(getString(R.string.home_location));
                                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (MainActivity.lat != null && MainActivity.lon != null) {
                                            homeLocationUpdateWS();
                                        } else {
                                            Toast.makeText(getActivity(), "You need to give permission to access location ! ", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();

                            } else {
                                double lat = Double.valueOf(MainActivity.lat);
                                double lng = Double.valueOf(MainActivity.lon);
                                if (lat != 0 && lng != 0) {
                                    showDirections(lat, lng, edtAddress.getText().toString());
                                }
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        tbtn_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (Common.isOnline(getActivity())) {
                    if (isChecked) {
                        mEditor.putBoolean(Common.Constant_Class.TBTN_SHARE, true);
                        mEditor.commit();
                        getActivity().startService(new Intent(getActivity(), MyLocationService.class));
                    } else {
                        mEditor.putBoolean(Common.Constant_Class.TBTN_SHARE, false);
                        mEditor.commit();
                        getActivity().stopService(new Intent(getActivity(), MyLocationService.class));
                    }
                } else {
                    Toast.makeText(getActivity(), Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
                    tbtn_share.setChecked(!isChecked);
                }
            }
        });

        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            EnableAll();
        } else {
            if (!AppController.isAdmin) {
                DisableAll();
            }
        }

        return rootView;
    }

    private void openImageDialog(String name, String url) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle(name);

        ImageView image = dialog.findViewById(R.id.img_dialog);
        Glide.with(getActivity()).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(image);

        // new Common.ImageLoadTask(url, image).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        dialog.show();
    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void showDirections(double latitude, double longitude, String address) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)&daddr=%f,%f (%s)", latitude, longitude, "", home_lat, home_lng, address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    private void MemoryAllocation(View rootView) {


        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        user_id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
        edtFName = rootView.findViewById(R.id.edtFName);
        edtLName = rootView.findViewById(R.id.edtLName);
        edtFatherName = rootView.findViewById(R.id.edtFatherName);
        edtMotherName = rootView.findViewById(R.id.edtMotherName);
        edtbdate = rootView.findViewById(R.id.edtbdate);
        edtbTime = rootView.findViewById(R.id.edtbTime);
        edtEducation = rootView.findViewById(R.id.edtEducation);
        edtBPlace = rootView.findViewById(R.id.edtBPlace);
        edtNPlace = rootView.findViewById(R.id.edtNPlace);
        edtGotra = rootView.findViewById(R.id.edtGotra);
        edtMobile = rootView.findViewById(R.id.edtMobile);
        edt_Eaddress = rootView.findViewById(R.id.edt_Eaddress);
        edtCity = rootView.findViewById(R.id.edt_City);

        txt_home = rootView.findViewById(R.id.txt_home);
        edtAddress = rootView.findViewById(R.id.edtAddress);
        edt_phone = rootView.findViewById(R.id.edt_phone);
        img_profile = rootView.findViewById(R.id.img_profile);
        img_father = rootView.findViewById(R.id.img_father);
        img_mother = rootView.findViewById(R.id.img_mother);
        tbtn_share = rootView.findViewById(R.id.tbtn_share);

        /*if (mSharedPreferences.getBoolean(Common.Constant_Class.OFFLINE_SP, false)) {
            img_profile.setVisibility(View.GONE);
            img_father.setVisibility(View.GONE);
            img_mother.setVisibility(View.GONE);
        } else {
            img_profile.setVisibility(View.VISIBLE);
            img_father.setVisibility(View.VISIBLE);
            img_mother.setVisibility(View.VISIBLE);
        }*/

        rbtnM = rootView.findViewById(R.id.rbtnM);
        rbtnM.setChecked(true);
        rbtnF = rootView.findViewById(R.id.rbtnF);

        spinnerBlood = rootView.findViewById(R.id.spinnerBlood);
        spinnerBlood.setOnItemSelectedListener(this);


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

    private void EnableAll() {
        edtFName.setEnabled(true);
        edtLName.setEnabled(true);
        edtFatherName.setEnabled(true);
        edtMotherName.setEnabled(true);
        edtEducation.setEnabled(true);
        edtBPlace.setEnabled(true);
        edtNPlace.setEnabled(true);
        edtGotra.setEnabled(true);
        edtCity.setEnabled(true);
        edt_Eaddress.setEnabled(true);
        edtMobile.setEnabled(true);
        edtAddress.setEnabled(true);
        edt_phone.setEnabled(true);
        edtbdate.setEnabled(true);
        edtbTime.setEnabled(true);

        rbtnM.setEnabled(true);
        rbtnF.setEnabled(true);
        spinnerBlood.setEnabled(true);
    }

    private void DisableAll() {

        edtFName.setKeyListener(null);
        edtFName.setCursorVisible(false);

        edtLName.setKeyListener(null);
        edtLName.setCursorVisible(false);

        edtFatherName.setKeyListener(null);
        edtFatherName.setCursorVisible(false);

        edtMotherName.setKeyListener(null);
        edtMotherName.setCursorVisible(false);

        edtEducation.setKeyListener(null);
        edtEducation.setCursorVisible(false);

        edtBPlace.setKeyListener(null);
        edtBPlace.setCursorVisible(false);

        edtNPlace.setKeyListener(null);
        edtNPlace.setCursorVisible(false);

        edtGotra.setKeyListener(null);
        edtGotra.setCursorVisible(false);

        edtMobile.setKeyListener(null);
        edtMobile.setCursorVisible(false);

        edt_Eaddress.setKeyListener(null);
        edt_Eaddress.setCursorVisible(false);

        edtAddress.setKeyListener(null);
        edtAddress.setCursorVisible(false);

        edt_phone.setKeyListener(null);


        edt_phone.setCursorVisible(false);

        edtbTime.setKeyListener(null);
        edtbTime.setCursorVisible(false);

        edtbdate.setKeyListener(null);
        edtbdate.setCursorVisible(false);

        edtCity.setKeyListener(null);
        edtCity.setCursorVisible(false);

        rbtnM.setKeyListener(null);
        rbtnF.setKeyListener(null);

        spinnerBlood.setEnabled(false);
    }

    private void setOfflineData(RealmList<ListProfileData> mListProfileDatas) {

        if (mListProfileDatas.size() > 0) {
            ListProfileData mListProfileData = mListProfileDatas.get(0);

            edtFName.setText(mListProfileData.getFirst_name());
            edtLName.setText(mListProfileData.getLast_name());
            edtFatherName.setText(mListProfileData.getFather_name());
            edtMotherName.setText(mListProfileData.getMother_name());
            edtbdate.setText(mListProfileData.getBirth_date());
            edtBPlace.setText(mListProfileData.getBirth_place());
            String str_time = mListProfileData.getBirth_time();
            if (str_time.length() > 5) {
                str_time = mListProfileData.getBirth_time().substring(0, 5);
            }
            edtbTime.setText(str_time);
            edtMobile.setText(mListProfileData.getMobile());
            edt_phone.setText(mListProfileData.getPhone());
            edtCity.setText(mListProfileData.getCity());
            edtGotra.setText(mListProfileData.getGotra());
            edtNPlace.setText(mListProfileData.getNative_place());
            edtEducation.setText(mListProfileData.getEducation());
            edt_Eaddress.setText(mListProfileData.getEmail_address());
            edtAddress.setText(mListProfileData.getAddress());
            String blood = mListProfileData.getBlood_group();

            if (blood.equalsIgnoreCase(Common.Constant_Class.A_POSITIVE)) {
                spinnerBlood.setSelection(1);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.A_NAGATIVE)) {
                spinnerBlood.setSelection(2);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_POSITIVE)) {
                spinnerBlood.setSelection(3);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_NAGATIVE)) {
                spinnerBlood.setSelection(4);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_POSITIVE)) {
                spinnerBlood.setSelection(5);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_NAGATIVE)) {
                spinnerBlood.setSelection(6);
            }
            if (mListProfileData.getGender().equalsIgnoreCase("male") || mListProfileData.getGender().equalsIgnoreCase("")) {
                rbtnF.setChecked(false);
                rbtnM.setChecked(true);
                gender="Male";

            } else {
                rbtnF.setChecked(true);
                rbtnM.setChecked(false);
                gender="Female";
            }

            if (!mListProfileData.getUser_lat().equalsIgnoreCase("null") && !mListProfileData.getUser_lat().equalsIgnoreCase("")) {
                user_lat = Double.parseDouble(mListProfileData.getUser_lat());
            }
            if (!mListProfileData.getUser_lng().equalsIgnoreCase("null") && !mListProfileData.getUser_lng().equalsIgnoreCase("")) {
                user_lng = Double.parseDouble(mListProfileData.getUser_lng());
            }
            if (!mListProfileData.getHome_lat().equalsIgnoreCase("null") && !mListProfileData.getHome_lat().equalsIgnoreCase("")) {
                home_lat = Double.parseDouble(mListProfileData.getHome_lat());
            }
            if (!mListProfileData.getHome_lng().equalsIgnoreCase("null") && !mListProfileData.getHome_lng().equalsIgnoreCase("")) {
                home_lng = Double.parseDouble(mListProfileData.getHome_lng());
            }

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false)) {

                EnableAll();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("My Profile");
                mEditor.putString(Common.Constant_Class.PROFILE_PIC_URL, mListProfileData.getProfile_pic_url());
                mEditor.putString(Common.Constant_Class.FIRST_NAME, mListProfileData.getFirst_name());
                mEditor.putString(Common.Constant_Class.LAST_NAME, mListProfileData.getLast_name());
                mEditor.commit();

                tbtn_share.setVisibility(View.VISIBLE);
                tbtn_share.setText(null);
                tbtn_share.setTextOn(null);
                tbtn_share.setTextOff(null);
                boolean bool = mSharedPreferences.getBoolean(Common.Constant_Class.TBTN_SHARE, false);
                tbtn_share.setChecked(bool);


                AppController.getInstance().firebaseAnalytics.setUserProperty("Name", edtFName.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Father Name", edtFatherName.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Mother Name", edtMotherName.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Mobile", edtMobile.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Email Address", edt_Eaddress.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Home Address", edtAddress.getText().toString());



                /*if (mSharedPreferences.getBoolean(Common.Constant_Class.TBTN_SHARE_SP, false)) {
                    txt_distance.setVisibility(View.VISIBLE);

                    if (user_lat != 0 && user_lng != 0) {
                        int distance = (int) Common.getDistance(getActivity(), user_lat, user_lng);
                        if (distance == -1) {
                            txt_distance.setText("Need to enable location");
                        } else {
                            txt_distance.setText("" + (distance / 1000) + " Km");
                        }
                    } else {
                        txt_distance.setText("User has not set location");
                    }
                }*/

            } else {
                if (!AppController.isAdmin) {
                    DisableAll();
                }
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(name + " Profile");
                if (home_lat != 0 && home_lng != 0) {
                    int distance = (int) Common.getDistance(getActivity(), home_lat, home_lng);
                    if (distance == -1) {
                        txt_home.setText("Need to enable location");
                    } else {
                        txt_home.setText("" + (distance / 1000) + " Km");
                    }
                } else {
                    txt_home.setText("User has not set location");
                }
                tbtn_share.setVisibility(View.GONE);
            }

            profile_url = mListProfileData.getProfile_pic_url();
            father_url = mListProfileData.getImg_father_url();
            mother_url = mListProfileData.getImg_mother_url();

            Glide.with(getActivity()).load(profile_url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_profile);
            Glide.with(getActivity()).load(father_url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_father);
            Glide.with(getActivity()).load(mother_url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_mother);


        } else {
            Toast.makeText(getActivity(), "No Record Found !!", Toast.LENGTH_SHORT).show();
        }
    }

/*
    private void setOnlineData(String data) {
        try {
            JSONObject mData = new JSONObject(data);
            name = mData.getString(Common.Constant_Class.FIRST_NAME) + " " + mData.getString(Common.Constant_Class.LAST_NAME);

            edtFName.setText(mData.getString(Common.Constant_Class.FIRST_NAME));
            edtLName.setText(mData.getString(Common.Constant_Class.LAST_NAME));
            edtFatherName.setText(mData.getString(Common.Constant_Class.FATHER_NAME));
            edtMotherName.setText(mData.getString(Common.Constant_Class.MOTHER_NAME));
            edtbdate.setText(mData.getString(Common.Constant_Class.BIRTH_DATE));
            edtBPlace.setText(mData.getString(Common.Constant_Class.BIRTH_PLACE));
            String str_time = mData.getString(Common.Constant_Class.BIRTH_TIME);
            if (str_time.length() > 5) {
                str_time = str_time.substring(0, 5);
            }
            edtbTime.setText(str_time);
            // edtbTime.setText(mData.getString(Common.Constant_Class.BIRTH_TIME));
            edtMobile.setText(mData.getString(Common.Constant_Class.MOBILE));
            edt_phone.setText(mData.getString(Common.Constant_Class.PHONE));
            edtGotra.setText(mData.getString(Common.Constant_Class.GOTRA));
            edtCity.setText(mData.getString(Common.Constant_Class.CITY));
            edtNPlace.setText(mData.getString(Common.Constant_Class.NATIVE_PLACE));
            edtEducation.setText(mData.getString(Common.Constant_Class.EDUCATION));
            edt_Eaddress.setText(mData.getString(Common.Constant_Class.EMAIL_ADDRESS));
            edtAddress.setText(mData.getString(Common.Constant_Class.ADDRESS));
            String blood = mData.getString(Common.Constant_Class.BLOOD_GROUP);

            if (blood.equalsIgnoreCase(Common.Constant_Class.A_POSITIVE)) {
                spinnerBlood.setSelection(1);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.A_NAGATIVE)) {
                spinnerBlood.setSelection(2);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_POSITIVE)) {
                spinnerBlood.setSelection(3);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_NAGATIVE)) {
                spinnerBlood.setSelection(4);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_POSITIVE)) {
                spinnerBlood.setSelection(5);
            } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_NAGATIVE)) {
                spinnerBlood.setSelection(6);
            }
            if (mData.getString(Common.Constant_Class.GENDER).equalsIgnoreCase("1")) {
                rbtnF.setChecked(false);
                rbtnM.setChecked(true);

            } else {
                rbtnF.setChecked(true);
                rbtnM.setChecked(false);
            }

            String _user_lat = mData.getString(Common.Constant_Class.USER_LAT).toString();
            if (!_user_lat.isEmpty() && !_user_lat.equalsIgnoreCase("null")) {
                user_lat = Double.parseDouble(mData.getString(Common.Constant_Class.USER_LAT));
            }
            String _user_lng = mData.getString(Common.Constant_Class.USER_LNG).toString();
            if (!_user_lng.isEmpty() && !_user_lng.equalsIgnoreCase("null")) {
                user_lng = Double.parseDouble(mData.getString(Common.Constant_Class.USER_LNG));
            }


            String _home_lat = mData.getString(Common.Constant_Class.HOME_LAT).toString();
            if (!_home_lat.isEmpty() && !_home_lat.equalsIgnoreCase("null")) {
                home_lat = Double.parseDouble(mData.getString(Common.Constant_Class.HOME_LAT));
            }

            String _home_lng = mData.getString(Common.Constant_Class.HOME_LNG).toString();
            if (!_home_lng.isEmpty() && !_home_lng.equalsIgnoreCase("null")) {
                home_lng = Double.parseDouble(mData.getString(Common.Constant_Class.HOME_LNG));
            }

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("My Profile");
                mEditor.putString(Common.Constant_Class.PROFILE_PIC_URL, mData.getString(Common.Constant_Class.PROFILE_PIC_URL));
                mEditor.putString(Common.Constant_Class.FIRST_NAME, mData.getString(Common.Constant_Class.FIRST_NAME));
                mEditor.putString(Common.Constant_Class.LAST_NAME, mData.getString(Common.Constant_Class.LAST_NAME));
                mEditor.commit();

                tbtn_share.setVisibility(View.VISIBLE);
                tbtn_share.setText(null);
                tbtn_share.setTextOn(null);
                tbtn_share.setTextOff(null);
                boolean bool = mSharedPreferences.getBoolean(Common.Constant_Class.TBTN_SHARE_SP, false);
                tbtn_share.setChecked(bool);

                AppController.getInstance().firebaseAnalytics.setUserProperty("Name", edtFName.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Father Name", edtFatherName.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Mother Name", edtMotherName.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Mobile", edtMobile.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Email Address", edt_Eaddress.getText().toString());
                AppController.getInstance().firebaseAnalytics.setUserProperty("Home Address", edtAddress.getText().toString());


            } else {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(name + " Profile");
                if (home_lat != 0 && home_lng != 0) {
                    int distance = (int) Common.getDistance(getActivity(), home_lat, home_lng);
                    if (distance == -1) {
                        txt_home.setText("Need to enable location");
                    } else {
                        txt_home.setText("" + (distance / 1000) + " Km");
                    }
                } else {
                    txt_home.setText("User has not set location");
                }

                tbtn_share.setVisibility(View.GONE);
                if (mSharedPreferences.getBoolean(Common.Constant_Class.TBTN_SHARE_SP, false)) {
                    txt_distance.setVisibility(View.VISIBLE);

                    if (user_lat != 0 && user_lng != 0) {
                        int distance = (int) Common.getDistance(getActivity(), user_lat, user_lng);
                        if (distance == -1) {
                            txt_distance.setText("Need to enable location");
                        } else {
                            txt_distance.setText("" + (distance / 1000) + " Km");
                        }
                    } else {
                        txt_distance.setText("User has not set location");
                    }
                }

            }

            profile_url = mData.getString(Common.Constant_Class.PROFILE_PIC_URL);
            father_url = mData.getString(Common.Constant_Class.IMG_FATHER_URL);
            mother_url = mData.getString(Common.Constant_Class.IMG_MOTHER_URL);

            Glide.with(getActivity()).load(profile_url).thumbnail(0.5f).into(img_profile);
            Glide.with(getActivity()).load(father_url).thumbnail(0.5f).into(img_father);
            Glide.with(getActivity()).load(mother_url).thumbnail(0.5f).into(img_mother);

            //     new Common.ImageLoadTask(profile_url, img_profile).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //     new Common.ImageLoadTask(father_url, img_father).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //     new Common.ImageLoadTask(mother_url, img_mother).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/

    private void homeLocationUpdateWS() {
        if (Common.isOnline(getActivity())) {
            JSONObject mJsonObject = null;
            try {
                double lat = Double.valueOf(MainActivity.lat);
                double lng = Double.valueOf(MainActivity.lon);
                mJsonObject = new JSONObject();
                if (lat != 0 && lng != 0) {
                    mJsonObject.put(Common.Constant_Class.HOME_LAT, lat);
                    mJsonObject.put(Common.Constant_Class.HOME_LNG, lng);
                    mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                    mJsonObject.put(Common.Constant_Class.IS_UPDATE, "1");
                    mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.PROFILE_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            alert("Home location updated!");
                        } else {
                            alert("Something went wrong!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
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
        }
    }

    private void alert(String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bmp = null;
        if (data != null) {

            if (data.getData() == null) {
                bmp = (Bitmap) data.getExtras().get("data");
            } else {
                Uri selectedImage = data.getData();
                try {
                    bmp = Common.scaleImage(getActivity(), selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bmp != null) {

                switch (requestCode) {
                    case 0:
                        if (resultCode == getActivity().RESULT_OK) {
                            setImageFromActivityResult(bmp);
                        }
                        break;
                    case 1:
                        if (resultCode == getActivity().RESULT_OK) {
                            setImageFromActivityResult(bmp);
                        }
                        break;
                }
            }
        }
    }

    private void setImageFromActivityResult(Bitmap bmp) {
        if (img_selection.equalsIgnoreCase("profile")) {
            //  img_profile.setImageBitmap(bmp);
            Glide.with(getActivity()).load(bmp).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_profile);
            str_profile_hash = Common.getBase64(getActivity(), bmp);
        } else if (img_selection.equalsIgnoreCase("father")) {
            // img_father.setImageBitmap(bmp);
            Glide.with(getActivity()).load(bmp).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_father);
            str_father_hash = Common.getBase64(getActivity(), bmp);

        } else if (img_selection.equalsIgnoreCase("mother")) {
            //img_mother.setImageBitmap(bmp);
            Glide.with(getActivity()).load(bmp).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_mother);
            str_mother_hash = Common.getBase64(getActivity(), bmp);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //  Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
