package com.krs.community.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.community.R;
import com.krs.community.activity.FamilyTreeDetailActivity;
import com.krs.community.app.AppController;
import com.krs.community.model.NavDrawerItem;
import com.krs.community.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddRelativeFragment extends Fragment {

    private String[] titles = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_add_relative, container, false);
        titles = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.add_relative_labels);

        AppController mApp = (AppController) getApplicationContext();
        mApp.firebaseAnalytics(getContext(), "Add Relative Activity");
        mApp.facebookAnalytics(getContext(), "Add Relative Activity");

        RecyclerView rv_relation = root.findViewById(R.id.rv_relation);

        rv_relation.setAdapter(new AddRelativeAdapter(getData()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_relation.setLayoutManager(layoutManager);
        rv_relation.setItemAnimator(new DefaultItemAnimator());
        rv_relation.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.bg_gray, false);
        }

        ImageView iv_cancel = root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), FamilyTreeDetailActivity.class);
                startActivity(mIntent);
                getActivity().finish();
                Utility.fade(getActivity());
            }
        });


        return root;

    }

    private List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        // preparing navigation drawer items
        assert titles != null;
        for (String title : titles) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(title);
            data.add(navItem);
        }
        return data;
    }

    class AddRelativeAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private final List<NavDrawerItem> data;
        private final LayoutInflater inflater;

        public AddRelativeAdapter(List<NavDrawerItem> data) {
            inflater = LayoutInflater.from(getActivity());
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.add_relative_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            NavDrawerItem current = data.get(position);
            holder.title.setText(current.getTitle());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
