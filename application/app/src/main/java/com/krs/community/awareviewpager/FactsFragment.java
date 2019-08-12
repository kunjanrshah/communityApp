package com.krs.community.awareviewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krs.community.R;
import com.krs.community.activity.FamilyTreeDetailActivity;
import com.leinardi.android.speeddial.SpeedDialOverlayLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class FactsFragment extends ViewPagerFragmentBase  implements FamilyTreeDetailActivity.IhideView {

    private final static String TAG = FactsFragment.class.getSimpleName();

    private View mRoot;
    private ObservableRecyclerView mRecyclerView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.viewpager_fragment, container, false);

        return mRoot;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = mRoot.findViewById(R.id.recyclerView);
        setupRecyclerView(mRecyclerView);

        mSpeedDialView = mRoot.findViewById(R.id.speedDial);
        initSpeedDial(mSpeedDialView);

        overlay = mRoot.findViewById(R.id.overlay);

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


    private void updateModuleRecyclerData(ArrayList<String> arrayList) {
        if (!isAdded()) {
            return;
        }
        rvdapter =new HeaderAutoFooterRecyclerAdapter(getActivity(), arrayList, R.layout.facts_list_item, getHeaderHeight());
        mRecyclerView.setAdapter(rvdapter);
        initiateScrollPosition();
    }

    @Override
    public void hideOverlay() {
        if(mSpeedDialView.isOpen())
        {
            mSpeedDialView.close(true);
            overlay.hide();
        }

    }

    @Override
    public boolean isOpen() {
        return mSpeedDialView.isOpen();
    }
}