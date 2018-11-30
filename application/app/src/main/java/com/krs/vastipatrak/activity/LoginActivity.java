package com.krs.vastipatrak.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.ConnectivityReceiver;
import com.krs.vastipatrak.utils.LocaleHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.watchYoutubeVideo;


public class LoginActivity extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {


    final boolean[] isLogin = {false};
    private final String[] INIT_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS};
    private final int CAMARA_REQUEST = 4;
    private final String[] CALL_CAMARA = {Manifest.permission.CAMERA};

    @NonNull
    private final String tag_json_obj = "jobj_req";
    private final String TAG = MainActivity.class.getSimpleName();
    Resources resources;
    Context context;
    TextView txtLan;
    TextView tv;
    Snackbar snackbar;
    @Nullable
    private JSONObject json = null;
    private ImageView img_profile, img_cancel;
    private String str_profile_hash = "";
    private boolean isShow = true;
    private boolean isShow1 = true;
    @Nullable
    private String screen = "";
    private EditText inputEmail, inputPassword, inputName, inputConformPassword, inputForgotPassword, inputMobile, input_email_mobile, edt_spouse_name, edt_address;
    private TextInputLayout inputLayoutName, inputLayoutEmail, input_layout_email_mobile, inputLayoutPassword, inputLayoutConformPassword, InputLayoutForgotPassword, inputLayoutMobile, input_layout_spouse_name, input_layout_address;
    @Nullable
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor;
    private boolean SignupToggle = true;
    private Button btn_signup;
    private TextView txt_forgot, txtSignup, txt_label;
    @Nullable
    private TextView txtHow = null;

    private static boolean isValidEmail(@NonNull String email) {
        return TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Memory_Allocation();
        setListner();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                mEditor.putString(Common.Constant_Class.DEVICE_TOKEN, newToken);
                mEditor.apply();
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {

            if (Common.canCallPhone(this) || !Common.canAccessLocation(this) || !Common.canSMS(this)) {
                int INIT_REQUEST = 1;
                requestPermissions(INIT_PERMS, INIT_REQUEST);
            }
        }

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            screen = mBundle.getString(Common.Constant_Class.SCREEN);
            if (screen != null && screen.equalsIgnoreCase(Common.Constant_Class.SEARCH_FRAGMENT)) {
                SignupToggle = true;
                togglePage();
                txtSignup.setVisibility(View.GONE);
                btn_signup.setText(getResources().getString(R.string.btn_add_new));
            }
        }

        if (mSharedPreferences != null && !mSharedPreferences.getString(Common.Constant_Class.USER_ID, "").equalsIgnoreCase("") && screen == null) {
            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);

            mIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
            startActivity(mIntent);
            finish();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePage();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SignupToggle) {
                    LoginWS();
                } else {
                    SignupWS();
                }
            }
        });

        txt_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog forgot_dialog = new Dialog(LoginActivity.this);
                forgot_dialog.setContentView(R.layout.dialog_custom);
                forgot_dialog.setTitle(R.string.app_name);
                forgot_dialog.setCancelable(false);
                InputLayoutForgotPassword = forgot_dialog.findViewById(R.id.input_layout_forgot_password);
                inputForgotPassword = forgot_dialog.findViewById(R.id.input_forgot_password);
                inputForgotPassword.addTextChangedListener(new MyTextWatcher(inputForgotPassword));
                Button btn_send = forgot_dialog.findViewById(R.id.btn_send);
                Button btn_cancel = forgot_dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forgot_dialog.dismiss();
                    }
                });
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ForgotPasswordWS(forgot_dialog);
                    }
                });
                forgot_dialog.show();
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!SignupToggle) {
                    selectImage(LoginActivity.this);
                }
            }
        });

        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, @Nullable KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (SignupToggle) {
                        LoginWS();
                    }
                }
                return false;
            }
        });

        assert txtHow != null;
        txtHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(LoginActivity.this, getResources().getString(R.string.login_1));
            }
        });
        Common.getDeviceId(this);
        if (mSharedPreferences.getString(Common.Constant_Class.LAN, "en").equalsIgnoreCase("de")) {
            context = LocaleHelper.setLocale(LoginActivity.this, "de");
        } else {
            context = LocaleHelper.setLocale(LoginActivity.this, "en");
        }
        resources = context.getResources();
        checkConnection();
        //showActivityOverlay();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void Memory_Allocation() {
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
        mEditor.putString(Common.Constant_Class.NOTIFICATION, "");
        mEditor.apply();
        snackbar = Snackbar.make(findViewById(R.id.ll_login), R.string.not_connected, Snackbar.LENGTH_INDEFINITE);
        txtHow = findViewById(R.id.txtHow);
        txt_label = findViewById(R.id.txt_label);

        input_layout_spouse_name = findViewById(R.id.input_layout_spouse_name);
        edt_spouse_name = findViewById(R.id.edt_spouse_name);

        input_layout_address = findViewById(R.id.input_layout_address);
        edt_address = findViewById(R.id.edt_address);


        inputEmail = findViewById(R.id.input_email);
        inputLayoutEmail = findViewById(R.id.input_layout_email);

        input_email_mobile = findViewById(R.id.input_email_mobile);
        input_layout_email_mobile = findViewById(R.id.input_layout_email_mobile);

        inputMobile = findViewById(R.id.input_mobile);
        inputLayoutMobile = findViewById(R.id.input_layout_mobile);

        inputPassword = findViewById(R.id.input_password);

        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputConformPassword = findViewById(R.id.input_conform_password);


        inputConformPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);

        inputLayoutConformPassword = findViewById(R.id.input_layout_conform_password);
        inputName = findViewById(R.id.input_name);
        inputLayoutName = findViewById(R.id.input_layout_name);

        btn_signup = findViewById(R.id.btn_signup);
        txt_forgot = findViewById(R.id.txt_forgot);
        txtSignup = findViewById(R.id.txtSignup);
        img_profile = findViewById(R.id.img_profile);
        img_cancel = findViewById(R.id.img_cancel);

        tv = findViewById(R.id.TextView03);
        tv.setSelected(true);
        txtLan = findViewById(R.id.txtLan);
        txtLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(LoginActivity.this, "Work in Progress", Toast.LENGTH_SHORT).show();


            }
        });

        inputPassword.setOnTouchListener(new EditText.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputPassword.getRight() - inputPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (isShow) {
                            inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_view, 0);
                            inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                            isShow = false;
                        } else {
                            inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);
                            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            isShow = true;
                        }
                        inputPassword.setSelection(inputPassword.length());

                        return true;
                    }
                }
                return false;
            }
        });


        inputConformPassword.setOnTouchListener(new EditText.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputConformPassword.getRight() - inputConformPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (isShow1) {
                            inputConformPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_view, 0);
                            inputConformPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            isShow1 = false;
                        } else {
                            inputConformPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);
                            inputConformPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            isShow1 = true;
                        }
                        try {
                            inputConformPassword.setSelection(inputPassword.length());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_profile.setImageDrawable(getDrawable(R.drawable.user_profile));
            }
        });
    }

    private void showActivityOverlay() {
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_activity);

        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.llOverlay_activity);
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void setListner() {
        edt_spouse_name.addTextChangedListener(new MyTextWatcher(edt_spouse_name));
        edt_address.addTextChangedListener(new MyTextWatcher(edt_address));
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputConformPassword.addTextChangedListener(new MyTextWatcher(inputConformPassword));
    }

    private void togglePage() {
        if (SignupToggle) {
            txtSignup.setText(getResources().getString(R.string.btn_sign_in));
            inputLayoutMobile.setVisibility(View.VISIBLE);
            inputLayoutName.setVisibility(View.VISIBLE);
            inputLayoutConformPassword.setVisibility(View.VISIBLE);
            inputLayoutEmail.setVisibility(View.VISIBLE);
            input_layout_email_mobile.setVisibility(View.GONE);
            txt_forgot.setVisibility(View.GONE);
            img_profile.setVisibility(View.VISIBLE);
            img_cancel.setVisibility(View.VISIBLE);
            input_layout_address.setVisibility(View.VISIBLE);
            input_layout_spouse_name.setVisibility(View.VISIBLE);
            // spinnerSubcast.setVisibility(View.GONE);
            // spinnerEkdo.setVisibility(View.GONE);
            btn_signup.setText(getResources().getString(R.string.btn_sign_up));
            txtHow.setText(getResources().getString(R.string.how_to_signup));
            txt_label.setVisibility(View.VISIBLE);
            txt_label.setText(R.string.nice_signup);
            SignupToggle = false;
            inputPassword.setText("");
            inputName.requestFocus();
        } else {
            txtHow.setText(getResources().getString(R.string.how_to_login));
            txt_label.setVisibility(View.GONE);
            txtSignup.setText(getResources().getString(R.string.btn_sign_up));
            inputLayoutMobile.setVisibility(View.GONE);
            inputLayoutName.setVisibility(View.GONE);
            txt_forgot.setVisibility(View.VISIBLE);
            inputLayoutConformPassword.setVisibility(View.GONE);
            inputLayoutEmail.setVisibility(View.GONE);
            input_layout_email_mobile.setVisibility(View.VISIBLE);
            //  spinnerSubcast.setVisibility(View.GONE);
            //  spinnerEkdo.setVisibility(View.GONE);
            img_profile.setVisibility(View.INVISIBLE);
            img_cancel.setVisibility(View.INVISIBLE);
            input_layout_address.setVisibility(View.GONE);
            input_layout_spouse_name.setVisibility(View.GONE);
            btn_signup.setText(getResources().getString(R.string.btn_sign_in));
            SignupToggle = true;
            input_email_mobile.requestFocus();
        }
        //   setLocalization();
    }

    private void validateSpouseName() {
        if (edt_spouse_name.getText().toString().trim().isEmpty()) {
            input_layout_spouse_name.setError(getString(R.string.err_msg_spouse_name));
            requestFocus(edt_spouse_name);
        } else {
            input_layout_spouse_name.setErrorEnabled(false);
        }
    }


    private void validateAddress() {
        if (edt_address.getText().toString().trim().isEmpty()) {
            input_layout_address.setError(getString(R.string.err_msg_address));
            requestFocus(edt_address);
        } else {
            input_layout_address.setErrorEnabled(false);
        }
    }

    private void validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
    }

    private void validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
    }

    private void validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
    }

    private void validateConformPassword() {
        if (!inputPassword.getText().toString().trim().equalsIgnoreCase(inputConformPassword.getText().toString().trim())) {
            inputLayoutConformPassword.setError(getString(R.string.err_msg_conform_password));
            requestFocus(inputConformPassword);
        } else {
            inputLayoutConformPassword.setErrorEnabled(false);
        }
    }


    private void validateForgotPassword() {
        String email = inputForgotPassword.getText().toString().trim();

        if (email.isEmpty() || isValidEmail(email)) {
            InputLayoutForgotPassword.setError("Enter valid email address");
            requestFocus(inputForgotPassword);
        } else {
            InputLayoutForgotPassword.setErrorEnabled(false);
        }

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void ForgotPasswordWS(Dialog d) {
        if (Common.isOnline(this)) {

            String forgot_email = inputForgotPassword.getText().toString();
            if (!forgot_email.equalsIgnoreCase("")) {
                d.dismiss();
                JSONObject json = new JSONObject();
                try {
                    json.put(Common.Constant_Class.EMAIL_ADDRESS, forgot_email);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = Common.Constant_Class.FORGOT_PASSWORD_URL;
                Common.showProgressDialog(this);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            hideProgressDialog();
                            boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                            String message = response.getString(Common.Constant_Class.MESSAGE);

                            if (success) {
                                if (response.has(Common.Constant_Class.PASSWORD) && response.has(Common.Constant_Class.MOBILE)) {
                                    inputPassword.setText("");
                                }
                                Common.alert(LoginActivity.this, message);
                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                    String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                    if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                        Intent mIntent = new Intent(LoginActivity.this, LoginActivity.class);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mIntent);
                                        finish();
                                    }
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hideProgressDialog();
                    }
                }) {
                    @NonNull
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                        params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                        return params;
                    }
                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.err_msg_email), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void LoginWS() {

        final String email = input_email_mobile.getText().toString();
        final String password = inputPassword.getText().toString();

        if (!email.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
            if (Common.isOnline(this)) {

                try {
                    json = new JSONObject();
                    json.put(Common.Constant_Class.USERNAME, email);
                    json.put(Common.Constant_Class.PASSWORD, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Common.showProgressDialog(this);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.LOGIN_URL, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            hideProgressDialog();
                            boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                            String message = response.getString(Common.Constant_Class.MESSAGE);

                            if (success) {
                                String data = response.getString(Common.Constant_Class.DATA);
                                JSONObject mjson_data = new JSONObject(data);

                                String status = mjson_data.getString(Common.Constant_Class.STATUS);
                                if (status.equalsIgnoreCase("1")) {
                                    Common.SaveProfile(mjson_data);

                                    String user_id = mjson_data.getString(Common.Constant_Class.ID);
                                    String profile_url = mjson_data.getString(Common.Constant_Class.PROFILE_PIC_URL);
                                    String first_name = mjson_data.getString(Common.Constant_Class.FIRST_NAME);
                                    String last_name = mjson_data.getString(Common.Constant_Class.LAST_NAME);

                                    String office_lat = mjson_data.getString(Common.Constant_Class.OFFICE_LAT);
                                    String office_lng = mjson_data.getString(Common.Constant_Class.OFFICE_LNG);
                                    String home_lat = mjson_data.getString(Common.Constant_Class.HOME_LAT);
                                    String home_lng = mjson_data.getString(Common.Constant_Class.HOME_LNG);

                                    String access_token = mjson_data.getString(Common.Constant_Class.ACCESS_TOKEN);
                                    String updated_time = mjson_data.getString(Common.Constant_Class.UPDATED_TIME);
                                    String role = mjson_data.getString(Common.Constant_Class.ROLE);
                                    String is_location_enable = mjson_data.getString(Common.Constant_Class.IS_LOCATION_ENABLE);

                                    mEditor.putString(Common.Constant_Class.EMAIL, email);
                                    mEditor.putString(Common.Constant_Class.PASSWORD, password);
                                    mEditor.putString(Common.Constant_Class.USER_ID, user_id);
                                    mEditor.putString(Common.Constant_Class.PROFILE_PIC_URL, profile_url);
                                    mEditor.putString(Common.Constant_Class.FIRST_NAME, first_name);
                                    mEditor.putString(Common.Constant_Class.LAST_NAME, last_name);
                                    mEditor.putString(Common.Constant_Class.ACCESS_TOKEN, access_token);
                                    mEditor.putString(Common.Constant_Class.UPDATED_TIME, updated_time);
                                    mEditor.putString(Common.Constant_Class.ROLE, role);
                                    mEditor.putString(Common.Constant_Class.TBTN_SHARE, is_location_enable);
                                    mEditor.putString(Common.Constant_Class.OFFICE_LAT, office_lat);
                                    mEditor.putString(Common.Constant_Class.OFFICE_LNG, office_lng);
                                    mEditor.putString(Common.Constant_Class.HOME_LAT, home_lat);
                                    mEditor.putString(Common.Constant_Class.HOME_LNG, home_lng);
                                    mEditor.apply();

                                    /*Bundle fb_bundle = new Bundle();
                                    fb_bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, Integer.parseInt(user_id));
                                    fb_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, first_name + " " + last_name);
                                    AppController.getInstance().firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fb_bundle);
*/

                                   /* if (user_id.equalsIgnoreCase(Common.Constant_Class.ADMIN_1) || user_id.equalsIgnoreCase(Common.Constant_Class.ADMIN_2)) {
                                        AppController.isAdmin = true;
                                    }*/
                                    Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    if (mSharedPreferences != null) {
                                        mIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                                    }
                                    if (!isLogin[0]) {
                                        isLogin[0] = true;
                                        startActivity(mIntent);
                                        finish();
                                    }
                                } else {
                                    Common.alert(LoginActivity.this, "Registration request is pending. Please contact to Admin !!");
                                }
                            } else {
                                Common.alert(LoginActivity.this, message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        String message = null;
                        if (error instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_LONG).show();
                    }
                }) {
                    @NonNull
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                        params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                        params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                        params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences != null ? mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, "") : null);
                        if (mSharedPreferences != null) {
                            params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                        }
                        return params;
                    }
                };
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            }
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
        }
    }

    private void SignupWS() {
        if (Common.isOnline(this)) {
            final String name = inputName.getText().toString();
            final String email = inputEmail.getText().toString();
            final String mobile = inputMobile.getText().toString().trim();
            final String password = inputPassword.getText().toString();
            String cpassword = inputConformPassword.getText().toString();
            final String spouse_name = edt_spouse_name.getText().toString();
            final String address = edt_address.getText().toString();

            if (!email.equalsIgnoreCase("")) {
                if (Common.isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, "Type Valid Email Address!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!email.equalsIgnoreCase("") && !name.equalsIgnoreCase("") && !mobile.equalsIgnoreCase("") && !password.equalsIgnoreCase("") && !cpassword.equalsIgnoreCase("") && !spouse_name.equalsIgnoreCase("") && !address.equalsIgnoreCase("")) {
                if (password.equalsIgnoreCase(cpassword)) {
                    if (mobile.length() == 10) {
                        try {
                            json = new JSONObject();
                            Common.showProgressDialog(this);
                            json.put(Common.Constant_Class.FIRST_NAME, name);
                            json.put(Common.Constant_Class.SPOUSE_NAME, spouse_name);
                            json.put(Common.Constant_Class.EMAIL_ADDRESS, email);
                            json.put(Common.Constant_Class.MOBILE, mobile);
                            json.put(Common.Constant_Class.PASSWORD, password);
                            json.put(Common.Constant_Class.REPEAT_PASSWORD, cpassword);
                            json.put(Common.Constant_Class.ADDRESS, address);

                            if (screen != null && screen.equalsIgnoreCase(Common.Constant_Class.SEARCH_FRAGMENT)) {
                                json.put(Common.Constant_Class.STATUS, "1");
                            } else {
                                json.put(Common.Constant_Class.STATUS, "0");
                            }
                            if (!str_profile_hash.isEmpty()) {
                                json.put(Common.Constant_Class.PROFILE_PIC, str_profile_hash);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SIGNUP_URL, json, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(@NonNull JSONObject response) {
                                Log.d(TAG, response.toString());

                                try {
                                    hideProgressDialog();
                                    boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                                    String message = response.getString(Common.Constant_Class.MESSAGE);
                                    if (success) {
                                        if (message.contains("admin")) {
                                            inputName.setText("");
                                            inputEmail.setText("");
                                            inputMobile.setText("");
                                            inputPassword.setText("");
                                            inputConformPassword.setText("");
                                            inputPassword.setText("");
                                            edt_spouse_name.setText("");
                                            edt_address.setText("");
                                            togglePage();
                                        }
                                    }
                                    Common.alert(LoginActivity.this, message);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(@NonNull VolleyError error) {
                                hideProgressDialog();
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                                String message = null;
                                if (error instanceof NetworkError) {
                                    message = "Cannot connect to Internet...Please check your connection!";
                                } else if (error instanceof ServerError) {
                                    message = "The server could not be found. Please try again after some time!!";
                                } else if (error instanceof AuthFailureError) {
                                    message = "Cannot connect to Internet...Please check your connection!";
                                } else if (error instanceof ParseError) {
                                    message = "Parsing error! Please try again after some time!!";
                                } else if (error instanceof TimeoutError) {
                                    message = "Connection TimeOut! Please check your internet connection.";
                                }
                                Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_LONG).show();

                            }
                        }) {
                            @NonNull
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> params = new HashMap<>();
                                params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                                params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                                params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                                assert mSharedPreferences != null;
                                params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                                return params;
                            }
                        };
                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.err_msg_invalid_mobile), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.err_msg_repeat_password), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }

    private void selectImage(@NonNull final Activity mActivity) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(@NonNull DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (Common.canCAMARA(LoginActivity.this)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(CALL_CAMARA, CAMARA_REQUEST);
                        }
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    mActivity.startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap bmp = null;
        if (data != null) {
            try {
                if (data.getData() == null) {
                    bmp = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                } else {
                    Uri selectedImage = data.getData();
                    bmp = Common.scaleImage(this, selectedImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bmp != null) {
                if (resultCode == RESULT_OK) {
                    Glide.with(this).load(bmp).thumbnail(0.5f).apply(RequestOptions.circleCropTransform()).into(img_profile);
                    str_profile_hash = Common.getBase64(bmp);
                }
            }
        }
    }

    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            if (snackbar != null) {
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        } else {
            if (snackbar != null) {
                if (snackbar.isShownOrQueued()) {
                    snackbar.dismiss();
                }
            }
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private class MyTextWatcher implements TextWatcher {

        private final View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_conform_password:
                    validateConformPassword();
                    break;
                case R.id.input_forgot_password:
                    validateForgotPassword();
                    break;
                case R.id.edt_spouse_name:
                    validateSpouseName();
                    break;
                case R.id.edt_address:
                    validateAddress();
                    break;
            }
        }
    }
}
