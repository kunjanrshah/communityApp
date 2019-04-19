package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.HomeActivity;

import java.util.ArrayList;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Drawable> lstMenuDrawables;
    private ArrayList<String> lstMenuNames;
    private HomeActivity.RecyclerViewClickListener mListener;
    public MenuAdapter(Context context, HomeActivity.RecyclerViewClickListener mListener) {
        this.mContext = context;
        this.mListener=mListener;
        lstMenuDrawables=new ArrayList<>();
        lstMenuNames=new ArrayList<>();
        addLstMenuDrawables();
        addLstMenuNames();
    }

    private void addLstMenuDrawables() {

        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.icon_menu));


        /*lstMenuDrawables.add(mContext.getDrawable(R.drawable.my_profile));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.advance_search)); //quick search
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.advance_search)); //favorite search
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.calendar_blue));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.alphabatic_search));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.advance_search)); //search by city
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.advance_search));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.near_by_search));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.near_by));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.scan_qr_code));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.qr_code_image_blue));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.events));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.matrimony_blue));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.matrimony_form));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.matrimony_form));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.add_new));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.advance_search));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.advance_search));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.advance_search));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.change_color));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.change_password));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.app_tour));
        lstMenuDrawables.add(mContext.getDrawable(R.drawable.help));*/
    }

    private void addLstMenuNames() {
        lstMenuNames.add(mContext.getResources().getString(R.string.my_profile));
        lstMenuNames.add(mContext.getResources().getString(R.string.quick_search));
        lstMenuNames.add(mContext.getResources().getString(R.string.favorite_search));
        lstMenuNames.add(mContext.getResources().getString(R.string.calendar));
        lstMenuNames.add(mContext.getResources().getString(R.string.alphabetic_search));
        lstMenuNames.add(mContext.getResources().getString(R.string.search_by_city));
        lstMenuNames.add(mContext.getResources().getString(R.string.advance_search));
        lstMenuNames.add(mContext.getResources().getString(R.string.search_by_distance));
        lstMenuNames.add(mContext.getResources().getString(R.string.shared_profile));
        lstMenuNames.add(mContext.getResources().getString(R.string.scan_qr_code));
        lstMenuNames.add(mContext.getResources().getString(R.string.qr_code_image));
        lstMenuNames.add(mContext.getResources().getString(R.string.events));
        lstMenuNames.add(mContext.getResources().getString(R.string.matrimony));
        lstMenuNames.add(mContext.getResources().getString(R.string.matrimony_form));
        lstMenuNames.add(mContext.getResources().getString(R.string.medical_form));
        lstMenuNames.add(mContext.getResources().getString(R.string.add_new));
        lstMenuNames.add(mContext.getResources().getString(R.string.nonActives));
        lstMenuNames.add(mContext.getResources().getString(R.string.share_events));
        lstMenuNames.add(mContext.getResources().getString(R.string.change_language));
        lstMenuNames.add(mContext.getResources().getString(R.string.app_theme));
        lstMenuNames.add(mContext.getResources().getString(R.string.change_password));
        lstMenuNames.add(mContext.getResources().getString(R.string.app_tour));
        lstMenuNames.add(mContext.getResources().getString(R.string.help));
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mContentView = LayoutInflater.from(mContext).inflate(R.layout.row_menu, viewGroup, false);
        return new ViewHolder(mContentView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        holder.ivMenu.setBackground(lstMenuDrawables.get(position));
        holder.txtMenuName.setText(lstMenuNames.get(position));
    }

    @Override
    public int getItemCount() {
        return lstMenuDrawables.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView ivMenu;
            TextView txtMenuName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivMenu=itemView.findViewById(R.id.ivMenu);
            txtMenuName=itemView.findViewById(R.id.txtMenuName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
