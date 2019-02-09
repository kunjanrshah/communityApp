package com.yadav.samaj.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ListChildrenData extends RealmObject{

    @PrimaryKey
    private String id;
    private String profile_id;
    private boolean is_interested;
    private boolean is_married;
    private String child_name;
    private String child_bday;
    private String birth_place;
    private String birth_time;
    private String mobile;
    private String blood_group;
    private String child_edu;
    private String child_work;
    private String child_img_url;
    private String gender;
    private byte[] ChildBytes;

    public String getChild_bdate_reminder_id() {
        return child_bdate_reminder_id;
    }

    public void setChild_bdate_reminder_id(String child_bdate_reminder_id) {
        this.child_bdate_reminder_id = child_bdate_reminder_id;
    }

    private String child_bdate_reminder_id;
    public boolean isIs_married() {
        return is_married;
    }

    public void setIs_married(boolean is_married) {
        this.is_married = is_married;
    }

    public boolean isIs_interested() {
        return is_interested;
    }

    public void setIs_interested(boolean is_interested) {
        this.is_interested = is_interested;
    }

    public String getBirth_place() {
        return birth_place;
    }

    public void setBirth_place(String birth_place) {
        this.birth_place = birth_place;
    }

    public String getBirth_time() {
        return birth_time;
    }

    public void setBirth_time(String birth_time) {
        this.birth_time = birth_time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isInterest() {
        return is_interested;
    }

    public void setInterest(boolean interest) {
        is_interested = interest;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public byte[] getChildBytes() {
        return ChildBytes;
    }

    public void setChildBytes(byte[] ChildBytes) {
        this.ChildBytes = ChildBytes;
    }

    public String getChild_id() {
        return id;
    }

    public void setChild_id(String id) {
        this.id = id;
    }

    public String getChild_name() {
        return child_name;
    }

    public void setChild_name(String child_name) {
        this.child_name = child_name;
    }

    public String getChild_bday() {
        return child_bday;
    }

    public void setChild_bday(String child_bday) {
        this.child_bday = child_bday;
    }

    public String getChild_edu() {
        return child_edu;
    }

    public void setChild_edu(String child_edu) {
        this.child_edu = child_edu;
    }

    public String getChild_work() {
        return child_work;
    }

    public void setChild_work(String child_work) {
        this.child_work = child_work;
    }

    public String getChild_img_url() {
        return child_img_url;
    }

    public void setChild_img_url(String child_img_url) {
        this.child_img_url = child_img_url;
    }


}
