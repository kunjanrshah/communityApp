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
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MainActivity;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildData;
import com.krs.vastipatrak.model.ListParentData;
import com.krs.vastipatrak.service.LocationAlertService;
import com.krs.vastipatrak.utils.Common;
import com.krs.vastipatrak.utils.RoundedCornersTransformation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.dd_MMM_yyyy;
import static com.krs.vastipatrak.utils.Common.getChildRandomColor;
import static com.krs.vastipatrak.utils.Common.getParentRandomColor;
import static com.krs.vastipatrak.utils.Common.yyyy_MM_dd;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public final HashMap<Integer, Boolean> checkboxMap;
    private final Context _context;
    private final ArrayList<ListParentData> _listDataHeader;
    private final HashMap<ListParentData, List<ListChildData>> _listDataChild;
    private final String TAG = ExpandableListAdapter.class.getSimpleName();
    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;
    @NonNull
    private final String[] SPINNERLIST = {"Father", "Son", "Daughter", "Brother", "Sister", "Grandfather", "Grandson", "Uncle", "Uncle's Son", "Uncle in law", "Uncle's Son"};
    HashMap<String, String> testHashMap2;
    Gson gson;
    @Nullable
   // private ProgressDialog pDialog;
    private ChildViewHolder childViewHolder;

    @SuppressLint("UseSparseArrays")
    public ExpandableListAdapter(Context context, ArrayList<ListParentData> listDataHeader, HashMap<ListParentData, List<ListChildData>> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;


        mSharedPreferences = _context.getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
        checkboxMap = new HashMap<>();
        popolaCheckMap(_listDataHeader.size());

        gson = new Gson();
        String storedHashMapString = mSharedPreferences.getString("hashString", null);
        if (storedHashMapString != null) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            testHashMap2 = gson.fromJson(storedHashMapString, type);
        } else {
            testHashMap2 = new HashMap<>();
        }
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

    private void popolaCheckMap(int len) {
        for (int i = 0; i < len; i++) {
            checkboxMap.put(i, false);
        }
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

        final ListChildData mListChildData = (ListChildData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert infalInflater != null;
            convertView = infalInflater.inflate(R.layout.list_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.ll_child = convertView.findViewById(R.id.ll_child);
            childViewHolder.txt_blood = convertView.findViewById(R.id.txt_blood);
            childViewHolder.txt_gender = convertView.findViewById(R.id.txt_gender);
            childViewHolder.txt_gotra = convertView.findViewById(R.id.txt_gotra);
            childViewHolder.txt_bdate = convertView.findViewById(R.id.txt_bdate);
            childViewHolder.txt_btime = convertView.findViewById(R.id.txt_btime);
            childViewHolder.txt_bplace = convertView.findViewById(R.id.txt_bplace);
            childViewHolder.txt_address = convertView.findViewById(R.id.txt_address);
            childViewHolder.txt_native = convertView.findViewById(R.id.txt_native);
            childViewHolder.txt_mother = convertView.findViewById(R.id.txt_mother);
            childViewHolder.txt_phone = convertView.findViewById(R.id.txt_phone);

            childViewHolder.tbtn_share = convertView.findViewById(R.id.tbtn_share);
            childViewHolder.imgNudge = convertView.findViewById(R.id.imgNudge);
            childViewHolder.img_details = convertView.findViewById(R.id.img_details);

            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        getChildRandomColor(_context, childPosition, childViewHolder.ll_child);
        final String id = mListChildData.getID();
        final String name = mListChildData.getName();
        String address = mListChildData.getAddress();
        String birth_date = mListChildData.getbirth_date();
        birth_date = Common.parseDateToddMMyyyy(birth_date, yyyy_MM_dd, dd_MMM_yyyy);
        String birth_place = mListChildData.getBirth_place();
        String birth_time = mListChildData.getbirth_time();
        String blood_group = mListChildData.getBlood_Group();
        String gender = mListChildData.getGender();
        String gotra = mListChildData.getGotra();
        String Mother = mListChildData.getMother_name();
        final String can_share = mListChildData.getCan_share();
        final String profile_id = mListChildData.getID();
        final String mobile = mListChildData.getMobile().trim().replaceAll("\\?", "").replaceAll("\\+", "");
        String str_native = mListChildData.getNative();
        String phone = mListChildData.getPhone().trim().replaceAll("\\?", "").replaceAll("\\+", "");

        childViewHolder.txt_phone.setOnClickListener(new View.OnClickListener() {
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
                        String phone_no = childViewHolder.txt_phone.getText().toString().replaceAll("-", "");
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + phone_no.trim()));
                        Activity activity = (Activity) _context;
                        activity.startActivity(callIntent);
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        String bool = mSharedPreferences.getString(Common.Constant_Class.TBTN_SHARE, "0");
        if (bool.equalsIgnoreCase("1")) {
            childViewHolder.tbtn_share.setVisibility(View.VISIBLE);
            if (can_share.equalsIgnoreCase("1")) {
                childViewHolder.tbtn_share.setChecked(true);
            } else {
                childViewHolder.tbtn_share.setChecked(false);
            }
        } else {
            childViewHolder.tbtn_share.setVisibility(View.GONE);
        }
        childViewHolder.tbtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childViewHolder.tbtn_share.isChecked()) {
                    mListChildData.setCan_share("1");
                    userLocationShareWS(profile_id, name, "1");
                } else {
                    mListChildData.setCan_share("0");
                    userLocationShareWS(profile_id, name, "0");
                }
            }
        });

        childViewHolder.txt_address.setText(Common.camelCase(address));
        childViewHolder.txt_native.setText(Common.camelCase(str_native));
        childViewHolder.txt_mother.setText(Mother);

        childViewHolder.txt_phone.setText(phone);
        String styledText = "<u><font color='blue'>" + phone + "</font></u>";
        childViewHolder.txt_phone.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        childViewHolder.txt_blood.setText(blood_group);
        if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("")) {
            gender = "Male";
        } else {
            gender = "Female";
        }
        childViewHolder.txt_gender.setText(gender);
        childViewHolder.txt_gotra.setText(Common.camelCase(gotra));
        childViewHolder.txt_bdate.setText(birth_date);
        childViewHolder.txt_btime.setText(birth_time);
        childViewHolder.txt_bplace.setText(Common.camelCase(birth_place));

        childViewHolder.img_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.apply();
                Intent mIntent = new Intent(_context, MyProfileActivity.class);
                _context.startActivity(mIntent);
            }
        });

        childViewHolder.imgNudge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(_context.getString(R.string.app_name));

                builder.setMessage("Do you want to request on WhatsApp ?");
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

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (_listDataChild.get(this._listDataHeader.get(groupPosition)) == null) {
            return 0;
        }
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
        final ListParentData mListParentData = (ListParentData) getGroup(groupPosition);

        final GroupViewHolder groupViewHolder;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(infalInflater).inflate(R.layout.list_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.ll_parent = convertView.findViewById(R.id.ll_parent);
            groupViewHolder.ivIcon = convertView.findViewById(R.id.ivIcon);
            groupViewHolder.tvName = convertView.findViewById(R.id.tvName);
            groupViewHolder.imgShare = convertView.findViewById(R.id.imgShare);
            groupViewHolder.tvFatherName = convertView.findViewById(R.id.tvFatherName);
            groupViewHolder.tvMobile = convertView.findViewById(R.id.tvMobile);
            groupViewHolder.checkbox = convertView.findViewById(R.id.cbx1);
            groupViewHolder.tvUpdatedTime = convertView.findViewById(R.id.tvUpdatedTime);
            groupViewHolder.txt_distance = convertView.findViewById(R.id.txt_distance);
            groupViewHolder.tvCity = convertView.findViewById(R.id.tvCity);
            groupViewHolder.tvMail = convertView.findViewById(R.id.tvMail);


            if (mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
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
        String Mobile = mListParentData.getMobile();
        String city = mListParentData.getCity();
        String mail = mListParentData.getMail();
        String is_share = mListParentData.getIs_share();

        // Rounded corners
        Glide.with(_context).load(imgURL).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(_context, Common.Constant_Class.sCorner, Common.Constant_Class.sMargin, Common.Constant_Class.sColor, Common.Constant_Class.sBorder))).into(groupViewHolder.ivIcon);

        groupViewHolder.tvCity.setText(Common.camelCase(city));
        groupViewHolder.tvName.setText(Common.camelCase(Name));
        groupViewHolder.tvFatherName.setText(Common.camelCase(FatherName));
        groupViewHolder.tvMobile.setText("" + Mobile);
        if (mail == null || mail.equalsIgnoreCase("null")) {
            groupViewHolder.tvMail.setVisibility(View.GONE);
        } else {
            groupViewHolder.tvMail.setText("" + mail);
        }


        groupViewHolder.tvMobile.setOnClickListener(new View.OnClickListener() {
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
                        String mobile = groupViewHolder.tvMobile.getText().toString().replaceAll("-", "");
                        if (!mobile.isEmpty()) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + mobile.trim()));
                            Activity activity = (Activity) _context;
                            activity.startActivity(callIntent);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
        String styledText = "<u><font color='blue'>" + Mobile + "</font></u>";
        groupViewHolder.tvMobile.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);


        getParentRandomColor(_context, groupPosition, groupViewHolder.ll_parent);

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

        if (is_share.equalsIgnoreCase("1")&& mListParentData.isIs_location_enable().equalsIgnoreCase("1")) {
            String curr_lat= mSharedPreferences.getString(Common.Constant_Class.CURR_LAT,"");
            String curr_lng= mSharedPreferences.getString(Common.Constant_Class.CURR_LNG,"");
            if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && user_lat != null && user_lng != null && !user_lat.isEmpty() && !user_lng.isEmpty() && !user_lat.equalsIgnoreCase("null") && !user_lng.equalsIgnoreCase("null")) {
                groupViewHolder.txt_distance.setVisibility(View.VISIBLE);
                new Common.getDistance(groupViewHolder.txt_distance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Double.parseDouble(user_lat), Double.parseDouble(user_lng), Double.parseDouble(curr_lat), Double.parseDouble(curr_lng));
            } else {
                groupViewHolder.txt_distance.setVisibility(View.GONE);
            }
        } else {
            groupViewHolder.txt_distance.setVisibility(View.GONE);
        }

        groupViewHolder.txt_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(_context);
                dialog.setContentView(R.layout.dialog_alert);
                dialog.setTitle(_context.getResources().getString(R.string.app_name));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                Button btn_map = dialog.findViewById(R.id.btn_map);
                ImageView img_cancel = dialog.findViewById(R.id.img_cancel);
                final Button btnsave = dialog.findViewById(R.id.btnsave);
                final ToggleButton tbtn_alert = dialog.findViewById(R.id.btn_alert);
                final EditText edtAlertTime = dialog.findViewById(R.id.edtAlertTime);
                final TextInputLayout tlalert = dialog.findViewById(R.id.tlalert);

                if (testHashMap2.get(id) != null) {
                    tlalert.setVisibility(View.VISIBLE);
                    btnsave.setVisibility(View.VISIBLE);
                    tbtn_alert.setChecked(true);
                    edtAlertTime.setText("" + testHashMap2.get(id));
                } else {
                    tlalert.setVisibility(View.GONE);
                    btnsave.setVisibility(View.GONE);
                }

                img_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tbtn_alert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tbtn_alert.isChecked()) {
                            tlalert.setVisibility(View.VISIBLE);
                            btnsave.setVisibility(View.VISIBLE);
                        } else {
                            tlalert.setVisibility(View.GONE);
                            btnsave.setVisibility(View.GONE);
                            if (testHashMap2.get(id) != null) {
                                Intent mIntent = new Intent(_context, LocationAlertService.class);
                                mIntent.putExtra("isStop", true);
                                mIntent.putExtra("id", id);
                                _context.startService(mIntent);
                            }
                        }
                    }
                });

                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!edtAlertTime.getText().toString().isEmpty()) {

                            testHashMap2.put(id, edtAlertTime.getText().toString().trim());
                            String hashMapString = gson.toJson(testHashMap2);
                            mEditor.putString("hashString", hashMapString);
                            mEditor.commit();
                            Intent mIntent = new Intent(_context, LocationAlertService.class);
                            mIntent.putExtra("isStop", false);
                            mIntent.putExtra("id", id);
                            mIntent.putExtra("time", edtAlertTime.getText().toString().trim());
                            _context.startService(mIntent);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(_context, "Enter Alert Time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btn_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String curr_lat= mSharedPreferences.getString(Common.Constant_Class.CURR_LAT,"");
                        String curr_lng= mSharedPreferences.getString(Common.Constant_Class.CURR_LNG,"");
                        if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && !user_lat.isEmpty() && !user_lng.isEmpty()) {
                            showDirections(Double.parseDouble(curr_lat), Double.parseDouble(curr_lng), Double.parseDouble(user_lat), Double.parseDouble(user_lng), "");
                        } else {
                            Toast.makeText(_context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                try {
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        if (mListParentData.getStatus() != null && mListParentData.getStatus().equalsIgnoreCase("0")) {
            groupViewHolder.imgShare.setVisibility(View.GONE);
        } else {
            groupViewHolder.imgShare.setVisibility(View.VISIBLE);
        }
        groupViewHolder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                ImageView imageView = new ImageView(_context);
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                shareImage(bitmap, Name);
            }
        });
        return convertView;
    }

    private void showDirections(double clat, double clng, double dlat, double dlng, String address) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)&daddr=%f,%f (%s)", clat, clng, "", dlat, dlng, address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        _context.startActivity(intent);
    }

    private void userLocationShareWS(String share_id, final String name, final String is_share) {
        if (Common.isOnline(_context)) {
            JSONObject mJsonObject = null;
            try {

                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.SHARE_USER_IDS, share_id);
                mJsonObject.put(Common.Constant_Class.IS_SHARE, is_share);
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SHARED_USERS_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            if (is_share.equalsIgnoreCase("1")) {
                                Toast.makeText(_context, "You have shared your location to " + name, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(_context, "You have not shared your location to " + name, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(_context, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            }) {
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

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }


    private void shareImage(Bitmap bitmap, String text) {
        String pathofBmp = MediaStore.Images.Media.insertImage(_context.getContentResolver(), bitmap, "title", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text + "'s Profile QR Code");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text + "'s Profile");
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

   /* private void showProgressDialog() {
        if (pDialog != null && !pDialog.isShowing()) pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }*/

/*
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
                    params.put(Common.Constant_Class.DEVICE_TOKEN, mSharedPreferences.getString(Common.Constant_Class.DEVICE_TOKEN, ""));
                    return params;
                }
            };
            // Adding request to request queue
            String tag_json_obj = "jobj_req";
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

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
        TextView txt_address;
        TextView txt_native;
        TextView txt_mother;
        TextView txt_phone;
        LinearLayout ll_child;
        ToggleButton tbtn_share;
        ImageView imgNudge;
        ImageView img_details;
    }

    private class GroupViewHolder {
        LinearLayout ll_parent;
        ImageView ivIcon;
        ImageView imgShare;
        TextView tvName;
        TextView tvFatherName;
        TextView tvMobile;
        TextView tvCity;
        TextView tvMail;
        TextView txt_distance;
        TextView tvUpdatedTime;
        CheckBox checkbox;
    }

    class CheckListener implements CompoundButton.OnCheckedChangeListener {

        int pos;

        void setPosition(int p) {
            pos = p;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i("checkListenerChanged", String.valueOf(pos) + ":" + String.valueOf(isChecked));
            checkboxMap.put(pos, isChecked);
        }
    }
}
