package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.NavDrawerItem;

import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    private final List<NavDrawerItem> data;
    private final LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());

        switch (position) {
            case 0:
                holder.imgDrawer.setBackgroundResource(R.drawable.home);
                break;
            case 1:
                holder.imgDrawer.setBackgroundResource(R.drawable.analytics);
                //holder.imgDrawer.setBackgroundResource(R.drawable.user);
                break;
            case 2:
                holder.imgDrawer.setBackgroundResource(R.drawable.analytics);
                break;
            case 3:
                holder.imgDrawer.setBackgroundResource(R.drawable.vector_drawable_fav_user);
                break;
            case 4:
                holder.imgDrawer.setBackgroundResource(R.drawable.filter_icon);
                break;
            /*case 5:
                holder.imgDrawer.setBackgroundResource(R.drawable.matrimony);
                break;
            case 6:
                holder.imgDrawer.setBackgroundResource(R.drawable.calendar);
                break;
            case 7:
                holder.imgDrawer.setBackgroundResource(R.drawable.translate);
                break;
            case 8:
                holder.imgDrawer.setBackgroundResource(R.drawable.password);
                break;
            case 9:
                holder.imgDrawer.setBackgroundResource(R.drawable.tour);
                break;
            case 10:
                holder.imgDrawer.setBackgroundResource(R.drawable.help_icon);
                break;
            case 11:
                holder.imgDrawer.setBackgroundResource(R.drawable.logout);
                break;*/
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView imgDrawer;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            imgDrawer = itemView.findViewById(R.id.imgDrawer);
        }
    }
}
