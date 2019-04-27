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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.util.Base64;
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
import com.krs.vastipatrak.model.ListFamilyTreeData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.model.MatrimonyProfileData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class Utility {

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

    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isValidDate(String bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd);
        Date strDate = null;
        try {
            if (!bdate.isEmpty()) {
                strDate = sdf.parse(bdate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return !new Date().before(strDate);
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

    public static boolean hasCAMARA(@NonNull Context mContext) {
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
        return (Utility.hasPermission(mContext, Manifest.permission.READ_CONTACTS));
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



    /*public static class BitmapUtilsTask extends AsyncTask<Object, Void, Bitmap> {

        Context context;
        File f;
        public BitmapUtilsTask(Context context,File f) {
            this.context = context;
            this.f=f;
        }

        *//**
         * Loads a bitmap from the specified url.
         *
         * @param url The location of the bitmap asset
         * @return The bitmap, or null if it could not be loaded
         * @throws IOException
         *//*
        public Bitmap getBitmap() throws IOException {

            // Get the source image's dimensions
            int desiredWidth = 1000;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            //BitmapFactory.decodeResource(context.getResources(), R.drawable.green_background , options);
            BitmapFactory.decodeStream(new FileInputStream(f),null,options);
            int srcWidth = options.outWidth;
            int srcHeight = options.outHeight;

            // Only scale if the source is big enough. This code is just trying
            // to fit a image into a certain width.
            if (desiredWidth > srcWidth)
                desiredWidth = srcWidth;

            // Calculate the correct inSampleSize/scale value. This helps reduce
            // memory use. It should be a power of 2
            int inSampleSize = 1;
            while (srcWidth / 2 > desiredWidth) {
                srcWidth /= 2;
                srcHeight /= 2;
                inSampleSize *= 2;
            }
            // Decode with inSampleSize
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inSampleSize = inSampleSize;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inPurgeable = true;
            Bitmap sampledSrcBitmap;

            sampledSrcBitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.green_background , options);

            return sampledSrcBitmap;
        }

        *//**
         * The system calls this to perform work in a worker thread and delivers
         * it the parameters given to AsyncTask.execute()
         *//*
        @Override
        protected Bitmap doInBackground(Object... item) {
            try {
                return getBitmap();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }*/


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

    public static Bitmap getBitmap(Context context,File f) throws IOException {

        // Get the source image's dimensions
        int desiredWidth = 200;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(new FileInputStream(f),null,options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // Only scale if the source is big enough. This code is just trying
        // to fit a image into a certain width.
        if (desiredWidth > srcWidth)
            desiredWidth = srcWidth;

        // Calculate the correct inSampleSize/scale value. This helps reduce
        // memory use. It should be a power of 2
        int inSampleSize = 1;
        while (srcWidth / 2 > desiredWidth) {
            srcWidth /= 2;
            srcHeight /= 2;
            inSampleSize *= 2;
        }
        // Decode with inSampleSize
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;
        Bitmap sampledSrcBitmap;
        sampledSrcBitmap =BitmapFactory.decodeStream(new FileInputStream(f),null,options);
        return sampledSrcBitmap;
    }

    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=200;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
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
        return PiyushBase64.Base64.encodeBytes(ba);
    }




    public static  String getByteArrayFromImageURL(String url) {

        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return PiyushBase64.Base64.encodeBytes(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return null;
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
                        contains(AppConstants.FIRST_NAME, query).or().
                        contains(AppConstants.LAST_NAME, query).or().
                        contains(AppConstants.FATHER_NAME, query).or().
                        contains(AppConstants.MOTHER_NAME, query).or().
                        contains(AppConstants.EMAIL_ADDRESS, query).or().
                        contains(AppConstants.MOBILE, query).or().
                        contains(AppConstants.PHONE, query).or().
                        contains(AppConstants.BLOOD_GROUP, query).or().
                        contains(AppConstants.GENDER, query).or().
                        contains(AppConstants.CITY, query).or().
                        contains(AppConstants.GOTRA, query).or().
                        contains(AppConstants.EKDO, query).or().
                        contains(AppConstants.NATIVE_PLACE, query).or().
                        contains(AppConstants.BIRTH_PLACE, query).or().
                        contains(AppConstants.BIRTH_DATE, query).or().
                        contains(AppConstants.BIRTH_TIME, query).or().
                        contains(AppConstants.EDUCATION, query).or().
                        contains(AppConstants.OCCUPATION, query).or().
                        contains(AppConstants.WORK, query).or().
                        contains(AppConstants.ADDRESS, query).or().
                        contains(AppConstants.OFFICE_MOBILE, query).or().
                        contains(AppConstants.OFFICE_ADDRESS, query).or().
                        contains(AppConstants.SPOUSE_NAME, query).or().
                        contains(AppConstants.MARRIAGE_DATE, query).or().
                        contains(AppConstants.SPOUSE_FATHER_NAME, query).or().
                        contains(AppConstants.SPOUSE_MOTHER_NAME, query).or().
                        contains(AppConstants.STATUS, query).findAll();
                profileData.sort(AppConstants.CITY, Sort.ASCENDING);
                mlistProfileData.addAll(profileData);
                break;
            }
            case 3: {
                ListProfileData profileData = realm.where(ListProfileData.class).equalTo(AppConstants.PROFILE_ID, query).findFirst();
                mlistProfileData.add(profileData);
                break;
            }
            default:
                String first_name, last_name, father_name, mother_name, email_address, mobile, phone, blood_group, gender, gotra, ekdo, birth_place, native_place, birth_date, city, birth_time, education, occupation, work, address, office_mobile, office_address, spouse_name, marriage_date, spouse_father_name, spouse_mother_name;
                try {

                    JSONObject mJsonObject = new JSONObject(query);
                    if (mJsonObject.has(AppConstants.FIRST_NAME)) {
                        first_name = mJsonObject.getString(AppConstants.FIRST_NAME);
                        RealmResults<ListProfileData> first_name_data = realm.where(ListProfileData.class).contains(AppConstants.FIRST_NAME, first_name.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(first_name_data);

                    }
                    if (mJsonObject.has(AppConstants.LAST_NAME)) {
                        last_name = mJsonObject.getString(AppConstants.LAST_NAME);
                        RealmResults<ListProfileData> last_name_data = realm.where(ListProfileData.class).contains(AppConstants.LAST_NAME, last_name.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(last_name_data);
                    }
                    if (mJsonObject.has(AppConstants.FATHER_NAME)) {
                        father_name = mJsonObject.getString(AppConstants.FATHER_NAME);
                        RealmResults<ListProfileData> father_name_data = realm.where(ListProfileData.class).contains(AppConstants.FATHER_NAME, father_name.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(father_name_data);

                    }
                    if (mJsonObject.has(AppConstants.MOTHER_NAME)) {
                        mother_name = mJsonObject.getString(AppConstants.MOTHER_NAME);
                        RealmResults<ListProfileData> mother_name_data = realm.where(ListProfileData.class).contains(AppConstants.MOTHER_NAME, mother_name.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(mother_name_data);
                    }
                    if (mJsonObject.has(AppConstants.EMAIL_ADDRESS)) {
                        email_address = mJsonObject.getString(AppConstants.EMAIL_ADDRESS);
                        RealmResults<ListProfileData> email_data = realm.where(ListProfileData.class).contains(AppConstants.EMAIL_ADDRESS, email_address.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(email_data);
                    }

                    if (mJsonObject.has(AppConstants.MOBILE)) {
                        mobile = mJsonObject.getString(AppConstants.MOBILE);
                        RealmResults<ListProfileData> mobile_data = realm.where(ListProfileData.class).contains(AppConstants.MOBILE, mobile.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(mobile_data);
                    }
                    if (mJsonObject.has(AppConstants.PHONE)) {
                        phone = mJsonObject.getString(AppConstants.PHONE);
                        RealmResults<ListProfileData> phone_data = realm.where(ListProfileData.class).contains(AppConstants.PHONE, phone.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(phone_data);
                    }
                    if (mJsonObject.has(AppConstants.BLOOD_GROUP)) {
                        blood_group = mJsonObject.getString(AppConstants.BLOOD_GROUP);
                        RealmResults<ListProfileData> blood_group_data = realm.where(ListProfileData.class).contains(AppConstants.BLOOD_GROUP, blood_group.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(blood_group_data);
                    }
                    if (mJsonObject.has(AppConstants.GENDER)) {
                        gender = mJsonObject.getString(AppConstants.GENDER);
                        RealmResults<ListProfileData> gender_data = realm.where(ListProfileData.class).contains(AppConstants.GENDER, gender.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(gender_data);

                    }
                    if (mJsonObject.has(AppConstants.GOTRA)) {
                        gotra = mJsonObject.getString(AppConstants.GOTRA);
                        RealmResults<ListProfileData> gotra_data = realm.where(ListProfileData.class).contains(AppConstants.GOTRA, gotra.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(gotra_data);

                    }

                    if (mJsonObject.has(AppConstants.EKDO)) {
                        ekdo = mJsonObject.getString(AppConstants.EKDO);
                        RealmResults<ListProfileData> ekdo_data = realm.where(ListProfileData.class).contains(AppConstants.EKDO, ekdo.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(ekdo_data);
                    }
                    if (mJsonObject.has(AppConstants.NATIVE_PLACE)) {
                        native_place = mJsonObject.getString(AppConstants.NATIVE_PLACE);
                        RealmResults<ListProfileData> native_place_data = realm.where(ListProfileData.class).contains(AppConstants.NATIVE_PLACE, native_place.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(native_place_data);
                    }

                    if (mJsonObject.has(AppConstants.CITY)) {
                        city = mJsonObject.getString(AppConstants.CITY);
                        RealmResults<ListProfileData> city_data = realm.where(ListProfileData.class).contains(AppConstants.NATIVE_PLACE, city.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(city_data);
                    }

                    if (mJsonObject.has(AppConstants.BIRTH_PLACE)) {
                        birth_place = mJsonObject.getString(AppConstants.BIRTH_PLACE);
                        RealmResults<ListProfileData> birth_place_data = realm.where(ListProfileData.class).contains(AppConstants.BIRTH_PLACE, birth_place.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(birth_place_data);
                    }

                    if (mJsonObject.has(AppConstants.BIRTH_DATE)) {
                        birth_date = mJsonObject.getString(AppConstants.BIRTH_DATE);
                        RealmResults<ListProfileData> birth_date_data = realm.where(ListProfileData.class).contains(AppConstants.BIRTH_DATE, birth_date.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(birth_date_data);

                    }
                    if (mJsonObject.has(AppConstants.BIRTH_TIME)) {
                        birth_time = mJsonObject.getString(AppConstants.BIRTH_TIME);
                        RealmResults<ListProfileData> birth_time_data = realm.where(ListProfileData.class).contains(AppConstants.BIRTH_TIME, birth_time.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(birth_time_data);
                    }
                    if (mJsonObject.has(AppConstants.EDUCATION)) {
                        education = mJsonObject.getString(AppConstants.EDUCATION);
                        RealmResults<ListProfileData> education_data = realm.where(ListProfileData.class).contains(AppConstants.EDUCATION, education.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(education_data);

                    }
                    if (mJsonObject.has(AppConstants.OCCUPATION)) {
                        occupation = mJsonObject.getString(AppConstants.OCCUPATION);
                        RealmResults<ListProfileData> occupation_data = realm.where(ListProfileData.class).contains(AppConstants.OCCUPATION, occupation.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(occupation_data);

                    }
                    if (mJsonObject.has(AppConstants.WORK)) {
                        work = mJsonObject.getString(AppConstants.WORK);
                        RealmResults<ListProfileData> work_data = realm.where(ListProfileData.class).contains(AppConstants.WORK, work.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(work_data);
                    }

                    if (mJsonObject.has(AppConstants.ADDRESS)) {
                        address = mJsonObject.getString(AppConstants.ADDRESS);
                        RealmResults<ListProfileData> address_data = realm.where(ListProfileData.class).contains(AppConstants.ADDRESS, address.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();

                        mlistProfileData.addAll(address_data);
                    }
                    if (mJsonObject.has(AppConstants.OFFICE_MOBILE)) {
                        office_mobile = mJsonObject.getString(AppConstants.OFFICE_MOBILE);
                        RealmResults<ListProfileData> office_mobile_data = realm.where(ListProfileData.class).contains(AppConstants.OFFICE_MOBILE, office_mobile.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(office_mobile_data);
                    }
                    if (mJsonObject.has(AppConstants.OFFICE_ADDRESS)) {
                        office_address = mJsonObject.getString(AppConstants.OFFICE_ADDRESS);
                        RealmResults<ListProfileData> office_address_data = realm.where(ListProfileData.class).contains(AppConstants.OFFICE_ADDRESS, office_address.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(office_address_data);
                    }
                    if (mJsonObject.has(AppConstants.SPOUSE_NAME)) {
                        spouse_name = mJsonObject.getString(AppConstants.SPOUSE_NAME);
                        RealmResults<ListProfileData> spouse_name_data = realm.where(ListProfileData.class).contains(AppConstants.SPOUSE_NAME, spouse_name.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(spouse_name_data);
                    }
                    if (mJsonObject.has(AppConstants.MARRIAGE_DATE)) {
                        marriage_date = mJsonObject.getString(AppConstants.MARRIAGE_DATE);
                        RealmResults<ListProfileData> marriage_date_data = realm.where(ListProfileData.class).contains(AppConstants.MARRIAGE_DATE, marriage_date.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(marriage_date_data);
                    }
                    if (mJsonObject.has(AppConstants.SPOUSE_FATHER_NAME)) {
                        spouse_father_name = mJsonObject.getString(AppConstants.SPOUSE_FATHER_NAME);
                        RealmResults<ListProfileData> spouse_father_name_data = realm.where(ListProfileData.class).contains(AppConstants.SPOUSE_FATHER_NAME, spouse_father_name.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
                        mlistProfileData.addAll(spouse_father_name_data);
                    }
                    if (mJsonObject.has(AppConstants.SPOUSE_MOTHER_NAME)) {
                        spouse_mother_name = mJsonObject.getString(AppConstants.SPOUSE_MOTHER_NAME);
                        RealmResults<ListProfileData> spouse_mother_name_data = realm.where(ListProfileData.class).contains(AppConstants.SPOUSE_MOTHER_NAME, spouse_mother_name.toLowerCase()).equalTo(AppConstants.STATUS, "1").findAll();
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
            RealmResults<ListChildrenData> childrenData = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_NAME, query).or().contains(AppConstants.CHILD_EDU, query).or().contains(AppConstants.CHILD_WORK, query).or().contains(AppConstants.CHILD_BDAY, query).contains(AppConstants.CHILD_BPLACE, query).contains(AppConstants.CHILD_BTIME, query).findAll();
            mListChildrenData.addAll(childrenData);
        } else {
            try {
                JSONObject mJsonObject = new JSONObject(query);
                if (mJsonObject.has(AppConstants.CHILD_BDAY)) {
                    String child_bday = mJsonObject.getString(AppConstants.CHILD_BDAY);
                    RealmResults<ListChildrenData> child_bday_data = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_BDAY, child_bday.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_bday_data);
                }

                if (mJsonObject.has(AppConstants.CHILD_EDU)) {
                    String child_edu = mJsonObject.getString(AppConstants.CHILD_EDU);
                    RealmResults<ListChildrenData> child_edu_data = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_EDU, child_edu.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_edu_data);
                }
                if (mJsonObject.has(AppConstants.CHILD_WORK)) {
                    String child_work = mJsonObject.getString(AppConstants.CHILD_WORK);
                    RealmResults<ListChildrenData> child_work_data = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_WORK, child_work.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_work_data);
                }
                if (mJsonObject.has(AppConstants.CHILD_NAME)) {
                    String child_name = mJsonObject.getString(AppConstants.CHILD_NAME);
                    RealmResults<ListChildrenData> child_name_data = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_NAME, child_name.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_name_data);
                }

                if (mJsonObject.has(AppConstants.CHILD_BPLACE)) {
                    String child_bplace = mJsonObject.getString(AppConstants.CHILD_BPLACE);
                    RealmResults<ListChildrenData> child_bplace_data = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_BPLACE, child_bplace.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_bplace_data);
                }

                if (mJsonObject.has(AppConstants.CHILD_BTIME)) {
                    String child_btime = mJsonObject.getString(AppConstants.CHILD_BTIME);
                    RealmResults<ListChildrenData> child_btime_data = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_BTIME, child_btime.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_btime_data);
                }

                if (mJsonObject.has(AppConstants.CHILD_GENDER)) {
                    String child_gender = mJsonObject.getString(AppConstants.CHILD_GENDER);
                    RealmResults<ListChildrenData> child_gender_data = realm.where(ListChildrenData.class).contains(AppConstants.CHILD_GENDER, child_gender.toLowerCase()).findAll();
                    mListChildrenData.addAll(child_gender_data);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < mListChildrenData.size(); i++) {
            ListProfileData profile_data = realm.where(ListProfileData.class).equalTo(AppConstants.PROFILE_ID, Objects.requireNonNull(mListChildrenData.get(i)).getProfile_id()).findFirst();
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

   /* public static void alert(@NonNull Activity mActivity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(mActivity.getString(R.string.app_name));
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).show();
    }*/

    public static void UpdateProfilePassword(String password, String id) {

        ListProfileData mListProfile = AppController.getInstance().realm.where(ListProfileData.class).equalTo(AppConstants.PROFILE_ID, id).findFirst();
        AppController.getInstance().realm.beginTransaction();
        Objects.requireNonNull(mListProfile).setPassword(password);
        AppController.getInstance().realm.commitTransaction();

    }

    public static void UpdateProfileStatus(@NonNull ArrayList<String> lstSelectedIDs, String status) {
        try {
            Realm realm = AppController.getInstance().realm;
            for (int i = 0; i < lstSelectedIDs.size(); i++) {
                ListProfileData mListProfile = realm.where(ListProfileData.class).equalTo(AppConstants.PROFILE_ID, lstSelectedIDs.get(i)).findFirst();
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
                RealmResults<ListProfileData> results = realm.where(ListProfileData.class).equalTo(AppConstants.PROFILE_ID, lstSelectedIDs.get(i)).findAll();
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
            if (mJsonObject.has(AppConstants.ID)) {
                mListProfileData.setProfile_id(mJsonObject.getString(AppConstants.ID));
            }

            if (mJsonObject.has(AppConstants.BDATE_REMINDER_ID)) {
                mListProfileData.setBdate_reminder_id(mJsonObject.getString(AppConstants.BDATE_REMINDER_ID));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_BDATE_REMINDER_ID)) {
                mListProfileData.setSpouse_bdate_reminder_id(mJsonObject.getString(AppConstants.SPOUSE_BDATE_REMINDER_ID));
            }
            if (mJsonObject.has(AppConstants.MDATE_REMINDER_ID)) {
                mListProfileData.setMdate_reminder_id(mJsonObject.getString(AppConstants.MDATE_REMINDER_ID));
            }

            if (mJsonObject.has(AppConstants.FIRST_NAME)) {
                mListProfileData.setFirst_name(mJsonObject.getString(AppConstants.FIRST_NAME));
            }
            if (mJsonObject.has(AppConstants.LAST_NAME)) {
                mListProfileData.setLast_name(mJsonObject.getString(AppConstants.LAST_NAME));
            }
            if (mJsonObject.has(AppConstants.CITY)) {
                mListProfileData.setCity(mJsonObject.getString(AppConstants.CITY));
            }
            if (mJsonObject.has(AppConstants.IS_SHARE)) {
                mListProfileData.setIs_block(mJsonObject.getString(AppConstants.IS_SHARE));
            }
            if (mJsonObject.has(AppConstants.IS_LOCATION_ENABLE)) {
                mListProfileData.setIs_location_enable(mJsonObject.getString(AppConstants.IS_LOCATION_ENABLE));
            }

            if (mJsonObject.has(AppConstants.FATHER_NAME)) {
                mListProfileData.setFather_name(mJsonObject.getString(AppConstants.FATHER_NAME));
            }
            if (mJsonObject.has(AppConstants.MOTHER_NAME)) {
                mListProfileData.setMother_name(mJsonObject.getString(AppConstants.MOTHER_NAME));
            }
            if (mJsonObject.has(AppConstants.BIRTH_DATE)) {
                mListProfileData.setBirth_date(ChangedateFormat(mJsonObject.getString(AppConstants.BIRTH_DATE)));
            }
            if (mJsonObject.has(AppConstants.BIRTH_TIME)) {
                mListProfileData.setBirth_time(mJsonObject.getString(AppConstants.BIRTH_TIME));
            }
            if (mJsonObject.has(AppConstants.BIRTH_PLACE)) {
                mListProfileData.setBirth_place(mJsonObject.getString(AppConstants.BIRTH_PLACE));
            }
            if (mJsonObject.has(AppConstants.MOBILE)) {
                mListProfileData.setMobile(mJsonObject.getString(AppConstants.MOBILE));
            }
            if (mJsonObject.has(AppConstants.PHONE)) {
                mListProfileData.setPhone(mJsonObject.getString(AppConstants.PHONE));
            }
            if (mJsonObject.has(AppConstants.BLOOD_GROUP)) {
                mListProfileData.setBlood_group(mJsonObject.getString(AppConstants.BLOOD_GROUP));
            }
            if (mJsonObject.has(AppConstants.GENDER)) {
                mListProfileData.setGender(mJsonObject.getString(AppConstants.GENDER));
            }
            if (mJsonObject.has(AppConstants.GOTRA)) {
                mListProfileData.setGotra(mJsonObject.getString(AppConstants.GOTRA));
            }
            if (mJsonObject.has(AppConstants.EMAIL_ADDRESS)) {
                mListProfileData.setEmail_address(mJsonObject.getString(AppConstants.EMAIL_ADDRESS));
            }
            if (mJsonObject.has(AppConstants.ADDRESS)) {
                mListProfileData.setAddress(mJsonObject.getString(AppConstants.ADDRESS));
            }
            if (mJsonObject.has(AppConstants.NATIVE_PLACE)) {
                mListProfileData.setNative_place(mJsonObject.getString(AppConstants.NATIVE_PLACE));
            }
            if (mJsonObject.has(AppConstants.EDUCATION)) {
                mListProfileData.setEducation(mJsonObject.getString(AppConstants.EDUCATION));
            }
            if (mJsonObject.has(AppConstants.OCCUPATION)) {
                mListProfileData.setOccupation(mJsonObject.getString(AppConstants.OCCUPATION));
            }
            if (mJsonObject.has(AppConstants.OFFICE_MOBILE)) {
                mListProfileData.setOffice_mobile(mJsonObject.getString(AppConstants.OFFICE_MOBILE));
            }
            if (mJsonObject.has(AppConstants.WORK)) {
                mListProfileData.setWork(mJsonObject.getString(AppConstants.WORK));
            }
            if (mJsonObject.has(AppConstants.OFFICE_ADDRESS)) {
                mListProfileData.setOffice_address(mJsonObject.getString(AppConstants.OFFICE_ADDRESS));
            }
            if (mJsonObject.has(AppConstants.MARRIAGE_DATE)) {
                mListProfileData.setMarriage_date(ChangedateFormat(mJsonObject.getString(AppConstants.MARRIAGE_DATE)));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_NAME)) {
                mListProfileData.setSpouse_name(mJsonObject.getString(AppConstants.SPOUSE_NAME));
            }

            if (mJsonObject.has(AppConstants.SPOUSE_MOBILE)) {
                mListProfileData.setSponse_mobile(mJsonObject.getString(AppConstants.SPOUSE_MOBILE));
            }

            if (mJsonObject.has(AppConstants.SPOUSE_NATIVE)) {
                mListProfileData.setSponse_native(mJsonObject.getString(AppConstants.SPOUSE_NATIVE));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_EDU)) {
                mListProfileData.setSpouse_education(mJsonObject.getString(AppConstants.SPOUSE_EDU));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_BG)) {
                mListProfileData.setSponse_bg(mJsonObject.getString(AppConstants.SPOUSE_BG));
            }

            if (mJsonObject.has(AppConstants.SPOUSE_BDATE)) {
                mListProfileData.setSponse_bdate(ChangedateFormat(mJsonObject.getString(AppConstants.SPOUSE_BDATE)));
            }

            if (mJsonObject.has(AppConstants.SPOUSE_FATHER_NAME)) {
                mListProfileData.setSfather_name(mJsonObject.getString(AppConstants.SPOUSE_FATHER_NAME));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_MOTHER_NAME)) {
                mListProfileData.setSmother_name(mJsonObject.getString(AppConstants.SPOUSE_MOTHER_NAME));
            }
            if (mJsonObject.has(AppConstants.OFFICE_LAT)) {
                mListProfileData.setOffice_lat(mJsonObject.getString(AppConstants.OFFICE_LAT));
            }
            if (mJsonObject.has(AppConstants.OFFICE_LNG)) {
                mListProfileData.setOffice_lng(mJsonObject.getString(AppConstants.OFFICE_LNG));
            }
            if (mJsonObject.has(AppConstants.HOME_LAT)) {
                mListProfileData.setHome_lat(mJsonObject.getString(AppConstants.HOME_LAT));
            }
            if (mJsonObject.has(AppConstants.HOME_LNG)) {
                mListProfileData.setHome_lng(mJsonObject.getString(AppConstants.HOME_LNG));
            }
            if (mJsonObject.has(AppConstants.USER_LAT)) {
                mListProfileData.setUser_lat(mJsonObject.getString(AppConstants.USER_LAT));
            }
            if (mJsonObject.has(AppConstants.USER_LNG)) {
                mListProfileData.setUser_lng(mJsonObject.getString(AppConstants.USER_LNG));
            }

            if (mJsonObject.has(AppConstants.PROFILE_PIC_URL)) {
                mListProfileData.setProfile_pic_url(mJsonObject.getString(AppConstants.PROFILE_PIC_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_FATHER_URL)) {
                mListProfileData.setImg_father_url(mJsonObject.getString(AppConstants.IMG_FATHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_MOTHER_URL)) {
                mListProfileData.setImg_mother_url(mJsonObject.getString(AppConstants.IMG_MOTHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SPOUSE_URL)) {
                mListProfileData.setImg_spouse_url(mJsonObject.getString(AppConstants.IMG_SPOUSE_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SFATHER_URL)) {
                mListProfileData.setImg_sfather_url(mJsonObject.getString(AppConstants.IMG_SFATHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SMOTHER_URL)) {
                mListProfileData.setImg_smother_url(mJsonObject.getString(AppConstants.IMG_SMOTHER_URL));
            }

            if (mJsonObject.has(AppConstants.UPDATED_TIME)) {
                mListProfileData.setUpdated_time(mJsonObject.getString(AppConstants.UPDATED_TIME));
            }
            if (mJsonObject.has(AppConstants.SYNC_TIME)) {
                mListProfileData.setSync_time(mJsonObject.getString(AppConstants.SYNC_TIME));
            }

            if (mJsonObject.has(AppConstants.CHILDS)) {

                JSONArray mJsonArray = new JSONArray(mJsonObject.getString(AppConstants.CHILDS));
                RealmList<ListChildrenData> mlistchilds = new RealmList<>();

                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsonObj = mJsonArray.getJSONObject(i);
                    ListChildrenData mListChildrendata = new ListChildrenData();

                    if (mJsonObj.has(AppConstants.CHILD_ID)) {
                        mListChildrendata.setChild_id(mJsonObj.getString(AppConstants.CHILD_ID));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_IMAGE_URL)) {
                        mListChildrendata.setChild_img_url(mJsonObj.getString(AppConstants.CHILD_IMAGE_URL));
                    }

                    if (mJsonObj.has(AppConstants.CHILD_NAME)) {
                        mListChildrendata.setChild_name(mJsonObj.getString(AppConstants.CHILD_NAME));
                    }

                    if (mJsonObj.has(AppConstants.MOBILE)) {
                        mListChildrendata.setMobile(mJsonObj.getString(AppConstants.MOBILE));
                    }
                    if (mJsonObj.has(AppConstants.BLOOD_GROUP)) {
                        mListChildrendata.setBlood_group(mJsonObj.getString(AppConstants.BLOOD_GROUP));
                    }

                    if (mJsonObj.has(AppConstants.CHILD_BDATE_REMINDER_ID)) {
                        mListChildrendata.setChild_bdate_reminder_id(mJsonObj.getString(AppConstants.CHILD_BDATE_REMINDER_ID));
                    }

                    if (mJsonObj.has(AppConstants.GENDER)) {
                        mListChildrendata.setGender(mJsonObj.getString(AppConstants.GENDER));
                    }
                    if (mJsonObj.has(AppConstants.BIRTH_PLACE)) {
                        mListChildrendata.setBirth_place(mJsonObj.getString(AppConstants.BIRTH_PLACE));
                    }
                    if (mJsonObj.has(AppConstants.BIRTH_TIME)) {
                        mListChildrendata.setBirth_time(mJsonObj.getString(AppConstants.BIRTH_TIME));
                    }
                    if (mJsonObj.has(AppConstants.IS_INTERESTED)) {
                        String isInterest = mJsonObj.getString(AppConstants.IS_INTERESTED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setInterest(true);
                        } else {
                            mListChildrendata.setInterest(false);
                        }
                    }

                    if (mJsonObj.has(AppConstants.IS_MARRIED)) {
                        String isInterest = mJsonObj.getString(AppConstants.IS_MARRIED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setIs_married(true);
                        } else {
                            mListChildrendata.setIs_married(false);
                        }
                    }

                    if (mJsonObj.has(AppConstants.CHILD_BDAY)) {
                        mListChildrendata.setChild_bday(ChangedateFormat(mJsonObj.getString(AppConstants.CHILD_BDAY)));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_EDU)) {
                        mListChildrendata.setChild_edu(mJsonObj.getString(AppConstants.CHILD_EDU));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_WORK)) {
                        mListChildrendata.setChild_work(mJsonObj.getString(AppConstants.CHILD_WORK));
                    }
                    mListChildrendata.setProfile_id(mJsonObject.getString(AppConstants.ID));
                    mlistchilds.add(mListChildrendata);
                }
                mListProfileData.setmListChildrenData(mlistchilds);
            }

            if (mJsonObject.has(AppConstants.familyTree)) {
                JSONArray mjsonarray = mJsonObject.getJSONArray(AppConstants.familyTree);

                RealmList<ListFamilyTreeData> listFamilyTreeDataRealmList = new RealmList<>();
                for (int i = 0; i < mjsonarray.length(); i++) {
                    ListFamilyTreeData listFamilyTreeData = new ListFamilyTreeData();
                    JSONObject object = mjsonarray.getJSONObject(i);
                    String id = object.getString("id");
                    String profile_id = object.getString("profile_id");
                    String pic = object.getString("profile_pic");
                    String name = object.getString("name");
                    String level = object.getString("level");
                    listFamilyTreeData.setId(id);
                    listFamilyTreeData.setProfile_id(profile_id);
                    listFamilyTreeData.setProfile_pic(pic);
                    listFamilyTreeData.setName(name);
                    listFamilyTreeData.setLevel(level);
                    listFamilyTreeDataRealmList.add(listFamilyTreeData);
                }
                mListProfileData.setmListFamilyTreeData(listFamilyTreeDataRealmList);
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
            if (mJsonObject.has(AppConstants.ID)) {
                mListProfileData.setProfile_id(mJsonObject.getString(AppConstants.ID));
            }
            if (mJsonObject.has(AppConstants.FIRST_NAME)) {
                mListProfileData.setFirst_name(mJsonObject.getString(AppConstants.FIRST_NAME));
            }
            if (mJsonObject.has(AppConstants.LAST_NAME)) {
                mListProfileData.setLast_name(mJsonObject.getString(AppConstants.LAST_NAME));
            }
            if (mJsonObject.has(AppConstants.CITY)) {
                mListProfileData.setCity(mJsonObject.getString(AppConstants.CITY));
            }

            if (mJsonObject.has(AppConstants.FATHER_NAME)) {
                mListProfileData.setFather_name(mJsonObject.getString(AppConstants.FATHER_NAME));
            }
            if (mJsonObject.has(AppConstants.MOTHER_NAME)) {
                mListProfileData.setMother_name(mJsonObject.getString(AppConstants.MOTHER_NAME));
            }
            if (mJsonObject.has(AppConstants.BIRTH_DATE)) {
                mListProfileData.setBirth_date(ChangedateFormat(mJsonObject.getString(AppConstants.BIRTH_DATE)));
            }
            if (mJsonObject.has(AppConstants.BIRTH_TIME)) {
                mListProfileData.setBirth_time(mJsonObject.getString(AppConstants.BIRTH_TIME));
            }
            if (mJsonObject.has(AppConstants.BIRTH_PLACE)) {
                mListProfileData.setBirth_place(mJsonObject.getString(AppConstants.BIRTH_PLACE));
            }
            if (mJsonObject.has(AppConstants.MOBILE)) {
                mListProfileData.setMobile(mJsonObject.getString(AppConstants.MOBILE));
            }
            if (mJsonObject.has(AppConstants.PHONE)) {
                mListProfileData.setPhone(mJsonObject.getString(AppConstants.PHONE));
            }
            if (mJsonObject.has(AppConstants.BLOOD_GROUP)) {
                mListProfileData.setBlood_group(mJsonObject.getString(AppConstants.BLOOD_GROUP));
            }
            if (mJsonObject.has(AppConstants.GENDER)) {
                mListProfileData.setGender(mJsonObject.getString(AppConstants.GENDER));
            }
            if (mJsonObject.has(AppConstants.GOTRA)) {
                mListProfileData.setGotra(mJsonObject.getString(AppConstants.GOTRA));
            }
            if (mJsonObject.has(AppConstants.EMAIL_ADDRESS)) {
                mListProfileData.setEmail_address(mJsonObject.getString(AppConstants.EMAIL_ADDRESS));
            }
            if (mJsonObject.has(AppConstants.ADDRESS)) {
                mListProfileData.setAddress(mJsonObject.getString(AppConstants.ADDRESS));
            }
            if (mJsonObject.has(AppConstants.NATIVE_PLACE)) {
                mListProfileData.setNative_place(mJsonObject.getString(AppConstants.NATIVE_PLACE));
            }
            if (mJsonObject.has(AppConstants.EDUCATION)) {
                mListProfileData.setEducation(mJsonObject.getString(AppConstants.EDUCATION));
            }
            if (mJsonObject.has(AppConstants.OCCUPATION)) {
                mListProfileData.setOccupation(mJsonObject.getString(AppConstants.OCCUPATION));
            }
            if (mJsonObject.has(AppConstants.OFFICE_MOBILE)) {
                mListProfileData.setOffice_mobile(mJsonObject.getString(AppConstants.OFFICE_MOBILE));
            }
            if (mJsonObject.has(AppConstants.WORK)) {
                mListProfileData.setWork(mJsonObject.getString(AppConstants.WORK));
            }
            if (mJsonObject.has(AppConstants.OFFICE_ADDRESS)) {
                mListProfileData.setOffice_address(mJsonObject.getString(AppConstants.OCCUPATION));
            }
            if (mJsonObject.has(AppConstants.MARRIAGE_DATE)) {
                mListProfileData.setMarriage_date(ChangedateFormat(mJsonObject.getString(AppConstants.MARRIAGE_DATE)));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_NAME)) {
                mListProfileData.setSpouse_name(mJsonObject.getString(AppConstants.SPOUSE_NAME));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_FATHER_NAME)) {
                mListProfileData.setSfather_name(mJsonObject.getString(AppConstants.SPOUSE_FATHER_NAME));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_MOTHER_NAME)) {
                mListProfileData.setSmother_name(mJsonObject.getString(AppConstants.SPOUSE_MOTHER_NAME));
            }
            if (mJsonObject.has(AppConstants.OFFICE_LAT)) {
                mListProfileData.setOffice_lat(mJsonObject.getString(AppConstants.OFFICE_LAT));
            }
            if (mJsonObject.has(AppConstants.OFFICE_LNG)) {
                mListProfileData.setOffice_lng(mJsonObject.getString(AppConstants.OFFICE_LNG));
            }
            if (mJsonObject.has(AppConstants.HOME_LAT)) {
                mListProfileData.setHome_lat(mJsonObject.getString(AppConstants.HOME_LAT));
            }
            if (mJsonObject.has(AppConstants.HOME_LNG)) {
                mListProfileData.setHome_lng(mJsonObject.getString(AppConstants.HOME_LNG));
            }
            if (mJsonObject.has(AppConstants.USER_LAT)) {
                mListProfileData.setUser_lat(mJsonObject.getString(AppConstants.USER_LAT));
            }
            if (mJsonObject.has(AppConstants.USER_LNG)) {
                mListProfileData.setUser_lng(mJsonObject.getString(AppConstants.USER_LNG));
            }

            if (mJsonObject.has(AppConstants.PROFILE_PIC_URL)) {
                mListProfileData.setProfile_pic_url(mJsonObject.getString(AppConstants.PROFILE_PIC_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_FATHER_URL)) {
                mListProfileData.setImg_father_url(mJsonObject.getString(AppConstants.IMG_FATHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_MOTHER_URL)) {
                mListProfileData.setImg_mother_url(mJsonObject.getString(AppConstants.IMG_MOTHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SPOUSE_URL)) {
                mListProfileData.setImg_spouse_url(mJsonObject.getString(AppConstants.IMG_SPOUSE_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SFATHER_URL)) {
                mListProfileData.setImg_sfather_url(mJsonObject.getString(AppConstants.IMG_SFATHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SMOTHER_URL)) {
                mListProfileData.setImg_smother_url(mJsonObject.getString(AppConstants.IMG_SMOTHER_URL));
            }

            if (mJsonObject.has(AppConstants.UPDATED_TIME)) {
                mListProfileData.setUpdated_time(mJsonObject.getString(AppConstants.UPDATED_TIME));
            }
            if (mJsonObject.has(AppConstants.SYNC_TIME)) {
                mListProfileData.setSync_time(mJsonObject.getString(AppConstants.SYNC_TIME));
            }


            //   String strWhere = "" + AppConstants.PROFILE_ID + "=" + mJsonObject.getString(AppConstants.PROFILE_ID);
            //   db.update(AppConstants.TABLE_PROFILE, values, strWhere, null);

            if (mJsonObject.has(AppConstants.CHILDS)) {

                JSONArray mJsonArray = new JSONArray(mJsonObject.getString(AppConstants.CHILDS));
                RealmList<ListChildrenData> mlistchilds = new RealmList<>();

                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJsonObj = mJsonArray.getJSONObject(i);
                    ListChildrenData mListChildrendata = new ListChildrenData();

                    if (mJsonObj.has(AppConstants.CHILD_ID)) {
                        mListChildrendata.setChild_id(mJsonObj.getString(AppConstants.CHILD_ID));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_IMAGE_URL)) {
                        mListChildrendata.setChild_img_url(mJsonObj.getString(AppConstants.CHILD_IMAGE_URL));
                    }

                    if (mJsonObj.has(AppConstants.CHILD_NAME)) {
                        mListChildrendata.setChild_name(mJsonObj.getString(AppConstants.CHILD_NAME));
                    }

                    if (mJsonObj.has(AppConstants.MOBILE)) {
                        mListChildrendata.setMobile(mJsonObj.getString(AppConstants.MOBILE));
                    }
                    if (mJsonObj.has(AppConstants.BLOOD_GROUP)) {
                        mListChildrendata.setBlood_group(mJsonObj.getString(AppConstants.BLOOD_GROUP));
                    }

                    if (mJsonObj.has(AppConstants.GENDER)) {
                        mListChildrendata.setGender(mJsonObj.getString(AppConstants.GENDER));
                    }
                    if (mJsonObj.has(AppConstants.BIRTH_PLACE)) {
                        mListChildrendata.setBirth_place(mJsonObj.getString(AppConstants.BIRTH_PLACE));
                    }
                    if (mJsonObj.has(AppConstants.BIRTH_TIME)) {
                        mListChildrendata.setBirth_time(mJsonObj.getString(AppConstants.BIRTH_TIME));
                    }
                    if (mJsonObj.has(AppConstants.IS_INTERESTED)) {
                        String isInterest = mJsonObj.getString(AppConstants.IS_INTERESTED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setInterest(true);
                        } else {
                            mListChildrendata.setInterest(false);
                        }
                    }
                    if (mJsonObj.has(AppConstants.CHILD_BDAY)) {
                        mListChildrendata.setChild_bday(ChangedateFormat(mJsonObj.getString(AppConstants.CHILD_BDAY)));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_EDU)) {
                        mListChildrendata.setChild_edu(mJsonObj.getString(AppConstants.CHILD_EDU));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_WORK)) {
                        mListChildrendata.setChild_work(mJsonObj.getString(AppConstants.CHILD_WORK));
                    }
                    mListChildrendata.setProfile_id(mJsonObject.getString(AppConstants.ID));
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
            if (mJsonObject.has(AppConstants.ID)) {
                mListProfileData.setProfile_id(mJsonObject.getString(AppConstants.ID));
            }
            if (mJsonObject.has(AppConstants.FIRST_NAME)) {
                mListProfileData.setFirst_name(mJsonObject.getString(AppConstants.FIRST_NAME));
            }
            if (mJsonObject.has(AppConstants.LAST_NAME)) {
                mListProfileData.setLast_name(mJsonObject.getString(AppConstants.LAST_NAME));
            }
            if (mJsonObject.has(AppConstants.CITY)) {
                mListProfileData.setCity(mJsonObject.getString(AppConstants.CITY));
            }

            if (mJsonObject.has(AppConstants.FATHER_NAME)) {
                mListProfileData.setFather_name(mJsonObject.getString(AppConstants.FATHER_NAME));
            }
            if (mJsonObject.has(AppConstants.MOTHER_NAME)) {
                mListProfileData.setMother_name(mJsonObject.getString(AppConstants.MOTHER_NAME));
            }
            if (mJsonObject.has(AppConstants.BIRTH_DATE)) {
                mListProfileData.setBirth_date(ChangedateFormat(mJsonObject.getString(AppConstants.BIRTH_DATE)));
            }
            if (mJsonObject.has(AppConstants.BIRTH_TIME)) {
                mListProfileData.setBirth_time(mJsonObject.getString(AppConstants.BIRTH_TIME));
            }
            if (mJsonObject.has(AppConstants.BIRTH_PLACE)) {
                mListProfileData.setBirth_place(mJsonObject.getString(AppConstants.BIRTH_PLACE));
            }
            if (mJsonObject.has(AppConstants.MOBILE)) {
                mListProfileData.setMobile(mJsonObject.getString(AppConstants.MOBILE));
            }
            if (mJsonObject.has(AppConstants.PHONE)) {
                mListProfileData.setPhone(mJsonObject.getString(AppConstants.PHONE));
            }
            if (mJsonObject.has(AppConstants.BLOOD_GROUP)) {
                mListProfileData.setBlood_group(mJsonObject.getString(AppConstants.BLOOD_GROUP));
            }
            if (mJsonObject.has(AppConstants.GENDER)) {
                mListProfileData.setGender(mJsonObject.getString(AppConstants.GENDER));
            }
            if (mJsonObject.has(AppConstants.GOTRA)) {
                mListProfileData.setGotra(mJsonObject.getString(AppConstants.GOTRA));
            }
            if (mJsonObject.has(AppConstants.EMAIL_ADDRESS)) {
                mListProfileData.setEmail_address(mJsonObject.getString(AppConstants.EMAIL_ADDRESS));
            }
            if (mJsonObject.has(AppConstants.ADDRESS)) {
                mListProfileData.setAddress(mJsonObject.getString(AppConstants.ADDRESS));
            }
            if (mJsonObject.has(AppConstants.NATIVE_PLACE)) {
                mListProfileData.setNative_place(mJsonObject.getString(AppConstants.NATIVE_PLACE));
            }
            if (mJsonObject.has(AppConstants.EDUCATION)) {
                mListProfileData.setEducation(mJsonObject.getString(AppConstants.EDUCATION));
            }
            if (mJsonObject.has(AppConstants.OCCUPATION)) {
                mListProfileData.setOccupation(mJsonObject.getString(AppConstants.OCCUPATION));
            }
            if (mJsonObject.has(AppConstants.OFFICE_MOBILE)) {
                mListProfileData.setOffice_mobile(mJsonObject.getString(AppConstants.OFFICE_MOBILE));
            }
            if (mJsonObject.has(AppConstants.WORK)) {
                mListProfileData.setWork(mJsonObject.getString(AppConstants.WORK));
            }
            if (mJsonObject.has(AppConstants.OFFICE_ADDRESS)) {
                mListProfileData.setOffice_address(mJsonObject.getString(AppConstants.OCCUPATION));
            }
            if (mJsonObject.has(AppConstants.MARRIAGE_DATE)) {
                mListProfileData.setMarriage_date(ChangedateFormat(mJsonObject.getString(AppConstants.MARRIAGE_DATE)));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_NAME)) {
                mListProfileData.setSpouse_name(mJsonObject.getString(AppConstants.SPOUSE_NAME));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_FATHER_NAME)) {
                mListProfileData.setSfather_name(mJsonObject.getString(AppConstants.SPOUSE_FATHER_NAME));
            }
            if (mJsonObject.has(AppConstants.SPOUSE_MOTHER_NAME)) {
                mListProfileData.setSmother_name(mJsonObject.getString(AppConstants.SPOUSE_MOTHER_NAME));
            }
            if (mJsonObject.has(AppConstants.OFFICE_LAT)) {
                mListProfileData.setOffice_lat(mJsonObject.getString(AppConstants.OFFICE_LAT));
            }
            if (mJsonObject.has(AppConstants.OFFICE_LNG)) {
                mListProfileData.setOffice_lng(mJsonObject.getString(AppConstants.OFFICE_LNG));
            }
            if (mJsonObject.has(AppConstants.HOME_LAT)) {
                mListProfileData.setHome_lat(mJsonObject.getString(AppConstants.HOME_LAT));
            }
            if (mJsonObject.has(AppConstants.HOME_LNG)) {
                mListProfileData.setHome_lng(mJsonObject.getString(AppConstants.HOME_LNG));
            }
            if (mJsonObject.has(AppConstants.USER_LAT)) {
                mListProfileData.setUser_lat(mJsonObject.getString(AppConstants.USER_LAT));
            }
            if (mJsonObject.has(AppConstants.USER_LNG)) {
                mListProfileData.setUser_lng(mJsonObject.getString(AppConstants.USER_LNG));
            }

            if (mJsonObject.has(AppConstants.PROFILE_PIC_URL)) {
                mListProfileData.setProfile_pic_url(mJsonObject.getString(AppConstants.PROFILE_PIC_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_FATHER_URL)) {
                mListProfileData.setImg_father_url(mJsonObject.getString(AppConstants.IMG_FATHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_MOTHER_URL)) {
                mListProfileData.setImg_mother_url(mJsonObject.getString(AppConstants.IMG_MOTHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SPOUSE_URL)) {
                mListProfileData.setImg_spouse_url(mJsonObject.getString(AppConstants.IMG_SPOUSE_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SFATHER_URL)) {
                mListProfileData.setImg_sfather_url(mJsonObject.getString(AppConstants.IMG_SFATHER_URL));
            }
            if (mJsonObject.has(AppConstants.IMG_SMOTHER_URL)) {
                mListProfileData.setImg_smother_url(mJsonObject.getString(AppConstants.IMG_SMOTHER_URL));
            }

            if (mJsonObject.has(AppConstants.UPDATED_TIME)) {
                mListProfileData.setUpdated_time(mJsonObject.getString(AppConstants.UPDATED_TIME));
            }
            if (mJsonObject.has(AppConstants.SYNC_TIME)) {
                mListProfileData.setSync_time(mJsonObject.getString(AppConstants.SYNC_TIME));
            }


            //   String strWhere = "" + AppConstants.PROFILE_ID + "=" + mJsonObject.getString(AppConstants.PROFILE_ID);
            //   db.update(AppConstants.TABLE_PROFILE, values, strWhere, null);

            if (mJsonObject.has(AppConstants.CHILDS)) {

                JSONArray mJsonArray = new JSONArray(mJsonObject.getString(AppConstants.CHILDS));
                RealmList<ListChildrenData> mlistchilds = new RealmList<>();

                for (int i = 0; i < mJsonArray.length(); i++) {
                    String isInterest = "";
                    String childgender = "";
                    JSONObject mJsonObj = mJsonArray.getJSONObject(i);
                    ListChildrenData mListChildrendata = new ListChildrenData();

                    if (mJsonObj.has(AppConstants.CHILD_ID)) {
                        mListChildrendata.setChild_id(mJsonObj.getString(AppConstants.CHILD_ID));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_IMAGE_URL)) {
                        mListChildrendata.setChild_img_url(mJsonObj.getString(AppConstants.CHILD_IMAGE_URL));
                    }

                    if (mJsonObj.has(AppConstants.CHILD_NAME)) {
                        mListChildrendata.setChild_name(mJsonObj.getString(AppConstants.CHILD_NAME));
                    }

                    if (mJsonObj.has(AppConstants.MOBILE)) {
                        mListChildrendata.setMobile(mJsonObj.getString(AppConstants.MOBILE));
                    }
                    if (mJsonObj.has(AppConstants.BLOOD_GROUP)) {
                        mListChildrendata.setBlood_group(mJsonObj.getString(AppConstants.BLOOD_GROUP));
                    }

                    if (mJsonObj.has(AppConstants.GENDER)) {

                        childgender = mJsonObj.getString(AppConstants.GENDER);
                        mListChildrendata.setGender(mJsonObj.getString(AppConstants.GENDER));
                    }
                    if (mJsonObj.has(AppConstants.BIRTH_PLACE)) {
                        mListChildrendata.setBirth_place(mJsonObj.getString(AppConstants.BIRTH_PLACE));
                    }
                    if (mJsonObj.has(AppConstants.BIRTH_TIME)) {
                        mListChildrendata.setBirth_time(mJsonObj.getString(AppConstants.BIRTH_TIME));
                    }

                    if (mJsonObj.has(AppConstants.IS_INTERESTED)) {
                        isInterest = mJsonObj.getString(AppConstants.IS_INTERESTED);
                        if (isInterest.equals("1")) {
                            mListChildrendata.setInterest(true);
                        } else {
                            mListChildrendata.setInterest(false);
                        }
                    }
                    if (mJsonObj.has(AppConstants.CHILD_BDAY)) {
                        mListChildrendata.setChild_bday(ChangedateFormat(mJsonObj.getString(AppConstants.CHILD_BDAY)));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_EDU)) {
                        mListChildrendata.setChild_edu(mJsonObj.getString(AppConstants.CHILD_EDU));
                    }
                    if (mJsonObj.has(AppConstants.CHILD_WORK)) {
                        mListChildrendata.setChild_work(mJsonObj.getString(AppConstants.CHILD_WORK));
                    }
                    if (!isShared) {
                        if (child_gender.equalsIgnoreCase(childgender) && is_interested.equalsIgnoreCase(isInterest)) {
                            mListChildrendata.setProfile_id(mJsonObject.getString(AppConstants.ID));
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
        AppConstants.DEVICE_ID_VALUE = m_androidId;
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

            if (!((Activity) mContext).isFinishing()) {
                if (pDialog == null) {
                    pDialog = new ProgressDialog(mContext);
                    pDialog.setMessage(mContext.getString(R.string.loading));
                    pDialog.setCancelable(false);
                }
                if (!pDialog.isShowing()) pDialog.show();
                ProgressBar progressbar = pDialog.findViewById(android.R.id.progress);
                progressbar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
            }


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
        paint.setTextSize((24));

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
        return strTo.after(strFrom) || strTo.equals(strFrom);
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

    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public static void ShareScreenShot(Context context, View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
            view.draw(canvas);
        }
        store(bitmap, "family_tree.png", context);
    }

    public static void store(Bitmap bm, String fileName, Context context) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            shareImage(file, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static void shareImage(File file, Context context) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Family Tree");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.krs.vastipatrak \nUpdate your Profile and complete your Family Tree");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            context.startActivity(Intent.createChooser(intent, "Share Family Tree"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public static int getAppVersion(Context context) {
        int verCode = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            verCode = pInfo.versionCode;
            Log.d("getAppVersion", "version: " + version + " verCode:" + verCode);
            return verCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    public static void changeLang(Context context, String lang) {
        String loc = "en";
        if (lang.equals(context.getResources().getString(R.string._english))) {
            loc = "en";
        } else if (lang.equals(context.getResources().getString(R.string._gujarati))) {
            loc = "de";
        } else if (lang.equals(context.getResources().getString(R.string._hindi))) {
            loc = "hi";
        }
        AppController.getInstance().mEditor.putString(context.getResources().getString(R.string.locale_sp),lang);
        AppController.getInstance().mEditor.apply();
        Locale myLocale = new Locale(loc);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    private boolean checktimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            return date1.before(date2);
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
}
