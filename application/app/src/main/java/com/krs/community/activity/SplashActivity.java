package com.krs.community.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.github.squti.guru.Guru;
import com.google.gson.Gson;
import com.krs.community.R;
import com.krs.community.fragments.FamilyDetailActivity;
import com.krs.community.model.Member;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.Utility;

public class SplashActivity extends Activity {

    private KenBurnsView kbv;
    private View imglogo,darkoverlay, ll_spinner;//, ll_login;
    private Button btn_login, btn_register;
    private Spinner splanguage;
    private DisplayMetrics dm;
    private boolean is_login = false;
    private boolean is_register = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        MemoryAllocation();
        setAnimation();

        String[] languages = getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        splanguage.setAdapter(aa);


        btn_login.setOnClickListener(v -> {
            if (!is_login) {
                is_login = true;
                Intent mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mIntent);
                finish();
                Utility.fade(this);
            }
        });

        btn_register.setOnClickListener(v -> {
            if (!is_register) {
                is_register = true;
                Intent mIntent = new Intent(SplashActivity.this, RegisterActivty.class);
                startActivity(mIntent);
                finish();
                Utility.fade(this);
            }
        });

        splanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Utility.changeLang(SplashActivity.this, splanguage.getSelectedItem().toString());
                btn_login.setText(getResources().getString(R.string.login));
                btn_register.setText(getResources().getString(R.string.register));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String userId=Guru.getString(getString(R.string.user_id),"");
        String member = Guru.getString(getString(R.string.loginUser), "");

        if( (userId==null || userId.isEmpty()) && (member==null || member.isEmpty())){
        }else if(member==null || member.isEmpty()){
            Intent mIntent = new Intent(SplashActivity.this, FamilyDetailActivity.class);
            mIntent.putExtra(getString(R.string.id),userId);
            startActivity(mIntent);
            finish();
        }else{
            Intent mIntent = new Intent(SplashActivity.this, DashboardActivity.class);
            mIntent.putExtra(getString(R.string.user_id),userId);
            startActivity(mIntent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String locale = Guru.getString(getResources().getString(R.string.locale_sp), getResources().getString(R.string._english));
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

    private void setAnimation() {
        RandomTransitionGenerator generator = new RandomTransitionGenerator(19000, new AccelerateDecelerateInterpolator());
        kbv.setTransitionGenerator(generator);
        imglogo.animate().setStartDelay(3000).setDuration(2000).alpha(1).start();

        darkoverlay.animate().setStartDelay(3000).setDuration(3000).alpha(0.6f).start();

        ll_spinner.animate().translationY(dm.heightPixels).setStartDelay(0).setDuration(0).start();
        ll_spinner.animate().translationY(0).setDuration(2000).alpha(1).setStartDelay(5000).start();

        btn_login.animate().translationX(dm.widthPixels+ btn_login.getMeasuredWidth()).setDuration(0).setStartDelay(0).start();
        btn_login.animate().translationX(0).setStartDelay(5500).setDuration(2000).setInterpolator(new OvershootInterpolator()).start();

        btn_register.animate().translationX(dm.widthPixels+ btn_register.getMeasuredWidth()).setDuration(0).setStartDelay(0).start();
        btn_register.animate().translationX(0).setStartDelay(5500).setDuration(2000).setInterpolator(new OvershootInterpolator()).start();
    }

    private void MemoryAllocation()
    {
        imglogo=findViewById(R.id.fragmentloginLogo);
        dm = getResources().getDisplayMetrics();
        kbv= findViewById(R.id.fragmentloginKenBurnsView1);
        darkoverlay=findViewById(R.id.fragmentloginView1);
        ll_spinner =findViewById(R.id.ll_spinner);
       // btn_login =findViewById(R.id.btn_login);
        splanguage = findViewById(R.id.splanguage);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setTag(0);
        btn_register = findViewById(R.id.btn_register1);
        btn_register.setTag(0);
    }
}
