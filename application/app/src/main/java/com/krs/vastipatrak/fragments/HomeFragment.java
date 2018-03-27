package com.krs.vastipatrak.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.EventlistActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.interfaces.OnItemClickListener;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


public class HomeFragment extends Fragment {

    RecyclerView mRecycleView;
    Realm realm;
    eve
    String TAG = "HomeFragment";
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    RealmResults<ListEventData> eventData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_events);

        MemoryAllocation(rootView);

        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEvents();
            }
        });

        getEvents();
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        mRecycleView = rootView.findViewById(R.id.recycler_view);
        mWaveSwipeRefreshLayout = rootView.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(getResources().getColor(R.color.colorPrimary));
        realm = AppController.getInstance().realm;
        eventData = realm.where(ListEventData.class).findAll();
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(Common.Constant_Class.FragmentSp, HomeFragment.class.getSimpleName());
        mEditor.commit();
    }

    private void getEvents() {

        if (Common.isOnline(getActivity())) {
            mWaveSwipeRefreshLayout.setRefreshing(true);
            JSONObject mJsonObject = new JSONObject();


            try {
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                if (eventData.size() > 1) {
                    String date = eventData.get(eventData.size() - 1).getEventDate();
                    mJsonObject.put(Common.Constant_Class.EVENT_DATE, date);
                }
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.EVENTS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success) {
                            JSONArray mJsonArray = response.getJSONArray("data");
                            RealmList<String> YoutubeUrls = null, ImagesUrls = null;
                            ListEventData mEventdata = new ListEventData();

                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mjson = mJsonArray.getJSONObject(i);
                                mEventdata.setId(mjson.getString("id"));
                                mEventdata.setTitle(mjson.getString("title"));
                                mEventdata.setDescription(mjson.getString("description"));
                                mEventdata.setLocation(mjson.getString("location"));
                                mEventdata.setEventDate(mjson.getString("event_date"));
                                mEventdata.setLat(mjson.getString("lat"));
                                mEventdata.setLng(mjson.getString("lng"));

                                JSONArray youtubeArray = mjson.getJSONArray("youtube_url");
                                if (youtubeArray != null && youtubeArray.length() > 0) {
                                    YoutubeUrls = new RealmList<>();
                                    for (int j = 0; j < youtubeArray.length(); j++) {
                                        String YUrl = youtubeArray.getString(j);
                                        YoutubeUrls.add(YUrl);
                                    }
                                    mEventdata.setYoutubeUrl(YoutubeUrls);
                                }

                                JSONArray ImagesArray = mjson.getJSONArray("images");
                                if (ImagesArray != null && ImagesArray.length() > 0) {
                                    ImagesUrls = new RealmList<>();
                                    for (int k = 0; k < ImagesArray.length(); k++) {
                                        String IUrl = ImagesArray.getString(k);
                                        ImagesUrls.add(IUrl);
                                    }
                                    mEventdata.setImages(ImagesUrls);
                                }
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(mEventdata);
                                realm.commitTransaction();
                            }
                        }

                        setEventAdapter();
                        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Common.hideProgressDialog();
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    String message = null;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        } else {
            setEventAdapter();
        }
    }

    private void setEventAdapter() {

        EventAdapter mEventListAdapter = new EventAdapter(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                eventData = realm.where(ListEventData.class).findAll();
                int i = eventData.get(position).getImages().size();
                int j = eventData.get(position).getYoutubeUrl().size();
                if (i > 0 || j > 0) {
                    Intent mIntent = new Intent(getActivity(), EventlistActivity.class);
                    mIntent.putExtra("", position);
                    startActivity(mIntent);

                } else {
                    Toast.makeText(getActivity(), "Event Details not found!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mEventListAdapter);
        mWaveSwipeRefreshLayout.setRefreshing(false);
    }

    private void getRandomColor(int min, int max, LinearLayout ll_event) {
        int i = (new Random()).nextInt((max - min) + 1) + min;
        Log.v("color number:", "" + i);
        ll_event.setAlpha((float) 0.9);
        switch (i) {
            case 1:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape1));
                break;
            case 2:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape2));
                break;
            case 3:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape3));
                break;
            case 4:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape4));
                break;
            case 5:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape5));
                break;
            case 6:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape6));
                break;
            case 7:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape7));
                break;
            case 8:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape8));
                break;
            case 9:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape9));
                break;
            case 10:
                ll_event.setBackground(getResources().getDrawable(R.drawable.shape10));
                break;
        }

    }


    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {


        private final OnItemClickListener listener;


        public EventAdapter(OnItemClickListener listener) {
            eventData = realm.where(ListEventData.class).findAll();
            this.listener = listener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_event_group, parent, false);
            final MyViewHolder holder = new MyViewHolder(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, holder.getPosition());
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            ListEventData data = eventData.get(position);
            holder.txtTitle.setText(data.getTitle());
            holder.txtDesc.setText(data.getDescription());
            holder.txtLocation.setText(data.getLocation());
            holder.txtEventDate.setText(data.getEventDate());
            getRandomColor(1, 10, holder.ll_event);
        }

        @Override
        public int getItemCount() {
            if (eventData != null && eventData.size() > 0) {
                return eventData.size();
            } else {
                mRecycleView.setVisibility(View.GONE);
                return 0;
            }

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtTitle;
            public TextView txtDesc;
            public TextView txtLocation;
            public TextView txtEventDate;
            public LinearLayout ll_event;

            public MyViewHolder(View view) {
                super(view);
                txtTitle = view.findViewById(R.id.tvEventTitle);
                txtDesc = view.findViewById(R.id.tvEventDesc);
                txtLocation = view.findViewById(R.id.tvEventLocation);
                txtEventDate = view.findViewById(R.id.tvEventDate);
                ll_event = view.findViewById(R.id.ll_event);
            }
        }
    }
}
