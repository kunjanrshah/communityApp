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

public class RegisterActivty extends Activity {

    TextView txt_already,txt_how_register;
    ImageView img_back,img_header_logo,img_profile,img_cancel;
    EditText edt_head_name,edt_spouse_name,edt_email_id,edt_mobile,edt_password,edt_cpassword,edt_address;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MemoryAllocation();

        txt_already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(RegisterActivty.this,LoginActivity1.class);
                startActivity(mIntent);
                finish();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MemoryAllocation() {
        img_back=findViewById(R.id.img_back);
        img_header_logo=findViewById(R.id.img_header_logo);
        img_profile=findViewById(R.id.img_profile);
        img_cancel=findViewById(R.id.img_cancel);
        txt_how_register=findViewById(R.id.txt_how_register);
        txt_already=findViewById(R.id.txt_already);
        edt_head_name=findViewById(R.id.edt_head_name);
        edt_spouse_name=findViewById(R.id.edt_spouse_name);
        edt_email_id=findViewById(R.id.edt_email_id);
        edt_mobile=findViewById(R.id.edt_mobile);
        edt_password=findViewById(R.id.edt_password);
        edt_cpassword=findViewById(R.id.edt_cpassword);
        edt_address=findViewById(R.id.edt_address);
        btn_register=findViewById(R.id.btn_register);
        String str=getResources().getString(R.string.already_have_a_account_sign_in)+ "<b>" +" "+ getString(R.string.login) +"</b>";
        txt_already.setText(Html.fromHtml(str));
    }

}
