package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import java.util.Objects;

public class ChooseLanguage extends Activity {

    String[] languages;
    private Button btn_login, btn_register;
    private Spinner spinner1;
    private boolean is_login = false;
    private boolean is_register = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_languages);
        MemoryAllocation();
        languages = Objects.requireNonNull(this).getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        spinner1.setAdapter(aa);
        spinner1.setSelection(1);

        btn_login.setOnClickListener(v -> {
            if (!is_login) {
                is_login = true;
                Intent mIntent = new Intent(ChooseLanguage.this, LoginActivity.class);
                startActivity(mIntent);
            }
        });

        btn_register.setOnClickListener(v -> {
            if (!is_register) {
                is_register = true;
                Intent mIntent = new Intent(ChooseLanguage.this, RegisterActivty.class);
                startActivity(mIntent);
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (Utility.canCallPhone(this) || !Utility.canAccessLocation(this) || !Utility.canSMS(this)) {
                requestPermissions(AppConstants.INIT_PERMS, AppConstants.INIT_REQUEST);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean is_home = AppController.getInstance().mSharedPreferences.getBoolean(AppConstants.IS_HOME, false);
        if (!is_home) {
            return;
        }
        Intent mIntent = new Intent(ChooseLanguage.this, HomeActivity.class);
        mIntent.putExtra(AppConstants.USER_ID, AppController.getInstance().mSharedPreferences.getString(AppConstants.USER_ID, ""));
        startActivity(mIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        is_login = false;
        is_register = false;
    }

    private void MemoryAllocation() {

        spinner1 = findViewById(R.id.splanguage);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Utility.changeLang(ChooseLanguage.this, spinner1.getSelectedItem().toString());
                btn_login.setText(getResources().getString(R.string.login));
                btn_register.setText(getResources().getString(R.string.register));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
        spinner1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Utility.changeLang(ChooseLanguage.this,spinner1.getText().toString());
                btn_login.setText(getResources().getString(R.string.login));
                btn_register.setText(getResources().getString(R.string.register));
            }
        });*/
    }
}
