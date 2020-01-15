package com.krs.community.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.PermissionToken;
import com.krs.community.R;
import com.krs.community.utils.Utility;

public class BaseActivity extends AppCompatActivity {

    public static final int PICK_GALLERY_REQUEST = 1;
    View contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentView=findViewById(android.R.id.content);
        Utility.createPermissionListeners(this,contentView);
    }

    public void showPermissionGranted(String permission) {
        Log.d("BaseActivity","Granted: "+permission);
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        Log.d("BaseActivity","Denied: "+permission+" isPermanentlyDenied: "+isPermanentlyDenied);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

}
