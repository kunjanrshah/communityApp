package com.krs.vastipatrak.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.fragments.BusinessFilter;
import com.krs.vastipatrak.fragments.FamilyFilter;
import com.krs.vastipatrak.fragments.FragmentDrawer;
import com.krs.vastipatrak.fragments.PersonalFilter;
import com.krs.vastipatrak.utils.Common;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilterActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private final String[] READ_CONTACT_PERMS = {Manifest.permission.READ_CONTACTS};
    private final int READ_CONTACT_REQUEST = 3;
    ViewPager viewPager;
    SearchView searchView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    Fragment personal, business, family;

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
    }

    private void MemoryAllocation() {
        viewPager = findViewById(R.id.viewpager);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backNavigation();
    }

    private void backNavigation() {
        Fragment fragment = new FragmentDrawer();
        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
        Intent mIntent = new Intent(FilterActivity.this, MainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        personal = new PersonalFilter();

        business = new BusinessFilter();

        family = new FamilyFilter();
        adapter.addFrag(personal, Common.Constant_Class.PERSONAL);
        adapter.addFrag(business, Common.Constant_Class.BUSINESS);
        adapter.addFrag(family, Common.Constant_Class.FAMILY);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    public void callAdvanceSearchWS() {
        JSONObject mJsonObject = new JSONObject();
        try {

            String valid = "";
            if (((PersonalFilter) personal).edtFName != null) {

                String strFName = ((PersonalFilter) personal).edtFName.getText().toString().trim();
                String strLName = ((PersonalFilter) personal).edtLName.getText().toString().trim();
                String strFatherName = ((PersonalFilter) personal).edtFatherName.getText().toString().trim();
                String strMotherName = ((PersonalFilter) personal).edtMotherName.getText().toString().trim();
                String strEducation = ((PersonalFilter) personal).edtEducation.getText().toString().trim();
                String strBPlace = ((PersonalFilter) personal).edtBPlace.getText().toString().trim();
                String strNPlace = ((PersonalFilter) personal).edtNPlace.getText().toString().trim();
                String strGotra = ((PersonalFilter) personal).edtGotra.getText().toString().trim();
                String strMobile = ((PersonalFilter) personal).edtMobile.getText().toString().trim();
                String strAddress = ((PersonalFilter) personal).edtAddress.getText().toString().trim();
                String strphone = ((PersonalFilter) personal).edt_phone.getText().toString().trim();
                String strbdate = ((PersonalFilter) personal).edtbdate.getText().toString().trim();
                String strbTime = ((PersonalFilter) personal).edtbTime.getText().toString().trim();
                String strEaddress = ((PersonalFilter) personal).edt_Eaddress.getText().toString().trim();
                String strCity = ((PersonalFilter) personal).edtCity.getText().toString().trim();
                String bgroup = ((PersonalFilter) personal).spinnerBlood.getSelectedItem().toString().trim();
                String gender = ((PersonalFilter) personal).gender;

                if (bgroup.equalsIgnoreCase(Common.Constant_Class.TITLE_BLOOD_GROUP)) {
                    bgroup = "";
                }

                if (((PersonalFilter) personal).rbtnF.isChecked()) {
                    gender = "0";
                } else if (((PersonalFilter) personal).rbtnM.isChecked()) {
                    gender = "1";
                }

                if (!strEaddress.equalsIgnoreCase("")) {
                    if (!Common.isValidEmail(strEaddress)) {
                        valid = "Email is not valid Format";
                    }
                }
          /*      if (!strbdate.equalsIgnoreCase("")) {
                    if (!Common.isThisDateValid(strbdate, "dd/MM/yyyy")) {
                        valid = "Birth Date is not valid Format";
                    }
                }*/

                if (!strbTime.equalsIgnoreCase("")) {
                    if (!Common.IsValidate(strbTime)) {
                        valid = "Birth Time is not valid 24 Hours";
                    }
                }

                if (!strFName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.FIRST_NAME, strFName);
                }
                if (!strLName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.LAST_NAME, strLName);
                }
                if (!strFatherName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.FATHER_NAME, strFatherName);
                }
                if (!strMotherName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.MOTHER_NAME, strMotherName);
                }
                if (!strbdate.equalsIgnoreCase("")) {

                    mJsonObject.put(Common.Constant_Class.BIRTH_DATE, strbdate);
                }
                if (!strBPlace.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.BIRTH_PLACE, strBPlace);
                }
                if (!strbTime.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.BIRTH_TIME, strbTime);
                }
                if (!strMobile.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.MOBILE, strMobile);
                }
                if (!strphone.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.PHONE, strphone);
                }
                if (!strGotra.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.GOTRA, strGotra);
                }
                if (!strNPlace.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.NATIVE_PLACE, strNPlace);
                }
                if (!strCity.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CITY, strCity);
                }
                if (!strEducation.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.EDUCATION, strEducation);
                }
                if (!strEaddress.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.EMAIL_ADDRESS, strEaddress);
                }
                if (!strAddress.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.ADDRESS, strAddress);
                }
                if (!bgroup.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.BLOOD_GROUP, bgroup);
                }
                if (!gender.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.GENDER, gender);
                }
            }

            if (((BusinessFilter) business).edtOccupation.getText() != null) {

                String strOccupation, strWork, strOMobile, strOAddress;
                strOccupation = ((BusinessFilter) business).edtOccupation.getText().toString().trim();
                strWork = ((BusinessFilter) business).edtWork.getText().toString().trim();
                strOMobile = ((BusinessFilter) business).edtOMobile.getText().toString().trim();
                strOAddress = ((BusinessFilter) business).edtOAddress.getText().toString().trim();

                if (!strOccupation.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.OCCUPATION, strOccupation);
                }
                if (!strWork.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.WORK, strWork);
                }
                if (!strOMobile.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.OFFICE_MOBILE, strOMobile);
                }
                if (!strOAddress.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.OFFICE_ADDRESS, strOAddress);
                }
            }

            if (((FamilyFilter) family).edt_mdate != null) {

                String strmdate = ((FamilyFilter) family).edt_mdate.getText().toString().trim();
                String strSpouseName = ((FamilyFilter) family).edtSpouseName.getText().toString().trim();
                String strSpouseFName = ((FamilyFilter) family).edtSpouseFName.getText().toString().trim();
                String strSpouseMName = ((FamilyFilter) family).edtSpouseMName.getText().toString().trim();
                String strchild_name = ((FamilyFilter) family).edtchild_name.getText().toString().trim();
                String strcedu = ((FamilyFilter) family).edtcedu.getText().toString().trim();
                String strchild_work = ((FamilyFilter) family).edtchild_work.getText().toString().trim();
                String childBdate = ((FamilyFilter) family).edt_childbdate.getText().toString().trim();
                String childBtime = ((FamilyFilter) family).edtchildbtime.getText().toString().trim();
                String childBplace = ((FamilyFilter) family).edtchildbplace.getText().toString().trim();
                String childGender = ((FamilyFilter) family).gender;

                if (!childBtime.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_BTIME, childBtime);
                }

                if (!childBplace.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_BPLACE, childBplace);
                }

                if (!childGender.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_GENDER, childGender);
                }

                if (!strmdate.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.MARRIAGE_DATE, strmdate);
                }
                if (!childBdate.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_BDAY, childBdate);
                }
                if (!strSpouseName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_NAME, strSpouseName);
                }
                if (!strSpouseFName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_FATHER_NAME, strSpouseFName);
                }
                if (!strSpouseMName.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.SPOUSE_MOTHER_NAME, strSpouseMName);
                }
                if (!strchild_name.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_NAME, strchild_name);
                }
                if (!strcedu.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_EDU, strcedu);
                }
                if (!strchild_work.equalsIgnoreCase("")) {
                    mJsonObject.put(Common.Constant_Class.CHILD_WORK, strchild_work);
                }
            }

            if (valid.equalsIgnoreCase("")) {
                navigateActivity(mJsonObject);
            } else {
                Toast.makeText(FilterActivity.this, "" + valid, Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateActivity(JSONObject mJsonObject) {
        Intent mIntent = new Intent(FilterActivity.this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.putExtra(Common.Constant_Class.QUERY_STRING, mJsonObject.toString());
        startActivity(mIntent);
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent mIntent = new Intent(FilterActivity.this, MainActivity.class);
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
        export.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.ExportSearchData(FilterActivity.this);
                return false;
            }
        });

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.promptSpeechInput(FilterActivity.this);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

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
