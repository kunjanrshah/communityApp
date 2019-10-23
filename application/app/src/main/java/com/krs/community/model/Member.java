
package com.krs.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("head_id")
    @Expose
    private String headId;
    @SerializedName("member_code")
    @Expose
    private String memberCode;
    @SerializedName("relation_id")
    @Expose
    private String relationId;
    @SerializedName("sub_community_id")
    @Expose
    private String subCommunityId;
    @SerializedName("local_community_id")
    @Expose
    private String localCommunityId;
    @SerializedName("committee_id")
    @Expose
    private String committeeId;
    @SerializedName("designation_id")
    @Expose
    private String designationId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("sub_cast_id")
    @Expose
    private String subCastId;
    @SerializedName("email_address")
    @Expose
    private String emailAddress;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("plain_password")
    @Expose
    private Object plainPassword;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("local_address")
    @Expose
    private String localAddress;
    @SerializedName("city_id")
    @Expose
    private Object cityId;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("matrimony")
    @Expose
    private String matrimony;
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    @SerializedName("birth_time")
    @Expose
    private String birthTime;
    @SerializedName("birth_place")
    @Expose
    private String birthPlace;
    @SerializedName("distinct_id")
    @Expose
    private String distinctId;
    @SerializedName("native_place_id")
    @Expose
    private String nativePlaceId;
    @SerializedName("blood_group")
    @Expose
    private String bloodGroup;
    @SerializedName("about_me")
    @Expose
    private String aboutMe;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("is_spect")
    @Expose
    private String isSpect;
    @SerializedName("is_mangal")
    @Expose
    private String isMangal;
    @SerializedName("is_shani")
    @Expose
    private String isShani;
    @SerializedName("hobby")
    @Expose
    private String hobby;
    @SerializedName("facebook_profile")
    @Expose
    private String facebookProfile;
    @SerializedName("expectation")
    @Expose
    private String expectation;
    @SerializedName("mosaad_id")
    @Expose
    private String mosaadId;
    @SerializedName("current_activity_id")
    @Expose
    private String currentActivityId;
    @SerializedName("marital_status")
    @Expose
    private String maritalStatus;
    @SerializedName("marriage_date")
    @Expose
    private Object marriageDate;
    @SerializedName("gotra_id")
    @Expose
    private Object gotraId;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("is_rented")
    @Expose
    private String isRented;
    @SerializedName("is_expired")
    @Expose
    private String isExpired;
    @SerializedName("expire_date")
    @Expose
    private String expireDate;
    @SerializedName("is_donor")
    @Expose
    private String isDonor;
    @SerializedName("business_category_id")
    @Expose
    private String businessCategoryId;
    @SerializedName("business_sub_category_id")
    @Expose
    private String businessSubCategoryId;
    @SerializedName("work_details")
    @Expose
    private String workDetails;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("business_address")
    @Expose
    private String businessAddress;
    @SerializedName("business_logo")
    @Expose
    private String businessLogo;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("education_id")
    @Expose
    private String educationId;
    @SerializedName("occupation_id")
    @Expose
    private String occupationId;
    @SerializedName("user_lat")
    @Expose
    private Object userLat;
    @SerializedName("user_lng")
    @Expose
    private Object userLng;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("is_location_enable")
    @Expose
    private String isLocationEnable;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("created_dt")
    @Expose
    private String createdDt;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_dt")
    @Expose
    private String updatedDt;
    @SerializedName("updated_by")
    @Expose
    private String updatedBy;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("state")
    @Expose
    private Object state;
    @SerializedName("sub_community")
    @Expose
    private String subCommunity;
    @SerializedName("local_community")
    @Expose
    private String localCommunity;
    @SerializedName("relation")
    @Expose
    private String relation;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("native")
    @Expose
    private String _native;
    @SerializedName("mossad")
    @Expose
    private Object mossad;
    @SerializedName("current_activity")
    @Expose
    private String currentActivity;
    @SerializedName("gotra")
    @Expose
    private Object gotra;
    @SerializedName("business_category")
    @Expose
    private String businessCategory;
    @SerializedName("business_sub_category")
    @Expose
    private String businessSubCategory;
    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("occupation")
    @Expose
    private String occupation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHeadId() {
        return headId;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getSubCommunityId() {
        return subCommunityId;
    }

    public void setSubCommunityId(String subCommunityId) {
        this.subCommunityId = subCommunityId;
    }

    public String getLocalCommunityId() {
        return localCommunityId;
    }

    public void setLocalCommunityId(String localCommunityId) {
        this.localCommunityId = localCommunityId;
    }

    public String getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(String committeeId) {
        this.committeeId = committeeId;
    }

    public String getDesignationId() {
        return designationId;
    }

    public void setDesignationId(String designationId) {
        this.designationId = designationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSubCastId() {
        return subCastId;
    }

    public void setSubCastId(String subCastId) {
        this.subCastId = subCastId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(Object plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public Object getCityId() {
        return cityId;
    }

    public void setCityId(Object cityId) {
        this.cityId = cityId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMatrimony() {
        return matrimony;
    }

    public void setMatrimony(String matrimony) {
        this.matrimony = matrimony;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getDistinctId() {
        return distinctId;
    }

    public void setDistinctId(String distinctId) {
        this.distinctId = distinctId;
    }

    public String getNativePlaceId() {
        return nativePlaceId;
    }

    public void setNativePlaceId(String nativePlaceId) {
        this.nativePlaceId = nativePlaceId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getIsSpect() {
        return isSpect;
    }

    public void setIsSpect(String isSpect) {
        this.isSpect = isSpect;
    }

    public String getIsMangal() {
        return isMangal;
    }

    public void setIsMangal(String isMangal) {
        this.isMangal = isMangal;
    }

    public String getIsShani() {
        return isShani;
    }

    public void setIsShani(String isShani) {
        this.isShani = isShani;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getFacebookProfile() {
        return facebookProfile;
    }

    public void setFacebookProfile(String facebookProfile) {
        this.facebookProfile = facebookProfile;
    }

    public String getExpectation() {
        return expectation;
    }

    public void setExpectation(String expectation) {
        this.expectation = expectation;
    }

    public String getMosaadId() {
        return mosaadId;
    }

    public void setMosaadId(String mosaadId) {
        this.mosaadId = mosaadId;
    }

    public String getCurrentActivityId() {
        return currentActivityId;
    }

    public void setCurrentActivityId(String currentActivityId) {
        this.currentActivityId = currentActivityId;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Object getMarriageDate() {
        return marriageDate;
    }

    public void setMarriageDate(Object marriageDate) {
        this.marriageDate = marriageDate;
    }

    public Object getGotraId() {
        return gotraId;
    }

    public void setGotraId(Object gotraId) {
        this.gotraId = gotraId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIsRented() {
        return isRented;
    }

    public void setIsRented(String isRented) {
        this.isRented = isRented;
    }

    public String getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getIsDonor() {
        return isDonor;
    }

    public void setIsDonor(String isDonor) {
        this.isDonor = isDonor;
    }

    public String getBusinessCategoryId() {
        return businessCategoryId;
    }

    public void setBusinessCategoryId(String businessCategoryId) {
        this.businessCategoryId = businessCategoryId;
    }

    public String getBusinessSubCategoryId() {
        return businessSubCategoryId;
    }

    public void setBusinessSubCategoryId(String businessSubCategoryId) {
        this.businessSubCategoryId = businessSubCategoryId;
    }

    public String getWorkDetails() {
        return workDetails;
    }

    public void setWorkDetails(String workDetails) {
        this.workDetails = workDetails;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEducationId() {
        return educationId;
    }

    public void setEducationId(String educationId) {
        this.educationId = educationId;
    }

    public String getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(String occupationId) {
        this.occupationId = occupationId;
    }

    public Object getUserLat() {
        return userLat;
    }

    public void setUserLat(Object userLat) {
        this.userLat = userLat;
    }

    public Object getUserLng() {
        return userLng;
    }

    public void setUserLng(Object userLng) {
        this.userLng = userLng;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getIsLocationEnable() {
        return isLocationEnable;
    }

    public void setIsLocationEnable(String isLocationEnable) {
        this.isLocationEnable = isLocationEnable;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public String getSubCommunity() {
        return subCommunity;
    }

    public void setSubCommunity(String subCommunity) {
        this.subCommunity = subCommunity;
    }

    public String getLocalCommunity() {
        return localCommunity;
    }

    public void setLocalCommunity(String localCommunity) {
        this.localCommunity = localCommunity;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getNative() {
        return _native;
    }

    public void setNative(String _native) {
        this._native = _native;
    }

    public Object getMossad() {
        return mossad;
    }

    public void setMossad(Object mossad) {
        this.mossad = mossad;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public Object getGotra() {
        return gotra;
    }

    public void setGotra(Object gotra) {
        this.gotra = gotra;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getBusinessSubCategory() {
        return businessSubCategory;
    }

    public void setBusinessSubCategory(String businessSubCategory) {
        this.businessSubCategory = businessSubCategory;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

}
