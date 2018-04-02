package com.krs.vastipatrak.model;

public class City {

    private String name;
    private final boolean isSelected;

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

}
