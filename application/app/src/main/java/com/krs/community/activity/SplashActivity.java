package com.krs.community.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.github.squti.guru.Guru;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.app.ConnectionLiveData;
import com.krs.community.fragments.FamilyDetailActivity;
import com.krs.community.utils.Utility;

import static com.krs.community.utils.Utility.getHashKey;

;

public class SplashActivity extends AppCompatActivity {

    private KenBurnsView kbv;
    private View imglogo, darkoverlay, llSpinner;
    private Button btnLogin, btnRegister;
    private Spinner splanguage;
    private DisplayMetrics dm;
    private boolean isLogin = false;
    private boolean isRegister = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHashKey(this);
        if (ConnectionLiveData.Companion.isNetworkConnected(this)) {
            setScreenLayout();
        }else{
            setNoInternetLayout();
        }

        AppController mApp = (AppController) getApplicationContext();
        mApp.firebaseAnalytics(SplashActivity.this, SplashActivity.class.getSimpleName());
        mApp.facebookAnalytics(SplashActivity.this, SplashActivity.class.getSimpleName());

    }

    private void setNoInternetLayout(){
        setContentView(R.layout.no_internet_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(6000);
        anim.setRepeatMode(AlphaAnimation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        AppCompatImageView imageView = findViewById(R.id.no_internet_image);
        imageView.setAnimation(anim);
        AppCompatButton retryButton=findViewById(R.id.retry_button);
        retryButton.setOnClickListener(v -> {
            if (ConnectionLiveData.Companion.isNetworkConnected(this)) {
                setScreenLayout();
            }
        });
    }


    private void setScreenLayout(){

        if (ConnectionLiveData.Companion.isNetworkConnected(this)) {
            setContentView(R.layout.activity_splash);
            MemoryAllocation();
            setAnimation();

            String[] languages = getResources().getStringArray(R.array.languages);
            ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
            splanguage.setAdapter(aa);

            btnLogin.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorDark));
                        return true;
                    case MotionEvent.ACTION_UP:
                        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        btnLogin.performClick();
                        return true;
                    default:
                        return false;
                }
            });

            btnRegister.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnRegister.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
                        return true;
                    case MotionEvent.ACTION_UP:
                        btnRegister.setBackgroundColor(getResources().getColor(R.color.white));
                        btnRegister.performClick();
                        return true;
                    default:
                        return false;
                }
            });

            btnLogin.setOnClickListener(v -> {
                if (!isLogin) {
                    isLogin = true;
                    Intent mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mIntent);

                    Utility.fade(this);
                }
            });
            btnRegister.setOnClickListener(v -> {
                if (!isRegister) {
                    isRegister = true;
                    Intent mIntent = new Intent(SplashActivity.this, RegisterActivty.class);
                    startActivity(mIntent);

                    Utility.fade(this);
                }
            });
            splanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Utility.changeLang(SplashActivity.this, splanguage.getSelectedItem().toString());
                    btnLogin.setText(getResources().getString(R.string.login));
                    btnRegister.setText(getResources().getString(R.string.register));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onStart() {
        super.onStart();

        String userId = Guru.getString(getString(R.string.user_id), "");
        String member = Guru.getString(getString(R.string.loginMember), "");

        if ((userId == null || userId.isEmpty()) && (member == null || member.isEmpty())) {
        } else if (member == null || member.isEmpty()) {
            Intent mIntent = new Intent(SplashActivity.this, FamilyDetailActivity.class);
            mIntent.putExtra(getString(R.string.id), userId);
            startActivity(mIntent);
            finish();
        } else {
            Intent mIntent = new Intent(SplashActivity.this, DashboardActivity.class);
            mIntent.putExtra(getString(R.string.user_id), userId);
            startActivity(mIntent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(splanguage!=null){
            String locale = Guru.getString(getResources().getString(R.string.locale_sp), getResources().getString(R.string._english));
            if (locale.equalsIgnoreCase(getResources().getString(R.string._gujarati))) {
                splanguage.setSelection(2);
            } else if (locale.equalsIgnoreCase(getResources().getString(R.string._hindi))) {
                splanguage.setSelection(3);
            } else {
                splanguage.setSelection(1);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isLogin = false;
        isRegister = false;
    }

    @SuppressLint("NewApi")
    private void setAnimation() {
        RandomTransitionGenerator generator = new RandomTransitionGenerator(19000, new AccelerateDecelerateInterpolator());
        kbv.setTransitionGenerator(generator);
        imglogo.animate().setStartDelay(3000).setDuration(2000).alpha(1).start();

        darkoverlay.animate().setStartDelay(3000).setDuration(3000).alpha(0.6f).start();

        llSpinner.animate().translationY(dm.heightPixels).setStartDelay(0).setDuration(0).start();
        llSpinner.animate().translationY(0).setDuration(2000).alpha(1).setStartDelay(5000).start();

        btnLogin.animate().translationX(dm.widthPixels + btnLogin.getMeasuredWidth()).setDuration(0).setStartDelay(0).start();
        btnLogin.animate().translationX(0).setStartDelay(5500).setDuration(2000).setInterpolator(new OvershootInterpolator()).start();

        btnRegister.animate().translationX(dm.widthPixels + btnRegister.getMeasuredWidth()).setDuration(0).setStartDelay(0).start();
        btnRegister.animate().translationX(0).setStartDelay(5500).setDuration(2000).setInterpolator(new OvershootInterpolator()).start();
    }

    private void MemoryAllocation() {
        imglogo = findViewById(R.id.fragmentloginLogo);
        dm = getResources().getDisplayMetrics();
        kbv = findViewById(R.id.fragmentloginKenBurnsView1);
        darkoverlay = findViewById(R.id.fragmentloginView1);
        llSpinner = findViewById(R.id.ll_spinner);
        // btnLogin =findViewById(R.id.btnLogin);
        splanguage = findViewById(R.id.splanguage);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setTag(0);
        btnRegister = findViewById(R.id.btn_register1);
        btnRegister.setTag(0);
    }
}
