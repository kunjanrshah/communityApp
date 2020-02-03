/*
 * Copyright 2018 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.krs.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.authenticator.PasscodeViewPinAuthenticator;
import com.kevalpatel.passcodeview.indicators.CircleIndicator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.keys.KeyNamesBuilder;
import com.kevalpatel.passcodeview.keys.RoundKey;
import com.krs.community.R;
import com.krs.community.model.Member;
import com.krs.community.utils.Utility;

import okhttp3.internal.Util;

/**
 * Created by Keval on 06-Apr-17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
public class PinViewActivity extends AppCompatActivity {
    private static final String ARG_CURRENT_PIN = "current_pin";

    private PinView mPinView;
    private Member member;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinview);

        mPinView = findViewById(R.id.pattern_view);
        member= (Member)getIntent().getSerializableExtra(getString(R.string.member));
        imgView = findViewById(R.id.imageView);
        //Set the correct pin code.
        //REQUIRED
        //if (!member.getProfilePic().isEmpty()) {
            try {
                //String str=getResources().getString(R.string.base_url_thumb)+member.getProfilePic();
                String str="https://muslimghanchisamaj.in/uploads/users/thumb/6338dfa3c3f1c07515fcb2503e242975.jpg";
                Glide.with(this).load(str).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(imgView);
            } catch (Exception e) {
                e.getMessage();
            }
        //}
        String pass=member.getProfilePassword();
        int[] correctPattern = null;
        if(pass!=null && !pass.isEmpty()){
            correctPattern=new int[pass.length()];
            for (int i = 0; i < pass.length(); i++) {
                try {
                  correctPattern[i] = Integer.parseInt(String.valueOf(pass.charAt(i)));
                } catch (NumberFormatException nfe) {
                }
            }
        }
        //final int[] correctPattern = new int[]{1, 2, 3, 5,4,7};
        if(correctPattern!=null){
            mPinView.setPinAuthenticator(new PasscodeViewPinAuthenticator(correctPattern));
        }

        //Build the desired key shape and pass the theme parameters.
        //REQUIRED
        mPinView.setKey(new RoundKey.Builder(mPinView)
                .setKeyPadding(R.dimen.key_padding)
                .setKeyStrokeColorResource(R.color.white)
                .setKeyStrokeWidth(R.dimen.key_stroke_width)
                .setKeyTextColorResource(R.color.white)
                .setKeyTextSize(R.dimen.key_text_size));

        //Build the desired indicator shape and pass the theme attributes.
        //REQUIRED
        mPinView.setIndicator(new CircleIndicator.Builder(mPinView)
                .setIndicatorRadius(R.dimen.indicator_radius)
                .setIndicatorFilledColorResource(R.color.white)
                .setIndicatorStrokeColorResource(R.color.white)
                .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width));

        mPinView.setPinLength(PinView.DYNAMIC_PIN_LENGTH);

        //Set the name of the keys based on your locale.
        //OPTIONAL. If not passed key names will be displayed based on english locale.
        mPinView.setKeyNames(new KeyNamesBuilder()
                .setKeyOne(this, R.string.key_1)
                .setKeyTwo(this, R.string.key_2)
                .setKeyThree(this, R.string.key_3)
                .setKeyFour(this, R.string.key_4)
                .setKeyFive(this, R.string.key_5)
                .setKeySix(this, R.string.key_6)
                .setKeySeven(this, R.string.key_7)
                .setKeyEight(this, R.string.key_8)
                .setKeyNine(this, R.string.key_9)
                .setKeyZero(this, R.string.key_0));

        mPinView.setTitle("Enter the PIN");

        mPinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                //User authenticated successfully.
                //Navigate to secure screens.
                startActivity(new Intent(PinViewActivity.this, DashboardActivity.class));
                finish();
                Utility.fade(PinViewActivity.this);
            }

            @Override
            public void onAuthenticationFailed() {
                //Calls whenever authentication is failed or user is unauthorized.
                //Do something
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putIntArray(ARG_CURRENT_PIN, mPinView.getCurrentTypedPin());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPinView.setCurrentTypedPin(savedInstanceState.getIntArray(ARG_CURRENT_PIN));
    }
}
