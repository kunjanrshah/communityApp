package com.yadav.vastipatrak.adapter;

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
import android.widget.RadioButton;
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
import com.yadav.vastipatrak.R;
import com.yadav.vastipatrak.activity.FamilyTreeActivity;
import com.yadav.vastipatrak.activity.LoginActivity;
import com.yadav.vastipatrak.activity.ProfileActivity;
import com.yadav.vastipatrak.app.AppController;
import com.yadav.vastipatrak.model.ListChildData;
import com.yadav.vastipatrak.model.ListParentData;
import com.yadav.vastipatrak.service.LocationAlertService;
import com.yadav.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yadav.vastipatrak.utils.Common.Constant_Class.BIRTH_DATE;
import static com.yadav.vastipatrak.utils.Common.Constant_Class.MARRIAGE_DATE;
import static com.yadav.vastipatrak.utils.Common.dd_MMM_yyyy;
import static com.yadav.vastipatrak.utils.Common.getChildRandomColor;
import static com.yadav.vastipatrak.utils.Common.getParentRandomColor;
import static com.yadav.vastipatrak.utils.Common.yyyy_MM_dd;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public final HashMap<Integer, Boolean> checkboxMap;
    private final Context _context;
    private final ArrayList<ListParentData> _listDataHeader;
    private final HashMap<ListParentData, List<ListChildData>> _listDataChild;
    private final String TAG = ExpandableListAdapter.class.getSimpleName();
    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;
    private HashMap<String, String> testHashMap2;
    private Gson gson;
    private boolean isNearby = false;
    private ChildViewHolder childViewHolder;
    private boolean sharedUsers = false;
    private HashMap<Integer, String> remHashMap;

    @SuppressLint("UseSparseArrays")
    public ExpandableListAdapter(Context context, ArrayList<ListParentData> listDataHeader, HashMap<ListParentData, List<ListChildData>> listDataChild, boolean sharedUsers) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        this.sharedUsers = sharedUsers;
        remHashMap = new HashMap<>();
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

    private static double milesTokm(double distanceInMiles) {
        return distanceInMiles * 1.60934;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
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
            childViewHolder.imgTree = convertView.findViewById(R.id.imgTree);
            childViewHolder.txt_blood = convertView.findViewById(R.id.txt_blood);
            childViewHolder.txt_gender = convertView.findViewById(R.id.txt_gender);
            childViewHolder.txt_gotra = convertView.findViewById(R.id.txt_gotra);
            childViewHolder.txt_bdate = convertView.findViewById(R.id.txt_bdate);
            childViewHolder.txt_spouse = convertView.findViewById(R.id.txt_spouse);
            childViewHolder.txt_address = convertView.findViewById(R.id.txt_address);
            childViewHolder.txt_native = convertView.findViewById(R.id.txt_native);
            childViewHolder.txt_mother = convertView.findViewById(R.id.txt_mother);
            childViewHolder.txt_phone = convertView.findViewById(R.id.txt_phone);
            childViewHolder.tbtn_share = convertView.findViewById(R.id.tbtn_share);
            childViewHolder.tbtn_share.setTextOn(null);
            childViewHolder.tbtn_share.setText(null);
            childViewHolder.tbtn_share.setTextOff(null);
            childViewHolder.imgNudge = convertView.findViewById(R.id.imgNudge);
            childViewHolder.img_details = convertView.findViewById(R.id.img_details);
            childViewHolder.imgROR = convertView.findViewById(R.id.imgROR);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        final String id = mListChildData.getID();
        final String name = mListChildData.getName();
        String address = mListChildData.getAddress();
        String birth_date = mListChildData.getbirth_date();
        birth_date = Common.parseDateToddMMyyyy(birth_date, yyyy_MM_dd, dd_MMM_yyyy);
        String spouse_name = mListChildData.getSpouse_name();
        String blood_group = mListChildData.getBlood_Group();
        String gender = mListChildData.getGender();
        String gotra = mListChildData.getGotra();
        String Shared = mListChildData.getShared();
        String Mother = mListChildData.getMother_name();
        final String can_share = mListChildData.getCan_share();
        final String profile_id = mListChildData.getID();
        final String mobile = mListChildData.getMobile().trim().replaceAll("\\?", "").replaceAll("\\+", "");
        String str_native = mListChildData.getNative();
        String phone = mListChildData.getPhone().trim().replaceAll("\\?", "").replaceAll("\\+", "");


        if (sharedUsers) {
            if (Shared != null && Shared.equalsIgnoreCase("from")) {
                childViewHolder.ll_child.setBackground(_context.getResources().getDrawable(R.drawable.parent_shape1));
            } else {
                childViewHolder.ll_child.setBackground(_context.getResources().getDrawable(R.drawable.parent_shape5));
            }
        } else {
            getChildRandomColor(_context, childPosition, childViewHolder.ll_child);
        }

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


        if (mSharedPreferences.getString(Common.Constant_Class.USER_ID, "").equalsIgnoreCase(id)) {
            childViewHolder.imgROR.setVisibility(View.GONE);
            childViewHolder.imgNudge.setVisibility(View.GONE);
            childViewHolder.tbtn_share.setVisibility(View.GONE);
        } else {
            childViewHolder.tbtn_share.setVisibility(View.VISIBLE);
            childViewHolder.imgNudge.setVisibility(View.VISIBLE);
            childViewHolder.imgROR.setVisibility(View.VISIBLE);
        }
        childViewHolder.img_details.setVisibility(View.VISIBLE);
        String bool = mSharedPreferences.getString(Common.Constant_Class.TBTN_SHARE, "0");
        if (bool.equalsIgnoreCase("1")) {
            if (can_share.equalsIgnoreCase("1")) {
                childViewHolder.tbtn_share.setChecked(true);
            } else {
                childViewHolder.tbtn_share.setChecked(false);
            }
            if (Shared == null) {
                childViewHolder.tbtn_share.setVisibility(View.GONE);
            } else {
                if (Shared.equalsIgnoreCase("from")) {
                    childViewHolder.tbtn_share.setVisibility(View.GONE);
                } else {
                    childViewHolder.tbtn_share.setVisibility(View.VISIBLE);
                }
            }
        } else {
            childViewHolder.tbtn_share.setVisibility(View.GONE);
        }


        childViewHolder.tbtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "";
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(_context.getString(R.string.app_name));
                if (childViewHolder.tbtn_share.isChecked()) {
                    msg = "Start share your location to " + name + "?";
                    builder.setMessage(msg);
                    builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mListChildData.setCan_share("1");
                            userLocationShareWS(profile_id, name, "1");
                        }
                    });
                    builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            childViewHolder.tbtn_share.setChecked(false);
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    msg = "Stop share your location to " + name + "?";
                    builder.setMessage(msg);
                    builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mListChildData.setCan_share("0");
                            userLocationShareWS(profile_id, name, "0");
                        }
                    });
                    builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            childViewHolder.tbtn_share.setChecked(true);
                            dialog.dismiss();
                        }
                    }).show();
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
        childViewHolder.txt_spouse.setText(spouse_name);


        childViewHolder.img_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.apply();
                ProfileActivity.isEnable = false;
                Intent mIntent = new Intent(_context, ProfileActivity.class);
                _context.startActivity(mIntent);
            }
        });


        childViewHolder.imgROR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog relation_dialog = new Dialog(_context);
                relation_dialog.setTitle("Request of relation");
                relation_dialog.setContentView(R.layout.custom_relation_dialog);
                Button btnSend = relation_dialog.findViewById(R.id.btnSend);
                final EditText edt_rel = relation_dialog.findViewById(R.id.edt_rel);

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = edt_rel.getText().toString().trim();
                        relation_dialog.cancel();
                        if (!msg.isEmpty()) {
                            requestOfRelationWS(msg, profile_id);
                        }
                    }
                });
                relation_dialog.show();
            }
        });

        childViewHolder.imgNudge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(_context.getString(R.string.app_name));

                builder.setMessage(_context.getResources().getString(R.string.go_to_whatsapp));
                builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        String fname = mSharedPreferences.getString(Common.Constant_Class.FIRST_NAME, "");
                        String lname = mSharedPreferences.getString(Common.Constant_Class.LAST_NAME, "");
                        String uname = fname + " " + lname;
                        Common.SendWhatsappMessage(_context, mobile, String.format(_context.getResources().getString(R.string.nice_html), uname, name));
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

        childViewHolder.imgTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(_context, "Coming Soon", Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(_context, FamilyTreeActivity.class);
                JSONObject mjson = mListChildData.getmJsonObject();
                mIntent.putExtra(_context.getString(R.string.ft_intent), mjson.toString());
                _context.startActivity(mIntent);
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
            groupViewHolder.txt_dist = convertView.findViewById(R.id.txt_dist);
            groupViewHolder.tvPassword = convertView.findViewById(R.id.tvPassword);
            groupViewHolder.ll_lable = convertView.findViewById(R.id.ll_lable);


            if (mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN) && !sharedUsers && !isNearby) {
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
        final String Mobile = mListParentData.getMobile();
        final String address = mListParentData.getAddress();
        String FatherName = mListParentData.getFatherName();
        String city = mListParentData.getCity();
        String mail = mListParentData.getMail();
        String is_share = mListParentData.getIs_share();
        String dist = mListParentData.getDistance();
        String Shared = mListParentData.getShared();
        String type = mListParentData.getType();

        String is_location = mListParentData.isIs_location_enable();

        if (!dist.isEmpty()) {
            groupViewHolder.txt_dist.setVisibility(View.VISIBLE);
            DecimalFormat df2 = new DecimalFormat("#.##");
            if (type.equalsIgnoreCase(_context.getString(R.string.near_by_users))) {
                if (is_location.equalsIgnoreCase("1")) {
                    groupViewHolder.txt_dist.setText("Now " + df2.format(milesTokm(Double.parseDouble(dist))) + "> Km");
                } else {
                    groupViewHolder.txt_dist.setText("Last " + df2.format(milesTokm(Double.parseDouble(dist))) + "> Km");
                    groupViewHolder.txt_dist.setTextColor(_context.getResources().getColor(R.color.navigationBarColor));
                }
            } else {
                groupViewHolder.txt_dist.setText(df2.format(milesTokm(Double.parseDouble(dist))) + "> Km");
            }
        } else {
            groupViewHolder.txt_dist.setVisibility(View.GONE);
        }

        // Rounded corners
        //Glide.with(_context).load(imgURL).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(_context, Common.Constant_Class.sCorner, Common.Constant_Class.sMargin, Common.Constant_Class.sColor, Common.Constant_Class.sBorder))).thumbnail(0.5f).into(groupViewHolder.ivIcon);
        Glide.with(_context).load(imgURL).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(groupViewHolder.ivIcon);

        groupViewHolder.tvCity.setText(Common.camelCase(city));
        groupViewHolder.tvName.setText(Common.camelCase(Name));
        groupViewHolder.tvFatherName.setText(Common.camelCase(FatherName));
        groupViewHolder.tvMobile.setText("" + Mobile);
        if (mail == null || mail.equalsIgnoreCase("null")) {
            groupViewHolder.tvMail.setVisibility(View.GONE);
        } else {
            groupViewHolder.tvMail.setText("" + mail);
        }

        groupViewHolder.txt_dist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lat = "", lng = "", curr_lat = "", curr_lng = "";
                curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
                curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
                if (mListParentData.getType().isEmpty() || mListParentData.getType().equalsIgnoreCase("home")) {
                    lat = mListParentData.getHome_lat();
                    lng = mListParentData.getHome_lng();
                } else if (mListParentData.getType().equalsIgnoreCase("office")) {
                    lat = mListParentData.getOffice_lat();
                    lng = mListParentData.getOffice_lng();
                } else if (mListParentData.getType().equalsIgnoreCase("user")) {
                    lat = mListParentData.getUser_lat();
                    lng = mListParentData.getUser_lng();
                }
                if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && !lat.isEmpty() && !lng.isEmpty()) {
                    Common.showDirections((Activity) _context, Double.parseDouble(curr_lat), Double.parseDouble(curr_lng), Double.parseDouble(lat), Double.parseDouble(lng), "");
                } else {
                    Toast.makeText(_context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        String user_id = mSharedPreferences.getString(Common.Constant_Class.USER_ID, "");
        if (user_id.equalsIgnoreCase("4345")) {
            String password = mListParentData.getPassword();
            groupViewHolder.tvPassword.setVisibility(View.VISIBLE);
            groupViewHolder.tvPassword.setText(password);
        } else {
            groupViewHolder.tvPassword.setVisibility(View.GONE);
        }

        String updated_time = mListParentData.getUpdated_time();
        if (updated_time.equalsIgnoreCase("0")) {
            groupViewHolder.tvUpdatedTime.setText("Not Updated");
        } else {
            groupViewHolder.tvUpdatedTime.setText("Updated: " + Common.getUpdatedTime(updated_time));
        }

        final String user_lat = mListParentData.getUser_lat();
        final String user_lng = mListParentData.getUser_lng();

        if (is_share.equalsIgnoreCase("1") && mListParentData.isIs_location_enable().equalsIgnoreCase("1")) {
            String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
            String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
            if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && user_lat != null && user_lng != null && !user_lat.isEmpty() && !user_lng.isEmpty() && !user_lat.equalsIgnoreCase("null") && !user_lng.equalsIgnoreCase("null")) {
                groupViewHolder.txt_distance.setVisibility(View.VISIBLE);
                new Common.getDistance((Activity) _context, groupViewHolder.txt_distance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, curr_lat, curr_lng, user_lat, user_lng);
            } else {
                groupViewHolder.txt_distance.setText("Not found");
                groupViewHolder.txt_distance.setClickable(false);
                groupViewHolder.txt_distance.setEnabled(false);
                groupViewHolder.txt_distance.setVisibility(View.VISIBLE);
            }
        } else if (is_share.equalsIgnoreCase("1") && mListParentData.isIs_location_enable().equalsIgnoreCase("0")) {
            groupViewHolder.txt_distance.setText("OFF");
            groupViewHolder.txt_distance.setClickable(false);
            groupViewHolder.txt_distance.setEnabled(false);
            groupViewHolder.txt_distance.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.txt_distance.setVisibility(View.GONE);
        }

        if (sharedUsers) {
            if (Shared != null && Shared.equalsIgnoreCase("from")) {
                groupViewHolder.ll_parent.setBackground(_context.getResources().getDrawable(R.drawable.parent_shape1));
            } else {
                groupViewHolder.txt_distance.setText("Shared");
                groupViewHolder.txt_distance.setClickable(false);
                groupViewHolder.txt_distance.setEnabled(false);
                groupViewHolder.txt_distance.setVisibility(View.VISIBLE);
                groupViewHolder.ll_parent.setBackground(_context.getResources().getDrawable(R.drawable.parent_shape5));
            }
        } else {
            groupViewHolder.txt_distance.setEnabled(true);
            groupViewHolder.txt_distance.setClickable(true);
            getParentRandomColor(_context, groupPosition, groupViewHolder.ll_parent);
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
                                testHashMap2.remove(id);
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
                        String curr_lat = mSharedPreferences.getString(Common.Constant_Class.CURR_LAT, "");
                        String curr_lng = mSharedPreferences.getString(Common.Constant_Class.CURR_LNG, "");
                        if (!curr_lat.isEmpty() && !curr_lng.isEmpty() && !user_lat.isEmpty() && !user_lng.isEmpty()) {
                            Common.showDirections((Activity) _context, Double.parseDouble(curr_lat), Double.parseDouble(curr_lng), Double.parseDouble(user_lat), Double.parseDouble(user_lng), "");
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


                // custom dialog
                final Dialog dialog = new Dialog(_context);
                dialog.setContentView(R.layout.custom_share_dialog);
                dialog.setTitle(_context.getString(R.string.app_name));

                // set the custom dialog components - text, image and button
                final RadioButton radio_qr = dialog.findViewById(R.id.radio_qr);
                final RadioButton radio_text = dialog.findViewById(R.id.radio_text);
                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                radio_text.setChecked(true);
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
                            Bitmap bitmap = null;
                            ImageView imageView = new ImageView(_context);
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            try {
                                BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 200, 200);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                bitmap = Common.drawTextToBitmap(bitmap, Name);
                                imageView.setImageBitmap(bitmap);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                            shareImage(bitmap, Name);
                        } else {
                            String shareBody = "Name: " + Name + "\n" + "Mobile: " + Mobile + "\n" + " Address: " + address;
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

        JSONArray array = mListParentData.getCalLabelArray();
        JSONArray childs = mListParentData.getChilds();
        if (array != null && array.length() > 0) {
            String bdate_rem_id = mListParentData.getBdate_rem_id();
            String spouse_rem_id = mListParentData.getSpouse_rem_id();
            String mdate_rem_id = mListParentData.getMdate_rem_id();
            groupViewHolder.ll_lable.removeAllViewsInLayout();

            for (int i = 0; i < array.length(); i++) {
                JSONObject mjson = null;
                try {
                    mjson = array.getJSONObject(i);
                    mjson.put("bdate_rem_id", bdate_rem_id);
                    mjson.put("spouse_rem_id", spouse_rem_id);
                    mjson.put("mdate_rem_id", mdate_rem_id);
                    addLabel(groupViewHolder.ll_lable, mjson, id, childs, groupPosition);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return convertView;
    }

    private void addLabel(LinearLayout view, JSONObject jsonObject, final String id, JSONArray childs, final int groupPosition) {
        LayoutInflater layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addView = layoutInflater.inflate(R.layout.cal_lable_view, null);
        final ViewHolder mViewholder = new ViewHolder();
        mViewholder.img_dType = addView.findViewById(R.id.img_dType);
        mViewholder.imgType = addView.findViewById(R.id.imgType);
        mViewholder.txtName = addView.findViewById(R.id.txtName);
        mViewholder.txtyear = addView.findViewById(R.id.txtyear);
        mViewholder.txtDate = addView.findViewById(R.id.txtDate);
        mViewholder.chk_rem = addView.findViewById(R.id.chk_rem);
        try {

            String rem_id = "";
            final String label_id = jsonObject.getString("id");
            final String date_type = jsonObject.getString(_context.getString(R.string.date_type));

            String child_id = "0";
            final String type = jsonObject.getString(_context.getString(R.string.type));
            if (type.equalsIgnoreCase("self")) {
                rem_id = jsonObject.getString("bdate_rem_id");
                if (!date_type.equalsIgnoreCase("marriagedate")) {
                    mViewholder.imgType.setImageDrawable(_context.getResources().getDrawable(R.drawable.man));
                } else {
                    mViewholder.imgType.setVisibility(View.GONE);
                }
            }
            if (type.equalsIgnoreCase("wife")) {
                mViewholder.imgType.setImageDrawable(_context.getResources().getDrawable(R.drawable._woman));
                rem_id = jsonObject.getString("spouse_rem_id");
            } else if (type.equalsIgnoreCase("child")) {
                child_id = label_id;
                for (int i = 0; i < childs.length(); i++) {
                    JSONObject json = childs.getJSONObject(i);
                    String cid = json.getString("id");
                    if (cid.equalsIgnoreCase(child_id)) {
                        rem_id = json.getString("child_bdate_reminder_id");
                        break;
                    }
                }
                mViewholder.imgType.setImageDrawable(_context.getResources().getDrawable(R.drawable.child));
            }


            if (date_type.equalsIgnoreCase("marriagedate")) {
                rem_id = jsonObject.getString("mdate_rem_id");
                mViewholder.img_dType.setImageDrawable(_context.getDrawable(R.drawable.marriage));
            } else {
                mViewholder.img_dType.setImageDrawable(_context.getDrawable(R.drawable.birthday));
            }

            mViewholder.txtName.setText(jsonObject.getString(_context.getString(R.string.name)));
            mViewholder.txtyear.setText(jsonObject.getString(_context.getString(R.string.age)) + "Y");
            String rdate = jsonObject.getString(_context.getString(R.string.date));
            if (!rdate.isEmpty()) {
                rdate = Common.parseDateToddMMyyyy(rdate, yyyy_MM_dd, dd_MMM_yyyy);
                mViewholder.txtDate.setText(rdate);
            }

            final String finalRdate = rdate;
            final String finalChild_id = child_id;
            final String finalRem_id = rem_id;
            if (!rem_id.isEmpty() && !rem_id.equalsIgnoreCase("0")) {
                mViewholder.chk_rem.setChecked(true);
            } else {
                mViewholder.chk_rem.setChecked(false);
            }
            mViewholder.chk_rem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String date = Common.parseDateToddMMyyyy(finalRdate, dd_MMM_yyyy, yyyy_MM_dd);
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(_context.getString(R.string.app_name));
                    builder.setCancelable(false);

                    String message = "Do you want set Reminder for Birthdate ? Change Message for wish ";
                    String messge1 = "Do you want Unset Reminder for Birthdate ?";
                    String msg = "Happy Birthday from ";
                    String status = BIRTH_DATE;
                    if (date_type.equalsIgnoreCase("marriagedate")) {
                        message = "Do you want set Reminder for Marriage anniversary ? Change Message for wish ";
                        messge1 = "Do you want Unset Reminder for Marriage anniversary ?";
                        msg = "Happy Marriage anniversary from ";
                        status = MARRIAGE_DATE;
                    }

                    if (mViewholder.chk_rem.isChecked()) {
                        builder.setMessage(message);
                        final EditText input = new EditText(_context);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        String name = "";
                        name = mSharedPreferences.getString(Common.Constant_Class.FIRST_NAME, "");
                        name = name + " " + mSharedPreferences.getString(Common.Constant_Class.LAST_NAME, "");
                        input.setText(msg + name);
                        builder.setView(input);
                        final String finalStatus = status;

                        builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder(id, date, finalStatus, input.getText().toString(), finalChild_id, 0);
                            }
                        });
                        builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                mViewholder.chk_rem.setChecked(false);
                                dialog.dismiss();
                            }
                        }).show();

                    } else {
                        builder.setMessage(messge1);
                        final String finalStatus1 = status;
                        int reminder_id = Integer.parseInt(finalRem_id);
                        if (remHashMap.size() > 0) {
                            if (!finalChild_id.equalsIgnoreCase("0") && !remHashMap.get(Integer.parseInt(finalChild_id)).isEmpty()) {
                                reminder_id = Integer.parseInt(remHashMap.get(Integer.parseInt(finalChild_id)));
                            } else if (finalChild_id.equalsIgnoreCase("0") && !remHashMap.get(Integer.parseInt(id)).isEmpty()) {
                                reminder_id = Integer.parseInt(remHashMap.get(Integer.parseInt(id)));
                            }
                        }

                        final int finalReminder_id = reminder_id;
                        builder.setPositiveButton(_context.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder(id, date, finalStatus1, "", finalChild_id, finalReminder_id);
                            }
                        });
                        builder.setNegativeButton(_context.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                mViewholder.chk_rem.setChecked(true);
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        addView.setTag(mViewholder);
        view.addView(addView);
    }

    private void setReminder(final String profile_id, String rem_date, String rem_type, String msg, final String child_id, final int rem_id) {
        if (Common.isOnline(_context)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.PROFILE_ID, profile_id);
                mJsonObject.put(Common.Constant_Class.REMINDER_DATE, rem_date);
                mJsonObject.put(Common.Constant_Class.REMINDER_TYPE, rem_type);
                mJsonObject.put(Common.Constant_Class.REMINDER_ID, rem_id);
                mJsonObject.put(Common.Constant_Class.MESSAGE, msg);
                mJsonObject.put(Common.Constant_Class._CHILD_ID, child_id);
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Common.showProgressDialog(_context);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SET_REMINDER_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        Common.hideProgressDialog();
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONObject mObject = response.getJSONObject(Common.Constant_Class.DATA);
                            String rem = mObject.getString("reminder_id");
                            if (rem_id == 0) {
                                if (child_id.equalsIgnoreCase("0")) {
                                    remHashMap.put(Integer.parseInt(profile_id), rem);
                                } else {
                                    remHashMap.put(Integer.parseInt(child_id), rem);
                                }
                            } else {
                                if (remHashMap.size() > 0) {
                                    if (child_id.equalsIgnoreCase("0")) {
                                        remHashMap.remove(Integer.parseInt(profile_id));
                                    } else {
                                        remHashMap.remove(Integer.parseInt(child_id));
                                    }
                                }
                            }
                        } else {
                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                    Intent mIntent = new Intent(_context, LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    _context.startActivity(mIntent);
                                    ((Activity) _context).finish();
                                }
                            }
                        }
                        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Common.hideProgressDialog();
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
                        String message = "";
                        if (response.has(Common.Constant_Class.MESSAGE)) {
                            message = response.getString(Common.Constant_Class.MESSAGE);
                        }

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            if (is_share.equalsIgnoreCase("1")) {
                                Toast.makeText(_context, "You have shared your location to " + name, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(_context, "You have not shared your location to " + name, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
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

    private void requestOfRelationWS(String relation, String to_user_id) {
        if (Common.isOnline(_context)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.TO_USER_ID, to_user_id);
                mJsonObject.put(Common.Constant_Class.RELATION, relation);
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SEND_REQUEST_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                        if (!success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {

                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
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
        //  shareIntent.putExtra(Intent.EXTRA_TEXT, text + "'s Profile");
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

    public static class ViewHolder {
        ImageView img_dType;
        ImageView imgType;
        TextView txtName;
        TextView txtyear;
        TextView txtDate;
        CheckBox chk_rem;
    }

    private class ChildViewHolder {
        TextView txt_blood;
        TextView txt_gender;
        TextView txt_gotra;
        TextView txt_bdate;
        TextView txt_spouse;
        //TextView txt_btime;
        //TextView txt_bplace;
        TextView txt_address;
        TextView txt_native;
        TextView txt_mother;
        TextView txt_phone;
        LinearLayout ll_child;
        ToggleButton tbtn_share;
        ImageView imgNudge;
        ImageView img_details;
        ImageView imgROR;
        ImageView imgTree;
    }

    private class GroupViewHolder {
        LinearLayout ll_parent;
        LinearLayout ll_lable;
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
        TextView txt_dist;
        TextView tvPassword;
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
