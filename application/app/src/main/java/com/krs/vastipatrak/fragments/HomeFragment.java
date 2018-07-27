package com.krs.vastipatrak.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

import static com.krs.vastipatrak.utils.Common.DatetoString;
import static com.krs.vastipatrak.utils.Common.getRandomColor;
import static com.krs.vastipatrak.utils.Common.parseDateToddMMyyyy;


public class HomeFragment extends Fragment {

    @NonNull
    private final String TAG = "HomeFragment";
    private RecyclerView mRecycleView;
    private Realm realm;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private RealmResults<ListEventData> eventData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(R.string.title_events);

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
        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(Common.Constant_Class.FragmentSp, HomeFragment.class.getSimpleName());
        mEditor.apply();
    }



    private void getEvents() {

        if (Common.isOnline(Objects.requireNonNull(getActivity()))) {
            mWaveSwipeRefreshLayout.setRefreshing(true);
            JSONObject mJsonObject = new JSONObject();


            try {
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                if (eventData.size() > 1) {
                    String date = DatetoString(Objects.requireNonNull(eventData.get(eventData.size() - 1)).getEventDate());
                    mJsonObject.put(Common.Constant_Class.EVENT_DATE, date);
                }
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.EVENTS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);

                        if (success) {
                            JSONArray mJsonArray = response.getJSONArray("data");
                            RealmList<String> YoutubeUrls, ImagesUrls;
                            ListEventData mEventdata = new ListEventData();

                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mjson = mJsonArray.getJSONObject(i);
                                mEventdata.setId(mjson.getString("id"));
                                mEventdata.setTitle(mjson.getString("title"));
                                mEventdata.setDescription(mjson.getString("description"));
                                mEventdata.setLocation(mjson.getString("location"));
                                mEventdata.setEventDate(Common.StringToDate(mjson.getString("event_date")));
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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
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
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
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
                int i = Objects.requireNonNull(Objects.requireNonNull(eventData.get(position)).getImages()).size();
                int j = Objects.requireNonNull(Objects.requireNonNull(eventData.get(position)).getYoutubeUrl()).size();
                if (i > 0 || j > 0) {
                    Intent mIntent = new Intent(getActivity(), EventlistActivity.class);
                    mIntent.putExtra("eventId", Objects.requireNonNull(eventData.get(position)).getId());
                    startActivity(mIntent);

                } else {
                    Toast.makeText(getActivity(), "Event Details not found!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mEventListAdapter);
        mWaveSwipeRefreshLayout.setRefreshing(false);
    }

    private void showDirections(double src_lat, double src_lng,double dst_lat, double dst_lng, String address) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)&daddr=%f,%f (%s)", src_lat,src_lng, "",dst_lat,dst_lng , address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

        private final OnItemClickListener listener;

        EventAdapter(OnItemClickListener listener) {
            eventData = realm.where(ListEventData.class).sort(Common.Constant_Class.EVENT_DATE, Sort.DESCENDING).findAll();
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            final ListEventData data = eventData.get(position);
            assert data != null;
            holder.txtTitle.setText(data.getTitle());
            holder.txtDesc.setText(data.getDescription());
            holder.txtLocation.setText(data.getLocation());


            holder.txtEventDate.setText(parseDateToddMMyyyy(DatetoString(data.getEventDate())));
            getRandomColor(Objects.requireNonNull(getActivity()), position, holder.ll_event);

            holder.txtLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"get location",Toast.LENGTH_SHORT).show();
                   // showDirections(Double.parseDouble(data.getLat()) ,Double.parseDouble(data.getLng()) ,data.getLocation());
                }
            });

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

        class MyViewHolder extends RecyclerView.ViewHolder {
            final TextView txtTitle;
            final TextView txtDesc;
            final TextView txtLocation;
            final TextView txtEventDate;
            final LinearLayout ll_event;

            MyViewHolder(@NonNull View view) {
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
