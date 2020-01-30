package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.krs.community.model.Member;

public class UpdateProfileResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data") //members
    @Expose
    private Member member;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
