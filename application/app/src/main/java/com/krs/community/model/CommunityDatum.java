
package com.krs.community.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommunityDatum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("localcommunity")
    @Expose
    private List<Localcommunity> localcommunity = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Localcommunity> getLocalcommunity() {
        return localcommunity;
    }

    public void setLocalcommunity(List<Localcommunity> localcommunity) {
        this.localcommunity = localcommunity;
    }

}
