package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserCounts {

    @SerializedName("matrimony_counts")
    @Expose
    private String matrimonyCounts;
    @SerializedName("status_counts")
    @Expose
    private String statusCounts;

    public String getMatrimonyCounts() {
        return matrimonyCounts;
    }

    public void setMatrimonyCounts(String matrimonyCounts) {
        this.matrimonyCounts = matrimonyCounts;
    }

    public String getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(String statusCounts) {
        this.statusCounts = statusCounts;
    }

}
