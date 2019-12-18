
package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.krs.community.entities.CurrentActivity;
import com.krs.community.entities.Occupations;

import java.util.List;

public class OccupationResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("last_updated")
    @Expose
    private String last_updated;
    @SerializedName("data")
    @Expose
    private List<Occupations> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Occupations> getData() {
        return data;
    }

    public void setData(List<Occupations> data) {
        this.data = data;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}
