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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.app.Config;
import com.krs.vastipatrak.app.PrefManager;
import com.krs.vastipatrak.fragments.AboutFragment;
import com.krs.vastipatrak.fragments.ChangePasswordFragment;
import com.krs.vastipatrak.fragments.FragmentDrawer;
import com.krs.vastipatrak.fragments.SearchFragment;
import com.krs.vastipatrak.fragments.SyncFragment;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.NotificationUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;

import static com.krs.vastipatrak.utils.Common.Constant_Class.LOCATION_INTERVAL;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, TimePickerDialog.OnTimeSetListener,
        OnDateSetListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener

{

    static final int REQUEST_CHECK_SETTINGS = 199;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static Location mLastLocation;
    public static GoogleApiClient mGoogleApiClient;
    public static String lat, lon;
    private final String[] INIT_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};
    private final String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final String[] CALL_PERMS = {Manifest.permission.CALL_PHONE};
    private final int INIT_REQUEST = 1;
    private final int CALL_REQUEST = 2;
    private final int LOCATION_REQUEST = 3;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String query = "", query_string = "";
    FragmentDrawer drawerFragment;
    boolean doubleBackToExitPressedOnce = false;
    GoogleApiClient googleApiClient;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                if (!Common.CheckGpsStatus(MainActivity.this)) {
                    MainActivity.this.displayLocationSettingsRequest(MainActivity.this);
                }
            }
        }
    };
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Toolbar mToolbar;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private PrefManager prefManager;
    private LocationRequest mLocationRequest;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        AppController.getInstance().setMainActivityContext(MainActivity.this);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            query = mBundle.getString(Common.Constant_Class.QUERY);
            query_string = mBundle.getString(Common.Constant_Class.QUERY_STRING);
           /* try {
                JSONObject mJsonObject = new JSONObject(query_string);
                Log.i("jsonObject :", "" + mJsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Common.canCallPhone(this) && !Common.canAccessLocation(this)) {
                requestPermissions(INIT_PERMS, INIT_REQUEST);
            } else if (!Common.canAccessLocation(this)) {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            } else if (!Common.canCallPhone(this)) {
                requestPermissions(CALL_PERMS, CALL_REQUEST);
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (Common.canAccessLocation(this)) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }
        String id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
        if (id.equalsIgnoreCase(Common.Constant_Class.ADMIN_1) || id.equalsIgnoreCase(Common.Constant_Class.ADMIN_2)) {
            AppController.isAdmin = true;
        }
        displayView(0);



/*
        String userid = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
        String name = mSharedPreferences.getString(Common.Constant_Class.FIRST_NAME, "") + " " + mSharedPreferences.getString(Common.Constant_Class.LAST_NAME, "") + " - " + mSharedPreferences.getString(Common.Constant_Class.NATIVE_PLACE, "");
*/

        if (!Common.CheckGpsStatus(this)) {
            displayLocationSettingsRequest(MainActivity.this);
        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Push notification: " + message);
                    //txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
        Common.getDeviceId(this);
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //txtRegId.setText("Firebase Reg Id: " + regId);
            Log.d(TAG, "Firebase Reg Id: " + regId);
        } else {
            //txtRegId.setText("Firebase Reg Id is not received yet!");
            Log.d(TAG, "Firebase Reg Id: is not received yet!");
        }
    }


    private void displayLocationSettingsRequest(Context context) {
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
                public void onResult(LocationSettingsResult result) {
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
                                status.startResolutionForResult(MainActivity.this, MainActivity.REQUEST_CHECK_SETTINGS);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "You need to give permission to access location ! ", Toast.LENGTH_SHORT).show();
                } else if (Common.canAccessLocation(this)) {
                    buildGoogleApiClient();
                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...
                }


                break;
            case CALL_REQUEST:

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(this, "You need to give permission to access phone ! ", Toast.LENGTH_SHORT).show();
                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...
                }

                break;
            case INIT_REQUEST:
                if (!Common.canCallPhone(this) && !Common.canAccessLocation(this)) {
                    Toast.makeText(this, "You need to give permission to access phone and location ! ", Toast.LENGTH_SHORT).show();
                } else if (Common.canAccessLocation(this)) {
                    buildGoogleApiClient();
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent mIntent = new Intent(MainActivity.this, FilterActivity.class);
                // mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                //finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }
        });

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Fragment fragment = new SearchFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(Common.Constant_Class.QUERY, query);
                fragment.setArguments(mBundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

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

        return true;
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

        if (position == 0) {
            query = "";
            query_string = "";
        }

        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        Common.Title = getString(R.string.app_name);
        switch (position) {

            case 0:
                //  fragment = new HomeFragment();
           /*     Intent mIntent = new Intent(MainActivity.this, ShowWebView.class);
                startActivity(mIntent);*/

                fragment = new SearchFragment();
                Bundle mBundle = new Bundle();
                if (query != null) {
                    mBundle.putString(Common.Constant_Class.QUERY, query);
                } else if (query_string != null) {
                    mBundle.putString(Common.Constant_Class.QUERY_STRING, query_string);
                }
                fragment.setArguments(mBundle);

                //            Common.Title = getString(R.string.title_home);
                break;
            case 1:

                mEditor.putBoolean("myprofile", true);
                mEditor.commit();

                Intent mIntent1 = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(mIntent1);
                this.overridePendingTransition(0, 0);

                //              Common.Title = getString(R.string.title_my_profile);
                break;
            case 2:

                fragment = new ChangePasswordFragment();
//                Common.Title = getString(R.string.title_change_password);
                break;
            case 3:
                fragment = new AboutFragment();
                break;
            case 4:

                fragment = new SyncFragment();
//                Common.Title = getString(R.string.title_change_password);
                break;

            case 5:

                prefManager.setSliderWelcome(true);
                prefManager.setFirstTimeLaunch(true);
                mEditor.commit();
                Intent mIntent2 = new Intent(MainActivity.this, TourActivity.class);
                startActivity(mIntent2);
                this.overridePendingTransition(0, 0);
                break;
            case 6:

                ExitAlert();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            //  getSupportActionBar().setTitle(Common.Title);
        }
    }

    private void ExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));

        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton(getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mSharedPreferences.getString(Common.Constant_Class.PROFILE_PIC_URL, "");
                mEditor.clear();
                mEditor.commit();
                //    Common.Title = getString(R.string.title_login);
                Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(mIntent);
                finish();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            case REQUEST_CHECK_SETTINGS:
                break;
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        //  String date = dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        String date = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = hourString + ":" + minuteString;
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
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
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

}