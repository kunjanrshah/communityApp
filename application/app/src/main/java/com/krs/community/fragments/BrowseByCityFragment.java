package com.krs.community.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.ericliu.asyncexpandablelist.CollectionView;
import com.ericliu.asyncexpandablelist.async.AsyncExpandableListView;
import com.ericliu.asyncexpandablelist.async.AsyncExpandableListViewCallbacks;
import com.ericliu.asyncexpandablelist.async.AsyncHeaderViewHolder;
import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.model.City;
import com.krs.community.utils.Utility;
import com.krs.community.viewmodel.BrowseCityViewModel;
import com.krs.community.viewmodel.RegisterViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BrowseByCityFragment extends Fragment implements AsyncExpandableListViewCallbacks<String, City> {

    private AsyncExpandableListView<String, City> mAsyncExpandableListView;
    private CollectionView.Inventory<String, City> inventory;
    private BrowseCityViewModel browseCityViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        View view = layoutInflater.inflate(R.layout.fragment_browse_city, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.colorBG, false);
        }

        browseCityViewModel = ViewModelProviders.of(this,factory).get(RegisterViewModel::class.java)
        browseCityViewModel.iBrow=this;

        ImageView iv_cancel = view.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        mAsyncExpandableListView = view.findViewById(R.id.asyncExpandableCollectionView);
        mAsyncExpandableListView.setCallbacks(this);

        inventory = new CollectionView.Inventory<>();

        String[] states = new String[]{"Gujarat", "Maharashtra", "Rajashtan", "Delhi", "Madhya Pradesh"};

        for (int i = 0; i < 5; i++) {
            CollectionView.InventoryGroup<String, City> group = inventory.newGroup(i); // groupOrdinal is the smallest, displayed first
            group.setHeaderItem(states[i]);
        }
        mAsyncExpandableListView.updateInventory(inventory);
        return view;
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

    @Override
    public void onStartLoadingGroup(int groupOrdinal) {
        new LoadDataTask(groupOrdinal, mAsyncExpandableListView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public AsyncHeaderViewHolder newCollectionHeaderView(Context context, int groupOrdinal, ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.header_row_item_async, parent, false);
        return new MyHeaderViewHolder(v, groupOrdinal, mAsyncExpandableListView);
    }

    @Override
    public RecyclerView.ViewHolder newCollectionItemView(Context context, int groupOrdinal, ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.text_row_item_async, parent, false);

        return new NewsItemHolder(v);
    }

    @Override
    public void bindCollectionHeaderView(Context context, AsyncHeaderViewHolder holder, int groupOrdinal, String headerItem) {
        MyHeaderViewHolder myHeaderViewHolder = (MyHeaderViewHolder) holder;
        myHeaderViewHolder.getTextView().setText(headerItem);
    }

    @Override
    public void bindCollectionItemView(Context context, RecyclerView.ViewHolder holder, int i, City item) {
        NewsItemHolder newsItemHolder = (NewsItemHolder) holder;
        // newsItemHolder.getTextViewTitle().setText(item.getNewsTitle());
        newsItemHolder.getTextViewCity().setText(item.getCityName());

        if (item.getCityName().equalsIgnoreCase("other")) {
            newsItemHolder.getTextViewDevider().setVisibility(View.GONE);
        } else {
            newsItemHolder.getTextViewDevider().setVisibility(View.VISIBLE);
        }
    }

    private static class LoadDataTask extends AsyncTask<Void, Void, Void> {

        private final int mGroupOrdinal;
        private WeakReference<AsyncExpandableListView<String, City>> listviewRef = null;

        public LoadDataTask(int groupOrdinal, AsyncExpandableListView<String, City> listview) {
            mGroupOrdinal = groupOrdinal;
            listviewRef = new WeakReference<>(listview);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            List<City> items = new ArrayList<>();
            City news = new City();
            // news.setNewsTitle("Lawyers meet voluntary pro bono target for first time since 2013");
            news.setCityName("Ahmedabad");
            items.add(news);

            news = new City();
            //news.setNewsTitle("HSC 2016: 77000 students to sit first exams across NSW");
            news.setCityName("Gandhinagar");
            items.add(news);

            news = new City();
            //news.setNewsTitle("HSC 2016: 77000 students to sit first exams across NSW");
            news.setCityName("Other");
            items.add(news);

            if (listviewRef.get() != null) {
                listviewRef.get().onFinishLoadingGroup(mGroupOrdinal, items);
            }
        }

    }

    public class NewsItemHolder extends RecyclerView.ViewHolder {

        private final TextView tv_city;
        private final View view_devider;
        private final LinearLayout row_city;


        public NewsItemHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });

            tv_city = v.findViewById(R.id.tv_city);
            view_devider = v.findViewById(R.id.view_devider);
            row_city= v.findViewById(R.id.row_city);

            row_city.setOnClickListener(v1 -> Toast.makeText(getActivity(), ""+tv_city.getText(), Toast.LENGTH_SHORT).show());

        }

        /*public TextView getTextViewTitle() {
            return tvTitle;
        }*/
        public View getTextViewDevider() {
            return view_devider;
        }

        public TextView getTextViewCity() {
            return tv_city;
        }

    }

    public static class MyHeaderViewHolder extends AsyncHeaderViewHolder implements AsyncExpandableListView.OnGroupStateChangeListener {

        private final TextView textView;
        private final ProgressBar mProgressBar;
        private ImageView ivExpansionIndicator;

        public MyHeaderViewHolder(View v, int groupOrdinal, AsyncExpandableListView asyncExpandableListView) {
            super(v, groupOrdinal, asyncExpandableListView);
            textView = v.findViewById(R.id.title);
            mProgressBar = v.findViewById(R.id.progressBar);
            mProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF,android.graphics.PorterDuff.Mode.MULTIPLY);
            ivExpansionIndicator = v.findViewById(R.id.ivExpansionIndicator);
        }


        public TextView getTextView() {
            return textView;
        }


        @Override
        public void onGroupStartExpending() {
            mProgressBar.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setVisibility(View.GONE);
        }

        @Override
        public void onGroupExpanded() {
            mProgressBar.setVisibility(View.GONE);
            ivExpansionIndicator.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_up);
        }

        @Override
        public void onGroupCollapsed() {
            mProgressBar.setVisibility(View.GONE);
            ivExpansionIndicator.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_down);
        }
    }
}
