package com.krs.vastipatrak.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListMatrimonyChildData;
import com.krs.vastipatrak.model.ListMatrimonyParentData;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.RoundedCornersTransformation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class ExpandableMarimonyListAdapter extends BaseExpandableListAdapter {

    Realm realm = AppController.getInstance().realm;
    private Context _context;
    private ArrayList<ListMatrimonyParentData> _listDataHeader = null;
    private HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> _listDataChild = null;
    private ProgressDialog pDialog;
    private String TAG = "ExpandableMarimonyListAdapter";
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    public ExpandableMarimonyListAdapter(Context context, ArrayList<ListMatrimonyParentData> listDataHeader, HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        pDialog = new ProgressDialog(_context);
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(true);
        mSharedPreferences = _context.getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }


    private void openImageDialog(String name, String url) {
        Dialog dialog = new Dialog(_context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle(name);
        ImageView image = dialog.findViewById(R.id.img_dialog);
        Glide.with(_context).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(image);
        dialog.show();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ListMatrimonyChildData mListMatrimonyChildData = (ListMatrimonyChildData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_matrimony_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.txt_blood = convertView.findViewById(R.id.txt_blood);
            childViewHolder.txt_gotra = convertView.findViewById(R.id.txt_gotra);
            childViewHolder.txt_bdate = convertView.findViewById(R.id.txt_bdate);
            childViewHolder.txt_btime = convertView.findViewById(R.id.txt_btime);
            childViewHolder.txt_bplace = convertView.findViewById(R.id.txt_bplace);
            childViewHolder.txt_details = convertView.findViewById(R.id.txt_details);
            childViewHolder.txt_address = convertView.findViewById(R.id.txt_address);
            childViewHolder.txt_mobile = convertView.findViewById(R.id.txt_mobile);

            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        final String id = mListMatrimonyChildData.getProfile_id();
        String address = mListMatrimonyChildData.getChild_address();
        String birth_date = mListMatrimonyChildData.getChild_birth_date();
        String birth_place = mListMatrimonyChildData.getChild_birth_place();
        String birth_time = mListMatrimonyChildData.getChild_birth_time();
        String blood_group = mListMatrimonyChildData.getChild_blood_group();
        String gotra = mListMatrimonyChildData.getChild_gotra();
        String mobile = mListMatrimonyChildData.getChild_mobile().trim().replaceAll("\\?", "").replaceAll("\\+", "");

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

        String styledText = "<font color='blue'> Details </font>";
        childViewHolder.txt_details.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        childViewHolder.txt_address.setText(Common.camelCase(address));
        childViewHolder.txt_mobile.setText(mobile);
        styledText = "<u><font color='blue'>" + mobile + "</font></u>";
        childViewHolder.txt_mobile.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        childViewHolder.txt_blood.setText(blood_group);
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
        final ListMatrimonyParentData mListMatrimonyParentData = (ListMatrimonyParentData) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_marimony_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.ivChildIcon = convertView.findViewById(R.id.ivChildIcon);
            groupViewHolder.tvChildName = convertView.findViewById(R.id.tvChildName);
            groupViewHolder.imgChildShare = convertView.findViewById(R.id.imgChildShare);
            groupViewHolder.tvChildFatherName = convertView.findViewById(R.id.tvChildFatherName);
            groupViewHolder.tvChildMotherName = convertView.findViewById(R.id.tvChildMotherName);
            groupViewHolder.imgSync = convertView.findViewById(R.id.imgSync);
            groupViewHolder.tvNudge = convertView.findViewById(R.id.tvNudge);
            groupViewHolder.tvUpdatedTime = convertView.findViewById(R.id.tvUpdatedTime);
            groupViewHolder.tvChildCity = convertView.findViewById(R.id.tvChildCity);
            groupViewHolder.img_home_loc = convertView.findViewById(R.id.img_home_loc);
            groupViewHolder.imgChildGender = convertView.findViewById(R.id.imgChildGender);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        final String child_id = mListMatrimonyParentData.getId();
        final String child_profile_id = mListMatrimonyParentData.getProfile_id();
        final String imgURL = mListMatrimonyParentData.getProfilePicUrl();
        final String Name = mListMatrimonyParentData.getName();
        String FatherName = mListMatrimonyParentData.getFatherName();
        String MotherName = mListMatrimonyParentData.getMotherName();
        String city = mListMatrimonyParentData.getCity();

        Glide.with(_context).load(imgURL)
                .apply(RequestOptions.bitmapTransform(
                        new RoundedCornersTransformation(_context, Common.Constant_Class.sCorner, Common.Constant_Class.sMargin, Common.Constant_Class.sColor, Common.Constant_Class.sBorder))).into(groupViewHolder.ivChildIcon);
        if (mListMatrimonyParentData.getChild_gender().toString().equalsIgnoreCase("male")) {
            groupViewHolder.imgChildGender.setBackgroundResource(R.drawable.boy);
        } else {
            groupViewHolder.imgChildGender.setBackgroundResource(R.drawable.girl);
        }
        groupViewHolder.tvChildCity.setText(Common.camelCase(city));
        groupViewHolder.tvChildName.setText(Common.camelCase(Name));
        groupViewHolder.tvChildFatherName.setText(Common.camelCase(FatherName));
        groupViewHolder.tvChildMotherName.setText(Common.camelCase(MotherName));

        //  int group_id = (int) getGroupId(groupPosition);
        groupViewHolder.ivChildIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageDialog(Name, imgURL);
            }
        });
        String updated_time = mListMatrimonyParentData.getUpdated_time();
        groupViewHolder.tvUpdatedTime.setText("Updated: " + Common.getUpdatedTime(updated_time));

        final String home_lat = mListMatrimonyParentData.getHome_lat();
        final String home_lng = mListMatrimonyParentData.getHome_lng();
        if (home_lat != null && home_lng != null && !home_lat.isEmpty() && !home_lng.isEmpty() && !home_lat.equalsIgnoreCase("null") && !home_lng.equalsIgnoreCase("null")) {
            groupViewHolder.img_home_loc.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.img_home_loc.setVisibility(View.GONE);
        }
        groupViewHolder.tvNudge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(_context, "Request of Update!", Toast.LENGTH_SHORT).show();
            }
        });

        groupViewHolder.imgSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncUser(mListMatrimonyParentData.getProfile_id());
            }
        });


        groupViewHolder.img_home_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.lat != null && MainActivity.lon != null) {
                    Common.showDirections((Activity) _context, Double.parseDouble(home_lat), Double.parseDouble(home_lng), "");
                    Toast.makeText(_context, "distance between you and " + Name + "'s home", Toast.LENGTH_SHORT).show();
                }
            }
        });

        groupViewHolder.imgChildShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListChildrenData childData = realm.where(ListChildrenData.class).equalTo(Common.Constant_Class.CHILD_ID, child_id).findFirst();
                String name="";
                Bitmap bitmap=null;
                if (childData != null) {
                    name = childData.getChild_name();
                    String id=childData.getProfile_id();
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        bitmap = barcodeEncoder.createBitmap(bitMatrix);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                shareImage(bitmap,name);
                /*String shareBody = "", child_name = "";
                if (childData != null && profileData != null) {

                    child_name = childData.getChild_name();
                    String father_name = profileData.getFirst_name() + " " + profileData.getLast_name();
                    String mother_name = profileData.getSpouse_name();
                    String birth_date = childData.getChild_bday();
                    String birth_time = childData.getBirth_time();
                    String birthPlace = childData.getBirth_place();
                    String address = profileData.getAddress();
                    String gotra = profileData.getGotra();
                    String blood = profileData.getBlood_group();
                    String city = profileData.getCity();
                    String mobile = profileData.getMobile().toString().trim().replaceAll("\\?", "").replaceAll("\\+", "");

                    shareBody = " Name :" + child_name + "\n"
                            + " Father Name :" + father_name + "\n"
                            + " Mother Name :" + mother_name + "\n"
                            + " Gotra :" + gotra + "\n"
                            + " Birth Date :" + birth_date + "\n"
                            + " Birth Time :" + birth_time + "\n"
                            + " Birth Place :" + birthPlace + "\n"
                            + " Blood :" + blood + "\n"
                            + " Mobile :" + mobile + "\n"
                            + " Address :" + address + "\n"
                            + " City :" + city + "\n";
                }*/


/*                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "" + child_name + " details");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                _context.startActivity(Intent.createChooser(sharingIntent, _context.getResources().getString(R.string.share_using)));*/
            }
        });
        return convertView;
    }


    void shareImage(Bitmap bitmap, String text){
        String pathofBmp= MediaStore.Images.Media.insertImage(_context.getContentResolver(), bitmap,"title", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text+"'s Father Profile QR Code");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text+"'s Father Profile QR Code");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        _context.startActivity(Intent.createChooser(shareIntent, "Vastipatrak"));
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
                                notifyDataSetChanged();
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
                    params.put(Common.Constant_Class.DEVICE_ID, Common.Constant_Class.DEVICE_ID_VALUE);
                    params.put(Common.Constant_Class.DEVICE_TOKEN,mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN,""));
                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    private class ChildViewHolder {
        TextView txt_blood;
        TextView txt_gotra;
        TextView txt_bdate;
        TextView txt_btime;
        TextView txt_bplace;
        TextView txt_details;
        TextView txt_address;
        TextView txt_mobile;
    }

    private class GroupViewHolder {
        ImageView ivChildIcon;
        ImageView imgChildShare;
        ImageView imgSync;
        ImageView img_home_loc;
        ImageView imgChildGender;
        TextView tvNudge;
        TextView tvChildName;
        TextView tvChildFatherName;
        TextView tvChildMotherName;
        TextView tvChildCity;
        TextView tvUpdatedTime;
    }
}
