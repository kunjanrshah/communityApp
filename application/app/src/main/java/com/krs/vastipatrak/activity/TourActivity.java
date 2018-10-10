package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.VideoListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.ConnectivityReceiver;

public class TourActivity extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        MemoryAllocation();
        snackbar = Snackbar.make(findViewById(R.id.list), R.string.not_connected, Snackbar.LENGTH_INDEFINITE);
        checkConnection();
    }

    private void MemoryAllocation() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        VideoListAdapter adapter = new VideoListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            if (snackbar != null) {
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
