package com.krs.vastipatrak.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.ProfileActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.interfaces.OnItemClickListener;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.krs.vastipatrak.utils.AppConstants.INIT_TIMEOUT;

public class RelativeFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private RecyclerView recycler_view;
    private TextView txtLable;
    private ArrayList<Relative> lstRelative = null;
    private SharedPreferences.Editor mEditor;
    private String TAG = RelativeFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_relative, container, false);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        getRelationsWS();
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        mSharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        recycler_view = rootView.findViewById(R.id.recycler_view);
        txtLable = rootView.findViewById(R.id.txtLable);
    }

    public void showProgressDialog(Context mContext) {
        try {

            if (pDialog == null) {
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage(mContext.getString(R.string.loading));
                pDialog.setCancelable(false);
            }

            if (!pDialog.isShowing()) pDialog.show();
            ProgressBar progressbar = pDialog.findViewById(android.R.id.progress);
            progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#3b5998"), android.graphics.PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (pDialog != null && pDialog.isShowing()) pDialog.cancel();
            pDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRelationsWS() {
        if (Utility.isOnline(getActivity())) {
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                String id = "";
                if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, true)) {
                    id = mSharedPreferences.getString(AppConstants.USER_ID, "");
                } else {
                    id = mSharedPreferences.getString(AppConstants.PROFILE_ID, "");
                }
                mJsonObject.put(AppConstants.PROFILE_ID, id);
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.GET_RELATIONS_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        Log.d(TAG, "RelationsWS: " + response.toString());
                        String success = response.getString(AppConstants.SUCCESS);
                        if (success.equalsIgnoreCase("true")) {
                            JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);
                            if (mJsonArray.length() > 0) {
                                lstRelative = new ArrayList<>();
                            }
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsonreldata = mJsonArray.getJSONObject(i);
                                String id = mJsonreldata.getString(AppConstants.ID);
                                String user_id = mJsonreldata.getString(AppConstants.USER_ID);
                                String to_user_id = mJsonreldata.getString(AppConstants.TO_USER_ID);
                                String relation = mJsonreldata.getString(AppConstants.RELATION);
                                String status = mJsonreldata.getString(AppConstants.RELATIONSHIP_STATUS);
                                String to_first_name = mJsonreldata.getString(AppConstants.TO_FIRST_NAME);
                                String to_last_name = mJsonreldata.getString(AppConstants.TO_LAST_NAME);
                                String from_first_name = mJsonreldata.getString(AppConstants.FROM_FIRST_NAME);
                                String from_last_name = mJsonreldata.getString(AppConstants.FROM_LAST_NAME);
                                String to_profile_pic = mJsonreldata.getString(AppConstants.FROM_PROFILE_PIC);
                                String from_profile_pic = mJsonreldata.getString(AppConstants.TO_PROFILE_PIC);
                                Relative mRelative = new Relative();
                                mRelative.setId(id);
                                mRelative.setUser_id(user_id);
                                mRelative.setTo_profile_pic(to_profile_pic);
                                mRelative.setFrom_profile_pic(from_profile_pic);
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
                    hideProgressDialog();
                    VolleyLog.d(getClass().getSimpleName(), "Error: " + error.getMessage());
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

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(INIT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  DEFAULT_BACKOFF_MULT));
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, "jobj_req");
        }
    }

    private void setActionWS(String id, String status) {
        if (Utility.isOnline(getActivity())) {
            JSONObject mJsonObject = null;
            try {
                showProgressDialog(getActivity());
                mJsonObject = new JSONObject();
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences.getString(AppConstants.USER_ID, ""));
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences.getString(AppConstants.ACCESS_TOKEN, ""));
                mJsonObject.put(AppConstants.RELATIONSHIP_ID, id);
                mJsonObject.put(AppConstants.RELATIONSHIP_STATUS, status);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.REQUEST_ACTION_URL, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    try {
                        Log.d(TAG, "ActionWS: " + response.toString());
                        String message = response.getString(AppConstants.MESSAGE);
                        try {
                            JSONArray mJsonArray = response.getJSONArray(AppConstants.DATA);
                            if (mJsonArray.length() > 0) {
                                lstRelative = new ArrayList<>();
                            }
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsonreldata = mJsonArray.getJSONObject(i);
                                String id = mJsonreldata.getString(AppConstants.ID);
                                String user_id = mJsonreldata.getString(AppConstants.USER_ID);
                                String to_user_id = mJsonreldata.getString(AppConstants.TO_USER_ID);
                                String relation = mJsonreldata.getString(AppConstants.RELATION);
                                String status = mJsonreldata.getString(AppConstants.RELATIONSHIP_STATUS);
                                String to_first_name = mJsonreldata.getString(AppConstants.TO_FIRST_NAME);
                                String to_last_name = mJsonreldata.getString(AppConstants.TO_LAST_NAME);
                                String from_first_name = mJsonreldata.getString(AppConstants.FROM_FIRST_NAME);
                                String from_last_name = mJsonreldata.getString(AppConstants.FROM_LAST_NAME);
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
                    params.put(AppConstants.API_KEY, AppConstants.API_KEY_VALUE);
                    params.put(AppConstants.DEVICE_TYPE, AppConstants.DEVICE_TYPE_VALUE);
                    params.put(AppConstants.DEVICE_ID, AppConstants.DEVICE_ID_VALUE);
                    params.put(AppConstants.DEVICE_TOKEN, mSharedPreferences.getString(AppConstants.DEVICE_TOKEN, ""));
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
                /*mEditor.putString(AppConstants.PROFILE_ID, id);
                mEditor.putBoolean(AppConstants.MYPROFILE_SP, false);
                mEditor.apply();
                ProfileActivity.isEnable = false;
                Intent mIntent = new Intent(getActivity(), ProfileActivity.class);
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
        String to_profile_pic;
        String from_profile_pic;

        public String getTo_profile_pic() {
            return to_profile_pic;
        }

        public void setTo_profile_pic(String to_profile_pic) {
            this.to_profile_pic = to_profile_pic;
        }

        public String getFrom_profile_pic() {
            return from_profile_pic;
        }

        public void setFrom_profile_pic(String from_profile_pic) {
            this.from_profile_pic = from_profile_pic;
        }

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

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_relatives1, parent, false);
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
            mEditor.putString(AppConstants.PROFILE_ID, id);
            mEditor.putBoolean(AppConstants.MYPROFILE_SP, false);
            mEditor.apply();
            ProfileActivity.isEnable = false;
            Intent mIntent = new Intent(getActivity(), ProfileActivity.class);
            getActivity().startActivity(mIntent);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final Relative reldata = lstRelative.get(position);

            String from_name = reldata.getFrom_first_name();// + " " + reldata.getFrom_last_name();
            from_name = Utility.camelCase(from_name);

            Glide.with(getActivity()).load(reldata.getTo_profile_pic()).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(holder.img_request);
            Glide.with(getActivity()).load(reldata.getFrom_profile_pic()).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(holder.img_approve);

            String to_name = reldata.getToFirst_name(); //+ " " + reldata.getToLast_name();
            to_name = Utility.camelCase(to_name);

            String status = reldata.getStatus();
            final String rel_id = reldata.getId();
            final String user_id = reldata.getUser_id();
            String relation = reldata.getRelation();

            holder.txt_from.setText(from_name);
            holder.txt_to.setText(to_name);
            holder.txt_msg.setText(Utility.getCapsSentences(relation));
            holder.img_delete.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            if (status.contains(AppConstants.ACCEPTED)) {
                holder.txt_request.setText("Requested");
                holder.txt_approve.setText("Approved");
                holder.txt_from.setTextColor(getResources().getColor(R.color.primary_blue));
                holder.txt_to.setTextColor(getResources().getColor(R.color.primary_blue));
                holder.txt_status.setClickable(true);
                String input = "Reject";
                String output = input.substring(0, 1).toUpperCase() + input.substring(1);
                holder.txt_status.setText(output);
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape1));
            } else if (status.contains(AppConstants.REQUESTED)) {
                holder.txt_request.setText("Requested");
                holder.txt_approve.setText("To ");
                holder.txt_from.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                holder.txt_to.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                String input = "Waiting";
                String output = input.substring(0, 1).toUpperCase() + input.substring(1);
                holder.txt_status.setText(output);
                holder.txt_status.setTextColor(getResources().getColor(R.color.mdtp_white));
                holder.txt_status.setBackground(getResources().getDrawable(R.drawable.mybutton1));
                holder.txt_status.setClickable(false);
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape10));
            } else if (status.contains(AppConstants.REJECTED)) {
                holder.txt_request.setText("Requested");
                holder.txt_approve.setText("Rejected");
                holder.txt_from.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                holder.txt_to.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                String input = "Approve";
                String output = input.substring(0, 1).toUpperCase() + input.substring(1);
                holder.txt_status.setText(output);

                holder.txt_status.setClickable(true);
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape9));

            }

            if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, false)) {
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

            final String finalLast_name = to_name;
            holder.txt_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "";
                    String status = "";
                    if (!mSharedPreferences.getString(AppConstants.USER_ID, "").equalsIgnoreCase(user_id)) {
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
            ImageView img_request, img_approve;
            TextView txt_status;


            MyViewHolder(@NonNull View view) {
                super(view);

                img_request = view.findViewById(R.id.img_request);
                img_approve = view.findViewById(R.id.img_approve);

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
