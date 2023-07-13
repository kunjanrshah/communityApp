package com.krs.community.utils;

import com.krs.community.BuildConfig;

public class AppConstants {

    public static final String TRANSITION_CARD = "card_transition_";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String API_KEY_VALUE = "q1fgdfggfw2e2rt3y5u6i8iug12fh123yhhddaf";
    public static final String PREF_NAME = "Vastipatrak";
    public static final String INSERT = "insert";
    public static final String VERSION = "version";
    //public static final String APPLICATION_BASE_URL = "https://muslimghanchi.samajapp.in/API/";
    public static final String APPLICATION_BASE_URL = BuildConfig.BASE_URL;
    public static final String UPLOAD_DOCUMENT = APPLICATION_BASE_URL + "UploadFiles";

    public interface UrlPath {
        String GET_SUB_COMM = "GetSubCommunity";
        String GET_LOCAL_COMM = "GetLocalCommunity";
        String GET_LIST_LOCAL_COMM = "GetListLocalCommunity";
        String GET_LIST_CITY = "GetListCity";
        String GET_LIST_BUSINESS_SUB_CATEGORY = "GetListBusinessSubCategory";
        String GET_RELATIONS = "GetRelations";
        String GET_STATE = "GetState";
        String GET_CITIES = "GetCities";
        String GET_SUBCASTE = "GetSurname";
        String GET_NATIVE = "GetNative";
        String GET_GOTRA = "getGotra";
        String GET_EDUCATION = "getEducation";
        String GET_ACTIVITY = "getActivity";
        String GET_BUSINESS_CATEGORY = "getBusinessCategory";
        String GET_BUSINESS_SUB_CATEGORY = "GetBusinessSubCategory";
        String GET_OCCUPATION = "GetOccupation";
        String GET_COMMITTEE = "GetCommittee";
        String GET_DESIGNATION = "GetDesignation";
        String SEARCH_BY_KEYWORDS = "SmartSearch";
        String CHANGE_STATUS = "StatusChange";
        String CHANGE_ROLE = "RoleChange";
        String GET_CONTACT_LIST = "GetContactList";
        String GET_UPDATED_VERSION = "getUpdatedVersion";
        String GET_INACTIVE_USERS = "getInactiveUsers";
        String GET_DOCUMENT = "GetFiles";
        String GET_COMMITTEE_USERS = "SearchCommitteeUsers";
        String ADD_MEMBER = "AddMember";
        String DELETE_MEMBER = "DeleteMember";
        String GET_STATISTICS = "GetStatistics";
        String UPDATE_PROFILE = "EditProfile";
        String GET_SMART_FILTER = "SmartFilter";
        String GET_USERS_BYDATE = "GetUsersByDate";
        String GET_EVENTS = "getEvents";
        String GET_SHARED_PROFILE = "GetSharedProfile";
        String GET_SEARCH_NEAR_BY = "GetNearByUsers";
        String GET_SEARCH_BY_CITY = "SearchByCity";
        String GET_FAMILY_MEMBER = "GetFamilyMembers";
        String GET_REGISTER = "Registration";
        String GET_LOGIN = "Login";
        String UPLOAD_PROFILE_IMAGE = "Upload";
        String CREATE_EVENT = "CreateEvent";
        String GET_FORGOT_PASS = "ForgotPassword";
        String GET_CHANGE_PASS = "ChangePassword";
        String INNER_LOGIN = "InnerLogin";
        String INNER_LOGOUT = "InnerLogout";
        String SET_REMINDER = "SetReminder";
        String GET_USER_PROFILE = "GetUserProfile";
        String GET_USER_STATUS = "GetUserActivityStatus";
        String GET_MASTER_UPDATE_COUNTS = "GetMasterUpdateCounts";
        String SEND_MAIL = "SendMail";
        String UPLOAD_FILES = "UploadFiles";

    }

    public interface TimeOut {
        int IMAGE_UPLOAD_CONNECTION_TIMEOUT = 120;
        int IMAGE_UPLOAD_SOCKET_TIMEOUT = 120;
        int SOCKET_TIME_OUT = 60;
        int CONNECTION_TIME_OUT = 60;
    }

    public interface ErrorClass {
        String CODE = "code";
        String STATUS = "status";
        String MESSAGE = "message";
        String DEVELOPER_MESSAGE = "developerMessage";
    }


    public static class UserRegister {
        String is_admin;
        String first_name;
        String sub_cast_id;
        String email_address;
        String mobile;
        String plain_password;
        String state_id;
        String city_id;
        String native_place_id;
        String address;
        String sub_community_id;
        String local_community_id;
        String gender;
        String father_name;
        String birth_date;
        String head_id;
        String profile_pic;

        public String getProfilePic() {
            return profile_pic;
        }

        public void setProfilePic(String profilePic) {
            this.profile_pic = profilePic;
        }

        public String getFather() {
            return father_name;
        }

        public void setFather(String father) {
            this.father_name = father;
        }

        public String getBirthDate() {
            return birth_date;
        }

        public void setBirthdate(String birthdate) {
            this.birth_date = birthdate;
        }

        public String getSub_cast_id() {
            return sub_cast_id;
        }

        public void setSub_cast_id(String sub_cast_id) {
            this.sub_cast_id = sub_cast_id;
        }

        public String getHeadId() {
            return head_id;
        }

        public void setHeadId(String head_id) {
            this.head_id = head_id;
        }

        public String getNativePlaceId() {
            return native_place_id;
        }

        public void setNativePlaceId(String native_place_id) {
            this.native_place_id = native_place_id;
        }

        public String getIsAdmin() {
            return is_admin;
        }

        public void setIsAdmin(String is_admin) {
            this.is_admin = is_admin;
        }

        public String getProfile_password() {
            return plain_password;
        }

        public void setProfile_password(String profile_password) {
            this.plain_password = profile_password;
        }

        public String getState_id() {
            return state_id;
        }

        public void setState_id(String state_id) {
            this.state_id = state_id;
        }

        public String getSub_community_id() {
            return sub_community_id;
        }

        public void setSub_community_id(String sub_community_id) {
            this.sub_community_id = sub_community_id;
        }

        public String getLocal_community_id() {
            return local_community_id;
        }

        public void setLocal_community_id(String local_community_id) {
            this.local_community_id = local_community_id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getEmail_address() {
            return email_address;
        }

        public void setEmail_address(String email_address) {
            this.email_address = email_address;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class LoginRequest {
        String username;
        String hashcode;
        String login_type;
        String password;

        public String getLogin_type() {
            return login_type;
        }

        public void setLogin_type(String login_type) {
            this.login_type = login_type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHashcode() {
            return hashcode;
        }

        public void setHashcode(String hashkey) {
            this.hashcode = hashkey;
        }
    }
}
