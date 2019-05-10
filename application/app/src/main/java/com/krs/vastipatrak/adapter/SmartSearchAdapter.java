package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.krs.vastipatrak.R;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmartSearchAdapter extends BaseExpandableListAdapter {

    public int previousGroup = -1;
    private Context _context;
    private List<String> header;
    private HashMap<String, String> mapChildValues;
    private EditText edt_head_name, edt_member_name, edt_email, edt_mobile, edt_local_add, edt_permanent_add, edt_pin_code, edt_bdate, edt_mdate, edt_office, edt_birth_time, edt_height_meter, edt_weight_kg, edt_created, edt_updated;
    private MaterialSpinner sp_surname, sp_samaj, sp_marital, sp_city, sp_gender, sp_native, sp_area, sp_state, sp_mosad, sp_education, sp_gotra, sp_bg, sp_main_cat, sp_sub_cat, sp_occupation, sp_activity, sp_bplace;
    private CrystalRangeSeekbar rangeAgeBar, rangeUpdationBar;
    private CheckBox chk_is_donor, chk_is_rented, chk_is_expired, chk_is_spect, chk_is_shani, chk_is_mangal;
    private ArrayAdapter<String> surnameAdapter, samajAdapter, maritalAdapter, cityAdapter, genderAdapter, nativeAdapter, mosadAdapter, educationAdapter, gotraAdapter, bgAdapter, areaAdapter, stateAdapter, categoryAdapter, subcatAdapter, occupationAdapter, curActivityAdapter, birthPlaceAdapter;
    private ExpandableListView expandableListView;

    public SmartSearchAdapter(Context context, ExpandableListView expandableListView) {
        this._context = context;
        this.expandableListView = expandableListView;

        mapChildValues = new HashMap<>();
        header = new ArrayList<>();
        header.add("");
        header.add(context.getString(R.string.main));
        header.add(context.getString(R.string.contact));
        header.add(context.getString(R.string.personal));
        header.add(context.getString(R.string.professonal));
        header.add(context.getString(R.string._matrimony));
        header.add(context.getString(R.string.some_more));
        header.add("");

        List<String> lst_gender = new ArrayList<>();
        lst_gender.add(context.getString(R.string.ss_gender));
        lst_gender.add("Male");
        lst_gender.add("Female");

        List<String> lst_native = new ArrayList<>();
        lst_native.add(context.getString(R.string.ss_native));
        lst_native.add("Native1");
        lst_native.add("Native2");

        List<String> lst_surname = new ArrayList<>();
        lst_surname.add(context.getString(R.string.ss_surname));
        lst_surname.add("surname1");
        lst_surname.add("surname2");

        List<String> lst_samaj = new ArrayList<>();
        lst_samaj.add(context.getString(R.string.ss_samaj));
        lst_samaj.add("samaj1");
        lst_samaj.add("samaj2");

        List<String> lst_city = new ArrayList<>();
        lst_city.add(context.getString(R.string.ss_city));
        lst_city.add("city1");
        lst_city.add("city2");

        List<String> lst_marital = new ArrayList<>();
        lst_marital.add(context.getString(R.string.ss_marital));
        lst_marital.add("marital1");
        lst_marital.add("marital2");

        List<String> lst_area = new ArrayList<>();
        lst_area.add(context.getString(R.string.ss_area));
        lst_area.add("area1");
        lst_area.add("area2");

        List<String> lst_state = new ArrayList<>();
        lst_state.add(context.getString(R.string.ss_state));
        lst_state.add("state1");
        lst_state.add("state2");

        List<String> lst_mosad = new ArrayList<>();
        lst_mosad.add(context.getString(R.string.ss_mosad));
        lst_mosad.add("mosad1");
        lst_mosad.add("mosad2");

        List<String> lst_education = new ArrayList<>();
        lst_education.add(context.getString(R.string.ss_education));
        lst_education.add("education1");
        lst_education.add("education2");

        List<String> lst_gotra = new ArrayList<>();
        lst_gotra.add(context.getString(R.string.ss_gotra));
        lst_gotra.add("gotra1");
        lst_gotra.add("gotra2");

        List<String> lst_bg = new ArrayList<>();
        lst_bg.add(context.getString(R.string.ss_bg));
        lst_bg.add("bg1");
        lst_bg.add("bg2");

        List<String> lst_category = new ArrayList<>();
        lst_category.add(context.getString(R.string.ss_catogory));
        lst_category.add("category1");
        lst_category.add("category2");

        List<String> lst_sub_cat = new ArrayList<>();
        lst_sub_cat.add(context.getString(R.string.ss_sub_cat));
        lst_sub_cat.add("sub_cat1");
        lst_sub_cat.add("sub_cat2");

        List<String> lst_occupation = new ArrayList<>();
        lst_occupation.add(context.getString(R.string.ss_Occupation));
        lst_occupation.add("occupation1");
        lst_occupation.add("occupation2");

        List<String> lst_activity = new ArrayList<>();
        lst_activity.add(context.getString(R.string.ss_activity));
        lst_activity.add("activity1");
        lst_activity.add("activity2");

        List<String> lst_bplace = new ArrayList<>();
        lst_bplace.add(context.getResources().getString(R.string.ss_bplace));
        lst_bplace.add("bplace1");
        lst_bplace.add("bplace2");

        birthPlaceAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_bplace);
        birthPlaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_category);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcatAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_sub_cat);
        subcatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupationAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_occupation);
        occupationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        curActivityAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_activity);
        curActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        surnameAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_surname);
        surnameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        samajAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_samaj);
        samajAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maritalAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_marital);
        maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_city);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nativeAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_native);
        nativeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mosadAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_mosad);
        mosadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_education);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gotraAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_gotra);
        gotraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bgAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_bg);
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_area);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateAdapter = new ArrayAdapter<>(_context, android.R.layout.simple_spinner_item, lst_state);
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
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.header, parent, false);
            }
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
        btn_confirm.setOnClickListener(v -> {
            storeFieldsValues();
            SmartPopUpAdapter popUpAdapter = new SmartPopUpAdapter(_context, mapChildValues);
            DialogPlus dialog = DialogPlus.newDialog(_context).setAdapter(popUpAdapter).setOnItemClickListener((dialog1, item, view1, position) -> {
            }).setExpanded(true).setFooter(R.layout.popup_footer).setHeader(R.layout.popup_header).create();
            dialog.show();
        });

        header_text.setText(headerTitle);

        // If group is expanded then change the text into bold and change the
        // icon
        if (isExpanded) {
            header_text.setBackground(_context.getResources().getDrawable(R.drawable.rounded_top_corner));
            header_text.setTypeface(null, Typeface.BOLD);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sort_up, 0);
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon
            header_text.setBackground(_context.getResources().getDrawable(R.drawable.rounded_corder));
            header_text.setTypeface(null, Typeface.NORMAL);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sort_down, 0);
        }
        return convertView;
    }

    public void storeFieldsValues() {
        if (mapChildValues != null) {
            if (edt_head_name != null) {
                mapChildValues.put(_context.getResources().getString(R.string.ss_head_name), edt_head_name.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_mem_name), edt_member_name.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_surname), sp_surname.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_samaj), sp_samaj.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_marital), sp_marital.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_city), sp_city.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_gender), sp_gender.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_native), sp_native.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_minage), rangeAgeBar.getSelectedMinValue().toString());
                mapChildValues.put(_context.getString(R.string.ss_maxage), rangeAgeBar.getSelectedMaxValue().toString());
            }
            if (edt_email != null) {
                mapChildValues.put(_context.getString(R.string.ss_edt_email), edt_email.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_mobile), edt_mobile.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_local_add), edt_local_add.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_permanent_add), edt_permanent_add.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_pin_code), edt_pin_code.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_area), sp_area.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_state), sp_state.getText().toString().trim());
            }
            if (edt_bdate != null) {
                mapChildValues.put(_context.getString(R.string.ss_edt_bdate), edt_bdate.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_mdate), edt_mdate.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_mosad), sp_mosad.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_education), sp_education.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_gotra), sp_gotra.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_bg), sp_bg.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_chk_is_donor), String.valueOf(chk_is_donor.isChecked()));
                mapChildValues.put(_context.getString(R.string.ss_chk_is_rented), String.valueOf(chk_is_rented.isChecked()));
                mapChildValues.put(_context.getString(R.string.ss_chk_is_expired), String.valueOf(chk_is_expired.isChecked()));
            }
            if (edt_office != null) {
                mapChildValues.put(_context.getString(R.string.ss_edt_office), edt_office.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_main_cat), sp_main_cat.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_sub_cat), sp_sub_cat.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_occupation), sp_occupation.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_activity), sp_activity.getText().toString().trim());
            }
            if (edt_birth_time != null) {
                mapChildValues.put(_context.getString(R.string.ss_edt_birth_time), edt_birth_time.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_height_meter), edt_height_meter.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_weight_kg), edt_weight_kg.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_sp_bplace), sp_bplace.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_chk_is_spect), String.valueOf(chk_is_spect.isChecked()));
                mapChildValues.put(_context.getString(R.string.ss_chk_is_shani), String.valueOf(chk_is_shani.isChecked()));
                mapChildValues.put(_context.getString(R.string.ss_chk_is_mangal), String.valueOf(chk_is_mangal.isChecked()));
            }
            if (edt_created != null) {
                mapChildValues.put(_context.getString(R.string.ss_edt_created), edt_created.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_edt_updated), edt_updated.getText().toString().trim());
                mapChildValues.put(_context.getString(R.string.ss_minUpdate), rangeUpdationBar.getSelectedMinValue().toString());
                mapChildValues.put(_context.getResources().getString(R.string.ss_maxUpdate), rangeUpdationBar.getSelectedMaxValue().toString());
            }
        }
    }

    private void retrieveFieldsValues() {
        if (mapChildValues != null && mapChildValues.size() > 0) {
            if (edt_head_name != null) {
                String head_name = mapChildValues.get(_context.getResources().getString(R.string.ss_head_name));
                if (head_name != null && !head_name.isEmpty()) {
                    edt_head_name.setText(head_name);
                }
                String mem_name = mapChildValues.get(_context.getResources().getString(R.string.ss_mem_name));
                if (mem_name != null && !mem_name.isEmpty()) {
                    edt_member_name.setText(mem_name);
                }
                String surname = mapChildValues.get(_context.getResources().getString(R.string.ss_sp_surname));
                if (surname != null && !surname.isEmpty()) {
                    sp_surname.setText(surname);
                }
                String samaj = mapChildValues.get(_context.getResources().getString(R.string.ss_sp_samaj));
                if (samaj != null && !samaj.isEmpty()) {
                    sp_samaj.setText(samaj);
                }
                String marital = mapChildValues.get(_context.getResources().getString(R.string.ss_sp_marital));
                if (marital != null && !marital.isEmpty()) {
                    sp_marital.setText(marital);
                }
                String city = mapChildValues.get(_context.getResources().getString(R.string.ss_sp_city));
                if (city != null && !city.isEmpty()) {
                    sp_city.setText(city);
                }
                String gender = mapChildValues.get(_context.getResources().getString(R.string.ss_sp_gender));
                if (gender != null && !gender.isEmpty()) {
                    sp_gender.setText(gender);
                }

                String native1 = mapChildValues.get(_context.getResources().getString(R.string.ss_sp_native));
                if (native1 != null && !native1.isEmpty()) {
                    sp_native.setText(native1);
                }

                String maxAge = mapChildValues.get(_context.getString(R.string.ss_maxAge));
                String minAge = mapChildValues.get(_context.getString(R.string.ss_minAge));
                if (maxAge != null && !maxAge.isEmpty()) {
                    rangeAgeBar.setMaxStartValue(Integer.parseInt(mapChildValues.get(_context.getString(R.string.ss_maxAge)))).apply();
                }
                if (minAge != null && !minAge.isEmpty()) {
                    rangeAgeBar.setMinStartValue(Integer.parseInt(mapChildValues.get(_context.getString(R.string.ss_minAge)))).apply();
                }
            }
            if (edt_email != null) {
                String email = mapChildValues.get(_context.getString(R.string.ss_edt_email));
                if (email != null && !email.isEmpty()) {
                    edt_email.setText(email);
                }
                String mobile = mapChildValues.get(_context.getString(R.string.ss_edt_mobile));
                if (mobile != null && !mobile.isEmpty()) {
                    edt_mobile.setText(mobile);
                }
                String local_add = mapChildValues.get(_context.getString(R.string.ss_edt_local_add));
                if (local_add != null && !local_add.isEmpty()) {
                    edt_local_add.setText(local_add);
                }
                String permanent_add = mapChildValues.get(_context.getString(R.string.ss_edt_permanent_add));
                if (permanent_add != null && !permanent_add.isEmpty()) {
                    edt_permanent_add.setText(permanent_add);
                }
                String pin_code = mapChildValues.get(_context.getString(R.string.ss_edt_pin_code));
                if (pin_code != null && !pin_code.isEmpty()) {
                    edt_pin_code.setText(pin_code);
                }

                String area = mapChildValues.get(_context.getString(R.string.ss_sp_area));
                if (area != null && !area.isEmpty()) {
                    sp_area.setText(area);
                }
                String state = mapChildValues.get(_context.getString(R.string.ss_sp_state));
                if (state != null && !state.isEmpty()) {
                    sp_state.setText(state);
                }
            }
            if (edt_bdate != null) {
                String bdate = mapChildValues.get(_context.getString(R.string.ss_edt_bdate));
                if (bdate != null && !bdate.isEmpty()) {
                    edt_bdate.setText(bdate);
                }

                String mdate = mapChildValues.get(_context.getString(R.string.ss_edt_mdate));
                if (mdate != null && !mdate.isEmpty()) {
                    edt_mdate.setText(mdate);
                }

                String mosad = mapChildValues.get(_context.getString(R.string.ss_sp_mosad));
                if (mosad != null && !mosad.isEmpty()) {
                    sp_mosad.setText(mosad);
                }

                String education = mapChildValues.get(_context.getString(R.string.ss_sp_education));
                if (education != null && !education.isEmpty()) {
                    sp_education.setText(education);
                }

                String gotra = mapChildValues.get(_context.getString(R.string.ss_sp_gotra));
                if (gotra != null && !gotra.isEmpty()) {
                    sp_gotra.setText(gotra);
                }

                String bg = mapChildValues.get(_context.getString(R.string.ss_sp_bg));
                if (gotra != null && !gotra.isEmpty()) {
                    sp_bg.setText(bg);
                }

                String is_doner = mapChildValues.get(_context.getString(R.string.ss_chk_is_donor));
                if (is_doner != null && is_doner.equalsIgnoreCase(_context.getString(R.string.ss_true))) {
                    chk_is_donor.setChecked(true);
                } else {
                    chk_is_donor.setChecked(false);
                }

                String is_rented = mapChildValues.get(_context.getString(R.string.ss_chk_is_rented));
                if (is_rented != null && is_rented.equalsIgnoreCase(_context.getString(R.string.ss_true))) {
                    chk_is_rented.setChecked(true);
                } else {
                    chk_is_rented.setChecked(false);
                }

                String is_expired = mapChildValues.get(_context.getString(R.string.ss_chk_is_expired));
                if (is_expired != null && is_expired.equalsIgnoreCase(_context.getString(R.string.ss_true))) {
                    chk_is_expired.setChecked(true);
                } else {
                    chk_is_expired.setChecked(false);
                }

            }
            if (edt_office != null) {
                String office = mapChildValues.get(_context.getString(R.string.ss_edt_office));
                if (office != null && !office.isEmpty()) {
                    edt_office.setText(office);
                }

                String main_cat = mapChildValues.get(_context.getString(R.string.ss_sp_main_cat));
                if (main_cat != null && !main_cat.isEmpty()) {
                    sp_main_cat.setText(main_cat);
                }

                String sub_cat = mapChildValues.get(_context.getString(R.string.ss_sp_sub_cat));
                if (sub_cat != null && !sub_cat.isEmpty()) {
                    sp_sub_cat.setText(sub_cat);
                }

                String occupation = mapChildValues.get(_context.getString(R.string.ss_sp_occupation));
                if (occupation != null && !occupation.isEmpty()) {
                    sp_occupation.setText(occupation);
                }

                String activity = mapChildValues.get(_context.getString(R.string.ss_sp_activity));
                if (activity != null && !activity.isEmpty()) {
                    sp_activity.setText(activity);
                }


            }
            if (edt_birth_time != null) {
                String birth_time = mapChildValues.get(_context.getString(R.string.ss_edt_birth_time));
                if (birth_time != null && !birth_time.isEmpty()) {
                    edt_birth_time.setText(birth_time);
                }

                String height_meter = mapChildValues.get(_context.getString(R.string.ss_edt_height_meter));
                if (height_meter != null && !height_meter.isEmpty()) {
                    edt_height_meter.setText(height_meter);
                }

                String weight_kg = mapChildValues.get(_context.getString(R.string.ss_edt_weight_kg));
                if (weight_kg != null && !weight_kg.isEmpty()) {
                    edt_weight_kg.setText(weight_kg);
                }

                String bplace = mapChildValues.get(_context.getString(R.string.ss_sp_bplace));
                if (bplace != null && !bplace.isEmpty()) {
                    sp_bplace.setText(bplace);
                }

                String is_spect = mapChildValues.get(_context.getString(R.string.ss_chk_is_spect));
                if (is_spect != null && is_spect.equalsIgnoreCase(_context.getString(R.string.ss_true))) {
                    chk_is_spect.setChecked(true);
                } else {
                    chk_is_spect.setChecked(false);
                }

                String is_shani = mapChildValues.get(_context.getString(R.string.ss_chk_is_shani));
                if (is_shani != null && is_shani.equalsIgnoreCase(_context.getString(R.string.ss_true))) {
                    chk_is_shani.setChecked(true);
                } else {
                    chk_is_shani.setChecked(false);
                }

                String is_mangal = mapChildValues.get(_context.getString(R.string.ss_chk_is_mangal));
                if (is_mangal != null && is_mangal.equalsIgnoreCase(_context.getString(R.string.ss_true))) {
                    chk_is_mangal.setChecked(true);
                } else {
                    chk_is_mangal.setChecked(false);
                }
            }
            if (edt_created != null) {
                String created = mapChildValues.get(_context.getString(R.string.ss_edt_created));
                if (created != null && !created.isEmpty()) {
                    edt_created.setText(created);
                }
                String updated = mapChildValues.get(_context.getString(R.string.ss_edt_updated));
                if (updated != null && !updated.isEmpty()) {
                    edt_updated.setText(updated);
                }


                String maxUpdate = mapChildValues.get(_context.getString(R.string.ss_maxUpdate));
                String minUpdate = mapChildValues.get(_context.getString(R.string.ss_minUpdate));
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
                    tvMin.setText("Age " + minValue);
                    tvMax.setText("Age " + maxValue);
                });

                // set final value listener
                rangeAgeBar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue)));
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
                chk_is_donor = convertView.findViewById(R.id.chk_is_donor);
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
                if (inflater != null) convertView = inflater.inflate(R.layout.see_more, null);
                edt_created = convertView.findViewById(R.id.edt_created);
                edt_updated = convertView.findViewById(R.id.edt_updated);
                rangeUpdationBar = convertView.findViewById(R.id.rangeUpdationBar);

                final TextView tvMin1 = convertView.findViewById(R.id.textMin1);
                final TextView tvMax1 = convertView.findViewById(R.id.textMax1);

                // set listener
                rangeUpdationBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                    tvMin1.setText(minValue + "%");
                    tvMax1.setText(maxValue + "%");
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
