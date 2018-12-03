package com.krs.vastipatrak.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.allyants.draggabletreeview.SimpleTreeViewAdapter;
import com.allyants.draggabletreeview.TreeNode;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.LoginActivity;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.blox.graphview.BaseGraphAdapter;
import de.blox.graphview.Graph;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;

public class FamilyTreeFragment extends Fragment {

    Graph graph;
    SharedPreferences mSharedPreferences;
    GraphView graphView;
    BaseGraphAdapter<ViewHolder> adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tree, container, false);
        graphView = rootView.findViewById(R.id.graph);
        Memory_Allocation(rootView);

        //getTreeViews(SecondActivity.adapter);
        // you can set the graph via the constructor or use the adapter.setGraph(Graph) method
        adapter = new BaseGraphAdapter<ViewHolder>(getActivity(), R.layout.node, graph) {

            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                viewHolder.mTextView.setText(data.toString());
            }
        };

        SyncUser();
        return rootView;
    }

    private void Memory_Allocation(View rootView) {
        graph = new Graph();
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void getTreeViews(SimpleTreeViewAdapter adapter) {
        ArrayList<TreeNode> children = adapter.root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            TreeNode node = children.get(i);
            Object object = children.get(i).getData();
            int level = children.get(i).getLevel();

            Node node1 = new Node(object.toString());
            graph.addNode(node1);

            Log.d("Treeviews", "Name: " + object.toString() + " Level:" + level);
            if (children.get(i).getChildren().size() != 0) {
                getTreeNodeView(children.get(i), node1);
            }
        }
    }

    public void getTreeNodeView(TreeNode node, Node p_node) {
        ArrayList<TreeNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {

            TreeNode node1 = children.get(i);
            Object object1 = children.get(i).getData();
            int level1 = children.get(i).getLevel();

            Node node2 = new Node(children.get(i).getData());
            graph.addEdge(p_node, node2);

            Log.d("Treeviews", "Name1: " + object1.toString() + " Level1:" + level1);
            if (children.get(i).getChildren().size() != 0) {
                getTreeNodeView(children.get(i), node2);
            }
        }
    }

    private void SyncUser() {
        if (Common.isOnline(getActivity())) {
            Common.showProgressDialog(getActivity());
            JSONObject mJsonObject = null;
            try {
                mJsonObject = new JSONObject();
                mJsonObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                mJsonObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
                mJsonObject.put(Common.Constant_Class.PROFILE_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sync_url = Common.Constant_Class.SYNC_URL;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sync_url, mJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(FamilyTreeFragment.class.getSimpleName(), "response: " + response.toString());

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            JSONObject mJsonData=mJsonArray.getJSONObject(0);
                            JSONArray tree_array = mJsonData.getJSONArray("familyTree");

                            for (int i = 0; i < tree_array.length(); i++) {
                                JSONObject mJsonObject = tree_array.getJSONObject(i);
                                mJsonObject.getString("name");
                                mJsonObject.getString("level");
                                mJsonObject.getString("profile_id");
                                TreeNode item = new TreeNode(mJsonObject);
                                Node node1 = new Node(mJsonObject.toString());
                                graph.addNode(node1);
                                getTreeNodeView(item, node1);
                            }
                            graphView.setAdapter(adapter);

                            // set the algorithm here
                            final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder().setSiblingSeparation(100).setLevelSeparation(300).setSubtreeSeparation(300).setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM).build();
                            adapter.setAlgorithm(new BuchheimWalkerAlgorithm(configuration));
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Common.hideProgressDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    VolleyLog.d(FamilyTreeFragment.class.getSimpleName(), "Error: " + error.getMessage());
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
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }

    private class ViewHolder {
        TextView mTextView;

        ViewHolder(View view) {
            mTextView = view.findViewById(R.id.textView);
        }
    }


}
