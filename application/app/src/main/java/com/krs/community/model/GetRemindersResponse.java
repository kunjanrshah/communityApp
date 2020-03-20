
package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRemindersResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;



    @SerializedName("data")
    @Expose
    private List<Reminders> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Reminders> getData() {
        return data;
    }

    public void setData(List<Reminders> data) {
        this.data = data;
    }

}
