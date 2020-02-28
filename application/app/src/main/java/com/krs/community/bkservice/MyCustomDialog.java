package com.krs.community.bkservice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.krs.community.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCustomDialog extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(true);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.truecaller_bottom_sheet);
            CircleImageView ivProfile = findViewById(R.id.iv_profile);
            ImageView ivGender = findViewById(R.id.iv_gender);
            LinearLayout llUser = findViewById(R.id.ll_user);
            LinearLayout llHome = findViewById(R.id.ll_home);
            LinearLayout llOffice = findViewById(R.id.ll_office);
            TextView tvName = findViewById(R.id.tv_name);
            TextView tvAge = findViewById(R.id.tv_age);
            TextView tvArea = findViewById(R.id.tv_area);
            TextView tvCity = findViewById(R.id.tv_city);
            TextView tvWork = findViewById(R.id.tv_work);

        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }


}