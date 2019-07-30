package com.krs.community.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.Utility;
import com.nightonke.boommenu.BoomMenuButton;

import org.json.JSONObject;

import java.util.ArrayList;

public class AdminsFragment extends Fragment {

    ArrayList<JSONObject> lstAdmins;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_admins,container,false);

        ParallaxRecyclerAdapter<JSONObject> adapter = new ParallaxRecyclerAdapter<JSONObject>(lstAdmins) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<JSONObject> adapter, int position) {
                ListViewHolder holder= (ListViewHolder) viewHolder;
                holder.tv_name.setText("Kunjan Shah");
                holder.boomMenuButton.clearBuilders();

                for(int i=0; i<holder.boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++)
                {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder());
                }
                holder.boomMenuButton.setOnClickListener(v -> {
                    holder.boomMenuButton.boom();
                });
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter<JSONObject> adapter, int i) {
                return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_list_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<JSONObject> adapter) {
                return 10;
            }
        };

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_admins, container, false);
        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView rv_admins=root.findViewById(R.id.rv_Admins);
        rv_admins.setLayoutManager(MyLayoutManager);
        rv_admins.setItemAnimator(new DefaultItemAnimator());
        adapter.setParallaxHeader(header, rv_admins);
        rv_admins.setAdapter(adapter);
        rv_admins.setHasFixedSize(true);

        return root;
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        BoomMenuButton boomMenuButton;
        ListViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            boomMenuButton= v.findViewById(R.id.boomMenuButton);
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
