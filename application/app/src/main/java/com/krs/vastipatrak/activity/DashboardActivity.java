package com.krs.vastipatrak.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.fragments.DashboardFragment;
import com.krs.vastipatrak.fragments.FragmentDrawer;
import com.krs.vastipatrak.fragments.SmartSearchFragment;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;

public class DashboardActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    FragmentDrawer drawerFragment;
    DrawerLayout mDrawerLayout;
    DashboardFragment dashboardFragment;
    SmartSearchFragment ssfragment;
    private SpaceNavigationView spaceNavigationView;

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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, mToolbar);

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
        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Home", R.drawable.home));
        spaceNavigationView.addSpaceItem(new SpaceItem("Filter", R.drawable.filter));
        spaceNavigationView.shouldShowFullBadgeText(false);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);

        dashboardFragment = new DashboardFragment();
        ssfragment = new SmartSearchFragment();
        movetoFragment(dashboardFragment);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                if (itemIndex == 1) {
                    movetoFragment(ssfragment);
                } else if (itemIndex == 0) {
                    movetoFragment(dashboardFragment);
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
                if (itemIndex == 1) {
                    movetoFragment(ssfragment);
                } else if (itemIndex == 0) {
                    movetoFragment(dashboardFragment);
                }
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                Toast.makeText(DashboardActivity.this, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(DashboardActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
        //spaceNavigationView.showIconOnly();
    }

    private void movetoFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
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
