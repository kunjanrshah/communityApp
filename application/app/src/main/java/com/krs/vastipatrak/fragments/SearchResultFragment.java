package com.krs.vastipatrak.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.RecentProfiles;

import java.util.ArrayList;

public class SearchResultFragment extends Fragment {

    RecyclerView lstRecentSearch;
    ArrayList<RecentProfiles> listRecentProfiles = new ArrayList<>();
    String[] RecentProfileNames = {"Rajendra", "Tejas", "Kunjan", "Mukund", "Kushal"};
    int[] RecentProfileImages = {R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);

        lstRecentSearch =  rootView.findViewById(R.id.lstRecentSearch);

        setRecentSearch();
        return rootView;
    }

    private void setRecentSearch() {
        listRecentProfiles.clear();
        for(int i = 0; i< RecentProfileNames.length; i++){
            RecentProfiles item = new RecentProfiles();
            item.setCardName(RecentProfileNames[i]);
            item.setImageResourceId(RecentProfileImages[i]);
            listRecentProfiles.add(item);
        }

        lstRecentSearch.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (listRecentProfiles.size() > 0 & lstRecentSearch != null) {
            lstRecentSearch.setAdapter(new RecentProfileAdapter(listRecentProfiles));
        }
        lstRecentSearch.setLayoutManager(MyLayoutManager);
    }

    public class RecentProfileAdapter extends RecyclerView.Adapter<RecentActivityViewHolder> {
        private ArrayList<RecentProfiles> list;

        RecentProfileAdapter(ArrayList<RecentProfiles> Data) {
            list = Data;
        }

        @Override
        public RecentActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_menu, parent, false);
            return new RecentActivityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecentActivityViewHolder holder, int position) {

            holder.titleTextView.setText(list.get(position).getCardName());
            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
            holder.coverImageView.setTag(list.get(position).getImageResourceId());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class RecentActivityViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView coverImageView;

        RecentActivityViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            coverImageView = v.findViewById(R.id.coverImageView);
        }
    }

}
