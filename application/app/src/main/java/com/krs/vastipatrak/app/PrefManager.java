package com.krs.vastipatrak.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.krs.vastipatrak.utils.Common;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
  //  private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_SLIDER_WELCOME = "IsSliderWelcome";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Common.Constant_Class.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setSliderWelcome(boolean isSliderWelcome) {
        editor.putBoolean(IS_SLIDER_WELCOME, isSliderWelcome);
        editor.commit();
    }

    public boolean isSliderWelcome() {
        return pref.getBoolean(IS_SLIDER_WELCOME, false);
    }

}
