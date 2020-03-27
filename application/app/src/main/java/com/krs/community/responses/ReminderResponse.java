
package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReminderResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Reminder data;

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

    public Reminder getData() {
        return data;
    }

    public void setData(Reminder data) {
        this.data = data;
    }

}
