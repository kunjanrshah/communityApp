package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.Common;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Objects;

public class ChooseLanguage extends Activity implements AdapterView.OnItemSelectedListener {

    private Button btn_login,btn_register;
    private MaterialBetterSpinner spinner;
    String[] languages ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_languages);
        MemoryAllocation();
        languages = Objects.requireNonNull(this).getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, languages);
        spinner.setAdapter(aa);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(ChooseLanguage.this,LoginActivity1.class);
                startActivity(mIntent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(ChooseLanguage.this,RegisterActivty.class);
                startActivity(mIntent);
            }
        });
    }

    private void MemoryAllocation() {

        spinner=findViewById(R.id.splanguage);
        spinner.setOnItemSelectedListener(this);
        btn_login=findViewById(R.id.btn_login);
        btn_register=findViewById(R.id.btn_register);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Common.changeLang(ChooseLanguage.this,languages[position]);
        btn_login.setText(getResources().getString(R.string.login));
        btn_register.setText(getResources().getString(R.string.register));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
