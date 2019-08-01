package com.krs.community.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.utils.Utility;

public class FamilyTreeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tree_detailview);


        RadioButton rbFact = findViewById(R.id.rbFact);
        RadioButton rbPhoto = findViewById(R.id.rbPhoto);
        RadioButton rbRel = findViewById(R.id.rbRel);

        rbFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbFact.isChecked()) {
                    rbFact.setTypeface(AppController.getInstance().typeface_bold);
                    rbPhoto.setTypeface(AppController.getInstance().typeface);
                    rbRel.setTypeface(AppController.getInstance().typeface);
                }
            }
        });

        rbPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbPhoto.isChecked()) {
                    rbFact.setTypeface(AppController.getInstance().typeface);
                    rbPhoto.setTypeface(AppController.getInstance().typeface_bold);
                    rbRel.setTypeface(AppController.getInstance().typeface);
                }
            }
        });

        rbRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbRel.isChecked()) {
                    rbFact.setTypeface(AppController.getInstance().typeface);
                    rbPhoto.setTypeface(AppController.getInstance().typeface);
                    rbRel.setTypeface(AppController.getInstance().typeface_bold);
                }
            }
        });

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.arati);
        ImageView iv_profile = findViewById(R.id.iv_profile);

        ImageView iv_bg = findViewById(R.id.iv_bg);

        Bitmap bitmapImage= Utility.fastblur(icon,1,100);
        try {
            Glide.with(this).load(bitmapImage).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(iv_bg);
          // Bitmap icon1= Utility.getRoundedCornerBitmap(icon,150);
            Glide.with(this).load(icon).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(iv_profile);
        } catch (Exception e) {
            e.getMessage();
        }

/*
        LinearLayout ll_blure=findViewById(R.id.ll_blure);

        Bitmap bitmapImage=Utility.fastblur(icon,1,100);
        BitmapDrawable background = new BitmapDrawable(getResources(), bitmapImage);
        ll_blure.setBackground(background);*/


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
