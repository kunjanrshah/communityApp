package com.krs.vastipatrak.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.activity.ProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListMatrimonyChildData;
import com.krs.vastipatrak.model.ListMatrimonyParentData;
import com.krs.vastipatrak.model.MatrimonyProfileData;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.realm.Realm;

import static com.krs.vastipatrak.utils.Utility.hideProgressDialog;

public class ExpandableMarimonyListAdapter extends BaseExpandableListAdapter {

    private final Realm realm = AppController.getInstance().realm;
    private final Context _context;
    private final ArrayList<ListMatrimonyParentData> _listDataHeader;
    /**
     * Display detail view in Expandable list
     */
    private final HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> _listDataChild;
    @NonNull
    private final String TAG = ExpandableMarimonyListAdapter.class.getSimpleName();
    /**
     * stored session in SharedPreferences
     */
    private final SharedPreferences mSharedPreferences;
    @Nullable
    private final SharedPreferences.Editor mEditor;
    @Nullable
    //private ProgressDialog pDialog;
    private ChildViewHolder childViewHolder;

    public ExpandableMarimonyListAdapter(Context context, ArrayList<ListMatrimonyParentData> listDataHeader, HashMap<ListMatrimonyParentData, List<ListMatrimonyChildData>> listDataChild) {
        this._context = context;

        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;

        mSharedPreferences = _context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
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
        Utility.getChildRandomColor(_context, childPosition, childViewHolder.ll_child_matrimony);
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

                builder.setMessage(_context.getResources().getString(R.string.go_to_whatsapp));
                builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {

                        Utility.SendWhatsappMessage(_context, mobile, _context.getResources().getString(R.string.nice_html));
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

                        final String curr_lat = mSharedPreferences.getString(AppConstants.CURR_LAT, "");
                        final String curr_lng = mSharedPreferences.getString(AppConstants.CURR_LNG, "");
                        double lat = Double.valueOf(curr_lat);
                        double lng = Double.valueOf(curr_lng);
                        Utility.showDirections((Activity) _context, lat, lng, Double.parseDouble(home_lat), Double.parseDouble(home_lng), "");
                        Toast.makeText(_context, "distance between you and " + name + "'s home", Toast.LENGTH_SHORT).show();
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
                        if (Utility.canCallPhone(_context)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        String mobile = childViewHolder.txt_mobile.getText().toString().replaceAll("-", "");
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + mobile.trim()));
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
                mEditor.putString(AppConstants.PROFILE_ID, id);
                mEditor.putBoolean(AppConstants.MYPROFILE_SP, false);
                mEditor.apply();
                ProfileActivity.isEnable = false;
                Intent mIntent = new Intent(_context, ProfileActivity.class);
                _context.startActivity(mIntent);
            }
        });


        childViewHolder.txt_address.setText(Utility.camelCase(address));
        childViewHolder.txt_mobile.setText(mobile);
        String styledText = "<u><font color='blue'>" + mobile + "</font></u>";
        childViewHolder.txt_mobile.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        childViewHolder.txt_blood.setText(blood_group);
        childViewHolder.txt_gotra.setText(Utility.camelCase(gotra));
        childViewHolder.txt_bdate.setText(birth_date);
        childViewHolder.txt_btime.setText(birth_time);
        childViewHolder.txt_bplace.setText(Utility.camelCase(birth_place));


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
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

        Utility.getParentRandomColor(_context, groupPosition, groupViewHolder.ll_parent_matrimony);
        final String child_id = mListMatrimonyParentData.getId();
        final String imgURL = mListMatrimonyParentData.getProfilePicUrl();
        final String Name = mListMatrimonyParentData.getName();
        String FatherName = mListMatrimonyParentData.getFatherName();
        String MotherName = mListMatrimonyParentData.getMotherName();
        String city = mListMatrimonyParentData.getCity();

       // Glide.with(_context).load(imgURL).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(_context, AppConstants.sCorner, AppConstants.sMargin, AppConstants.sColor, AppConstants.sBorder))).into(groupViewHolder.ivChildIcon);
        Glide.with(_context).load(imgURL).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(groupViewHolder.ivChildIcon);

        if (mListMatrimonyParentData.getChild_gender().equalsIgnoreCase("male")) {
            groupViewHolder.imgChildGender.setBackgroundResource(R.drawable.boy);
        } else {
            groupViewHolder.imgChildGender.setBackgroundResource(R.drawable.woman);
        }
        groupViewHolder.tvChildCity.setText(Utility.camelCase(city));
        groupViewHolder.tvChildName.setText(Utility.camelCase(Name));
        groupViewHolder.tvChildFatherName.setText(Utility.camelCase(FatherName));
        groupViewHolder.tvChildMotherName.setText(Utility.camelCase(MotherName));

        //  int group_id = (int) getGroupId(groupPosition);
        groupViewHolder.ivChildIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageDialog(Name, imgURL);
            }
        });
        String updated_time = mListMatrimonyParentData.getUpdated_time();
        groupViewHolder.tvUpdatedTime.setText("Updated: " + Utility.getUpdatedTime(updated_time));

        groupViewHolder.imgChildShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(_context);
                dialog.setContentView(R.layout.custom_share_dialog);
                dialog.setTitle(_context.getString(R.string.app_name));
                final RadioButton radio_qr = dialog.findViewById(R.id.radio_qr);
                final RadioButton radio_text = dialog.findViewById(R.id.radio_text);
                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                radio_text.setChecked(true);
                final ListChildrenData childData = realm.where(ListChildrenData.class).equalTo(AppConstants.CHILD_ID, child_id).findFirst();
                final String id = childData.getProfile_id();
                final MatrimonyProfileData profileData = realm.where(MatrimonyProfileData.class).equalTo("profile_id", id).findFirst();
                radio_qr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (radio_qr.isChecked()) {
                            radio_text.setChecked(false);
                        }
                    }
                });

                radio_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (radio_text.isChecked()) {
                            radio_qr.setChecked(false);
                        }
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (radio_qr.isChecked()) {
                            String name = "";
                            Bitmap bitmap = null;
                            if (childData != null) {
                                name = childData.getChild_name();

                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 200, 200);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                    bitmap = Utility.drawTextToBitmap(bitmap, Name);
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            }
                            shareImage(bitmap, name);
                        } else {
                            String address = "", mobile = "";
                            if (profileData != null) {
                                address = profileData.getAddress();
                                mobile = childData.getMobile();
                            }
                            String shareBody = "Name: " + Name + "\n" + "Mobile: " + mobile + "\n" + " Address: " + address;
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Name);
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            _context.startActivity(Intent.createChooser(sharingIntent, "Share Using"));
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();


            }
        });
        return convertView;
    }


    private void shareImage(Bitmap bitmap, String text) {
        String pathofBmp = MediaStore.Images.Media.insertImage(_context.getContentResolver(), bitmap, "title", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text + "'s Father Profile QR Code");
        // shareIntent.putExtra(Intent.EXTRA_TEXT, text + "'s Father Profile QR Code");
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

    /*private void showProgressDialog() {
        if (pDialog != null && !pDialog.isShowing()) pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }*/

    private void SyncUser(String profile_id) {
        if (Utility.isOnline(_context)) {
            Utility.showProgressDialog(_context);

            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
                mJsonObject.put(AppConstants.PROFILE_ID, mSharedPreferences.getString(AppConstants.PROFILE_ID, profile_id));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sync_url = AppConstants.SYNC_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    hideProgressDialog();
                    try {
                        String success = response.getString(AppConstants.SUCCESS);
                        String message = response.getString(AppConstants.MESSAGE);
                        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                        if (success.equalsIgnoreCase(AppConstants.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                Utility.SaveProfile(mJsondata);
                                notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                            if (response.has(AppConstants.ERROR_CODE)) {
                                String error = response.getString(AppConstants.ERROR_CODE);
                                if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                                    Intent mIntent = new Intent(_context, LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    _context.startActivity(mIntent);
                                    ((Activity) _context).finish();
                                }
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
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
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
