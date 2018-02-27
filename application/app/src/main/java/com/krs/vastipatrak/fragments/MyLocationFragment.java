package com.krs.vastipatrak.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.GPSTracker;
import com.krs.vastipatrak.R;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by kushal on 28/01/16.
 */
public class MyLocationFragment extends Fragment implements View.OnClickListener {


    Button btn_your_loc, btn_home_loc, btn_office_loc;
    GPSTracker gpsTracker;

    String tag_json_obj = "jobj_req";
    String TAG = "MyLocationFragment";
    String name = "",home_address="",office_address="";
    FloatingActionButton floatingActionButton;
    SharedPreferences mSharedPreferences;
    double user_lat = 0, user_lng = 0, home_lat = 0, home_lng = 0, office_lat = 0, office_lng = 0, curr_lat = 0, curr_lng = 0;
    ObservableScrollView scroll_ldetails;
    boolean save_flag = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        Memory_Allocation(rootView);
        setListener();

        Bundle args = getArguments();
        if(args!=null)
        {
            String data=args.getString(Common.Constant_Class.DATA);

            SetData(data);
        }
        else
        {
            callSetLocationWS();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_flag = true;
                callSetLocationWS();
            }
        });
        System.out.println("Location Fragment");



        return rootView;
    }

    private void Memory_Allocation(View rootView) {
        scroll_ldetails = (ObservableScrollView) rootView.findViewById(R.id.scroll_ldetails);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_bsave);
        floatingActionButton.attachToScrollView(scroll_ldetails);
        btn_your_loc = (Button) rootView.findViewById(R.id.btn_your_loc);
        btn_home_loc = (Button) rootView.findViewById(R.id.btn_home_loc);
        btn_office_loc = (Button) rootView.findViewById(R.id.btn_office_loc);
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
        gpsTracker = new GPSTracker(getActivity());

    }

    private void setListener() {
        btn_your_loc.setOnClickListener(this);
        btn_home_loc.setOnClickListener(this);
        btn_office_loc.setOnClickListener(this);
    }
    double lat=0,lon=0;

    @Override
    public void onClick(View v) {



        if (gpsTracker.IsGetLocation()) {
            lat = gpsTracker.getLatitude();
            lon = gpsTracker.getLongitude();
        }

        if (v == btn_your_loc) {

            if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {

      /*          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.app_name));

                builder.setMessage("Do you want to set your Location ?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        user_lat=lat;
                        user_lng=lon;

                        *//*   user_lat = 23.027597;
                           user_lng = 72.504255;*//*
                    }
                })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show().getWindow().setLayout(400, 300);




            } else {*/

                curr_lat=lat;
                curr_lng=lon;


/*    curr_lat=23.227597;
    curr_lng=71.804255;*/
                showDirections(name,user_lat, user_lng);
            }


        } else if (v == btn_home_loc) {

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.app_name));

                builder.setMessage("Are you at your Home ??");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        home_lat=lat;
                        home_lng=lon;
                    }
                })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show().getWindow().setLayout(400, 300);





            } else {
                curr_lat=lat;
                curr_lng=lon;

                showDirections(home_address,home_lat, home_lng);
            }


        } else if (v == btn_office_loc) {

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.app_name));

                builder.setMessage("Are you at your Office ??");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        office_lat=lat;
                        office_lng=lon;
                    }
                })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show().getWindow().setLayout(400, 300);



            } else {
                curr_lat=lat;
                curr_lng=lon;

                showDirections(office_address,office_lat, office_lng);
            }


        }

    }


    public void showDirections(String address,double latitude, double longitude) {

/*        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + curr_lat + "," + curr_lng + "&daddr=" + latitude + "," +
                longitude));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);*/



        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)&daddr=%f,%f (%s)",curr_lat,curr_lng,"Garvi Gujarat Soc", latitude, longitude, address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);

    }


    private void callSetLocationWS() {
        if (Common.isOnline(getActivity())) {

            JSONObject mJsonObject = null;


            if (save_flag) {
                save_flag = false;


                try {
                    mJsonObject = new JSONObject();
                    if (user_lat != 0 && user_lng != 0) {
                        mJsonObject.put(Common.Constant_Class.USER_LAT, user_lat);
                        mJsonObject.put(Common.Constant_Class.USER_LNG, user_lng);
                    }
                    if (home_lat != 0 && home_lng != 0) {
                        mJsonObject.put(Common.Constant_Class.HOME_LAT, home_lat);
                        mJsonObject.put(Common.Constant_Class.HOME_LNG, home_lng);
                    }
                    if (office_lat != 0 && office_lng != 0) {
                        mJsonObject.put(Common.Constant_Class.OFFICE_LAT, office_lat);
                        mJsonObject.put(Common.Constant_Class.OFFICE_LNG, office_lng);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            String id = "";
            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
                id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
            } else {
                id = mSharedPreferences.getString(Common.Constant_Class.PROFILE_ID_SP, "");
            }

            final String profile_url = Common.Constant_Class.PROFILE_URL + id;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, profile_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            String data = response.getString(Common.Constant_Class.DATA);
                            if (!message.contains(Common.Constant_Class.UPDATED)) {
                                SetData(data.toString());
                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }

    private float getDistance(double lat,double lon)
    {
        if (gpsTracker.IsGetLocation()) {
            curr_lat = gpsTracker.getLatitude();
            curr_lng = gpsTracker.getLongitude();
        }
        Location loc1=new Location("");
        loc1.setLatitude(curr_lat);
        loc1.setLongitude(curr_lng);

        Location loc2=new Location("");
        loc2.setLatitude(lat);
        loc2.setLongitude(lon);
        float distanceInMeters = loc1.distanceTo(loc2);
        return  distanceInMeters;
    }

    private void SetData(String data) {
        try {
            JSONObject mData = new JSONObject(data);
            if (!mData.getString(Common.Constant_Class.USER_LAT).toString().equalsIgnoreCase("null")) {
                user_lat = Double.parseDouble(mData.getString(Common.Constant_Class.USER_LAT));
            }

            if (!mData.getString(Common.Constant_Class.USER_LNG).toString().equalsIgnoreCase("null")) {
                user_lng = Double.parseDouble(mData.getString(Common.Constant_Class.USER_LNG));
            }

            if (!mData.getString(Common.Constant_Class.HOME_LAT).toString().equalsIgnoreCase("null")) {

                home_lat = Double.parseDouble(mData.getString(Common.Constant_Class.HOME_LAT));
            }

            if (!mData.getString(Common.Constant_Class.HOME_LNG).toString().equalsIgnoreCase("null")) {

                home_lng = Double.parseDouble(mData.getString(Common.Constant_Class.HOME_LNG));
            }

            if (!mData.getString(Common.Constant_Class.OFFICE_LAT).toString().equalsIgnoreCase("null")) {

                office_lat = Double.parseDouble(mData.getString(Common.Constant_Class.OFFICE_LAT));
            }

            if (!mData.getString(Common.Constant_Class.OFFICE_LNG).toString().equalsIgnoreCase("null")) {
                office_lng = Double.parseDouble(mData.getString(Common.Constant_Class.OFFICE_LNG));
            }


            name = mData.getString(Common.Constant_Class.FIRST_NAME) + " " + mData.getString(Common.Constant_Class.LAST_NAME);
            home_address=mData.getString(Common.Constant_Class.ADDRESS) ;
            office_address =mData.getString(Common.Constant_Class.OFFICE_ADDRESS) ;

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
                floatingActionButton.setVisibility(View.VISIBLE);
                btn_your_loc.setText(getResources().getString(R.string.set_your_location));
                btn_home_loc.setText(getResources().getString(R.string.set_your_home_location));
                btn_office_loc.setText(getResources().getString(R.string.set_your_office_location));
            } else {
                floatingActionButton.setVisibility(View.GONE);

                btn_your_loc.setText(name + "\n Distance : " + (int)(getDistance(user_lat, user_lng)/1000)+" Km");
                btn_home_loc.setText( name + " Home To My Home \n Distance : "+ (int)(getDistance(home_lat,home_lng)/1000)+" Km");
                btn_office_loc.setText( name + " Office To My Office \n Distance : "+ (int)(getDistance(office_lat,office_lng)/1000)+" Km");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
