package com.krs.community.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.CountryData;
import com.krs.community.utils.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import spencerstudios.com.bungeelib.Bungee;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
import static com.krs.community.utils.AppConstants.INIT_TIMEOUT;
import static com.krs.community.utils.Utility.hideProgressDialog;
import static com.krs.community.utils.Utility.watchYoutubeVideo;

public class RegisterActivty extends Activity {

    private static String TAG = RegisterActivty.class.getSimpleName();
    private TextView txt_already, txt_how_register;
    private ImageView img_back;
    private CircleImageView img_profile;
    private ImageView img_cancel;
    private EditText edt_head_name, edt_email_id, edt_mobile, edt_password, edt_cpassword, edt_address, edt_head_surname;
    private Button btn_register;
    private JSONObject json = null;
    private String str_profile_hash = "";
    private boolean isShow = true;
    private boolean isShow1 = true;
    private String add_new = "";
    private Spinner spinnerCountries;
    private Spinner sp_community, sp_region;
    private AutoCompleteTextView txtCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            add_new = mBundle.getString(AppConstants.SCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false);
        }
        Memory_Allocation();
        runOnUiThread(() -> setCityListAdapter());

        txt_already.setOnClickListener(v -> {
            Intent mIntent = new Intent(RegisterActivty.this, LoginActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();
            Bungee.fade(this);
        });

        img_back.setOnClickListener(v -> {
            Intent mIntent = new Intent(RegisterActivty.this, ChooseLanActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();
            Bungee.fade(this);
        });

        btn_register.setOnClickListener(v -> {

            Intent mIntent = new Intent(RegisterActivty.this, DashboardActivity.class);
            startActivity(mIntent);
            finish();
            //RegistrationWS();
        });

        img_profile.setOnClickListener(v -> cropImageActivity());

        img_cancel.setOnClickListener(v -> {
            img_profile.setImageResource(R.drawable.man_reg);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.man_reg);
            if (icon != null) {
                str_profile_hash = Utility.getBase64(icon);
            }
            img_cancel.setVisibility(View.GONE);
        });

        txt_how_register.setOnClickListener(v -> {
            watchYoutubeVideo(RegisterActivty.this, getResources().getString(R.string.login_1));
        });


        edt_password.setOnTouchListener((v, event) -> {

            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edt_password.getRight() - edt_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isShow) {
                        edt_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0);
                        edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                        isShow = false;
                    } else {
                        edt_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0);
                        edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                        isShow = true;
                    }
                    edt_password.setSelection(edt_password.length());

                    return true;
                }
            }
            return false;
        });


        edt_cpassword.setOnTouchListener((v, event) -> {

            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edt_cpassword.getRight() - edt_cpassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isShow1) {
                        edt_cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0);
                        edt_cpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isShow1 = false;
                    } else {
                        edt_cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0);
                        edt_cpassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isShow1 = true;
                    }
                    try {
                        edt_cpassword.setSelection(edt_cpassword.length());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
            return false;
        });


        if (Build.VERSION.SDK_INT >= 23) {
            if (Utility.canCallPhone(this) || !Utility.canAccessLocation(this) || !Utility.canSMS(this)) {
                requestPermissions(AppConstants.INIT_PERMS, AppConstants.INIT_REQUEST);
            }
        }

        spinnerCountries.setAdapter(new ArrayAdapter<String>(RegisterActivty.this, R.layout.my_spinner_style, CountryData.countryNames) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(18);
                ((TextView) v).setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
                ((TextView) v).setTextColor(getResources().getColor(R.color.colorHint));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(20);
                return v;
            }
        });


        sp_community.setAdapter(new ArrayAdapter<String>(RegisterActivty.this, R.layout.my_spinner_style, CountryData.communityNames) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(18);
                ((TextView) v).setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                ((TextView) v).setTextColor(getResources().getColor(R.color.colorHint));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(20);
                return v;
            }
        });


        sp_region.setAdapter(new ArrayAdapter<String>(RegisterActivty.this, R.layout.my_spinner_style, CountryData.regionNames) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(18);
                ((TextView) v).setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                ((TextView) v).setTextColor(getResources().getColor(R.color.colorHint));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(20);
                return v;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false);
        }

    }

    private void setCityListAdapter() {
        String citylist = AppController.getInstance().mSharedPreferences.getString(getString(R.string.CityList_SP), "");
        ArrayList<String> lstCities = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(citylist);
            JSONArray mArray = response.getJSONArray(AppConstants.DATA);
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject mObject = mArray.getJSONObject(i);
                lstCities.add(mObject.getString("city_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, lstCities);
        txtCity.setThreshold(2);
        txtCity.setAdapter(adapter);
    }

    private void Memory_Allocation() {
        sp_community = findViewById(R.id.sp_community);
        sp_region = findViewById(R.id.sp_region);
        txtCity = findViewById(R.id.txtCity);

        img_back = findViewById(R.id.img_back);
        ImageView img_header_logo = findViewById(R.id.img_header_logo);
        img_profile = findViewById(R.id.img_profile);
        img_cancel = findViewById(R.id.img_cancel);
        spinnerCountries = findViewById(R.id.spinnerCountries);
        txt_how_register = findViewById(R.id.txt_how_register);
        txt_already = findViewById(R.id.txt_already);

        edt_head_name = findViewById(R.id.edt_head_name);
        edt_head_surname = findViewById(R.id.edt_head_surname);
        edt_email_id = findViewById(R.id.edt_email_id);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_password = findViewById(R.id.edt_password);
        edt_cpassword = findViewById(R.id.edt_cpassword);
        edt_address = findViewById(R.id.edt_address);
        btn_register = findViewById(R.id.btn_register);

        String str = getResources().getString(R.string.already_have_a_account_sign_in) + "<b>" + " " + getString(R.string.login) + "</b>";
        txt_already.setText(Html.fromHtml(str));
    }

    private void cropImageActivity() {
        if (Utility.hasPermission(RegisterActivty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && Utility.hasPermission(RegisterActivty.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            CropImage.startPickImageActivity(RegisterActivty.this);
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(RegisterActivty.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = null;
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = CropImage.getPickImageResultUri(RegisterActivty.this, data);
            if (CropImage.hasPermissionInManifest(RegisterActivty.this, Manifest.permission.READ_EXTERNAL_STORAGE) && CropImage.hasPermissionInManifest(RegisterActivty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {

                    File f = new File(String.valueOf(result.getUri().getPath()));

                    runOnUiThread(() -> {
                        Bitmap bmp1 = null;
                        try {
                            bmp1 = Utility.getBitmap(this, f);
                            str_profile_hash = Utility.getBase64(bmp1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    //Bitmap bmp= decodeFile(f);
                    //runOnUiThread(() -> str_profile_hash = Utility.getBase64(bmp));

                    img_cancel.setVisibility(View.VISIBLE);
                    img_profile.setImageURI(result.getUri());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(RegisterActivty.this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void RegistrationWS() {
        if (Utility.isOnline(this)) {
            String name = edt_head_name.getText().toString().trim();
            String surname = edt_head_surname.getText().toString().trim();
            String email = edt_email_id.getText().toString().trim();
            String mobile = edt_mobile.getText().toString().trim();
            String password = edt_password.getText().toString().trim();
            String cpassword = edt_cpassword.getText().toString().trim();
            String address = edt_address.getText().toString().trim();
            String city = txtCity.getText().toString().trim();

            if (!name.isEmpty()
                    && !surname.isEmpty()
                    && !email.isEmpty()
                    && !mobile.isEmpty()
                    && !password.isEmpty()
                    && !cpassword.isEmpty()
                    && !city.isEmpty()
                    && !address.isEmpty()) {
            } else {
                Utility.alert(RegisterActivty.this, getString(R.string.err_msg_blank));
                return;
            }

            if (mobile.length() != 10) {
                Utility.alert(RegisterActivty.this, getString(R.string.invalid_mobile_range));
                return;
            }
            /*String code = CountryData.countryAreaCodes[spinnerCountries.getSelectedItemPosition()];
            mobile=code+mobile;*/

            if (!email.isEmpty()) {
                if (Utility.isValidEmail(email)) {
                    Utility.alert(RegisterActivty.this, getString(R.string.invalid_email));
                    return;
                }
            }

            if (!password.isEmpty() && !cpassword.isEmpty()) {
                if (!password.equals(cpassword)) {
                    Utility.alert(RegisterActivty.this, getString(R.string.err_msg_repeat_password));
                    return;
                }
            }

            if (!surname.isEmpty() && !city.isEmpty() && !email.isEmpty() && !name.isEmpty() && !mobile.isEmpty() && !password.isEmpty() && !cpassword.isEmpty() && !address.isEmpty()) {
                if (password.equalsIgnoreCase(cpassword)) {
                    if (mobile.length() == 10) {
                        try {
                            Utility.showProgressDialog(this);
                            json = new JSONObject();
                            json.put(AppConstants.FIRST_NAME, name);
                            json.put(AppConstants.LAST_NAME, surname);
                            json.put(AppConstants.EMAIL_ADDRESS, email);
                            json.put(AppConstants.MOBILE, mobile);
                            json.put(AppConstants.PASSWORD, password);
                            json.put(AppConstants.REPEAT_PASSWORD, cpassword);
                            json.put(AppConstants.ADDRESS, address);
                            json.put(AppConstants.CITY, city);

                            if (add_new != null && add_new.equalsIgnoreCase(AppConstants.SEARCH_FRAGMENT)) {
                                json.put(AppConstants.STATUS, "1");
                            } else {
                                json.put(AppConstants.STATUS, "0");
                            }

                            if (!str_profile_hash.isEmpty()) {
                                json.put(AppConstants.PROFILE_PIC, str_profile_hash);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.SIGNUP_URL, json, response -> {
                            Log.d(TAG, "SignupWS: " + response.toString());

                            try {
                                hideProgressDialog();
                                boolean success = response.getBoolean(AppConstants.SUCCESS);
                                String message = response.getString(AppConstants.MESSAGE);
/*                                    if (success) {
                                    if (message.contains("admin")) {
                                        inputName.setText("");
                                        inputEmail.setText("");
                                        inputMobile.setText("");
                                        inputPassword.setText("");
                                        inputConformPassword.setText("");
                                        inputPassword.setText("");
                                        edt_spouse_name.setText("");
                                        edt_address.setText("");
                                        togglePage();
                                    }
                                }*/
                                Utility.alert(RegisterActivty.this, message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            hideProgressDialog();
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            String message = null;
                            if (error instanceof NetworkError) {
                                message = getString(R.string.can_not_connect_to_internet);
                            } else if (error instanceof ServerError) {
                                message = getString(R.string.server_could_not_found);
                            } else if (error instanceof AuthFailureError) {
                                message = getString(R.string.can_not_connect_to_internet);
                            } else if (error instanceof ParseError) {
                                message = getString(R.string.parsing_error);
                            } else if (error instanceof TimeoutError) {
                                message = getString(R.string.connection_timeout);
                            }
                            //Toast.makeText(RegisterActivty.this, "" + message, Toast.LENGTH_LONG).show();
                            Utility.alert(RegisterActivty.this, message);
                        }) {
                            @NonNull
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> params = new HashMap<>();
                                params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                                params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                                params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                                assert AppController.getInstance().mSharedPreferences != null;
                                params.put(AppConstants.DEVICE_TOKEN, AppController.getInstance().mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
                                return params;
                            }
                        };

                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
                        AppController.getInstance().addToRequestQueue(jsonObjReq, "");
                    } else {
                        Utility.alert(RegisterActivty.this, getString(R.string.invalid_mobile_range));
                        //Toast.makeText(RegisterActivty.this, getString(R.string.err_msg_invalid_mobile), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Utility.alert(RegisterActivty.this, getString(R.string.err_msg_repeat_password));
                    //Toast.makeText(RegisterActivty.this, getString(R.string.err_msg_repeat_password), Toast.LENGTH_LONG).show();
                }
            } else {
                Utility.alert(RegisterActivty.this, getString(R.string.err_msg_blank));
                //Toast.makeText(RegisterActivty.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
            }
        } else {
            Utility.alert(RegisterActivty.this, getString(R.string.can_not_connect_to_internet));
            //Toast.makeText(RegisterActivty.this, AppConstants.NO_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }
}
