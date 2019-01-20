
package com.yadav.vastipatrak.model;

import android.support.annotation.Nullable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ListEventData extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private String description;
    private String location;
    private String event_date;
    private String lat;
    private String lng;
    @Nullable
    private RealmList<String> youtubeUrl = null;
    @Nullable
    private RealmList<String> images = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    @Nullable
    public RealmList<String> getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(@Nullable RealmList<String> youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    @Nullable
    public RealmList<String> getImages() {
        return images;
    }

    public void setImages(@Nullable RealmList<String> images) {
        this.images = images;
    }

    public String getEventDate() {
        return event_date;
    }

    public void setEventDate(String event_date) {
        this.event_date = event_date;
    }

}
