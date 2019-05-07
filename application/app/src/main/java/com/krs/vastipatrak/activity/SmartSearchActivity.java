package com.krs.vastipatrak.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.SmartSearchAdapter;

public class SmartSearchActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private SmartSearchAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smart_search);
        expandableListView = (ExpandableListView) findViewById(R.id.lst_expandable);
        expandableListView.setGroupIndicator(null);
        adapter = new SmartSearchAdapter(SmartSearchActivity.this);
        expandableListView.setAdapter(adapter);
        setListener();

    }

    // Setting different listeners to expandablelistview
    void setListener() {

        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener((listview, view, group_pos, id) -> {

            if (group_pos == 0 || group_pos == 7) {
                return true;
            } else {
                return false;
            }
        });

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            // Default position
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
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
