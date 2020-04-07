package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ByDistanceModel {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("nearBy")
    @Expose
    private String nearBy;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("km")
    @Expose
    private String km;

    @SerializedName("start")
    @Expose
    private String start;

    @SerializedName("length")
    @Expose
    private String length;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getNearBy() {
        return nearBy;
    }

    public void setNearBy(String nearBy) {
        this.nearBy = nearBy;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}
