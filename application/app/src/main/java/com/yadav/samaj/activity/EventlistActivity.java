package com.yadav.samaj.activity;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yadav.samaj.R;
import com.yadav.samaj.adapter.EventListAdapter;
import com.yadav.samaj.app.AppController;
import com.yadav.samaj.model.ListEventData;
import com.yadav.samaj.utils.Common;
import com.yadav.samaj.utils.ConnectivityReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

import static com.yadav.samaj.utils.Common.dd_MMM_yyyy;
import static com.yadav.samaj.utils.Common.parseDateToddMMyyyy;
import static com.yadav.samaj.utils.Common.yyyy_MM_dd;

public class EventlistActivity extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int RECOVERY_REQUEST = 1;
    Snackbar snackbar;
    private RecyclerView listEvents;
    private TextView txt_title, txt_desc, tvEventLocation, tvEventDate, txt_distance;
    @Nullable
    private String eventId = "";
    private String eventDesc = "";
    private String eventDate = "";
    private String eventTitle = "";
    private String eventLocation = "";
    private String lat = "", lng = "";
    private ImageView img_back;
    @Nullable
    private EventListAdapter adapter;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fragment_eventlist);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            eventId = mBundle.getString("id");
            eventDesc = mBundle.getString("desc");
            eventDate = mBundle.getString("date");
            eventTitle = mBundle.getString("title");
            eventLocation = mBundle.getString("location");
            lat = mBundle.getString("lat");
            lng = mBundle.getString("lng");
        }

        MemoryAllocation();
        txt_title.setText(eventTitle);
        txt_desc.setText(eventDesc);
        tvEventLocation.setText(eventLocation);
        setDate(tvEventDate, eventDate);
        listEvents.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listEvents.setLayoutManager(mLayoutManager);
        listEvents.setItemAnimator(new DefaultItemAnimator());
        listEvents.setAdapter(adapter);

        tvEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
                String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
                final double clat = Double.valueOf(curr_lat);
                final double clng = Double.valueOf(curr_lng);
                if (clat != 0 && clng != 0 && !lat.isEmpty() && !lng.isEmpty()) {
                    Common.showDirections(EventlistActivity.this, clat, clng, Double.parseDouble(lat), Double.parseDouble(lat), eventLocation);
                } else {
                    Toast.makeText(EventlistActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
        String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
        if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && !lat.isEmpty() && !lng.isEmpty()) {
            new Common.getDistance(this, txt_distance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, curr_lat, curr_lng, lat, lng);
        }
        checkConnection();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
    }


    private void setDate(TextView tvEventDate, String edate) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = inFormat.parse(edate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String goal = outFormat.format(date);

        String strDate = "";
        if (DateUtils.isToday(date.getTime())) strDate = "Today";
        else if (DateUtils.isToday(date.getTime() + DateUtils.DAY_IN_MILLIS)) strDate = "Yesterday";
        else if (DateUtils.isToday(date.getTime() - DateUtils.DAY_IN_MILLIS)) strDate = "Tommorrow";
        else strDate = parseDateToddMMyyyy(edate, yyyy_MM_dd, dd_MMM_yyyy);

        tvEventDate.setText(strDate + "\n" + goal);
    }

    private void MemoryAllocation() {
        snackbar = Snackbar.make(findViewById(R.id.eventlist_layout), R.string.not_connected, Snackbar.LENGTH_INDEFINITE);

        listEvents = findViewById(R.id.listEvents);
        txt_title = findViewById(R.id.txt_title);
        txt_desc = findViewById(R.id.txt_desc);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        tvEventDate = findViewById(R.id.tvEventDate);
        img_back = findViewById(R.id.img_back);
        txt_distance = findViewById(R.id.txt_distance);
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);

        Realm realm = AppController.getInstance().realm;
        ListEventData eventData = realm.where(ListEventData.class).endsWith("id", eventId).findFirst();
        assert eventData != null;
        adapter = new EventListAdapter(this, eventData);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showSnack(boolean isConnected) {
        if (!isConnected) {
            if (snackbar != null) {
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        } else {
            if (snackbar != null) {
                if (snackbar.isShownOrQueued()) {
                    snackbar.dismiss();
                }
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
