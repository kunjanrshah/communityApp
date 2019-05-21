package com.krs.vastipatrak.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iammert.library.ui.multisearchviewlib.MultiSearchView;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.ProfileAdapter;
import com.krs.vastipatrak.model.RecentProfiles;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment {

    private RecyclerView lstRecentSearch;
    private RecyclerView lstProfile;
    private ProfileAdapter profileAdapter;

    private ArrayList<RecentProfiles> listRecentProfiles = new ArrayList<>();
    private String[] RecentProfileNames = {"Rajendra", "Tejas", "Kunjan", "Mukund", "Kushal"};
    private int[] RecentProfileImages = {R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile, R.drawable.user_profile};
    private MultiSearchView multiSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);

        lstRecentSearch =  rootView.findViewById(R.id.lstRecentSearch);
        multiSearchView =  rootView.findViewById(R.id.multiSearchView);
        lstProfile = rootView.findViewById(R.id.lstProfile);

        multiSearchView.setSearchViewListener(new MultiSearchView.MultiSearchViewListener() {
            @Override
            public void onTextChanged(int i, CharSequence charSequence) {
               // Toast.makeText(getActivity(), "onTextChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchComplete(int i, CharSequence charSequence) {
                Toast.makeText(getActivity(), "onSearchComplete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchItemRemoved(int i) {
                Toast.makeText(getActivity(), "onSearchItemRemoved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemSelected(int i, CharSequence charSequence) {
                Toast.makeText(getActivity(), "onItemSelected", Toast.LENGTH_SHORT).show();
            }
        });

        setRecentSearch();
        setupList();
        return rootView;
    }

    private void setupList() {

        lstProfile.setLayoutManager(new LinearLayoutManager(getActivity()));

        profileAdapter = new ProfileAdapter(getActivity(), createList(20));
        lstProfile.setAdapter(profileAdapter);
    }

    private List<String> createList(int n) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            list.add("View " + i);
        }

        return list;
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
