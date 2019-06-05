package com.krs.vastipatrak.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.krs.vastipatrak.R;

import java.util.ArrayList;
import java.util.List;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;
import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;

public class ProfileDetailFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        ScrollerViewPager viewPager = (ScrollerViewPager) rootView.findViewById(R.id.view_pager);
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
        ArrayList<Integer> list=new ArrayList<>();
        list.add(R.drawable._woman);
        list.add(R.drawable._woman);
        list.add(R.drawable._woman);
        list.add(R.drawable._woman);
        return list;
    }

    private List<String> getTitles()
    {
        ArrayList<String> list=new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        return list;
    }
}
