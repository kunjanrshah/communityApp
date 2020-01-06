/*
package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
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

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.model.FeedItem;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.FeedImageView;
import com.krs.community.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NewsFragment extends Fragment {


    private static final String TAG = NewsFragment.class.getSimpleName();
    private RecyclerView listView;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://api.androidhive.info/feed/feed.json";
    private ShimmerFrameLayout mShimmerViewContainer;
    ParallaxRecyclerAdapter<FeedItem> adapter = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        listView = rootView.findViewById(R.id.list);
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);
        feedItems = new ArrayList<FeedItem>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.bg_gray, false);
        }

        adapter = new ParallaxRecyclerAdapter<FeedItem>(feedItems) {

            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<FeedItem> adapter, int position) {
                if (imageLoader == null) imageLoader = AppController.mApplication.getImageLoader();

                FeedItem item = feedItems.get(position);
                FeedListViewHolder holder = (FeedListViewHolder) viewHolder;
                holder.name.setText(item.getName());

                // Converting timestamp into x ago format
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(item.getTimeStamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                holder.timestamp.setText(timeAgo);

                // Chcek for empty status message
                if (!TextUtils.isEmpty(item.getStatus())) {
                    holder.statusMsg.setText(item.getStatus());
                    holder.statusMsg.setVisibility(View.VISIBLE);
                } else {
                    // status is empty, remove from view
                    holder.statusMsg.setVisibility(View.GONE);
                }

                // Checking for null feed url
                if (item.getUrl() != null) {
                    holder.url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">" + item.getUrl() + "</a> "));

                    // Making url clickable
                    holder.url.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.url.setVisibility(View.VISIBLE);
                } else {
                    // url is null, remove from the view
                    holder.url.setVisibility(View.GONE);
                }

                // user profile pic
                holder.profilePic.setImageUrl(item.getProfilePic(), imageLoader);

                // Feed image
                if (item.getImge() != null) {
                    holder.feedImageView.setImageUrl(item.getImge(), imageLoader);
                    holder.feedImageView.setVisibility(View.VISIBLE);
                    holder.feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
                } else {
                    holder.feedImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter<FeedItem> adapter, int i) {
                return new FeedListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<FeedItem> adapter) {
                return feedItems.size();
            }
        };


        // listAdapter = new FeedListAdapter(getActivity(), feedItems);


        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setLayoutManager(MyLayoutManager);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_news, container, false);

        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        adapter.setParallaxHeader(header, listView);
        listView.setAdapter(adapter);


        // We first check for cached request
        Cache cache = AppController.mApplication.getRequestQueue().getCache();
        //Cache.Entry entry = cache.get(URL_FEED);
        Cache.Entry entry = null;
        if (entry != null) {
            // fetch the data from cache
            String data = new String(entry.data, StandardCharsets.UTF_8);
            try {
                parseJsonFeed(new JSONObject(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, error -> VolleyLog.d(TAG, "Error: " + error.getMessage()));

            // Adding request to volley request queue
            AppController.mApplication.addToRequestQueue(jsonReq);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    class FeedListViewHolder extends RecyclerView.ViewHolder {
        TextView name, timestamp, statusMsg, url;
        NetworkImageView profilePic;
        FeedImageView feedImageView;

        FeedListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            timestamp = itemView.findViewById(R.id.timestamp);
            statusMsg = itemView.findViewById(R.id.txtStatusMsg);
            url = itemView.findViewById(R.id.txtUrl);
            profilePic = itemView.findViewById(R.id.profilePic);
            feedImageView = itemView.findViewById(R.id.feedImage1);
        }
    }


    */
/**
     * Parsing json reponse and passing the data to feed view list adapter
     *//*

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItems.add(item);
            }
            // stop animating Shimmer and hideOverlay the layout
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
            // notify data changes to list adapater
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
*/
