package com.krs.vastipatrak.model;

public class ListParentData {


    private String str_father_name;
    private String str_mother_name;
    private String str_profile_pic_url;
    private String status;
    private String id;

    private String city;
    private String is_location_enable;
    private String updated_time;
    private String mobile;
    private String user_lat;
    private String user_lng;
    private String str_name;

    private String is_share = "0";

    private String mail;


    public String getIs_share() {
        return is_share;
    }

    public void setIs_share(String is_share) {
        this.is_share = is_share;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String isIs_location_enable() {
        return is_location_enable;
    }

    public void setIs_location_enable(String is_location_enable) {
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
