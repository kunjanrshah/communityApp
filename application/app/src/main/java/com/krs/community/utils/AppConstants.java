package com.krs.community.utils;

import android.Manifest;

public class AppConstants {

    public static final String[] INIT_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS};
    public static final String[] CALL_CAMARA = {Manifest.permission.CAMERA};
    public static final int CAMARA_REQUEST = 4;
    public static final int INIT_REQUEST = 1;

    public static final String EXTRA_COORDINATES = "coordinates";
    public static final String EXTRA_POSITION = "position";

    public static final String TRANSITION_CARD = "card_transition_";
    public static final String TRANSITION_TOOLBAR = "toolbar_transition";


    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    public static final int INIT_TIMEOUT = 15000;
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final float DEFAULT_BACKOFF_MULT = 2f;

    public static final String LAN = "lan";

    public static final String YOUTUBE_API_KEY = "AIzaSyBOkoXTYsY32OQtLTxidxci5R3Zml84oUY";

    public static final String PERSONAL = "PERSONAL";
    public static final String BUSINESS = " PROFESSIONAL ";
    public static final String FAMILY = "FAMILY";
    public static final String FAMILY_TREE = "FAMILY TREE";
    public static final String RELATIVES = " RELATIVES ";

    public static final String _PERSONAL = "     PERSONAL     ";
    public static final String _BUSINESS = " PROFESSIONAL";
    public static final String _FAMILY = "      FAMILY      ";

    public static final String NOTIFICATION = "notification";
    public static final int NonActive = 0;
    public static final String AdminControl = "AdminControl";

    public static final String ERROR_CODE = "errorcode";
    public static final String ERROR_13 = "-13";

    public static final String GIRLS = "     GIRLS  ";
    public static final String BOYS_P1 = "  BOYS PART-1  ";
    public static final String BOYS_P2 = "   BOYS PART-2";

    public static final String SHARE_USER_IDS = "share_user_ids";
    public static final String IS_SHARE = "is_share";
    public static final String CAN_SHARE = "can_share";
    public static final String API_KEY = "api_key";
    public static final String ALLOW_GET_DATA = "allow_get_data";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String DEVICE_ID = "int_udid";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String RESPONSE_DATA = "response_data";
    public static final String NEAR_BY = "nearBy";
    public static final String DISTANCE = "distance";

    public static final String DATE = "date";
    public static final String BIRTH_DATE = "birth_date";
    public static final String CHILD_BIRTH_DATE = "child_birth_date";
    public static final String WIFE_BIRTH_DATE = "spouse_birth_date";
    public static final String MARRIAGE_DATE = "marriage_date";
    public static final String RELATION = "relation";
    public static final String TO_USER_ID = "to_user_id";
    public static final String RELATIONSHIP_STATUS = "relationship_status";
    public static final String RELATIONSHIP_ID = "relationship_id";
    public static final String PAGE = "page";
    public static final String API_KEY_VALUE = "q1fgdfggfw2e2rt3y5u6i8iug12fh123yhhddaf";
    public static final String DEVICE_TYPE_VALUE = "Android";
    public static final String PREF_NAME = "Vastipatrak";
    public static final String PREF_WELCOME = "Welcome";
    public static final String PREF_FILTER = "Vastipatrak_Filter";
    public static final String PREF_TOKEN = "Pref_Token";
    public static final String SCREEN = "screen";
    public static final String SEARCH_FRAGMENT = "SearchFragment";
    public static final String IS_UPDATE = "is_update";
    public static final String IS_RESET = "is_reset";
    public static final String USER_ID = "user_id";
    public static final String IS_HOME = "is_home";
    public static final String UPDATE_USER_ID = "update_user_id";
    public static final String EVENT_DATE = "event_date";
    public static final String NO_CONNECTION = "No internet connection!";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    public static final String TO_FIRST_NAME = "to_first_name";
    public static final String TO_LAST_NAME = "to_last_name";
    public static final String FROM_FIRST_NAME = "from_first_name";
    public static final String FROM_LAST_NAME = "from_last_name";

    public static final String FROM_PROFILE_PIC = "from_profile_pic";
    public static final String TO_PROFILE_PIC = "to_profile_pic";

    public static final String REQUESTED = "REQUESTED";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";

    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "hashcode";
    public static final String INSERT = "insert";
    public static final String VERSION = "version";

    public static final String PLAIN_PASSWORD = "plain_password";

    public static final String REPEAT_PASSWORD = "repeat_password";
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String TOTAL_RECORDS = "totalRecords";
    public static final String TRUE = "true";
    public static final String USERNAME = "username";
    public static final String IS_SOCIAL = "is_social";
    public static final String MOBILE = "mobile";
    public static final String DATA = "data";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String KM = "km";
    public static final String USER_LAT = "user_lat";
    public static final String USER_LNG = "user_lng";
    public static final String CURR_LAT = "curr_lat";
    public static final String CURR_LNG = "curr_lng";
    public static final String HOME_LAT = "home_lat";
    public static final String HOME_LNG = "home_lng";
    public static final String OFFICE_LAT = "office_lat";
    public static final String OFFICE_LNG = "office_lng";
    public static final String LOCATION_TYPE = "location_type";
    public static final String MYPROFILE_SP = "myprofile";
    public static final String OFFICE_ADDRESS = "office_address";
    public static final String ADDRESS = "address";
    public static final String SUB_CAST = "sub_cast";
    public static final String EKDO = "ekdo";
    public static final String OCCUPATION = "occupation";
    public static final String OFFICE_MOBILE = "office_mobile";
    public static final String WORK = "work";
    public static final String QUERY = "query";
    public static final String QUERY_STRING = "query_string";
    public static final String PUSH_MESSAGE = "push_message";
    public static final String ID = "id";

    public static final String BDATE_REMINDER_ID = "bdate_reminder_id";
    public static final String SPOUSE_BDATE_REMINDER_ID = "spouse_bdate_reminder_id";
    public static final String MDATE_REMINDER_ID = "mdate_reminder_id";

    public static final String IDList = "idList";
    public static final String STATUS = "status";
    public static final String PROFILE_PIC_URL = "profile_pic_url";
    public static final int sCorner = 25;
    public static final int sMargin = 1;
    public static final String FATHER_NAME = "father_name";
    public static final String MOTHER_NAME = "mother_name";
    public static final String NATIVE_PLACE = "native_place";
    public static final String FROM_BIRTH_DATE = "from_birth_date";
    public static final String TO_BIRTH_DATE = "to_birth_date";
    public static final String BIRTH_TIME = "birth_time";
    public static final String BIRTH_PLACE = "birth_place";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String CHILD_BLOOD_GROUP = "child_blood_group";
    public static final String PHONE = "phone";
    public static final String GENDER = "gender";
    public static final String CHILD_GENDER = "child_gender";
    public static final String CHILD_MOBILE = "child_mobile";
    public static final String GOTRA = "gotra";
    public static final String IS_LOCATION_ENABLE = "is_location_enable";
    public static final String UPDATED_TIME = "updated_time";
    public static final String ROLE = "role";
    public static final String SYNC_TIME = "sync_time";
    public static final String CITY = "city";
    public static final String TITLE_BLOOD_GROUP = "Blood Group";
    public static final String TITLE_GOTRA = "Gotra";
    public static final String TITLE_NATIVE = "Native";
    public static final String TITLE_EDUCATION = "Education";
    public static final String TITLE_CHILD_BLOOD_GROUP = "Child BG";
    public static final String TITLE_SPOUSE_BLOOD_GROUP = "SPOUSE BG";
    public static final String A_POSITIVE = "A +VE";
    public static final String A_NAGATIVE = "A -VE";
    public static final String B_POSITIVE = "B +VE";
    public static final String B_NAGATIVE = "B -VE";
    public static final String O_POSITIVE = "O +VE";
    public static final String O_NAGATIVE = "O -VE";
    public static final String AB_POSITIVE = "AB +VE";
    public static final String AB_NAGATIVE = "AB -VE";
    public static final String EDUCATION = "education";
    public static final String PROFILE_PIC = "profile_pic";
    public static final String PROFILE_PIC_HASH = "profile_pic_hash";
    public static final String IMG_MOTHER = "img_mother";
    public static final String IMG_FATHER = "img_father";
    public static final String SPOUSE_NAME = "spouse_name";

    public static final String SPOUSE_BG = "spouse_blood_group";
    public static final String SPOUSE_EDU = "spouse_education";
    public static final String SPOUSE_START_AGE = "spouse_start_age";
    public static final String SPOUSE_END_AGE = "spouse_end_age";
    public static final String CHILD_START_AGE = "child_start_age";
    public static final String CHILD_END_AGE = "child_end_age";
    public static final String USER_START_AGE = "user_start_age";
    public static final String USER_END_AGE = "user_end_age";


    public static final String SPOUSE_NATIVE = "spouse_native_place";
    public static final String SPOUSE_BDATE = "spouse_birth_place";
    public static final String SPOUSE_MOBILE = "spouse_mobile";
    public static final String FROM_MARRIAGE_DATE = "from_marriage_date";
    public static final String TO_MARRIAGE_DATE = "to_marriage_date";
    public static final String MARRIED = "1";
    public static final String UNMARRIED = "0";
    public static final String SPOUSE_FATHER_NAME = "spouse_father_name";
    public static final String SPOUSE_MOTHER_NAME = "spouse_mother_name";
    public static final int sBorder = 5;
    public static final String sColor = "#FFC0CB";
    //public static final long LOCATION_INTERVAL = 1000 * 30;
    public static final String CHILDS = "childs";
    public static final String CHILD_DELETE = "delete";
    public static final String CHILD_ID = "id";
    public static final String _CHILD_ID = "child_id";
    public static final String CHILD_NAME = "child_name";

    public static final String CHILD_BDATE_REMINDER_ID = "child_bdate_reminder_id";


    public static final String familyTree = "familyTree";

    public static final String CHILD_BDAY = "child_bday";
    public static final String FROM_CHILD_BDAY = "from_child_bday";
    public static final String TO_CHILD_BDAY = "to_child_bday";
    public static final String CHILD_BTIME = "birth_time";
    public static final String CHILD_BPLACE = "child_birth_place";
    public static final String CHILD_MARRIAGE = "isMarried";
    public static final String IS_INTERESTED = "is_interested";
    public static final String IS_MARRIED = "isMarried";
    public static final String CHILD_EDU = "child_edu";
    public static final String CHILD_WORK = "child_work";
    public static final String FragmentSp = "fragment";
    public static final String CHILD_IMAGE = "child_image";
    public static final String IMG_SPOUSE = "img_spouse";
    public static final String IMG_SMOTHER = "img_smother";
    public static final String IMG_SFATHER = "img_sfather";
    public static final String PROFILE_ID = "profile_id";
    public static final String REMINDER_DATE = "reminder_date";
    public static final String REMINDER_TYPE = "reminder_type";
    public static final String REMINDER_ID = "reminder_id";
    public static final String TBTN_SHARE = "tbtn_share";
    public static final String TBTN_SYNC = "tbtn_sync";
    public static final String IMG_FATHER_URL = "img_father_url";
    public static final String IMG_MOTHER_URL = "img_mother_url";
    public static final String IMG_SPOUSE_URL = "img_spouse_url";
    public static final String IMG_SFATHER_URL = "img_sfather_url";
    public static final String IMG_SMOTHER_URL = "img_smother_url";
    public static final String CHILD_IMAGE_URL = "child_image_url";

    public static final String APPLICATION_BASE_URL = "https://www.muslimghanchisamaj.in/API/";

    //private static final String BASE_URL = "http://www.superbinstruments.com/directory-dev";
      private static final String BASE_URL = "https://www.superbinstruments.com/yadav";
    public static final String LOGIN_URL = BASE_URL + "/API/login";
    public static final String SIGNUP_URL = BASE_URL + "/API/Userreg";
    public static final String FORGOT_PASSWORD_URL = BASE_URL + "/API/forgotPassword";
    public static final String ADVANCE_SEARCH_URL = BASE_URL + "/API/searchUsers";
    public static final String GLOBAL_SEARCH_URL = BASE_URL + "/API/globalSearch";
    public static final String CHANGE_PASSWORD_URL = BASE_URL + "/API/changePassword";
    public static final String LOGOUT_URL = BASE_URL + "/API/logout";
    public static final String DELETE_URL = BASE_URL + "/API/delete";
    public static final String STATUS_URL = BASE_URL + "/API/StatusChange";
    public static final String SYNC_URL = BASE_URL + "/API/sync";
    public static final String INACTIVES_URL = BASE_URL + "/API/getInactiveUsers";
    public static final String PROFILE_URL = BASE_URL + "/API/profile";
    public static final String EVENTS_URL = BASE_URL + "/API/getEvents";
    public static final String GET_CITIES_URL = BASE_URL + "/API/getCities";
    public static final String CHANGE_ROLE_URL = BASE_URL + "/API/changeRole";
    //public static final String GET_GOTRA_URL = BASE_URL + "/API/getGotra";
    public static final String SHARED_USERS_URL = BASE_URL + "/API/shareUsers";
    public static final String NEAR_BY_USERS_URL = BASE_URL + "/API/getNearByUsers";
    public static final String SEND_REQUEST_URL = BASE_URL + "/API/sendRequest";
    public static final String REQUEST_ACTION_URL = BASE_URL + "/API/requestAction";
    public static final String GET_RELATIONS_URL = BASE_URL + "/API/getRelations";
    public static final String SET_REMINDER_URL = BASE_URL + "/API/setReminder";
    public static final String SET_TREE_URL = BASE_URL + "/API/saveTree";
    public static final String SET_UPDATED_VERSION_URL = BASE_URL + "/API/getUpdatedVersion";
    public static final String GET_MASTER_DATA_URL = BASE_URL + "/API/getMasterData";
    public static final String GET_USERS_BY_DATE_URL = BASE_URL + "/API/getUsersByDate";
    public static String DEVICE_ID_VALUE = "";

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
        String GET_INACTIVE_USERS = "getInactiveUsers";
        String ADD_MEMBER = "AddMember";
        String DELETE_MEMBER = "DeleteMember";
        String GET_STATISTICS = "GetStatistics";
        String UPDATE_PROFILE = "EditProfile";
        String GET_SMART_FILTER = "SmartFilter";
        String GET_USERS_BYDATE = "GetUsersByDate";
        String GET_EVENTS = "getEvents";
        String GET_SEARCH_NEAR_BY = "GetNearByUsers";
        String GET_SEARCH_BY_CITY = "SearchByCity";
        String GET_FAMILY_MEMBER = "GetFamilyMembers";
        String GET_REGISTER = "Registration";
        String GET_LOGIN = "Login";
        String GET_FORGOT_PASS = "ForgotPassword";
        String GET_CHANGE_PASS = "ChangePassword";
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


    public static class UserRegister{
        String profile_pic;
        String first_name;
        String sub_cast_id;
        String email_address;
        String mobile;
        String plain_password;
        String state_id;
        String city_id;
        String address;
        String sub_community_id;
        String local_community_id;

        public String getLocal_community_id() {
            return local_community_id;
        }

        public void setLocal_community_id(String local_community_id) {
            this.local_community_id = local_community_id;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getSub_cast_id() {
            return sub_cast_id;
        }

        public void setSub_cast_id(String sub_cast_id) {
            this.sub_cast_id = sub_cast_id;
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

        public String getPlain_password() {
            return plain_password;
        }

        public void setPlain_password(String plain_password) {
            this.plain_password = plain_password;
        }

        public String getState_id() {
            return state_id;
        }

        public void setState_id(String state_id) {
            this.state_id = state_id;
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

        public String getSub_community_id() {
            return sub_community_id;
        }

        public void setSub_community_id(String sub_community_id) {
            this.sub_community_id = sub_community_id;
        }
    }

    public static class LoginRequest {
        String username;
        String hashcode;
        String login_type;

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

        public String getHashcode() {
            return hashcode;
        }

        public void setHashcode(String hashkey) {
            this.hashcode = hashkey;
        }
    }
}
