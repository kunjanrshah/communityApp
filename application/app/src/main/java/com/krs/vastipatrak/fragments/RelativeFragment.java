package com.krs.vastipatrak.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.interfaces.OnItemClickListener;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.krs.vastipatrak.utils.Common.getRandomColor;
import static com.krs.vastipatrak.utils.Common.hideProgressDialog;
import static com.krs.vastipatrak.utils.Common.showProgressDialog;

public class RelativeFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private RecyclerView recycler_view;
    private TextView txtLable;
    private ArrayList<Relative> lstRelative = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_relative, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(R.string.title_relatives);
        setHasOptionsMenu(true);
        MemoryAllocation(rootView);
        getRelationsWS();
        return rootView;
    }

    private void MemoryAllocation(View rootView) {
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
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
                            String to_user_id = mJsondata.getString(Common.Constant_Class.TO_USER_ID);
                            String relation = mJsondata.getString(Common.Constant_Class.RELATION);
                            String status = mJsondata.getString(Common.Constant_Class.RELATIONSHIP_STATUS);
                            String first_name = mJsondata.getString(Common.Constant_Class.FIRST_NAME);
                            String last_name = mJsondata.getString(Common.Constant_Class.LAST_NAME);
                            Relative mRelative = new Relative();
                            mRelative.setFirst_name(first_name);
                            mRelative.setLast_name(last_name);
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
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        getRelationsWS();
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
                Toast.makeText(getActivity(), lstRelative.get(position).getTo_user_id() + "", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mRelativeAdapter);
    }

    private class Relative {
        String to_user_id;
        String relation;
        String status;
        String first_name;
        String last_name;

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

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
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
            String name = data.getFirst_name() + " " + data.getLast_name();
            String status = data.getStatus();

            if (status.contains("ACCEPTED")) {
                holder.txt_name.setText(name);
                holder.txt_status.setText(" request Approved");
                holder.img_status.setImageDrawable(getResources().getDrawable(R.drawable.approve));
            } else {
                holder.txt_name.setText(name);
                holder.txt_status.setText(" has sent request");
                holder.img_status.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }

            holder.txtDesc.setText(data.getRelation());
            getRandomColor(Objects.requireNonNull(getActivity()), position, holder.ll_relative);

            holder.img_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "";
                    if (data.getStatus().equalsIgnoreCase("ACCEPTED")) {
                        message = "Do you want to REJECT relation ?";
                    } else {
                        message = "Do you want to ACCEPT relation ?";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(getActivity().getString(R.string.app_name));
                    builder.setCancelable(false);
                    builder.setMessage(message);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setActionWS(data.getTo_user_id(), data.getStatus());
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
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
            final TextView txt_name;
            final TextView txt_status;
            final TextView txtDesc;
            final LinearLayout ll_relative;
            final ImageView img_status;

            MyViewHolder(@NonNull View view) {
                super(view);
                txt_status = view.findViewById(R.id.txt_status);
                txt_name = view.findViewById(R.id.txt_name);
                txtDesc = view.findViewById(R.id.txt_desc);
                ll_relative = view.findViewById(R.id.ll_relative);
                img_status = view.findViewById(R.id.img_status);
            }
        }
    }
}
