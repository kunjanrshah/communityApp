package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchDataModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("totalHead")
    @Expose
    private Integer totalHead;
    @SerializedName("totalMem")
    @Expose
    private Integer totalMem;
    @SerializedName("members")
    @Expose
    private SearchData data;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getTotalHead() {
        return totalHead;
    }

    public void setTotalHead(Integer totalHead) {
        this.totalHead = totalHead;
    }

    public Integer getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(Integer totalMem) {
        this.totalMem = totalMem;
    }

    public SearchData getData() {
        return data;
    }

    public void setData(SearchData data) {
        this.data = data;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
