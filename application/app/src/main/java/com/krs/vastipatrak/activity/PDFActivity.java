package com.krs.vastipatrak.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.Utility;
import com.krs.vastipatrak.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PDFActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        MemoryAllocation();
        ToolbarSetup();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!(isFinishing())) {
                    Utility.showProgressDialog(PDFActivity.this);
                }
            }
        });
    }

    private void MemoryAllocation() {

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
    }

    private void ToolbarSetup() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("2017 Matrimony PDF");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNavigation();
            }
        });
    }

    private void backNavigation() {
        Intent mIntent = new Intent(PDFActivity.this, HomeActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPager(ViewPager viewPager) {

        /*ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment girlsFragment = new GirlsFragment();
        Fragment boys1Fragment = new Boys1Fragment();
        Fragment boys2Fragment = new Boys2Fragment();
        adapter.addFrag(girlsFragment, AppConstants.GIRLS);
        adapter.addFrag(boys1Fragment, AppConstants.BOYS_P1);
        adapter.addFrag(boys2Fragment, AppConstants.BOYS_P2);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backNavigation();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
