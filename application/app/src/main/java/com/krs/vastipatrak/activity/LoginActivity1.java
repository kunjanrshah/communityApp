package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.krs.vastipatrak.R;


public class LoginActivity1 extends Activity {

    ImageView img_back;
    TextView txt_login_now, txt_pls_login, login_with, txt_forgot_pass, txt_do_you_have, txt_or_login_with;
    Button btn_mobile, btn_email, btn_login, btn_login_fb, btn_login_google;
    EditText edt_username, edt_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login1);

        MemoryAllocation();

        btn_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_username.setHint("Enter Mobile no");
                btn_mobile.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                btn_mobile.setTextColor(getColor(R.color.mdtp_white));
                btn_email.setBackground(getDrawable(R.drawable.border));
                btn_email.setTextColor(getColor(R.color.mdtp_transparent_black));
            }
        });

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_username.setHint("Enter Email ID");
                btn_email.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                btn_email.setTextColor(getColor(R.color.mdtp_white));
                btn_mobile.setBackground(getDrawable(R.drawable.border));
                btn_mobile.setTextColor(getColor(R.color.mdtp_transparent_black));
            }
        });

        btn_login_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txt_do_you_have.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(LoginActivity1.this,RegisterActivty.class);
                startActivity(mIntent);
                finish();
            }
        });
    }

    private void MemoryAllocation() {
        img_back = findViewById(R.id.img_back);
        edt_username = findViewById(R.id.edt_username);
        edt_pass = findViewById(R.id.edt_pass);
        btn_mobile = findViewById(R.id.btn_mobile);
        btn_email = findViewById(R.id.btn_email);
        btn_login = findViewById(R.id.btn_login);
        btn_login_fb = findViewById(R.id.btn_login_fb);
        btn_login_google = findViewById(R.id.btn_login_google);
        txt_login_now = findViewById(R.id.txt_login_now);
        txt_pls_login = findViewById(R.id.txt_pls_login);
        login_with = findViewById(R.id.login_with);
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass);
        txt_do_you_have = findViewById(R.id.txt_do_you_have);
        txt_or_login_with = findViewById(R.id.txt_or_login_with);
        String sourcestr=getResources().getString(R.string.do_you_have_an_account_register_now);
        sourcestr=sourcestr+"<b>"+"REGISTER NOW"+"</b>";
        txt_do_you_have.setText(Html.fromHtml(sourcestr));
    }
}
