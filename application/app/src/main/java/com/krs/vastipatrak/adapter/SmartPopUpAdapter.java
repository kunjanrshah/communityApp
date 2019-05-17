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
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.krs.vastipatrak.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartPopUpAdapter extends BaseAdapter {

    ICloseDialog mICloseDialog;
    private Context _context;
    private HashMap<String, String> mapChildValues;
    private EditText edt_email, edt_mobile, edt_local_add, edt_permanent_add, edt_pin_code, edt_bdate, edt_mdate, edt_office, edt_birth_time, edt_height_meter, edt_weight_kg, edt_created, edt_updated;
    private MaterialSpinner sp_area, sp_state, sp_mosad, sp_education, sp_gotra, sp_bg, sp_main_cat, sp_sub_cat, sp_occupation, sp_activity, sp_bplace;
    private CrystalRangeSeekbar rangeUpdationBar;
    private CheckBox chk_is_donor, chk_is_rented, chk_is_expired, chk_is_spect, chk_is_shani, chk_is_mangal;
    private ArrayAdapter<String> mosadAdapter, educationAdapter, gotraAdapter, bgAdapter, areaAdapter, stateAdapter, categoryAdapter, subcatAdapter, occupationAdapter, curActivityAdapter, birthPlaceAdapter;

    public SmartPopUpAdapter(Context _context, SmartSearchAdapter adapter, HashMap<String, String> stringHashMap) {
        this._context = _context;
        this.mapChildValues = stringHashMap;
        mICloseDialog = (ICloseDialog) adapter;
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
            viewHolder.rangeAgeBar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue)));
        } else {
            viewHolder = (PopUpViewHolder) convertView.getTag();
        }

        viewHolder.img_popup_close.setOnClickListener(v -> {
            mICloseDialog.PopupClose();
        });
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
        return convertView;
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
        ImageView img_popup_close, img_code_close, img_age_close, img_head_close, img_member_close, img_surname_close,
                img_samaj_close, img_gender_close, img_marital_close, img_native_close, img_city_close,
                img_email_close, img_mobile_close, img_local_add_close, img_permanent_add_close, img_pincode_close, img_area_close, img_state_close;
        LinearLayout ll_family_code, ll_head, ll_member, ll_surname, ll_samaj, ll_gender, ll_marital, ll_native, ll_city, ll_age,
                ll_email, ll_mobile, ll_local_add, ll_permanent_add, ll_pincode, ll_area, ll_state;
        EditText edt_family_code, edt_head_name, edt_member, edt_email, edt_mobile, edt_local_add, edt_permanent_add, edt_pincode;
        MaterialSpinner sp_surname, sp_samaj, sp_marital, sp_city, sp_gender, sp_native, sp_area, sp_state;
        CrystalRangeSeekbar rangeAgeBar;
        TextView tvMin, tvMax;

        PopUpViewHolder(View view) {
            flexboxLayout = view.findViewById(R.id.flexbox_layout);
            img_popup_close = view.findViewById(R.id.img_popup_close);
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
            edt_email= view.findViewById(R.id.edt_email);
            edt_mobile= view.findViewById(R.id.edt_mobile);
            edt_local_add= view.findViewById(R.id.edt_local_add);
            edt_permanent_add= view.findViewById(R.id.edt_permanent_add);
            edt_pincode= view.findViewById(R.id.edt_pincode);
            edt_family_code = view.findViewById(R.id.edt_family_code);
            edt_head_name = view.findViewById(R.id.edt_head_name);
            edt_member = view.findViewById(R.id.edt_member);
            sp_surname = view.findViewById(R.id.sp_surname);
            sp_samaj = view.findViewById(R.id.sp_samaj);
            sp_marital = view.findViewById(R.id.sp_marital);
            sp_city = view.findViewById(R.id.sp_city);
            sp_gender = view.findViewById(R.id.sp_gender);
            sp_native = view.findViewById(R.id.sp_native);
            sp_area= view.findViewById(R.id.sp_area);
            sp_state= view.findViewById(R.id.sp_state);
            rangeAgeBar = view.findViewById(R.id.rangeSeekbar);
            tvMin = view.findViewById(R.id.textMin1);
            tvMax = view.findViewById(R.id.textMax1);
        }
    }


}
