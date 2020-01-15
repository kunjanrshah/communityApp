package com.krs.community.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.krs.community.R;
import com.krs.community.dexter.SampleBackgroundThreadPermissionListener;
import com.krs.community.dexter.SampleErrorListener;
import com.krs.community.dexter.SampleMultiplePermissionListener;
import com.krs.community.dexter.SamplePermissionListener;

public class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    public static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    public static final int REQUEST_LOCATION_PERMISSION = 103;

    public static final int PICK_GALLERY_REQUEST = 1;
    private AlertDialog mAlertDialog;

    private MultiplePermissionsListener allPermissionsListener;
    private PermissionListener cameraPermissionListener;
    private PermissionListener contactsPermissionListener;
    private PermissionListener audioPermissionListener;
    private PermissionRequestErrorListener errorListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createPermissionListeners();
    }

    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new SamplePermissionListener(this);
        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new SampleMultiplePermissionListener(this);

        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(contentView,
                                R.string.all_permissions_denied_feedback)
                                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                                .build());
        contactsPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(contentView,
                        R.string.contacts_permission_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .withCallback(new Snackbar.Callback() {
                            @Override public void onShown(Snackbar snackbar) {
                                super.onShown(snackbar);
                            }

                            @Override public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                            }
                        })
                        .build());

        PermissionListener dialogOnDeniedPermissionListener =
                DialogOnDeniedPermissionListener.Builder.withContext(this)
                        .withTitle(R.string.audio_permission_denied_dialog_title)
                        .withMessage(R.string.audio_permission_denied_dialog_feedback)
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_logo_karumi)
                        .build();
        audioPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                dialogOnDeniedPermissionListener);
        cameraPermissionListener = new SampleBackgroundThreadPermissionListener(this);

        errorListener = new SampleErrorListener();
    }

    public void showPermissionGranted(String permission) {
        TextView feedbackView = getFeedbackViewForPermission(permission);
        feedbackView.setText(R.string.permission_granted_feedback);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_granted));
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        TextView feedbackView = getFeedbackViewForPermission(permission);
        feedbackView.setText(isPermanentlyDenied ? R.string.permission_permanently_denied_feedback
                : R.string.permission_denied_feedback);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_denied));
    }

}
