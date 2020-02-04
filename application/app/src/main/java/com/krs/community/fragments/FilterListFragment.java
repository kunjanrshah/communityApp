package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.google.android.flexbox.FlexboxLayout;
import com.krs.community.R;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class FilterListFragment extends Fragment {

    ArrayList<JSONObject> lstFilters;
    JSONArray mJsonArray;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_filters, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.colorBG, false);
        }
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
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ParallaxRecyclerAdapter<JSONObject> adapter = new ParallaxRecyclerAdapter<JSONObject>(lstFilters) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<JSONObject> adapter, int i) {

                ListViewHolder holder = ((ListViewHolder) viewHolder);
                try {
                    String name = lstFilters.get(i).getString(getActivity().getString(R.string.name_filter));
                    String value =lstFilters.get(i).getString(getActivity().getString(R.string.value_filter));
                    Log.d("FilterListFragment",""+value);
                    holder.tvName.setText(name);

                    JSONObject json=new JSONObject(value);
                    /*for(Iterator<String> iter = json.keys(); iter.hasNext();) {
                        String key = iter.next();
                        holder.stub.setLayoutResource(R.layout.item_filters);
                        View inflated = holder.stub.inflate();
                        TextView label=inflated.findViewById(R.id.tv_label);
                        label.setText(key);
                        TextView value1=inflated.findViewById(R.id.tv_value);
                        value1.setText(json.getString(key));
                        if(inflated.getParent()!=null){
                            ((ViewGroup)inflated.getParent()).removeView(inflated);
                        }
                        holder.flexboxLayout.addView(inflated);
                    }*/
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

                });

                holder.flexboxLayout.setOnClickListener(v -> {
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
            Guru.putString(getActivity().getString(R.string.list_filter), "");
            lstFilters.clear();
            adapter.notifyDataSetChanged();
        });

        adapter.setParallaxHeader(header, rv_filters);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        rv_filters.setLayoutManager(MyLayoutManager);
        rv_filters.setItemAnimator(new DefaultItemAnimator());
        rv_filters.setAdapter(adapter);
        rv_filters.setHasFixedSize(true);

        return root;
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

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        FlexboxLayout flexboxLayout;
        ImageView imgEdit, imgDelete;
        ViewStub stub;
        ListViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            flexboxLayout = v.findViewById(R.id.flexbox_layout);
            imgEdit = v.findViewById(R.id.img_edit);
            imgDelete = v.findViewById(R.id.img_delete);
            stub = v.findViewById(R.id.layout_stub);
        }
    }
}
