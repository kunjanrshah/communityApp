package com.krs.vastipatrak.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.fragments.HeaderDetailFragment;
import com.krs.vastipatrak.fragments.MainDetailsFragment;
import com.krs.vastipatrak.fragments.MatrimonyDetailsFragment;
import com.krs.vastipatrak.fragments.PersonalDetailsFragment;
import com.krs.vastipatrak.fragments.ProfessionalDetailsFragment;
import com.krs.vastipatrak.utils.Utility;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;
import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;

public class ProfileDetailActivity extends AppCompatActivity {

    ImageView img_close;
    int id=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_profile_detail);

        Bundle mbundle=getIntent().getExtras();
        if(mbundle!=null)
        {
           id= mbundle.getInt("id");
        }
        Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
        img_close=findViewById(R.id.img_close);



        img_close.setOnClickListener(v -> {
            finish();
        });

        ScrollerViewPager viewPager =  findViewById(R.id.view_pager);
        SpringIndicator springIndicator = findViewById(R.id.indicator);

        PagerModelManager manager = new PagerModelManager();
        manager.addFragment(new MainDetailsFragment(),"1");
        manager.addFragment(new PersonalDetailsFragment(),"2");
        manager.addFragment(new ProfessionalDetailsFragment(),"3");
        manager.addFragment(new MatrimonyDetailsFragment(),"4");
        ModelPagerAdapter adapter = new ModelPagerAdapter(getSupportFragmentManager(), manager);
        viewPager.setAdapter(adapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);
        Utility.hideKeyboard(this);
    }
}
