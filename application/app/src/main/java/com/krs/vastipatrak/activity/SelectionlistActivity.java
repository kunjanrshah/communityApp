package com.krs.vastipatrak.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.ItemsAdapter;
import com.krs.vastipatrak.adapter.SelectionListAdapter;
import com.krs.vastipatrak.model.Items;
import com.krs.vastipatrak.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Button btnSave;
    private List<Items> ItemList;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // get the listview
        expListView = findViewById(R.id.lvExp);
        recyclerView = findViewById(R.id.lvList);
        edt_other = findViewById(R.id.edt_other);
        btnSave = findViewById(R.id.btnSave);

        Bundle mBundle = new Bundle();
        boolean listview = false;
        String section = "";
        if (mBundle != null) {
            mBundle = getIntent().getExtras();
            listview = mBundle.getBoolean(getString(R.string.listview));
            section = mBundle.getString(getString(R.string.section));
        }
        ToolbarSetup(section);
        if (listview) {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setVisibility(View.VISIBLE);
            expListView.setVisibility(View.GONE);
            prepareListData();
            adapter = new ItemsAdapter(this, ItemList);
            recyclerView.setAdapter(adapter);

        } else {
            recyclerView.setVisibility(View.GONE);
            expListView.setVisibility(View.VISIBLE);
            prepareExpandableListData();
            listAdapter = new SelectionListAdapter(this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
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
                    finishActivity(value);
                } else {
                    Toast.makeText(SelectionlistActivity.this, "Specify if Other", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void finishActivity(String value) {
        Common.hideKeyboard(this);
        Intent mIntent = new Intent();
        mIntent.putExtra("selection", value);
        setResult(RESULT_OK, mIntent);
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void ToolbarSetup(String section) {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (section.equalsIgnoreCase("city")) {
            getSupportActionBar().setSubtitle("Select City");
        } else if (section.equalsIgnoreCase("nplace")) {
            getSupportActionBar().setSubtitle("Select Native");
        } else if (section.equalsIgnoreCase("bplace")) {
            getSupportActionBar().setSubtitle("Select BirthPlace");
        } else if (section.equalsIgnoreCase("education")) {
            getSupportActionBar().setSubtitle("Select Education");
        }

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
            case Common.REQ_CODE_SPEECH_INPUT: {
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
        Common.hideKeyboard(this);
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

                Intent mIntent = new Intent(SelectionlistActivity.this, MainActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.putExtra(Common.Constant_Class.QUERY, query);
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

        MenuItem admins = menu.findItem(R.id.action_admins);
        admins.setVisible(false);

        MenuItem scan_image = menu.findItem(R.id.action_scan_image);
        scan_image.setVisible(false);

        MenuItem scan_qr = menu.findItem(R.id.action_scan);
        scan_qr.setVisible(false);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.promptSpeechInput(SelectionlistActivity.this);
                return false;
            }
        });

        MenuItem action_toggle = menu.findItem(R.id.action_toggle);
        action_toggle.setVisible(false);

        return true;
    }

    private void prepareListData() {
        ItemList = new ArrayList<>();
        ItemList.add(new Items("item1"));
        ItemList.add(new Items("item2"));
        ItemList.add(new Items("item3"));
    }

    private void prepareExpandableListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

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
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }
    }

}
