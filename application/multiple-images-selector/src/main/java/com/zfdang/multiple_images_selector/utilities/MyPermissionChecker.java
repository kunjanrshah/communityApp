package com.zfdang.multiple_images_selector.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zfdang.multiple_images_selector.ImagesSelectorActivity;

public class MyPermissionChecker {

    private static final int MY_PERMISSIONS_REQUEST_STORAGE_CODE = 197;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA_CODE = 341;
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean checkAndRequestReadImagesPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( (ImagesSelectorActivity)context , new String[]{Manifest.permission.READ_MEDIA_IMAGES}, MY_PERMISSIONS_REQUEST_STORAGE_CODE);
            return false;
        }else{
            return true;
        }
    }

    public static boolean checkAndRequestReadStoragePermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( (ImagesSelectorActivity)context , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE_CODE);
            return false;
        }else{
            return true;
        }
    }

    public static boolean requestCameraRuntimePermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((ImagesSelectorActivity)context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA_CODE);
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasExternalStoragePermission(Context context) {
        return  context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }
}
