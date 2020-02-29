package com.krs.community.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.krs.community.R;
import com.krs.community.app.AppController;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyContactListFragment extends Fragment {

    public static final int RequestPermissionCode = 1;
    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name, phonenumber;
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mycontactlist, container, false);

        AppController mApp = (AppController) getApplicationContext();
        mApp.FirebaseAnalytics(getContext(),MyContactListFragment.class.getSimpleName());

        listView = (ListView) root.findViewById(R.id.listview1);

        button = (Button) root.findViewById(R.id.button1);

        StoreContacts = new ArrayList<String>();

        EnableRuntimePermission();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetContactsIntoArrayList();

                arrayAdapter = new ArrayAdapter<String>(
                        getContext(), R.layout.contact_items_listview, R.id.textView, StoreContacts
                );

                listView.setAdapter(arrayAdapter);


            }
        });
        return root;

    }

    public void GetContactsIntoArrayList() {

        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " " + ":" + " " + phonenumber);
        }

        cursor.close();

    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(), Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(getActivity(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(), "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getActivity(), "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

}
