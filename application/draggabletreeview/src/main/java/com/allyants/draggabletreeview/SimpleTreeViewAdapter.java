package com.allyants.draggabletreeview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbonk on 6/16/2017.
 */

public class SimpleTreeViewAdapter extends TreeViewAdapter {

    public SimpleTreeViewAdapter(Context context, TreeNode root) {
        super(context, root);
    }

    @Override
    public View createTreeView(Context context, final TreeNode node, Object data, int level, boolean hasChildren) {
        View view = View.inflate(context, R.layout.tree_view_item, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(layoutParams);

        JSONObject mjobj = (JSONObject) data;
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        try {
            String url = mjobj.getString(context.getString(R.string.FT_IMG));
            String name = mjobj.getString(context.getString(R.string.FT_NAME));
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(imageView);
            textView.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}
