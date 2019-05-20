package com.krs.vastipatrak.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.FamilyTreeActivity;
import com.krs.vastipatrak.activity.ProfileActivity;
import com.krs.vastipatrak.model.ListFamilyTreeData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.AppConstants;
import com.krs.vastipatrak.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.blox.graphview.BaseGraphAdapter;
import de.blox.graphview.Graph;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;
import io.realm.RealmList;

import static com.krs.vastipatrak.utils.Utility.ShareScreenShot;


public class FamilyTreeFragment extends Fragment {

    Graph graph;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    GraphView graphView;
    BaseGraphAdapter<ViewHolder> adapter;
    private ImageView imgShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tree, container, false);

        Memory_Allocation(rootView);

        // you can set the graph via the constructor or use the adapter.setGraph(Graph) method
        adapter = new BaseGraphAdapter<ViewHolder>(getActivity(), R.layout.node, graph) {

            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                JSONObject mjson = (JSONObject) data;
                String imgUrl = "", name = "", id = "";
                try {
                    imgUrl = mjson.getString(getString(R.string.FT_IMG));
                    name = mjson.getString(getString(R.string.FT_NAME));
                    id = mjson.getString(getString(R.string.FT_PROFILE_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] strArray = name.split(" ");
                final StringBuilder builder = new StringBuilder();
                for (String s : strArray) {
                    String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    builder.append(cap + " ");
                }
                viewHolder.mTextView.setText(builder);
                try {
                    Glide.with(getActivity()).load(imgUrl).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(viewHolder.imgView);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final String finalId = id;
                viewHolder.imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d(FamilyTreeActivity.class.getSimpleName(), "name: " + builder);
                        mEditor.putString(AppConstants.PROFILE_ID, finalId);
                        mEditor.putBoolean(AppConstants.MYPROFILE_SP, false);
                        mEditor.apply();
                        try {
                            ProfileActivity.isEnable = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent mIntent = new Intent(getActivity(), ProfileActivity.class);
                        startActivity(mIntent);
                    }
                });

                if (mSharedPreferences.getBoolean(AppConstants.MYPROFILE_SP, true)) {
                    viewHolder.imglink.setVisibility(View.GONE);
                } else {
                    viewHolder.imglink.setVisibility(View.VISIBLE);
                }

                viewHolder.llLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // x from web service
                        String msg = "Do you want add x as child of " + builder + "?";
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle(getActivity().getResources().getString(R.string.app_name));
                        alertDialog.setMessage(msg);
                        alertDialog.setPositiveButton("Request", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                });
            }
        };

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareScreenShot(getActivity(), graphView);
            }
        });


        try {
            ListProfileData mListProfileData = ((ProfileActivity) getActivity()).getMyData();
            if (mListProfileData != null) {
                SetOfflineData(mListProfileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }


    private void SetOfflineData(ListProfileData mListProfileData) {

        RealmList<ListFamilyTreeData> familyTreeData = mListProfileData.getmListFamilyTreeData();

        ArrayList<Node> lstNode = new ArrayList<>();
        ArrayList<Integer> lstLevel = new ArrayList<>();
       for (int i = 0; i < familyTreeData.size(); i++) {
            ListFamilyTreeData fdata = familyTreeData.get(i);
            JSONObject mjson = new JSONObject();
            try {
                mjson.put(getString(R.string.FT_IMG), fdata.getProfile_pic());
                mjson.put(getString(R.string.FT_NAME), fdata.getName());
                mjson.put(getString(R.string.FT_PROFILE_ID), fdata.getProfile_id());
            } catch (Exception e) {
                e.printStackTrace();
            }
            lstNode.add(new Node(mjson));
            lstLevel.add(Integer.parseInt(fdata.getLevel()));
        }

        ArrayList<Node> listNode1 = new ArrayList<>();
        ArrayList<Node> listNode2 = new ArrayList<>();
        if (lstLevel.size() > 1) {
            for (int j = lstLevel.size() - 1; j >= 0; j--) {
                for (int i = j - 1; i >= 0; i--) {
                    if (lstLevel.get(j) > lstLevel.get(i)) {
                        Log.d(FamilyTreeFragment.class.getSimpleName(), "i=" + i + " j=" + j);
                        listNode1.add(lstNode.get(i));
                        listNode2.add(lstNode.get(j));
                        break;
                    }
                }
            }
        }

        if (lstLevel.size() > 1) {
            for (int i = listNode1.size() - 1; i >= 0; i--) {
                graph.addEdge(listNode1.get(i), listNode2.get(i));
            }
        } else if (lstLevel.size() == 1) {
            graph.addNode(lstNode.get(0));
        }

        graphView.setAdapter(adapter);
        // set the algorithm here
        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder().setSiblingSeparation(100).setLevelSeparation(300).setSubtreeSeparation(300).setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM).build();
        adapter.setAlgorithm(new BuchheimWalkerAlgorithm(configuration));


    }

    private void Memory_Allocation(View rootView) {
        graph = new Graph();
        graphView = rootView.findViewById(R.id.graph);
        imgShare = rootView.findViewById(R.id.imgShare);
        mSharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

   private class ViewHolder {
        TextView mTextView;
        ImageView imgView;
        LinearLayout ll_node, llLink;
        ImageView imglink;

        ViewHolder(View view) {
            mTextView = view.findViewById(R.id.textView);
            imgView = view.findViewById(R.id.imgView);
            ll_node = view.findViewById(R.id.ll_node);
            imglink = view.findViewById(R.id.imglink);
            llLink = view.findViewById(R.id.llLink);
        }
    }
}
