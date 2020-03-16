
package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.krs.community.model.Member;

import java.util.List;

public class SmartFilterResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("total_records")
    @Expose
    private Integer totalRecords;
    @SerializedName("members")
    @Expose
    private List<Member> members = null;

    @SerializedName("membersharing")
    @Expose
    private List<Member> membersharing = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Member> getMembersharing() {
        return membersharing;
    }

    public void setMembersharing(List<Member> membersharing) {
        this.membersharing = membersharing;
    }
}
