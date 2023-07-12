package com.krs.community.awareviewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krs.community.R;

import java.util.ArrayList;


public class ViewPagerFragment extends ViewPagerFragmentBase {

    private final static String TAG = ViewPagerFragment.class.getSimpleName();

    private View mRoot;

    private ObservableRecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.viewpager_fragment, container, false);

        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = mRoot.findViewById(R.id.recyclerView);
        setupRecyclerView(mRecyclerView);


        updateModuleRecyclerData(getRandomizedData());

    }

    private ArrayList<String> getRandomizedData() {
        ArrayList<String> arrayList = new ArrayList<>();

        int random = (int) (Math.random() * 40 + 5);
        for (int i = 0; i < random; i++) {
            arrayList.add(" number " + i);
        }

        return arrayList;
    }


    public void updateModuleRecyclerData(ArrayList<String> arrayList) {
        if (!isAdded()) {
            return;
        }
        mRecyclerView.setAdapter(new HeaderAutoFooterRecyclerAdapter(getActivity(), arrayList, R.layout.facts_list_item, getHeaderHeight()));
        initiateScrollPosition();
    }
}