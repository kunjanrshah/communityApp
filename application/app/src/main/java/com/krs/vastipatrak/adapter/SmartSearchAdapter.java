package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.krs.vastipatrak.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class SmartSearchAdapter extends BaseExpandableListAdapter {

    private Hashtable<String, String> mapChildValues;
    private EditText edt_head_name, edt_member_name, edt_email, edt_mobile, edt_local_add, edt_permanent_add, edt_pin_code, edt_bdate, edt_mdate, edt_office, edt_birth_time, edt_height_meter, edt_weight_kg, edt_created, edt_updated;
    private MaterialSpinner sp_surname, sp_samaj, sp_marital, sp_city, sp_gender, sp_native, sp_area, sp_state, sp_mosad, sp_education, sp_gotra, sp_bg, sp_main_cat, sp_sub_cat, sp_occupation, sp_activity, sp_bplace;
    private CrystalRangeSeekbar rangeAgeBar, rangeUpdationBar;
    private Context _context;
    private List<String> header;
    private ArrayAdapter<String> surnameAdapter, samajAdapter, maritalAdapter, cityAdapter, genderAdapter, nativeAdapter, mosadAdapter, educationAdapter, gotraAdapter, bgAdapter, areaAdapter, stateAdapter, categoryAdapter, subcatAdapter, occupationAdapter, curActivityAdapter, birthPlaceAdapter;
    private CheckBox chk_is_doner, chk_is_rented, chk_is_expired, chk_is_spect, chk_is_shani, chk_is_mangal;


    public SmartSearchAdapter(Context context) {
        this._context = context;
        mapChildValues = new Hashtable<>();
        header = new ArrayList<>();
        header.add("");
        header.add(context.getString(R.string.main));
        header.add(context.getString(R.string.contact));
        header.add(context.getString(R.string.personal));
        header.add(context.getString(R.string.professonal));
        header.add(context.getString(R.string._matrimony));
        header.add(context.getString(R.string.some_more));
        header.add("");

        List<String> lst_gender = new ArrayList<String>();
        lst_gender.add("Gender");
        lst_gender.add("Male");
        lst_gender.add("Female");

        List<String> lst_native = new ArrayList<String>();
        lst_native.add("Native");
        lst_native.add("Native1");
        lst_native.add("Native2");

        List<String> lst_surname = new ArrayList<String>();
        lst_surname.add("surname");
        lst_surname.add("surname1");
        lst_surname.add("surname2");

        List<String> lst_samaj = new ArrayList<String>();
        lst_samaj.add("samaj");
        lst_samaj.add("samaj1");
        lst_samaj.add("samaj2");

        List<String> lst_city = new ArrayList<String>();
        lst_city.add("city");
        lst_city.add("city1");
        lst_city.add("city2");

        List<String> lst_marital = new ArrayList<String>();
        lst_marital.add("marital");
        lst_marital.add("marital1");
        lst_marital.add("marital2");

        List<String> lst_area = new ArrayList<String>();
        lst_area.add("area");
        lst_area.add("area1");
        lst_area.add("area2");

        List<String> lst_state = new ArrayList<String>();
        lst_state.add("state");
        lst_state.add("state1");
        lst_state.add("state2");

        List<String> lst_mosad = new ArrayList<String>();
        lst_mosad.add("mosad");
        lst_mosad.add("mosad1");
        lst_mosad.add("mosad2");

        List<String> lst_education = new ArrayList<String>();
        lst_education.add("education");
        lst_education.add("education1");
        lst_education.add("education2");

        List<String> lst_gotra = new ArrayList<String>();
        lst_gotra.add("gotra");
        lst_gotra.add("gotra1");
        lst_gotra.add("gotra2");

        List<String> lst_bg = new ArrayList<String>();
        lst_bg.add("bg");
        lst_bg.add("bg1");
        lst_bg.add("bg2");

        List<String> lst_category = new ArrayList<String>();
        lst_category.add("category");
        lst_category.add("category1");
        lst_category.add("category2");

        List<String> lst_sub_cat = new ArrayList<String>();
        lst_sub_cat.add("sub_cat");
        lst_sub_cat.add("sub_cat1");
        lst_sub_cat.add("sub_cat2");

        List<String> lst_occupation = new ArrayList<String>();
        lst_occupation.add("occupation");
        lst_occupation.add("occupation1");
        lst_occupation.add("occupation2");

        List<String> lst_activity = new ArrayList<String>();
        lst_activity.add("activity");
        lst_activity.add("activity1");
        lst_activity.add("activity2");

        List<String> lst_bplace = new ArrayList<String>();
        lst_bplace.add("bplace");
        lst_bplace.add("bplace1");
        lst_bplace.add("bplace2");

        birthPlaceAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_bplace);
        birthPlaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_category);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcatAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_sub_cat);
        subcatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupationAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_occupation);
        occupationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        curActivityAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_activity);
        curActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        surnameAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_surname);
        surnameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        samajAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_samaj);
        samajAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maritalAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_marital);
        maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_city);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nativeAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_native);
        nativeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mosadAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_mosad);
        mosadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_education);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gotraAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_gotra);
        gotraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bgAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_bg);
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_area);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_state);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


    }

    @Override
    public int getGroupCount() {
        return 8;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.header.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Getting header title
        String headerTitle = (String) getGroup(groupPosition);

        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.header, parent, false);
        }
        LinearLayout ll_title = convertView.findViewById(R.id.ll_title);
        TextView header_text = convertView.findViewById(R.id.header);
        Button btn_confirm = convertView.findViewById(R.id.btn_confirm);

        if (groupPosition == 0) {
            ll_title.setVisibility(View.VISIBLE);
            header_text.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.GONE);
        } else if (groupPosition == 7) {
            ll_title.setVisibility(View.GONE);
            header_text.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.VISIBLE);
        } else {
            ll_title.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.GONE);
            header_text.setVisibility(View.VISIBLE);
        }


        header_text.setText(headerTitle);

        // If group is expanded then change the text into bold and change the
        // icon
        if (isExpanded) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                header_text.setBackground(_context.getResources().getDrawable(R.drawable.rounded_top_corner));
            }
            header_text.setTypeface(null, Typeface.BOLD);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sort_up, 0);
            }
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                header_text.setBackground(_context.getResources().getDrawable(R.drawable.rounded_corder));
            }
            header_text.setTypeface(null, Typeface.NORMAL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sort_down, 0);
            }
        }
        return convertView;
    }

    public void storeFieldsValues() {
        if (mapChildValues != null) {
            if (edt_head_name != null) {
                mapChildValues.put("head_name", edt_head_name.getText().toString().trim());
                mapChildValues.put("mem_name", edt_member_name.getText().toString().trim());
                mapChildValues.put("sp_surname", sp_surname.getText().toString().trim());
                mapChildValues.put("sp_samaj", sp_samaj.getText().toString().trim());
                mapChildValues.put("sp_marital", sp_marital.getText().toString().trim());
                mapChildValues.put("sp_city", sp_city.getText().toString().trim());
                mapChildValues.put("sp_gender", sp_gender.getText().toString().trim());
                mapChildValues.put("sp_native", sp_native.getText().toString().trim());
                mapChildValues.put("minAge", rangeAgeBar.getSelectedMinValue().toString());
                mapChildValues.put("maxAge", rangeAgeBar.getSelectedMaxValue().toString());
            }
            if (edt_email != null) {
                mapChildValues.put("edt_email", edt_email.getText().toString().trim());
                mapChildValues.put("edt_mobile", edt_mobile.getText().toString().trim());
                mapChildValues.put("edt_local_add", edt_local_add.getText().toString().trim());
                mapChildValues.put("edt_permanent_add", edt_permanent_add.getText().toString().trim());
                mapChildValues.put("edt_pin_code", edt_pin_code.getText().toString().trim());
                mapChildValues.put("sp_area", sp_area.getText().toString().trim());
                mapChildValues.put("sp_state", sp_state.getText().toString().trim());
            }
            if (edt_bdate != null) {
                mapChildValues.put("edt_bdate", edt_bdate.getText().toString().trim());
                mapChildValues.put("edt_mdate", edt_mdate.getText().toString().trim());
                mapChildValues.put("sp_mosad", sp_mosad.getText().toString().trim());
                mapChildValues.put("sp_education", sp_education.getText().toString().trim());
                mapChildValues.put("sp_gotra", sp_gotra.getText().toString().trim());
                mapChildValues.put("sp_bg", sp_bg.getText().toString().trim());
                mapChildValues.put("chk_is_doner", String.valueOf(chk_is_doner.isSelected()));
                mapChildValues.put("chk_is_rented", String.valueOf(chk_is_rented.isSelected()));
                mapChildValues.put("chk_is_expired", String.valueOf(chk_is_expired.isSelected()));
            }
            if (edt_office != null) {
                mapChildValues.put("edt_office", edt_office.getText().toString().trim());
                mapChildValues.put("sp_main_cat", sp_main_cat.getText().toString().trim());
                mapChildValues.put("sp_sub_cat", sp_sub_cat.getText().toString().trim());
                mapChildValues.put("sp_occupation", sp_occupation.getText().toString().trim());
                mapChildValues.put("sp_activity", sp_activity.getText().toString().trim());
            }
            if (edt_birth_time != null) {
                mapChildValues.put("edt_birth_time", edt_birth_time.getText().toString().trim());
                mapChildValues.put("edt_height_meter", edt_height_meter.getText().toString().trim());
                mapChildValues.put("edt_weight_kg", edt_weight_kg.getText().toString().trim());
                mapChildValues.put("sp_bplace", sp_bplace.getText().toString().trim());
                mapChildValues.put("chk_is_spect", String.valueOf(chk_is_spect.isSelected()));
                mapChildValues.put("chk_is_shani", String.valueOf(chk_is_shani.isSelected()));
                mapChildValues.put("chk_is_mangal", String.valueOf(chk_is_mangal.isSelected()));
            }
            if (edt_created != null) {
                mapChildValues.put("edt_created", edt_created.getText().toString().trim());
                mapChildValues.put("edt_updated", edt_updated.getText().toString().trim());
                mapChildValues.put("minUpdate", rangeUpdationBar.getSelectedMinValue().toString());
                mapChildValues.put("maxUpdate", rangeUpdationBar.getSelectedMaxValue().toString());
            }
        }
    }

    public void retrieveFieldsValues() {
        if (mapChildValues != null && mapChildValues.size() > 0) {
            if (edt_head_name != null) {
                String head_name = mapChildValues.get("head_name");
                if (head_name != null && !head_name.isEmpty()) {
                    edt_head_name.setText(head_name);
                }
                String mem_name = mapChildValues.get("mem_name");
                if (mem_name != null && !mem_name.isEmpty()) {
                    edt_member_name.setText(mem_name);
                }
                String surname = mapChildValues.get("sp_surname");
                if (surname != null && !surname.isEmpty()) {
                    sp_surname.setText(surname);
                }
                String samaj = mapChildValues.get("sp_samaj");
                if (samaj != null && !samaj.isEmpty()) {
                    sp_samaj.setText(samaj);
                }
                String marital = mapChildValues.get("sp_marital");
                if (marital != null && !marital.isEmpty()) {
                    sp_marital.setText(marital);
                }
                String city = mapChildValues.get("sp_city");
                if (city != null && !city.isEmpty()) {
                    sp_city.setText(city);
                }
                String gender = mapChildValues.get("sp_gender");
                if (gender != null && !gender.isEmpty()) {
                    sp_gender.setText(gender);
                }

                String native1 = mapChildValues.get("sp_native");
                if (native1 != null && !native1.isEmpty()) {
                    sp_native.setText(native1);
                }

                String maxAge = mapChildValues.get("maxAge");
                String minAge = mapChildValues.get("minAge");
                if (maxAge != null && !maxAge.isEmpty()) {
                    rangeAgeBar.setMaxStartValue(Integer.parseInt(mapChildValues.get("maxAge"))).apply();
                }
                if (minAge != null && !minAge.isEmpty()) {
                    rangeAgeBar.setMinStartValue(Integer.parseInt(mapChildValues.get("minAge"))).apply();
                }
            }
            if (edt_email != null) {
                String email = mapChildValues.get("edt_email");
                if (email != null && !email.isEmpty()) {
                    edt_email.setText(email);
                }
                String mobile = mapChildValues.get("edt_mobile");
                if (mobile != null && !mobile.isEmpty()) {
                    edt_mobile.setText(mobile);
                }
                String local_add = mapChildValues.get("edt_local_add");
                if (local_add != null && !local_add.isEmpty()) {
                    edt_local_add.setText(local_add);
                }
                String permanent_add = mapChildValues.get("edt_permanent_add");
                if (permanent_add != null && !permanent_add.isEmpty()) {
                    edt_permanent_add.setText(permanent_add);
                }
                String pin_code = mapChildValues.get("edt_pin_code");
                if (pin_code != null && !pin_code.isEmpty()) {
                    edt_pin_code.setText(pin_code);
                }

                String area = mapChildValues.get("sp_area");
                if (area != null && !area.isEmpty()) {
                    sp_area.setText(area);
                }
                String state = mapChildValues.get("sp_state");
                if (state != null && !state.isEmpty()) {
                    sp_state.setText(state);
                }


            }
            if (edt_bdate != null) {
                edt_bdate.setText(mapChildValues.get("edt_bdate"));
                edt_mdate.setText(mapChildValues.get("edt_mdate"));
                sp_mosad.setText(mapChildValues.get("sp_mosad"));
                sp_education.setText(mapChildValues.get("sp_education"));
                sp_gotra.setText(mapChildValues.get("sp_gotra"));
                sp_bg.setText(mapChildValues.get("sp_bg"));

                String is_doner = mapChildValues.get("chk_is_doner");
                if (is_doner != null && is_doner.equalsIgnoreCase("true")) {
                    chk_is_doner.setChecked(true);
                } else {
                    chk_is_doner.setChecked(false);
                }

                String is_rented = mapChildValues.get("chk_is_rented");
                if (is_rented != null && is_rented.equalsIgnoreCase("true")) {
                    chk_is_rented.setChecked(true);
                } else {
                    chk_is_rented.setChecked(false);
                }

                String is_expired = mapChildValues.get("chk_is_expired");
                if (is_expired != null && is_expired.equalsIgnoreCase("true")) {
                    chk_is_expired.setChecked(true);
                } else {
                    chk_is_expired.setChecked(false);
                }

            }
            if (edt_office != null) {
                edt_office.setText(mapChildValues.get("edt_office"));
                sp_main_cat.setText(mapChildValues.get("sp_main_cat"));
                sp_sub_cat.setText(mapChildValues.get("sp_sub_cat"));
                sp_occupation.setText(mapChildValues.get("sp_occupation"));
                sp_activity.setText(mapChildValues.get("sp_activity"));
            }
            if (edt_birth_time != null) {
                edt_birth_time.setText(mapChildValues.get("edt_birth_time"));
                edt_height_meter.setText(mapChildValues.get("edt_height_meter"));
                edt_weight_kg.setText(mapChildValues.get("edt_weight_kg"));
                sp_bplace.setText(mapChildValues.get("sp_bplace"));

                String is_spect = mapChildValues.get("chk_is_spect");
                if (is_spect != null && is_spect.equalsIgnoreCase("true")) {
                    chk_is_spect.setChecked(true);
                } else {
                    chk_is_spect.setChecked(false);
                }

                String is_shani = mapChildValues.get("chk_is_shani");
                if (is_shani != null && is_shani.equalsIgnoreCase("true")) {
                    chk_is_shani.setChecked(true);
                } else {
                    chk_is_shani.setChecked(false);
                }

                String is_mangal = mapChildValues.get("chk_is_mangal");
                if (is_mangal != null && is_mangal.equalsIgnoreCase("true")) {
                    chk_is_mangal.setChecked(true);
                } else {
                    chk_is_mangal.setChecked(false);
                }
            }
            if (edt_created != null) {
                edt_created.setText(mapChildValues.get("edt_created"));
                edt_updated.setText(mapChildValues.get("edt_updated"));

                String maxUpdate = mapChildValues.get("maxUpdate");
                String minUpdate = mapChildValues.get("minUpdate");
                if (maxUpdate != null && !maxUpdate.isEmpty()) {
                    rangeUpdationBar.setMaxStartValue(Integer.parseInt(maxUpdate)).apply();
                }
                if (minUpdate != null && !minUpdate.isEmpty()) {
                    rangeUpdationBar.setMinStartValue(Integer.parseInt(minUpdate)).apply();
                }

            }
        }


    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (groupPosition) {
            case 1:
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.main_details, null);
                }
                edt_head_name = convertView.findViewById(R.id.edt_head_name);
                edt_member_name = convertView.findViewById(R.id.edt_member_name);
                sp_surname = convertView.findViewById(R.id.sp_surname);
                sp_samaj = convertView.findViewById(R.id.sp_samaj);
                sp_marital = convertView.findViewById(R.id.sp_marital);
                sp_city = convertView.findViewById(R.id.sp_city);
                sp_gender = convertView.findViewById(R.id.sp_gender);
                sp_native = convertView.findViewById(R.id.sp_native);
                rangeAgeBar = convertView.findViewById(R.id.rangeSeekbar);
                final TextView tvMin = (TextView) convertView.findViewById(R.id.textMin1);
                final TextView tvMax = (TextView) convertView.findViewById(R.id.textMax1);

                sp_surname.setAdapter(surnameAdapter);
                sp_samaj.setAdapter(samajAdapter);
                sp_marital.setAdapter(maritalAdapter);
                sp_city.setAdapter(cityAdapter);
                sp_gender.setAdapter(genderAdapter);
                sp_native.setAdapter(nativeAdapter);

                // set listener
                rangeAgeBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                    tvMin.setText("Age " + String.valueOf(minValue));
                    tvMax.setText("Age " + String.valueOf(maxValue));
                });

                // set final value listener
                rangeAgeBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                    @Override
                    public void finalValue(Number minValue, Number maxValue) {
                        Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                    }
                });
                retrieveFieldsValues();

                break;
            case 2:
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.contact_details, null);
                }
                edt_email = convertView.findViewById(R.id.edt_email);
                edt_mobile = convertView.findViewById(R.id.edt_mobile);
                edt_local_add = convertView.findViewById(R.id.edt_local_add);
                edt_permanent_add = convertView.findViewById(R.id.edt_permanent_add);
                edt_pin_code = convertView.findViewById(R.id.edt_pin_code);
                sp_area = convertView.findViewById(R.id.sp_area);
                sp_state = convertView.findViewById(R.id.sp_state);

                sp_area.setAdapter(areaAdapter);
                sp_state.setAdapter(stateAdapter);

                retrieveFieldsValues();

                break;
            case 3:
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.personal_details, null);
                }
                edt_bdate = convertView.findViewById(R.id.edt_bdate);
                edt_mdate = convertView.findViewById(R.id.edt_mdate);
                sp_mosad = convertView.findViewById(R.id.sp_mosad);
                sp_education = convertView.findViewById(R.id.sp_education);
                sp_gotra = convertView.findViewById(R.id.sp_gotra);
                sp_bg = convertView.findViewById(R.id.sp_bg);
                chk_is_doner = convertView.findViewById(R.id.chk_is_doner);
                chk_is_rented = convertView.findViewById(R.id.chk_is_rented);
                chk_is_expired = convertView.findViewById(R.id.chk_is_expired);

                sp_mosad.setAdapter(mosadAdapter);
                sp_education.setAdapter(educationAdapter);
                sp_gotra.setAdapter(gotraAdapter);
                sp_bg.setAdapter(bgAdapter);

                retrieveFieldsValues();

                break;
            case 4:
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.professional_details, null);
                }

                edt_office = convertView.findViewById(R.id.edt_office);
                sp_main_cat = convertView.findViewById(R.id.sp_main_cat);
                sp_sub_cat = convertView.findViewById(R.id.sp_sub_cat);
                sp_occupation = convertView.findViewById(R.id.sp_occupation);
                sp_activity = convertView.findViewById(R.id.sp_current_activity);
                sp_main_cat.setAdapter(categoryAdapter);
                sp_sub_cat.setAdapter(subcatAdapter);
                sp_occupation.setAdapter(occupationAdapter);
                sp_activity.setAdapter(curActivityAdapter);
                retrieveFieldsValues();
                break;
            case 5:
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.matrimony_details, null);
                }

                edt_birth_time = convertView.findViewById(R.id.edt_birth_time);
                edt_height_meter = convertView.findViewById(R.id.edt_height_meter);
                edt_weight_kg = convertView.findViewById(R.id.edt_weight_kg);
                sp_bplace = convertView.findViewById(R.id.sp_birth_place);
                chk_is_spect = convertView.findViewById(R.id.chk_is_spect);
                chk_is_shani = convertView.findViewById(R.id.chk_is_shani);
                chk_is_mangal = convertView.findViewById(R.id.chk_is_mangal);

                sp_bplace.setAdapter(birthPlaceAdapter);
                retrieveFieldsValues();
                break;
            case 6:
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.see_more, null);
                }
                edt_created = convertView.findViewById(R.id.edt_created);
                edt_updated = convertView.findViewById(R.id.edt_updated);
                rangeUpdationBar = convertView.findViewById(R.id.rangeUpdationBar);

                final TextView tvMin1 = (TextView) convertView.findViewById(R.id.textMin1);
                final TextView tvMax1 = (TextView) convertView.findViewById(R.id.textMax1);

                // set listener
                rangeUpdationBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        tvMin1.setText(String.valueOf(minValue) + "%");
                        tvMax1.setText(String.valueOf(maxValue) + "%");
                    }
                });

                // set final value listener
                rangeUpdationBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                    @Override
                    public void finalValue(Number minValue, Number maxValue) {
                        Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                    }
                });
                retrieveFieldsValues();

                break;
        }

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
