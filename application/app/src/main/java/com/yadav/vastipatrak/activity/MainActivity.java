package com.yadav.vastipatrak.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.yadav.vastipatrak.R;
import com.yadav.vastipatrak.app.AppController;
import com.yadav.vastipatrak.app.Config;
import com.yadav.vastipatrak.fragments.CalendarFragment;
import com.yadav.vastipatrak.fragments.ChangePasswordFragment;
import com.yadav.vastipatrak.fragments.FragmentDrawer;
import com.yadav.vastipatrak.fragments.HelpFragment;
import com.yadav.vastipatrak.fragments.HomeFragment;
import com.yadav.vastipatrak.fragments.MatrimonyFragment;
import com.yadav.vastipatrak.fragments.NearByFragment;
import com.yadav.vastipatrak.fragments.SearchFragment;
import com.yadav.vastipatrak.fragments.SharedUsersFragment;
import com.yadav.vastipatrak.interfaces.IAdminControl;
import com.yadav.vastipatrak.service.MyLocationService;
import com.yadav.vastipatrak.utils.Common;
import com.yadav.vastipatrak.utils.ConnectivityReceiver;
import com.yadav.vastipatrak.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, SearchFragment.ISearchCallback, ConnectivityReceiver.ConnectivityReceiverListener {
    public static final String[] CALL_CAMARA = {Manifest.permission.CAMERA};
    public static final int CAMARA_REQUEST = 4;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static int MOVE_TO_SEARCH = 0;
    private final int REQUEST_CHECK_SETTINGS = 199;
    private final int IMAGEREQUESTCODE = 1;
    private final String[] INIT_PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};
    private final String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final String[] CALL_PERMS = {Manifest.permission.CALL_PHONE};
    private final int INIT_REQUEST = 1;
    private final int CALL_REQUEST = 2;
    private final int LOCATION_REQUEST = 3;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                if (!Common.CheckGpsStatus(MainActivity.this)) {
                    Intent mIntent = new Intent(MainActivity.this, MyLocationService.class);
                    startService(mIntent);
                } else {
                    Intent mIntent = new Intent(MainActivity.this, MyLocationService.class);
                    stopService(mIntent);
                }
            }
        }
    };
    MenuItem deactiveItem;
    MenuItem deleteItem;
    MenuItem activeItem;
    private int MOVE_TO_POSITION = 0;
    private String query = "";
    private String push_message = null;
    private String query_string = "";
    private boolean doubleBackToExitPressedOnce = false;
    private GoogleApiClient googleApiClient;
    @Nullable
    private Fragment fragment = null;
    private Fragment searchFragment = null;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferencesWelcome;
    private SharedPreferences.Editor mEditorWelcome;
    private SearchView searchView;
    private IntentIntegrator qrScan;
    private IAdminControl IAdminControl;
    private MenuItem export;
    private MenuItem change_role;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snackbar = Snackbar.make(findViewById(R.id.drawer_layout), R.string.not_connected, Snackbar.LENGTH_INDEFINITE);
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();

        mPreferencesWelcome = getSharedPreferences(Common.Constant_Class.PREF_WELCOME, MODE_PRIVATE);
        mEditorWelcome = mPreferencesWelcome.edit();
        mEditorWelcome.apply();

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        //intializing scan object
        qrScan = new IntentIntegrator(this);
        FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        searchFragment = new SearchFragment();

        if (Build.VERSION.SDK_INT >= 23) {
            if (Common.canCallPhone(this) && !Common.canAccessLocation(this)) {
                requestPermissions(INIT_PERMS, INIT_REQUEST);
            } else if (!Common.canAccessLocation(this)) {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            } else if (Common.canCallPhone(this)) {
                requestPermissions(CALL_PERMS, CALL_REQUEST);
            } else if (Common.canCallPhone(this)) {
                requestPermissions(CALL_CAMARA, CAMARA_REQUEST);
            }
        }

        if (Common.CheckGpsStatus(this)) {
            displayLocationSettingsRequest(MainActivity.this);
        } else {
            Intent mIntent = new Intent(MainActivity.this, MyLocationService.class);
            startService(mIntent);
        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, @NonNull Intent intent) {

                // checking for type intent filter
                if (Objects.requireNonNull(intent.getAction()).equals(Config.REGISTRATION_COMPLETE)) {
                    String token = intent.getStringExtra("token");
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra(Common.Constant_Class.PUSH_MESSAGE);
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Push notification: " + message);
                }
            }
        };
        Common.getDeviceId(this);
        logUser();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                mEditor.putString(Common.Constant_Class.DEVICE_TOKEN, newToken);
                mEditor.apply();
            }
        });
        if (mSharedPreferences.getString(Common.Constant_Class.USER_ID, "").equalsIgnoreCase("")) {
            mEditor.putString(Common.Constant_Class.NOTIFICATION, "");
            mEditor.apply();
            Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();
        } else {
            //getGotraWS();
            String push = mSharedPreferences.getString(Common.Constant_Class.NOTIFICATION, "");
            if (push.toLowerCase().contains("approve") && !push.toLowerCase().contains("admin")) {
                mEditor.putString(Common.Constant_Class.NOTIFICATION, "");
                mEditor.apply();
                MOVE_TO_SEARCH = 1;
                moveToSearch(MOVE_TO_SEARCH);
            } else if (push.toLowerCase().contains("location")) {
                ProfileActivity.isEnable = false;
                mEditor.putString(Common.Constant_Class.NOTIFICATION, "");
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.apply();
                Intent mIntent = new Intent(this, ProfileActivity.class);
                startActivity(mIntent);
            } else {
                Bundle mBundle = getIntent().getExtras();
                if (mBundle != null) {
                    query = mBundle.getString(Common.Constant_Class.QUERY);
                    query_string = mBundle.getString(Common.Constant_Class.QUERY_STRING);
                }
                if (query == null && query_string == null && push_message == null) {
                    displayView(0);
                } else if (query_string != null && query != null && query.isEmpty() && query_string.isEmpty()) {
                    displayView(0);
                } else {
                    displayView(-1);
                }
            }
        }
        checkConnection();

        // showActivityOverlay();
        if (mPreferencesWelcome.getBoolean("first_time_main", true)) {
            mEditorWelcome.putBoolean("first_time_main", false);
            mEditorWelcome.apply();
        }

        Log.d(TAG, "MainActivity Screen");
        if (mSharedPreferences.getBoolean("app_create", false)) {
            mEditor.putBoolean("app_create", false);
            mEditor.apply();
            get_updated_ver_ws();
            getList(getResources().getString(R.string._gotra));
            getList(getResources().getString(R.string._native));
            getList(getResources().getString(R.string._education));
        }
    }

    private void get_updated_ver_ws() {

        if (Common.isOnline(this)) {

            Common.showProgressDialog(this);
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                String user_id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
                String token = mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, "");
                mJsonObject.put(Common.Constant_Class.USER_ID, user_id);
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, token);
                mJsonObject.put(Common.Constant_Class.INSERT, "0");
                mJsonObject.put(Common.Constant_Class.VERSION, Common.getAppVersion(this));
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SET_UPDATED_VERSION_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    Common.hideProgressDialog();

                    try {
                        String data = response.getString(Common.Constant_Class.DATA);
                        if (data.equals("0")) {
                            displayAlert();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Common.hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    Common.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }



    private void getList(final String type) {
        if (Common.isOnline(this)) {

            Common.showProgressDialog(this);
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                String user_id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
                String token = mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, "");
                mJsonObject.put(Common.Constant_Class.USER_ID, user_id);
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, token);
                mJsonObject.put(Common.Constant_Class.RESPONSE_DATA, type);

            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.GET_MASTER_DATA_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    Common.hideProgressDialog();

                    try {
                        JSONArray mArray = response.getJSONArray(Common.Constant_Class.DATA);
                        if (type.equalsIgnoreCase(getResources().getString(R.string._gotra))) {
                            for (int i = 0; i < mArray.length(); i++) {
                                JSONObject mObject = mArray.getJSONObject(i);
                                AppController.getInstance().lstGotra.add(mObject.getString("gotra"));
                            }
                            Collections.sort(AppController.getInstance().lstGotra);
                            AppController.getInstance().lstGotra.add(0, Common.Constant_Class.TITLE_GOTRA);
                        } else if (type.equalsIgnoreCase(getString(R.string._native))) {
                            for (int i = 0; i < mArray.length(); i++) {
                                JSONObject mObject = mArray.getJSONObject(i);
                                AppController.getInstance().lstNative.add(mObject.getString(getString(R.string._native)));
                            }
                            Collections.sort(AppController.getInstance().lstNative);
                            AppController.getInstance().lstNative.add(Common.Constant_Class.TITLE_NATIVE);
                        } else if (type.equalsIgnoreCase(getString(R.string._education))) {
                            for (int i = 0; i < mArray.length(); i++) {
                                JSONObject mObject = mArray.getJSONObject(i);
                                AppController.getInstance().lstEducation.add(mObject.getString(getString(R.string._education)));
                            }
                            Collections.sort(AppController.getInstance().lstEducation);
                            AppController.getInstance().lstEducation.add(Common.Constant_Class.TITLE_EDUCATION);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Common.hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Common.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    private void displayAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage("Please update your app");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.app_logo);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void showActivityOverlay() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_activity);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        LinearLayout layout = dialog.findViewById(R.id.llOverlay_activity);
        CheckBox chkOk = dialog.findViewById(R.id.chkOk);
        chkOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //layout.setAlpha(0.8f);
        layout.setBackgroundColor(Color.TRANSPARENT);
        /*layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });*/
        dialog.show();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void logUser() {
        Crashlytics.setUserIdentifier(mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
        Crashlytics.setUserEmail(mSharedPreferences.getString(Common.Constant_Class.EMAIL, ""));
        String fname = mSharedPreferences.getString(Common.Constant_Class.FIRST_NAME, "");
        String lname = mSharedPreferences.getString(Common.Constant_Class.LAST_NAME, "");
        Crashlytics.setUserName(fname + " " + lname);
    }

/*
    private void getGotraWS() {
        if (Common.isOnline(this)) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Common.Constant_Class.GET_GOTRA_URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray("data");
                            AppController.getInstance().lstGotra = new ArrayList<>();

                            for (int i = 0; i < mJsonArray.length(); i++) {
                                AppController.getInstance().lstGotra.add(mJsonArray.getString(i));
                            }
                            Collections.sort(AppController.getInstance().lstGotra);
                            AppController.getInstance().lstGotra.add(0, Common.Constant_Class.TITLE_GOTRA);
                            AppController.getInstance().dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, AppController.getInstance().lstGotra);
                            AppController.getInstance().dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        } else {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                    Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
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
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }
*/

    private void displayLocationSettingsRequest(@NonNull Context context) {
        if (this.googleApiClient == null) {
            this.googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
            this.googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);


            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            builder.setNeedBle(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            MainActivity.this.googleApiClient = null;

                            Log.i("Vastipatrak", "All location settings are satisfied.");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i("Vastipatrak", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                            MainActivity.this.googleApiClient = null;
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the result
                                // in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                Log.i("Vastipatrak", "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            MainActivity.this.googleApiClient = null;
                            Log.i("Vastipatrak", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                            break;
                    }
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        if (push_message != null && push_message.contains("approve")) {
            MOVE_TO_SEARCH = 1;
            moveToSearch(MOVE_TO_SEARCH);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
            this.registerReceiver(this.mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "You need to give permission to access location ! ", Toast.LENGTH_SHORT).show();
                } else if (Common.canAccessLocation(this)) {
                    Intent mIntent = new Intent(MainActivity.this, MyLocationService.class);
                    startService(mIntent);
                }

                break;
            case CALL_REQUEST:

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(this, "You need to give permission to access phone ! ", Toast.LENGTH_SHORT).show();
                }

                break;
            case INIT_REQUEST:
                if (Common.canCallPhone(this) && !Common.canAccessLocation(this)) {
                    Toast.makeText(this, "You need to give permission to access phone and location ! ", Toast.LENGTH_SHORT).show();
                } else if (Common.canAccessLocation(this)) {
                    Intent mIntent = new Intent(MainActivity.this, MyLocationService.class);
                    startService(mIntent);
                }
                break;
            case CAMARA_REQUEST:
                Toast.makeText(this, "Give permission to access CAMARA ! ", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem event = menu.findItem(R.id.action_event);
        event.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent mIntent = new Intent(MainActivity.this, ShareEventActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }
        });

        MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent mIntent = new Intent(MainActivity.this, AdvanceSearchActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }
        });


        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Bundle mBundle = new Bundle();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                searchFragment = new SearchFragment();
                IAdminControl = (IAdminControl) searchFragment;
                ((SearchFragment) searchFragment).setmContext(MainActivity.this);
                mBundle.putString(Common.Constant_Class.QUERY, query);
                searchFragment.setArguments(mBundle);
                fragmentTransaction.replace(R.id.container_body, searchFragment).commit();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        export = menu.findItem(R.id.action_export);

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Common.promptSpeechInput(MainActivity.this);
                return false;
            }
        });

        MenuItem action_scan = menu.findItem(R.id.action_scan);
        action_scan.setVisible(true);
        action_scan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                qrScan.initiateScan();
                return false;
            }
        });

        MenuItem action_scan_image = menu.findItem(R.id.action_scan_image);
        action_scan_image.setVisible(true);
        action_scan_image.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), IMAGEREQUESTCODE);

                return false;
            }
        });


        MenuItem activeAdd = menu.findItem(R.id.action_add);
        MenuItem nonActives = menu.findItem(R.id.action_nonActives);
        MenuItem block_users = menu.findItem(R.id.action_block_users);
        change_role = menu.findItem(R.id.action_change_role);
        deactiveItem = menu.findItem(R.id.action_deactive);
        deleteItem = menu.findItem(R.id.action_delete);
        activeItem = menu.findItem(R.id.action_activate);
        MenuItem menu_admins = menu.findItem(R.id.action_admins);

        nonActives.setVisible(false);
        activeAdd.setVisible(false);
        activeItem.setVisible(false);
        deactiveItem.setVisible(false);
        export.setVisible(false);
        change_role.setVisible(false);
        deleteItem.setVisible(false);

        if (MOVE_TO_POSITION == 4) {
            export.setVisible(true);
        } else {
            export.setVisible(false);
        }

        if (mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
            if (Common.isOnline(this)) {
                nonActives.setVisible(true);
                event.setVisible(true);

                activeAdd.setVisible(true);
                if (MOVE_TO_POSITION == 5) {
                    change_role.setVisible(true);
                    deleteItem.setVisible(true);
                } else {
                    change_role.setVisible(false);
                    deleteItem.setVisible(false);
                }

                if (MOVE_TO_SEARCH == 1) {
                    activeItem.setVisible(true);
                    deactiveItem.setVisible(true);
                    deleteItem.setVisible(true);
                }
            } else {
                Toast.makeText(this, "" + Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        }

        block_users.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        nonActives.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MOVE_TO_SEARCH = 1;
                activeItem.setVisible(true);
                deactiveItem.setVisible(true);
                deleteItem.setVisible(true);
                moveToSearch(MOVE_TO_SEARCH);
                return false;
            }
        });

        activeAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
                mIntent.putExtra(Common.Constant_Class.SCREEN, Common.Constant_Class.SEARCH_FRAGMENT);
                startActivity(mIntent);
                return false;
            }
        });

        activeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MOVE_TO_SEARCH = 2;
                moveToSearch(MOVE_TO_SEARCH);
                return false;
            }
        });

        deactiveItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MOVE_TO_SEARCH = 3;
                moveToSearch(MOVE_TO_SEARCH);
                return false;
            }
        });

        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MOVE_TO_SEARCH = 4;
                moveToSearch(MOVE_TO_SEARCH);
                return false;
            }
        });

        change_role.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MOVE_TO_SEARCH = 5;
                moveToSearch(MOVE_TO_SEARCH);
                return false;
            }
        });

        menu_admins.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MOVE_TO_SEARCH = 6;
                moveToSearch(MOVE_TO_SEARCH);
                return false;
            }
        });
        return true;
    }


    public void moveToSearch(int menu) {


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = searchFragment;
        IAdminControl = (IAdminControl) fragment;
        ((SearchFragment) searchFragment).setmContext(MainActivity.this);
        Handler mhandler = new Handler();
        if (menu == 1) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(Common.Constant_Class.AdminControl, Common.Constant_Class.NonActive);
            fragment.setArguments(mBundle);
        } else if (menu == 6) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(Common.Constant_Class.AdminControl, -1);
            fragment.setArguments(mBundle);
        }
        fragmentTransaction.replace(R.id.container_body, fragment).commit();

        switch (menu) {
            case 1:
                try {
                    if (fragment != null) {
                        mhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((SearchFragment) fragment).callNonActivesWS();
                            }
                        }, 500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IAdminControl.CallActivate();
                        }
                    }, 500);

                } catch (Exception e) {
                    Toast.makeText(this, "Select Non-Actives First", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IAdminControl.CallDeActivate();
                        }
                    }, 500);


                } catch (Exception e) {
                    Toast.makeText(this, "Select Non-Actives First", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 4:
                try {

                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IAdminControl.CallDelete();
                        }
                    }, 500);

                } catch (Exception e) {
                    Toast.makeText(this, "Select Non-Actives First", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 5:
                try {

                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IAdminControl.ChangeRole();
                        }
                    }, 500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 6:
                try {

                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IAdminControl.SearchAdmins();
                        }
                    }, 500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && menu != null) {
            Log.d(TAG, "step onMenuOpened1");
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        Log.d(TAG, "step onPanelClosed");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_MENU:
                Log.d(TAG, "step onKeyDown");
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        MOVE_TO_POSITION = position;
        displayView(position);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle mBundle = intent.getExtras();
        if (mBundle != null) {
            push_message = mBundle.getString(Common.Constant_Class.PUSH_MESSAGE);
            String user_id = mBundle.getString(Common.Constant_Class.USER_ID);
        }
    }

    private void displayView(int position) {

        switch (position) {
            case -1:
                fragment = searchFragment;
                ((SearchFragment) searchFragment).setmContext(MainActivity.this);
                Bundle mBundle = new Bundle();
                if (query != null) {
                    mBundle.putString(Common.Constant_Class.QUERY, query);
                    fragment.setArguments(mBundle);
                } else if (query_string != null) {
                    mBundle.putString(Common.Constant_Class.QUERY_STRING, query_string);
                    fragment.setArguments(mBundle);
                } else if (push_message != null) {
                    IAdminControl = (IAdminControl) fragment;
                    mBundle.putString(Common.Constant_Class.PUSH_MESSAGE, push_message);
                    mBundle.putInt(Common.Constant_Class.AdminControl, Common.Constant_Class.NonActive);
                    fragment.setArguments(mBundle);
                    push_message = null;
                    try {
                        ((SearchFragment) fragment).callNonActivesWS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                mEditor.putBoolean("myprofile", true);
                mEditor.apply();
                Intent mIntent1 = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(mIntent1);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case 2:
                fragment = new NearByFragment();
                break;
            case 3:
                fragment = new SharedUsersFragment();
                break;
            case 4:
                fragment = new MatrimonyFragment();
                break;
            case 5:
                fragment = new CalendarFragment();
                break;
            case 6:
                Toast.makeText(MainActivity.this, "Language Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                fragment = new ChangePasswordFragment();
                break;
            case 8:
                fragment = new TourFragment();
                break;
            case 9:
                fragment = new HelpFragment();
                break;
            case 10:
                ExitAlert();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }

    private void ExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));

        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton(getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {

                dialog.dismiss();
                call_log_out_ws();
            }
        });
        builder.setNegativeButton(getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void call_log_out_ws() {

        if (Common.isOnline(this)) {

            Common.showProgressDialog(this);
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }


            final String password_url = Common.Constant_Class.LOGOUT_URL;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, password_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {

                    Common.hideProgressDialog();

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase("false")) {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                        // if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {

                        try {
                            mEditor.clear();
                            mEditor.apply();
                            mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_FILTER, MODE_PRIVATE);
                            mEditor = mSharedPreferences.edit();
                            mEditor.clear();
                            mEditor.apply();

                            AppController.getInstance().realm.beginTransaction();
                            AppController.getInstance().realm.deleteAll();
                            AppController.getInstance().realm.commitTransaction();

                            Intent mIntent = new Intent(MainActivity.this, MyLocationService.class);
                            stopService(mIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    Common.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int REQ_CODE_SPEECH_INPUT = 100;

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQueryHint(result.get(0));
                    searchView.setQuery(result.get(0), true);
                    Toast.makeText(MainActivity.this, "" + result.get(0), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case IMAGEREQUESTCODE:

                if (data != null) {
                    manageImageFromUri(data.getData());
                }

                break;
            case REQUEST_CHECK_SETTINGS:
                break;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                String id = result.getContents();
                mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.apply();
                ProfileActivity.isEnable = false;
                Intent mIntent = new Intent(this, ProfileActivity.class);
                startActivity(mIntent);
            }
        }
    }

    private void readQRImage(Bitmap bMap) {
        String id;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();// use this otherwise ChecksumException
        try {
            Result result = reader.decode(bitmap);
            id = result.getText();
            if (id != null) {
                mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.apply();
                ProfileActivity.isEnable = false;
                Intent mIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(mIntent);
            }
            //byte[] rawBytes = result.getRawBytes();
            //BarcodeFormat format = result.getBarcodeFormat();
            //ResultPoint[] points = result.getResultPoints();
        } catch (@NonNull NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }

    }


    private void manageImageFromUri(Uri imageUri) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), imageUri);
            if (bitmap != null) {
                readQRImage(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void setIsSearch(boolean isSearch) {
        if (isSearch) {
            if (export != null) {
                export.setVisible(true);
            }
            if (change_role != null && mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
                change_role.setVisible(true);
                if (deleteItem != null) {
                    deleteItem.setVisible(true);
                }
            }

        } else {
            export.setVisible(false);
            change_role.setVisible(true);
            deleteItem.setVisible(true);
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
}