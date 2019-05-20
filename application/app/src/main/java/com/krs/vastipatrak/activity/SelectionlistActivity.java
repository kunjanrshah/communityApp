package com.krs.vastipatrak.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.ItemsAdapter;
import com.krs.vastipatrak.adapter.SelectionListAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.Items;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SelectionlistActivity extends AppCompatActivity {

    SelectionListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Toolbar mToolbar;
    private RecyclerView recyclerView;
    private ItemsAdapter adapter;
    private String TAG = SelectionlistActivity.class.getSimpleName();
    private SearchView searchView;
    private int lastExpandedPosition = -1;
    private EditText edt_other;
    private Button btnSave,btnClear;
    private List<Items> ItemList;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // get the listview
        expListView = findViewById(R.id.lvExp);
        recyclerView = findViewById(R.id.lvList);
        edt_other = findViewById(R.id.edt_other);
        btnSave = findViewById(R.id.btnSave);
        btnClear= findViewById(R.id.btnClear);
        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        Bundle mBundle = new Bundle();
        boolean listview = false;
        String title = "";
        if (mBundle != null) {
            mBundle = getIntent().getExtras();
            listview = mBundle.getBoolean(getString(R.string.listview));
            title = mBundle.getString(getString(R.string.title));
        }
        ToolbarSetup(title);
        if (listview) {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setVisibility(View.VISIBLE);
            expListView.setVisibility(View.GONE);
            if (title.toLowerCase().contains("native")) {
                prepareListData(AppController.getInstance().lstNative);
            } else if (title.toLowerCase().contains("education")) {
                prepareListData(AppController.getInstance().lstEducation);
            }

            adapter = new ItemsAdapter(this, ItemList);
            recyclerView.setAdapter(adapter);

        } else {
            recyclerView.setVisibility(View.GONE);
            expListView.setVisibility(View.VISIBLE);
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();
            // prepareExpandableListData();
            getStateList();

        }


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                finishActivity(ItemList.get(position).getName());
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));

        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
                // Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                // Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Collapsed", Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " : " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                finishActivity(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                return false;
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edt_other.getText().toString();
                if (!value.isEmpty()) {
                    value=value+" (other)";
                    finishActivity(value);
                } else {
                    Toast.makeText(SelectionlistActivity.this, "Specify if Other", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.hideKeyboard(this);
    }

    private void getStateList() {
        if (Utility.isOnline(this)) {

            Utility.showProgressDialog(this);
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                String user_id = mSharedPreferences.getString(AppConstants.USER_ID, "");
                String token = mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, "");
                mJsonObject.put(AppConstants.USER_ID, user_id);
                mJsonObject.put(AppConstants.ACCESS_TOKEN, token);
                mJsonObject.put(AppConstants.RESPONSE_DATA, "city_state");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.GET_MASTER_DATA_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    Utility.hideProgressDialog();

                    try {
                        JSONArray mArray = response.getJSONArray(AppConstants.DATA);
                        for (int i = 0; i < mArray.length(); i++) {
                            JSONObject mObject = mArray.getJSONObject(i);
                            listDataHeader.add(mObject.getString("state"));
                            JSONArray cityArray = mObject.getJSONArray("city");
                            List<String> citylist = new ArrayList<String>();
                            for (int j = 0; j < cityArray.length(); j++) {
                                citylist.add(cityArray.getString(j));
                            }
                            listDataChild.put(mObject.getString("state"), citylist);
                        }
                        listAdapter = new SelectionListAdapter(SelectionlistActivity.this, listDataHeader, listDataChild);
                        expListView.setAdapter(listAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Utility.hideProgressDialog();
                }
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
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }


    private void finishActivity(String value) {
        Utility.hideKeyboard(this);
        Intent mIntent = new Intent();
        mIntent.putExtra(getString(R.string.selection), value);
        setResult(RESULT_OK, mIntent);
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void ToolbarSetup(String title) {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(title);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "step setNavigationOnClickListener");
                backNavigation();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Utility.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQueryHint(result.get(0));
                    searchView.setQuery(result.get(0), true);
                }
                break;
            }
        }
    }

    private void backNavigation() {
        Utility.hideKeyboard(this);
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem saveItem = menu.findItem(R.id.action_save);
        saveItem.setVisible(false);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent mIntent = new Intent(SelectionlistActivity.this, HomeActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.putExtra(AppConstants.QUERY, query);
                startActivity(mIntent);
                Log.d(TAG, "step onQueryTextSubmit");
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem export = menu.findItem(R.id.action_export);
        export.setVisible(false);

       /* MenuItem admins = menu.findItem(R.id.action_admins);
        admins.setVisible(false);

        MenuItem scan_image = menu.findItem(R.id.action_scan_image);
        scan_image.setVisible(false);

        MenuItem scan_qr = menu.findItem(R.id.action_scan);
        scan_qr.setVisible(false);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);*/

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utility.promptSpeechInput(SelectionlistActivity.this);
                return false;
            }
        });

        MenuItem action_toggle = menu.findItem(R.id.action_toggle);
        action_toggle.setVisible(false);

        return true;
    }

    private void prepareListData(ArrayList<String> list) {
        Collections.sort(list);
        ItemList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemList.add(new Items(list.get(i)));
        }
    }

    private void prepareExpandableListData() {


        // Adding child data
        listDataHeader.add("Gujarat");
        listDataHeader.add("Maharastra");
        listDataHeader.add("Madhyapradesh");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        GestureDetector mGestureDetector;
        private OnItemClickListener mListener;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onLongItemClick(View view, int position);
        }
    }

}
