package com.krs.vastipatrak.activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.krs.vastipatrak.fragments.FamilyTreeFragment;
import com.krs.vastipatrak.fragments.FragmentDrawer;
import com.krs.vastipatrak.fragments.PersonalFragment;
import com.krs.vastipatrak.fragments.RelativeFragment;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;
import com.krs.vastipatrak.utils.ConnectivityReceiver;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
import static com.krs.vastipatrak.utils.AppConstants.INIT_TIMEOUT;
import static com.krs.vastipatrak.utils.AppConstants.TITLE_SPOUSE_BLOOD_GROUP;

public class ProfileActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, ConnectivityReceiver.ConnectivityReceiverListener {
    public static boolean isEnable = false;
    public static String chooseFragment = "";
    private final String TAG = ProfileActivity.class.getSimpleName();
    Snackbar snackbar;
    String id = "";
    private SharedPreferences.Editor mEditor;
    private SearchView searchView;
    private Bundle mBundle = null;
    private String valid = "";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private ListProfileData mListProfileData1 = null;
    private Fragment personal = null;
    private Fragment business = null;
    private Fragment family = null;
    private Fragment relative = null;
    private Fragment familytree = null;
    private SharedPreferences mSharedPreferences = null;
    private boolean isBackPressed = false;
    private MenuItem saveItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        MemoryAllocation();
        ToolbarSetup();

        if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, true)) {
            id = mSharedPreferences.getString(AppConstants.USER_ID, "");
        } else {
            id = mSharedPreferences.getString(AppConstants.PROFILE_ID, "");
        }
        checkConnection();
        SyncUserWS(id);
    }


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
    }

    @Nullable
    public ListProfileData getMyData() {
        return mListProfileData1;
    }

    @SuppressLint("CommitPrefEdits")
    private void MemoryAllocation() {
        snackbar = Snackbar.make(findViewById(R.id.ll_profile), R.string.not_connected, Snackbar.LENGTH_INDEFINITE);

        toolbar = findViewById(R.id.toolbar);
        mSharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            isEnable = mBundle.getBoolean("isEnable", false);
        }
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void ToolbarSetup() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("My Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "step setNavigationOnClickListener");
                backNavigation();
            }
        });
    }

    private void backNavigation() {

        Utility.hideKeyboard(this);
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        saveItem = menu.findItem(R.id.action_save);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                /*Bundle mBundle = new Bundle();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                SearchFragment searchFragment = new SearchFragment();
               // IAdminControl = (IAdminControl) searchFragment;
                ((SearchFragment) searchFragment).setmContext(ProfileActivity.this);
                mBundle.putString(AppConstants.QUERY, query);
                searchFragment.setArguments(mBundle);
                fragmentTransaction.add(R.id.fm_container_body, searchFragment).commit();
                finish();*/
                Log.d(TAG, "step onQueryTextSubmit");
                Intent mIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.putExtra(AppConstants.QUERY, query);
                startActivity(mIntent);
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem export = menu.findItem(R.id.action_export);
        export.setVisible(false);

       /* MenuItem admins = menu.findItem(R.id.action_admins);
        admins.setVisible(false);

        MenuItem scan_image = menu.findItem(R.id.action_scan_image);
        scan_image.setVisible(false);

        MenuItem scan_qr = menu.findItem(R.id.action_scan);
        scan_qr.setVisible(false);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent mIntent = new Intent(ProfileActivity.this, AdvanceSearchActivity.class);
                startActivity(mIntent);
                Log.d(TAG, "step action_filter");
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }
        });
*/
        saveMenuVisible();

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utility.promptSpeechInput(ProfileActivity.this);
                return false;
            }
        });


        MenuItem action_toggle = menu.findItem(R.id.action_toggle);
        if (!mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, false) && mSharedPreferences.getString(AppConstants.ROLE, AppConstants.USER).equals(AppConstants.ADMIN)) {
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

                Intent mIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                if (mBundle != null) {
                    mBundle.putBoolean(getString(R.string.isEnable), isEnable);
                } else {
                    mBundle = new Bundle();
                    mBundle.putBoolean(getString(R.string.isEnable), isEnable);
                }

                mIntent.putExtras(mBundle);
                startActivity(mIntent);
                finish();

                return false;
            }
        });

        saveItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                saveProfile(0);
                return false;
            }
        });
        return true;
    }

    private void saveMenuVisible() {
        if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, true)) {
            if (viewPager.getCurrentItem() != 4 && viewPager.getCurrentItem() != 3) {
                saveItem.setVisible(true);
            }
        } else {
            if (isEnable && mSharedPreferences.getString(AppConstants.ROLE, AppConstants.USER).equals(AppConstants.ADMIN)) {
                saveItem.setVisible(true);
            } else {
                saveItem.setVisible(false);
            }
        }
    }

    private void saveProfile(int state) {
        ListProfileData mListProfileData = new ListProfileData();
        valid = "";
        if (personal != null && ((PersonalFragment) personal).edtFName != null && business != null && ((BusinessFragment) business).edtOccupation != null) {
            // Personal Details
            String fname = ((PersonalFragment) personal).edtFName.getText().toString().trim();
            String lname = ((PersonalFragment) personal).edtLName.getText().toString().trim();
            String FatherName = ((PersonalFragment) personal).edtFatherName.getText().toString().trim();
            String MotherName = ((PersonalFragment) personal).edtMotherName.getText().toString().trim();
            String Education = ((PersonalFragment) personal).edtEducation.getText().toString().trim();
            String BPlace = ((PersonalFragment) personal).txtBPlace.getText().toString().trim();
            String NPlace = ((PersonalFragment) personal).txtNPlace.getText().toString().trim();
            String Gotra = ((PersonalFragment) personal).spinnerGotra.getSelectedItem().toString().trim();
            String Mobile = ((PersonalFragment) personal).edtMobile.getText().toString().trim();
            String Address = ((PersonalFragment) personal).edtAddress.getText().toString().trim();
            String Eaddress = ((PersonalFragment) personal).edt_Eaddress.getText().toString().trim();
            String city = ((PersonalFragment) personal).txtCity.getText().toString().trim();

            if (!Eaddress.equalsIgnoreCase("")) {
                if (Utility.isValidEmail(Eaddress)) {
                    valid = "Email is not valid Format";
                }
            }
            String phone = Objects.requireNonNull(((PersonalFragment) personal).edt_phone).getText().toString().trim();
            String bdate = Objects.requireNonNull(((PersonalFragment) personal).edtbdate).getText().toString().trim();
            bdate = Utility.parseDateToddMMyyyy(bdate, Utility.ddMMMyyyy, Utility.yyyy_MM_dd);
            if (!bdate.isEmpty()) {
                if (!Utility.isThisDateValid(bdate, Utility.yyyy_MM_dd)) {
                    valid = "Birth Date is not valid Format";
                }
               /* if (!Utility.isValidDate(bdate) && valid.isEmpty()) {
                    valid = "Birth Date is not valid ";
                }*/
            }


            String time = Objects.requireNonNull(((PersonalFragment) personal).edtbTime).getText().toString().trim();
            if (!time.isEmpty()) {
                if (Utility.IsValidate(time)) {
                    valid = "Birth Time is not valid 24 Hours";
                }
            }

            String gender = ((PersonalFragment) personal).gender;
            String bgroup = ((PersonalFragment) personal).spinnerBlood.getSelectedItem().toString().trim();
            String str_profile_hash = ((PersonalFragment) personal).str_profile_hash;
            String str_father_hash = ((PersonalFragment) personal).str_father_hash;
            String str_mother_hash = ((PersonalFragment) personal).str_mother_hash;
            boolean bdate_rem = ((PersonalFragment) personal).chk_profile_bdate_rem.isChecked();


            // Business Details
            String occupation = ((BusinessFragment) business).edtOccupation.getText().toString().trim();
            String Work = ((BusinessFragment) business).edtWork.getText().toString().trim();
            String OMobile = ((BusinessFragment) business).edtOMobile.getText().toString().trim();
            String OAddress = ((BusinessFragment) business).edtOAddress.getText().toString().trim();

            // Familty Details
            String spouseName = "", SpouseFName = "", MSpouseName = "", mdate = "", sdate = "", str_fspouse_hash = "", str_mspouse_hash = "", str_spouse_hash = "";
            String sponseNative = "", sponseMobile = "", spouse_bg = "", spouse_edu = "";
            //  boolean chk_spouse_bdate_rem = false, chk_m_bdate_rem = false;
            LinearLayout child_container = null;
            ArrayList<Integer> lst_delID = null;
            try {
                spouseName = ((FamilyFragment) family).edtSpouseName.getText().toString().trim();
                spouse_bg = ((FamilyFragment) family).sp_spouse_blood.getSelectedItem().toString();
                sponseMobile = ((FamilyFragment) family).edtsponse_mobile.getText().toString().trim();
                sponseNative = ((FamilyFragment) family).txt_sponse_nplace.getText().toString().trim();
                SpouseFName = ((FamilyFragment) family).edtSpouseFName.getText().toString().trim();
                MSpouseName = ((FamilyFragment) family).edtMSpouseName.getText().toString().trim();
                mdate = ((FamilyFragment) family).edt_mdate.getText().toString().trim();

                mdate = Utility.parseDateToddMMyyyy(mdate, Utility.ddMMMyyyy, Utility.yyyy_MM_dd);
                if (!mdate.isEmpty()) {
                    if (!Utility.isThisDateValid(mdate, "yyyy-mm-dd")) {
                        valid = "Marriage Date is not valid Format";
                    }
                }

                sdate = ((FamilyFragment) family).edtsponse_bdate.getText().toString().trim();
                spouse_edu = ((FamilyFragment) family).edt_spouse_edu.getText().toString().trim();

                if (!sdate.isEmpty()) {
                    sdate = Utility.parseDateToddMMyyyy(sdate, Utility.ddMMMyyyy, Utility.yyyy_MM_dd);
                }
                if (!sdate.isEmpty()) {
                    if (!Utility.isThisDateValid(sdate, "yyyy-mm-dd")) {
                        valid = "Sponse Birth Date is not valid Format";
                    }
                    /*if (!Utility.isValidDate(sdate) && valid.isEmpty()) {
                        valid = "Sponse Birth Date is not valid ";
                    }*/
                }


                str_fspouse_hash = ((FamilyFragment) family).str_fspouse_hash;
                str_mspouse_hash = ((FamilyFragment) family).str_mspouse_hash;
                str_spouse_hash = ((FamilyFragment) family).str_spouse_hash;
                child_container = ((FamilyFragment) family).child_container;
                lst_delID = ((FamilyFragment) family).lst_delID;

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (valid.isEmpty()) {

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
                if (!spouse_bg.equalsIgnoreCase(TITLE_SPOUSE_BLOOD_GROUP)) {
                    mListProfileData.setSponse_bg(spouse_bg);
                }
                mListProfileData.setSpouse_education(spouse_edu);
                mListProfileData.setSponse_bdate(sdate);
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
                valid = "";
            } else {
                Toast.makeText(ProfileActivity.this, "" + valid, Toast.LENGTH_SHORT).show();
            }
        }
        /*if (valid.isEmpty() && state == 1) {
            Utility.hideKeyboard(this);
            finish();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }*/

    }

    private void setProfileJsonObject(@NonNull ListProfileData mListProfileData, @Nullable LinearLayout child_container, @NonNull ArrayList<Integer> lst_delID) {

        JSONObject mJsonObject = null;
        // Personal Details
        try {
            mJsonObject = new JSONObject();
            mJsonObject.put(AppConstants.IS_UPDATE, "1");
            mJsonObject.put(AppConstants.USER_ID, Objects.requireNonNull(mSharedPreferences).getString(AppConstants.USER_ID, ""));
            mJsonObject.put(AppConstants.FIRST_NAME, mListProfileData.getFirst_name());
            mJsonObject.put(AppConstants.LAST_NAME, mListProfileData.getLast_name());
            mJsonObject.put(AppConstants.FATHER_NAME, mListProfileData.getFather_name());
            mJsonObject.put(AppConstants.MOTHER_NAME, mListProfileData.getMother_name());
            mJsonObject.put(AppConstants.BIRTH_DATE, mListProfileData.getBirth_date());
            mJsonObject.put(AppConstants.BIRTH_TIME, mListProfileData.getBirth_time());
            mJsonObject.put(AppConstants.BIRTH_PLACE, mListProfileData.getBirth_place());
            mJsonObject.put(AppConstants.MOBILE, mListProfileData.getMobile());
            mJsonObject.put(AppConstants.PHONE, mListProfileData.getPhone());
            mJsonObject.put(AppConstants.CITY, mListProfileData.getCity());
            mJsonObject.put(AppConstants.BLOOD_GROUP, mListProfileData.getBlood_group());
            mJsonObject.put(AppConstants.GENDER, mListProfileData.getGender());
            mJsonObject.put(AppConstants.GOTRA, mListProfileData.getGotra());
            mJsonObject.put(AppConstants.EMAIL_ADDRESS, mListProfileData.getEmail_address());
            mJsonObject.put(AppConstants.ADDRESS, mListProfileData.getAddress());
            mJsonObject.put(AppConstants.NATIVE_PLACE, mListProfileData.getNative_place());
            mJsonObject.put(AppConstants.EDUCATION, mListProfileData.getEducation());
            if (!mListProfileData.getStr_profile_hash().equalsIgnoreCase("")) {
                mJsonObject.put(AppConstants.PROFILE_PIC, mListProfileData.getStr_profile_hash());
            }
            if (!mListProfileData.getStr_mother_hash().equalsIgnoreCase("")) {
                mJsonObject.put(AppConstants.IMG_MOTHER, mListProfileData.getStr_mother_hash());
            }
            if (!mListProfileData.getStr_father_hash().equalsIgnoreCase("")) {
                mJsonObject.put(AppConstants.IMG_FATHER, mListProfileData.getStr_father_hash());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Business details
        try {
            assert mJsonObject != null;
            mJsonObject.put(AppConstants.OCCUPATION, mListProfileData.getOccupation());
            mJsonObject.put(AppConstants.OFFICE_MOBILE, mListProfileData.getOffice_mobile());
            mJsonObject.put(AppConstants.WORK, mListProfileData.getWork());
            mJsonObject.put(AppConstants.OFFICE_ADDRESS, mListProfileData.getOffice_address());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Family Details
        try {
            // mListProfileData.getch
            Objects.requireNonNull(mJsonObject).put(AppConstants.MARRIAGE_DATE, mListProfileData.getMarriage_date());
            mJsonObject.put(AppConstants.SPOUSE_NAME, mListProfileData.getSpouse_name());
            mJsonObject.put(AppConstants.SPOUSE_BG, mListProfileData.getSponse_bg());
            mJsonObject.put(AppConstants.SPOUSE_EDU, mListProfileData.getSpouse_education());
            mJsonObject.put(AppConstants.SPOUSE_BDATE, mListProfileData.getSponse_bdate());
            mJsonObject.put(AppConstants.SPOUSE_MOBILE, mListProfileData.getSponse_mobile());
            mJsonObject.put(AppConstants.SPOUSE_NATIVE, mListProfileData.getSponse_native());

            mJsonObject.put(AppConstants.SPOUSE_FATHER_NAME, mListProfileData.getSfather_name());
            mJsonObject.put(AppConstants.SPOUSE_MOTHER_NAME, mListProfileData.getSmother_name());

            if (!mListProfileData.getStr_spouse_hash().equalsIgnoreCase("")) {
                mJsonObject.put(AppConstants.IMG_SPOUSE, mListProfileData.getStr_spouse_hash());
            }
            if (!mListProfileData.getStr_mspouse_hash().equalsIgnoreCase("")) {
                mJsonObject.put(AppConstants.IMG_SMOTHER, mListProfileData.getStr_mspouse_hash());
            }
            if (!mListProfileData.getStr_fspouse_hash().equalsIgnoreCase("")) {
                mJsonObject.put(AppConstants.IMG_SFATHER, mListProfileData.getStr_fspouse_hash());
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
                        mJsonObject_Child.put(AppConstants.CHILD_DELETE, "true");
                    }
                    mJsonObject_Child.put(AppConstants.CHILD_NAME, Objects.requireNonNull(mViewholder.edtchild_name).getText());
                    String child_bday = Objects.requireNonNull(mViewholder.edtchild_bdate.getText().toString().trim());
                    if (!child_bday.isEmpty()) {
                        child_bday = Utility.parseDateToddMMyyyy(child_bday, Utility.ddMMMyyyy, Utility.yyyy_MM_dd);
                    }

                    if (!child_bday.isEmpty()) {
                        if (!Utility.isThisDateValid(child_bday, "yyyy-mm-dd")) {
                            valid = "Child Birth Date is not valid Format";
                        }
                       /* if (!Utility.isValidDate(child_bday) && valid.isEmpty()) {
                            valid = "Child birthdate is not valid ";
                        }*/
                    }


                    boolean child_interest = Objects.requireNonNull(mViewholder.tbtn_interest).isChecked();
                    String child_gender = mViewholder.gender;
                    String child_btime = Objects.requireNonNull(mViewholder.edtchild_btime).getText().toString();
                    String child_bplace = Objects.requireNonNull(mViewholder.txt_child_bplace).getText().toString();
                    String child_mobile = Objects.requireNonNull(mViewholder.edtMobile).getText().toString();
                    String child_blood = Objects.requireNonNull(mViewholder.spinnerBlood).getSelectedItem().toString();
                    boolean chk_bdate_rem = mViewholder.chk_child_bdate_rem.isChecked();
                    boolean chk_married = mViewholder.chk_child_marriage.isChecked();
                    mJsonObject_Child.put(AppConstants.IS_MARRIED, chk_married);
                    mJsonObject_Child.put(AppConstants.BLOOD_GROUP, child_blood);
                    mJsonObject_Child.put(AppConstants.MOBILE, child_mobile);
                    mJsonObject_Child.put(AppConstants.IS_INTERESTED, child_interest);
                    mJsonObject_Child.put(AppConstants.GENDER, child_gender);
                    mJsonObject_Child.put(AppConstants.BIRTH_TIME, child_btime);
                    mJsonObject_Child.put(AppConstants.BIRTH_PLACE, child_bplace);
                    mJsonObject_Child.put(AppConstants.CHILD_BDAY, child_bday);
                    mJsonObject_Child.put(AppConstants.CHILD_EDU, Objects.requireNonNull(mViewholder.edt_child_edu).getText());
                    mJsonObject_Child.put(AppConstants.CHILD_WORK, Objects.requireNonNull(mViewholder.edtchild_work).getText());
                    if (!mViewholder.ImgHash.equalsIgnoreCase("")) {
                        mJsonObject_Child.put(AppConstants.CHILD_IMAGE, mViewholder.ImgHash);
                    }
                    mJsonArray.put(mJsonObject_Child);
                    mJsonObject.put(AppConstants.CHILDS, mJsonArray);

                }

                for (int i = 0; i < lst_delID.size(); i++) {

                    JSONObject mJsonObject_Child = new JSONObject();
                    if (lst_delID.get(i) != 0) {
                        mJsonObject_Child.put("id", lst_delID.get(i));
                        mJsonObject_Child.put(AppConstants.CHILD_DELETE, "true");
                        mJsonArray.put(mJsonObject_Child);
                        mJsonObject.put(AppConstants.CHILDS, mJsonArray);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (valid.equalsIgnoreCase("")) {
            call_profile_ws(mJsonObject);
        } else {
            Toast.makeText(ProfileActivity.this, "" + valid, Toast.LENGTH_SHORT).show();
            valid = "";
        }
    }

    private void call_profile_ws(@Nullable JSONObject mJsonObject) {
        if (Utility.isOnline(this) && mSharedPreferences != null) {
            try {
                if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, true)) {
                    mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));

                } else {
                    mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                    mJsonObject.put(AppConstants.UPDATE_USER_ID, mSharedPreferences.getString(AppConstants.PROFILE_ID, ""));

                }
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!(isFinishing())) {
                            Utility.showProgressDialog(ProfileActivity.this);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.PROFILE_URL, mJsonObject, response -> {
                Log.d(TAG,"CallProfileWS: "+ response.toString());
                try {
                    Utility.hideProgressDialog();
                    String success = response.getString(AppConstants.SUCCESS);
                    String message = response.getString(AppConstants.MESSAGE);
                    if (success.equalsIgnoreCase(AppConstants.TRUE)) {
                        String data = response.getString(AppConstants.DATA);
                        JSONObject mData = new JSONObject(data);
                        mListProfileData1 = Utility.SaveProfile(mData);
                        if ("1".equalsIgnoreCase("1")) {
                            if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, false)) {
                                mEditor.putString(AppConstants.PROFILE_PIC_URL, mData.getString(AppConstants.PROFILE_PIC_URL));
                                mEditor.putString(AppConstants.FIRST_NAME, mData.getString(AppConstants.FIRST_NAME));
                                mEditor.putString(AppConstants.LAST_NAME, mData.getString(AppConstants.LAST_NAME));
                                mEditor.putString(AppConstants.OFFICE_LAT, mData.getString(AppConstants.OFFICE_LAT));
                                mEditor.putString(AppConstants.OFFICE_LNG, mData.getString(AppConstants.OFFICE_LNG));
                                mEditor.putString(AppConstants.HOME_LAT, mData.getString(AppConstants.HOME_LAT));
                                mEditor.putString(AppConstants.HOME_LNG, mData.getString(AppConstants.HOME_LNG));
                                mEditor.apply();
                                AppController.getInstance().isUpdate = true;
                            }
                            //  Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            alert(message);
                        } else {
                            setupViewPager(viewPager);
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    } else {
                        try {
                            JSONObject mData = new JSONObject(message);
                            if (mData.has(mData.getString(AppConstants.EMAIL_ADDRESS))) {
                                message = mData.getString(AppConstants.EMAIL_ADDRESS);
                            } else if (mData.has(mData.getString(AppConstants.MOBILE))) {
                                message = mData.getString(AppConstants.MOBILE);
                            } else {

                                if (response.has(AppConstants.ERROR_CODE)) {
                                    String error = response.getString(AppConstants.ERROR_CODE);
                                    if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                                        Intent mIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mIntent);
                                        finish();
                                    }
                                }
                            }
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        } else {
            Toast.makeText(ProfileActivity.this, "" + AppConstants.NO_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }


    private void SyncUserWS(String profile_id) {
        if (Utility.isOnline(this)) {
            Utility.showProgressDialog(this);
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
                mJsonObject.put(AppConstants.PROFILE_ID, profile_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sync_url = AppConstants.SYNC_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "SyncUserWS Response: " + response.toString());

                    try {
                        String success = response.getString(AppConstants.SUCCESS);
                        String message = response.getString(AppConstants.MESSAGE);

                        if (success.equalsIgnoreCase(AppConstants.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                mListProfileData1 = Utility.SaveProfile(mJsondata);
                            }
                            setupViewPager(viewPager);
                            tabLayout.setupWithViewPager(viewPager);
                        } else {
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (response.has(AppConstants.ERROR_CODE)) {
                                String error = response.getString(AppConstants.ERROR_CODE);
                                if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                                    Intent mIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    finish();
                                }
                            }
                        }
                        Utility.hideProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Utility.hideProgressDialog();
                    Toast.makeText(ProfileActivity.this, "Please try again Something went wrong!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                    return params;
                }
            };

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));

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

        if (chooseFragment.equalsIgnoreCase(FamilyFragment.class.getSimpleName())) {
            family.onActivityResult(requestCode, resultCode, data);
        } else if (chooseFragment.equalsIgnoreCase(PersonalFragment.class.getSimpleName())) {
            personal.onActivityResult(requestCode, resultCode, data);
        }

        switch (requestCode) {
            case Utility.REQ_CODE_SPEECH_INPUT: {
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

            Utility.hideKeyboard(this);
            Fragment fragment = new FragmentDrawer();
            getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
            Log.d(TAG, "step onKeyDown");
            finish();
            setResult(11);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

            /*if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, false)) {
              //  saveProfile(1);
            } else {

            }*/
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

        if (familytree == null) {
            familytree = new FamilyTreeFragment();
        }
        familytree.setArguments(mBundle);

        if (relative == null) {
            relative = new RelativeFragment();
        }


        adapter.addFrag(personal, AppConstants.PERSONAL);
        adapter.addFrag(business, AppConstants.BUSINESS);
        adapter.addFrag(family, AppConstants.FAMILY);
        adapter.addFrag(familytree, AppConstants.FAMILY_TREE);
        adapter.addFrag(relative, AppConstants.RELATIVES);

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected");
                saveMenuVisible();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged");
            }
        });
    }

    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            if (snackbar != null) {
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        } else {
            if (snackbar != null) {
                if (snackbar.isShownOrQueued()) {
                    snackbar.dismiss();
                }
            }
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

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
