package com.krs.vastipatrak.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
import com.krs.vastipatrak.adapter.ExpandableEventListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListEventChildData;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.model.ListEventParentData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class HomeFragment extends Fragment {

    ExpandableListView lvEventList;
    ExpandableEventListAdapter mExpandableEventListAdapter = null;
    ArrayList<ListEventParentData> listDataHeader = null;
    HashMap<ListEventParentData, List<ListEventChildData>> listDataChild = null;
    Realm realm;
    String TAG = "HomeFragment";
    SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_events);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        getEvents();
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        lvEventList = rootView.findViewById(R.id.lvEventList);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        realm = AppController.getInstance().realm;
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
    }

    private void getEventRecords() {

        RealmResults<ListEventData> eventData = realm.where(ListEventData.class).findAll();
        if (eventData != null && eventData.size() > 0) {
            for (ListEventData data : eventData) {
                ListEventParentData lpd = new ListEventParentData();
                lpd.setEventId(data.getId());
                lpd.setEventTitle(data.getTitle());
                lpd.setEventDesc(data.getDescription());
                lpd.setEventLocation(data.getLocation());
                lpd.setEventDate(data.getEventDate());
                listDataHeader.add(lpd);
                ListEventChildData lcd = new ListEventChildData();
                lcd.setImageUrls(data.getImages());
                lcd.setYoutubeUrls(data.getYoutubeUrl());
                ArrayList<ListEventChildData> mlstChildData = new ArrayList<ListEventChildData>();
                mlstChildData.add(lcd);
                listDataChild.put(lpd, mlstChildData);
            }
            mExpandableEventListAdapter = new ExpandableEventListAdapter(getActivity(), listDataHeader, listDataChild);
            lvEventList.setAdapter(mExpandableEventListAdapter);
            lvEventList.setVisibility(View.VISIBLE);
        } else {
            lvEventList.setVisibility(View.GONE);
        }
    }

    private void getEvents() {

        if (Common.isOnline(getActivity())) {
            Common.initProgressDialog(getActivity());
            Common.showProgressDialog();

            JSONObject mJsonObject = new JSONObject();
            try {
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.EVENTS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        Common.hideProgressDialog();
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
                            }
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(mEventdata);
                            realm.commitTransaction();
                        }

                        getEventRecords();

                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }
}
