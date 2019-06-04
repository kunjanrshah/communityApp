package com.krs.vastipatrak.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.krs.vastipatrak.R;

import java.util.List;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;
import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;

public class ProfileDetailFragment extends Fragment {

    ScrollerViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        viewPager = (ScrollerViewPager) rootView.findViewById(R.id.view_pager);
        SpringIndicator springIndicator = (SpringIndicator) rootView.findViewById(R.id.indicator);


        PagerModelManager manager = new PagerModelManager();
        manager.addCommonFragment(MemberProfileFragment.class, getLayoutRes(), getTitles());
        ModelPagerAdapter adapter = new ModelPagerAdapter(getActivity().getSupportFragmentManager(), manager);
        viewPager.setAdapter(adapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);

        return rootView;
    }

    private List<Integer> getLayoutRes(){
        return Lists.newArrayList(R.drawable._woman, R.drawable._woman, R.drawable._woman, R.drawable._woman);
    }

    private List<String> getTitles(){
        return Lists.newArrayList("1", "2", "3", "4");
    }
}
