package com.yadav.samaj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.allyants.draggabletreeview.DraggableTreeView;
import com.allyants.draggabletreeview.SimpleTreeViewAdapter;
import com.allyants.draggabletreeview.TreeNode;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yadav.samaj.R;
import com.yadav.samaj.adapter.FtSpinnerAdapter;
import com.yadav.samaj.app.AppController;
import com.yadav.samaj.utils.Common;
import com.yadav.samaj.utils.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.blox.graphview.Node;

public class FamilyTreeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    String TAG = FamilyTreeActivity.class.getSimpleName();
    SimpleTreeViewAdapter adapter;
    TreeNode root;
    DraggableTreeView draggableTreeView;
    ArrayList<String> LstImages;
    ArrayList<String> LstNames;
    ArrayList<String> LstLevel;
    ArrayList<String> LstIdentifier;
    ArrayList<String> lstDupName;
    Toolbar mToolbar;
    Snackbar snackbar;
    private Spinner spin;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ftree);
        ToolbarSetup();

        snackbar = Snackbar.make(findViewById(R.id.ll_ftree), R.string.not_connected, Snackbar.LENGTH_INDEFINITE);

        mSharedPreferences = getSharedPreferences(Common.Constant_Class.PREF_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        String json = getIntent().getExtras().getString(getString(R.string.ft_intent));
        JSONObject mObj = null;

        lstDupName = new ArrayList<>();
        LstLevel = new ArrayList<>();
        LstNames = new ArrayList<>();
        LstImages = new ArrayList<>();
        LstIdentifier = new ArrayList<>();
        String id = "";
        final String first_name;
        final String spouse;
        final String sfather;
        final String smother;
        final String father;
        final String mother;
        final String spouse_url;
        final String sfather_url;
        final String smother_url;
        final String father_url;
        final String mother_url;
        final String profile_url;
        final String bdate;
        try {
            LstIdentifier.clear();
            LstNames.clear();
            LstImages.clear();
            LstLevel.clear();
            mObj = new JSONObject(json);
            id = mObj.getString(Common.Constant_Class.ID);
            first_name = mObj.getString(Common.Constant_Class.FIRST_NAME);
            profile_url = mObj.getString(Common.Constant_Class.PROFILE_PIC_URL);

            LstNames.add(first_name);
            LstImages.add(profile_url);
            LstIdentifier.add("profile");

            father = mObj.getString(Common.Constant_Class.FATHER_NAME);
            father_url = mObj.getString(Common.Constant_Class.IMG_FATHER_URL);
            LstNames.add(father);
            LstImages.add(father_url);
            LstIdentifier.add("father");

            mother = mObj.getString(Common.Constant_Class.MOTHER_NAME);
            mother_url = mObj.getString(Common.Constant_Class.IMG_MOTHER_URL);
            LstNames.add(mother);
            LstImages.add(mother_url);
            LstIdentifier.add("mother");

            spouse = mObj.getString(Common.Constant_Class.SPOUSE_NAME);
            spouse_url = mObj.getString(Common.Constant_Class.IMG_SPOUSE_URL);
            LstNames.add(spouse);
            LstImages.add(spouse_url);
            LstIdentifier.add("spouse");

            sfather = mObj.getString(Common.Constant_Class.SPOUSE_FATHER_NAME);
            sfather_url = mObj.getString(Common.Constant_Class.IMG_SFATHER_URL);
            LstNames.add(sfather);
            LstImages.add(sfather_url);
            LstIdentifier.add("sfather");

            smother = mObj.getString(Common.Constant_Class.SPOUSE_MOTHER_NAME);
            smother_url = mObj.getString(Common.Constant_Class.IMG_SMOTHER_URL);
            LstNames.add(smother);
            LstImages.add(smother_url);
            LstIdentifier.add("smother");


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
                    LstIdentifier.add("child_" + mJsonObj.getString(Common.Constant_Class.CHILD_ID));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Button btn_add = findViewById(R.id.btn_add);
        Button btn_save = findViewById(R.id.btn_save);

        root = new TreeNode(this);
        draggableTreeView = findViewById(R.id.dtv);
        spin = findViewById(R.id.simpleSpinner);

        spin.setAdapter(new FtSpinnerAdapter(this, LstImages, LstNames));

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
                        mjsonobj.put(getString(R.string.FT_IDENTIFIER), LstIdentifier.get(spin.getSelectedItemPosition()));
                        mjsonobj.put(getString(R.string.FT_PROFILE_ID), finalId);
                        lstDupName.add(LstNames.get(spin.getSelectedItemPosition()));

                        TreeNode item = new TreeNode(mjsonobj);
                        root.addChild(item);
                        adapter = new SimpleTreeViewAdapter(FamilyTreeActivity.this, root);
                        draggableTreeView.setAdapter(adapter);

                    } else {
                        Toast.makeText(FamilyTreeActivity.this, "Name already exist!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                Log.e("end", parent.getData() + " > " + child.getData() + ":" + String.valueOf(position));

                JSONObject mObject = (JSONObject) parent.getData();
                String name = "";
                try {
                    name = mObject.getString(getString(R.string.FT_NAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mSharedPreferences.getBoolean("is_delete", false)) {
                    if (lstDupName != null && lstDupName.size() > 0) {
                        lstDupName.remove(name);
                    }
                    Log.d(TAG, "step remove1 ");
                    mEditor.putBoolean("is_delete", false);
                    mEditor.apply();
                }
                Log.d(TAG, "step end");
            }
        });
    }

    private void ToolbarSetup() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Profile");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "step setNavigationOnClickListener");
                backNavigation();
            }
        });
    }

    private void backNavigation() {
        Common.hideKeyboard(this);
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem saveItem = menu.findItem(R.id.action_save);
        saveItem.setVisible(false);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent mIntent = new Intent(FamilyTreeActivity.this, MainActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.putExtra(Common.Constant_Class.QUERY, query);
                startActivity(mIntent);
                Log.d(TAG, "step onQueryTextSubmit");
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem export = menu.findItem(R.id.action_export);
        export.setVisible(false);

        MenuItem admins = menu.findItem(R.id.action_admins);
        admins.setVisible(false);

        MenuItem scan_image = menu.findItem(R.id.action_scan_image);
        scan_image.setVisible(false);

        MenuItem scan_qr = menu.findItem(R.id.action_scan);
        scan_qr.setVisible(false);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent mIntent = new Intent(FamilyTreeActivity.this, AdvanceSearchActivity.class);
                startActivity(mIntent);
                Log.d(TAG, "step action_filter");
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return false;
            }
        });


        MenuItem voiceItem = menu.findItem(R.id.action_voice);
        voiceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Common.promptSpeechInput(FamilyTreeActivity.this);
                return false;
            }
        });

        MenuItem action_toggle = menu.findItem(R.id.action_toggle);
        action_toggle.setVisible(false);

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Common.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQueryHint(result.get(0));
                    searchView.setQuery(result.get(0), true);
                }
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        spin.setSelection(position);
        /*Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void getTreeViewsFromAdapter(SimpleTreeViewAdapter adapter) {

        JSONArray mjsonArray = new JSONArray();
        ArrayList<TreeNode> children = adapter.root.getChildren();
        boolean isvalid = true;
        for (int i = 0; i < children.size(); i++) {
            TreeNode node = children.get(i);
            JSONObject object = (JSONObject) children.get(i).getData();
            int level = children.get(i).getLevel();
            if (i != children.size() - 1) {
                int level1 = children.get(i + 1).getLevel();
                if (level == 1 && level1 == 1) {
                    isvalid = false;
                    break;
                }
            }
            Node node1 = new Node(object.toString());
            //   graph.addNode(node1);
            JSONObject mjson = new JSONObject();
            try {
                /*String url = object.getString(getString(R.string.FT_IMG));
                if (url.contains("no-image")) {
                    url = "";
                }*/
                //mjson.put(getString(R.string.FT_IMG), url);
                mjson.put(getString(R.string.FT_IDENTIFIER), object.getString(getString(R.string.FT_IDENTIFIER)));
                // mjson.put(getString(R.string.FT_NAME), object.getString(getString(R.string.FT_NAME)));
                mjson.put(getString(R.string.FT_PROFILE_ID), object.getString(getString(R.string.FT_PROFILE_ID)));
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
        if (isvalid) {
            saveTreeWs(mjsonArray);
        } else {
            Toast.makeText(this, "Only one root parent valid", Toast.LENGTH_SHORT).show();
        }
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
                //mjson.put(getString(R.string.FT_IMG), object1.get(getString(R.string.FT_IMG)));
                //mjson.put(getString(R.string.FT_NAME), object1.get(getString(R.string.FT_NAME)));
                mjson.put(getString(R.string.FT_IDENTIFIER), object1.getString(getString(R.string.FT_IDENTIFIER)));
                mjson.put(getString(R.string.FT_PROFILE_ID), object1.get(getString(R.string.FT_PROFILE_ID)));
                mjson.put(getString(R.string.FT_LEVEL), level1);
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
                                /*String id=mobject.getString("id");
                                String profile_id= mobject.getString("profile_id");
                                String profile_pic=mobject.getString("profile_pic");
                                String level = mobject.getString("level");*/
                                String name = mobject.getString(getString(R.string.FT_NAME));
                                lstDupName.add(name);

                                lstNode.add(new TreeNode(mobject));
                                lstLevel.add(Integer.parseInt(mobject.getString(getString(R.string.FT_LEVEL))));

                            }


                            for (int j = lstLevel.size() - 1; j >= 0; j--) {
                                if (j == 0) {
                                    root.addChild(lstNode.get(j));
                                } else {
                                    for (int i = j - 1; i >= 0; i--) {
                                        if (lstLevel.get(j) > lstLevel.get(i)) {
                                            Log.d(FamilyTreeActivity.class.getSimpleName(), "i=" + i + " j=" + j);
                                            lstNode.get(i).addChild(lstNode.get(j));
                                            break;
                                        } else if (lstLevel.get(j) == lstLevel.get(i)) {
                                            root.addChild(lstNode.get(j));
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
                            if (lstDupName != null) {
                                lstDupName.clear();
                            }
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


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            if (snackbar != null) {
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        } else {
            if (snackbar != null) {
                if (snackbar.isShownOrQueued()) {
                    snackbar.dismiss();
                }
            }
        }
    }
}
