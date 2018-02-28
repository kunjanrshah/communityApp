package com.krs.vastipatrak.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.RealmList;

/**
 * Created by kunjan on 28/2/18.
 */

public class SyncService extends Service {

    String TAG = "SyncService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        callSyncWS();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void callSyncWS() {

        if (Common.isOnline(this)) {

            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.STATUS, "1");
                mJsonObject.put(Common.Constant_Class.FIRST_NAME, "kunjan");

            } catch (Exception e) {
                e.printStackTrace();
            }

            final String sync_url = Common.Constant_Class.SYNC_URL;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "sync_url: " + sync_url);
                    Log.d(TAG, "response: " + response.toString());


                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        RealmList<ListProfileData> mArrlstProfiledata = null;
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {

                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {

                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                ListProfileData mListProfileData = new ListProfileData();

                                String profile_id = mJsondata.getString(Common.Constant_Class.ID);
                                mListProfileData.setProfile_id(profile_id);
                                String status = mJsondata.getString(Common.Constant_Class.STATUS);
                                mListProfileData.setStatus(status);
                                String first_name = mJsondata.getString(Common.Constant_Class.FIRST_NAME);
                                mListProfileData.setFirst_name(first_name.toLowerCase());
                                String last_name = mJsondata.getString(Common.Constant_Class.LAST_NAME);
                                mListProfileData.setLast_name(last_name.toLowerCase());


                                String father_name = mJsondata.getString(Common.Constant_Class.FATHER_NAME);
                                mListProfileData.setFather_name(father_name.toLowerCase());
                                String mother_name = mJsondata.getString(Common.Constant_Class.MOTHER_NAME);
                                mListProfileData.setMother_name(mother_name.toLowerCase());
                                String email_address = mJsondata.getString(Common.Constant_Class.EMAIL_ADDRESS);
                                mListProfileData.setEmail_address(email_address.toLowerCase());
                                String password = mJsondata.getString(Common.Constant_Class.PLAIN_PASSWORD);
                                mListProfileData.setPassword(password);
                                String mobile = mJsondata.getString(Common.Constant_Class.MOBILE);
                                mListProfileData.setMobile(mobile);
                                String phone = mJsondata.getString(Common.Constant_Class.PHONE);
                                mListProfileData.setPhone(phone);
                                String blood_group = mJsondata.getString(Common.Constant_Class.BLOOD_GROUP);
                                mListProfileData.setBlood_group(blood_group);
                                String gender = mJsondata.getString(Common.Constant_Class.GENDER);
                                mListProfileData.setGender(gender);
                                String gotra = mJsondata.getString(Common.Constant_Class.GOTRA);
                                mListProfileData.setGotra(gotra.toLowerCase());
                                String ekdo = mJsondata.getString(Common.Constant_Class.EKDO);
                                mListProfileData.setEkdo(ekdo.toLowerCase());
                                String native_place = mJsondata.getString(Common.Constant_Class.NATIVE_PLACE);
                                mListProfileData.setNative_place(native_place.toLowerCase());
                                String birth_place = mJsondata.getString(Common.Constant_Class.BIRTH_PLACE);
                                mListProfileData.setBirth_date(birth_place.toLowerCase());
                                String birth_date = mJsondata.getString(Common.Constant_Class.BIRTH_DATE);
                                mListProfileData.setBirth_date(birth_date);
                                String birth_time = mJsondata.getString(Common.Constant_Class.BIRTH_TIME);
                                mListProfileData.setBirth_time(birth_time);
                                String education = mJsondata.getString(Common.Constant_Class.EDUCATION);
                                mListProfileData.setEducation(education.toLowerCase());
                                String occupation = mJsondata.getString(Common.Constant_Class.OCCUPATION);
                                mListProfileData.setOccupation(occupation.toLowerCase());
                                String work = mJsondata.getString(Common.Constant_Class.WORK);
                                mListProfileData.setWork(work.toLowerCase());
                                String address = mJsondata.getString(Common.Constant_Class.ADDRESS);
                                mListProfileData.setAddress(address.toLowerCase());
                                String office_address = mJsondata.getString(Common.Constant_Class.OFFICE_ADDRESS);
                                mListProfileData.setOffice_address(office_address.toLowerCase());
                                String office_mobile = mJsondata.getString(Common.Constant_Class.OFFICE_MOBILE);
                                mListProfileData.setOffice_mobile(office_mobile);
                                String office_lat = mJsondata.getString(Common.Constant_Class.OFFICE_LAT);
                                mListProfileData.setOffice_lat(office_lat);
                                String office_lng = mJsondata.getString(Common.Constant_Class.OFFICE_LNG);
                                mListProfileData.setOffice_lng(office_lng);
                                String home_lat = mJsondata.getString(Common.Constant_Class.HOME_LAT);
                                mListProfileData.setHome_lat(home_lat);
                                String home_lng = mJsondata.getString(Common.Constant_Class.HOME_LNG);
                                mListProfileData.setHome_lng(home_lng);
                                String user_lat = mJsondata.getString(Common.Constant_Class.USER_LAT);
                                mListProfileData.setUser_lat(user_lat);
                                String user_lng = mJsondata.getString(Common.Constant_Class.USER_LNG);
                                mListProfileData.setUser_lng(user_lng);
                                String spouse_name = mJsondata.getString(Common.Constant_Class.SPOUSE_NAME);
                                mListProfileData.setSpouse_name(spouse_name.toLowerCase());
                                String marriage_date = mJsondata.getString(Common.Constant_Class.MARRIAGE_DATE);
                                mListProfileData.setMarriage_date(marriage_date);
                                String sfather_name = mJsondata.getString(Common.Constant_Class.SPOUSE_FATHER_NAME);
                                mListProfileData.setSfather_name(sfather_name.toLowerCase());
                                String smother_name = mJsondata.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME);
                                mListProfileData.setSmother_name(smother_name.toLowerCase());
                                String profile_pic_url = mJsondata.getString(Common.Constant_Class.PROFILE_PIC_URL);
                                mListProfileData.setProfile_pic_url(profile_pic_url);
                                String img_spouse_url = mJsondata.getString(Common.Constant_Class.IMG_SPOUSE_URL);
                                mListProfileData.setImg_spouse_url(img_spouse_url);
                                String img_father_url = mJsondata.getString(Common.Constant_Class.IMG_FATHER_URL);
                                mListProfileData.setImg_father_url(img_father_url);
                                String img_mother_url = mJsondata.getString(Common.Constant_Class.IMG_MOTHER_URL);
                                mListProfileData.setImg_mother_url(img_mother_url);
                                String img_sfather_url = mJsondata.getString(Common.Constant_Class.IMG_SFATHER_URL);
                                mListProfileData.setImg_sfather_url(img_sfather_url);
                                String img_smother_url = mJsondata.getString(Common.Constant_Class.IMG_SMOTHER_URL);
                                mListProfileData.setImg_smother_url(img_smother_url);

                                if (mJsondata.has(Common.Constant_Class.CHILDS)) {
                                    JSONArray mJsonChildArray = new JSONArray(mJsondata.getString(Common.Constant_Class.CHILDS));
                                    RealmList<ListChildrenData> arrayListChildren = new RealmList<>();

                                    for (int j = 0; j < mJsonChildArray.length(); j++) {
                                        ListChildrenData mListChildrenData = new ListChildrenData();
                                        JSONObject mObjChild = mJsonChildArray.getJSONObject(j);
                                        mListChildrenData.setChild_id(mObjChild.getString(Common.Constant_Class.CHILD_ID));
                                        mListChildrenData.setChild_bday(mObjChild.getString(Common.Constant_Class.CHILD_BDAY));
                                        mListChildrenData.setChild_name(mObjChild.getString(Common.Constant_Class.CHILD_NAME).toLowerCase());
                                        mListChildrenData.setChild_edu(mObjChild.getString(Common.Constant_Class.CHILD_EDU).toLowerCase());
                                        mListChildrenData.setChild_work(mObjChild.getString(Common.Constant_Class.CHILD_WORK).toLowerCase());
                                        mListChildrenData.setChild_img_url(mObjChild.getString(Common.Constant_Class.CHILD_IMAGE_URL));
                                        arrayListChildren.add(mListChildrenData);
                                    }
                                    mListProfileData.setmListChildrenData(arrayListChildren);
                                }
                                mArrlstProfiledata.add(mListProfileData);
                                Log.d(TAG, "sync: id: " + mListProfileData.getProfile_id() + " name :" + mListProfileData.getFirst_name());
                            }

                            //  new SyncTask().execute();

                            for (int i = 0; i < mArrlstProfiledata.size(); i++) {
                                AppController.getInstance().realm.beginTransaction();
                                AppController.getInstance().realm.copyToRealmOrUpdate(mArrlstProfiledata.get(i));
                                AppController.getInstance().realm.commitTransaction();
                             /*   progress_status = (i * 100) / mArrlstProfiledata.size();
                                publishProgress(progress_status);*/
                                Log.v("inserted ", "Records : " + i);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }

}
