package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Statistics {

    @SerializedName("TotalVillages")
    @Expose
    private Integer totalVillages;
    @SerializedName("TotalFamily")
    @Expose
    private Integer totalFamily;
    @SerializedName("TotalMembers")
    @Expose
    private Integer totalMembers;
    @SerializedName("TotalMale")
    @Expose
    private Integer totalMale;
    @SerializedName("TotalFemale")
    @Expose
    private Integer totalFemale;
    @SerializedName("TotalUnmarriedMale")
    @Expose
    private Integer totalUnmarriedMale;
    @SerializedName("TotalUnmarriedFemale")
    @Expose
    private Integer totalUnmarriedFemale;

    @SerializedName("TotalInterestedMale")
    @Expose
    private Integer totalInterestedMale;

    @SerializedName("TotalInterestedFemale")
    @Expose
    private Integer totalInterestedFemale;

    public Integer getTotalVillages() {
        return totalVillages;
    }

    public void setTotalVillages(Integer totalVillages) {
        this.totalVillages = totalVillages;
    }

    public Integer getTotalFamily() {
        return totalFamily;
    }

    public void setTotalFamily(Integer totalFamily) {
        this.totalFamily = totalFamily;
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }

    public Integer getTotalMale() {
        return totalMale;
    }

    public void setTotalMale(Integer totalMale) {
        this.totalMale = totalMale;
    }

    public Integer getTotalFemale() {
        return totalFemale;
    }

    public void setTotalFemale(Integer totalFemale) {
        this.totalFemale = totalFemale;
    }

    public Integer getTotalUnmarriedMale() {
        return totalUnmarriedMale;
    }

    public void setTotalUnmarriedMale(Integer totalUnmarriedMale) {
        this.totalUnmarriedMale = totalUnmarriedMale;
    }

    public Integer getTotalUnmarriedFemale() {
        return totalUnmarriedFemale;
    }

    public void setTotalUnmarriedFemale(Integer totalUnmarriedFemale) {
        this.totalUnmarriedFemale = totalUnmarriedFemale;
    }

    public Integer getTotalInterestedFemale() {
        return totalInterestedFemale;
    }

    public void setTotalInterestedFemale(Integer totalInterestedFemale) {
        this.totalInterestedFemale = totalInterestedFemale;
    }

    public Integer getTotalInterestedMale() {
        return totalInterestedMale;
    }

    public void setTotalInterestedMale(Integer totalInterestedMale) {
        this.totalInterestedMale = totalInterestedMale;
    }
}

