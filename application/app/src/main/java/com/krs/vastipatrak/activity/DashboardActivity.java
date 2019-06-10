package com.krs.vastipatrak.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.fragments.DashboardFragment;
import com.krs.vastipatrak.fragments.FragmentDrawer;
import com.krs.vastipatrak.fragments.HeaderDetailFragment;
import com.krs.vastipatrak.fragments.SmartFilterFragment;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;

import spencerstudios.com.bungeelib.Bungee;


public class DashboardActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    public static AppBarLayout myAppBar;
    FragmentDrawer drawerFragment;
    DrawerLayout mDrawerLayout;
    DashboardFragment dashboardFragment;
    SmartFilterFragment ssfragment;
    HeaderDetailFragment headerDetailFragment;
    private SpaceNavigationView spaceNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");

        // View details_toolbar_transition_helper = findViewById(R.id.details_toolbar_transition_helper);
        // details_toolbar_transition_helper.setTranslationY(-156);
        myAppBar = findViewById(R.id.myAppBar);
        myAppBar.setTranslationY(-getToolbarHeight());
        myAppBar.animate().translationY(0f).alpha(1f).setDuration(2000).start();

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
        spaceNavigationView.animate().translationY(0f).alpha(1f).setDuration(2000).start();

        dashboardFragment = new DashboardFragment();
        ssfragment = new SmartFilterFragment();
        headerDetailFragment = new HeaderDetailFragment();

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

    private int getToolbarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }


    private void movetoFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
        fragmentTransaction.replace(R.id.container_body, fragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        if (position == 3) {
            Intent mIntent = new Intent(this, NearByLocationActivity.class);
            startActivity(mIntent);
            Bungee.fade(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_profile:

                movetoFragment(headerDetailFragment);

                return true;
            case R.id.action_notification:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
