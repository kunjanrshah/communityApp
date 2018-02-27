package com.krs.vastipatrak.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {


    SQLiteDatabase db;

    /*public ArrayList<ListProfileData> GetListFromProfileTable(String query, int search) {

        ArrayList<ListProfileData> mListProfileDatas = new ArrayList<ListProfileData>();
        String squery = "";
        int count = 0;
        if (search == 1) {
            squery = "Select * From " + Common.Constant_Class.TABLE_PROFILE + " Where ("
                    + Common.Constant_Class.FIRST_NAME + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.LAST_NAME + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.FATHER_NAME + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.MOTHER_NAME + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.EMAIL_ADDRESS + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.MOBILE + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.PHONE + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.BLOOD_GROUP + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.GENDER + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.GOTRA + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.EKDO + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.NATIVE_PLACE + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.BIRTH_PLACE + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.BIRTH_DATE + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.BIRTH_TIME + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.EDUCATION + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.OCCUPATION + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.WORK + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.ADDRESS + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.OFFICE_MOBILE + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.OFFICE_ADDRESS + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.SPOUSE_NAME + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.MARRIAGE_DATE + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.SPOUSE_FATHER_NAME + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.SPOUSE_MOTHER_NAME + " Like '%" + query + "%') And " +
                    Common.Constant_Class.STATUS + " = 1";
        } else if (search == 3) {
            squery = "Select * From " + Common.Constant_Class.TABLE_PROFILE + " Where " + Common.Constant_Class.PROFILE_ID + " = " + query;
        } else {

            try {
                String password = "", first_name = "", last_name = "", father_name = "", mother_name = "", email_address = "", mobile = "", phone = "", blood_group = "", gender = "", gotra = "", ekdo = "", birth_place = "", native_place = "", birth_date = "",
                        birth_time = "", education = "", occupation = "", work = "", address = "", office_mobile = "", office_address = "", spouse_name = "", marriage_date = "", spouse_father_name = "", spouse_mother_name = "";
                squery = "Select * From " + Common.Constant_Class.TABLE_PROFILE + " Where ";


                JSONObject mJsonObject = new JSONObject(query);
                if (mJsonObject.has(Common.Constant_Class.FIRST_NAME)) {
                    first_name = mJsonObject.getString(Common.Constant_Class.FIRST_NAME);
                    squery = squery + Common.Constant_Class.FIRST_NAME + " Like '%" + first_name + "%' ";
                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.LAST_NAME)) {
                    last_name = mJsonObject.getString(Common.Constant_Class.LAST_NAME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.LAST_NAME + " Like '%" + last_name + "%' ";
                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.FATHER_NAME)) {
                    father_name = mJsonObject.getString(Common.Constant_Class.FATHER_NAME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.FATHER_NAME + " Like '%" + father_name + "%' ";
                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.MOTHER_NAME)) {
                    mother_name = mJsonObject.getString(Common.Constant_Class.MOTHER_NAME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.MOTHER_NAME + " Like '%" + mother_name + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.EMAIL_ADDRESS)) {
                    email_address = mJsonObject.getString(Common.Constant_Class.EMAIL_ADDRESS);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.EMAIL_ADDRESS + " Like '%" + email_address + "%' ";

                    count++;
                }

                if (mJsonObject.has(Common.Constant_Class.PASSWORD)) {
                    password = mJsonObject.getString(Common.Constant_Class.PASSWORD);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.PASSWORD + " Like '%" + password + "%' ";
                    count++;
                }


                if (mJsonObject.has(Common.Constant_Class.MOBILE)) {
                    mobile = mJsonObject.getString(Common.Constant_Class.MOBILE);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.MOBILE + " Like '%" + mobile + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.PHONE)) {
                    phone = mJsonObject.getString(Common.Constant_Class.PHONE);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.PHONE + " Like '%" + phone + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.BLOOD_GROUP)) {
                    blood_group = mJsonObject.getString(Common.Constant_Class.BLOOD_GROUP);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.BLOOD_GROUP + " Like '%" + blood_group + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.GENDER)) {
                    gender = mJsonObject.getString(Common.Constant_Class.GENDER);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.GENDER + " Like '%" + gender + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.GOTRA)) {
                    gotra = mJsonObject.getString(Common.Constant_Class.GOTRA);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.GOTRA + " Like '%" + gotra + "%' ";

                    count++;
                }

                if (mJsonObject.has(Common.Constant_Class.EKDO)) {
                    ekdo = mJsonObject.getString(Common.Constant_Class.EKDO);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.EKDO + " Like '%" + ekdo + "%' ";

                    count++;

                }
                if (mJsonObject.has(Common.Constant_Class.NATIVE_PLACE)) {
                    native_place = mJsonObject.getString(Common.Constant_Class.NATIVE_PLACE);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.NATIVE_PLACE + " Like '%" + native_place + "%' ";

                    count++;
                }

                if (mJsonObject.has(Common.Constant_Class.BIRTH_PLACE)) {
                    birth_place = mJsonObject.getString(Common.Constant_Class.BIRTH_PLACE);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.BIRTH_PLACE + " Like '%" + birth_place + "%' ";

                    count++;
                }

                if (mJsonObject.has(Common.Constant_Class.BIRTH_DATE)) {
                    birth_date = mJsonObject.getString(Common.Constant_Class.BIRTH_DATE);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.BIRTH_DATE + " Like '%" + birth_date + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.BIRTH_TIME)) {
                    birth_time = mJsonObject.getString(Common.Constant_Class.BIRTH_TIME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.BIRTH_TIME + " Like '%" + birth_time + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.EDUCATION)) {
                    education = mJsonObject.getString(Common.Constant_Class.EDUCATION);

                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.EDUCATION + " Like '%" + education + "%' ";

                    count++;

                }
                if (mJsonObject.has(Common.Constant_Class.OCCUPATION)) {
                    occupation = mJsonObject.getString(Common.Constant_Class.OCCUPATION);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.OCCUPATION + " Like '%" + occupation + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.WORK)) {
                    work = mJsonObject.getString(Common.Constant_Class.WORK);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.WORK + " Like '%" + work + "%' ";

                    count++;
                }

                if (mJsonObject.has(Common.Constant_Class.ADDRESS)) {
                    address = mJsonObject.getString(Common.Constant_Class.ADDRESS);

                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.ADDRESS + " Like '%" + address + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.OFFICE_MOBILE)) {
                    office_mobile = mJsonObject.getString(Common.Constant_Class.OFFICE_MOBILE);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.OFFICE_MOBILE + " Like '%" + office_mobile + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.OFFICE_ADDRESS)) {
                    office_address = mJsonObject.getString(Common.Constant_Class.OFFICE_ADDRESS);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.OFFICE_ADDRESS + " Like '%" + office_address + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.SPOUSE_NAME)) {
                    spouse_name = mJsonObject.getString(Common.Constant_Class.SPOUSE_NAME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.SPOUSE_NAME + " Like '%" + spouse_name + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.MARRIAGE_DATE)) {
                    marriage_date = mJsonObject.getString(Common.Constant_Class.MARRIAGE_DATE);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.MARRIAGE_DATE + " Like '%" + marriage_date + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.SPOUSE_FATHER_NAME)) {
                    spouse_father_name = mJsonObject.getString(Common.Constant_Class.SPOUSE_FATHER_NAME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.SPOUSE_FATHER_NAME + " Like '%" + spouse_father_name + "%' ";

                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.SPOUSE_MOTHER_NAME)) {
                    spouse_mother_name = mJsonObject.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.SPOUSE_MOTHER_NAME + " Like '%" + spouse_mother_name + "%' ";

                }
                if (count > 0) {
                    squery = squery + " AND ";
                }
                squery = squery + Common.Constant_Class.STATUS + " = 1";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Cursor c = null;
        if (search == 1 || search == 3 || count != 0) {
            c = db.rawQuery(squery, null);
        }

        if (c != null && c.getCount() > 0) {
            c.moveToNext();
            for (int i = 0; i < c.getCount(); i++) {
                ListProfileData mListProfileData = new ListProfileData();
                mListProfileData.setProfile_id(c.getString(0));
                mListProfileData.setStatus(c.getString(1));
                mListProfileData.setFirst_name(c.getString(3));
                mListProfileData.setLast_name(c.getString(4));
                mListProfileData.setFather_name(c.getString(5));
                mListProfileData.setMother_name(c.getString(6));
                mListProfileData.setEmail_address(c.getString(7));
                mListProfileData.setMobile(c.getString(8));
                mListProfileData.setPhone(c.getString(9));
                mListProfileData.setBlood_group(c.getString(10));
                mListProfileData.setGender(c.getString(11));
                mListProfileData.setGotra(c.getString(12));
                mListProfileData.setEkdo(c.getString(13));
                mListProfileData.setNative_place(c.getString(14));
                mListProfileData.setBirth_place(c.getString(15));
                mListProfileData.setBirth_date(c.getString(16));
                mListProfileData.setBirth_time(c.getString(17));
                mListProfileData.setEducation(c.getString(18));
                mListProfileData.setOccupation(c.getString(19));
                mListProfileData.setWork(c.getString(20));
                mListProfileData.setAddress(c.getString(21));
                mListProfileData.setOffice_mobile(c.getString(22));
                mListProfileData.setOffice_address(c.getString(23));
                mListProfileData.setOffice_lat(c.getString(24));
                mListProfileData.setOffice_lng(c.getString(25));
                mListProfileData.setHome_lat(c.getString(26));
                mListProfileData.setHome_lng(c.getString(27));
                mListProfileData.setUser_lat(c.getString(28));
                mListProfileData.setUser_lng(c.getString(29));
                mListProfileData.setSpouse_name(c.getString(30));
                mListProfileData.setMarriage_date(c.getString(31));
                mListProfileData.setSfather_name(c.getString(32));
                mListProfileData.setSmother_name(c.getString(33));

                squery = "Select * From " + Common.Constant_Class.TABLE_CHILDREN + " Where " + Common.Constant_Class.PROFILE_ID + " = " + c.getString(0) + "";
                Cursor c1 = db.rawQuery(squery, null);
                if (c1 != null && c1.getCount() > 0) {
                    c1.moveToNext();
                    ArrayList<ListChildrenData> mListChildrenDatas = new ArrayList<ListChildrenData>();
                    for (int j = 0; j < c1.getCount(); j++) {
                        ListChildrenData mListChildrenData = new ListChildrenData();
                        mListChildrenData.setChild_id(c1.getString(0));
                        mListChildrenData.setChild_name(c1.getString(2));
                        mListChildrenData.setChild_bday(c1.getString(3));
                        mListChildrenData.setChild_edu(c1.getString(4));
                        mListChildrenData.setChild_work(c1.getString(5));
                        mListChildrenDatas.add(mListChildrenData);
                        c1.moveToNext();
                    }
                    mListProfileData.setmListChildrenData(mListChildrenDatas);
                }
                mListProfileDatas.add(mListProfileData);
                c.moveToNext();
            }
        }
        return mListProfileDatas;
    }

    public ArrayList<ListProfileData> getDataFromChildTable(String query, int search) {

        ArrayList<ListProfileData> mListProfileDatas = new ArrayList<ListProfileData>();
        String squery = "";
        int count = 0;
        if (search == 1) {
            squery = "Select * From " + Common.Constant_Class.TABLE_CHILDREN + " Where "
                    + Common.Constant_Class.CHILD_BDAY + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.CHILD_EDU + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.CHILD_WORK + " Like '%" + query + "%' Or " +
                    Common.Constant_Class.CHILD_NAME + " Like '%" + query + "%'";
        } else {
            squery = "Select * From " + Common.Constant_Class.TABLE_CHILDREN + " Where ";
            String child_bday = "", child_edu = "", child_work = "", child_name = "";
            try {
                JSONObject mJsonObject = new JSONObject(query);
                if (mJsonObject.has(Common.Constant_Class.CHILD_BDAY)) {
                    child_bday = mJsonObject.getString(Common.Constant_Class.CHILD_BDAY);
                    squery = squery + Common.Constant_Class.CHILD_BDAY + " Like '%" + child_bday + "%' ";
                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.CHILD_EDU)) {
                    child_edu = mJsonObject.getString(Common.Constant_Class.CHILD_EDU);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.CHILD_EDU + " Like '%" + child_edu + "%' ";
                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.CHILD_WORK)) {
                    child_work = mJsonObject.getString(Common.Constant_Class.CHILD_WORK);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.CHILD_WORK + " Like '%" + child_work + "%' ";
                    count++;
                }
                if (mJsonObject.has(Common.Constant_Class.CHILD_NAME)) {
                    child_name = mJsonObject.getString(Common.Constant_Class.CHILD_NAME);
                    if (count > 0) {
                        squery = squery + " AND ";
                    }
                    squery = squery + Common.Constant_Class.CHILD_NAME + " Like '%" + child_name + "%' ";
                    count++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Cursor c_child = null;
        if (count != 0 || search == 1) {
            c_child = db.rawQuery(squery, null);
        }

        if (c_child != null && c_child.getCount() > 0) {
            c_child.moveToNext();
            for (int i = 0; i < c_child.getCount(); i++) {

                boolean flag = true;
                for (int j = 0; j < mListProfileDatas.size(); j++) {
                    ListProfileData mListProfileData = mListProfileDatas.get(j);
                    if (c_child.getString(1).equalsIgnoreCase(mListProfileData.getProfile_id())) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    squery = "Select * From " + Common.Constant_Class.TABLE_PROFILE + " Where " + Common.Constant_Class.PROFILE_ID + " = " + c_child.getString(1) + "";
                    Cursor c_profile = db.rawQuery(squery, null);
                    if (c_profile != null && c_profile.getCount() > 0) {
                        c_profile.moveToNext();
                        ListProfileData mListProfileData = new ListProfileData();
                        mListProfileData.setProfile_id(c_profile.getString(0));
                        mListProfileData.setStatus(c_profile.getString(1));
                        mListProfileData.setFirst_name(c_profile.getString(3));
                        mListProfileData.setLast_name(c_profile.getString(4));
                        mListProfileData.setFather_name(c_profile.getString(5));
                        mListProfileData.setMother_name(c_profile.getString(6));
                        mListProfileData.setEmail_address(c_profile.getString(7));
                        mListProfileData.setMobile(c_profile.getString(8));
                        mListProfileData.setPhone(c_profile.getString(9));
                        mListProfileData.setBlood_group(c_profile.getString(10));
                        mListProfileData.setGender(c_profile.getString(11));
                        mListProfileData.setGotra(c_profile.getString(12));
                        mListProfileData.setEkdo(c_profile.getString(13));
                        mListProfileData.setNative_place(c_profile.getString(14));
                        mListProfileData.setBirth_place(c_profile.getString(15));
                        mListProfileData.setBirth_date(c_profile.getString(16));
                        mListProfileData.setBirth_time(c_profile.getString(17));
                        mListProfileData.setEducation(c_profile.getString(18));
                        mListProfileData.setOccupation(c_profile.getString(19));
                        mListProfileData.setWork(c_profile.getString(20));
                        mListProfileData.setAddress(c_profile.getString(21));
                        mListProfileData.setOffice_mobile(c_profile.getString(22));
                        mListProfileData.setOffice_address(c_profile.getString(23));
                        mListProfileData.setOffice_lat(c_profile.getString(24));
                        mListProfileData.setOffice_lng(c_profile.getString(25));
                        mListProfileData.setHome_lat(c_profile.getString(26));
                        mListProfileData.setHome_lng(c_profile.getString(27));
                        mListProfileData.setUser_lat(c_profile.getString(28));
                        mListProfileData.setUser_lng(c_profile.getString(29));
                        mListProfileData.setSpouse_name(c_profile.getString(30));
                        mListProfileData.setMarriage_date(c_profile.getString(31));
                        mListProfileData.setSfather_name(c_profile.getString(32));
                        mListProfileData.setSmother_name(c_profile.getString(33));

                        squery = "Select * From " + Common.Constant_Class.TABLE_CHILDREN + " Where " + Common.Constant_Class.PROFILE_ID + " = " + c_profile.getString(0) + "";
                        Cursor c1 = db.rawQuery(squery, null);
                        if (c1 != null && c1.getCount() > 0) {
                            c1.moveToNext();
                            ArrayList<ListChildrenData> mListChildrenDatas = new ArrayList<ListChildrenData>();
                            for (int j = 0; j < c1.getCount(); j++) {
                                ListChildrenData mListChildrenData = new ListChildrenData();
                                mListChildrenData.setChild_id(c1.getString(0));
                                mListChildrenData.setChild_name(c1.getString(2));
                                mListChildrenData.setChild_bday(c1.getString(3));
                                mListChildrenData.setChild_edu(c1.getString(4));
                                mListChildrenData.setChild_work(c1.getString(5));
                                mListChildrenDatas.add(mListChildrenData);
                                c1.moveToNext();
                            }
                            mListProfileData.setmListChildrenData(mListChildrenDatas);
                        }
                        mListProfileDatas.add(mListProfileData);
                    }
                }

                c_child.moveToNext();
            }
        }
        return mListProfileDatas;
    }
*/

    public DatabaseHandler(Context context) {
        super(context, Common.Constant_Class.DATABASE_NAME, null, Common.Constant_Class.DATABASE_VERSION);
        db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFILE_TABLE = "CREATE TABLE " + Common.Constant_Class.TABLE_PROFILE + "("
                + Common.Constant_Class.PROFILE_ID + " TEXT ," +
                Common.Constant_Class.STATUS + " TEXT," +
                Common.Constant_Class.PASSWORD + " TEXT," +
                Common.Constant_Class.FIRST_NAME + " TEXT," +
                Common.Constant_Class.LAST_NAME + " TEXT," +
                Common.Constant_Class.FATHER_NAME + " TEXT," +
                Common.Constant_Class.MOTHER_NAME + " TEXT," +
                Common.Constant_Class.EMAIL_ADDRESS + " TEXT," +
                Common.Constant_Class.MOBILE + " TEXT," +
                Common.Constant_Class.PHONE + " TEXT," +
                Common.Constant_Class.BLOOD_GROUP + " TEXT," +
                Common.Constant_Class.GENDER + " TEXT," +
                Common.Constant_Class.GOTRA + " TEXT," +
                Common.Constant_Class.EKDO + " TEXT," +
                Common.Constant_Class.NATIVE_PLACE + " TEXT," +
                Common.Constant_Class.BIRTH_PLACE + " TEXT," +
                Common.Constant_Class.BIRTH_DATE + " TEXT," +
                Common.Constant_Class.BIRTH_TIME + " TEXT," +
                Common.Constant_Class.EDUCATION + " TEXT," +
                Common.Constant_Class.OCCUPATION + " TEXT," +
                Common.Constant_Class.WORK + " TEXT," +
                Common.Constant_Class.ADDRESS + " TEXT," +
                Common.Constant_Class.OFFICE_MOBILE + " TEXT," +
                Common.Constant_Class.OFFICE_ADDRESS + " TEXT," +
                Common.Constant_Class.OFFICE_LAT + " TEXT," +
                Common.Constant_Class.OFFICE_LNG + " TEXT," +
                Common.Constant_Class.HOME_LAT + " TEXT," +
                Common.Constant_Class.HOME_LNG + " TEXT," +
                Common.Constant_Class.USER_LAT + " TEXT," +
                Common.Constant_Class.USER_LNG + " TEXT," +
                Common.Constant_Class.SPOUSE_NAME + " TEXT," +
                Common.Constant_Class.MARRIAGE_DATE + " TEXT," +
                Common.Constant_Class.SPOUSE_FATHER_NAME + " TEXT," +
                Common.Constant_Class.SPOUSE_MOTHER_NAME + " TEXT," +
                Common.Constant_Class.PROFILE_PIC_URL + " TEXT," +
                Common.Constant_Class.IMG_SPOUSE_URL + " TEXT," +
                Common.Constant_Class.IMG_FATHER_URL + " TEXT," +
                Common.Constant_Class.IMG_MOTHER_URL + " TEXT," +
                Common.Constant_Class.IMG_SFATHER_URL + " TEXT," +
                Common.Constant_Class.IMG_SMOTHER_URL + " TEXT" + ")";

        String CREATE_CHILDREN_TABLE = "CREATE TABLE " + Common.Constant_Class.TABLE_CHILDREN + "("
                + Common.Constant_Class.CHILDREN_ID + " TEXT," +
                Common.Constant_Class.PROFILE_ID + " TEXT," +
                Common.Constant_Class.CHILD_NAME + " TEXT," +
                Common.Constant_Class.CHILD_BDAY + " TEXT," +
                Common.Constant_Class.CHILD_EDU + " TEXT," +
                Common.Constant_Class.CHILD_WORK + " TEXT," +
                Common.Constant_Class.CHILD_IMAGE_URL + " TEXT" + ")";

        db.execSQL(CREATE_PROFILE_TABLE);
        db.execSQL(CREATE_CHILDREN_TABLE);

    }

   /* public void ClearProfileTableData() {

        db.delete(Common.Constant_Class.TABLE_PROFILE, null, null);
        db.delete(Common.Constant_Class.TABLE_CHILDREN, null, null);
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Common.Constant_Class.TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + Common.Constant_Class.TABLE_CHILDREN);

        // Create tables again
        onCreate(db);
    }

    /*public void UpdateProfilePassword(String password, String id) {
        ContentValues values = new ContentValues();
        values.put(Common.Constant_Class.PASSWORD, password);
        db.update(Common.Constant_Class.TABLE_PROFILE, values, "" + Common.Constant_Class.PROFILE_ID + "=" + id, null);
    }*/

    /*public void UpdateProfileStatus(ArrayList<String> lstSelectedIDs, String status) {
        ContentValues values = new ContentValues();
        values.put(Common.Constant_Class.STATUS, status);

        for (int i = 0; i < lstSelectedIDs.size(); i++) {
            db.update(Common.Constant_Class.TABLE_PROFILE, values, "" + Common.Constant_Class.PROFILE_ID + "=" + lstSelectedIDs.get(i), null);
        }
    }*/

   /* public void DeleteProfiles(ArrayList<String> lstSelectedIDs) {

        for (int i = 0; i < lstSelectedIDs.size(); i++) {
            db.delete(Common.Constant_Class.TABLE_PROFILE, Common.Constant_Class.PROFILE_ID + " = ?", new String[]{lstSelectedIDs.get(i)});
            db.delete(Common.Constant_Class.TABLE_CHILDREN, Common.Constant_Class.PROFILE_ID + " = ?", new String[]{lstSelectedIDs.get(i)});
        }
    }*/


   /* public void SaveProfile(JSONObject mJsonObject) {
        //db.update();

        try {
            ContentValues values = new ContentValues();
            if (mJsonObject.has(Common.Constant_Class.FIRST_NAME)) {
                values.put(Common.Constant_Class.FIRST_NAME, mJsonObject.getString(Common.Constant_Class.FIRST_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.LAST_NAME)) {
                values.put(Common.Constant_Class.LAST_NAME, mJsonObject.getString(Common.Constant_Class.LAST_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.FATHER_NAME)) {
                values.put(Common.Constant_Class.FATHER_NAME, mJsonObject.getString(Common.Constant_Class.FATHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.MOTHER_NAME)) {
                values.put(Common.Constant_Class.MOTHER_NAME, mJsonObject.getString(Common.Constant_Class.MOTHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.BIRTH_DATE)) {
                values.put(Common.Constant_Class.BIRTH_DATE, mJsonObject.getString(Common.Constant_Class.BIRTH_DATE));
            }
            if (mJsonObject.has(Common.Constant_Class.BIRTH_TIME)) {
                values.put(Common.Constant_Class.BIRTH_TIME, mJsonObject.getString(Common.Constant_Class.BIRTH_TIME));
            }
            if (mJsonObject.has(Common.Constant_Class.BIRTH_PLACE)) {
                values.put(Common.Constant_Class.BIRTH_PLACE, mJsonObject.getString(Common.Constant_Class.BIRTH_PLACE));
            }
            if (mJsonObject.has(Common.Constant_Class.MOBILE)) {
                values.put(Common.Constant_Class.MOBILE, mJsonObject.getString(Common.Constant_Class.MOBILE));
            }
            if (mJsonObject.has(Common.Constant_Class.PHONE)) {
                values.put(Common.Constant_Class.PHONE, mJsonObject.getString(Common.Constant_Class.PHONE));
            }
            if (mJsonObject.has(Common.Constant_Class.BLOOD_GROUP)) {
                values.put(Common.Constant_Class.BLOOD_GROUP, mJsonObject.getString(Common.Constant_Class.BLOOD_GROUP));
            }
            if (mJsonObject.has(Common.Constant_Class.GENDER)) {
                values.put(Common.Constant_Class.GENDER, mJsonObject.getString(Common.Constant_Class.GENDER));
            }
            if (mJsonObject.has(Common.Constant_Class.GOTRA)) {
                values.put(Common.Constant_Class.GOTRA, mJsonObject.getString(Common.Constant_Class.GOTRA));
            }
            if (mJsonObject.has(Common.Constant_Class.EMAIL_ADDRESS)) {
                values.put(Common.Constant_Class.EMAIL_ADDRESS, mJsonObject.getString(Common.Constant_Class.EMAIL_ADDRESS));
            }
            if (mJsonObject.has(Common.Constant_Class.ADDRESS)) {
                values.put(Common.Constant_Class.ADDRESS, mJsonObject.getString(Common.Constant_Class.ADDRESS));
            }
            if (mJsonObject.has(Common.Constant_Class.NATIVE_PLACE)) {
                values.put(Common.Constant_Class.NATIVE_PLACE, mJsonObject.getString(Common.Constant_Class.NATIVE_PLACE));
            }
            if (mJsonObject.has(Common.Constant_Class.EDUCATION)) {
                values.put(Common.Constant_Class.EDUCATION, mJsonObject.getString(Common.Constant_Class.EDUCATION));
            }
            if (mJsonObject.has(Common.Constant_Class.OCCUPATION)) {
                values.put(Common.Constant_Class.OCCUPATION, mJsonObject.getString(Common.Constant_Class.OCCUPATION));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_MOBILE)) {
                values.put(Common.Constant_Class.OFFICE_MOBILE, mJsonObject.getString(Common.Constant_Class.OFFICE_MOBILE));
            }
            if (mJsonObject.has(Common.Constant_Class.WORK)) {
                values.put(Common.Constant_Class.WORK, mJsonObject.getString(Common.Constant_Class.WORK));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_ADDRESS)) {
                values.put(Common.Constant_Class.OCCUPATION, mJsonObject.getString(Common.Constant_Class.OCCUPATION));
            }
            if (mJsonObject.has(Common.Constant_Class.MARRIAGE_DATE)) {
                values.put(Common.Constant_Class.OFFICE_MOBILE, mJsonObject.getString(Common.Constant_Class.OFFICE_MOBILE));
            }
            if (mJsonObject.has(Common.Constant_Class.SPOUSE_NAME)) {
                values.put(Common.Constant_Class.SPOUSE_NAME, mJsonObject.getString(Common.Constant_Class.SPOUSE_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.SPOUSE_FATHER_NAME)) {
                values.put(Common.Constant_Class.SPOUSE_FATHER_NAME, mJsonObject.getString(Common.Constant_Class.SPOUSE_FATHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.SPOUSE_MOTHER_NAME)) {
                values.put(Common.Constant_Class.SPOUSE_MOTHER_NAME, mJsonObject.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_LAT)) {
                values.put(Common.Constant_Class.OFFICE_LAT, mJsonObject.getString(Common.Constant_Class.OFFICE_LAT));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_LNG)) {
                values.put(Common.Constant_Class.OFFICE_LNG, mJsonObject.getString(Common.Constant_Class.OFFICE_LNG));
            }
            if (mJsonObject.has(Common.Constant_Class.HOME_LAT)) {
                values.put(Common.Constant_Class.HOME_LAT, mJsonObject.getString(Common.Constant_Class.HOME_LAT));
            }
            if (mJsonObject.has(Common.Constant_Class.HOME_LNG)) {
                values.put(Common.Constant_Class.HOME_LNG, mJsonObject.getString(Common.Constant_Class.HOME_LNG));
            }
            if (mJsonObject.has(Common.Constant_Class.USER_LAT)) {
                values.put(Common.Constant_Class.USER_LAT, mJsonObject.getString(Common.Constant_Class.USER_LAT));
            }
            if (mJsonObject.has(Common.Constant_Class.USER_LNG)) {
                values.put(Common.Constant_Class.USER_LNG, mJsonObject.getString(Common.Constant_Class.USER_LNG));
            }

            String strWhere = "" + Common.Constant_Class.PROFILE_ID + "=" + mJsonObject.getString(Common.Constant_Class.PROFILE_ID);
            db.update(Common.Constant_Class.TABLE_PROFILE, values, strWhere, null);

            if (mJsonObject.has(Common.Constant_Class.CHILDS)) {

                JSONArray mJsonArray = new JSONArray(mJsonObject.getString(Common.Constant_Class.CHILDS));
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsonObj = mJsonArray.getJSONObject(i);
                    ContentValues values1 = new ContentValues();
                    if (mJsonObj.has(Common.Constant_Class.CHILD_NAME)) {
                        values1.put(Common.Constant_Class.CHILD_NAME, mJsonObj.getString(Common.Constant_Class.CHILD_NAME));
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_BDAY)) {
                        values1.put(Common.Constant_Class.CHILD_BDAY, mJsonObj.getString(Common.Constant_Class.CHILD_BDAY));
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_EDU)) {
                        values1.put(Common.Constant_Class.CHILD_EDU, mJsonObj.getString(Common.Constant_Class.CHILD_EDU));
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_WORK)) {
                        values1.put(Common.Constant_Class.CHILD_WORK, mJsonObj.getString(Common.Constant_Class.CHILD_WORK));
                    }
                    db.update(Common.Constant_Class.TABLE_CHILDREN, values1, "" + Common.Constant_Class.CHILDREN_ID + "=" + mJsonObj.getString(Common.Constant_Class.ID), null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

/*
    public void InsertChildData(String profile_id, ArrayList<ListChildrenData> listChildrenDatas) {
        //SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < listChildrenDatas.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(Common.Constant_Class.CHILDREN_ID, listChildrenDatas.get(i).getChild_id());
            values.put(Common.Constant_Class.PROFILE_ID, profile_id);
            values.put(Common.Constant_Class.CHILD_NAME, listChildrenDatas.get(i).getChild_name());
            values.put(Common.Constant_Class.CHILD_BDAY, listChildrenDatas.get(i).getChild_bday());
            values.put(Common.Constant_Class.CHILD_EDU, listChildrenDatas.get(i).getChild_edu());
            values.put(Common.Constant_Class.CHILD_WORK, listChildrenDatas.get(i).getChild_work());
            values.put(Common.Constant_Class.CHILD_IMAGE_URL, listChildrenDatas.get(i).getChild_img_url());
            // Inserting Row
            db.insert(Common.Constant_Class.TABLE_CHILDREN, null, values);
        }
        //      db.close(); // Closing database connection
    }
*/

/*
    public void InsertProfileData(ListProfileData mListProfileData) {

        ContentValues values = new ContentValues();
        values.put(Common.Constant_Class.PROFILE_ID, mListProfileData.getProfile_id());
        values.put(Common.Constant_Class.STATUS, mListProfileData.getStatus());
        values.put(Common.Constant_Class.PASSWORD, mListProfileData.getPassword());
        values.put(Common.Constant_Class.FIRST_NAME, mListProfileData.getFirst_name());
        values.put(Common.Constant_Class.LAST_NAME, mListProfileData.getLast_name());
        values.put(Common.Constant_Class.FATHER_NAME, mListProfileData.getFather_name());
        values.put(Common.Constant_Class.MOTHER_NAME, mListProfileData.getMother_name());
        values.put(Common.Constant_Class.EMAIL_ADDRESS, mListProfileData.getEmail_address());
        values.put(Common.Constant_Class.MOBILE, mListProfileData.getMobile());
        values.put(Common.Constant_Class.PHONE, mListProfileData.getPhone());
        values.put(Common.Constant_Class.BLOOD_GROUP, mListProfileData.getBlood_group());
        values.put(Common.Constant_Class.GENDER, mListProfileData.getGender());
        values.put(Common.Constant_Class.GOTRA, mListProfileData.getGotra());
        values.put(Common.Constant_Class.EKDO, mListProfileData.getEkdo());
        values.put(Common.Constant_Class.NATIVE_PLACE, mListProfileData.getNative_place());
        values.put(Common.Constant_Class.BIRTH_PLACE, mListProfileData.getBirth_place());
        values.put(Common.Constant_Class.BIRTH_DATE, mListProfileData.getBirth_date());
        values.put(Common.Constant_Class.BIRTH_TIME, mListProfileData.getBirth_time());
        values.put(Common.Constant_Class.EDUCATION, mListProfileData.getEducation());
        values.put(Common.Constant_Class.OCCUPATION, mListProfileData.getOccupation());
        values.put(Common.Constant_Class.WORK, mListProfileData.getWork());
        values.put(Common.Constant_Class.ADDRESS, mListProfileData.getAddress());
        values.put(Common.Constant_Class.OFFICE_ADDRESS, mListProfileData.getOffice_address());
        values.put(Common.Constant_Class.OFFICE_MOBILE, mListProfileData.getOffice_mobile());
        values.put(Common.Constant_Class.OFFICE_LAT, mListProfileData.getOffice_lat());
        values.put(Common.Constant_Class.OFFICE_LNG, mListProfileData.getOffice_lng());
        values.put(Common.Constant_Class.HOME_LAT, mListProfileData.getHome_lat());
        values.put(Common.Constant_Class.HOME_LNG, mListProfileData.getHome_lng());
        values.put(Common.Constant_Class.USER_LAT, mListProfileData.getUser_lat());
        values.put(Common.Constant_Class.USER_LNG, mListProfileData.getUser_lng());
        values.put(Common.Constant_Class.SPOUSE_NAME, mListProfileData.getSpouse_name());
        values.put(Common.Constant_Class.MARRIAGE_DATE, mListProfileData.getMarriage_date());
        values.put(Common.Constant_Class.SPOUSE_FATHER_NAME, mListProfileData.getSfather_name());
        values.put(Common.Constant_Class.SPOUSE_MOTHER_NAME, mListProfileData.getSmother_name());
        values.put(Common.Constant_Class.PROFILE_PIC_URL, mListProfileData.getProfile_pic_url());
        values.put(Common.Constant_Class.IMG_SPOUSE_URL, mListProfileData.getImg_spouse_url());
        values.put(Common.Constant_Class.IMG_FATHER_URL, mListProfileData.getImg_father_url());
        values.put(Common.Constant_Class.IMG_MOTHER_URL, mListProfileData.getImg_mother_url());
        values.put(Common.Constant_Class.IMG_SFATHER_URL, mListProfileData.getImg_sfather_url());
        values.put(Common.Constant_Class.IMG_SMOTHER_URL, mListProfileData.getImg_smother_url());
        // Inserting Row
        db.insert(Common.Constant_Class.TABLE_PROFILE, null, values);
        if (mListProfileData.getmListChildrenData() != null) {
            InsertChildData(mListProfileData.getProfile_id(), mListProfileData.getmListChildrenData());
        }
    }
*/
}
