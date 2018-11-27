package com.krs.vastipatrak.model;

import org.json.JSONArray;

public class ListParentData {


    private String str_father_name;
    private String str_mother_name;
    private String str_profile_pic_url;
    private String status;
    private String id;
    private String password;
    private String city;
    private String is_location_enable;
    private String updated_time;
    private String mobile;
    private String user_lat;
    private String user_lng;
    private String home_lat;
    private String home_lng;
    private String office_lat;
    private String office_lng;
    private String type;
    private String str_name;
    private String Shared;
    private JSONArray CalLabelArray;

    String bdate_rem_id;
    String spouse_rem_id;
    String mdate_rem_id;
    String child_rem_id;
    public String getBdate_rem_id() {
        return bdate_rem_id;
    }

    public void setBdate_rem_id(String bdate_rem_id) {
        this.bdate_rem_id = bdate_rem_id;
    }

    public String getSpouse_rem_id() {
        return spouse_rem_id;
    }

    public void setSpouse_rem_id(String spouse_rem_id) {
        this.spouse_rem_id = spouse_rem_id;
    }

    public String getMdate_rem_id() {
        return mdate_rem_id;
    }

    public void setMdate_rem_id(String mdate_rem_id) {
        this.mdate_rem_id = mdate_rem_id;
    }

    public String getChild_rem_id() {
        return child_rem_id;
    }

    public void setChild_rem_id(String child_rem_id) {
        this.child_rem_id = child_rem_id;
    }




    public JSONArray getCalLabelArray() {
        return CalLabelArray;
    }

    public void setCalLabelArray(JSONArray calLabelArray) {
        CalLabelArray = calLabelArray;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShared() {
        return Shared;
    }

    public void setShared(String isShared) {
        this.Shared = isShared;
    }

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

    public String getOffice_lat() {
        return office_lat;
    }

    public void setOffice_lat(String office_lat) {
        this.office_lat = office_lat;
    }

    public String getOffice_lng() {
        return office_lng;
    }

    public void setOffice_lng(String office_lng) {
        this.office_lng = office_lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    String distance="";
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
