package com.krs.vastipatrak.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.ProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;

public class BusinessFragment extends Fragment implements Serializable {


    private static final int CONTACT_PICKER_RESULT = 1001;
    private final String tag_json_obj = "jobj_req";
    private final String TAG = BusinessFragment.class.getSimpleName();
    public EditText edtOccupation, edtWork, edtOMobile, edtOAddress;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private TextView txt_office;
    private double office_lat = 0;
    private double office_lng = 0;

    private Activity mActivity;

    public BusinessFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_business, container, false);
        Memory_Allocation(rootView);

        try {
            ListProfileData mListProfileData = ((ProfileActivity) mActivity).getMyData();
            if (mListProfileData != null) {
                SetOfflineData(mListProfileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        edtOAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtOAddress.getRight() - edtOAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
                        String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
                        final double lat = Double.valueOf(curr_lat);
                        final double lng = Double.valueOf(curr_lng);

                        if (lat == 0 && lng == 0) {
                            Common.showSettingsAlert(mActivity);
                        } else {
                            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || ProfileActivity.isEnable) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                builder.setTitle(getString(R.string.app_name));

                                builder.setMessage(getString(R.string.office_location));
                                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (lat != 0 && lng != 0) {
                                            /*office_loc_flag = true;*/
                                            officeLocUpdateWS();
                                        }
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(@NonNull DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

                            } else {
                                if (lat != 0 && lng != 0 && office_lat != 0 && office_lng != 0) {
                                    Common.showDirections(getActivity(),lat, lng,office_lat,office_lng,"");
                                } else {
                                    Toast.makeText(mActivity, "Something wrong went!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        edtOMobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((event.getRawX()) >= (edtOMobile.getRight() - edtOMobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
                    } else {
                        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) && !ProfileActivity.isEnable) {
                            try {
                                boolean flag = true;
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (Common.canCallPhone(getActivity())) {
                                        flag = false;
                                    }
                                }
                                if (flag) {
                                    String phone_no = edtOMobile.getText().toString().replaceAll("-", "");
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + phone_no.trim()));
                                    getActivity().startActivity(callIntent);
                                }
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return false;
            }
        });

        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            if (mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.USER)) {
                DisableAll();
            }
        }

        return rootView;
    }

    private void officeLocUpdateWS() {
        if (Common.isOnline(mActivity)) {
            JSONObject mJsonObject = null;
            String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
            String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
            final double lat = Double.valueOf(curr_lat);
            final double lng = Double.valueOf(curr_lng);
            try {
                mJsonObject = new JSONObject();
                if (lat != 0 && lng != 0) {
                    mJsonObject.put(Common.Constant_Class.OFFICE_LAT, lat);
                    mJsonObject.put(Common.Constant_Class.OFFICE_LNG, lng);
                    if (ProfileActivity.isEnable && mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
                        mJsonObject.put(Common.Constant_Class.UPDATE_USER_ID, mSharedPreferences.getString(Common.Constant_Class.PROFILE_ID, ""));
                    }
                    mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                    mJsonObject.put(Common.Constant_Class.IS_UPDATE, "1");
                    mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.PROFILE_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            mEditor.putString(Common.Constant_Class.OFFICE_LAT, String.valueOf(lat));
                            mEditor.putString(Common.Constant_Class.OFFICE_LNG, String.valueOf(lng));
                            mEditor.apply();
                            alert("Office location updated!");
                        } else {
                            alert("Something went wrong!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    hideProgressDialog();
                }
            }

            ) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Common.Constant_Class.INIT_TIMEOUT, Common.Constant_Class.DEFAULT_MAX_RETRIES, Common.Constant_Class.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }

    private void Memory_Allocation(View rootView) {

        mActivity = getActivity();
        edtOccupation = rootView.findViewById(R.id.edtOccupation);
        edtWork = rootView.findViewById(R.id.edtWork);
        edtOMobile = rootView.findViewById(R.id.edtOMobile);
        edtOAddress = rootView.findViewById(R.id.edtOAddress);
        mSharedPreferences = mActivity.getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
       // user_id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
        txt_office = rootView.findViewById(R.id.txt_office);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICKER_RESULT && resultCode == Activity.RESULT_OK && null != data) {
            Uri contactUri = data.getData();
            Cursor contactCursor = Objects.requireNonNull(getActivity()).getContentResolver().query(Objects.requireNonNull(contactUri), new String[]{ContactsContract.Contacts._ID}, null, null, null);
            String id = null;
            if (Objects.requireNonNull(contactCursor).moveToFirst()) {
                id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
            }
            contactCursor.close();
            String phoneNumber;
            Cursor phoneCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ", new String[]{id}, null);
            if (Objects.requireNonNull(phoneCursor).moveToFirst()) {
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.v("phoneNumber :", "" + phoneNumber);
                if (phoneNumber != null) {
                    edtOMobile.setText(phoneNumber.replace("+", ""));
                }
            }
            phoneCursor.close();
        }
    }

    private void alert(String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void DisableAll() {
        edtOccupation.setKeyListener(null);
        edtOccupation.setCursorVisible(false);

        edtWork.setKeyListener(null);
        edtWork.setCursorVisible(false);

        edtOMobile.setKeyListener(null);
        edtOMobile.setCursorVisible(false);

        edtOAddress.setKeyListener(null);
        edtOAddress.setCursorVisible(false);
    }

    private void EnableAll() {
        edtOccupation.setEnabled(true);
        edtWork.setEnabled(true);
        edtOAddress.setEnabled(true);
    }

    @SuppressLint("SetTextI18n")
    private void SetOfflineData(ListProfileData mListProfileData) {
        edtOccupation.setText(Objects.requireNonNull(mListProfileData).getOccupation());
        edtWork.setText(mListProfileData.getWork());
        edtOMobile.setText(mListProfileData.getOffice_mobile());
        edtOAddress.setText(mListProfileData.getOffice_address());

        if (!mListProfileData.getOffice_lat().equalsIgnoreCase("null") && !mListProfileData.getOffice_lat().equalsIgnoreCase("")) {
            office_lat = Double.parseDouble(mListProfileData.getOffice_lat());
        }
        if (!mListProfileData.getOffice_lng().equalsIgnoreCase("null") && !mListProfileData.getOffice_lng().equalsIgnoreCase("")) {
            office_lng = Double.parseDouble(mListProfileData.getOffice_lng());
        }
        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            if (office_lat != 0 && office_lng != 0) {
                final String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
                final String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
                // double lat = Double.valueOf(curr_lat);
                //double lng = Double.valueOf(curr_lng);

                new Common.getDistance(getActivity(), txt_office).execute(curr_lat, curr_lng, String.valueOf(office_lat), String.valueOf(office_lng));

               /* int distance = (int) Common.getDistance(mActivity, office_lat, office_lng);
                if (distance == -1) {
                    txt_office.setText(R.string.enable_location);
                } else {
                    txt_office.setText("Approx " + (distance + 3) + getString(R.string.km)); //distance / 1000
                }*/
            } else {
                txt_office.setText(R.string.no_set_location);
            }
        }

        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
            EnableAll();
        } else {
            DisableAll();
        }
    }
}
