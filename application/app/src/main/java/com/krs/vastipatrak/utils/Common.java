package com.krs.vastipatrak.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListProfileData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import PiyushBase64.Base64;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class Common {

    public static final int REQ_CODE_SPEECH_INPUT = 100;
    public static String Title = "";
    private static ProgressDialog pDialog;

   /* public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(maxImageSize / realImage.getWidth(), maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        return Bitmap.createScaledBitmap(realImage, width, height, filter);
    }*/

    public static boolean IsValidate(@NonNull final String time) {
        String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);
        Matcher matcher = pattern.matcher(time);
        return !matcher.matches();

    }

    public static boolean isValidEmail(@Nullable CharSequence target) {
        return target == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isThisDateValid(@Nullable String dateToValidate, @NonNull String dateFromat) {

        if (dateToValidate == null) {
            return true;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);

        } catch (ParseException e) {

            e.printStackTrace();
            return true;
        }

        return false;
    }

    public static boolean canCAMARA(@NonNull Context mContext) {
        return (hasPermission(mContext, Manifest.permission.CAMERA));
    }

    public static boolean canCallPhone(@NonNull Context mContext) {
        return (!hasPermission(mContext, Manifest.permission.CALL_PHONE));
    }

    public static boolean canAccessLocation(@NonNull Context mContext) {
        return (hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public static boolean canSMS(@NonNull Context mContext) {
        return (hasPermission(mContext, Manifest.permission.SEND_SMS));
    }

    public static boolean canReadContacts(@NonNull Context mContext) {
        return (Common.hasPermission(mContext, Manifest.permission.READ_CONTACTS));
    }

    private static boolean hasPermission(@NonNull Context mContext, @NonNull String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mContext, perm));
    }

    public static float getDistance(@NonNull Activity mActivity, double lat, double lon) {

        double curr_lat, curr_lng;
        float rvalue = -1.0f;
        try {
            if (Common.canAccessLocation(mActivity)) {
                if (MainActivity.lat != null && MainActivity.lon != null) {
                    curr_lat = Double.parseDouble(MainActivity.lat);
                    curr_lng = Double.parseDouble(MainActivity.lon);
                    Location loc1 = new Location("");
                    loc1.setLatitude(curr_lat);
                    loc1.setLongitude(curr_lng);
                    Location loc2 = new Location("");
                    loc2.setLatitude(lat);
                    loc2.setLongitude(lon);
                    float distanceInMeters = loc1.distanceTo(loc2);
                    int distanceInKm = 0;
                    if (distanceInMeters != 0) {
                        distanceInKm = (int) (distanceInMeters / 1000);
                    }

                    return distanceInKm;
                } else {
                    return rvalue;
                }

            } else {
                Toast.makeText(mActivity, "You need to give permission to access location ! ", Toast.LENGTH_SHORT).show();
                return rvalue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rvalue;
       /* if (gpsTracker.IsGetLocation()) {
            curr_lat = gpsTracker.getLatitude();
            curr_lng = gpsTracker.getLongitude();
        }*/

    }

    public static boolean CheckGpsStatus(Context mcontext) {
        LocationManager locationManager;
        boolean GpsStatus;

        locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);

        return !GpsStatus;

    }

    public static void showDirections(@NonNull Activity mActivity, double latitude, double longitude, String address) {


        if (MainActivity.lat != null && MainActivity.lon != null) {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)&daddr=%f,%f (%s)", Double.parseDouble(MainActivity.lat), Double.parseDouble(MainActivity.lon), "", latitude, longitude, address);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            mActivity.startActivity(intent);
        }
    }

    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static String getBase64(Bitmap bitmap) {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] ba = bao.toByteArray();
        return Base64.encodeBytes(ba);
    }

    public static Bitmap scaleImage(Context context, @NonNull Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        Objects.requireNonNull(is).close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        int MAX_IMAGE_DIMENSION = 120;
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        Objects.requireNonNull(is).close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (Objects.equals(type, "image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (Objects.equals(type, "image/jpg") || Objects.equals(type, "image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    public static void showSettingsAlert(@NonNull final Activity mActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);

        //Setting Dialog Title
        alertDialog.setTitle(mActivity.getResources().getString(R.string.app_name));

        //Setting Dialog Message
        alertDialog.setMessage("Do you want to enable location service ?");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mActivity.startActivity(intent);
                dialog.cancel();
                mActivity.finish();
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private static int getOrientation(Context context, @NonNull Uri photoUri) {
        /* it's on the external media. */
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(photoUri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (Objects.requireNonNull(cursor).getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

  /*  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }*/

    /*public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(@NonNull byte[] image) {
        return BitmapFactory.decodeByteArray(image, 100, image.length);
    }*/

    public static void promptSpeechInput(Activity mActivity) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");
        try {
            mActivity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(mActivity, "Sorry! Your device doesn\\'t support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    public static String camelCase(@Nullable String stringToConvert) {
        if (stringToConvert == null || TextUtils.isEmpty(stringToConvert)) return "";
        return Character.toUpperCase(stringToConvert.charAt(0)) + stringToConvert.substring(1).toLowerCase();
    }

    @NonNull
    public static RealmList<ListProfileData> getDataFromParentTable(String query, int search) {
        RealmList<ListProfileData> mlistProfileData = new RealmList<>();
        Realm realm = AppController.getInstance().realm;
        switch (search) {
            case 1: {
                query = query.toLowerCase();

                RealmResults<ListProfileData> profileData = AppController.getInstance().realm.where(ListProfileData.class).
                        contains(Constant_Class.FIRST_NAME, query).or().
                        contains(Constant_Class.LAST_NAME, query).or().
                        contains(Constant_Class.FATHER_NAME, query).or().
                        contains(Constant_Class.MOTHER_NAME, query).or().
                        contains(Constant_Class.EMAIL_ADDRESS, query).or().
                        contains(Constant_Class.MOBILE, query).or().
                        contains(Constant_Class.PHONE, query).or().
                        contains(Constant_Class.BLOOD_GROUP, query).or().
                        contains(Constant_Class.GENDER, query).or().
                        contains(Constant_Class.CITY, query).or().
                        contains(Constant_Class.GOTRA, query).or().
                        contains(Constant_Class.EKDO, query).or().
                        contains(Constant_Class.NATIVE_PLACE, query).or().
                        contains(Constant_Class.BIRTH_PLACE, query).or().
                        contains(Constant_Class.BIRTH_DATE, query).or().
                        contains(Constant_Class.BIRTH_TIME, query).or().
                        contains(Constant_Class.EDUCATION, query).or().
                        contains(Constant_Class.OCCUPATION, query).or().
                        contains(Constant_Class.WORK, query).or().
                        contains(Constant_Class.ADDRESS, query).or().
                        contains(Constant_Class.OFFICE_MOBILE, query).or().
                        contains(Constant_Class.OFFICE_ADDRESS, query).or().
                        contains(Constant_Class.SPOUSE_NAME, query).or().
                        contains(Constant_Class.MARRIAGE_DATE, query).or().
                        contains(Constant_Class.SPOUSE_FATHER_NAME, query).or().
                        contains(Constant_Class.SPOUSE_MOTHER_NAME, query).or().
                        contains(Constant_Class.STATUS, query).findAll();
                profileData.sort(Constant_Class.CITY, Sort.ASCENDING);
                mlistProfileData.addAll(profileData);
                break;
            }
            case 3: {
                ListProfileData profileData = realm.where(ListProfileData.class).equalTo(Constant_Class.PROFILE_ID, query).findFirst();
                mlistProfileData.add(profileData);
                break;
            }
            default:
                String first_name, last_name, father_name, mother_name, email_address, mobile, phone, blood_group, gender, gotra, ekdo, birth_place, native_place, birth_date, city, birth_time, education, occupation, work, address, office_mobile, office_address, spouse_name, marriage_date, spouse_father_name, spouse_mother_name;
                try {

                    JSONObject mJsonObject = new JSONObject(query);
                    if (mJsonObject.has(Constant_Class.FIRST_NAME)) {
                        first_name = mJsonObject.getString(Constant_Class.FIRST_NAME);
                        RealmResults<ListProfileData> first_name_data = realm.where(ListProfileData.class).contains(Constant_Class.FIRST_NAME, first_name.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(first_name_data);

                    }
                    if (mJsonObject.has(Constant_Class.LAST_NAME)) {
                        last_name = mJsonObject.getString(Constant_Class.LAST_NAME);
                        RealmResults<ListProfileData> last_name_data = realm.where(ListProfileData.class).contains(Constant_Class.LAST_NAME, last_name.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(last_name_data);
                    }
                    if (mJsonObject.has(Constant_Class.FATHER_NAME)) {
                        father_name = mJsonObject.getString(Constant_Class.FATHER_NAME);
                        RealmResults<ListProfileData> father_name_data = realm.where(ListProfileData.class).contains(Constant_Class.FATHER_NAME, father_name.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(father_name_data);

                    }
                    if (mJsonObject.has(Constant_Class.MOTHER_NAME)) {
                        mother_name = mJsonObject.getString(Constant_Class.MOTHER_NAME);
                        RealmResults<ListProfileData> mother_name_data = realm.where(ListProfileData.class).contains(Constant_Class.MOTHER_NAME, mother_name.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(mother_name_data);
                    }
                    if (mJsonObject.has(Constant_Class.EMAIL_ADDRESS)) {
                        email_address = mJsonObject.getString(Constant_Class.EMAIL_ADDRESS);
                        RealmResults<ListProfileData> email_data = realm.where(ListProfileData.class).contains(Constant_Class.EMAIL_ADDRESS, email_address.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(email_data);
                    }

                    if (mJsonObject.has(Constant_Class.MOBILE)) {
                        mobile = mJsonObject.getString(Constant_Class.MOBILE);
                        RealmResults<ListProfileData> mobile_data = realm.where(ListProfileData.class).contains(Constant_Class.MOBILE, mobile.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(mobile_data);
                    }
                    if (mJsonObject.has(Constant_Class.PHONE)) {
                        phone = mJsonObject.getString(Constant_Class.PHONE);
                        RealmResults<ListProfileData> phone_data = realm.where(ListProfileData.class).contains(Constant_Class.PHONE, phone.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(phone_data);
                    }
                    if (mJsonObject.has(Constant_Class.BLOOD_GROUP)) {
                        blood_group = mJsonObject.getString(Constant_Class.BLOOD_GROUP);
                        RealmResults<ListProfileData> blood_group_data = realm.where(ListProfileData.class).contains(Constant_Class.BLOOD_GROUP, blood_group.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(blood_group_data);
                    }
                    if (mJsonObject.has(Constant_Class.GENDER)) {
                        gender = mJsonObject.getString(Constant_Class.GENDER);
                        RealmResults<ListProfileData> gender_data = realm.where(ListProfileData.class).contains(Constant_Class.GENDER, gender.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(gender_data);

                    }
                    if (mJsonObject.has(Constant_Class.GOTRA)) {
                        gotra = mJsonObject.getString(Constant_Class.GOTRA);
                        RealmResults<ListProfileData> gotra_data = realm.where(ListProfileData.class).contains(Constant_Class.GOTRA, gotra.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(gotra_data);

                    }

                    if (mJsonObject.has(Constant_Class.EKDO)) {
                        ekdo = mJsonObject.getString(Constant_Class.EKDO);
                        RealmResults<ListProfileData> ekdo_data = realm.where(ListProfileData.class).contains(Constant_Class.EKDO, ekdo.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(ekdo_data);
                    }
                    if (mJsonObject.has(Constant_Class.NATIVE_PLACE)) {
                        native_place = mJsonObject.getString(Constant_Class.NATIVE_PLACE);
                        RealmResults<ListProfileData> native_place_data = realm.where(ListProfileData.class).contains(Constant_Class.NATIVE_PLACE, native_place.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(native_place_data);
                    }

                    if (mJsonObject.has(Constant_Class.CITY)) {
                        city = mJsonObject.getString(Constant_Class.CITY);
                        RealmResults<ListProfileData> city_data = realm.where(ListProfileData.class).contains(Constant_Class.NATIVE_PLACE, city.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(city_data);
                    }

                    if (mJsonObject.has(Constant_Class.BIRTH_PLACE)) {
                        birth_place = mJsonObject.getString(Constant_Class.BIRTH_PLACE);
                        RealmResults<ListProfileData> birth_place_data = realm.where(ListProfileData.class).contains(Constant_Class.BIRTH_PLACE, birth_place.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(birth_place_data);
                    }

                    if (mJsonObject.has(Constant_Class.BIRTH_DATE)) {
                        birth_date = mJsonObject.getString(Constant_Class.BIRTH_DATE);
                        RealmResults<ListProfileData> birth_date_data = realm.where(ListProfileData.class).contains(Constant_Class.BIRTH_DATE, birth_date.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(birth_date_data);

                    }
                    if (mJsonObject.has(Constant_Class.BIRTH_TIME)) {
                        birth_time = mJsonObject.getString(Constant_Class.BIRTH_TIME);
                        RealmResults<ListProfileData> birth_time_data = realm.where(ListProfileData.class).contains(Constant_Class.BIRTH_TIME, birth_time.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(birth_time_data);
                    }
                    if (mJsonObject.has(Constant_Class.EDUCATION)) {
                        education = mJsonObject.getString(Constant_Class.EDUCATION);
                        RealmResults<ListProfileData> education_data = realm.where(ListProfileData.class).contains(Constant_Class.EDUCATION, education.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(education_data);

                    }
                    if (mJsonObject.has(Constant_Class.OCCUPATION)) {
                        occupation = mJsonObject.getString(Constant_Class.OCCUPATION);
                        RealmResults<ListProfileData> occupation_data = realm.where(ListProfileData.class).contains(Constant_Class.OCCUPATION, occupation.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(occupation_data);

                    }
                    if (mJsonObject.has(Constant_Class.WORK)) {
                        work = mJsonObject.getString(Constant_Class.WORK);
                        RealmResults<ListProfileData> work_data = realm.where(ListProfileData.class).contains(Constant_Class.WORK, work.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(work_data);
                    }

                    if (mJsonObject.has(Constant_Class.ADDRESS)) {
                        address = mJsonObject.getString(Constant_Class.ADDRESS);
                        RealmResults<ListProfileData> address_data = realm.where(ListProfileData.class).contains(Constant_Class.ADDRESS, address.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();

                        mlistProfileData.addAll(address_data);
                    }
                    if (mJsonObject.has(Constant_Class.OFFICE_MOBILE)) {
                        office_mobile = mJsonObject.getString(Constant_Class.OFFICE_MOBILE);
                        RealmResults<ListProfileData> office_mobile_data = realm.where(ListProfileData.class).contains(Constant_Class.OFFICE_MOBILE, office_mobile.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(office_mobile_data);
                    }
                    if (mJsonObject.has(Constant_Class.OFFICE_ADDRESS)) {
                        office_address = mJsonObject.getString(Constant_Class.OFFICE_ADDRESS);
                        RealmResults<ListProfileData> office_address_data = realm.where(ListProfileData.class).contains(Constant_Class.OFFICE_ADDRESS, office_address.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(office_address_data);
                    }
                    if (mJsonObject.has(Constant_Class.SPOUSE_NAME)) {
                        spouse_name = mJsonObject.getString(Constant_Class.SPOUSE_NAME);
                        RealmResults<ListProfileData> spouse_name_data = realm.where(ListProfileData.class).contains(Constant_Class.SPOUSE_NAME, spouse_name.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(spouse_name_data);
                    }
                    if (mJsonObject.has(Constant_Class.MARRIAGE_DATE)) {
                        marriage_date = mJsonObject.getString(Constant_Class.MARRIAGE_DATE);
                        RealmResults<ListProfileData> marriage_date_data = realm.where(ListProfileData.class).contains(Constant_Class.MARRIAGE_DATE, marriage_date.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(marriage_date_data);
                    }
                    if (mJsonObject.has(Constant_Class.SPOUSE_FATHER_NAME)) {
                        spouse_father_name = mJsonObject.getString(Constant_Class.SPOUSE_FATHER_NAME);
                        RealmResults<ListProfileData> spouse_father_name_data = realm.where(ListProfileData.class).contains(Constant_Class.SPOUSE_FATHER_NAME, spouse_father_name.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(spouse_father_name_data);
                    }
                    if (mJsonObject.has(Constant_Class.SPOUSE_MOTHER_NAME)) {
                        spouse_mother_name = mJsonObject.getString(Constant_Class.SPOUSE_MOTHER_NAME);
                        RealmResults<ListProfileData> spouse_mother_name_data = realm.where(ListProfileData.class).contains(Constant_Class.SPOUSE_MOTHER_NAME, spouse_mother_name.toLowerCase()).equalTo(Constant_Class.STATUS, "1").findAll();
                        mlistProfileData.addAll(spouse_mother_name_data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return mlistProfileData;
    }

    @NonNull
    public static RealmList<ListProfileData> getDataFromChildTable(String query, int search) {
        RealmList<ListProfileData> mlistProfileData = new RealmList<>();
        RealmList<ListChildrenData> mListChildrenData = new RealmList<>();
        Realm realm = AppController.getInstance().realm;
        if (search == 1) {
            query = query.toLowerCase();
            RealmResults<ListChildrenData> childrenData = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_NAME, query).or().contains(Constant_Class.CHILD_EDU, query).or().contains(Constant_Class.CHILD_WORK, query).or().contains(Constant_Class.CHILD_BDAY, query).contains(Constant_Class.CHILD_BPLACE, query).contains(Constant_Class.CHILD_BTIME, query).findAll();
            mListChildrenData.addAll(childrenData);
        } else {
            try {
                JSONObject mJsonObject = new JSONObject(query);
                if (mJsonObject.has(Common.Constant_Class.CHILD_BDAY)) {
                    String child_bday = mJsonObject.getString(Common.Constant_Class.CHILD_BDAY);
                    RealmResults<ListChildrenData> child_bday_data = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_BDAY, child_bday.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_bday_data);
                }

                if (mJsonObject.has(Common.Constant_Class.CHILD_EDU)) {
                    String child_edu = mJsonObject.getString(Common.Constant_Class.CHILD_EDU);
                    RealmResults<ListChildrenData> child_edu_data = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_EDU, child_edu.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_edu_data);
                }
                if (mJsonObject.has(Common.Constant_Class.CHILD_WORK)) {
                    String child_work = mJsonObject.getString(Common.Constant_Class.CHILD_WORK);
                    RealmResults<ListChildrenData> child_work_data = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_WORK, child_work.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_work_data);
                }
                if (mJsonObject.has(Common.Constant_Class.CHILD_NAME)) {
                    String child_name = mJsonObject.getString(Common.Constant_Class.CHILD_NAME);
                    RealmResults<ListChildrenData> child_name_data = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_NAME, child_name.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_name_data);
                }

                if (mJsonObject.has(Constant_Class.CHILD_BPLACE)) {
                    String child_bplace = mJsonObject.getString(Constant_Class.CHILD_BPLACE);
                    RealmResults<ListChildrenData> child_bplace_data = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_BPLACE, child_bplace.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_bplace_data);
                }

                if (mJsonObject.has(Constant_Class.CHILD_BTIME)) {
                    String child_btime = mJsonObject.getString(Constant_Class.CHILD_BTIME);
                    RealmResults<ListChildrenData> child_btime_data = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_BTIME, child_btime.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_btime_data);
                }

                if (mJsonObject.has(Constant_Class.CHILD_GENDER)) {
                    String child_gender = mJsonObject.getString(Constant_Class.CHILD_GENDER);
                    RealmResults<ListChildrenData> child_gender_data = realm.where(ListChildrenData.class).contains(Constant_Class.CHILD_GENDER, child_gender.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_gender_data);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < mListChildrenData.size(); i++) {
            ListProfileData profile_data = realm.where(ListProfileData.class).equalTo(Constant_Class.PROFILE_ID, Objects.requireNonNull(mListChildrenData.get(i)).getProfile_id()).findFirst();
            if (!mlistProfileData.contains(profile_data)) {
                mlistProfileData.add(profile_data);
            }
        }
        return mlistProfileData;
    }

    public static void UpdateProfilePassword(String password, String id) {

        ListProfileData mListProfile = AppController.getInstance().realm.where(ListProfileData.class).equalTo(Common.Constant_Class.PROFILE_ID, id).findFirst();
        AppController.getInstance().realm.beginTransaction();
        Objects.requireNonNull(mListProfile).setPassword(password);
        AppController.getInstance().realm.commitTransaction();
    }

    public static void UpdateProfileStatus(@NonNull ArrayList<String> lstSelectedIDs, String status) {
        try {
            Realm realm = AppController.getInstance().realm;
            for (int i = 0; i < lstSelectedIDs.size(); i++) {
                ListProfileData mListProfile = realm.where(ListProfileData.class).equalTo(Common.Constant_Class.PROFILE_ID, lstSelectedIDs.get(i)).findFirst();
                realm.beginTransaction();
                Objects.requireNonNull(mListProfile).setStatus(status);
                realm.commitTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DeleteProfiles(ArrayList<String> lstSelectedIDs) {
        Realm realm = AppController.getInstance().realm;
        for (int i = 0; i < lstSelectedIDs.size(); i++) {
            RealmResults<ListProfileData> results = realm.where(ListProfileData.class).equalTo(Constant_Class.PROFILE_ID, lstSelectedIDs.get(i)).findAll();
            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

    public static void SaveProfile(@NonNull JSONObject mJsonObject) {
        try {
            ListProfileData mListProfileData = new ListProfileData();
            if (mJsonObject.has(Constant_Class.ID)) {
                mListProfileData.setProfile_id(mJsonObject.getString(Constant_Class.ID));
            }
            if (mJsonObject.has(Common.Constant_Class.FIRST_NAME)) {
                mListProfileData.setFirst_name(mJsonObject.getString(Common.Constant_Class.FIRST_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.LAST_NAME)) {
                mListProfileData.setLast_name(mJsonObject.getString(Common.Constant_Class.LAST_NAME));
            }
            if (mJsonObject.has(Constant_Class.CITY)) {
                mListProfileData.setCity(mJsonObject.getString(Constant_Class.CITY));
            }

            if (mJsonObject.has(Common.Constant_Class.FATHER_NAME)) {
                mListProfileData.setFather_name(mJsonObject.getString(Common.Constant_Class.FATHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.MOTHER_NAME)) {
                mListProfileData.setMother_name(mJsonObject.getString(Common.Constant_Class.MOTHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.BIRTH_DATE)) {
                mListProfileData.setBirth_date(mJsonObject.getString(Common.Constant_Class.BIRTH_DATE));
            }
            if (mJsonObject.has(Common.Constant_Class.BIRTH_TIME)) {
                mListProfileData.setBirth_time(mJsonObject.getString(Common.Constant_Class.BIRTH_TIME));
            }
            if (mJsonObject.has(Common.Constant_Class.BIRTH_PLACE)) {
                mListProfileData.setBirth_place(mJsonObject.getString(Common.Constant_Class.BIRTH_PLACE));
            }
            if (mJsonObject.has(Common.Constant_Class.MOBILE)) {
                mListProfileData.setMobile(mJsonObject.getString(Common.Constant_Class.MOBILE));
            }
            if (mJsonObject.has(Common.Constant_Class.PHONE)) {
                mListProfileData.setPhone(mJsonObject.getString(Common.Constant_Class.PHONE));
            }
            if (mJsonObject.has(Common.Constant_Class.BLOOD_GROUP)) {
                mListProfileData.setBlood_group(mJsonObject.getString(Common.Constant_Class.BLOOD_GROUP));
            }
            if (mJsonObject.has(Common.Constant_Class.GENDER)) {
                mListProfileData.setGender(mJsonObject.getString(Common.Constant_Class.GENDER));
            }
            if (mJsonObject.has(Common.Constant_Class.GOTRA)) {
                mListProfileData.setGotra(mJsonObject.getString(Common.Constant_Class.GOTRA));
            }
            if (mJsonObject.has(Common.Constant_Class.EMAIL_ADDRESS)) {
                mListProfileData.setEmail_address(mJsonObject.getString(Common.Constant_Class.EMAIL_ADDRESS));
            }
            if (mJsonObject.has(Common.Constant_Class.ADDRESS)) {
                mListProfileData.setAddress(mJsonObject.getString(Common.Constant_Class.ADDRESS));
            }
            if (mJsonObject.has(Common.Constant_Class.NATIVE_PLACE)) {
                mListProfileData.setNative_place(mJsonObject.getString(Common.Constant_Class.NATIVE_PLACE));
            }
            if (mJsonObject.has(Common.Constant_Class.EDUCATION)) {
                mListProfileData.setEducation(mJsonObject.getString(Common.Constant_Class.EDUCATION));
            }
            if (mJsonObject.has(Common.Constant_Class.OCCUPATION)) {
                mListProfileData.setOccupation(mJsonObject.getString(Common.Constant_Class.OCCUPATION));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_MOBILE)) {
                mListProfileData.setOffice_mobile(mJsonObject.getString(Common.Constant_Class.OFFICE_MOBILE));
            }
            if (mJsonObject.has(Common.Constant_Class.WORK)) {
                mListProfileData.setWork(mJsonObject.getString(Common.Constant_Class.WORK));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_ADDRESS)) {
                mListProfileData.setOffice_address(mJsonObject.getString(Common.Constant_Class.OCCUPATION));
            }
            if (mJsonObject.has(Common.Constant_Class.MARRIAGE_DATE)) {
                mListProfileData.setMarriage_date(mJsonObject.getString(Constant_Class.MARRIAGE_DATE));
            }
            if (mJsonObject.has(Common.Constant_Class.SPOUSE_NAME)) {
                mListProfileData.setSpouse_name(mJsonObject.getString(Common.Constant_Class.SPOUSE_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.SPOUSE_FATHER_NAME)) {
                mListProfileData.setSfather_name(mJsonObject.getString(Common.Constant_Class.SPOUSE_FATHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.SPOUSE_MOTHER_NAME)) {
                mListProfileData.setSmother_name(mJsonObject.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_LAT)) {
                mListProfileData.setOffice_lat(mJsonObject.getString(Common.Constant_Class.OFFICE_LAT));
            }
            if (mJsonObject.has(Common.Constant_Class.OFFICE_LNG)) {
                mListProfileData.setOffice_lng(mJsonObject.getString(Common.Constant_Class.OFFICE_LNG));
            }
            if (mJsonObject.has(Common.Constant_Class.HOME_LAT)) {
                mListProfileData.setHome_lat(mJsonObject.getString(Common.Constant_Class.HOME_LAT));
            }
            if (mJsonObject.has(Common.Constant_Class.HOME_LNG)) {
                mListProfileData.setHome_lng(mJsonObject.getString(Common.Constant_Class.HOME_LNG));
            }
            if (mJsonObject.has(Common.Constant_Class.USER_LAT)) {
                mListProfileData.setUser_lat(mJsonObject.getString(Common.Constant_Class.USER_LAT));
            }
            if (mJsonObject.has(Common.Constant_Class.USER_LNG)) {
                mListProfileData.setUser_lng(mJsonObject.getString(Common.Constant_Class.USER_LNG));
            }

            if (mJsonObject.has(Constant_Class.PROFILE_PIC_URL)) {
                mListProfileData.setProfile_pic_url(mJsonObject.getString(Constant_Class.PROFILE_PIC_URL));
            }
            if (mJsonObject.has(Constant_Class.IMG_FATHER_URL)) {
                mListProfileData.setImg_father_url(mJsonObject.getString(Constant_Class.IMG_FATHER_URL));
            }
            if (mJsonObject.has(Constant_Class.IMG_MOTHER_URL)) {
                mListProfileData.setImg_mother_url(mJsonObject.getString(Constant_Class.IMG_MOTHER_URL));
            }
            if (mJsonObject.has(Constant_Class.IMG_SPOUSE_URL)) {
                mListProfileData.setImg_spouse_url(mJsonObject.getString(Constant_Class.IMG_SPOUSE_URL));
            }
            if (mJsonObject.has(Constant_Class.IMG_SFATHER_URL)) {
                mListProfileData.setImg_sfather_url(mJsonObject.getString(Constant_Class.IMG_SFATHER_URL));
            }
            if (mJsonObject.has(Constant_Class.IMG_SMOTHER_URL)) {
                mListProfileData.setImg_smother_url(mJsonObject.getString(Constant_Class.IMG_SMOTHER_URL));
            }

            if (mJsonObject.has(Constant_Class.UPDATED_TIME)) {
                mListProfileData.setUpdated_time(mJsonObject.getString(Constant_Class.UPDATED_TIME));
            }
            if (mJsonObject.has(Constant_Class.SYNC_TIME)) {
                mListProfileData.setSync_time(mJsonObject.getString(Constant_Class.SYNC_TIME));
            }


            //   String strWhere = "" + Common.Constant_Class.PROFILE_ID + "=" + mJsonObject.getString(Common.Constant_Class.PROFILE_ID);
            //   db.update(Common.Constant_Class.TABLE_PROFILE, values, strWhere, null);

            if (mJsonObject.has(Common.Constant_Class.CHILDS)) {

                JSONArray mJsonArray = new JSONArray(mJsonObject.getString(Common.Constant_Class.CHILDS));
                RealmList<ListChildrenData> mlistchilds = new RealmList<>();

                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsonObj = mJsonArray.getJSONObject(i);
                    ListChildrenData mListChildrendata = new ListChildrenData();

                    if (mJsonObj.has(Constant_Class.CHILD_ID)) {
                        mListChildrendata.setChild_id(mJsonObj.getString(Common.Constant_Class.CHILD_ID));
                    }
                    if (mJsonObj.has(Constant_Class.CHILD_IMAGE_URL)) {
                        mListChildrendata.setChild_img_url(mJsonObj.getString(Common.Constant_Class.CHILD_IMAGE_URL));
                    }

                    if (mJsonObj.has(Common.Constant_Class.CHILD_NAME)) {
                        mListChildrendata.setChild_name(mJsonObj.getString(Common.Constant_Class.CHILD_NAME));
                    }

                    if (mJsonObj.has(Constant_Class.MOBILE)) {
                        mListChildrendata.setMobile(mJsonObj.getString(Common.Constant_Class.MOBILE));
                    }
                    if (mJsonObj.has(Constant_Class.BLOOD_GROUP)) {
                        mListChildrendata.setBlood_group(mJsonObj.getString(Common.Constant_Class.BLOOD_GROUP));
                    }

                    if (mJsonObj.has(Constant_Class.CHILD_GENDER)) {
                        mListChildrendata.setGender(mJsonObj.getString(Common.Constant_Class.CHILD_GENDER));
                    }
                    if (mJsonObj.has(Constant_Class.CHILD_BPLACE)) {
                        mListChildrendata.setBirth_place(mJsonObj.getString(Common.Constant_Class.CHILD_BPLACE));
                    }
                    if (mJsonObj.has(Constant_Class.CHILD_BTIME)) {
                        mListChildrendata.setBirth_time(mJsonObj.getString(Common.Constant_Class.CHILD_BTIME));
                    }
                    if (mJsonObj.has(Constant_Class.IS_INTERESTED)) {
                        String isInterest = mJsonObj.getString(Common.Constant_Class.IS_INTERESTED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setInterest(true);
                        } else {
                            mListChildrendata.setInterest(false);
                        }
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_BDAY)) {
                        mListChildrendata.setChild_bday(mJsonObj.getString(Common.Constant_Class.CHILD_BDAY));
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_EDU)) {
                        mListChildrendata.setChild_edu(mJsonObj.getString(Common.Constant_Class.CHILD_EDU));
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_WORK)) {
                        mListChildrendata.setChild_work(mJsonObj.getString(Common.Constant_Class.CHILD_WORK));
                    }
                    mListChildrendata.setProfile_id(mJsonObject.getString(Constant_Class.ID));
                    mlistchilds.add(mListChildrendata);
                }
                mListProfileData.setmListChildrenData(mlistchilds);
            }

            AppController.getInstance().realm.beginTransaction();
            AppController.getInstance().realm.copyToRealmOrUpdate(mListProfileData);
            AppController.getInstance().realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String DatetoString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String dateTime = dateFormat.format(date);
            System.out.println("Current Date Time : " + dateTime);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date StringToDate(String dtStart) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getDeviceId(Context mContext) {
        @SuppressLint("HardwareIds") String m_androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Constant_Class.DEVICE_ID_VALUE = m_androidId;
    }

    @NonNull
    public static String getUpdatedTime(String timestamp) {
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Integer.parseInt(timestamp) * 1000L);
            return DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        } catch (Exception e) {
            return "0";
        }
    }

    public static void showProgressDialog(Context mContext) {
        try {

            if (pDialog == null) {
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage(Constant_Class.LOADING);
                pDialog.setCancelable(false);
            }


            if (!pDialog.isShowing()) pDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (pDialog.isShowing()) pDialog.cancel();
            pDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    /*public static void ExportMatrimonyData(@NonNull Activity mActiviy) {

    }*/

    public static void ExportSearchData(@NonNull Activity mActiviy) {
        List<ListProfileData> mListProfileResult = AppController.getInstance().mListSearchList;

        if (mListProfileResult != null && mListProfileResult.size() > 0) {

            try {
                File sd = Environment.getExternalStorageDirectory();
                String csvFile = "Vastipatrak.xls";

                File directory = new File(sd.getAbsolutePath());
                //create directory if not exist
                if (!directory.isDirectory()) directory.mkdirs();

                showProgressDialog(mActiviy);

                //file path
                File file = new File(directory, csvFile);
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));
                WritableWorkbook workbook;
                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("profileList", 0);

                sheet.addCell(new Label(0, 0, "ID"));
                sheet.addCell(new Label(1, 0, "FirstName"));
                sheet.addCell(new Label(2, 0, "LastName"));
                sheet.addCell(new Label(3, 0, "Address"));
                sheet.addCell(new Label(4, 0, "City"));
                sheet.addCell(new Label(5, 0, "Father"));
                sheet.addCell(new Label(6, 0, "Mother"));
                sheet.addCell(new Label(7, 0, "Email"));
                sheet.addCell(new Label(8, 0, "Mobile"));
                sheet.addCell(new Label(9, 0, "Phone"));
                sheet.addCell(new Label(10, 0, "Blood"));
                sheet.addCell(new Label(11, 0, "Gotra"));
                sheet.addCell(new Label(12, 0, "Native"));
                sheet.addCell(new Label(13, 0, "Birth Place"));
                sheet.addCell(new Label(14, 0, "Birth date"));
                sheet.addCell(new Label(15, 0, "Birth time"));
                sheet.addCell(new Label(16, 0, "Education"));
                sheet.addCell(new Label(17, 0, "Occupation"));
                sheet.addCell(new Label(18, 0, "Work"));
                sheet.addCell(new Label(19, 0, "Office Address"));
                sheet.addCell(new Label(20, 0, "Office Mobile"));
                sheet.addCell(new Label(21, 0, "Spouse"));
                sheet.addCell(new Label(22, 0, "Marriage date"));
                sheet.addCell(new Label(23, 0, "Father in law"));
                sheet.addCell(new Label(24, 0, "Mother in law"));
                sheet.addCell(new Label(25, 0, "Updated"));
                sheet.addCell(new Label(26, 0, "Sync"));

                for (int i = 0; i < mListProfileResult.size(); i++) {
                    int k = i + 1;

                    sheet.addCell(new Label(0, k, Objects.requireNonNull(mListProfileResult.get(i)).getProfile_id()));
                    sheet.addCell(new Label(1, k, Objects.requireNonNull(mListProfileResult.get(i)).getFirst_name()));
                    sheet.addCell(new Label(2, k, Objects.requireNonNull(mListProfileResult.get(i)).getLast_name()));
                    sheet.addCell(new Label(3, k, Objects.requireNonNull(mListProfileResult.get(i)).getAddress()));
                    sheet.addCell(new Label(4, k, Objects.requireNonNull(mListProfileResult.get(i)).getCity()));
                    sheet.addCell(new Label(5, k, Objects.requireNonNull(mListProfileResult.get(i)).getFather_name()));
                    sheet.addCell(new Label(6, k, Objects.requireNonNull(mListProfileResult.get(i)).getMother_name()));
                    sheet.addCell(new Label(7, k, Objects.requireNonNull(mListProfileResult.get(i)).getEmail_address())); // column and row
                    sheet.addCell(new Label(8, k, Objects.requireNonNull(mListProfileResult.get(i)).getMobile()));
                    sheet.addCell(new Label(9, k, Objects.requireNonNull(mListProfileResult.get(i)).getPhone()));
                    sheet.addCell(new Label(10, k, Objects.requireNonNull(mListProfileResult.get(i)).getBlood_group()));
                    sheet.addCell(new Label(11, k, Objects.requireNonNull(mListProfileResult.get(i)).getGotra()));
                    sheet.addCell(new Label(12, k, Objects.requireNonNull(mListProfileResult.get(i)).getNative_place()));
                    sheet.addCell(new Label(13, k, Objects.requireNonNull(mListProfileResult.get(i)).getBirth_place()));
                    sheet.addCell(new Label(14, k, Objects.requireNonNull(mListProfileResult.get(i)).getBirth_date()));
                    sheet.addCell(new Label(15, k, Objects.requireNonNull(mListProfileResult.get(i)).getBirth_time()));
                    sheet.addCell(new Label(16, k, Objects.requireNonNull(mListProfileResult.get(i)).getEducation()));
                    sheet.addCell(new Label(17, k, Objects.requireNonNull(mListProfileResult.get(i)).getOccupation()));
                    sheet.addCell(new Label(18, k, Objects.requireNonNull(mListProfileResult.get(i)).getWork()));
                    sheet.addCell(new Label(19, k, Objects.requireNonNull(mListProfileResult.get(i)).getOffice_address()));
                    sheet.addCell(new Label(20, k, Objects.requireNonNull(mListProfileResult.get(i)).getOffice_mobile()));
                    sheet.addCell(new Label(21, k, Objects.requireNonNull(mListProfileResult.get(i)).getSpouse_name()));
                    sheet.addCell(new Label(22, k, Objects.requireNonNull(mListProfileResult.get(i)).getMarriage_date()));
                    sheet.addCell(new Label(23, k, Objects.requireNonNull(mListProfileResult.get(i)).getSfather_name()));
                    sheet.addCell(new Label(24, k, Objects.requireNonNull(mListProfileResult.get(i)).getSmother_name()));
                    sheet.addCell(new Label(25, k, Objects.requireNonNull(mListProfileResult.get(i)).getUpdated_time()));
                    sheet.addCell(new Label(26, k, Objects.requireNonNull(mListProfileResult.get(i)).getSync_time()));
                    int counter = 26;
                    for (int j = 0; j < Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().size(); j++) {

                        sheet.addCell(new Label(++counter, 0, "Child Id"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_id()));

                        sheet.addCell(new Label(++counter, 0, "Child Name"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_name()));

                        sheet.addCell(new Label(++counter, 0, "Child Gender"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getGender()));

                        sheet.addCell(new Label(++counter, 0, "Child Bdate"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_bday()));

                        sheet.addCell(new Label(++counter, 0, "Child Btime"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j))).getBirth_time()));

                        sheet.addCell(new Label(++counter, 0, "Child Bplace"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getBirth_place()));

                        sheet.addCell(new Label(++counter, 0, "Interested"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).isInterest() + ""));

                        sheet.addCell(new Label(++counter, 0, "Child Edu"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_edu()));

                        sheet.addCell(new Label(++counter, 0, "Child Work"));
                        sheet.addCell(new Label(counter, k, Objects.requireNonNull(Objects.requireNonNull(mListProfileResult.get(i)).getmListChildrenData().get(j)).getChild_work()));
                    }
                }
                workbook.write();
                workbook.close();
                ExportAlert(mActiviy, file);
                //Toast.makeText(mActiviy, "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();

                hideProgressDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alert(mActiviy, "No Search records found!");
        }
    }

    private static void ExportAlert(@NonNull final Activity mActivity, @NonNull final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(mActivity.getString(R.string.app_name));

        builder.setMessage("Data Exported in a Excel Sheet");
        builder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                //  File fileWithinMyDir = new File(myFilePath);

                if (file.exists()) {
                    intentShareFile.setType("application/xls");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                    mActivity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }

                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    public static void alert(@NonNull Activity mActivity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(mActivity.getString(R.string.app_name));
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).show();
    }

    public static void SendWhatsappMessage(@NonNull Context mActivity, String mob_num, String message) {
        String digits = "\\d+";
        if (mob_num.matches(digits)) {
            try {
                //linking for whatsapp
                Uri uri = Uri.parse("whatsapp://send?phone=+91" + mob_num + "&text=" + URLEncoder.encode(message, "UTF-8"));
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void getChildRandomColor(@NonNull Context context, int position, LinearLayout ll_event) {
        int i = position % 10;
        Log.v("color number:", "" + i);
        ll_event.setAlpha((float) 0.9);
        switch (i) {
            case 0:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape5));
                break;
            case 1:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape1));
                break;
            case 2:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape2));
                break;
            case 3:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape3));
                break;
            case 4:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape4));
                break;
            case 5:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape5));
                break;
            case 6:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape6));
                break;
            case 7:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape7));
                break;
            case 8:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape8));
                break;
            case 9:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape9));
                break;
            case 10:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.child_shape10));
                break;
        }

    }


    public static void getParentRandomColor(@NonNull Context context, int position, LinearLayout ll_event) {
        int i = position % 10;
        Log.v("color number:", "" + i);
        ll_event.setAlpha((float) 0.9);
        switch (i) {
            case 0:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape5));
                break;
            case 1:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape1));
                break;
            case 2:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape2));
                break;
            case 3:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape3));
                break;
            case 4:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape4));
                break;
            case 5:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape5));
                break;
            case 6:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape6));
                break;
            case 7:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape7));
                break;
            case 8:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape8));
                break;
            case 9:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape9));
                break;
            case 10:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.parent_shape10));
                break;
        }

    }


    public static void getRandomColor(@NonNull Context context, int position, LinearLayout ll_event) {
        int i = position % 10;
        Log.v("color number:", "" + i);
        ll_event.setAlpha((float) 0.9);
        switch (i) {
            case 0:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape5));
                break;
            case 1:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape1));
                break;
            case 2:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape2));
                break;
            case 3:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape3));
                break;
            case 4:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape4));
                break;
            case 5:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape5));
                break;
            case 6:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape6));
                break;
            case 7:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape7));
                break;
            case 8:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape8));
                break;
            case 9:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape9));
                break;
            case 10:
                ll_event.setBackground(context.getResources().getDrawable(R.drawable.shape10));
                break;
        }

    }


    @Nullable
    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        String str = null;
        try {
            Date date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static class Constant_Class {

        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";

        public static final String YOUTUBE_API_KEY = "AIzaSyBOkoXTYsY32OQtLTxidxci5R3Zml84oUY";

        public static final String PERSONAL = "     PERSONAL  ";
        public static final String BUSINESS = "     BUSINESS  ";
        public static final String FAMILY = "     FAMILY  ";

        public static final String GIRLS = "     GIRLS  ";
        public static final String BOYS_P1 = "  BOYS PART-1  ";
        public static final String BOYS_P2 = "   BOYS PART-2";

        public static final int NonActive = 0;
        public static final String AdminControl = "AdminControl";


        public static final String API_KEY = "api_key";
        public static final String DEVICE_TYPE = "device_type";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String DEVICE_ID = "int_udid";
        public static final String ACCESS_TOKEN = "access_token";

        public static final String API_KEY_VALUE = "q1fgdfggfw2e2rt3y5u6i8iug12fh123yhhddaf";
        public static final String DEVICE_TYPE_VALUE = "Android";
        public static final String PREF_NAME = "Vastipatrak";
        public static final String PREF_TOKEN = "Pref_Token";
        public static final String SCREEN = "screen";
        public static final String SEARCH_FRAGMENT = "SearchFragment";
        public static final String IS_UPDATE = "is_update";
        public static final String IS_RESET = "is_reset";
        public static final String USER_ID = "user_id";
        public static final String EVENT_DATE = "event_date";
        public static final String LOADING = "Loading...";
        public static final String NO_CONNECTION = "No internet connection!";
        public static final String EMAIL = "email";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String EMAIL_ADDRESS = "email_address";
        public static final String PASSWORD = "password";
        public static final String REPEAT_PASSWORD = "repeat_password";
        public static final String SUCCESS = "success";
        public static final String MESSAGE = "message";
        public static final String TRUE = "true";
        public static final String USERNAME = "username";
        public static final String MOBILE = "mobile";
        public static final String DATA = "data";
        public static final String USER_LAT = "user_lat";
        public static final String USER_LNG = "user_lng";
        public static final String HOME_LAT = "home_lat";
        public static final String HOME_LNG = "home_lng";
        public static final String OFFICE_LAT = "office_lat";
        public static final String OFFICE_LNG = "office_lng";
        public static final String MYPROFILE_SP = "myprofile";
        public static final String OFFICE_ADDRESS = "office_address";
        public static final String ADDRESS = "address";
        public static final String SUB_CAST = "sub_cast";
        public static final String EKDO = "ekdo";
        public static final String OCCUPATION = "occupation";
        public static final String OFFICE_MOBILE = "office_mobile";
        public static final String WORK = "work";
        public static final String QUERY = "query";
        public static final String QUERY_STRING = "query_string";
        public static final String PUSH_MESSAGE = "push_message";
        public static final String ID = "id";
        public static final String IDList = "idList";
        public static final String STATUS = "status";
        public static final String PROFILE_PIC_URL = "profile_pic_url";
        public static final int sCorner = 25;
        public static final int sMargin = 1;
        public static final String FATHER_NAME = "father_name";
        public static final String MOTHER_NAME = "mother_name";
        public static final String NATIVE_PLACE = "native_place";
        public static final String BIRTH_DATE = "birth_date";
        public static final String BIRTH_TIME = "birth_time";
        public static final String BIRTH_PLACE = "birth_place";
        public static final String BLOOD_GROUP = "blood_group";
        public static final String PHONE = "phone";
        public static final String GENDER = "gender";
        public static final String GOTRA = "gotra";
        public static final String IS_LOCATION_ENABLE = "is_location_enable";
        public static final String UPDATED_TIME = "updated_time";
        public static final String ROLE = "role";
        public static final String SYNC_TIME = "sync_time";
        public static final String CITY = "city";
        public static final String TITLE_BLOOD_GROUP = "Blood Group";
        public static final String A_POSITIVE = "A +VE";
        public static final String A_NAGATIVE = "A -VE";
        public static final String B_POSITIVE = "B +VE";
        public static final String B_NAGATIVE = "B -VE";
        public static final String O_POSITIVE = "O +VE";
        public static final String O_NAGATIVE = "O -VE";
        public static final String EDUCATION = "education";
        public static final String PROFILE_PIC = "profile_pic";
        public static final String PROFILE_PIC_HASH = "profile_pic_hash";
        public static final String IMG_MOTHER = "img_mother";
        public static final String IMG_FATHER = "img_father";
        public static final String SPOUSE_NAME = "spouse_name";
        public static final String MARRIAGE_DATE = "marriage_date";
        public static final String SPOUSE_FATHER_NAME = "spouse_father_name";
        public static final String SPOUSE_MOTHER_NAME = "spouse_mother_name";
        public static final int sBorder = 5;
        public static final String sColor = "#FFC0CB";
        public static final long LOCATION_INTERVAL = 1000 * 30;
        public static final String CHILDS = "childs";
        public static final String CHILD_DELETE = "delete";
        public static final String CHILD_ID = "id";
        public static final String CHILD_NAME = "child_name";
        public static final String CHILD_BDAY = "child_bday";
        public static final String CHILD_GENDER = "gender";
        public static final String CHILD_BTIME = "birth_time";
        public static final String CHILD_BPLACE = "birth_place";
        public static final String IS_INTERESTED = "is_interested";
        public static final String CHILD_EDU = "child_edu";
        public static final String CHILD_WORK = "child_work";
        public static final String FragmentSp = "fragment";
        public static final String CHILD_IMAGE = "child_image";
        public static final String IMG_SPOUSE = "img_spouse";
        public static final String IMG_SMOTHER = "img_smother";
        public static final String IMG_SFATHER = "img_sfather";
        public static final String PROFILE_ID = "profile_id";
        public static final String TBTN_SHARE = "tbtn_share";
        public static final String TBTN_SYNC = "tbtn_sync";
        static final String IMG_FATHER_URL = "img_father_url";
        static final String IMG_MOTHER_URL = "img_mother_url";
        static final String IMG_SPOUSE_URL = "img_spouse_url";
        static final String IMG_SFATHER_URL = "img_sfather_url";
        static final String IMG_SMOTHER_URL = "img_smother_url";
        static final String CHILD_IMAGE_URL = "child_image_url";
        private static final String BASE_URL = "http://www.srbrothersinfotech.com/directory-dev";
        //  private static final String BASE_URL= "http://www.geniusaccountancy.in/directory-dev";
        public static final String LOGIN_URL = BASE_URL + "/API/login";
        public static final String SIGNUP_URL = BASE_URL + "/API/register";
        public static final String FORGOT_PASSWORD_URL = BASE_URL + "/API/forgotPassword";
        public static final String CHANGE_PASSWORD_URL = BASE_URL + "/API/changePassword";
        public static final String LOGOUT_URL = BASE_URL + "/API/logout";
        public static final String DELETE_URL = BASE_URL + "/API/delete";
        public static final String STATUS_URL = BASE_URL + "/API/StatusChange";
        public static final String SYNC_URL = BASE_URL + "/API/sync";
        public static final String INACTIVES_URL = BASE_URL + "/API/getInactiveUsers";
        public static final String PROFILE_URL = BASE_URL + "/API/profile";
        public static final String EVENTS_URL = BASE_URL + "/API/getEvents";
        public static final String GET_CITIES_URL = BASE_URL + "/API/getCities";
        public static final String CHANGE_ROLE_URL = BASE_URL + "/API/changeRole";
        public static final String BLOCK_USERS_URL = BASE_URL + "/API/blockUsers";
        public static String DEVICE_ID_VALUE = "";
    }
}
