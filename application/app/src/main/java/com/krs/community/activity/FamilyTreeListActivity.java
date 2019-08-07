package com.krs.community.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.krs.community.R;
import com.krs.community.utils.Utility;

import java.lang.reflect.Method;


public class FamilyTreeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tree_listview);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorTree, true);
        }

        ImageView iv_tree = findViewById(R.id.iv_tree);
        iv_tree.setOnClickListener(v -> Toast.makeText(FamilyTreeListActivity.this, "Go to Family tree", Toast.LENGTH_SHORT).show());

        ImageView iv_cancel = findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v ->
                {
                    finish();
                }
        );

        ImageView iv_filter = findViewById(R.id.iv_filter);
        iv_filter.setOnClickListener(v -> {
            try {
                PopupMenu popup = new PopupMenu(this, v);
                popup.getMenuInflater().inflate(R.menu.custom_menu, popup.getMenu());

                popup.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
                    Toast.makeText(this, "Relationship", Toast.LENGTH_SHORT).show();
                    return false;
                });

                popup.getMenu().getItem(1).setOnMenuItemClickListener(item -> {
                    Toast.makeText(this, "First name", Toast.LENGTH_SHORT).show();
                    return false;
                });

                popup.getMenu().getItem(2).setOnMenuItemClickListener(item -> {
                    Toast.makeText(this, "Last added", Toast.LENGTH_SHORT).show();
                    return false;
                });

                Method method = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(popup.getMenu(), true);
                popup.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ViewGroup ll_parent = findViewById(R.id.ll_parent);
        for (int i = 0; i < 10; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.row_list_tree, ll_parent, false);

            ImageView iv_profile=view.findViewById(R.id.iv_profile);
            ImageView iv_user_in_tree=view.findViewById(R.id.iv_user_in_tree);
            TextView tv_name=view.findViewById(R.id.tv_name);
            TextView tv_relation=view.findViewById(R.id.tv_relation);
            TextView tv_year=view.findViewById(R.id.tv_year);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent mIntent=new Intent(FamilyTreeListActivity.this,FamilyTreeDetailActivity.class);
                    startActivity(mIntent);

                }
            });
            ll_parent.addView(view);
        }
    }
}




