
package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.krs.community.model.Member;

import java.util.List;

public class FamilyDetailResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("total_records")
    @Expose
    private String total_records;
    @SerializedName("members")
    @Expose
    private List<Member> member = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Member> getMember() {
        return member;
    }

    public void setMember(List<Member> member) {
        this.member = member;
    }

    public String getTotal_records() {
        return total_records;
    }

    public void setTotal_records(String total_records) {
        this.total_records = total_records;
    }
}
