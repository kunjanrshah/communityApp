package com.krs.community.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.krs.community.R;
import com.krs.community.fragments.SmartFilterResult;
import com.krs.community.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartPopUpAdapter extends BaseAdapter {

    ICloseDialog mICloseDialog;
    private Context _context;
    private HashMap<String, String> mapChildValues;

    public SmartPopUpAdapter(Context _context, SmartFilterAdapter adapter, HashMap<String, String> stringHashMap) {
        this._context = _context;
        this.mapChildValues = stringHashMap;
        mICloseDialog = adapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PopUpViewHolder viewHolder;

        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_smart_popup, null);
            viewHolder = new PopUpViewHolder(convertView);
            convertView.setTag(viewHolder);
            for (Map.Entry<String, String> entry : mapChildValues.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().trim();
                System.out.println(entry.getKey() + " = " + entry.getValue());

                if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_family_code))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_family_code.setVisibility(View.VISIBLE);
                        viewHolder.edt_family_code.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_head_name))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_head.setVisibility(View.VISIBLE);
                        viewHolder.edt_head_name.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_mem_name))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_member.setVisibility(View.VISIBLE);
                        viewHolder.edt_member.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_surname))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_surname))) {
                        List<String> lst_surname = new ArrayList<>();
                        lst_surname.add(_context.getString(R.string.ss_surname));
                        lst_surname.add("surname1");
                        lst_surname.add("surname2");
                        ArrayAdapter<String> surnameAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_surname);
                        surnameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_surname.setAdapter(surnameAdapter);
                        viewHolder.ll_surname.setVisibility(View.VISIBLE);
                        viewHolder.sp_surname.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_samaj))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_samaj))) {
                        List<String> lst_samaj = new ArrayList<>();
                        lst_samaj.add(_context.getString(R.string.ss_samaj));
                        lst_samaj.add("samaj1");
                        lst_samaj.add("samaj2");
                        ArrayAdapter<String> samajAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_samaj);
                        samajAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_samaj.setAdapter(samajAdapter);
                        viewHolder.ll_samaj.setVisibility(View.VISIBLE);
                        viewHolder.sp_samaj.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_marital))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_marital))) {
                        List<String> lst_marital = new ArrayList<>();
                        lst_marital.add(_context.getString(R.string.ss_marital));
                        lst_marital.add("marital1");
                        lst_marital.add("marital2");

                        ArrayAdapter<String> maritalAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_marital);
                        maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_marital.setAdapter(maritalAdapter);
                        viewHolder.ll_marital.setVisibility(View.VISIBLE);
                        viewHolder.sp_marital.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_city))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_city))) {
                        List<String> lst_city = new ArrayList<>();
                        lst_city.add(_context.getString(R.string.ss_city));
                        lst_city.add("city1");
                        lst_city.add("city2");
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_city);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_city.setAdapter(cityAdapter);
                        viewHolder.ll_city.setVisibility(View.VISIBLE);
                        viewHolder.sp_city.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_gender))) {

                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_gender))) {
                        List<String> lst_gender = new ArrayList<>();
                        lst_gender.add(_context.getString(R.string.ss_gender));
                        lst_gender.add("Male");
                        lst_gender.add("Female");

                        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_gender);
                        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_gender.setAdapter(genderAdapter);
                        viewHolder.ll_gender.setVisibility(View.VISIBLE);
                        viewHolder.sp_gender.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_native))) {

                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_native))) {
                        List<String> lst_native = new ArrayList<>();
                        lst_native.add(_context.getString(R.string.ss_native));
                        lst_native.add("Native1");
                        lst_native.add("Native2");

                        ArrayAdapter<String> nativeAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_native);
                        nativeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_native.setAdapter(nativeAdapter);
                        viewHolder.ll_native.setVisibility(View.VISIBLE);
                        viewHolder.sp_native.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_minage))) {
                    if (!value.isEmpty()) {
                        viewHolder.rangeAgeBar.setMinStartValue(Integer.parseInt(value)).apply();
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_maxage))) {
                    if (!value.isEmpty()) {
                        viewHolder.rangeAgeBar.setMaxStartValue(Integer.parseInt(value)).apply();
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_email))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_email.setVisibility(View.VISIBLE);
                        viewHolder.edt_email.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_mobile))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_mobile.setVisibility(View.VISIBLE);
                        viewHolder.edt_mobile.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_local_add))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_local_add.setVisibility(View.VISIBLE);
                        viewHolder.edt_local_add.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_permanent_add))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_permanent_add.setVisibility(View.VISIBLE);
                        viewHolder.edt_permanent_add.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_chk_is_donor))) {
                    if (value.equalsIgnoreCase("true")) {
                        viewHolder.ll_isDonor.setVisibility(View.VISIBLE);
                        viewHolder.chk_is_donor.setChecked(true);
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_chk_is_expired))) {
                    if (value.equalsIgnoreCase("true")) {
                        viewHolder.ll_isExpired.setVisibility(View.VISIBLE);
                        viewHolder.chk_is_expired.setChecked(true);
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_chk_is_rented))) {
                    if (value.equalsIgnoreCase("true")) {
                        viewHolder.ll_isRented.setVisibility(View.VISIBLE);
                        viewHolder.chk_is_rented.setChecked(true);
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_pin_code))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_pincode.setVisibility(View.VISIBLE);
                        viewHolder.edt_pincode.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_area))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_area))) {
                        List<String> lst_area = new ArrayList<>();
                        lst_area.add(_context.getString(R.string.ss_area));
                        lst_area.add("Area1");
                        lst_area.add("Area2");

                        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_area);
                        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_area.setAdapter(areaAdapter);
                        viewHolder.ll_area.setVisibility(View.VISIBLE);
                        viewHolder.sp_area.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_state))) {

                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_state))) {
                        List<String> lst_state = new ArrayList<>();
                        lst_state.add(_context.getString(R.string.ss_gender));
                        lst_state.add("State1");
                        lst_state.add("State2");

                        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_state);
                        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_state.setAdapter(stateAdapter);
                        viewHolder.ll_state.setVisibility(View.VISIBLE);
                        viewHolder.sp_state.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_mosad))) {

                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_mosad))) {
                        List<String> lst_mosad = new ArrayList<>();
                        lst_mosad.add(_context.getString(R.string.ss_mosad));
                        lst_mosad.add("Mosad1");
                        lst_mosad.add("Mosad2");

                        ArrayAdapter<String> mosadAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_mosad);
                        mosadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_mosad.setAdapter(mosadAdapter);
                        viewHolder.ll_mosad.setVisibility(View.VISIBLE);
                        viewHolder.sp_mosad.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_education))) {

                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_education))) {
                        List<String> lst_education = new ArrayList<>();
                        lst_education.add(_context.getString(R.string.ss_education));
                        lst_education.add("Education1");
                        lst_education.add("Education2");

                        ArrayAdapter<String> educationAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_education);
                        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_education.setAdapter(educationAdapter);
                        viewHolder.ll_education.setVisibility(View.VISIBLE);
                        viewHolder.sp_education.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_gotra))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_gotra))) {
                        List<String> lst_gotra = new ArrayList<>();
                        lst_gotra.add(_context.getString(R.string.ss_gotra));
                        lst_gotra.add("Gotra1");
                        lst_gotra.add("Gotra2");
                        ArrayAdapter<String> gotraAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_gotra);
                        gotraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_gotra.setAdapter(gotraAdapter);
                        viewHolder.ll_gotra.setVisibility(View.VISIBLE);
                        viewHolder.sp_gotra.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_bg))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_bg))) {
                        List<String> lst_bg = new ArrayList<>();
                        lst_bg.add(_context.getString(R.string.ss_bg));
                        lst_bg.add("BG1");
                        lst_bg.add("BG2");
                        ArrayAdapter<String> bgAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_bg);
                        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_bg.setAdapter(bgAdapter);
                        viewHolder.ll_bg.setVisibility(View.VISIBLE);
                        viewHolder.sp_bg.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_office))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_office_add.setVisibility(View.VISIBLE);
                        viewHolder.edt_office.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_main_cat))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_catogory))) {
                        List<String> lst_cat = new ArrayList<>();
                        lst_cat.add(_context.getString(R.string.ss_catogory));
                        lst_cat.add("Cat1");
                        lst_cat.add("Cat2");
                        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_cat);
                        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_main_cat.setAdapter(catAdapter);
                        viewHolder.ll_cat.setVisibility(View.VISIBLE);
                        viewHolder.sp_main_cat.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_sub_cat))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_sub_cat))) {
                        List<String> lst_sub_cat = new ArrayList<>();
                        lst_sub_cat.add(_context.getString(R.string.ss_sub_cat));
                        lst_sub_cat.add("SubCat1");
                        lst_sub_cat.add("SubCat2");
                        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_sub_cat);
                        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_sub_cat.setAdapter(catAdapter);
                        viewHolder.ll_sub_cat.setVisibility(View.VISIBLE);
                        viewHolder.sp_sub_cat.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_occupation))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_Occupation))) {
                        List<String> lst_occu = new ArrayList<>();
                        lst_occu.add(_context.getString(R.string.ss_Occupation));
                        lst_occu.add("Occupation1");
                        lst_occu.add("Occupation2");
                        ArrayAdapter<String> occuAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_occu);
                        occuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_occupation.setAdapter(occuAdapter);
                        viewHolder.ll_occupation.setVisibility(View.VISIBLE);
                        viewHolder.sp_occupation.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_activity))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_activity))) {
                        List<String> lst_activity = new ArrayList<>();
                        lst_activity.add(_context.getString(R.string.ss_activity));
                        lst_activity.add("Activity1");
                        lst_activity.add("Activity2");
                        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_activity);
                        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_activity.setAdapter(activityAdapter);
                        viewHolder.ll_activity.setVisibility(View.VISIBLE);
                        viewHolder.sp_activity.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_birth_time))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_btime.setVisibility(View.VISIBLE);
                        viewHolder.edt_btime.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_bplace))) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_bplace))) {
                        List<String> lst_bplace = new ArrayList<>();
                        lst_bplace.add(_context.getString(R.string.ss_bplace));
                        lst_bplace.add("BPlace1");
                        lst_bplace.add("BPlace2");
                        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_bplace);
                        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewHolder.sp_bplace.setAdapter(activityAdapter);
                        viewHolder.ll_bplace.setVisibility(View.VISIBLE);
                        viewHolder.sp_bplace.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_height_meter))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_height.setVisibility(View.VISIBLE);
                        viewHolder.edt_height_meter.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_weight_kg))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_weight.setVisibility(View.VISIBLE);
                        viewHolder.edt_weight_kg.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_chk_is_shani))) {
                    if (value.equalsIgnoreCase("true")) {
                        viewHolder.ll_isShani.setVisibility(View.VISIBLE);
                        viewHolder.chk_is_shani.setChecked(true);
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_chk_is_mangal))) {
                    if (value.equalsIgnoreCase("true")) {
                        viewHolder.ll_isMangal.setVisibility(View.VISIBLE);
                        viewHolder.chk_is_mangal.setChecked(true);
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_chk_is_spect))) {
                    if (value.equalsIgnoreCase("true")) {
                        viewHolder.ll_isSpect.setVisibility(View.VISIBLE);
                        viewHolder.chk_is_spect.setChecked(true);
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_created))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_created.setVisibility(View.VISIBLE);
                        viewHolder.edt_created.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_edt_updated))) {
                    if (!value.isEmpty()) {
                        viewHolder.ll_updated.setVisibility(View.VISIBLE);
                        viewHolder.edt_updated.setText(entry.getValue());
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_minUpdate))) {
                    if (!value.isEmpty()) {
                        viewHolder.rangeUpdationBar.setMinStartValue(Integer.parseInt(value)).apply();
                    }
                } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_maxUpdate))) {
                    if (!value.isEmpty()) {
                        viewHolder.rangeUpdationBar.setMaxStartValue(Integer.parseInt(value)).apply();
                    }
                }
            }

            viewHolder.flexboxLayout.setFlexDirection(FlexDirection.ROW);
            View view = viewHolder.flexboxLayout.getChildAt(0);
            FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) view.getLayoutParams();
            lp.setFlexGrow(1.0f);
            lp.setAlignSelf(AlignItems.FLEX_START);
            view.setLayoutParams(lp);

            // set listener
            viewHolder.rangeAgeBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                if (minValue.intValue() > 0 || maxValue.intValue() < 100) {
                    viewHolder.ll_age.setVisibility(View.VISIBLE);
                    viewHolder.tvMin.setText("Age " + minValue);
                    viewHolder.tvMax.setText("Age " + maxValue);
                }
            });
            // set final value listener
            viewHolder.rangeAgeBar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> Log.d("CRS=>", minValue + " : " + maxValue));

            // set listener
            viewHolder.rangeUpdationBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                if (minValue.intValue() > 0 || maxValue.intValue() < 100) {
                    viewHolder.ll_percentage.setVisibility(View.VISIBLE);
                    viewHolder.txt_min_per.setText(minValue + "%");
                    viewHolder.txt_max_per.setText(maxValue + "%");
                }
            });
            // set final value listener
            viewHolder.rangeAgeBar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> Log.d("CRS=>", minValue + " : " + maxValue));

        } else {
            viewHolder = (PopUpViewHolder) convertView.getTag();
        }

        viewHolder.img_code_close.setOnClickListener(v -> {
            viewHolder.ll_family_code.setVisibility(View.GONE);
        });
        viewHolder.img_age_close.setOnClickListener(v -> {
            viewHolder.ll_age.setVisibility(View.GONE);
        });
        viewHolder.img_head_close.setOnClickListener(v -> {
            viewHolder.ll_head.setVisibility(View.GONE);
        });
        viewHolder.img_member_close.setOnClickListener(v -> {
            viewHolder.ll_member.setVisibility(View.GONE);
        });
        viewHolder.img_surname_close.setOnClickListener(v -> {
            viewHolder.ll_surname.setVisibility(View.GONE);
        });
        viewHolder.img_samaj_close.setOnClickListener(v -> {
            viewHolder.ll_samaj.setVisibility(View.GONE);
        });
        viewHolder.img_gender_close.setOnClickListener(v -> {
            viewHolder.ll_gender.setVisibility(View.GONE);
        });
        viewHolder.img_marital_close.setOnClickListener(v -> {
            viewHolder.ll_marital.setVisibility(View.GONE);
        });
        viewHolder.img_native_close.setOnClickListener(v -> {
            viewHolder.ll_native.setVisibility(View.GONE);
        });
        viewHolder.img_city_close.setOnClickListener(v -> {
            viewHolder.ll_city.setVisibility(View.GONE);
        });
        viewHolder.img_email_close.setOnClickListener(v -> {
            viewHolder.ll_email.setVisibility(View.GONE);
        });
        viewHolder.img_mobile_close.setOnClickListener(v -> {
            viewHolder.ll_mobile.setVisibility(View.GONE);
        });
        viewHolder.img_local_add_close.setOnClickListener(v -> {
            viewHolder.ll_local_add.setVisibility(View.GONE);
        });
        viewHolder.img_permanent_add_close.setOnClickListener(v -> {
            viewHolder.ll_permanent_add.setVisibility(View.GONE);
        });
        viewHolder.img_pincode_close.setOnClickListener(v -> {
            viewHolder.ll_pincode.setVisibility(View.GONE);
        });
        viewHolder.img_area_close.setOnClickListener(v -> {
            viewHolder.ll_area.setVisibility(View.GONE);
        });
        viewHolder.img_state_close.setOnClickListener(v -> {
            viewHolder.ll_state.setVisibility(View.GONE);
        });
        viewHolder.img_bdate_close.setOnClickListener(v -> {
            viewHolder.ll_birth_date.setVisibility(View.GONE);
        });
        viewHolder.img_mdate_close.setOnClickListener(v -> {
            viewHolder.ll_mdate.setVisibility(View.GONE);
        });
        viewHolder.img_mosad_close.setOnClickListener(v -> {
            viewHolder.ll_mosad.setVisibility(View.GONE);
        });
        viewHolder.img_education_close.setOnClickListener(v -> {
            viewHolder.ll_education.setVisibility(View.GONE);
        });
        viewHolder.img_gotra_close.setOnClickListener(v -> {
            viewHolder.ll_gotra.setVisibility(View.GONE);
        });
        viewHolder.img_bg_close.setOnClickListener(v -> {
            viewHolder.ll_bg.setVisibility(View.GONE);
        });
        viewHolder.img_rented_close.setOnClickListener(v -> {
            viewHolder.ll_isRented.setVisibility(View.GONE);
        });
        viewHolder.img_expired_close.setOnClickListener(v -> {
            viewHolder.ll_isExpired.setVisibility(View.GONE);
        });
        viewHolder.img_donor_close.setOnClickListener(v -> {
            viewHolder.ll_isDonor.setVisibility(View.GONE);
        });

        viewHolder.img_oaddress_close.setOnClickListener(v -> {
            viewHolder.ll_office_add.setVisibility(View.GONE);
        });
        viewHolder.img_sub_cat_close.setOnClickListener(v -> {
            viewHolder.ll_sub_cat.setVisibility(View.GONE);
        });
        viewHolder.img_cat_close.setOnClickListener(v -> {
            viewHolder.ll_cat.setVisibility(View.GONE);
        });
        viewHolder.img_occu_close.setOnClickListener(v -> {
            viewHolder.ll_occupation.setVisibility(View.GONE);
        });
        viewHolder.img_activity_close.setOnClickListener(v -> {
            viewHolder.ll_activity.setVisibility(View.GONE);
        });
        viewHolder.img_btime_close.setOnClickListener(v -> {
            viewHolder.ll_btime.setVisibility(View.GONE);
        });
        viewHolder.img_bplace_close.setOnClickListener(v -> {
            viewHolder.ll_bplace.setVisibility(View.GONE);
        });
        viewHolder.img_spect_close.setOnClickListener(v -> {
            viewHolder.ll_isSpect.setVisibility(View.GONE);
        });
        viewHolder.img_shani_close.setOnClickListener(v -> {
            viewHolder.ll_isShani.setVisibility(View.GONE);
        });
        viewHolder.img_mangal_close.setOnClickListener(v -> {
            viewHolder.ll_isMangal.setVisibility(View.GONE);
        });
        viewHolder.img_height_close.setOnClickListener(v -> {
            viewHolder.ll_height.setVisibility(View.GONE);
        });
        viewHolder.img_weight_close.setOnClickListener(v -> {
            viewHolder.ll_weight.setVisibility(View.GONE);
        });
        viewHolder.img_per_close.setOnClickListener(v -> {
            viewHolder.ll_percentage.setVisibility(View.GONE);
        });
        viewHolder.img_updated_close.setOnClickListener(v -> {
            viewHolder.ll_updated.setVisibility(View.GONE);
        });
        viewHolder.img_created_close.setOnClickListener(v -> {
            viewHolder.ll_created.setVisibility(View.GONE);
        });

        viewHolder.chk_save.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewHolder.edt_filter_name.setVisibility(View.VISIBLE);
            } else {
                viewHolder.edt_filter_name.setVisibility(View.INVISIBLE);
            }
        });

        viewHolder.btnApply.setOnClickListener(v -> {

            if (getValues(viewHolder).length() > 0) {
                mICloseDialog.PopupClose();
                SmartFilterResult filterResult = new SmartFilterResult();
                Bundle mBundle = new Bundle();
                mBundle.putString("filter_values", getValues(viewHolder).toString());
                filterResult.setArguments(mBundle);
                Utility.movetoFragment((Activity) _context, filterResult);
            } else {
                Toast.makeText(_context, "No Filter found!", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    private JSONObject getValues(PopUpViewHolder viewHolder) {
        JSONObject lstValues = new JSONObject();
        try {
            if (viewHolder.ll_family_code.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_family_code), viewHolder.edt_family_code.getText().toString().trim());
            }
            if (viewHolder.ll_head.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_head_name), viewHolder.edt_head_name.getText().toString().trim());
            }
            if (viewHolder.ll_member.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_mem_name), viewHolder.edt_member.getText().toString().trim());
            }
            if (viewHolder.ll_surname.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_surname), viewHolder.sp_surname.getText().toString().trim());
            }

            if (viewHolder.ll_samaj.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_samaj), viewHolder.sp_samaj.getText().toString().trim());
            }

            if (viewHolder.ll_gender.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_gender), viewHolder.sp_gender.getText().toString().trim());
            }

            if (viewHolder.ll_marital.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_marital), viewHolder.sp_marital.getText().toString().trim());
            }

            if (viewHolder.ll_native.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_native), viewHolder.sp_native.getText().toString().trim());
            }

            if (viewHolder.ll_city.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_city), viewHolder.sp_city.getText().toString().trim());
            }

            if (viewHolder.ll_age.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_minAge), viewHolder.rangeAgeBar.getSelectedMinValue().toString().trim());
                lstValues.put(_context.getResources().getString(R.string.ss_maxAge), viewHolder.rangeAgeBar.getSelectedMaxValue().toString().trim());
            }

            if (viewHolder.ll_email.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_email), viewHolder.edt_email.getText().toString().trim());
            }

            if (viewHolder.ll_mobile.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_mobile), viewHolder.edt_mobile.getText().toString().trim());
            }

            if (viewHolder.ll_local_add.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_local_add), viewHolder.edt_local_add.getText().toString().trim());
            }

            if (viewHolder.ll_permanent_add.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_permanent_add), viewHolder.edt_permanent_add.getText().toString().trim());
            }

            if (viewHolder.ll_pincode.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_pin_code), viewHolder.edt_pincode.getText().toString().trim());
            }

            if (viewHolder.ll_area.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_area), viewHolder.sp_area.getText().toString().trim());
            }

            if (viewHolder.ll_state.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_state), viewHolder.sp_state.getText().toString().trim());
            }

            if (viewHolder.ll_birth_date.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_bdate), viewHolder.edt_birth_date.getText().toString().trim());
            }

            if (viewHolder.ll_mdate.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_mdate), viewHolder.edt_mdate.getText().toString().trim());
            }

            if (viewHolder.ll_mosad.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_mosad), viewHolder.sp_mosad.getText().toString().trim());
            }

            if (viewHolder.ll_education.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_education), viewHolder.sp_education.getText().toString().trim());
            }

            if (viewHolder.ll_gotra.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_gotra), viewHolder.sp_gotra.getText().toString().trim());
            }

            if (viewHolder.ll_bg.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_bg), viewHolder.sp_bg.getText().toString().trim());
            }

            if (viewHolder.ll_isDonor.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_chk_is_donor), viewHolder.chk_is_donor.getText().toString().trim());
            }

            if (viewHolder.ll_isRented.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_chk_is_rented), viewHolder.chk_is_rented.getText().toString().trim());
            }

            if (viewHolder.ll_isExpired.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_chk_is_expired), viewHolder.chk_is_expired.getText().toString().trim());
            }

            if (viewHolder.ll_office_add.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_office), viewHolder.edt_office.getText().toString().trim());
            }

            if (viewHolder.ll_cat.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_main_cat), viewHolder.sp_main_cat.getText().toString().trim());
            }

            if (viewHolder.ll_sub_cat.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_sub_cat), viewHolder.sp_sub_cat.getText().toString().trim());
            }

            if (viewHolder.ll_occupation.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_occupation), viewHolder.sp_occupation.getText().toString().trim());
            }

            if (viewHolder.ll_activity.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_activity), viewHolder.sp_activity.getText().toString().trim());
            }

            if (viewHolder.ll_btime.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_birth_time), viewHolder.edt_btime.getText().toString().trim());
            }

            if (viewHolder.ll_bplace.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_sp_bplace), viewHolder.sp_bplace.getText().toString().trim());
            }

            if (viewHolder.ll_isSpect.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_chk_is_spect), viewHolder.chk_is_spect.getText().toString().trim());
            }

            if (viewHolder.ll_isShani.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_chk_is_shani), viewHolder.chk_is_shani.getText().toString().trim());
            }

            if (viewHolder.ll_isMangal.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_chk_is_mangal), viewHolder.chk_is_mangal.getText().toString().trim());
            }

            if (viewHolder.ll_height.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_height_meter), viewHolder.edt_height_meter.getText().toString().trim());
            }

            if (viewHolder.ll_weight.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_weight_kg), viewHolder.edt_weight_kg.getText().toString().trim());
            }

            if (viewHolder.ll_created.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_created), viewHolder.edt_created.getText().toString().trim());
            }

            if (viewHolder.ll_updated.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_edt_updated), viewHolder.edt_updated.getText().toString().trim());
            }

            if (viewHolder.ll_percentage.isShown()) {
                lstValues.put(_context.getResources().getString(R.string.ss_maxUpdate), viewHolder.rangeUpdationBar.getSelectedMaxValue().toString().trim());
                lstValues.put(_context.getResources().getString(R.string.ss_minUpdate), viewHolder.rangeUpdationBar.getSelectedMinValue().toString().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return lstValues;
    }


    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    interface ICloseDialog {
        void PopupClose();
    }

    private class PopUpViewHolder {
        FlexboxLayout flexboxLayout;
        ImageView img_code_close, img_age_close, img_head_close, img_member_close, img_surname_close, img_samaj_close, img_gender_close, img_marital_close, img_native_close, img_city_close, img_email_close, img_mobile_close, img_local_add_close, img_permanent_add_close, img_pincode_close, img_area_close, img_state_close, img_bdate_close, img_mdate_close, img_mosad_close, img_education_close, img_gotra_close, img_bg_close, img_expired_close, img_rented_close, img_donor_close, img_oaddress_close, img_sub_cat_close, img_cat_close, img_occu_close, img_activity_close, img_btime_close, img_bplace_close, img_spect_close, img_shani_close, img_mangal_close, img_height_close, img_weight_close, img_per_close, img_updated_close, img_created_close;
        LinearLayout ll_family_code, ll_head, ll_member, ll_surname, ll_samaj, ll_gender, ll_marital, ll_native, ll_city, ll_age, ll_email, ll_mobile, ll_local_add, ll_permanent_add, ll_pincode, ll_area, ll_state, ll_birth_date, ll_mdate, ll_mosad, ll_education, ll_gotra, ll_bg, ll_isExpired, ll_isRented, ll_isDonor, ll_office_add, ll_cat, ll_sub_cat, ll_occupation, ll_activity, ll_btime, ll_bplace, ll_isSpect, ll_isShani, ll_isMangal, ll_height, ll_weight, ll_created, ll_updated, ll_percentage;
        EditText edt_family_code, edt_head_name, edt_member, edt_email, edt_mobile, edt_local_add, edt_permanent_add, edt_pincode, edt_birth_date, edt_mdate, edt_office, edt_btime, edt_height_meter, edt_weight_kg, edt_updated, edt_created;
        MaterialSpinner sp_surname, sp_samaj, sp_marital, sp_city, sp_gender, sp_native, sp_area, sp_state, sp_mosad, sp_education, sp_gotra, sp_bg, sp_main_cat, sp_sub_cat, sp_occupation, sp_activity, sp_bplace;
        CrystalRangeSeekbar rangeAgeBar, rangeUpdationBar;
        TextView tvMin, tvMax, txt_min_per, txt_max_per;
        EditText edt_filter_name;
        CheckBox chk_save, chk_is_expired, chk_is_rented, chk_is_donor, chk_is_spect, chk_is_shani, chk_is_mangal;
        Button btnApply;


        PopUpViewHolder(View view) {
            flexboxLayout = view.findViewById(R.id.flexbox_layout);
            ll_family_code = view.findViewById(R.id.ll_family_code);
            ll_head = view.findViewById(R.id.ll_head);
            ll_member = view.findViewById(R.id.ll_member);
            ll_surname = view.findViewById(R.id.ll_surname);
            ll_samaj = view.findViewById(R.id.ll_samaj);
            ll_gender = view.findViewById(R.id.ll_gender);
            ll_marital = view.findViewById(R.id.ll_marital);
            ll_native = view.findViewById(R.id.ll_native);
            ll_city = view.findViewById(R.id.ll_city);
            ll_age = view.findViewById(R.id.ll_age);
            ll_email = view.findViewById(R.id.ll_email);
            ll_mobile = view.findViewById(R.id.ll_mobile);
            ll_local_add = view.findViewById(R.id.ll_local_add);
            ll_permanent_add = view.findViewById(R.id.ll_permanent_add);
            ll_pincode = view.findViewById(R.id.ll_pincode);
            ll_area = view.findViewById(R.id.ll_area);
            ll_state = view.findViewById(R.id.ll_state);
            ll_mdate = view.findViewById(R.id.ll_mdate);
            ll_birth_date = view.findViewById(R.id.ll_birth_date);
            ll_mosad = view.findViewById(R.id.ll_mosad);
            ll_education = view.findViewById(R.id.ll_education);
            ll_gotra = view.findViewById(R.id.ll_gotra);
            ll_bg = view.findViewById(R.id.ll_bg);
            ll_isExpired = view.findViewById(R.id.ll_isExpired);
            ll_isRented = view.findViewById(R.id.ll_isRented);
            ll_isDonor = view.findViewById(R.id.ll_isDonor);
            ll_office_add = view.findViewById(R.id.ll_office_add);
            ll_cat = view.findViewById(R.id.ll_cat);
            ll_sub_cat = view.findViewById(R.id.ll_sub_cat);
            ll_occupation = view.findViewById(R.id.ll_occupation);
            ll_activity = view.findViewById(R.id.ll_activity);
            ll_btime = view.findViewById(R.id.ll_btime);
            ll_bplace = view.findViewById(R.id.ll_bplace);
            ll_isSpect = view.findViewById(R.id.ll_isSpect);
            ll_isShani = view.findViewById(R.id.ll_isShani);
            ll_isMangal = view.findViewById(R.id.ll_isMangal);
            ll_height = view.findViewById(R.id.ll_height);
            ll_weight = view.findViewById(R.id.ll_weight);
            ll_created = view.findViewById(R.id.ll_created);
            ll_updated = view.findViewById(R.id.ll_updated);
            ll_percentage = view.findViewById(R.id.ll_percentage);
            img_code_close = view.findViewById(R.id.img_code_close);
            img_age_close = view.findViewById(R.id.img_age_close);
            img_head_close = view.findViewById(R.id.img_head_close);
            img_member_close = view.findViewById(R.id.img_member_close);
            img_surname_close = view.findViewById(R.id.img_surname_close);
            img_samaj_close = view.findViewById(R.id.img_samaj_close);
            img_gender_close = view.findViewById(R.id.img_gender_close);
            img_marital_close = view.findViewById(R.id.img_marital_close);
            img_native_close = view.findViewById(R.id.img_native_close);
            img_city_close = view.findViewById(R.id.img_city_close);
            img_email_close = view.findViewById(R.id.img_email_close);
            img_mobile_close = view.findViewById(R.id.img_mobile_close);
            img_local_add_close = view.findViewById(R.id.img_local_add_close);
            img_permanent_add_close = view.findViewById(R.id.img_permanent_add_close);
            img_pincode_close = view.findViewById(R.id.img_pincode_close);
            img_area_close = view.findViewById(R.id.img_area_close);
            img_state_close = view.findViewById(R.id.img_state_close);
            img_bdate_close = view.findViewById(R.id.img_bdate_close);
            img_gotra_close = view.findViewById(R.id.img_gotra_close);
            img_mdate_close = view.findViewById(R.id.img_mdate_close);
            img_mosad_close = view.findViewById(R.id.img_mosad_close);
            img_education_close = view.findViewById(R.id.img_education_close);
            img_bg_close = view.findViewById(R.id.img_bg_close);
            img_expired_close = view.findViewById(R.id.img_expired_close);
            img_rented_close = view.findViewById(R.id.img_rented_close);
            img_donor_close = view.findViewById(R.id.img_donor_close);
            img_oaddress_close = view.findViewById(R.id.img_oaddress_close);
            img_sub_cat_close = view.findViewById(R.id.img_sub_cat_close);
            img_cat_close = view.findViewById(R.id.img_cat_close);
            img_occu_close = view.findViewById(R.id.img_occu_close);
            img_activity_close = view.findViewById(R.id.img_activity_close);
            img_btime_close = view.findViewById(R.id.img_btime_close);
            img_bplace_close = view.findViewById(R.id.img_bplace_close);
            img_spect_close = view.findViewById(R.id.img_spect_close);
            img_shani_close = view.findViewById(R.id.img_shani_close);
            img_mangal_close = view.findViewById(R.id.img_mangal_close);
            img_height_close = view.findViewById(R.id.img_height_close);
            img_weight_close = view.findViewById(R.id.img_weight_close);
            img_per_close = view.findViewById(R.id.img_per_close);
            img_updated_close = view.findViewById(R.id.img_updated_close);
            img_created_close = view.findViewById(R.id.img_created_close);

            edt_email = view.findViewById(R.id.edt_email);
            edt_mobile = view.findViewById(R.id.edt_mobile);
            edt_local_add = view.findViewById(R.id.edt_local_add);
            edt_permanent_add = view.findViewById(R.id.edt_permanent_add);
            edt_pincode = view.findViewById(R.id.edt_pincode);
            edt_family_code = view.findViewById(R.id.edt_family_code);
            edt_head_name = view.findViewById(R.id.edt_head_name);
            edt_member = view.findViewById(R.id.edt_member);
            edt_filter_name = view.findViewById(R.id.edt_filter_name);
            edt_birth_date = view.findViewById(R.id.edt_birth_date);
            edt_mdate = view.findViewById(R.id.edt_mdate);
            edt_office = view.findViewById(R.id.edt_office);
            edt_btime = view.findViewById(R.id.edt_btime);
            edt_height_meter = view.findViewById(R.id.edt_height_meter);
            edt_weight_kg = view.findViewById(R.id.edt_weight_kg);

            edt_updated = view.findViewById(R.id.edt_updated);
            edt_created = view.findViewById(R.id.edt_created);

            sp_surname = view.findViewById(R.id.sp_surname);
            sp_samaj = view.findViewById(R.id.sp_samaj);
            sp_marital = view.findViewById(R.id.sp_marital);
            sp_city = view.findViewById(R.id.sp_city);
            sp_gender = view.findViewById(R.id.sp_gender);
            sp_native = view.findViewById(R.id.sp_native);
            sp_area = view.findViewById(R.id.sp_area);
            sp_gotra = view.findViewById(R.id.sp_gotra);
            sp_state = view.findViewById(R.id.sp_state);
            sp_mosad = view.findViewById(R.id.sp_mosad);
            sp_education = view.findViewById(R.id.sp_education);
            sp_bg = view.findViewById(R.id.sp_bg);
            sp_main_cat = view.findViewById(R.id.sp_main_cat);
            sp_sub_cat = view.findViewById(R.id.sp_sub_cat);
            sp_occupation = view.findViewById(R.id.sp_occupation);
            sp_activity = view.findViewById(R.id.sp_activity);
            sp_bplace = view.findViewById(R.id.sp_bplace);
            chk_save = view.findViewById(R.id.chk_save);
            rangeAgeBar = view.findViewById(R.id.rangeSeekbar);
            tvMin = view.findViewById(R.id.textMin1);
            tvMax = view.findViewById(R.id.textMax1);

            rangeUpdationBar = view.findViewById(R.id.rangeUpdationBar);
            txt_min_per = view.findViewById(R.id.txt_min_per);
            txt_max_per = view.findViewById(R.id.txt_max_per);

            btnApply = view.findViewById(R.id.btnApply);
            chk_is_expired = view.findViewById(R.id.chk_is_expired);
            chk_is_rented = view.findViewById(R.id.chk_is_rented);
            chk_is_donor = view.findViewById(R.id.chk_is_donor);
            chk_is_spect = view.findViewById(R.id.chk_is_spect);
            chk_is_shani = view.findViewById(R.id.chk_is_shani);
            chk_is_mangal = view.findViewById(R.id.chk_is_mangal);
        }
    }
}
