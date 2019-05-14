package com.krs.vastipatrak.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.AlignSelf;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.krs.vastipatrak.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartPopUpAdapter extends BaseAdapter {

    private Context _context;
    private HashMap<String, String> mapChildValues;
    private EditText edt_email, edt_mobile, edt_local_add, edt_permanent_add, edt_pin_code, edt_bdate, edt_mdate, edt_office, edt_birth_time, edt_height_meter, edt_weight_kg, edt_created, edt_updated;
    private MaterialSpinner sp_area, sp_state, sp_mosad, sp_education, sp_gotra, sp_bg, sp_main_cat, sp_sub_cat, sp_occupation, sp_activity, sp_bplace;
    private CrystalRangeSeekbar rangeUpdationBar;
    private CheckBox chk_is_donor, chk_is_rented, chk_is_expired, chk_is_spect, chk_is_shani, chk_is_mangal;
    private ArrayAdapter<String> mosadAdapter, educationAdapter, gotraAdapter, bgAdapter, areaAdapter, stateAdapter, categoryAdapter, subcatAdapter, occupationAdapter, curActivityAdapter, birthPlaceAdapter;
    ICloseDialog mICloseDialog;

    public SmartPopUpAdapter(Context _context,SmartSearchAdapter adapter, HashMap<String, String> stringHashMap) {
        this._context = _context;
        this.mapChildValues = stringHashMap;
        mICloseDialog=(ICloseDialog)adapter;
        /*List<String> lst_area = new ArrayList<>();
        lst_area.add("area");
        lst_area.add("area1");
        lst_area.add("area2");

        List<String> lst_state = new ArrayList<>();
        lst_state.add("state");
        lst_state.add("state1");
        lst_state.add("state2");

        List<String> lst_mosad = new ArrayList<>();
        lst_mosad.add("mosad");
        lst_mosad.add("mosad1");
        lst_mosad.add("mosad2");

        List<String> lst_education = new ArrayList<>();
        lst_education.add("education");
        lst_education.add("education1");
        lst_education.add("education2");

        List<String> lst_gotra = new ArrayList<>();
        lst_gotra.add("gotra");
        lst_gotra.add("gotra1");
        lst_gotra.add("gotra2");

        List<String> lst_bg = new ArrayList<>();
        lst_bg.add("bg");
        lst_bg.add("bg1");
        lst_bg.add("bg2");

        List<String> lst_category = new ArrayList<>();
        lst_category.add("category");
        lst_category.add("category1");
        lst_category.add("category2");

        List<String> lst_sub_cat = new ArrayList<>();
        lst_sub_cat.add("sub_cat");
        lst_sub_cat.add("sub_cat1");
        lst_sub_cat.add("sub_cat2");

        List<String> lst_occupation = new ArrayList<>();
        lst_occupation.add("occupation");
        lst_occupation.add("occupation1");
        lst_occupation.add("occupation2");

        List<String> lst_activity = new ArrayList<>();
        lst_activity.add("activity");
        lst_activity.add("activity1");
        lst_activity.add("activity2");

        List<String> lst_bplace = new ArrayList<>();
        lst_bplace.add("bplace");
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
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_smart_popup, null);
        }
        FlexboxLayout flexboxLayout = convertView.findViewById(R.id.flexbox_layout);
        flexboxLayout.setFlexDirection(FlexDirection.ROW);

        View view = flexboxLayout.getChildAt(0);
        FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) view.getLayoutParams();
        lp.setFlexGrow(1.0f);
        lp.setAlignSelf(AlignItems.FLEX_START);
        view.setLayoutParams(lp);

        ImageView img_popup_close=convertView.findViewById(R.id.img_popup_close);
        img_popup_close.setOnClickListener(v -> {
            mICloseDialog.PopupClose();
        });

        EditText edt_family_code = convertView.findViewById(R.id.edt_family_code);
        EditText edt_head_name = convertView.findViewById(R.id.edt_head_name);
        EditText edt_member = convertView.findViewById(R.id.edt_member);
        MaterialSpinner sp_surname = convertView.findViewById(R.id.sp_surname);
        MaterialSpinner sp_samaj = convertView.findViewById(R.id.sp_samaj);
        MaterialSpinner sp_marital = convertView.findViewById(R.id.sp_marital);
        MaterialSpinner sp_city = convertView.findViewById(R.id.sp_city);
        MaterialSpinner sp_gender = convertView.findViewById(R.id.sp_gender);
        MaterialSpinner sp_native = convertView.findViewById(R.id.sp_native);
        CrystalRangeSeekbar rangeAgeBar = convertView.findViewById(R.id.rangeSeekbar);
        final TextView tvMin = convertView.findViewById(R.id.textMin1);
        final TextView tvMax = convertView.findViewById(R.id.textMax1);
        LinearLayout ll_age = convertView.findViewById(R.id.ll_age);
        // set listener
        rangeAgeBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            if (minValue.intValue() > 0 || maxValue.intValue() < 100) {
                ll_age.setVisibility(View.VISIBLE);
                rangeAgeBar.setVisibility(View.VISIBLE);
                tvMin.setText("Age " + minValue);
                tvMax.setText("Age " + maxValue);
            }
        });

        // set final value listener
        rangeAgeBar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue)));

        for (Map.Entry<String, String> entry : mapChildValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().trim();
            System.out.println(entry.getKey() + " = " + entry.getValue());

            if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_family_code))) {
                if (!value.isEmpty()) {
                    edt_family_code.setVisibility(View.VISIBLE);
                    edt_family_code.setText(entry.getValue());
                }
            }
            else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_head_name))) {
                if (!value.isEmpty()) {
                    edt_head_name.setVisibility(View.VISIBLE);
                    edt_head_name.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_mem_name))) {
                if (!value.isEmpty()) {
                    edt_member.setVisibility(View.VISIBLE);
                    edt_member.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_surname))) {
                if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_surname))) {
                    List<String> lst_surname = new ArrayList<>();
                    lst_surname.add(_context.getString(R.string.ss_surname));
                    lst_surname.add("surname1");
                    lst_surname.add("surname2");
                    ArrayAdapter<String> surnameAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_surname);
                    surnameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_surname.setAdapter(surnameAdapter);
                    sp_surname.setVisibility(View.VISIBLE);
                    sp_surname.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_samaj))) {
                if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_samaj))) {
                    List<String> lst_samaj = new ArrayList<>();
                    lst_samaj.add(_context.getString(R.string.ss_samaj));
                    lst_samaj.add("samaj1");
                    lst_samaj.add("samaj2");
                    ArrayAdapter<String> samajAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_samaj);
                    samajAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_samaj.setAdapter(samajAdapter);
                    sp_samaj.setVisibility(View.VISIBLE);
                    sp_samaj.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_marital))) {
                if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_marital))) {
                    List<String> lst_marital = new ArrayList<>();
                    lst_marital.add(_context.getString(R.string.ss_marital));
                    lst_marital.add("marital1");
                    lst_marital.add("marital2");

                    ArrayAdapter<String> maritalAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_marital);
                    maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_marital.setAdapter(maritalAdapter);
                    sp_marital.setVisibility(View.VISIBLE);
                    sp_marital.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_city))) {
                if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_city))) {
                    List<String> lst_city = new ArrayList<>();
                    lst_city.add(_context.getString(R.string.ss_city));
                    lst_city.add("city1");
                    lst_city.add("city2");
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_city);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_city.setAdapter(cityAdapter);
                    sp_city.setVisibility(View.VISIBLE);
                    sp_city.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_gender))) {

                if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_gender))) {
                    List<String> lst_gender = new ArrayList<>();
                    lst_gender.add(_context.getString(R.string.ss_gender));
                    lst_gender.add("Male");
                    lst_gender.add("Female");

                    ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_gender);
                    genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_gender.setAdapter(genderAdapter);
                    sp_gender.setVisibility(View.VISIBLE);
                    sp_gender.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_sp_native))) {

                if (!value.isEmpty() && !value.equalsIgnoreCase(_context.getString(R.string.ss_native))) {
                    List<String> lst_native = new ArrayList<>();
                    lst_native.add(_context.getString(R.string.ss_native));
                    lst_native.add("Native1");
                    lst_native.add("Native2");

                    ArrayAdapter<String> nativeAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_native);
                    nativeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_native.setAdapter(nativeAdapter);
                    sp_native.setVisibility(View.VISIBLE);
                    sp_native.setText(entry.getValue());
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_minage))) {
                if (!value.isEmpty()) {
                    rangeAgeBar.setMinStartValue(Integer.parseInt(value)).apply();
                }
            } else if (key.equalsIgnoreCase(_context.getResources().getString(R.string.ss_maxage))) {
                if (!value.isEmpty()) {
                    rangeAgeBar.setMaxStartValue(Integer.parseInt(value)).apply();
                }
            }
        }


        return convertView;
    }

    interface ICloseDialog
    {
        void PopupClose();
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


}
