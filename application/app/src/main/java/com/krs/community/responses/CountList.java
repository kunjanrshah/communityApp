package com.krs.community.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountList {

    @SerializedName("business_categories")
    @Expose
    private String businessCategories;
    @SerializedName("business_sub_categories")
    @Expose
    private String businessSubCategories;
    @SerializedName("cities")
    @Expose
    private String cities;
    @SerializedName("committees")
    @Expose
    private String committees;
    @SerializedName("current_activity")
    @Expose
    private String currentActivity;
    @SerializedName("designations")
    @Expose
    private String designations;
    @SerializedName("districts")
    @Expose
    private String districts;
    @SerializedName("educations")
    @Expose
    private String educations;
    @SerializedName("local_community")
    @Expose
    private String localCommunity;
    @SerializedName("native")
    @Expose
    private String _native;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("relations")
    @Expose
    private String relations;
    @SerializedName("states")
    @Expose
    private String states;
    @SerializedName("sub_casts")
    @Expose
    private String subCasts;
    @SerializedName("sub_community")
    @Expose
    private String subCommunity;

    @SerializedName("gotra")
    @Expose
    private String gotra;

    public String getBusinessCategories() {
        return businessCategories;
    }

    public void setBusinessCategories(String businessCategories) {
        this.businessCategories = businessCategories;
    }

    public String getBusinessSubCategories() {
        return businessSubCategories;
    }

    public void setBusinessSubCategories(String businessSubCategories) {
        this.businessSubCategories = businessSubCategories;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getCommittees() {
        return committees;
    }

    public void setCommittees(String committees) {
        this.committees = committees;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public String getDesignations() {
        return designations;
    }

    public void setDesignations(String designations) {
        this.designations = designations;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getEducations() {
        return educations;
    }

    public void setEducations(String educations) {
        this.educations = educations;
    }

    public String getLocalCommunity() {
        return localCommunity;
    }

    public void setLocalCommunity(String localCommunity) {
        this.localCommunity = localCommunity;
    }

    public String getNative() {
        return _native;
    }

    public void setNative(String _native) {
        this._native = _native;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getSubCasts() {
        return subCasts;
    }

    public void setSubCasts(String subCasts) {
        this.subCasts = subCasts;
    }

    public String getSubCommunity() {
        return subCommunity;
    }

    public void setSubCommunity(String subCommunity) {
        this.subCommunity = subCommunity;
    }

    public String getGotra() {
        return gotra;
    }

    public void setGotra(String gotra) {
        this.gotra = gotra;
    }
}
