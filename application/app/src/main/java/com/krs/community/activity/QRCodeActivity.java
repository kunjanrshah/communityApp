package com.krs.community.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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
import com.krs.community.activity.DashboardActivity;
import com.krs.community.activity.ProfileDetailActivity;
import com.krs.community.activity.ScanQRCodeActivity;
import com.krs.community.databinding.FragmentByQrcodeBinding;
import com.krs.community.model.Member;
import com.krs.community.utils.AESUtils;
import com.krs.community.utils.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.ydcool.lib.qrmodule.encoding.QrGenerator;

import static android.app.Activity.RESULT_OK;
import static com.facebook.AccessTokenManager.TAG;
import static com.facebook.FacebookSdk.getCacheDir;

public class QRCodeActivity extends AppCompatActivity {

    Bitmap b;
    Handler handler;
    private static final int SELECT_PHOTO = 100;
    private Member member;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentByQrcodeBinding binding = DataBindingUtil.setContentView(this,R.layout.fragment_by_qrcode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this,R.color.colorBG,false);
        }

        Bundle mBundle=getIntent().getExtras();
        if (mBundle != null){
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

            binding.llscan.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvScan.setTextColor(getResources().getColor(R.color.black1));

            binding.llShare.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvShare.setTextColor(getResources().getColor(R.color.black1));

            Intent photoPic = new Intent(Intent.ACTION_PICK);
            photoPic.setType("image/*");
            startActivityForResult(photoPic, SELECT_PHOTO);

        });

        binding.llscan.setOnClickListener(v -> {

            binding.llscan.setBackground(getResources().getDrawable(R.drawable.border_bg_color_primary));
            binding.tvScan.setTextColor(getResources().getColor(R.color.white));

            binding.llGallery.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvGallery.setTextColor(getResources().getColor(R.color.black1));

            binding.llShare.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvShare.setTextColor(getResources().getColor(R.color.black1));

            Intent i = new Intent(this, ScanQRCodeActivity.class);
            startActivity(i);
        });

        binding.llShare.setOnClickListener(v -> {

            binding.llShare.setBackground(getResources().getDrawable(R.drawable.border_bg_color_primary));
            binding.tvShare.setTextColor(getResources().getColor(R.color.white));

            binding.llscan.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvScan.setTextColor(getResources().getColor(R.color.black1));

            binding.llGallery.setBackground(getResources().getDrawable(R.drawable.border_color_gray3));
            binding.tvGallery.setTextColor(getResources().getColor(R.color.black1));

            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            String date = df.format(Calendar.getInstance().getTime());

            handler = new Handler();
            handler.postDelayed(() -> {

                binding.ivCode.setDrawingCacheEnabled(true);
                binding.ivCode.buildDrawingCache(true);

                b = Bitmap.createBitmap(binding.ivCode.getDrawingCache());
                String str=member.getId()+"   "+member.getFirstName()+"   "+date;
                Bitmap bmp= Utility.drawTextToBitmap(b,str);
                saveImage(bmp);

            }, 1000);

        });

        binding.ivCancel.setOnClickListener(v -> {
            finish();
            Utility.fade(this);
        });
    }


    private void shareImageUri(Uri uri){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
    private Uri saveImage(Bitmap image) {
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);


            Log.e("uri--",""+uri);
        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }

        shareImageUri(uri);
        return uri;
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
                      String  output = result.getText();
                        Log.d("QRCODE Result:","output: "+output);
                        if (output != null) {
                            String decrypted = "";
                            try {
                                decrypted = AESUtils.decrypt(output);
                                Log.e(TAG, "decrypted:" + decrypted);

                                Intent mIntent=new Intent(this,ProfileDetailActivity.class);
                                mIntent.putExtra(getString(R.string.scanId),decrypted);
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

}
