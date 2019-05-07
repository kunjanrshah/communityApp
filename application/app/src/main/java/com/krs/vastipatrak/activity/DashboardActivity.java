package com.krs.vastipatrak.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.fragments.FragmentDrawer;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    FragmentDrawer drawerFragment;
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        Toolbar mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("HOME");


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerLayout= findViewById(R.id.drawer_layout);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,mDrawerLayout , mToolbar);

        drawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(false);


        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_slide1, getTheme());
        drawerFragment.mDrawerToggle.setHomeAsUpIndicator(drawable);

        drawerFragment.setDrawerListener(this);
        if (drawerFragment.mDrawerToggle != null) {
            drawerFragment.mDrawerToggle.setToolbarNavigationClickListener(v -> {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }
}
