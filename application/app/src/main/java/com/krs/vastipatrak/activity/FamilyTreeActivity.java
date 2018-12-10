package com.krs.vastipatrak.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.allyants.draggabletreeview.DraggableTreeView;
import com.allyants.draggabletreeview.SimpleTreeViewAdapter;
import com.allyants.draggabletreeview.TreeNode;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.FtSpinnerAdapter;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.blox.graphview.Node;

public class FamilyTreeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String TAG = FamilyTreeActivity.class.getSimpleName();
    SimpleTreeViewAdapter adapter;
    TreeNode root;
    DraggableTreeView draggableTreeView;
    ArrayList<String> LstImages = new ArrayList<>();
    ArrayList<String> LstNames = new ArrayList<>();
    ArrayList<String> LstLevel = new ArrayList<>();
    ArrayList<String> lstDupName = new ArrayList<>();
    HashMap<Integer,String> lstHashNode=new HashMap<>();

    Spinner spin;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ftree);
        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        String json = getIntent().getExtras().getString(getString(R.string.ft_intent));
        JSONObject mObj = null;
        String id = "", first_name, spouse, sfather, smother, father, mother, spouse_url, sfather_url, smother_url, father_url, mother_url, profile_url, bdate;
        try {
            LstNames.clear();
            LstImages.clear();
            LstLevel.clear();
            mObj = new JSONObject(json);
            id = mObj.getString(Common.Constant_Class.ID);
            first_name = mObj.getString(Common.Constant_Class.FIRST_NAME);
            profile_url = mObj.getString(Common.Constant_Class.PROFILE_PIC_URL);

            LstNames.add(first_name);
            LstImages.add(profile_url);

            father = mObj.getString(Common.Constant_Class.FATHER_NAME);
            father_url = mObj.getString(Common.Constant_Class.IMG_FATHER_URL);
            LstNames.add(father);
            LstImages.add(father_url);

            mother = mObj.getString(Common.Constant_Class.MOTHER_NAME);
            mother_url = mObj.getString(Common.Constant_Class.IMG_MOTHER_URL);
            LstNames.add(mother);
            LstImages.add(mother_url);

            spouse = mObj.getString(Common.Constant_Class.SPOUSE_NAME);
            spouse_url = mObj.getString(Common.Constant_Class.IMG_SPOUSE_URL);
            LstNames.add(spouse);
            LstImages.add(spouse_url);

            sfather = mObj.getString(Common.Constant_Class.SPOUSE_FATHER_NAME);
            sfather_url = mObj.getString(Common.Constant_Class.IMG_SFATHER_URL);
            LstNames.add(sfather);
            LstImages.add(sfather_url);

            smother = mObj.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME);
            smother_url = mObj.getString(Common.Constant_Class.IMG_SMOTHER_URL);
            LstNames.add(smother);
            LstImages.add(smother_url);


            if (mObj.has(Common.Constant_Class.CHILDS)) {
                JSONArray jsonArray = mObj.getJSONArray(Common.Constant_Class.CHILDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject mJsonObj = jsonArray.getJSONObject(i);
                    String chlid_url = "";
                    String child_name = "";
                    if (mJsonObj.has(Common.Constant_Class.CHILD_IMAGE_URL)) {
                        chlid_url = mJsonObj.getString(Common.Constant_Class.CHILD_IMAGE_URL);
                    }
                    if (mJsonObj.has(Common.Constant_Class.CHILD_NAME)) {
                        child_name = mJsonObj.getString(Common.Constant_Class.CHILD_NAME);
                    }
                    LstImages.add(chlid_url);
                    LstNames.add(child_name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Button btn_add = (Button) findViewById(R.id.btn_add);
        Button btn_save = (Button) findViewById(R.id.btn_save);

        root = new TreeNode(this);
        draggableTreeView = (DraggableTreeView) findViewById(R.id.dtv);

        spin = (Spinner) findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);

        FtSpinnerAdapter customAdapter = new FtSpinnerAdapter(getApplicationContext(), LstImages, LstNames, LstLevel);
        spin.setAdapter(customAdapter);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    getTreeViewsFromAdapter(adapter);
                }
            }
        });

        final String finalId = id;
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject mjsonobj = null;
                try {
                    if (!lstDupName.contains(LstNames.get(spin.getSelectedItemPosition()))) {
                        mjsonobj = new JSONObject();
                        mjsonobj.put(getString(R.string.FT_IMG), LstImages.get(spin.getSelectedItemPosition()));
                        mjsonobj.put(getString(R.string.FT_NAME), LstNames.get(spin.getSelectedItemPosition()));
                        mjsonobj.put(getString(R.string.FT_PROFILE_ID), finalId);
                        lstDupName.add(LstNames.get(spin.getSelectedItemPosition()));
                    } else {
                        Toast.makeText(FamilyTreeActivity.this, "Name already exist!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TreeNode item = new TreeNode(mjsonobj);
                root.addChild(item);
                adapter = new SimpleTreeViewAdapter(FamilyTreeActivity.this, root);
                draggableTreeView.setAdapter(adapter);
            }
        });


        adapter = new SimpleTreeViewAdapter(this, root);
        draggableTreeView.setAdapter(adapter);
        fetchProfileData();
        draggableTreeView.setOnDragItemListener(new DraggableTreeView.DragItemCallback() {
            @Override
            public void onStartDrag(View item, TreeNode node) {
                Log.d(TAG, "start");
            }

            @Override
            public void onChangedPosition(View item, TreeNode child, TreeNode parent, int position) {
                // Log.e("changed", (String) parent.getData() + " > " + (String) child.getData() + ":" + String.valueOf(position));
                Log.d(TAG, "changed");
            }

            @Override
            public void onEndDrag(View item, TreeNode child, TreeNode parent, int position) {
                // Log.e("end", (String) parent.getData() + " > " + (String) child.getData() + ":" + String.valueOf(position));
                Log.d(TAG, "end");
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*mEditor.putBoolean("is_delete", true);
        mEditor.apply();*/
    }

    public void getTreeViewsFromAdapter(SimpleTreeViewAdapter adapter) {

        JSONArray mjsonArray = new JSONArray();
        ArrayList<TreeNode> children = adapter.root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            TreeNode node = children.get(i);
            JSONObject object = (JSONObject) children.get(i).getData();
            int level = children.get(i).getLevel();

            Node node1 = new Node(object.toString());
            //   graph.addNode(node1);
            JSONObject mjson = new JSONObject();
            try {
                mjson.put(getString(R.string.FT_IMG), object.get(getString(R.string.FT_IMG)));
                mjson.put(getString(R.string.FT_NAME), object.get(getString(R.string.FT_NAME)));
                mjson.put(getString(R.string.FT_PROFILE_ID), object.get(getString(R.string.FT_PROFILE_ID)));
                mjson.put(getString(R.string.FT_LEVEL), level);
                mjsonArray.put(mjson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(FamilyTreeActivity.class.getSimpleName(), "Name: " + object.toString() + " Level:" + level);
            if (children.get(i).getChildren().size() != 0) {
                getTreeNodeView(children.get(i), node1, mjsonArray);
            }
        }
        saveTreeWs(mjsonArray);
    }

    public void getTreeNodeView(TreeNode node, Node p_node, JSONArray mjsonArray) {
        ArrayList<TreeNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {

            TreeNode node1 = children.get(i);
            JSONObject object1 = (JSONObject) children.get(i).getData();
            int level1 = children.get(i).getLevel();

            Node node2 = new Node(children.get(i).getData());
            //  graph.addEdge(p_node,node2);
            try {
                JSONObject mjson = new JSONObject();
                mjson.put(getString(R.string.FT_IMG), object1.get(getString(R.string.FT_IMG)));
                mjson.put(getString(R.string.FT_NAME), object1.get(getString(R.string.FT_NAME)));
                mjson.put(getString(R.string.FT_PROFILE_ID), object1.get(getString(R.string.FT_PROFILE_ID)));
                mjson.put("level", level1);
                mjsonArray.put(mjson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(FamilyTreeActivity.class.getSimpleName(), "Name1: " + object1.toString() + " Level1:" + level1);
            if (children.get(i).getChildren().size() != 0) {
                getTreeNodeView(children.get(i), node2, mjsonArray);
            }
        }
    }

    private void fetchProfileData() {
        if (Common.isOnline(this)) {
            Common.showProgressDialog(this);
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
                    Log.d(TAG, "response: " + response.toString());

                    try {
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        String message = response.getString(Common.Constant_Class.MESSAGE);

                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            JSONArray mJsonArray = response.getJSONArray(Common.Constant_Class.DATA);
                            JSONObject mJsondata = mJsonArray.getJSONObject(0);
                            JSONArray mjArray = mJsondata.getJSONArray("familyTree");
                            ArrayList<TreeNode> lstNode = new ArrayList<>();
                            ArrayList<Integer> lstLevel = new ArrayList<>();
                            for (int i = 0; i < mjArray.length(); i++) {
                                JSONObject mobject = mjArray.getJSONObject(i);
                                mobject.getString("id");
                                mobject.getString("user_id");
                                mobject.getString("profile_pic");

                                String name = mobject.getString("name");
                                mobject.getString("level");
                                TreeNode item = new TreeNode(mobject);
                                lstDupName.add(name);
                                lstNode.add(new TreeNode(name));
                                lstLevel.add(Integer.parseInt(mobject.getString("level")));
                                lstHashNode.put(Integer.parseInt(mobject.getString("id")),name);

                            }
                            String url = "http://www.superbinstruments.com/directory-dev/uploads/no-image.png";
                            JSONObject mjson = new JSONObject();
                            mjson.put(getString(R.string.FT_IMG), url);
                            mjson.put(getString(R.string.FT_NAME), "a");
                            mjson.put(getString(R.string.FT_PROFILE_ID), "4345");
                            lstNode.add(new TreeNode(mjson));

                            mjson = new JSONObject();
                            mjson.put(getString(R.string.FT_IMG), url);
                            mjson.put(getString(R.string.FT_NAME), "b");
                            mjson.put(getString(R.string.FT_PROFILE_ID), "4345");
                            lstNode.add(new TreeNode(mjson));

                            lstLevel.add(1);
                            lstLevel.add(2);

                            for (int j = lstLevel.size() - 1; j >= 0; j--) {
                                if (j == 0) {
                                    root.addChild(lstNode.get(j));
                                } else {
                                    for (int i = j - 1; i >= 0; i--) {
                                        if (lstLevel.get(j) > lstLevel.get(i)) {
                                            Log.d(FamilyTreeActivity.class.getSimpleName(), "i=" + i + " j=" + j);
                                            lstNode.get(i).addChild(lstNode.get(j));
                                            break;
                                        }
                                    }
                                }
                            }

                            adapter = new SimpleTreeViewAdapter(FamilyTreeActivity.this, root);
                            draggableTreeView.setAdapter(adapter);

                        } else {
                            Toast.makeText(FamilyTreeActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                    Intent mIntent = new Intent(FamilyTreeActivity.this, LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    finish();
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
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }


    private void saveTreeWs(JSONArray JsonArray) {

        JSONObject JSONObject = new JSONObject();
        try {
            JSONObject.put("familyTree", JsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (Common.isOnline(this)) {
            Common.showProgressDialog(this);
            try {
                JSONObject.put(Common.Constant_Class.USER_ID, mSharedPreferences.getString(Common.Constant_Class.USER_ID, ""));
                JSONObject.put(Common.Constant_Class.ACCESS_TOKEN, mSharedPreferences.getString(Common.Constant_Class.ACCESS_TOKEN, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Common.Constant_Class.SET_TREE_URL, JSONObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(@NonNull JSONObject response) {
                    Log.d(TAG, "response: " + response.toString());
                    Common.hideProgressDialog();

                    try {
                        String message = response.getString(Common.Constant_Class.MESSAGE);
                        String success = response.getString(Common.Constant_Class.SUCCESS);
                        if (success.equalsIgnoreCase(Common.Constant_Class.TRUE)) {
                            Toast.makeText(FamilyTreeActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(FamilyTreeActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (response.has(Common.Constant_Class.ERROR_CODE)) {
                                String error = response.getString(Common.Constant_Class.ERROR_CODE);
                                if (error.equalsIgnoreCase(Common.Constant_Class.ERROR_13)) {
                                    Intent mIntent = new Intent(FamilyTreeActivity.this, LoginActivity.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    finish();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Common.hideProgressDialog();
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
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        }
    }


}
