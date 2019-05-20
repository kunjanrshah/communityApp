package com.krs.vastipatrak.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangePasswordFragment extends Fragment {

    private final String TAG = ChangePasswordFragment.class.getSimpleName();
    private SharedPreferences mSharedPreferences;
    private EditText input_password;
    private EditText input_repeat;
    private FloatingActionButton fab;
    private boolean isShow = true;
    private boolean isShow1 = true;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
        Memory_Allocation(rootView);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_topback);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) mActivity).getSupportActionBar()).setSubtitle("Change Password");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_password.getText().toString().equalsIgnoreCase("") && !input_repeat.getText().toString().equalsIgnoreCase("")) {
                    if (input_password.getText().toString().equalsIgnoreCase(input_repeat.getText().toString())) {
                        if (Utility.isOnline(mActivity)) {
                            call_change_password_ws();
                        } else {
                            Toast.makeText(mActivity, AppConstants.NO_CONNECTION, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mActivity, "Password does not match !!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mActivity, "Enter your password !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        input_password.setOnTouchListener(new EditText.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (input_password.getRight() - input_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (isShow) {
                            input_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_show, 0);
                            input_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                            isShow = false;
                        } else {
                            input_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_hide, 0);
                            input_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            isShow = true;
                        }
                        input_password.setSelection(input_password.length());

                        return true;
                    }
                }
                return false;
            }
        });

        input_repeat.setOnTouchListener(new EditText.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (input_repeat.getRight() - input_repeat.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (isShow1) {
                            input_repeat.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_show, 0);
                            input_repeat.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                            isShow1 = false;
                        } else {
                            input_repeat.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_hide, 0);
                            input_repeat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            isShow1 = true;
                        }
                        input_repeat.setSelection(input_repeat.length());
                        return true;
                    }
                }
                return false;
            }
        });

        return rootView;
    }

    private void call_change_password_ws() {

        if (Utility.isOnline(mActivity)) {

            Utility.showProgressDialog(getActivity());
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
                mJsonObject.put(AppConstants.PASSWORD, input_password.getText());
                mJsonObject.put(AppConstants.REPEAT_PASSWORD, input_repeat.getText());

            } catch (Exception e) {
                e.printStackTrace();
            }


            final String password_url = AppConstants.CHANGE_PASSWORD_URL;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, password_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "profile_url: " + password_url);
                    Log.d(TAG, "response: " + response.toString());
                    Utility.hideProgressDialog();

                    try {
                        String message = response.getString(AppConstants.MESSAGE);
                        String success = response.getString(AppConstants.SUCCESS);
                        if (success.equalsIgnoreCase(AppConstants.TRUE)) {
                            Utility.UpdateProfilePassword(input_password.getText().toString(), mSharedPreferences.getString(AppConstants.USER_ID, ""));
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            if (response.has(AppConstants.ERROR_CODE)) {
                                String error = response.getString(AppConstants.ERROR_CODE);
                                if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                                    Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    getActivity().finish();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    Utility.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    private void Memory_Allocation(View rootView) {

        mActivity = getActivity();
        input_password = rootView.findViewById(R.id.input_password);
        input_repeat = rootView.findViewById(R.id.input_repeat);
        input_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_hide, 0);
        input_repeat.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_hide, 0);

        fab = rootView.findViewById(R.id.fab);
        mSharedPreferences = mActivity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
