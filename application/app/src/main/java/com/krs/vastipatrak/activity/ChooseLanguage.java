package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Objects;

public class ChooseLanguage extends Activity  {

    private Button btn_login,btn_register;
    private MaterialBetterSpinner spinner1;
    String[] languages ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_languages);
        MemoryAllocation();
        languages = Objects.requireNonNull(this).getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, languages);
        spinner1.setAdapter(aa);

        btn_login.setOnClickListener(v -> {
            Intent mIntent=new Intent(ChooseLanguage.this, LoginActivity.class);
            startActivity(mIntent);
        });

        btn_register.setOnClickListener(v -> {
            Intent mIntent=new Intent(ChooseLanguage.this,RegisterActivty.class);
            startActivity(mIntent);
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (Utility.canCallPhone(this) || !Utility.canAccessLocation(this) || !Utility.canSMS(this)) {
                requestPermissions(AppConstants.INIT_PERMS, AppConstants.INIT_REQUEST);
            }
        }
    }

    private void MemoryAllocation() {

        spinner1 =findViewById(R.id.splanguage);
        btn_login=findViewById(R.id.btn_login);
        btn_register=findViewById(R.id.btn_register);

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
        });
    }
}
