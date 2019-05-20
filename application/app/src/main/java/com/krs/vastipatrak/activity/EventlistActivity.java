package com.krs.vastipatrak.activity;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.EventListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;
import com.krs.vastipatrak.utils.ConnectivityReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

import static com.krs.vastipatrak.utils.Utility.dd_MMM_yyyy;
import static com.krs.vastipatrak.utils.Utility.parseDateToddMMyyyy;
import static com.krs.vastipatrak.utils.Utility.yyyy_MM_dd;

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

                String curr_lat = mSharedPreferences.getString(AppConstants.CURR_LAT, "");
                String curr_lng = mSharedPreferences.getString(AppConstants.CURR_LNG, "");
                final double clat = Double.valueOf(curr_lat);
                final double clng = Double.valueOf(curr_lng);
                if (clat != 0 && clng != 0 && !lat.isEmpty() && !lng.isEmpty()) {
                    Utility.showDirections(EventlistActivity.this, clat, clng, Double.parseDouble(lat), Double.parseDouble(lat), eventLocation);
                } else {
                    Toast.makeText(EventlistActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String curr_lat = mSharedPreferences.getString(AppConstants.CURR_LAT, "");
        String curr_lng = mSharedPreferences.getString(AppConstants.CURR_LNG, "");
        if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && !lat.isEmpty() && !lng.isEmpty()) {
            new Utility.getDistance(this, txt_distance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, curr_lat, curr_lng, lat, lng);
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
        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);

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
                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
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
