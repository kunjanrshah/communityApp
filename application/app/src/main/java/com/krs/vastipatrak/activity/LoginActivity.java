package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.CountryData;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
import static com.krs.vastipatrak.utils.AppConstants.INIT_TIMEOUT;
import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;
import static com.krs.vastipatrak.utils.Utility.isValidEmail;
import static com.krs.vastipatrak.utils.Utility.isValidMobile;


public class LoginActivity extends Activity {

    private static final int RC_SIGN_IN = 9001;
    private final String TAG = LoginActivity.class.getSimpleName();
    private final boolean[] isLogin = {false};
    private ImageView img_back, img_login_fb, img_login_google;
    private TextView txt_login_now, txt_pls_login, login_with, txt_forgot_pass, txt_do_you_have, txt_cancel, txt_or_login_with;
    private Button btn_mobile, btn_email, btn_login;
    private EditText edt_username, edt_pass;
    private boolean isShow = true;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private SignInButton btn_login_google;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private String verificationId;
    private Spinner spinnerCountries;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                edt_username.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login1);

        MemoryAllocation();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                mEditor.putString(AppConstants.DEVICE_TOKEN, newToken);
                mEditor.apply();
            }
        });

        btn_mobile.setOnClickListener(v -> {
            spinnerCountries.setVisibility(View.VISIBLE);
            edt_username.setHint(getString(R.string.enter_mobile_no));
            btn_mobile.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            btn_mobile.setTextColor(getColor(R.color.mdtp_white));
            btn_email.setBackground(getDrawable(R.drawable.border));
            btn_email.setTextColor(getColor(R.color.mdtp_transparent_black));
            txt_cancel.performClick();
            if (txt_forgot_pass.getText().toString().contains("Email")) {
                txt_forgot_pass.setText("Send OTP");
            }

            /*String sourcestr = getString(R.string._forgot_password);
            sourcestr = sourcestr + "<b>" + " " + getString(R.string.send_otp) + "</b>";
            txt_forgot_pass.setText(Html.fromHtml(sourcestr));*/
        });

        btn_email.setOnClickListener(v -> {
            spinnerCountries.setVisibility(View.GONE);
            edt_username.setHint(R.string.enter_email_id);
            txt_cancel.performClick();
            btn_email.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            btn_email.setTextColor(getColor(R.color.mdtp_white));
            btn_mobile.setBackground(getDrawable(R.drawable.border));
            btn_mobile.setTextColor(getColor(R.color.mdtp_transparent_black));

            if (txt_forgot_pass.getText().toString().contains("OTP")) {
                txt_forgot_pass.setText("Send Email");
            }
            /*String sourcestr = getString(R.string._forgot_password);
            sourcestr = sourcestr + "<b>" + " " + getString(R.string.send_email) + "</b>";
            txt_forgot_pass.setText(Html.fromHtml(sourcestr));*/
        });

        img_login_fb.setOnClickListener(v -> {

            img_login_fb.setEnabled(false);
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);

                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());

                            // Application code
                            try {
                                String email = object.getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,email");
                    request.setParameters(parameters);
                    request.executeAsync();


                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                    // ...
                    img_login_fb.setEnabled(true);
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                    img_login_fb.setEnabled(true);
                    // ...
                }
            });

        });


        btn_login_google.setOnClickListener(v -> signIn());

        img_login_google.setOnClickListener(v -> {

        });

        txt_do_you_have.setOnClickListener(v -> {
            Intent mIntent = new Intent(LoginActivity.this, RegisterActivty.class);
            startActivity(mIntent);
            finish();
        });

        img_back.setOnClickListener(v -> finish());

        btn_login.setOnClickListener(v -> {
            LoginWS(null);
        });

        edt_pass.setOnTouchListener((v, event) -> {

            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edt_pass.getRight() - edt_pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isShow) {
                        edt_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_view, 0);
                        edt_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                        isShow = false;
                    } else {
                        edt_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);
                        edt_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                        isShow = true;
                    }
                    edt_pass.setSelection(edt_pass.length());

                    return true;
                }
            }
            return false;
        });


        txt_forgot_pass.setOnClickListener(v -> {

            String str = edt_username.getText().toString().trim();
            if (txt_forgot_pass.getText().toString().contains("OTP")) {
                if (str.isEmpty() || str.length() < 10) {
                    edt_username.requestFocus();
                    Toast.makeText(this, "Valid number is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyValidUser(str, false);
            } else if (txt_forgot_pass.getText().toString().contains("Email")) {
                verifyValidUser(str, true);
            } else {
                edt_pass.setVisibility(View.GONE);
                txt_cancel.setVisibility(View.VISIBLE);
                if (edt_username.getHint().toString().contains("Email")) {
                    txt_forgot_pass.setText("Send Email");
                } else {
                    txt_forgot_pass.setText("Send OTP");
                }
            }
        });

        txt_cancel.setOnClickListener(v -> {
            edt_pass.setVisibility(View.VISIBLE);
            txt_cancel.setVisibility(View.GONE);
            txt_forgot_pass.setText(getResources().getString(R.string._forgot_password));
        });


        spinnerCountries.setAdapter(new ArrayAdapter<String>(LoginActivity.this, R.layout.my_spinner_style,CountryData.countryNames) {

            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(18);
                ((TextView) v).setGravity(Gravity.CENTER);

                return v;

            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {

                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setTextSize(20);
                //((TextView) v).setGravity(Gravity.CENTER);

                return v;

            }

        });

       /* LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        AppController.getInstance().mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, task -> {
            Toast.makeText(LoginActivity.this, "Logout", Toast.LENGTH_SHORT).show();
        });*/

    }

    private void signIn() {
        Intent signInIntent = AppController.getInstance().mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void MemoryAllocation() {
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = AppController.getInstance().mSharedPreferences;
        mEditor = AppController.getInstance().mEditor;
        img_back = findViewById(R.id.img_back);
        edt_username = findViewById(R.id.edt_username);
        edt_pass = findViewById(R.id.edt_pass);
        btn_mobile = findViewById(R.id.btn_mobile);
        btn_email = findViewById(R.id.btn_email);
        btn_login = findViewById(R.id.btn_login);
        txt_cancel = findViewById(R.id.txt_cancel);
        spinnerCountries = findViewById(R.id.spinnerCountries);
        btn_login_google = findViewById(R.id.btn_login_google);
        img_login_fb = findViewById(R.id.img_login_fb);
        img_login_google = findViewById(R.id.img_login_google);

        txt_login_now = findViewById(R.id.txt_login_now);
        txt_pls_login = findViewById(R.id.txt_pls_login);
        login_with = findViewById(R.id.login_with);
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass);



        String sourcestr = getString(R.string._forgot_password);
        //sourcestr = sourcestr + "<b>" + " " + getString(R.string.send_otp) + "</b>";
        // txt_forgot_pass.setText(Html.fromHtml(sourcestr));

        txt_do_you_have = findViewById(R.id.txt_do_you_have);
        txt_or_login_with = findViewById(R.id.txt_or_login_with);

        sourcestr = getResources().getString(R.string.do_you_have_an_account_register_now);
        sourcestr = sourcestr + "<b>" + " " + getString(R.string.register_now) + "</b>";
        txt_do_you_have.setText(Html.fromHtml(sourcestr));
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success");
                img_login_fb.setEnabled(true);
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    LoginWS(user);
                }

            } else {
                img_login_fb.setEnabled(true);
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();


                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(getApplicationContext(), "Firebase Facebook login failed", Toast.LENGTH_SHORT).show();

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();

                        //exception.getErrorCode()

                        mAuth.fetchProvidersForEmail("kunjanrshah@gmail.com").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                                if (task.isSuccessful()) {
                                    if (task.getResult().getProviders().contains(EmailAuthProvider.PROVIDER_ID)) {


                                    }
                                }
                            }
                        });


                        Toast.makeText(getApplicationContext(), "User with Email id already exists", Toast.LENGTH_SHORT).show();
                    }
                    LoginManager.getInstance().logOut();
                }

            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        Utility.showProgressDialog(this);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "email: " + user.getEmail() + " phone: " + user.getPhoneNumber());
                    LoginWS(user);
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Snackbar.make(findViewById(R.id.ll_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
            }
            hideProgressDialog();
        });
    }

    private void updateUI() {

        String userId = mSharedPreferences.getString(AppConstants.USER_ID, "");

        if (userId.isEmpty()) {
            // Toast.makeText(LoginActivity.this, "User not found Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent mIntent = new Intent(LoginActivity.this, HomeActivity.class);
        if (mSharedPreferences != null) {
            mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
        }
        if (!isLogin[0]) {
            isLogin[0] = true;
            startActivity(mIntent);
            finish();
        }
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);

    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "OTP Success", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyValidUser(String username, boolean isEmail) {
        JSONObject json = new JSONObject();
        try {
            json.put(AppConstants.USERNAME, username);
            json.put(AppConstants.PASSWORD, "kunj");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.LOGIN_URL, json, response -> {
            hideProgressDialog();
            boolean success = false;
            try {
                success = response.getBoolean(AppConstants.SUCCESS);
                if (isEmail && success) {
                    ForgotPassword(username);
                } else if (success) {
                    String code = CountryData.countryAreaCodes[spinnerCountries.getSelectedItemPosition()];
                    String number = "+" + code + username;
                    sendVerificationCode(number);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
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
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences != null ? mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, "") : null);
                if (mSharedPreferences != null) {
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                }
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, "");
    }

    private void ForgotPassword(String forgot_email) {
        JSONObject json = new JSONObject();
        try {
            json.put(AppConstants.EMAIL_ADDRESS, forgot_email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = AppConstants.FORGOT_PASSWORD_URL;
        Utility.showProgressDialog(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(@NonNull JSONObject response) {
                Log.d(TAG, "ForgotPasswordWS: " + response.toString());

                try {
                    hideProgressDialog();
                    boolean success = response.getBoolean(AppConstants.SUCCESS);
                    String message = response.getString(AppConstants.MESSAGE);

                    if (success) {
                        if (response.has(AppConstants.PASSWORD) && response.has(AppConstants.MOBILE)) {
                            //    inputPassword.setText("");
                        }
                        Utility.alert(LoginActivity.this, message);
                    } else {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (response.has(AppConstants.ERROR_CODE)) {
                            String error = response.getString(AppConstants.ERROR_CODE);
                            if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
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
        }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hideProgressDialog();
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, AppConstants.DEFAULT_MAX_RETRIES, AppConstants.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, "");
    }

    private void LoginWS(FirebaseUser user) {
        String username = "";
        String email_or_mobile = "";
        String password = "";
        JSONObject json = new JSONObject();
        if (user != null) {
            Log.e(TAG, " email: " + user.getEmail() + " phone: " + user.getPhoneNumber() + " Id: " + user.getUid() + " Name: " + user.getDisplayName());
            username = user.getEmail();
            if (username != null && !username.isEmpty()) {
            } else {
                username = user.getPhoneNumber();
                if (username != null && !username.isEmpty()) {
                } else {
                    Toast.makeText(LoginActivity.this, "Not able to get your details from Social account", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            try {
                json.put(AppConstants.USERNAME, username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            email_or_mobile = edt_username.getText().toString().trim();
            password = edt_pass.getText().toString();
            if (!email_or_mobile.isEmpty() && !password.isEmpty()) {
                try {
                    if (edt_username.getHint().toString().contains("Email")) {
                        if (!isValidEmail(email_or_mobile)) {
                            json.put(AppConstants.USERNAME, email_or_mobile);
                            json.put(AppConstants.PASSWORD, password);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (edt_username.getHint().toString().contains("Mobile")) {
                        if (isValidMobile(email_or_mobile)) {
                            json.put(AppConstants.USERNAME, email_or_mobile);
                            json.put(AppConstants.PASSWORD, password);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Mobile", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (Utility.isOnline(this)) {

            Utility.showProgressDialog(this);
            String finalEmail = email_or_mobile;
            String finalPassword = password;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.LOGIN_URL, json, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "LoginWS: " + response.toString());

                    try {
                        hideProgressDialog();
                        boolean success = response.getBoolean(AppConstants.SUCCESS);
                        String message = response.getString(AppConstants.MESSAGE);

                        if (success) {
                            String data = response.getString(AppConstants.DATA);
                            JSONObject mjson_data = new JSONObject(data);

                            String status = mjson_data.getString(AppConstants.STATUS);
                            if (status.equalsIgnoreCase("1")) {
                                Utility.SaveProfile(mjson_data);

                                String user_id = mjson_data.getString(AppConstants.ID);
                                String profile_url = mjson_data.getString(AppConstants.PROFILE_PIC_URL);
                                String first_name = mjson_data.getString(AppConstants.FIRST_NAME);
                                String last_name = mjson_data.getString(AppConstants.LAST_NAME);

                                String office_lat = mjson_data.getString(AppConstants.OFFICE_LAT);
                                String office_lng = mjson_data.getString(AppConstants.OFFICE_LNG);
                                String home_lat = mjson_data.getString(AppConstants.HOME_LAT);
                                String home_lng = mjson_data.getString(AppConstants.HOME_LNG);

                                String access_token = mjson_data.getString(AppConstants.ACCESS_TOKEN);
                                String updated_time = mjson_data.getString(AppConstants.UPDATED_TIME);
                                String role = mjson_data.getString(AppConstants.ROLE);
                                String is_location_enable = mjson_data.getString(AppConstants.IS_LOCATION_ENABLE);

                                mEditor.putString(AppConstants.EMAIL, finalEmail);
                                mEditor.putString(AppConstants.PASSWORD, finalPassword);
                                mEditor.putString(AppConstants.USER_ID, user_id);
                                mEditor.putString(AppConstants.PROFILE_PIC_URL, profile_url);
                                mEditor.putString(AppConstants.FIRST_NAME, first_name);
                                mEditor.putString(AppConstants.LAST_NAME, last_name);
                                mEditor.putString(AppConstants.ACCESS_TOKEN, access_token);
                                mEditor.putString(AppConstants.UPDATED_TIME, updated_time);
                                mEditor.putString(AppConstants.ROLE, role);
                                mEditor.putString(AppConstants.TBTN_SHARE, is_location_enable);
                                mEditor.putString(AppConstants.OFFICE_LAT, office_lat);
                                mEditor.putString(AppConstants.OFFICE_LNG, office_lng);
                                mEditor.putString(AppConstants.HOME_LAT, home_lat);
                                mEditor.putString(AppConstants.HOME_LNG, home_lng);
                                mEditor.apply();

                                Bundle fb_bundle = new Bundle();
                                fb_bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, Integer.parseInt(user_id));
                                fb_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, first_name + " " + last_name);
                                  /*  AppController.getInstance().firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fb_bundle);
                                    if (user_id.equalsIgnoreCase(AppConstants.ADMIN_1) || user_id.equalsIgnoreCase(AppConstants.ADMIN_2)) {
                                        AppController.isAdmin = true;
                                    }*/
                                Intent mIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                if (mSharedPreferences != null) {
                                    mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                                }
                                if (!isLogin[0]) {
                                    isLogin[0] = true;
                                    startActivity(mIntent);
                                    finish();
                                }
                            } else {
                                Utility.alert(LoginActivity.this, "Registration request is pending. Please contact to Admin !!");
                            }
                        } else {
                            Utility.alert(LoginActivity.this, message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, error -> {
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
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences != null ? mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, "") : null);
                    if (mSharedPreferences != null) {
                        params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                    }
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "");
        }

    }


}
