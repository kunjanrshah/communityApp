package com.krs.community.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.CountryData;
import com.krs.community.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import spencerstudios.com.bungeelib.Bungee;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
import static com.krs.community.utils.AppConstants.INIT_TIMEOUT;
import static com.krs.community.utils.Utility.hideProgressDialog;
import static com.krs.community.utils.Utility.isValidEmail;
import static com.krs.community.utils.Utility.isValidMobile;
import static com.krs.community.utils.Utility.showProgressDialog;


public class LoginActivity extends Activity {

    private static final int RC_SIGN_IN = 9001;
    private final String TAG = LoginActivity.class.getSimpleName();
    private final boolean[] isLogin = {false};
    private final int is_from_normal = 0;
    private final int is_from_fb = 1;
    private final int is_from_google = 2;
    private final String Mobile = "M";
    private final String Email = "E";
    private ImageView img_back, img_login_fb, img_login_google;
    private TextView txt_forgot_pass, txt_do_you_have, txt_cancel;
    private Button btn_mobile, btn_email, btn_login;
    private EditText edt_username, edt_pass,edt_cpass;
    private boolean isShow = true;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private SignInButton btn_login_google;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private String verificationId;
    private Spinner spinnerCountries;
    private RelativeLayout rl_spinner;
    private String isSelected = Mobile;
    private String mobile_no = "";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            btn_login.setText(getResources().getString(R.string.verify_otp));
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
            Utility.alert(LoginActivity.this, e.getMessage());
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        MemoryAllocation();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);
            mEditor.putString(AppConstants.DEVICE_TOKEN, newToken);
            mEditor.apply();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this,R.color.colorBG,false);
        }
        btn_mobile.setOnClickListener(v -> {
            isSelected = Mobile;
            rl_spinner.setVisibility(View.VISIBLE);
            edt_username.setHint(getString(R.string.enter_mobile_no));
            edt_username.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            edt_username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon, 0, 0, 0);
            edt_username.setText("");

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(10); //Filter to 10 characters
            edt_username.setFilters(filters);

            btn_mobile.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btn_mobile.setTextColor(getResources().getColor(R.color.mdtp_white));

            btn_email.setBackground(getDrawable(R.drawable.border));
            btn_email.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));

            edt_pass.setHint(getString(R.string.password));
            edt_pass.setVisibility(View.VISIBLE);

            txt_cancel.setVisibility(View.GONE);
            btn_login.setText(getString(R.string.login));

            txt_forgot_pass.setVisibility(View.VISIBLE);
            txt_forgot_pass.setText(getResources().getString(R.string._forgot_password));

        });

        btn_email.setOnClickListener(v -> {
            isSelected = Email;
            rl_spinner.setVisibility(View.GONE);
            edt_username.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            edt_username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close_envelope, 0, 0, 0);
            edt_username.setHint(R.string.enter_email_id);
            edt_username.setText("");

            btn_email.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btn_email.setTextColor(getResources().getColor(R.color.mdtp_white));

            btn_mobile.setBackground(getDrawable(R.drawable.border));
            btn_mobile.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));

            edt_pass.setHint(getString(R.string.password));
            edt_pass.setVisibility(View.VISIBLE);
            txt_cancel.setVisibility(View.GONE);
            btn_login.setText(getString(R.string.login));
            txt_forgot_pass.setVisibility(View.VISIBLE);
            txt_forgot_pass.setText(getResources().getString(R.string._forgot_password));

        });

        /*edt_pass.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (btn_login.getText().toString().toLowerCase().contains("Change".toLowerCase())) {
                    if (!edt_username.getText().toString().isEmpty() && !edt_pass.getText().toString().isEmpty()) {
                        if (edt_username.getText().toString().equals(edt_pass.getText().toString().isEmpty())) {
                            call_change_password_ws();
                        }
                    }
                } else {
                    btn_login.performClick();
                }
            }
            return false;
        });*/

        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isSelected.equalsIgnoreCase(Mobile)) {
                    if (edt_username.getText().length() > 10) {
                        String str = edt_username.getText().toString().substring(0, 10);
                        edt_username.setText(str);
                        edt_username.setSelection(str.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_cancel.setOnClickListener(v -> {

            if (isSelected.equalsIgnoreCase(Email)) {
                btn_email.performClick();
            } else {
                btn_mobile.performClick();
            }
            edt_username.setText("");
            edt_pass.setText("");
            edt_cpass.setText("");
            edt_username.setVisibility(View.VISIBLE);
            edt_cpass.setVisibility(View.GONE);
            edt_pass.setHint(getString(R.string.password));
            edt_pass.setVisibility(View.VISIBLE);
            txt_cancel.setVisibility(View.GONE);
            btn_login.setText(getString(R.string.login));
            txt_forgot_pass.setVisibility(View.VISIBLE);
            txt_forgot_pass.setText(getResources().getString(R.string._forgot_password));
        });

        img_login_fb.setOnClickListener(v -> {

            img_login_fb.setEnabled(false);
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "user_birthday", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Utility.hideProgressDialog();
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                        Log.v(TAG, response.toString());
                        // Application code
                        try {
                            String email = object.getString("email");
                            String url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            JSONObject mJsonObject = new JSONObject();
                            mJsonObject.put("email", email);
                            mJsonObject.put("url", url);
                            LoginWS(null, mJsonObject, is_from_fb);
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Error while getting records from Facebook", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,picture.width(200)");
                    request.setParameters(parameters);
                    request.executeAsync();
                    //handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Utility.hideProgressDialog();
                    Log.d(TAG, "facebook:onCancel");
                    img_login_fb.setEnabled(true);
                }

                @Override
                public void onError(FacebookException error) {
                    Utility.hideProgressDialog();
                    Log.d(TAG, "facebook:onError", error);
                    img_login_fb.setEnabled(true);
                }
            });

        });


        btn_login_google.setOnClickListener(v -> signIn());

        img_login_google.setOnClickListener(v -> {
            signIn();
        });

        txt_do_you_have.setOnClickListener(v -> {
            Intent mIntent = new Intent(LoginActivity.this, RegisterActivty.class);
            startActivity(mIntent);
            finish();
            Bungee.fade(this);
        });

        img_back.setOnClickListener(v -> {
            Intent mIntent = new Intent(LoginActivity.this, ChooseLanguageActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();
            Bungee.fade(this);
        });


        btn_login.setOnClickListener(v -> {
            String str = edt_username.getText().toString().trim();

            if (btn_login.getText().toString().contains(getString(R.string.send_otp))) {
                if (str.isEmpty() || str.length() < 10 || !isValidMobile(str)) {
                    edt_username.requestFocus();
                    Utility.alert(this, getString(R.string.err_msg_invalid_mobile));
                    return;
                }
                verifyValidUser(str, false);
            } else if (btn_login.getText().toString().contains(getString(R.string.email))) {
                if (str.isEmpty() || isValidEmail(str)) {
                    edt_username.requestFocus();
                    Utility.alert(this, getString(R.string.err_msg_email));
                    return;
                }
                verifyValidUser(str, true);
            } else if (btn_login.getText().toString().contains(getString(R.string.password))) {
                String str1 = edt_pass.getText().toString().trim();
                String str2 = edt_cpass.getText().toString().trim();
                if (!str1.isEmpty() && !str2.isEmpty()) {
                    if (str1.equals(str2)) {
                        call_change_password_ws(str1,str2);
                    } else {
                        Utility.alert(this, getString(R.string.err_msg_repeat_password));
                    }
                } else {
                    Utility.alert(this, getString(R.string.err_msg_password));
                }
            } else if (btn_login.getText().toString().contains(getResources().getString(R.string.verify_otp))) {
                verifyCode(edt_username.getText().toString().trim());
            } else if (btn_login.getText().toString().contains(getResources().getString(R.string.resend_otp))) {
                sendVerificationCode(mobile_no);
            } else {
                if (edt_pass.isShown()) {
                    LoginWS(null, null, is_from_normal);
                } else {
                    txt_cancel.performClick();
                }
            }
        });

        edt_cpass.setOnTouchListener((v, event) -> {

            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edt_cpass.getRight() - edt_cpass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isShow) {
                        edt_cpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0);
                        edt_cpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isShow = false;
                    } else {
                        edt_cpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0);
                        edt_cpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isShow = true;
                    }
                    edt_cpass.setSelection(edt_cpass.length());

                    return true;
                }
            }
            return false;
        });

        edt_pass.setOnTouchListener((v, event) -> {

            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edt_pass.getRight() - edt_pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isShow) {
                        edt_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0);
                        edt_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isShow = false;
                    } else {
                        edt_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0);
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
            txt_forgot_pass.setVisibility(View.GONE);
            edt_pass.setVisibility(View.GONE);
            txt_cancel.setVisibility(View.VISIBLE);
            String hint = edt_username.getHint().toString();
            if (hint.contains(getString(R.string.email))) {
                btn_login.setText(getString(R.string.send_email));
            } else {
                btn_login.setText(getString(R.string.send_otp));
            }
        });


        spinnerCountries.setAdapter(new ArrayAdapter<String>(LoginActivity.this, R.layout.my_spinner_style, CountryData.countryNames) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(14);
                ((TextView) v).setGravity(Gravity.LEFT);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(16);
                return v;
            }

        });


        if (Build.VERSION.SDK_INT >= 23) {
            if (Utility.canCallPhone(this) || !Utility.canAccessLocation(this) || !Utility.canSMS(this)) {
                requestPermissions(AppConstants.INIT_PERMS, AppConstants.INIT_REQUEST);
            }
        }

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
        edt_cpass = findViewById(R.id.edt_cpass);
        btn_mobile = findViewById(R.id.btn_mobile);
        btn_email = findViewById(R.id.btn_email);
        btn_login = findViewById(R.id.btn_login);
        txt_cancel = findViewById(R.id.txt_cancel);
        spinnerCountries = findViewById(R.id.spinnerCountries);
        rl_spinner = findViewById(R.id.rl_spinner);
        btn_login_google = findViewById(R.id.btn_login_google);
        img_login_fb = findViewById(R.id.img_login_fb);
        img_login_google = findViewById(R.id.img_login_google);
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass);
        txt_do_you_have = findViewById(R.id.txt_do_you_have);

        String sourcestr = getResources().getString(R.string.do_you_have_an_account_register_now);
        sourcestr = sourcestr + "<b>" + " " + getString(R.string.register_now) + "</b>";
        txt_do_you_have.setText(Html.fromHtml(sourcestr));
    }


    @Override
    protected void onStart() {
        super.onStart();
        /*FirebaseUser currentUser = mAuth.getCurrentUser();*/

        boolean is_home = mSharedPreferences.getBoolean(AppConstants.IS_HOME, false);
        if (!is_home) {
            return;
        }
        Intent mIntent = new Intent(LoginActivity.this, DashboardActivity.class);
        if (mSharedPreferences != null) {
            mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
        }
        if (!isLogin[0]) {
            isLogin[0] = true;
            startActivity(mIntent);
            finish();
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
                    LoginWS(user, null, is_from_fb);
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
                        /*FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();
                        //exception.getErrorCode()
                        mAuth.fetchProvidersForEmail("kunjanrshah@gmail.com").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                                if (task.isSuccessful()) {
                                    if (task.getResult().getProviders().contains(EmailAuthProvider.PROVIDER_ID)) {


                                    }
                                }
                            }
                        });*/
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
                    Log.d(TAG, "email: " + user.getEmail() + " phone: " + user.getPhoneNumber() + " photo: " + user.getPhotoUrl());
                    LoginWS(user, null, is_from_google);
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Snackbar.make(findViewById(R.id.ll_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
            }
            hideProgressDialog();
        });
    }


    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
        Toast.makeText(LoginActivity.this, "OTP Send Please wait for a minute", Toast.LENGTH_LONG).show();
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                edt_username.setText("");
                edt_pass.setText("");
                edt_pass.setVisibility(View.VISIBLE);
                edt_cpass.setVisibility(View.VISIBLE);
                edt_username.setVisibility(View.GONE);
                btn_login.setText(getString(R.string.nav_item_change_password));
            } else {
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyValidUser(String username, boolean isEmail) {
        JSONObject json = new JSONObject();
        try {
            json.put(AppConstants.USERNAME, username.trim());
            json.put(AppConstants.IS_SOCIAL, "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utility.showProgressDialog(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.LOGIN_URL, json, response -> {
            hideProgressDialog();
            boolean success;
            try {
                success = response.getBoolean(AppConstants.SUCCESS);
                if (success) {
                    if (isEmail) {
                        ForgotPassword(username);
                    } else {
                        String data = response.getString(AppConstants.DATA);
                        JSONObject mjson_data = new JSONObject(data);
                        String status = mjson_data.getString(AppConstants.STATUS);
                        if (status.equalsIgnoreCase("1")) {
                            AfterValidCheck(response, false);
                            String code = CountryData.countryAreaCodes[spinnerCountries.getSelectedItemPosition()];
                            String number = "+" + code + username;
                            rl_spinner.setVisibility(View.GONE);
                            edt_username.setText("");
                            btn_login.setText(getString(R.string.resend_otp));
                            edt_username.setHint(getString(R.string.type_otp));
                            mobile_no = number;
                            sendVerificationCode(number);
                        } else {
                            Utility.alert(LoginActivity.this, getString(R.string.registraion_request_pending));
                        }
                    }
                } else {
                    Utility.alert(this, getString(R.string.invalid_username));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            hideProgressDialog();
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            String message = null;
            if (error instanceof NetworkError) {
                message = getString(R.string.can_not_connect_to_internet);
            } else if (error instanceof ServerError) {
                message = getString(R.string.server_could_not_found);
            } else if (error instanceof AuthFailureError) {
                message = getString(R.string.can_not_connect_to_internet);
            } else if (error instanceof ParseError) {
                message = getString(R.string.parsing_error);
            } else if (error instanceof TimeoutError) {
                message = getString(R.string.connection_timeout);
            }
            Utility.alert(this, message);
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

    private void call_change_password_ws(String str1,String str2) {

        if (Utility.isOnline(this)) {

            Utility.showProgressDialog(this);
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
                mJsonObject.put(AppConstants.PASSWORD, str1);
                mJsonObject.put(AppConstants.REPEAT_PASSWORD, str2);
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
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            String userid = mSharedPreferences.getString(AppConstants.USER_ID, "");
                            if (userid.isEmpty()) {
                                return;
                            }
                            Intent mIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                            if (mSharedPreferences != null) {
                                mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                            }
                            if (!isLogin[0]) {
                                isLogin[0] = true;
                                startActivity(mIntent);
                                finish();
                            }
                        } else {
                            Utility.alert(LoginActivity.this, message);
                            if (response.has(AppConstants.ERROR_CODE)) {
                                String error = response.getString(AppConstants.ERROR_CODE);
                                /*if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                                }*/
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.hideProgressDialog();
                    }
                }
            }, error -> {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Utility.hideProgressDialog();
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

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "");
        }
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
                        Utility.alert(LoginActivity.this, message);
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

    private void LoginWS(FirebaseUser user, JSONObject data, int is_from) {

        if (Utility.isOnline(this)) {
            String username = "";
            String email_or_mobile = "";
            String password = "";
            JSONObject json = new JSONObject();

            if (is_from == is_from_normal) {
                email_or_mobile = edt_username.getText().toString().trim();
                password = edt_pass.getText().toString();
                if (!email_or_mobile.isEmpty() && !password.isEmpty()) {
                    try {
                        if (isSelected.equalsIgnoreCase(Email)) {
                            if (!isValidEmail(email_or_mobile)) {
                                json.put(AppConstants.USERNAME, email_or_mobile);
                                json.put(AppConstants.PASSWORD, password);
                                fetchLoginData(json);
                            } else {
                                Utility.alert(LoginActivity.this, getResources().getString(R.string.invalid_email));
                                return;
                            }
                        } else {
                            if (isValidMobile(email_or_mobile)) {
                                json.put(AppConstants.USERNAME, email_or_mobile);
                                json.put(AppConstants.PASSWORD, password);
                                fetchLoginData(json);
                            } else {
                                Utility.alert(LoginActivity.this, getResources().getString(R.string.err_msg_invalid_mobile));
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.alert(LoginActivity.this, getString(R.string.err_msg_blank));
                    return;
                }
            } else if (is_from == is_from_fb) {
                String fb_email = "", fb_profile_url = "";
                try {
                    fb_email = data.getString("email");
                    fb_profile_url = data.getString("url");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fb_email.isEmpty()) {
                    return;
                }

                if (!fb_profile_url.isEmpty()) {

                    String finalFb_email = fb_email;
                    String finalFb_profile_url = fb_profile_url;
                    new AlertDialog.Builder(this).setTitle(getString(R.string.app_name)).setMessage(getResources().getString(R.string.update_profile_photo)).setIcon(R.drawable.app_icon).setCancelable(false).setPositiveButton(getString(R.string.yes), (dialog, whichButton) -> {
                        try {
                            json.put(AppConstants.USERNAME, finalFb_email);
                            json.put(AppConstants.IS_SOCIAL, "1");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new GetBase64String(json).execute(finalFb_profile_url);
                    }).setNegativeButton(getString(R.string.no), (dialog, which) -> {
                        try {
                            json.put(AppConstants.USERNAME, finalFb_email);
                            json.put(AppConstants.IS_SOCIAL, "1");
                            fetchLoginData(json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).show();
                } else {
                    try {
                        json.put(AppConstants.USERNAME, fb_email);
                        json.put(AppConstants.IS_SOCIAL, "1");
                        fetchLoginData(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (is_from == is_from_google) {

                if (user != null) {
                    Log.e(TAG, " email: " + user.getEmail() + " phone: " + user.getPhoneNumber() + " Id: " + user.getUid() + " Name: " + user.getDisplayName());
                    username = user.getEmail();
                    if (username != null && !username.isEmpty()) {
                    } else {
                        username = user.getPhoneNumber();
                        if (username != null && !username.isEmpty()) {
                        } else {
                            Utility.alert(LoginActivity.this, getResources().getString(R.string.error_msg_get_data_social_site));
                            return;
                        }
                    }

                    if (!user.getPhotoUrl().toString().isEmpty()) {

                        final String finalUsername = username;
                        new AlertDialog.Builder(this).setTitle(getString(R.string.app_name)).setMessage(getResources().getString(R.string.update_profile_photo)).setIcon(R.drawable.app_icon).setCancelable(false).setPositiveButton(getString(R.string.yes), (dialog, whichButton) -> {
                            try {
                                json.put(AppConstants.USERNAME, finalUsername);
                                json.put(AppConstants.IS_SOCIAL, "1");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new GetBase64String(json).execute(user.getPhotoUrl().toString().replace("s96-c", "s240-c"));
                        }).setNegativeButton(getString(R.string.no), (dialog, which) -> {
                            try {
                                json.put(AppConstants.USERNAME, finalUsername);
                                json.put(AppConstants.IS_SOCIAL, "1");
                                fetchLoginData(json);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }).show();
                    } else {
                        try {
                            json.put(AppConstants.USERNAME, username);
                            json.put(AppConstants.IS_SOCIAL, "1");
                            fetchLoginData(json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        }

    }

    private void fetchLoginData(JSONObject json) {
        Utility.showProgressDialog(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.LOGIN_URL, json, response -> {
            Log.d(TAG, "LoginWS: " + response.toString());

            try {
                hideProgressDialog();
                boolean success = response.getBoolean(AppConstants.SUCCESS);
                String message = response.getString(AppConstants.MESSAGE).toLowerCase();
                if (success) {
                    AfterValidCheck(response, true);
                } else {
                    String str = "";
                    if (message.contains(getString(R.string.incorrect).toLowerCase())) {
                        str = getResources().getString(R.string.invalid_username_password);
                    } else if (message.contains(getString(R.string.invalid_mobile).toLowerCase())) {
                        str = getResources().getString(R.string.err_msg_invalid_mobile);
                    } else if (message.contains(getString(R.string.err_invalid_email).toLowerCase())) {
                        str = getResources().getString(R.string.invalid_email);
                    } else if (message.contains(getString(R.string.err_invalid_email).toLowerCase())) {
                        str = getResources().getString(R.string.invalid_email);
                    } else if (message.contains(getString(R.string.user_not).toLowerCase())) {
                        str = getResources().getString(R.string.user_not_found);
                    } else {
                        str = message;
                    }
                    Utility.alert(LoginActivity.this, str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, error -> {
            hideProgressDialog();
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            String message = null;
            if (error instanceof NetworkError) {
                message = getString(R.string.can_not_connect_to_internet);
            } else if (error instanceof ServerError) {
                message = getString(R.string.server_could_not_found);
            } else if (error instanceof AuthFailureError) {
                message = getString(R.string.can_not_connect_to_internet);
            } else if (error instanceof ParseError) {
                message = getString(R.string.parsing_error);
            } else if (error instanceof TimeoutError) {
                message = getString(R.string.connection_timeout);
            }
            Utility.alert(LoginActivity.this, message);
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

    private void AfterValidCheck(JSONObject response, boolean isLoginSuccess) throws Exception {
        String data = response.getString(AppConstants.DATA);
        JSONObject mjson_data = new JSONObject(data);

        String status = mjson_data.getString(AppConstants.STATUS);
        if (status.equalsIgnoreCase("1")) {

            String user_id = mjson_data.getString(AppConstants.ID);
            String email = mjson_data.getString(AppConstants.EMAIL_ADDRESS);
            String password = mjson_data.getString(AppConstants.PLAIN_PASSWORD);
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

            mEditor.putString(AppConstants.EMAIL, email);
            mEditor.putString(AppConstants.PASSWORD, password);
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
            if (isLoginSuccess) {
                Intent mIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                if (mSharedPreferences != null) {
                    mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                }
                if (!isLogin[0]) {
                    isLogin[0] = true;
                    startActivity(mIntent);
                    finish();
                }
            }
        } else {
            Utility.alert(LoginActivity.this, getString(R.string.registraion_request_pending));
        }
    }

    class GetBase64String extends AsyncTask<String, Void, String> {
        JSONObject mJsonObject = null;

        GetBase64String(JSONObject mJsonObject) {
            this.mJsonObject = mJsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            return Utility.getByteArrayFromImageURL(strings[0]);
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            hideProgressDialog();
            if (mJsonObject != null) {
                try {
                    mJsonObject.put(AppConstants.PROFILE_PIC, str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fetchLoginData(mJsonObject);
            }

        }
    }

}
