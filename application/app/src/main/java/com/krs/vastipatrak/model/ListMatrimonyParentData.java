package com.krs.vastipatrak.model;

public class ListMatrimonyParentData {

    String id;
    String profile_id;
    String profile_pic_url;
    String father_name;
    String mother_name;
    String city;
    String updated_time;
    String child_gender;
    String name;

    public String getChild_gender() {
        return child_gender;
    }

    public void setChild_gender(String child_gender) {
        this.child_gender = child_gender;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String str_name) {
        this.name = str_name;
    }

    public String getFatherName() {
        return father_name;
    }

    public void setFatherName(String str_father_name) {
        this.father_name = str_father_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMotherName() {
        return mother_name;
    }

    public void setMotherName(String str_mother_name) {
        this.mother_name = str_mother_name;
    }

    public String getProfilePicUrl() {
        return profile_pic_url;
    }

    public void setProfilePicUrl(String str_profile_pic_url) {
        this.profile_pic_url = str_profile_pic_url;
    }
}
