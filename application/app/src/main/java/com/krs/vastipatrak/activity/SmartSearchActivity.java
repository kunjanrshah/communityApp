package com.krs.vastipatrak.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.SmartPopUpAdapter;
import com.krs.vastipatrak.adapter.SmartSearchAdapter;
import com.orhanobut.dialogplus.DialogPlus;

public class SmartSearchActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    int previousGroup = -1;
    private SmartSearchAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enterFromBottomAnimation();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smart_search);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        getSupportActionBar().setTitle("Smart Search");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_left_arrow);

        expandableListView = findViewById(R.id.lst_expandable);
        expandableListView.setGroupIndicator(null);
        adapter = new SmartSearchAdapter(SmartSearchActivity.this,expandableListView);
        expandableListView.setAdapter(adapter);
        setListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        exitToBottomAnimation();
    }

    protected void enterFromBottomAnimation(){
        overridePendingTransition(R.anim.activity_open_translate_from_bottom, R.anim.activity_no_animation);
    }

    protected void exitToBottomAnimation(){
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_close_translate_to_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Setting different listeners to expandablelistview
    void setListener() {

        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener((listview, view, group_pos, id) -> {

            if (group_pos == 0) {
                return true;
            }  else {
                adapter.storeFieldsValues();
                return false;
            }
        });

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            // Default position

            @Override
            public void onGroupExpand(int groupPosition) {
                adapter.previousGroup=groupPosition;
                if (groupPosition != previousGroup)

                // Collapse the expanded group
                expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }

        });

        // This listener will show toast on child click
        expandableListView.setOnChildClickListener((listview, view, groupPos, childPos, id) -> {
            Toast.makeText(SmartSearchActivity.this, "You clicked : " + adapter.getChild(groupPos, childPos), Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}
