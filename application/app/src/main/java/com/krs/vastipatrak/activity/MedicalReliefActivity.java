package com.krs.vastipatrak.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.DownloadTask;

import java.io.File;
import java.util.Objects;

public class MedicalReliefActivity extends AppCompatActivity {

    Button btnDownload, btnView;
    String URL = "http://www.superbinstruments.com/dungar_form.pdf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicalrelief);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        btnDownload = findViewById(R.id.btnDownload);
        btnView = findViewById(R.id.btnView);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadTask(MedicalReliefActivity.this, URL);
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vastipatrak/dungar_form.pdf");
                if (file.exists()) {
                    Common.ExportAlert(MedicalReliefActivity.this, file, false);
                } else {
                    Toast.makeText(MedicalReliefActivity.this, "File not found. Please download again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
