package com.krs.vastipatrak.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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
import android.util.Base64;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;
import com.krs.vastipatrak.utils.ConnectivityReceiver;
import com.krs.vastipatrak.utils.LocaleHelper;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.AppConstants.DEFAULT_BACKOFF_MULT;
import static com.krs.vastipatrak.utils.AppConstants.DEFAULT_MAX_RETRIES;
import static com.krs.vastipatrak.utils.AppConstants.INIT_TIMEOUT;
import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;
import static com.krs.vastipatrak.utils.Utility.watchYoutubeVideo;


public class LoginActivity2 extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {


    final boolean[] isLogin = {false};
    /*private final String[] INIT_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS};
    private final int CAMARA_REQUEST = 4;
    private final String[] CALL_CAMARA = {Manifest.permission.CAMERA};*/

    @NonNull
    private final String tag_json_obj = "jobj_req";
    private final String TAG = LoginActivity2.class.getSimpleName();
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
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private Button mfbBtn;
    SignInButton login_google;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;


    private static boolean isValidEmail(@NonNull String email) {
        return TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Memory_Allocation();
        setListner();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity2.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                mEditor.putString(AppConstants.DEVICE_TOKEN, newToken);
                mEditor.apply();
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            if (Utility.canCallPhone(this) || !Utility.canAccessLocation(this) || !Utility.canSMS(this)) {
                int INIT_REQUEST = 1;
                requestPermissions(AppConstants.INIT_PERMS, INIT_REQUEST);
            }
        }

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            screen = mBundle.getString(AppConstants.SCREEN);
            if (screen != null && screen.equalsIgnoreCase(AppConstants.SEARCH_FRAGMENT)) {
                SignupToggle = true;
                togglePage();
                txtSignup.setVisibility(View.GONE);
                btn_signup.setText(getResources().getString(R.string.btn_add_new));
            }
        }

        if (mSharedPreferences != null && !mSharedPreferences.getString(AppConstants.USER_ID, "").equalsIgnoreCase("") && screen == null) {
            Intent mIntent = new Intent(LoginActivity2.this, HomeActivity.class);

            mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
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

        txt_forgot.setOnClickListener(v -> {

            final Dialog forgot_dialog = new Dialog(LoginActivity2.this);
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
        });

        img_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!SignupToggle) {
                    selectImage(LoginActivity2.this);
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
                watchYoutubeVideo(LoginActivity2.this, getResources().getString(R.string.login_1));
            }
        });
        Utility.getDeviceId(this);
        if (mSharedPreferences.getString(AppConstants.LAN, "en").equalsIgnoreCase("de")) {
            context = LocaleHelper.setLocale(LoginActivity2.this, "de");
        } else {
            context = LocaleHelper.setLocale(LoginActivity2.this, "en");
        }
        resources = context.getResources();
        checkConnection();
        Utility.hideKeyboard(this);
        //showActivityOverlay();


        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity2.this, task -> Toast.makeText(LoginActivity2.this, "Logout", Toast.LENGTH_SHORT).show());
            }
        });
        mfbBtn = findViewById(R.id.login_button);
        mfbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mfbBtn.setEnabled(false);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity2.this, Arrays.asList("email", "public_profile"));
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
                        mfbBtn.setEnabled(true);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        mfbBtn.setEnabled(true);
                        // ...
                    }
                });
            }
        });

        login_google.setOnClickListener((View.OnClickListener) v -> signIn());

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.krs.vastipatrak",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }

    }

    private void updateUI() {
        Toast.makeText(LoginActivity2.this, "Logged In", Toast.LENGTH_SHORT).show();
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
                    mfbBtn.setEnabled(true);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        updateUI();
                    }

                } else {
                    mfbBtn.setEnabled(true);
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity2.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI();
                }

                // ...
            }
        });
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        Utility.showProgressDialog(this);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null)
                            {
                                updateUI();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.ll_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

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
        mAuth = FirebaseAuth.getInstance();
        login_google=findViewById(R.id.login_google);
        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
        mEditor.putString(AppConstants.NOTIFICATION, "");
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
        txtLan.setOnClickListener(v -> Toast.makeText(LoginActivity2.this, "Work in Progress", Toast.LENGTH_SHORT).show());

        inputPassword.setOnTouchListener((v, event) -> {

            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (inputPassword.getRight() - inputPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isShow) {
                        inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_show, 0);
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
        });


        inputConformPassword.setOnTouchListener((v, event) -> {

            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (inputConformPassword.getRight() - inputConformPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isShow1) {
                        inputConformPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_show, 0);
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
        });

        img_cancel.setOnClickListener(v -> img_profile.setImageDrawable(getDrawable(R.drawable.user_profile)));
    }

    private void showActivityOverlay() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_activity);

        LinearLayout layout = dialog.findViewById(R.id.llOverlay_activity);
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
            txtHow.setText(getResources().getString(R.string.how_to_register));
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
        if (Utility.isOnline(this)) {

            String forgot_email = inputForgotPassword.getText().toString();
            if (!forgot_email.equalsIgnoreCase("")) {
                d.dismiss();
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
                                    inputPassword.setText("");
                                }
                                Utility.alert(LoginActivity2.this, message);
                            } else {
                                Toast.makeText(LoginActivity2.this, message, Toast.LENGTH_SHORT).show();
                                if (response.has(AppConstants.ERROR_CODE)) {
                                    String error = response.getString(AppConstants.ERROR_CODE);
                                    if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                                        Intent mIntent = new Intent(LoginActivity2.this, LoginActivity2.class);
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
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

            } else {
                Toast.makeText(LoginActivity2.this, getString(R.string.err_msg_email), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity2.this, AppConstants.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void LoginWS() {

        final String email = input_email_mobile.getText().toString();
        final String password = inputPassword.getText().toString();

        if (!email.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
            if (Utility.isOnline(this)) {

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
                                    Intent mIntent = new Intent(LoginActivity2.this, HomeActivity.class);
                                    if (mSharedPreferences != null) {
                                        mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                                    }
                                    if (!isLogin[0]) {
                                        isLogin[0] = true;
                                        startActivity(mIntent);
                                        finish();
                                    }
                                } else {
                                    Utility.alert(LoginActivity2.this, "Registration request is pending. Please contact to Admin !!");
                                }
                            } else {
                                Utility.alert(LoginActivity2.this, message);
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
                        Toast.makeText(LoginActivity2.this, "" + message, Toast.LENGTH_LONG).show();
                    }
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
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            }
        } else {
            Toast.makeText(LoginActivity2.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
        }
    }

    private void SignupWS() {
        if (Utility.isOnline(this)) {
            final String name = inputName.getText().toString();
            final String email = inputEmail.getText().toString();
            final String mobile = inputMobile.getText().toString().trim();
            final String password = inputPassword.getText().toString();
            String cpassword = inputConformPassword.getText().toString();
            final String spouse_name = edt_spouse_name.getText().toString();
            final String address = edt_address.getText().toString();

            if (!email.equalsIgnoreCase("")) {
                if (Utility.isValidEmail(email)) {
                    Toast.makeText(LoginActivity2.this, "Type Valid Email Address!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!email.equalsIgnoreCase("") && !name.equalsIgnoreCase("") && !mobile.equalsIgnoreCase("") && !password.equalsIgnoreCase("") && !cpassword.equalsIgnoreCase("") && !spouse_name.equalsIgnoreCase("") && !address.equalsIgnoreCase("")) {
                if (password.equalsIgnoreCase(cpassword)) {
                    if (mobile.length() == 10) {
                        try {
                            json = new JSONObject();
                            Utility.showProgressDialog(this);
                            json.put(AppConstants.FIRST_NAME, name);
                            json.put(AppConstants.SPOUSE_NAME, spouse_name);
                            json.put(AppConstants.EMAIL_ADDRESS, email);
                            json.put(AppConstants.MOBILE, mobile);
                            json.put(AppConstants.PASSWORD, password);
                            json.put(AppConstants.REPEAT_PASSWORD, cpassword);
                            json.put(AppConstants.ADDRESS, address);

                            if (screen != null && screen.equalsIgnoreCase(AppConstants.SEARCH_FRAGMENT)) {
                                json.put(AppConstants.STATUS, "1");
                            } else {
                                json.put(AppConstants.STATUS, "0");
                            }
                            if (!str_profile_hash.isEmpty()) {
                                json.put(AppConstants.PROFILE_PIC, str_profile_hash);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.SIGNUP_URL, json, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(@NonNull JSONObject response) {
                                Log.d(TAG, "SignupWS: " + response.toString());

                                try {
                                    hideProgressDialog();
                                    boolean success = response.getBoolean(AppConstants.SUCCESS);
                                    String message = response.getString(AppConstants.MESSAGE);
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
                                    Utility.alert(LoginActivity2.this, message);
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
                                Toast.makeText(LoginActivity2.this, "" + message, Toast.LENGTH_LONG).show();

                            }
                        }) {
                            @NonNull
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> params = new HashMap<>();
                                params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                                params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                                params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                                assert mSharedPreferences != null;
                                params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                                return params;
                            }
                        };
                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
                    } else {
                        Toast.makeText(LoginActivity2.this, getString(R.string.err_msg_invalid_mobile), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity2.this, getString(R.string.err_msg_repeat_password), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity2.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity2.this, AppConstants.NO_CONNECTION, Toast.LENGTH_LONG).show();
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
                    if (Utility.hasCAMARA(LoginActivity2.this)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(AppConstants.CALL_CAMARA, AppConstants.CAMARA_REQUEST);
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



        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }else
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        Bitmap bmp = null;
        if (data != null) {
            try {
                if (data.getData() == null) {
                    bmp = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                } else {
                    Uri selectedImage = data.getData();
                    bmp = Utility.scaleImage(this, selectedImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bmp != null) {
                if (resultCode == RESULT_OK) {
                    Glide.with(this).load(bmp).thumbnail(0.5f).apply(RequestOptions.circleCropTransform()).into(img_profile);
                    str_profile_hash = Utility.getBase64(bmp);
                }
            }
        }
    }

    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            if (snackbar != null) {
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
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
