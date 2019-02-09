package com.yadav.samaj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yadav.samaj.R;
import com.yadav.samaj.model.Items;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder>{

    private Context mContext;
    private List<Items> itemList;

    public ItemsAdapter(Context mContext, List<Items> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item;

        public MyViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
        }
    }

    @NonNull
    @Override
    public ItemsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.MyViewHolder holder, int position) {
        Items item = itemList.get(position);
        holder.item.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
