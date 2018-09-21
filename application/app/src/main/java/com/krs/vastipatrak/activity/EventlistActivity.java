package com.krs.vastipatrak.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.EventListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListEventData;
import com.krs.vastipatrak.utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;

import static com.krs.vastipatrak.utils.Common.dd_MMM_yyyy;
import static com.krs.vastipatrak.utils.Common.parseDateToddMMyyyy;
import static com.krs.vastipatrak.utils.Common.yyyy_MM_dd;

public class EventlistActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private RecyclerView listEvents;
    private TextView txt_title, txt_desc, tvEventLocation, tvEventDate,txt_distance;
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
                    showDirections(clat, clng, Double.parseDouble(lat), Double.parseDouble(lat), eventLocation);
                } else {
                    Toast.makeText(EventlistActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
        String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
        if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && !lat.isEmpty() && !lng.isEmpty()) {
            new Common.getDistance(this,txt_distance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, curr_lat, curr_lng, lat, lng);
        }
    }



    private void showDirections(double src_lat, double src_lng, double dst_lat, double dst_lng, String address) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)&daddr=%f,%f (%s)", src_lat, src_lng, "", dst_lat, dst_lng, address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
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
        if (DateUtils.isToday(date.getTime()))
            strDate = "Today";
        else if (DateUtils.isToday(date.getTime() + DateUtils.DAY_IN_MILLIS))
            strDate = "Yesterday";
        else if (DateUtils.isToday(date.getTime() - DateUtils.DAY_IN_MILLIS))
            strDate = "Tommorrow";
        else
            strDate = parseDateToddMMyyyy(edate, yyyy_MM_dd, dd_MMM_yyyy);

        tvEventDate.setText(strDate + "\n" + goal);
    }

    private void MemoryAllocation() {
        listEvents = findViewById(R.id.listEvents);
        txt_title = findViewById(R.id.txt_title);
        txt_desc = findViewById(R.id.txt_desc);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        tvEventDate = findViewById(R.id.tvEventDate);
        img_back= findViewById(R.id.img_back);
        txt_distance= findViewById(R.id.txt_distance);
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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, @NonNull YouTubePlayer youTubePlayer, boolean b) {
        assert adapter != null;
        String url = adapter.getYoutubeUrl();
        int i = url.indexOf("v=");
        url = url.substring(i + 2);
        youTubePlayer.cueVideo(url);//fhWaJi1Hsfo
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, @NonNull YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            EventListAdapter.ViewHolder.youTubeView.initialize(Common.Constant_Class.YOUTUBE_API_KEY, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
