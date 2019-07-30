package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.krs.community.R;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.Utility;
import org.json.JSONObject;
import java.util.ArrayList;

public class FiltersFragment extends Fragment {

    ArrayList<JSONObject> lstFilters;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_filters,container,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
        }
        lstFilters=new ArrayList<JSONObject>();

        ParallaxRecyclerAdapter<JSONObject> adapter = new ParallaxRecyclerAdapter<JSONObject>(lstFilters) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<JSONObject> adapter, int i) {
                ((ListViewHolder) viewHolder).tv_name.setText("Filter Name");//lstFilters.get(i) // your bind holder routine.
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<JSONObject> adapter, int i) {
                return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filters_list_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<JSONObject> adapter) {
                //return lstFilters.size();
                return 10;
            }
        };


        RecyclerView rv_filters=root.findViewById(R.id.rv_filters);
        View header=LayoutInflater.from(getActivity()).inflate(R.layout.header_filters, container, false);
        ImageView iv_cancel= header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });

        adapter.setParallaxHeader(header, rv_filters);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        rv_filters.setLayoutManager(MyLayoutManager);
        rv_filters.setItemAnimator(new DefaultItemAnimator());
        rv_filters.setAdapter(adapter);
        rv_filters.setHasFixedSize(true);

        return root;
    }


    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        FlexboxLayout flexbox_layout;

        ListViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            flexbox_layout = v.findViewById(R.id.flexbox_layout);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
