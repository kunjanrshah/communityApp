package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.DownloadTask;

public class MedicalReliefActivity extends Activity {

    Button btnDownload;
    String URL = "http://www.superbinstruments.com/dungar_form.pdf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicalrelief);
        btnDownload = findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadTask(MedicalReliefActivity.this, URL);
            }
        });
    }


}
