package com.krs.vastipatrak.model;

/**
 * Created by kushal on 26/01/16.
 */
public class ListParentData {

    String str_name;
    String str_father_name;
    String str_mother_name;
    String str_profile_pic_url;
    String status;
    String id;
    String user_lat;
    String user_lng;
    String home_lat;
    String home_lng;
    String city;
    boolean is_location_enable;
    String updated_time;

    public String getHome_lat() {
        return home_lat;
    }

    public void setHome_lat(String home_lat) {
        this.home_lat = home_lat;
    }

    public String getHome_lng() {
        return home_lng;
    }

    public void setHome_lng(String home_lng) {
        this.home_lng = home_lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public boolean isIs_location_enable() {
        return is_location_enable;
    }

    public void setIs_location_enable(boolean is_location_enable) {
        this.is_location_enable = is_location_enable;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }




    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public String getUser_lng() {
        return user_lng;
    }

    public void setUser_lng(String user_lng) {
        this.user_lng = user_lng;
    }


    public String getName() {
        return str_name;
    }

    public void setName(String str_name) {
        this.str_name = str_name;
    }

    public String getFatherName() {
        return str_father_name;
    }

    public void setFatherName(String str_father_name) {
        this.str_father_name = str_father_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMotherName() {
        return str_mother_name;
    }

    public void setMotherName(String str_mother_name) {
        this.str_mother_name = str_mother_name;
    }


    public String getProfilePicUrl() {
        return str_profile_pic_url;
    }

    public void setProfilePicUrl(String str_profile_pic_url) {
        this.str_profile_pic_url = str_profile_pic_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String str_status) {
        this.status = str_status;
    }



}
