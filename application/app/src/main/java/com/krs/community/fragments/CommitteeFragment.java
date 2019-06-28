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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.Utility;
import com.nightonke.boommenu.BoomMenuButton;

public class CommitteeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_committee,container,false);

        RecyclerView listCommittee=root.findViewById(R.id.listCommittee);
        ImageView filter=root.findViewById(R.id.filter);
        filter.setOnClickListener(v -> {
            openFilter();
        });

        ImageView iv_cancel=root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });

        ListCommitteeAdapter mAdapter=new ListCommitteeAdapter();
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        listCommittee.setLayoutManager(MyLayoutManager);
        listCommittee.setItemAnimator(new DefaultItemAnimator());
        listCommittee.setAdapter(mAdapter);
        listCommittee.setHasFixedSize(true);

        return root;
    }

    private void openFilter()
    {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filter_committee);

        ImageView iv_cancel=dialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Button btn_search=dialog.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    public class ListCommitteeAdapter extends RecyclerView.Adapter<ListViewHolder>
    {
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

            for(int i=0; i<holder.boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++)
            {
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
            boomMenuButton= v.findViewById(R.id.boomMenuButton);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
    }
}
