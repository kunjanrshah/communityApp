package com.krs.vastipatrak.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.realm.RealmList;

public class FamilyFragment extends Fragment implements Serializable,AdapterView.OnItemSelectedListener {


    public EditText edtSpouseName, edtSpouseFName, edtMSpouseName, edt_mdate;
    public String str_spouse_hash = "", str_fspouse_hash = "", str_mspouse_hash = "";
    public LinearLayout child_container = null;
    public ArrayList<Integer> lst_delID = null;
    public RadioButton rbtnChildYes, rbtnChildNo;
    String spouse_url = "", fspouse_url = "", mspouse_url = "";
    Button btn_add;
    ImageView img_spouse, img_fspouse, img_mspouse;
    String img_selection = "";
    SharedPreferences mSharedPreferences;
    String user_id = "";
    Activity mActivity;

    public FamilyFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_family, container, false);
        Memory_Allocation(rootView);

        try {
            RealmList<ListProfileData> mListProfileData = ((MyProfileActivity) Objects.requireNonNull(getActivity())).getMyData();
            if (mListProfileData != null) {
                SetOfflineData(mListProfileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        edt_mdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_mdate.getRight() - edt_mdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
                                edt_mdate.setText(date);
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

        img_spouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || AppController.isAdmin) {
                    img_selection = "spouse";
                    selectImage();
                } else {
                    String Name = edtSpouseName.getText().toString();
                    openImageDialog(Name, spouse_url);
                }

            }
        });

        img_fspouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || AppController.isAdmin) {
                    img_selection = "fspouse";
                    selectImage();
                } else {
                    String Name = edtSpouseFName.getText().toString();
                    openImageDialog(Name, fspouse_url);
                }

            }
        });

        img_mspouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || AppController.isAdmin) {
                    img_selection = "mspouse";
                    selectImage();
                } else {
                    String Name = edtMSpouseName.getText().toString();
                    openImageDialog(Name, mspouse_url);
                }

            }
        });

        rbtnChildYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    rbtnChildNo.setChecked(false);
                    btn_add.setVisibility(View.VISIBLE);
                    child_container.setVisibility(View.VISIBLE);
                }
            }
        });

        rbtnChildNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtnChildYes.setChecked(false);
                    btn_add.setVisibility(View.GONE);
                    child_container.setVisibility(View.GONE);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add_child_layout();
            }
        });

        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            if (!AppController.isAdmin) {
                DisableAll();
            }
        }

        return rootView;
    }

    private void Memory_Allocation(View root) {

        mActivity = Objects.requireNonNull(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        user_id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");

        edt_mdate = root.findViewById(R.id.edt_mdate);
        edtSpouseName = root.findViewById(R.id.edtSpouseName);
        edtSpouseFName = root.findViewById(R.id.edtSpouseFName);
        edtMSpouseName = root.findViewById(R.id.edtMSpouseName);
        img_spouse = root.findViewById(R.id.img_spouse);
        img_fspouse = root.findViewById(R.id.img_fspouse);
        img_mspouse = root.findViewById(R.id.img_mspouse);
        btn_add = root.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.GONE);
        rbtnChildYes = root.findViewById(R.id.rbtnChildYes);
        rbtnChildNo = root.findViewById(R.id.rbtnChildNo);
        rbtnChildNo.setChecked(true);
        child_container = root.findViewById(R.id.child_container);
        lst_delID = new ArrayList<>();
    }

    private void DisableAll() {
        edt_mdate.setKeyListener(null);
        edt_mdate.setCursorVisible(false);

        edtSpouseName.setKeyListener(null);
        edtSpouseName.setCursorVisible(false);

        edtSpouseFName.setKeyListener(null);
        edtSpouseFName.setCursorVisible(false);

        edtMSpouseName.setKeyListener(null);
        edtMSpouseName.setCursorVisible(false);

        /*img_spouse.setEnabled(false);
        img_fspouse.setEnabled(false);
        img_mspouse.setEnabled(false);*/
        btn_add.setVisibility(View.GONE);
        rbtnChildYes.setEnabled(false);
        rbtnChildNo.setEnabled(false);

    }

    private void EnableAll() {
        edt_mdate.setEnabled(true);
        edtSpouseName.setEnabled(true);
        edtSpouseFName.setEnabled(true);
        edtMSpouseName.setEnabled(true);
    }

    private void SetOfflineData(RealmList<ListProfileData> mListProfileDatas) {

        if (mListProfileDatas.size() > 0) {

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || AppController.isAdmin) {
                EnableAll();
            } else {
                DisableAll();
            }

            ListProfileData mListProfileData = mListProfileDatas.get(0);

            edtSpouseName.setText(Objects.requireNonNull(mListProfileData).getSpouse_name());
            edt_mdate.setText(mListProfileData.getMarriage_date());
            edtSpouseFName.setText(mListProfileData.getSfather_name());
            edtMSpouseName.setText(mListProfileData.getSmother_name());

            spouse_url = mListProfileData.getImg_spouse_url();
            fspouse_url = mListProfileData.getImg_sfather_url();
            mspouse_url = mListProfileData.getImg_smother_url();
            Glide.with(mActivity).load(spouse_url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_spouse);
            Glide.with(mActivity).load(fspouse_url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_fspouse);
            Glide.with(mActivity).load(mspouse_url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_mspouse);

            if (mListProfileData.getmListChildrenData() != null) {
                if (mListProfileData.getmListChildrenData().size() > 0) {

                    for (int i = 0; i < mListProfileData.getmListChildrenData().size(); i++) {

                        if (i == 0) {
                            rbtnChildYes.setChecked(true);
                            rbtnChildNo.setChecked(false);
                            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || AppController.isAdmin) {
                                btn_add.setVisibility(View.VISIBLE);
                            } else {
                                btn_add.setVisibility(View.GONE);
                            }
                        }
                        add_child_layout();
                        final Viewholder mViewholder = (Viewholder) child_container.getChildAt(i).getTag();
                        ListChildrenData mObjChild = mListProfileData.getmListChildrenData().get(i);
                        mViewholder.child_id = Integer.parseInt(Objects.requireNonNull(mObjChild).getChild_id());
                        Objects.requireNonNull(mViewholder.edtchild_name).setText(mObjChild.getChild_name());
                        Objects.requireNonNull(mViewholder.edtchild_bdate).setText(mObjChild.getChild_bday());
                        Objects.requireNonNull(mViewholder.edtMobile).setText(mObjChild.getMobile());
                        String blood=mObjChild.getBlood_group();
                        if (blood != null && !blood.isEmpty())
                        {
                            if (blood.equalsIgnoreCase(Common.Constant_Class.A_POSITIVE)) {
                                Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(1);
                            } else if (blood.equalsIgnoreCase(Common.Constant_Class.A_NAGATIVE)) {
                                Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(2);
                            } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_POSITIVE)) {
                                Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(3);
                            } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_NAGATIVE)) {
                                Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(4);
                            } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_POSITIVE)) {
                                Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(5);
                            } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_NAGATIVE)) {
                                Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(6);
                            }
                        }

                        Objects.requireNonNull(mViewholder.edtchild_btime).setText(mObjChild.getBirth_time());
                        Objects.requireNonNull(mViewholder.edtchild_bplace).setText(mObjChild.getBirth_place());
                        Objects.requireNonNull(mViewholder.tbtn_interest).setChecked(mObjChild.isInterest());

                        if (mObjChild.getGender().equalsIgnoreCase("male")) {
                            Objects.requireNonNull(mViewholder.radioGroupId).check(R.id.radioM);
                        } else {
                            Objects.requireNonNull(mViewholder.radioGroupId).check(R.id.radioF);
                        }

                        Objects.requireNonNull(mViewholder.edtchild_edu).setText(mObjChild.getChild_edu());
                        Objects.requireNonNull(mViewholder.edtchild_work).setText(mObjChild.getChild_work());


                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false)) {
                            mViewholder.edtchild_name.setEnabled(true);
                            mViewholder.edtchild_bdate.setEnabled(true);
                            mViewholder.edtchild_edu.setEnabled(true);
                            mViewholder.edtchild_work.setEnabled(true);
                            Objects.requireNonNull(mViewholder.img_child).setEnabled(true);
                            mViewholder.img_child.setClickable(true);
                        } else {
                            mViewholder.edtchild_name.setKeyListener(null);
                            mViewholder.edtchild_name.setCursorVisible(false);

                            mViewholder.edtchild_bdate.setKeyListener(null);
                            mViewholder.edtchild_bdate.setCursorVisible(false);

                            mViewholder.edtchild_edu.setKeyListener(null);
                            mViewholder.edtchild_edu.setCursorVisible(false);

                            mViewholder.edtchild_work.setKeyListener(null);
                            mViewholder.edtchild_work.setCursorVisible(false);
                            Objects.requireNonNull(mViewholder.img_child).setEnabled(false);
                            mViewholder.img_child.setClickable(false);
                        }
                        final String child_url = mObjChild.getChild_img_url();
                        Glide.with(mActivity).load(child_url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(mViewholder.img_child);
                        mViewholder.img_child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || AppController.isAdmin) {
                                    mViewholder.ImgHash = "selectImage";
                                    selectImage();
                                } else {
                                    String Name = mViewholder.edtchild_name.getText().toString();
                                    openImageDialog(Name, child_url);
                                }
                            }
                        });
                    }
                }
            }
        }
    }


    private void openImageDialog(String name, String url) {
        Dialog dialog = new Dialog(mActivity);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle(name);

        ImageView image = dialog.findViewById(R.id.img_dialog);
        Glide.with(mActivity).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(image);
        dialog.show();
    }

    private void add_child_layout() {
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View addView = Objects.requireNonNull(layoutInflater).inflate(R.layout.child_row, null);
        final Viewholder mViewholder = new Viewholder();
        mViewholder.child_id = 0;
        mViewholder.img_child = addView.findViewById(R.id.img_child);
        mViewholder.edtchild_name = addView.findViewById(R.id.edtchild_name);
        mViewholder.edtMobile = addView.findViewById(R.id.edtMobile);
        mViewholder.spinnerBlood = addView.findViewById(R.id.spinnerBlood);
        mViewholder.radioGroupId = addView.findViewById(R.id.radioGroupId);
        mViewholder.tbtn_interest = addView.findViewById(R.id.tbtn_interest);
        mViewholder.edtchild_bdate = addView.findViewById(R.id.edtchild_bdate);
        mViewholder.edtchild_btime = addView.findViewById(R.id.edtchild_btime);
        mViewholder.edtchild_bplace = addView.findViewById(R.id.edtchild_bplace);
        mViewholder.edtchild_edu = addView.findViewById(R.id.edtchild_edu);
        mViewholder.edtchild_work = addView.findViewById(R.id.edtchild_work);
        mViewholder.edtchild_work.requestFocus();
        Objects.requireNonNull(mViewholder.tbtn_interest).setText(null);
        mViewholder.tbtn_interest.setTextOn(null);
        mViewholder.tbtn_interest.setTextOff(null);

        Objects.requireNonNull(mViewholder.spinnerBlood).setOnItemSelectedListener(this);


        List<String> blood_cate = new ArrayList<>();
        blood_cate.add(Common.Constant_Class.TITLE_BLOOD_GROUP);
        blood_cate.add(Common.Constant_Class.A_POSITIVE);
        blood_cate.add(Common.Constant_Class.A_NAGATIVE);
        blood_cate.add(Common.Constant_Class.B_POSITIVE);
        blood_cate.add(Common.Constant_Class.B_NAGATIVE);
        blood_cate.add(Common.Constant_Class.O_POSITIVE);
        blood_cate.add(Common.Constant_Class.O_NAGATIVE);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, blood_cate);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViewholder.spinnerBlood.setAdapter(dataAdapter);


        mViewholder.btn_remove = addView.findViewById(R.id.btn_remove);
        mViewholder.ImgHash = "";
        mViewholder.setClickBDate = false;

        assert mViewholder.radioGroupId != null;
        mViewholder.radioGroupId.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioM) {
                    mViewholder.gender = "male";
                } else if (checkedId == R.id.radioF) {
                    mViewholder.gender = "female";
                }
            }
        });

        assert mViewholder.img_child != null;
        mViewholder.img_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || AppController.isAdmin) {
                    mViewholder.ImgHash = "selectImage";
                    selectImage();
                }
            }
        });

        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false)) {

            if (!AppController.isAdmin) {

                Objects.requireNonNull(mViewholder.btn_remove).setVisibility(View.GONE);
                Objects.requireNonNull(mViewholder.edtchild_name).setKeyListener(null);
                mViewholder.edtchild_name.setCursorVisible(false);

                Objects.requireNonNull(mViewholder.edtMobile).setKeyListener(null);
                mViewholder.edtMobile.setCursorVisible(false);
                mViewholder.spinnerBlood.setEnabled(false);
                Objects.requireNonNull(mViewholder.edtchild_bdate).setKeyListener(null);
                mViewholder.edtchild_bdate.setCursorVisible(false);

                Objects.requireNonNull(mViewholder.edtchild_edu).setKeyListener(null);
                mViewholder.edtchild_edu.setCursorVisible(false);

                mViewholder.edtchild_work.setKeyListener(null);
                mViewholder.edtchild_work.setCursorVisible(false);

                Objects.requireNonNull(mViewholder.edtchild_bplace).setKeyListener(null);
                mViewholder.edtchild_bplace.setCursorVisible(false);

                Objects.requireNonNull(mViewholder.edtchild_btime).setKeyListener(null);
                mViewholder.edtchild_btime.setCursorVisible(false);

                mViewholder.tbtn_interest.setEnabled(false);
                mViewholder.radioGroupId.setEnabled(false);

            }
        }

        mViewholder.edtchild_bdate.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mViewholder.edtchild_bdate.getRight() - mViewholder.edtchild_bdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mViewholder.setClickBDate = true;
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

                                monthOfYear = (++monthOfYear);
                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = year + "-" + str_month + "-" + str_day;
                                mViewholder.edtchild_bdate.setText(date);
                            }
                        });
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || AppController.isAdmin) {
                            dpd.show(mActivity.getFragmentManager(), "Datepickerdialog");
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mViewholder.edtchild_btime.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mViewholder.edtchild_btime.getRight() - mViewholder.edtchild_btime.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        TimePickerDialog tpd = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) getContext(), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                        tpd.setThemeDark(true);
                        tpd.vibrate(true);
                        tpd.dismissOnPause(false);
                        tpd.enableSeconds(false);
                        tpd.setTitle("Birth Time");
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
                                mViewholder.edtchild_btime.setText(time);
                            }
                        });
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || AppController.isAdmin) {
                            tpd.show(mActivity.getFragmentManager(), "Timepickerdialog");
                        }
                        return true;
                    }
                }

                return false;
            }
        });
        mViewholder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getString(R.string.app_name));

                builder.setMessage("Do you want to delete this child ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {

                        int cid = mViewholder.child_id;
                        lst_delID.add(cid);
                        ((LinearLayout) addView.getParent()).removeView(addView);

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


            }
        });
        child_container.addView(addView);
        addView.setTag(mViewholder);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (Common.canCAMARA(mActivity)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                    } else {
                        requestPermissions(MainActivity.CALL_CAMARA, MainActivity.CAMARA_REQUEST);
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bmp = null;
        if (data != null) {
            if (data.getData() == null) {
                bmp = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            } else {
                Uri selectedImage = data.getData();
                try {
                    bmp = Common.scaleImage(mActivity, selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bmp != null) {

                switch (requestCode) {
                    case 0:
                        if (resultCode == Activity.RESULT_OK) {
                            setImageFromActivityResult(bmp);
                        }
                        break;
                    case 1:
                        if (resultCode == Activity.RESULT_OK) {
                            setImageFromActivityResult(bmp);
                        }
                        break;
                }
            }
        }
    }

    private void setImageFromActivityResult(@NonNull Bitmap bmp) {

        if (img_selection.equalsIgnoreCase("spouse")) {
            //    img_spouse.setImageBitmap(bmp);
            Glide.with(mActivity).load(bmp).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_spouse);
            str_spouse_hash = Common.getBase64(mActivity, bmp);
        } else if (img_selection.equalsIgnoreCase("fspouse")) {
            //  img_fspouse.setImageBitmap(bmp);
            Glide.with(mActivity).load(bmp).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_fspouse);
            str_fspouse_hash = Common.getBase64(mActivity, bmp);
        } else if (img_selection.equalsIgnoreCase("mspouse")) {
            //img_mspouse.setImageBitmap(bmp);
            Glide.with(mActivity).load(bmp).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_mspouse);
            str_mspouse_hash = Common.getBase64(mActivity, bmp);
        }
        img_selection = "";
        for (int i = 0; i < child_container.getChildCount(); i++) {
            Viewholder cViewholder = (Viewholder) child_container.getChildAt(i).getTag();
            if (cViewholder.ImgHash.equalsIgnoreCase("selectImage")) {
                Glide.with(mActivity).load(bmp).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(Objects.requireNonNull(cViewholder.img_child));
                cViewholder.ImgHash = Common.getBase64(mActivity, bmp);
                break;
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class Viewholder {
        @Nullable
        public ImageView img_child = null;
        public int child_id;
        @Nullable
        public EditText edtchild_name = null;
        @Nullable
        public EditText edtMobile=null;
        @Nullable
        public Spinner spinnerBlood=null;
        @Nullable
        public EditText edtchild_bdate = null;
        @Nullable
        public EditText edtchild_btime = null;
        @Nullable
        public EditText edtchild_bplace = null;
        @Nullable
        public RadioGroup radioGroupId = null;
        @NonNull
        public String gender = "male";
        @Nullable
        public ToggleButton tbtn_interest = null;
        @Nullable
        public EditText edtchild_edu = null;
        @Nullable
        public EditText edtchild_work = null;
        @Nullable
        public Button btn_remove = null;
        public String ImgHash = "";
        public boolean setClickBDate = false;
    }
}
