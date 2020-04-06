package com.krs.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.krs.community.R;

import java.util.ArrayList;
import java.util.List;

public class MyRoleAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    iChangeRoleListner changeRoleListner = null;
    private List<String> lstRole;
    private ArrayAdapter<String> roleAdapter;

    public MyRoleAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        lstRole = new ArrayList<>();
        lstRole.add(mContext.getString(R.string.select));
        lstRole.add(mContext.getString(R.string.USER));
        lstRole.add(mContext.getString(R.string.localAdmin));
        lstRole.add(mContext.getString(R.string.subAdmin));
        roleAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, lstRole);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


    }

    public void setChangeRoleListner(iChangeRoleListner changeRoleListner) {
        this.changeRoleListner = changeRoleListner;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.my_role_bottom_sheet, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.spRole.setAdapter(roleAdapter);

        viewHolder.btnChange.setOnClickListener(v -> {
            String role = lstRole.get(viewHolder.spRole.getSelectedIndex());
            changeRoleListner.changeRole(role);
        });

        viewHolder.ivCancel.setOnClickListener(v -> {
            changeRoleListner.cancelDialog();
        });

        return convertView;
    }

    public interface iChangeRoleListner {
        void changeRole(String role);

        void cancelDialog();
    }

    static class ViewHolder {

        AppCompatButton btnChange;
        MaterialSpinner spRole;
        ImageView ivCancel;

        ViewHolder(View view) {
            spRole = view.findViewById(R.id.sp_role1);
            btnChange = view.findViewById(R.id.btn_change);
            ivCancel = view.findViewById(R.id.iv_cancel);
        }
    }
}
