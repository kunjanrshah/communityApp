package com.krs.vastipatrak.model;


import org.json.JSONObject;

public class ListChildData {
    private String str_phone;
    private String str_native;
    private String str_address;
    private String str_birth_date;
    private String str_birth_time;
    private String str_birth_place;
    private String str_gotra;
    private String str_gender;
    private String str_blood_group;
    private String str_mobile;
    private String str_id;
    private String home_lat;
    private String home_lng;
    private String profile_id;
    private String can_share = "0";
    private String user_lat;
    private String user_lng;
    private String str_name;
    private String Shared;

    public JSONObject getMjsonobj() {
        return mjsonobj;
    }

    public void setMjsonobj(JSONObject mjsonobj) {
        this.mjsonobj = mjsonobj;
    }

    private JSONObject mjsonobj;
    private String father;
    private String mother;
    private String spouse;

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public String getSpouse_father() {
        return spouse_father;
    }

    public void setSpouse_father(String spouse_father) {
        this.spouse_father = spouse_father;
    }

    public String getSpouse_mother() {
        return spouse_mother;
    }

    public void setSpouse_mother(String spouse_mother) {
        this.spouse_mother = spouse_mother;
    }

    private String spouse_father;
    private String spouse_mother;

    public String getShared() {
        return Shared;
    }

    public void setShared(String isShared) {
        this.Shared = isShared;
    }


    public String getCan_share() {
        return can_share;
    }

    public void setCan_share(String can_share) {
        this.can_share = can_share;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }
    public String getMother_name() {
        return mother;
    }
    public void setMother_name(String mother_name) {
        this.mother = mother_name;
    }

    public String getName() {
        return str_name;
    }

    public void setName(String str_name) {
        this.str_name = str_name;
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

    public String getID() {
        return str_id;
    }

    public void setID(String str_id) {
        this.str_id = str_id;
    }

    public String getMobile() {
        return str_mobile;
    }

    public void setMobile(String str_mobile) {
        this.str_mobile = str_mobile;
    }

    public String getBlood_Group() {
        return str_blood_group;
    }

    public void setBlood_Group(String str_blood_group) {
        this.str_blood_group = str_blood_group;
    }


    public String getGender() {
        return str_gender;
    }

    public void setGender(String str_gender) {
        this.str_gender = str_gender;
    }


    public String getGotra() {
        return str_gotra;
    }

    public void setGotra(String str_gotra) {
        this.str_gotra = str_gotra;
    }

    public String getBirth_place() {
        return str_birth_place;
    }

    public void setBirth_place(String str_birth_place) {
        this.str_birth_place = str_birth_place;
    }

    public String getPhone() {
        return str_phone;
    }

    public void setPhone(String str_phone) {
        this.str_phone = str_phone;
    }

    public String getAddress() {
        return str_address;
    }

    public void setAddress(String str_address) {
        this.str_address = str_address;
    }

    public String getbirth_date() {
        return str_birth_date;
    }

    public void setbirth_date(String str_birth_date) {
        this.str_birth_date = str_birth_date;
    }

    public String getNative() {
        return str_native;
    }

    public void setNative(String str_native) {
        this.str_native = str_native;
    }


    public String getbirth_time() {
        return str_birth_time;
    }

    public void setbirth_time(String str_birth_time) {
        this.str_birth_time = str_birth_time;
    }

}
