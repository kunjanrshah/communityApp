
package com.krs.vastipatrak.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Data extends RealmObject{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("father_name")
    @Expose
    private String fatherName;
    @SerializedName("mother_name")
    @Expose
    private String motherName;
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    @SerializedName("sibling_name")
    @Expose
    private String siblingName;
    @SerializedName("email_address")
    @Expose
    private String emailAddress;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("blood_group")
    @Expose
    private String bloodGroup;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("gotra")
    @Expose
    private String gotra;
    @SerializedName("sub_cast")
    @Expose
    private String subCast;
    @SerializedName("ekdo")
    @Expose
    private String ekdo;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("marital_status")
    @Expose
    private String maritalStatus;
    @SerializedName("birth_place")
    @Expose
    private String birthPlace;
    @SerializedName("native_place")
    @Expose
    private String nativePlace;
    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("birth_time")
    @Expose
    private String birthTime;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("office_address")
    @Expose
    private String officeAddress;
    @SerializedName("office_lat")
    @Expose
    private String officeLat;
    @SerializedName("office_lng")
    @Expose
    private String officeLng;
    @SerializedName("home_lat")
    @Expose
    private String homeLat;
    @SerializedName("home_lng")
    @Expose
    private String homeLng;
    @SerializedName("user_lat")
    @Expose
    private String userLat;
    @SerializedName("user_lng")
    @Expose
    private String userLng;
    @SerializedName("office_mobile")
    @Expose
    private String officeMobile;
    @SerializedName("spouse_name")
    @Expose
    private String spouseName;
    @SerializedName("marriage_date")
    @Expose
    private String marriageDate;
    @SerializedName("spouse_father_name")
    @Expose
    private String spouseFatherName;
    @SerializedName("spouse_mother_name")
    @Expose
    private String spouseMotherName;
    @SerializedName("img_father")
    @Expose
    private String imgFather;
    @SerializedName("img_mother")
    @Expose
    private String imgMother;
    @SerializedName("img_spouse")
    @Expose
    private String imgSpouse;
    @SerializedName("img_sfather")
    @Expose
    private String imgSfather;
    @SerializedName("img_smother")
    @Expose
    private String imgSmother;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("status")
    @Expose
    private String status;
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
    @SerializedName("plain_password")
    @Expose
    private String plainPassword;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("profile_pic_url")
    @Expose
    private String profilePicUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSiblingName() {
        return siblingName;
    }

    public void setSiblingName(String siblingName) {
        this.siblingName = siblingName;
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

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGotra() {
        return gotra;
    }

    public void setGotra(String gotra) {
        this.gotra = gotra;
    }

    public String getSubCast() {
        return subCast;
    }

    public void setSubCast(String subCast) {
        this.subCast = subCast;
    }

    public String getEkdo() {
        return ekdo;
    }

    public void setEkdo(String ekdo) {
        this.ekdo = ekdo;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
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

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getOfficeLat() {
        return officeLat;
    }

    public void setOfficeLat(String officeLat) {
        this.officeLat = officeLat;
    }

    public String getOfficeLng() {
        return officeLng;
    }

    public void setOfficeLng(String officeLng) {
        this.officeLng = officeLng;
    }

    public String getHomeLat() {
        return homeLat;
    }

    public void setHomeLat(String homeLat) {
        this.homeLat = homeLat;
    }

    public String getHomeLng() {
        return homeLng;
    }

    public void setHomeLng(String homeLng) {
        this.homeLng = homeLng;
    }

    public String getUserLat() {
        return userLat;
    }

    public void setUserLat(String userLat) {
        this.userLat = userLat;
    }

    public String getUserLng() {
        return userLng;
    }

    public void setUserLng(String userLng) {
        this.userLng = userLng;
    }

    public String getOfficeMobile() {
        return officeMobile;
    }

    public void setOfficeMobile(String officeMobile) {
        this.officeMobile = officeMobile;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getMarriageDate() {
        return marriageDate;
    }

    public void setMarriageDate(String marriageDate) {
        this.marriageDate = marriageDate;
    }

    public String getSpouseFatherName() {
        return spouseFatherName;
    }

    public void setSpouseFatherName(String spouseFatherName) {
        this.spouseFatherName = spouseFatherName;
    }

    public String getSpouseMotherName() {
        return spouseMotherName;
    }

    public void setSpouseMotherName(String spouseMotherName) {
        this.spouseMotherName = spouseMotherName;
    }

    public String getImgFather() {
        return imgFather;
    }

    public void setImgFather(String imgFather) {
        this.imgFather = imgFather;
    }

    public String getImgMother() {
        return imgMother;
    }

    public void setImgMother(String imgMother) {
        this.imgMother = imgMother;
    }

    public String getImgSpouse() {
        return imgSpouse;
    }

    public void setImgSpouse(String imgSpouse) {
        this.imgSpouse = imgSpouse;
    }

    public String getImgSfather() {
        return imgSfather;
    }

    public void setImgSfather(String imgSfather) {
        this.imgSfather = imgSfather;
    }

    public String getImgSmother() {
        return imgSmother;
    }

    public void setImgSmother(String imgSmother) {
        this.imgSmother = imgSmother;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

}
