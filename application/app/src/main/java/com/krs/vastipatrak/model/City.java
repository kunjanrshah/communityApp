package com.krs.vastipatrak.model;

/**
 * Created by kunjan on 1/3/18.
 */

public class City {

    private String name;
    private boolean isSelected;

    public City() {
    }

    public City(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
