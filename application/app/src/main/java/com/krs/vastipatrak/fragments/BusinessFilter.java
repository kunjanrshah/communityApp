package com.krs.vastipatrak.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FilterActivity;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

/**
 * Created by Kunjan on 27/08/2016.
 */
public class BusinessFilter extends Fragment {

    FloatingActionButton floatingActionButton;
    ObservableScrollView scroll_bdetails;
    public static EditText edtOccupation, edtWork, edtOMobile, edtOAddress;
    private static final int CONTACT_PICKER_RESULT = 1001;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_business, container, false);
        MemoryAllocation(rootView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FilterActivity) getActivity()).callAdvanceSearchWS();
            }
        });

        edtOMobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtOMobile.getRight() - edtOMobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Common.canReadContacts(getActivity())) {
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


    void MemoryAllocation(View rootView) {
        scroll_bdetails = (ObservableScrollView) rootView.findViewById(R.id.scroll_bdetails);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_bsave);
        edtOccupation = (EditText) rootView.findViewById(R.id.edtOccupation);
        edtWork = (EditText) rootView.findViewById(R.id.edtWork);
        edtOMobile = (EditText) rootView.findViewById(R.id.edtOMobile);
        edtOAddress = (EditText) rootView.findViewById(R.id.edtOAddress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == getActivity().RESULT_OK && null != data) {
            Uri contactUri = data.getData();
            Cursor contactCursor = getActivity().getContentResolver().query(contactUri,
                    new String[]{ContactsContract.Contacts._ID}, null, null,
                    null);
            String id = null;
            if (contactCursor.moveToFirst()) {
                id = contactCursor.getString(contactCursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
            }
            contactCursor.close();
            String phoneNumber = null;
            Cursor phoneCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                    new String[]{id}, null);
            if (phoneCursor.moveToFirst()) {
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
