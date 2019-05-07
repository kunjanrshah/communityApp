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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.krs.vastipatrak.R;

import java.util.ArrayList;
import java.util.List;

public class SmartSearchAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> header;

    public SmartSearchAdapter(Context context)
    {
        this._context = context;
        header = new ArrayList<>();
        header.add("");
        header.add(context.getString(R.string.main));
        header.add(context.getString(R.string.contact));
        header.add(context.getString(R.string.personal));
        header.add(context.getString(R.string.professonal));
        header.add(context.getString(R.string._matrimony));
        header.add(context.getString(R.string.some_more));
        header.add("");
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
        TextView header_text =  convertView.findViewById(R.id.header);
        Button btn_confirm =  convertView.findViewById(R.id.btn_confirm);

        if (groupPosition == 0) {
            ll_title.setVisibility(View.VISIBLE);
            header_text.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.GONE);
        } else if (groupPosition == 7){
            ll_title.setVisibility(View.GONE);
            header_text.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.VISIBLE);
        }else
        {
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

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (groupPosition) {
            case 1:
                MaterialSpinner sp_native, sp_gender;
                convertView = inflater.inflate(R.layout.main_details, null);
                CrystalRangeSeekbar rangeSeekbar = convertView.findViewById(R.id.rangeSeekbar);
                final TextView tvMin = (TextView) convertView.findViewById(R.id.textMin1);
                final TextView tvMax = (TextView) convertView.findViewById(R.id.textMax1);

                sp_gender = convertView.findViewById(R.id.sp_gender);
                List<String> lst_gender = new ArrayList<String>();
                lst_gender.add("Gender");
                lst_gender.add("Male");
                lst_gender.add("Female");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_gender);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_gender.setAdapter(dataAdapter);

                sp_native = convertView.findViewById(R.id.sp_native);
                List<String> lst_native = new ArrayList<String>();
                lst_native.add("Native");
                lst_native.add("Native1");
                lst_native.add("Native2");
                dataAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, lst_native);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_native.setAdapter(dataAdapter);

                // set listener
                rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        tvMin.setText("Age " + String.valueOf(minValue));
                        tvMax.setText("Age " + String.valueOf(maxValue));
                    }
                });

                // set final value listener
                rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                    @Override
                    public void finalValue(Number minValue, Number maxValue) {
                        Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                    }
                });


                break;
            case 2:
                convertView = inflater.inflate(R.layout.contact_details, null);

                break;
            case 3:
                convertView = inflater.inflate(R.layout.personal_details, null);
                break;
            case 4:
                convertView = inflater.inflate(R.layout.professional_details, null);
                break;
            case 5:
                convertView = inflater.inflate(R.layout.matrimony_details, null);
                break;
            case 6:
                convertView = inflater.inflate(R.layout.see_more, null);
                CrystalRangeSeekbar rangeSeekbar1 = convertView.findViewById(R.id.rangeSeekbar);
                final TextView tvMin1 = (TextView) convertView.findViewById(R.id.textMin1);
                final TextView tvMax1 = (TextView) convertView.findViewById(R.id.textMax1);

                // set listener
                rangeSeekbar1.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        tvMin1.setText(String.valueOf(minValue) + "%");
                        tvMax1.setText(String.valueOf(maxValue) + "%");
                    }
                });

                // set final value listener
                rangeSeekbar1.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                    @Override
                    public void finalValue(Number minValue, Number maxValue) {
                        Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                    }
                });
                break;
        }

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
