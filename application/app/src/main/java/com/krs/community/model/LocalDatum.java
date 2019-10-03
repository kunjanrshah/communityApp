
package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocalDatum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sub_community_id")
    @Expose
    private String subCommunityId;
    @SerializedName("name")
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubCommunityId() {
        return subCommunityId;
    }

    public void setSubCommunityId(String subCommunityId) {
        this.subCommunityId = subCommunityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
