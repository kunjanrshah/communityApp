package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import java.util.Objects;

public class ChooseLan extends Activity {

    KenBurnsView kbv;
    View imglogo,darkoverlay,form_login,button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_lan);
        imglogo=findViewById(R.id.fragmentloginLogo);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        kbv=(KenBurnsView) findViewById(R.id.fragmentloginKenBurnsView1);
        darkoverlay=findViewById(R.id.fragmentloginView1);
        form_login=findViewById(R.id.form_login);
        button_login=findViewById(R.id.button_login);
        button_login.setTag(0);

        RandomTransitionGenerator generator = new RandomTransitionGenerator(20000, new AccelerateDecelerateInterpolator());
        kbv.setTransitionGenerator(generator);
        imglogo.animate().setStartDelay(5000).setDuration(2000).alpha(1).start();

        darkoverlay.animate().setStartDelay(5000).setDuration(2000).alpha(0.6f).start();
        form_login.animate().translationY(dm.heightPixels).setStartDelay(0).setDuration(0).start();
        form_login.animate().translationY(0).setDuration(1500).alpha(1).setStartDelay(6000).start();

        button_login.animate().translationX(dm.widthPixels+button_login.getMeasuredWidth()).setDuration(0).setStartDelay(0).start();
        button_login.animate().translationX(0).setStartDelay(6500).setDuration(1500).setInterpolator(new OvershootInterpolator()).start();

    }
}
