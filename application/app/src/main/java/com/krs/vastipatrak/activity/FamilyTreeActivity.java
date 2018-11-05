package com.krs.vastipatrak.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.allyants.draggabletreeview.DraggableTreeView;
import com.allyants.draggabletreeview.SimpleTreeViewAdapter;
import com.allyants.draggabletreeview.TreeNode;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.FtSpinnerAdapter;
import com.krs.vastipatrak.utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.blox.graphview.Node;

public class FamilyTreeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SimpleTreeViewAdapter adapter;
    TreeNode root;
    DraggableTreeView draggableTreeView;
    ArrayList<String> LstImages = new ArrayList<>();
    ArrayList<String> LstNames = new ArrayList<>();
    Spinner spin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ftree);

        String json = getIntent().getExtras().getString(getString(R.string.ft_intent));
        JSONObject mObj = null;
        String first_name, spouse, sfather, smother, father, mother, spouse_url, sfather_url, smother_url, father_url, mother_url, profile_url;
        try {
            LstNames.clear();
            LstImages.clear();

            mObj = new JSONObject(json);
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

        FtSpinnerAdapter customAdapter = new FtSpinnerAdapter(getApplicationContext(), LstImages, LstNames);
        spin.setAdapter(customAdapter);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    getTreeViews(adapter);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject mjsonobj = null;
                try {
                    mjsonobj = new JSONObject();
                    mjsonobj.put(getString(R.string.FT_IMG), LstImages.get(spin.getSelectedItemPosition()));
                    mjsonobj.put(getString(R.string.FT_NAME), LstNames.get(spin.getSelectedItemPosition()));
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

        draggableTreeView.setOnDragItemListener(new DraggableTreeView.DragItemCallback() {
            @Override
            public void onStartDrag(View item, TreeNode node) {
                //   Log.e("start", (String) node.getData());
            }

            @Override
            public void onChangedPosition(View item, TreeNode child, TreeNode parent, int position) {
                // Log.e("changed", (String) parent.getData() + " > " + (String) child.getData() + ":" + String.valueOf(position));
            }

            @Override
            public void onEndDrag(View item, TreeNode child, TreeNode parent, int position) {
                //    Log.e("end", (String) parent.getData() + " > " + (String) child.getData() + ":" + String.valueOf(position));
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getTreeViews(SimpleTreeViewAdapter adapter) {
        ArrayList<TreeNode> children = adapter.root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            TreeNode node = children.get(i);
            Object object = children.get(i).getData();
            int level = children.get(i).getLevel();

            Node node1 = new Node(object.toString());
            //   graph.addNode(node1);

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
            //  graph.addEdge(p_node,node2);

            Log.d("Treeviews", "Name1: " + object1.toString() + " Level1:" + level1);
            if (children.get(i).getChildren().size() != 0) {
                getTreeNodeView(children.get(i), node2);
            }
        }
    }
}
