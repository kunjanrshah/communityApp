package com.krs.vastipatrak.activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.app.PrefManager;
import com.krs.vastipatrak.model.Data;
import com.krs.vastipatrak.model.ForgotPasswordData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.model.LoginData;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.RoundedImageView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;


public class LoginActivity extends Activity {


    private final String[] INIT_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS};
    private final int INIT_REQUEST = 1;
    JSONObject json = null;
    Dialog Forgot_dialog;
    String[] SubcastList = {"Dasha"};
    String[] EkdoList = {"Modasa"};
    RoundedImageView img_profile;
    String str_profile_hash = "";
    MaterialBetterSpinner spinnerSubcast, spinnerEkdo;
    boolean isShow = true, isShow1 = true;
    String screen = "";
    ProgressDialog pDialog;
    TextView txtTour = null;
    Realm realm;
    private EditText inputEmail, inputPassword, inputName, inputConformPassword, inputForgotPassword, inputMobile, input_email_mobile, edt_father_name, edt_surname, edt_address, edt_native;
    private TextInputLayout inputLayoutName, inputLayoutEmail, input_layout_email_mobile, inputLayoutPassword, inputLayoutConformPassword, InputLayoutForgotPassword, inputLayoutMobile, input_layout_father_name, input_layout_surname, input_layout_address, input_layout_native_place;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor;
    private PrefManager prefManager;
    private boolean SignupToggle = true;
    private Button btn_signup;
    private TextView txt_forgot, txtSignup;
    // These tags will be used to cancel the requests
    private String tag_json_obj = "jobj_req";
    private String TAG = MainActivity.class.getSimpleName();

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_login);
        Memory_Allocation();
        setListner();

        if (Build.VERSION.SDK_INT >= 23) {

            if (!Common.canCallPhone(this) || !Common.canAccessLocation(this) || !Common.canSMS(this)) {
                requestPermissions(INIT_PERMS, INIT_REQUEST);
            }
        }

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            screen = mBundle.getString(Common.Constant_Class.SCREEN);
            if (screen.equalsIgnoreCase(Common.Constant_Class.SEARCH_FRAGMENT)) {
                SignupToggle = true;
                togglePage();
                txtSignup.setVisibility(View.GONE);
                btn_signup.setText(getResources().getString(R.string.btn_add_new));
            }
        }


        if (!mSharedPreferences.getString(Common.Constant_Class.USER_ID, "").toString().equalsIgnoreCase("") && !screen.equalsIgnoreCase(Common.Constant_Class.SEARCH_FRAGMENT)) {

            if (!prefManager.isSliderWelcome()) {
                Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                mIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                startActivity(mIntent);
            } else {
                prefManager.setSliderWelcome(false);
            }
            finish();
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
                //      clearAll();
            }
        });

        txt_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Forgot_dialog = new Dialog(LoginActivity.this);
                Forgot_dialog.setContentView(R.layout.dialog_custom);
                Forgot_dialog.setTitle(getResources().getString(R.string.forgot_password));

                InputLayoutForgotPassword = Forgot_dialog.findViewById(R.id.input_layout_forgot_password);
                inputForgotPassword = Forgot_dialog.findViewById(R.id.input_forgot_password);
                inputForgotPassword.addTextChangedListener(new MyTextWatcher(inputForgotPassword));
                Button btn_send = Forgot_dialog.findViewById(R.id.btn_send);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ForgotPasswordWS();
                        //String url = Common.Constant_Class.FORGOT_PASSWORD_URL + "?" + Common.Constant_Class.EMAIL_ADDRESS + "=" + "kunjanrshah@gmail.com";

//                        new JsonTask().execute(url);
                    }
                });
                Forgot_dialog.show();
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
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (SignupToggle) {
                        LoginWS();
                    }
                }
                return false;
            }
        });

        txtTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(LoginActivity.this, TourActivity.class);
                startActivity(mIntent);
            }
        });
        Common.getDeviceId(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void clearAll() {
        inputEmail.setText("");
        inputPassword.setText("");
        inputName.setText("");
        inputConformPassword.setText("");
        //inputForgotPassword.setText("");
        inputMobile.setText("");
        input_email_mobile.setText("");

        edt_surname.setText("");
        edt_native.setText("");
        edt_father_name.setText("");
        edt_address.setText("");

    }

    private void Memory_Allocation() {


        prefManager = new PrefManager(this);
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        realm = AppController.getInstance().realm;
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(true);

        txtTour = findViewById(R.id.txtTour);

        input_layout_father_name = findViewById(R.id.input_layout_father_name);
        edt_father_name = findViewById(R.id.edt_father_name);

        input_layout_surname = findViewById(R.id.input_layout_surname);
        edt_surname = findViewById(R.id.edt_surname);

        input_layout_address = findViewById(R.id.input_layout_address);
        edt_address = findViewById(R.id.edt_address);

        input_layout_native_place = findViewById(R.id.input_layout_native_place);
        edt_native = findViewById(R.id.edt_native);

        inputEmail = findViewById(R.id.input_email);
        inputLayoutEmail = findViewById(R.id.input_layout_email);

        input_email_mobile = findViewById(R.id.input_email_mobile);
        input_layout_email_mobile = findViewById(R.id.input_layout_email_mobile);

        inputMobile = findViewById(R.id.input_mobile);
        inputLayoutMobile = findViewById(R.id.input_layout_mobile);

        inputPassword = findViewById(R.id.input_password);

        //  inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);


        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputConformPassword = findViewById(R.id.input_conform_password);

        inputConformPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);

        inputLayoutConformPassword = findViewById(R.id.input_layout_conform_password);
        inputName = findViewById(R.id.input_name);
        inputLayoutName = findViewById(R.id.input_layout_name);

        btn_signup = findViewById(R.id.btn_signup);
        txt_forgot = findViewById(R.id.txt_forgot);
        txtSignup = findViewById(R.id.txtSignup);
        //  fab = (FloatingActionButton) findViewById(R.id.fab);
        img_profile = findViewById(R.id.img_profile);
        spinnerSubcast = findViewById(R.id.spinnerSubcast);
        spinnerEkdo = findViewById(R.id.spinnerEkdo);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, EkdoList);
        spinnerEkdo.setAdapter(arrayAdapter1);
        spinnerEkdo.setText("Modasa");

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, SubcastList);
        spinnerSubcast.setAdapter(arrayAdapter2);
        spinnerSubcast.setText("Dasha");

        TextView tv = findViewById(R.id.TextView03);
        tv.setSelected(true);

        inputPassword.setOnTouchListener(new EditText.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

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
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

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
                        inputConformPassword.setSelection(inputPassword.length());
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void showProgressDialog() {

        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage(Common.Constant_Class.LOADING);
            pDialog.setCancelable(true);
        }

        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void setListner() {

        edt_father_name.addTextChangedListener(new MyTextWatcher(edt_father_name));
        edt_surname.addTextChangedListener(new MyTextWatcher(edt_surname));
        edt_address.addTextChangedListener(new MyTextWatcher(edt_address));
        edt_native.addTextChangedListener(new MyTextWatcher(edt_native));
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
            input_layout_address.setVisibility(View.GONE);
            input_layout_father_name.setVisibility(View.GONE);
            input_layout_native_place.setVisibility(View.GONE);
            input_layout_surname.setVisibility(View.GONE);
            spinnerSubcast.setVisibility(View.GONE);
            spinnerEkdo.setVisibility(View.GONE);
            btn_signup.setText(getResources().getString(R.string.btn_sign_up));
            SignupToggle = false;
            inputName.requestFocus();
        } else {
            txtSignup.setText(getResources().getString(R.string.btn_sign_up));
            inputLayoutMobile.setVisibility(View.GONE);
            inputLayoutName.setVisibility(View.GONE);
            txt_forgot.setVisibility(View.VISIBLE);
            inputLayoutConformPassword.setVisibility(View.GONE);
            inputLayoutEmail.setVisibility(View.GONE);
            input_layout_email_mobile.setVisibility(View.VISIBLE);
            spinnerSubcast.setVisibility(View.GONE);
            spinnerEkdo.setVisibility(View.GONE);
            img_profile.setVisibility(View.INVISIBLE);
            input_layout_address.setVisibility(View.GONE);
            input_layout_father_name.setVisibility(View.GONE);
            input_layout_native_place.setVisibility(View.GONE);
            input_layout_surname.setVisibility(View.GONE);

            btn_signup.setText(getResources().getString(R.string.btn_sign_in));
            SignupToggle = true;
            input_email_mobile.requestFocus();
        }
    }

    private boolean validateFatherName() {
        if (edt_father_name.getText().toString().trim().isEmpty()) {
            input_layout_father_name.setError(getString(R.string.err_msg_father_name));
            requestFocus(edt_father_name);
            return false;
        } else {
            input_layout_father_name.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateSurname() {
        if (edt_surname.getText().toString().trim().isEmpty()) {
            input_layout_surname.setError(getString(R.string.err_msg_surname));
            requestFocus(edt_surname);
            return false;
        } else {
            input_layout_surname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNative() {
        if (edt_native.getText().toString().trim().isEmpty()) {
            input_layout_native_place.setError(getString(R.string.err_msg_native));
            requestFocus(edt_native);
            return false;
        } else {
            input_layout_native_place.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress() {
        if (edt_address.getText().toString().trim().isEmpty()) {
            input_layout_address.setError(getString(R.string.err_msg_address));
            requestFocus(edt_address);
            return false;
        } else {
            input_layout_address.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConformPassword() {
        if (!inputPassword.getText().toString().trim().equalsIgnoreCase(inputConformPassword.getText().toString().trim())) {
            inputLayoutConformPassword.setError(getString(R.string.err_msg_conform_password));
            requestFocus(inputConformPassword);
            return false;
        } else {
            inputLayoutConformPassword.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateForgotPassword() {
        String email = inputForgotPassword.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            InputLayoutForgotPassword.setError("Enter valid email address");
            requestFocus(inputForgotPassword);
            return false;
        } else {
            InputLayoutForgotPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).show();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void send_sms(final String phoneNo, final String password) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));

        builder.setMessage("Send SMS Your Password ??");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    String str = "Vastipatrak Application Password :" + password;
                    smsManager.sendTextMessage(phoneNo, null, str, null, null);
                    Toast.makeText(LoginActivity.this, getString(R.string.sms_sent), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.sms_failed), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    /*ProgressDialog pd;
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            Toast.makeText(LoginActivity.this,""+result,Toast.LENGTH_SHORT).show();

        }
    }*/


    private void ForgotPasswordWS() {
        if (Common.isOnline(this)) {

            String forgot_email = inputForgotPassword.getText().toString();
            if (!forgot_email.equalsIgnoreCase("")) {
                /*JSONObject json = new JSONObject();
                try {
                    json.put(Common.Constant_Class.EMAIL_ADDRESS, forgot_email);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                String url = Common.Constant_Class.FORGOT_PASSWORD_URL + "?" + Common.Constant_Class.EMAIL_ADDRESS + "=" + forgot_email;
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        if (Forgot_dialog != null) {
                            Forgot_dialog.dismiss();
                        }

                        //{"success":true,"message":"Password has been sent to your email address.","password":"UiacrkPa","mobile":"9825450261"}
                        try {
                            boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                            String message = response.getString(Common.Constant_Class.MESSAGE);

                            ForgotPasswordData mfpassData = new ForgotPasswordData();
                            mfpassData.setMessage(message);
                            mfpassData.setSuccess(success);


                            if (success) {
                                if (response.has(Common.Constant_Class.PASSWORD) && response.has(Common.Constant_Class.MOBILE)) {
                                    String mobile = response.getString(Common.Constant_Class.MOBILE);
                                    String password = response.getString(Common.Constant_Class.PASSWORD);
                                    mfpassData.getMobile(mobile);
                                    mfpassData.setPassword(password);

                                    inputPassword.setText("");
                                    // send_sms(mobile, password);
                                }

                            }

                            realm.beginTransaction();
                            realm.copyToRealm(mfpassData);
                            realm.commitTransaction();

                            // Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            alert(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        if (Forgot_dialog != null) {
                            Forgot_dialog.dismiss();
                        }

                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
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

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.LOGIN_URL, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            LoginData mLogindata = new LoginData();


                            boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                            String message = response.getString(Common.Constant_Class.MESSAGE);

                            mLogindata.setSuccess(success);
                            mLogindata.setMessage(message);

                            if (success) {
                                String data = response.getString(Common.Constant_Class.DATA);
                                JSONObject mjson_data = new JSONObject(data);

                                Data mdata = new Data();

                                String status = mjson_data.getString(Common.Constant_Class.STATUS);
                                mdata.setStatus(status);
                                if (status.equalsIgnoreCase("1")) {
                                    String user_id = mjson_data.getString(Common.Constant_Class.ID);
                                    String profile_url = mjson_data.getString(Common.Constant_Class.PROFILE_PIC_URL);
                                    String first_name = mjson_data.getString(Common.Constant_Class.FIRST_NAME);
                                    String last_name = mjson_data.getString(Common.Constant_Class.LAST_NAME);
                                    String access_token=mjson_data.getString(Common.Constant_Class.ACCESS_TOKEN);
                                    mdata.setId(user_id);
                                    mdata.setProfilePicUrl(profile_url);
                                    mdata.setFirstName(first_name);
                                    mdata.setLastName(last_name);

                                    mEditor.putString(Common.Constant_Class.EMAIL, email);
                                    mEditor.putString(Common.Constant_Class.PASSWORD, password);
                                    mEditor.putString(Common.Constant_Class.USER_ID, user_id);
                                    mEditor.putString(Common.Constant_Class.PROFILE_PIC_URL, profile_url);
                                    mEditor.putString(Common.Constant_Class.FIRST_NAME, first_name);
                                    mEditor.putString(Common.Constant_Class.LAST_NAME, last_name);
                                    mEditor.putString(Common.Constant_Class.ACCESS_TOKEN, access_token);
                                    mEditor.commit();

                                    Bundle fb_bundle = new Bundle();
                                    fb_bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, Integer.parseInt(user_id));
                                    fb_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, first_name + " " + last_name);
                                    AppController.getInstance().firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fb_bundle);


                                    if (user_id.equalsIgnoreCase(Common.Constant_Class.ADMIN_1) || user_id.equalsIgnoreCase(Common.Constant_Class.ADMIN_2)) {
                                        AppController.isAdmin = true;
                                    }
                                    Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    mIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                                    startActivity(mIntent);
                                    finish();
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    alert("Registration request is pending. Please contact to Admin !!");
                                }
                                mLogindata.setData(mdata);

                                realm.beginTransaction();
                                realm.copyToRealm(mLogindata);
                                realm.commitTransaction();

                            } else {
                                alert(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                        } else if (error instanceof NoConnectionError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                        params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                        params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);

                        return params;
                    }
                };


                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            } else {

                try {
                    json = new JSONObject();
                    json.put(Common.Constant_Class.EMAIL_ADDRESS, email);
                    json.put(Common.Constant_Class.PASSWORD, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ListProfileData mListProfileDatas = realm.where(ListProfileData.class).equalTo(Common.Constant_Class.EMAIL_ADDRESS, email)
                        .equalTo(Common.Constant_Class.PASSWORD, password).findFirst();


                if (mListProfileDatas != null) {
                    String user_id = mListProfileDatas.getProfile_id();
                    mEditor.putString(Common.Constant_Class.EMAIL, email);
                    mEditor.putString(Common.Constant_Class.PASSWORD, password);
                    mEditor.putString(Common.Constant_Class.USER_ID, user_id);
                    mEditor.commit();

                    Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                    startActivity(mIntent);
                    finish();
                }
                //  Toast.makeText(LoginActivity.this, Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
        }

    }

    private void SignupWS() {
        if (Common.isOnline(this)) {

            final String name = inputName.getText().toString();
            final String email = inputEmail.getText().toString();
            final String mobile = inputMobile.getText().toString();
            final String password = inputPassword.getText().toString();
            String cpassword = inputConformPassword.getText().toString();
            final String father_name = edt_father_name.getText().toString();
            final String surname = edt_surname.getText().toString();
            final String _native = edt_native.getText().toString();
            final String address = edt_address.getText().toString();
            String subcast = spinnerSubcast.getText().toString();
            final String ekdo = spinnerEkdo.getText().toString();


            if (!email.equalsIgnoreCase("") && !name.equalsIgnoreCase("") && !mobile.equalsIgnoreCase("") && !password.equalsIgnoreCase("") && !cpassword.equalsIgnoreCase("")) {
                if (password.equalsIgnoreCase(cpassword)) {

                    try {
                        json = new JSONObject();
                        json.put(Common.Constant_Class.FIRST_NAME, name);
                        json.put(Common.Constant_Class.EMAIL_ADDRESS, email);
                        json.put(Common.Constant_Class.MOBILE, mobile);
                        json.put(Common.Constant_Class.PASSWORD, password);
                        json.put(Common.Constant_Class.REPEAT_PASSWORD, cpassword);
                        json.put(Common.Constant_Class.SUB_CAST, subcast);
                        json.put(Common.Constant_Class.EKDO, ekdo);

                        if (screen != null && screen.equalsIgnoreCase(Common.Constant_Class.SEARCH_FRAGMENT)) {
                            json.put(Common.Constant_Class.STATUS, "1");
                        } else {
                            json.put(Common.Constant_Class.STATUS, "0");
                        }
                        if (!str_profile_hash.isEmpty()) {
                            json.put(Common.Constant_Class.PROFILE_PIC, "profile.png");
                            json.put(Common.Constant_Class.PROFILE_PIC_HASH, str_profile_hash);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SIGNUP_URL, json, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());

                            try {
                                boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                                String message = response.getString(Common.Constant_Class.MESSAGE);


                                if (success) {

                                    String user_id = response.getString(Common.Constant_Class.USER_ID);
                                    if (!message.contains("admin")) {

                                        String profile_url = response.getString(Common.Constant_Class.PROFILE_PIC_URL);
                                        String first_name = response.getString(Common.Constant_Class.FIRST_NAME);
                                        String last_name = response.getString(Common.Constant_Class.LAST_NAME);


                                        mEditor.putString(Common.Constant_Class.EMAIL, email);
                                        mEditor.putString(Common.Constant_Class.PASSWORD, password);
                                        mEditor.putString(Common.Constant_Class.USER_ID, user_id);
                                        mEditor.putString(Common.Constant_Class.FIRST_NAME, first_name);
                                        mEditor.putString(Common.Constant_Class.LAST_NAME, last_name);
                                        mEditor.putString(Common.Constant_Class.PROFILE_PIC_URL, profile_url);
                                        mEditor.commit();


                                        Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        mIntent.putExtra(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                                        startActivity(mIntent);
                                        finish();
                                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                    } else {

                                        ListProfileData mListProfileData = new ListProfileData();
                                        mListProfileData.setProfile_id(user_id);
                                        mListProfileData.setFirst_name(name);
                                        mListProfileData.setLast_name(surname);
                                        mListProfileData.setNative_place(_native);
                                        mListProfileData.setFather_name(father_name);
                                        mListProfileData.setAddress(address);
                                        mListProfileData.setEkdo(ekdo);
                                        mListProfileData.setEmail_address(email);
                                        mListProfileData.setPassword(password);
                                        mListProfileData.setMobile(mobile);

                                        realm.beginTransaction();
                                        realm.copyToRealm(mListProfileData);
                                        realm.commitTransaction();

                                        alert(message);
                                        // AppController.dbHelper.InsertProfileData(mListProfileData);

                                    }
                                } else {

                                    JSONObject mObjData = response.getJSONObject(Common.Constant_Class.DATA);
                                    String msg = "";
                                    if (mObjData.has(Common.Constant_Class.EMAIL_ADDRESS)) {
                                        msg = mObjData.getString(Common.Constant_Class.EMAIL_ADDRESS);
                                    } else if (mObjData.has(Common.Constant_Class.MOBILE)) {
                                        msg = mObjData.getString(Common.Constant_Class.MOBILE);
                                    }
                                    JSONArray mJsonArray = new JSONArray(msg);
                                    msg = mJsonArray.getString(0);
                                    alert(msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

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
                            } else if (error instanceof NoConnectionError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }
                            Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_LONG).show();

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                            params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                            params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);

                            return params;
                        }
                    };
                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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

    /*private void callStatusChangeWS(final int mode, final String user_id) {
        if (Common.isOnline(this)) {
            showProgressDialog();
            JSONObject mJsonObject = new JSONObject();
            try {
                mJsonObject.put(Common.Constant_Class.IDList, user_id);
                mJsonObject.put(Common.Constant_Class.STATUS, String.valueOf(mode));

            } catch (Exception e) {
                e.printStackTrace();
            }
            String deActivate_url = Common.Constant_Class.STATUS_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, deActivate_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    hideProgressDialog();
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            ArrayList<String> lstSelectedIDs = new ArrayList<String>();
                            lstSelectedIDs.add(user_id);
                            Common.UpdateProfileStatus(lstSelectedIDs, String.valueOf(mode));

                            if (screen != null && screen.equalsIgnoreCase(Common.Constant_Class.SEARCH_FRAGMENT)) {
                                Intent mIntent = new Intent(LoginActivity.this, MyProfileActivity.class);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mIntent.putExtra(Common.Constant_Class.DATA, user_id);
                                mIntent.putExtra(Common.Constant_Class.SCREEN, Common.Constant_Class.LOGIN_ACTIVITY);
                                startActivity(mIntent);
                                finish();
                            } else {
                                alert(message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    hideProgressDialog();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        } else {
            Toast.makeText(this, Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }*/

    private void selectImage(final Activity mActivity) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mActivity.startActivityForResult(intent, 0);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bmp = null;
        if (data != null) {

            if (data.getData() == null) {
                bmp = (Bitmap) data.getExtras().get("data");
            } else {
                Uri selectedImage = data.getData();
                try {
                    bmp = Common.scaleImage(this, selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bmp != null) {
                if (resultCode == RESULT_OK) {
                    img_profile.setImageBitmap(bmp);
                    str_profile_hash = Common.getBase64(this, bmp);
                }

            }
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

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
                case R.id.edt_father_name:
                    validateFatherName();
                    break;
                case R.id.edt_surname:
                    validateSurname();
                    break;
                case R.id.edt_address:
                    validateAddress();
                    break;
                case R.id.edt_native:
                    validateNative();
                    break;
            }
        }
    }
}
