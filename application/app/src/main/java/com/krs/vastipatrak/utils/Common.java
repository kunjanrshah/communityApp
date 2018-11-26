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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ExportProfileData;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.model.MatrimonyProfileData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public static String yyyy_MM_dd = "yyyy-MM-dd";
    public static String dd_MMM_yyyy = "dd-MMM-yyyy";
    public static String ddMMMyyyy = "dd/MM/yyyy";
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
            return false;
        }

        return true;
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

    public static boolean hasPermission(@NonNull Context mContext, @NonNull String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mContext, perm));
    }

/*
    private static String getDistanceOnRoad(double latitude, double longitude, double prelatitute, double prelongitude) {
        String result_in_kms = "";
        String strurl = "http://maps.google.com/maps/api/directions/xml?origin=" + latitude + "," + longitude + "&destination=" + prelatitute + "," + prelongitude + "&sensor=false&units=metric";
        String tag[] = {"text"};
        Log.d("getDistanceOnRoad","step "+strurl);
        //  HttpResponse response = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strurl);
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream is = urlConnection.getInputStream();

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            if (doc != null) {
                NodeList nl;
                ArrayList args = new ArrayList();
                for (String s : tag) {
                    nl = doc.getElementsByTagName(s);
                    if (nl.getLength() > 0) {
                        Node node = nl.item(nl.getLength() - 1);
                        args.add(node.getTextContent());
                    } else {
                        args.add(" - ");
                    }
                }
                result_in_kms = String.format("%s", args.get(0));
                Log.d("getDistanceOnRoad", "step result_in_kms :" + result_in_kms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result_in_kms;
    }
*/

/*
    public static void getDistanceOnRoad(Context mcontext, String slat, String slng, String dlat, String dlng) {
        JSONObject locationJsonObject = new JSONObject();
        try {
            String sloc = slat + "," + slng;
            String dloc = dlat + "," + dlng;
            locationJsonObject.put("origin", sloc);
            locationJsonObject.put("destination", dloc);
            RequestQueue queue = Volley.newRequestQueue(mcontext);
            String url = "http://maps.googleapis.com/maps/api/distancematrix/" +
                    "json?origins=" + locationJsonObject.getString("origin") + "&destinations=" + locationJsonObject.getString("destination") + "&mode=driving&" +
                    "language=en-EN&sensor=false";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject mjson = new JSONObject(response);
                                if (mjson.has("rows")) {
                                    JSONArray jsonArray = mjson.getJSONArray("rows");
                                    JSONObject object = jsonArray.getJSONObject(0);
                                    if (object.has("elements")) {
                                        JSONArray jsonElements = mjson.getJSONArray("elements");
                                        JSONObject object1 = jsonElements.getJSONObject(0);
                                        JSONObject mobject1 = object1.getJSONObject("distance");
                                        String distance = mobject1.getString("text");
                                        JSONObject mobject2 = object1.getJSONObject("duration");
                                        String time = mobject2.getString("text");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            Log.d("distance: ", "Response is: " + response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("distance: ", "That didn't work!");
                }
            });
            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


    public static boolean CheckGpsStatus(Context mcontext) {
        LocationManager locationManager;
        boolean GpsStatus;

        locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);

        return !GpsStatus;

    }


    public static void showDirections(Activity mActivity, double slatitude, double slongitude, double dlatitude, double dlongitude, String address) {

        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f &daddr=%f,%f", slatitude, slongitude, dlatitude, dlongitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        mActivity.startActivity(intent);
    }

    public static boolean isOnline(Context mContext) {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
            if (netInfo != null) {
                if (netInfo.isConnected()) {
                    return true;
                } else {
                    Toast.makeText(mContext, "Network is not connected!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            Toast.makeText(mContext, "Network is not connected!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        rotatedWidth = dbo.outWidth;
        rotatedHeight = dbo.outHeight;

        /*int orientation = getOrientation(context, photoUri);
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {

        }*/

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
        /*if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }*/

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

    public static void alert(Context context, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
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
        try {
            for (int i = 0; i < lstSelectedIDs.size(); i++) {
                RealmResults<ListProfileData> results = realm.where(ListProfileData.class).equalTo(Constant_Class.PROFILE_ID, lstSelectedIDs.get(i)).findAll();
                if (!realm.isInTransaction()) {
                    realm.beginTransaction();
                    results.deleteAllFromRealm();
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ListProfileData SaveProfile(@NonNull JSONObject mJsonObject) {
        try {

            final ListProfileData mListProfileData = new ListProfileData();
            if (mJsonObject.has(Constant_Class.ID)) {
                mListProfileData.setProfile_id(mJsonObject.getString(Constant_Class.ID));
            }

            if (mJsonObject.has(Constant_Class.BDATE_REMINDER_ID)) {
                mListProfileData.setBdate_reminder_id(mJsonObject.getString(Constant_Class.BDATE_REMINDER_ID));
            }
            if (mJsonObject.has(Constant_Class.SPOUSE_BDATE_REMINDER_ID)) {
                mListProfileData.setSpouse_bdate_reminder_id(mJsonObject.getString(Constant_Class.SPOUSE_BDATE_REMINDER_ID));
            }
            if (mJsonObject.has(Constant_Class.MDATE_REMINDER_ID)) {
                mListProfileData.setMdate_reminder_id(mJsonObject.getString(Constant_Class.MDATE_REMINDER_ID));
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
            if (mJsonObject.has(Constant_Class.IS_SHARE)) {
                mListProfileData.setIs_block(mJsonObject.getString(Constant_Class.IS_SHARE));
            }
            if (mJsonObject.has(Constant_Class.IS_LOCATION_ENABLE)) {
                mListProfileData.setIs_location_enable(mJsonObject.getString(Constant_Class.IS_LOCATION_ENABLE));
            }

            if (mJsonObject.has(Common.Constant_Class.FATHER_NAME)) {
                mListProfileData.setFather_name(mJsonObject.getString(Common.Constant_Class.FATHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.MOTHER_NAME)) {
                mListProfileData.setMother_name(mJsonObject.getString(Common.Constant_Class.MOTHER_NAME));
            }
            if (mJsonObject.has(Common.Constant_Class.BIRTH_DATE)) {
                mListProfileData.setBirth_date(ChangedateFormat(mJsonObject.getString(Common.Constant_Class.BIRTH_DATE)));
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
                mListProfileData.setOffice_address(mJsonObject.getString(Constant_Class.OFFICE_ADDRESS));
            }
            if (mJsonObject.has(Common.Constant_Class.MARRIAGE_DATE)) {
                mListProfileData.setMarriage_date(ChangedateFormat(mJsonObject.getString(Constant_Class.MARRIAGE_DATE)));
            }
            if (mJsonObject.has(Common.Constant_Class.SPOUSE_NAME)) {
                mListProfileData.setSpouse_name(mJsonObject.getString(Common.Constant_Class.SPOUSE_NAME));
            }

            if (mJsonObject.has(Constant_Class.SPOUSE_MOBILE)) {
                mListProfileData.setSponse_mobile(mJsonObject.getString(Common.Constant_Class.SPOUSE_MOBILE));
            }

            if (mJsonObject.has(Constant_Class.SPOUSE_NATIVE)) {
                mListProfileData.setSponse_native(mJsonObject.getString(Common.Constant_Class.SPOUSE_NATIVE));
            }
            if (mJsonObject.has(Constant_Class.SPOUSE_EDU)) {
                mListProfileData.setSpouse_education(mJsonObject.getString(Common.Constant_Class.SPOUSE_EDU));
            }
            if (mJsonObject.has(Constant_Class.SPOUSE_BG)) {
                mListProfileData.setSponse_bg(mJsonObject.getString(Common.Constant_Class.SPOUSE_BG));
            }

            if (mJsonObject.has(Constant_Class.SPOUSE_BDATE)) {
                mListProfileData.setSponse_bdate(ChangedateFormat(mJsonObject.getString(Common.Constant_Class.SPOUSE_BDATE)));
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

                    if (mJsonObj.has(Constant_Class.CHILD_BDATE_REMINDER_ID)) {
                        mListChildrendata.setChild_bdate_reminder_id(mJsonObj.getString(Common.Constant_Class.CHILD_BDATE_REMINDER_ID));
                    }

                    if (mJsonObj.has(Constant_Class.GENDER)) {
                        mListChildrendata.setGender(mJsonObj.getString(Common.Constant_Class.GENDER));
                    }
                    if (mJsonObj.has(Constant_Class.BIRTH_PLACE)) {
                        mListChildrendata.setBirth_place(mJsonObj.getString(Constant_Class.BIRTH_PLACE));
                    }
                    if (mJsonObj.has(Constant_Class.BIRTH_TIME)) {
                        mListChildrendata.setBirth_time(mJsonObj.getString(Constant_Class.BIRTH_TIME));
                    }
                    if (mJsonObj.has(Constant_Class.IS_INTERESTED)) {
                        String isInterest = mJsonObj.getString(Common.Constant_Class.IS_INTERESTED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setInterest(true);
                        } else {
                            mListChildrendata.setInterest(false);
                        }
                    }

                    if (mJsonObj.has(Constant_Class.IS_MARRIED)) {
                        String isInterest = mJsonObj.getString(Common.Constant_Class.IS_MARRIED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setIs_married(true);
                        } else {
                            mListChildrendata.setIs_married(false);
                        }
                    }

                    if (mJsonObj.has(Common.Constant_Class.CHILD_BDAY)) {
                        mListChildrendata.setChild_bday(ChangedateFormat(mJsonObj.getString(Common.Constant_Class.CHILD_BDAY)));
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
            try {
                if (AppController.getInstance().realm.isInTransaction()) {
                    AppController.getInstance().realm.commitTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            AppController.getInstance().realm.beginTransaction();
            AppController.getInstance().realm.copyToRealmOrUpdate(mListProfileData);
            AppController.getInstance().realm.commitTransaction();

            /*AppController.getInstance().realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // AppController.getInstance().realm.copyFromRealm(mListProfileData);
                    AppController.getInstance().realm.copyToRealmOrUpdate(mListProfileData);
                }
            });*/

            return mListProfileData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ExportProfile(JSONObject mJsonObject, Activity mActivity) {
        try {

            ExportProfileData mListProfileData = new ExportProfileData();
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
                mListProfileData.setBirth_date(ChangedateFormat(mJsonObject.getString(Common.Constant_Class.BIRTH_DATE)));
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
                mListProfileData.setMarriage_date(ChangedateFormat(mJsonObject.getString(Constant_Class.MARRIAGE_DATE)));
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

                    if (mJsonObj.has(Constant_Class.GENDER)) {
                        mListChildrendata.setGender(mJsonObj.getString(Common.Constant_Class.GENDER));
                    }
                    if (mJsonObj.has(Constant_Class.BIRTH_PLACE)) {
                        mListChildrendata.setBirth_place(mJsonObj.getString(Constant_Class.BIRTH_PLACE));
                    }
                    if (mJsonObj.has(Constant_Class.BIRTH_TIME)) {
                        mListChildrendata.setBirth_time(mJsonObj.getString(Constant_Class.BIRTH_TIME));
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
                        mListChildrendata.setChild_bday(ChangedateFormat(mJsonObj.getString(Common.Constant_Class.CHILD_BDAY)));
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
            // AppController.getInstance().realm.copyFromRealm(mListProfileData);
            AppController.getInstance().realm.copyToRealmOrUpdate(mListProfileData);
            AppController.getInstance().realm.commitTransaction();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MatrimonyProfile(@NonNull JSONObject mJsonObject, String child_gender, String is_interested, boolean isShared) {
        try {

            MatrimonyProfileData mListProfileData = new MatrimonyProfileData();
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
                mListProfileData.setBirth_date(ChangedateFormat(mJsonObject.getString(Common.Constant_Class.BIRTH_DATE)));
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
                mListProfileData.setMarriage_date(ChangedateFormat(mJsonObject.getString(Constant_Class.MARRIAGE_DATE)));
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
                    String isInterest = "";
                    String childgender = "";
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

                    if (mJsonObj.has(Constant_Class.GENDER)) {

                        childgender = mJsonObj.getString(Common.Constant_Class.GENDER);
                        mListChildrendata.setGender(mJsonObj.getString(Common.Constant_Class.GENDER));
                    }
                    if (mJsonObj.has(Constant_Class.BIRTH_PLACE)) {
                        mListChildrendata.setBirth_place(mJsonObj.getString(Constant_Class.BIRTH_PLACE));
                    }
                    if (mJsonObj.has(Constant_Class.BIRTH_TIME)) {
                        mListChildrendata.setBirth_time(mJsonObj.getString(Constant_Class.BIRTH_TIME));
                    }

                    if (mJsonObj.has(Constant_Class.IS_INTERESTED)) {
                        isInterest = mJsonObj.getString(Common.Constant_Class.IS_INTERESTED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setInterest(true);
                        } else {
                            mListChildrendata.setInterest(false);
                        }
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_BDAY)) {
                        mListChildrendata.setChild_bday(ChangedateFormat(mJsonObj.getString(Common.Constant_Class.CHILD_BDAY)));
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_EDU)) {
                        mListChildrendata.setChild_edu(mJsonObj.getString(Common.Constant_Class.CHILD_EDU));
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_WORK)) {
                        mListChildrendata.setChild_work(mJsonObj.getString(Common.Constant_Class.CHILD_WORK));
                    }
                    if (!isShared) {
                        if (child_gender.equalsIgnoreCase(childgender) && is_interested.equalsIgnoreCase(isInterest)) {
                            mListChildrendata.setProfile_id(mJsonObject.getString(Constant_Class.ID));
                            mlistchilds.add(mListChildrendata);
                        }
                    }
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

    public static String ChangedateFormat(String strDate) {
        //String mStringDate = "25-Nov-15 14:23:34";
        String oldFormat = "yyyy-MM-dd";
        String newFormat = "dd/MM/yyyy";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            if (!strDate.equalsIgnoreCase("0000-00-00") && !strDate.isEmpty()) {
                myDate = dateFormat.parse(strDate);
                SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
                formatedDate = timeFormat.format(myDate);
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formatedDate;
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
            ProgressBar progressbar = (ProgressBar) pDialog.findViewById(android.R.id.progress);
            progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#F50057"), android.graphics.PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (pDialog != null && pDialog.isShowing()) pDialog.cancel();
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

    public static void ExportSearchData(@NonNull Activity mActiviy) {

        RealmResults<ExportProfileData> mListProfileResult = AppController.getInstance().realm.where(ExportProfileData.class).findAll();

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
                WritableSheet sheet = workbook.createSheet("ProfileList", 0);

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
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (file.exists()) {
                    intentShareFile.setType("application/xls");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                    mActivity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
            }
        });
        builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
                mActivity.startActivity(intent);
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

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawTextToBitmap(Bitmap bitmap, String gText) {
        //  Resources resources = mcontext.getResources();
        // float scale = resources.getDisplayMetrics().density;

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (24));

        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(gText, 10, 20, paint);

        return bitmap;
    }


    //method to convert your text to image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
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

    public static String getCapsSentences(String tagName) {
        String[] splits = tagName.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String eachWord = splits[i];
            if (i > 0 && eachWord.length() > 0) {
                sb.append(" ");
            }
            String cap = eachWord.substring(0, 1).toUpperCase() + eachWord.substring(1);
            sb.append(cap);
        }
        return sb.toString();
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
    public static String parseDateToddMMyyyy(String mydate, String inputPattern, String outputPattern) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        String str = "";
        try {
            Date date = inputFormat.parse(mydate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean CompareTwoDates(String from, String to) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strFrom = sdf.parse(from);
        Date strTo = sdf.parse(to);
        if (strTo.after(strFrom) || strTo.equals(strFrom)) {
            return true;
        } else {
            return false;
        }
    }

    public static double CalculationByDistance(double lat1, double lon1, double lat2, double lon2) {

        int Radius = 6371;// radius of earth in Km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        // double meter = valueResult % 1000;
        //  int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.d("Radius Value", "lat1: " + lat1 + " lon1: " + lon1 + " lat2: " + lat2 + " lon2: " + lon2 + " KM " + kmInDec);

        return Radius * c;
    }

    public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

   /* public static double distance(double lat1, double lon1, double lat2, double lon2) {

        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);
        float distanceInMeters = loc1.distanceTo(loc2);
        int distanceInKm = 0;
        if (distanceInMeters != 0) {
            distanceInKm = (int) (distanceInMeters / 1000);
        }

        return (distanceInKm);
    }*/

    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    private boolean checktimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class getDistance extends AsyncTask<String, String, String> {
        public String strDisctance = "";
        TextView txtDistance;
        Activity mActivity;

        public getDistance(Activity mActivity, TextView txtDistance) {
            this.txtDistance = txtDistance;
            this.mActivity = mActivity;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (!strings[0].isEmpty() && !strings[1].isEmpty() && !strings[2].isEmpty() && !strings[3].isEmpty()) {
                double result_in_kms = CalculationByDistance(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3]));
                result_in_kms = round(result_in_kms, 2);
                return String.valueOf(result_in_kms);
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            s = s + ">";
            Log.d("getDistance", "distance: " + s);

            if (txtDistance != null) {
                String next = "<font color='#EE0000'>" + s + "</font>";
                txtDistance.setText(Html.fromHtml(next));
            } else {
                strDisctance = s;
            }
        }
    }


    public static class Constant_Class {

        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";

        public static final String LAN = "lan";

        public static final String YOUTUBE_API_KEY = "AIzaSyBOkoXTYsY32OQtLTxidxci5R3Zml84oUY";

        public static final String PERSONAL = "PERSONAL";
        public static final String BUSINESS = " PROFESSIONAL ";
        public static final String FAMILY = "FAMILY";
        public static final String RELATIVES = " RELATIVES ";

        public static final String _PERSONAL = "     PERSONAL     ";
        public static final String _BUSINESS = " PROFESSIONAL";
        public static final String _FAMILY = "      FAMILY      ";

        public static final String NOTIFICATION = "notification";
        public static final int NonActive = 0;
        public static final String AdminControl = "AdminControl";

        public static final String ERROR_CODE = "errorcode";
        public static final String ERROR_13 = "-13";


        public static final String SHARE_USER_IDS = "share_user_ids";
        public static final String IS_SHARE = "is_share";
        public static final String CAN_SHARE = "can_share";
        public static final String API_KEY = "api_key";
        public static final String DEVICE_TYPE = "device_type";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String DEVICE_ID = "int_udid";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String NEAR_BY = "nearBy";
        public static final String DISTANCE = "distance";

        public static final String DATE = "date";
        public static final String BIRTH_DATE = "birth_date";
        public static final String CHILD_BIRTH_DATE = "child_birth_date";
        public static final String WIFE_BIRTH_DATE = "spouse_birth_date";
        public static final String MARRIAGE_DATE = "marriage_date";
        public static final String RELATION = "relation";
        public static final String TO_USER_ID = "to_user_id";
        public static final String RELATIONSHIP_STATUS = "relationship_status";
        public static final String RELATIONSHIP_ID = "relationship_id";
        public static final String PAGE = "page";
        public static final String API_KEY_VALUE = "q1fgdfggfw2e2rt3y5u6i8iug12fh123yhhddaf";
        public static final String DEVICE_TYPE_VALUE = "Android";
        public static final String PREF_NAME = "Vastipatrak";
        public static final String PREF_FILTER = "Vastipatrak_Filter";
        public static final String PREF_TOKEN = "Pref_Token";
        public static final String SCREEN = "screen";
        public static final String SEARCH_FRAGMENT = "SearchFragment";
        public static final String IS_UPDATE = "is_update";
        public static final String IS_RESET = "is_reset";
        public static final String USER_ID = "user_id";

        public static final String UPDATE_USER_ID = "update_user_id";
        public static final String EVENT_DATE = "event_date";
        public static final String LOADING = "Loading...";
        public static final String NO_CONNECTION = "No internet connection!";
        public static final String EMAIL = "email";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";

        public static final String TO_FIRST_NAME = "to_first_name";
        public static final String TO_LAST_NAME = "to_last_name";
        public static final String FROM_FIRST_NAME = "from_first_name";
        public static final String FROM_LAST_NAME = "from_last_name";
        public static final String REQUESTED = "REQUESTED";
        public static final String ACCEPTED = "ACCEPTED";
        public static final String REJECTED = "REJECTED";

        public static final String EMAIL_ADDRESS = "email_address";
        public static final String PASSWORD = "password";
        public static final String PLAIN_PASSWORD = "plain_password";

        public static final String REPEAT_PASSWORD = "repeat_password";
        public static final String SUCCESS = "success";
        public static final String MESSAGE = "message";
        public static final String TOTAL_RECORDS = "totalRecords";
        public static final String TRUE = "true";
        public static final String USERNAME = "username";
        public static final String MOBILE = "mobile";
        public static final String DATA = "data";
        public static final String LAT = "lat";
        public static final String LNG = "lng";
        public static final String KM = "km";
        public static final String USER_LAT = "user_lat";
        public static final String USER_LNG = "user_lng";
        public static final String CURR_LAT = "curr_lat";
        public static final String CURR_LNG = "curr_lng";
        public static final String HOME_LAT = "home_lat";
        public static final String HOME_LNG = "home_lng";
        public static final String OFFICE_LAT = "office_lat";
        public static final String OFFICE_LNG = "office_lng";
        public static final String LOCATION_TYPE = "location_type";
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

        public static final String BDATE_REMINDER_ID = "bdate_reminder_id";
        public static final String SPOUSE_BDATE_REMINDER_ID = "spouse_bdate_reminder_id";
        public static final String MDATE_REMINDER_ID = "mdate_reminder_id";

        public static final String IDList = "idList";
        public static final String STATUS = "status";
        public static final String PROFILE_PIC_URL = "profile_pic_url";
        public static final int sCorner = 25;
        public static final int sMargin = 1;
        public static final String FATHER_NAME = "father_name";
        public static final String MOTHER_NAME = "mother_name";
        public static final String NATIVE_PLACE = "native_place";
        public static final String FROM_BIRTH_DATE = "from_birth_date";
        public static final String TO_BIRTH_DATE = "to_birth_date";
        public static final String BIRTH_TIME = "birth_time";
        public static final String BIRTH_PLACE = "birth_place";
        public static final String BLOOD_GROUP = "blood_group";
        public static final String CHILD_BLOOD_GROUP = "child_blood_group";
        public static final String PHONE = "phone";
        public static final String GENDER = "gender";
        public static final String CHILD_GENDER = "child_gender";
        public static final String CHILD_MOBILE = "child_mobile";
        public static final String GOTRA = "gotra";
        public static final String IS_LOCATION_ENABLE = "is_location_enable";
        public static final String UPDATED_TIME = "updated_time";
        public static final String ROLE = "role";
        public static final String SYNC_TIME = "sync_time";
        public static final String CITY = "city";
        public static final String TITLE_BLOOD_GROUP = "Blood Group";
        public static final String TITLE_GOTRA = "Gotra";
        public static final String TITLE_CHILD_BLOOD_GROUP = "Child BG";
        public static final String TITLE_SPOUSE_BLOOD_GROUP = "SPOUSE BG";
        public static final String A_POSITIVE = "A +VE";
        public static final String A_NAGATIVE = "A -VE";
        public static final String B_POSITIVE = "B +VE";
        public static final String B_NAGATIVE = "B -VE";
        public static final String O_POSITIVE = "O +VE";
        public static final String O_NAGATIVE = "O -VE";
        public static final String AB_POSITIVE = "AB +VE";
        public static final String AB_NAGATIVE = "AB -VE";
        public static final String EDUCATION = "education";
        public static final String PROFILE_PIC = "profile_pic";
        public static final String PROFILE_PIC_HASH = "profile_pic_hash";
        public static final String IMG_MOTHER = "img_mother";
        public static final String IMG_FATHER = "img_father";
        public static final String SPOUSE_NAME = "spouse_name";

        public static final String SPOUSE_BG = "spouse_blood_group";
        public static final String SPOUSE_EDU = "spouse_education";

        public static final String SPOUSE_NATIVE = "spouse_native_place";
        public static final String SPOUSE_BDATE = "spouse_birth_place";
        public static final String SPOUSE_MOBILE = "spouse_mobile";
        public static final String FROM_MARRIAGE_DATE = "from_marriage_date";
        public static final String TO_MARRIAGE_DATE = "to_marriage_date";
        public static final String MARRIED = "1";
        public static final String UNMARRIED = "0";
        public static final String SPOUSE_FATHER_NAME = "spouse_father_name";
        public static final String SPOUSE_MOTHER_NAME = "spouse_mother_name";
        public static final int sBorder = 5;
        public static final String sColor = "#FFC0CB";
        //public static final long LOCATION_INTERVAL = 1000 * 30;
        public static final String CHILDS = "childs";
        public static final String CHILD_DELETE = "delete";
        public static final String CHILD_ID = "id";
        public static final String _CHILD_ID = "child_id";
        public static final String CHILD_NAME = "child_name";

        public static final String CHILD_BDATE_REMINDER_ID = "child_bdate_reminder_id";

        public static final String CHILD_BDAY = "child_bday";
        public static final String FROM_CHILD_BDAY = "from_child_bday";
        public static final String TO_CHILD_BDAY = "to_child_bday";
        public static final String CHILD_BTIME = "birth_time";
        public static final String CHILD_BPLACE = "child_birth_place";
        public static final String CHILD_MARRIAGE = "isMarried";
        public static final String FROM_CHILD_AGE = "from_child_age";
        public static final String TO_CHILD_AGE = "to_child_age";
        public static final String IS_INTERESTED = "is_interested";
        public static final String IS_MARRIED = "isMarried";
        public static final String CHILD_EDU = "child_edu";
        public static final String CHILD_WORK = "child_work";
        public static final String FragmentSp = "fragment";
        public static final String CHILD_IMAGE = "child_image";
        public static final String IMG_SPOUSE = "img_spouse";
        public static final String IMG_SMOTHER = "img_smother";
        public static final String IMG_SFATHER = "img_sfather";
        public static final String PROFILE_ID = "profile_id";
        public static final String REMINDER_DATE = "reminder_date";
        public static final String REMINDER_TYPE = "reminder_type";
        public static final String REMINDER_ID = "reminder_id";
        public static final String TBTN_SHARE = "tbtn_share";
        public static final String TBTN_SYNC = "tbtn_sync";
        public static final String IMG_FATHER_URL = "img_father_url";
        public static final String IMG_MOTHER_URL = "img_mother_url";
        public static final String IMG_SPOUSE_URL = "img_spouse_url";
        public static final String IMG_SFATHER_URL = "img_sfather_url";
        public static final String IMG_SMOTHER_URL = "img_smother_url";
        public static final String CHILD_IMAGE_URL = "child_image_url";
        private static final String BASE_URL = "http://www.superbinstruments.com/directory-dev";
        public static final String LOGIN_URL = BASE_URL + "/API/login";
        public static final String SIGNUP_URL = BASE_URL + "/API/register";
        public static final String FORGOT_PASSWORD_URL = BASE_URL + "/API/forgotPassword";
        public static final String ADVANCE_SEARCH_URL = BASE_URL + "/API/searchUsers";
        public static final String GLOBAL_SEARCH_URL = BASE_URL + "/API/globalSearch";
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
        public static final String GET_GOTRA_URL = BASE_URL + "/API/getGotra";
        public static final String SHARED_USERS_URL = BASE_URL + "/API/shareUsers";
        public static final String NEAR_BY_USERS_URL = BASE_URL + "/API/getNearByUsers";
        public static final String SEND_REQUEST_URL = BASE_URL + "/API/sendRequest";
        public static final String REQUEST_ACTION_URL = BASE_URL + "/API/requestAction";
        public static final String GET_RELATIONS_URL = BASE_URL + "/API/getRelations";
        public static final String SET_REMINDER_URL = BASE_URL + "/API/setReminder";
        public static final String SET_TREE_URL = BASE_URL + "/API/saveTree";
        public static final String GET_USERS_BY_DATE_URL = BASE_URL + "/API/getUsersByDate";
        public static String DEVICE_ID_VALUE = "";
    }
}
