package com.krs.community.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.github.squti.guru.Guru;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FilterListFragment extends Fragment {

    ArrayList<JSONObject> lstFilters;
    JSONArray mJsonArray;
    LinearLayout llNotFound;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_filters, container, false);
        llNotFound = root.findViewById(R.id.ll_not_found);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.colorBG, false);
        }

        AppController mApp = (AppController) getApplicationContext();
        mApp.firebaseAnalytics(getContext(), FilterListFragment.class.getSimpleName());
        mApp.facebookAnalytics(getContext(), FilterListFragment.class.getSimpleName());

        lstFilters = new ArrayList<>();
        String listFilter = Guru.getString(getActivity().getString(R.string.list_filter), "");

        try {
            if (listFilter != null && !listFilter.isEmpty()) {
                mJsonArray = new JSONArray(listFilter);
            } else {
                mJsonArray = new JSONArray();
            }
            for (int i = 0; i < mJsonArray.length(); i++) {
                lstFilters.add((JSONObject) mJsonArray.get(i));
            }
            if (lstFilters.size() > 0) {
                llNotFound.setVisibility(View.GONE);
            } else {
                llNotFound.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ParallaxRecyclerAdapter<JSONObject> adapter = new ParallaxRecyclerAdapter<JSONObject>(lstFilters) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<JSONObject> adapter, int i) {

                ListViewHolder holder = ((ListViewHolder) viewHolder);
                try {
                    String name = lstFilters.get(i).getString(getActivity().getString(R.string.name_filter));
                    String value = lstFilters.get(i).getString(getActivity().getString(R.string.value_filter));
                    Log.d("FilterListFragment", "" + value);
                    holder.tvName.setText(name);

                    JSONObject json = new JSONObject(value);
                    for (Iterator<String> iter = json.keys(); iter.hasNext(); ) {
                        String key = iter.next();
                        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = layoutInflater.inflate(R.layout.item_filters, null, false);
                        TextView label = view.findViewById(R.id.tv_label);
                        String str = key.replace("_", " ");
                        String[] strArray = str.split(" ");
                        StringBuilder builder = new StringBuilder();
                        for (String s : strArray) {
                            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                            builder.append(cap + " ");
                        }
                        label.setText(builder.toString() + ": ");
                        TextView value1 = view.findViewById(R.id.tv_value);
                        value1.setText(json.getString(key) + " ");
                        holder.flexboxLayout.addView(view);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                holder.imgDelete.setOnClickListener(v -> {
                    lstFilters.remove(i);
                    mJsonArray.remove(i);
                    notifyDataSetChanged();
                    Guru.putString(getActivity().getString(R.string.list_filter), mJsonArray.toString());
                });

                holder.imgEdit.setOnClickListener(v -> {
                    ExpandableFilterListFragment listFragment = new ExpandableFilterListFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putString(getActivity().getString(R.string.edit_filter), lstFilters.get(i).toString());
                    listFragment.setArguments(mBundle);
                    Utility.movetoFragment(getActivity(), listFragment);
                });

                holder.llFilter.setOnClickListener(v -> {
                    try {
                        SmartFilterResult filterResult = new SmartFilterResult();
                        Bundle mBundle = new Bundle();
                        String filter = lstFilters.get(i).getString(getActivity().getString(R.string.value_filter));
                        mBundle.putString(getActivity().getString(R.string.filter_values), filter);
                        filterResult.setArguments(mBundle);
                        Utility.movetoFragment(getActivity(), filterResult);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<JSONObject> adapter, int i) {
                return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filters_list_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<JSONObject> adapter) {
                return lstFilters.size();
            }
        };

        RecyclerView rv_filters = root.findViewById(R.id.rv_filters);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_filters, container, false);
        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.backNavigation(getActivity());
        });
        TextView tvClear = header.findViewById(R.id.tv_clear);
        tvClear.setOnClickListener(v -> {
            if (lstFilters.size() > 0) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText(getActivity().getString(R.string.smart_filter))
                        .setContentText("Do you want to clear all Filters?")
                        .setConfirmText(getActivity().getString(R.string.YesPleaseCity))
                        .setCancelText(getActivity().getString(R.string.no))
                        .setCustomImage(R.drawable.ic_medk)
                        .showCancelButton(true)
                        .setConfirmClickListener(sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
                            Guru.putString(getActivity().getString(R.string.list_filter), "");
                            lstFilters.clear();
                            llNotFound.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                            noRecordDialog("Filters clear successfully!");
                        })
                        .show();
            } else {
                noRecordDialog("No filter found!");
            }
        });

        adapter.setParallaxHeader(header, rv_filters);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        rv_filters.setLayoutManager(MyLayoutManager);
        rv_filters.setItemAnimator(new DefaultItemAnimator());
        rv_filters.setAdapter(adapter);
        rv_filters.setHasFixedSize(true);

        return root;
    }

    private void noRecordDialog(String message) {
        int gif = R.drawable.gif_no_record;
        new TTFancyGifDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveBtnText(getString(R.string.ok))
                .setPositiveBtnBackground("#843f52")
                .setGifResource(gif)
                .isCancellable(false)
                .OnPositiveClicked(() -> {

                })
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        LinearLayout llFilter;
        FlexboxLayout flexboxLayout;
        ImageView imgEdit, imgDelete;

        ListViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            llFilter = v.findViewById(R.id.ll_filter);
            flexboxLayout = v.findViewById(R.id.flexbox_layout);
            flexboxLayout.setFlexDirection(FlexDirection.ROW);
            imgEdit = v.findViewById(R.id.img_edit);
            imgDelete = v.findViewById(R.id.img_delete);
        }
    }
}
