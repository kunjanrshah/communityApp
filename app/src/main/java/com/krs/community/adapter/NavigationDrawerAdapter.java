package com.krs.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.community.R;
import com.krs.community.model.NavDrawerItem;

import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    private final List<NavDrawerItem> data;
    private final LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
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
                holder.imgDrawer.setBackgroundResource(R.drawable.home_primary);
                holder.llItem.setBackgroundResource(R.drawable.right_round_corner);
                holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case 1:
                holder.imgDrawer.setBackgroundResource(R.drawable.filter_outline);
                break;
            case 2:
                holder.imgDrawer.setBackgroundResource(R.drawable.analytics);
                break;
            case 3:
                holder.imgDrawer.setBackgroundResource(R.drawable.committee1);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView imgDrawer;
        final LinearLayout llItem;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.ll_item);
            title = itemView.findViewById(R.id.title);
            imgDrawer = itemView.findViewById(R.id.imgDrawer);
        }
    }
}
