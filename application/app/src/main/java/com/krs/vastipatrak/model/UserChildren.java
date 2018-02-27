package com.krs.vastipatrak.model;

import com.krs.vastipatrak.utils.Common;

import io.realm.RealmObject;

/**
 * Created by kunjan on 22/2/18.
 */

public class UserChildren extends RealmObject {

    public String getChildren_id() {
        return children_id;
    }

    public void setChildren_id(String children_id) {
        this.children_id = children_id;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
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

    public String getChild_image_url() {
        return child_image_url;
    }

    public void setChild_image_url(String child_image_url) {
        this.child_image_url = child_image_url;
    }

    private String children_id;
    private String profile_id;
    private String child_name;
    private String child_bday;
    private String child_edu;
    private String child_work;
    private String child_image_url;
    
}
