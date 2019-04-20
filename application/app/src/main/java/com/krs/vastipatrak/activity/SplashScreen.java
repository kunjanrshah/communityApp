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
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
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
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }

    private void getCityList() {
        if (Utility.isOnline(this)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                String user_id = AppController.getInstance().mSharedPreferences.getString(AppConstants.USER_ID, "");
                String token = AppController.getInstance().mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, "");
                mJsonObject.put(AppConstants.USER_ID, "4345");
                mJsonObject.put(AppConstants.ACCESS_TOKEN, "419fc");
                mJsonObject.put(AppConstants.RESPONSE_DATA, "all_cities");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.GET_MASTER_DATA_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Utility.hideProgressDialog();
                    AppController.getInstance().mEditor.putString(getString(R.string.CityList_SP), response.toString());
                    AppController.getInstance().mEditor.apply();
                }
            }, error -> {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Utility.hideProgressDialog();
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConstants.API_KEY, "q1fgdfggfw2e2rt3y5u6i8iug12fh123yhhddaf");
                    params.put(AppConstants.DEVICE_TYPE, "Android");
                    params.put(AppConstants.DEVICE_ID, "8a48868c47e1a6e3");
                    params.put(AppConstants.DEVICE_TOKEN, "f1pYvxtdeqQ:APA91bF6eydEjZ2G1YOVtaJLddNL8RmNj7LFnJY-_SFnzXtRmzxMMte2K8B2PnAyI2SxAUfUQB8M65dAalkhOHRyh2qd34ZRWhhwElqvscjkCNeohPG6NXhfRhqG_4jOYPkjmyxyyokI");
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(AppConstants.INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "");
        }
    }

}