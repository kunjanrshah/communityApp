package com.krs.community.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.krs.community.R;

import java.util.ArrayList;

public class AtoZBottomAdapter extends BaseAdapter {

    private Context _context;
    public AtoZBottomAdapter(Context _context)
    {
        this._context=_context;
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
        AtoZViewHolder viewHolder;

        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.bottom_sheet_atoz_dialog, null);
            viewHolder = new AtoZViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (AtoZViewHolder) convertView.getTag();
        }


        return convertView;
    }


    private class AtoZViewHolder
    {
        TextView textView;
        AtoZViewHolder(View view)
        {
            textView=view.findViewById(R.id.tv_d);
        }
    }

}
