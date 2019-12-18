package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Statistics {

    @SerializedName("TotalFamily")
    @Expose
    private Boolean TotalFamily;

    @SerializedName("TotalMembers")
    @Expose
    private Boolean TotalMembers;

    @SerializedName("TotalMale")
    @Expose
    private Boolean TotalMale;

    @SerializedName("TotalFemale")
    @Expose
    private Boolean TotalFemale;
    @SerializedName("TotalUnmarriedMale")
    @Expose
    private Boolean TotalUnmarriedMale;
    @SerializedName("TotalUnmarriedFemale")
    @Expose
    private Boolean TotalUnmarriedFemale;

    public Boolean getTotalUnmarriedFemale() {
        return TotalUnmarriedFemale;
    }

    public void setTotalUnmarriedFemale(Boolean totalUnmarriedFemale) {
        TotalUnmarriedFemale = totalUnmarriedFemale;
    }

    public Boolean getTotalUnmarriedMale() {
        return TotalUnmarriedMale;
    }

    public void setTotalUnmarriedMale(Boolean totalUnmarriedMale) {
        TotalUnmarriedMale = totalUnmarriedMale;
    }

    public Boolean getTotalFemale() {
        return TotalFemale;
    }

    public void setTotalFemale(Boolean totalFemale) {
        TotalFemale = totalFemale;
    }

    public Boolean getTotalMale() {
        return TotalMale;
    }

    public void setTotalMale(Boolean totalMale) {
        TotalMale = totalMale;
    }

    public Boolean getTotalMembers() {
        return TotalMembers;
    }

    public void setTotalMembers(Boolean totalMembers) {
        TotalMembers = totalMembers;
    }

    public Boolean getTotalFamily() {
        return TotalFamily;
    }

    public void setTotalFamily(Boolean totalFamily) {
        TotalFamily = totalFamily;
    }
}

