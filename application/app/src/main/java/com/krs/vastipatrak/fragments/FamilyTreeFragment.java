package com.krs.vastipatrak.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.activity.MyProfileActivity;
import com.krs.vastipatrak.model.ListFamilyTreeData;
import com.krs.vastipatrak.model.ListProfileData;
import com.krs.vastipatrak.utils.Common;

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

public class FamilyTreeFragment extends Fragment {

    Graph graph;
    SharedPreferences mSharedPreferences;
    GraphView graphView;
    BaseGraphAdapter<ViewHolder> adapter;
    private ImageView imgShare;
    private ImageView imglink;

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
                String imgUrl = "", name = "";
                try {
                    imgUrl = mjson.getString(getString(R.string.FT_IMG));
                    name = mjson.getString(getString(R.string.FT_NAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] strArray = name.split(" ");
                StringBuilder builder = new StringBuilder();
                for (String s : strArray) {
                    String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    builder.append(cap + " ");
                }
                viewHolder.mTextView.setText(builder);
                Glide.with(getActivity()).load(imgUrl).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(viewHolder.imgView);

            }
        };

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Work in progress", Toast.LENGTH_SHORT).show();
            }
        });

        imglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Work in progress", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            ListProfileData mListProfileData = ((MyProfileActivity) getActivity()).getMyData();
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
        try {
            String url = "http://www.superbinstruments.com/directory-dev/uploads/no-image.png";
            JSONObject mjson = new JSONObject();
            mjson.put(getString(R.string.FT_IMG), url);
            mjson.put(getString(R.string.FT_NAME), "a");
            lstNode.add(new Node(mjson));

            mjson = new JSONObject();
            mjson.put(getString(R.string.FT_IMG), url);
            mjson.put(getString(R.string.FT_NAME), "b");
            lstNode.add(new Node(mjson));

            mjson = new JSONObject();
            mjson.put(getString(R.string.FT_IMG), url);
            mjson.put(getString(R.string.FT_NAME), "c");
            lstNode.add(new Node(mjson));

            mjson = new JSONObject();
            mjson.put(getString(R.string.FT_IMG), url);
            mjson.put(getString(R.string.FT_NAME), "d");
            lstNode.add(new Node(mjson));

            mjson = new JSONObject();
            mjson.put(getString(R.string.FT_IMG), url);
            mjson.put(getString(R.string.FT_NAME), "e");
            lstNode.add(new Node(mjson));
            lstLevel.add(1);
            lstLevel.add(2);
            lstLevel.add(3);
            lstLevel.add(2);
            lstLevel.add(3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for (int i = 0; i < familyTreeData.size(); i++) {
            ListFamilyTreeData fdata = familyTreeData.get(i);
            String name = fdata.getName();
            lstNode.add(new Node(name));
            lstLevel.add(Integer.parseInt(fdata.getLevel()));
        }*/

        ArrayList<Node> listNode1 = new ArrayList<>();
        ArrayList<Node> listNode2 = new ArrayList<>();
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
        for (int i = listNode1.size() - 1; i >= 0; i--) {
            graph.addEdge(listNode1.get(i), listNode2.get(i));
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
        imglink = rootView.findViewById(R.id.imglink);
        mSharedPreferences = getActivity().getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
    }

   /* public void getTreeViews(SimpleTreeViewAdapter adapter) {
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
    }*/

    /*public void getTreeNodeView(TreeNode node, Node p_node) {
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
    }*/

    private class ViewHolder {
        TextView mTextView;
        ImageView imgView;

        ViewHolder(View view) {
            mTextView = view.findViewById(R.id.textView);
            imgView = view.findViewById(R.id.imgView);
        }
    }
}
