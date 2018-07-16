package com.krs.vastipatrak.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.krs.vastipatrak.utils.Common;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import org.json.JSONObject;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class BusinessFilter extends Fragment {

    private static final int CONTACT_PICKER_RESULT = 1001;
    public EditText edtOccupation, edtWork, edtOMobile, edtOAddress;
    private FloatingActionButton floatingActionButton;
    private ObservableScrollView scroll_bdetails;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_business, container, false);
        MemoryAllocation(rootView);
        setPreferenceData();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FilterActivity) requireNonNull(getActivity())).callAdvanceSearchWS();
            }
        });

        edtOMobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtOMobile.getRight() - edtOMobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Common.canReadContacts(Objects.requireNonNull(getActivity()))) {
                                Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(it, CONTACT_PICKER_RESULT);
                            }
                        } else {
                            Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            startActivityForResult(it, CONTACT_PICKER_RESULT);
                        }

                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    private void MemoryAllocation(@NonNull View rootView) {
        scroll_bdetails = rootView.findViewById(R.id.scroll_bdetails);
        floatingActionButton = rootView.findViewById(R.id.fab_bsave);
        edtOccupation = rootView.findViewById(R.id.edtOccupation);
        edtWork = rootView.findViewById(R.id.edtWork);
        edtOMobile = rootView.findViewById(R.id.edtOMobile);
        edtOAddress = rootView.findViewById(R.id.edtOAddress);
    }

    private void setPreferenceData() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        String json = mSharedPreferences.getString("adv_search", "");
        JSONObject mjsonObject = null;
        try {
            mjsonObject = new JSONObject(json);
            if (mjsonObject.has(Common.Constant_Class.OCCUPATION)) {
                edtOccupation.setText(mjsonObject.getString(Common.Constant_Class.OCCUPATION));
            }
            if (mjsonObject.has(Common.Constant_Class.WORK)) {
                edtWork.setText(mjsonObject.getString(Common.Constant_Class.WORK));
            }
            if (mjsonObject.has(Common.Constant_Class.OFFICE_MOBILE)) {
                edtOMobile.setText(mjsonObject.getString(Common.Constant_Class.OFFICE_MOBILE));
            }
            if (mjsonObject.has(Common.Constant_Class.OFFICE_ADDRESS)) {
                edtOAddress.setText(mjsonObject.getString(Common.Constant_Class.OFFICE_ADDRESS));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == Activity.RESULT_OK && null != data) {
            Uri contactUri = data.getData();
            Cursor contactCursor = requireNonNull(getActivity()).getContentResolver().query(Objects.requireNonNull(contactUri),
                    new String[]{ContactsContract.Contacts._ID}, null, null,
                    null);
            String id = null;
            if (requireNonNull(contactCursor).moveToFirst()) {
                id = contactCursor.getString(contactCursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
            }
            contactCursor.close();
            String phoneNumber;
            Cursor phoneCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                    new String[]{id}, null);
            if (requireNonNull(phoneCursor).moveToFirst()) {
                phoneNumber = phoneCursor
                        .getString(phoneCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.v("phoneNumber :", "" + phoneNumber);
                if (phoneNumber != null) {
                    edtOMobile.setText(phoneNumber.replace("+", ""));
                }
            }
            phoneCursor.close();
        }
    }
}
