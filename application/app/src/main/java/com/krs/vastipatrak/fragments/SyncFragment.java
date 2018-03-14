package com.krs.vastipatrak.fragments;

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
import com.android.volley.NoConnectionError;
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

public class SyncFragment extends Fragment {

    private static ProgressBar pb_sync;
    private static TextView tvUpdatedTime;
    private static Button btn_sync;
    public static Handler mHandler = new Handler() {
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
    //SearchView searchView;
    ProgressDialog pDialog;
    String TAG = "SyncFragment";
    String tag_json_obj = "jobj_req";
    private ToggleButton tbtn_sync;
    private EditText edt_sync;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private List<City> cityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CityAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sync, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_sync);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);


        prepareCityData();

        edt_sync.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 0) {
                    if (!edt_sync.getText().toString().isEmpty()) {
                        int val = Integer.parseInt(edt_sync.getText().toString());
                        mEditor.putInt(Common.Constant_Class.EDT_SYNC_TIME, val);
                        mEditor.commit();
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        btn_sync.setOnClickListener(new View.OnClickListener() {
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

                    if (selectedList != null && selectedList.size() > 0) {
                        tvSyncCity.setText("City: " + selectedList.toString().replace("[", "").replace("]", ""));
                    } else {
                        tvSyncCity.setText("City: Default All");
                    }

                    btnDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectedId = radioGroupId.getCheckedRadioButtonId();
                            RadioButton radioSelButton = sync_dialog.findViewById(selectedId);
                            boolean is_reset = false;
                            if (radioSelButton.getId() == R.id.radioResetSync) {
                                is_reset = true;
                            } else {
                                is_reset = false;
                            }
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
                if (isChecked) {
                    mEditor.putBoolean(Common.Constant_Class.TBTN_SYNC, true);
                    mEditor.commit();
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
                    mEditor.commit();
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
            json.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            showProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.GET_CITIES_URL, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response: " + response);
                hideProgressDialog();
                try {
                    boolean success = response.getBoolean(Common.Constant_Class.SUCCESS);
                    String message = response.getString(Common.Constant_Class.MESSAGE);
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
                Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();

            }
        }
        ) {
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
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.cancel();
    }

    private void MemoryAllocation(View rootView) {

        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        tvUpdatedTime = rootView.findViewById(R.id.tvUpdatedTime1);
        btn_sync = rootView.findViewById(R.id.btn_sync);
        pb_sync = rootView.findViewById(R.id.pb_sync);
        edt_sync = rootView.findViewById(R.id.edt_sync);
        tbtn_sync = rootView.findViewById(R.id.tbtn_sync);
        tbtn_sync.setChecked(mSharedPreferences.getBoolean(Common.Constant_Class.TBTN_SYNC, false));
        tbtn_sync.setTextOff(null);
        tbtn_sync.setTextOn(null);
        tbtn_sync.setText(null);
        int val = mSharedPreferences.getInt(Common.Constant_Class.EDT_SYNC_TIME, 0);
        edt_sync.setText("" + val);
        edt_sync.setSelection(edt_sync.getText().length());
        edt_sync.setCursorVisible(false);
        String date = Common.getUpdatedTime(mSharedPreferences.getString(Common.Constant_Class.UPDATED_TIME, "0"));
        tvUpdatedTime.setText(date);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Fetching Cities...");
        pDialog.setCancelable(false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new CityAdapter(cityList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }
}
