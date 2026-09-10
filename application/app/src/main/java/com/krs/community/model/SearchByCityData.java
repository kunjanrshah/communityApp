package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchByCityData {

    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("length")
    @Expose
    private String length;

    @SerializedName("alpha")
    @Expose
    private String alpha;

    @SerializedName("sub_community_id")
    @Expose
    private String sub_community_id;

    @SerializedName("filter_by")
    @Expose
    private FilterBy filterBy;

    @SerializedName("search")
    @Expose
    private String search;

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

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getSub_community_id() {
        return sub_community_id;
    }

    public void setSub_community_id(String sub_community_id) {
        this.sub_community_id = sub_community_id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
