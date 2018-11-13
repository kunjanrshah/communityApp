package com.krs.vastipatrak.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.interfaces.OnItemClickListener;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.utils.Common;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.krs.vastipatrak.utils.Common.dd_MMM_yyyy;
import static com.krs.vastipatrak.utils.Common.parseDateToddMMyyyy;
import static com.krs.vastipatrak.utils.Common.textAsBitmap;
import static com.krs.vastipatrak.utils.Common.yyyy_MM_dd;


public class HomeFragment extends Fragment {

    @NonNull
    private final String TAG = "HomeFragment";
    int page_count = 0;
    private RecyclerView mRecycleView;
    private Realm realm;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private RealmResults<ListEventData> eventData;
    private int page = 1;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private FloatingActionButton mFloatingActionButton;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(R.string.title_events);
        MemoryAllocation(rootView);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    page--;
                } else {
                    page++;
                }
                if (page < 1) {
                    page = 1;
                }
                getEvents();
            }
        });


        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


            }
        });
       /* recycler_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {

                    mFloatingActionButton.setVisibility(View.INVISIBLE);
                } else {
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });*/

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_page_count);
                dialog.setTitle(R.string.app_name);
                dialog.setCancelable(false);
                final EditText input_page = dialog.findViewById(R.id.input_page);
                Button btn_send = dialog.findViewById(R.id.btn_send);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!input_page.getText().toString().isEmpty()) {
                            try {
                                int page1 = Integer.parseInt(input_page.getText().toString());
                                if (page1 > 0 && page1 <= page_count) {
                                    page = page1;
                                    dialog.dismiss();
                                    getEvents();
                                } else {
                                    Toast.makeText(getActivity(), "Invalid page number", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Invalid page number", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Enter page number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        getEvents();
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        mRecycleView = rootView.findViewById(R.id.recycler_view);
        mFloatingActionButton = rootView.findViewById(R.id.floating_action_button);
        mSwipyRefreshLayout = rootView.findViewById(R.id.swipyrefreshlayout);
        realm = AppController.getInstance().realm;
        if (realm.isClosed()) {
            AppController.getInstance().initRealm();
        }
        eventData = realm.where(ListEventData.class).findAll();
        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(Common.Constant_Class.FragmentSp, HomeFragment.class.getSimpleName());
        mEditor.apply();
        TextView tv = rootView.findViewById(R.id.txt_marquee);
        tv.setSelected(true);
    }

    private void getEvents() {

        if (Common.isOnline(Objects.requireNonNull(getActivity()))) {
            mSwipyRefreshLayout.setRefreshing(true);
            JSONObject mJsonObject = new JSONObject();
            try {
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                /*if (eventData.size() > 1) {
                    String date = eventData.get(eventData.size() - 1).getEventDate();
                    mJsonObject.put(Common.Constant_Class.EVENT_DATE, date);
                }*/
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.PAGE, String.valueOf(page));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Common.showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.EVENTS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        mSwipyRefreshLayout.setRefreshing(false);
                        String total_records = "0";
                        boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (response.has(Common.Constant_Class.TOTAL_RECORDS)) {
                            total_records = response.getString(Common.Constant_Class.TOTAL_RECORDS);
                        }
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
                            try {
                                int total = Integer.parseInt(total_records);
                                page_count = total / 25;
                                int mod = total % 25;
                                if (mod != 0) {
                                    page_count = page_count + 1;
                                }
                                mFloatingActionButton.setImageBitmap(textAsBitmap(String.valueOf(page) + "/" + String.valueOf(page_count), 40, Color.WHITE));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            setEventAdapter();
                        } else {
                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                    Intent mIntent = new Intent( getActivity(), LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    getActivity().finish();
                                }
                            }
                        }
                        Common.hideProgressDialog();
                        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
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
                /*int i = Objects.requireNonNull(Objects.requireNonNull(eventData.get(position)).getImages()).size();
                int j = Objects.requireNonNull(Objects.requireNonNull(eventData.get(position)).getYoutubeUrl()).size();
                if (i > 0 || j > 0) {*/
                Intent mIntent = new Intent(getActivity(), EventlistActivity.class);
                mIntent.putExtra("id", Objects.requireNonNull(eventData.get(position)).getId());
                mIntent.putExtra("desc", Objects.requireNonNull(eventData.get(position)).getDescription());
                mIntent.putExtra("date", Objects.requireNonNull(eventData.get(position)).getEventDate());
                mIntent.putExtra("title", Objects.requireNonNull(eventData.get(position)).getTitle());
                mIntent.putExtra("location", Objects.requireNonNull(eventData.get(position)).getLocation());
                mIntent.putExtra("lat", Objects.requireNonNull(eventData.get(position)).getLat());
                mIntent.putExtra("lng", Objects.requireNonNull(eventData.get(position)).getLng());
                startActivity(mIntent);
                /*} else {
                    Toast.makeText(getActivity(), "Event Details not found!", Toast.LENGTH_SHORT).show();
                }*/

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mEventListAdapter);
        mSwipyRefreshLayout.setRefreshing(false);
    }

    /*private void showDirections(double src_lat, double src_lng, double dst_lat, double dst_lng, String address) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)&daddr=%f,%f (%s)", src_lat, src_lng, "", dst_lat, dst_lng, address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }*/

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
            holder.txtTitle.setText(data.getTitle());
            holder.txtDesc.setText(data.getDescription());
            holder.txtLocation.setText(data.getLocation());
            Date mdate = Common.StringToDate(data.getEventDate());


            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = inFormat.parse(data.getEventDate().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            String goal = outFormat.format(date);

            String strDate = "";
            if (DateUtils.isToday(mdate.getTime()))
                strDate = "Today";
            else if (DateUtils.isToday(mdate.getTime() + DateUtils.DAY_IN_MILLIS))
                strDate = "Yesterday";
            else if (DateUtils.isToday(mdate.getTime() - DateUtils.DAY_IN_MILLIS))
                strDate = "Tommorrow";
            else
                strDate = parseDateToddMMyyyy(data.getEventDate(), yyyy_MM_dd, dd_MMM_yyyy);

            holder.txtEventDate.setText(strDate + "\n" + goal);
            //getRandomColor(Objects.requireNonNull(getActivity()), position, holder.ll_event);

            holder.txtLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = data.getLat();
                    String lng = data.getLng();
                    String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
                    String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
                    final double clat = Double.valueOf(curr_lat);
                    final double clng = Double.valueOf(curr_lng);
                    if (clat != 0 && clng != 0 && !lat.isEmpty() && !lng.isEmpty()) {
                        Common.showDirections(getActivity(), clat, clng, Double.parseDouble(data.getLat()), Double.parseDouble(data.getLng()), data.getLocation());
                    } else {
                        Toast.makeText(getActivity(), "Location not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            String lat = data.getLat();
            String lng = data.getLng();
            String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
            String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
            if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && !lat.isEmpty() && !lng.isEmpty()) {
                new Common.getDistance(getActivity(), holder.txt_distance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, curr_lat, curr_lng, lat, lng);
            }
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
            final TextView txt_distance;
            final LinearLayout ll_event;

            MyViewHolder(@NonNull View view) {
                super(view);
                txtTitle = view.findViewById(R.id.tvEventTitle);
                txtDesc = view.findViewById(R.id.tvEventDesc);
                txtLocation = view.findViewById(R.id.tvEventLocation);
                txt_distance = view.findViewById(R.id.txt_distance);
                txtEventDate = view.findViewById(R.id.tvEventDate);
                ll_event = view.findViewById(R.id.ll_event);
            }
        }
    }
}
