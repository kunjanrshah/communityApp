package com.krs.community.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.github.squti.guru.Guru;
import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.activity.ScanQRCodeActivity;
import com.krs.community.model.Member;
import com.krs.community.utils.AESUtils;
import com.krs.community.utils.Utility;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import me.ydcool.lib.qrmodule.encoding.QrGenerator;

import static android.app.Activity.RESULT_OK;
import static com.facebook.AccessTokenManager.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

public class ByQRCodeFragment extends Fragment {
    private Uri imageUri;
    private Intent intent;
    Bitmap b;
    TextView TvDate;
    Handler handler;
    private static final int SELECT_PHOTO = 100;
    public String barcode;
    String Flag = "0",Id, Mobile, Fname;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (getArguments() != null){
            Mobile = getArguments().getString("Mobile");
            Id = getArguments().getString("Id");
            Fname = getArguments().getString("Fname");
            Flag = getArguments().getString("Flag");
        }

        View root=inflater.inflate(R.layout.fragment_by_qrcode,container,false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
        }

        String loginMember=Guru.getString(getString(R.string.loginUser),"");
        Member member= new Gson().fromJson(loginMember, Member.class);

        ImageView iv_cancel=root.findViewById(R.id.iv_cancel);
        ImageView imgCode=root.findViewById(R.id.imgCode);
        TextView memberId=root.findViewById(R.id.memberId);
        TextView Tvname=root.findViewById(R.id.Tvname);
        TvDate=root.findViewById(R.id.TvDate);
        LinearLayout llScanQrCode=root.findViewById(R.id.llScanQrCode);
        LinearLayout llShareQRCode=root.findViewById(R.id.llShareQRCode);
        LinearLayout llQrCode=root.findViewById(R.id.llQrCode);
        LinearLayout llCodeGallery=root.findViewById(R.id.llCodeGallery);


        llCodeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPic = new Intent(Intent.ACTION_PICK);
                photoPic.setType("image/*");
                startActivityForResult(photoPic, SELECT_PHOTO);
            }
        });
        llScanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ScanQRCodeActivity.class);
                startActivity(i);

            }
        });

        Log.e("Mobile-----",""+Mobile);

        String MId ;
        if (Flag.equalsIgnoreCase("1")){
            MId = Id;
            Tvname.setText(Fname);
            memberId.setText(Mobile);

        }else {

            MId= member.getId();
            Tvname.setText(member.getFirstName());
            memberId.setText(member.getMobile());
        }


        String encrypted = "";
        Log.e("loginMember---", ":" + loginMember);
        try {
            encrypted = AESUtils.encrypt(MId);
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

        imgCode.setImageBitmap(qrCode);

        llShareQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' h:mm a");
                String date = df.format(Calendar.getInstance().getTime());

                TvDate.setText(date);
                memberId.setText("MemberId -"+MId);

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        llQrCode.setDrawingCacheEnabled(true);
                        llQrCode.buildDrawingCache(true);

                        b = Bitmap.createBitmap(llQrCode.getDrawingCache());

                        saveImage(b);

                        Log.e("b---",""+b);
                    }
                }, 1000);

            }
        });

        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });




        return root;
    }

    private void shareImageUri(Uri uri){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
    private Uri saveImage(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", file);


            Log.e("uri--",""+uri);
        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }

        TvDate.setText("");
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
                        bMap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
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
                      String  id = result.getText();
                        Log.d("QRCODE Result:","id: "+id);
                        if (id != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Scan Result");
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setMessage("" + id);
                            AlertDialog alert1 = builder.create();
                            alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alert1.setCanceledOnTouchOutside(false);

                            alert1.show();

                          /*  Intent mIntent = new Intent(MainActivity.this, MyProfileActivity.class);
                            startActivity(mIntent);*/
                        }
                        //byte[] rawBytes = result.getRawBytes();
                        //BarcodeFormat format = result.getBarcodeFormat();
                        //ResultPoint[] points = result.getResultPoints();
                    } catch (@NonNull NotFoundException | ChecksumException | FormatException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
    }


}
