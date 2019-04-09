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
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.krs.vastipatrak.utils.AppConstants.DEFAULT_BACKOFF_MULT;
import static com.krs.vastipatrak.utils.AppConstants.DEFAULT_MAX_RETRIES;
import static com.krs.vastipatrak.utils.AppConstants.INIT_TIMEOUT;
import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;


public class LoginActivity1 extends Activity {

    private static final int RC_SIGN_IN = 9001;
    private final String TAG = LoginActivity1.class.getSimpleName();
    private final boolean[] isLogin = {false};
    private ImageView img_back, img_login_fb, img_login_google;
    private TextView txt_login_now, txt_pls_login, login_with, txt_forgot_pass, txt_do_you_have, txt_or_login_with;
    private Button btn_mobile, btn_email, btn_login;
    private EditText edt_username, edt_pass;
    private boolean isShow = true;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private SignInButton btn_login_google;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login1);

        MemoryAllocation();

        btn_mobile.setOnClickListener(v -> {
            edt_username.setHint(getString(R.string.enter_mobile_no));
            btn_mobile.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            btn_mobile.setTextColor(getColor(R.color.mdtp_white));
            btn_email.setBackground(getDrawable(R.drawable.border));
            btn_email.setTextColor(getColor(R.color.mdtp_transparent_black));

            String sourcestr = getString(R.string._forgot_password);
            sourcestr = sourcestr + "<b>" + " " + getString(R.string.send_otp) + "</b>";
            txt_forgot_pass.setText(Html.fromHtml(sourcestr));
        });

        btn_email.setOnClickListener(v -> {
            edt_username.setHint(R.string.enter_email_id);
            btn_email.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            btn_email.setTextColor(getColor(R.color.mdtp_white));
            btn_mobile.setBackground(getDrawable(R.drawable.border));
            btn_mobile.setTextColor(getColor(R.color.mdtp_transparent_black));

            String sourcestr = getString(R.string._forgot_password);
            sourcestr = sourcestr + "<b>" + " " + getString(R.string.send_email) + "</b>";
            txt_forgot_pass.setText(Html.fromHtml(sourcestr));
        });

        img_login_fb.setOnClickListener(v -> {

            img_login_fb.setEnabled(false);
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity1.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_login_google.setOnClickListener(v -> signIn());

        img_login_google.setOnClickListener(v -> {

        });

        txt_do_you_have.setOnClickListener(v -> {
            Intent mIntent = new Intent(LoginActivity1.this, RegisterActivty.class);
            startActivity(mIntent);
            finish();
        });

        img_back.setOnClickListener(v -> finish());

        btn_login.setOnClickListener(v -> {
            LoginWS();
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

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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

        btn_login_google = findViewById(R.id.btn_login_google);
        img_login_fb = findViewById(R.id.img_login_fb);

        img_login_google = findViewById(R.id.img_login_google);


        txt_login_now = findViewById(R.id.txt_login_now);
        txt_pls_login = findViewById(R.id.txt_pls_login);
        login_with = findViewById(R.id.login_with);
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass);

        String sourcestr = getString(R.string._forgot_password);
        sourcestr = sourcestr + "<b>" + " " + getString(R.string.send_otp) + "</b>";
        txt_forgot_pass.setText(Html.fromHtml(sourcestr));

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
        }else
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    img_login_fb.setEnabled(true);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        updateUI();
                    }

                } else {
                    img_login_fb.setEnabled(true);
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity1.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI();
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
                    updateUI();
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Snackbar.make(findViewById(R.id.ll_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
            }
            hideProgressDialog();
        });
    }

    private void updateUI() {
        Toast.makeText(LoginActivity1.this, "Logged In", Toast.LENGTH_SHORT).show();
    }

    private void LoginWS() {

        final String email = edt_username.getText().toString();
        final String password = edt_pass.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (Utility.isOnline(this)) {
                JSONObject json = null;
                try {
                    json = new JSONObject();
                    json.put(AppConstants.USERNAME, email);
                    json.put(AppConstants.PASSWORD, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Utility.showProgressDialog(this);
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

                                    /*Bundle fb_bundle = new Bundle();
                                    fb_bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, Integer.parseInt(user_id));
                                    fb_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, first_name + " " + last_name);
                                    AppController.getInstance().firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fb_bundle);
*/

                                   /* if (user_id.equalsIgnoreCase(AppConstants.ADMIN_1) || user_id.equalsIgnoreCase(AppConstants.ADMIN_2)) {
                                        AppController.isAdmin = true;
                                    }*/
                                    Intent mIntent = new Intent(LoginActivity1.this, HomeActivity.class);
                                    if (mSharedPreferences != null) {
                                        mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                                    }
                                    if (!isLogin[0]) {
                                        isLogin[0] = true;
                                        startActivity(mIntent);
                                        finish();
                                    }
                                } else {
                                    Utility.alert(LoginActivity1.this, "Registration request is pending. Please contact to Admin !!");
                                }
                            } else {
                                Utility.alert(LoginActivity1.this, message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, error -> {
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
                    Toast.makeText(LoginActivity1.this, "" + message, Toast.LENGTH_LONG).show();
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
        } else {
            Toast.makeText(LoginActivity1.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
        }
    }


}
