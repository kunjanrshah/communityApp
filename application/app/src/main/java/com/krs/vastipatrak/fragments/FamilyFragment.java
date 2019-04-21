package com.krs.vastipatrak.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.AdvanceSearchActivity;
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.activity.ProfileActivity;
import com.krs.vastipatrak.activity.SelectionlistActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListChildrenData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.krs.vastipatrak.activity.ProfileActivity.chooseFragment;
import static com.krs.vastipatrak.utils.Common.ddMMMyyyy;
import static com.krs.vastipatrak.utils.Common.yyyy_MM_dd;

public class FamilyFragment extends Fragment implements Serializable, AdapterView.OnItemSelectedListener {


    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int CONTACT_PICKER_RESULT_CHILD = 1002;
    public EditText edtSpouseName, edtSpouseFName, edtMSpouseName, edtsponse_mobile, edt_spouse_edu;
    public TextView txt_sponse_nplace;
    public String str_spouse_hash = "", str_fspouse_hash = "", str_mspouse_hash = "";
    public LinearLayout child_container = null;
    public ArrayList<Integer> lst_delID = null;
    public RadioButton rbtnChildNo;
    public EditText edt_mdate, edtsponse_bdate;
    public CheckBox chk_marriage_bdate_rem = null;
    public CheckBox chk_spouse_bdate_rem = null;
    public Spinner sp_spouse_blood;
    ArrayAdapter<String> dataAdapter;
    RequestOptions requestOptions;
    private RadioButton rbtnChildYes;
    private String spouse_url = "";
    private String fspouse_url = "";
    private String mspouse_url = "";
    private Button btn_add;
    private CircleImageView img_spouse;
    private CircleImageView img_fspouse;
    private CircleImageView img_mspouse;
    private ImageView img_spouse_cancel;
    private ImageView img_fspouse_cancel;
    private ImageView img_mspouse_cancel;
    private String img_selection = "";
    private SharedPreferences mSharedPreferences;
    private Activity mActivity;
    private String profile_id = "";
    private String mdate_rem = "0";
    private String sbdate_rem = "0";
    private HashMap<Integer, String> lstchild = null;
    private String TAG = FamilyFragment.class.getSimpleName();
    private List<String> blood;
    private Uri mCropImageUri;
    private boolean is_first = true;
    private HashMap<Integer, TextView> hashMap;
    private int educate = 110;

    public FamilyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem saveItem = menu.findItem(R.id.action_save);
        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            saveItem.setVisible(true);
        } else {
            if (ProfileActivity.isEnable && mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
                saveItem.setVisible(true);
            } else {
                saveItem.setVisible(false);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_family, container, false);
        Memory_Allocation(rootView);
        setAdapterBGlist();
        try {
            ListProfileData mListProfileData = ((ProfileActivity) getActivity()).getMyData();
            if (mListProfileData != null) {
                SetOfflineData(mListProfileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        edtsponse_bdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtsponse_bdate.getRight() - edtsponse_bdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Spouse Birth Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                                monthOfYear = (++monthOfYear);
                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = str_day + "/" + str_month + "/" + year;
                                edtsponse_bdate.setText(date);
                            }
                        });
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {

                            dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        edt_mdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_mdate.getRight() - edt_mdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Marriage Date");
                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                                monthOfYear = (++monthOfYear);
                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = str_day + "/" + str_month + "/" + year;
                                // mdate = year + "-" + str_month + "-" + str_day;
                                edt_mdate.setText(date);
                            }
                        });
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                            dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        edtsponse_mobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((event.getRawX()) >= (edtsponse_mobile.getRight() - edtsponse_mobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
                    }
                }
                return false;
            }
        });

        img_spouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || ProfileActivity.isEnable) {
                    img_selection = "spouse";
                    startImageActivity();
                } else {
                    String Name = edtSpouseName.getText().toString();
                    openImageDialog(Name, spouse_url, false);
                }

            }
        });

        img_fspouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || ProfileActivity.isEnable) {
                    img_selection = "fspouse";
                    startImageActivity();
                } else {
                    String Name = edtSpouseFName.getText().toString();
                    openImageDialog(Name, fspouse_url, false);
                }

            }
        });

        img_mspouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) || ProfileActivity.isEnable) {
                    img_selection = "mspouse";
                    startImageActivity();
                } else {
                    String Name = edtMSpouseName.getText().toString();
                    openImageDialog(Name, mspouse_url, false);
                }

            }
        });

        rbtnChildYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    rbtnChildNo.setChecked(false);
                    btn_add.setVisibility(View.VISIBLE);
                    child_container.setVisibility(View.VISIBLE);
                }
            }
        });

        rbtnChildNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtnChildYes.setChecked(false);
                    btn_add.setVisibility(View.GONE);
                    child_container.setVisibility(View.GONE);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true) && !ProfileActivity.isEnable) {

                } else {
                    add_child_layout(null);
                }
            }
        });

        chk_marriage_bdate_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mdate = edt_mdate.getText().toString().trim();
                String msg = "";
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(mActivity.getString(R.string.app_name));

                if (!mdate.isEmpty()) {
                    final String date = Common.parseDateToddMMyyyy(mdate, ddMMMyyyy, yyyy_MM_dd);
                    if (chk_marriage_bdate_rem.isChecked()) {
                        msg = "Do you want to set Reminder for Marriage Date ? Type Message to wish ";
                        builder.setMessage(msg);
                        builder.setCancelable(false);
                        final EditText input = new EditText(getActivity());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        String name = mSharedPreferences.getString(Common.Constant_Class.FIRST_NAME, "") + " " + mSharedPreferences.getString(Common.Constant_Class.LAST_NAME, "");
                        input.setText("Happy Marriage Anniversary from " + name);
                        builder.setView(input);
                        builder.setPositiveButton(mActivity.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder(input.getText().toString(), date, Common.Constant_Class.MARRIAGE_DATE, "0", 0);
                            }
                        });
                        builder.setNegativeButton(mActivity.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chk_marriage_bdate_rem.setChecked(false);
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        msg = "Do you want to Unset Reminder for Marriage Date ?";
                        builder.setMessage(msg);
                        builder.setPositiveButton(mActivity.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder("", date, Common.Constant_Class.MARRIAGE_DATE, mdate_rem, 0);
                            }
                        });
                        builder.setNegativeButton(mActivity.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chk_marriage_bdate_rem.setChecked(true);
                                dialog.dismiss();
                            }
                        }).show();
                    }
                } else {
                    chk_marriage_bdate_rem.setChecked(false);
                    Toast.makeText(getActivity(), "Marriage date not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chk_spouse_bdate_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sbdate = edtsponse_bdate.getText().toString().trim();
                String msg = "";
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(mActivity.getString(R.string.app_name));
                if (!sbdate.isEmpty()) {
                    final String date = Common.parseDateToddMMyyyy(sbdate, ddMMMyyyy, yyyy_MM_dd);
                    if (chk_spouse_bdate_rem.isChecked()) {
                        msg = "Do you want to set Reminder for Spouse BirthDate ? Type Message to wish ";
                        builder.setMessage(msg);
                        builder.setCancelable(false);
                        final EditText input = new EditText(getActivity());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        String name = mSharedPreferences.getString(Common.Constant_Class.FIRST_NAME, "") + " " + mSharedPreferences.getString(Common.Constant_Class.LAST_NAME, "");
                        input.setText("Happy Birthday from " + name);
                        builder.setView(input);
                        builder.setPositiveButton(mActivity.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder(input.getText().toString(), date, Common.Constant_Class.WIFE_BIRTH_DATE, "0", 0);
                            }
                        });
                        builder.setNegativeButton(mActivity.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chk_spouse_bdate_rem.setChecked(false);
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        msg = "Do you want to Unset Reminder for Spouse BirthDate ?";
                        builder.setMessage(msg);
                        builder.setPositiveButton(mActivity.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder("", date, Common.Constant_Class.WIFE_BIRTH_DATE, sbdate_rem, 0);
                            }
                        });
                        builder.setNegativeButton(mActivity.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chk_spouse_bdate_rem.setChecked(false);
                                dialog.dismiss();
                            }
                        }).show();
                    }
                } else {
                    chk_spouse_bdate_rem.setChecked(false);
                    Toast.makeText(getActivity(), "Spouse Birthdate not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, true)) {
            if (!ProfileActivity.isEnable) {
                DisableAll();
            }
        }

        edtsponse_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) && !ProfileActivity.isEnable) {
                    try {
                        boolean flag = true;
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Common.canCallPhone(getActivity())) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            String phone_no = edtsponse_mobile.getText().toString().replaceAll("-", "");
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + phone_no.trim()));
                            getActivity().startActivity(callIntent);
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        img_spouse_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_spouse.setImageResource(R.drawable.user_profile);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.user_profile);
                if (icon != null) {
                    str_spouse_hash = Common.getBase64(icon);
                }
            }
        });

        img_fspouse_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_fspouse.setImageResource(R.drawable.user_profile);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.user_profile);
                if (icon != null) {
                    str_fspouse_hash = Common.getBase64(icon);
                }
            }
        });

        img_mspouse_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_mspouse.setImageResource(R.drawable.user_profile);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.user_profile);
                if (icon != null) {
                    str_mspouse_hash = Common.getBase64(icon);
                }
            }
        });

        txt_sponse_nplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_first) {
                    is_first = false;
                    AdvanceSearchActivity.chooseFragment = TAG;
                    Intent mIntent = new Intent(getActivity(), SelectionlistActivity.class);
                    mIntent.putExtra(getString(R.string.listview), true);
                    mIntent.putExtra(getString(R.string.title), "Native");
                    startActivityForResult(mIntent, 11);
                }
            }
        });

        /*txt_spouse_edu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_first) {
                    is_first = false;
                    AdvanceSearchActivity.chooseFragment = TAG;
                    Intent mIntent = new Intent(getActivity(), SelectionlistActivity.class);
                    mIntent.putExtra(getString(R.string.listview), true);
                    mIntent.putExtra(getString(R.string.title), "Education");
                    startActivityForResult(mIntent, 12);
                }
            }
        });*/

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        is_first = true;
    }

    private void setAdapterBGlist() {
        blood = new ArrayList<>();
        blood.add(Common.Constant_Class.TITLE_SPOUSE_BLOOD_GROUP);
        blood.add(Common.Constant_Class.A_POSITIVE);
        blood.add(Common.Constant_Class.A_NAGATIVE);
        blood.add(Common.Constant_Class.B_POSITIVE);
        blood.add(Common.Constant_Class.B_NAGATIVE);
        blood.add(Common.Constant_Class.AB_POSITIVE);
        blood.add(Common.Constant_Class.AB_NAGATIVE);
        blood.add(Common.Constant_Class.O_POSITIVE);
        blood.add(Common.Constant_Class.O_NAGATIVE);

        dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, blood);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_spouse_blood.setAdapter(dataAdapter);
    }

    private void Memory_Allocation(View root) {
        hashMap = new HashMap<>();
        mActivity = Objects.requireNonNull(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        edt_mdate = root.findViewById(R.id.edt_mdate);
        edtsponse_bdate = root.findViewById(R.id.edtsponse_bdate);
        edt_spouse_edu = root.findViewById(R.id.edt_spouse_edu);
        edtsponse_mobile = root.findViewById(R.id.edtsponse_mobile);
        txt_sponse_nplace = root.findViewById(R.id.txt_sponse_nplace);
        edtSpouseName = root.findViewById(R.id.edtSpouseName);
        edtSpouseFName = root.findViewById(R.id.edtSpouseFName);
        edtMSpouseName = root.findViewById(R.id.edtMSpouseName);
        img_spouse = root.findViewById(R.id.img_spouse);
        img_fspouse = root.findViewById(R.id.img_fspouse);
        img_mspouse = root.findViewById(R.id.img_mspouse);

        img_spouse_cancel = root.findViewById(R.id.img_spouse_cancel);
        img_fspouse_cancel = root.findViewById(R.id.img_fspouse_cancel);
        img_mspouse_cancel = root.findViewById(R.id.img_mspouse_cancel);


        sp_spouse_blood = root.findViewById(R.id.sp_spouse_blood);
        chk_marriage_bdate_rem = root.findViewById(R.id.chk_marriage_bdate_rem);
        chk_spouse_bdate_rem = root.findViewById(R.id.chk_spouse_bdate_rem);
        btn_add = root.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.GONE);
        rbtnChildYes = root.findViewById(R.id.rbtnChildYes);
        rbtnChildNo = root.findViewById(R.id.rbtnChildNo);
        rbtnChildNo.setChecked(true);
        child_container = root.findViewById(R.id.child_container);
        lst_delID = new ArrayList<>();
        lstchild = new HashMap<>();
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.user_profile);
        requestOptions.error(R.drawable.download);
    }

    private void DisableAll() {
        edt_mdate.setKeyListener(null);
        edt_mdate.setCursorVisible(false);

        edtsponse_bdate.setKeyListener(null);
        edtsponse_bdate.setCursorVisible(false);

        edt_spouse_edu.setKeyListener(null);
        edt_spouse_edu.setCursorVisible(false);


        txt_sponse_nplace.setClickable(false);

        sp_spouse_blood.setClickable(false);
        edtsponse_mobile.setKeyListener(null);
        edtsponse_mobile.setCursorVisible(false);

        edtSpouseName.setKeyListener(null);
        edtSpouseName.setCursorVisible(false);

        edtSpouseFName.setKeyListener(null);
        edtSpouseFName.setCursorVisible(false);

        edtMSpouseName.setKeyListener(null);
        edtMSpouseName.setCursorVisible(false);

        btn_add.setVisibility(View.GONE);
        rbtnChildYes.setEnabled(false);
        rbtnChildNo.setEnabled(false);
        chk_marriage_bdate_rem.setVisibility(View.VISIBLE);
        chk_spouse_bdate_rem.setVisibility(View.VISIBLE);

        img_spouse_cancel.setVisibility(View.GONE);
        img_fspouse_cancel.setVisibility(View.GONE);
        img_mspouse_cancel.setVisibility(View.GONE);

    }

    private void EnableAll() {
        edt_mdate.setEnabled(true);
        edtSpouseName.setEnabled(true);
        edtSpouseFName.setEnabled(true);
        edtMSpouseName.setEnabled(true);
        edtsponse_bdate.setEnabled(true);
        edt_spouse_edu.setEnabled(true);
        txt_sponse_nplace.setClickable(true);
        sp_spouse_blood.setClickable(true);
        chk_marriage_bdate_rem.setVisibility(View.GONE);
        chk_spouse_bdate_rem.setVisibility(View.GONE);
        img_spouse_cancel.setVisibility(View.VISIBLE);
        img_fspouse_cancel.setVisibility(View.VISIBLE);
        img_mspouse_cancel.setVisibility(View.VISIBLE);
    }


    private void SetOfflineData(ListProfileData mListProfileData) {

        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
            EnableAll();
        } else {
            DisableAll();
        }
        profile_id = mListProfileData.getProfile_id();
        edtSpouseName.setText(mListProfileData.getSpouse_name());
        edt_spouse_edu.setText(mListProfileData.getSpouse_education());
        txt_sponse_nplace.setText(mListProfileData.getSponse_native());
        edtsponse_bdate.setText(mListProfileData.getSponse_bdate());
        edtsponse_mobile.setText(mListProfileData.getSponse_mobile());
        sp_spouse_blood.setSelection(blood.indexOf(mListProfileData.getSponse_bg()));

        edt_mdate.setText(mListProfileData.getMarriage_date());
        edtSpouseFName.setText(mListProfileData.getSfather_name());
        edtMSpouseName.setText(mListProfileData.getSmother_name());
        if (mListProfileData.getMdate_reminder_id().equalsIgnoreCase("0")) {
            chk_marriage_bdate_rem.setChecked(false);
            mdate_rem = "0";
        } else {
            mdate_rem = mListProfileData.getMdate_reminder_id();
            chk_marriage_bdate_rem.setChecked(true);
        }

        if (mListProfileData.getSpouse_bdate_reminder_id().equalsIgnoreCase("0")) {
            chk_spouse_bdate_rem.setChecked(false);
            sbdate_rem = "0";
        } else {
            sbdate_rem = mListProfileData.getSpouse_bdate_reminder_id();
            chk_spouse_bdate_rem.setChecked(true);
        }

        spouse_url = mListProfileData.getImg_spouse_url();
        fspouse_url = mListProfileData.getImg_sfather_url();
        mspouse_url = mListProfileData.getImg_smother_url();

        try {
            Glide.with(mActivity).load(spouse_url).apply(requestOptions).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_spouse);

        } catch (Exception e) {
            e.getMessage();
        }
        try {
            Glide.with(mActivity).load(fspouse_url).apply(requestOptions).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_fspouse);

        } catch (Exception e) {
            e.getMessage();
        }

        try {
            Glide.with(mActivity).load(mspouse_url).apply(requestOptions).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_mspouse);
        } catch (Exception e) {
            e.getMessage();
        }


        if (mListProfileData.getmListChildrenData() != null) {
            if (mListProfileData.getmListChildrenData().size() > 0) {
                child_container.removeAllViews();
                child_container.removeAllViewsInLayout();
                for (int i = 0; i < mListProfileData.getmListChildrenData().size(); i++) {

                    if (i == 0) {
                        rbtnChildYes.setChecked(true);
                        rbtnChildNo.setChecked(false);
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                            btn_add.setVisibility(View.VISIBLE);
                        } else {
                            btn_add.setVisibility(View.GONE);
                        }
                    }
                    ListChildrenData mObjChild = mListProfileData.getmListChildrenData().get(i);
                    add_child_layout(mObjChild);
                    final Viewholder mViewholder = (Viewholder) child_container.getChildAt(i).getTag();

                    mViewholder.child_id = Integer.parseInt(Objects.requireNonNull(mObjChild).getChild_id());
                    mViewholder.edtchild_name.setText(mObjChild.getChild_name());
                    Log.d(FamilyFragment.class.getName(), "kunj: " + mObjChild.getChild_name());
                    mViewholder.edtchild_bdate.setText(mObjChild.getChild_bday());
                    mViewholder.edtMobile.setText(mObjChild.getMobile());
                    mViewholder.chk_child_marriage.setChecked(mObjChild.isIs_married());

                    if (mObjChild.getChild_bdate_reminder_id().equalsIgnoreCase("0")) {
                        mViewholder.chk_child_bdate_rem.setChecked(false);
                        lstchild.put(mViewholder.child_id, "0");
                    } else {
                        mViewholder.chk_child_bdate_rem.setChecked(true);
                        lstchild.put(mViewholder.child_id, mObjChild.getChild_bdate_reminder_id());
                    }
                    String blood = mObjChild.getBlood_group();
                    if (blood != null && !blood.isEmpty()) {
                        if (blood.equalsIgnoreCase(Common.Constant_Class.A_POSITIVE)) {
                            Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(1);
                        } else if (blood.equalsIgnoreCase(Common.Constant_Class.A_NAGATIVE)) {
                            Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(2);
                        } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_POSITIVE)) {
                            Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(3);
                        } else if (blood.equalsIgnoreCase(Common.Constant_Class.B_NAGATIVE)) {
                            Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(4);
                        } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_POSITIVE)) {
                            Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(5);
                        } else if (blood.equalsIgnoreCase(Common.Constant_Class.O_NAGATIVE)) {
                            Objects.requireNonNull(mViewholder.spinnerBlood).setSelection(6);
                        }
                    }

                    Objects.requireNonNull(mViewholder.edtchild_btime).setText(mObjChild.getBirth_time());
                    Objects.requireNonNull(mViewholder.txt_child_bplace).setText(mObjChild.getBirth_place());
                    Objects.requireNonNull(mViewholder.tbtn_interest).setChecked(mObjChild.isInterest());

                    if (mObjChild.getGender().equalsIgnoreCase("male")) {
                        Objects.requireNonNull(mViewholder.radioGroupId).check(R.id.radioM);
                    } else if (mObjChild.getGender().equalsIgnoreCase("female")) {
                        Objects.requireNonNull(mViewholder.radioGroupId).check(R.id.radioF);
                    }

                    Objects.requireNonNull(mViewholder.edt_child_edu).setText(mObjChild.getChild_edu());
                    Objects.requireNonNull(mViewholder.edtchild_work).setText(mObjChild.getChild_work());


                    if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                        mViewholder.edtchild_name.setEnabled(true);

                        mViewholder.edtchild_bdate.setEnabled(true);
                        mViewholder.edt_child_edu.setEnabled(true);
                        mViewholder.edtchild_work.setEnabled(true);
                    } else {
                        mViewholder.edtchild_name.setKeyListener(null);
                        mViewholder.edtchild_name.setCursorVisible(false);

                        mViewholder.edtchild_bdate.setKeyListener(null);
                        mViewholder.edtchild_bdate.setCursorVisible(false);

                        mViewholder.edt_child_edu.setKeyListener(null);
                        mViewholder.edt_child_edu.setCursorVisible(false);

                        mViewholder.edtchild_work.setKeyListener(null);
                        mViewholder.edtchild_work.setCursorVisible(false);
                    }
                    final String child_url = mObjChild.getChild_img_url();
                    try {
                        Glide.with(mActivity).load(child_url).apply(requestOptions).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(mViewholder.img_child);
                    } catch (Exception e) {
                        e.getMessage();
                    }

                    mViewholder.img_child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                                mViewholder.ImgHash = "selectImage";
                                startImageActivity();
                            } else {
                                String Name = mViewholder.edtchild_name.getText().toString();
                                openImageDialog(Name, child_url, true);
                            }
                        }
                    });
                }
            }
        }
    }

    private void openImageDialog(String name, final String url, boolean isChild) {
        Dialog dialog = new Dialog(mActivity);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle(name);

        final ImageView image = dialog.findViewById(R.id.img_dialog);
        if (url.isEmpty()) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.user_profile));
        } else {

            try {
                if (isChild) {
                    Glide.with(mActivity).load(url).apply(requestOptions).thumbnail(0.5f).into(image);
                } else {
                    Glide.with(mActivity).load(url).apply(requestOptions).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(image);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dialog.show();
    }


    private void add_child_layout(final ListChildrenData mObjChild) {

        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.child_row, null);
        final Viewholder mViewholder = new Viewholder();
        mViewholder.child_id = 0;
        mViewholder.img_child = addView.findViewById(R.id.img_child);
        mViewholder.img_child_cancel = addView.findViewById(R.id.img_child_cancel);
        mViewholder.edtchild_name = addView.findViewById(R.id.edtchild_name);
        mViewholder.edtMobile = addView.findViewById(R.id.edtMobile);
        mViewholder.spinnerBlood = addView.findViewById(R.id.spinnerBlood);
        mViewholder.radioGroupId = addView.findViewById(R.id.radioGroupId);
        mViewholder.tbtn_interest = addView.findViewById(R.id.tbtn_interest);
        mViewholder.edtchild_bdate = addView.findViewById(R.id.edtchild_bdate);
        mViewholder.edtchild_btime = addView.findViewById(R.id.edtchild_btime);
        mViewholder.txt_child_bplace = addView.findViewById(R.id.txt_child_bplace);
        mViewholder.edt_child_edu = addView.findViewById(R.id.edt_child_edu);
        mViewholder.edtchild_work = addView.findViewById(R.id.edtchild_work);
        mViewholder.chk_child_bdate_rem = addView.findViewById(R.id.chk_child_bdate_rem);
        mViewholder.chk_child_marriage = addView.findViewById(R.id.chk_child_marriage);

        mViewholder.edtchild_work.requestFocus();
        Objects.requireNonNull(mViewholder.tbtn_interest).setText(null);
        mViewholder.tbtn_interest.setTextOn(null);
        mViewholder.tbtn_interest.setTextOff(null);

        Objects.requireNonNull(mViewholder.spinnerBlood).setOnItemSelectedListener(this);


        List<String> blood_cate = new ArrayList<>();
        blood_cate.add(Common.Constant_Class.TITLE_BLOOD_GROUP);
        blood_cate.add(Common.Constant_Class.A_POSITIVE);
        blood_cate.add(Common.Constant_Class.A_NAGATIVE);
        blood_cate.add(Common.Constant_Class.B_POSITIVE);
        blood_cate.add(Common.Constant_Class.B_NAGATIVE);
        blood_cate.add(Common.Constant_Class.O_POSITIVE);
        blood_cate.add(Common.Constant_Class.O_NAGATIVE);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, blood_cate);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViewholder.spinnerBlood.setAdapter(dataAdapter);

        mViewholder.btn_remove = addView.findViewById(R.id.btn_remove);
        mViewholder.ImgHash = "";
        mViewholder.setClickBDate = false;


        mViewholder.txt_child_bplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_first) {
                    is_first = false;
                    AdvanceSearchActivity.chooseFragment = TAG;
                    Intent mIntent = new Intent(getActivity(), SelectionlistActivity.class);
                    mIntent.putExtra(getString(R.string.listview), false);
                    mIntent.putExtra(getString(R.string.title), "Birth Place");
                    startActivityForResult(mIntent, educate);
                }
            }
        });

        educate++;
        hashMap.put(educate, mViewholder.txt_child_bplace);
        mViewholder.txt_child_bplace.setTag(educate);
       /* mViewholder.edt_child_edu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_first) {
                    is_first = false;
                    AdvanceSearchActivity.chooseFragment = TAG;
                    Intent mIntent = new Intent(getActivity(), SelectionlistActivity.class);
                    mIntent.putExtra(getString(R.string.listview), true);
                    mIntent.putExtra(getString(R.string.title), "Education");
                    startActivityForResult(mIntent, Integer.parseInt(mViewholder.edt_child_edu.getTag().toString()));
                }
            }
        });*/

        mViewholder.chk_child_bdate_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bdate = mViewholder.edtchild_bdate.getText().toString().trim();
                String msg = "";
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(mActivity.getString(R.string.app_name));
                if (!bdate.isEmpty()) {
                    final String date = Common.parseDateToddMMyyyy(bdate, ddMMMyyyy, yyyy_MM_dd);
                    if (mViewholder.chk_child_bdate_rem.isChecked()) {
                        msg = "Do you want to set Reminder for Child Birthdate ? Type Message to wish ";
                        builder.setMessage(msg);
                        builder.setCancelable(false);
                        final EditText input = new EditText(getActivity());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        String name = mSharedPreferences.getString(Common.Constant_Class.FIRST_NAME, "") + " " + mSharedPreferences.getString(Common.Constant_Class.LAST_NAME, "");
                        input.setText("Happy Birthday from " + name);
                        builder.setView(input);
                        builder.setPositiveButton(mActivity.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder(input.getText().toString(), date, Common.Constant_Class.CHILD_BIRTH_DATE, "0", mViewholder.child_id);
                            }
                        });
                        builder.setNegativeButton(mActivity.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewholder.chk_child_bdate_rem.setChecked(false);
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        msg = "Do you want to Unset Reminder for Child Birthdate ?";
                        builder.setMessage(msg);
                        builder.setPositiveButton(mActivity.getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setReminder("", date, Common.Constant_Class.CHILD_BIRTH_DATE, lstchild.get(mViewholder.child_id), mViewholder.child_id);
                            }
                        });
                        builder.setNegativeButton(mActivity.getString(R.string.mdtp_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewholder.chk_child_bdate_rem.setChecked(true);
                                dialog.dismiss();
                            }
                        }).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Child Birthdate not found!", Toast.LENGTH_SHORT).show();
                    // if (mViewholder.chk_child_bdate_rem.isChecked()) {
                    mViewholder.chk_child_bdate_rem.setChecked(false);
                    // } else {
                    //    mViewholder.chk_child_bdate_rem.setChecked(true);
                    // }
                }
            }
        });


        mViewholder.tbtn_interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "";
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(mActivity.getString(R.string.app_name));
                if (mViewholder.tbtn_interest.isChecked()) {
                    msg = "Interested for Matrimony ?";
                    builder.setMessage(msg);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();

                            JSONArray mJsonArray = null;
                            try {
                                mJsonArray = new JSONArray();
                                JSONObject mJSONObject = new JSONObject();
                                mJSONObject.put("id", mViewholder.child_id);
                                mJSONObject.put("is_interested", mViewholder.tbtn_interest.isChecked());
                                mJsonArray.put(mJSONObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MatrimonyUpdateWS(mJsonArray);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mViewholder.tbtn_interest.setChecked(false);
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    msg = "Not interested for Matrimony ?";
                    builder.setMessage(msg);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                            JSONArray mJsonArray = null;
                            try {
                                mJsonArray = new JSONArray();
                                JSONObject mJSONObject = new JSONObject();
                                mJSONObject.put("id", mViewholder.child_id);
                                mJSONObject.put("is_interested", mViewholder.tbtn_interest.isChecked());
                                mJsonArray.put(mJSONObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MatrimonyUpdateWS(mJsonArray);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            mViewholder.tbtn_interest.setChecked(true);
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });

        assert mViewholder.radioGroupId != null;
        mViewholder.radioGroupId.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioM) {
                    mViewholder.gender = "male";
                } else if (checkedId == R.id.radioF) {
                    mViewholder.gender = "female";
                }
            }
        });

        assert mViewholder.img_child != null;
        mViewholder.img_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                    mViewholder.ImgHash = "selectImage";
                    startImageActivity();
                } else {
                    String Name = mViewholder.edtchild_name.getText().toString();
                    String url = "";
                    if (mObjChild != null) {
                        url = mObjChild.getChild_img_url();
                    }
                    openImageDialog(Name, url, false);
                }
            }
        });

        mViewholder.img_child_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewholder.img_child.setImageResource(R.drawable.user_profile);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.user_profile);
                if (icon != null) {
                    mViewholder.ImgHash = Common.getBase64(icon);
                }
            }
        });


        if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) && !ProfileActivity.isEnable) {
            Objects.requireNonNull(mViewholder.btn_remove).setVisibility(View.GONE);
            Objects.requireNonNull(mViewholder.edtchild_name).setKeyListener(null);
            mViewholder.edtchild_name.setCursorVisible(false);
            mViewholder.img_child_cancel.setVisibility(View.GONE);
            Objects.requireNonNull(mViewholder.edtMobile).setKeyListener(null);
            mViewholder.edtMobile.setCursorVisible(false);
            mViewholder.spinnerBlood.setEnabled(false);
            Objects.requireNonNull(mViewholder.edtchild_bdate).setKeyListener(null);
            mViewholder.edtchild_bdate.setCursorVisible(false);
            mViewholder.txt_child_bplace.setClickable(false);
            mViewholder.edt_child_edu.setKeyListener(null);
            mViewholder.edt_child_edu.setCursorVisible(false);

            mViewholder.edtchild_work.setKeyListener(null);
            mViewholder.edtchild_work.setCursorVisible(false);

            Objects.requireNonNull(mViewholder.edtchild_btime).setKeyListener(null);
            mViewholder.edtchild_btime.setCursorVisible(false);

            mViewholder.tbtn_interest.setEnabled(false);
            mViewholder.chk_child_bdate_rem.setVisibility(View.VISIBLE);
            mViewholder.chk_child_marriage.setClickable(false);
        } else {
            mViewholder.chk_child_bdate_rem.setVisibility(View.INVISIBLE);
        }

        mViewholder.edtMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) && !ProfileActivity.isEnable) {
                    try {
                        boolean flag = true;
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Common.canCallPhone(getActivity())) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            String phone_no = mViewholder.edtMobile.getText().toString().replaceAll("-", "");
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + phone_no.trim()));
                            getActivity().startActivity(callIntent);
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mViewholder.edtchild_bdate.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mViewholder.edtchild_bdate.getRight() - mViewholder.edtchild_bdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mViewholder.setClickBDate = true;
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getContext(), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setThemeDark(true);
                        dpd.vibrate(true);
                        dpd.dismissOnPause(false);
                        dpd.showYearPickerFirst(false);
                        dpd.setTitle("Child Birth Date");

                        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                                monthOfYear = (++monthOfYear);
                                String str_month = String.valueOf(monthOfYear);
                                String str_day = String.valueOf(dayOfMonth);
                                if (str_month.length() == 1) {
                                    str_month = "0" + str_month;
                                }
                                if (str_day.length() == 1) {
                                    str_day = "0" + str_day;
                                }
                                String date = str_day + "/" + str_month + "/" + year;
                                mViewholder.edtchild_bdate.setText(date);
                            }
                        });
                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                            dpd.show(mActivity.getFragmentManager(), "Datepickerdialog");
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mViewholder.edtchild_btime.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mViewholder.edtchild_btime.getRight() - mViewholder.edtchild_btime.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        final Calendar c = Calendar.getInstance();
                        final boolean is24Hours = DateFormat.is24HourFormat(getContext());
                        final TimePickerDialogFragment timePicker = TimePickerDialogFragment.newInstance(
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                is24Hours);
                        timePicker.setListener(new android.app.TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                                String time = hourString + ":" + minuteString;
                                mViewholder.edtchild_btime.setText(time);
                            }
                        });

                        if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                            timePicker.showNow(getChildFragmentManager(), null);
                        }
                        /*Calendar now = Calendar.getInstance();
                        TimePickerDialog tpd = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) getContext(), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                        tpd.setThemeDark(true);
                        tpd.vibrate(true);
                        tpd.dismissOnPause(false);
                        tpd.enableSeconds(false);
                        tpd.setTitle("Birth Time");
                        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d("TimePicker", "Dialog was cancelled");
                            }
                        });
                        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                                String time = hourString + ":" + minuteString;
                                mViewholder.edtchild_btime.setText(time);
                            }
                        });*/
                        /*if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false) || ProfileActivity.isEnable) {
                           tpd.show(mActivity.getFragmentManager(), "Timepickerdialog");
                        }*/
                        return true;
                    }
                }

                return false;
            }
        });
        mViewholder.btn_remove.setTag(child_container.getChildCount());
        mViewholder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getString(R.string.app_name));

                builder.setMessage("Do you want to delete this child ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        int id = mViewholder.child_id;
                        lst_delID.add(id);
                        ((LinearLayout) addView.getParent()).removeView(addView);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        addView.setTag(mViewholder);
        child_container.addView(addView, child_container.getChildCount());

    }

    private void startImageActivity() {
        if (Common.hasPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) && Common.hasPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            chooseFragment = TAG;
            CropImage.startPickImageActivity(getActivity());
        }
    }

/*
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (Common.canCAMARA(mActivity)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                    } else {
                        requestPermissions(MainActivity.CALL_CAMARA, MainActivity.CAMARA_REQUEST);
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
*/

    private void MatrimonyUpdateWS(JSONArray jsonArray) {
        if (Common.isOnline(mActivity)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put("childs", jsonArray);
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                if (ProfileActivity.isEnable && mSharedPreferences.getString(Common.Constant_Class.ROLE, Common.Constant_Class.USER).equals(Common.Constant_Class.ADMIN)) {
                    mJsonObject.put(Common.Constant_Class.UPDATE_USER_ID, mSharedPreferences.getString(Common.Constant_Class.PROFILE_ID, ""));
                }
                mJsonObject.put(Common.Constant_Class.IS_UPDATE, "1");
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "MatrimonyUpdateWS: " + mJsonObject.toString());
            Common.showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.PROFILE_URL, mJsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        Common.hideProgressDialog();
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            Toast.makeText(getActivity(), "Matrimony Updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
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

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Common.Constant_Class.INIT_TIMEOUT, Common.Constant_Class.DEFAULT_MAX_RETRIES, Common.Constant_Class.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }


    private void setReminder(String msg, String rem_date, final String rem_type, String rem_value, final int child_id) {
        if (Common.isOnline(mActivity)) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.PROFILE_ID, profile_id);
                mJsonObject.put(Common.Constant_Class.REMINDER_DATE, rem_date);
                mJsonObject.put(Common.Constant_Class.REMINDER_TYPE, rem_type);
                mJsonObject.put(Common.Constant_Class.REMINDER_ID, rem_value);
                mJsonObject.put(Common.Constant_Class.MESSAGE, msg);
                mJsonObject.put(Common.Constant_Class._CHILD_ID, child_id);
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Common.showProgressDialog(getActivity());
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
                            if (rem_type.equalsIgnoreCase(Common.Constant_Class.MARRIAGE_DATE)) {
                                mdate_rem = rem;
                            } else if (rem_type.equalsIgnoreCase(Common.Constant_Class.WIFE_BIRTH_DATE)) {
                                sbdate_rem = rem;
                            } else if (rem_type.equalsIgnoreCase(Common.Constant_Class.CHILD_BIRTH_DATE)) {
                                lstchild.put(child_id, rem);
                            }
                        } else {
                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                    Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    getActivity().finish();
                                }
                            }
                        }
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(FamilyFragment.class.getSimpleName(), "Error: " + error.getMessage());
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Common.Constant_Class.INIT_TIMEOUT, Common.Constant_Class.DEFAULT_MAX_RETRIES, Common.Constant_Class.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == CONTACT_PICKER_RESULT || requestCode == CONTACT_PICKER_RESULT_CHILD) && resultCode == RESULT_OK && null != data) {
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
                    if (requestCode == CONTACT_PICKER_RESULT) {
                        edtsponse_mobile.setText(phoneNumber.replace("+", ""));
                    }
                }
            }
            phoneCursor.close();
        }
        Uri imageUri = null;
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = CropImage.getPickImageResultUri(getActivity(), data);

            if (CropImage.hasPermissionInManifest(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) && CropImage.hasPermissionInManifest(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startCropImageActivity(imageUri);
            }
        }

        if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            mCropImageUri = imageUri;
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            // no permissions required or already grunted, can start crop image activity
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                setImageFromActivityResult(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 11) {
            if (data != null) {
                is_first = true;
                txt_sponse_nplace.setText(data.getStringExtra(getString(R.string.selection)));
            }
        } /*else if (requestCode == 12) {
            if (data != null) {
                is_first = true;
                txt_sponse_nplace.setText(data.getStringExtra(getString(R.string.selection)));
            }
        } */ else {
            if (hashMap.get(requestCode) != null) {
                hashMap.get(requestCode).setText(data.getStringExtra(getString(R.string.selection)));
            }
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(getActivity());
    }

    private void setImageFromActivityResult(Uri resultUri) {
        Bitmap bmp;
        if (img_selection.equalsIgnoreCase("spouse")) {
            img_spouse.setImageURI(resultUri);
            BitmapDrawable drawable = (BitmapDrawable) img_spouse.getDrawable();
            bmp = drawable.getBitmap();
            str_spouse_hash = Common.getBase64(bmp);
        } else if (img_selection.equalsIgnoreCase("fspouse")) {
            img_fspouse.setImageURI(resultUri);
            BitmapDrawable drawable = (BitmapDrawable) img_fspouse.getDrawable();
            bmp = drawable.getBitmap();
            str_fspouse_hash = Common.getBase64(bmp);
        } else if (img_selection.equalsIgnoreCase("mspouse")) {
            img_mspouse.setImageURI(resultUri);
            BitmapDrawable drawable = (BitmapDrawable) img_mspouse.getDrawable();
            bmp = drawable.getBitmap();
            str_mspouse_hash = Common.getBase64(bmp);
        }
        img_selection = "";
        for (int i = 0; i < child_container.getChildCount(); i++) {
            Viewholder cViewholder = (Viewholder) child_container.getChildAt(i).getTag();
            if (cViewholder.ImgHash.equalsIgnoreCase("selectImage")) {
                cViewholder.img_child.setImageURI(resultUri);
                BitmapDrawable drawable = (BitmapDrawable) cViewholder.img_child.getDrawable();
                bmp = drawable.getBitmap();
                cViewholder.ImgHash = Common.getBase64(bmp);
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class Viewholder {
        public int child_id;
        public String gender = "male";
        @Nullable
        public EditText edtchild_name = null;
        @Nullable
        public EditText edtMobile = null;
        @Nullable
        public Spinner spinnerBlood = null;
        @Nullable
        public EditText edtchild_btime = null;

        public TextView txt_child_bplace = null;
        public EditText edt_child_edu = null;


        public ToggleButton tbtn_interest = null;
        @Nullable
        public EditText edtchild_work = null;
        public String ImgHash = "";
        @Nullable
        public CheckBox chk_child_marriage = null;
        public CheckBox chk_child_bdate_rem = null;
        public EditText edtchild_bdate = null;
        @Nullable
        CircleImageView img_child = null;
        ImageView img_child_cancel = null;
        @Nullable
        RadioGroup radioGroupId = null;
        @Nullable
        Button btn_remove = null;
        boolean setClickBDate = false;
    }
}
