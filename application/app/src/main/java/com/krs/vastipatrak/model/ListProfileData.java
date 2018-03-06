package com.krs.vastipatrak.model;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ListProfileData extends RealmObject{

    @PrimaryKey
    private String profile_id="";
    private String profile_pic_url = "", img_spouse_url = "", img_father_url = "", img_mother_url = "", img_sfather_url = "", img_smother_url = "",status="",first_name="",last_name="",father_name="",mother_name="",email_address="",mobile="",phone="",blood_group="",gender="",gotra="",ekdo="",native_place="",birth_place="",birth_date="",birth_time="",education="",occupation="",
            work="",address="",office_address="",office_mobile="",office_lat="",office_lng="",home_lat="",home_lng="",user_lat="",user_lng="",spouse_name="",marriage_date="",spouse_father_name="",spouse_mother_name="";
    private boolean is_location_enable;
    private String updated_time;
    private String sync_time;
    private String city;
    private String str_profile_hash;
    private String str_father_hash;
    private String str_mother_hash;
    private String str_fspouse_hash;
    private String str_mspouse_hash;
    private String str_spouse_hash;
    private String password;
    private RealmList<ListChildrenData> mListChildrenData;

    public boolean isIs_location_enable() {
        return is_location_enable;
    }

    public void setIs_location_enable(boolean is_location_enable) {
        this.is_location_enable = is_location_enable;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getStr_mother_hash() {
        return str_mother_hash;
    }

    public void setStr_mother_hash(String str_mother_hash) {
        this.str_mother_hash = str_mother_hash;
    }

    public String getStr_profile_hash() {
        return str_profile_hash;
    }

    public void setStr_profile_hash(String str_profile_hash) {
        this.str_profile_hash = str_profile_hash;
    }

    public String getStr_father_hash() {
        return str_father_hash;
    }

    public void setStr_father_hash(String str_father_hash) {
        this.str_father_hash = str_father_hash;
    }

    public String getStr_fspouse_hash() {
        return str_fspouse_hash;
    }

    public void setStr_fspouse_hash(String str_fspouse_hash) {
        this.str_fspouse_hash = str_fspouse_hash;
    }

    public String getStr_mspouse_hash() {
        return str_mspouse_hash;
    }

    public void setStr_mspouse_hash(String str_mspouse_hash) {
        this.str_mspouse_hash = str_mspouse_hash;
    }

    public String getStr_spouse_hash() {
        return str_spouse_hash;
    }

    public void setStr_spouse_hash(String str_spouse_hash) {
        this.str_spouse_hash = str_spouse_hash;
    }

    public RealmList<ListChildrenData> getmListChildrenData() {
        return mListChildrenData;
    }

    public void setmListChildrenData(RealmList<ListChildrenData> mListChildrenData) {
        this.mListChildrenData = mListChildrenData;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getImg_spouse_url() {
        return img_spouse_url;
    }

    public void setImg_spouse_url(String img_spouse_url) {
        this.img_spouse_url = img_spouse_url;
    }

    public String getImg_father_url() {
        return img_father_url;
    }

    public void setImg_father_url(String img_father_url) {
        this.img_father_url = img_father_url;
    }

    public String getImg_mother_url() {
        return img_mother_url;
    }

    public void setImg_mother_url(String img_mother_url) {
        this.img_mother_url = img_mother_url;
    }

    public String getImg_sfather_url() {
        return img_sfather_url;
    }

    public void setImg_sfather_url(String img_sfather_url) {
        this.img_sfather_url = img_sfather_url;
    }

    public String getImg_smother_url() {
        return img_smother_url;
    }

    public void setImg_smother_url(String img_smother_url) {
        this.img_smother_url = img_smother_url;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getMother_name() {
        return mother_name;
    }

    public void setMother_name(String mother_name) {
        this.mother_name = mother_name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getGotra() {
        return gotra;
    }

    public void setGotra(String gotra) {
        this.gotra = gotra;
    }

    public String getEkdo() {
        return ekdo;
    }

    public void setEkdo(String ekdo) {
        this.ekdo = ekdo;
    }

    public String getNative_place() {
        return native_place;
    }

    public void setNative_place(String native_place) {
        this.native_place = native_place;
    }

    public String getBirth_place() {
        return birth_place;
    }

    public void setBirth_place(String birth_place) {
        this.birth_place = birth_place;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getBirth_time() {
        return birth_time;
    }

    public void setBirth_time(String birth_time) {
        this.birth_time = birth_time;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOffice_address() {
        return office_address;
    }

    public void setOffice_address(String office_address) {
        this.office_address = office_address;
    }

    public String getOffice_mobile() {
        return office_mobile;
    }

    public void setOffice_mobile(String office_mobile) {
        this.office_mobile = office_mobile;
    }

    public String getOffice_lat() {
        return office_lat;
    }

    public void setOffice_lat(String office_lat) {
        this.office_lat = office_lat;
    }

    public String getOffice_lng() {
        return office_lng;
    }

    public void setOffice_lng(String office_lng) {
        this.office_lng = office_lng;
    }

    public String getHome_lat() {
        return home_lat;
    }

    public void setHome_lat(String home_lat) {
        this.home_lat = home_lat;
    }

    public String getHome_lng() {
        return home_lng;
    }

    public void setHome_lng(String home_lng) {
        this.home_lng = home_lng;
    }

    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public String getUser_lng() {
        return user_lng;
    }

    public void setUser_lng(String user_lng) {
        this.user_lng = user_lng;
    }

    public String getSpouse_name() {
        return spouse_name;
    }

    public void setSpouse_name(String spouse_name) {
        this.spouse_name = spouse_name;
    }

    public String getMarriage_date() {
        return marriage_date;
    }

    public void setMarriage_date(String marriage_date) {
        this.marriage_date = marriage_date;
    }

    public String getSfather_name() {
        return spouse_father_name;
    }

    public void setSfather_name(String spouse_father_name) {
        this.spouse_father_name = spouse_father_name;
    }

    public String getSmother_name() {
        return spouse_mother_name;
    }

    public void setSmother_name(String spouse_mother_name) {
        this.spouse_mother_name = spouse_mother_name;
    }
}
