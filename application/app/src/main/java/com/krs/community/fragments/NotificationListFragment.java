package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.krs.community.utils.Utility;

public class NotificationListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragmnet_notification, container, false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.bg_gray, false);
        }

        ImageView iv_cancel = root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        NotificationAdapter mAdapter = new NotificationAdapter();
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView rv_notification = root.findViewById(R.id.rv_notification);
        rv_notification.setLayoutManager(MyLayoutManager);
        rv_notification.setItemAnimator(new DefaultItemAnimator());
        rv_notification.setAdapter(mAdapter);
        rv_notification.setHasFixedSize(true);

        return root;
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

    public class NotificationAdapter extends RecyclerView.Adapter<ListViewHolder> {

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
            return new ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

            holder.tv_date.setText("26/07/2019");

            LayoutInflater layoutInflater = getLayoutInflater();
            View view;
            holder.ll_parent.removeAllViews();
            if (position == 0) {
                view = layoutInflater.inflate(R.layout.notification_item1, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_devider, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_item2, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_devider, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_item2, holder.ll_parent, false);
                holder.ll_parent.addView(view);
            } else if (position == 1) {
                view = layoutInflater.inflate(R.layout.notification_item1, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_devider, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_item1, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_devider, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_item1, holder.ll_parent, false);
                holder.ll_parent.addView(view);
            } else if (position == 2) {
                view = layoutInflater.inflate(R.layout.notification_item2, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_devider, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_item1, holder.ll_parent, false);
                holder.ll_parent.addView(view);
            } else {
                view = layoutInflater.inflate(R.layout.notification_item1, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_devider, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_item2, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_devider, holder.ll_parent, false);
                holder.ll_parent.addView(view);
                view = layoutInflater.inflate(R.layout.notification_item1, holder.ll_parent, false);
                holder.ll_parent.addView(view);
            }
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date;
        LinearLayout ll_parent;

        ListViewHolder(View v) {
            super(v);
            tv_date = v.findViewById(R.id.tv_date);
            ll_parent = v.findViewById(R.id.ll_parent);
        }
    }

}
