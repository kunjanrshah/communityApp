package com.krs.vastipatrak.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allyants.draggabletreeview.SimpleTreeViewAdapter;
import com.allyants.draggabletreeview.TreeNode;
import com.krs.vastipatrak.R;

import java.util.ArrayList;

import de.blox.graphview.BaseGraphAdapter;
import de.blox.graphview.Graph;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;

public class FamilyTreeFragment extends Fragment {

    Graph graph;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tree, container, false);
        GraphView graphView = rootView.findViewById(R.id.graph);
        Memory_Allocation(rootView);

        //getTreeViews(SecondActivity.adapter);
        // you can set the graph via the constructor or use the adapter.setGraph(Graph) method
        final BaseGraphAdapter<ViewHolder> adapter = new BaseGraphAdapter<ViewHolder>(getActivity(), R.layout.node, graph) {

            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                viewHolder.mTextView.setText(data.toString());
            }
        };
        graphView.setAdapter(adapter);

        // set the algorithm here
        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(300)
                .setSubtreeSeparation(300)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
                .build();
        adapter.setAlgorithm(new BuchheimWalkerAlgorithm(configuration));

        return rootView;
    }

    private void Memory_Allocation(View rootView) {
        graph = new Graph();
    }

    public void getTreeViews(SimpleTreeViewAdapter adapter) {
        ArrayList<TreeNode> children = adapter.root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            TreeNode node = children.get(i);
            Object object = children.get(i).getData();
            int level = children.get(i).getLevel();

            Node node1=new Node(object.toString());
            graph.addNode(node1);

            Log.d("Treeviews","Name: "+object.toString()+" Level:"+level);
            if (children.get(i).getChildren().size() != 0) {
                getTreeNodeView(children.get(i),node1);
            }
        }
    }

    public void getTreeNodeView(TreeNode node,Node p_node) {
        ArrayList<TreeNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {

            TreeNode node1 = children.get(i);
            Object object1 = children.get(i).getData();
            int level1 = children.get(i).getLevel();

            Node node2=new Node(children.get(i).getData());
            graph.addEdge(p_node,node2);

            Log.d("Treeviews","Name1: "+object1.toString()+" Level1:"+level1);
            if (children.get(i).getChildren().size() != 0) {
                getTreeNodeView(children.get(i),node2);
            }
        }
    }

    private class ViewHolder {
        TextView mTextView;
        ViewHolder(View view) {
            mTextView = view.findViewById(R.id.textView);
        }
    }
}
