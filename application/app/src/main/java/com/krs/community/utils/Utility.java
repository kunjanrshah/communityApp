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
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.TypedArray;
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
import android.location.Address;
import android.location.Geocoder;
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
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.fragments.CalendarFragment;
import com.krs.community.fragments.DashboardFragment;
import com.krs.community.fragments.ExpandableFilterListFragment;
import com.krs.community.fragments.FragmentDrawer;
import com.krs.community.fragments.MatrimonyListFragment;
import com.krs.community.fragments.SmartFilterResult;
import com.krs.community.model.ErrorObject;
import com.krs.community.model.Member;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.Util;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class Utility {

    public static final int PICK_GALLERY_REQUEST = 1;
    public static final int FINE_LOCATION_REQUEST = 2;
    public static final int EXTERNAL_STORAGE_REQUEST = 3;
    public static final int CALL_PHONE_REQUEST = 4;
    public static final int READ_CALL_LOG = 5;
    public static final int READ_PHONE_STATE = 6;

    static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    public static String Title = "";
    public static String yyyy_MM_dd = "yyyy-MM-dd";
    public static String ddMMyyyy = "dd.MM.yyyy";
    public static String dd_MM_yyyy = "dd-MM-yyyy";
    public static String yyyy_MM_dd_TIME = "yyyy-MM-dd HH:mm:ss";
    public static String dd_MM_yyyy_TIME = "dd-MM-yyyy h:mm a";

    public static SweetAlertDialog dialog = null;
    public static long INTERVAL = 5 * 60 * 1000;
    public static InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (Character.isWhitespace(source.charAt(i))) {
                return "";
            }
        }
        return null;
    };
    private static ProgressDialog pDialog;
    private static Logger logger = new Logger(Utility.class.getSimpleName());

    private static int[] imageResources = new int[]{R.drawable.export_dot, R.drawable.family_tree_dot, R.drawable.whatsapp_dot, R.drawable.qr_code_dot, R.drawable.share_dot, R.drawable.location_dot};
    private static int[] textResources = new int[]{R.string._export, R.string._qrcode, R.string._share, R.string._location, R.string._whatsapp, R.string._family_tree};
    private static int imageResourceIndex = 0;
    private static int textResourceIndex = 0;

    public static boolean checkReadExternalStoragePermission(Context mContext) {
        int permissionState = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkFineLocationPermission(Context mContext) {
        int permissionState = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkReadCallLogPermission(Context mContext) {
        int permissionState = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkReadPhoneStatePermission(Context mContext) {
        int permissionState = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkExternalStoragePermission(Context mContext) {
        int permissionState;
        int permissionState1 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionState2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionState1 == 0 && permissionState2 == 0) {
            permissionState = 0;
        } else {
            permissionState = 1;
        }
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestReadStoragePermission(AppCompatActivity mActivity) {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_GALLERY_REQUEST);
    }

    public static void requestStoragePermission(AppCompatActivity mActivity) {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
    }

    public static void requestFineLocationPermission(AppCompatActivity mActivity) {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST);
    }

    public static void requestPermissions(AppCompatActivity mActivity) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, READ_CALL_LOG);
        }
    }

    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static JSONObject getServerErrorJsonObject(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConstants.ErrorClass.STATUS, 505);
            jsonObject.put(AppConstants.ErrorClass.CODE, 3000);
            jsonObject.put(AppConstants.ErrorClass.MESSAGE, context.getString(R.string.server_not_available));
            jsonObject.put(AppConstants.ErrorClass.DEVELOPER_MESSAGE, context.getString(R.string.server_not_available));
        } catch (Exception e) {
            logger.error(e);
        }
        return jsonObject;
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
    }

    public static void displaySnackBarWithBottomMargin(View main, String message) {
        Snackbar snackbar = Snackbar.make(main, message, Snackbar.LENGTH_LONG);
        final FrameLayout snackBarView = (FrameLayout) snackbar.getView();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.setMargins(params.leftMargin + 15,
                params.topMargin,
                params.rightMargin + 15,
                params.bottomMargin + 150);
        snackBarView.setLayoutParams(params);
        snackbar.show();
    }

    public static int calculatePercentage(Member member) {
        int percentage = 0;
        int empty = 0;
        int total = 41;

        if (member.getMemberCode() == null || member.getMemberCode().isEmpty()) {
            empty++;
        }
        if (member.getRelationId() == null || member.getRelationId().isEmpty()) {
            empty++;
        }
        if (member.getMobile() == null || member.getMobile().isEmpty()) {
            empty++;
        }
        if (member.getEmailAddress() == null || member.getEmailAddress().isEmpty()) {
            empty++;
        }

        if (member.getFirstName() == null || member.getFirstName().isEmpty()) {
            empty++;
        }
        if (member.getFatherName() == null || member.getFatherName().isEmpty()) {
            empty++;
        }
        if (member.getMotherName() == null || member.getMotherName().isEmpty()) {
            empty++;
        }
        if (member.getSubCastId() == null || member.getSubCastId().isEmpty()) {
            empty++;
        }
        if (member.getGender() == null || member.getGender().isEmpty()) {
            empty++;
        }
        if (member.getAddress() == null || member.getAddress().isEmpty()) {
            empty++;
        }
        if (member.getLocalAddress() == null || member.getLocalAddress().isEmpty()) {
            empty++;
        }
        if (member.getCityId() == null || member.getCityId().isEmpty()) {
            empty++;
        }

        if (member.getStateId() == null || member.getStateId().isEmpty()) {
            empty++;
        }
        if (member.getArea() == null || member.getArea().isEmpty()) {
            empty++;
        }
        if (member.getPincode() == null || member.getPincode().isEmpty()) {
            empty++;
        }
       /* if(member.getPhone()==null||member.getPhone().isEmpty()){
            empty++;
        }*/
        if (member.getBirthDate() == null || member.getBirthDate().isEmpty()) {
            empty++;
        }
        if (member.getBirthTime() == null || member.getBirthTime().isEmpty()) {
            empty++;
        }
        if (member.getBirthPlace() == null || member.getBirthPlace().isEmpty()) {
            empty++;
        }
        if (member.getNativePlaceId() == null || member.getNativePlaceId().isEmpty()) {
            empty++;
        }
        if (member.getBloodGroup() == null || member.getBloodGroup().isEmpty()) {
            empty++;
        }
        if (member.getAboutMe() == null || member.getAboutMe().isEmpty()) {
            empty++;
        }
        if (member.getWeight() == null || member.getWeight().isEmpty()) {
            empty++;
        }
        if (member.getHeight() == null || member.getHeight().isEmpty()) {
            empty++;
        }
        if (member.getHobby() == null || member.getHobby().isEmpty()) {
            empty++;
        }
        if (member.getFacebookProfile() == null || member.getFacebookProfile().isEmpty()) {
            empty++;
        }
        if (member.getCurrentActivityId() == null || member.getCurrentActivityId().isEmpty()) {
            empty++;
        }
        if (member.getMaritalStatus() == null || member.getMaritalStatus().isEmpty()) {
            empty++;
        }
        if (member.getMarriageDate() == null || member.getMarriageDate().isEmpty()) {
            empty++;
        }
        if (member.getGotraId() == null || member.getGotraId().isEmpty()) {
            empty++;
        }
        if (member.getBusinessCategoryId() == null || member.getBusinessCategoryId().isEmpty()) {
            empty++;
        }
        if (member.getBusinessSubCategoryId() == null || member.getBusinessSubCategoryId().isEmpty()) {
            empty++;
        }
        if (member.getWorkDetails() == null || member.getWorkDetails().isEmpty()) {
            empty++;
        }
        if (member.getCompanyName() == null || member.getCompanyName().isEmpty()) {
            empty++;
        }
        if (member.getBusinessAddress() == null || member.getBusinessAddress().isEmpty()) {
            empty++;
        }
        if (member.getProfilePic() == null || member.getProfilePic().isEmpty()) {
            empty++;
        }
        if (member.getBusinessLogo() == null || member.getBusinessLogo().isEmpty()) {
            empty++;
        }
        if (member.getWebsite() == null || member.getWebsite().isEmpty()) {
            empty++;
        }
        if (member.getEducationId() == null || member.getEducationId().isEmpty()) {
            empty++;
        }
        if (member.getOccupationId() == null || member.getOccupationId().isEmpty()) {
            empty++;
        }
        if (member.getHomeLat() == null || member.getHomeLat().isEmpty()) {
            empty++;
        }
        if (member.getHomeLng() == null || member.getHomeLng().isEmpty()) {
            empty++;
        }
        percentage = (100 * empty) / total;
        return (100 - percentage);
    }

    /**
     * This method returns a Json object for handling Force update error
     *
     * @return
     */
    public static ErrorObject getServerErrorPojo(Context context) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(getServerErrorJsonObject(context).toString(), ErrorObject.class);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public static boolean IsValidate(@NonNull final String time) {
        String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);
        Matcher matcher = pattern.matcher(time);
        return !matcher.matches();

    }

    public static boolean isValidFormat(String value, String format) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    public static String changeDateFormat(String inputDateStr, String input, String output) {
        String outputDateStr = inputDateStr;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(input);
            SimpleDateFormat outputFormat = new SimpleDateFormat(output);
            if (inputDateStr != null && !inputDateStr.isEmpty()) {
                Date date = inputFormat.parse(inputDateStr);
                if (date != null) {
                    outputDateStr = outputFormat.format(date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputDateStr;
    }





   /* public static String distance(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
           dist=dist/1000;

        return String.format("%.02f", dist)+" KM";
    }*/

    public static void fade(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.fade_enter, R.anim.fade_exit);
    }

    public static void fade1(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    public static Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static TextInsideCircleButton.Builder getTextInsideCircleButtonBuilder() {
        return new TextInsideCircleButton.Builder().normalColor(Color.WHITE).pieceColor(Color.GRAY).normalImageRes(getImageResource()).normalTextRes(getTextResource());
    }

    public static TextInsideCircleButton.Builder getSquareTextInsideCircleButtonBuilder() {
        return new TextInsideCircleButton.Builder().isRound(false).shadowCornerRadius(Util.dp2px(10)).buttonCornerRadius(Util.dp2px(10)).normalImageRes(getImageResource()).normalTextRes(R.string.text_inside_circle_button_text_normal);
    }

    public static TextInsideCircleButton.Builder getTextInsideCircleButtonBuilderWithDifferentPieceColor() {
        return new TextInsideCircleButton.Builder().normalImageRes(getImageResource()).normalTextRes(R.string.text_inside_circle_button_text_normal).pieceColor(Color.WHITE);
    }

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    static int getTextResource() {
        if (textResourceIndex >= textResources.length) textResourceIndex = 0;
        return textResources[textResourceIndex++];
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
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void backNavigation(Activity activity) {
        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        Log.d("backNavigation", "count: " + fragmentManager.getBackStackEntryCount());
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Fragment dashboard = fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
            Fragment calendar = fragmentManager.findFragmentByTag(CalendarFragment.class.getSimpleName());
            Fragment smartFilterResult = fragmentManager.findFragmentByTag(SmartFilterResult.class.getSimpleName());
            Fragment expandableFragment = fragmentManager.findFragmentByTag(ExpandableFilterListFragment.class.getSimpleName());
            if (dashboard != null && dashboard.isVisible()) {
                activity.finish();
                return;
            } else if ((calendar != null && calendar.isVisible()) || (smartFilterResult != null && smartFilterResult.isVisible()) || expandableFragment != null && expandableFragment.isVisible()) {
                movetoFragment(activity, new DashboardFragment());
                return;
            } else {
                fragmentManager.popBackStack();
                fade(activity);
            }
        } else {
            if (FragmentDrawer.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                FragmentDrawer.mDrawerLayout.closeDrawers();
            } else {
                activity.finish();
                fade(activity);
            }
        }
    }

    public static void movetoFragment(Activity activity, Fragment fragment) {

        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container_body);
        if (currentFragment != null) {
            if (currentFragment.getClass().getSimpleName().equals(fragment.getClass().getSimpleName()) && !currentFragment.getClass().getSimpleName().equals(MatrimonyListFragment.class.getSimpleName())) {
                return;
            }
        }
        if (fragment.isVisible()) {
            return;
        }
        if (fragment.getClass().getSimpleName().equals(DashboardFragment.class.getSimpleName())) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        Fragment oldFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.container_body, fragment, fragment.getClass().getSimpleName());

        if (oldFragment != null) {
            fragmentTransaction.remove(oldFragment);
        } else {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
        fade(activity);
    }

    public static int getRandomMaterialColor(Context context, String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void changeStatusbarColor(Activity activity, int color, boolean pIsDark) {
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

        */

    /**
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

        return GpsStatus;

    }

    public static List<Address> getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            /*String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

/*    public static String getCompleteAddressString(Context context,double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }*/


    public static void showDirections(Activity mActivity, double dlatitude, double dlongitude, String address) {

        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", dlatitude, dlongitude, address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        mActivity.startActivity(intent);
    }

    public static Bitmap getBitmap(Context context, File f) throws IOException {

        // Get the source image's dimensions
        int desiredWidth = 200;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // Only scale if the source is big enough. This code is just trying
        // to fit a image into a certain width.
        if (desiredWidth > srcWidth) desiredWidth = srcWidth;

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
        sampledSrcBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
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
            final int REQUIRED_SIZE = 200;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
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
        alertDialog.setPositiveButton("Setting", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mActivity.startActivity(intent);
            dialog.cancel();
            mActivity.finish();
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


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixelSize) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = roundPixelSize;
        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /*public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
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


    public static String DatetoString(Date date, String pattern) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            String dateTime = dateFormat.format(date);
            System.out.println("Current Date Time : " + dateTime);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date StringToDate(String dtStart, String pattern) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(pattern);
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
        String newFormat = "dd-MM-yyyy";

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

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static int getAge(String dobString, String pattern) {

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            date = sdf.parse(dobString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(YEAR);
        int month = dob.get(MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(YEAR) - dob.get(YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }


        return (age + 1);
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

    public static String getPath() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppController.mApplication.getString(R.string.folder_name);
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }


    public static void startSweetProgress(Context context, String title, String message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setContentText(message);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#843f52"));
        dialog.setTitleText(title);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void startSweetDialog(Context context, int type, String title, String message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new SweetAlertDialog(context, type).setContentText(message);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#843f52"));

        dialog.setTitleText(title);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void hideSweetProgress() {
        try {
            if (dialog != null && dialog.isShowing()) dialog.cancel();
            dialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }


    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static Bitmap drawTextToBitmap(Bitmap bitmap, String gText, Context context) {
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
        paint.setTextSize((30));

        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) - 40;

        canvas.drawText(gText, x, y, paint);
        canvas.drawText(context.getString(R.string.app_name), x + 50, 25, paint);
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

    public static void sendWhatsappMessage(@NonNull Context mActivity, String mob_num, String message) {
        // String digits = "\\d+";
        //if (mob_num.matches(digits)) {
        try {
            //linking for whatsapp
            Uri uri = Uri.parse("whatsapp://send?phone=+91" + mob_num + "&text=" + URLEncoder.encode(message, "UTF-8"));
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            mActivity.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mActivity, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
        }
        //}
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
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppController.mApplication.getString(R.string.folder_name);
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

    public static void shareToGMail(Activity activity, String[] email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = activity.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        activity.startActivity(emailIntent);
    }


    public static void skype(String number, Context ctx) {
        try {
            //Intent sky = new Intent("android.intent.action.CALL_PRIVILEGED");
            //the above line tries to create an intent for which the skype app doesn't supply public api

            Intent sky = new Intent("android.intent.action.VIEW");
            sky.setData(Uri.parse("skype:" + number));
            Log.d("UTILS", "tel:" + number);
            ctx.startActivity(sky);
        } catch (ActivityNotFoundException e) {
            Log.e("SKYPE CALL", "Skype failed", e);
        }
        Toast.makeText(ctx, number, Toast.LENGTH_LONG).show();
    }

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

    public static String getHashKey(Context context) {
        String hashKey = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return hashKey;
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
        Guru.putString(context.getResources().getString(R.string.locale_sp), lang);
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
