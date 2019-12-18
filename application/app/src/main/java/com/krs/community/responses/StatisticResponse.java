package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.krs.community.entities.City;

import java.util.List;

public class StatisticResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Statistics> data = null;

    public List<Statistics> getData() {
        return data;
    }

    public void setData(List<Statistics> data) {
        this.data = data;
    }
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
