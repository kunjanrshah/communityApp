package com.krs.community.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.model.Checksum;
import com.krs.community.model.Paytm;
import com.krs.community.utils.WebServiceCaller;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PaytmFragment extends Fragment {

    AppCompatButton paytmButton;
    AppCompatEditText amountText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_paytm, container, false);

        AppController mApp = (AppController) getApplicationContext();
        mApp.FirebaseAnalytics(getContext(),PaytmFragment.class.getSimpleName());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }


        paytmButton = root.findViewById(R.id.paytmButton);
        amountText = root.findViewById(R.id.amountText);


        processPaytm();

        return root;

    }

    private void processPaytm() {
        Log.e("MAINACTIVITY", "Start----");
        String custID = generateString();
        String orderID = generateString();
        String callBackurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderID;

        final Paytm paytm = new Paytm(
                "vdyfeQ58148286158208",
                orderID,
                custID,
                "Retail",
                "WAP",
                "1",
                "WEBSTAGING",

                callBackurl

        );

        WebServiceCaller.getClient().getChecksum(paytm.getmId(), paytm.getOrderId(), paytm.getCustId(), paytm.getIndustryTypeId()
                , paytm.getChannelId(), paytm.getTxnAmount(), paytm.getWebsite(), paytm.getCallBackUrl()).enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {
                if (response.isSuccessful()) {
                    processToPay(response.body().getChecksumHash(), paytm);
                }
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {
                Log.e("error", "" + t.getMessage());
            }
        });


    }

    private void processToPay(String checksumHash, Paytm paytm) {

        Log.e("MAINACTIVITY", "checkSum----" + checksumHash);
        PaytmPGService Service = PaytmPGService.getStagingService();

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", paytm.getmId());
// Key in your staging and production MID available in your dashboard
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
// This is the staging value. Production value is available in your dashboard
// This is the staging value. Production value is available in your dashboard
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        PaytmOrder Order = new PaytmOrder(paramMap);
        Service.initialize(Order, null);

        Service.startPaymentTransaction(getContext(), true, true, new PaytmPaymentTransactionCallback() {
            /*Call Backs*/
            public void someUIErrorOccurred(String inErrorMessage) {
            }

            public void onTransactionResponse(Bundle inResponse) {
                Toast.makeText(getContext(), inResponse.toString(), Toast.LENGTH_SHORT).show();
                Log.e("checksum ", "-- " + inResponse.toString());

            }

            public void networkNotAvailable() {
            }

            public void clientAuthenticationFailed(String inErrorMessage) {
            }

            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

            }

            public void onBackPressedCancelTransaction() {
                Log.e("checksum ", " cancel call back respon  ");

            }

            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
            }
        });

    }

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

}
