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
import com.krs.community.utils.Utility;
import com.nightonke.boommenu.BoomMenuButton;

public class AdminsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_admins,container,false);

        ImageView iv_cancel=root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });

        ListAdminAdapter mAdapter=new ListAdminAdapter();
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView listAdmin=root.findViewById(R.id.listAdmin);
        listAdmin.setLayoutManager(MyLayoutManager);
        listAdmin.setItemAnimator(new DefaultItemAnimator());
        listAdmin.setAdapter(mAdapter);
        listAdmin.setHasFixedSize(true);

        return root;
    }

    public class ListAdminAdapter extends RecyclerView.Adapter<ListViewHolder>
    {
        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_list_item, parent, false);
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
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}
