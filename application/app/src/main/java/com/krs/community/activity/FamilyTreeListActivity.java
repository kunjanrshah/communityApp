package com.krs.community.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.utils.Utility;
import com.wessam.library.NetworkChecker;

import java.lang.reflect.Method;


public class FamilyTreeListActivity extends AppCompatActivity {

    NetworkChangeReceiver mNetworkReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mNetworkReceiver = new NetworkChangeReceiver();

        registerNetworkBroadcastForNougat();


        if (NetworkChecker.isNetworkConnected(this)) {
            setScreenLayout();
        }else{
            setNoInternetLayout();
        }


        AppController mApp = (AppController) getApplicationContext();
        mApp.FirebaseAnalytics(FamilyTreeListActivity.this,FamilyTreeListActivity.class.getSimpleName());
        mApp.FacebookAnalytics(FamilyTreeListActivity.this,FamilyTreeListActivity.class.getSimpleName());


    }
    private void unregisterNetworkBroadcastForNougat() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                unregisterReceiver(mNetworkReceiver);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                unregisterReceiver(mNetworkReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkBroadcastForNougat();
    }
    private void setNoInternetLayout(){
        setContentView(R.layout.no_internet_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(6000);
        anim.setRepeatMode(AlphaAnimation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        AppCompatImageView imageView = findViewById(R.id.no_internet_image);
        imageView.setAnimation(anim);
        AppCompatButton retryButton=findViewById(R.id.retry_button);
        retryButton.setOnClickListener(v -> {
            if (NetworkChecker.isNetworkConnected(this)) {
                setScreenLayout();
            }
        });
    }
    private void setScreenLayout(){

        if (NetworkChecker.isNetworkConnected(this)) {

            setContentView(R.layout.activity_tree_listview);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(this, R.color.colorTree, true);
            }

            ImageView iv_tree = findViewById(R.id.iv_tree);
            iv_tree.setOnClickListener(v -> Toast.makeText(FamilyTreeListActivity.this, getResources().getString(R.string.GotoFamilyTree), Toast.LENGTH_SHORT).show());

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
                        Toast.makeText(this, getResources().getString(R.string.RelationShip), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    popup.getMenu().getItem(1).setOnMenuItemClickListener(item -> {
                        Toast.makeText(this, getResources().getString(R.string.FirstName), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    popup.getMenu().getItem(2).setOnMenuItemClickListener(item -> {
                        Toast.makeText(this, getResources().getString(R.string.LastAdded), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    Method method = popup.getMenu().getClass().getDeclaredMethod(getResources().getString(R.string.serOptinall), boolean.class);
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
                TextView tv_name=view.findViewById(R.id.tv_name);
                TextView tv_relation=view.findViewById(R.id.tv_relation);
                TextView tv_year=view.findViewById(R.id.tv_year);

                ImageView iv_menu=view.findViewById(R.id.iv_menu);
                iv_menu.setOnClickListener(v -> {
                    try {
                        PopupMenu popup = new PopupMenu(this, v);
                        popup.getMenuInflater().inflate(R.menu.menu_tree_list_item, popup.getMenu());

                        popup.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
                            Toast.makeText(this, getResources().getString(R.string.AddRelative), Toast.LENGTH_SHORT).show();
                            return false;
                        });

                        popup.getMenu().getItem(1).setOnMenuItemClickListener(item -> {
                            Toast.makeText(this, getResources().getString(R.string.ViewTree), Toast.LENGTH_SHORT).show();
                            return false;
                        });

                        popup.getMenu().getItem(2).setOnMenuItemClickListener(item -> {
                            Toast.makeText(this, getResources().getString(R.string.ProfileDetail), Toast.LENGTH_SHORT).show();
                            return false;
                        });

                        Method method = popup.getMenu().getClass().getDeclaredMethod(getResources().getString(R.string.serOptinall), boolean.class);
                        method.setAccessible(true);
                        method.invoke(popup.getMenu(), true);
                        popup.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                view.setOnClickListener(v -> {

                    Intent mIntent=new Intent(FamilyTreeListActivity.this,FamilyTreeDetailActivity.class);
                    startActivity(mIntent);

                });
                ll_parent.addView(view);
            }
        }


    }
    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
    class NetworkChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                if (NetworkChecker.isNetworkConnected(context)) {
                    setScreenLayout();
                }else{
                    setNoInternetLayout();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}




