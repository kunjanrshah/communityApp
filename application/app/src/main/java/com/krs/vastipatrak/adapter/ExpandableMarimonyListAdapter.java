package com.krs.vastipatrak.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

import io.realm.Realm;

public class ExpandableMarimonyListAdapter extends BaseExpandableListAdapter {

    private final Realm realm = AppController.getInstance().realm;
    private final Context _context;
    private final ArrayList<ListMatrimonyParentData> _listDataHeader;
    /**
     * Display detail view in Expandable list
     */
    private final HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> _listDataChild;
    @Nullable
    private ProgressDialog pDialog;
    @NonNull
    private final String TAG = ExpandableMarimonyListAdapter.class.getSimpleName();
    /**
     * stored session in SharedPreferences
     */
    private final SharedPreferences mSharedPreferences;
    @Nullable
    private final SharedPreferences.Editor mEditor;
    private ChildViewHolder childViewHolder;

    public ExpandableMarimonyListAdapter(Context context, ArrayList<ListMatrimonyParentData> listDataHeader, HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        pDialog = new ProgressDialog(_context);
        pDialog.setMessage(Common.Constant_Class.LOADING);
        pDialog.setCancelable(true);
        mSharedPreferences = _context.getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
    }


    private void openImageDialog(String name, String url) {
        Dialog dialog = new Dialog(_context);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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

    @Nullable
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, @Nullable View convertView, ViewGroup parent) {

        ListMatrimonyChildData mListMatrimonyChildData = (ListMatrimonyChildData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_matrimony_item, null);
            }
            childViewHolder = new ChildViewHolder();
            assert convertView != null;
            childViewHolder.txt_blood = convertView.findViewById(R.id.txt_blood);
            childViewHolder.txt_gotra = convertView.findViewById(R.id.txt_gotra);
            childViewHolder.txt_bdate = convertView.findViewById(R.id.txt_bdate);
            childViewHolder.txt_btime = convertView.findViewById(R.id.txt_btime);
            childViewHolder.txt_bplace = convertView.findViewById(R.id.txt_bplace);
            childViewHolder.imgDetail = convertView.findViewById(R.id.imgDetail);
            childViewHolder.txt_address = convertView.findViewById(R.id.txt_address);
            childViewHolder.txt_mobile = convertView.findViewById(R.id.txt_mobile);
            childViewHolder.imgSync = convertView.findViewById(R.id.imgSync);
            childViewHolder.imgNudge = convertView.findViewById(R.id.imgNudge);
            childViewHolder.imgDetail = convertView.findViewById(R.id.imgDetail);
            childViewHolder.img_home_loc = convertView.findViewById(R.id.img_home_loc);
            childViewHolder.ll_child_matrimony = convertView.findViewById(R.id.ll_child_matrimony);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        Common.getChildRandomColor(_context, childPosition, childViewHolder.ll_child_matrimony);
        final String id = mListMatrimonyChildData.getProfile_id();
        String address = mListMatrimonyChildData.getChild_address();
        String birth_date = mListMatrimonyChildData.getChild_birth_date();
        String birth_place = mListMatrimonyChildData.getChild_birth_place();
        String birth_time = mListMatrimonyChildData.getChild_birth_time();
        String blood_group = mListMatrimonyChildData.getChild_blood_group();
        String gotra = mListMatrimonyChildData.getChild_gotra();
        final String name = mListMatrimonyChildData.getName();
        final String mobile = mListMatrimonyChildData.getChild_mobile().trim().replaceAll("\\?", "").replaceAll("\\+", "");

        final String home_lat = mListMatrimonyChildData.getHome_lat();
        final String home_lng = mListMatrimonyChildData.getHome_lng();
        if (home_lat != null && home_lng != null && !home_lat.isEmpty() && !home_lng.isEmpty() && !home_lat.equalsIgnoreCase("null") && !home_lng.equalsIgnoreCase("null")) {
            childViewHolder.img_home_loc.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.img_home_loc.setVisibility(View.GONE);
        }
        childViewHolder.imgNudge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(_context.getString(R.string.app_name));

                builder.setMessage("Do you want to request for update ?");
                builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {

                        Common.SendWhatsappMessage(_context, mobile, _context.getResources().getString(R.string.nice_html));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        childViewHolder.imgSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "Do you want to Sync ?";

                AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(_context.getString(R.string.app_name));

                builder.setMessage(message);
                builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        SyncUser(id);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });


        childViewHolder.img_home_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = "Do you want to navigate " + name + " home ?";
                AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(_context.getString(R.string.app_name));

                builder.setMessage(message);
                builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        if (MainActivity.lat != null && MainActivity.lon != null) {
                            Common.showDirections((Activity) _context, Double.parseDouble(home_lat), Double.parseDouble(home_lng), "");
                            Toast.makeText(_context, "distance between you and " + name + "'s home", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


            }
        });

        childViewHolder.txt_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    boolean flag = true;
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (Common.canCallPhone(_context)) {
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

        childViewHolder.imgDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert mEditor != null;
                mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.apply();
                Intent mIntent = new Intent(_context, MyProfileActivity.class);
                _context.startActivity(mIntent);
            }
        });


        childViewHolder.txt_address.setText(Common.camelCase(address));
        childViewHolder.txt_mobile.setText(mobile);
        String styledText = "<u><font color='blue'>" + mobile + "</font></u>";
        childViewHolder.txt_mobile.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        childViewHolder.txt_blood.setText(blood_group);
        childViewHolder.txt_gotra.setText(Common.camelCase(gotra));
        childViewHolder.txt_bdate.setText(birth_date);
        childViewHolder.txt_btime.setText(birth_time);
        childViewHolder.txt_bplace.setText(Common.camelCase(birth_place));


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

    @Nullable
    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, @Nullable View convertView, ViewGroup parent) {
        final ListMatrimonyParentData mListMatrimonyParentData = (ListMatrimonyParentData) getGroup(groupPosition);

        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert infalInflater != null;
            convertView = infalInflater.inflate(R.layout.list_marimony_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.ivChildIcon = convertView.findViewById(R.id.ivChildIcon);
            groupViewHolder.tvChildName = convertView.findViewById(R.id.tvChildName);
            groupViewHolder.imgChildShare = convertView.findViewById(R.id.imgChildShare);
            groupViewHolder.tvChildFatherName = convertView.findViewById(R.id.tvChildFatherName);
            groupViewHolder.tvChildMotherName = convertView.findViewById(R.id.tvChildMotherName);
            groupViewHolder.tvUpdatedTime = convertView.findViewById(R.id.tvUpdatedTime);
            groupViewHolder.tvChildCity = convertView.findViewById(R.id.tvChildCity);
            groupViewHolder.imgChildGender = convertView.findViewById(R.id.imgChildGender);
            groupViewHolder.ll_parent_matrimony = convertView.findViewById(R.id.ll_parent_matrimony);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        Common.getParentRandomColor(_context, groupPosition, groupViewHolder.ll_parent_matrimony);
        final String child_id = mListMatrimonyParentData.getId();
        final String imgURL = mListMatrimonyParentData.getProfilePicUrl();
        final String Name = mListMatrimonyParentData.getName();
        String FatherName = mListMatrimonyParentData.getFatherName();
        String MotherName = mListMatrimonyParentData.getMotherName();
        String city = mListMatrimonyParentData.getCity();

        Glide.with(_context).load(imgURL).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(_context, Common.Constant_Class.sCorner, Common.Constant_Class.sMargin, Common.Constant_Class.sColor, Common.Constant_Class.sBorder))).into(groupViewHolder.ivChildIcon);
        if (mListMatrimonyParentData.getChild_gender().equalsIgnoreCase("male")) {
            groupViewHolder.imgChildGender.setBackgroundResource(R.drawable.boy);
        } else {
            groupViewHolder.imgChildGender.setBackgroundResource(R.drawable.woman);
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
            }
        });
        return convertView;
    }


    private void shareImage(Bitmap bitmap, String text) {
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
                public void onResponse(@NonNull JSONObject response) {
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
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    hideProgressDialog();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
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
        TextView txt_address;
        TextView txt_mobile;
        LinearLayout ll_child_matrimony;
        ImageView img_home_loc;
        ImageView imgNudge;
        ImageView imgDetail;
        ImageView imgSync;
    }

    private class GroupViewHolder {
        ImageView ivChildIcon;
        ImageView imgChildShare;
        ImageView imgChildGender;
        TextView tvChildName;
        TextView tvChildFatherName;
        TextView tvChildMotherName;
        TextView tvChildCity;
        TextView tvUpdatedTime;
        LinearLayout ll_parent_matrimony;
    }


}
