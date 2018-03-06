package com.krs.vastipatrak.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by TecStub on 01-Sep-16.
 */
public class ListChildrenData extends RealmObject{

    @PrimaryKey
    private String child_id;
    private String profile_id;
    private String child_name, child_bday, child_edu, child_work, child_img_url;
    private byte[] ChildBytes;

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
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
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
