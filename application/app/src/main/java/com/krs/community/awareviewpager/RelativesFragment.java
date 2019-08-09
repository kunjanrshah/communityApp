package com.krs.community.awareviewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krs.community.R;
import com.krs.community.adapter.BottomSheetAdapter;
import com.krs.community.fabtransitionlayout.BottomSheetLayout;
import com.krs.community.model.BottomSheet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class RelativesFragment extends ViewPagerFragmentBase {

    private final static String TAG = RelativesFragment.class.getSimpleName();

    private View mRoot;
    private ObservableRecyclerView mRecyclerView;

    private ListView mMenuList;
    private FloatingActionButton mFab;
    private BottomSheetLayout mBottomSheetLayout;

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
        mMenuList=mRoot.findViewById(R.id.list_menu);
        mFab=mRoot.findViewById(R.id.fab);
        mBottomSheetLayout =mRoot.findViewById(R.id.bottom_sheet);

        updateModuleRecyclerData(getRandomizedData());
        initListMenu();
        mBottomSheetLayout.setFab(mFab);

        mFab.setOnClickListener(v -> mBottomSheetLayout.expandFab());
    }

    private ArrayList<String> getRandomizedData() {
        ArrayList<String> arrayList = new ArrayList<>();

        int random = (int )(Math.random() * 40 + 5);
        for (int i = 0; i < random; i++) {
            arrayList.add(" number " + i);
        }

        return arrayList;
    }


    private void updateModuleRecyclerData(ArrayList<String> arrayList) {
        if (!isAdded()) {
            return;
        }
        mRecyclerView.setAdapter(new HeaderAutoFooterRecyclerAdapter(getActivity(),arrayList, R.layout.relatives_list_item,getHeaderHeight()));
        initiateScrollPosition();
    }

    private void initListMenu() {
        ArrayList<BottomSheet> bottomSheets = new ArrayList<>();
        bottomSheets.add(BottomSheet.to().setBottomSheetMenuType(BottomSheet.BottomSheetMenuType.EMAIL));
        bottomSheets.add(BottomSheet.to().setBottomSheetMenuType(BottomSheet.BottomSheetMenuType.ACCOUNT));
        bottomSheets.add(BottomSheet.to().setBottomSheetMenuType(BottomSheet.BottomSheetMenuType.SETTING));
        BottomSheetAdapter adapter = new BottomSheetAdapter(getActivity(), bottomSheets);
        mMenuList.setFooterDividersEnabled(true);
        mMenuList.setHeaderDividersEnabled(true);
        mMenuList.setDividerHeight(5);
        mMenuList.setAdapter(adapter);
    }
}