package com.krs.vastipatrak.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.CityAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.City;
import com.krs.vastipatrak.service.SyncService;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;

public class SyncFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar pb_sync;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvUpdatedTime;
    @SuppressLint("StaticFieldLeak")
    private static Button btn_sync;
    @SuppressLint("HandlerLeak")
    @Nullable
    public static final Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            boolean sync_start = bundle.getBoolean("sync_start");
            String sync_time = bundle.getString("sync_time");
            try {
                if (sync_start) {
                    pb_sync.setVisibility(View.VISIBLE);
                    btn_sync.setText("Stop");
                } else {
                    pb_sync.setVisibility(View.GONE);
                    btn_sync.setText("Start");
                }
                if (sync_time != null && !sync_time.isEmpty()) {
                    tvUpdatedTime.setText(sync_time);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @NonNull
    private final String TAG = "SyncFragment";
    @NonNull
    private final List<City> cityList = new ArrayList<>();

    private ToggleButton tbtn_sync;
    private EditText edt_sync;
    @Nullable
    private SharedPreferences mSharedPreferences = null;
    @Nullable
    private SharedPreferences.Editor mEditor = null;
    //SearchView searchView;
    @Nullable
    //private ProgressDialog pDialog;
    private CityAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sync, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(R.string.title_sync);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);


        prepareCityData();

        edt_sync.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(@NonNull TextView v, int actionId, KeyEvent event) {
                if (actionId == 0) {
                    if (!edt_sync.getText().toString().isEmpty()) {
                        assert mEditor != null;
                        mEditor.putString(Common.Constant_Class.SYNC_TIME, edt_sync.getText().toString());
                        mEditor.apply();
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (btn_sync.getText().toString().equalsIgnoreCase("Start")) {
                    final ArrayList<String> selectedList = mAdapter.getSelectedCities();
                    final Dialog sync_dialog = new Dialog(getActivity());
                    sync_dialog.setContentView(R.layout.custom_sync_dialog);
                    sync_dialog.setTitle(getResources().getString(R.string.sync_data));

                    TextView tvSyncCity = sync_dialog.findViewById(R.id.tvSyncCity);
                    final RadioGroup radioGroupId = sync_dialog.findViewById(R.id.radioGroupId);
                    Button btnDownload = sync_dialog.findViewById(R.id.btnDownload);

                    if (selectedList.size() > 0) {
                        tvSyncCity.setText("City: " + selectedList.toString().replace("[", "").replace("]", ""));
                    } else {
                        tvSyncCity.setText("City: Default All");
                    }

                    btnDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectedId = radioGroupId.getCheckedRadioButtonId();
                            RadioButton radioSelButton = sync_dialog.findViewById(selectedId);
                            boolean is_reset = radioSelButton.getId() == R.id.radioResetSync;
                            Intent mIntent = new Intent(getActivity(), SyncService.class);
                            mIntent.putStringArrayListExtra("selectedCities", selectedList);
                            mIntent.putExtra(Common.Constant_Class.IS_RESET, is_reset);
                            getActivity().startService(mIntent);
                            btn_sync.setText("Stop");
                            sync_dialog.cancel();
                        }
                    });
                    sync_dialog.show();
                } else {
                    btn_sync.setText("Start");
                    pb_sync.setVisibility(View.GONE);
                    Intent mIntent = new Intent(getActivity(), SyncService.class);
                    getActivity().stopService(mIntent);
                }
            }
        });

        tbtn_sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                Intent serviceIntent = new Intent(getActivity(), SyncService.class);
                ArrayList<String> selectedList = mAdapter.getSelectedCities();
                serviceIntent.putStringArrayListExtra("selectedCities", selectedList);
                PendingIntent servicePendingIntent = PendingIntent.getService(getActivity(), 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                assert am != null;
                assert mEditor != null;
                mEditor.putString(Common.Constant_Class.SYNC_TIME, edt_sync.getText().toString());
                if (isChecked) {
                    mEditor.putBoolean(Common.Constant_Class.TBTN_SYNC, true);
                    mEditor.apply();
                    long interval = 0;
                    if (!edt_sync.getText().toString().isEmpty()) {
                        interval = Long.parseLong(edt_sync.getText().toString());
                    }
                    //interval = interval * 1000 * 60 * 60 * 24;
                    interval = interval * 1000 * 30;

                    if (interval != 0) {
                        am.setRepeating(AlarmManager.RTC_WAKEUP, interval, interval, servicePendingIntent);
                        Toast.makeText(getActivity(), "Enjoy Sync Service!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Enter Days!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mEditor.putBoolean(Common.Constant_Class.TBTN_SYNC, false);
                    mEditor.apply();
                    am.cancel(servicePendingIntent);
                    Toast.makeText(getActivity(), "Cancelled Sync!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    private void prepareCityData() {

        JSONObject json = new JSONObject();
        try {
            assert mSharedPreferences != null;
            json.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            Common.showProgressDialog(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.GET_CITIES_URL, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                Log.d(TAG, "response: " + response);
                hideProgressDialog();
                try {
                    boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);

                    if (success) {
                        JSONArray mJsonArray = response.getJSONArray("data");
                        for (int i = 0; i < mJsonArray.length(); i++) {
                            City city = new City(mJsonArray.getString(i), false);
                            cityList.add(city);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                String message = null;
                hideProgressDialog();
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
                Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();

            }
        }
        ) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                params.put(Common.Constant_Class.DEVICE_TOKEN,mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN,""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Common.Constant_Class.INIT_TIMEOUT, Common.Constant_Class.DEFAULT_MAX_RETRIES, Common.Constant_Class.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
    }

   /* private void showProgressDialog() {
        assert pDialog != null;
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        assert pDialog != null;
        if (pDialog.isShowing())
            pDialog.cancel();
    }*/

    @SuppressLint("SetTextI18n")
    private void MemoryAllocation(View rootView) {

        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
        tvUpdatedTime = rootView.findViewById(R.id.tvUpdatedTime1);
        btn_sync = rootView.findViewById(R.id.btn_sync);
        pb_sync = rootView.findViewById(R.id.pb_sync);
        if (SyncService.isProcessing) {
            pb_sync.setVisibility(View.VISIBLE);
            btn_sync.setText("Stop");
        }
        edt_sync = rootView.findViewById(R.id.edt_sync);
        tbtn_sync = rootView.findViewById(R.id.tbtn_sync);
        tbtn_sync.setChecked(mSharedPreferences.getBoolean(Common.Constant_Class.TBTN_SYNC, false));
        tbtn_sync.setTextOff(null);
        tbtn_sync.setTextOn(null);
        tbtn_sync.setText(null);
        String val = mSharedPreferences.getString(Common.Constant_Class.SYNC_TIME, "0");
        edt_sync.setText("" + val);
        edt_sync.setSelection(edt_sync.getText().length());
        //  edt_sync.setCursorVisible(false);
        String date = Common.getUpdatedTime(mSharedPreferences.getString(Common.Constant_Class.UPDATED_TIME, "0"));
        tvUpdatedTime.setText(date);
       /* pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Fetching Cities...");
        pDialog.setCancelable(false);*/

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new CityAdapter(cityList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }
}
