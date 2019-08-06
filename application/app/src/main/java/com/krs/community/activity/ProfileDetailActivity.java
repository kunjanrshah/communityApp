package com.krs.community.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.krs.community.R;
import com.krs.community.fragments.MainDetailsFragment;
import com.krs.community.fragments.MatrimonyDetailsFragment;
import com.krs.community.fragments.PersonalDetailsFragment;
import com.krs.community.fragments.ProfessionalDetailsFragment;
import com.krs.community.utils.Utility;


public class ProfileDetailActivity extends AppCompatActivity {

    ImageView img_close, img_one, img_two, img_three, img_four;
    int id = 0;
    ViewPager viewpager;
    MainDetailsFragment mainDetailsFragment;
    PersonalDetailsFragment personalDetailsFragment;
    ProfessionalDetailsFragment professionalDetailsFragment;
    MatrimonyDetailsFragment matrimonyDetailsFragment;
    MyPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this,R.color.mdtp_white,false);
        }


        Bundle mbundle = getIntent().getExtras();
        if (mbundle != null) {
            id = mbundle.getInt("id");
        }

        img_one = findViewById(R.id.img_one);
        img_two = findViewById(R.id.img_two);
        img_three = findViewById(R.id.img_three);
        img_four = findViewById(R.id.img_four);
        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(v -> {
            finish();
            Utility.fade(this);
        });

        viewpager = findViewById(R.id.viewpager);
        mainDetailsFragment = new MainDetailsFragment();
        personalDetailsFragment = new PersonalDetailsFragment();
        professionalDetailsFragment = new ProfessionalDetailsFragment();
        matrimonyDetailsFragment = new MatrimonyDetailsFragment();

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapterViewPager);


        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    img_one.setImageResource(R.drawable.one_step);
                    img_two.setImageResource(R.drawable.two_step_gray);
                    img_three.setImageResource(R.drawable.three_step_gray);
                    img_four.setImageResource(R.drawable.four_step_gray);
                } else if (position == 1) {
                    img_one.setImageResource(R.drawable.one_step_gray);
                    img_two.setImageResource(R.drawable.two_step);
                    img_three.setImageResource(R.drawable.three_step_gray);
                    img_four.setImageResource(R.drawable.four_step_gray);
                } else if (position == 2) {
                    img_one.setImageResource(R.drawable.one_step_gray);
                    img_two.setImageResource(R.drawable.two_step_gray);
                    img_three.setImageResource(R.drawable.three_step);
                    img_four.setImageResource(R.drawable.four_step_gray);
                } else if (position == 3) {
                    img_one.setImageResource(R.drawable.one_step_gray);
                    img_two.setImageResource(R.drawable.two_step_gray);
                    img_three.setImageResource(R.drawable.three_step_gray);
                    img_four.setImageResource(R.drawable.four_step);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Utility.hideKeyboard(this);
    }


    class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mainDetailsFragment;
                case 1:
                    return personalDetailsFragment;
                case 2:
                    return professionalDetailsFragment;
                case 3:
                    return matrimonyDetailsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }


}
