
package com.krs.community.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchByCityModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("totalHead")
    @Expose
    private Integer totalHead;
    @SerializedName("totalMem")
    @Expose
    private Integer totalMem;
    @SerializedName("searchByCityData")
    @Expose
    private SearchByCityData searchByCityData;
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

    public SearchByCityData getSearchByCityData() {
        return searchByCityData;
    }

    public void setSearchByCityData(SearchByCityData searchByCityData) {
        this.searchByCityData = searchByCityData;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
