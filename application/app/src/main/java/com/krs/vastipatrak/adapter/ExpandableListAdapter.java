package com.krs.vastipatrak.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;
    //String[] check_string_array;

    public ExpandableListAdapter(Context context, ArrayList<ListParentData> listDataHeader, HashMap<ListParentData, List<ListChildData>> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        pDialog = new ProgressDialog(_context);
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(true);
        mSharedPreferences = _context.getSharedPreferences(Common.Constant_Class.PREFERENCE_NAME, Context.MODE_PRIVATE);
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

        RoundedImageView image = (RoundedImageView) dialog.findViewById(R.id.img_dialog);
        Glide.with(_context).load(url).thumbnail(0.5f).into(image);

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
            childViewHolder.txt_blood = (TextView) convertView.findViewById(R.id.txt_blood);
            childViewHolder.txt_gender = (TextView) convertView.findViewById(R.id.txt_gender);
            childViewHolder.txt_gotra = (TextView) convertView.findViewById(R.id.txt_gotra);
            childViewHolder.txt_bdate = (TextView) convertView.findViewById(R.id.txt_bdate);
            childViewHolder.txt_btime = (TextView) convertView.findViewById(R.id.txt_btime);
            childViewHolder.txt_bplace = (TextView) convertView.findViewById(R.id.txt_bplace);
            childViewHolder.txt_details = (TextView) convertView.findViewById(R.id.txt_details);
            childViewHolder.txt_address = (TextView) convertView.findViewById(R.id.txt_address);
            childViewHolder.txt_native = (TextView) convertView.findViewById(R.id.txt_native);
            childViewHolder.txt_mobile = (TextView) convertView.findViewById(R.id.txt_mobile);
            childViewHolder.txt_phone = (TextView) convertView.findViewById(R.id.txt_phone);
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

        childViewHolder.txt_address.setText(address);
        childViewHolder.txt_native.setText(str_native);
        childViewHolder.txt_mobile.setText(mobile);

        styledText = "<u><font color='blue'>" + mobile + "</font></u>";
        childViewHolder.txt_mobile.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

        childViewHolder.txt_phone.setText(phone);
        styledText = "<u><font color='blue'>" + phone + "</font></u>";
        childViewHolder.txt_phone.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        childViewHolder.txt_blood.setText(blood_group);
        if (gender.equalsIgnoreCase("1")) {
            gender = "Male";
        } else {
            gender = "Female";
        }
        childViewHolder.txt_gender.setText(gender);
        childViewHolder.txt_gotra.setText(gotra);
        childViewHolder.txt_bdate.setText(birth_date);
        childViewHolder.txt_btime.setText(birth_time);
        childViewHolder.txt_bplace.setText(birth_place);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Common.Constant_Class.PROFILE_ID_SP, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.commit();
                callProfileWS(id);
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
        ListParentData mListParentData = (ListParentData) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.ivIcon = (RoundedImageView) convertView.findViewById(R.id.ivIcon);


            if (mSharedPreferences.getBoolean(Common.Constant_Class.OFFLINE_SP, false)) {
                groupViewHolder.ivIcon.setVisibility(View.GONE);
            } else {
                groupViewHolder.ivIcon.setVisibility(View.VISIBLE);
            }

            groupViewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            groupViewHolder.imgShare = (ImageView) convertView.findViewById(R.id.imgShare);
            groupViewHolder.tvFatherName = (TextView) convertView.findViewById(R.id.tvFatherName);
            groupViewHolder.tvMotherName = (TextView) convertView.findViewById(R.id.tvMotherName);
            groupViewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.cbx1);
            groupViewHolder.tbtn_pshare = (ToggleButton) convertView.findViewById(R.id.tbtn_pshare);
            groupViewHolder.tbtn_pshare.setText(null);
            groupViewHolder.tbtn_pshare.setTextOn(null);
            groupViewHolder.tbtn_pshare.setTextOff(null);


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
//        int status = Integer.parseInt(mListParentData.getStatus().toString());
        Glide.with(_context).load(imgURL).thumbnail(1f).into(groupViewHolder.ivIcon);

        //   new Common.ImageLoadTask(imgURL, groupViewHolder.ivIcon).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        groupViewHolder.tvName.setText(Name);
        groupViewHolder.tvFatherName.setText(FatherName);
        groupViewHolder.tvMotherName.setText(MotherName);

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

        groupViewHolder.txt_distance = convertView.findViewById(R.id.txt_distance);
        final String user_lat = mListParentData.getUser_lat();
        final String user_lng = mListParentData.getUser_lng();
        if (user_lat!=null && user_lng!=null && !user_lat.isEmpty() && !user_lat.equalsIgnoreCase("null") && !user_lng.isEmpty() && !user_lng.equalsIgnoreCase("null")) {
            groupViewHolder.txt_distance.setText("approx. "+Common.getDistance((Activity) _context, Double.parseDouble(user_lat), Double.parseDouble(user_lng)) + " Km");
            groupViewHolder.txt_distance.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.txt_distance.setVisibility(View.GONE);
        }


        groupViewHolder.txt_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.lat != null && MainActivity.lon != null) {
                    Common.showDirections((Activity) _context, Double.parseDouble(user_lat), Double.parseDouble(user_lng), "");
                }
            }
        });

        groupViewHolder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareBody = "";
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
                    shareBody = " Name :" + first_name + " " + last_name + "\n"
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
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Profile details");
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

    private void alert(final String id, String message, final boolean isChecked) {
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
    }

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

    private void callStatusChangeWS(String id, boolean isChecked) {
        if (Common.isOnline(_context)) {
            showProgressDialog();
            JSONObject mJsonObject = new JSONObject();

            try {
                if (isChecked) {
                    mJsonObject.put(Common.Constant_Class.STATUS, "1");
                } else {
                    mJsonObject.put(Common.Constant_Class.STATUS, "0");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String search_url = Common.Constant_Class.STATUS_URL + id;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, search_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    hideProgressDialog();
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            //   notifyDataSetChanged();
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
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        }

    }

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
        RoundedImageView ivIcon;
        TextView tvName;
        TextView tvFatherName;
        TextView tvMotherName;
        CheckBox checkbox;
        ImageView imgShare;
        ToggleButton tbtn_pshare;
        TextView txt_distance;
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
