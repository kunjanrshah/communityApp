package com.krs.community.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.krs.community.BuildConfig;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.databinding.FragmentByQrcodeBinding;
import com.krs.community.model.Member;
import com.krs.community.utils.AESUtils;
import com.krs.community.utils.Utility;
import com.wessam.library.NetworkChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.ydcool.lib.qrmodule.encoding.QrGenerator;

import static com.facebook.AccessTokenManager.TAG;

public class QRCodeActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    Bitmap b;
    Handler handler;
    NetworkChangeReceiver mNetworkReceiver;
    FragmentByQrcodeBinding binding;
    private Member member;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNetworkReceiver = new NetworkChangeReceiver();

        registerNetworkBroadcastForNougat();

        if (NetworkChecker.isNetworkConnected(this)) {
            setScreenLayout();
        } else {
            setNoInternetLayout();
        }

        AppController mApp = (AppController) getApplicationContext();
        mApp.FirebaseAnalytics(QRCodeActivity.this, QRCodeActivity.class.getSimpleName());
    }

    private void setNoInternetLayout() {
        setContentView(R.layout.no_internet_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(6000);
        anim.setRepeatMode(AlphaAnimation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        AppCompatImageView imageView = findViewById(R.id.no_internet_image);
        imageView.setAnimation(anim);
        AppCompatButton retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(v -> {
            if (NetworkChecker.isNetworkConnected(this)) {
                setScreenLayout();
            }
        });
    }


    private void setScreenLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_by_qrcode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false);
        }

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            member = (Member) mBundle.getSerializable("member");
        }

        String encrypted = "";
        try {
            encrypted = AESUtils.encrypt(member.getId());
            Log.e("TEST", "encrypted:" + encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap qrCode = null;
        try {
            qrCode = new QrGenerator.Builder()
                    .content(encrypted)
                    .qrSize(300)
                    .margin(2)
                    .color(Color.BLACK)
                    .bgColor(Color.WHITE)
                    .ecc(ErrorCorrectionLevel.H)
                    .overlaySize(100)
                    .overlayAlpha(255)
                    .encode();
        } catch (WriterException e) {
            e.printStackTrace();
        }

        binding.ivCode.setImageBitmap(qrCode);
        binding.tvName.setText(member.getFirstName());
        binding.tvMobile.setText(member.getMobile());

        binding.llGallery.setOnClickListener(v -> {

            binding.llGallery.setBackground(getResources().getDrawable(R.drawable.border_bg_color_primary));
            binding.tvGallery.setTextColor(getResources().getColor(R.color.white));
            binding.ivGallery.setBackground(getResources().getDrawable(R.drawable.gallery_white));

            binding.llscan.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvScan.setTextColor(getResources().getColor(R.color.black1));
            binding.ivScan.setBackground(getResources().getDrawable(R.drawable.scan_qr_code));

            binding.llShare.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvShare.setTextColor(getResources().getColor(R.color.black1));
            binding.ivShare.setBackground(getResources().getDrawable(R.drawable.share_black));

            Intent photoPic = new Intent(Intent.ACTION_PICK);
            photoPic.setType("image/*");
            startActivityForResult(photoPic, SELECT_PHOTO);

        });

        binding.llscan.setOnClickListener(v -> {

            binding.llscan.setBackground(getResources().getDrawable(R.drawable.border_bg_color_primary));
            binding.tvScan.setTextColor(getResources().getColor(R.color.white));
            binding.ivScan.setBackground(getResources().getDrawable(R.drawable.scan_qr_code_white));

            binding.llGallery.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvGallery.setTextColor(getResources().getColor(R.color.black1));
            binding.ivGallery.setBackground(getResources().getDrawable(R.drawable.gallery));

            binding.llShare.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvShare.setTextColor(getResources().getColor(R.color.black1));
            binding.ivShare.setBackground(getResources().getDrawable(R.drawable.share_black));

            Intent i = new Intent(this, ScanQRCodeActivity.class);
            startActivity(i);
        });

        binding.llShare.setOnClickListener(v -> {

            binding.llShare.setBackground(getResources().getDrawable(R.drawable.border_bg_color_primary));
            binding.tvShare.setTextColor(getResources().getColor(R.color.white));
            binding.ivShare.setBackground(getResources().getDrawable(R.drawable.share));

            binding.llscan.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvScan.setTextColor(getResources().getColor(R.color.black1));
            binding.ivScan.setBackground(getResources().getDrawable(R.drawable.scan_qr_code));

            binding.llGallery.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvGallery.setTextColor(getResources().getColor(R.color.black1));
            binding.ivGallery.setBackground(getResources().getDrawable(R.drawable.gallery));

            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            String date = df.format(Calendar.getInstance().getTime());

            handler = new Handler();
            Utility.startSweetProgress(this, "QRCode Gallery", "Generating QRCode...");
            handler.postDelayed(() -> {
                Utility.hideSweetProgress();
                binding.ivCode.setDrawingCacheEnabled(true);
                binding.ivCode.buildDrawingCache(true);

                b = Bitmap.createBitmap(binding.ivCode.getDrawingCache());
                String str = member.getId() + "   " + member.getFirstName() + "   " + date;
                Bitmap bmp = Utility.drawTextToBitmap(b, str, QRCodeActivity.this);
                saveImage(bmp);

            }, 1000);

        });

        binding.ivCancel.setOnClickListener(v -> {
            finish();
            Utility.fade(this);
        });
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkBroadcastForNougat();
    }

    private void unregisterNetworkBroadcastForNougat() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                unregisterReceiver(mNetworkReceiver);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                unregisterReceiver(mNetworkReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareImageUri(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }

    private void saveImage(Bitmap image) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppController.mApplication.getString(R.string.folder_name);
        File imagesFolder = new File(path);
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "qrcode.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            Log.e("uri--", "" + uri);
        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        if (uri != null) {
            shareImageUri(uri);
        } else {
            Utility.displaySnackBarWithBottomMargin(binding.scrollView, getString(R.string.went_wrong));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();

                    Bitmap bMap = null;
                    try {
                        bMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    //copy pixel data from the Bitmap into the 'intArray' array
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Reader reader = new MultiFormatReader();

                    try {
                        Result result = reader.decode(bitmap);
                        String output = result.getText();
                        Log.d("QRCODE Result:", "output: " + output);
                        if (output != null) {
                            String decrypted = "";
                            try {
                                decrypted = AESUtils.decrypt(output);
                                Log.e(TAG, "decrypted:" + decrypted);

                                Intent mIntent = new Intent(this, ProfileDetailActivity.class);
                                mIntent.putExtra(getString(R.string.scanId), decrypted);
                                startActivity(mIntent);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (NetworkChecker.isNetworkConnected(context)) {
                    setScreenLayout();
                } else {
                    setNoInternetLayout();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
