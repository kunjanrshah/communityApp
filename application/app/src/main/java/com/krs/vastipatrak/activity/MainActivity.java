package com.krs.vastipatrak.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.app.Config;
import com.krs.vastipatrak.fragments.AboutFragment;
import com.krs.vastipatrak.fragments.ChangePasswordFragment;
import com.krs.vastipatrak.fragments.FragmentDrawer;
import com.krs.vastipatrak.fragments.HomeFragment;
import com.krs.vastipatrak.fragments.MatrimonyFragment;
import com.krs.vastipatrak.fragments.RelativeFragment;
import com.krs.vastipatrak.fragments.SearchFragment;
import com.krs.vastipatrak.interfaces.IAdminControl;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.NotificationUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.Constant_Class.LOCATION_INTERVAL;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener

{

    public static final String[] CALL_CAMARA = {Manifest.permission.CAMERA};
    public static final int CAMARA_REQUEST = 4;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static GoogleApiClient mGoogleApiClient;
    public static String lat, lon;
    public static int MOVE_TO_SEARCH = 0;
    private static Location mLastLocation;
    private final int REQUEST_CHECK_SETTINGS = 199;
    private final int IMAGEREQUESTCODE = 1;
    private final String[] INIT_PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};
    private final String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final String[] CALL_PERMS = {Manifest.permission.CALL_PHONE};
    private final int INIT_REQUEST = 1;
    private final int CALL_REQUEST = 2;
    private final int LOCATION_REQUEST = 3;
    private String query = "";
    private String push_message = null;
    private String query_string = "";
    private boolean doubleBackToExitPressedOnce = false;
    private GoogleApiClient googleApiClient;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                if (Common.CheckGpsStatus(MainActivity.this)) {
                    MainActivity.this.displayLocationSettingsRequest(MainActivity.this);
                }
            }
        }
    };
    @Nullable
    private Fragment fragment = null;
    private Fragment searchFragment = null;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private SearchView searchView;
    private IntentIntegrator qrScan;
    private IAdminControl IAdminControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
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
        if (Build.VERSION.SDK_INT >= 23) {
            if (Common.canAccessLocation(this)) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }
        /*String id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
        if (id.equalsIgnoreCase(Common.Constant_Class.ADMIN_1) || id.equalsIgnoreCase(Common.Constant_Class.ADMIN_2)) {
            AppController.isAdmin = true;
        }*/

        if (Common.CheckGpsStatus(this)) {
            displayLocationSettingsRequest(MainActivity.this);
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
    }


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
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

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

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        this.registerReceiver(this.mReceiver, filter);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.mReceiver);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "You need to give permission to access location ! ", Toast.LENGTH_SHORT).show();
                } else if (Common.canAccessLocation(this)) {
                    buildGoogleApiClient();
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
                    buildGoogleApiClient();
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

        MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent mIntent = new Intent(MainActivity.this, FilterActivity.class);
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


                IAdminControl = (IAdminControl) searchFragment;
                mBundle.putString(Common.Constant_Class.QUERY, query);
                searchFragment.setArguments(mBundle);
                fragmentTransaction.replace(R.id.container_body, searchFragment).commit();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem export = menu.findItem(R.id.action_export);
        export.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.ExportSearchData(MainActivity.this);
                return false;
            }
        });

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Common.promptSpeechInput(MainActivity.this);
                return false;
            }
        });

        MenuItem action_scan = menu.findItem(R.id.action_scan);
        action_scan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                qrScan.initiateScan();
                return false;
            }
        });

        MenuItem action_scan_image = menu.findItem(R.id.action_scan_image);
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
        MenuItem change_role = menu.findItem(R.id.action_change_role);
        MenuItem deactiveItem = menu.findItem(R.id.action_deactive);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem activeItem = menu.findItem(R.id.action_activate);
        if (mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
            if (Common.isOnline(this)) {
                nonActives.setVisible(true);
                change_role.setVisible(true);
                activeAdd.setVisible(true);
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

        return true;
    }

    private void moveToSearch(int menu) {

        Bundle mBundle = new Bundle();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (menu) {
            case 1:
                fragment = searchFragment;
                IAdminControl = (IAdminControl) fragment;
                try {
                    ((SearchFragment) fragment).callNonActivesWS();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBundle.putInt(Common.Constant_Class.AdminControl, Common.Constant_Class.NonActive);
                fragment.setArguments(mBundle);
                fragmentTransaction.replace(R.id.container_body, fragment).commit();

                break;
            case 2:
                try {
                    IAdminControl.CallActivate();
                } catch (Exception e) {
                    Toast.makeText(this, "Select Non-Actives First", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    IAdminControl.CallDeActivate();
                } catch (Exception e) {
                    Toast.makeText(this, "Select Non-Actives First", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    IAdminControl.CallDelete();
                } catch (Exception e) {
                    Toast.makeText(this, "Select Non-Actives First", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 5:
                try {
                    IAdminControl.ChangeRole();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*int id = item.getItemId();
        if (id == R.id.action_search) {

            return false;
        }*/

        return false;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

       /* if (position == 0) {
            query = "";
            query_string = "";
        }*/

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
                Intent mIntent1 = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(mIntent1);
                this.overridePendingTransition(0, 0);
                break;
            case 2:
                fragment = new ChangePasswordFragment();
                break;
            case 3:
                fragment = new RelativeFragment();
                break;
            case 4:
                fragment = new MatrimonyFragment();
                break;
            case 5:
                /*Intent mIntent = new Intent(MainActivity.this, PDFActivity.class);
                startActivity(mIntent);
                this.overridePendingTransition(0, 0);*/
                // fragment = new SyncFragment();
                Intent mIntent2 = new Intent(MainActivity.this, TourActivity.class);
                startActivity(mIntent2);
                this.overridePendingTransition(0, 0);
                break;
            case 6:
                fragment = new AboutFragment();
                break;
            case 7:
                ExitAlert();
                break;
            /*case 8:
                break;*/
            /*case 9:
                break;*/

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
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
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {

                            try {
                                //   mEditor.clear();
                                //  mEditor.apply();
                                   /* AppController.getInstance().realm.beginTransaction();
                                    AppController.getInstance().realm.deleteAll();
                                    AppController.getInstance().realm.commitTransaction();*/
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mIntent);
                            finish();
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        }
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
                Intent mIntent = new Intent(this, MyProfileActivity.class);
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
                Intent mIntent = new Intent(MainActivity.this, MyProfileActivity.class);
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


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_INTERVAL); // Update location every minutes
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }
}