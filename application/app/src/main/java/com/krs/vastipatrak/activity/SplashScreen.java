package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;


public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 300;

    /*MyProgressDialog myProgressDialog = null;*/
    SharedPreferences mSharedPreferences = null;
    SharedPreferences.Editor mEditor = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();


/*
        if (mSharedPreferences.getBoolean(Common.Constant_Class.FIRST_TIME, true) && Common.isOnline(this)) {

            new SyncAlertDialog(this, getString(R.string.sync_msg)).show();

        } else {*/
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
        // }
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppController.isSplashLive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppController.isSplashLive = false;
    }

/*
    class MyProgressDialog extends AlertDialog {
        String message = "";

        protected MyProgressDialog(Context context, String message) {
            super(context);
            this.message = message;
        }

        @Override
        public void show() {

            super.show();
            setContentView(R.layout.layout_progress);
            Button btnSkip = (Button) findViewById(R.id.btnSkip);
            TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
            txtMessage.setText(message);
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SplashScreen.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }

    }
*/

  /*  class SyncAlertDialog extends AlertDialog {
        String message = "";

        protected SyncAlertDialog(Context context, String message) {
            super(context);
            this.message = message;
            setIcon(R.drawable.app_icon);
            setTitle(getString(R.string.app_name));
        }

        @Override
        public void show() {

            super.show();
            setContentView(R.layout.layout_sync);
            Button btnOk = (Button) findViewById(R.id.btnOk);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);
            CheckBox chkMsg = (CheckBox) findViewById(R.id.chkMsg);

            TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
            txtMessage.setText(message);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SplashScreen.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //    callSyncWS();
                }
            });

            chkMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mEditor.putBoolean(Common.Constant_Class.FIRST_TIME, false);
                        mEditor.commit();
                    } else {
                        mEditor.putBoolean(Common.Constant_Class.FIRST_TIME, true);
                        mEditor.commit();
                    }
                }
            });
        }

    }

    private void showProgressDialog(String msg) {

        myProgressDialog = new MyProgressDialog(this, msg);
        myProgressDialog.setCancelable(false);

        if (!myProgressDialog.isShowing())
            myProgressDialog.show();
    }

    private void hideProgressDialog() {

        if (AppController.isSplashLive) {
            if (myProgressDialog != null) {
                myProgressDialog.hide();
            }
        }
    }

    public void callSyncWS() {

        final ArrayList<ListProfileData> mArrlstProfiledata = new ArrayList<ListProfileData>();


        if (Common.isOnline(this)) {
            JSONObject mJsonObject = null;
            showProgressDialog(getResources().getString(R.string.offline_progress));
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.STATUS, "1");

            } catch (Exception e) {
                e.printStackTrace();
            }

            final String sync_url = Common.Constant_Class.SEARCH_URL;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

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
                                mListProfileData.setFirst_name(first_name);
                                String last_name = mJsondata.getString(Common.Constant_Class.LAST_NAME);
                                mListProfileData.setLast_name(last_name);
                                String father_name = mJsondata.getString(Common.Constant_Class.FATHER_NAME);
                                mListProfileData.setFather_name(father_name);
                                String mother_name = mJsondata.getString(Common.Constant_Class.MOTHER_NAME);
                                mListProfileData.setMother_name(mother_name);
                                String email_address = mJsondata.getString(Common.Constant_Class.EMAIL_ADDRESS);
                                mListProfileData.setEmail_address(email_address);
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
                                mListProfileData.setGotra(gotra);
                                String ekdo = mJsondata.getString(Common.Constant_Class.EKDO);
                                mListProfileData.setEkdo(ekdo);
                                String native_place = mJsondata.getString(Common.Constant_Class.NATIVE_PLACE);
                                mListProfileData.setNative_place(native_place);
                                String birth_place = mJsondata.getString(Common.Constant_Class.BIRTH_PLACE);
                                mListProfileData.setBirth_date(birth_place);
                                String birth_date = mJsondata.getString(Common.Constant_Class.BIRTH_DATE);
                                mListProfileData.setBirth_date(birth_date);
                                String birth_time = mJsondata.getString(Common.Constant_Class.BIRTH_TIME);
                                mListProfileData.setBirth_time(birth_time);
                                String education = mJsondata.getString(Common.Constant_Class.EDUCATION);
                                mListProfileData.setEducation(education);
                                String occupation = mJsondata.getString(Common.Constant_Class.OCCUPATION);
                                mListProfileData.setOccupation(occupation);
                                String work = mJsondata.getString(Common.Constant_Class.WORK);
                                mListProfileData.setWork(work);
                                String address = mJsondata.getString(Common.Constant_Class.ADDRESS);
                                mListProfileData.setAddress(address);
                                String office_address = mJsondata.getString(Common.Constant_Class.OFFICE_ADDRESS);
                                mListProfileData.setOffice_address(office_address);
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
                                mListProfileData.setSpouse_name(spouse_name);
                                String marriage_date = mJsondata.getString(Common.Constant_Class.MARRIAGE_DATE);
                                mListProfileData.setMarriage_date(marriage_date);
                                String sfather_name = mJsondata.getString(Common.Constant_Class.SPOUSE_FATHER_NAME);
                                mListProfileData.setSfather_name(sfather_name);
                                String smother_name = mJsondata.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME);
                                mListProfileData.setSmother_name(smother_name);
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
                                    ArrayList<ListChildrenData> arrayListChildren = new ArrayList<ListChildrenData>();

                                    for (int j = 0; j < mJsonChildArray.length(); j++) {
                                        ListChildrenData mListChildrenData = new ListChildrenData();
                                        JSONObject mObjChild = mJsonChildArray.getJSONObject(j);
                                        mListChildrenData.setChild_id(mObjChild.getString(Common.Constant_Class.CHILD_ID));
                                        mListChildrenData.setChild_bday(mObjChild.getString(Common.Constant_Class.CHILD_BDAY));
                                        mListChildrenData.setChild_name(mObjChild.getString(Common.Constant_Class.CHILD_NAME));
                                        mListChildrenData.setChild_edu(mObjChild.getString(Common.Constant_Class.CHILD_EDU));
                                        mListChildrenData.setChild_work(mObjChild.getString(Common.Constant_Class.CHILD_WORK));
                                        mListChildrenData.setChild_img_url(mObjChild.getString(Common.Constant_Class.CHILD_IMAGE_URL));
                                        arrayListChildren.add(mListChildrenData);
                                    }
                                    mListProfileData.setmListChildrenData(arrayListChildren);
                                }
                                mArrlstProfiledata.add(mListProfileData);
                            }
                            // hideProgressDialog();


                            new SyncTask(mArrlstProfiledata).execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("", "Error: " + error.getMessage());
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        } else {
            Toast.makeText(SplashScreen.this, Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    public class SyncTask extends AsyncTask<Void, Integer, Bitmap> {

        int progress_status;
        ArrayList<ListProfileData> mArrlstProfiledata;


        SyncTask(ArrayList<ListProfileData> mArrlstProfiledata) {
            this.mArrlstProfiledata = mArrlstProfiledata;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_status = 0;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {

                Common.ClearProfileTableData();

                for (int i = 0; i < mArrlstProfiledata.size(); i++) {
                    AppController.dbHelper.InsertProfileData(mArrlstProfiledata.get(i));
                    progress_status = (i * 100) / mArrlstProfiledata.size();
                    publishProgress(progress_status);
                    Log.v("inserted ", "Records : " + i);
                }
            } catch (Exception e) {
                Log.v("exception : ", "" + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hideProgressDialog();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            hideProgressDialog();
            mEditor.putBoolean(Common.Constant_Class.FIRST_TIME, false);
            mEditor.commit();
            Log.v("inserted ", "Records Successfully");
            if (AppController.isSplashLive) {
                Intent i = new Intent(SplashScreen.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }

            Toast.makeText(SplashScreen.this, "Enjoy offline support !!", Toast.LENGTH_LONG).show();
        }
    }

*/
}
