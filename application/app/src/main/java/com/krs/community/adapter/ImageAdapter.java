package com.krs.community.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.community.R;
import com.krs.community.app.AppController;

public class ImageAdapter extends BaseAdapter {

    private Context _context;
    private String path;
    public ImageAdapter(Context _context,String path)
    {
        this._context = _context;
        this.path=path;
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
        ImageViewHolder viewHolder;
        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.image_dialog, null);
            viewHolder = new ImageViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageViewHolder) convertView.getTag();
        }
        try {
            Glide.with(_context).load(path).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(viewHolder.imageView);
        } catch (Exception e) {
            e.getMessage();
        }

        return convertView;
    }



    private class ImageViewHolder {
        ImageView imageView;

        ImageViewHolder(View view) {
            imageView= view.findViewById(R.id.imgView);
        }
    }

}
