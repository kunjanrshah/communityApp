package com.krs.vastipatrak.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import io.realm.RealmList;

public class SettingFragment extends Fragment {

    SearchView searchView;

    ProgressDialog pDialog;
    ProgressBar progressBar;

    String TAG = "SettingFragment";
    String tag_json_obj = "jobj_req";
    RealmList<ListProfileData> mArrlstProfiledata = null;
    ToggleButton tbtn_net, tbtn_sync;
    LinearLayout ll_progress;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private TextView txt_offline, txt_sync, txt_sync_val;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.title_setting);
        setHasOptionsMenu(true);

        MemoryAllocation(rootView);

        tbtn_net.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    txt_offline.setText("offline");
                    mEditor.putBoolean(Common.Constant_Class.OFFLINE_SP, true);
                    mEditor.commit();
                    SyncAlert();
                    /*if (checkDataBase()) {
                        copyFile();
                    }*/
                } else {
                    txt_offline.setText("online");
                    mEditor.putBoolean(Common.Constant_Class.OFFLINE_SP, false);
                    mEditor.commit();
                }

            }
        });

        tbtn_sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SyncAlert();
                }
            }
        });


        return rootView;
    }

    public boolean checkDataBase() {
        String PACKAGE_NAME = getActivity().getApplicationContext().getPackageName();
        String DB_PATH = "/data/data/" + PACKAGE_NAME + "/databases/";
        String DB_NAME = Common.Constant_Class.DATABASE_NAME;
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    public void copyFile() {
        try {
            //File sd = Environment.getExternalStorageDirectory();
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String PACKAGE_NAME = getActivity().getApplicationContext().getPackageName();
                String DB_PATH = "/data/" + PACKAGE_NAME + "/databases/";
                String DB_NAME = Common.Constant_Class.DATABASE_NAME;

                String currentDBPath = DB_PATH + DB_NAME;
                String backupDBPath = DB_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            Log.w("Settings Backup", e);
        }
    }


    private void SyncAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));

        builder.setMessage(getString(R.string.sync_msg));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callSyncWS();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tbtn_sync.setChecked(false);
            }
        }).show();
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    private void callSyncWS() {

        if (Common.isOnline(getActivity())) {

            showProgressDialog();
            JSONObject mJsonObject = null;

            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.STATUS, "1");
                mJsonObject.put(Common.Constant_Class.FIRST_NAME, "kunjan");

            } catch (Exception e) {
                e.printStackTrace();
            }

            final String sync_url = Common.Constant_Class.SEARCH_URL;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "sync_url: " + sync_url);
                    Log.d(TAG, "response: " + response.toString());


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
                        hideProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    hideProgressDialog();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }


    private void MemoryAllocation(View rootView) {

        txt_offline = (TextView) rootView.findViewById(R.id.txt_offline);
        txt_sync = (TextView) rootView.findViewById(R.id.txt_sync);
        txt_sync_val = (TextView) rootView.findViewById(R.id.txt_sync_val);
        tbtn_net = (ToggleButton) rootView.findViewById(R.id.tbtn_net);
        tbtn_sync = (ToggleButton) rootView.findViewById(R.id.tbtn_sync);
        ll_progress = (LinearLayout) rootView.findViewById(R.id.ll_progress);


        tbtn_sync.setChecked(false);
        tbtn_net.setText(null);
        tbtn_net.setTextOn(null);
        tbtn_net.setTextOff(null);
        tbtn_sync.setText(null);
        tbtn_sync.setTextOn(null);
        tbtn_sync.setTextOff(null);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);

        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        if (mSharedPreferences.getBoolean(Common.Constant_Class.OFFLINE_SP, false)) {
            tbtn_net.setChecked(true);
            txt_offline.setText("offline");
        } else {
            tbtn_net.setChecked(false);
            txt_offline.setText("online");
        }

        mArrlstProfiledata = new RealmList<>();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(false);

    }

    /*public class SyncTask extends AsyncTask<Void, Integer, Bitmap> {

        int progress_status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_status = 0;
            txt_sync_val.setText(" 0%");
            txt_sync.setVisibility(View.GONE);
            ll_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {

                //Common.ClearProfileTableData();

                for (int i = 0; i < mArrlstProfiledata.size(); i++) {
                    AppController.getInstance().realm.beginTransaction();
                    AppController.getInstance().realm.copyToRealmOrUpdate(mArrlstProfiledata.get(i));
                    AppController.getInstance().realm.commitTransaction();
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);
            txt_sync_val.setText(" " + values[0] + "%");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            Log.v("inserted ", "Records Successfully");
            Toast.makeText(getActivity(), "Enjoy offline support !!", Toast.LENGTH_LONG).show();
            ll_progress.setVisibility(View.GONE);
            txt_sync.setVisibility(View.VISIBLE);
            tbtn_sync.setChecked(false);
        }
    }*/


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Fragment fragment = new SearchFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(Common.Constant_Class.QUERY, query);
                fragment.setArguments(mBundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                Intent mIntent = new Intent(getActivity(), FilterActivity.class);
                // mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                //finish();
                getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }
        });
    }
}
