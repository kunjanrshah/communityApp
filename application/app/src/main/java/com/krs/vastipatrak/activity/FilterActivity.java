package com.krs.vastipatrak.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

/**
 * Created by kushal on 31/01/16.
 */
public class FilterActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private final String[] READ_CONTACT_PERMS = {Manifest.permission.READ_CONTACTS};
    private final int READ_CONTACT_REQUEST = 3;
    SharedPreferences mSharedPreferences;
    String TAG = "FilterActivity";
    ViewPager viewPager;
    SearchView searchView;
    private Toolbar toolbar;
    private TabLayout tabLayout;

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.title_filter);
        // toolbar.setNavigationIcon(R.drawable.bac);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new FragmentDrawer();
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();

                Intent mIntent = new Intent(FilterActivity.this, MainActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                finish();
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment personal = new PersonalFilter();

        Fragment business = new BusinessFilter();

        Fragment family = new FamilyFilter();
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

     }
 */
    public void callAdvanceSearchWS() {
        JSONObject mJsonObject = new JSONObject();
        try {

            // mJsonObject.put(Common.Constant_Class.STATUS, "1");
            String valid = "";
            if (PersonalFilter.edtFName != null) {

                String strFName = PersonalFilter.edtFName.getText().toString().trim();
                String strLName = PersonalFilter.edtLName.getText().toString().trim();
                String strFatherName = PersonalFilter.edtFatherName.getText().toString().trim();
                String strMotherName = PersonalFilter.edtMotherName.getText().toString().trim();
                String strEducation = PersonalFilter.edtEducation.getText().toString().trim();
                String strBPlace = PersonalFilter.edtBPlace.getText().toString().trim();
                String strNPlace = PersonalFilter.edtNPlace.getText().toString().trim();
                String strGotra = PersonalFilter.edtGotra.getText().toString().trim();
                String strMobile = PersonalFilter.edtMobile.getText().toString().trim();
                String strAddress = PersonalFilter.edtAddress.getText().toString().trim();
                String strphone = PersonalFilter.edt_phone.getText().toString().trim();
                String strbdate = PersonalFilter.edtbdate.getText().toString().trim();
                String strbTime = PersonalFilter.edtbTime.getText().toString().trim();
                String strEaddress = PersonalFilter.edt_Eaddress.getText().toString().trim();
                String strCity = PersonalFilter.edtCity.getText().toString().trim();
                String bgroup = PersonalFilter.spinnerBlood.getSelectedItem().toString().trim();
                String gender = PersonalFilter.gender;

                if (bgroup.equalsIgnoreCase(Common.Constant_Class.TITLE_BLOOD_GROUP)) {
                    bgroup = "";
                }

                if (PersonalFilter.rbtnF.isChecked()) {
                    gender = "0";
                } else if (PersonalFilter.rbtnM.isChecked()) {
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

            if (BusinessFilter.edtOccupation.getText() != null) {

                String strOccupation = "", strWork = "", strOMobile = "", strOAddress = "";
                strOccupation = BusinessFilter.edtOccupation.getText().toString().trim();
                strWork = BusinessFilter.edtWork.getText().toString().trim();
                strOMobile = BusinessFilter.edtOMobile.getText().toString().trim();
                strOAddress = BusinessFilter.edtOAddress.getText().toString().trim();

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

            if (FamilyFilter.edt_mdate != null) {

                String strmdate = FamilyFilter.edt_mdate.getText().toString().trim();
                String strSpouseName = FamilyFilter.edtSpouseName.getText().toString().trim();
                String strSpouseFName = FamilyFilter.edtSpouseFName.getText().toString().trim();
                String strSpouseMName = FamilyFilter.edtSpouseMName.getText().toString().trim();
                String strchild_name = FamilyFilter.edtchild_name.getText().toString().trim();
                String strcedu = FamilyFilter.edtcedu.getText().toString().trim();
                String strchild_work = FamilyFilter.edtchild_work.getText().toString().trim();
                String childBdate = FamilyFilter.edt_childbdate.getText().toString().trim();
                String childBtime = FamilyFilter.edtchildbtime.getText().toString().trim();
                String childBplace = FamilyFilter.edtchildbplace.getText().toString().trim();
                String childGender = FamilyFilter.gender;

         /*       if (!strmdate.equalsIgnoreCase("")) {
                    if (!Common.isThisDateValid(strmdate, "dd/MM/yyyy")) {
                        valid = "Marriage Date is not valid Format";
                    }
                }*/

                /*if (!strcdate.equalsIgnoreCase("")) {
                    if (!Common.isThisDateValid(strcdate, "dd/MM/yyyy")) {
                        valid = "Child Birth Date is not valid Format";
                    }
                }*/

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

            //clear_string();
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

/*    private void clear_string() {
        if (PersonalFilter.strFName != null) {
            PersonalFilter.strFName = "";
            PersonalFilter.strLName = "";
            PersonalFilter.strFatherName = "";
            PersonalFilter.strMotherName = "";
            PersonalFilter.strbdate = "";
            PersonalFilter.strBPlace = "";
            PersonalFilter.strbTime = "";
            PersonalFilter.strMobile = "";
            PersonalFilter.strphone = "";
            PersonalFilter.strGotra = "";
            PersonalFilter.strNPlace = "";
            PersonalFilter.strEducation = "";
            PersonalFilter.strEaddress = "";
            PersonalFilter.strAddress = "";
            PersonalFilter.bgroup = "";
            PersonalFilter.gender = "";
        }
        if (BusinessFilter.strOccupation != null) {
            BusinessFilter.strOccupation = "";
            BusinessFilter.strWork = "";
            BusinessFilter.strOMobile = "";
            BusinessFilter.strOAddress = "";
        }
        if (FamilyFilter.strmdate != null) {

            FamilyFilter.strmdate = "";
            FamilyFilter.strcdate = "";
            FamilyFilter.strSpouseName = "";
            FamilyFilter.strSpouseFName = "";
            FamilyFilter.strSpouseMName = "";
            FamilyFilter.strchild_name = "";
            FamilyFilter.strcedu = "";
            FamilyFilter.strchild_work = "";

        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem voiceItem = menu.findItem(R.id.action_voice);
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

        voiceItem = menu.findItem(R.id.action_voice);
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

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
