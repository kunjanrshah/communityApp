
package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("OTP")
    @Expose
    private String OTP;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private Member data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Member getData() {
        return data;
    }

    public void setData(Member data) {
        this.data = data;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
