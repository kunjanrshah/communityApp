package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchData {

    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("length")
    @Expose
    private String length;
    @SerializedName("filter_by")
    @Expose
    private FilterBy filterBy;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public FilterBy getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(FilterBy filterBy) {
        this.filterBy = filterBy;
    }

}
