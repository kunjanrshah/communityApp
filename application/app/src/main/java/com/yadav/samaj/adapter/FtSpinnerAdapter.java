package com.yadav.samaj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yadav.samaj.R;

import java.util.ArrayList;

public class FtSpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> lstImages;
    ArrayList<String> lstNames;
    LayoutInflater inflter;

    public FtSpinnerAdapter(Context context, ArrayList<String> lstImages, ArrayList<String> lstNames) {
        this.context = context;
        this.lstImages = lstImages;
        this.lstNames = lstNames;

        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return lstNames.size();
    }

    @Override
    public Object getItem(int i) {
        return lstNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        ImageView icon = view.findViewById(R.id.sp_image);
        TextView names = view.findViewById(R.id.sp_text);


        try {
            names.setText(lstNames.get(i));
            Glide.with(context).load(lstImages.get(i)).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
