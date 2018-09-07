package com.krs.vastipatrak.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.fragments.BusinessFragment;
import com.krs.vastipatrak.fragments.FamilyFragment;
import com.krs.vastipatrak.fragments.FragmentDrawer;
import com.krs.vastipatrak.fragments.PersonalFragment;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MyProfileActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    public static boolean isEnable = false;
    private final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences.Editor mEditor;
    private SearchView searchView;
    private Bundle mBundle = null;
    private String valid = "";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private ListProfileData mListProfileData = null;
    private Fragment personal = null;
    private Fragment business = null;
    private Fragment family = null;
    private SharedPreferences mSharedPreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        MemoryAllocation();
        ToolbarSetup();
        String id = "";
        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
        } else {
            id = mSharedPreferences.getString(Common.Constant_Class.PROFILE_ID, "");
        }
        SyncUser(id);
        // call_profile_ws(new JSONObject(), "0");
    }

    @Nullable
    public ListProfileData getMyData() {
        return mListProfileData;
    }

    @SuppressLint("CommitPrefEdits")
    private void MemoryAllocation() {

        toolbar = findViewById(R.id.toolbar);
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mBundle = getIntent().getExtras();
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
    }

    private void ToolbarSetup() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("My Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNavigation();
            }
        });
    }

    private void backNavigation() {
        Fragment fragment = new FragmentDrawer();
        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
        assert mSharedPreferences != null;
        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false)) {
            Intent mIntent = new Intent(MyProfileActivity.this, MainActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
        }
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backNavigation();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent mIntent = new Intent(MyProfileActivity.this, MainActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.putExtra(Common.Constant_Class.QUERY, query);
                startActivity(mIntent);
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem export = menu.findItem(R.id.action_export);
        export.setVisible(false);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent mIntent = new Intent(MyProfileActivity.this, FilterActivity.class);
                startActivity(mIntent);
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }
        });

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Common.promptSpeechInput(MyProfileActivity.this);
                return false;
            }
        });

        final MenuItem saveItem = menu.findItem(R.id.action_save);
        if (Objects.requireNonNull(mSharedPreferences).getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            saveItem.setVisible(true);
        } else {
            saveItem.setVisible(false);
        }


        MenuItem action_toggle = menu.findItem(R.id.action_toggle);
        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) && mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
            action_toggle.setVisible(true);
        } else {
            action_toggle.setVisible(false);
        }

        action_toggle.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (isEnable) {
                    saveItem.setVisible(false);
                    isEnable = false;
                } else {
                    isEnable = true;
                    saveItem.setVisible(true);
                }
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
                return false;
            }
        });

        saveItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                ListProfileData mListProfileData = new ListProfileData();

                // Personal Details
                String fname = ((PersonalFragment) personal).edtFName.getText().toString().trim();
                String lname = ((PersonalFragment) personal).edtLName.getText().toString().trim();
                String FatherName = ((PersonalFragment) personal).edtFatherName.getText().toString().trim();
                String MotherName = ((PersonalFragment) personal).edtMotherName.getText().toString().trim();
                String Education = ((PersonalFragment) personal).edtEducation.getText().toString().trim();
                String BPlace = ((PersonalFragment) personal).edtBPlace.getText().toString().trim();
                String NPlace = ((PersonalFragment) personal).edtNPlace.getText().toString().trim();
                String Gotra = ((PersonalFragment) personal).spinnerGotra.getSelectedItem().toString().trim();
                String Mobile = ((PersonalFragment) personal).edtMobile.getText().toString().trim();
                String Address = ((PersonalFragment) personal).edtAddress.getText().toString().trim();
                String Eaddress = ((PersonalFragment) personal).edt_Eaddress.getText().toString().trim();
                String city = ((PersonalFragment) personal).edtCity.getText().toString().trim();

                if (!Eaddress.equalsIgnoreCase("")) {
                    if (Common.isValidEmail(Eaddress)) {
                        valid = "Email is not valid Format";
                    }
                }

                String phone = Objects.requireNonNull(((PersonalFragment) personal).edt_phone).getText().toString().trim();
                String bdate = Objects.requireNonNull(((PersonalFragment) personal).edtbdate).getText().toString().trim();
                bdate = Common.parseDateToddMMyyyy(bdate, Common.ddMMMyyyy, Common.yyyy_MM_dd);
                if (!bdate.equalsIgnoreCase("")) {
                    if (!Common.isThisDateValid(bdate, "yyyy-mm-dd")) {
                        valid = "Birth Date is not valid Format";
                    }
                }

                String time = Objects.requireNonNull(((PersonalFragment) personal).edtbTime).getText().toString().trim();
                if (!time.equalsIgnoreCase("")) {
                    if (Common.IsValidate(time)) {
                        valid = "Birth Time is not valid 24 Hours";
                    }
                }

                String gender = ((PersonalFragment) personal).gender;
                String bgroup = ((PersonalFragment) personal).spinnerBlood.getSelectedItem().toString().trim();
                String str_profile_hash = ((PersonalFragment) personal).str_profile_hash;
                String str_father_hash = ((PersonalFragment) personal).str_father_hash;
                String str_mother_hash = ((PersonalFragment) personal).str_mother_hash;

                // Business Details
                String occupation = ((BusinessFragment) business).edtOccupation.getText().toString().trim();
                String Work = ((BusinessFragment) business).edtWork.getText().toString().trim();
                String OMobile = ((BusinessFragment) business).edtOMobile.getText().toString().trim();
                String OAddress = ((BusinessFragment) business).edtOAddress.getText().toString().trim();

                // Familty Details
                String spouseName = "", SpouseFName = "", MSpouseName = "", mdate = "", sdate = "", str_fspouse_hash = "", str_mspouse_hash = "", str_spouse_hash = "";
                String sponseBdate="",sponseNative="",sponseMobile="";
                LinearLayout child_container = null;
                ArrayList<Integer> lst_delID = null;
                try {
                    spouseName = ((FamilyFragment) family).edtSpouseName.getText().toString().trim();
                    sponseMobile = ((FamilyFragment) family).edtsponse_mobile.getText().toString().trim();
                    sponseBdate = ((FamilyFragment) family).edtsponse_bdate.getText().toString().trim();
                    sponseNative = ((FamilyFragment) family).edtsponse_nplace.getText().toString().trim();
                    SpouseFName = ((FamilyFragment) family).edtSpouseFName.getText().toString().trim();
                    MSpouseName = ((FamilyFragment) family).edtMSpouseName.getText().toString().trim();
                    mdate = ((FamilyFragment) family).edt_mdate.getText().toString().trim();
                    mdate = Common.parseDateToddMMyyyy(mdate, Common.ddMMMyyyy, Common.yyyy_MM_dd);
                    if (!mdate.equalsIgnoreCase("")) {
                        if (!Common.isThisDateValid(mdate, "yyyy-mm-dd")) {
                            valid = "Marriage Date is not valid Format";
                        }
                    }

                    sdate = ((FamilyFragment) family).edtsponse_bdate.getText().toString().trim();
                    sdate = Common.parseDateToddMMyyyy(sdate, Common.ddMMMyyyy, Common.yyyy_MM_dd);
                    if (!sdate.equalsIgnoreCase("")) {
                        if (!Common.isThisDateValid(sdate, "yyyy-mm-dd")) {
                            valid = "Sponse Birth Date is not valid Format";
                        }
                    }

                    str_fspouse_hash = ((FamilyFragment) family).str_fspouse_hash;
                    str_mspouse_hash = ((FamilyFragment) family).str_mspouse_hash;
                    str_spouse_hash = ((FamilyFragment) family).str_spouse_hash;
                    child_container = ((FamilyFragment) family).child_container;
                    lst_delID = ((FamilyFragment) family).lst_delID;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (valid.equalsIgnoreCase("")) {

                    mListProfileData.setFirst_name(fname);
                    mListProfileData.setLast_name(lname);
                    mListProfileData.setFather_name(FatherName);
                    mListProfileData.setMother_name(MotherName);
                    mListProfileData.setEducation(Education);
                    mListProfileData.setBirth_place(BPlace);
                    mListProfileData.setNative_place(NPlace);
                    mListProfileData.setGotra(Gotra);
                    mListProfileData.setMobile(Mobile);
                    mListProfileData.setAddress(Address);
                    mListProfileData.setEmail_address(Eaddress);
                    mListProfileData.setPhone(phone);
                    mListProfileData.setCity(city);
                    mListProfileData.setBirth_date(bdate);
                    mListProfileData.setBirth_time(time);
                    mListProfileData.setGender(gender);
                    mListProfileData.setBlood_group(bgroup);
                    mListProfileData.setOccupation(occupation);
                    mListProfileData.setWork(Work);
                    mListProfileData.setOffice_mobile(OMobile);
                    mListProfileData.setOffice_address(OAddress);
                    mListProfileData.setSpouse_name(spouseName);
                    mListProfileData.setSponse_bdate(sponseBdate);
                    mListProfileData.setSponse_mobile(sponseMobile);
                    mListProfileData.setSponse_native(sponseNative);
                    mListProfileData.setSfather_name(SpouseFName);
                    mListProfileData.setSmother_name(MSpouseName);
                    mListProfileData.setMarriage_date(mdate);
                    mListProfileData.setStr_profile_hash(str_profile_hash);
                    mListProfileData.setStr_father_hash(str_father_hash);
                    mListProfileData.setStr_mother_hash(str_mother_hash);
                    mListProfileData.setStr_fspouse_hash(str_fspouse_hash);
                    mListProfileData.setStr_mspouse_hash(str_mspouse_hash);
                    mListProfileData.setStr_spouse_hash(str_spouse_hash);

                    setProfileJsonObject(mListProfileData, child_container, Objects.requireNonNull(lst_delID));
                } else {
                    Toast.makeText(MyProfileActivity.this, "" + valid, Toast.LENGTH_SHORT).show();
                    valid = "";
                }
                return false;
            }
        });
        return true;
    }

    private void setProfileJsonObject(@NonNull ListProfileData mListProfileData, @Nullable LinearLayout child_container, @NonNull ArrayList<Integer> lst_delID) {


        JSONObject mJsonObject = null;

        // Personal Details
        try {
            mJsonObject = new JSONObject();

            mJsonObject.put(Common.Constant_Class.IS_UPDATE, "1");
            mJsonObject.put(Common.Constant_Class.USER_ID, Objects.requireNonNull(mSharedPreferences).getString(Common.Constant_Class.USER_ID, ""));
            mJsonObject.put(Common.Constant_Class.FIRST_NAME, mListProfileData.getFirst_name());
            mJsonObject.put(Common.Constant_Class.LAST_NAME, mListProfileData.getLast_name());
            mJsonObject.put(Common.Constant_Class.FATHER_NAME, mListProfileData.getFather_name());
            mJsonObject.put(Common.Constant_Class.MOTHER_NAME, mListProfileData.getMother_name());
            mJsonObject.put(Common.Constant_Class.BIRTH_DATE, mListProfileData.getBirth_date());
            mJsonObject.put(Common.Constant_Class.BIRTH_TIME, mListProfileData.getBirth_time());
            mJsonObject.put(Common.Constant_Class.BIRTH_PLACE, mListProfileData.getBirth_place());
            mJsonObject.put(Common.Constant_Class.MOBILE, mListProfileData.getMobile());
            mJsonObject.put(Common.Constant_Class.PHONE, mListProfileData.getPhone());
            mJsonObject.put(Common.Constant_Class.CITY, mListProfileData.getCity());
            mJsonObject.put(Common.Constant_Class.BLOOD_GROUP, mListProfileData.getBlood_group());
            mJsonObject.put(Common.Constant_Class.GENDER, mListProfileData.getGender());
            mJsonObject.put(Common.Constant_Class.GOTRA, mListProfileData.getGotra());
            mJsonObject.put(Common.Constant_Class.EMAIL_ADDRESS, mListProfileData.getEmail_address());
            mJsonObject.put(Common.Constant_Class.ADDRESS, mListProfileData.getAddress());
            mJsonObject.put(Common.Constant_Class.NATIVE_PLACE, mListProfileData.getNative_place());
            mJsonObject.put(Common.Constant_Class.EDUCATION, mListProfileData.getEducation());
            if (!mListProfileData.getStr_profile_hash().equalsIgnoreCase("")) {
                mJsonObject.put(Common.Constant_Class.PROFILE_PIC, mListProfileData.getStr_profile_hash());
            }
            if (!mListProfileData.getStr_mother_hash().equalsIgnoreCase("")) {
                mJsonObject.put(Common.Constant_Class.IMG_MOTHER, mListProfileData.getStr_mother_hash());
            }
            if (!mListProfileData.getStr_father_hash().equalsIgnoreCase("")) {
                mJsonObject.put(Common.Constant_Class.IMG_FATHER, mListProfileData.getStr_father_hash());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Business details
        try {

            assert mJsonObject != null;
            mJsonObject.put(Common.Constant_Class.OCCUPATION, mListProfileData.getOccupation());
            mJsonObject.put(Common.Constant_Class.OFFICE_MOBILE, mListProfileData.getOffice_mobile());
            mJsonObject.put(Common.Constant_Class.WORK, mListProfileData.getWork());
            mJsonObject.put(Common.Constant_Class.OFFICE_ADDRESS, mListProfileData.getOffice_address());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Family Details
        try {
            Objects.requireNonNull(mJsonObject).put(Common.Constant_Class.MARRIAGE_DATE, mListProfileData.getMarriage_date());
            mJsonObject.put(Common.Constant_Class.SPOUSE_NAME, mListProfileData.getSpouse_name());
            mJsonObject.put(Common.Constant_Class.SPOUSE_BDATE, mListProfileData.getSponse_bdate());
            mJsonObject.put(Common.Constant_Class.SPOUSE_MOBILE, mListProfileData.getSponse_mobile());
            mJsonObject.put(Common.Constant_Class.SPOUSE_NATIVE, mListProfileData.getSponse_native());

            mJsonObject.put(Common.Constant_Class.SPOUSE_FATHER_NAME, mListProfileData.getSfather_name());
            mJsonObject.put(Common.Constant_Class.SPOUSE_MOTHER_NAME, mListProfileData.getSmother_name());

            if (!mListProfileData.getStr_spouse_hash().equalsIgnoreCase("")) {
                mJsonObject.put(Common.Constant_Class.IMG_SPOUSE, mListProfileData.getStr_spouse_hash());
            }
            if (!mListProfileData.getStr_mspouse_hash().equalsIgnoreCase("")) {
                mJsonObject.put(Common.Constant_Class.IMG_SMOTHER, mListProfileData.getStr_mspouse_hash());
            }
            if (!mListProfileData.getStr_fspouse_hash().equalsIgnoreCase("")) {
                mJsonObject.put(Common.Constant_Class.IMG_SFATHER, mListProfileData.getStr_fspouse_hash());
            }

            if (child_container != null) {
                int child_count = child_container.getChildCount();
                JSONArray mJsonArray = new JSONArray();
                for (int i = 0; i < child_count; i++) {
                    FamilyFragment.Viewholder mViewholder = (FamilyFragment.Viewholder) child_container.getChildAt(i).getTag();
                    JSONObject mJsonObject_Child = new JSONObject();
                    if (mViewholder.child_id != 0) {
                        mJsonObject_Child.put("id", mViewholder.child_id);
                    }
                    if (((FamilyFragment) family).rbtnChildNo.isChecked()) {
                        mJsonObject_Child.put(Common.Constant_Class.CHILD_DELETE, "true");
                    }
                    mJsonObject_Child.put(Common.Constant_Class.CHILD_NAME, Objects.requireNonNull(mViewholder.edtchild_name).getText());
                    String child_bday = Objects.requireNonNull(mViewholder.edtchild_bdate.getText().toString().trim());
                    child_bday = Common.parseDateToddMMyyyy(child_bday, Common.ddMMMyyyy, Common.yyyy_MM_dd);
                    if (!child_bday.equalsIgnoreCase("")) {
                        if (!Common.isThisDateValid(child_bday, "yyyy-mm-dd")) {
                            valid = "Child Birth Date is not valid Format";
                        }
                    }

                    boolean child_interest = Objects.requireNonNull(mViewholder.tbtn_interest).isChecked();
                    String child_gender = mViewholder.gender;
                    String child_btime = Objects.requireNonNull(mViewholder.edtchild_btime).getText().toString();
                    String child_bplace = Objects.requireNonNull(mViewholder.edtchild_bplace).getText().toString();
                    String child_mobile = Objects.requireNonNull(mViewholder.edtMobile).getText().toString();
                    String child_blood = Objects.requireNonNull(mViewholder.spinnerBlood).getSelectedItem().toString();

                    mJsonObject_Child.put(Common.Constant_Class.BLOOD_GROUP, child_blood);
                    mJsonObject_Child.put(Common.Constant_Class.MOBILE, child_mobile);
                    mJsonObject_Child.put(Common.Constant_Class.IS_INTERESTED, child_interest);
                    mJsonObject_Child.put(Common.Constant_Class.GENDER, child_gender);
                    mJsonObject_Child.put(Common.Constant_Class.BIRTH_TIME, child_btime);
                    mJsonObject_Child.put(Common.Constant_Class.BIRTH_PLACE, child_bplace);
                    mJsonObject_Child.put(Common.Constant_Class.CHILD_BDAY, child_bday);
                    mJsonObject_Child.put(Common.Constant_Class.CHILD_EDU, Objects.requireNonNull(mViewholder.edtchild_edu).getText());
                    mJsonObject_Child.put(Common.Constant_Class.CHILD_WORK, Objects.requireNonNull(mViewholder.edtchild_work).getText());
                    if (!mViewholder.ImgHash.equalsIgnoreCase("")) {
                        mJsonObject_Child.put(Common.Constant_Class.CHILD_IMAGE, mViewholder.ImgHash);
                    }
                    mJsonArray.put(mJsonObject_Child);
                    mJsonObject.put(Common.Constant_Class.CHILDS, mJsonArray);

                }

                for (int i = 0; i < lst_delID.size(); i++) {

                    JSONObject mJsonObject_Child = new JSONObject();
                    if (lst_delID.get(i) != 0) {
                        mJsonObject_Child.put("id", lst_delID.get(i));
                        mJsonObject_Child.put(Common.Constant_Class.CHILD_DELETE, "true");
                        mJsonArray.put(mJsonObject_Child);
                        mJsonObject.put(Common.Constant_Class.CHILDS, mJsonArray);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (valid.equalsIgnoreCase("")) {
            call_profile_ws(mJsonObject, "1");
        } else {
            Toast.makeText(MyProfileActivity.this, "" + valid, Toast.LENGTH_SHORT).show();
            valid = "";
        }
    }

    private void call_profile_ws(@Nullable JSONObject mJsonObject, final String is_update) {
        if (Common.isOnline(this) && mSharedPreferences != null) {
            try {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
                    mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                    mJsonObject.put(Common.Constant_Class.IS_UPDATE, is_update);
                } else {
                    mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                    mJsonObject.put(Common.Constant_Class.UPDATE_USER_ID, mSharedPreferences.getString(Common.Constant_Class.PROFILE_ID, ""));
                    mJsonObject.put(Common.Constant_Class.IS_UPDATE, is_update);
                }
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!(isFinishing())) {
                            Common.showProgressDialog(MyProfileActivity.this);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.PROFILE_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {

                    try {
                        Common.hideProgressDialog();
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            String data = response.getString(Common.Constant_Class.DATA);
                            JSONObject mData = new JSONObject(data);
                            mListProfileData = Common.SaveProfile(mData);
                            if (is_update.equalsIgnoreCase("1")) {
                                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false)) {
                                    mEditor.putString(Common.Constant_Class.PROFILE_PIC_URL, mData.getString(Common.Constant_Class.PROFILE_PIC_URL));
                                    mEditor.putString(Common.Constant_Class.FIRST_NAME, mData.getString(Common.Constant_Class.FIRST_NAME));
                                    mEditor.putString(Common.Constant_Class.LAST_NAME, mData.getString(Common.Constant_Class.LAST_NAME));
                                    mEditor.apply();
                                    AppController.getInstance().isUpdate = true;
                                }
                                alert(message);
                            } else {
                                setupViewPager(viewPager);
                                tabLayout.setupWithViewPager(viewPager);
                            }
                        } else {
                            try {
                                JSONObject mData = new JSONObject(message);
                                if (mData.has(mData.getString(Common.Constant_Class.EMAIL_ADDRESS))) {
                                    message = mData.getString(Common.Constant_Class.EMAIL_ADDRESS);
                                } else if (mData.has(mData.getString(Common.Constant_Class.MOBILE))) {
                                    message = mData.getString(Common.Constant_Class.MOBILE);
                                }
                                Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        } else {
            Toast.makeText(MyProfileActivity.this, "" + Common.Constant_Class.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }


    private void SyncUser(String profile_id) {
        if (Common.isOnline(this)) {
            Common.showProgressDialog(this);

            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.PROFILE_ID, profile_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sync_url = Common.Constant_Class.SYNC_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                mListProfileData = Common.SaveProfile(mJsondata);
                            }
                            setupViewPager(viewPager);
                            tabLayout.setupWithViewPager(viewPager);
                        } else {
                            Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                        Common.hideProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Common.hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }


    private void alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).show();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case Common.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    searchView.setQueryHint(result.get(0));
                    searchView.setQuery(result.get(0), true);
                }
                break;
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Fragment fragment = new FragmentDrawer();
            getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();

            //   Intent mIntent = new Intent(MyProfileActivity.this, MainActivity.class);
            //   mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //   startActivity(mIntent);
            overridePendingTransition(0, 0);
            finish();


            // your code here
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (personal == null) {
            personal = new PersonalFragment();
        }
        personal.setArguments(mBundle);
        if (business == null) {
            business = new BusinessFragment();
        }
        business.setArguments(mBundle);

        if (family == null) {
            family = new FamilyFragment();
        }
        family.setArguments(mBundle);

        adapter.addFrag(personal, Common.Constant_Class.PERSONAL);
        adapter.addFrag(business, Common.Constant_Class.BUSINESS);
        adapter.addFrag(family, Common.Constant_Class.FAMILY);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

   /* @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }*/

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
