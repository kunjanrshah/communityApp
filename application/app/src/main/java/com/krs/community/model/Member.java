
package com.krs.community.model;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity
public class Member implements Serializable {

    @SerializedName("login_status")
    @Expose
    private int loginStatus=0;

    @SerializedName("members_count")
    @Expose
    private int membersCount = 0;

    @SerializedName("last_login")
    @Expose
    private String lastLogin="";

    @SerializedName("online_status")
    @Expose
    private int onlineStatus=0;

    @SerializedName("sharing_id")
    @Expose
    private String sharingId="";

    @SerializedName("matched")
    @Expose
    private String matched="";

    @SerializedName("isImportant")
    @Expose
    private boolean isImportant;

    @SerializedName("nearBy")
    @Expose
    private String nearBy="";
    @SerializedName("access_token")
    @Expose
    private String accessToken="";
    @SerializedName("profile_completed")
    @Expose
    private String profileCompleted="0%";

    @SerializedName("profile_password")
    @Expose
    private String profilePassword="";

    @SerializedName("distance")
    @Expose
    private String distance="";

    @SerializedName("head_sub_cast_id")
    @Expose
    private String head_sub_cast_id="";
    @SerializedName("head_name")
    @Expose
    private String head_name="";

    @SerializedName("id")
    @Expose
    private String id="";
    @SerializedName("role")
    @Expose
    private String role="";
    @SerializedName("head_id")
    @Expose
    private String headId="0";
    @SerializedName("member_code")
    @Expose
    private String memberCode="";
    @SerializedName("email_address")
    @Expose
    private String emailAddress="";
    @SerializedName("mobile")
    @Expose
    private String mobile="";
    @SerializedName("plain_password")
    @Expose
    private String plainPassword="";
    @SerializedName("password")
    @Expose
    private String password="";
    @SerializedName("relation_id")
    @Expose
    private String relationId="";

    @SerializedName("relation")
    @Expose
    private String relation="";

    @SerializedName("sub_community_id")
    @Expose
    private String subCommunityId="";
    @SerializedName("local_community_id")
    @Expose
    private String localCommunityId="";
    @SerializedName("committee_id")
    @Expose
    private String committeeId="";
    @SerializedName("designation_id")
    @Expose
    private String designationId="";
    @SerializedName("first_name")
    @Expose
    private String firstName="";
    @SerializedName("last_name")
    @Expose
    private String lastName="";
    @SerializedName("father_name")
    @Expose
    private String fatherName="";
    @SerializedName("mother_name")
    @Expose
    private String motherName="";
    @SerializedName("sub_cast_id")
    @Expose
    private String subCastId="";
    @SerializedName("status")
    @Expose
    private String status="";
    @SerializedName("gender")
    @Expose
    private String gender="";
    @SerializedName("address")
    @Expose
    private String address="";
    @SerializedName("local_address")
    @Expose
    private String localAddress="";
    @SerializedName("city_id")
    @Expose
    private String cityId="";
    @SerializedName("city")
    @Expose
    private String city="";
    @SerializedName("state_id")
    @Expose
    private String stateId="";
    @SerializedName("area")
    @Expose
    private String area="";
    @SerializedName("pincode")
    @Expose
    private String pincode="";
    @SerializedName("phone")
    @Expose
    private String phone="";
    @SerializedName("matrimony")
    @Expose
    private String matrimony="NO";
    @SerializedName("birth_date")
    @Expose
    private String birthDate="";
    @SerializedName("birth_time")
    @Expose
    private String birthTime="";
    @SerializedName("birth_place")
    @Expose
    private String birthPlace="";
    @SerializedName("distinct_id")
    @Expose
    private String distinctId="";
    @SerializedName("native_place_id")
    @Expose
    private String nativePlaceId="";
    @SerializedName("blood_group")
    @Expose
    private String bloodGroup="";
    @SerializedName("about_me")
    @Expose
    private String aboutMe="";
    @SerializedName("weight")
    @Expose
    private String weight="";
    @SerializedName("height")
    @Expose
    private String height="";
    @SerializedName("is_spect")
    @Expose
    private String isSpect="0";
    @SerializedName("is_mangal")
    @Expose
    private String isMangal="0";
    @SerializedName("is_shani")
    @Expose
    private String isShani="0";
    @SerializedName("hobby")
    @Expose
    private String hobby="";
    @SerializedName("facebook_profile")
    @Expose
    private String facebookProfile="";
    @SerializedName("expectation")
    @Expose
    private String expectation="";
    @SerializedName("mosaad_id")
    @Expose
    private String mosaadId="";
    @SerializedName("current_activity_id")
    @Expose
    private String currentActivityId="";
    @SerializedName("marital_status")
    @Expose
    private String maritalStatus="";
    @SerializedName("marriage_date")
    @Expose
    private String marriageDate="";
    @SerializedName("gotra_id")
    @Expose
    private String gotraId="";
    @SerializedName("profile_pic")
    @Expose
    private String profilePic="";
    @SerializedName("region")
    @Expose
    private String region="";
    @SerializedName("is_rented")
    @Expose
    private String isRented="0";
    @SerializedName("is_expired")
    @Expose
    private String isExpired="";
    @SerializedName("expire_date")
    @Expose
    private String expireDate="";
    @SerializedName("is_donor")
    @Expose
    private String isDonor="0";
    @SerializedName("business_category_id")
    @Expose
    private String businessCategoryId="";
    @SerializedName("business_sub_category_id")
    @Expose
    private String businessSubCategoryId="";
    @SerializedName("work_details")
    @Expose
    private String workDetails="";
    @SerializedName("company_name")
    @Expose
    private String companyName="";
    @SerializedName("business_address")
    @Expose
    private String businessAddress="";
    @SerializedName("business_logo")
    @Expose
    private String businessLogo="";
    @SerializedName("website")
    @Expose
    private String website="";
    @SerializedName("education_id")
    @Expose
    private String educationId="";
    @SerializedName("occupation_id")
    @Expose
    private String occupationId="";
    @SerializedName("user_lat")
    @Expose
    private String userLat="";
    @SerializedName("user_lng")
    @Expose
    private String userLng="";
    @SerializedName("home_lat")
    @Expose
    private String homeLat="";
    @SerializedName("home_lng")
    @Expose
    private String homeLng="";
    @SerializedName("office_lat")
    @Expose
    private String officeLat="";
    @SerializedName("office_lng")
    @Expose
    private String officeLng="";
    @SerializedName("is_location_enable")
    @Expose
    private String isLocationEnable="0";

    @SerializedName("updated_dt")
    @Expose
    private String updatedDt="0";

    @SerializedName("matches")
    @Expose
    private List<String> matches = null;

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(String profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getSubCastId() {
        return subCastId;
    }

    public void setSubCastId(String subCastId) {
        this.subCastId = subCastId;
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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
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

    public String getMarriageDate() {
        return marriageDate;
    }

    public void setMarriageDate(String marriageDate) {
        this.marriageDate = marriageDate;
    }

    public String getGotraId() {
        return gotraId;
    }

    public void setGotraId(String gotraId) {
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

    public String getIsLocationEnable() {
        return isLocationEnable;
    }

    public void setIsLocationEnable(String isLocationEnable) {
        this.isLocationEnable = isLocationEnable;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

    public String getNearBy() {
        return nearBy;
    }

    public void setNearBy(String nearBy) {
        this.nearBy = nearBy;
    }

    public List<String> getMatches() {
        return matches;
    }

    public void setMatches(List<String> matches) {
        this.matches = matches;
    }

    public String getHead_name() {
        return head_name;
    }

    public void setHead_name(String head_name) {
        this.head_name = head_name;
    }

    public String getHead_sub_cast_id() {
        return head_sub_cast_id;
    }

    public void setHead_sub_cast_id(String head_sub_cast_id) {
        this.head_sub_cast_id = head_sub_cast_id;
    }

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        this.matched = matched;
    }

    public String getSharingId() {
        return sharingId;
    }

    public void setSharingId(String sharingId) {
        this.sharingId = sharingId;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getProfilePassword() {
        return profilePassword;
    }

    public void setProfilePassword(String profilePassword) {
        this.profilePassword = profilePassword;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }
}
