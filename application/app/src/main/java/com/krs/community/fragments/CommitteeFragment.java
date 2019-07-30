package com.krs.community.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class CommitteeFragment extends Fragment {


    ArrayList<JSONObject> lstCommitee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_committee, container, false);

        lstCommitee = new ArrayList<JSONObject>();

        ParallaxRecyclerAdapter<JSONObject> adapter = new ParallaxRecyclerAdapter<JSONObject>(lstCommitee) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<JSONObject> adapter, int position) {
                ListViewHolder holder = (ListViewHolder) viewHolder;

                holder.tv_name.setText("Kunjan Shah");
                holder.boomMenuButton.clearBuilders();

                for (int i = 0; i < holder.boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder());
                }
                holder.boomMenuButton.setOnClickListener(v -> {
                    holder.boomMenuButton.boom();
                });

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<JSONObject> adapter, int i) {
                return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.committee_list_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<JSONObject> adapter) {
                //return lstFilters.size();
                return 10;
            }
        };

        //ListCommitteeAdapter mAdapter=new ListCommitteeAdapter();
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView listCommittee = root.findViewById(R.id.listCommittee);
        listCommittee.setLayoutManager(MyLayoutManager);
        listCommittee.setItemAnimator(new DefaultItemAnimator());
        listCommittee.setHasFixedSize(true);

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_committees, container, false);
        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        TextView txt_region = header.findViewById(R.id.txt_region);
        TextView txt_duration = header.findViewById(R.id.txt_duration);
        TextView txt_committee = header.findViewById(R.id.txt_committee);
        TextView txt_designation = header.findViewById(R.id.txt_designation);

        LinearLayout ll_region = header.findViewById(R.id.ll_region);
        LinearLayout ll_duration = header.findViewById(R.id.ll_duration);
        LinearLayout ll_committee = header.findViewById(R.id.ll_committee);
        LinearLayout ll_designation = header.findViewById(R.id.ll_designation);


        ImageView img_region_close = header.findViewById(R.id.img_region_close);
        img_region_close.setOnClickListener(v -> {
            ll_region.setVisibility(View.GONE);
        });

        ImageView img_duration_close = header.findViewById(R.id.img_duration_close);
        img_duration_close.setOnClickListener(v -> {
            ll_duration.setVisibility(View.GONE);
        });

        ImageView img_committee_close = header.findViewById(R.id.img_committee_close);
        img_committee_close.setOnClickListener(v -> {
            ll_committee.setVisibility(View.GONE);
        });

        ImageView img_designation_close = header.findViewById(R.id.img_designation_close);
        img_designation_close.setOnClickListener(v -> {
            ll_designation.setVisibility(View.GONE);
        });


        ImageView filter = header.findViewById(R.id.filter);
        filter.setOnClickListener(v -> {
            openFilter();
        });


        adapter.setParallaxHeader(header, listCommittee);
        listCommittee.setAdapter(adapter);

        return root;
    }

    private void openFilter() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filter_committee);

        ImageView iv_cancel = dialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Button btn_search = dialog.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
       // DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
       // DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
    }

    public class ListCommitteeAdapter extends RecyclerView.Adapter<ListViewHolder> {
        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.committee_list_item, parent, false);
            return new ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
            holder.tv_name.setText("Kunjan Shah");
            holder.boomMenuButton.clearBuilders();

            for (int i = 0; i < holder.boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
                holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder());
            }
            holder.boomMenuButton.setOnClickListener(v -> {
                holder.boomMenuButton.boom();
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        BoomMenuButton boomMenuButton;

        ListViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            boomMenuButton = v.findViewById(R.id.boomMenuButton);
        }
    }
}
