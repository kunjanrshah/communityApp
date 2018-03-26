package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.NavDrawerItem;

import java.util.Collections;
import java.util.List;



public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());

        switch (position)
        {
            case 0:
                holder.imgDrawer.setBackgroundResource(R.drawable.home);
                break;
            case 1:
                holder.imgDrawer.setBackgroundResource(R.drawable.user);
                break;
            case 2:
                holder.imgDrawer.setBackgroundResource(R.drawable.password);
                break;
            case 3:
                holder.imgDrawer.setBackgroundResource(R.drawable.relative);
                break;
            case 4:
                holder.imgDrawer.setBackgroundResource(R.drawable.matrimony);
                break;
            case 5:
                holder.imgDrawer.setBackgroundResource(R.drawable.sync);
                break;
            case 6:
                holder.imgDrawer.setBackgroundResource(R.drawable.tour);
                break;
            case 7:
                holder.imgDrawer.setBackgroundResource(R.drawable.about);
                break;
            case 8:
                holder.imgDrawer.setBackgroundResource(R.drawable.logout);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imgDrawer;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            imgDrawer=(ImageView)itemView.findViewById(R.id.imgDrawer);
        }
    }
}
