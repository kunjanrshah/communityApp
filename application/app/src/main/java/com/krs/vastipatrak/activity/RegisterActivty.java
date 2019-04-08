package com.krs.vastipatrak.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;

public class RegisterActivty extends Activity {

    private TextView txt_already,txt_how_register,txtCity;
    private ImageView img_back,img_header_logo,img_profile,img_cancel;
    private EditText edt_head_name,edt_spouse_name,edt_email_id,edt_mobile,edt_password,edt_cpassword,edt_address,edt_head_surname;
    private Button btn_register;
    private JSONObject json = null;
    private String str_profile_hash = "";
    private static String TAG=RegisterActivty.class.getSimpleName();
    private Uri mCropImageUri;
    final int REQUEST_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MemoryAllocation();

        txt_already.setOnClickListener(v -> {
            Intent mIntent=new Intent(RegisterActivty.this,LoginActivity1.class);
            startActivity(mIntent);
            finish();
        });

        img_back.setOnClickListener(v -> finish());

        btn_register.setOnClickListener(v -> SignupWS());

        img_profile.setOnClickListener(v -> {
            cropImageActivity();
        });

        txtCity.setOnClickListener(v -> {
            Intent mIntent = new Intent(RegisterActivty.this, SelectionlistActivity.class);
            mIntent.putExtra(getString(R.string.listview), false);
            mIntent.putExtra(getString(R.string.title), R.string.city);
            startActivityForResult(mIntent, 14);
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (Utility.canCallPhone(this) || !Utility.canAccessLocation(this) || !Utility.canSMS(this)) {
                int INIT_REQUEST = 1;
                requestPermissions(AppConstants.INIT_PERMS, INIT_REQUEST);
            }
        }

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Utility.hasPermission(RegisterActivty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(permissions, REQUEST_PERMISSION_CODE);
            }
            if (!Utility.hasPermission(RegisterActivty.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(permissions, REQUEST_PERMISSION_CODE);
            }
        }

    }

    private void MemoryAllocation() {
        img_back=findViewById(R.id.img_back);
        img_header_logo=findViewById(R.id.img_header_logo);
        img_profile=findViewById(R.id.img_profile);
        img_cancel=findViewById(R.id.img_cancel);
        txt_how_register=findViewById(R.id.txt_how_register);
        txt_already=findViewById(R.id.txt_already);

        edt_head_name=findViewById(R.id.edt_head_name);
        edt_head_surname=findViewById(R.id.edt_head_surname);
        edt_spouse_name=findViewById(R.id.edt_spouse_name);
        edt_email_id=findViewById(R.id.edt_email_id);
        edt_mobile=findViewById(R.id.edt_mobile);
        edt_password=findViewById(R.id.edt_password);
        edt_cpassword=findViewById(R.id.edt_cpassword);
        edt_address=findViewById(R.id.edt_address);
        btn_register=findViewById(R.id.btn_register);
        txtCity=findViewById(R.id.txtCity);

        String str=getResources().getString(R.string.already_have_a_account_sign_in)+ "<b>" +" "+ getString(R.string.login) +"</b>";
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(RegisterActivty.this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }

        if (REQUEST_PERMISSION_CODE == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            CropImage.startPickImageActivity(RegisterActivty.this);
        }
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

        if (CropImage.isReadExternalStoragePermissionsRequired(RegisterActivty.this, imageUri)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            mCropImageUri = imageUri;
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            // no permissions required or already grunted, can start crop image activity
        }


        Bitmap bmp = null;
        if (data != null) {
            try {
                if (data.getData() == null) {
                    bmp = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                } else {
                    Uri selectedImage = data.getData();
                    bmp = Utility.scaleImage(this, selectedImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bmp != null) {
                if (resultCode == RESULT_OK) {
                    Glide.with(this).load(bmp).thumbnail(0.5f).apply(RequestOptions.circleCropTransform()).into(img_profile);
                    str_profile_hash = Utility.getBase64(bmp);
                }
            }
        }
    }



    private void SignupWS() {
        if (Utility.isOnline(this)) {
            final String name = edt_head_name.getText().toString().trim();
            final String surname = edt_head_surname.getText().toString().trim();
            final String spouse_name = edt_spouse_name.getText().toString().trim();
            final String email = edt_email_id.getText().toString().trim();
            final String mobile = edt_mobile.getText().toString().trim();
            final String password = edt_password.getText().toString().trim();
            String cpassword = edt_cpassword.getText().toString().trim();
            final String address = edt_address.getText().toString().trim();
            final String city = txtCity.getText().toString().trim();

            if (!email.equalsIgnoreCase("")) {
                if (Utility.isValidEmail(email)) {
                    Toast.makeText(RegisterActivty.this, "Type Valid Email Address!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!surname.isEmpty() && !city.isEmpty() && !email.isEmpty() && !name.isEmpty() && !mobile.isEmpty() && !password.isEmpty() && !cpassword.isEmpty() && !spouse_name.isEmpty() && !address.isEmpty()) {
                if (password.equalsIgnoreCase(cpassword)) {
                    if (mobile.length() == 10) {
                        try {
                            json = new JSONObject();
                            Utility.showProgressDialog(this);
                            json.put(AppConstants.FIRST_NAME, name);
                            json.put(AppConstants.LAST_NAME, surname);
                            json.put(AppConstants.SPOUSE_NAME, spouse_name);
                            json.put(AppConstants.EMAIL_ADDRESS, email);
                            json.put(AppConstants.MOBILE, mobile);
                            json.put(AppConstants.PASSWORD, password);
                            json.put(AppConstants.REPEAT_PASSWORD, cpassword);
                            json.put(AppConstants.ADDRESS, address);
                            json.put(AppConstants.CITY, city);

                          //  if (screen != null && screen.equalsIgnoreCase(AppConstants.SEARCH_FRAGMENT)) {
                           //     json.put(AppConstants.STATUS, "1");
                           // } else {
                                json.put(AppConstants.STATUS, "0");
                          //  }
                            if (!str_profile_hash.isEmpty()) {
                                json.put(AppConstants.PROFILE_PIC, str_profile_hash);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.SIGNUP_URL, json, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(@NonNull JSONObject response) {
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
                            }
                        }, error -> {
                            hideProgressDialog();
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            String message = null;
                            if (error instanceof NetworkError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof ServerError) {
                                message = "The server could not be found. Please try again after some time!!";
                            } else if (error instanceof AuthFailureError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof ParseError) {
                                message = "Parsing error! Please try again after some time!!";
                            } else if (error instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }
                            Toast.makeText(RegisterActivty.this, "" + message, Toast.LENGTH_LONG).show();

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
                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(jsonObjReq, "");
                    } else {
                        Toast.makeText(RegisterActivty.this, getString(R.string.err_msg_invalid_mobile), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivty.this, getString(R.string.err_msg_repeat_password), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(RegisterActivty.this, getString(R.string.err_msg_blank), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(RegisterActivty.this, AppConstants.NO_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }
}
