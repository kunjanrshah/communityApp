package com.krs.community.activity;

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

import com.google.android.material.appbar.AppBarLayout;
import com.krs.community.R;
import com.krs.community.fragments.CalendarFragment;
import com.krs.community.fragments.CommitteeFragment;
import com.krs.community.fragments.DashboardFragment;
import com.krs.community.fragments.FragmentDrawer;
import com.krs.community.fragments.HeaderDetailFragment;
import com.krs.community.fragments.SmartFilterFragment;
import com.krs.community.fragments.StatisticFragment;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;

import static com.krs.community.utils.Utility.movetoFragment;


public class DashboardActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    public static AppBarLayout myAppBar;
    public static SpaceNavigationView spaceNavigationView;
    private DrawerLayout mDrawerLayout;
    private String TAG = DashboardActivity.class.getSimpleName();

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

        FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
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
        spaceNavigationView.addSpaceItem(new SpaceItem("Calendar", R.drawable.calendar));
        spaceNavigationView.shouldShowFullBadgeText(false);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);
        spaceNavigationView.setCentreButtonIcon(R.drawable.filter_icon);
        spaceNavigationView.animate().translationY(0f).alpha(1f).setDuration(2000).start();

        movetoFragment(DashboardActivity.this,new DashboardFragment());

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(CalendarFragment.class.getSimpleName());
                //if (fragment == null || !fragment.isVisible()) {
                movetoFragment(DashboardActivity.this,new SmartFilterFragment());
                //}
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                if (itemIndex == 1) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(CalendarFragment.class.getSimpleName());
                    if (fragment == null || !fragment.isVisible()) {
                        movetoFragment(DashboardActivity.this,new CalendarFragment());
                    }
                } else if (itemIndex == 0) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(DashboardFragment.class.getSimpleName());
                    if (fragment == null || !fragment.isVisible()) {
                        movetoFragment(DashboardActivity.this,new DashboardFragment());
                    }
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
                if (itemIndex == 1) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(CalendarFragment.class.getSimpleName());
                    if (fragment == null || !fragment.isVisible()) {
                        movetoFragment(DashboardActivity.this,new CalendarFragment());
                    }
                } else if (itemIndex == 0) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(DashboardFragment.class.getSimpleName());
                    if (fragment == null || !fragment.isVisible()) {
                        movetoFragment(DashboardActivity.this,new DashboardFragment());
                    }
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        Log.d(TAG, "position: " + position);
        if (position == 0) {
            movetoFragment(this,new DashboardFragment());
        }else if(position == 1)
        {
            movetoFragment(this,new StatisticFragment());
        }
        else if(position == 2)
        {
            movetoFragment(this,new CommitteeFragment());
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

                movetoFragment(this,new HeaderDetailFragment());

                return true;
            case R.id.action_notification:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
