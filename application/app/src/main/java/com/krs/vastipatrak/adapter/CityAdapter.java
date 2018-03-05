package com.krs.vastipatrak.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kunjan on 1/3/18.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder> {

    public ArrayList<String> selectedList = new ArrayList<>();
    private List<City> cityList;

    public CityAdapter(List<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public CityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CityAdapter.MyViewHolder holder, int position) {
        final City city = cityList.get(position);
        holder.name.setText(city.getName());
        holder.chkCity.setChecked(city.isSelected());
        holder.chkCity.setTag(position);
        holder.chkCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selectedList.add(city.getName());
                } else {
                    selectedList.remove(city.getName());
                }
            }
        });
    }

    public ArrayList<String> getSelectedCities()
    {
        return selectedList;
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CheckBox chkCity;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.txtcityname);
            chkCity = view.findViewById(R.id.chkCity);
        }
    }
}
