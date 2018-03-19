package com.krs.vastipatrak.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.RoundedCornersTransformation;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public HashMap<Integer, Boolean> checkboxMap;
    Context _context;
    ArrayList<ListParentData> _listDataHeader = null;
    HashMap<ListParentData, List<ListChildData>> _listDataChild = null;
    ProgressDialog pDialog;
    String TAG = "ExpandableListAdapter";
    String tag_json_obj = "jobj_req";
    SharedPreferences mSharedPreferences = null;
    SharedPreferences.Editor mEditor;
    String[] SPINNERLIST = {"Father", "Son", "Daughter", "Brother", "Sister", "Grandfather", "Grandson", "Uncle", "Uncle's Son", "Uncle in low", "Uncle's Son"};
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    public ExpandableListAdapter(Context context, ArrayList<ListParentData> listDataHeader, HashMap<ListParentData, List<ListChildData>> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        pDialog = new ProgressDialog(_context);
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(true);
        mSharedPreferences = _context.getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        checkboxMap = new HashMap<Integer, Boolean>();
        //check_string_array = new String[_listDataHeader.size()];
        popolaCheckMap(_listDataHeader.size());


    }


    private void openImageDialog(String name, String url) {
        Dialog dialog = new Dialog(_context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle(name);

        ImageView image = dialog.findViewById(R.id.img_dialog);
        Glide.with(_context).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(image);

        //  new Common.ImageLoadTask(url,image).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        dialog.show();
    }

    public void popolaCheckMap(int len) {

        //   SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);
        // String buffer = null;

        for (int i = 0; i < len; i++) {
            //buffer = settings.getString(String.valueOf((int)i),"false");
            // if(buffer.equals("false"))
            checkboxMap.put(i, false);
            // else checkboxMap.put((long)i, true);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ListChildData mListChildData = (ListChildData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.txt_blood = convertView.findViewById(R.id.txt_blood);
            childViewHolder.txt_gender = convertView.findViewById(R.id.txt_gender);
            childViewHolder.txt_gotra = convertView.findViewById(R.id.txt_gotra);
            childViewHolder.txt_bdate = convertView.findViewById(R.id.txt_bdate);
            childViewHolder.txt_btime = convertView.findViewById(R.id.txt_btime);
            childViewHolder.txt_bplace = convertView.findViewById(R.id.txt_bplace);
            childViewHolder.txt_details = convertView.findViewById(R.id.txt_details);
            childViewHolder.txt_address = convertView.findViewById(R.id.txt_address);
            childViewHolder.txt_native = convertView.findViewById(R.id.txt_native);
            childViewHolder.txt_mobile = convertView.findViewById(R.id.txt_mobile);
            childViewHolder.txt_phone = convertView.findViewById(R.id.txt_phone);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        final String id = mListChildData.getID();
        String address = mListChildData.getAddress();
        String birth_date = mListChildData.getbirth_date();
        String birth_place = mListChildData.getBirth_place();
        String birth_time = mListChildData.getbirth_time();
        String blood_group = mListChildData.getBlood_Group();
        String gender = mListChildData.getGender();
        String gotra = mListChildData.getGotra();
        String mobile = mListChildData.getMobile().trim().replaceAll("\\?", "").replaceAll("\\+", "");
        String str_native = mListChildData.getNative();
        String phone = mListChildData.getPhone().trim().replaceAll("\\?", "").replaceAll("\\+", "");


        childViewHolder.txt_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    boolean flag = true;
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Common.canCallPhone(_context)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        String mobile = childViewHolder.txt_mobile.getText().toString().replaceAll("-", "");
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:+" + mobile.trim()));
                        Activity activity = (Activity) _context;
                        activity.startActivity(callIntent);
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }


            }
        });

        childViewHolder.txt_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    boolean flag = true;
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Common.canCallPhone(_context)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        String phone_no = childViewHolder.txt_phone.getText().toString().replaceAll("-", "");
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:+" + phone_no.trim()));
                        Activity activity = (Activity) _context;
                        activity.startActivity(callIntent);
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        });

        String styledText = "<font color='blue'> Details </font>";
        childViewHolder.txt_details.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

        childViewHolder.txt_address.setText(Common.camelCase(address));
        childViewHolder.txt_native.setText(Common.camelCase(str_native));
        childViewHolder.txt_mobile.setText(mobile);

        styledText = "<u><font color='blue'>" + mobile + "</font></u>";
        childViewHolder.txt_mobile.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

        childViewHolder.txt_phone.setText(phone);
        styledText = "<u><font color='blue'>" + phone + "</font></u>";
        childViewHolder.txt_phone.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        childViewHolder.txt_blood.setText(blood_group);
        if (gender.equalsIgnoreCase("1") || gender.equalsIgnoreCase("")) {
            gender = "Male";
        } else {
            gender = "Female";
        }
        childViewHolder.txt_gender.setText(gender);
        childViewHolder.txt_gotra.setText(Common.camelCase(gotra));
        childViewHolder.txt_bdate.setText(birth_date);
        childViewHolder.txt_btime.setText(birth_time);
        childViewHolder.txt_bplace.setText(Common.camelCase(birth_place));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.commit();
                Intent mIntent = new Intent(_context, MyProfileActivity.class);
                _context.startActivity(mIntent);
                // mIntent.putExtra(Common.Constant_Class.DATA, str_id);
                // callProfileWS(id);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ListParentData mListParentData = (ListParentData) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.ivIcon = convertView.findViewById(R.id.ivIcon);
            groupViewHolder.tvName = convertView.findViewById(R.id.tvName);
            groupViewHolder.imgShare = convertView.findViewById(R.id.imgShare);
            groupViewHolder.tvFatherName = convertView.findViewById(R.id.tvFatherName);
            groupViewHolder.tvMotherName = convertView.findViewById(R.id.tvMotherName);
            groupViewHolder.checkbox = convertView.findViewById(R.id.cbx1);
            groupViewHolder.imgSync = convertView.findViewById(R.id.imgSync);
            groupViewHolder.tvNudge = convertView.findViewById(R.id.tvNudge);
            groupViewHolder.tvROR = convertView.findViewById(R.id.tvROR);

            groupViewHolder.tvUpdatedTime = convertView.findViewById(R.id.tvUpdatedTime);
            groupViewHolder.txt_distance = convertView.findViewById(R.id.txt_distance);
            groupViewHolder.tvCity = convertView.findViewById(R.id.tvCity);
            groupViewHolder.img_home_loc = convertView.findViewById(R.id.img_home_loc);
            groupViewHolder.img_user_loc = convertView.findViewById(R.id.img_user_loc);


            if (AppController.isAdmin) {
                groupViewHolder.checkbox.setVisibility(View.VISIBLE);
            } else {
                groupViewHolder.checkbox.setVisibility(View.GONE);
            }
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        final String id = mListParentData.getId();
        final String imgURL = mListParentData.getProfilePicUrl();
        final String Name = mListParentData.getName();
        String FatherName = mListParentData.getFatherName();
        String MotherName = mListParentData.getMotherName();
        String city = mListParentData.getCity();

//        int status = Integer.parseInt(mListParentData.getStatus().toString());
        //Glide.with(_context).load(imgURL).apply(RequestOptions.circleCropTransform().encodeQuality(100)).thumbnail(1f).into(groupViewHolder.ivIcon);

        // Rounded corners
        Glide.with(_context).load(imgURL)
                .apply(RequestOptions.bitmapTransform(
                        new RoundedCornersTransformation(_context, Common.Constant_Class.sCorner, Common.Constant_Class.sMargin, Common.Constant_Class.sColor, Common.Constant_Class.sBorder))).into(groupViewHolder.ivIcon);

        //   new Common.ImageLoadTask(imgURL, groupViewHolder.ivIcon).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        groupViewHolder.tvCity.setText(Common.camelCase(city));
        groupViewHolder.tvName.setText(Common.camelCase(Name));
        groupViewHolder.tvFatherName.setText(Common.camelCase(FatherName));
        groupViewHolder.tvMotherName.setText(Common.camelCase(MotherName));

        int group_id = (int) getGroupId(groupPosition);
        CheckListener checkL = new CheckListener();
        groupViewHolder.checkbox.setOnCheckedChangeListener(checkL);
        checkL.setPosition(group_id);
        groupViewHolder.checkbox.setFocusable(false);

        groupViewHolder.checkbox.setChecked(checkboxMap.get(group_id));

        groupViewHolder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageDialog(Name, imgURL);
            }
        });
        String updated_time = mListParentData.getUpdated_time();
        groupViewHolder.tvUpdatedTime.setText("Updated: " + Common.getUpdatedTime(updated_time));

        final String user_lat = mListParentData.getUser_lat();
        final String user_lng = mListParentData.getUser_lng();
        final String home_lat = mListParentData.getHome_lat();
        final String home_lng = mListParentData.getHome_lng();

        if (user_lat != null && user_lng != null && !user_lat.isEmpty() && !user_lng.isEmpty() && !user_lat.equalsIgnoreCase("null") && !user_lng.equalsIgnoreCase("null")) {
            groupViewHolder.txt_distance.setText("" + Common.getDistance((Activity) _context, Double.parseDouble(user_lat), Double.parseDouble(user_lng)) + " Km");
            groupViewHolder.txt_distance.setVisibility(View.VISIBLE);
            if (mListParentData.isIs_location_enable()) {
                groupViewHolder.txt_distance.setTextColor(_context.getResources().getColor(R.color.colorPrimary));
            } else {
                groupViewHolder.txt_distance.setTextColor(_context.getResources().getColor(R.color.navigationBarColor));
            }
            groupViewHolder.img_user_loc.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.img_user_loc.setVisibility(View.GONE);
            groupViewHolder.txt_distance.setVisibility(View.GONE);
        }

        if (home_lat != null && home_lng != null && !home_lat.isEmpty() && !home_lng.isEmpty() && !home_lat.equalsIgnoreCase("null") && !home_lng.equalsIgnoreCase("null")) {

            groupViewHolder.img_home_loc.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.img_home_loc.setVisibility(View.GONE);
        }

        groupViewHolder.tvROR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog relation_dialog = new Dialog(_context);
                relation_dialog.setTitle("Request of relation");
                relation_dialog.setContentView(R.layout.custom_relation_dialog);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
                MaterialBetterSpinner relation_spinner = relation_dialog.findViewById(R.id.relation_spinner);
                relation_spinner.setAdapter(arrayAdapter);
                Button btnSend = relation_dialog.findViewById(R.id.btnSend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(_context, "Request Sent Successfully!", Toast.LENGTH_SHORT).show();
                        relation_dialog.cancel();
                    }
                });
                relation_dialog.show();
            }
        });

        groupViewHolder.tvNudge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.SendWhatsappMessage(_context,mListParentData.getMobile(),"Hi");
            }
        });



        groupViewHolder.imgSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncUser(mListParentData.getId());
            }
        });

        groupViewHolder.img_user_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.lat != null && MainActivity.lon != null) {
                    Common.showDirections((Activity) _context, Double.parseDouble(user_lat), Double.parseDouble(user_lng), "");
                    Toast.makeText(_context, "distance between you and " + Name, Toast.LENGTH_LONG).show();
                }
            }
        });

        groupViewHolder.img_home_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.lat != null && MainActivity.lon != null) {
                    Common.showDirections((Activity) _context, Double.parseDouble(home_lat), Double.parseDouble(home_lng), "");
                    Toast.makeText(_context, "distance between your home and " + Name + " home", Toast.LENGTH_SHORT).show();
                }
            }
        });

        groupViewHolder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareBody = "",name="";
                RealmList<ListProfileData> mListProfileData1 = Common.getDataFromParentTable(id, 3);
                if (mListProfileData1.size() > 0) {
                    ListProfileData mListProfileData = mListProfileData1.get(0);
                    String first_name = mListProfileData.getFirst_name();
                    String last_name = mListProfileData.getLast_name();
                    String father_name = mListProfileData.getFather_name();
                    String mother_name = mListProfileData.getMother_name();
                    String birth_date = mListProfileData.getBirth_date();
                    String birth_time = mListProfileData.getBirth_time();
                    String birthPlace = mListProfileData.getBirth_place();
                    String email_address = mListProfileData.getEmail_address();
                    String address = mListProfileData.getAddress();
                    String office_address = mListProfileData.getOffice_address();
                    String phone = mListProfileData.getPhone().toString().trim().replaceAll("\\?", "").replaceAll("\\+", "");
                    String mobile = mListProfileData.getMobile().toString().trim().replaceAll("\\?", "").replaceAll("\\+", "");
                    String office_mobile = mListProfileData.getOffice_mobile();
                    name=first_name + " " + last_name;
                    shareBody = " Name :" + name + "\n"
                            + " Father Name :" + father_name + "\n"
                            + " Mother Name :" + mother_name + "\n"
                            + " Mobile :" + mobile + "\n"
                            + " Phone :" + phone + "\n"
                            + " Address :" + address + "\n"
                            + " Email Address :" + email_address + "\n"
                            + " Office Mobile :" + office_mobile + "\n"
                            + " Office Address :" + office_address + "\n"
                            + " Birth Date :" + birth_date + "\n"
                            + " Birth Time :" + birth_time + "\n"
                            + " Birth Place :" + birthPlace + "\n";
                }


                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, name+" details");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                _context.startActivity(Intent.createChooser(sharingIntent, _context.getResources().getString(R.string.share_using)));
            }
        });
        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

/*    private void alert(final String id, String message, final boolean isChecked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(_context.getString(R.string.app_name));

        builder.setMessage(message);
        builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                callStatusChangeWS(id, isChecked);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }*/

    private void showProgressDialog() {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void SyncUser(String profile_id) {
        if (Common.isOnline(_context)) {
            showProgressDialog();

            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.PROFILE_ID, mSharedPreferences.getString(Common.Constant_Class.PROFILE_ID, profile_id));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sync_url = Common.Constant_Class.SYNC_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    hideProgressDialog();
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                Common.SaveProfile(mJsondata);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    hideProgressDialog();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(Common.Constant_Class.API_KEY, Common.Constant_Class.API_KEY_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TYPE, Common.Constant_Class.DEVICE_TYPE_VALUE);
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        }

    }

/*
    private void callProfileWS(String str_id) {

        if (!mSharedPreferences.getBoolean(Common.Constant_Class.OFFLINE_SP, false) && Common.isOnline(_context)) {

            final String profile_url = Common.Constant_Class.PROFILE_URL + str_id;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, profile_url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "profile_url: " + profile_url);
                    Log.d(TAG, "response: " + response.toString());

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            String data = response.getString(Common.Constant_Class.DATA);

                            Intent mIntent = new Intent(_context, MyProfileActivity.class);
                            mIntent.putExtra(Common.Constant_Class.DATA, data);
                            _context.startActivity(mIntent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());


                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        } else {


            Intent mIntent = new Intent(_context, MyProfileActivity.class);
            mIntent.putExtra(Common.Constant_Class.DATA, str_id);
            _context.startActivity(mIntent);
        }
    }
*/

    private class ChildViewHolder {
        TextView txt_blood;
        TextView txt_gender;
        TextView txt_gotra;
        TextView txt_bdate;
        TextView txt_btime;
        TextView txt_bplace;
        TextView txt_details;
        TextView txt_address;
        TextView txt_native;
        TextView txt_mobile;
        TextView txt_phone;
    }

    private class GroupViewHolder {
        ImageView ivIcon;
        ImageView imgShare;
        ImageView imgSync;
        ImageView img_user_loc;
        ImageView img_home_loc;
        TextView tvName;
        TextView tvFatherName;
        TextView tvMotherName;
        TextView tvNudge;
        TextView tvROR;
        TextView tvCity;
        TextView txt_distance;
        TextView tvUpdatedTime;
        //ToggleButton tbtn_pshare;
        CheckBox checkbox;
        //SwitchCompat btn_active;
    }

    public class CheckListener implements CompoundButton.OnCheckedChangeListener {

        int pos;

        public void setPosition(int p) {
            pos = p;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i("checkListenerChanged", String.valueOf(pos) + ":" + String.valueOf(isChecked));
            checkboxMap.put(pos, isChecked);
           /* if(isChecked == true) check_string_array[(int)pos] = "true";
            else				  check_string_array[(int)pos] = "false";*/
            // save checkbox state of each group
   /*         SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);
            SharedPreferences.Editor preferencesEditor = settings.edit();
            preferencesEditor.putString(String.valueOf((int)pos), check_string_array[(int)pos]);
            preferencesEditor.commit();*/
        }
    }
}
