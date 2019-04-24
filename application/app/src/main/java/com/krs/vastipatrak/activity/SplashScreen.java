package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends Activity {

    private String TAG = SplashScreen.class.getSimpleName();

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StartAnimations();
        // Splash screen timer
        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this, ChooseLanguage.class);
            startActivity(i);
            finish();
        }, SPLASH_TIME_OUT);
        if (AppController.getInstance().mSharedPreferences.getString(getString(R.string.CityList_SP), "").isEmpty()) {
            getCityList();
        }
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }

    private void getCityList() {
        if (Utility.isOnline(this)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.RESPONSE_DATA, getResources().getString(R.string.all_cities_list));
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.GET_MASTER_DATA_URL, mJsonObject, response -> {
                Utility.hideProgressDialog();
                AppController.getInstance().mEditor.putString(getString(R.string.CityList_SP), response.toString());
                AppController.getInstance().mEditor.apply();
            }, error -> {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Utility.hideProgressDialog();
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConstants.ALLOW_GET_DATA, getResources().getString(R.string.allow_get_data_value));
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(AppConstants.INIT_TIMEOUT, AppConstants.DEFAULT_MAX_RETRIES, AppConstants.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "");
        }
    }
}