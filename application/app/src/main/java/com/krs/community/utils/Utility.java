package com.krs.community.utils;

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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.krs.community.R;
import com.krs.community.app.AppController;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spencerstudios.com.bungeelib.Bungee;


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


    public static TextInsideCircleButton.Builder getTextInsideCircleButtonBuilder() {
        return new TextInsideCircleButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.text_inside_circle_button_text_normal);
    }

    public static TextInsideCircleButton.Builder getSquareTextInsideCircleButtonBuilder() {
        return new TextInsideCircleButton.Builder()
                .isRound(false)
                .shadowCornerRadius(Util.dp2px(10))
                .buttonCornerRadius(Util.dp2px(10))
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.text_inside_circle_button_text_normal);
    }

    public static TextInsideCircleButton.Builder getTextInsideCircleButtonBuilderWithDifferentPieceColor() {
        return new TextInsideCircleButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.text_inside_circle_button_text_normal)
                .pieceColor(Color.WHITE);
    }

    private static int[] imageResources = new int[]{
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher
    };

    private static int imageResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
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

    public static void movetoFragment(Activity activity,Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();
        Fragment oldFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (oldFragment != null) {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.container_body, fragment, fragment.getClass().getSimpleName()).commit();
        Bungee.fade(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void changeStatusbarColor(Activity activity, int color,boolean pIsDark)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            window.setStatusBarColor(activity.getResources().getColor(color));
            int lFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
            activity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
        }
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

    public static int getToolbarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
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
    }

    /*public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(@NonNull byte[] image) {
        return BitmapFactory.decodeByteArray(image, 100, image.length);
    }*/


    public static void alert(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
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
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.krs.community \nUpdate your Profile and complete your Family Tree");
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
