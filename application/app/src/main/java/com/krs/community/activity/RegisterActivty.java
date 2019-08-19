package com.krs.community.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.CountryData;
import com.krs.community.utils.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;
import com.yalantis.ucrop.model.AspectRatio;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.krs.community.utils.Utility.watchYoutubeVideo;
import static com.yalantis.ucrop.UCrop.Options;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;
import static com.yalantis.ucrop.UCrop.RESULT_ERROR;
import static com.yalantis.ucrop.UCrop.getError;
import static com.yalantis.ucrop.UCrop.getOutput;
import static com.yalantis.ucrop.UCrop.of;

public class RegisterActivty extends BaseActivity implements UCropFragmentCallback {

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
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
    private int requestMode = 1;
    private boolean mShowLoader;

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
            Utility.fade(this);
        });

        img_back.setOnClickListener(v -> {
            Intent mIntent = new Intent(RegisterActivty.this, SplashActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();
            Utility.fade(this);
        });


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


      /*  if (Build.VERSION.SDK_INT >= 23) {
            if (Utility.canCallPhone(this) || !Utility.canAccessLocation(this) || !Utility.haveSMS(this)) {
                requestPermissions(AppConstants.INIT_PERMS, AppConstants.INIT_REQUEST);
            }
        }*/

        spinnerCountries.setAdapter(new ArrayAdapter<String>(RegisterActivty.this, R.layout.my_spinner_style, CountryData.countryNames) {

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


        MaterialTapTargetPrompt registerPrompt = new MaterialTapTargetPrompt.Builder(RegisterActivty.this)
                .setTarget(R.id.btn_register)
                .setBackButtonDismissEnabled(false)
                .setBackgroundColour(getResources().getColor(R.color.colorPrimary))
                .setPrimaryText("નવો પરિવાર રેજીસ્ટર કરો.")
                .setSecondaryText("બધી જ અગત્ય ની ફેમિલી હેડ ની વીગતો ભરી નવી ફેમિલી બનાવા માટે રેજીસ્ટર બટન પર ક્લિક કરો.")
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        prompt.dismiss();
                    }
                }).create();

        MaterialTapTargetPrompt photoPrompt = new MaterialTapTargetPrompt.Builder(RegisterActivty.this)
                .setTarget(R.id.img_profile)
                .setAutoFinish(false)
                .setAutoDismiss(false)
                .setBackButtonDismissEnabled(false)
                .setBackgroundColour(getResources().getColor(R.color.colorPrimary))
                .setPrimaryText("તમારો પ્રોફાઈલ ફોટો અપલોડ કરો.")
                .setSecondaryText("મોબાઈલ ગેલેરી માંથી તમારો મનપસંદ ફોટો સિલેક્ટ કરો અને મનપસંદ ઈફેક્ટ આપી ને સેવ કરો.")
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        prompt.dismiss();
                        assert registerPrompt != null;
                        registerPrompt.show();
                    }
                })
                .show();


        img_profile.setOnClickListener(v ->
        {
            if (photoPrompt.getState() == MaterialTapTargetPrompt.STATE_DISMISSED) {
                pickFromGallery();
            }

        });


        btn_register.setOnClickListener(v -> {

            if (registerPrompt.getState() == MaterialTapTargetPrompt.STATE_DISMISSED) {
                Intent mIntent = new Intent(RegisterActivty.this, DashboardActivity.class);
                startActivity(mIntent);
                finish();
            }
        });
    }


    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= 23) {
                if (!Utility.hasPermission(this,"READ_EXTERNAL_STORAGE")) {
                    new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Storage read Permission")
                            .setContentText("Permission is needed to pick image from gallery for your profile")
                            .setConfirmText("Yes, please!")
                            .setCancelText("No!")
                            .showCancelButton(true)
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismiss();
                                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage read permission is needed to pick files.", REQUEST_STORAGE_READ_ACCESS_PERMISSION);
                            })
                            .show();
                }
            }
        } else {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestMode);
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

        if (resultCode == RESULT_OK) {
            if (requestCode == requestMode) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCrop(selectedUri);
                } else {
                    Toast.makeText(RegisterActivty.this, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == RESULT_ERROR) {
            handleCropError(data);
        }

        /*Uri imageUri = null;
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
        }*/
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = getOutput(result);
        if (resultUri != null) {

            Log.d(TAG, "resultUri: " + resultUri);

            try {

                File f = new File(String.valueOf(resultUri.getPath()));

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
                img_profile.setImageURI(resultUri);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(RegisterActivty.this, "Cannot retrieve cropped image", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(RegisterActivty.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(RegisterActivty.this, "Unexpected error", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        UCrop uCrop = of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop = advancedConfig(uCrop);
        uCrop.start(RegisterActivty.this);
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        Options options = new Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setCompressionQuality(100);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        options.setBrightnessEnabled(true);
        options.setContrastEnabled(true);
        options.setSaturationEnabled(true);
        options.setSharpnessEnabled(true);

        options.setImageToCropBoundsAnimDuration(666);
        //  options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimary));
        //options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
                new AspectRatio("WOW", 1, 2),
                new AspectRatio("MUCH", 3, 4),
                new AspectRatio("RATIO", 0f, 0f),
                new AspectRatio("SO", 16, 9),
                new AspectRatio("ASPECT", 1, 1));

        return uCrop.withOptions(options);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_crop).setVisible(!mShowLoader);
        menu.findItem(R.id.menu_loader).setVisible(mShowLoader);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void loadingProgress(boolean showLoader) {
        mShowLoader = showLoader;
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        switch (result.mResultCode) {
            case RESULT_OK:
                handleCropResult(result.mResultData);
                break;
            case RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
        }
    }
}
