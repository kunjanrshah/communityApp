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

    private Button btn_login, btn_register;
    private Spinner splanguage;
    private boolean is_login = false;
    private boolean is_register = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_languages);
        MemoryAllocation();
        String[] languages = Objects.requireNonNull(this).getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        splanguage.setAdapter(aa);

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
    protected void onResume() {
        super.onResume();

        String locale = AppController.getInstance().mSharedPreferences.getString(getResources().getString(R.string.locale_sp), getResources().getString(R.string._english));
        if (locale.equalsIgnoreCase(getResources().getString(R.string._gujarati))) {
            splanguage.setSelection(2);
        } else if (locale.equalsIgnoreCase(getResources().getString(R.string._hindi))) {
            splanguage.setSelection(3);
        } else {
            splanguage.setSelection(1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        is_login = false;
        is_register = false;
    }

    private void MemoryAllocation() {

        splanguage = findViewById(R.id.splanguage);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        splanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Utility.changeLang(ChooseLanguage.this, splanguage.getSelectedItem().toString());
                btn_login.setText(getResources().getString(R.string.login));
                btn_register.setText(getResources().getString(R.string.register));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
