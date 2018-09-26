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
import android.text.Html;
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
                        JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                        if (mJsonArray.length() > 0) {
                            lstRelative = new ArrayList<>();
                        }
                        for (int i = 0; i < mJsonArray.length(); i++) {
                            JSONObject mJsondata = mJsonArray.getJSONObject(i);
                            String id = mJsondata.getString(Common.Constant_Class.ID);
                            String user_id = mJsondata.getString(Common.Constant_Class.USER_ID);
                            String to_user_id = mJsondata.getString(Common.Constant_Class.TO_USER_ID);
                            String relation = mJsondata.getString(Common.Constant_Class.RELATION);
                            String status = mJsondata.getString(Common.Constant_Class.RELATIONSHIP_STATUS);
                            String to_first_name = mJsondata.getString(Common.Constant_Class.TO_FIRST_NAME);
                            String to_last_name = mJsondata.getString(Common.Constant_Class.TO_LAST_NAME);
                            String from_first_name = mJsondata.getString(Common.Constant_Class.FROM_FIRST_NAME);
                            String from_last_name = mJsondata.getString(Common.Constant_Class.FROM_LAST_NAME);
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
                                JSONObject mJsondata = mJsonArray.getJSONObject(i);
                                String id = mJsondata.getString(Common.Constant_Class.ID);
                                String user_id = mJsondata.getString(Common.Constant_Class.USER_ID);
                                String to_user_id = mJsondata.getString(Common.Constant_Class.TO_USER_ID);
                                String relation = mJsondata.getString(Common.Constant_Class.RELATION);
                                String status = mJsondata.getString(Common.Constant_Class.RELATIONSHIP_STATUS);
                                String to_first_name = mJsondata.getString(Common.Constant_Class.TO_FIRST_NAME);
                                String to_last_name = mJsondata.getString(Common.Constant_Class.TO_LAST_NAME);
                                String from_first_name = mJsondata.getString(Common.Constant_Class.FROM_FIRST_NAME);
                                String from_last_name = mJsondata.getString(Common.Constant_Class.FROM_LAST_NAME);
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

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Relative data = lstRelative.get(position);
            String from_name = data.getFrom_first_name() + " " + data.getFrom_last_name();
            from_name = Common.camelCase(from_name);
            String last_name = data.getToFirst_name() + " " + data.getToLast_name();
            last_name = Common.camelCase(last_name);
            String status = data.getStatus();
            final String rel_id = data.getId();
            final String user_id = data.getUser_id();
            String relation = data.getRelation();
            if (status.contains(Common.Constant_Class.ACCEPTED)) {
                holder.txt_name1.setText(last_name);
                holder.txt_name2.setText(from_name);
                String txt = " is <b>" + Common.getCapsSentences(relation) + "</b> of ";
                holder.txt_status.setText(Html.fromHtml(txt));
                holder.img_status.setImageDrawable(getResources().getDrawable(R.drawable.ico_approve));
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape1));
            } else if (status.contains(Common.Constant_Class.REQUESTED)) {
                holder.txt_name1.setText(from_name);
                holder.txt_name2.setText(last_name);
                String txt = " has requsted for <b>" + Common.getCapsSentences(relation) + "</b> to ";
                holder.txt_status.setText(Html.fromHtml(txt));
                holder.img_status.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
                holder.ll_relative.setBackground(getActivity().getDrawable(R.drawable.shape10));

            }

            if (mSharedPreferences.getBoolean(Common.Constant_Class.MYPROFILE_SP, false)) {
                holder.img_status.setEnabled(true);
                holder.img_status.setClickable(true);
                holder.img_status.setLongClickable(true);
            } else {
                holder.img_status.setEnabled(false);
                holder.img_status.setClickable(false);
                holder.img_status.setLongClickable(false);
            }

            final String finalLast_name = last_name;
            holder.img_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "";
                    String status = "";
                    if (!mSharedPreferences.getString(Common.Constant_Class.USER_ID, "").equalsIgnoreCase(user_id)) {
                        if (data.getStatus().equalsIgnoreCase("ACCEPTED")) {
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
                    } else {
                        Toast.makeText(getActivity(), "Ask " + finalLast_name + " to approve !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.img_status.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
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
                    return false;
                }
            });

            holder.txt_name1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = "";
                    if (data.getStatus().equalsIgnoreCase(Common.Constant_Class.ACCEPTED)) {
                        id = data.getTo_user_id();
                    } else {
                        id = data.getUser_id();
                    }
                    mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                    mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                    mEditor.apply();
                    MyProfileActivity.isEnable = false;
                    Intent mIntent = new Intent(getActivity(), MyProfileActivity.class);
                    getActivity().startActivity(mIntent);
                }
            });

            holder.txt_name2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = "";
                    if (data.getStatus().equalsIgnoreCase(Common.Constant_Class.ACCEPTED)) {
                        id = data.getTo_user_id();
                    } else {
                        id = data.getUser_id();
                    }
                    mEditor.putString(Common.Constant_Class.PROFILE_ID, id);
                    mEditor.putBoolean(Common.Constant_Class.MYPROFILE_SP, false);
                    mEditor.apply();
                    MyProfileActivity.isEnable = false;
                    Intent mIntent = new Intent(getActivity(), MyProfileActivity.class);
                    getActivity().startActivity(mIntent);
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
            final TextView txt_status;
            final TextView txt_name1;
            final TextView txt_name2;
            final LinearLayout ll_relative;
            final ImageView img_status;

            MyViewHolder(@NonNull View view) {
                super(view);
                txt_status = view.findViewById(R.id.txt_status);
                txt_name1 = view.findViewById(R.id.txt_name1);
                txt_name2 = view.findViewById(R.id.txt_name2);
                ll_relative = view.findViewById(R.id.ll_relative);
                img_status = view.findViewById(R.id.img_status);
            }
        }
    }
}
