package com.krs.vastipatrak.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.interfaces.OnItemClickListener;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.showProgressDialog;

public class RelativeFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private RecyclerView recycler_view;
    private TextView txtLable;
    private ArrayList<Relative> lstRelative = null;
    private SharedPreferences.Editor mEditor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_relative, container, false);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        getRelationsWS();
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        recycler_view = rootView.findViewById(R.id.recycler_view);
        txtLable = rootView.findViewById(R.id.txtLable);
    }

    private void getRelationsWS() {
        if (Common.isOnline(getActivity())) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.GET_RELATIONS_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {

                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        if (success.equalsIgnoreCase("true")) {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            if (mJsonArray.length() > 0) {
                                lstRelative = new ArrayList<>();
                            }
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsonreldata = mJsonArray.getJSONObject(i);
                                String id = mJsonreldata.getString(Common.Constant_Class.ID);
                                String user_id = mJsonreldata.getString(Common.Constant_Class.USER_ID);
                                String to_user_id = mJsonreldata.getString(Common.Constant_Class.TO_USER_ID);
                                String relation = mJsonreldata.getString(Common.Constant_Class.RELATION);
                                String status = mJsonreldata.getString(Common.Constant_Class.RELATIONSHIP_STATUS);
                                String to_first_name = mJsonreldata.getString(Common.Constant_Class.TO_FIRST_NAME);
                                String to_last_name = mJsonreldata.getString(Common.Constant_Class.TO_LAST_NAME);
                                String from_first_name = mJsonreldata.getString(Common.Constant_Class.FROM_FIRST_NAME);
                                String from_last_name = mJsonreldata.getString(Common.Constant_Class.FROM_LAST_NAME);
                                Relative mRelative = new Relative();
                                mRelative.setId(id);
                                mRelative.setUser_id(user_id);
                                mRelative.setFrom_first_name(from_first_name);
                                mRelative.setFrom_last_name(from_last_name);
                                mRelative.setToFirst_name(to_first_name);
                                mRelative.setToLast_name(to_last_name);
                                mRelative.setRelation(relation);
                                mRelative.setStatus(status);
                                mRelative.setTo_user_id(to_user_id);
                                lstRelative.add(mRelative);
                            }

                            if (mJsonArray.length() > 0) {
                                recycler_view.setVisibility(View.VISIBLE);
                                txtLable.setVisibility(View.GONE);
                                setAdapter();
                            } else {
                                recycler_view.setVisibility(View.GONE);
                                txtLable.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                    hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(getClass().getSimpleName(), "Error: " + error.getMessage());
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

    private void setActionWS(String id, String status) {
        if (Common.isOnline(getActivity())) {
            JSONObject mJsonObject = null;
            try {
                showProgressDialog(getActivity());
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.RELATIONSHIP_ID, id);
                mJsonObject.put(Common.Constant_Class.RELATIONSHIP_STATUS, status);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.REQUEST_ACTION_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        try {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            if (mJsonArray.length() > 0) {
                                lstRelative = new ArrayList<>();
                            }
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsonreldata = mJsonArray.getJSONObject(i);
                                String id = mJsonreldata.getString(Common.Constant_Class.ID);
                                String user_id = mJsonreldata.getString(Common.Constant_Class.USER_ID);
                                String to_user_id = mJsonreldata.getString(Common.Constant_Class.TO_USER_ID);
                                String relation = mJsonreldata.getString(Common.Constant_Class.RELATION);
                                String status = mJsonreldata.getString(Common.Constant_Class.RELATIONSHIP_STATUS);
                                String to_first_name = mJsonreldata.getString(Common.Constant_Class.TO_FIRST_NAME);
                                String to_last_name = mJsonreldata.getString(Common.Constant_Class.TO_LAST_NAME);
                                String from_first_name = mJsonreldata.getString(Common.Constant_Class.FROM_FIRST_NAME);
                                String from_last_name = mJsonreldata.getString(Common.Constant_Class.FROM_LAST_NAME);
                                Relative mRelative = new Relative();
                                mRelative.setId(id);
                                mRelative.setUser_id(user_id);
                                mRelative.setFrom_first_name(from_first_name);
                                mRelative.setFrom_last_name(from_last_name);
                                mRelative.setToFirst_name(to_first_name);
                                mRelative.setToLast_name(to_last_name);
                                mRelative.setRelation(relation);
                                mRelative.setStatus(status);
                                mRelative.setTo_user_id(to_user_id);
                                lstRelative.add(mRelative);
                            }

                            if (mJsonArray.length() > 0) {
                                recycler_view.setVisibility(View.VISIBLE);
                                txtLable.setVisibility(View.GONE);
                                setAdapter();
                            } else {
                                recycler_view.setVisibility(View.GONE);
                                txtLable.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                    hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(getClass().getSimpleName(), "Error: " + error.getMessage());
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
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }

    private void setAdapter() {
        RelativeAdapter mRelativeAdapter = new RelativeAdapter(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                /*mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                mEditor.apply();
                MyProfileActivity.isEnable = false;
                Intent mIntent = new Intent(getActivity(), MyProfileActivity.class);
                getActivity().startActivity(mIntent);*/
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mRelativeAdapter);
    }

    private class Relative {
        String id;
        String to_user_id;
        String user_id;
        String relation;
        String status;
        String to_first_name;
        String to_last_name;
        String from_first_name;
        String from_last_name;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getFrom_first_name() {
            return from_first_name;
        }

        public void setFrom_first_name(String from_first_name) {
            this.from_first_name = from_first_name;
        }

        public String getFrom_last_name() {
            return from_last_name;
        }

        public void setFrom_last_name(String from_last_name) {
            this.from_last_name = from_last_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(String to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getToFirst_name() {
            return to_first_name;
        }

        public void setToFirst_name(String first_name) {
            this.to_first_name = first_name;
        }

        public String getToLast_name() {
            return to_last_name;
        }

        public void setToLast_name(String last_name) {
            this.to_last_name = last_name;
        }
    }

    public class RelativeAdapter extends RecyclerView.Adapter<RelativeAdapter.MyViewHolder> {

        private final OnItemClickListener listener;


        RelativeAdapter(OnItemClickListener listener) {
            this.listener = listener;
        }

        /*public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
            SpannableString spannableString = new SpannableString(textView.getText());
            for (int i = 0; i < links.length; i++) {
                ClickableSpan clickableSpan = clickableSpans[i];
                String link = links[i];

                int startIndexOfLink = textView.getText().toString().indexOf(link);
                spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spannableString, TextView.BufferType.SPANNABLE);
        }*/

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_relatives, parent, false);
            final MyViewHolder holder = new MyViewHolder(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, holder.getPosition());
                }
            });
            return holder;
        }

        private void moveToprofile(String id) {
            mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
            mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
            mEditor.apply();
            MyProfileActivity.isEnable = false;
            Intent mIntent = new Intent(getActivity(), MyProfileActivity.class);
            getActivity().startActivity(mIntent);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final Relative reldata = lstRelative.get(position);
            String from_name = reldata.getFrom_first_name() + " " + reldata.getFrom_last_name();
            from_name = Common.camelCase(from_name);
            String last_name = reldata.getToFirst_name() + " " + reldata.getToLast_name();
            last_name = Common.camelCase(last_name);
            String status = reldata.getStatus();
            final String rel_id = reldata.getId();
            final String user_id = reldata.getUser_id();
            String relation = reldata.getRelation();

           /* ClickableSpan clickableSpan1 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String str = ((TextView) textView).getText().toString().toLowerCase();
                    int len = str.indexOf(reldata.getRelation().toLowerCase());
                    String str1 = str.substring(0, len);
                    String id = "";
                    if (str1.contains(reldata.getFrom_first_name().toLowerCase())) {
                        id = reldata.getUser_id();
                    } else {
                        id = reldata.getTo_user_id();
                    }
                    moveToprofile(id);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String str = ((TextView) textView).getText().toString().toLowerCase();
                    int len = str.toLowerCase().indexOf(reldata.getRelation().toLowerCase());
                    String str1 = str.substring(0, len);
                    String id = "";
                    if (str1.contains(reldata.getFrom_first_name().toLowerCase())) {
                        id = reldata.getTo_user_id();
                    } else {
                        id = reldata.getUser_id();
                    }
                    moveToprofile(id);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };*/

            holder.txt_from.setText(from_name);
            holder.txt_to.setText(last_name);
            holder.txt_msg.setText(Common.getCapsSentences(relation));
            holder.img_delete.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            if (status.contains(Common.Constant_Class.ACCEPTED)) {
                holder.txt_request.setText("Requested by ");
                holder.txt_approve.setText("Approved by ");
                holder.txt_from.setTextColor(getResources().getColor(R.color.primary_blue));
                holder.txt_to.setTextColor(getResources().getColor(R.color.primary_blue));
                holder.txt_status.setText("Reject");
                holder.txt_status.setClickable(true);
                //holder.img_status.setImageDrawable(getResources().getDrawable(R.drawable.ico_approve));
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape1));
                // holder.txt_status.setText(Html.fromHtml(last_name + "  <b>" + Common.getCapsSentences(relation) + "</b> of " + from_name));
                // makeLinks(holder.txt_status, new String[]{last_name, from_name}, new ClickableSpan[]{clickableSpan1, clickableSpan2});
            } else if (status.contains(Common.Constant_Class.REQUESTED)) {
                holder.txt_request.setText("Requested by ");
                holder.txt_approve.setText("To ");
                holder.txt_from.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                holder.txt_to.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                //holder.txt_status.setText(Html.fromHtml(from_name + " requested <b>" + Common.getCapsSentences(relation) + "</b> to " + last_name));
                holder.txt_status.setText("Waiting");
                holder.txt_status.setClickable(false);
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape10));
                // makeLinks(holder.txt_status, new String[]{from_name, last_name}, new ClickableSpan[]{clickableSpan1, clickableSpan2});
            } else if (status.contains(Common.Constant_Class.REJECTED)) {
                holder.txt_request.setText("Requested by ");
                holder.txt_approve.setText("Rejected by ");
                holder.txt_from.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                holder.txt_to.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                //holder.txt_status.setText(Html.fromHtml(last_name + " rejected <b>" + Common.getCapsSentences(relation) + "</b> to " + from_name));
                holder.txt_status.setText("Approve");
                holder.txt_status.setClickable(true);
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape9));
                // makeLinks(holder.txt_status, new String[]{from_name, last_name}, new ClickableSpan[]{clickableSpan1, clickableSpan2});
            }

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false)) {
                holder.txt_status.setEnabled(true);
                holder.img_delete.setEnabled(true);
                holder.txt_status.setClickable(true);
                holder.img_delete.setClickable(true);
            } else {
                holder.txt_status.setEnabled(false);
                holder.img_delete.setEnabled(false);
                holder.txt_status.setClickable(false);
                holder.img_delete.setClickable(false);
            }

            final String finalLast_name = last_name;
            holder.txt_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "";
                    String status = "";
                    if (!mSharedPreferences.getString(Common.Constant_Class.USER_ID, "").equalsIgnoreCase(user_id)) {
                        if (reldata.getStatus().equalsIgnoreCase("ACCEPTED")) {
                            message = "Do you want to REJECT relation ?";
                            status = "REJECTED";
                        } else {
                            message = "Do you want to ACCEPT relation ?";
                            status = "ACCEPTED";
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                        builder.setTitle(getActivity().getString(R.string.app_name));
                        builder.setCancelable(false);
                        builder.setMessage(message);
                        final String finalStatus = status;
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setActionWS(rel_id, finalStatus);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            });

            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(getActivity().getString(R.string.app_name));
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to DELETE relation ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setActionWS(rel_id, "DELETE");
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    }).show();

                }
            });


            holder.ll_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  String str = ((TextView) textView).getText().toString().toLowerCase();
                    int len = str.indexOf(reldata.getRelation().toLowerCase());
                    String str1 = str.substring(0, len);*/
                  /*  String id = "";
                    if (holder.txt_from.getText().toString().toLowerCase().contains(reldata.getFrom_first_name().toLowerCase())) {
                        id = reldata.getUser_id();
                    } else {
                        id = reldata.getTo_user_id();
                    }*/
                    moveToprofile(reldata.getUser_id());
                }
            });

            holder.ll_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToprofile(reldata.getTo_user_id());
                }
            });
        }

        @Override
        public int getItemCount() {
            if (lstRelative != null && lstRelative.size() > 0) {
                return lstRelative.size();
            } else {
                recycler_view.setVisibility(View.GONE);
                txtLable.setVisibility(View.VISIBLE);
                return 0;
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            final TextView txt_msg, txt_to, txt_from, txt_request, txt_approve;
            final LinearLayout ll_relative, ll_from, ll_to;
            final ImageView img_delete;
            TextView txt_status;


            MyViewHolder(@NonNull View view) {
                super(view);
                txt_msg = view.findViewById(R.id.txt_msg);
                txt_to = view.findViewById(R.id.txt_to);

                txt_request = view.findViewById(R.id.txt_request);
                txt_approve = view.findViewById(R.id.txt_approve);

                txt_from = view.findViewById(R.id.txt_from);
                ll_relative = view.findViewById(R.id.ll_relative);
                ll_from = view.findViewById(R.id.ll_from);
                ll_to = view.findViewById(R.id.ll_to);
                img_delete = view.findViewById(R.id.img_delete);
                txt_status = view.findViewById(R.id.txt_status);
            }
        }
    }
}
