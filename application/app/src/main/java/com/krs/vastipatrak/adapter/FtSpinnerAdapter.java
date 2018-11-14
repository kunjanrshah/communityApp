package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;

import java.util.ArrayList;

public class FtSpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> lstImages;
    ArrayList<String> lstNames;
    ArrayList<String> lstLevels;
    LayoutInflater inflter;

    public FtSpinnerAdapter(Context applicationContext, ArrayList<String> lstImages,ArrayList<String> lstNames,ArrayList<String> lstLevels) {
        this.context = applicationContext;
        this.lstImages = lstImages;
        this.lstNames = lstNames;
        this.lstLevels=lstLevels;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return lstNames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        ImageView icon = (ImageView) view.findViewById(R.id.sp_image);
        TextView names = (TextView) view.findViewById(R.id.sp_text);
        Glide.with(context).load(lstImages.get(i)).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(icon);
        names.setText(lstNames.get(i));
        return view;
    }
}
