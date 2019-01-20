package com.yadav.vastipatrak.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.yadav.vastipatrak.R;
import com.yadav.vastipatrak.adapter.ItemArrayAdapter;
import com.yadav.vastipatrak.app.AppController;
import com.yadav.vastipatrak.fragments.BusinessSearch;
import com.yadav.vastipatrak.fragments.FamilySearch;
import com.yadav.vastipatrak.fragments.PersonalSearch;
import com.yadav.vastipatrak.utils.Common;
import com.yadav.vastipatrak.utils.ConnectivityReceiver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yadav.vastipatrak.utils.Common.Constant_Class.TITLE_CHILD_BLOOD_GROUP;
import static com.yadav.vastipatrak.utils.Common.Constant_Class.TITLE_SPOUSE_BLOOD_GROUP;
import static com.yadav.vastipatrak.utils.Common.ddMMMyyyy;
import static com.yadav.vastipatrak.utils.Common.yyyy_MM_dd;

public class AdvanceSearchActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, ConnectivityReceiver.ConnectivityReceiverListener {

    static final int CUSTOM_DIALOG_ID = 0;
    public static String chooseFragment = "";
    private final String[] READ_CONTACT_PERMS = {Manifest.permission.READ_CONTACTS};
    private final int READ_CONTACT_REQUEST = 3;
    RecyclerView recyclerView;
    Snackbar snackbar;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private Fragment personal;
    private Fragment business;
    private Fragment family;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);
        MemoryAllocation();
        ToolbarSetup();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Common.canReadContacts(this)) {
                requestPermissions(READ_CONTACT_PERMS, READ_CONTACT_REQUEST);
            }
        }
        checkConnection();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void MemoryAllocation() {
        snackbar = Snackbar.make(findViewById(R.id.ll_filter), R.string.not_connected, Snackbar.LENGTH_INDEFINITE);
        viewPager = findViewById(R.id.viewpager);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);
        SharedPreferences mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_FILTER, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACT_REQUEST) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void ToolbarSetup() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.title_filter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNavigation();
            }
        });
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        backNavigation();
    }*/

    private void backNavigation() {
        /*Fragment fragment = new FragmentDrawer();
        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
        Intent mIntent = new Intent(AdvanceSearchActivity.this, MainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);*/
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        personal = new PersonalSearch();

        business = new BusinessSearch();

        family = new FamilySearch();
        adapter.addFrag(personal, Common.Constant_Class._PERSONAL);
        adapter.addFrag(business, Common.Constant_Class._BUSINESS);
        adapter.addFrag(family, Common.Constant_Class._FAMILY);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @SuppressWarnings("ConstantConditions")
    public void callAdvanceSearchWS() {
        JSONObject mJsonObject = new JSONObject();
        try {
            String valid = "";
            ArrayList<String> lstProceed = new ArrayList<>();
            if (((PersonalSearch) personal).edtFName != null) {
                String strFName = ((PersonalSearch) personal).edtFName.getText().toString().trim();
                String strLName = ((PersonalSearch) personal).edtLName.getText().toString().trim();
                String strFatherName = ((PersonalSearch) personal).edtFatherName.getText().toString().trim();
                String strMotherName = ((PersonalSearch) personal).edtMotherName.getText().toString().trim();
                String strEducation = ((PersonalSearch) personal).edt_Education.getText().toString().trim();
                String strBPlace = ((PersonalSearch) personal).txtBPlace.getText().toString().trim();
                String strNPlace = ((PersonalSearch) personal).txtNPlace.getText().toString().trim();
                String strMobile = ((PersonalSearch) personal).edtMobile.getText().toString().trim();
                String strAddress = ((PersonalSearch) personal).edtAddress.getText().toString().trim();
                String strphone = ((PersonalSearch) personal).edt_phone.getText().toString().trim();
                String strbdateFrom = ((PersonalSearch) personal).edtbdateFrom.getText().toString().trim();
                strbdateFrom = Common.parseDateToddMMyyyy(strbdateFrom, ddMMMyyyy, yyyy_MM_dd);
                String strbdateTo = ((PersonalSearch) personal).edtbdateTo.getText().toString().trim();
                strbdateTo = Common.parseDateToddMMyyyy(strbdateTo, ddMMMyyyy, yyyy_MM_dd);

                String strEaddress = ((PersonalSearch) personal).edt_Eaddress.getText().toString().trim();
                String strCity = ((PersonalSearch) personal).txtCity.getText().toString().trim();

                String strGotra = "Gotra";
                if (((PersonalSearch) personal).spinnerGotra.getSelectedItem() != null) {
                    strGotra = ((PersonalSearch) personal).spinnerGotra.getSelectedItem().toString().trim();
                }

                String bgroup = ((PersonalSearch) personal).spinnerBlood.getSelectedItem().toString().trim();

                String sp_user_start_age = ((PersonalSearch) personal).sp_user_start_age.getSelectedItem().toString().trim();
                String sp_user_end_age = ((PersonalSearch) personal).sp_user_end_age.getSelectedItem().toString().trim();

                if (!sp_user_start_age.equalsIgnoreCase(getString(R.string.AGE)) && !sp_user_end_age.equalsIgnoreCase(getString(R.string.AGE))) {
                    String start = sp_user_start_age.replace("Age ", "");
                    String end = sp_user_end_age.replace("Age ", "");
                    if (Integer.parseInt(start) <= Integer.parseInt(end)) {
                        mJsonObject.put(Common.Constant_Class.USER_START_AGE, sp_user_start_age.replace("Age ", ""));
                        lstProceed.add("User Age From: " + sp_user_start_age);
                        mJsonObject.put(Common.Constant_Class.USER_END_AGE, sp_user_end_age.replace("Age ", ""));
                        lstProceed.add("User Age To: " + sp_user_end_age);
                    } else {
                        valid = "User 'From' Age is larger than 'End' Age";
                    }
                }

                String gender;

                if (bgroup.equalsIgnoreCase(Common.Constant_Class.TITLE_BLOOD_GROUP)) {
                    bgroup = "";
                }

                if (((PersonalSearch) personal).rbtnF.isChecked()) {
                    gender = "female";
                } else if (((PersonalSearch) personal).rbtnM.isChecked()) {
                    gender = "male";
                } else {
                    gender = "both";
                }


                if (!strbdateFrom.equalsIgnoreCase("")) {
                    if (!Common.isThisDateValid(strbdateFrom, "yyyy-MM-dd")) {
                        valid = "Birth Date From is not valid Format";
                    }
                    if (!strbdateTo.equalsIgnoreCase("")) {
                        if (!Common.isThisDateValid(strbdateTo, "yyyy-MM-dd")) {
                            valid = "Birth Date To is not valid Format";
                        }
                    } else {
                        valid = "Enter Birthdate To";
                    }
                }

                if (!strFName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.FIRST_NAME, strFName);
                    lstProceed.add("FirstName: " + strFName);
                }
                if (!strLName.equalsIgnoreCase("")) {
                    lstProceed.add("LastName: " + strLName);
                    mJsonObject.put(Common.Constant_Class.LAST_NAME, strLName);
                }
                if (!strFatherName.equalsIgnoreCase("")) {
                    lstProceed.add("FatherName: " + strFatherName);
                    mJsonObject.put(Common.Constant_Class.FATHER_NAME, strFatherName);
                }
                if (!strMotherName.equalsIgnoreCase("")) {
                    lstProceed.add("MotherName: " + strMotherName);
                    mJsonObject.put(Common.Constant_Class.MOTHER_NAME, strMotherName);
                }
                if (!strbdateFrom.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.FROM_BIRTH_DATE, strbdateFrom);
                    if (!strbdateTo.equalsIgnoreCase("")) {
                        mJsonObject.put(Common.Constant_Class.TO_BIRTH_DATE, strbdateTo);
                    } else {
                        valid = "Enter birth date To";
                    }
                    lstProceed.add("Birthdate From: " + ((PersonalSearch) personal).edtbdateFrom.getText().toString());
                    lstProceed.add("Birthdate To: " + ((PersonalSearch) personal).edtbdateTo.getText().toString());
                }
                if (!strBPlace.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.BIRTH_PLACE, strBPlace);
                    lstProceed.add("BirthPalace: " + strBPlace);
                }

                if (!strMobile.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.MOBILE, strMobile);
                    lstProceed.add("Mobile: " + strMobile);
                }
                if (!strphone.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.PHONE, strphone);
                    lstProceed.add("Phone: " + strphone);
                }
                if (!strGotra.equalsIgnoreCase("") && !strGotra.equalsIgnoreCase("Gotra")) {
                    mJsonObject.put(Common.Constant_Class.GOTRA, strGotra);
                    lstProceed.add("Gotra: " + strGotra);
                }
                if (!strNPlace.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.NATIVE_PLACE, strNPlace);
                    lstProceed.add("Native Place: " + strNPlace);
                }
                if (!strCity.equalsIgnoreCase("") && !strCity.toLowerCase().contains(getResources().getString(R.string.press_for_city))) {
                    String city = strCity.replace("City: ", "");
                    mJsonObject.put(Common.Constant_Class.CITY, city);
                    lstProceed.add("City: " + city);
                }
                if (!strEducation.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.EDUCATION, strEducation);
                    lstProceed.add("Education: " + strEducation);
                }
                if (!strEaddress.equalsIgnoreCase("")) {
                    if (Common.isValidEmail(strEaddress)) {
                        valid = "Email is not valid Format";
                    } else {
                        mJsonObject.put(Common.Constant_Class.EMAIL_ADDRESS, strEaddress);
                        lstProceed.add("Email: " + strEaddress);
                    }
                }
                if (!strAddress.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.ADDRESS, strAddress);
                    lstProceed.add("Address: " + strAddress);
                }
                if (!bgroup.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.BLOOD_GROUP, bgroup);
                    lstProceed.add("BloodGroup: " + bgroup);
                }
                if (!gender.equalsIgnoreCase("")) {
                    if (gender.equalsIgnoreCase("male")) {
                        lstProceed.add("Gender: " + "Male");
                        mJsonObject.put(Common.Constant_Class.GENDER, gender);
                    } else if (gender.equalsIgnoreCase("female")) {
                        lstProceed.add("Gender: " + "Female");
                        mJsonObject.put(Common.Constant_Class.GENDER, gender);
                    } else {
                        lstProceed.add("Gender: " + "Both");
                    }
                }
            }

            if (((BusinessSearch) business).edtOccupation.getText() != null) {

                String strOccupation, strWork, strOMobile, strOAddress;
                strOccupation = ((BusinessSearch) business).edtOccupation.getText().toString().trim();
                strWork = ((BusinessSearch) business).edtWork.getText().toString().trim();
                strOMobile = ((BusinessSearch) business).edtOMobile.getText().toString().trim();
                strOAddress = ((BusinessSearch) business).edtOAddress.getText().toString().trim();

                if (!strOccupation.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.OCCUPATION, strOccupation);
                    lstProceed.add("Occupation: " + strOccupation);
                }
                if (!strWork.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.WORK, strWork);
                    lstProceed.add("Work: " + strWork);
                }
                if (!strOMobile.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.OFFICE_MOBILE, strOMobile);
                    lstProceed.add("Office Mobile: " + strOMobile);
                }
                if (!strOAddress.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.OFFICE_ADDRESS, strOAddress);
                    lstProceed.add("Office Address: " + strOAddress);
                }
            }

            if (((FamilySearch) family).edtSpouseName != null) {

                boolean child_married = ((FamilySearch) family).chk_child_marriage.isChecked();

                String strmdate_from = ((FamilySearch) family).edt_mdate_from.getText().toString().trim();
                strmdate_from = Common.parseDateToddMMyyyy(strmdate_from, ddMMMyyyy, yyyy_MM_dd);

                String strmdate_to = ((FamilySearch) family).edt_mdate_to.getText().toString().trim();
                strmdate_to = Common.parseDateToddMMyyyy(strmdate_to, ddMMMyyyy, yyyy_MM_dd);

                String childBdateFrom = ((FamilySearch) family).edt_cdate_from.getText().toString().trim();
                childBdateFrom = Common.parseDateToddMMyyyy(childBdateFrom, ddMMMyyyy, yyyy_MM_dd);

                String childBdateTo = ((FamilySearch) family).edt_cdate_to.getText().toString().trim();
                childBdateTo = Common.parseDateToddMMyyyy(childBdateTo, ddMMMyyyy, yyyy_MM_dd);

                String strSpouseName = ((FamilySearch) family).edtSpouseName.getText().toString().trim();
                String strSpouseFName = ((FamilySearch) family).edtSpouseFName.getText().toString().trim();
                String strSpouseMName = ((FamilySearch) family).edtSpouseMName.getText().toString().trim();
                String strchild_name = ((FamilySearch) family).edtchild_name.getText().toString().trim();
                String strcedu = ((FamilySearch) family).edtchildEdu.getText().toString().trim();
                String strchild_work = ((FamilySearch) family).edtchild_work.getText().toString().trim();
                String childMobile = ((FamilySearch) family).edtcmobile.getText().toString().trim();
                String childBplace = ((FamilySearch) family).txtchildBplace.getText().toString().trim();
                String bgroup = ((FamilySearch) family).spinnerBlood.getSelectedItem().toString().trim();

                String sp_spouse_blood = ((FamilySearch) family).sp_spouse_blood.getSelectedItem().toString().trim();
                String edtSpouseEdu = ((FamilySearch) family).edtSpouseEdu.getText().toString().trim();
                String sp_spouse_start_age = ((FamilySearch) family).sp_spouse_start_age.getSelectedItem().toString().trim();
                String sp_spouse_end_age = ((FamilySearch) family).sp_spouse_end_age.getSelectedItem().toString().trim();
                String sp_child_start_age = ((FamilySearch) family).sp_child_start_age.getSelectedItem().toString().trim();
                String sp_child_end_age = ((FamilySearch) family).sp_child_end_age.getSelectedItem().toString().trim();
                String childGender = ((FamilySearch) family).gender;

                if (!sp_spouse_blood.equalsIgnoreCase(TITLE_SPOUSE_BLOOD_GROUP)) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_BG, sp_spouse_blood);
                    lstProceed.add("Spouse BG: " + sp_spouse_blood);
                }

                if (!edtSpouseEdu.isEmpty()) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_EDU, edtSpouseEdu);
                    lstProceed.add("Spouse Education: " + edtSpouseEdu);
                }


                if (!sp_spouse_start_age.equalsIgnoreCase(getString(R.string.AGE)) && !sp_spouse_end_age.equalsIgnoreCase(getString(R.string.AGE))) {
                    String start = sp_spouse_start_age.replace("Age ", "");
                    String end = sp_spouse_end_age.replace("Age ", "");
                    if (Integer.parseInt(start) <= Integer.parseInt(end)) {
                        mJsonObject.put(Common.Constant_Class.SPOUSE_START_AGE, sp_spouse_start_age.replace("Age ", ""));
                        lstProceed.add("Spouse Age From: " + sp_spouse_start_age);
                        mJsonObject.put(Common.Constant_Class.SPOUSE_END_AGE, sp_spouse_end_age.replace("Age ", ""));
                        lstProceed.add("Spouse Age To: " + sp_spouse_end_age);
                    } else {
                        valid = "Spouse 'From' Age is larger than 'End' Age";
                    }
                }

                if (!sp_child_start_age.equalsIgnoreCase(getString(R.string.AGE)) && !sp_child_end_age.equalsIgnoreCase(getString(R.string.AGE))) {
                    String start = sp_child_start_age.replace("Age ", "");
                    String end = sp_child_end_age.replace("Age ", "");
                    if (Integer.parseInt(start) <= Integer.parseInt(end)) {
                        mJsonObject.put(Common.Constant_Class.CHILD_START_AGE, sp_child_start_age.replace("Age ", ""));
                        lstProceed.add("Child Age From: " + sp_child_start_age);
                        mJsonObject.put(Common.Constant_Class.CHILD_END_AGE, sp_child_end_age.replace("Age ", ""));
                        lstProceed.add("Child Age To: " + sp_child_end_age);
                    } else {
                        valid = "Child 'From' Age is larger than 'End' Age";
                    }
                }

                if (child_married) {
                    mJsonObject.put(Common.Constant_Class.CHILD_MARRIAGE, child_married);
                    lstProceed.add("Child Marriage: " + child_married);
                }

                if (!bgroup.equalsIgnoreCase("") && !bgroup.equalsIgnoreCase(TITLE_CHILD_BLOOD_GROUP)) {
                    mJsonObject.put(Common.Constant_Class.CHILD_BLOOD_GROUP, bgroup);
                    lstProceed.add("Child BloodGroup: " + bgroup);
                }

                if (!childBplace.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_BPLACE, childBplace);
                    lstProceed.add("Child BirthPlace: " + childBplace);
                }

                if (!childGender.equalsIgnoreCase("")) {
                    if (!childGender.equalsIgnoreCase("both")) {
                        mJsonObject.put(Common.Constant_Class.CHILD_GENDER, childGender);
                    }
                    lstProceed.add("Child Gender: " + childGender);
                }

                if (!strmdate_from.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.FROM_MARRIAGE_DATE, strmdate_from);
                    lstProceed.add("Marriage From: " + ((FamilySearch) family).edt_mdate_from.getText().toString());
                }

                if (!strmdate_to.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.TO_MARRIAGE_DATE, strmdate_to);
                    lstProceed.add("Marriage To: " + ((FamilySearch) family).edt_mdate_to.getText().toString());
                }

                if (!childBdateFrom.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.FROM_CHILD_BDAY, childBdateFrom);
                    lstProceed.add("Child BDay From: " + ((FamilySearch) family).edt_cdate_from.getText().toString());
                }
                if (!childBdateTo.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.TO_CHILD_BDAY, childBdateTo);
                    lstProceed.add("Child BDay To: " + ((FamilySearch) family).edt_cdate_to.getText().toString());
                }
                if (!strSpouseName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_NAME, strSpouseName);
                    lstProceed.add("Spouse Name: " + strSpouseName);
                }
                if (!strSpouseFName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_FATHER_NAME, strSpouseFName);
                    lstProceed.add("Spouse Father Name: " + strSpouseFName);
                }
                if (!strSpouseMName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_MOTHER_NAME, strSpouseMName);
                    lstProceed.add("Spouse Mother Name: " + strSpouseMName);
                }
                if (!strchild_name.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_NAME, strchild_name);
                    lstProceed.add("Child Name: " + strchild_name);
                }
                if (!strcedu.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_EDU, strcedu);
                    lstProceed.add("Child Education: " + strcedu);
                }
                if (!strchild_work.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_WORK, strchild_work);
                    lstProceed.add("Child Work: " + strchild_work);
                }

                if (!childMobile.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_MOBILE, childMobile);
                    lstProceed.add("Child Mobile: " + childMobile);
                }
            }

            if (valid.equalsIgnoreCase("")) {
                // Bundle mBundle = new Bundle();
                // mBundle.putString("search_json", mJsonObject.toString());
                // mBundle.putStringArrayList("proceed", lstProceed);
                DisplayConfimDialog(mJsonObject.toString(), lstProceed);
                //showDialog(CUSTOM_DIALOG_ID, mBundle);
            } else {
                Toast.makeText(AdvanceSearchActivity.this, "" + valid, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (chooseFragment.equalsIgnoreCase(FamilySearch.class.getSimpleName())) {
                family.onActivityResult(requestCode, resultCode, data);
            } else if (chooseFragment.equalsIgnoreCase(PersonalSearch.class.getSimpleName())) {
                personal.onActivityResult(requestCode, resultCode, data);
            } else if (chooseFragment.equalsIgnoreCase(BusinessSearch.class.getSimpleName())) {
                business.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void DisplayConfimDialog(String json, ArrayList<String> lstProceed) {
        Dialog dialog = null;
        dialog = new Dialog(AdvanceSearchActivity.this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setTitle("Vastipatrak");

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item_search, lstProceed);
        recyclerView = dialog.findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);
        Button btn_proceed = dialog.findViewById(R.id.btn_proceed);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_proceed.setTag(json);
        mEditor.putString("adv_search", json);
        mEditor.apply();
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = (String) v.getTag();
                try {
                    if (json != null && !json.isEmpty()) {
                        JSONObject mJson = new JSONObject(json);
                        navigateActivity(mJson);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final Dialog finalDialog = dialog;
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        try {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateActivity(JSONObject mJsonObject) {
        Intent mIntent = new Intent(AdvanceSearchActivity.this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.putExtra(Common.Constant_Class.QUERY_STRING, mJsonObject.toString());

        startActivity(mIntent);
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem filterAdmins = menu.findItem(R.id.action_admins);
        filterAdmins.setVisible(false);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent mIntent = new Intent(AdvanceSearchActivity.this, MainActivity.class);
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
        MenuItem action_scan = menu.findItem(R.id.action_scan);
        action_scan.setVisible(false);
        MenuItem action_scan_image = menu.findItem(R.id.action_scan_image);
        action_scan_image.setVisible(false);

        /*export.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.ExportSearchData(AdvanceSearchActivity.this);
                return false;
            }
        });*/

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.promptSpeechInput(AdvanceSearchActivity.this);
                return false;
            }
        });

        MenuItem csearch = menu.findItem(R.id.action_clear_search);
        csearch.setVisible(true);
        csearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mEditor.clear();
                mEditor.apply();
                Toast.makeText(AdvanceSearchActivity.this, "Clear search data", Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(AdvanceSearchActivity.this, AdvanceSearchActivity.class);
                startActivity(mIntent);
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
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
